package DB; // Adjust the package as needed

public class Offering {
    private String title;
    private String organization;
    private String city;
    private String time;
    private int capacity;
    private int numStudents;
    private Integer instructorId; //using Integer wrapper class because instructorId can be null

    public Offering(String title, String organization, String city, String time, int capacity) {
        this.title = title;
        this.organization = organization;
        this.city = city;
        this.time = time;
        this.capacity = capacity;
        this.numStudents = 0;
        this.instructorId = null;
    }

    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public int getInstructorId() { return instructorId; }
    public String getCity() { return city; }
    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
}
