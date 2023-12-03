import Helper.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatroomClient extends Application {
  public ChatroomClient(String[] args) {
    launch();
  }

  public ChatroomClient() {}

  @Override
  public void start(Stage primaryStage) throws Exception {
    final ServerIpAddressController controller = new ServerIpAddressController(primaryStage);
    final FXMLLoader loader =
        new FXMLLoader(getClass().getResource(Constants.SERVER_IP_ADDRESS_FXML_PATH));
    loader.setController(controller);
    primaryStage.setTitle(Constants.CHATROOM_TITLE);
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.show();
  }
}
