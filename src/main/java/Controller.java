import java.io.IOException;
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
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Controller Exception: " + ex.getMessage());
          }
        });
  }

  public void handleButtonClick() throws IOException {
    clientHandler.sendMessageHandler(textarea.getText());
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
