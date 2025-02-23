import Helper.Constants;
import Helper.CustomListView;
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

public class ChatroomMainPageController implements Initializable {

  @FXML public TextArea messageTextArea;

  @FXML public Button sendButton;

  @FXML public Text errorText;

  @FXML public Button attachedImageButton;

  @FXML public Text attachedImageName;

  private ClientManager clientManager;

  private Stage primaryStage;

  private File selectedFile;

  private CustomListView customListView;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    sendButton.setOnAction(e -> handleButtonClick());
    attachedImageButton.setOnAction(e -> attachImage());
  }

  public void handleButtonClick() {
    final String userInput = messageTextArea.getText();
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

    clientManager.sendMessageHandler(userInput, image);
  }

  public void attachImage() {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select image resource");
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    this.selectedFile = fileChooser.showOpenDialog(this.primaryStage);

    if (null != this.selectedFile) {
      this.attachedImageName.setText(this.selectedFile.getName());
    }
  }

  // TODO: Refactor to not use this method this way
  public void setParams(
      ClientManager incomingResponseManager, Stage stage, CustomListView customListView) {
    this.clientManager = incomingResponseManager;
    this.primaryStage = stage;
    this.customListView = customListView;
  }

  public void addMessage(Message message) {
    Platform.runLater(
        () -> {
          customListView.getMessages().add(message);
          errorText.setText("");
          attachedImageName.setText("");
          selectedFile = null;
          messageTextArea.clear();
        });
  }

  private void displayErrorMessage(String message) {
    Platform.runLater(() -> errorText.setText(message));
  }
}
