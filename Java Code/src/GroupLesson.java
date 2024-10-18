import java.util.ArrayList;

public class GroupLesson extends Lesson {
	
	private int maxStudents;
	private int numStudents;
	private ArrayList<Client> clients;
	
	public boolean isFull() {
		if (numStudents == maxStudents) {
			return true;
		}
		else
			return false;
	}
	
	public void addClient(Client client) {
		if(!isFull()) {
			clients.add(client);
			numStudents++;
		} 
		else
			System.out.println("Max number of students reached");
	}
	
	public void removeClient(Client client) {
		if(clients.remove(client)) {
			numStudents--;
		}
	}
	
	public int getMaxStudents() {
		return maxStudents;
	}
	
	public int getNumStudents() {
		return numStudents;
	}
	
}
