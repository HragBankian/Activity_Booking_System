package DB;

public class City {
    private int id;
    private String name;
    private int instructorId;

    public City(int id, String name, int instructorId) {
        this.id = id;
        this.name = name;
        this.instructorId = instructorId;
    }

    public City(String name, int instructorId) {
        this.name = name;
        this.instructorId = instructorId;
    }

    public String getName() { return name; }
    public int getInstructorId() { return instructorId; }
}
