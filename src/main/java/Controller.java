import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class Controller {
    @FXML
    public ListView listview;

    @FXML
    public TextArea textarea;

    @FXML
    public Button button;

    final ObservableList<String> listItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listview.setItems(listItems);
    }

    public void handleButtonClick() {
        String input = textarea.getText();
        listItems.add(input);
    }
}
