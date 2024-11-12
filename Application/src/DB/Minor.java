package DB;

public class Minor {
    private int id;
    private String fullName;
    private int guardianId;

    // Constructor
    public Minor(int id, String fullName, int guardianId) {
        this.id = id;
        this.fullName = fullName;
        this.guardianId = guardianId;
    }

    public Minor(String fullName, int guardianId) {
        this.fullName = fullName;
        this.guardianId = guardianId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public int getGuardianId() {
        return guardianId;
    }
    public void setGuardianId(int guardianId) {
        this.guardianId = guardianId;
    }
}

