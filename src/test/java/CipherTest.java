import io.github.felipebonezi.cipherizy.CipherException;
import io.github.felipebonezi.cipherizy.ICipher;
import io.github.felipebonezi.cipherizy.algorithm.CipherFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

/**
 * Cipher class test.
 */
public class CipherTest {
  
  /**
   * Credit card number with 16 chars.
   */
  private static final String CREDIT_CARD_NUMBER_16 = "6516600011112222";
  
  /**
   * Credit card number with 22 chars.
   */
  private static final String CREDIT_CARD_NUMBER_22 = "6062000011112222333355";
  
  /**
   * Master key used in encryption and decryption mode.
   * 16 bytes = 128 bits.
   */
  private static final byte[] KEY = "00_FELIPE_BONEZI".getBytes(StandardCharsets.UTF_8);
  
  /**
   * Vector salt used in encryption and decryption mode.
   * 16 bytes = 128 bits.
   */
  private static final byte[] SALT = "FELIPEBONEZISALT".getBytes(StandardCharsets.UTF_8);
  
  /**
   * Test with the master key and vector salt has input length divisible by 8.
   */
  @Test
  public void checkKeyAndSaltInputLength() {
    Assert.assertEquals(0, KEY.length % 8);
    Assert.assertEquals(0, SALT.length % 8);
  }
  
  /**
   * When we encrypt data with AES algorithm for 16 inputs length, then we expect decrypt data are
   * the same.
   *
   * @throws CipherException Throw when we've problems to instantiate {@link ICipher} or encrypt
   *                         data.
   */
  @Test
  public void whenAESEncryptData16_thenDecryptSameData() throws CipherException {
    CipherFactory factory   = CipherFactory.getInstance();
    ICipher       cipherAES = factory.get(CipherFactory.Algorithm.AES);
    
    byte[] data          = CREDIT_CARD_NUMBER_16.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedData = cipherAES.encrypt(KEY, SALT, data);
    Assert.assertEquals(0, encryptedData.length % 16);
    
    byte[] decryptedData = cipherAES.decrypt(KEY, SALT, encryptedData);
    Assert.assertArrayEquals(data, decryptedData);
    
    String creditCardNumber = new String(decryptedData);
    Assert.assertEquals(CREDIT_CARD_NUMBER_16, creditCardNumber);
    
    String creditCardNumberStr = cipherAES.decryptToString(KEY, SALT, encryptedData);
    Assert.assertEquals(CREDIT_CARD_NUMBER_16, creditCardNumberStr);
  }
  
  /**
   * When we encrypt data with AES algorithm for 22 inputs length, then we expect decrypt data are
   * the same.
   *
   * @throws CipherException Throw when we've problems to instantiate {@link ICipher} or encrypt
   *                         data.
   */
  @Test
  public void whenAESEncryptData22_thenDecryptSameData() throws CipherException {
    CipherFactory factory   = CipherFactory.getInstance();
    ICipher       cipherAES = factory.get(CipherFactory.Algorithm.AES);
    
    byte[] data          = CREDIT_CARD_NUMBER_22.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedData = cipherAES.encrypt(KEY, SALT, data);
    Assert.assertEquals(0, encryptedData.length % 16);
    
    byte[] decryptedData = cipherAES.decrypt(KEY, SALT, encryptedData);
    Assert.assertArrayEquals(data, decryptedData);
    
    String creditCardNumber = new String(decryptedData);
    Assert.assertEquals(CREDIT_CARD_NUMBER_22, creditCardNumber);
    
    String creditCardNumberStr = cipherAES.decryptToString(KEY, SALT, encryptedData);
    Assert.assertEquals(CREDIT_CARD_NUMBER_22, creditCardNumberStr);
  }
  
  @Test
  public void whenAESEncryptWithSalt_thenDoNotDecryptWithOtherSalt() throws CipherException {
    CipherFactory factory   = CipherFactory.getInstance();
    ICipher       cipherAES = factory.get(CipherFactory.Algorithm.AES);
    
    byte[] data          = CREDIT_CARD_NUMBER_16.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedData = cipherAES.encrypt(KEY, SALT, data);
    byte[] decryptWithOtherSalt =
        cipherAES.decrypt(KEY, "1234567890098765".getBytes(), encryptedData);
    
    Assert.assertNotEquals(data, decryptWithOtherSalt);
  }
  
  @Test
  public void whenAESEncryptWithKey_thenDoNotDecryptWithOtherKey() throws CipherException {
    CipherFactory factory   = CipherFactory.getInstance();
    ICipher       cipherAES = factory.get(CipherFactory.Algorithm.AES);
    
    byte[] data          = CREDIT_CARD_NUMBER_16.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedData = cipherAES.encrypt(KEY, SALT, data);
    
    try {
      cipherAES.decrypt("0987654321678901".getBytes(), SALT, encryptedData);
      Assert.fail();
    } catch (CipherException ignored) {
    }
  }

  @Test
  public void whenAESEncryptStringWithKey_thenDecryptSuccefully() throws CipherException {
    CipherFactory factory = CipherFactory.getInstance();
    ICipher cipherAES = factory.get(CipherFactory.Algorithm.AES);

    byte[] encryptedData = cipherAES.encryptFromString(KEY, SALT, CREDIT_CARD_NUMBER_16);
    Assert.assertEquals(0, encryptedData.length % 16);

    String decryptedData = cipherAES.decryptToString(KEY, SALT, encryptedData);
    Assert.assertEquals("Original data must be equals to decrypted data", CREDIT_CARD_NUMBER_16, decryptedData);
  }

  @Test
  public void whenAESEncryptFileWithKey_thenDecryptSuccefully() throws CipherException, IOException {
    CipherFactory factory = CipherFactory.getInstance();
    ICipher cipherAES = factory.get(CipherFactory.Algorithm.AES);

    File originalFile = File.createTempFile("cipherizy-decrypt-test", ".tmp");
    Files.write(originalFile.toPath(), CREDIT_CARD_NUMBER_22.getBytes(StandardCharsets.UTF_8));
    originalFile.deleteOnExit();

    byte[] encryptedData = cipherAES.encrypt(KEY, SALT, originalFile);
    Assert.assertEquals(0, encryptedData.length % 16);

    File decryptedFile          = cipherAES.decryptToFile(KEY, SALT, encryptedData);
    String decryptedFileContent = new String(Files.readAllBytes(decryptedFile.toPath()), StandardCharsets.UTF_8);
    Assert.assertEquals("Original file content must be equals to decrypted file content", CREDIT_CARD_NUMBER_22, decryptedFileContent);
  }
  
}
