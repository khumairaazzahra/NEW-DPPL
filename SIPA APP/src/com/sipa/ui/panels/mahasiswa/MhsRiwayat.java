package com.sipa.ui.panels.mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class MhsRiwayat extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private SipaApp app;
    private User user;

    public MhsRiwayat(User u, SipaApp app) {
        this.app = app;
        this.user = u;
        setLayout(new BorderLayout(20,20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 20, 20, 20)); // Tambah margin agar rapi
        
        JLabel title = new JLabel("Riwayat Perkuliahan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(SipaApp.COL_PRIMARY);
        add(title, BorderLayout.NORTH);

        String[] col = {"Tanggal", "Waktu", "Mata Kuliah", "Status", "Catatan"};
        
        // PERUBAHAN UTAMA: Override isCellEditable agar tabel tidak bisa diedit
        model = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengunci semua sel agar tidak bisa diubah
            }
        };

        table = new JTable(model);
        table.setRowHeight(35); // Tinggi baris diperbesar sedikit
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(new Color(230, 230, 230));
        
        // Button Refresh di bawah
        ModernButton btnRefresh = new ModernButton("Refresh Data", SipaApp.COL_PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(150, 35));
        btnRefresh.addActionListener(e -> refreshData());
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(btnRefresh);

        ShadowPanel p = new ShadowPanel();
        p.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // Hilangkan border default scrollpane
        p.add(scroll, BorderLayout.CENTER);
        
        add(p, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        
        refreshData();
    }
    
    private void refreshData() {
        model.setRowCount(0);
        // Mengambil data log presensi berdasarkan ID mahasiswa
        for(PresensiLog log : app.getData().getLogsByMhs(user.id)) {
            String namaMk = log.kodeMk;
            
            // Mencari Nama Mata Kuliah berdasarkan Kode
            for(MataKuliah m : app.getData().mataKuliah) {
                if(m.kode.equals(log.kodeMk)) {
                    namaMk = m.nama;
                    break;
                }
            }
            
            // Menambahkan baris ke tabel
            model.addRow(new Object[]{
                log.tanggal, 
                log.waktuCheckIn, 
                namaMk, 
                log.status, 
                log.catatan
            });
        }
    }
}