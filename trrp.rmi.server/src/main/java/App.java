import rmiInterfaces.DbInterface;
import service.DbImpl;
import service.DbService;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class App {

    public static void main(String[] args) throws IOException {
        DbService dbService = new DbService();
        dbService.initDb();

        DbInterface impl = new DbImpl();
        try {
            DbInterface stub = (DbInterface) UnicastRemoteObject.exportObject(impl, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("db", stub);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println("Bound!");
    }
}