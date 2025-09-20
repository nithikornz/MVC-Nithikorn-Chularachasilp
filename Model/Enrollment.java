package Model;

public class Enrollment {
    public enum Status { ENROLLED, COMPLETED }

    private final String studentId;
    private final String subjectId;
    private Status status;
    private String grade; 

    public Enrollment(String studentId, String subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.status = Status.ENROLLED;
        this.grade = null;
    }

    public Enrollment(String studentId, String subjectId, Status status, String grade) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.status = status;
        this.grade = grade;
    }

    public String getStudentId() { return studentId; }
    public String getSubjectId() { return subjectId; }
    public Status getStatus() { return status; }
    public String getGrade() { return grade; }

    public void complete(String grade) {
        this.status = Status.COMPLETED;
        this.grade = grade;
    }

    public boolean isPassed() {
        return grade != null && !grade.equalsIgnoreCase("F");
    }
}
