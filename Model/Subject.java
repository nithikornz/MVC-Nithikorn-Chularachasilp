package Model;

public class Subject {
    private final String subjectId;
    private final String name;
    private final String instructor; 
    private final int credits;
    private final String prerequisiteSubjectId;
    private final int maxSeats;
    private int currentEnrolled;

    public Subject(String subjectId, String name, String instructor, int credits,
            String prerequisiteSubjectId, int maxSeats, int currentEnrolled) {
        if (credits <= 0)
            throw new IllegalArgumentException("credits must be > 0");
        if (!(maxSeats == -1 || maxSeats > 0))
            throw new IllegalArgumentException("maxSeats must be -1 or > 0");
        if (currentEnrolled < 0)
            throw new IllegalArgumentException("currentEnrolled must be >= 0");
        this.subjectId = subjectId;
        this.name = name;
        this.instructor = (instructor == null ? "" : instructor);
        this.credits = credits;
        this.prerequisiteSubjectId = (prerequisiteSubjectId == null || prerequisiteSubjectId.isBlank()) ? null
                : prerequisiteSubjectId;
        this.maxSeats = maxSeats;
        this.currentEnrolled = currentEnrolled;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getName() {
        return name;
    }

    public String getInstructor() {
        return instructor;
    } // <--- NEW

    public int getCredits() {
        return credits;
    }

    public String getPrerequisiteSubjectId() {
        return prerequisiteSubjectId;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getCurrentEnrolled() {
        return currentEnrolled;
    }

    public boolean isUnlimited() {
        return maxSeats == -1;
    }

    public boolean hasSeat() {
        return isUnlimited() || currentEnrolled < maxSeats;
    }

    public void addOneSeat() {
        currentEnrolled++;
    }
}
