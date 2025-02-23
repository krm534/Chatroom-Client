import Helper.Constants;
import Helper.Message;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ClientManager {
  private String serverAddress;
  private ChatroomMainPageController chatroomMainPageController;
  private Socket socket;
  private BufferedWriter bufferedWriter;
  private String userId;
  private SecretKey secretKey;
  private static final Logger LOGGER =
      LogManager.getLogger(IncomingResponseManager.class.getName());

  public ClientManager(
      String serverAddress, ChatroomMainPageController chatroomMainPageController) {
    try {
      this.serverAddress = serverAddress;
      this.chatroomMainPageController = chatroomMainPageController;
      this.socket = new Socket(serverAddress, Constants.DEFAULT_PORT);
    } catch (Exception e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }

  public void setupClientManager() {
    try {
      Scanner input = new Scanner(socket.getInputStream());

      // Setup encryption with server instance
      String publicKeyString = input.nextLine();
      handleSymmetricKeySetup(publicKeyString);

      // Setup new socket connection between client and server
      final String serverSetupResponse = input.nextLine();
      setupClientConnectionWithServer(serverSetupResponse);
      input = new Scanner(socket.getInputStream());
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Listen for messages from server
      final IncomingResponseManager incomingResponseManager =
          new IncomingResponseManager(input, this);
      incomingResponseManager.start();
    } catch (Exception e) {
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
        new OutgoingRequestManager(bufferedWriter, message, getSecretKey());
    outgoingRequestManager.start();
  }

  public SecretKey getSecretKey() {
    return secretKey;
  }

  public ChatroomMainPageController getChatroomMainPageController() {
    return chatroomMainPageController;
  }

  private void handleSymmetricKeySetup(String publicKeyString)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
          InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
    secretKey = EncryptionDecryptionManager.handleEncryptionSetup(publicKeyString);
    final PublicKey publicKey = EncryptionDecryptionManager.getPublicKeyFromServer(publicKeyString);
    final byte[] encryptedSymmetricKey =
        EncryptionDecryptionManager.generateEncryptedSymmetricKey(publicKey, secretKey);
    final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
    printWriter.println(Base64.getEncoder().encodeToString(encryptedSymmetricKey));
  }

  private void setupClientConnectionWithServer(String serverResponse)
      throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
          BadPaddingException, InvalidKeyException, IOException {
    // Initialization information returned from server
    final byte[] decodedServerInfo = Base64.getDecoder().decode(serverResponse);
    final String decryptedServerInfo =
        new String(
            EncryptionDecryptionManager.decryptMessage(decodedServerInfo, secretKey),
            StandardCharsets.UTF_8);
    LOGGER.info(String.format("'%s' returned from the chatroom server", decryptedServerInfo));

    // Parse initialization information
    JSONObject jsonObject = new JSONObject(decryptedServerInfo);
    userId = jsonObject.getString("userId");
    int port = jsonObject.getInt("port");

    // Update server socket to use new port number
    socket = new Socket(serverAddress, port);
  }
}
