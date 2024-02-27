package server;

import Utilities.*;

import java.math.BigInteger;
import java.util.Arrays;
// static class that provides the parsing of the raw binary data
public class MasterParser {
    // utilities to parse the raw binary and extracts the arguments of the message

    // Returns a pair of the message type and the arguments: username & password
    private static Pair<String,String> parseLogin(byte[] data, int dataSize){
    	return null;
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