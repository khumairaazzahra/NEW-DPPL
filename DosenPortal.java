import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DosenPortal {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private PortalUnifiedUI parent;
    private String dosenName;
    private String dosenNip;
    private Map<String, List<StudentRecord>> kelasData;
    private List<Course> courses;
    private Map<String, List<String>> riwayatMap = new HashMap<>();

    public DosenPortal(PortalUnifiedUI parent, String dosenName, String dosenNip, Map<String, List<StudentRecord>> kelasData, List<Course> courses) {
        this.parent = parent;
        this.dosenName = dosenName;
        this.dosenNip = dosenNip;
        this.kelasData = kelasData;
        this.courses = courses;
        for (Course c : courses) riwayatMap.put(c.getCode(), new ArrayList<>());

    
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 248, 253));

        JPanel sidebar = buildSidebar();
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(buildDashboard(), BorderLayout.CENTER);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // --- (Metode buildSidebar, setContent, createMenuButton TIDAK DIUBAH) ---

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(89, 133, 184)); // biru lebih dalam
        sidebar.setPreferredSize(new Dimension(220, 600));

        JLabel title = new JLabel("<html><font color='white'><b>Portal Dosen</b></font></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        sidebar.add(title);

        JButton btnDashboard = createMenuButton("Dashboard");
        JButton btnMataKuliah = createMenuButton("Mata Kuliah");
        JButton btnRiwayat = createMenuButton("Riwayat Presensi");
        JButton btnData = createMenuButton("Data Kehadiran");
        JButton btnLogout = createMenuButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(btnMataKuliah);
        sidebar.add(btnRiwayat);
        sidebar.add(btnData);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

    
        btnDashboard.addActionListener(e -> setContent(buildDashboard()));
        btnMataKuliah.addActionListener(e -> setContent(createCoursesPanel()));
        btnRiwayat.addActionListener(e -> setContent(createRiwayatPanel()));
        btnData.addActionListener(e -> setContent(createDataKehadiranPanel()));
        btnLogout.addActionListener(e -> parent.backToLogin());

        return sidebar;
    }

    private void setContent(JComponent panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(89, 133, 184));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(68, 107, 155));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(89, 133, 184));
            }
        });
        return btn;
    }

    
    /**
     * INI ADALAH METODE YANG DIUBAH
     * (Outline foto dihapus, nama digeser ke kiri)
     */
    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 253));

        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(117, 172, 224));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel leftLabel = new JLabel("<html><font color='white' size='5'><b>Dashboard Dosen</b></font></html>");
        JLabel rightLabel = new JLabel("<html><font color='white'><b>" + dosenName + "</b><br>" + dosenNip + "</font></html>", SwingConstants.RIGHT);
        topBar.add(leftLabel, BorderLayout.WEST);
        topBar.add(rightLabel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(245, 248, 253));
        body.setBorder(new EmptyBorder(25, 40, 25, 40));

        // --- PERUBAHAN DI DALAM BAGIAN INI ---
        
        JPanel biodataCard = new JPanel();
        biodataCard.setLayout(new GridLayout(1, 4, 20, 0)); 
        biodataCard.setBackground(Color.WHITE);
        biodataCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // 2. Membuat panel foto (left)
        ImageIcon dosenIcon = null;
        try {
            ImageIcon tempIcon = new ImageIcon(getClass().getResource("dosen_foto.png"));
            Image image = tempIcon.getImage();
            Image resizedImage = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            dosenIcon = new ImageIcon(resizedImage);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar dosen_foto.png");
            e.printStackTrace();
        }

        JPanel left = new JPanel(); 
        left.setOpaque(false);
        
        JLabel foto = new JLabel();
        foto.setIcon(dosenIcon);
        foto.setPreferredSize(new Dimension(80, 80));
        // foto.setBorder(BorderFactory.createLineBorder(new Color(180, 190, 200), 2)); // <-- PERUBAHAN 1: Outline dihapus
        left.add(foto); 

        // 3. Membuat panel info (Nama, NIP)
        JPanel info = new JPanel(); 
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(new JLabel("<html><b>" + dosenName + "</b></html>"));
        info.add(new JLabel("NIP: " + dosenNip));
        // Beri padding (Top, Left, Bottom, Right)
        info.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10)); // <-- PERUBAHAN 2: Padding kiri jadi 0

        // 4. Membuat panel email dan telepon
        JPanel emailPanel = buildContactInfo("Email", dosenName.toLowerCase().replace(" ", ".") + "@dosen.univ.ac.id", "📧");
        JPanel phonePanel = buildContactInfo("Nomor Telepon", "0812XXXXXXX", "📞");
        
        // 5. Menambahkan 4 komponen ke biodataCard
        biodataCard.add(left);
        biodataCard.add(info);
        biodataCard.add(emailPanel);
        biodataCard.add(phonePanel);

        // 6. Atur Max Height
        biodataCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); 

        // --- PERUBAHAN SELESAI ---

        body.add(biodataCard);
        body.add(Box.createVerticalStrut(25));

        // === MATA KULIAH (Tidak Diubah) ===
        JLabel mkLabel = new JLabel("Mata Kuliah Diampu");
        mkLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        body.add(mkLabel);
        body.add(Box.createVerticalStrut(10));

        JPanel mkPanel = new JPanel();
        mkPanel.setOpaque(false);
        mkPanel.setLayout(new BoxLayout(mkPanel, BoxLayout.Y_AXIS));

        for (Course c : courses) {
            JPanel mkCard = new JPanel(new BorderLayout());
            mkCard.setBackground(Color.WHITE);
            mkCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            mkCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.add(new JLabel("<html><b>" + c.getName() + "</b> - " + c.getCode() + "</html>"));
            infoPanel.add(new JLabel("Waktu: 08:00 - 09:40 | Ruang: Gedung C-318"));
            infoPanel.add(new JLabel("Dosen: " + dosenName));

            JButton btn = new JButton("Buka Presensi");
            btn.setFocusPainted(false);
            btn.setBackground(new Color(117, 172, 224));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                List<StudentRecord> list = kelasData.get(c.getCode());
                if (list == null) list = new ArrayList<>();
                new DosenPresensiDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel), c, list, this);
            });

            mkCard.add(infoPanel, BorderLayout.CENTER);
            mkCard.add(btn, BorderLayout.EAST);
            mkPanel.add(mkCard);
            mkPanel.add(Box.createVerticalStrut(12));
        }

        body.add(mkPanel);
        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Metode ini TIDAK DIUBAH (masih sama seperti perbaikan sebelumnya)
     */
    private JPanel buildContactInfo(String title, String value, String icon) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)); 
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("SansSerif", Font.PLAIN, 20));
        ic.setPreferredSize(new Dimension(30, 30)); 
        ic.setHorizontalAlignment(SwingConstants.CENTER); 
        
        JLabel text = new JLabel("<html><b>" + title + "</b><br>" + value + "</html>");
        panel.add(ic, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    // --- (Metode createCoursesPanel, createRiwayatPanel, createDataKehadiranPanel, addRiwayat, getPanel TIDAK DIUBAH) ---
    
    private JScrollPane createCoursesPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12,12,12,12));
        for (Course c : courses) {
            JPanel row = new JPanel(new BorderLayout(8,8));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            row.setBorder(BorderFactory.createLineBorder(new Color(210,210,210)));
            row.setBackground(Color.WHITE);

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(new JLabel("<html><b>" + c.getName() + "</b></html>"));
            info.add(new JLabel("Kode: " + c.getCode() + "  |  Ruang: C-323   |  Jam: 13:00-14:40"));

            JButton openBtn = new JButton("Buka Presensi");
            openBtn.addActionListener(e -> {
                List<StudentRecord> list = kelasData.get(c.getCode());
                if (list == null) list = new ArrayList<>();
                new DosenPresensiDialog((Frame)SwingUtilities.getWindowAncestor(mainPanel), c, list, this);
            });

            row.add(info, BorderLayout.CENTER);
            row.add(openBtn, BorderLayout.EAST);
            p.add(row);
            p.add(Box.createVerticalStrut(8));
        }
        return new JScrollPane(p);
    }

    private JScrollPane createRiwayatPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        JComboBox<String> cb = new JComboBox<>();
        for (Course c : courses) cb.addItem(c.getCode() + " - " + c.getName());
        DefaultListModel<String> lm = new DefaultListModel<>();
        JList<String> list = new JList<>(lm);
        cb.addActionListener(e -> {
            lm.clear();
            String sel = (String) cb.getSelectedItem();
            if (sel == null) return;
            String code = sel.split(" - ")[0];
            List<String> hist = riwayatMap.get(code);
            if (hist != null) hist.forEach(lm::addElement);
        });
        if (cb.getItemCount()>0) cb.setSelectedIndex(0);
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Pilih Mata Kuliah:"), BorderLayout.WEST);
        top.add(cb, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return new JScrollPane(p);
    }

    private JScrollPane createDataKehadiranPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12,12,12,12));
        for (Course c : courses) {
            p.add(new JLabel("<html><h4>" + c.getName() + " (" + c.getCode() + ")</h4></html>"));
            DefaultTableModel model = new DefaultTableModel(new Object[]{"NIM","Nama","Status"},0);
            List<StudentRecord> list = kelasData.get(c.getCode());
            if (list != null) {
                for (StudentRecord s : list) model.addRow(new Object[]{s.getNim(), s.getName(), s.getStatus()});
            }
            JTable tbl = new JTable(model);
            tbl.setRowHeight(24);
            JScrollPane sp = new JScrollPane(tbl);
            sp.setPreferredSize(new Dimension(900, 160));
            p.add(sp);
            JButton save = new JButton("Simpan Perubahan (" + c.getCode() + ")");
            save.addActionListener(e -> {
                for (int r=0; r<model.getRowCount(); r++) {
                    String nim = model.getValueAt(r,0).toString();
                    String status = model.getValueAt(r,2).toString();
                    List<StudentRecord> listS = kelasData.get(c.getCode());
                    if (listS != null) {
                        for (StudentRecord sr : listS) {
                            if (sr.getNim().equals(nim)) { sr.setStatus(status); break; }
                        }
                    }
                }
                JOptionPane.showMessageDialog(mainPanel, "Data kehadiran tersimpan untuk " + c.getCode());
            });
            p.add(save);
            p.add(Box.createVerticalStrut(12));
        }
        return new JScrollPane(p);
    }

    public void addRiwayat(String kodeMataKuliah) {
        List<String> list = riwayatMap.computeIfAbsent(kodeMataKuliah, k -> new ArrayList<>());
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        list.add(now + " — Presensi dibuka/disimpan oleh " + dosenName);
    }

    public JPanel getPanel() { 
        return mainPanel; 
    }
}