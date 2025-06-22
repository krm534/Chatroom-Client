package Helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

  public final ListView<MessagesJO> messageView = new ListView<>();
  public final ObservableList<MessagesJO> messagesJOS = FXCollections.observableArrayList();

  public CustomListView() {
    messageView.setItems(messagesJOS);
    messageView.setCellFactory(list -> new CustomCellFactory());
  }

  public ListView<MessagesJO> getMessageView() {
    return messageView;
  }

  public ObservableList<MessagesJO> getMessages() {
    return messagesJOS;
  }

  static class CustomCellFactory extends ListCell<MessagesJO> {
    HBox hbox = new HBox();
    Label messageLabel = new Label("");
    Label usernameLabel = new Label("");
    Label timestampLabel = new Label("");
    Pane pane = new Pane();
    Button button = new Button("Image");
    MessagesJO messagesJO;

    public CustomCellFactory() {
      super();

      timestampLabel.setPadding(new Insets(0, 10, 0, 10));
      usernameLabel.setPadding(new Insets(0, 10, 0, 10));
      messageLabel.setPadding(new Insets(0, 10, 0, 10));
      hbox.getChildren().addAll(timestampLabel, usernameLabel, messageLabel, pane, button);

      button.setOnAction(
          event -> {
            Image image;
            try {
              image =
                  new Image(
                      new FileInputStream(String.format("./images/%s.png", messagesJO.getUuid())));
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
    protected void updateItem(MessagesJO item, boolean empty) {
      super.updateItem(item, empty);
      messagesJO = item;

      if (null != item && !empty) {
        messageLabel.setText(messagesJO.getMessage());
        timestampLabel.setText(messagesJO.getTimestamp());
        usernameLabel.setText(messagesJO.getUserId());

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
