package DB;

//import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static DB.DatabaseConnection.connect;

public abstract class User {
    protected int id;
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected String dateOfBirth;

    public User(int id, String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public User(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public static User validateLogin(String userType, String email, String password) {
        String query = String.format("SELECT * FROM \"%s\" WHERE email = ? AND password = ?", userType);

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String phoneNumber = rs.getString("phone_number");
                String dateOfBirth = rs.getString("date_of_birth");

                switch (userType) {
                    case "Client":
                        return new Client(id, fullName, email, password, phoneNumber, dateOfBirth);
                    case "Guardian":
                        return new Guardian(id, fullName, email, password, phoneNumber, dateOfBirth);
                    case "Instructor":
                        String specialty = rs.getString("specialty"); // Retrieve the specialty attribute
                        return new Instructor(id, fullName, email, password, phoneNumber, dateOfBirth, specialty);
                    default:
                        throw new IllegalArgumentException("Unknown user type: " + userType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public abstract String getUserType();

}
