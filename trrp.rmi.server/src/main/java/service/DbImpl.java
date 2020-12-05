package service;

import rmiInterfaces.DbInterface;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class DbImpl implements DbInterface {
    @Override
    public void sendDataToNormalizeDb(String championshipPojos) throws SQLException {
        try {
            DbService dbService = new DbService();
            DesService desService = new DesService();
            dbService.sendDataToNormalizeDb(desService.decrypt(championshipPojos));
            System.out.println("БД наполнена!");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}