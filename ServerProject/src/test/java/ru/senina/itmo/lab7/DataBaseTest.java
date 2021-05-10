package ru.senina.itmo.lab7;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class DataBaseTest {
    //TODO: fix the run problem
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_CONNECTION = "jdbc:postgresql://localhost:12345/studs";
    private Connection dbConnection;

    @BeforeEach
    private void before() throws ClassNotFoundException, SQLException {
        Class.forName(DB_DRIVER);
        String login = ""; //Write login here
        String password = ""; //Write password here
        dbConnection = DriverManager.getConnection(DB_CONNECTION, login, password);
    }


    @AfterEach
    private void close() throws SQLException {
        dbConnection.close();
    }

    @Test
    private void sqlConnection() throws SQLException {

            String[] arr = new String[]{"'Katia'", "'Kolesenkova'", "'Natalia'", "'Nikonova'", "'Sava'", "'Rattus'"};
            Statement st = dbConnection.createStatement();
            int createTable = st.executeUpdate("CREATE TABLE if not exists persons(\n" +
                    "    id serial PRIMARY KEY,\n" +
                    "    name varchar UNIQUE NOT NULL,\n" +
                    "    surname varchar UNIQUE NOT NULL\n" +
                    "    );");
            st.close();

            for (int i = 0; i < arr.length; i += 2) {
                String name = arr[i];
                String surname = arr[i + 1];
                Statement stmt = dbConnection.createStatement();
                int addPerson = stmt.executeUpdate("INSERT INTO persons\n" +
                        "VALUES (" + name + "," + surname + ");");
                stmt.close();
            }
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM persons");
            while (resultSet.next()) {
                String str = resultSet.getInt("id") + " " + resultSet.getString("name") + " " + resultSet.getString("surname");
                System.out.println("User:" + str);
            }
            statement.cancel();
            resultSet.close();

            Statement dropTable = dbConnection.createStatement();
            dropTable.executeUpdate("DROP TABLE persons");
    }
}
