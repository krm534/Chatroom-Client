import Helper.Constants;
import Helper.KeyType;
import Helper.Message;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
import javax.crypto.*;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class IncomingResponseManager extends Thread {
  private String address;
  private ChatroomMainPageController chatroomMainPageController;
  private Socket socket;
  private BufferedWriter bufferedWriter;
  private String userId;
  private Gson gson;
  private SecretKey secretKey;
  private static final Logger LOGGER =
      LogManager.getLogger(IncomingResponseManager.class.getName());

  public IncomingResponseManager(
      String address, ChatroomMainPageController chatroomMainPageController) {
    try {
      this.address = address;
      this.chatroomMainPageController = chatroomMainPageController;
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
      String encryptedPublicKey = input.nextLine();
      handleEncryptionSetup(encryptedPublicKey);

      // Initialization information returned from server
      final byte[] decodedServerInfo = Base64.getDecoder().decode(input.nextLine());
      final String decryptedServerInfo =
          new String(decryptMessage(decodedServerInfo), StandardCharsets.UTF_8);
      LOGGER.info(String.format("'%s' returned from the chatroom server", decryptedServerInfo));
      socket.close();

      // Parse initialization information
      JSONObject jsonObject = new JSONObject(decryptedServerInfo);
      userId = jsonObject.getString("userId");
      int port = jsonObject.getInt("port");

      // Update server socket to use new port number
      socket = new Socket(address, port);
      input = new Scanner(socket.getInputStream());
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Listen for messages from Server
      while (true) {
        final String encryptedMessage = input.nextLine();
        if (null == encryptedMessage || encryptedMessage.isEmpty()) {
          throw new Exception();
        }
        final byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        String decryptedMessageString =
            new String(decryptMessage(decodedMessage), StandardCharsets.UTF_8);
        decryptedMessageString = decryptedMessageString.replace(Constants.DELIMITER, "");
        LOGGER.info(String.format("Decrypted message is %s", decryptedMessageString));
        final Message message = gson.fromJson(decryptedMessageString, Message.class);
        message.setUuid(UUID.randomUUID().toString());
        if (null != message.getMessage()) {
          chatroomMainPageController.addMessage(message);
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

  public void sendMessageHandler(String attachedMessage, byte[] attachedImage) {
    final String updatedMessage = String.format("%s: %s", userId, attachedMessage);
    final Message message = new Message();
    message.setMessage(updatedMessage);

    if (null != attachedImage) {
      message.setAttachedB64Image(Base64.getEncoder().encodeToString(attachedImage));
    }

    final OutgoingRequestManager outgoingRequestManager =
        new OutgoingRequestManager(bufferedWriter, message, this);
    outgoingRequestManager.start();
  }

  private void handleEncryptionSetup(String encryptedPublicKey)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
          InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    // Get the public key from server
    final byte[] initialPublicKey = Base64.getDecoder().decode(encryptedPublicKey);
    final PublicKey publicKey =
        KeyFactory.getInstance(KeyType.RSA.name())
            .generatePublic(new X509EncodedKeySpec(initialPublicKey));
    LOGGER.info(String.format("Public Key is %s", Arrays.toString(publicKey.getEncoded())));
    LOGGER.info(String.format("Public Key length is %d", publicKey.getEncoded().length));

    // Generate symmetric key and send it encrypted to server
    final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyType.AES.name());
    keyGenerator.init(Constants.DEFAULT_AES_KEY_SIZE);
    secretKey = keyGenerator.generateKey();
    LOGGER.info(String.format("Symmetric Key is %s", Arrays.toString(secretKey.getEncoded())));
    LOGGER.info(String.format("Symmetric Key length is %d", secretKey.getEncoded().length));
    final Cipher cipher = Cipher.getInstance(KeyType.RSA.name());
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    final byte[] encryptedMessage = cipher.doFinal(secretKey.getEncoded());
    final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
    printWriter.println(Base64.getEncoder().encodeToString(encryptedMessage));
  }

  public byte[] encryptMessage(String message)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException {
    final Cipher cipher = Cipher.getInstance(KeyType.AES.name());
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return cipher.doFinal(message.getBytes());
  }

  public byte[] decryptMessage(byte[] message)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException {
    LOGGER.info(String.format("Message is %s", Arrays.toString(message)));
    LOGGER.info(String.format("Public Key length is %d", message.length));
    final Cipher cipher = Cipher.getInstance(KeyType.AES.name());
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(message);
  }
}
