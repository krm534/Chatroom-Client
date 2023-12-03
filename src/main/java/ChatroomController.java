import Helper.Constants;
import Helper.Message;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChatroomController implements Initializable {

  @FXML public TextArea textArea;

  @FXML public Button button;

  @FXML public Text errorText;

  @FXML public Button attachedImageButton;

  @FXML public Text attachedImageName;

  private IncomingResponseManager clientHandler;

  private Stage stage;

  private File selectedFile;

  private CustomListView customListView;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    button.setOnAction(e -> handleButtonClick());
    attachedImageButton.setOnAction(e -> attachImage());
  }

  public void handleButtonClick() {
    final String userInput = textArea.getText();
    byte[] image = null;

    if (null == userInput || userInput.equals("")) {
      displayErrorMessage("Error: Message is empty");
      return;
    }

    if (userInput.length() > Constants.MAX_MESSAGE_SIZE) {
      displayErrorMessage("Error: Message is greater than max message size");
      return;
    }

    if (null != this.selectedFile) {
      try {
        image = Files.readAllBytes(this.selectedFile.toPath());
      } catch (Exception ex) {
        displayErrorMessage(ex.getMessage());
      }
    }

    clientHandler.sendMessageHandler(userInput, image);
  }

  public void attachImage() {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select image resource");
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    this.selectedFile = fileChooser.showOpenDialog(this.stage);

    if (null != this.selectedFile) {
      this.attachedImageName.setText(this.selectedFile.getName());
    }
  }

  public void setParams(
      IncomingResponseManager incomingResponseManager, Stage stage, CustomListView customListView) {
    this.clientHandler = incomingResponseManager;
    this.stage = stage;
    this.customListView = customListView;
  }

  public void addMessage(Message message) {
    Platform.runLater(
        () -> {
          customListView.getMessages().add(message);
          errorText.setText("");
          attachedImageName.setText("");
          selectedFile = null;
          textArea.clear();
        });
  }

  private void displayErrorMessage(String message) {
    Platform.runLater(() -> errorText.setText(message));
  }
}
