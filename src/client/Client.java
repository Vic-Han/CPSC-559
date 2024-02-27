package client;
import java.io.*;
import java.net.*;
import client.ClientLogic;
// class responsible for handling user input and output through a jswing interface
public class Client {

    
    public static void main(String a[]) throws IOException {
        int port = 1969;

        // open the connection to the server
        Socket s = new Socket("localhost", port);

        // get I/O streams
        PrintStream os = new PrintStream(s.getOutputStream());
        //DataInputStream is = new DataInputStream(s.getInputStream());
        // readLine is depricated in DataInputStream
        BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));

        // get the server welcome message
        System.out.println("Server says:\t" + is.readLine());

        // send a request to the server
        os.println("1$C:/users/mmmat/Temp/tmp.txt"); // ask to transfer the file Runner.java
        os.flush();

        // receive the servers response
        System.out.println("Server Says:\t" + is.readLine());

        s.close();

    }
}

