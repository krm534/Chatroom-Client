import Helper.Constants;
import Helper.CustomListView;
import Helper.Message;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatroomSplashPageController implements Initializable {

  @FXML public TextField serverIpAddressTextField;

  @FXML public Button sendButton;

  @FXML public Text errorText;

  private final Stage primaryStage;

  private static final Logger LOGGER =
      LogManager.getLogger(ChatroomSplashPageController.class.getName());

  public ChatroomSplashPageController(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    sendButton.setOnAction(
        e -> {
          try {
            final String userInput = serverIpAddressTextField.getText();

            if (null == userInput || userInput.equals("")) {
              displayErrorMessage("Error: IP address field is empty");
              return;
            }

            final InetAddress inetAddress = InetAddress.getByName(userInput);
            handleChatroomMainPageFxmlSetup(inetAddress);
          } catch (Exception ex) {
            LOGGER.error(String.format("Server IP Address Exception: %s", ex.getMessage()));
            displayErrorMessage("Error: Invalid IP address was entered");
          }
        });
  }

  private void handleChatroomMainPageFxmlSetup(InetAddress serverIpAddress) throws IOException {
    final CustomListView customListView = new CustomListView();
    final ChatroomMainPageController chatroomMainPageController = new ChatroomMainPageController();

    final ClientManager clientManager =
        new ClientManager(serverIpAddress.getHostName(), chatroomMainPageController);
    clientManager.setupClientManager();
    chatroomMainPageController.setParams(clientManager, this.primaryStage, customListView);

    final FXMLLoader loader =
        new FXMLLoader(getClass().getResource(Constants.CHATROOM_CLIENT_MAIN_PAGE_FXML_PATH));
    loader.setController(chatroomMainPageController);

    final VBox vbox = loader.load();
    final ObservableList<Node> nodeList = vbox.getChildren();
    final ObservableList<Node> newNodeList = FXCollections.observableArrayList();

    final ListView<Message> messageListView = customListView.getMessageView();
    newNodeList.add(messageListView);
    newNodeList.addAll(nodeList);

    vbox.getChildren().clear();
    vbox.getChildren().addAll(newNodeList);
    primaryStage.setScene(new Scene(vbox));
  }

  private void displayErrorMessage(String message) {
    Platform.runLater(() -> errorText.setText(message));
  }
}
