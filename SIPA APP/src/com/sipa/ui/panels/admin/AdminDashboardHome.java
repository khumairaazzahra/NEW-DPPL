package com.sipa.ui.panels.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// Import model dan komponen yang diperlukan
import com.sipa.SipaApp;
import com.sipa.model.User;
import com.sipa.ui.components.ShadowPanel;

public class AdminDashboardHome extends JPanel {
    private SipaApp app;
    private User user;

    public AdminDashboardHome(User user, SipaApp app) {
        this.user = user;
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ============================================================
        // BAGIAN 1: Profil & Statistik (Header)
        // ============================================================
        JPanel topSection = new JPanel(new GridLayout(1, 4, 20, 0));
        topSection.setOpaque(false);
        topSection.setPreferredSize(new Dimension(0, 140));

        // --- A. Kartu Profil Admin ---
        ShadowPanel profileCard = new ShadowPanel();
        profileCard.setLayout(new GridBagLayout());
        JPanel textPanel = new JPanel(); 
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS)); 
        textPanel.setOpaque(false);
        
        JLabel lblName = new JLabel("Hi, Admin");
        if(user.name != null && user.name.contains(" ")) {
            lblName.setText("Hi, " + user.name.split(" ")[0]);
        } else {
             lblName.setText("Hi, " + user.name);
        }
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(SipaApp.COL_PRIMARY);
        
        JLabel lblRole = new JLabel("Administrator");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(Color.GRAY);

        textPanel.add(lblName);
        textPanel.add(lblRole);
        profileCard.add(textPanel);

        // --- B. Hitung Data Statistik ---
        // Refresh data agar akurat saat dashboard dibuka
        app.getData().refreshUserList();
        app.getData().refreshMataKuliah();

        long totalMhs = 0;
        long totalDosen = 0;
        for(User u : app.getData().users) {
            if(u.role.equalsIgnoreCase("MAHASISWA")) totalMhs++;
            if(u.role.equalsIgnoreCase("DOSEN")) totalDosen++;
        }
        int totalMatkul = app.getData().mataKuliah.size();

        // --- C. Tambahkan Kartu Statistik (Tanpa Icon) ---
        topSection.add(profileCard);
        topSection.add(createStatCard("Total Dosen", String.valueOf(totalDosen), SipaApp.COL_SUCCESS));
        topSection.add(createStatCard("Total Mhs", String.valueOf(totalMhs), SipaApp.COL_PRIMARY));
        topSection.add(createStatCard("Mata Kuliah", String.valueOf(totalMatkul), new Color(155, 89, 182)));

        // ============================================================
        // BAGIAN 2: Visualisasi & Info (Tengah)
        // ============================================================
        JPanel centerSection = new JPanel(new GridLayout(1, 2, 20, 20));
        centerSection.setOpaque(false);

        // --- Panel Kiri: Grafik Distribusi User ---
        ShadowPanel chartPanel = new ShadowPanel();
        chartPanel.setLayout(new BorderLayout());
        
        JLabel titleChart = new JLabel("Distribusi Pengguna Sistem");
        titleChart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleChart.setBorder(new EmptyBorder(15, 20, 10, 10));
        
        // PERBAIKAN LAYOUT: Menggunakan BoxLayout agar bar menumpuk rapi ke bawah
        JPanel bars = new JPanel();
        bars.setLayout(new BoxLayout(bars, BoxLayout.Y_AXIS)); 
        bars.setOpaque(false);
        bars.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        int totalUsers = app.getData().users.size();
        
        // Menambahkan bar visual
        bars.add(createBar("Dosen", (int)totalDosen, totalUsers, SipaApp.COL_SUCCESS));
        bars.add(createBar("Mahasiswa", (int)totalMhs, totalUsers, SipaApp.COL_PRIMARY));
        // Sisanya adalah admin
        bars.add(createBar("Admin", (int)(totalUsers - totalMhs - totalDosen), totalUsers, SipaApp.COL_WARNING));
        
        // Glue ini penting agar jika data sedikit, bar tetap di atas (tidak menyebar/stretch)
        bars.add(Box.createVerticalGlue());
        
        chartPanel.add(titleChart, BorderLayout.NORTH);
        chartPanel.add(bars, BorderLayout.CENTER);

        // --- Panel Kanan: Quick Info / Status ---
        ShadowPanel infoPanel = new ShadowPanel();
        infoPanel.setLayout(new BorderLayout());
        
        JLabel titleInfo = new JLabel("Status Sistem");
        titleInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleInfo.setBorder(new EmptyBorder(15, 20, 10, 10));
        
        JTextArea txtInfo = new JTextArea();
        txtInfo.setText("Sistem berjalan normal.\nKoneksi Database: Terhubung (MySQL)\n\nTips Administrator:\n1. Gunakan menu 'Manajemen User' untuk menambah akun Dosen/Mahasiswa.\n2. Pastikan ID (NIM/NIP) bersifat unik.\n3. Data Mata Kuliah diambil dari Database.");
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBorder(new EmptyBorder(10, 20, 20, 20));

        infoPanel.add(titleInfo, BorderLayout.NORTH);
        infoPanel.add(txtInfo, BorderLayout.CENTER);

        // Gabungkan ke Layout Utama
        centerSection.add(chartPanel);
        centerSection.add(infoPanel);

        add(topSection, BorderLayout.NORTH);
        add(centerSection, BorderLayout.CENTER);
    }

    /**
     * Membuat Kartu Statistik Sederhana (Angka Besar + Judul Kecil)
     */
    private JPanel createStatCard(String title, String value, Color accent) {
        ShadowPanel p = new ShadowPanel();
        p.setLayout(new BorderLayout());
        
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblVal.setForeground(accent);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        content.add(lblVal);
        content.add(lblTitle);
        
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    /**
     * Membuat Bar Chart Modern (Slim Design)
     */
    private JPanel createBar(String label, int val, int max, Color c) {
        // Container utama untuk satu item (Label + Bar)
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(5, 0, 15, 0)); // Jarak antar item

        // 1. Header Text: Label di Kiri, Nilai/Persen di Kanan
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Short.MAX_VALUE, 20)); 

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(Color.DARK_GRAY);

        // Hitung persentase
        int percent = (max > 0) ? (int)((double)val/max * 100) : 0;
        JLabel lblVal = new JLabel(val + " (" + percent + "%)");
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVal.setForeground(c); // Warna angka mengikuti warna bar

        header.add(lblName, BorderLayout.WEST);
        header.add(lblVal, BorderLayout.EAST);

        // 2. Visual Bar (Custom Painting agar Slim & Rounded)
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = 8; // Tinggi bar (lebih ramping/slim)
                int y = (getHeight() - h) / 2; // Posisi Y agar di tengah panel
                
                // Gambar Background Track (Abu-abu sangat muda)
                g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, y, getWidth(), h, h, h);

                // Gambar Progress Bar (Warna)
                if (max > 0 && val > 0) {
                    int w = (int) ((double) val / max * getWidth());
                    g2.setColor(c);
                    // Math.max menjamin bar tetap terlihat bulat meski nilainya kecil
                    g2.fillRoundRect(0, y, Math.max(h, w), h, h, h);
                }
            }
        };
        // Set tinggi fix untuk panel bar agar tidak melar
        bar.setPreferredSize(new Dimension(0, 15)); 
        bar.setMaximumSize(new Dimension(Short.MAX_VALUE, 15));
        bar.setOpaque(false);

        container.add(header);
        container.add(Box.createVerticalStrut(5)); // Spasi kecil antara teks dan bar
        container.add(bar);

        return container;
    }
}