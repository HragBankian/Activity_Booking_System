package DB;

public class Instructor extends User {
    private String specialty;

    public Instructor(String fullName, String email, String password, String phoneNumber, String dateOfBirth, String specialty) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String getUserType() {
        return "Instructor";
    }
}
