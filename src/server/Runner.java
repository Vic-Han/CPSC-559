package server;

import java.io.*;
import java.net.*;
import Utilities.Message;
import Utilities.codes;

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
    DataInputStream is = null;
    DataOutputStream os = null;
    //ProtocolHandler handler = new ProtocolHandler(os, is);

    // Get these I/O streams
    try {
      is = new DataInputStream(this.s.getInputStream());
      os = new DataOutputStream(this.s.getOutputStream());
      
      ProtocolHandler protocolHandler = new ProtocolHandler(os, is);
      while(true) {
        byte code = is.readByte();
        if(code == codes.QUIT) break;
        switch(code) {
          case(codes.UPLOADREQUEST):
            protocolHandler.workerHandleUploadRequest();
            break;
          case(codes.DOWNLOADREQUEST):
            protocolHandler.workerHandleDownloadRequest();
            break;
          case(codes.LOGINREQUEST):
            protocolHandler.handleLoginRequest();
            break;
          case(codes.REGISTERREQUEST):
            protocolHandler.handleRegisterRequest();
            break;
          case(codes.SHAREREQUEST):
            protocolHandler.handleShareRequest();
            break;
          case(codes.UNSHAREREQUEST):
            protocolHandler.handleUnshareRequest();
            break;
          case(codes.DELETEREQUEST):
            protocolHandler.handleDeleteRequest();
        }
      }
      
      this.s.close();

    }
    catch (IOException e) {
      System.out.println("IO Exception: " + e);
    }
  }

private Socket s;

}
