package DB;

public class Offering {
    private int id;
    private String title;
    private String organization;
    private String city;
    private String time;
    private int capacity;
    private int numStudents;
    private Integer instructorId; // Using Integer because instructorId can be null

    // Constructor
    public Offering(String title, String organization, String city, String time, int capacity) {
        this.title = title;
        this.organization = organization;
        this.city = city;
        this.time = time;
        this.capacity = capacity;
        this.numStudents = 0;
        this.instructorId = null;
    }

    // Getter and Setter for id (set by the database later)
    public int getId() {
        return id;
    }

    // Other getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }
}
