import java.io.*;
import java.net.*;
import java.util.*;

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

    String command = null;
    String argument = null;
    File rf;

    System.out.println("\nNew Thread: " + Runner.currentThread());

    // I/O streams for the socket
    DataInputStream is = null;
    PrintStream os = null;

    // Get these I/O streams
    try {
      is = new DataInputStream(this.s.getInputStream());
      os = new PrintStream(this.s.getOutputStream());

      // send your greating to the client
      os.println("Welcome to this wonderful server");
      os.flush();

      // receive the client's request. Example request: 1:source.txt
      StringTokenizer request = new StringTokenizer(is.readLine(),"$");
      if (request.countTokens() == 2) {
        command = request.nextToken();
        argument = request.nextToken();
        System.out.println("Client's command: " + "\t" + argument);
      }

      switch (command) { // a file is requested
        case "1":
          System.out.println("Client requested file upload: " + argument);
          rf = new File(argument);
          if (rf.exists()){
            os.println("File Exists modify the code to transfer it");
          }
          else {
            os.println("File Does Not Exist ...");
          }
          break;
        case "2":
          System.out.println("Client requested file upload: " + argument);
          break;
        default:
          System.out.println("Invalid Command");
      }

      // close "this"  connection with the client
      this.s.close();

    }
    catch (IOException e) {
      System.out.println("IO Exception: " + e);
    }
  }

private Socket s;

}
