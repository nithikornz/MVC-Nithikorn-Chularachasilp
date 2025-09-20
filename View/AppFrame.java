package View;

import Controller.RegistrationController;
import Model.Enrollment;
import Model.Subject;
import Model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppFrame extends JFrame {
    private final RegistrationController controller;

    private String currentStudentId = null;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_REGISTER = "register";
    private static final String PAGE_DETAIL = "detail";
    private static final String PAGE_PROFILE = "profile";

    private JTextField txtStudentId;
    private JTable tblSubjects;

    private JLabel lblId, lblName, lblInstructor, lblCredits, lblPrereq, lblMax, lblEnrolled; // add lblInstructor

    private JLabel lblProfileHeader;
    private JTable tblProfileEnrolls;

    public AppFrame(RegistrationController controller) {
        super("Pre-Enrollment");
        this.controller = controller;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        root.add(buildLoginPage(), PAGE_LOGIN);
        root.add(buildRegistrationPage(), PAGE_REGISTER);
        root.add(buildSubjectDetailPage(), PAGE_DETAIL);
        root.add(buildProfilePage(), PAGE_PROFILE);
        setContentPane(root);
    }

    // Login Page
    private JPanel buildLoginPage() { /* (เหมือนเดิมของคุณ) */
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblStudentId = new JLabel("Student ID:");
        JTextField txtLoginId = new JTextField(12);
        JButton btnLogin = new JButton("Login");

        c.gridx = 0;
        c.gridy = 0;
        p.add(lblStudentId, c);
        c.gridx = 1;
        c.gridy = 0;
        p.add(txtLoginId, c);
        c.gridx = 1;
        c.gridy = 1;
        p.add(btnLogin, c);

        btnLogin.addActionListener(e -> {
            String sid = txtLoginId.getText().trim();
            if (sid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Student ID");
                return;
            }
            if (controller.findStudent(sid).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student not found");
                return;
            }
            currentStudentId = sid;
            showRegister();
        });
        return p;
    }

    // Registration Page
    private JPanel buildRegistrationPage() {
        JPanel page = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        top.add(new JLabel("Student ID:"));
        txtStudentId = new JTextField(12);
        txtStudentId.setEditable(false);
        top.add(txtStudentId);

        JButton btnDetail = new JButton("View Detail");
        JButton btnEnroll = new JButton("Enroll Selected");
        JButton btnProfile = new JButton("Go to Profile");
        JButton btnLogout = new JButton("Logout");

        btnDetail.addActionListener(e -> openSelectedDetail());
        btnEnroll.addActionListener(e -> doEnroll());
        btnProfile.addActionListener(e -> showProfile(currentStudentId));
        btnLogout.addActionListener(e -> logout());

        top.add(btnDetail);
        top.add(btnEnroll);
        top.add(btnProfile);
        top.add(btnLogout);
        page.add(top, BorderLayout.NORTH);

        tblSubjects = new JTable(new DefaultTableModel(
                new String[] { "Subject ID", "Name", "Instructor", "Credits", "Prereq", "Max", "Enrolled" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        page.add(new JScrollPane(tblSubjects), BorderLayout.CENTER);

        return page;
    }

    private void refreshSubjects() {
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(this, "Student login required");
            return;
        }
        txtStudentId.setText(currentStudentId);

        List<Subject> list = controller.listNotYetEnrolled(currentStudentId);
        DefaultTableModel m = (DefaultTableModel) tblSubjects.getModel();
        m.setRowCount(0);
        for (Subject s : list) {
            m.addRow(new Object[] {
                    s.getSubjectId(),
                    s.getName(),
                    s.getInstructor(), 
                    s.getCredits(),
                    s.getPrerequisiteSubjectId() == null ? "-" : s.getPrerequisiteSubjectId(),
                    s.getMaxSeats() == -1 ? "∞" : s.getMaxSeats(),
                    s.getCurrentEnrolled()
            });
        }
        if (m.getRowCount() > 0)
            tblSubjects.setRowSelectionInterval(0, 0);
    }

    private void openSelectedDetail() {
        int r = tblSubjects.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject");
            return;
        }
        String subId = tblSubjects.getValueAt(r, 0).toString();
        setSubjectDetail(subId);
        cards.show(root, PAGE_DETAIL);
    }

    private void doEnroll() {
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(this, "Student login required");
            return;
        }
        int r = tblSubjects.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a subject");
            return;
        }
        String subId = tblSubjects.getValueAt(r, 0).toString();
        try {
            controller.register(currentStudentId, subId);
            JOptionPane.showMessageDialog(this, "Enroll success");
            showProfile(currentStudentId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Subject Detail Page
    private JPanel buildSubjectDetailPage() {
        JPanel page = new JPanel(new BorderLayout(8, 8));
        JLabel header = new JLabel("Subject Detail", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 15f));
        header.setBorder(new EmptyBorder(8, 0, 6, 0));
        page.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 12, 6, 12);
        c.anchor = GridBagConstraints.WEST;

        lblId = new JLabel("-");
        lblName = new JLabel("-");
        lblInstructor = new JLabel("-"); 
        lblCredits = new JLabel("-");
        lblPrereq = new JLabel("-");
        lblMax = new JLabel("-");
        lblEnrolled = new JLabel("-");

        addRow(center, c, 0, "Subject ID:", lblId);
        addRow(center, c, 1, "Name:", lblName);
        addRow(center, c, 2, "Instructor:", lblInstructor); 
        addRow(center, c, 3, "Credits:", lblCredits);
        addRow(center, c, 4, "Prerequisite:", lblPrereq);
        addRow(center, c, 5, "Max Seats:", lblMax);
        addRow(center, c, 6, "Current Enrolled:", lblEnrolled);

        page.add(center, BorderLayout.CENTER);

        JButton btnBack = new JButton("Back to Registration");
        btnBack.addActionListener(e -> cards.show(root, PAGE_REGISTER));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        bottom.add(btnBack);
        page.add(bottom, BorderLayout.SOUTH);

        return page;
    }

    private void addRow(JPanel panel, GridBagConstraints c, int y, String label, JLabel value) {
        c.gridx = 0;
        c.gridy = y;
        JLabel k = new JLabel(label);
        panel.add(k, c);
        c.gridx = 1;
        value.setFont(value.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(value, c);
    }

    private void setSubjectDetail(String subjectId) {
        Subject s = controller.findSubject(subjectId).orElse(null);
        if (s == null) {
            lblId.setText("N/A");
            lblName.setText("Not found");
            lblInstructor.setText("-");
            lblCredits.setText("-");
            lblPrereq.setText("-");
            lblMax.setText("-");
            lblEnrolled.setText("-");
            return;
        }
        lblId.setText(s.getSubjectId());
        lblName.setText(s.getName());
        lblInstructor.setText(s.getInstructor()); // NEW
        lblCredits.setText(String.valueOf(s.getCredits()));
        lblPrereq.setText(s.getPrerequisiteSubjectId() == null ? "-" : s.getPrerequisiteSubjectId());
        lblMax.setText(s.getMaxSeats() == -1 ? "∞" : String.valueOf(s.getMaxSeats()));
        lblEnrolled.setText(String.valueOf(s.getCurrentEnrolled()));
    }

    // Profile Page 
    private JPanel buildProfilePage() { 
        JPanel page = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        lblProfileHeader = new JLabel("Student Profile");
        JButton btnBack = new JButton("Back to Registration");
        JButton btnLogout = new JButton("Logout");
        btnBack.addActionListener(e -> showRegister());
        btnLogout.addActionListener(e -> logout());
        top.add(lblProfileHeader);
        top.add(btnBack);
        top.add(btnLogout);

        tblProfileEnrolls = new JTable(new DefaultTableModel(
                new String[] { "Subject ID", "Status", "Grade" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        page.add(top, BorderLayout.NORTH);
        page.add(new JScrollPane(tblProfileEnrolls), BorderLayout.CENTER);
        return page;
    }

    private void refreshProfile(String sid) {
        Student s = controller.findStudent(sid).orElse(null);
        if (s == null) {
            lblProfileHeader.setText("Student not found");
            ((DefaultTableModel) tblProfileEnrolls.getModel()).setRowCount(0);
            return;
        }
        lblProfileHeader.setText("Student Profile: " + s.getFullName() + " (" + s.getStudentId() + ")");
        DefaultTableModel m = (DefaultTableModel) tblProfileEnrolls.getModel();
        m.setRowCount(0);
        for (Enrollment e : controller.getEnrollments(sid)) {
            m.addRow(new Object[] { e.getSubjectId(), e.getStatus().name(), e.getGrade() == null ? "" : e.getGrade() });
        }
    }

    public void showLogin() {
        cards.show(root, PAGE_LOGIN);
    }

    public void showRegister() {
        if (currentStudentId == null) {
            showLogin();
            return;
        }
        txtStudentId.setText(currentStudentId);
        cards.show(root, PAGE_REGISTER);
        refreshSubjects();
    }

    public void showProfile(String sid) {
        if (currentStudentId == null || !currentStudentId.equals(sid)) {
            showLogin();
            return;
        }
        refreshProfile(sid);
        cards.show(root, PAGE_PROFILE);
    }

    private void logout() {
        currentStudentId = null;
        if (txtStudentId != null)
            txtStudentId.setText("");
        cards.show(root, PAGE_LOGIN);
    }
}
