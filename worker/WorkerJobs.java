import java.io.File;

public class WorkerJobs {

    // testing method, will be deleted later
    public static void recivetest(String arg){
        System.out.println("Recieved: " + arg);
    }

    // return type and parameters may change
    // method that is called when the worker recieves a command to upload contents to its directory
    public static void uploadFile(String filePath) {
       
        System.out.println("File uploaded: " + filePath);
    }

    // return type and parameters may change
    // method that is called when the worker recieves a command to retrieve contents from its directory
    public static void downloadFile(String filePath) {
        
        System.out.println("File downloaded: " + filePath);
    }

}
