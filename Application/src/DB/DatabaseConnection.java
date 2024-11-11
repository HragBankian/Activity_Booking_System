package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;


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

    public static void insertMinor(String minorName, int guardianId) {
        String sql = "INSERT INTO \"Minor\" (full_name, guardian_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, minorName);
            pstmt.setInt(2, guardianId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding Minor to database.");
        }
    }

    public static void insertCity(String cityName, int instructorId) {
        String sql = "INSERT INTO \"City\" (name, instructor_id) VALUES (?::city_enum, ?)";

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

    public static int validateLogin(String userType, String email, String password) {
        String tableName = userType;
        String query = String.format("SELECT id, password FROM \"%s\" WHERE email = ?", tableName);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)){
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getUserNameById(int id, String userType) {
        String tableName = userType;
        String query = String.format("SELECT full_name FROM \"%s\" WHERE id = ?", tableName);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "User"; // Fallback name if query fails
    }

    public static boolean createOffering(Offering offering) {
        String query = "INSERT INTO \"Offering\" (title, organization, city, time, capacity, num_students, instructor_id) VALUES (?::specialty_enum, ?, ?::city_enum, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, offering.getTitle());
            pstmt.setString(2, offering.getOrganization());
            pstmt.setString(3, offering.getCity());
            pstmt.setString(4, offering.getTime());
            pstmt.setInt(5, offering.getCapacity());
            pstmt.setInt(6, offering.getNumStudents());
            pstmt.setObject(7, offering.getInstructorId(), Types.INTEGER);  // Handle null values

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Offering> getOfferings() {
        ArrayList<Offering> offerings = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String organization = rs.getString("organization");
                String city = rs.getString("city");
                String time = rs.getString("time");
                int capacity = rs.getInt("capacity");
                int numStudents = rs.getInt("num_students");
                Integer instructorId = (Integer) rs.getObject("instructor_id");

                Offering offering = new Offering(id, title, organization, city, time, capacity);
                offering.setNumStudents(numStudents);
                offering.setInstructorId(instructorId);
                offerings.add(offering);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offerings;
    }

    public static boolean updateOffering(int offeringId, Offering offering) {
        String query = "UPDATE \"Offering\" SET title = ?::specialty_enum, organization = ?, city = ?::city_enum, time = ?, capacity = ?, instructor_id = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, offering.getTitle());
            pstmt.setString(2, offering.getOrganization());
            pstmt.setString(3, offering.getCity()); // Set city using the enum directly
            pstmt.setString(4, offering.getTime());
            pstmt.setInt(5, offering.getCapacity());

            // Handle instructor_id as Integer, including possible null value
            if (offering.getInstructorId() != null) {
                pstmt.setInt(6, offering.getInstructorId());
            } else {
                pstmt.setNull(6, Types.INTEGER);  // Set null for instructor_id if it's null
            }

            pstmt.setInt(7, offeringId); // Set offering ID for where clause

            pstmt.executeUpdate();
            int test = offering.getCapacity();
            System.out.println(test);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteOffering(int offeringId) {
        String query = "DELETE FROM \"Offering\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, offeringId); // Set offering ID for deletion

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Client> getClients() {
        ArrayList<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM \"Client\"";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phone_number");
                String dateOfBirth = rs.getString("date_of_birth");

                // Create Client object
                Client client = new Client(fullName, email, null, phoneNumber, dateOfBirth);
                client.setId(id);
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    public static boolean deleteClient(int clientId) {
        String query = "DELETE FROM \"Client\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, clientId); // Set client ID for deletion

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Guardian> getGuardians() {
        ArrayList<Guardian> guardians = new ArrayList<>();
        String query = "SELECT * FROM \"Guardian\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phone_number");
                String dateOfBirth = rs.getString("date_of_birth");

                Guardian guardian = new Guardian(fullName, email, "", phoneNumber, dateOfBirth);
                guardian.setId(id); // Assuming setId is implemented in the User or Guardian class
                guardians.add(guardian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guardians;
    }

    public static ArrayList<Minor> getMinors() {
        ArrayList<Minor> minors = new ArrayList<>();
        String query = "SELECT id, full_name, guardian_id FROM \"Minor\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                int guardianId = rs.getInt("guardian_id");
                Minor minor = new Minor(id, fullName, guardianId);
                minors.add(minor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return minors;
    }

    public static boolean deleteGuardian(int guardianId) {
        String query = "DELETE FROM \"Guardian\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, guardianId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMinor(int minorId) {
        String query = "DELETE FROM \"Minor\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, minorId); // Set minor ID for deletion
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Instructor> getInstructors() {
        ArrayList<Instructor> instructors = new ArrayList<>();
        String query = "SELECT id, full_name, email, phone_number, date_of_birth, specialty FROM \"Instructor\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phone_number");
                String dateOfBirth = rs.getString("date_of_birth");
                String specialty = rs.getString("specialty");

                // Create an Instructor object and add it to the list
                Instructor instructor = new Instructor(fullName, email, "", phoneNumber, dateOfBirth, specialty);
                instructor.setId(id);  // Set the ID for the instructor
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    public static boolean deleteInstructor(int instructorId) {
        String query = "DELETE FROM \"Instructor\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, instructorId);  // Set instructor ID for deletion
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<ClientBooking> getClientBookings() {
        ArrayList<ClientBooking> clientBookings = new ArrayList<>();
        String query = "SELECT id, offering_id, client_id FROM \"ClientBooking\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int offeringId = rs.getInt("offering_id");
                int clientId = rs.getInt("client_id");
                clientBookings.add(new ClientBooking(offeringId, clientId) {{
                    setId(id); // Set the generated ID
                }});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientBookings;
    }

    public static ArrayList<MinorBooking> getMinorBookings() {
        ArrayList<MinorBooking> minorBookings = new ArrayList<>();
        String query = "SELECT id, offering_id, minor_id FROM \"MinorBooking\"";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int offeringId = rs.getInt("offering_id");
                int minorId = rs.getInt("minor_id");
                minorBookings.add(new MinorBooking(offeringId, minorId) {{
                    setId(id); // Set the generated ID
                }});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return minorBookings;
    }

    public static boolean deleteClientBooking(int bookingId) {
        String query = "DELETE FROM \"ClientBooking\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMinorBooking(int bookingId) {
        String query = "DELETE FROM \"MinorBooking\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        connect();
    }

}
