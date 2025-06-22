import Helper.*;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientManager {
  private String serverAddress;
  private ChatroomMainPageController chatroomMainPageController;
  private Socket socket;
  private BufferedWriter bufferedWriter;
  private SecretKey secretKey;
  private String username;
  private Gson gson;
  private static final Logger LOGGER =
      LogManager.getLogger(IncomingResponseManager.class.getName());

  public ClientManager(
      String serverAddress,
      ChatroomMainPageController chatroomMainPageController,
      String username) {
    try {
      this.serverAddress = serverAddress;
      this.chatroomMainPageController = chatroomMainPageController;
      this.username = username;
      gson = new Gson();
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
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Listen for messages from server
      input = new Scanner(socket.getInputStream());
      final IncomingResponseManager incomingResponseManager =
          new IncomingResponseManager(input, this);
      incomingResponseManager.start();

      // Send server setup information
      sendMessageHandler(null, null, MessageType.Setup);
    } catch (Exception e) {
      LOGGER.error("Client Exception: " + e.getMessage());
    }
  }

  public void sendMessageHandler(
      String attachedMessage, byte[] attachedImage, MessageType messageType) {
    final MessagesJO messagesJO = new MessagesJO();
    messagesJO.setMessage(attachedMessage);
    messagesJO.setUserId(username);
    messagesJO.setMessageType(messageType);

    if (null != attachedImage) {
      messagesJO.setAttachedB64Image(Base64.getEncoder().encodeToString(attachedImage));
    }

    final OutgoingRequestManager outgoingRequestManager =
        new OutgoingRequestManager(bufferedWriter, messagesJO, getSecretKey());
    outgoingRequestManager.start();
  }

  public SecretKey getSecretKey() {
    return secretKey;
  }

  public ChatroomMainPageController getChatroomMainPageController() {
    return chatroomMainPageController;
  }

  public void storeImage(MessagesJO messagesJO) throws IOException {
    final String userDir = System.getProperty("user.dir");
    final String attachedImage = String.format("/images/%s.png", messagesJO.getUuid());
    final File outputFile = new File(userDir + attachedImage);
    outputFile.mkdirs();

    final BufferedImage image =
        ImageIO.read(
            new ByteArrayInputStream(Base64.getDecoder().decode(messagesJO.getAttachedB64Image())));
    ImageIO.write(image, "png", outputFile);
  }

  private void handleSymmetricKeySetup(String publicKeyString)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
          InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
    secretKey = EncryptionDecryptionManager.handleEncryptionSetup();
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
    final ServerSetupResponseJO message =
        gson.fromJson(decryptedServerInfo, ServerSetupResponseJO.class);

    if (null != message.getMessages()) {
      addPreviousMessages(message.getMessages());
    }

    // Update server socket to use new port number
    socket = new Socket(serverAddress, message.getClientPort());
  }

  private void addPreviousMessages(List<MessagesJO> messagesJOS) throws IOException {
    for (final MessagesJO messagesJO : messagesJOS) {
      if (null != messagesJO.getAttachedB64Image()) {
        storeImage(messagesJO);
      }
      chatroomMainPageController.addMessage(messagesJO);
    }
  }
}
