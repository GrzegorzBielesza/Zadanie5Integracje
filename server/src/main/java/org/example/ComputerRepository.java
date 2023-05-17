package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComputerRepository {
    public static void writeToDatabase(String[] sqls) throws Exception {
            System.out.println("Writing to database");
            Connection connection = H2DatabaseConnection.getConnection();
            Statement deleteStatement = connection.createStatement();
            String sql = "DELETE FROM computers";
            deleteStatement.executeUpdate(sql);
            deleteStatement.close();

            Statement insertStatement = connection.createStatement();
            Arrays.stream(sqls).forEach(sql2 -> {
                try {
                    System.out.println(sql2);
                    insertStatement.executeUpdate(sql2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            insertStatement.close();
            connection.close();

    }
}
