package DB;

public class Client extends User {

    public Client(String fullName, String email, String password, String phoneNumber, String dateOfBirth) {
        super(fullName, email, password, phoneNumber, dateOfBirth);
    }

    @Override
    public String getUserType() {
        return "Client";
    }
}
