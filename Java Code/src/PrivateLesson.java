
public class PrivateLesson extends Lesson {
	
	private boolean booked;
	private Client student;
	
	public boolean isFull() {
		return booked;
	}

	public Client getStudent() {
		return student;
	}

	public void setStudent(Client student) {
		if (student != null) {
			this.student = student;
			booked = true;
		} 
		else
			booked = false;
	}
	
}
