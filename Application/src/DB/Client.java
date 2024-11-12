package DB;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static DB.DatabaseConnection.connect;

public class Client extends User {

    public Client(int id, String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(id, fullName, email, password, phoneNumber, dateOfBirth);
    }

    public Client(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
    }

    @Override
    public String getUserType() {
        return "Client";
    }

    public ArrayList<Offering> getAvailableOfferings() {
        ArrayList<Offering> availableOfferings = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\" WHERE instructor_id IS NOT NULL AND num_students < capacity " +
                "AND id NOT IN (SELECT offering_id FROM \"ClientBooking\" WHERE client_id = ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.id);
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

    public boolean bookOffering(int offeringId) {
        String checkClientBookingQuery = "SELECT o.time FROM \"ClientBooking\" cb " +
                "JOIN \"Offering\" o ON cb.offering_id = o.id " +
                "WHERE cb.client_id = ? AND o.time = (SELECT time FROM \"Offering\" WHERE id = ?)";
        String clientBookingQuery = "INSERT INTO \"ClientBooking\" (offering_id, client_id) VALUES (?, ?)";
        String updateOfferingQuery = "UPDATE \"Offering\" SET num_students = num_students + 1 WHERE id = ? AND num_students < capacity";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);  // Start transaction

            // Step 1: Check if the client already has a booking at the same time
            try (PreparedStatement checkStmt = conn.prepareStatement(checkClientBookingQuery)) {
                checkStmt.setInt(1, this.id);
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
                pstmt.setInt(2,  this.id);
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

    public ArrayList<ClientBooking> getBookings() {
        ArrayList<ClientBooking> clientBookings = new ArrayList<>();
        String clientBookingQuery = "SELECT cb.id, cb.offering_id FROM \"ClientBooking\" cb WHERE cb.client_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(clientBookingQuery)) {
            pstmt.setInt(1, this.id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int offeringId = rs.getInt("offering_id");

                    // Create a new ClientBooking object using the correct constructor
                    ClientBooking booking = new ClientBooking(offeringId, this.id);
                    booking.setId(id);  // Set the id for the ClientBooking

                    clientBookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientBookings;
    }

    public boolean cancelBooking(int bookingId) {
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

}
