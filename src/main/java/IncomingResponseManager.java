import Helper.Constants;
import Helper.MessagesJO;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
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
        final MessagesJO messagesJO = gson.fromJson(decryptedMessageString, MessagesJO.class);
        messagesJO.setUuid(UUID.randomUUID().toString());
        if (null != messagesJO.getMessage()) {
          clientManager.getChatroomMainPageController().addMessage(messagesJO);
        }
        if (null != messagesJO.getAttachedB64Image()) {
          clientManager.storeImage(messagesJO);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }
}
