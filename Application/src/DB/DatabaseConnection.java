package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Replace these placeholders with your actual PostgreSQL database details
    private static final String HOST = "db-postgresql-nyc3-90878-do-user-18201226-0.f.db.ondigitalocean.com";
    private static final String PORT = "25060";
    private static final String DATABASE = "defaultdb";
    private static final String USERNAME = "doadmin";
    private static final String PASSWORD = "AVNS_t0aUppBCNtUPS9c9y4M";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Build the connection URL
            String url = String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, DATABASE);

            // Establish the connection
            conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
            System.out.println("Connected to the database successfully.");

        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        connect();
    }
}
