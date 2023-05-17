package org.example;

import java.sql.*;

//    String[] headers = {"Marka", "Rozmiar", "Rozdzielczosc", "Matryca", "Dotykowy", "Procesor", "Liczba_rdzeni", "Taktowanie", "Ram", "Rozmiar_dysku", "Typ_dysku", "Karta_graficzna", "Pamiec_karty", "System_operacyjny", "Nagrywarka_DVD"};

public class H2DatabaseInitializer {

    public static void initializeDatabase() {
        try{
            H2DatabaseInitializer.createSchema();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void createSchema() throws SQLException {
        Connection connection = H2DatabaseConnection.getConnection();

        Statement clearStatement = connection.createStatement();
        String clearSql = "DROP TABLE IF EXISTS COMPUTERS";
        clearStatement.executeUpdate(clearSql);

        Statement statement = connection.createStatement();
        String sql = "CREATE TABLE COMPUTERS " +
                "( id INTEGER not NULL, " +
                " Marka VARCHAR(255), " +
                " Rozmiar VARCHAR(255), " +
                " Rozdzielczosc VARCHAR(255), " +
                " Matryca VARCHAR(255), " +
                " Dotykowy VARCHAR(255), " +
                " Procesor VARCHAR(255), " +
                " Liczba_rdzeni VARCHAR(255), " +
                " Taktowanie VARCHAR(255), " +
                " Ram VARCHAR(255), " +
                " Rozmiar_dysku VARCHAR(255), " +
                " Typ_dysku VARCHAR(255), " +
                " Karta_graficzna VARCHAR(255), " +
                " Pamiec_karty VARCHAR(255), " +
                " System_operacyjny VARCHAR(255), " +
                " Nagrywarka_DVD VARCHAR(255), " +
                " PRIMARY KEY ( id ));";
        statement.executeUpdate(sql);

        Statement statement2 = connection.createStatement();
        String sql2 = "INSERT INTO computers (id, Marka, Rozmiar, Rozdzielczosc, Matryca, Dotykowy, Procesor, Liczba_rdzeni, Taktowanie, Ram, Rozmiar_dysku, Typ_dysku, Karta_graficzna, Pamiec_karty, System_operacyjny, Nagrywarka_DVD) VALUES \n" +
                "(1, 'Dell', '12\"', '1920x1080', 'matowa', 'nie', 'intel i7', '4', '2800', '8GB', '240GB', 'SSD', 'intel HD Graphics 4000', '1GB', 'Windows 7 Home', 'Brak danych'),\n" +
                "(2, 'Asus', '14\"', '1600x900', 'matowa', 'nie', 'intel i5', '4', 'Brak danych', '16GB', '120GB', 'SSD', 'intel HD Graphics 5000', '1GB', 'Brak danych', 'brak'),\n" +
                "(3, 'Fujitsu', '14\"', '1920x1080', 'blyszczaca', 'tak', 'intel i7', '8', '1900', '24GB', '500GB', 'HDD', 'intel HD Graphics 520', '1GB', 'brak systemu', 'Blu-Ray'),\n" +
                "(4, 'Huawei', '13\"', 'Brak danych', 'matowa', 'nie', 'intel i7', '4', '2400', '12GB', '24GB', 'HDD', 'NVIDIA GeForce GTX 1050', 'Brak danych', 'Brak danych', 'brak'),\n" +
                "(5, 'MSI', '17\"', '1600x900', 'blyszczaca', 'tak', 'intel i7', '4', '3300', '8GB', '60GB', 'SSD', 'AMD Radeon Pro 455', '1GB', 'Windows 8.1 Profesional', 'DVD'),\n" +
                "(6, 'Dell', 'Brak danych', '1280x800', 'matowa', 'nie', 'intel i7', '4', '2800', '8GB', '240GB', 'SSD', 'Brak danych', 'Brak danych', 'Windows 7 Home', 'brak'),\n" +
                "(7, 'Asus', '14\"', '1600x900', 'matowa', 'nie', 'intel i5', '4', '2800', 'Brak danych', '120GB', 'SSD', 'intel HD Graphics 5000', '1GB', 'Windows 10 Home', 'Brak danych'),\n" +
                "(8, 'Fujitsu', '15\"', '1920x1080', 'blyszczaca', 'tak', 'intel i7', '8', '2800', '24GB', '500GB', 'HDD', 'intel HD Graphics 520', 'Brak danych', 'brak systemu', 'Blu-Ray'),\n" +
                "(9, 'Samsung', '13\"', '1366x768', 'matowa', 'nie', 'intel i7', '4', '2800', '12GB', '24GB', 'HDD', 'NVIDIA GeForce GTX 1050', '1GB', 'Windows 10 Home', 'brak'),\n" +
                "(10, 'Sony', '16\"', 'Brak danych', 'blyszczaca', 'tak', 'intel i7', '4', '2800', '8GB', 'Brak danych', 'Brak danych', 'AMD Radeon Pro 455', '1GB', 'Windows 7 Profesional', 'DVD'),\n" +
                "(11, 'Samsung', '12\"', '1280x800', 'matowa', 'nie', 'intel i7', 'Brak danych', '2120', 'Brak danych', 'Brak danych', 'Brak danych', 'intel HD Graphics 4000', '1GB', 'Brak danych', 'brak'),\n" +
                "(12, 'Samsung', '14\"', '1600x900', 'matowa', 'nie', 'intel i5', 'Brak danych', 'Brak danych', 'Brak danych', 'Brak danych', 'SSD', 'intel HD Graphics 5000', '1GB', 'Windows 10 Home', 'brak'),\n" +
                "(13, 'Fujitsu', '15\"', '1920x1080', 'blyszczaca', 'tak', 'intel i7', '8', '2800', '24GB', '500GB', 'HDD', 'intel HD Graphics 520', 'Brak danych', 'brak systemu', 'Blu-Ray'),\n" +
                "(14, 'Huawei', '13\"', '1366x768', 'matowa', 'nie', 'intel i7', '4', '3000', 'Brak danych', '24GB', 'HDD', 'NVIDIA GeForce GTX 1050', 'Brak danych', 'Windows 10 Home', 'brak'),\n" +
                "(15, 'MSI', '17\"', '1600x900', 'blyszczaca', 'tak', 'intel i7', '4', '9999', '8GB', '60GB', 'SSD', 'AMD Radeon Pro 455', '1GB', 'Windows 7 Profesional', 'Brak danych'),\n" +
                "(16, 'Huawei', '14\"', 'Brak danych', 'matowa', 'nie', 'intel i7', '4', '2200', '8GB', '16GB', 'HDD', 'NVIDIA GeForce GTX 1080', 'Brak danych', 'Brak danych', 'brak'),\n" +
                "(17, 'MSI', '17\"', '1600x900', 'blyszczaca', 'tak', 'intel i7', '4', '3300', '8GB', '60GB', 'SSD', 'AMD Radeon Pro 455', '1GB', 'Brak danych', 'Brak danych'),\n" +
                "(18, 'Asus', 'Brak danych', '1600x900', 'blyszczaca', 'tak', 'intel i5', '2', '3200', '16GB', '320GB', 'HDD', 'Brak danych', 'Brak danych', 'Windows 7 Home', 'brak'),\n" +
                "(19, 'Asus', '14\"', '1600x900', 'matowa', 'nie', 'intel i5', '4', '2800', 'Brak danych', '120GB', 'SSD', 'intel HD Graphics 5000', '1GB', 'Windows 10 Profesional', 'Brak danych'),\n" +
                "(20, 'Fujitsu', '14\"', '1280x800', 'blyszczaca', 'tak', 'intel i7', '8', '2800', '24GB', '500GB', 'HDD', 'intel HD Graphics 520', 'Brak danych', 'brak systemu', 'Blu-Ray'),\n" +
                "(21, 'Samsung', '12\"', '1600x900', 'Brak danych', 'nie', 'intel i5', '4', '2800', '12GB', '24GB', 'HDD', 'NVIDIA GeForce GTX 1050', '1GB', 'Windows 8.1 Home', 'brak'),\n" +
                "(22, 'Sony', '11\"', 'Brak danych', 'blyszczaca', 'tak', 'intel i7', '4', '2800', '8GB', 'Brak danych', 'Brak danych', 'AMD Radeon Pro 455', '1GB', 'Windows 7 Profesional', 'brak'),\n" +
                "(23, 'Samsung', '13\"', '1366x768', 'Brak danych', 'nie', 'intel i5', 'Brak danych', '2120', 'Brak danych', 'Brak danych', 'Brak danych', 'intel HD Graphics 4000', '2GB', 'Brak danych', 'DVD'),\n" +
                "(24, 'Samsung', '15\"', '1920x1080', 'matowa', 'nie', 'intel i9', 'Brak danych', 'Brak danych', 'Brak danych', 'Brak danych', 'SSD', 'intel HD Graphics 4000', '2GB', 'Windows 10 Profesional', 'Blu-Ray');";

        statement2.executeUpdate(sql2);
        statement.close();
        statement2.close();

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
        System.out.println("Utworzone tabele:\n");
        while (rs.next()) {
            System.out.println(rs.getString("TABLE_NAME"));
        }
        connection.close();
    }
}
