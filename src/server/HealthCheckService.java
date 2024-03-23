package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HealthCheckService implements Runnable{

    private final int healthCheckPort; 
    private final Server server; 
    private final int leaderPort; //i.e., the main service port 1972 
    ServerActionNotifier notifier; 

    //Exponential backoff parameters (exponential backoff in case of consecutive health check failures to avoid overwhelming the leader)
    private long backoffTime = 1000; //1second (1000 milliseconds)
    private final long maxBackoffTime = 32000; //maximum backoff time = 32 seconds (32,000 milliseconds)
    private final double backoffMultiplier = 2.0; //Multiplier to increase backoff time 


    public HealthCheckService(int healthCheckPort, Server server, int leaderPort, ServerActionNotifier notifier)//, String leaderAddress)
    {
        this.healthCheckPort = healthCheckPort; 
        this.server = server;
        this.leaderPort = leaderPort;
        this.notifier = notifier;
        //this.leaderAddress = leaderAddress; 

    }

    @Override
    public void run() {
        // Listening for health check pings on healthCheckPort
        listenForPings();

        // Periodically check the health of the leader server
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(backoffTime); // Wait before checking again
                if (checkLeaderHealth()) {
                    // If health check is successful, reset backoff time
                    backoffTime = 1000;
                } else {
                    // Increase backoff time upon failure
                    backoffTime = Math.min(backoffTime * (long) backoffMultiplier, maxBackoffTime);
                    server.detectLeaderFailure(); // Notify the server of leader failure
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
            } catch (Exception e) {
                System.err.println("Error during leader health check: " + e.getMessage());
                backoffTime = Math.min(backoffTime * (long) backoffMultiplier, maxBackoffTime);
            }
        }
    }

    private void listenForPings() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(healthCheckPort)) {
                System.out.println("Health Check Service listening on port: " + healthCheckPort);
                while (true) {
                    try (Socket socket = serverSocket.accept();
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String request = in.readLine();
                        if ("PING".equals(request)) {
                            out.println("OK");
                        }
                    } catch (Exception e) {
                        System.err.println("Error in health check ping listener: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to start health check ping listener: " + e.getMessage());
            }
        }).start();
    }

    private String retrieveLeaderAddress()
    {
        String leaderFullAddress = notifier.requestLeaderDetails(); 
        String parts[] = leaderFullAddress.split(":"); 
        return parts[0];
    }

    // @Override
    // public void run(){
    //     String leaderAddress = LeaderState.getLeaderAddress(); 

    //     try(ServerSocket serverSocket = new ServerSocket(healthCheckPort))
    //     {
    //         System.out.println("Health Check Service listening on port: " + healthCheckPort);

    //         while(true)
    //         {
    //             try(Socket socket = serverSocket.accept();
    //             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    //             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
    //                 String request = in.readLine(); 
    //                 if("PING".equals(request))
    //                 {
    //                     out.println("OK");
    //                 }
    //             }catch(Exception e)
    //             {
    //                 System.err.println("Health Check Service encountered an error: " + e.getMessage());
    //                 server.detectLeaderFailure(); //called when a leader is obviously offline (which should be here as it couldnt respond and gets an exception)
    //             }
    //         }
    //     }catch (Exception e) {
    //         System.err.println("Failed to start Health Check Service: " + e.getMessage());
    //     }
    // }

    private boolean checkLeaderHealth() {
        // Use ServerActionNotifier to request current leader details
        String leaderFullAddress = notifier.requestLeaderDetails();
        if (leaderFullAddress == null || leaderFullAddress.isEmpty()) {
            System.err.println("Failed to obtain leader address for health check.");
            return false;
        }

        String[] parts = leaderFullAddress.split(":");
        String leaderAddress = parts[0];
        int leaderHealthCheckPort = Integer.parseInt(parts.length > 1 ? parts[1] : "1973"); // Default to 1973 if not specified

        try (Socket leaderSocket = new Socket(leaderAddress, leaderHealthCheckPort);
             PrintWriter out = new PrintWriter(leaderSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(leaderSocket.getInputStream()))) {
            out.println("PING");
            String response = in.readLine();
            return "OK".equals(response);
        } catch (Exception e) {
            System.out.println("Failed to connect to leader for health check: " + e.getMessage());
            return false;
        }
    }
}

    // private boolean checkLeaderHealth()
    // {
    //     String leaderFullAddress = notifier.requestLeaderDetails();//retrieveLeaderAddress(); 
    //     String[] parts = leaderFullAddress.split(":"); 
    //     String leaderAddress = parts[0];
    //     int leaderHealthCheckPort = 1973; 
        
    //     try(Socket leaderSocket = new Socket(leaderAddress, leaderHealthCheckPort);
    //         PrintWriter out = new PrintWriter(leaderSocket.getOutputStream(), true);
    //         BufferedReader in = new BufferedReader(new InputStreamReader(leaderSocket.getInputStream()))){
    //             out.println("PING");
    //             String response = in.readLine();
    //             return "OK".equals(response);

    //         }catch(Exception e)
    //         {
    //             System.out.println("Failed to connect to leader for health check: " + e.getMessage());
    //             return false;
    //         }
    // }





