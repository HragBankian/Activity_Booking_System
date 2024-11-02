package DB;

public class Guardian extends User {

    public Guardian(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
    }

    @Override
    public String getUserType() {
        return "Guardian";
    }
}
