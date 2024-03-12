package server;

import java.io.*;
import java.net.*;
//import Utilities.codes;

public class LBConnectionHandler extends Thread{
    
    private Socket s;
    DataInputStream is; 
    DataOutputStream os;

    public LBConnectionHandler(DataInputStream is, DataOutputStream os)
    {
        this.is = is;
        this.os = os; 
    }


    public void run() {
        try {
            System.out.println("LB connection streams open");
            while(true) {
                System.out.println("test1");
                byte code = is.readByte();
                System.out.println("testtesttest");


                //report busy value of some variety
                // if(code == 0) {
                // 	os.writeShort(0);
                // }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
