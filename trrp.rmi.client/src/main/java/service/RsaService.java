package service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class RsaService {
    private Cipher cipher;
    private PublicKey publicKey;

    public RsaService(PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("RSA");
        this.publicKey = publicKey;
    }

    public String encrypt(String key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return HexService.toHex(cipher.doFinal(key.getBytes()));
    }
}
