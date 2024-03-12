package server;
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
<<<<<<< Updated upstream
=======
            // contact load balancer to declare server for use
            System.out.println("Connecting to load balancer");
            String lbip = "localhost";
            int lbport =  1970;
            Socket lb = new Socket(lbip, lbport);
            DataOutputStream lbos = new DataOutputStream(lb.getOutputStream());
            DataInputStream lbis = new DataInputStream(lb.getInputStream());
            lbos.writeByte(codes.SERVERSTARTREQUEST);
            long time = lbis.readLong();
            System.out.println("Connected to LB with time + "+time);
            lbos.writeUTF(ss.getInetAddress().getHostAddress());
            lbos.writeInt(ss.getLocalPort());
            LBConnectionHandler lbch = new LBConnectionHandler(lbis, lbos);
            lbch.start();
            System.out.println("Registered.");
>>>>>>> Stashed changes
            // daemon like thing
            while (true) {
            	System.out.println("Waiting for new connection MASTER");
                //s = ss.accept(); // accept a new client connection
                Socket testSocket = ss.accept(); 
                //new Runner(s).start(); // spwan a runner thread to serve the client
                new Runner(testSocket).start(); // spwan a runner thread to serve the client
            }
        }
    }
}
