import Helper.Constants;
import Helper.KeyType;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptionDecryptionManager {
  private static final Logger LOGGER =
      LogManager.getLogger(EncryptionDecryptionManager.class.getName());

  public static SecretKey handleEncryptionSetup(String publicKeyString)
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
          InvalidKeyException {
    final PublicKey publicKey = getPublicKeyFromServer(publicKeyString);
    return generateSymmetricKey(publicKey);
  }

  public static PublicKey getPublicKeyFromServer(String publicKeyString)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    final byte[] initialPublicKey = Base64.getDecoder().decode(publicKeyString);
    final PublicKey publicKey =
        KeyFactory.getInstance(KeyType.RSA.name())
            .generatePublic(new X509EncodedKeySpec(initialPublicKey));
    LOGGER.info(String.format("Public Key is %s", Arrays.toString(publicKey.getEncoded())));
    LOGGER.info(String.format("Public Key length is %d", publicKey.getEncoded().length));
    return publicKey;
  }

  private static SecretKey generateSymmetricKey(PublicKey publicKey)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyType.AES.name());
    keyGenerator.init(Constants.DEFAULT_AES_KEY_SIZE);
    final SecretKey secretKey = keyGenerator.generateKey();
    LOGGER.info(String.format("Symmetric Key is %s", Arrays.toString(secretKey.getEncoded())));
    LOGGER.info(String.format("Symmetric Key length is %d", secretKey.getEncoded().length));
    return secretKey;
  }

  public static byte[] generateEncryptedSymmetricKey(PublicKey publicKey, SecretKey secretKey)
      throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
          IllegalBlockSizeException, BadPaddingException {
    final Cipher cipher = Cipher.getInstance(KeyType.RSA.name());
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return cipher.doFinal(secretKey.getEncoded());
  }

  public static byte[] encryptMessage(String message, SecretKey secretKey)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException {
    final Cipher cipher = Cipher.getInstance(KeyType.AES.name());
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return cipher.doFinal(message.getBytes());
  }

  public static byte[] decryptMessage(byte[] message, SecretKey secretKey)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          IllegalBlockSizeException, BadPaddingException {
    LOGGER.info(String.format("Message is %s", Arrays.toString(message)));
    LOGGER.info(String.format("Public Key length is %d", message.length));
    final Cipher cipher = Cipher.getInstance(KeyType.AES.name());
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(message);
  }
}
