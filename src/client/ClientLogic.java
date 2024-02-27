package client;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;     
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import Utilities.Pair;



// class that is responsible for handling the logic of the client
// class is static because it is not meant to be instantiated by several threads, may change later
public class ClientLogic {

    private static ObjectInputStream is;
    private static ObjectOutputStream os;
    private static String masterIP;
    private static int masterPort;
    // private static int userID;


    public void setIO(ObjectOutputStream os, ObjectInputStream is) {
        this.os = os;
        this.is = is;
    }
    public void setMaster(String masterIP, int masterPort) {
        this.masterIP = masterIP;
        this.masterPort = masterPort;
    }
    // method that is called when the user chooses to download a file
    // socket messages will be sent by helper methods defined below
    // gui should not allow user to download a file that they do not have access to
    public static void fileUpload(String filePath, int userID) {
        //send to load balancer request for server
    	//receive response from server
    	//create new socket from response message
    	//
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
    // method that is called when the user chooses to download a file
    // socket messages will be sent by helper methods defined below
    // gui should not allow user to download a file that they do not have access to
    public static void fileDownload(String filePath, int userID) {
        
    }
    // socket messages should be sent and recived here
    // method that is called when user tries to login
    // returns userID
    public static int loginRequest(String username, String password) {

        return -1;
    }
  
    // method that is called when user tries to register
    // socket messages should be sent and recived here
    // returns userID
    public static int registerRequest(String username, String password) {

        return -1;
    }
    // method that is called when user tries to delete a file
    // socket messages should be sent and recived here
    public static void deleteRequest(String filePath, int userID) {

    }
    // method that is called when user tries to share a file
    // socket messages should be sent and recived here
    public static void shareRequest(String fileName, String sharedUser, int userID) {

    }
    // method that is called when user tries to unshare a file
    // socket messages should be sent and recived here
    public static void unshareRequest(String fileName, String sharedUser, int userID) {

    }
    // method that is called when user wants to see all files they can download
    // returns a list of pairs; first entry is the file name, second entry is the owner/permission(owner/shared w me)
    public static ArrayList<Pair<String, Integer>> getAllFiles(int userID) {

        return null;
    }

    // Todo: implement some type of authentication token to talk to workers

    // asks the load balancer/master for a worker that can accept the upload
    // returns the worker IP and port number
    // socket messages should be sent and recived here
    private static Pair<String, Integer> sendUploadRequest(String fileName){
        //changed int to Integer as java must use a wrapper (Integer) for generic types rather than primitive (int)
            //src: https://stackoverflow.com/questions/34885463/insert-dimensions-to-complete-expression-referencetype
        return null;
    }

    // method that uploads file to worker
    // socket messages should be sent and recived here    
    private static void uploadFiletoWorker(String fileName, String workerIP, int portNo){

    }

    // method that tells the load balancer/master that the file has been uploaded successfully
    // implement for future demo when we cannot assume process is successful
    private static void sendSuccessUploadMessage(String fileName, int userID){

    }

    //  asks the load balancer/master for a worker that has the file
    // returns the worker IP and port number
    // socket messages should be sent and recived here
    private static Pair<String, Integer> sendDownloadRequest(String fileName){

        return null;
    }
    // method that downloads file from worker
    // puts the file in local(client) file system and the user can download it from there
    // may need to change UX mentioned above
    // socket messages should be sent and recived here
    private static void downloadFileFromWorker(String fileName, String workerIP, int portNo){

    }

}
