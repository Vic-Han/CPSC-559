package server;

import java.io.*;
import java.net.*;
import Utilities.Message;

/**
 * For each incomming request, the Server spawns a new Runner thread to satisfy
 * the request
 */

public class Runner extends Thread {

// Constructor
  public Runner(Socket s) {
    this.s = s;
  }

  public void run() {

    System.out.println("\nNew Thread: " + Runner.currentThread());

    // I/O streams for the socket
    ObjectInputStream is = null;
    ObjectOutputStream os = null;
    //ProtocolHandler handler = new ProtocolHandler(os, is);

    // Get these I/O streams
    try {
      is = new ObjectInputStream(this.s.getInputStream());
      os = new ObjectOutputStream(this.s.getOutputStream());
      
      ProtocolHandler handler = new ProtocolHandler(os, is);
      try {
    	  Message m = (Message) is.readObject();
    	  handler.handleMessage(m);
      } catch(ClassNotFoundException e) {
    	  e.printStackTrace();
      }
      
      this.s.close();

    }
    catch (IOException e) {
      System.out.println("IO Exception: " + e);
    }
  }

private Socket s;

}
