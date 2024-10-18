import java.util.Date;

public abstract class User {

	    private String fullName;
	    private String email;
	    private String password;
	    private String phoneNumber;
	    private Date dateOfBirth;

	    public User(String fullName, String email, String password, String phoneNumber, Date dateOfBirth) {
	        this.fullName = fullName;
	        this.email = email;
	        this.password = password;
	        this.phoneNumber = phoneNumber;
	        this.dateOfBirth = dateOfBirth;
	    }

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public Date getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
	
}
