package server;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
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
                //os.writeUTF("ACK");
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
            os.writeByte(codes.DOWNLOADRESPONSE);//send response to client so the client can proceed
            String fileName = is.readUTF();
            try{
            File file = new File("content/"+fileName);
            FileInputStream fis = new FileInputStream(file);
            os.writeLong(file.length());
            byte[] buf = new byte[2048];
            int read;
            while ((read = fis.read(buf)) > 0) {
                os.write(buf, 0, read);
            }
            fis.close();
            //os.writeByte(codes.DOWNLOADSUCCESS); 
            }catch(NoSuchFileException f)
            {
                os.writeByte(codes.NOSUCHFILE);
            }
        } catch(IOException e) {
            //os.writeByte(codes.ERR); 
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
            //gui should return the clientID so we can store it in the instance of the client to help with share requests and such later (prevents an extra DB lookup)
            if(true) {
                os.writeByte(codes.LOGINSUCCESS);
            } else {
                os.writeByte(codes.LOGINFAIL);
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
                os.writeByte(codes.REGISTERFAIL);
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
            os.writeByte(codes.SHARERESPONSE); //write response to client to tell them we are starting to handle the share request 
            String filename = is.readUTF();

            //TODO:get file from server if it exists 
            //byte doesFileExist = 
            //if (file does not exist){
                //os.writeByte(codes.NOSUCHFILE);
                //return; 
            //}
            //else{ file must exist
            os.writeByte(codes.FILEEXISTS); //file must exist so write OK
            //}


            //String owner = is.readUTF();
            String sharedTo = is.readUTF();

            //TODO: do checks for if the user exists
            //byte doesUserExist = 
            //if (user does not exist){
                //os.writeByte(codes.NOSUCHUSER);
                //return; 
            //}

            os.writeByte(codes.USEREXISTS); 
            int userID = is.readInt(); //should be valid if gui can check data base , if not we should do checks here on the files and such 
            //Alternatively, we could get the userID of the sharer IF we instantiate it upon login and return it to the ClientLogic (client)


            os.writeByte(codes.SHARESUCCESS); 
            //boolean testValidity = true; 
            //add share permission to database
            // if(testValidity) {
            //     os.writeByte(codes.SHARESUCCESS);
            // } else {
            //     os.writeByte(codes.SHAREFAIL);
            //     //os.writeUTF("Error message");
            // }
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
                return; 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method that is called when the server recieves a request to see all files the user can download
    // should tell the client the file names and permissions(owner/shared w me)
    private void handleGetAllFilesRequest(int userID, String ClientIP, int ClientPort) {

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
