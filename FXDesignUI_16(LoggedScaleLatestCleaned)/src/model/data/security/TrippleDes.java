package model.data.security;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.log4j.Logger;

public class TrippleDes {

	private static final Logger logger = Logger.getLogger(TrippleDes.class);
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	private static final String UNICODE_FORMAT = "UTF8";
	private KeySpec ks;
	private SecretKeyFactory skf;
	private Cipher cipher;
	private byte[] arrayBytes;
	private String myEncryptionKey;
	private String myEncryptionScheme;
	private SecretKey key;

	public TrippleDes() throws Exception {
		myEncryptionKey = "THIS SECURITY IS FREAKING CRAZY AWESOME";
		myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
		arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
		ks = new DESedeKeySpec(arrayBytes);
		skf = SecretKeyFactory.getInstance(myEncryptionScheme);
		cipher = Cipher.getInstance(myEncryptionScheme);
		key = skf.generateSecret(ks);
	}

	public String encrypt(String unencryptedString) {
		String encryptedString = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] encryptedText = cipher.doFinal(plainText);
			encryptedString = new String(Base64.getEncoder().encode(
					encryptedText));
		} catch (Exception e) {
			logger.error(e);
		}
		return encryptedString;
	}

	public String decrypt(String encryptedString) {
		String decryptedText = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedText = Base64.getDecoder().decode(
					encryptedString.getBytes());
			byte[] plainText = cipher.doFinal(encryptedText);
			decryptedText = new String(plainText);
		} catch (Exception e) {
			logger.error(e);
		}
		return decryptedText;
	}

}
