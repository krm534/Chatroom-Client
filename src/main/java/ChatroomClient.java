import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatroomClient extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    final ServerIpAddressController controller = new ServerIpAddressController(primaryStage);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServerIpAddress.fxml"));
    loader.setController(controller);
    primaryStage.setTitle(Constants.CHATROOM_TITLE);
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.show();
  }
}
