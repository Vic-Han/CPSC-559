package Utilities;

import java.io.Serializable;
// all communication protocols will be done using this class
// can add or remove things as needed, without changing the protocol
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;



    private short sourcePort;
    private String sourceIP;
    
    private short destinationPort;
    private String destinationIP;

    private byte messageCode;
    private byte[] messageData;
    private int[] dataSizes;
    private int messageLength;

    // message types
    // list may be incomplete
    private static final byte LOGINREQUEST = 0;
    private static final byte LOGINRESPONSE = 1;
    private static final byte REGISTERREQUEST = 2;
    private static final byte REGISTERRESPONSE = 3;
    private static final byte UPLOADREQUEST = 4;
    private static final byte UPLOADRESPONSE = 5;
    private static final byte DOWNLOADREQUEST = 6;
    private static final byte DOWNLOADRESPONSE = 7;
    private static final byte SHAREREQUEST = 8;
    private static final byte SHARERESPONSE = 9;
    private static final byte UNSHAREREQUEST = 10;
    private static final byte UNSHARERESPONSE = 11;
    private static final byte DELETEREQUEST = 12;
    private static final byte DELETERESPONSE = 13;
    private static final byte GETALLFILESREQUEST = 14;
    private static final byte GETALLFILESRESPONSE = 15;
    private static final byte FILEDATA = 16;
    private static final byte FILEDATAACK = 17;
    private static final byte ACK = 52;

    public Message(byte messageType, byte[] messageData, int[] dataSizes) {
        this.messageCode = messageType;
        this.messageData = messageData;
        this.dataSizes = dataSizes;
    }
    
    public byte getMessageType() {
    	return messageCode;
    }
    
    public byte[] getMessageData() {
    	return messageData;
    }
    
    public int[] getDataSizes() {
    	return dataSizes;
    }
}   