import Helper.Constants;
import Helper.Message;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IncomingResponseManager extends Thread {
  private Gson gson;
  private Scanner input;
  private ClientManager clientManager;
  private static final Logger LOGGER =
      LogManager.getLogger(IncomingResponseManager.class.getName());

  public IncomingResponseManager(Scanner input, ClientManager clientManager) {
    this.input = input;
    this.clientManager = clientManager;
    gson = new Gson();
  }

  @Override
  public void run() {
    try {
      while (true) {
        final String encryptedMessage = input.nextLine();
        if (null == encryptedMessage || encryptedMessage.isEmpty()) {
          throw new Exception();
        }
        final byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        String decryptedMessageString =
            new String(
                EncryptionDecryptionManager.decryptMessage(
                    decodedMessage, clientManager.getSecretKey()),
                StandardCharsets.UTF_8);
        decryptedMessageString = decryptedMessageString.replace(Constants.DELIMITER, "");
        LOGGER.info(String.format("Decrypted message is %s", decryptedMessageString));
        final Message message = gson.fromJson(decryptedMessageString, Message.class);
        message.setUuid(UUID.randomUUID().toString());
        if (null != message.getMessage()) {
          clientManager.getChatroomMainPageController().addMessage(message);
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
    } catch (Exception e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }
}
