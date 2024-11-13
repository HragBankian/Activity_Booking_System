package OOP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static DB.DatabaseConnection.connect;

public class Offering {
    private int id;
    private String title;
    private String organization;
    private String city;
    private String time;
    private int capacity;
    private int numStudents;
    private Integer instructorId; // Using Integer because instructorId can be null
    private String location;

    // Constructor
    public Offering(int id, String title, String organization, String city, String time, int capacity, String location) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.city = city;
        this.time = time;
        this.capacity = capacity;
        this.numStudents = 0;
        this.instructorId = null;
        this.location = location;
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
                    String location = rs.getString("location");

                    offering = new Offering(offeringId, title, organization, city, time, capacity, location);
                    offering.setNumStudents(numStudents);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offering;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getNumStudents() { return numStudents; }
    public void setNumStudents(int numStudents) { this.numStudents = numStudents; }
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public String getLocation() { return location; };
}
