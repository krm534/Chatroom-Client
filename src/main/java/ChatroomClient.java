import Helper.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatroomClient extends Application {
  public ChatroomClient(String[] args) {
    launch(args);
  }

  public ChatroomClient() {}

  @Override
  public void start(Stage primaryStage) throws Exception {
    final ChatroomSplashPageController controller = new ChatroomSplashPageController(primaryStage);
    final FXMLLoader loader =
        new FXMLLoader(getClass().getResource(Constants.CHATROOM_CLIENT_SPLASH_PAGE_FXML_PATH));
    loader.setController(controller);
    primaryStage.setTitle(Constants.CHATROOM_TITLE);
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.show();
  }
}
