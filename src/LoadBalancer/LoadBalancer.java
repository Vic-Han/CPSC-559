package LoadBalancer;

import java.util.ArrayList;
import java.io.*;
import java.net.*;
import Utilities.*;


public class LoadBalancer {

    protected static ArrayList<Triple<Socket, String, Integer>> replicas;
    public static void main(String[] args) throws IOException{
        int port = 1970; 
        Socket s;
        long time = System.currentTimeMillis();
        replicas = new ArrayList<Triple<Socket, String, Integer>>();
        System.out.println("Initiating Load Balancer. Time = "+time);
        // Create a server socket
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
            	System.out.println("Waiting for new connection");
                s = ss.accept(); // accept a new client connection
                DataInputStream is = new DataInputStream(s.getInputStream());
                DataOutputStream os = new DataOutputStream(s.getOutputStream());
                byte systemType = is.readByte();
                if(systemType == Utilities.codes.SERVERSTARTREQUEST) {
                    System.out.println("New server request");
                    os.writeLong(time);
                    String repip = is.readUTF(); //get hostname being used by server
                    int repport = is.readInt(); //get port being used by server
                    System.out.println("Host:port "+repip+":"+repport);
                    Triple<Socket, String, Integer> toAdd = new Triple<Socket, String, Integer>(s, repip, repport);
                    replicas.add(toAdd);
                } else if (systemType == Utilities.codes.CLIENTROUTEREQUEST) {
                	System.out.println("Client routing request");
                    routeClient(s);
                }
                s.close();
            }
        }
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
