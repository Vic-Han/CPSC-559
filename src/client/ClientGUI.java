package client;

import javafx.application.Application; import javafx.scene.Scene; import javafx.scene.control.Button;
import javafx.scene.control.Label; import javafx.scene.image.Image; import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane; import javafx.scene.layout.VBox; import javafx.stage.DirectoryChooser;
import javafx.scene.control.TextField; import javafx.scene.control.PasswordField; import javafx.stage.Stage;
import javafx.stage.FileChooser; import javafx.scene.layout.HBox; import javafx.scene.control.TextInputDialog;
import java.io.IOException; import java.io.File; import java.nio.file.Files;
import javafx.stage.FileChooser; import javafx.collections.FXCollections;
import javafx.collections.ObservableList; import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.Optional;

import Utilities.Pair;
import Utilities.codes;

// run/compile within gui directory using
// java --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls GUI
// this reminder's just for matteo cuz i forget the module-path shit :)

public class ClientGUI extends Application {

    private Stage primaryStage;
    private static ClientLogic clientLogic;
    private static String usrname;
    public static void main(String[] args) {
        clientLogic = new ClientLogic("localhost",1970); 
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.getIcons().add(new Image("file:resources/logo.png"));
        this.primaryStage.setTitle("File Transfer App");
        showLoginPage();
    }

    private void showLoginPage(){
        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox
        // Place login and register buttons horizontally using HBox
        HBox buttonBox = new HBox(10);

        // UI components
        Label titleLabel = new Label("File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");

        buttonBox.getChildren().addAll(registerButton, loginButton);

        // Event handler
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> handleRegistration(usernameField.getText(), passwordField.getText()));

        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, buttonBox);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 300, 200);

        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainPage(){
        primaryStage.setTitle("File Transfer App");

        // UI components
        Button uploadButton = new Button("Upload File");
        Button downloadButton = new Button("Download File");
        Button shareButton = new Button("Share File");
        Button unshareButton = new Button("Unshare File");

        // Event handlers
        uploadButton.setOnAction(e -> uploadFile(primaryStage));
        downloadButton.setOnAction(e -> showDownloadPage(primaryStage));
        shareButton.setOnAction(e -> showSharingPage(primaryStage));
        unshareButton.setOnAction(e -> showUnsharingPage(primaryStage));

        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox
        HBox buttonBox1 = new HBox(10);
        buttonBox1.getChildren().addAll(uploadButton, downloadButton);
        HBox buttonBox2 = new HBox(10);
        buttonBox2.getChildren().addAll(shareButton, unshareButton);

        Label titleLabel = new Label(usrname+"'s File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, buttonBox1, buttonBox2);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox1.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox2.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 400, 300);
        
        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showDownloadPage(Stage primaryStage){
        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox
        // Place login and register buttons horizontally using HBox
        HBox buttonBox = new HBox(10);

        // UI components
        Label titleLabel = new Label("File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        ComboBox<String> fileComboBox = new ComboBox<>();
        fileComboBox.setPromptText("Select File to Download");
        fileComboBox.getStyleClass().add("combo-box");

        // Populate the ComboBox with data from your ArrayList
        ObservableList<String> fileOptions = FXCollections.observableArrayList(getAllFiles());
        fileComboBox.setItems(fileOptions);

        Button downloadButton = new Button("Select Destination Folder");
        downloadButton.getStyleClass().add("button");

        buttonBox.getChildren().addAll(downloadButton);

        // Event handler
        downloadButton.setOnAction(e -> downloadFile(primaryStage, fileComboBox.getValue())); //fileField.getText()
        
        vbox.getChildren().addAll(titleLabel, fileComboBox, buttonBox);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 300, 200);

        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showSharingPage(Stage primaryStage){
        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox
        // Place login and register buttons horizontally using HBox
        HBox buttonBox = new HBox(10);

        // UI components
        Label titleLabel = new Label("File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        ComboBox<String> fileComboBox = new ComboBox<>();
        fileComboBox.setPromptText("Select File to Share");
        fileComboBox.getStyleClass().add("combo-box");

        // Populate the ComboBox with data from your ArrayList
        ObservableList<String> fileOptions = FXCollections.observableArrayList(getOwnedFiles());
        fileComboBox.setItems(fileOptions);

        TextField shareField = new TextField();
        shareField.setPromptText("Share with");
        shareField.getStyleClass().add("text-field");

        Button shareButton = new Button("Share");
        shareButton.getStyleClass().add("button");

        buttonBox.getChildren().addAll(shareButton);

        // Event handler
        shareButton.setOnAction(e -> shareFile(primaryStage, fileComboBox.getValue(), shareField.getText())); //fileField.getText()
        
        vbox.getChildren().addAll(titleLabel, fileComboBox, shareField, buttonBox);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 300, 200);

        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showUnsharingPage(Stage primaryStage){
        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox
        // Place login and register buttons horizontally using HBox
        HBox buttonBox = new HBox(10);

        // UI components
        Label titleLabel = new Label("File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        ComboBox<String> fileComboBox = new ComboBox<>();
        fileComboBox.setPromptText("Select File & User to Unshare");
        fileComboBox.getStyleClass().add("combo-box");

        // Populate the ComboBox with data from your ArrayList
        ObservableList<String> fileOptions = FXCollections.observableArrayList(getSharedFiles());
        fileComboBox.setItems(fileOptions);

        Button shareButton = new Button("Unshare");
        shareButton.getStyleClass().add("button");

        buttonBox.getChildren().addAll(shareButton);

        // Event handler
        shareButton.setOnAction(e -> unshareFile(primaryStage, fileComboBox.getValue())); //fileField.getText()
        
        vbox.getChildren().addAll(titleLabel, fileComboBox, buttonBox);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 300, 200);

        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(String username, String password) {
        System.out.println("Username: " + username + "\tPassword: " + password);
        int req = clientLogic.loginRequest(username, password);
        
        switch(req){
            case codes.LOGINSUCCESS:
                System.out.println("Successful login");
                usrname = username;
                showMainPage();
                break;
            case codes.LOGINFAIL:
                System.out.println("Intruder Alert");
                break;
            default: // error
                System.out.println("Something broke");
        }
    }

    private void handleRegistration(String username, String password) {
        System.out.println("New Username: " + username);
        System.out.println("New Password: " + password);
        
        int req = clientLogic.registerRequest(username, password);
        
        switch(req){
            case codes.REGISTERSUCCESS:
                System.out.println("Successful Registration");
                usrname = username;
                showMainPage();
                break;
            case codes.REGISTERFAIL:
                System.out.println("Failed Reg LOL");
                break;
            default: // error
                System.out.println("Something broke (reg)");
        }
    }

    private void uploadFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                byte req = clientLogic.uploadRequest(selectedFile);
                
                switch(req){
                    case codes.UPLOADSUCCESS:
                        System.out.println("Successful upload of " + selectedFile.getName());
                        break;
                    case codes.UPLOADFAIL:
                        System.out.println("Failed upload :(");
                        break;
                    default: // error
                        System.out.println("Something broke (upload)");
                }
            } catch (Exception e) {
                System.out.println("FAILED to uploaded: " + selectedFile.getName());
            }
        }
    }

    private void downloadFile(Stage primaryStage, String fileName) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination Directory");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        String dest = selectedDirectory.getAbsolutePath();

        if(fileName.endsWith(" (shared)")) fileName = fileName.substring(0, fileName.length() - 9);
        System.out.println(fileName + " -> " + dest);

        if (selectedDirectory != null) {
            try {
                byte req = clientLogic.downloadRequest(dest, fileName);
                
                switch(req){
                    case codes.DOWNLOADSUCCESS:
                        System.out.println("Successful download of " + fileName + " to " + dest);
                        break;
                    case codes.DOWNLOADFAIL:
                        System.out.println("Failed download :(");
                        break;
                    case codes.NOSUCHFILE:
                        System.out.println(fileName + " does not exist");
                        break;
                    default: // error
                        System.out.println("Something broke (download)");
                }
            } catch (Exception e) {
                System.out.println("FAILED to download " + fileName + " to: " + dest);
            }
        }
        showMainPage();
    }

    private void shareFile(Stage primaryStage, String fileName, String shareUser){
        try {
            if(fileName.endsWith(" (shared)")) fileName = fileName.substring(0, fileName.length() - 9);
            // TODO: allow users to share files that have been shared with them?
            System.out.println("File selected: " + fileName + ", User selected: " + shareUser);
            byte req = clientLogic.shareRequest(fileName, shareUser);

            switch(req){
                case codes.NOSUCHFILE:
                    System.out.println(fileName + " does not exist");
                    break;
                case codes.NOSUCHUSER:
                    System.out.println(shareUser + " does not exist");
                    break;
                case codes.SHARESUCCESS:
                    System.out.println(fileName + " successfully shared with " + shareUser);
                    break;
                case codes.SHAREFAIL:
                    System.out.println("Failed to share "+fileName+" with "+shareUser);
                    break;
                default: // error
                    System.out.println("Something broke (sharing)");
            }
        } catch (Exception e) {
            System.out.println("FAILED to select: " + fileName);
        }
        showMainPage();
    }

    private void unshareFile(Stage primaryStage, String selection){
        String[] parts = selection.split(" -> ");
        String fileName = parts[0], shareUser = parts[1];
        try {
            System.out.println("File selected: " + fileName + ", User selected: " + shareUser);
            byte req = clientLogic.unshareRequest(fileName, shareUser);

            switch(req){
                case codes.NOSUCHFILE:
                    System.out.println(fileName + " does not exist");
                    break;
                case codes.NOSUCHUSER:
                    System.out.println(shareUser + " does not exist");
                    break;
                case codes.SHARESUCCESS:
                    System.out.println(fileName + " successfully shared with " + shareUser);
                    break;
                case codes.SHAREFAIL:
                    System.out.println("Failed to share "+fileName+" with "+shareUser);
                    break;
                default: // error
                    System.out.println("Something broke (sharing)");
            }
        } catch (Exception e) {
            System.out.println("FAILED to select: " + fileName);
        }
        showMainPage();
    }

    private ArrayList<String> getAllFiles(){
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Pair<String, String>> all = clientLogic.getAllFilesRequest();
        for (Pair<String,String> pair : all)
            ret.add(pair.second.equals("share") ? pair.first+" (shared)": pair.first);
        return ret;
    }

    private ArrayList<String> getOwnedFiles(){
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Pair<String, String>> all = clientLogic.getAllFilesRequest();
        for (Pair<String,String> pair : all) if(pair.second.equals("own")) ret.add(pair.first);
        return ret;
    }

    private ArrayList<String> getSharedFiles(){
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Pair<String, String>> all = clientLogic.getSharedFilesRequest(); // filename, shareUser
        for (Pair<String,String> pair : all) ret.add(pair.first + " -> " + pair.second);
        return ret;
    }
}
