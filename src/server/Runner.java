package server;

import java.io.*;
import java.net.*;

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

      // send your greating to the client
      os.writeUTF("Welcome to this wonderful server");
      os.flush();

      // while(true) {
      //   int code = is.readUnsignedByte();
      //   if(code == 0) break;
      //   switch(code) {
      //     case 1:
      //       handler.login();
      //       break;
      //     case 2:
      //       handler.register();
      //       break;
      //     case 3:
      //       handler.upload();
      //       break;
      //     case 4:
      //       handler.download();
      //       break;
      //     case 5:
      //       handler.share();
      //       break;
      //     case 6:
      //       handler.unshare();
      //       break;
      //     case 7:
      //       handler.delete();
      //       break;
      //   }
      // }
      
      this.s.close();

    }
    catch (IOException e) {
      System.out.println("IO Exception: " + e);
    }
  }

private Socket s;

}
