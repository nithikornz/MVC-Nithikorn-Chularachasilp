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

        // Students ช
        try (BufferedWriter bw = Files.newBufferedWriter(dir.resolve("students.csv"), StandardCharsets.UTF_8)) {
            bw.write("studentId,prefix,firstName,lastName,birthDate,school,email\n");
            for (int i = 1; i <= 15; i++) {
                String id = String.format("69%06d", i);
                String prefix = new String[] { "Mr.", "Ms.", "Miss" }[rand.nextInt(3)];
                String first = "Stu" + i;
                String last = "Last" + i;
                int year = 2005 + rand.nextInt(6);
                int m = 1 + rand.nextInt(12), d = 1 + rand.nextInt(28);
                String school = new String[] { "Sci-High", "Math-High", "Tech-High", "Bio-High" }[rand.nextInt(4)];
                String email = "stu" + i + "@example.com";
                bw.write(String.join(",", id, prefix, first, last,
                        LocalDate.of(year, m, d).toString(), school, email));
                bw.newLine();
            }
        }

        // Subjects 
        try (BufferedWriter bw = Files.newBufferedWriter(dir.resolve("subjects.csv"), StandardCharsets.UTF_8)) {
            bw.write("subjectId,name,instructor,credits,prerequisiteSubjectId,maxSeats,currentEnrolled\n");
            bw.write("05500001,Intro to CS,Dr. Alice,3,,40,0\n");
            bw.write("05500002,Programming I,Dr. Bob,3,,40,0\n");
            bw.write("05500003,Programming II,Dr. Carol,3,05500002,35,0\n");
            bw.write("05500004,Data Structures,Dr. Dave,3,05500003,30,0\n");
            bw.write("05500005,Algorithms,Dr. Eve,3,05500004,30,0\n");
            bw.write("05500006,Databases,Dr. Frank,3,05500002,30,0\n");
            bw.write("05500007,Operating Systems,Dr. Grace,3,05500003,25,0\n");
            bw.write("05500008,Computer Networks,Dr. Heidi,3,05500003,25,0\n");
            bw.write("90690001,Critical Thinking,Mr. Nolan,2,,10,0\n");
            bw.write("90690002,Communication,Ms. Quinn,2,,5,0\n");
            bw.write("90690003,Art,Mr. Ray,2,,5,0\n");
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