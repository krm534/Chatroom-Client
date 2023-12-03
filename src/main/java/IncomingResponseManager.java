import Helper.Constants;
import Helper.Message;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IncomingResponseManager extends Thread {
  private String address;
  private ChatroomController chatroomController;
  private Socket socket;
  private BufferedWriter bufferedWriter;
  private String userId;
  private Gson gson;
  private static final Logger LOGGER =
      LogManager.getLogger(IncomingResponseManager.class.getName());

  public IncomingResponseManager(String address, ChatroomController chatroomController) {
    try {
      this.address = address;
      this.chatroomController = chatroomController;
      this.socket = new Socket(address, Constants.DEFAULT_PORT);
      gson = new Gson();
    } catch (Exception e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }

  @Override
  public void run() {
    try {
      Scanner input = new Scanner(socket.getInputStream());

      // Initialization information returned from server
      String serverInfo = input.nextLine();
      LOGGER.info(String.format("'%s' returned from the chatroom server", serverInfo));
      socket.close();

      // Parse initialization information
      JSONObject jsonObject = new JSONObject(serverInfo);
      userId = jsonObject.getString("userId");
      int port = jsonObject.getInt("port");

      // Update server socket to use new port number
      socket = new Socket(address, port);
      input = new Scanner(socket.getInputStream());
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Listen for messages from Server
      while (true) {
        final StringBuilder serverMessage = new StringBuilder();
        while (input.hasNext()) {
          String message = input.next();
          serverMessage.append(message);

          if (message.contains(Constants.DELIMITER)) {
            break;
          }
        }

        final String parsedMessage = serverMessage.toString().replace(Constants.DELIMITER, "");
        final Message message = gson.fromJson(parsedMessage, Message.class);
        message.setUuid(UUID.randomUUID().toString());
        if (null != message) {
          if (null != message.getMessage()) {
            chatroomController.addMessage(message);
          }
          if (null != message.getAttachedB64Image()) {
            final String userDir = System.getProperty("user.dir");
            final String attachedImage = String.format("/images/%s.png", message.getUuid());
            final File outputFile = new File(userDir + attachedImage);
            outputFile.mkdirs();

            final BufferedImage image =
                ImageIO.read(
                    new ByteArrayInputStream(
                        Base64.getDecoder().decode(message.getAttachedB64Image())));
            ImageIO.write(image, "png", outputFile);
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }

  public void sendMessageHandler(String attachedMessage, byte[] attachedImage) {
    final String updatedMessage = String.format("%s: %s", userId, attachedMessage);
    final Message message = new Message();
    message.setMessage(updatedMessage);

    if (null != attachedImage) {
      message.setAttachedB64Image(Base64.getEncoder().encodeToString(attachedImage));
    }

    final OutgoingRequestManager outgoingRequestManager =
        new OutgoingRequestManager(bufferedWriter, message);
    outgoingRequestManager.start();
  }
}
