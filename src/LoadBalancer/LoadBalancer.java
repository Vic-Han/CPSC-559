package LoadBalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.net.*;
import Utilities.*;


public class LoadBalancer {
    private List<String> serverAddresses; //private list of serverAddresses
    private AtomicInteger currentIndex = new AtomicInteger(0); //atomic for thread safety 




    //Basic constructor to ensure that we get all the server addresses here. This is now being initialized by LoadBalancerInit (which also initializes the HealthChecks to run at a set interval to ensure servers are up)
    public LoadBalancer(List<String> initialServerAddresses)
    {
        serverAddresses.addAll(initialServerAddresses); 
    }

    public synchronized String getNextServer(){

        if(serverAddresses.isEmpty())
        {
            throw new IllegalStateException("No servers are available.");
        }
        int index = currentIndex.getAndUpdate(i -> (i + 1) % serverAddresses.size()); 
        return serverAddresses.get(index);
    }

    //synchronized as we can't have more than 1 thread trying to access the global var at the same time or we get RACE CONDITIONS
    public synchronized void removeFailedServer(String serverAddress)
    {
        if(serverAddresses.contains(serverAddress))
        {
            serverAddresses.remove(serverAddress);
            System.out.println("Server removed due to failure: " + serverAddress);
        }
        else
        {
            System.out.println("Attempted to remove a server that was not in the list: " + serverAddress);
        }

        //can also re-sync list if shared (but I don't think we share this so we should be good)
    }

    //synchronized as we can't have more than 1 thread trying to access the global var at the same time or we get RACE CONDITIONS
    public synchronized void addRecoveredServer(String serverAddress)
    {
        //perform a check to ensure that this server isn't already in the serverlist so that our round robin algorithm actually works properly 
        if (!serverAddresses.contains(serverAddress))
        {
            serverAddresses.add(serverAddress);
            System.out.println("Server recovered and added: " + serverAddress);
        }
        else
        {
            //Do we want to log anything? 
            System.out.println("Server already in the list: " + serverAddress); 
        }
    }

    protected static ArrayList<Triple<Socket, String, Integer>> replicas;
    public static void main(String[] args) throws IOException{
        int port = 1970; 
        Socket replicaSocket;
        long time = System.currentTimeMillis();
        replicas = new ArrayList<Triple<Socket, String, Integer>>();
        System.out.println("Initiating Load Balancer. Time = "+time);
        // Create a server socket
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
            	System.out.println("Waiting for new connection LOADBALANCER");

                replicaSocket = ss.accept(); // accept a new client connection

                DataInputStream is = new DataInputStream(replicaSocket.getInputStream()); //input stream
                DataOutputStream os = new DataOutputStream(replicaSocket.getOutputStream()); //output stream
                byte systemType = is.readByte(); //systemType is either Server Start Request (i.e., start a server), or a Client Route Request (i.e., route client to appropriate server via round robin)
                //byte systemType = handleStartOnLogin(); 
                if(systemType == Utilities.codes.SERVERSTARTREQUEST) {
                    System.out.println("New server request");
                    os.writeLong(time);
                    String replicaIP = is.readUTF(); //get hostname being used by server
                    int replicaPort = is.readInt(); //get port being used by server
                    System.out.println("Host:port "+replicaIP+":"+replicaPort);
                    Triple<Socket, String, Integer> toAdd = new Triple<Socket, String, Integer>(replicaSocket, replicaIP, replicaPort);
                    replicas.add(toAdd);
                } else if (systemType == Utilities.codes.CLIENTROUTEREQUEST) {
                	System.out.println("Client routing request");
                    routeClient(replicaSocket);
                }
                // else{
                //     s.close();
                // }
                //s.close();
            }
        }
    }

    private static byte handleStartOnLogin(){
        return codes.SERVERSTARTREQUEST; 

    }

    private static class globalSocket {
        protected static Socket toSend;
        protected static String replicaIP;
        protected static int replicaPort;
    }

    private static void routeClient(Socket c) {
        globalSocket.toSend = null;
        ArrayList<Triple<Socket, String, Integer>> toRemove = new ArrayList<Triple<Socket, String, Integer>>();
        replicas.forEach(s->{
            try {
            	
                //ask socket for how busy it is
                DataInputStream is = new DataInputStream(s.first.getInputStream()); 
                DataOutputStream os = new DataOutputStream(s.first.getOutputStream());
                os.writeByte(0);
                int busy = is.readShort();
                //get either error because socket is dead or a busy value
                //if less busy than current socket replace current socket 
                
                //for now just return first one if alive
                if(globalSocket.toSend == null) {
                    globalSocket.toSend = s.first;
                    globalSocket.replicaIP = s.second;
                    globalSocket.replicaPort = s.third;
                }
            } catch (IOException e) {
                //socket is dead, remove from list
                System.out.println("IO Exception on replica at "+s.first.getInetAddress().getHostAddress()+". Will remove from replicas");
                toRemove.add(s);
            }
        });
        toRemove.forEach(r->{
            replicas.remove(r);
        });
        try {
            DataOutputStream os = new DataOutputStream(c.getOutputStream());
            if (globalSocket.toSend != null) {
            	System.out.println("Routing to "+globalSocket.replicaIP+":"+globalSocket.replicaPort);
                os.writeUTF(globalSocket.replicaIP);
                os.writeInt(globalSocket.replicaPort);
            } else {
            	System.out.println("!!! NO REPLICAS FOUND !!!");
                os.writeUTF("NONEFOUND");
                os.writeInt(-1);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
