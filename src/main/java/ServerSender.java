import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSender extends Thread {
  private Socket clientSocket;
  private String message;
  private static final Logger LOGGER = Logger.getLogger(ServerSender.class.getName());

  public ServerSender(Socket clientSocket, String message) {
    this.clientSocket = clientSocket;
    this.message = message;
  }

  @Override
  public void run() {
    try {
      LOGGER.log(Level.INFO, String.format("Message to be sent to chatroom server is %s", message));
      final PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
      printWriter.println(message);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "ServerSender Exception: ", e.getMessage());
    }
  }
}
