package service;

import pojo.ChampionshipPojo;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class DesService {
    private SecretKey key;
    private Cipher cipher;

    public String getKey() {
        return Base64.getEncoder().encodeToString(this.key.getEncoded());
    }

    public DesService() throws NoSuchAlgorithmException, NoSuchPaddingException {
        key = new SecretKeySpec("trrpRmi1".getBytes(), "DES");
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public String encrypt(List<ChampionshipPojo> championships) throws InvalidKeyException, IOException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, key);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SealedObject sealedObj = new SealedObject((Serializable) championships, cipher);
        ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(baos));
        out.writeObject(sealedObj);
        out.close();

        return HexService.toHex(baos.toByteArray());
    }
}
