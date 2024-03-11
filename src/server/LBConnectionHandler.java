package server;

import java.io.*;
import java.net.*;
//import Utilities.codes;

public class LBConnectionHandler extends Thread{
    
    private Socket s;
    public LBConnectionHandler(Socket socket) {
        s = socket;
    }

    public void run() {
    	DataInputStream is = null;
        DataOutputStream os = null;
        try {
            is = new DataInputStream(s.getInputStream());
            os = new DataOutputStream(s.getOutputStream());
            System.out.println("LB connection streams open");
            while(true) {
                byte code = is.readByte();
                //report busy value of some variety
                if(code == 0) {
                	os.writeShort(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
