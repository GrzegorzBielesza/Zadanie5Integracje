package org.example;

import org.example.H2DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class ComputerFinder {

    public ArrayList<ArrayList<String>> findComputers() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        String[] headers = {"Marka", "Rozmiar", "Rozdzielczosc", "Matryca", "Dotykowy", "Procesor", "Liczba_rdzeni", "Taktowanie", "Ram", "Rozmiar_dysku", "Typ_dysku", "Karta_graficzna", "Pamiec_karty", "System_operacyjny", "Nagrywarka_DVD"};

        try {
            Connection connection = H2DatabaseConnection.getConnection();
            Statement selectStatement = connection.createStatement();
            String sql = "SELECT * FROM computers";
            ResultSet rs = selectStatement.executeQuery(sql);

            while (rs.next()) {
                String[] rowData = new String[headers.length];
                for (int j = 0; j < headers.length; j++) {
                    rowData[j] = rs.getString(headers[j]);
                }
                rows.add(new ArrayList<>(Arrays.asList(rowData)));
            }
            selectStatement.close();
            connection.close();

        } catch (SQLException e){
            e.printStackTrace();
        }
        return rows;
    }

}
