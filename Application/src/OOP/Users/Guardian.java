package OOP.Users;

import OOP.Minor;
import OOP.MinorBooking;
import OOP.Offering;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static DB.DatabaseConnection.connect;

public class Guardian extends User {

    public Guardian(int id, String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(id, fullName, email, password, phoneNumber, dateOfBirth);
    }

    public Guardian(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
    }

    @Override
    public String getUserType() { return "Guardian"; }

    public static ArrayList<Offering> getAvailableOfferings() {
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
                    String location = rs.getString("location");

                    Offering offering = new Offering(id, title, organization, city, time, capacity, location);
                    offering.setNumStudents(numStudents);  // Set numStudents using setter
                    availableOfferings.add(offering);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableOfferings;
    }

    public boolean addMinor(String minorFullName) {
        String query = "INSERT INTO \"Minor\" (full_name, guardian_id) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, minorFullName);
            pstmt.setInt(2, this.id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the insert was successful
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

    public ArrayList<Minor> getMinors() {
        ArrayList<Minor> minors = new ArrayList<>();
        String query = "SELECT id, full_name FROM \"Minor\" WHERE guardian_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                minors.add(new Minor(id, fullName, this.id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return minors;
    }

    public boolean bookOfferingForMinor(String minorFullName, int offeringId) {
        // Step 1: Find the minor_id using guardian_id and minor full_name
        int minorId = -1;
        String getMinorIdQuery = "SELECT id FROM \"Minor\" WHERE guardian_id = ? AND full_name = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(getMinorIdQuery)) {
            pstmt.setInt(1, this.id);
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

        // Step 2: Check if the minor already has a booking at the same time
        String checkMinorBookingQuery = "SELECT o.time FROM \"MinorBooking\" mb " +
                "JOIN \"Offering\" o ON mb.offering_id = o.id " +
                "WHERE mb.minor_id = ? AND o.time = (SELECT time FROM \"Offering\" WHERE id = ?)";

        try (Connection conn = connect(); PreparedStatement checkStmt = conn.prepareStatement(checkMinorBookingQuery)) {
            checkStmt.setInt(1, minorId);
            checkStmt.setInt(2, offeringId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Minor already has a booking at the same time
                JOptionPane.showMessageDialog(null, "The minor already has a booking at this time.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while checking for existing booking
        }

        // Step 3: Insert the booking into the MinorBooking table
        String insertBookingQuery = "INSERT INTO \"MinorBooking\" (offering_id, minor_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertBookingQuery)) {
            pstmt.setInt(1, offeringId);
            pstmt.setInt(2, minorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while inserting booking
        }

        // Step 4: Increment num_students in the Offering table
        String updateOfferingQuery = "UPDATE \"Offering\" SET num_students = num_students + 1 WHERE id = ? AND num_students < capacity";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(updateOfferingQuery)) {
            pstmt.setInt(1, offeringId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated == 0) {
                return false;  // No update, meaning capacity is full
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Error while updating offering
        }

        return true;  // Successfully booked the offering for the minor
    }

    public ArrayList<MinorBooking> getBookingsForMinor(int minorId) {
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

    public boolean cancelMinorBooking(int bookingId, int offeringId) {
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

}

