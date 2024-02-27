package client;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class ClientStandinTestAlt {

	public static void main(String[] args) {
		System.out.println("Test started");
		try {
			int port = 1969;
	
	        // open the connection to the server
	        Socket s = new Socket("localhost", port);
	
	        System.out.println("Socket open");
	        // get and set I/O streams
	        DataOutputStream os = new DataOutputStream(s.getOutputStream());
	        DataInputStream is = new DataInputStream(s.getInputStream());
	        String uname = "test";
	        String pass = "testpass";
	        ClientLogicAlt.setIO(os, is);
	       
	        //test request
	        System.out.println("Testing "+uname+" and "+pass);
	        int returnCode = ClientLogicAlt.loginRequest(uname, pass);
	        System.out.println("Process returned with code "+returnCode);
	        
	        s.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
			
	}

}
