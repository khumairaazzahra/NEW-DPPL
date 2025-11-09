

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PortalUnifiedUI {
    private JFrame frame;
    private CardLayout rootLayout;
    private JPanel rootPanel;

    
    private Map<String, java.util.List<StudentRecord>> kelasData = new HashMap<>();
    private java.util.List<Course> courses = new ArrayList<>();

    private String currentRole = null; //mahasiswa / dosen
    private String currentName = "";
    private String currentId = "";

   
    private StudentPortal studentPortal;
    private DosenPortal dosenPortal;

    public PortalUnifiedUI() {
        initMockCourses(); 
        createAndShow();
    }

    private void initMockCourses() {
        
        Course c1 = new Course("TIS12035", "Matematika Diskrit");
        Course c2 = new Course("TIS12083", "Interaksi Manusia & Komputer");
        Course c3 = new Course("TIS12053", "Algoritma & Struktur Data");
        courses.add(c1); courses.add(c2); courses.add(c3);

      
        kelasData.put(c1.getCode(), new ArrayList<>(Arrays.asList(
                new StudentRecord("2407110001","Asma Elhasna"),
                new StudentRecord("2407110002","Andriansyah"),
                new StudentRecord("2407110003","Alya Kinanti")
        )));
        kelasData.put(c2.getCode(), new ArrayList<>(Arrays.asList(
                new StudentRecord("2407110004","Fitri Aura"),
                new StudentRecord("2407110005","Suci Septy")
        )));
        kelasData.put(c3.getCode(), new ArrayList<>(Arrays.asList(
                new StudentRecord("2407110006","Muhammad Nabil"),
                new StudentRecord("2407110007","Raka Nadwa")
        )));
    }

    private void createAndShow() {
        frame = new JFrame("Portal Presensi Terpadu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 760);
        frame.setLocationRelativeTo(null);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

       
        LoginPanel loginPanel = new LoginPanel(this::onLogin);
        rootPanel.add(loginPanel.getPanel(), "login");

        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    private void onLogin(String name, String id) {
        
        this.currentName = name;
        this.currentId = id;

        // nim 10 -> student, nip 18 -> dosen
        if (id != null && id.matches("\\d+")) {
            if (id.length() == 10) currentRole = "student";
            else if (id.length() == 18) currentRole = "dosen";
            else currentRole = null;
        } else {
            currentRole = null;
        }

        if ("student".equals(currentRole)) {
            
            Map<String,Object> studentData = new HashMap<>();
            studentData.put("nim", id);
            studentData.put("nama", name);
            studentPortal = new StudentPortal(this, studentData, courses);
            rootPanel.add(studentPortal.getPanel(), "student");
            rootLayout.show(rootPanel, "student");
        } 
            else if ("dosen".equals(currentRole)) { 
            dosenPortal = new DosenPortal(this, name, id, kelasData, courses);
            rootPanel.add(dosenPortal.getPanel(), "dosen");
            rootLayout.show(rootPanel, "dosen");
        } 
            else {
            JOptionPane.showMessageDialog(frame, "Format ID tidak dikenali. Gunakan NIM (10 digit) atau NIP (18 digit).");
        }
    }

    public void backToLogin() {
        
        if (studentPortal != null) rootPanel.remove(studentPortal.getPanel());
        if (dosenPortal != null) rootPanel.remove(dosenPortal.getPanel());
        studentPortal = null;
        dosenPortal = null;
        currentRole = null;
        currentId = "";
        currentName = "";
        rootLayout.show(rootPanel, "login");
    }

    
    public void pushRiwayatToDosen(String kodeMataKuliah) {
        if (dosenPortal != null) dosenPortal.addRiwayat(kodeMataKuliah);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PortalUnifiedUI::new);
    }
}
