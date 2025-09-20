import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {
    private static final Random rand = new Random();

    public static void main(String[] args) throws Exception {
        Path dir = Path.of("data");
        Files.createDirectories(dir);

        // Students
        String[] prefixes = { "Mr.", "Ms.", "Miss" };
        String[] schools = { "Sci-High", "Math-High", "Tech-High", "Bio-High" };
        try (BufferedWriter bw = Files.newBufferedWriter(dir.resolve("students.csv"), StandardCharsets.UTF_8)) {
            bw.write("studentId,prefix,firstName,lastName,birthDate,school,email\n");
            for (int i = 1; i <= 15; i++) {
                String id = String.format("69%06d", i);
                String prefix = prefixes[rand.nextInt(prefixes.length)];
                String first = "Stu" + i;
                String last = "Last" + i;
                int year = 2005 + rand.nextInt(6); // ≈ 15–20
                int m = 1 + rand.nextInt(12), d = 1 + rand.nextInt(28);
                String school = schools[rand.nextInt(schools.length)];
                String email = "stu" + i + "@example.com";
                bw.write(String.join(",", id, prefix, first, last,
                        LocalDate.of(year, m, d).toString(), school, email));
                bw.newLine();
            }
        }

        // Subjects
        try (BufferedWriter bw = Files.newBufferedWriter(dir.resolve("subjects.csv"), StandardCharsets.UTF_8)) {
            bw.write("subjectId,name,credits,prerequisiteSubjectId,maxSeats,currentEnrolled\n");
            bw.write("05500001,Intro to CS,3,,40,0\n");
            bw.write("05500002,Programming I,3,,40,0\n");
            bw.write("05500003,Programming II,3,0550002,35,0\n");
            bw.write("05500004,Data Structures,3,0550003,30,0\n");
            bw.write("05500005,Algorithms,3,0550004,30,0\n");
            bw.write("05500006,Databases,3,0550002,30,0\n");
            bw.write("05500007,Operating Systems,3,0550003,25,0\n");
            bw.write("05500008,Computer Networks,3,0550003,25,0\n");
            bw.write("90690001,Critical Thinking,2,,10,0\n");
            bw.write("90690002,Communication,2,,5,0\n");
            bw.write("90690003,Art,2,,5,0\n");
        }

        // Enrollments
        try (BufferedWriter bw = Files.newBufferedWriter(dir.resolve("enrollments.csv"), StandardCharsets.UTF_8)) {
            bw.write("studentId,subjectId,status,grade\n");
            bw.write("69000002,05500002,COMPLETED,A\n");
            bw.write("69000005,05500002,COMPLETED,B\n");
            bw.write("69000003,05500001,ENROLLED,\n");
            bw.write("69000004,90690001,ENROLLED,\n");
        }

        System.out.println("Random CSV generated at ./data/");
    }
}