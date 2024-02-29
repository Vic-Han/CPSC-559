package server;
import java.sql.*;
import java.util.ArrayList;

public class MasterDatabase {

    static String url = "jdbc:sqlite:src/server/database/server.db";
    private static Connection conn = null;
    
	private static void listEntries() {
		String query = "SELECT * FROM users";
		String query2 = "SELECT * FROM files";
		String query3 = "SELECT * from shared";
		try (Connection conn = DriverManager.getConnection(url)) {
            ResultSet rs = conn.createStatement().executeQuery(query);
            System.out.println("Users:");
            while(rs.next()) {
            	System.out.println(rs.getInt("userID") +" "+ rs.getString("username") + "\t" + rs.getString("password"));
            }
            
            rs = conn.createStatement().executeQuery(query2);
            System.out.println("Files:");
            while(rs.next()) {
            	System.out.println(rs.getInt("fileID") +" "+ rs.getString("fileName") + "\t" + rs.getInt("owner"));
            }
            
            rs = conn.createStatement().executeQuery(query3);
            System.out.println("Shares:");
            while(rs.next()) {
            	System.out.println(rs.getInt("shareID") +" "+ rs.getInt("fileID") + "\t" + rs.getInt("sharedWith"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}

    public static Connection getConnection() throws SQLException{
    	if(conn == null)
    		//Connect to the Database file
    		conn = DriverManager.getConnection(url);
        return conn;
    }
    
    private static int getFileID(String fileName, int ownerID) {
    	String selectQuery = "SELECT fileID FROM files WHERE owner =\'"+ownerID+"\' AND fileName = \'"+fileName+"\'";
    
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Get result set before deleting row
            ResultSet rs = statement.executeQuery(selectQuery);
          //Get File ID to delete anything in the shared table that references this file.
            rs.next();
            int fileID = rs.getInt("fileID");
            return fileID;
    	}catch(Exception e) {
    		return -1;
    	}
    }
    
    private static int getUserID(String username){
    	String selectQuery = "SELECT userID FROM users WHERE username =\'"+username+"\'";
        
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Get result set before deleting row
            ResultSet rs = statement.executeQuery(selectQuery);
          //Get File ID to delete anything in the shared table that references this file.
            rs.next();
            int fileID = rs.getInt("userID");
            return fileID;
    	}catch(Exception e) {
    		return -1;
    	}
    }
    
    private static String getNameFromID(int userID) {
    	String query = "SELECT * FROM users WHERE userID = \'" + userID +"\'";
    	
        try {
        	//Execute the query to select the users that contain a username.
            ResultSet rs = getConnection().createStatement().executeQuery(query);
            
            rs.next();
            return rs.getString("username");
        }catch(Exception e) {
        	return null;
        }
    }
    
    private static String getFileNameFromID(int fileID) {
    	String query = "SELECT * FROM files WHERE fileID = \'" + fileID +"\'";
    	
        try {
        	//Execute the query to select the users that contain a username.
            ResultSet rs = getConnection().createStatement().executeQuery(query);
            
            rs.next();
            return rs.getString("fileName");
        }catch(Exception e) {
        	return null;
        }
    }
    

    // method to exec the sql to insert a new user with given username and password
    // should return userID on success
    // should return -1 on failure or user already exists
    public static int registerUser(String username, String password){
        String updateQuery = "INSERT INTO users (username,password)\nVALUES (\'" + username + "\',\'" + password + "\')";
        String selectQuery = "SELECT userID FROM USERS where username = \'"+username+"'";

        try {
        	//Get the connection to the database.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Execute the insert statement (will throw error if user already exists)
            statement.executeUpdate(updateQuery);
            
            //Return the UserID of the inserted user.
            return getUserID(username);
        }catch(Exception e) {
        	e.printStackTrace();;
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
        	//Execute the query to select the users that contain a username.
            rs = getConnection().createStatement().executeQuery(query);

            rs.next();
            String dbPass = rs.getString("password");
            
            //Check if password is equal to the provided password.
            if(dbPass.equals(password)) {
                return rs.getInt("userID");
            }else {
                return -1;
            }

        }catch(Exception e) {
        	e.printStackTrace();
            return -1;
        }


    }

    // method to exec the sql to insert a new file with given fileName and userID files will be stored on the server in the path /user/file
    // should return true on success
    // should return false on failure
    public static boolean addFile(String fileName, int userID){
    	String updateQuery = "INSERT INTO files (fileName,owner)\nVALUES (\'" + fileName + "\',\'" + userID + "\')";
    	
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            statement.executeUpdate(updateQuery);
    	}catch(Exception e) {
    		return false;
    	}
    	
        return true;
    }

    // method to exec the sql to delete a file with given fileName and userID
    // should return true on success
    // should return false on failure
    public static boolean deleteFile(String fileName, int userID){
    	String deleteFileQuery = "DELETE FROM files WHERE owner =\'"+userID+"\' AND fileName = \'"+fileName+"\'";
    	String deleteShareQuery = "DELETE FROM shared WHERE fileID =\'"+getFileID(fileName, userID)+"\'";
    	
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Delete row
            statement.executeUpdate(deleteFileQuery);
            statement.executeUpdate(deleteShareQuery);
            
    	}catch(Exception e) {
    		return false;
    	}

        return true;
    }

    // method to exec the sql to share a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static boolean shareFile(String fileName, int sharedUser, int userID){
    	String updateQuery = "INSERT INTO shared (fileID,sharedWith)\nVALUES (\'" + getFileID(fileName, userID) + "\',\'" + sharedUser + "\')";
    	
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Insert row
            statement.executeUpdate(updateQuery);
            
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
        return true;
    }

    // method to exec the sql to unshare a file with given filePath and sharedUser
    // should return true on success
    // should return false on failure
    public static boolean unshareFile(String fileName, int sharedUser, int userID){
    	String deleteQuery = "DELETE FROM shared WHERE fileID =\'"+getFileID(fileName, userID)+"\' AND sharedWith = \'"+sharedUser+"\'";
    	
    	try {
    		//Execute the statement to add the file.
        	Connection conn = getConnection();
            Statement statement = conn.createStatement();
            
            //Insert row
            statement.executeUpdate(deleteQuery);
            
    	}catch(Exception e) {
    		return false;
    	}
        return true;
    }

    // method to exec the sql to get all files that the user owns
    // should return a list of file paths
    public static ArrayList<String> getAllOwnedFiles(int userID){
    	ArrayList<String> owned = new ArrayList<String>();
    	String query = "SELECT * FROM files WHERE owner = \'" + userID +"\'";
    	
        try {
        	//Execute the query to select the users that contain a username.
            ResultSet rs = getConnection().createStatement().executeQuery(query);
            
            while(rs.next()) {
            	owned.add(getFileNameFromID(rs.getInt("fileID")));
            }
        }catch(Exception e) {
        	return null;
        }
    	
        return owned;
    }

    // method to exec the sql to get all files that are shared with the user

    public static ArrayList<String> getAllSharedFiles(int userID){
    	ArrayList<String> shared = new ArrayList<String>();
    	String query = "SELECT * FROM shared WHERE sharedWith = \'" + userID +"\'";
    	
        try {
        	//Execute the query to select the users that contain a username.
            ResultSet rs = getConnection().createStatement().executeQuery(query);
            
            while(rs.next()) {
            	shared.add(getFileNameFromID(rs.getInt("fileID")));
            }
        }catch(Exception e) {
        	return null;
        }
    	
        return shared;
    }

//    public static void main(String[] args) throws InterruptedException {
//    	//System.out.println(registerUser("Owen", "Test"));
//    	//System.out.println(registerUser("Matteo", "Test2"));
//    	//System.out.println(addFile("Test_File3.txt", 1));
//    	System.out.println(shareFile("Test_File3.txt", 2, 1));
//    	//listEntries();
//    	
//    	//System.out.println(unshareFile("Test_File.txt", 2, 1));
//    	//listEntries();
//    	
//    	//System.out.println(getAllOwnedFiles(1).toString());
//    	//System.out.println(getAllSharedFiles(2).toString());
//    	
//       
//    }

}