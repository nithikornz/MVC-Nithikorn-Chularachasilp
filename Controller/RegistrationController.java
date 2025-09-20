package Controller;

import Model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RegistrationController {
    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    public void loadData() throws Exception {
        service.loadAll();
    }

    public void saveData() throws Exception {
        service.saveAll();
    }

    public Optional<Student> findStudent(String id) {
        return service.findStudent(id);
    }

    public Optional<Subject> findSubject(String id) {
        return service.findSubject(id);
    }

    public List<Subject> listNotYetEnrolled(String studentId) {
        return service.listNotYetEnrolled(studentId);
    }

    public Collection<Subject> getSubjects() {
        return service.getAllSubjects();
    }

    public List<Enrollment> getEnrollments(String studentId) {
        return service.listEnrollmentsOf(studentId);
    }

    public void register(String studentId, String subjectId) {
        service.register(studentId, subjectId, LocalDate.now());
    }

    public void setGrade(String studentId, String subjectId, String grade) {
        service.setGrade(studentId, subjectId, grade);
    }
}
