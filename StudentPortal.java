import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StudentPortal {
    private JPanel mainPanel;
    private Map<String,Object> studentData;
    private PortalUnifiedUI parent;
    private List<Course> courses;

    public StudentPortal(PortalUnifiedUI parent, Map<String,Object> dataMahasiswa, List<Course> courses) {
        this.parent = parent;
        this.studentData = dataMahasiswa;
        this.courses = courses;
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTop(), BorderLayout.NORTH);
        mainPanel.add(createMain(), BorderLayout.CENTER);
    }

    private JPanel createTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(101, 153, 204)); // biru header
        p.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logo = new JLabel("Portal Mahasiswa");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel prof = new JLabel(studentData.getOrDefault("nama", "Mahasiswa") + "  |  " +
                studentData.getOrDefault("nim", ""));
        prof.setForeground(Color.WHITE);
        prof.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton logout = new JButton("Logout");
        logout.setFocusPainted(false);
        logout.setBackground(Color.WHITE);
        logout.setForeground(new Color(101, 153, 204));
        logout.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        logout.addActionListener(e -> parent.backToLogin());

        right.add(prof);
        right.add(logout);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel createMain() {
        JPanel container = new JPanel(new BorderLayout());

        
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 700));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        String[] menuItems = {"Dashboard", "Informasi Perkuliahan", "Riwayat Perkuliahan", "Data Kehadiran"};
        JButton[] buttons = new JButton[menuItems.length];

        for (int i = 0; i < menuItems.length; i++) {
            buttons[i] = new JButton(menuItems[i]);
            buttons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            buttons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(new Color(245, 247, 250));
            buttons[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            buttons[i].setHorizontalAlignment(SwingConstants.LEFT);
            buttons[i].setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            sidebar.add(buttons[i]);
            sidebar.add(Box.createVerticalStrut(10));
        }

        container.add(sidebar, BorderLayout.WEST);

       
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel dash = buildStudentDashboard();
        JPanel info = buildStudentInfoPerkuliahan();
        JPanel riwayat = buildStudentRiwayat();
        JPanel data = buildStudentDataKehadiran();

        content.add(dash, "dash");
        content.add(info, "info");
        content.add(riwayat, "riwayat");
        content.add(data, "data");

        buttons[0].addActionListener(e -> ((CardLayout)content.getLayout()).show(content,"dash"));
        buttons[1].addActionListener(e -> ((CardLayout)content.getLayout()).show(content,"info"));
        buttons[2].addActionListener(e -> ((CardLayout)content.getLayout()).show(content,"riwayat"));
        buttons[3].addActionListener(e -> ((CardLayout)content.getLayout()).show(content,"data"));

        container.add(content, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildStudentDashboard() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(new EmptyBorder(20, 20, 20, 20));

    // ====== Bagian Data Diri ======
    JPanel biodataPanel = new JPanel();
    biodataPanel.setLayout(new GridLayout(1, 2, 12, 12));
    biodataPanel.setBorder(BorderFactory.createTitledBorder("Data Diri Mahasiswa"));
    biodataPanel.setBackground(Color.WHITE);

    JPanel left = new JPanel(new GridLayout(4, 1, 6, 6));
    left.setOpaque(false);
    left.add(new JLabel("Nama: " + studentData.getOrDefault("nama", "-")));
    left.add(new JLabel("NIM: " + studentData.getOrDefault("nim", "-")));
    left.add(new JLabel("Email: " +
            studentData.getOrDefault("nama", "").toString().toLowerCase().replace(" ", ".") + "@student.univ"));
    left.add(new JLabel("No Telp: -"));

    JPanel right = new JPanel(new GridLayout(4, 1, 6, 6));
    right.setOpaque(false);
    right.add(new JLabel("IPK Kumulatif: 3.99"));
    right.add(new JLabel("IP Semester Saat Ini: 0.00"));
    right.add(new JLabel("IP Semester 1: 3.99"));
    right.add(new JLabel("IP Semester 2: 4.00"));

    biodataPanel.add(left);
    biodataPanel.add(right);

    p.add(biodataPanel);
    p.add(Box.createVerticalStrut(20));
   
    JLabel mkLabel = new JLabel("Mata Kuliah Terdaftar");
    mkLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    p.add(mkLabel);
    p.add(Box.createVerticalStrut(10));

    JPanel matkulPanel = new JPanel();
    matkulPanel.setLayout(new BoxLayout(matkulPanel, BoxLayout.Y_AXIS));
    matkulPanel.setBackground(Color.WHITE);

    for (Course c : courses) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBackground(Color.WHITE);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel("<html><b>" + c.getName() + "</b></html>"));
        info.add(new JLabel("Kode: " + c.getCode() + " | Ruang: C-323 | Jam: 08:00-09:40"));
        info.add(new JLabel("Dosen pengampu: -"));

        JButton pres = new JButton("Isi Presensi ▼");
        pres.addActionListener(e -> {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem hadir = new JMenuItem("Hadir");
            JMenuItem sakit = new JMenuItem("Sakit");
            JMenuItem izin = new JMenuItem("Izin");
            popup.add(hadir);
            popup.add(sakit);
            popup.add(izin);

            hadir.addActionListener(ae -> JOptionPane.showMessageDialog(mainPanel, "Presensi Hadir disimpan (mock)."));
            sakit.addActionListener(ae -> showIzinSakitForm("Sakit", c));
            izin.addActionListener(ae -> showIzinSakitForm("Izin", c));
            popup.show(pres, 0, pres.getHeight());
        });

        card.add(info, BorderLayout.CENTER);
        card.add(pres, BorderLayout.EAST);
        matkulPanel.add(card);
        matkulPanel.add(Box.createVerticalStrut(8));
    }

    JScrollPane sp = new JScrollPane(matkulPanel);
    sp.setBorder(null);
    sp.setPreferredSize(new Dimension(900, 250));
    p.add(sp);

    return p;
}

    private JPanel buildStudentInfoPerkuliahan() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245, 247, 250));
        p.setBorder(new EmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Perkuliahan Hari Ini");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        p.add(title);
        p.add(Box.createVerticalStrut(15));

        for (Course c : courses) {
            JPanel card = new JPanel(new BorderLayout(10,10));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230,230,230)),
                    new EmptyBorder(12,12,12,12)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(new JLabel(c.getName() + " - " + c.getCode()));
            info.add(new JLabel("08:00 - 09:40 | Ruang: C-318"));
            info.add(new JLabel("Dosen: -"));

            JButton pres = new JButton("Isi Presensi");
            pres.setBackground(new Color(101, 153, 204));
            pres.setForeground(Color.WHITE);
            pres.setFocusPainted(false);
            pres.setFont(new Font("SansSerif", Font.BOLD, 13));
            pres.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));

            pres.addActionListener(e -> {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem hadir = new JMenuItem("Hadir");
                JMenuItem sakit = new JMenuItem("Sakit");
                JMenuItem izin = new JMenuItem("Izin");
                popup.add(hadir); popup.add(sakit); popup.add(izin);

                hadir.addActionListener(ae -> JOptionPane.showMessageDialog(mainPanel, "Presensi Hadir disimpan (mock)."));
                sakit.addActionListener(ae -> showIzinSakitForm("Sakit", c));
                izin.addActionListener(ae -> showIzinSakitForm("Izin", c));
                popup.show(pres, 0, pres.getHeight());
            });

            card.add(info, BorderLayout.CENTER);
            card.add(pres, BorderLayout.EAST);
            p.add(card);
            p.add(Box.createVerticalStrut(10));
        }

        return p;
    }

    private void showIzinSakitForm(String tipe, Course c) {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(mainPanel), tipe + " - " + c.getCode(), true);
        dlg.setSize(520,360);
        dlg.setLocationRelativeTo(mainPanel);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16,16,16,16));
        content.add(new JLabel("Formulir " + tipe + " untuk " + c.getName()));
        JTextArea alasan = new JTextArea(5, 40);
        JScrollPane sp = new JScrollPane(alasan);
        sp.setPreferredSize(new Dimension(480,120));
        content.add(sp);
        JButton upload = new JButton("Upload Dokumen");
        JLabel fileLabel = new JLabel("Belum ada file");
        upload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int r = chooser.showOpenDialog(dlg);
            if (r == JFileChooser.APPROVE_OPTION) fileLabel.setText(chooser.getSelectedFile().getName());
        });
        content.add(upload);
        content.add(fileLabel);
        JButton kirim = new JButton("Kirim");
        kirim.addActionListener(e -> {
            if (alasan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Isi alasan dahulu.");
                return;
            }
            JOptionPane.showMessageDialog(dlg, "Form " + tipe + " terkirim (mock).");
            dlg.dispose();
        });
        content.add(Box.createVerticalStrut(8)); content.add(kirim);
        dlg.add(content);
        dlg.setVisible(true);
    }

    private JPanel buildStudentRiwayat() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        p.add(new JLabel("Riwayat Perkuliahan"), BorderLayout.NORTH);
        String[] cols = {"No","Mata Kuliah","Tanggal","Status","Catatan"};
        Object[][] data = {
                {"1","Matematika Diskrit","10-05-2025","Hadir","-"},
                {"2","IMK","11-05-2025","Izin","Acara keluarga"}
        };
        JTable tbl = new JTable(data, cols);
        tbl.setRowHeight(24);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStudentDataKehadiran() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        p.add(new JLabel("Data Kehadiran"), BorderLayout.NORTH);
        JPanel content = new JPanel(new GridLayout(1,2,12,12));
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JLabel("Kehadiran IMK - 75%"));
        left.add(new DonutChart(75));
        left.add(Box.createVerticalStrut(8));
        left.add(new ProgressBarLine("Hadir",75));
        left.add(new ProgressBarLine("Izin/Sakit",15));
        left.add(new ProgressBarLine("Alfa",10));
        content.add(left);
        content.add(new CalendarPanel(2025,2));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    public JPanel getPanel() { return mainPanel; }
}
