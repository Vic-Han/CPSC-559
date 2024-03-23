package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Utilities.codes;

public class ServerActionNotifier {
    List<String> serversToShareTo; 
    //notifies load balancer after completing an action 
    // public List<String> notifyLoadBalancerDownload(String loadBalancerAddress, int loadBalancerPort)
    // {
    //     try(Socket socket = new Socket(loadBalancerAddress, loadBalancerPort);
    //     DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    //     DataInputStream in = new DataInputStream(socket.getInputStream())){

    //         out.writeByte(codes.SERVERSHAREFILEREQUEST); //write byte to let the server know that we want to share and it should give us the servers we need to share to as a response 
    //         int numberOfActiveServers = in.readInt(); 
    //         serversToShareTo.clear(); //clear before trying to add

    //         for(int i = 0; i < numberOfActiveServers; i++)
    //         {
    //             String serverToAdd = in.readUTF(); //get a server 
    //             serversToShareTo.add(serverToAdd);
    //         }


    //     }catch(Exception e)
    //     {
    //         //couldn't connect to load balancer listener server 
    //     }
    //     return serversToShareTo;
    // }

    // Method which requests leaders address & port from the LoadBalancer
        // Because the HealthCheckService & the ProtocolHandler are using shraed instance of ServerActionNotifier we must make this thread safe (more efficient to share 1 instance)
    public synchronized String requestLeaderDetails()//String loadBalancerAddress, int loadBalancerPort)
    {

        String loadBalancerAddress = "127.0.0.1"; //TODO: FIX THE IP ON THIS LATER (UNLESS WE KEEP IT AT LOCALHOST IDK)
        int loadBalancerPort = 2001; 
        try(Socket socket = new Socket(loadBalancerAddress, loadBalancerPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())){
                out.writeByte(codes.REQUESTLEADERDETAILS);

                return in.readUTF(); //return the leader details string (IP:PORT)
            }catch(Exception e)
            {
                System.err.println("Error requesting leader details: " + e.getMessage());
                return null; // TODO: either return null or have some sort of some error handling here
            }
    }


    //Method which gets active servers, creates List<String> object and returns to caller (ProtocolHandler i.e., Server instance) 
    // Because the HealthCheckService & the ProtocolHandler are using shraed instance of ServerActionNotifier we must make this thread safe (more efficient to share 1 instance)
    public synchronized List<String> requestActiveServers(String loadBalancerAddress, int loadBalancerPort){
        List<String> activeServers = new ArrayList<>(); 
        try (Socket socket = new Socket(loadBalancerAddress, loadBalancerPort);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())){

                //Send request active servers code 
                out.writeByte(codes.REQUESTACTIVESERVERS);
                //Read how many servers in activeServers list maintained by LoadBalancer
                int numberOfActiveServers = in.readInt(); 
                //Since we are using Data Input/Output streams we need to build a list as we can only read primitives :(
                for(int i = 0; i < numberOfActiveServers; i++)
                {
                    activeServers.add(in.readUTF());
                }

            }catch(Exception e)
            {
                //handle exception 
                //logging? 
                //retry mechanism 
            }
            return activeServers; //return built list of currently active servers 
        
    }



}
