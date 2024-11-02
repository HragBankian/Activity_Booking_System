package DB;

//import org.mindrot.jbcrypt.BCrypt;

public abstract class User {
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected String dateOfBirth;

    public User(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDateOfBirth() { return dateOfBirth; }

    public abstract String getUserType();

}
