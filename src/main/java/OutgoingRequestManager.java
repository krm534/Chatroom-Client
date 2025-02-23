import Helper.Constants;
import Helper.Message;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OutgoingRequestManager extends Thread {
  private final BufferedWriter bufferedWriter;
  private final Message message;
  private final Gson gson;
  private final SecretKey secretKey;
  private static final Logger LOGGER = LogManager.getLogger(OutgoingRequestManager.class.getName());

  public OutgoingRequestManager(
      BufferedWriter bufferedWriter, Message message, SecretKey secretKey) {
    this.bufferedWriter = bufferedWriter;
    this.message = message;
    this.secretKey = secretKey;
    this.gson = new Gson();
  }

  @Override
  public void run() {
    try {
      String json = gson.toJson(message, Message.class);
      json += Constants.DELIMITER;
      LOGGER.info(String.format("Message to be sent to chatroom server is %s", json));
      bufferedWriter.write(
          Base64.getEncoder()
              .encodeToString(EncryptionDecryptionManager.encryptMessage(json, secretKey)));
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (IOException
        | NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      LOGGER.error(String.format("ServerSender Exception: %s", e.getMessage()));
    }
  }
}
