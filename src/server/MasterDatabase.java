package server;
import java.sql.*;
import java.util.ArrayList;

public class MasterDatabase {

    static String url = "jdbc:sqlite:database/server.db";

    public static Connection getConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    // method to exec the sql to insert a new user with given username and password
    // should return userID on success
    // should return -1 on failure or user already exists
    public static int registerUser(String username, String password){
        String query = "INSERT INTO users (username,password)\nVALUES (\'" + username + "\',\'" + password + "\')";

        try {
            ResultSet rs = getConnection().createStatement().executeQuery(query);
            rs.afterLast();
            return rs.getInt("userID");
        }catch(Exception e) {
            return -1;
        }

    }

    // method to exec the sql to check if the user exists and the password is correct
    // should return userID on success
    // should return -1 on failure or user does not exist
    public static int loginUser(String username, String password){
        String query = "SELECT * FROM users WHERE username = \'" +username+"\'";
        ResultSet rs;
        try {
            rs = getConnection().createStatement().executeQuery(query);

            //Return -1 if there is no data
            if(rs.isBeforeFirst()) {
                return -1;
            }else {
                rs.next();
                String dbPass = rs.getString("password");
                if(dbPass.equals(password)) {
                    return rs.getInt("userID");
                }else {
                    return -1;
                }
            }

        }catch(Exception e) {
            return -1;
        }


    }

    // method to exec the sql to insert a new file with given filePath and userID
    // should return true on success
    // should return false on failure
    public static boolean addFile(String filePath, int userID){

        return true;
    }

    // method to exec the sql to delete a file with given filePath and userID
    // should return true on success
    // should return false on failure
    public static boolean deleteFile(String filePath, int userID){

        return true;
    }

    // method to exec the sql to share a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static boolean shareFile(String filePath, String sharedUser, int userID){

        return true;
    }

    // method to exec the sql to unshare a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static boolean unshareFile(String filePath, String sharedUser, int userID){

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

    public static void main(String[] args) {
        System.out.println(registerUser("Owen", "DatabaseMaster"));
        loginUser("Owen", "DatabaseMaster");
    }

}