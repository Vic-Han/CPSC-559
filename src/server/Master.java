package server;
import java.io.*;
import java.net.*;

// class responsible for load balancing and managing workers
public class Master {
    /**
     * The main method of the server
     * 
     * @param a command line arguments
     * @throws IOException
     */
    public static void main(String a[]) throws IOException {
        int port = 1969; 
        Socket s;
        System.out.println("Initiating Server...");

        // Create a server socket
        try (ServerSocket ss = new ServerSocket(port)) {
            // daemon like thing
            while (true) {
            	System.out.println("Waiting for new connection");
                s = ss.accept(); // accept a new client connection
                new Runner(s).start(); // spwan a runner thread to serve the client
            }
        }
    }
}
