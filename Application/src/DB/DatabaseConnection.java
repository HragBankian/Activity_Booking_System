package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    public static void insertUser(User user) {
        String sql = String.format(
                "INSERT INTO \"%s\" (full_name, email, password, phone_number, date_of_birth) VALUES (?, ?, ?, ?, ?)",
                user.getUserType() // Directly using the table name from getUserType()
        );

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getDateOfBirth());

            pstmt.executeUpdate();
            System.out.println(user.getUserType() + " added to database.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding " + user.getUserType() + " to database.");
        }
    }

    public static void main(String[] args) {
        connect();
    }
}
