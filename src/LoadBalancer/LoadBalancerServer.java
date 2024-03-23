package LoadBalancer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import Utilities.codes;

public class LoadBalancerServer {
    private LoadBalancer loadBalancer; 
    private int listenPort; 

    public LoadBalancerServer(LoadBalancer loadBalancer, int listenPort)
    {
        this.loadBalancer = loadBalancer; 
        this.listenPort = listenPort; 
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(listenPort)){

            System.out.println("Load Balancer listening on port: " + listenPort);
            
            while(true)
            {
                try(Socket clientSocket = serverSocket.accept();
                DataInputStream is = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream())){
                //PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)){ 
                //String serverAddress = loadBalancer.getNextServer(); 

                //Expect specific request type "GET_ACTIVE_SERVERS"
                //String command = (String) is.readObject(); 
                byte command = is.readByte(); 
                if(command == codes.GETACTIVESERVERS)
                {
                    List<String> activeServers = loadBalancer.getActiveServers(); 
                    os.writeInt(activeServers.size());
                    for(String serverAddress : activeServers)
                    {
                        os.writeUTF(serverAddress);
                    }
                }
                //os.writeUTF(serverAddress); 
                }catch(Exception e)
                {
                    System.out.println("An error occured while handling client request: " + e.getMessage()); 
                }
                
            }

        }catch(Exception e)
        {
            System.out.println("Load Balancer failed to start: " + e.getMessage()); 
        }
    }

}
