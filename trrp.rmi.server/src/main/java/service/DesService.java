package service;

import pojo.ChampionshipPojo;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class DesService {
    private SecretKey key;
    private Cipher cipher;

    public DesService() throws NoSuchAlgorithmException, NoSuchPaddingException {
        key = new SecretKeySpec("trrpRmi1".getBytes(), "DES");
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public List<ChampionshipPojo> decrypt(String data) throws InvalidKeyException, IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, key);

        SealedObject sealedObject = null;
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(HexService.fromHex(data))));
        sealedObject = (SealedObject) in.readObject();
        List<ChampionshipPojo> championships = (List<ChampionshipPojo>) sealedObject.getObject(cipher);
        in.close();

        return championships;
    }
}
