package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import Utilities.*;




// class that is responsible for handling the logic of the client
// class is static because it is not meant to be instantiated by several threads, may change later
public class ClientLogic {

    DataInputStream in;
    DataOutputStream out;
		byte id;
    //private  String host;
    //private  int port;	neither used? commented out for now
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
        //this.host = host; 
        //this.port = port;  
        try {
        //Host string into useable IP address
	    InetAddress address = InetAddress.getByName(host);
	
	    //Instantiate socket with given IP address and port number
	    this.socket = new Socket(address, port);
	
	    //instantiate output/input streams
	    this.out = new DataOutputStream(this.socket.getOutputStream());
	    this.in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
        	e.printStackTrace();
        	this.socket = null;
        	this.out = null;
        	this.in = null;
        }
    }
    
    public boolean isConnected() {
    	return this.socket == null;
    }
    
    public void stop() {
    	try {
    		out.writeByte(codes.QUIT);
    		socket.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public int loginRequest(String username, String password) {
    	try {
    		out.writeByte(codes.LOGINREQUEST);
    		out.writeUTF(username);
    		out.writeUTF(password);//should be hashed
    		System.out.println("Sent Request");
    		
    		byte returned = in.readByte();
    		System.out.println("Recieved Response: " + returned);
    		if(returned == codes.LOGINFAIL) {
    			String msg = in.readUTF();
    			System.out.println(msg);
                return codes.LOGINFAIL; 
    		}
    		
			id = in.readByte(); //If success then ID was also written
			System.out.println("Read ID: " + id);
    		return codes.LOGINSUCCESS; //should be ok since it made it here and thus we return OK 
    	} catch(IOException e) {
    		e.printStackTrace();
            return codes.ERR;
    	}
    }

    public int registerRequest(String username, String password) {
    	try {
            out.writeByte(codes.REGISTERREQUEST);
            byte response = in.readByte(); 
            if(response == codes.REGISTERRESPONSE)
            {
                out.writeUTF(username);
                //byte user = in.readByte(); 
                //if (user == codes.USEREXISTS) then the user already exists and thus we cant create new user with same name 
                out.writeUTF(password);//should be hashed
                byte returned = in.readByte();
                System.out.println("Returned: " + returned);
                if(returned == codes.PASSWORDINVALID)
                {
                    return codes.PASSWORDINVALID; 
                }
                if(returned == codes.REGISTERFAIL) {
                    //String msg = in.readUTF(); unreachable, but doesnt happen yet - just in case
                    //System.out.println(msg);
                    return codes.REGISTERFAIL;
                }
				id = in.readByte();
                return codes.REGISTERSUCCESS;
            }
            else//some sort of error
            {
                return codes.ERR; 
            }
    	} catch(IOException e) {
    		e.printStackTrace();
            return codes.ERR;
    	}
    }

    public byte uploadRequest(File file){
        try{
        	out.writeByte(codes.UPLOADREQUEST); //send request to server
            byte response = in.readByte();  //get response from server
        	if(response == codes.UPLOADRESPONSE) //server responded with valid code so we can proceed 
        	{
                out.writeUTF(file.getName()); //write filename to server 
	            long fileSize = file.length(); //get file size so we can write it to the server 
	            out.writeLong(fileSize); //send fileSize to the runner so that they can determine storage/server to use and other stuffs
	            out.writeByte(id);
							FileInputStream fileIS = new FileInputStream(file); //instantiate FileInputStream to get file contents to send over the socket input stream after
	            byte[] buffer = new byte[4096]; //buffer of 4kb
	            int bytesRead; 
	
	            while((bytesRead = (fileIS.read(buffer))) != -1)//while file still has contents to read we should read them 
	            {
	                out.write(buffer, 0, bytesRead);
	                out.flush();
	            }
	            fileIS.close();
	            System.out.println("File " + file.getName() + " has been uploaded.");
                byte result = in.readByte();
                if(result == codes.UPLOADSUCCESS){
	            return codes.UPLOADSUCCESS; 
                }else {
                    return codes.UPLOADFAIL; 
                }
        	}
        	else{
        		System.out.println("Something went wrong when trying to upload, try again");
        		return codes.UPLOADFAIL; 
        	}
        }catch(IOException e){
        	e.printStackTrace();
        	return codes.ERR; 
        }
    }

    public byte downloadRequest(String destination, String filename){
        try{
        	
            out.writeByte(codes.DOWNLOADREQUEST);
            byte response = in.readByte(); 
            if(response == codes.DOWNLOADRESPONSE)
            {
                out.writeUTF(filename);//send filename to server 
                //String PREPEND = "C:\\CPSC559Proj\\CLIENTFILES\\"; //TODO: solidify this?
                File file = new File(destination+"\\"+filename);
                // file.getParentFile().mkdirs(); creates parent dir if it doesn't exist
                file.createNewFile(); //ensures that it doesn't already exist
                long fileSize = in.readLong(); 
                
                FileOutputStream fileOS = new FileOutputStream(file, false); //false so it doesn't append to the file if it exists (we could use this to resume downloads if one fails later potentially)
                byte[] buffer = new byte[4096];
                int bytesRead = 0; 
                long totalRead = 0; 

                while(totalRead < fileSize)
                {
                    bytesRead = in.read(buffer, 0, Math.min(buffer.length, Math.min(buffer.length, (int)fileSize-bytesRead)));
                    fileOS.write(buffer,0,bytesRead);
                    totalRead += bytesRead;
                }
                fileOS.close();
                byte result = in.readByte();
                if(result == codes.DOWNLOADSUCCESS)
                {
                    System.out.println("File " + filename + " has been downloaded.");
                    return codes.DOWNLOADSUCCESS;
                }
                else
                {
                    return codes.DOWNLOADFAIL; 
                }
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
            return codes.ERR; 
        }
    }

    //fileName is the name of the file which user wants to share 
    public byte shareRequest(String fileName, String sharedUser, int idSharer){
    	try {
	        out.writeByte(codes.SHAREREQUEST); //send request to server to start share request functionality 
	        byte response = in.readByte();  //get response that server is now running the share request functionality 
	
	        if(response == codes.SHARERESPONSE) //valid response from server
	        {
	            out.writeUTF(fileName); //send file name so we can check if it exists
	            byte doesFileExist = in.readByte();  //read from server to see if the file actually exists
	            //if file doesn't exist we should return as we can't share something not in the system duh
	            if(doesFileExist == codes.NOSUCHFILE)
	            {
	                return codes.NOSUCHFILE; 
	            }
	
	            out.writeUTF(sharedUser);
	            byte doesUserExist = in.readByte(); 
	
	            //if the user to share with doesn't exist then we shouldn't share with them 
	            if(doesUserExist == codes.NOSUCHUSER)
	            {
	                return codes.NOSUCHUSER; 
	            }
	
	            out.writeInt(idSharer);
	            byte serverResponse = in.readByte(); 
	
	            //byte response = in.readByte(); 
	           // out.writeInt(idReceiver);  
	
	            //validity checks already done.
	            return serverResponse; 
	            //return codes.OK
	            //maybe implement codes.SHARESUCCESS
	        }
	        else {
	            return codes.ERR; //something happened not sure if this can actually get hit though 
	        }
    	}catch(IOException e) {
    		e.printStackTrace();
    		return codes.ERR;
    	}

    }
    
    public byte unshareRequest(String fileName, String sharedUser, int idSharer){
    	try {
	        out.writeByte(codes.UNSHAREREQUEST); //send request to server to start share request functionality 
	        byte response = in.readByte();  //get response that server is now running the share request functionality 
	        
	
	        if(response == codes.UNSHARERESPONSE) //valid response from server
	        {
	            out.writeUTF(fileName); //send file name so we can check if it exists
	            byte doesFileExist = in.readByte();  //read from server to see if the file actually exists
	            //if file doesn't exist we should return as we can't share something not in the system duh
	            if(doesFileExist == codes.NOSUCHFILE)
	            {
	                return codes.NOSUCHFILE; 
	            }
	
	            out.writeUTF(sharedUser);
	            byte doesUserExist = in.readByte(); 
	
	            //if the user to share with doesn't exist then we shouldn't share with them 
	            if(doesUserExist == codes.NOSUCHUSER)
	            {
	                return codes.NOSUCHUSER; 
	            }
	
	            out.writeInt(idSharer);
	            byte serverResponse = in.readByte(); 
	
	            //byte response = in.readByte(); 
	           // out.writeInt(idReceiver);  
	
	            //validity checks already done.
	            return serverResponse; 
	            //return codes.OK
	        }
	        else{ return codes.ERR;} //not sure if this can get hit 
    	} catch(IOException e) {
    		e.printStackTrace();
    		return codes.ERR;
    	}
    }

    public byte deleteRequest(String filePath, int userID){
    	try {
	        out.writeByte(codes.DELETEREQUEST); //send request to server
	
	        byte response = in.readByte(); //get servers response to request
	        if(response == codes.DELETERESPONSE) //server responded so we can start doing the important stuff
	        {
	            out.writeUTF(filePath); 
	
	            byte doesFileExist = in.readByte(); 
	
	            if(doesFileExist == codes.NOSUCHFILE)
	            {
	                return codes.NOSUCHFILE; 
	            }
	
	            //check if the user owns it with USEREXISTS
	
	            out.writeInt(userID); 
	
	            byte doesUserExist = in.readByte(); 
	            if(doesUserExist == codes.NOSUCHUSER)
	            {
	                return codes.NOSUCHUSER; 
	            }
	
	            response = in.readByte(); //should be returning DELETESUCCESS REALISTICALLY BUT SINCE THE CALL IS BEING SENT WE MUST READ IT OR ELSE IT WILL MESS WITH SOMETHING LATER 
	            return response; 
	        } else return codes.ERR;
    	} catch(IOException e) {
    		e.printStackTrace();
    		return codes.ERR;
    	}
    }

    public ArrayList<Pair<String, String>> getAllFilesRequest()
    {
    	ArrayList<Pair<String,String>> errorReturn = new ArrayList<Pair<String,String>>();
    	errorReturn.add(new Pair<String, String>("", "Error"));
    	try {
	        out.writeByte(codes.GETALLFILESREQUEST); 
	
	        byte response = in.readByte();
	
	        if(response == codes.GETALLFILESRESPONSE)
	        {
	            //PROBABLY SHOULD HAVE SOME SORT OF INSTANCE OF USERID TO ACTUALLY VALIDATE AGAINST OR THE GUI INPUTS THE USERID NOT THE USER THEMSELVES OR THEY COULD RETRIEVE OTHER PEOPLES FILES
	            out.writeInt(id); 
	            byte doesUserExist = in.readByte(); 
	            if(doesUserExist == codes.USEREXISTS)
	            {
	            	short records = in.readShort();
	            	ArrayList<Pair<String,String>> allFiles = new ArrayList<Pair<String,String>>();
	            	for(int i = 0; i < records; i++) {
	            		String name = in.readUTF();
						String perm = in.readUTF();
						allFiles.add(new Pair<String, String>(name, perm));
	            	}
	                //byte finalResponse = in.readByte(); 
	                return allFiles; 
	            }
	            else
	            {
	                return errorReturn; //wrong user input somehow
	            }
	        }
	        else
	        {
	            return errorReturn; 
	        }
    	} catch(IOException e) {
    		e.printStackTrace();
    		return errorReturn;
    	}
    }
}
