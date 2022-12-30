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
    final Controller controller = new Controller();
    final Client client = new Client("147.182.179.7", controller);
    controller.setClient(client);
    client.start();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
    loader.setController(controller);
    primaryStage.setTitle(Constants.CHATROOM_TITLE);
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.show();
  }
}
