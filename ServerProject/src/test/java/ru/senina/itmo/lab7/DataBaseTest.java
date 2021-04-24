package ru.senina.itmo.lab7;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseTest {
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_CONNECTION = "jdbc:postgresql://localhost:12345/studs";

    public static void main(String[] args) {
        DataBaseTest db = new DataBaseTest();
        db.testDatabase();
    }

    private void testDatabase() {

        try {
            Class.forName(DB_DRIVER);
            String login = ""; //Write login here
            String password = ""; //Write password here

            String[] arr = new String[]{"'Katia'", "'Kolesenkova'", "'Natalia'", "'Nikonova'", "'Sava'", "'Rattus'"};

            try (Connection dbConnection = DriverManager.getConnection(DB_CONNECTION, login, password)) {
                Statement st = dbConnection.createStatement();
                int createTable = st.executeUpdate("CREATE TABLE if not exists persons(\n" +
                        "    id serial PRIMARY KEY,\n" +
                        "    name varchar UNIQUE NOT NULL,\n" +
                        "    surname varchar UNIQUE NOT NULL\n" +
                        "    );");
                st.close();

                for (int i = 0; i < arr.length; i += 2) {
                    String nameI = arr[i];
                    String surnameI = arr[i + 1];
                    Statement stmt = dbConnection.createStatement();
                    int addPerson = stmt.executeUpdate("INSERT INTO persons(name, surname)\n" +
                            "VALUES (" + nameI + "," + surnameI + ");");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
