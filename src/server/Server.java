package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int mainServicePort; 

    public Server(int mainServicePort)
    {
        this.mainServicePort = mainServicePort; 
    }

    public void start()
    {
        //int mainServicePort = 1972; //the port the server is on (each should probably be hardcoded if we are just running on separate machines)
        
        try(ServerSocket serverSocket = new ServerSocket(mainServicePort)){
            System.out.println("Server listening on port: " + mainServicePort);

            while(true)//server can accept multiple clients on 1 port 
            {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Runner(clientSocket)).start(); // spwan a runner thread to serve the client; //client communicaiton with server uses Runner to interpret commands
            }
           
        }catch(Exception e)
        {
            System.out.println("Server Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        int mainServicePort = 1972; 
        int healthCheckPort = 1973; 

        //Start dedicated health check service server 
        new Thread(new HealthCheckService(healthCheckPort)).start();

        new Server(mainServicePort).start();


    }
}
