package OOP;

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

}
