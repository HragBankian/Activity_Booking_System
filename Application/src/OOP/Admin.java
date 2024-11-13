package OOP;

import OOP.Users.Client;
import OOP.Users.Guardian;
import OOP.Users.Instructor;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

import static DB.DatabaseConnection.connect;

public class Admin {
    private String email;
    private String password;
    private static Admin instance = new Admin();

    //Constructor
    private Admin() {}

    //Get Admin instance
    public static Admin getInstance() { return instance; };

    //Set the email and password
    public void initialize(String email, String password) {
        if (this.email == null && this.password == null) { // Initialize only once
            this.email = email;
            this.password = password;
        } else {
            throw new IllegalStateException("Admin instance is already initialized.");
        }
    }

    //Create Offering
    public static boolean createOffering(Offering offering) {
        String checkQuery = "SELECT COUNT(*) FROM \"Offering\" WHERE city = ?::city_enum AND time = ? AND location = ?";
        String insertQuery = "INSERT INTO \"Offering\" (title, organization, city, time, capacity, num_students, instructor_id, location) VALUES (?::specialty_enum, ?, ?::city_enum, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if an offering with the same city, time, and location already exists
            checkStmt.setString(1, offering.getCity());
            checkStmt.setString(2, offering.getTime());
            checkStmt.setString(3, offering.getLocation());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "An offering with the same Day/Time and City/Location already exists.", "Error", JOptionPane.ERROR_MESSAGE);
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
            insertStmt.setString(8, offering.getLocation());

            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Read Offerings
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
                String location = rs.getString("location");

                Offering offering = new Offering(id, title, organization, city, time, capacity, location);
                offering.setNumStudents(numStudents);
                offering.setInstructorId(instructorId);
                offerings.add(offering);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offerings;
    }

    //Update Offerings
    public static boolean updateOffering(int offeringId, Offering offering) {
        // Check if there's another offering with the same city, time, and location, excluding the current offering
        String checkQuery = "SELECT COUNT(*) FROM \"Offering\" WHERE city = ?::city_enum AND time = ? AND location = ? AND id <> ?";
        // Update query
        String updateQuery = "UPDATE \"Offering\" SET title = ?::specialty_enum, organization = ?, city = ?::city_enum, time = ?, capacity = ?, instructor_id = ?, location = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Trim whitespace from city, time, and location before passing them to the database
            String city = offering.getCity().trim();
            String time = offering.getTime().trim();
            String location = offering.getLocation().trim();

            // Check if an offering with the same city, time, and location already exists, excluding the current offering
            checkStmt.setString(1, city);
            checkStmt.setString(2, time);
            checkStmt.setString(3, location);
            checkStmt.setInt(4, offeringId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "An offering with the same Day/Time and City/Location already exists.", "Error", JOptionPane.ERROR_MESSAGE);
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

            updateStmt.setString(7, location);

            // Set the offeringId for the WHERE clause
            updateStmt.setInt(8, offeringId);

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

    //Delete Offering
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

    //Read Client bookings
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

    //Delete Client booking
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

    //Read Minor bookings
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

    //Delete Minor booking
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

    //Read Clients
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

    //Delete Client
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

    //Read Guardians
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

    //Delete Guardian
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

    //Read Minors
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

    //Delete Minors
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

    //Read Instructors
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

    //Delete Instructor
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
}
