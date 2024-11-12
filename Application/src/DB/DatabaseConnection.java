package DB;

import OOP.City;
import OOP.Users.Instructor;
import OOP.Minor;
import OOP.Users.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

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

    public static int insertUser(User user) {
        String sql;

        // Check if the user is an Instructor
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;

            // Modify the SQL query to include the 'specialty' column for instructors and cast the specialty to the enum type
            sql = "INSERT INTO \"Instructor\" (full_name, email, password, phone_number, date_of_birth, specialty) " +
                    "VALUES (?, ?, ?, ?, ?, ?::specialty_enum) RETURNING id";

            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getFullName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setString(4, user.getPhoneNumber());
                pstmt.setString(5, user.getDateOfBirth());
                pstmt.setString(6, instructor.getSpecialty());  // Set the specialty for the instructor

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    System.out.println(user.getUserType() + " added to database with ID: " + userId);
                    return userId;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error adding " + user.getUserType() + " to database.");
            }
        } else {
            // For other user types, keep the original query (without specialty)
            sql = String.format(
                    "INSERT INTO \"%s\" (full_name, email, password, phone_number, date_of_birth) VALUES (?, ?, ?, ?, ?) RETURNING id",
                    user.getUserType() // Directly using the table name from getUserType()
            );

            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getFullName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setString(4, user.getPhoneNumber());
                pstmt.setString(5, user.getDateOfBirth());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    System.out.println(user.getUserType() + " added to database with ID: " + userId);
                    return userId;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error adding " + user.getUserType() + " to database.");
            }
        }

        return -1;
    }

    public static void insertMinor(Minor minor) {
        String sql = "INSERT INTO \"Minor\" (full_name, guardian_id) VALUES (?, ?)";
        String minorName = minor.getFullName();
        int guardianId = minor.getGuardianId();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, minorName);
            pstmt.setInt(2, guardianId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding Minor to database.");
        }
    }

    public static void insertCity(City city) {
        String sql = "INSERT INTO \"City\" (name, instructor_id) VALUES (?::city_enum, ?)";
        String cityName = city.getName();
        int instructorId = city.getInstructorId();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cityName);
            pstmt.setInt(2, instructorId);
            pstmt.executeUpdate();
            System.out.println("City added: " + cityName + " for instructor ID: " + instructorId);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding city: " + cityName);
        }
    }

    public static void main(String[] args) {
        connect();
    }

}
