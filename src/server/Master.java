package server;
import java.io.*;
import java.net.*;
import Utilities.codes;

// class responsible for load balancing and managing workers
public class Master {
    public static void main(String a[]) throws IOException {
        int port = 1969; 
        Socket s;
        System.out.println("Initiating Server...");

        // Create a server socket
        try (ServerSocket ss = new ServerSocket(port)) {
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
            lbos.writeUTF("localhost");//HARDCODED FOR NOW - USE CLI
            lbos.writeInt(ss.getLocalPort());
            //LBConnectionHandler lbch = new LBConnectionHandler(lb);
            //lbch.start();
            lb.close();
            System.out.println("Registered.");
            // daemon like thing
            while (true) {
            	System.out.println("Waiting for new connection");
                s = ss.accept(); // accept a new client connection
                new Runner(s).start(); // spwan a runner thread to serve the client
            }
        }
    }
}