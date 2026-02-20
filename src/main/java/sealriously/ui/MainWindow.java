package sealriously.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sealriously.Sealriously;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sealriously sealriously;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaSlave.png"));
    private Image sealriouslyImage = new Image(this.getClass().getResourceAsStream("/images/DaMaster.png"));

    /**
     * Initializes UI bindings after the FXML has been loaded.
     * Binds the scroll pane to always scroll to the latest dialog.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Sealriously instance */
    public void setSealriously(Sealriously d) {
        sealriously = d;

        dialogContainer.getChildren().add(
            DialogBox.getSealriouslyDialog(sealriously.getGreeting(), sealriouslyImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Sealriously's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = sealriously.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSealriouslyDialog(response, sealriouslyImage)
        );
        userInput.clear();
    }
}
