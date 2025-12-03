package com.sipa.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.sipa.SipaApp;
import com.sipa.model.User;
import com.sipa.ui.components.ModernButton;
// PERBAIKAN: Import panel dashboard admin yang baru
import com.sipa.ui.panels.admin.AdminDashboardHome;
import com.sipa.ui.panels.admin.AdminPanel;
import com.sipa.ui.panels.dosen.*;
import com.sipa.ui.panels.mahasiswa.*;

public class MainDashboard extends JPanel {
    private SipaApp app;
    private User user;
    private JPanel contentArea;
    private CardLayout contentLayout;

    public MainDashboard(SipaApp app, User user) {
        this.app = app;
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(SipaApp.COL_BG);
        
        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel brand = new JLabel("Portal " + (user.role.equals("MAHASISWA") ? "Mahasiswa" : user.role));
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(SipaApp.COL_PRIMARY);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(40));
        
        // Menu Navigasi
        addMenu(sidebar, "Dashboard", "HOME");
        
        if (user.role.equals("MAHASISWA")) {
            addMenu(sidebar, "Informasi Perkuliahan", "INFO_KULIAH");
            addMenu(sidebar, "Riwayat Perkuliahan", "RIWAYAT");
            addMenu(sidebar, "Data Kehadiran", "STATS");
        } else if (user.role.equals("DOSEN")) {
            addMenu(sidebar, "Jadwal Mengajar", "DOSEN_JADWAL");
            addMenu(sidebar, "Laporan Kehadiran", "DOSEN_LAPORAN");
        } else {
            // Role ADMIN
            addMenu(sidebar, "Manajemen User", "ADMIN_USER");
        }
        
        sidebar.add(Box.createVerticalGlue());
        ModernButton btnLogout = new ModernButton("Logout", SipaApp.COL_ACCENT);
        btnLogout.setMaximumSize(new Dimension(200, 40));
        btnLogout.addActionListener(e -> app.logout());
        sidebar.add(btnLogout);

        // --- CONTENT AREA ---
        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(20,20,20,20));

        // Inisialisasi Panel Berdasarkan Role
        if(user.role.equals("MAHASISWA")) {
            contentArea.add(new MhsDashboardHome(user, app), "HOME");
            contentArea.add(new MhsInfoKuliah(user, app), "INFO_KULIAH");
            contentArea.add(new MhsRiwayat(user, app), "RIWAYAT");
            contentArea.add(new MhsStats(user, app), "STATS");
        } else if (user.role.equals("DOSEN")) {
            contentArea.add(new DosenDashboard(user, app), "HOME");
            contentArea.add(new DosenAbsensiPanel(user, app), "DOSEN_JADWAL");
            contentArea.add(new DosenLaporanPanel(user, app), "DOSEN_LAPORAN");
        } else {
            // Role ADMIN
            // PERBAIKAN: Gunakan AdminDashboardHome untuk "HOME"
            contentArea.add(new AdminDashboardHome(user, app), "HOME");
            contentArea.add(new AdminPanel(user, app), "ADMIN_USER");
        }

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private void addMenu(JPanel p, String title, String cardName) {
        JButton btn = new JButton(title);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> contentLayout.show(contentArea, cardName));
        p.add(btn);
        p.add(Box.createVerticalStrut(10));
    }
}