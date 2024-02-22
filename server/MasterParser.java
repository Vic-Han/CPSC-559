import Utilities.*;
// static class that provides the parsing of the raw binary data
public class MasterParser {
    // utilities to parse the raw binary and extracts the arguments of the message

    // Returns a pair of the message type and the arguments: username & password
    private static Pair<String,String> parseLogin(byte[] data, int dataSize){

    }

    // Returns a pair of the message type and the arguments: username & password
    private static Pair<String,String> parseRegister(byte[] data, int dataSize){

    }

    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,int> parseUpload(byte[] data, int dataSize){

    }
    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,int> parseDownload(byte[] data, int dataSize){

    }

    // Returns a triple of the message type and the arguments: file path, shared user, & userID
    private static Triple<String,String,int>  parseShare(byte[] data, int dataSize){

    }
    // Returns a triple of the message type and the arguments: file path, shared user, & userID
    private static Triple<String,String,int>  parseUnshare(byte[] data, int dataSize){

    }
    // Returns a pair of the message type and the arguments: file path & userID
    private static Pair<String,int> parseDelete(byte[] data, int dataSize){

    }
    // Returns a pair of the message type and the arguments: userID
    private static int parseGetAllFiles(byte[] data, int dataSize){

    }
}