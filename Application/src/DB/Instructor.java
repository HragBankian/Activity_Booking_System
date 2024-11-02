package DB;

public class Instructor extends User {

    public Instructor(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
    }

    @Override
    public String getUserType() {
        return "Instructor";
    }
}
