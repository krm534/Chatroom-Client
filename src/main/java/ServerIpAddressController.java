import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerIpAddressController implements Initializable {

  @FXML public TextField textField;

  @FXML public Button button;

  private Stage primaryStage;

  private static final Logger LOGGER = Logger.getLogger(ServerIpAddressController.class.getName());

  public ServerIpAddressController(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    button.setOnAction(
        e -> {
          try {
            final String userInput = textField.getText();

            if (null == userInput || userInput.equals("")) {
              throw new Exception("IP address field is empty");
            }

            final InetAddress inetAddress = InetAddress.getByName(userInput);
            handleChatroomFxmlSetup(inetAddress);
          } catch (Exception ex) {
            LOGGER.log(
                Level.SEVERE, String.format("Server IP Address Exception: %s", ex.getMessage()));
          }
        });
  }

  private void handleChatroomFxmlSetup(InetAddress serverIpAddress) throws IOException {
    final ChatroomController chatroomController = new ChatroomController();
    final ClientHandler clientHandler =
        new ClientHandler(serverIpAddress.getHostName(), chatroomController);
    chatroomController.setClient(clientHandler);
    clientHandler.start();

    final FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.MAIN_FXML_PATH));
    loader.setController(chatroomController);
    primaryStage.setScene(new Scene(loader.load()));
  }
}
