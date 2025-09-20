package Model;

import java.time.LocalDate;
import java.time.Period;

public class Student {
    private final String studentId;
    private final String prefix;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final String school;
    private final String email;

    public Student(String studentId, String prefix, String firstName, String lastName,
            LocalDate birthDate, String school, String email) {
        if (!studentId.matches("^69\\d{6}$")) {
            throw new IllegalArgumentException("studentId must be 8 digits and start with 69");
        }
        this.studentId = studentId;
        this.prefix = prefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.school = school;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getSchool() {
        return school;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return (prefix == null || prefix.isBlank() ? "" : (prefix + " ")) + firstName + " " + lastName;
    }

    public int getAgeYears(LocalDate onDate) {
        return Period.between(birthDate, onDate).getYears();
    }
}