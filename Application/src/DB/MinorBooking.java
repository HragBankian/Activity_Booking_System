package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static DB.DatabaseConnection.connect;

public class MinorBooking {
    private int id;
    private int offeringId;
    private int minorId;

    // Constructor
    public MinorBooking(int offeringId, int minorId) {
        this.offeringId = offeringId;
        this.minorId = minorId;
    }

    //Check if the offering-minor pair already exists
    public static boolean minorBookingExists(int offeringId, int minorId) {
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

    // Getter and Setter methods
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOfferingId() {
        return offeringId;
    }
    public void setOfferingId(int offeringId) {
        this.offeringId = offeringId;
    }
    public int getMinorId() {
        return minorId;
    }
    public void setMinorId(int minorId) {
        this.minorId = minorId;
    }

}
