package Model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RegistrationService {
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Subject> subjects = new HashMap<>();
    private final List<Enrollment> enrollments = new ArrayList<>();

    private final Path studentsCsv;
    private final Path subjectsCsv;
    private final Path enrollmentsCsv;

    public RegistrationService(Path dataDir) {
        this.studentsCsv = dataDir.resolve("students.csv");
        this.subjectsCsv = dataDir.resolve("subjects.csv");
        this.enrollmentsCsv = dataDir.resolve("enrollments.csv");
    }

    // Load / Save
    public void loadAll() throws IOException {
        students.clear();
        subjects.clear();
        enrollments.clear();

        if (Files.exists(studentsCsv)) {
            try (BufferedReader br = Files.newBufferedReader(studentsCsv, StandardCharsets.UTF_8)) {
                String line;
                boolean skip = true;
                while ((line = br.readLine()) != null) {
                    if (skip) {
                        skip = false;
                        continue;
                    }
                    String[] r = line.split(",", -1);
                    students.put(r[0],
                            new Student(r[0], r[1], r[2], r[3], java.time.LocalDate.parse(r[4]), r[5], r[6]));
                }
            }
        }

        // Subjects
        if (Files.exists(subjectsCsv)) {
            try (BufferedReader br = Files.newBufferedReader(subjectsCsv, StandardCharsets.UTF_8)) {
                String line;
                boolean skip = true;
                while ((line = br.readLine()) != null) {
                    if (skip) {
                        skip = false;
                        continue;
                    }
                    String[] r = line.split(",", -1);
                    subjects.put(r[0], new Subject(r[0], r[1], Integer.parseInt(r[2]), r[3].isBlank() ? null : r[3],
                            Integer.parseInt(r[4]), Integer.parseInt(r[5])));
                }
            }
        }

        // Enrollments
        if (Files.exists(enrollmentsCsv)) {
            try (BufferedReader br = Files.newBufferedReader(enrollmentsCsv, StandardCharsets.UTF_8)) {
                String line;
                boolean skip = true;
                while ((line = br.readLine()) != null) {
                    if (skip) {
                        skip = false;
                        continue;
                    }
                    String[] r = line.split(",", -1);
                    Enrollment.Status st = Enrollment.Status.valueOf(r[2]);
                    String grade = r[3].isBlank() ? null : r[3];
                    enrollments.add(new Enrollment(r[0], r[1], st, grade));
                    Subject sb = subjects.get(r[1]);
                    if (sb != null) {
                        sb.addOneSeat();   
                    }
                }
            }
        }
    }

    public void saveAll() throws IOException {
        Files.createDirectories(studentsCsv.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(studentsCsv, StandardCharsets.UTF_8)) {
            bw.write("studentId,prefix,firstName,lastName,birthDate,school,email");
            bw.newLine();
            for (Student s : students.values()) {
                bw.write(String.join(",",
                        s.getStudentId(), s.getPrefix(), s.getFirstName(), s.getLastName(),
                        s.getBirthDate().toString(), s.getSchool(), s.getEmail()));
                bw.newLine();
            }
        }

        try (BufferedWriter bw = Files.newBufferedWriter(subjectsCsv, StandardCharsets.UTF_8)) {
            bw.write("subjectId,name,credits,prerequisiteSubjectId,maxSeats,currentEnrolled");
            bw.newLine();
            for (Subject s : subjects.values()) {
                bw.write(String.join(",",
                        s.getSubjectId(), s.getName(), String.valueOf(s.getCredits()),
                        s.getPrerequisiteSubjectId() == null ? "" : s.getPrerequisiteSubjectId(),
                        String.valueOf(s.getMaxSeats()), String.valueOf(s.getCurrentEnrolled())));
                bw.newLine();
            }
        }

        try (BufferedWriter bw = Files.newBufferedWriter(enrollmentsCsv, StandardCharsets.UTF_8)) {
            bw.write("studentId,subjectId,status,grade");
            bw.newLine();
            for (Enrollment e : enrollments) {
                bw.write(String.join(",",
                        e.getStudentId(), e.getSubjectId(), e.getStatus().name(),
                        e.getGrade() == null ? "" : e.getGrade()));
                bw.newLine();
            }
        }
    }

    // Queries
    public Optional<Student> findStudent(String id) {
        return Optional.ofNullable(students.get(id));
    }

    public Optional<Subject> findSubject(String id) {
        return Optional.ofNullable(subjects.get(id));
    }

    public Collection<Subject> getAllSubjects() {
        return subjects.values();
    }

    public List<Subject> listNotYetEnrolled(String studentId) {
        Set<String> taken = enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .map(Enrollment::getSubjectId).collect(Collectors.toSet());
        return subjects.values().stream().filter(s -> !taken.contains(s.getSubjectId()))
                .sorted(Comparator.comparing(Subject::getSubjectId))
                .collect(Collectors.toList());
    }

    public List<Enrollment> listEnrollmentsOf(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    // Business rules
    public void register(String studentId, String subjectId, LocalDate today) {
        Student st = students.get(studentId);
        if (st == null)
            throw new IllegalArgumentException("Student not found");
        Subject sb = subjects.get(subjectId);
        if (sb == null)
            throw new IllegalArgumentException("Subject not found");

        // Age
        if (st.getAgeYears(today) < 15)
            throw new IllegalArgumentException("Student must be at least 15 years old");

        // Duplicate enrollment
        boolean dup = enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals(studentId) && e.getSubjectId().equals(subjectId));
        if (dup)
            throw new IllegalArgumentException("Already enrolled");

        // Prerequisite
        if (sb.getPrerequisiteSubjectId() != null) {
            boolean passed = enrollments.stream().anyMatch(e -> e.getStudentId().equals(studentId)
                    && e.getSubjectId().equals(sb.getPrerequisiteSubjectId())
                    && e.getStatus() == Enrollment.Status.COMPLETED
                    && e.isPassed());
            if (!passed)
                throw new IllegalArgumentException("Prerequisite not passed");
        }

        // Seats
        if (!sb.hasSeat()) {
            throw new IllegalArgumentException("No seats available");
        }

        sb.addOneSeat();
        enrollments.add(new Enrollment(studentId, subjectId));
    }

    public void setGrade(String studentId, String subjectId, String grade) {
        Enrollment e = enrollments.stream()
                .filter(x -> x.getStudentId().equals(studentId) && x.getSubjectId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        e.complete(grade);
    }
}
