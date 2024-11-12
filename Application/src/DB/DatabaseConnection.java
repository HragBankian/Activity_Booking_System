package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;


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
        String checkQuery = "SELECT COUNT(*) FROM \"Offering\" WHERE city = ?::city_enum AND time = ?";
        String insertQuery = "INSERT INTO \"Offering\" (title, organization, city, time, capacity, num_students, instructor_id) VALUES (?::specialty_enum, ?, ?::city_enum, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if an offering with the same city and time already exists
            checkStmt.setString(1, offering.getCity());
            checkStmt.setString(2, offering.getTime());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "An offering with the same Time and City already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Proceed with insertion if no conflict
            insertStmt.setString(1, offering.getTitle());
            insertStmt.setString(2, offering.getOrganization());
            insertStmt.setString(3, offering.getCity());
            insertStmt.setString(4, offering.getTime());
            insertStmt.setInt(5, offering.getCapacity());
            insertStmt.setInt(6, offering.getNumStudents());
            insertStmt.setObject(7, offering.getInstructorId(), Types.INTEGER);  // Handle null values

            insertStmt.executeUpdate();
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
        // Check if there's another offering with the same city and time, excluding the current offering
        String checkQuery = "SELECT COUNT(*) FROM \"Offering\" WHERE city = ?::city_enum AND time = ? AND id <> ?";
        // Update query
        String updateQuery = "UPDATE \"Offering\" SET title = ?::specialty_enum, organization = ?, city = ?::city_enum, time = ?, capacity = ?, instructor_id = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Trim whitespace from city and time before passing them to the database
            String city = offering.getCity().trim();
            String time = offering.getTime().trim();

            // Check if an offering with the same city and time already exists, excluding the current offering
            checkStmt.setString(1, city);
            checkStmt.setString(2, time);
            checkStmt.setInt(3, offeringId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "An offering with the same Time and City already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false; // If a conflict is found, return false
                }
            }

            // Proceed with update if no conflict
            updateStmt.setString(1, offering.getTitle());
            updateStmt.setString(2, offering.getOrganization());
            updateStmt.setString(3, city);
            updateStmt.setString(4, time);
            updateStmt.setInt(5, offering.getCapacity());

            // Handle instructor_id
            if (offering.getInstructorId() != null) {
                updateStmt.setInt(6, offering.getInstructorId());
            } else {
                updateStmt.setNull(6, Types.INTEGER);
            }

            // Set the offeringId for the WHERE clause
            updateStmt.setInt(7, offeringId);

            // Execute update statement
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated == 0) {
                JOptionPane.showMessageDialog(null, "Offering update failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return false; // If no rows were updated, return false
            }

            return true; // Return true if the update is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an SQL exception occurs
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
        String findOfferingsQuery = "SELECT offering_id FROM \"ClientBooking\" WHERE client_id = ?";
        String decrementStudentsQuery = "UPDATE \"Offering\" SET num_students = num_students - 1 WHERE id = ?";
        String deleteClientQuery = "DELETE FROM \"Client\" WHERE id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);  // Begin transaction

            // Step 1: Find all offering IDs associated with this client
            try (PreparedStatement findOfferingsStmt = conn.prepareStatement(findOfferingsQuery)) {
                findOfferingsStmt.setInt(1, clientId);
                ResultSet rs = findOfferingsStmt.executeQuery();

                // Step 2: Decrement num_students for each associated offering
                try (PreparedStatement decrementStmt = conn.prepareStatement(decrementStudentsQuery)) {
                    while (rs.next()) {
                        int offeringId = rs.getInt("offering_id");
                        decrementStmt.setInt(1, offeringId);
                        decrementStmt.executeUpdate();
                    }
                }
            }

            // Step 3: Delete the client from the Client table
            try (PreparedStatement deleteClientStmt = conn.prepareStatement(deleteClientQuery)) {
                deleteClientStmt.setInt(1, clientId);
                deleteClientStmt.executeUpdate();
            }

            conn.commit();  // Commit the transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


//    public static boolean deleteClient(int clientId) {
//        String query = "DELETE FROM \"Client\" WHERE id = ?";
//        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setInt(1, clientId); // Set client ID for deletion
//
//            pstmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

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
        String findOfferingsQuery = "SELECT offering_id FROM \"MinorBooking\" WHERE minor_id = ?";
        String decrementStudentsQuery = "UPDATE \"Offering\" SET num_students = num_students - 1 WHERE id = ?";
        String deleteMinorQuery = "DELETE FROM \"Minor\" WHERE id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);  // Begin transaction

            // Step 1: Find all offering IDs associated with this minor
            try (PreparedStatement findOfferingsStmt = conn.prepareStatement(findOfferingsQuery)) {
                findOfferingsStmt.setInt(1, minorId);
                ResultSet rs = findOfferingsStmt.executeQuery();

                // Step 2: Decrement num_students for each associated offering
                try (PreparedStatement decrementStmt = conn.prepareStatement(decrementStudentsQuery)) {
                    while (rs.next()) {
                        int offeringId = rs.getInt("offering_id");
                        decrementStmt.setInt(1, offeringId);
                        decrementStmt.executeUpdate();
                    }
                }
            }

            // Step 3: Delete the minor from Minor table
            try (PreparedStatement deleteMinorStmt = conn.prepareStatement(deleteMinorQuery)) {
                deleteMinorStmt.setInt(1, minorId);
                deleteMinorStmt.executeUpdate();
            }

            conn.commit();  // Commit the transaction
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
        // Query to get all offerings by the instructor
        String getOfferingsQuery = "SELECT id FROM \"Offering\" WHERE instructor_id = ?";
        // Query to delete bookings associated with these offerings in ClientBooking
        String deleteClientBookingsQuery = "DELETE FROM \"ClientBooking\" WHERE offering_id = ?";
        // Query to delete bookings associated with these offerings in MinorBooking
        String deleteMinorBookingsQuery = "DELETE FROM \"MinorBooking\" WHERE offering_id = ?";
        // Query to delete the instructor
        String deleteInstructorQuery = "DELETE FROM \"Instructor\" WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement getOfferingsStmt = conn.prepareStatement(getOfferingsQuery);
             PreparedStatement deleteClientBookingsStmt = conn.prepareStatement(deleteClientBookingsQuery);
             PreparedStatement deleteMinorBookingsStmt = conn.prepareStatement(deleteMinorBookingsQuery);
             PreparedStatement deleteInstructorStmt = conn.prepareStatement(deleteInstructorQuery)) {

            // Step 1: Get all offerings associated with the instructor
            getOfferingsStmt.setInt(1, instructorId);
            ResultSet rs = getOfferingsStmt.executeQuery();

            // Step 2: For each offering, delete the associated bookings in both ClientBooking and MinorBooking
            while (rs.next()) {
                int offeringId = rs.getInt("id");

                // Delete all bookings for this offering in ClientBooking
                deleteClientBookingsStmt.setInt(1, offeringId);
                deleteClientBookingsStmt.executeUpdate();

                // Delete all bookings for this offering in MinorBooking
                deleteMinorBookingsStmt.setInt(1, offeringId);
                deleteMinorBookingsStmt.executeUpdate();
            }

            // Step 3: Finally, delete the instructor
            deleteInstructorStmt.setInt(1, instructorId);
            deleteInstructorStmt.executeUpdate();

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

    public static ArrayList<Offering> getAvailableOfferingsForClient(int clientId) {
        ArrayList<Offering> availableOfferings = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\" WHERE instructor_id IS NOT NULL AND num_students < capacity " +
                "AND id NOT IN (SELECT offering_id FROM \"ClientBooking\" WHERE client_id = ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String organization = rs.getString("organization");
                    String city = rs.getString("city");
                    String time = rs.getString("time");
                    int capacity = rs.getInt("capacity");
                    int numStudents = rs.getInt("num_students");

                    Offering offering = new Offering(id, title, organization, city, time, capacity);
                    offering.setNumStudents(numStudents);  // Set numStudents using setter
                    availableOfferings.add(offering);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableOfferings;
    }

    public static boolean bookOfferingForClient(int offeringId, int clientId) {
        String checkClientBookingQuery = "SELECT o.time FROM \"ClientBooking\" cb " +
                "JOIN \"Offering\" o ON cb.offering_id = o.id " +
                "WHERE cb.client_id = ? AND o.time = (SELECT time FROM \"Offering\" WHERE id = ?)";
        String clientBookingQuery = "INSERT INTO \"ClientBooking\" (offering_id, client_id) VALUES (?, ?)";
        String updateOfferingQuery = "UPDATE \"Offering\" SET num_students = num_students + 1 WHERE id = ? AND num_students < capacity";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);  // Start transaction

            // Step 1: Check if the client already has a booking at the same time
            try (PreparedStatement checkStmt = conn.prepareStatement(checkClientBookingQuery)) {
                checkStmt.setInt(1, clientId);
                checkStmt.setInt(2, offeringId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Client already has a booking at the same time
                    JOptionPane.showMessageDialog(null, "You already have a booking at this time.", "Error", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();  // Rollback transaction
                    return false;
                }
            }

            // Step 2: Insert the client booking
            try (PreparedStatement pstmt = conn.prepareStatement(clientBookingQuery)) {
                pstmt.setInt(1, offeringId);
                pstmt.setInt(2, clientId);
                pstmt.executeUpdate();
            }

            // Step 3: Update the offering's num_students
            try (PreparedStatement pstmt = conn.prepareStatement(updateOfferingQuery)) {
                pstmt.setInt(1, offeringId);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated == 0) {
                    conn.rollback();  // Rollback if update fails
                    return false;
                }
            }

            conn.commit();  // Commit the transaction
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<ClientBooking> getClientBookingsForClient(int clientId) {
        ArrayList<ClientBooking> clientBookings = new ArrayList<>();
        String clientBookingQuery = "SELECT cb.id, cb.offering_id FROM \"ClientBooking\" cb WHERE cb.client_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(clientBookingQuery)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int offeringId = rs.getInt("offering_id");

                    // Create a new ClientBooking object using the correct constructor
                    ClientBooking booking = new ClientBooking(offeringId, clientId);
                    booking.setId(id);  // Set the id for the ClientBooking

                    clientBookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientBookings;
    }

    public static Offering getOfferingById(int offeringId) {
        Offering offering = null;
        String offeringQuery = "SELECT * FROM \"Offering\" WHERE id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(offeringQuery)) {
            pstmt.setInt(1, offeringId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String organization = rs.getString("organization");
                    String city = rs.getString("city");
                    String time = rs.getString("time");
                    int capacity = rs.getInt("capacity");
                    int numStudents = rs.getInt("num_students");

                    offering = new Offering(offeringId, title, organization, city, time, capacity);
                    offering.setNumStudents(numStudents);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offering;
    }

    public static boolean cancelClientBooking(int bookingId) {
        String getOfferingIdQuery = "SELECT offering_id FROM \"ClientBooking\" WHERE id = ?";
        String deleteBookingQuery = "DELETE FROM \"ClientBooking\" WHERE id = ?";
        String updateOfferingQuery = "UPDATE \"Offering\" SET num_students = num_students - 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement getOfferingStmt = conn.prepareStatement(getOfferingIdQuery);
             PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingQuery);
             PreparedStatement updateOfferingStmt = conn.prepareStatement(updateOfferingQuery)) {

            // Get the offering_id for the booking
            getOfferingStmt.setInt(1, bookingId);
            try (ResultSet rs = getOfferingStmt.executeQuery()) {
                if (rs.next()) {
                    int offeringId = rs.getInt("offering_id");

                    // Delete the booking from client_bookings table
                    deleteBookingStmt.setInt(1, bookingId);
                    deleteBookingStmt.executeUpdate();

                    // Decrement the num_students in the offerings table
                    updateOfferingStmt.setInt(1, offeringId);
                    updateOfferingStmt.executeUpdate();

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean assignInstructorToOffering(int offeringId, int instructorId) {
        String query = "UPDATE \"Offering\" SET instructor_id = ? WHERE id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, instructorId);
            pstmt.setInt(2, offeringId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;  // Return true if the offering was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getInstructorCities(int instructorId) {
        ArrayList<String> cities = new ArrayList<>();
        String query = "SELECT name FROM \"City\" WHERE instructor_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cities.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static String getInstructorSpecialty(int instructorId) {
        String specialty = null;
        String query = "SELECT specialty FROM \"Instructor\" WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                specialty = rs.getString("specialty");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specialty;
    }

    public static ArrayList<Object[]> getAvailableOfferings(int instructorId) {
        ArrayList<Object[]> offerings = new ArrayList<>();

        ArrayList<String> instructorCities = getInstructorCities(instructorId);
        String instructorSpecialty = getInstructorSpecialty(instructorId);

        String query = "SELECT * FROM \"Offering\" WHERE instructor_id IS NULL AND city = ANY (?) AND title = ?::specialty_enum";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            Array citiesArray = conn.createArrayOf("city_enum", instructorCities.toArray());
            pstmt.setArray(1, citiesArray);
            pstmt.setString(2, instructorSpecialty);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Create an Offering object and add to the list
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String organization = rs.getString("organization");
                String city = rs.getString("city");
                String time = rs.getString("time");
                int capacity = rs.getInt("capacity");
                offerings.add(new Object[]{id, title, organization, city, time, capacity});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offerings;
    }

    public static ArrayList<Object[]> getInstructorLessons(int instructorId) {
        ArrayList<Object[]> lessons = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\" WHERE instructor_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, instructorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lessons.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("organization"),
                            rs.getString("city"),
                            rs.getString("time"),
                            rs.getInt("capacity")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    public static boolean cancelLesson(int offeringId) {
        // Query to set instructor_id to NULL for the offering
        String cancelLessonQuery = "UPDATE \"Offering\" SET instructor_id = NULL WHERE id = ?";
        // Query to delete bookings associated with this offering from ClientBooking table
        String deleteClientBookingsQuery = "DELETE FROM \"ClientBooking\" WHERE offering_id = ?";
        // Query to delete bookings associated with this offering from MinorBooking table
        String deleteMinorBookingsQuery = "DELETE FROM \"MinorBooking\" WHERE offering_id = ?";

        try (Connection conn = connect();
             PreparedStatement cancelLessonStmt = conn.prepareStatement(cancelLessonQuery);
             PreparedStatement deleteClientBookingsStmt = conn.prepareStatement(deleteClientBookingsQuery);
             PreparedStatement deleteMinorBookingsStmt = conn.prepareStatement(deleteMinorBookingsQuery)) {

            // Step 1: Set instructor_id to NULL, indicating the lesson is canceled
            cancelLessonStmt.setInt(1, offeringId);
            int rowsUpdated = cancelLessonStmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Step 2: Delete all bookings associated with this offering in ClientBooking
                deleteClientBookingsStmt.setInt(1, offeringId);
                deleteClientBookingsStmt.executeUpdate();

                // Step 3: Delete all bookings associated with this offering in MinorBooking
                deleteMinorBookingsStmt.setInt(1, offeringId);
                deleteMinorBookingsStmt.executeUpdate();

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<String> getMinorsForGuardian(int guardianId) {
        ArrayList<String> minors = new ArrayList<>();
        String query = "SELECT full_name FROM \"Minor\" WHERE guardian_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                minors.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return minors;
    }

    public static ArrayList<Integer> getMinorIdsForGuardian(int guardianId) {
        ArrayList<Integer> minors = new ArrayList<>();
        String query = "SELECT id FROM \"Minor\" WHERE guardian_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                minors.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return minors;
    }

    public static ArrayList<Offering> getAvailableOfferingsForGuardian() {
        ArrayList<Offering> availableOfferings = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\" WHERE instructor_id IS NOT NULL AND num_students < capacity";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String organization = rs.getString("organization");
                    String city = rs.getString("city");
                    String time = rs.getString("time");
                    int capacity = rs.getInt("capacity");
                    int numStudents = rs.getInt("num_students");

                    Offering offering = new Offering(id, title, organization, city, time, capacity);
                    offering.setNumStudents(numStudents);  // Set numStudents using setter
                    availableOfferings.add(offering);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableOfferings;
    }

    public static boolean bookOfferingForMinor(int guardianId, String minorFullName, int offeringId) {
        // Step 1: Find the minor_id using guardian_id and minor full_name
        int minorId = -1;
        String getMinorIdQuery = "SELECT id FROM \"Minor\" WHERE guardian_id = ? AND full_name = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(getMinorIdQuery)) {
            pstmt.setInt(1, guardianId);
            pstmt.setString(2, minorFullName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    minorId = rs.getInt("id");  // Get minor's id
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while fetching minor id
        }

        if (minorId == -1) {
            return false;  // Minor not found
        }

        if (minorBookingExists(offeringId, minorId)){
            System.out.println("The offering-minor pair already exists.");
            return false;
        }

        // Step 2: Insert the booking into the MinorBooking table
        String insertBookingQuery = "INSERT INTO \"MinorBooking\" (offering_id, minor_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertBookingQuery)) {
            pstmt.setInt(1, offeringId);
            pstmt.setInt(2, minorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while inserting booking
        }

        // Step 3: Increment num_students in the Offering table
        String updateOfferingQuery = "UPDATE \"Offering\" SET num_students = num_students + 1 WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(updateOfferingQuery)) {
            pstmt.setInt(1, offeringId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while updating offering
        }

        return true;  // Successfully booked the offering for the minor
    }

    private static boolean minorBookingExists(int offeringId, int minorId) {
        String query = "SELECT COUNT(*) FROM \"MinorBooking\" WHERE offering_id = ? AND minor_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, offeringId);
            pstmt.setInt(2, minorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Booking already exists
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getMinorFullName(int minorId) {
        String query = "SELECT full_name FROM \"Minor\" WHERE id = ?";
        String fullName = null;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, minorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fullName = rs.getString("full_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fullName;
    }

    public static ArrayList<MinorBooking> getMinorBookingsForGuardian(int minorId) {
        ArrayList<MinorBooking> minorBookings = new ArrayList<>();
        String query = "SELECT * FROM \"MinorBooking\" WHERE minor_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, minorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int bookingId = rs.getInt("id");
                    int offeringId = rs.getInt("offering_id");

                    MinorBooking booking = new MinorBooking(offeringId, minorId);
                    booking.setId(bookingId);
                    minorBookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return minorBookings;
    }

    public static boolean cancelMinorBooking(int bookingId, int offeringId) {
        String deleteQuery = "DELETE FROM \"MinorBooking\" WHERE id = ?";
        String updateQuery = "UPDATE \"Offering\" SET num_students = num_students - 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Update the num_students count in the Offering table
            updateStmt.setInt(1, offeringId);
            updateStmt.executeUpdate();

            // Delete the booking from the MinorBooking table
            deleteStmt.setInt(1, bookingId);
            int rowsAffected = deleteStmt.executeUpdate();

            // Check if the deletion was successful
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Minor> getMinorsObjectForGuardian(int guardianId) {
        ArrayList<Minor> minors = new ArrayList<>();
        String query = "SELECT id, full_name FROM \"Minor\" WHERE guardian_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, guardianId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                minors.add(new Minor(id, fullName, guardianId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return minors;
    }

    public static boolean addMinor(String fullName, int guardianId) {
        String query = "INSERT INTO \"Minor\" (full_name, guardian_id) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, fullName);
            pstmt.setInt(2, guardianId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the insert was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        connect();
    }

}
