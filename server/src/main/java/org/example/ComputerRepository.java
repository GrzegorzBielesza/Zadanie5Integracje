package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class ComputerRepository {
    public static void overwriteDatabase(String[] sqls) throws Exception {
            System.out.println("Overwriting database\n");
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

    public static void deleteAll() throws SQLException {
        System.out.println("Deleting all from database");
        Connection connection = H2DatabaseConnection.getConnection();
        Statement deleteStatement = connection.createStatement();
        String sql = "DELETE FROM computers";
        deleteStatement.executeUpdate(sql);
        deleteStatement.close();
        connection.close();
    }

    public static void saveNewComputer(String sql) throws SQLException {
        System.out.println("Writing new computer to database\n");
        sql = sql.substring(1, sql.length() - 2);
        ComputerFinder computerFinder = new ComputerFinder();
        ArrayList<ArrayList<String>> rows = computerFinder.findComputers();
        int computerId = rows.size()+1;
        sql = sql.substring(0, 219) + computerId + sql.substring(219 + 1);
        System.out.println(sql);

        Connection connection = H2DatabaseConnection.getConnection();
        Statement insertStatement = connection.createStatement();
        insertStatement.executeUpdate(sql);
        insertStatement.close();
        connection.close();
    }
}
