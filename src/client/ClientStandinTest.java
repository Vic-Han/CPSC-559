package client;
import client.ClientLogic;
import Utilities.codes;

import java.net.*;
import java.io.*; 

import Utilities.Message;


public class ClientStandinTest {

	//port 
	private static int SERVER_PORT = 1969; 
	private static String SERVER_NAME = "127.0.0.1"; //will change when not running on local machine 
	//private ClientLogic logic = new ClientLogic(); 

	//Socket  s = null; 
	//Socket s;
	

	//the connection should be established after the gui is launched, so that basic communication can start. 
	public static void main(String[] args) throws IOException {
		//client object to handle requests
		//ClientLogic client = new ClientLogic(SERVER_PORT_TEST, SERVER_SOCKET_TEST); 

		DataOutputStream out = null; 
		DataInputStream in = null; 

		//try opening sockets to instantiate client variables (input/output/masterIP/port#)
		try 
		{
			Socket s = new Socket(SERVER_NAME, SERVER_PORT); //connect to server via server_name & server_port (currently localhost values and port 1969)
			out = new DataOutputStream(s.getOutputStream());//get output stream
			in = new DataInputStream(s.getInputStream());

			//request user login function over socket to server (Runner.java)
			while(true)
			{
				
			}
			testLogin(out, in);
			s.close();

		}catch(UnknownHostException e){
			//e.printStackTrace();
			System.out.println("Unknown host: " + SERVER_NAME);//print appropriate err message 
			System.exit(1);//quit
		}catch(IOException e)
		{
			System.out.println("I/O init failed for connection to " + SERVER_NAME);
		}


		// TODO Auto-generated method stub
		//open socket

		//use client logic
		//send some file to server
	}

	public static void testLogin(DataOutputStream outTest, DataInputStream inTest)
	{
		

		byte logicResponse = client.handleLoginRequest()
		
		//hardcoded username/password for login 
		String username = "test";
		String password = "success";



	}



}
