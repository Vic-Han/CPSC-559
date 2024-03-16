package LoadBalancer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerCommunicationService implements Runnable {
    private String serverAddress; 
    private int port; 

    //basic constructor
    public ServerCommunicationService(String serverAddress, int port)
    {
        this.serverAddress = serverAddress;
        this.port = port; 
    }

    @Override
    public void run(){
        //TODO: implement server communcation logic
            //health checks & sending/receiving commands/updates

        try{
            Socket socket = new Socket(serverAddress, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
            DataInputStream in = new DataInputStream(socket.getInputStream());


            //do the actual logic here for health checks & updates
            System.out.println("Communicating with server: " + serverAddress);
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed to connect to the server at " + serverAddress);
        }
    }

}
