package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import Utilities.codes;

public class ServerActionNotifier {
    List<String> serversToShareTo; 
    //notifies load balancer after completing an action 
    public List<String> notifyLoadBalancerDownload(String loadBalancerAddress, int loadBalancerPort)
    {
        try(Socket socket = new Socket(loadBalancerAddress, loadBalancerPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream())){

            out.writeByte(codes.SERVERSHAREFILEREQUEST); //write byte to let the server know that we want to share and it should give us the servers we need to share to as a response 
            int numberOfActiveServers = in.readInt(); 
            serversToShareTo.clear(); //clear before trying to add

            for(int i = 0; i < numberOfActiveServers; i++)
            {
                String serverToAdd = in.readUTF(); //get a server 
                serversToShareTo.add(serverToAdd);
            }



        }catch(Exception e)
        {
            //couldn't connect to load balancer listener server 
        }
        return serversToShareTo;
    }

}
