package controller;

import com.rabbitmq.client.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pojo.ChampionshipPojo;
import rmiInterfaces.DbInterface;
import service.ChampionshipReader;
import service.DesService;
import service.RsaService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class MainFrm {
    @FXML
    private Button btToNormalizedSocket;

    @FXML
    private Button btToNormalizedMQ;

    @FXML
    private Button btToNormalizedRMI;

    @FXML
    private TableView<ChampionshipPojo> tvChampionships;

    @FXML
    private TableColumn<ChampionshipPojo, Integer> clmnYear;

    @FXML
    private TableColumn<ChampionshipPojo, String> clmnCountry;

    @FXML
    private TableColumn<ChampionshipPojo, String> clmnKind;

    @FXML
    private TableColumn<ChampionshipPojo, String> clmnAthlete;

    @FXML
    private TableColumn<ChampionshipPojo, Integer> clmnAgeAthlete;

    private List<ChampionshipPojo> championshipPojos = new ArrayList<ChampionshipPojo>();

    @FXML
    void initialize() {
        calibrationColumns();
        setFactoriesColumns();
        setEvents();

        championshipPojos = ChampionshipReader.getChampionshipList("C:\\Users\\zimin\\Desktop\\championship.dbf");
        fillTvChampionships();
    }

    private void calibrationColumns() {
        btToNormalizedMQ.setDisable(true);
        btToNormalizedSocket.setDisable(true);
        clmnYear.prefWidthProperty().bind(tvChampionships.widthProperty().multiply(0.2));
        clmnCountry.prefWidthProperty().bind(tvChampionships.widthProperty().multiply(0.2));
        clmnKind.prefWidthProperty().bind(tvChampionships.widthProperty().multiply(0.2));
        clmnAthlete.prefWidthProperty().bind(tvChampionships.widthProperty().multiply(0.2));
        clmnAgeAthlete.prefWidthProperty().bind(tvChampionships.widthProperty().multiply(0.2));
    }

    private void setFactoriesColumns() {
        clmnYear.setCellValueFactory(new PropertyValueFactory<>("yearChampionship"));
        clmnCountry.setCellValueFactory(new PropertyValueFactory<>("nameCountry"));
        clmnKind.setCellValueFactory(new PropertyValueFactory<>("nameKindOfSport"));
        clmnAthlete.setCellValueFactory(new PropertyValueFactory<>("nameAthlete"));
        clmnAgeAthlete.setCellValueFactory(new PropertyValueFactory<>("ageAthlete"));
    }

    private void setEvents() {
        btToNormalizedSocket.setOnAction(event -> {
            try {
                Socket socket = new Socket("10.242.4.227", 8000);  // 10.242.4.227
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                RsaService rsaService = new RsaService((PublicKey) ois.readObject());
                DesService desService = new DesService();
                oos.writeObject(rsaService.encrypt(desService.getKey()));
                oos.writeObject(desService.encrypt(championshipPojos));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | ClassNotFoundException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.showAndWait();
                e.printStackTrace();
            }
        });

        btToNormalizedMQ.setOnAction(event -> {
            try {
                final String QUEUE_NAME = "championships";

                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("10.242.4.227");
                factory.setUsername("Ilya");
                factory.setPassword("Ilya");

                Channel channelSend = factory.newConnection().createChannel();

                factory.setHost("localhost");
                Channel channelRecv = factory.newConnection().createChannel();

                channelSend.queueDeclare(QUEUE_NAME, false, false, false, null);
                channelRecv.queueDeclare(QUEUE_NAME, false, false, false, null);

                try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                    try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                        o.writeObject(championshipPojos);
                    }
                    channelSend.basicPublish("", QUEUE_NAME, null, b.toByteArray());
                }


            } catch (TimeoutException | IOException e) {
                e.printStackTrace();
            }
        });

        btToNormalizedRMI.setOnAction(event -> {
            String name = "db";
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry("192.168.100.8", 1099);

                DbInterface comp = (DbInterface) registry.lookup(name);
                DesService desService = new DesService();
                comp.sendDataToNormalizeDb(desService.encrypt(championshipPojos));
            } catch (NotBoundException | SQLException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | IOException | InvalidKeyException e) {
                e.printStackTrace();
            }
        });
    }

    private void fillTvChampionships() {
        tvChampionships.getItems().clear();
        tvChampionships.getItems().addAll(championshipPojos);
    }
}