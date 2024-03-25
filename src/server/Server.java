package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import Utilities.codes;
import server.ServerState.State;
import Utilities.LeaderChangeNotification;
import Utilities.ServicePorts;

public class Server {
    private boolean isMainServiceRunning = false; 
    private boolean isFilePropagationServiceRunning = false; 
    private final int mainServicePort = ServicePorts.MAIN_SERVICE_PORT;
    private final int healthCheckPort = ServicePorts.HEALTH_CHECK_PORT;
    private final int managementPort = ServicePorts.MANAGEMENT_PORT; 
    private final int filePropagationPort = ServicePorts.FILE_PROPAGATION_PORT; 
    //private volatile ServerState serverState = ServerState.FOLLOWER; //volatile tells compiler that value of serverState may change at any time without any action being taken by the code the compiler finds nearby 
    private volatile ServerState serverState;
    private final String PREPEND = "C:\\CPSC559Proj\\SERVERFILES\\";
    private String thisServersAddress; 
    private ServerActionNotifier actionNotifier;
    

    public Server(String thisServersAddress)
    {
        this.thisServersAddress = thisServersAddress; //so that we can track who the leader is
    }

    //TODO: determine if we need this at any point; as of now we don't
    // // This method might be called directly by HealthCheckService if direct communication is set up
    // // to signal a detected leader failure. However, the primary action after such detection
    // // is to wait for the LoadBalancer to notify of the new leader.
    // public void detectLeaderFailure() {
    //     System.out.println("Leader failure detected. Awaiting new leader info from LoadBalancer.");
    //     // The actual update for the new leader will be handled in handleNewLeader(),
    //     // which is triggered by incoming management messages.

    //     //TODO: IMPLEMENT LOGIC FOR THIS
    // }

    private void startManagementListener() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(managementPort)) {
                System.out.println("Listening for management commands on port: " + managementPort);
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                        Object object = in.readObject();
                        if (object instanceof LeaderChangeNotification) {
                            LeaderChangeNotification notification = (LeaderChangeNotification) object;
                            processLeaderChangeNotification(notification);
                        }
                        else{
                            //handle other types of notifications if we need 
                        }
                    } catch (Exception e) {
                        System.err.println("Error handling management connection: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to start management listener: " + e.getMessage());
            }
        }, "ManagementListenerThread").start();
    }


    private void processLeaderChangeNotification(LeaderChangeNotification notification) {
        if (notification.getMessageType() == LeaderChangeNotification.MessageType.LEADER_CHANGE_NOTIFICATION) {
            // Determine the new state based on whether this server's address matches the leader address in the notification
            ServerState.State newState = notification.getLeaderAddress().equals(thisServersAddress) ? ServerState.State.LEADER : ServerState.State.FOLLOWER;
            
            // Call updateServerState with the new state and the leader's address from the notification
            updateServerState(newState, notification.getLeaderAddress());
        }
    }


    private void startFilePropagationListener(){
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(filePropagationPort)) {
                System.out.println("Listening for file propagation on port " + filePropagationPort);
                while(true)
                {
                    try(Socket clientSocket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())){

                        String fileName = in.readUTF(); 
                        int fileOwnerID = in.readInt();
                        long fileSize = in.readLong(); 
                        String checksum = in.readUTF(); 

                        File receivedFile = new File(PREPEND + fileName); 
                        receivedFile.createNewFile(); 

                        //Handle incoming file transfer. 
                        try(FileOutputStream fileOut = new FileOutputStream(receivedFile)){

                            byte[] buffer = new byte[4096]; 
                            int read; 

                            while(fileSize > 0 && (read = in.read(buffer, 0, Math.min(buffer.length, (int) fileSize))) != -1)
                            {
                                fileOut.write(buffer, 0, read); 
                                fileSize -= read; 
                            }
                            
                        }

                        //Verify checksum
                        try{
                        //Verify the checksum received from the leader server by verifying it (calculating checksum on file created (received) and comparing to received checksum from leader )
                        boolean isValid = ChecksumUtil.verifyChecksum(receivedFile, checksum);
                        //handle cases based on checksum result 
                        if(!isValid){
                            System.err.println("Received file checksum is invalid.");
                            //TODO: HANDLE INVALID CHECKSUM (i.e., failed propagaiton)
                            out.writeByte(codes.FILEPROPAGATIONFAILURE);
                        }
                        else{
                            System.out.println("File " + fileName + " received and verified.");
                            out.writeByte(codes.FILEPROPAGATIONSUCCESS);

                            //Notify load balancer server? or let leader notify after all success. Probably let leader notify. 
                        }


                        }catch(Exception e)
                        {
                            System.err.println("Error generating checksum for file: " + fileName + ". Error: " + e.getMessage());
                            //TODO: Handle the error, e.g., notify the client of failure, log the error, retry, etc. 
                        }



                    }catch(IOException e)
                    {
                        System.err.println("Failed to start file propagation listener: " + e.getMessage());
                        // Additional startup error handling logic
                        //TODO: log/handle exception 
                    }
                }

            }catch(IOException e)
            {
                //TODO: log/handle exception here
            }




        }, "FilePropagationListenerThread").start(); // Naming the thread for easier identification
    }

    private void startMainService(){
        isMainServiceRunning = true; //so we don't start multiple instances of this
        new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(mainServicePort)){
                System.out.println("Server listening on port: " + mainServicePort);
    
                while(true) //server can accept multiple clients on 1 port 
                {
                    if(this.serverState.getState() == State.LEADER) //only actually allow the client to connect to the LEADER server
                    {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new Runner(clientSocket, actionNotifier)).start(); // spawn a runner thread to serve the client
                    }
                }
               
            }catch(Exception e)
            {
                System.out.println("Server Exception: " + e.getMessage());
            }
        }, "MainServiceThread").start();
    }

    public void initializeServerState() {
        String leaderAddress = actionNotifier.requestLeaderDetails(); // Request the current leader's address
        
        if (thisServersAddress.equals(leaderAddress)) {
            // This server is the leader
            updateServerState(ServerState.State.LEADER, leaderAddress);
        } else {
            // This server is a follower
            updateServerState(ServerState.State.FOLLOWER, leaderAddress);
        }
    }

    public void updateServerState(ServerState.State newState, String newLeaderAddress) {
        ServerState.getInstance().setState(newState);
        if (newState == ServerState.State.LEADER) {
            if (!isMainServiceRunning) {
                startMainService();
                isMainServiceRunning = true;
            }
        // Update the leader's address when this server becomes the leader
        newLeaderAddress = thisServersAddress;

        } else if (newState == ServerState.State.FOLLOWER) {
            if (isMainServiceRunning) {
                // This shouldn't ever be reached with our current setup for leader transitions (as previous leader coming back online will not become the leader again)
                isMainServiceRunning = false;
            }
            if (!isFilePropagationServiceRunning) {
                startFilePropagationListener();
                isFilePropagationServiceRunning = true;
            }
        }

        // Update the leader's address when transitioning to a follower
        LeaderState.setLeaderAddress(newLeaderAddress);
    }

    private void startHealthCheckService() {
        new Thread(new HealthCheckService(this,  actionNotifier), "HealthCheckServiceThread").start();
    }

    public void start()
    {
        actionNotifier = new ServerActionNotifier(); 
        // Start the management listener to listen for leader change notifications
        startManagementListener();

        // Start the main service if this server is the leader, otherwise start the file propagation listener
        initializeServerState(); // Initialize the server state based on the current leader

        // Start HealthCheckService in a separate thread
        startHealthCheckService();


    }


    public static void main(String[] args)
    {
        //should probably parse from args the server address so we can do other stuffs (for now I'll hardcode to 127.0.0.1 but we need to change that )
            //TODO: parse args to get correct server IP addr 
        String thisAddress = "127.0.0.1";


        new Server(thisAddress).start();
    }

}
