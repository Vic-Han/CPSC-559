package server;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import Utilities.*;
import server.MasterParser;
// instance based as it may be used by multiple threads
public class ProtocolHandler {
    DataInputStream is;
    DataOutputStream os;


    public ProtocolHandler(DataOutputStream os, DataInputStream is) {
        this.os = os;
        this.is = is;
    }

    // method that is called when the server recieves a request to upload a file
    // should tell the client the worker IP and port number
    // should give the client an authentication token to talk to the worker(future)
    private void handleUploadRequest(String fileName, int userID, String ClientIP, int ClientPort) {
    	
    }
    
    public void workerHandleUploadRequest() {
        try {
            String fileName = is.readUTF();
            long fileSize = is.readLong();
            FileOutputStream fos = new FileOutputStream("content/"+fileName);
            byte[] buf = new byte[2048];
            int read = 0;
            long totalRead = 0;
            while ((read = is.read(buf, 0, Math.min(buf.length, (int) (fileSize - totalRead)))) > 0) {
                totalRead += read;
                fos.write(buf, 0, read);
                // Send ACK for each packet
                os.writeUTF("ACK");
            }
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    // method that is called when the server recieves a request to download a file
    // should tell the client the worker IP and port number that has the download
    private void handleDownloadRequest(String fileName, int userID, String ClientIP, int ClientPort) {
    	//LOAD BALANCER
    	// Get available worker
    	// send info back to client
    	//WORKER
    	
    }

    public void workerHandleDownloadRequest() {
        try {
            String fileName = is.readUTF();
            File file = new File("content/"+fileName);
            FileInputStream fis = new FileInputStream(file);
            os.writeLong(file.length());
            byte[] buf = new byte[2048];
            int read;
            while ((read = fis.read(buf)) > 0) {
                os.write(buf, 0, read);
                // Wait for ACK
                String ack = is.readUTF();
                if (!"ACK".equals(ack)) {
                    System.out.println("Error in transmission, stopping.");
                    break;
                }
            }
            fis.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to login
    // should return the userID to the client, -1 on failure
    public void handleLoginRequest() {
        try{
            String username = is.readUTF();
            String password = is.readUTF();
            System.out.println("Username: " + username + " Password: " + password);
            //if password matches for username
            if(true) {
                os.writeByte(codes.OK);
            } else {
                os.writeByte(codes.ERR);
                os.writeUTF("Error message");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to register
    // should return the userID to the client, -1 on failure
    public void handleRegisterRequest() {
        try{
            String username = is.readUTF();
            String password = is.readUTF();
            System.out.println("Username: " + username + " Password: " + password);
            //Register user and return status
            if(true) {
                os.writeByte(codes.OK);
            } else {
                os.writeByte(codes.ERR);
                os.writeUTF("Error message");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to share a file
    // should return a success or failure message to the client
    public void handleShareRequest() {
        try {
            String filename = is.readUTF();
            String owner = is.readUTF();
            String sharedTo = is.readUTF();
            //add share permission to database
            if(true) {
                os.writeByte(codes.OK);
            } else {
                os.writeByte(codes.ERR);
                os.writeUTF("Error message");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to unshare a file
    // should return a success or failure message to the client
    public void handleUnshareRequest() {
        try {
            String filename = is.readUTF();
            String owner = is.readUTF();
            String sharedTo = is.readUTF();
            //add share permission to database
            if(true) {
                os.writeByte(codes.OK);
            } else {
                os.writeByte(codes.ERR);
                os.writeUTF("Error message");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to see all files the user can download
    // should tell the client the file names and permissions(owner/shared w me)
    public void handleGetAllFilesRequest() {
        //receive username
        //query for all relevant files
        //for all files
            //send name
            //send size
            //send owner
            //send date uploaded?
    }

    // method that is called when the server recieves a request to delete a file
    // should return a success or failure message to the client
    public void handleDeleteRequest() {
        try {
            String filename = is.readUTF();
            String owner = is.readUTF();
            //attempt to delete
            if(true) {
                os.writeByte(codes.OK);
            } else {
                os.writeByte(codes.ERR);
                os.writeUTF("Error message");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
