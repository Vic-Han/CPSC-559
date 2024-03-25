package LoadBalancer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import Utilities.ServicePorts;
import Utilities.codes;

public class LoadBalancerServer {
    private LoadBalancer loadBalancer; 
    private final int listenPort = ServicePorts.LOAD_BALANCE_SERVER_PORT; 

    public LoadBalancerServer(LoadBalancer loadBalancer)
    {
        this.loadBalancer = loadBalancer; 
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(listenPort)){

            System.out.println("Load Balancer listening on port: " + listenPort);
            
            while(true)
            {
                try(Socket clientSocket = serverSocket.accept();
                DataInputStream is = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream())){

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
