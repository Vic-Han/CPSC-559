package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args)
    {
        int portNumber = 1972; //the port the server is on (each should probably be hardcoded if we are just running on separate machines)
        
        try(ServerSocket serverSocket = new ServerSocket(portNumber);
        Socket clientSocket = serverSocket.accept()){ 
            new Runner(clientSocket).start(); // spwan a runner thread to serve the client; //client communicaiton with server uses Runner to interpret commands
        }catch(Exception e)
        {
            System.out.println("Server Exception: " + e.getMessage());
        }
    }
}
