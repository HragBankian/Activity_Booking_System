package DB;

import java.sql.*;
import java.util.ArrayList;

import static DB.DatabaseConnection.connect;

public class Instructor extends User {
    private String specialty;

    public Instructor(int id, String fullName, String email, String password, String phoneNumber, String dateOfBirth, String specialty) {
        super(id, fullName, email, password, phoneNumber, dateOfBirth);
        this.specialty = specialty;
    }

    public Instructor(String fullName, String email, String password, String phoneNumber, String dateOfBirth, String specialty) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
        this.specialty = specialty;
    }

    @Override
    public String getUserType() {
        return "Instructor";
    }

    public ArrayList<Object[]> getAvailableOfferings() {
        ArrayList<Object[]> offerings = new ArrayList<>();

        ArrayList<String> instructorCities = this.getInstructorCities();
        String instructorSpecialty = this.getSpecialty();

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

    public boolean selectOffering(int offeringId) {
        String query = "UPDATE \"Offering\" SET instructor_id = ? WHERE id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.id);
            pstmt.setInt(2, offeringId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;  // Return true if the offering was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    public ArrayList<Object[]> getLessons() {
        ArrayList<Object[]> lessons = new ArrayList<>();
        String query = "SELECT * FROM \"Offering\" WHERE instructor_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.id);

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

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    private ArrayList<String> getInstructorCities() {
        ArrayList<String> cities = new ArrayList<>();
        String query = "SELECT name FROM \"City\" WHERE instructor_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cities.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }
}
