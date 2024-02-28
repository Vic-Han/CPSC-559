package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;     
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import Utilities.Pair;
import Utilities.codes;
import Utilities.Message;



// class that is responsible for handling the logic of the client
// class is static because it is not meant to be instantiated by several threads, may change later
public class ClientLogic {

    private  DataInputStream input;
    private  DataOutputStream output;
    private  String host;
    private  int port;
    Socket socket; 
    //private int userID;

    //default constructor to assign null values 
    // public ClientLogic(){
    //     this.host = "";
    //     this.port = -1; 
    // }

    //constructor called when given host and port to instantiate variables associated
    public ClientLogic(String host, int port)
    {
        this.host = host; 
        this.port = port; 
    }

    //get address, instantiate socket, instantiate input/output data streams
    public void start()
    {
        //Host string into useable IP address
        InetAddress address = InetAddress.getByName(host);

        //Instantiate socket with given IP address and port number
        this.socket = new Socket(address, port);

        //instantiate output/input streams
        out = new ObjectOutputStream(this.socket.getOutputStream());
        in = new ObjectInputStream(this.socket.getInputStream());


        //Loop while connecte
        bool inputCodeBool = true; 
        while(inputCodeBool)
        {
            //handle inputs
            byte inputCode = in.readByte(); 
            if(inputCode == codes.QUIT)
            {
                //quit
            }
            else
            {
                inputCodeHandler(inputCode);
            }
                        //get some sort of response from inputCodeHandler for output or write with class level out 
        }

    }

    public static void main(String[] args) throws IOException{
        ClientLogic client = new ClientLogic(); 
        client.start("localhost", 1969);
    }

    private static void inputCodeHandler(byte codeToHandle)
    {
        switch(codeToHandle)
        {
            case codes.LOGINREQUEST:
            //code
            byte response = loginRequest();
            //if (response == codes.LOGINSUCCESS){do something upon successful login}
            //if (response == codes.LOGINFAILURE){do something upon unsuccessful login}
            //if (response == codes.ERR){some sort of IO failure idk what to do here}
            break;
            case codes.REGISTERREQUEST:
            //code
            byte response = registerRequest();
            //if (response == codes.REGISTERSUCCESS){do something upon successful registration}
            //if (response == codes.REGISTERFAIL){do something upon unsuccessful registration}
            //if (response == codes.ERR){some sort of IO failure idk what to do here}
            break;
            case codes.UPLOADREQUEST:
            byte response = uploadRequest();
            if (response == codes.ERR)
            {
                System.out.println("Error in uploading the file, ensure file exists");
            }
            break;
            case codes.DOWNLOADREQUEST:
            byte response = downloadRequest();
            if(response == codes.ERR)
            {
                System.out.println("Error in downloading the file, either the connection dropped or you do not have enough storage to store locally");
            }
            if(response == codes.NOSUCHFILE)
            {
                //do something
            }
            break;
            case codes.SHAREREQUEST:
            shareRequest();
            break;
            case codes.UNSHAREREQUEST:
            unshareRequest();
            break;
            case codes.DELETEREQUEST:
            deleteRequest();
            break;
            case codes.GETALLFILESREQUEST:
            getAllFilesRequest();
            break;
            default:
        }
    }

    public static int loginRequest(String username, String password) {
    	try {
    		os.writeByte(codes.LOGINREQUEST);
    		os.writeUTF(username);
    		os.writeUTF(password);//should be hashed
    		
    		byte returned = is.readByte();
    		if(returned == codes.LOGINFAIL) {
    			String msg = is.readUTF();
    			System.out.println(msg);
                return codes.LOGINFAIL; 
    		}
    		return codes.LOGINSUCCESS; //should be ok since it made it here and thus we return OK 
    	} catch(IOException e) {
    		e.printStackTrace();
            return codes.ERR;
    	}
    }

    public static int registerRequest(String username, String password) {
    	try {
            os.writeByte(codes.REGISTERREQUEST);
    		os.writeUTF(username);
    		os.writeUTF(password);//should be hashed
    		
    		byte returned = is.readByte();
    		if(returned == codes.REGISTERFAIL) {
    			String msg = is.readUTF();
    			System.out.println(msg);
                return codes.REGISTERFAIL;
    		}
    		return codes.REGISTERSUCCESS;
    	} catch(IOException e) {
    		e.printStackTrace();
            return codes.ERR;
    	}
    }

  

    private byte uploadRequest(File file, String fileName){
        os.writeByte(codes.UPLOADREQUEST); //send request to server
        byte response = is.readByte();  //get response from server
        try{
        if(response == codes.UPLOADRESPONSE) //server responded with valid code so we can proceed 
        {
            long fileSize = file.length(); 
            os.writeLong(fileSize); //send fileSize to the runner so that they can determine storage/server to use and other stuffs
            FileInputStream fileIS = new FileInputStream(file); //instantiate FileInputStream to get file contents to send over the socket input stream after
            byte[] buffer = new byte[4096]; //buffer of 4kb
            int bytesRead; 

            while(bytesRead = (fileIS.read(buffer)) != -1)//while file still has contents to read we should read them 
            {
                os.write(buffer, 0, bytesRead);
            }

            fileIS.close();
            System.out.println("File " + fileName + " has been uploaded.");
            return codes.UPLOADSUCCESS; 

        }
        else{
            System.out.println("Something went wrong when trying to upload, try again");
            return codes.UPLOADFAIL; 
        }

    }catch(IOException e)
        e.printStackTrace();
    }

    private byte downloadRequest(String filename, int userID){
        try{
            os.writeByte(codes.DOWNLOADREQUEST);
            byte response = in.readByte(); 
            if(response == codes.DOWNLOADRESPONSE)
            {
                os.writeUTF(fileName);//send filename to server 


                File file = new File(filename);
                // file.getParentFile().mkdirs(); creates parent dir if it doesn't exist
                file.createNewFile(); //ensures that it doesn't already exist
                //

                long fileSize = is.readLong(); 
                
                FileOutputStream fileOS = new FileOutputStream(file, false); //false so it doesn't append to the file if it exists (we could use this to resume downloads if one fails later potentially)

                byte[] buffer = new byte[4096];
                int bytesRead; 
                long totalRead = 0; 

                while(totalRead < fileSize)
                {
                    bytesRead = is.read(buffer, 0, buffer.length); 
                    fileOS.write(buffer,0,bytesRead);
                    totalRead += bytesRead;
                }
                fileOS.close();
                System.out.println("File " + fileName + " has been downloaded.");
                return codes.DOWNLOADSUCCESS;
            }
            else if(response == codes.NOSUCHFILE)
            {
                System.out.println("No such file exists, try again");
                return codes.NOSUCHFILE; 
            }
            else
            {
                System.out.println("No response from the server");
                return codes.DOWNLOADFAIL; 
            }

        }catch(IOException e)
        {
            //can happen if user is out of storage space and/or connection lost to server
            e.printStackTrace();
        }
    }

    //fileName is the name of the file which user wants to share 
    private byte shareRequest(String fileName, String sharedUser, int idSharer){
        os.sendByte(codes.SHAREREQUEST); //send request to server to start share request functionality 
        byte response = is.readByte();  //get response that server is now running the share request functionality 
        

        if(response == codes.SHARERESPONSE) //valid response from server
        {
            os.writeUTF(fileName); //send file name so we can check if it exists
            byte doesFileExist = is.readByte();  //read from server to see if the file actually exists
            //if file doesn't exist we should return as we can't share something not in the system duh
            if(doesFileExist == codes.NOSUCHFILE)
            {
                return codes.NOSUCHFILE; 
            }

            os.writeUTF(sharedUser);
            byte doesUserExist = is.readByte(); 

            //if the user to share with doesn't exist then we shouldn't share with them 
            if(doesUserExist == codes.NOSUCHUSER)
            {
                return codes.NOSUCHUSER; 
            }

            os.writeInt(idSharer);
            byte serverResponse = is.readByte(); 

            //byte response = is.readByte(); 
           // os.writeInt(idReceiver);  

            //validity checks already done.
            return serverResponse; 
            //return codes.OK
            //maybe implement codes.SHARESUCCESS
        }
        // else {
        //     return codes.ERR; //something happened
        // }
        

    }
    
    private void unshareRequest(){

    }

    private void deleteRequest(){

    }

    private void getAllFilesRequest()
    {

    }



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
    // public static void fileUpload(String filePath, int userID) {
    //     //send to load balancer request for server
    // 	//receive response from server
    // 	//create new socket from response message
    // 	//
    // 	/*
	// 	 	int read;
	//         while ((read = fis.read(buf)) > 0) {
	//             out.write(buf, 0, read);
	//             // Wait for ACK
	//             String ack = in.readUTF();
	//             if (!"ACK".equals(ack)) {
	//                 System.out.println("Error in transmission, stopping.");
	//                 break;
	//             }
	//         }
	// 	 */
    // }
    // // method that is called when the user chooses to download a file
    // // socket messages will be sent by helper methods defined below
    // // gui should not allow user to download a file that they do not have access to
    // public static void fileDownload(String filePath, int userID) {
        
    // }
    // // socket messages should be sent and recived here
    // // method that is called when user tries to login
    // // returns userID
    // public static int loginRequest(){//String username, String password) {
    //     //hardcoded username/pass for testing

    //     return 0; 
    // }

    // //private byte 
  
    // // method that is called when user tries to register
    // // socket messages should be sent and recived here
    // // returns userID
    // public static int registerRequest(String username, String password) {

    //     return -1;
    // }
    // // method that is called when user tries to delete a file
    // // socket messages should be sent and recived here
    // public static void deleteRequest(String filePath, int userID) {

    // }
    // // method that is called when user tries to share a file
    // // socket messages should be sent and recived here
    // public static void shareRequest(String fileName, String sharedUser, int userID) {

    // }
    // // method that is called when user tries to unshare a file
    // // socket messages should be sent and recived here
    // public static void unshareRequest(String fileName, String sharedUser, int userID) {

    // }
    // // method that is called when user wants to see all files they can download
    // // returns a list of pairs; first entry is the file name, second entry is the owner/permission(owner/shared w me)
    // public static ArrayList<Pair<String, Integer>> getAllFiles(int userID) {

    //     return null;
    // }

    // // Todo: implement some type of authentication token to talk to workers

    // // asks the load balancer/master for a worker that can accept the upload
    // // returns the worker IP and port number
    // // socket messages should be sent and recived here
    // private static Pair<String, Integer> sendUploadRequest(String fileName){
    //     //changed int to Integer as java must use a wrapper (Integer) for generic types rather than primitive (int)
    //         //src: https://stackoverflow.com/questions/34885463/insert-dimensions-to-complete-expression-referencetype
    //     return null;
    // }

    // // method that uploads file to worker
    // // socket messages should be sent and recived here    
    // private static void uploadFiletoWorker(String fileName, String workerIP, int portNo){

    // }

    // // method that tells the load balancer/master that the file has been uploaded successfully
    // // implement for future demo when we cannot assume process is successful
    // private static void sendSuccessUploadMessage(String fileName, int userID){

    // }

    // //  asks the load balancer/master for a worker that has the file
    // // returns the worker IP and port number
    // // socket messages should be sent and recived here
    // private static Pair<String, Integer> sendDownloadRequest(String fileName){

    //     return null;
    // }
    // // method that downloads file from worker
    // // puts the file in local(client) file system and the user can download it from there
    // // may need to change UX mentioned above
    // // socket messages should be sent and recived here
    // private static void downloadFileFromWorker(String fileName, String workerIP, int portNo){

    // }

}
