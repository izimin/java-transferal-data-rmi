package rmiInterfaces;

import pojo.ChampionshipPojo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

public interface DbInterface extends Remote {
    void sendDataToNormalizeDb(String championshipPojos) throws RemoteException, SQLException;
}