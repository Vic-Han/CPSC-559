package server;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import Utilities.*;

// instance based as it may be used by multiple threads
public class ProtocolHandler {
    DataInputStream is;
    DataOutputStream os;
    private final String PREPEND = "C:\\CPSC559Proj\\SERVERFILES\\";

    public ProtocolHandler(DataOutputStream os, DataInputStream is) {
        this.os = os;
        this.is = is;
    }

    // method that is called when the server recieves a request to upload a file
    // should tell the client the worker IP and port number
    // should give the client an authentication token to talk to the worker(future)
    // private void handleUploadRequest(String fileName, int userID, String ClientIP, int ClientPort) {
    	
    // }
    
    public void workerHandleUploadRequest() {
        try {
            os.writeByte(codes.UPLOADRESPONSE); //so client knows it can start passing other important information 
            String fileName = is.readUTF();
            long fileSize = is.readLong();
            //create new file incase the file already exists (MAYBE WE SHOULD DO A DATABASE CHECK HERE IDK)
            File file = new File(PREPEND+fileName); 
            file.createNewFile(); //ensures file isn't already there? or just will create a new version idk the exact working of this 
            FileOutputStream fos = new FileOutputStream(file, false); //false to not append if the file already exists within the system

            byte[] buf = new byte[4096]; //4kb buffer
            int read;
            long totalRead = 0;
            while (totalRead < fileSize){//read = is.read(buf, 0, Math.min(buf.length, (int) (fileSize - totalRead)))) > 0) {
                read = is.read(buf, 0, buf.length); //read 
                fos.write(buf, 0, read);
                totalRead += read; 
            }
            fos.close();
            os.writeByte(codes.UPLOADSUCCESS);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    // method that is called when the server recieves a request to download a file
    // should tell the client the worker IP and port number that has the download
    // private void handleDownloadRequest(String fileName, int userID, String ClientIP, int ClientPort) {
    // 	//LOAD BALANCER
    // 	// Get available worker
    // 	// send info back to client
    // 	//WORKER
    	
    // }

    public void workerHandleDownloadRequest() {
        try {
            os.writeByte(codes.DOWNLOADRESPONSE);//send response to client so the client can proceed
            String fileName = is.readUTF();
            try{
            File file = new File(PREPEND+fileName);
            FileInputStream fis = new FileInputStream(file);
            os.writeLong(file.length()); //give file length to the client requesting so they know how long to download for 
            byte[] buf = new byte[4096]; //4kb buffer
            int read;
            while ((read = fis.read(buf)) != -1) {
                os.write(buf, 0, read);
                os.flush();
            }
            fis.close();
            os.writeByte(codes.DOWNLOADSUCCESS); 
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
            //System.out.println("Username: " + username + " Password: " + password);
            int loginReq = MasterDatabase.loginUser(username, password);
            //gui should return the clientID so we can store it in the instance of the client to help with share requests and such later (prevents an extra DB lookup)
            if(loginReq>=0) {
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
            //send respone code back
            os.writeByte(codes.REGISTERRESPONSE);
            String username = is.readUTF();
            String password = is.readUTF();

            
            //if username already taken or password blank should return error code 
            //if (username already in database){
            //os.writeByte(codes.USEREXISTS); return}

            if(password.length() <= 2)
            {
                os.writeByte(codes.PASSWORDINVALID);
                return; 
            }

            int registerReq = MasterDatabase.registerUser(username, password);

            //successful registration
            if(registerReq >= 0) {
                os.writeByte(codes.REGISTERSUCCESS);
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

        try{
        os.writeByte(codes.UNSHARERESPONSE); //write response to client to tell them we are starting to handle the share request 
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


        os.writeByte(codes.UNSHARESUCCESS); 
        //boolean testValidity = true; 
        //add share permission to database
        // if(testValidity) {
        //     os.writeByte(codes.UNSHARESUCCESSS);
        // } else {
        //     os.writeByte(codes.UNSHAREFAIL);
        //     //os.writeUTF("Error message");
        // }
    } catch (IOException e) {
        e.printStackTrace();
    }
        // try {
        //     String filename = is.readUTF();
        //     String owner = is.readUTF();
        //     String sharedTo = is.readUTF();
        //     //add share permission to database
        //     if(true) {
        //         os.writeByte(codes.OK);
        //     } else {
        //         os.writeByte(codes.ERR);
        //         os.writeUTF("Error message");
        //         return; 
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }


    }

    // method that is called when the server recieves a request to see all files the user can download
    // should tell the client the file names and permissions(owner/shared w me)
    // private void handleGetAllFilesRequest(int userID, String ClientIP, int ClientPort) {

    // }

    public void handleAllFilesRequest(){
        try{
        os.writeByte(codes.GETALLFILESRESPONSE);

        int userID = is.readInt(); 

        //check if userID is actually in system 
        //if(valid user id from caller (client)){
            os.writeByte(codes.USEREXISTS); 
            os.writeByte(codes.GETALLSUCCESS); //if successful 
            //if unsuccessful os.writeByte(codes.GETALLFAIL);
        //}
        }catch(Exception e)
        {
            e.printStackTrace();
            //os.writeByte(codes.GETALLFAIL);
        }

    }

    // method that is called when the server recieves a request to delete a file
    // should return a success or failure message to the client
    public void handleDeleteRequest() {
        try {
            os.writeByte(codes.DELETERESPONSE); 

            String filename = is.readUTF();


            //TODO:get file from server if it exists 
            //byte doesFileExist = 
            //if (file does not exist){
                //os.writeByte(codes.NOSUCHFILE);
                //return; 
            //}
            //else file must exist
            os.writeByte(codes.FILEEXISTS); //file must exist so write OK
          
            int userID = is.readInt();


            //TODO: do checks for if the user exists
            //byte doesUserExist = 
            //if (user does not exist){
                //os.writeByte(codes.NOSUCHUSER);
                //return; 
            //}

            //else
            os.writeByte(codes.USEREXISTS); //check if this person is actually the owner of the file to stop malicious attempts of file deletions


            //DELETE THE FILE IDK HOW YOU'RE DOING IT WITH DATABASE

            //if successful write this back
            os.writeByte(codes.DELETESUCCESS);
            //if not successful but somehow user exists and file exists you can write back os.writeByte(codes.DELETEFAIL); 

            // //attempt to delete
            // if(true) {
            //     os.writeByte(codes.OK);
            // } else {
            //     os.writeByte(codes.ERR);
            //     os.writeUTF("Error message");
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
