package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HealthCheckService implements Runnable{

    private final int healthCheckPort; 

    public HealthCheckService(int healthCheckPort)
    {
        this.healthCheckPort = healthCheckPort; 
    }

    @Override
    public void run(){
        try(ServerSocket serverSocket = new ServerSocket(healthCheckPort))
        {
            System.out.println("Health Check Service listening on port: " + healthCheckPort);

            while(true)
            {
                try(Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                    String request = in.readLine(); 
                    if("PING".equals(request))
                    {
                        out.println("OK");
                    }
                }catch(Exception e)
                {
                    System.err.println("Health Check Service encountered an error: " + e.getMessage());
                }
            }
        }catch (Exception e) {
            System.err.println("Failed to start Health Check Service: " + e.getMessage());
        }
    }

}
