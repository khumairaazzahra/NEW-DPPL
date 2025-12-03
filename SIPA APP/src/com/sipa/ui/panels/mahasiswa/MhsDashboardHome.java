package com.sipa.ui.panels.mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class MhsDashboardHome extends JPanel {
    private SipaApp app;
    private User user;
    private JPanel listPanel;
    private JLabel lblJadwalTitle;

    public MhsDashboardHome(User user, SipaApp app) {
        this.app = app;
        this.user = user;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        // --- BAGIAN ATAS: Profil & IPK ---
        JPanel top = new JPanel(new GridLayout(1, 2, 20, 0));
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 150));

        // 1. Kartu Profil
        ShadowPanel profil = new ShadowPanel();
        profil.setLayout(new GridBagLayout());
        JPanel profContent = new JPanel();
        profContent.setLayout(new BoxLayout(profContent, BoxLayout.Y_AXIS));
        profContent.setOpaque(false);
        
        JLabel lblName = new JLabel(user.name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblName.setForeground(SipaApp.COL_PRIMARY);
        
        profContent.add(lblName);
        profContent.add(new JLabel(user.id));
        profContent.add(Box.createVerticalStrut(5));
        profContent.add(new JLabel(user.email));
        
        profil.add(profContent);

        // 2. Kartu IPK (Dummy Data)
        ShadowPanel ipkPanel = new ShadowPanel();
        ipkPanel.setLayout(new BorderLayout());
        JLabel lblIpkVal = new JLabel("3.99");
        lblIpkVal.setFont(new Font("Segoe UI", Font.BOLD, 60));
        
        // IPK tetap Biru sesuai request sebelumnya
        lblIpkVal.setForeground(SipaApp.COL_PRIMARY); 
        
        lblIpkVal.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblIpkTitle = new JLabel("Indeks Prestasi Kumulatif", SwingConstants.CENTER);
        lblIpkTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblIpkTitle.setForeground(Color.GRAY);
        
        ipkPanel.add(lblIpkVal, BorderLayout.CENTER);
        ipkPanel.add(lblIpkTitle, BorderLayout.NORTH);

        top.add(profil);
        top.add(ipkPanel);

        // --- BAGIAN BAWAH: Daftar Jadwal & Absensi ---
        ShadowPanel schedule = new ShadowPanel();
        schedule.setLayout(new BorderLayout());
        
        // Header Jadwal
        JPanel headerSched = new JPanel(new BorderLayout());
        headerSched.setOpaque(false);
        headerSched.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblJadwalTitle = new JLabel("Jadwal Kuliah Hari Ini", SwingConstants.LEFT);
        lblJadwalTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        ModernButton btnRefresh = new ModernButton("⟳ Refresh", SipaApp.COL_WARNING);
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> {
            app.getData().refreshMataKuliah(); 
            refreshList();
        });
        
        headerSched.add(lblJadwalTitle, BorderLayout.WEST);
        headerSched.add(btnRefresh, BorderLayout.EAST);

        // List Container
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        schedule.add(headerSched, BorderLayout.NORTH);
        schedule.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(schedule, BorderLayout.CENTER);
        
        refreshList();
    }

    private void refreshList() {
        listPanel.removeAll();
        
        // Ambil data terbaru
        app.getData().refreshMataKuliah();
        
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // Dapatkan Nama Hari Ini
        Locale idLocale = new Locale("id", "ID");
        String hariIni = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE", idLocale));
        
        lblJadwalTitle.setText("Jadwal Kuliah Hari Ini (" + hariIni + ")");

        boolean adaJadwal = false;

        for(MataKuliah mk : app.getData().mataKuliah) {
            
            // FILTER: Hanya tampilkan mata kuliah hari ini
            if (!mk.waktu.toLowerCase().contains(hariIni.toLowerCase())) {
                continue; 
            }

            adaJadwal = true;

            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, new Color(240,240,240)),
                new EmptyBorder(15, 20, 15, 20)
            ));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(2000, 85));

            // Info Mata Kuliah
            // PERUBAHAN: Warna Dosen diubah jadi gray (bukan biru lagi)
            JLabel mkName = new JLabel("<html><b style='font-size:14px; color:#2c3e50'>" + mk.nama + "</b> <span style='color:gray'>(" + mk.kode + ")</span><br>" 
                                     + "<span style='font-size:11px; color:gray'>" + mk.waktu + " | " + mk.ruangan + "</span><br>"
                                     + "<span style='font-size:11px; color:gray'>Dosen: " + mk.dosen + "</span></html>");

            // Tombol Absensi
            ModernButton btnAbsen = new ModernButton("Isi Presensi", SipaApp.COL_PRIMARY);
            btnAbsen.setPreferredSize(new Dimension(140, 35));
            btnAbsen.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Cek Status Log
            PresensiLog log = app.getData().getLogHarian(user.id, mk.kode, todayDate);

            if (log != null) {
                // SUDAH ABSEN
                btnAbsen.setText(log.status.toUpperCase()); 
                if(log.status.equalsIgnoreCase("Hadir")) {
                    btnAbsen.setBackground(SipaApp.COL_SUCCESS);
                } else if(log.status.equalsIgnoreCase("Alpha")) {
                    btnAbsen.setBackground(SipaApp.COL_ACCENT);
                } else {
                    btnAbsen.setBackground(SipaApp.COL_WARNING);
                }
                btnAbsen.setEnabled(false);
                
            } else if (mk.sesiAktif) {
                // BELUM ABSEN & SESI BUKA
                btnAbsen.setText("Isi Presensi");
                btnAbsen.setBackground(SipaApp.COL_PRIMARY);
                btnAbsen.setForeground(Color.WHITE);
                btnAbsen.setEnabled(true);
                btnAbsen.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnAbsen.addActionListener(e -> showPresensiDialog(mk, btnAbsen));
                
            } else {
                // BELUM ABSEN & SESI TUTUP
                btnAbsen.setText("Sesi Tutup");
                btnAbsen.setBackground(Color.LIGHT_GRAY);
                btnAbsen.setForeground(Color.WHITE);
                btnAbsen.setEnabled(false);
            }

            row.add(mkName, BorderLayout.CENTER);
            row.add(btnAbsen, BorderLayout.EAST);
            listPanel.add(row);
        }

        if (!adaJadwal) {
            JLabel empty = new JLabel("<html><center>Tidak ada jadwal kuliah hari " + hariIni + ".<br>Selamat beristirahat!</center></html>", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            empty.setBorder(new EmptyBorder(50,0,0,0));
            listPanel.add(empty);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private void showPresensiDialog(MataKuliah mk, JButton btnSource) {
        String[] options = {"HADIR", "SAKIT", "IZIN"};
        int choice = JOptionPane.showOptionDialog(this, 
            "<html><h3>Konfirmasi Kehadiran</h3>" +
            "Mata Kuliah: <b>" + mk.nama + "</b><br>" +
            "Dosen: " + mk.dosen + "<br><br>" +
            "Silakan pilih status kehadiran Anda:</html>", 
            "Form Presensi Mahasiswa", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);

        if(choice == -1) return;

        String status = choice == 0 ? "Hadir" : (choice == 1 ? "Sakit" : "Izin");
        String catatan = "-";

        if(choice != 0) {
            catatan = JOptionPane.showInputDialog(this, "Masukkan Keterangan (Wajib diisi):");
            if(catatan == null || catatan.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Gagal: Keterangan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("HH:mm").format(new Date());

        PresensiLog newLog = new PresensiLog(
            user.id, user.name, mk.kode, status, date, time, catatan
        );

        app.getData().upsertLog(newLog);
        JOptionPane.showMessageDialog(this, "Berhasil! Kehadiran tercatat: " + status);
        refreshList();
    }
}