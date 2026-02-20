package sealriously.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sealriously.Sealriously;


/**
 * A GUI for Sealriously using FXML.
 */
public class Main extends Application {

    //private Sealriously sealriously = new Sealriously();

    private Sealriously sealriously = new Sealriously("./data/sealriously.txt");

    /**
     * Starts the JavaFX UI and loads the main window from FXML.
     *
     * @param stage Primary stage provided by JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSealriously(sealriously); // inject the Sealriously instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
