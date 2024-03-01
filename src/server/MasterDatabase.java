package server;
import java.sql.*;
import java.util.ArrayList;

public class MasterDatabase {

    static String url = "jdbc:sqlite:server/database/server.db";
    private static Connection conn = null;
    
    /**
     * TEST FUNCTION FOR LISTING CONTENTS OF DATABASES.
     */
//	private static void listEntries() {
//		String query = "SELECT * FROM users";
//		String query2 = "SELECT * FROM files";
//		String query3 = "SELECT * from shared";
//		try (Connection conn = DriverManager.getConnection(url)) {
//            ResultSet rs = conn.createStatement().executeQuery(query);
//            System.out.println("Users:");
//            while(rs.next()) {
//            	System.out.println(rs.getInt("userID") +" "+ rs.getString("username") + "\t" + rs.getString("password"));
//            }
//            
//            rs = conn.createStatement().executeQuery(query2);
//            System.out.println("Files:");
//            while(rs.next()) {
//            	System.out.println(rs.getInt("fileID") +" "+ rs.getString("fileName") + "\t" + rs.getInt("owner"));
//            }
//            
//            rs = conn.createStatement().executeQuery(query3);
//            System.out.println("Shares:");
//            while(rs.next()) {
//            	System.out.println(rs.getInt("shareID") +" "+ rs.getInt("fileID") + "\t" + rs.getInt("sharedWith"));
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//	}
    
    

    /**
     * Uses the hard coded URL to the database file to create a connection using JDBC SQLite to the DB file.
     * @return The connection to the database file
     * @throws SQLException
     */
    private static Connection getConnection() throws SQLException{
    	if(conn == null)
    		//Connect to the Database file
    		conn = DriverManager.getConnection(url);
        return conn;
    }
    
    /**
     * Gets the ID of a file given the owner and the file name
     * @param fileName - The string of the file name
     * @param ownerID - The ID of the owner of the file
     * @return File ID - the ID of the file in the database
     */
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
    
    /**
     * Returns the userID given the username
     * @param username - the username to get the ID for.
     * @return UserID - the ID of the corresponding username in the Database
     */
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
    		e.printStackTrace();
    		return -1;
    	}
    }
    
    /**
     * Given a fileID return the name of the file at that ID
     * @param fileID - the ID of the file to return the name for
     * @return fileName - the Name of the file with the corresponding ID
     */
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
    

    /**
     * Register a new user into the database.
     * @param username - The username of the user to add
     * @param password - The password for the user to add
     * @return The ID of the added user if adding was successful, -1 If an error occurred or the user already exists.
     */
    public static int registerUser(String username, String password){
        String updateQuery = "INSERT INTO users (username,password)\nVALUES (\'" + username + "\',\'" + password + "\')";

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

    /**
     * Logs in the user - Checks if the given username and password combo match
     * @param username - The username to login
     * @param password - The password in the database for the user
     * @return The userID if login was successful, -1 if not.
     */
    public static int loginUser(String username, String password){
        String query = "SELECT * FROM users WHERE username = \'" +username+"\'";
        ResultSet rs;
        try {
        	//Execute the query to select the users that contain a username.
            rs = getConnection().createStatement().executeQuery(query);

           	
            if(rs.next()) { //Check if something returned from DB
	            String dbPass = rs.getString("password");
	            
	            //Check if password is equal to the provided password.
	            if(dbPass.equals(password)) {
	                return rs.getInt("userID");
	            }else {
	                return -1;
	            }
	         }else {
	        	 return -1; //Empty result set, nothing returned from DB
	         }

        }catch(Exception e) {
            return -1;
        }


    }

    /**
     * Adds a file into the database, on the server storage it will be stored at /user/fileName
     * @param fileName - The name of the file added
     * @param userID - The userID of the user who added the file.
     * @return True if successful, False otherwise
     */
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

   
    /**
     * Delete a file from the database and deletes all share references to said file.
     * @param fileName - Name of the file to delete
     * @param userID - Owner of the file
     * @return True if successful, false otherwise.
     */
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

    /**
     * Adds a share into the shared database, indicating that a file is shared with a user
     * @param fileName - the name of the file to share
     * @param owner - The UserID for the owner of the file to share
     * @param sharedUser - The userID of the user to share the file to.
     * @return True if successful, False otherwise.
     */
    public static boolean shareFile(String fileName, int owner, int sharedUser){
    	String updateQuery = "INSERT INTO shared (fileID,sharedWith)\nVALUES (\'" + getFileID(fileName, owner) + "\',\'" + sharedUser + "\')";
    	
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

   /**
    * Deletes the share entry from the database
    * @param fileName - The name of the file to unshare to the specified user
    * @param owner - The userID of the owner of the file to unshare
    * @param sharedUser - The userID of the person to take away the share from
    * @return True if successful, False otherwise.
    */
    public static boolean unshareFile(String fileName, int owner, int sharedUser){
    	String deleteQuery = "DELETE FROM shared WHERE fileID =\'"+getFileID(fileName, owner)+"\' AND sharedWith = \'"+sharedUser+"\'";
    	
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

    /**
     * Gets all owned files for a specific userID
     * @param userID - The ID of the user to check owned files for
     * @return An array list containing all the fileNames that a user owns
     */
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

    /**
     * Gets all files shared with a specific user.
     * @param userID - The ID of the user to check shared files for
     * @return The array list of files that a user has shared with them.
     */
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
    

}