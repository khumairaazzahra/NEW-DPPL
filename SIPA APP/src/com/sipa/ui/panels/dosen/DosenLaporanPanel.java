package com.sipa.ui.panels.dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class DosenLaporanPanel extends JPanel {
    private SipaApp app;
    private JComboBox<MataKuliah> cbMatkul;
    private JTable tableDetail;
    private DefaultTableModel modelDetail;
    private JLabel lblTotalDetail;
    private JTable tableRekap;
    private DefaultTableModel modelRekap;
    
    public DosenLaporanPanel(User user, SipaApp app) {
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 20, 20, 20));
        
        // --- HEADER ---
        ShadowPanel headerCard = new ShadowPanel();
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);
        JLabel title = new JLabel("Laporan Kehadiran");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(SipaApp.COL_PRIMARY);
        JLabel subtitle = new JLabel("Pilih mata kuliah untuk melihat laporan");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        headerLeft.add(title); headerLeft.add(subtitle);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        JLabel lblPilih = new JLabel("Mata Kuliah: ");
        lblPilih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbMatkul = new JComboBox<>();
        cbMatkul.setPreferredSize(new Dimension(280, 35));
        
        java.util.List<MataKuliah> myMatkul = app.getData().getMataKuliahByDosen(user.name);
        for(MataKuliah mk : myMatkul) {
            cbMatkul.addItem(mk);
        }
        
        cbMatkul.addActionListener(e -> loadData());
        filterPanel.add(lblPilih);
        filterPanel.add(cbMatkul);
        headerCard.add(headerLeft, BorderLayout.WEST);
        headerCard.add(filterPanel, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);
        
        // --- TABBED PANE ---
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JPanel panelDetail = createDetailPanel();
        tabPane.addTab("Detail Log Presensi", panelDetail);
        
        JPanel panelRekap = createRekapPanel();
        tabPane.addTab("Rekapitulasi Per Tanggal", panelRekap);
        
        add(tabPane, BorderLayout.CENTER);
        loadData(); 
    }

    private JPanel createDetailPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        String[] cols = {"Tanggal", "NIM", "Nama Mahasiswa", "Status", "Waktu Check-in", "Keterangan"};
        
        modelDetail = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableDetail = new JTable(modelDetail);
        setupTableStyle(tableDetail);
        
        JScrollPane scroll = new JScrollPane(tableDetail);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblTotalDetail = new JLabel("Menampilkan 0 data.");
        lblTotalDetail.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        ModernButton btnPrint = new ModernButton("Cetak Detail (PDF)", SipaApp.COL_PRIMARY);
        btnPrint.setPreferredSize(new Dimension(150, 35));
        btnPrint.addActionListener(e -> printTable(tableDetail, "Laporan Detail Presensi"));
        
        footer.add(lblTotalDetail, BorderLayout.WEST);
        footer.add(btnPrint, BorderLayout.EAST);
        p.add(scroll, BorderLayout.CENTER);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createRekapPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        String[] cols = {"Tanggal", "Daftar Hadir", "Daftar Izin/Sakit", "Daftar Alpha", "Total", "% Hadir"};
        
        modelRekap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableRekap = new JTable(modelRekap);
        setupTableStyle(tableRekap);
        
        // GUNAKAN CUSTOM RENDERER
        MultiLineCellRenderer renderer = new MultiLineCellRenderer();
        tableRekap.setDefaultRenderer(Object.class, renderer);
        
        // Atur lebar kolom agar muat nama panjang
        TableColumnModel cm = tableRekap.getColumnModel();
        cm.getColumn(0).setPreferredWidth(90);  // Tanggal
        cm.getColumn(1).setPreferredWidth(300); // Daftar Hadir (Paling Lebar)
        cm.getColumn(2).setPreferredWidth(200); // Izin
        cm.getColumn(3).setPreferredWidth(200); // Alpha
        cm.getColumn(4).setPreferredWidth(60);  // Total
        cm.getColumn(5).setPreferredWidth(70);  // Persen

        JScrollPane scroll = new JScrollPane(tableRekap);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        ModernButton btnPrint = new ModernButton("Cetak Rekap (PDF)", SipaApp.COL_SUCCESS);
        btnPrint.setPreferredSize(new Dimension(150, 35));
        btnPrint.addActionListener(e -> printTable(tableRekap, "Rekapitulasi Kehadiran Harian"));
        footer.add(btnPrint);
        
        p.add(scroll, BorderLayout.CENTER);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private void setupTableStyle(JTable t) {
        t.setRowHeight(40); 
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(245, 245, 245));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setShowGrid(true);
        t.setGridColor(new Color(230, 230, 230));
    }

    private void loadData() {
        MataKuliah mk = (MataKuliah) cbMatkul.getSelectedItem();
        if(mk == null) return;
        
        // Load Detail
        modelDetail.setRowCount(0);
        java.util.List<PresensiLog> logs = app.getData().getLogsByMatkul(mk.kode);
        for(PresensiLog log : logs) {
            modelDetail.addRow(new Object[]{
                log.tanggal, log.nimMhs, log.namaMhs, log.status, log.waktuCheckIn, log.catatan
            });
        }
        lblTotalDetail.setText("Total Log: " + logs.size() + " data.");
    
        // Load Rekap
        modelRekap.setRowCount(0);
        java.util.List<RekapHarianLog> rekapList = app.getData().getRekapLogByMatkul(mk.kode);
        for(RekapHarianLog r : rekapList) {
            int percent = (r.total > 0) ? (int)((double)r.countHadir/r.total * 100) : 0;
            
            // Format List Nama Menjadi Per Baris
            String strHadir = formatDaftarNama(r.namesHadir, r.countHadir);
            String strIzin  = formatDaftarNama(r.namesIzin, r.countIzin);
            String strAlpha = formatDaftarNama(r.namesAlpha, r.countAlpha);
            
            modelRekap.addRow(new Object[]{
                r.tanggal,
                strHadir,
                strIzin,
                strAlpha,
                r.total + " Mhs",
                percent + "%"
            });
        }
        
        updateRowHeights();
    }
    
    // METHOD BARU: Mengubah "Andi, Budi" menjadi format list ke bawah
    private String formatDaftarNama(String names, int count) {
        if (names == null || names.isEmpty()) return "-";
        
        String[] arrNames = names.split(", ");
        StringBuilder sb = new StringBuilder();
        
        // Batasi maksimal tampil 50 nama agar tidak overload (opsional)
        int limit = Math.min(arrNames.length, 50); 
        
        for (int i = 0; i < limit; i++) {
            sb.append(i + 1).append(". ").append(arrNames[i]);
            if (i < limit - 1) sb.append("\n");
        }
        
        if (arrNames.length > limit) {
            sb.append("\n... dan ").append(arrNames.length - limit).append(" lainnya");
        }
        
        // Tambahkan total di bawah
        sb.append("\n\n(Total: ").append(count).append(")");
        
        return sb.toString();
    }
    
    private void updateRowHeights() {
        for (int row = 0; row < tableRekap.getRowCount(); row++) {
            int rowHeight = tableRekap.getRowHeight();
            for (int column = 0; column < tableRekap.getColumnCount(); column++) {
                Component comp = tableRekap.prepareRenderer(tableRekap.getCellRenderer(row, column), row, column);
                // Tambahkan padding extra agar tidak terlalu sesak
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height + 10);
            }
            tableRekap.setRowHeight(row, rowHeight);
        }
    }

    private void printTable(JTable t, String titleSub) {
        try {
            MataKuliah mk = (MataKuliah) cbMatkul.getSelectedItem();
            String fullTitle = titleSub + " - " + (mk != null ? mk.nama : "");
            boolean complete = t.print(JTable.PrintMode.FIT_WIDTH, 
                                     new java.text.MessageFormat(fullTitle), 
                                     new java.text.MessageFormat("Halaman {0}"));
            if (complete) JOptionPane.showMessageDialog(this, "Sukses mencetak data.");
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
        }
    }
    
    // --- CUSTOM RENDERER (JTextArea) ---
    static class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setMargin(new Insets(5, 5, 5, 5)); // Padding teks di dalam sel
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            
            setText((value == null) ? "" : value.toString());
            
            // Kolom Alpha (Index 3) warna Merah
            if (column == 3 && value != null && !value.toString().equals("-")) {
                setForeground(Color.RED);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
            
            // Set lebar agar wrapping berfungsi benar saat hitung tinggi
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            
            return this;
        }
    }
}