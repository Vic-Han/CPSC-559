import javafx.application.Application; import javafx.scene.Scene; import javafx.scene.control.Button;
import javafx.scene.control.Label; import javafx.scene.image.Image; import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane; import javafx.scene.layout.VBox; import javafx.stage.DirectoryChooser;
import javafx.scene.control.TextField; import javafx.scene.control.PasswordField; import javafx.stage.Stage;
import java.io.IOException; import java.io.File; import java.nio.file.Files;
import javafx.stage.FileChooser; 

// run/compile within gui directory using
// java --module-path "C:\Program Files\Java\javafx-sdk-21.0.2\lib" --add-modules javafx.controls GUI
// this reminder's just for matteo cuz i forget the module-path shit :)

public class GUI extends Application {

    private Stage primaryStage;
    public static void main(String[] args) {
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

        // Event handler
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        vbox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 300, 200);

        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainPage(String username){
        primaryStage.setTitle("File Transfer App");

        // UI components
        Button uploadButton = new Button("Upload File");
        Button downloadButton = new Button("Download File");

        // Event handlers
        uploadButton.setOnAction(e -> uploadFile(primaryStage));
        downloadButton.setOnAction(e -> downloadFile(primaryStage));

        // Layout
        VBox vbox = new VBox(10); // 10 pixels spacing between elements
        vbox.setPadding(new Insets(20)); // 20 pixels padding around the VBox

        Label titleLabel = new Label(username+"'s File Transfer App");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        vbox.getChildren().addAll(titleLabel, uploadButton, downloadButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        // Scene
        Scene scene = new Scene(new BorderPane(vbox), 400, 300);
        
        // Apply 'dark mode' stylesheet
        scene.getStylesheets().add(getClass().getResource("resources/dark-mode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(String username, String password) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        // temporary login logic
        if ("Matteo".equals(username) && "IsAwesome".equals(password)) showMainPage(username);
        else {
            System.out.println("Login failed. Invalid credentials.");
            // Add code for handling failed login
        }
    }

    private void uploadFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            try {
                // file upload logic here
                // temporarily just pretend to upload
                System.out.println("File uploaded: " + selectedFile.getName());
            } catch (Exception e) {
                System.out.println("FAILED to uploaded: " + selectedFile.getName());
            }
        }
    }

    private void downloadFile(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination Directory");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            try {
                // file download logic here
                // temporarily just create an empty file
                File dummyFile = new File(selectedDirectory, "downloaded_file.txt");
                Files.createFile(dummyFile.toPath());
                System.out.println("File downloaded to: " + selectedDirectory.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("FAILED to download to: " + selectedDirectory.getAbsolutePath());
            }
        }
    }
}

