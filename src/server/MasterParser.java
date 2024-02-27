package server;

import Utilities.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//source for encoding/decoding: https://www.baeldung.com/java-string-to-byte-array

// static class that provides the parsing of the raw binary data
public class MasterParser {
    // utilities to parse the raw binary and extracts the arguments of the message

    // Returns a pair of the message type and the arguments: username & password
    private static Pair<String,String> parseLogin(byte[] usernameBytes,byte[] passwordBytes){// int dataSize){

        String username = new String(usernameBytes, StandardCharsets.UTF_8);
        String password = new String(passwordBytes, StandardCharsets.UTF_8);
        Pair <String,String> info = new Pair<>(username, password);//new Pair<username, password>; 
        return info;
    	//return null;
    }

    //source: https://stackoverflow.com/questions/6406542/split-byte-array-with-string
        //source 2: https://stackoverflow.com/questions/58767601/how-to-split-a-byte-array-that-contains-multiple-lines-in-java

    // private static String splitDataBytesIntoStrings(byte[] data, int size, int start, int end)
    // {
    //     for(int i = start; start <= end; start++)
    //     {

    //     }
    //     return null;
    // }

    //another way to do it 
    private static Pair<String,String> parseLogin2(byte[] data, int[] dataSizes)
    {
        int startUserName = 0; 
        int userLength = dataSizes[0];
        //int endUserName = startUserName + (userLength - 1); //array control end
        int endUserName = startUserName + userLength; 
        int passLength = dataSizes[1];
        int startPass = startUserName + (userLength + 1); 
        int endPass = startPass + passLength; 
        String userName = new String(data, startUserName, endUserName, StandardCharsets.UTF_8);
        String pass = new String(data, startPass, endPass, StandardCharsets.UTF_8); 

        return new Pair<>(userName, pass);
        
        //String userName = splitDataBytesIntoStrings(data, userLength, startUserName, endUserName);
        //String pass = splitDataBytesIntoStrings(data, passLength, startPass, endPass);


        //datasizes
        //String username = 
        //return null;
    }

    // Returns a pair of the message type and the arguments: username & password
    private static Pair<String,String> parseRegister(byte[] data, int dataSize){
    	return null;
    }

    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,Integer> parseUpload(byte[] data, int dataSize){
    	return null;
    }
    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,Integer> parseDownload(byte[] data, int dataSize){
    	return null;
    }

    // Returns a triple of the message type and the arguments: file path, shared user, & userID
    private static Triple<String,String,Integer>  parseShare(byte[] data, int dataSize){
    	return null;
    }
    // Returns a triple of the message type and the arguments: file path, shared user, & userID
    private static Triple<String,String,Integer>  parseUnshare(byte[] data, int dataSize){
    	return null;
    }
    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,Integer> parseDelete(byte[] data, int dataSize){
    	return null;
    }
    // Returns a pair of the message type and the arguments: userID
    private static int parseGetAllFiles(byte[] data, int dataSize){
    	return 0;
    }
    
    public static Pair<Long, String> workerParseUpload(byte[] data, int[] datasize) {
    	byte[] lBytes = Arrays.copyOfRange(data, 0, datasize[0]);
    	byte[] sBytes = Arrays.copyOfRange(data, datasize[0], datasize[1]);
    	String s = new String(sBytes);
    	Long l = new BigInteger(lBytes).longValue();
    	return new Pair<Long, String>(l, s);
    }
}