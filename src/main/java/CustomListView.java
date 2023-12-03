import Helper.Message;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CustomListView {

  public final ListView<Message> messageView = new ListView<>();
  public final ObservableList<Message> messages = FXCollections.observableArrayList();

  public CustomListView() {
    messageView.setItems(messages);
    messageView.setCellFactory(list -> new CustomCellFactory());
  }

  public ListView<Message> getMessageView() {
    return messageView;
  }

  public ObservableList<Message> getMessages() {
    return messages;
  }

  static class CustomCellFactory extends ListCell<Message> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button button = new Button("Image");
    Message message;

    public CustomCellFactory() {
      super();
      hbox.getChildren().addAll(label, pane, button);
      button.setOnAction(
          event -> {
            Image image;
            try {
              image =
                  new Image(
                      new FileInputStream(String.format("./images/%s.png", message.getUuid())));
            } catch (FileNotFoundException e) {
              throw new RuntimeException(e);
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(image.getHeight());
            imageView.setFitWidth(image.getWidth());
            Group root = new Group(imageView);
            Scene scene = new Scene(root, image.getWidth(), image.getHeight());
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
          });

      HBox.setHgrow(pane, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
      super.updateItem(item, empty);
      message = item;

      if (null != item && !empty) {
        label.setText(message.getMessage());

        if (null == item.getAttachedB64Image()) {
          button.setVisible(false);
        } else {
          button.setVisible(true);
        }

        setStyle(
            getIndex() % 2 == 0
                ? "-fx-background-color: #CCCCCC;"
                : "-fx-background-color: #FFFFFF;");
        setGraphic(hbox);
      } else {
        setStyle(null);
        setGraphic(null);
      }
    }
  }
}
