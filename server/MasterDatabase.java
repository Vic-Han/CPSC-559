import java.sqlite;

public class MasterDatabase {
    // method to exec the sql to insert a new user with given username and password
    // should return the userID of the new user on success
    // should return -1 on failure or user already exists
    public static int registerUser(String username, String password){
        
        
        
        return -1;
    }

    // method to exec the sql to check if the user exists and the password is correct
    // should return the userID of the user on success
    // should return -1 on failure or user does not exist
    public static int loginUser(String username, String password){
        
        return -1;
    }

    // method to exec the sql to insert a new file with given filePath and userID
    // should return true on success
    // should return false on failure
    public static bool addFile(String filePath, int userID){
        
        return true;
    }

    // method to exec the sql to delete a file with given filePath and userID
    // should return true on success
    // should return false on failure
    public static bool deleteFile(String filePath, int userID){
        
        return true;
    }

    // method to exec the sql to share a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static bool shareFile(String filePath, String sharedUser, int userID){
        
        return true;
    }

    // method to exec the sql to unshare a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static bool unshareFile(String filePath, String sharedUser, int userID){
        
        return true;
    }

    // method to exec the sql to get all files that the user owns
    // should return a list of file paths
    public static ArrayList<String> getAllOwnedFiles(int userID){
        
        return new ArrayList<String>();
    }

    // method to exec the sql to get all files that are shared with the user
    
    public static ArrayList<String> getAllSharedFiles(int userID){
        
        return new ArrayList<String>();
    }

}