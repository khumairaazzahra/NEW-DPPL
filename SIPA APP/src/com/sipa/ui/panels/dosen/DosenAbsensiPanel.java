package com.sipa.ui.panels.dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class DosenAbsensiPanel extends JPanel {
    private SipaApp app;
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JPanel courseListContainer;
    private MataKuliah selectedMk; 
    private JPanel studentListContainer;
    private JLabel lblDetailMatkulName, lblDetailWaktu, lblDetailStatus;
    private JLabel lblTotalHadir, lblTotalIzin, lblTotalAlpha;
    private ModernButton btnToggleSesi;
    
    // Warna Status
    private final Color COL_MASUK = new Color(46, 204, 113); 
    private final Color COL_IZIN = new Color(241, 196, 15); 
    private final Color COL_ALPHA = new Color(231, 76, 60); 

    public DosenAbsensiPanel(User user, SipaApp app) {
        this.app = app;
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setOpaque(false);
        
        initCourseListView(); 
        initDetailView();     
        
        add(mainContainer, BorderLayout.CENTER);
        showCourseList(); 
    }
    
    // --- TAMPILAN 1: DAFTAR MATA KULIAH ---
    private void initCourseListView() {
        JPanel panelList = new JPanel(new BorderLayout());
        panelList.setOpaque(false);
        panelList.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("Jadwal Mengajar Anda");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(80, 80, 80));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        panelList.add(lblTitle, BorderLayout.NORTH);
        
        courseListContainer = new JPanel();
        courseListContainer.setLayout(new BoxLayout(courseListContainer, BoxLayout.Y_AXIS));
        courseListContainer.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(courseListContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panelList.add(scroll, BorderLayout.CENTER);
        mainContainer.add(panelList, "LIST_VIEW");
    }

    private void showCourseList() {
        courseListContainer.removeAll();
        java.util.List<MataKuliah> myMatkul = app.getData().getMataKuliahByDosen(currentUser.name);
        
        if (myMatkul.isEmpty()) {
             JLabel emptyLabel = new JLabel("<html><center>Tidak ada jadwal mengajar.<br>Hubungi admin jika ini kesalahan.</center></html>", SwingConstants.CENTER);
             emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
             emptyLabel.setForeground(Color.GRAY);
             emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             courseListContainer.add(Box.createVerticalStrut(50));
             courseListContainer.add(emptyLabel);
        }

        for (MataKuliah mk : myMatkul) {
            ShadowPanel card = new ShadowPanel();
            card.setLayout(new BorderLayout());
            card.setMaximumSize(new Dimension(2000, 90)); 
            card.setBorder(new EmptyBorder(15, 20, 15, 20));
            
            // Info Kiri
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            
            JLabel lblNama = new JLabel(mk.nama);
            lblNama.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblNama.setForeground(new Color(44, 62, 80));
            
            JLabel lblDetail = new JLabel("<html><font color='#7f8c8d'>" + mk.kode + " | " + mk.waktu + " | " + mk.ruangan + "</font></html>");
            lblDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            infoPanel.add(lblNama);
            infoPanel.add(lblDetail);
            
            // Tombol Kanan
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actionPanel.setOpaque(false);
            
            ModernButton btnBuka = new ModernButton(mk.sesiAktif ? "Lanjutkan Sesi" : "Buka Kelas", 
                                                    mk.sesiAktif ? SipaApp.COL_SUCCESS : SipaApp.COL_PRIMARY);
            btnBuka.setPreferredSize(new Dimension(140, 35));
            btnBuka.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnBuka.addActionListener(e -> openDetailView(mk)); 
            
            actionPanel.add(btnBuka);
            
            card.add(infoPanel, BorderLayout.CENTER);
            card.add(actionPanel, BorderLayout.EAST);
            
            courseListContainer.add(card);
            courseListContainer.add(Box.createVerticalStrut(15)); 
        }
        cardLayout.show(mainContainer, "LIST_VIEW");
        courseListContainer.revalidate();
        courseListContainer.repaint();
    }

    // --- TAMPILAN 2: DETAIL ABSENSI (Header Diperbaiki) ---
    private void initDetailView() {
        JPanel panelDetail = new JPanel(new BorderLayout(0, 15));
        panelDetail.setOpaque(false);
        panelDetail.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        // 1. Header Card (Desain Baru: Lebih Bersih)
        ShadowPanel headerCard = new ShadowPanel();
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Tombol Kembali (Kecil di Pojok Kiri Atas)
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        ModernButton btnBack = new ModernButton("← Kembali", new Color(149, 165, 166)); // Warna abu-abu soft
        btnBack.setPreferredSize(new Dimension(90, 28));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnBack.addActionListener(e -> showCourseList());
        topRow.add(btnBack);
        
        // Info Mata Kuliah (Tengah)
        JPanel centerInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        centerInfo.setOpaque(false);
        centerInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        lblDetailMatkulName = new JLabel("Nama Mata Kuliah");
        lblDetailMatkulName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDetailMatkulName.setForeground(SipaApp.COL_PRIMARY);
        
        lblDetailWaktu = new JLabel("Waktu | Ruangan | Tanggal");
        lblDetailWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetailWaktu.setForeground(Color.DARK_GRAY);
        
        centerInfo.add(lblDetailMatkulName);
        centerInfo.add(lblDetailWaktu);
        
        // Kontrol Sesi (Kanan)
        JPanel sessionCtrl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sessionCtrl.setOpaque(false);
        
        lblDetailStatus = new JLabel(" TUTUP ", SwingConstants.CENTER);
        lblDetailStatus.setOpaque(true);
        lblDetailStatus.setBackground(new Color(231, 76, 60)); // Merah default
        lblDetailStatus.setForeground(Color.WHITE);
        lblDetailStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDetailStatus.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        btnToggleSesi = new ModernButton("Buka Sesi", SipaApp.COL_SUCCESS);
        btnToggleSesi.setPreferredSize(new Dimension(130, 35));
        btnToggleSesi.addActionListener(e -> toggleSession());
        
        sessionCtrl.add(lblDetailStatus);
        sessionCtrl.add(Box.createHorizontalStrut(10));
        sessionCtrl.add(btnToggleSesi);
        
        // Susun Header
        JPanel mainHeader = new JPanel(new BorderLayout());
        mainHeader.setOpaque(false);
        mainHeader.add(centerInfo, BorderLayout.CENTER);
        mainHeader.add(sessionCtrl, BorderLayout.EAST);
        
        headerCard.add(topRow, BorderLayout.NORTH);
        headerCard.add(mainHeader, BorderLayout.CENTER);
        
        // 2. List Mahasiswa
        studentListContainer = new JPanel();
        studentListContainer.setLayout(new BoxLayout(studentListContainer, BoxLayout.Y_AXIS));
        studentListContainer.setBackground(Color.WHITE);
        
        // Header Kolom Tabel
        JPanel colHeader = new JPanel(new GridLayout(1, 4));
        colHeader.setPreferredSize(new Dimension(0, 40));
        colHeader.setBackground(new Color(248, 249, 250)); // Abu sangat muda
        colHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220,220,220)));
        
        colHeader.add(createLabel("No.", SwingConstants.CENTER));
        colHeader.add(createLabel("Nama Mahasiswa (NIM)", SwingConstants.LEFT));
        colHeader.add(createLabel("Aksi Absensi", SwingConstants.LEFT));
        colHeader.add(createLabel("Total Alpha", SwingConstants.CENTER));
        
        ShadowPanel listCard = new ShadowPanel();
        listCard.setLayout(new BorderLayout());
        listCard.add(colHeader, BorderLayout.NORTH);
        
        JScrollPane scrollList = new JScrollPane(studentListContainer);
        scrollList.setBorder(null);
        scrollList.getVerticalScrollBar().setUnitIncrement(16);
        listCard.add(scrollList, BorderLayout.CENTER);
        
        // Footer Statistik
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        footer.setOpaque(false);
        footer.setBackground(new Color(250, 250, 250));
        lblTotalHadir = new JLabel("Hadir: 0"); lblTotalHadir.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblTotalHadir.setForeground(COL_MASUK);
        lblTotalIzin = new JLabel("Izin: 0");  lblTotalIzin.setFont(new Font("Segoe UI", Font.BOLD, 13));  lblTotalIzin.setForeground(COL_IZIN);
        lblTotalAlpha = new JLabel("Alpha: 0"); lblTotalAlpha.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblTotalAlpha.setForeground(COL_ALPHA);
        
        footer.add(lblTotalHadir); footer.add(lblTotalIzin); footer.add(lblTotalAlpha);
        listCard.add(footer, BorderLayout.SOUTH);
        
        panelDetail.add(headerCard, BorderLayout.NORTH);
        panelDetail.add(listCard, BorderLayout.CENTER);
        
        mainContainer.add(panelDetail, "DETAIL_VIEW");
    }
    
    // ... (Sisa method logic openDetailView, toggleSession, refreshStudentList, dll tetap sama, 
    // hanya pastikan StudentRow menggunakan style yang rapi di bawah ini)

    private void openDetailView(MataKuliah mk) {
        this.selectedMk = mk;
        lblDetailMatkulName.setText(mk.nama);
        String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new java.util.Locale("id", "ID")));
        lblDetailWaktu.setText(mk.kode + " | " + mk.waktu + " | " + tgl);
        updateSessionStatusUI();
        refreshStudentList();
        cardLayout.show(mainContainer, "DETAIL_VIEW");
    }

    private void updateSessionStatusUI() {
        if (selectedMk.sesiAktif) {
            lblDetailStatus.setText(" SEDANG BERLANGSUNG ");
            lblDetailStatus.setBackground(COL_MASUK);
            btnToggleSesi.setText("Tutup Sesi");
            btnToggleSesi.setBackground(COL_ALPHA); 
        } else {
            lblDetailStatus.setText(" SESI DITUTUP ");
            lblDetailStatus.setBackground(Color.GRAY);
            btnToggleSesi.setText("Buka Sesi");
            btnToggleSesi.setBackground(SipaApp.COL_PRIMARY); 
        }
    }

    private void toggleSession() {
        if (!selectedMk.sesiAktif) {
            app.getData().updateSesiMatkul(selectedMk.kode, true);
            selectedMk.sesiAktif = true;
            JOptionPane.showMessageDialog(this, "Sesi dibuka. Mahasiswa dapat melakukan presensi.");
        } else {
            int c = JOptionPane.showConfirmDialog(this, "Tutup sesi dan simpan rekap kehadiran?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                saveAttendanceBatch();
                app.getData().updateSesiMatkul(selectedMk.kode, false);
                selectedMk.sesiAktif = false;
            } else return;
        }
        updateSessionStatusUI();
        refreshStudentList();
    }

    private java.util.List<StudentRow> activeRows = new ArrayList<>();

    private void refreshStudentList() {
        studentListContainer.removeAll();
        activeRows.clear();
        java.util.List<User> mhsList = app.getData().getListMahasiswa();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        boolean isEditable = selectedMk.sesiAktif;
        int no = 1;
        
        if (mhsList.isEmpty()) {
             studentListContainer.add(new JLabel("<html><br><center>Belum ada data mahasiswa.</center></html>", SwingConstants.CENTER));
        }

        for (User mhs : mhsList) {
            int alphaCount = app.getData().hitungTotalAlpha(mhs.id);
            String currentStatus = "Hadir"; // Default
            PresensiLog log = app.getData().getLogHarian(mhs.id, selectedMk.kode, today);
            
            if (log != null) currentStatus = log.status;
            else if (!isEditable) currentStatus = "-"; // Jika sesi tutup dan belum absen
            
            StudentRow row = new StudentRow(no++, mhs, alphaCount, isEditable, currentStatus);
            activeRows.add(row);
            studentListContainer.add(row);
            
            // Separator tipis
            JPanel line = new JPanel(); 
            line.setPreferredSize(new Dimension(0,1)); 
            line.setBackground(new Color(245,245,245));
            studentListContainer.add(line);
        }
        studentListContainer.revalidate();
        studentListContainer.repaint();
        calculateSummary();
    }

    private void saveAttendanceBatch() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        for (StudentRow row : activeRows) {
            // Hanya simpan jika status bukan "-"
            if(!row.getSelectedStatus().equals("-")) {
                 app.getData().upsertLog(new PresensiLog(
                    row.mhs.id, row.mhs.name, selectedMk.kode, 
                    row.getSelectedStatus(), date, time, "Dosen Input"
                ));
            }
        }
        JOptionPane.showMessageDialog(this, "Data kehadiran berhasil disimpan.");
    }
    
    private void calculateSummary() {
        int h=0, i=0, a=0;
        for(StudentRow r : activeRows) {
            String s = r.getSelectedStatus();
            if(s.equals("Hadir")) h++; 
            else if(s.equals("Alpha")) a++; 
            else if(s.equals("Izin") || s.equals("Sakit")) i++;
        }
        lblTotalHadir.setText("Hadir: " + h);
        lblTotalIzin.setText("Izin/Sakit: " + i);
        lblTotalAlpha.setText("Alpha: " + a);
    }

    private JLabel createLabel(String text, int align) {
        JLabel l = new JLabel(text, align);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        return l;
    }

    // --- INNER CLASS UNTUK BARIS TABEL MAHASISWA ---
    class StudentRow extends JPanel {
        User mhs;
        private StatusButton btnMasuk, btnIzin, btnAlpha;
        private String status;

        public StudentRow(int no, User mhs, int alphaHist, boolean editable, String initStatus) {
            this.mhs = mhs;
            this.status = initStatus;
            setLayout(new GridLayout(1, 4));
            setOpaque(false);
            setPreferredSize(new Dimension(0, 55)); 
            setBorder(new EmptyBorder(5, 10, 5, 10));
            
            add(new JLabel(String.valueOf(no), SwingConstants.CENTER));
            
            JPanel namePanel = new JPanel(new BorderLayout());
            namePanel.setOpaque(false);
            JLabel lblName = new JLabel(mhs.name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
            JLabel lblNim = new JLabel(mhs.id);
            lblNim.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblNim.setForeground(Color.GRAY);
            namePanel.add(lblName, BorderLayout.NORTH);
            namePanel.add(lblNim, BorderLayout.CENTER);
            namePanel.setBorder(new EmptyBorder(8, 0, 8, 0));
            add(namePanel);
            
            JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 12));
            btnGroup.setOpaque(false);
            
            if (editable || !initStatus.equals("-")) {
                btnMasuk = new StatusButton("Hadir", COL_MASUK);
                btnIzin = new StatusButton("Izin", COL_IZIN);
                btnAlpha = new StatusButton("Alpha", COL_ALPHA);
                
                btnMasuk.setEnabled(editable); 
                btnIzin.setEnabled(editable); 
                btnAlpha.setEnabled(editable);
                
                ActionListener al = e -> {
                    setSelection((StatusButton)e.getSource());
                    calculateSummary();
                };
                
                btnMasuk.addActionListener(al); 
                btnIzin.addActionListener(al); 
                btnAlpha.addActionListener(al);
                
                if (initStatus.equalsIgnoreCase("Hadir")) setSelection(btnMasuk);
                else if (initStatus.equalsIgnoreCase("Alpha")) setSelection(btnAlpha);
                else if (initStatus.equalsIgnoreCase("Izin") || initStatus.equalsIgnoreCase("Sakit")) setSelection(btnIzin);
                // Jika belum ada status tapi sesi aktif, default belum terpilih (atau Hadir jika mau auto)
                else if (editable) setSelection(btnMasuk); 
                
                btnGroup.add(btnMasuk); 
                btnGroup.add(btnIzin); 
                btnGroup.add(btnAlpha);
            } else {
                JLabel lblBelum = new JLabel("Belum Absen");
                lblBelum.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                lblBelum.setForeground(Color.GRAY);
                btnGroup.add(lblBelum);
            }
            add(btnGroup);
            
            JLabel lblAlpha = new JLabel(alphaHist > 0 ? alphaHist + "x" : "-", SwingConstants.CENTER);
            if(alphaHist > 2) lblAlpha.setForeground(Color.RED);
            add(lblAlpha);
        }

        private void setSelection(StatusButton btn) {
            if(btnMasuk!=null) btnMasuk.setActive(false);
            if(btnIzin!=null) btnIzin.setActive(false);
            if(btnAlpha!=null) btnAlpha.setActive(false);
            
            btn.setActive(true);
            
            if(btn == btnMasuk) status = "Hadir";
            else if(btn == btnAlpha) status = "Alpha";
            else status = "Izin";
        }
        
        public String getSelectedStatus() { return status; }
    }
}