package dynamic.com.dynamic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static javafx.scene.paint.Color.WHITE;

public class Dash extends Application {

    private TextField watchDirField = new TextField();
    private TextField targetDirField = new TextField();
    private TextArea logArea = new TextArea();
    private TextField delayField = new TextField();
    private Map<String, String> fileExtensionMap = new HashMap<>();
    private Map<String, String> fileTypeMap = new HashMap<>();
    private boolean isMonitoring = false;
    private WatchService watchService;
    private Thread monitoringThread;
    private int delayInSeconds = 0; // Default delay

    @Override
    public void start(Stage primaryStage) {
        // Create the root AnchorPane
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #803D3B;");
        root.setPrefSize(800.0, 600.0);
        // add image below welcome label


        Label welcomeLabel = new Label("Welcome to your File Organizer");
        welcomeLabel.setLayoutY(50.0);
        welcomeLabel.setLayoutX(340.0);
        welcomeLabel.setPrefSize(400.0, 80.0);
        welcomeLabel.setFont(new Font("Century Gothic", 20.0));
        welcomeLabel.setFont(new Font("System Bold", 25.0));
        welcomeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        welcomeLabel.setTextFill(WHITE);
        root.getChildren().add(welcomeLabel);

        Button startButton = new Button("Start Monitoring");
        startButton.setLayoutX(445.0);
        startButton.setLayoutY(440.0);
        startButton.setPrefSize(200.0, 50.0);
        startButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 30 30 30");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 30 30 30;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30  30 30;"));
        startButton.setTextFill(WHITE);
        startButton.setFont(new Font("System Bold", 17.0));
        startButton.setOnAction(e -> startMonitoring());
        root.getChildren().add(startButton);

        // Create the side AnchorPane
        AnchorPane sidePane = new AnchorPane();
        sidePane.setLayoutX(1.0);
        sidePane.setPrefSize(180.0, 600.0);
        sidePane.setStyle(" -fx-background-color: #353A56; -fx-background-radius: 40px; -fx-border-width: 2px 2px 2px 2px; -fx-border-radius: 40px; -fx-border-color: black; -fx-border-style: solid;");
        Pane sidePaneTop = new Pane();
        sidePaneTop.setLayoutX(40.0);
        sidePaneTop.setLayoutY(15.15);
        sidePaneTop.setPrefSize(200, 60.0);
        sidePaneTop.setStyle("-fx-background-color:#748CF1; -fx-border-width: 2px 2px 2px 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-border-style: solid;");
        Label sidePaneLabel = new Label("Dynamic File Organizer");
        sidePaneLabel.setLayoutX(30.0);
        sidePaneLabel.setLayoutY(0.0);
        sidePaneLabel.setPrefSize(150, 50.0);
        //   sidePaneLabel.setStyle("-fx-background-color:#748CF1; -fx-border-width: 2px 2px 2px 2px; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-border-style: solid;");
        sidePaneLabel.setFont(new Font("System Bold", 12.0));
        sidePaneLabel.setTextFill(WHITE);
        sidePaneTop.getChildren().add(sidePaneLabel);

        sidePane.getChildren().add(sidePaneTop);

        // Create buttons for the side pane
        Button watcherButton = new Button("Monitor");
        watcherButton.setLayoutX(40.0);
        watcherButton.setLayoutY(100.0);
        watcherButton.setPrefSize(260, 65.0);
        watcherButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;");
        watcherButton.setTextFill(WHITE);
        watcherButton.setFont(new Font("System Bold", 17.0));
        watcherButton.setOnMouseEntered(e -> watcherButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 0 0 30;"));
        watcherButton.setOnMouseExited(e -> watcherButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;"));

        Button delayButton = new Button("Delay");
        delayButton.setLayoutX(40.0);
        delayButton.setLayoutY(220.0);
        delayButton.setPrefSize(260, 65.0);
        delayButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;");
        delayButton.setTextFill(WHITE);
        delayButton.setFont(new Font("System Bold", 17.0));
        delayButton.setOnMouseEntered(e -> delayButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 0 0 30;"));
        delayButton.setOnMouseExited(e -> delayButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;"));
        delayButton.setOnAction(e -> setDelay());

        Button logsButton = new Button("Logs");
        logsButton.setLayoutX(40.0);
        logsButton.setLayoutY(340.0);
        logsButton.setPrefSize(260, 65.0);
        logsButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;");
        logsButton.setTextFill(WHITE);
        logsButton.setFont(new Font("System Bold", 17.0));
        logsButton.setOnMouseEntered(e -> logsButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 0 0 30;"));
        logsButton.setOnMouseExited(e -> logsButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;"));
        logsButton.setOnAction(e -> showLogs());

        Button settingsButton = new Button("Settings");
        settingsButton.setLayoutX(40.0);
        settingsButton.setLayoutY(460.0);
        settingsButton.setPrefSize(260, 65.0);
        settingsButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;");
        settingsButton.setTextFill(WHITE);
        settingsButton.setFont(new Font("System Bold", 17.0));
        settingsButton.setOnMouseEntered(e -> settingsButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 0 0 30;"));
        settingsButton.setOnMouseExited(e -> settingsButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30 0 0 30;"));


// Settings functionality can be added here

// Add buttons to the side pane
        sidePane.getChildren().addAll(watcherButton, delayButton, logsButton, settingsButton);

        // Create TextFields and Buttons for the main pane
        watchDirField.setLayoutX(360.0);
        watchDirField.setLayoutY(175);
        watchDirField.setPrefSize(150, 38.0);
        watchDirField.setPromptText("Enter Directory to Watch");
        watchDirField.setStyle("-fx-background-color: #322C2B;");

        Button browseWatchButton = new Button("Browse");
        browseWatchButton.setLayoutX(580.0);
        browseWatchButton.setLayoutY(180.0);
        browseWatchButton.setPrefSize(83.0, 18.0);
        browseWatchButton.setStyle("-fx-background-color: #322C2B;");
        browseWatchButton.setTextFill(WHITE);
        browseWatchButton.setFont(new Font("System Bold", 10.0));
        browseWatchButton.setOnAction(e -> browseDirectory(watchDirField));

        targetDirField.setLayoutX(360.0);
        targetDirField.setLayoutY(250.0);
        targetDirField.setPrefSize(149.0, 38.0);
        targetDirField.setPromptText("Enter Target Directory");
        targetDirField.setStyle("-fx-background-color: #322C2B;");

        Button browseTargetButton = new Button("Browse");
        browseTargetButton.setLayoutX(580.0);
        browseTargetButton.setLayoutY(260.0);
        browseTargetButton.setPrefSize(83.0, 18.0);
        browseTargetButton.setStyle("-fx-background-color: #322C2B;");
        browseTargetButton.setOnMouseEntered(e -> browseTargetButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 30 30 30;"));
        browseTargetButton.setOnMouseExited(e -> browseTargetButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30  30 30;"));

        browseTargetButton.setTextFill(WHITE);
        browseTargetButton.setFont(new Font("System Bold", 10.0));
        browseTargetButton.setOnAction(e -> browseDirectory(targetDirField));

        Button addButton = new Button("Add");
        addButton.setLayoutX(490.0);
        addButton.setLayoutY(348.0);
        addButton.setPrefSize(96.0, 38.0);
        addButton.setStyle("-fx-background-color: #322C2B;");
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-background-color: #4A4442; -fx-background-radius: 30 30 30 30;"));
        addButton.setOnMouseExited(e -> addButton.setStyle("-fx-background-color: #322C2B; -fx-background-radius: 30  30 30;"));
        addButton.setTextFill(WHITE);
        addButton.setFont(new Font("System Bold", 17.0));
        addButton.setOnAction(e -> addFileExtension());


        // Add TextFields and Buttons to the root pane
        root.getChildren().addAll(watchDirField, browseWatchButton, targetDirField, browseTargetButton, addButton);

        // Create the log area


        // Add side pane to the root pane
        root.getChildren().add(sidePane);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dynamic File Organizer");
        primaryStage.show();
    }


    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index).toLowerCase();
        }
        return "";
    }

    private void browseDirectory(TextField textField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            textField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void addFileExtension() {
    }

    private void removeFileExtension() {
    }

    private void setDelay() {
    }

    private void showLogs() {
        // Show log functionality can be added here
    }

    public void startMonitoring() {
        //   String userHome = System.getProperty("user.home");
        Path directoryToWatch = Paths.get(watchDirField.getText());
        Path baseTargetDirectory = Paths.get(targetDirField.getText());

        // Map to store file extensions and their corresponding target directories
        Map<String, String> fileTypeMap = new HashMap<>();
        fileTypeMap.put(".txt", "Text Files");
        fileTypeMap.put(".pdf", "PDF Files");
        fileTypeMap.put(".csv", "CSV Files");
        fileTypeMap.put(".pptx", "PPTX Files");
        fileTypeMap.put(".exe", "EXE Files");
        fileTypeMap.put(".docx", "WORD Files");
        fileTypeMap.put(".jpg", "Image Files");
        fileTypeMap.put(".png", "Image Files");
        fileTypeMap.put(".zip", "Compressed Files");
        fileTypeMap.put(".mp4", "Video Files");
        fileTypeMap.put(".mp3", "Audio Files");
        fileTypeMap.put(".java", "Code Files");
        fileTypeMap.put(".class", "Code Files");
        fileTypeMap.put(".html", "Web Files");
        fileTypeMap.put(".css", "Web Files");
        fileTypeMap.put(".js", "Web Files");
        fileTypeMap.put(".sql", "Database Files");
        fileTypeMap.put(".db", "Database Files");
        fileTypeMap.put(".json", "Database Files");
        fileTypeMap.put(".xml", "Database Files");
        fileTypeMap.put(".xls", "Excel Files");
        fileTypeMap.put(".xlsx", "Excel Files");
        fileTypeMap.put(".ppt", "PPT Files");

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            directoryToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            // Ensure the base target directory exists
            if (!Files.exists(baseTargetDirectory)) {
                Files.createDirectories(baseTargetDirectory);
            }

            System.out.println("Monitoring directory for new files...");

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path fileName = ev.context();
                        Path sourcePath = directoryToWatch.resolve(fileName);

                        System.out.println("New file: " + fileName);
//
                        String fileExtension = getFileExtension(fileName.toString());

                        if (fileTypeMap.containsKey(fileExtension)) {
                            Path targetDirectory = Paths.get(baseTargetDirectory.toString(), fileTypeMap.get(fileExtension));
                            Path targetPath = targetDirectory.resolve(fileName);

                            moveFileWithDelay(sourcePath, targetPath, 10);
                        } else {
                            System.out.println("No handling specified for this file type: " + fileExtension);
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }//

    private static void moveFileWithDelay(Path sourcePath, Path targetPath, int delayInSeconds) {
        try {
            Path targetDirectory = targetPath.getParent();
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }

            // Sleep for the specified delay to handle files in use
            Thread.sleep(delayInSeconds * 1000);

            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved to '" + targetDirectory.getFileName() + "' folder: " + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while moving the file.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            System.out.println("The thread was interrupted.");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
