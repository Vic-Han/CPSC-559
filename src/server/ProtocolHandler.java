package server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Utilities.*;
import server.MasterParser;
// instance based as it may be used by multiple threads
public class ProtocolHandler {
    ObjectInputStream is;
    ObjectOutputStream os;


    public ProtocolHandler(ObjectOutputStream os, ObjectInputStream is) {
        this.os = os;
        this.is = is;
    }

    // method that is called when the server recieves a message
    // should look at the message code and call appropriate methods
    // for each message it needs to call the static parser corresponding to the message type
    // it should then deconstruct the the tuple and use it to call the appropriate handler defined below
    public void handleMessage(Message message) {
    	if(message.getMessageType() == 4) {
    		//parse
    		//handleUploadRequest
    		Pair<Long, String> data = MasterParser.workerParseUpload(message.getMessageData(), message.getDataSizes());
    		workerHandleUploadRequest(data.first.longValue(), data.second);
    	}
    }

    // method that is called when the server recieves a request to upload a file
    // should tell the client the worker IP and port number
    // should give the client an authentication token to talk to the worker(future)
    private void handleUploadRequest(String fileName, int userID, String ClientIP, int ClientPort) {
    	
    }
    
    private void workerHandleUploadRequest(long size, String filename) {
    	FileOutputStream fos;
    	long totalRead = 0;
		try {
			fos = new FileOutputStream("content/"+filename);
			while(true) {
				Message newData = (Message)is.readObject();
				byte[] dataToWrite = newData.getMessageData();
				totalRead += dataToWrite.length;
				fos.write(dataToWrite);
				if(totalRead >= size) break;
			}
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
	    } catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
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
    	// parse message for length and filename
    	
    	// zach's code
    	/*
    	 	int read;
            while ((read = fis.read(buf)) > 0) {
                out.write(buf, 0, read);
                // Wait for ACK
                String ack = in.readUTF();
                if (!"ACK".equals(ack)) {
                    System.out.println("Error in transmission, stopping.");
                    break;
                }
            }
    	 */
    }

    // method that is called when the server recieves a request to login
    // should return the userID to the client, -1 on failure
    private void handleLoginRequest(String username, String password, String ClientIP, int ClientPort) {
        
    }

    // method that is called when the server recieves a request to register
    // should return the userID to the client, -1 on failure
    private void handleRegisterRequest(String username, String password, String ClientIP, int ClientPort) {

    }

    // method that is called when the server recieves a request to share a file
    // should return a success or failure message to the client
    private void handleShareRequest(String fileName, String sharedUser, int userID, String ClientIP, int ClientPort) {

    }

    // method that is called when the server recieves a request to unshare a file
    // should return a success or failure message to the client
    private void handleUnshareRequest(String fileName, String sharedUser, int userID, String ClientIP, int ClientPort) {

    }

    // method that is called when the server recieves a request to see all files the user can download
    // should tell the client the file names and permissions(owner/shared w me)
    private void handleGetAllFilesRequest(int userID, String ClientIP, int ClientPort) {

    }

    // method that is called when the server recieves a request to delete a file
    // should return a success or failure message to the client
    private void handleDeleteRequest(String fileName, int userID, String ClientIP, int ClientPort) {

    }
}
