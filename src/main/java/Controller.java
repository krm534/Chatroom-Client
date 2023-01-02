import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class Controller implements Initializable {
  @FXML public ListView listview;

  @FXML public TextArea textarea;

  @FXML public Button button;

  final ObservableList<String> listItems = FXCollections.observableArrayList();

  private ClientHandler clientHandler;

  private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    listview.setItems(listItems);
    button.setOnAction(
        e -> {
          try {
            handleButtonClick();
          } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Controller Exception: " + exception.getMessage());
          }
        });
  }

  public void handleButtonClick() throws Exception {
    final String userInput = textarea.getText();

    if (null == userInput || userInput.equals("")) {
      throw new Exception("Controller Exception: Message is empty");
    }

    if (userInput.length() > Constants.MAX_MESSAGE_SIZE) {
      throw new Exception("Controller Exception: Message is greater than max message size");
    }

    clientHandler.sendMessageHandler(userInput);
  }

  public void setClient(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  public void addMessage(String message) {
    // Run on Main thread
    Platform.runLater(
        () -> {
          listItems.add(message);
          textarea.clear();
        });
  }
}
