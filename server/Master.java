import java.io.*;
import java.net.*;

// class responsible for load balancing and managing workers
public class Master {
    public static void main(String a[]) throws IOException {
        int port = 1969; 
        Socket s;
        System.out.println("Initiating Server...");

        // Create a server socket
        try (ServerSocket ss = new ServerSocket(port)) {
            // daemon like thing
            while (true) {
                s = ss.accept(); // accept a new client connection
                new Runner(s).start(); // spwan a runner thread to serve the client
            }
        }
    }
}
