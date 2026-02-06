package duke;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!"); // Creating a new Label control
        //Scene scene = new Scene(helloWorld); // Setting the scene to be our Label

        StackPane root = new StackPane(helloWorld);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 200);

        stage.setScene(scene); // Setting the stage to show our scene
        stage.show(); // Render the stage.
    }
}