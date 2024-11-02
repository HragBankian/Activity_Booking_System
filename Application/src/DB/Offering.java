package DB; // Adjust the package as needed

public class Offering {
    private String title;
    private String organization;
    private String instructor;
    private String location;
    private String time;
    private int capacity;

    public Offering(String title, String organization, String instructor, String location, String time, int capacity) {
        this.title = title;
        this.organization = organization;
        this.instructor = instructor;
        this.location = location;
        this.time = time;
        this.capacity = capacity;
    }

    public String getTitle() { return title; }
    public String getOrganization() { return organization; }
    public String getInstructor() { return instructor; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
}
