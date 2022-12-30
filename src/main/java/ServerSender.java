import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSender extends Thread {
  private BufferedWriter bufferedWriter;
  private String message;
  private static final Logger LOGGER = Logger.getLogger(ServerSender.class.getName());

  public ServerSender(BufferedWriter bufferedWriter, String message) {
    this.bufferedWriter = bufferedWriter;
    this.message = message;
  }

  @Override
  public void run() {
    try {
      LOGGER.log(Level.INFO, String.format("Message to be sent to chatroom server is %s", message));
      bufferedWriter.write(message);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "ServerSender Exception: ", e.getMessage());
    }
  }
}
