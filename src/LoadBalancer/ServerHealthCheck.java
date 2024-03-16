package LoadBalancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

//Runnable allows multiple Asynchronous executions (i.e., Parallel Execution to reduce time to check all servers)
//Using separate threads for health checks also ensures these calls are non-blocking (i.e., don't slow or stop main thread executions)
//Runnable allows us to use 'ScheduledExecutorService' for regular interval checks (for round robin)
//Can be re-used for other asynchronous checks (not just health checks) later
public class ServerHealthCheck implements Runnable{
    private String serverAddress; //the address of the server to ping/check
    private LoadBalancer loadBalancer; //instance of the load balancer so we can actually remove failed servers from the current list and such

    public ServerHealthCheck(String serverAddress, LoadBalancer loadBalancer)
    {
        this.serverAddress = serverAddress; 
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void run() {

        //Health check logic here (connect to socket, ping server, get response, react)
        try{
            boolean isServerUp = checkServerHealth(serverAddress);
            if(!isServerUp){ //if the server isn't up then we should remove it from the list of active (up) servers
                loadBalancer.removeFailedServer(serverAddress); 
            }
            else{//otherwise, it didn't fail so thus it should be in the server list of active servers (which we will check if already in list in loadBalancer)
                loadBalancer.addRecoveredServer(serverAddress);
            }
        }catch(Exception e){
            e.printStackTrace(); //probably return a better error code later once I know how to handle this better 
            loadBalancer.removeFailedServer(serverAddress); //if we get an exception we should still remove it as it had to have failed to throw this
        }

        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    private boolean checkServerHealth(String serverAddress) //server address can be a string in the format: 127.0.0.1:6969 and just split it (saves stress of using Triple data struct)
    {

        //Split serverAddress into hostname and port 
        String[] parts = serverAddress.split(":"); 
        String host = parts[0]; //get the host (IP)
        int port = Integer.parseInt(parts[1]); //get the port 

        try (Socket socket = new Socket())
        {
            //connect with timeout to avoid hanging if server isn't active 
            socket.connect(new InetSocketAddress(host, port), 5000); //timeout currently set to 5000 milisecond (5 Seconds)

            //ping server 
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("PING"); //send ping string
            String response = in.readLine(); //get response 
            return "PONG".equals(response); //if the server says PONG as a response we check and evaluate to set the bool 



        }catch(IOException e)
        {
            //connection failed
            return false; 
        }
    }

}
