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

public class Server {
    private int mainServicePort;
    private int healthCheckPort; 
    private final int managementPort = 1984; 
    private final int filePropagationPort = 1985; 
    private AtomicInteger currentTerm = new AtomicInteger(0); //sync stuff
    private volatile int votedFor = -1; //volatile tells compiler that value of votedFor may change at any time without any action being taken by the code the compiler finds nearby 
    private volatile ServerState serverState = ServerState.FOLLOWER; //volatile tells compiler that value of serverState may change at any time without any action being taken by the code the compiler finds nearby 
    private String currentLeaderAddress; 
    private final String PREPEND = "C:\\CPSC559Proj\\SERVERFILES\\";

    //Used to track if the main server (leader) has crashed/is not online 
    private AtomicBoolean isLeaderDown = new AtomicBoolean(false);
    

    public Server(int mainServicePort, int healthCheckPort)
    {
        this.mainServicePort = mainServicePort; 
        this.healthCheckPort = healthCheckPort;
    }

    // This method might be called directly by HealthCheckService if direct communication is set up
    // to signal a detected leader failure. However, the primary action after such detection
    // is to wait for the LoadBalancer to notify of the new leader.
    public void detectLeaderFailure() {
        System.out.println("Leader failure detected. Awaiting new leader info from LoadBalancer.");
        // The actual update for the new leader will be handled in handleNewLeader(),
        // which is triggered by incoming management messages.

        //TODO: IMPLEMENT LOGIC FOR THIS
    }

    private void handleNewLeader(String newLeaderAddress) {
        //this.currentLeaderAddress = newLeaderAddress; // Update the current leader's address
        LeaderState.setLeaderAddress(newLeaderAddress);
        System.out.println("Updated current leader to: " + newLeaderAddress);
        // TODO: dd any additional logic needed to reconfigure the server in response to the new leader
    }

    private void startManagementListener() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(managementPort)) {
                System.out.println("Listening for management commands on port: " + managementPort);
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                        Object object = in.readObject();
                        if (object instanceof RPCMessage) {
                            RPCMessage message = (RPCMessage) object;
                            if (message.getMessageType() == RPCMessage.MessageType.NEW_LEADER) {
                                handleNewLeader(message.getLeaderAddress());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error handling management connection: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to start management listener: " + e.getMessage());
            }
        }).start();
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
                        //TODO: log/handle exception 
                    }
                }

            }catch(IOException e)
            {
                //TODO: log/handle exception here
            }




        }).start();
    }

    public void start()
    {
        ServerActionNotifier actionNotifier = new ServerActionNotifier(); 
        //int mainServicePort = 1972; //the port the server is on (each should probably be hardcoded if we are just running on separate machines)
        new Thread(new HealthCheckService(healthCheckPort, this, mainServicePort, actionNotifier)).start(); //leader port is the same regardless of which machine hosting (only IP differs for main service socket connections)
        startManagementListener();

        if(this.serverState != ServerState.LEADER)
        {
            //TODO: implement non leader logic?  
            startFilePropagationListener();
        }

        
        try(ServerSocket serverSocket = new ServerSocket(mainServicePort)){
            System.out.println("Server listening on port: " + mainServicePort);

            while(true)//server can accept multiple clients on 1 port 
            {
                Socket clientSocket = serverSocket.accept();
                if(this.serverState == ServerState.LEADER) //only actually allow the client to connect to the LEADER server
                {
                    new Thread(new Runner(clientSocket, actionNotifier)).start(); // spwan a runner thread to serve the client; //client communicaiton with server uses Runner to interpret commands
                }
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

        new Server(mainServicePort, healthCheckPort).start();


    }


    //TODO: REMOVE OLD CODE IF NEW CODE WORKS
     // public void detectLeaderFailure(){
    //     //Method should be called by HealthCheckService.java when it detects the leader is down 
    //     isLeaderDown.set(true); 
    //     //Check if server should start an election 
    //     if(serverState != serverState.LEADER)
    //     {
    //         startElection(); 
    //     }
    // }

    // private synchronized void startElection(){
    //     serverState = ServerState.CANDIDATE; 
    //     currentTerm.incrementAndGet(); //increment current term (thread safe method)
    //     votedFor = 0; //Assuming this servers ID is 0 for example TODO: (should probably have unique ID THAT WE IMPLEMENT )

    //     System.out.println("Server starting election for term: " + currentTerm.get());

    //     //Election Logic
    // }

    // private void startManagementListener()
    // {
    //     try(ServerSocket serverSocket = new ServerSocket(managementPort)){
    //         System.out.println("Listening for management commands on port: " + managementPort);
    //         while(true)
    //         {
    //             try (Socket clientSocket = serverSocket.accept();   
    //                 //DataInputStream in = new DataInputStream(clientSocket.getInputStream())){
    //                 // byte command = in.readByte(); 
    //                 // if(command == codes.NEWLEADERNOTIFICATION)
    //                 // {
    //                 //     String newLeaderAddress = in.readUTF(); 
    //                 //     handleNewLeader(newLeaderAddress);
    //                 // }
    //                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
    //                 RPCMessage message = (RPCMessage) in.readObject();
    //                 if (message.getMessageType() == RPCMessage.MessageType.NEW_LEADER) {
    //                 handleNewLeader(message.getLeaderAddress());
    //                 }
    //             }catch(Exception e)
    //             {
    //                 System.out.println("Management Listener Error: " + e.getMessage());
    //                 //TODO: HANDLE EXCEPTION 
    //             }

    //         }
    //     }catch(Exception e)
    //     {
    //         System.err.println("Failed to start Management Listener: " + e.getMessage());
    //         // TODO: Handle exception
    //     }
    // }
}
