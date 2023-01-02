import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ChatroomController implements Initializable {
  @FXML public ListView listview;

  @FXML public TextArea textarea;

  @FXML public Button button;

  @FXML public Text errorText;

  final ObservableList<String> listItems = FXCollections.observableArrayList();

  private ClientHandler clientHandler;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    listview.setItems(listItems);
    button.setOnAction(e -> handleButtonClick());
  }

  public void handleButtonClick() {
    final String userInput = textarea.getText();

    if (null == userInput || userInput.equals("")) {
      displayErrorMessage("Error: Message is empty");
      return;
    }

    if (userInput.length() > Constants.MAX_MESSAGE_SIZE) {
      displayErrorMessage("Error: Message is greater than max message size");
      return;
    }

    clientHandler.sendMessageHandler(userInput);
  }

  public void setClient(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  public void addMessage(String message) {
    Platform.runLater(
        () -> {
          listItems.add(message);
          errorText.setText("");
          textarea.clear();
        });
  }

  public void displayErrorMessage(String message) {
    Platform.runLater(
        () -> {
          errorText.setText(message);
        });
  }
}
