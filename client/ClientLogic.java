import java.io.File;
import java.util.ArrayList;
import java.util.Pair;
public class ClientLogic {

   
    // method that is called when the user chooses to download a file
    // gui should not allow user to download a file that they do not have access to
    public static void fileUpload(String filePath, int userID) {
        
    }
    // method that is called when the user chooses to download a file
    // gui should not allow user to download a file that they do not have access to
    public static void fileDownload(String filePath, int userID) {
        
    }
    // method that is called when user tries to login
    // returns userID
    public static int loginRequest(String username, String password) {

    }
  
    // method that is called when user tries to register
    public static int registerRequest(String username, String password) {

    }
    // method that is called when user tries to delete a file
    public static void deleteRequest(String filePath, int userID) {

    }
    // method that is called when user tries to share a file
    public static void shareRequest(String fileName, String sharedUser, int userID) {

    }
    // method that is called when user tries to unshare a file
    public static void unshareRequest(String fileName, String sharedUser, int userID) {

    }
    // method that is called when user wants to see all files they can download
    // returns a list of pairs; first entry is the file name, second entry is the owner/permission(owner/shared w me)
    public static ArrayList<Pair<String,String>> getAllFiles(int userID) {

    }

    // Todo: implement some type of authentication token to talk to workers

    // asks the load balancer/master for a worker that can accept the upload
    // returns the worker IP and port number
    private static Pair<Integer, String> sendUploadRequest(String fileName, String workerIP){

    }
     // method that uploads file to worker
    private static void uploadFiletoWorker(String fileName, String workerIP, int portNo){

    }
    // method that tells the load balancer/master that the file has been uploaded successfully
    private static void sendSuccessUploadMessage(String fileName, int userID){

    }

    //  asks the load balancer/master for a worker that has the file
    // returns the worker IP and port number
    private static Pair<Integer, String> sendDownloadRequest(String fileName, String workerIP){

    }
    // method that downloads file from worker
    // puts the file in local(client) file system and the user can download it from there
    // may need to change UX mentioned above
    private static void downloadFileFromWorker(String fileName, String workerIP, int portNo){

    }

   

}
