package service;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.linuxense.javadbf.DBFUtils;
import pojo.ChampionshipPojo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChampionshipReader {
    public static List<ChampionshipPojo> getChampionshipList(String fileName) {
        List<ChampionshipPojo> championshipPojos = new ArrayList<>();
        DBFReader reader = null;
        try {
            reader = new DBFReader(new FileInputStream(fileName));

            DBFRow row;

            while ((row = reader.nextRow()) != null) {
                championshipPojos.add(new ChampionshipPojo(
                        row.getInt("id_championship"),
                        row.getInt("year_championship"),
                        row.getInt("id_country"),
                        row.getString("name_country"),
                        row.getInt("id_competition"),
                        row.getInt("id_kind_of_sport"),
                        row.getString("name_kind_of_sport"),
                        row.getInt("id_athlete"),
                        row.getString("name_athlete"),
                        row.getInt("age_athlete")
                ));
            }
        } catch (DBFException | IOException e) {
            e.printStackTrace();
        } finally {
            DBFUtils.close(reader);
        }

        return championshipPojos;
    }
}
