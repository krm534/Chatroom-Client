import Helper.Constants;
import Helper.Message;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OutgoingRequestManager extends Thread {
  private final BufferedWriter bufferedWriter;
  private final Message message;

  private final Gson gson;
  private static final Logger LOGGER = LogManager.getLogger(OutgoingRequestManager.class.getName());

  public OutgoingRequestManager(BufferedWriter bufferedWriter, Message message) {
    this.bufferedWriter = bufferedWriter;
    this.message = message;
    this.gson = new Gson();
  }

  @Override
  public void run() {
    try {
      String json = gson.toJson(message, Message.class);
      json += Constants.DELIMITER;
      LOGGER.info(String.format("Message to be sent to chatroom server is %s", json));
      bufferedWriter.write(json);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (IOException e) {
      LOGGER.error(String.format("ServerSender Exception: %s", e.getMessage()));
    }
  }
}
