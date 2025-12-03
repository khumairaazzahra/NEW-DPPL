package com.sipa.ui.panels.mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class MhsInfoKuliah extends JPanel {
    private SipaApp app;
    private User user; // Perlu menyimpan user untuk cek log presensi
    private JPanel contentPanel;
    private JTextField txtSearch;

    public MhsInfoKuliah(User u, SipaApp app) {
        this.app = app;
        this.user = u;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 20, 20, 20));

        // --- HEADER: Judul & Pencarian ---
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Daftar Mata Kuliah");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(SipaApp.COL_PRIMARY);

        // Panel Pencarian
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel lblCari = new JLabel("Cari: ");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nama Matkul / Dosen...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadData(txtSearch.getText());
            }
        });

        searchPanel.add(lblCari, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        header.add(title, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- LIST KONTEN ---
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        // Load data awal
        loadData("");
    }

    private void loadData(String keyword) {
        contentPanel.removeAll();
        app.getData().refreshMataKuliah();
        
        boolean found = false;
        String search = keyword.toLowerCase();

        for(MataKuliah mk : app.getData().mataKuliah) {
            // Filter Pencarian
            if (!search.isEmpty()) {
                if (!mk.nama.toLowerCase().contains(search) && 
                    !mk.dosen.toLowerCase().contains(search) && 
                    !mk.kode.toLowerCase().contains(search)) {
                    continue;
                }
            }

            found = true;
            contentPanel.add(createCourseCard(mk));
            contentPanel.add(Box.createVerticalStrut(15)); // Jarak antar kartu
        }

        if (!found) {
            JLabel empty = new JLabel("Data tidak ditemukan.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(50));
            contentPanel.add(empty);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCourseCard(MataKuliah mk) {
        ShadowPanel card = new ShadowPanel();
        card.setLayout(new BorderLayout(10, 0)); // Gap horizontal antar komponen
        card.setMaximumSize(new Dimension(2000, 110));
        card.setPreferredSize(new Dimension(0, 110));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Panel Kiri: Nama & Kode
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(300, 0)); // Lebar fix agar rapi
        
        JLabel lblNama = new JLabel(mk.nama);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNama.setForeground(new Color(44, 62, 80)); 

        JLabel lblKode = new JLabel(mk.kode);
        lblKode.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKode.setForeground(SipaApp.COL_PRIMARY); 

        left.add(lblNama);
        left.add(lblKode);

        // 2. Panel Tengah: Detail Waktu, Ruang, Dosen (TANPA ICON)
        JPanel center = new JPanel(new GridLayout(3, 1, 0, 2));
        center.setOpaque(false);

        center.add(createDetailLabel("Waktu: " + mk.waktu));
        center.add(createDetailLabel("Ruangan: " + mk.ruangan));
        center.add(createDetailLabel("Dosen: " + mk.dosen));

        // 3. Panel Kanan: Button Absensi
        JPanel right = new JPanel(new GridBagLayout()); // Gunakan GridBag agar button di tengah vertikal
        right.setOpaque(false);
        
        // Logika Button (Sama seperti Dashboard)
        ModernButton btnAbsen = new ModernButton("Isi Presensi", SipaApp.COL_PRIMARY);
        btnAbsen.setPreferredSize(new Dimension(130, 35));
        btnAbsen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        PresensiLog log = app.getData().getLogHarian(user.id, mk.kode, todayDate);

        if (log != null) {
            // Sudah Absen
            btnAbsen.setText(log.status.toUpperCase()); 
            if(log.status.equalsIgnoreCase("Hadir")) btnAbsen.setBackground(SipaApp.COL_SUCCESS);
            else if(log.status.equalsIgnoreCase("Alpha")) btnAbsen.setBackground(SipaApp.COL_ACCENT);
            else btnAbsen.setBackground(SipaApp.COL_WARNING);
            btnAbsen.setEnabled(false);
        } else if (mk.sesiAktif) {
            // Sesi Buka
            btnAbsen.setText("Isi Presensi");
            btnAbsen.setBackground(SipaApp.COL_PRIMARY);
            btnAbsen.setEnabled(true);
            btnAbsen.addActionListener(e -> showPresensiDialog(mk));
        } else {
            // Sesi Tutup
            btnAbsen.setText("Sesi Tutup");
            btnAbsen.setBackground(Color.LIGHT_GRAY);
            btnAbsen.setEnabled(false);
        }

        right.add(btnAbsen);

        card.add(left, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JLabel createDetailLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    private void showPresensiDialog(MataKuliah mk) {
        String[] options = {"HADIR", "SAKIT", "IZIN"};
        int choice = JOptionPane.showOptionDialog(this, 
            "<html><h3>Konfirmasi Kehadiran</h3><br>Mata Kuliah: <b>" + mk.nama + "</b><br>Dosen: " + mk.dosen + "</html>", 
            "Form Presensi", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if(choice == -1) return;

        String status = choice == 0 ? "Hadir" : (choice == 1 ? "Sakit" : "Izin");
        String catatan = "-";

        if(choice != 0) {
            catatan = JOptionPane.showInputDialog(this, "Masukkan Keterangan (Wajib):");
            if(catatan == null || catatan.trim().isEmpty()) return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH:mm").format(new Date());

        app.getData().upsertLog(new PresensiLog(user.id, user.name, mk.kode, status, date, time, catatan));
        JOptionPane.showMessageDialog(this, "Berhasil! Kehadiran tercatat: " + status);
        
        // Refresh list untuk update status tombol, tetap pertahankan text search
        loadData(txtSearch.getText());
    }
}