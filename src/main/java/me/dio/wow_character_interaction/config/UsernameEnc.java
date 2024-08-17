package me.dio.wow_character_interaction.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class UsernameEnc {

    private static final Logger logger = LoggerFactory.getLogger(UsernameEnc.class);

    @Value("${user.crypt.secret}")
    private String secret;

    private static final String ALGORITHM = "AES";
    private SecretKey secretKey;

    @PostConstruct
    public void init() throws Exception {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(key, 0, key.length, ALGORITHM);
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getUrlEncoder().encodeToString(encryptedData);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedData = Base64.getUrlDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}