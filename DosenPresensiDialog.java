

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.Timer;

public class DosenPresensiDialog extends JDialog {
    public DosenPresensiDialog(Frame owner, Course course, List<StudentRecord> students, DosenPortal host) {
        super(owner, "Presensi: " + course.getName(), true);
        setSize(820,540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        JLabel info = new JLabel("<html><b>" + course.getName() + "</b> &nbsp; | &nbsp; " + course.getCode() + "</html>");
        info.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(info, BorderLayout.NORTH);

        String[] cols = {"No","NIM","Nama","Status"};
        DefaultTableModel model = new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        if (students != null) {
            for (int i=0;i<students.size();i++) {
                StudentRecord s = students.get(i);
                model.addRow(new Object[]{i+1, s.getNim(), s.getName(), s.getStatus()});
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        right.setPreferredSize(new Dimension(220,0));

        JButton btnStart = new JButton("Mulai Presensi (30m)");
        JButton btnTutup = new JButton("Tutup Presensi");
        btnTutup.setEnabled(false);
        JButton btnHadir = new JButton("Tandai Hadir");
        JButton btnIzin = new JButton("Tandai Izin");
        JButton btnSakit = new JButton("Tandai Sakit");
        JButton btnSimpan = new JButton("Simpan & Tutup");

        right.add(btnStart);
        right.add(Box.createVerticalStrut(8));
        right.add(btnTutup);
        right.add(Box.createVerticalStrut(12));
        right.add(new JLabel("Operasi baris terpilih:"));
        right.add(Box.createVerticalStrut(8));
        right.add(btnHadir);
        right.add(Box.createVerticalStrut(6));
        right.add(btnIzin);
        right.add(Box.createVerticalStrut(6));
        right.add(btnSakit);
        right.add(Box.createVerticalStrut(12));
        right.add(btnSimpan);

        add(right, BorderLayout.EAST);

        final Timer[] countdown = new Timer[1];
        final int[] remaining = new int[1];
        JLabel timerLabel = new JLabel("Belum dimulai");
        timerLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        add(timerLabel, BorderLayout.SOUTH);

        btnStart.addActionListener(e -> {
            remaining[0] = 30*60;
            btnStart.setEnabled(false);
            btnTutup.setEnabled(true);
            updateTimerLabel(timerLabel, remaining[0]);

            for (int r=0;r<model.getRowCount();r++) {
                Object st = model.getValueAt(r,3);
                if (st==null || st.toString().trim().isEmpty()) model.setValueAt("Belum Hadir", r, 3);
            }

            countdown[0] = new Timer(1000, ev -> {
                remaining[0]--;
                updateTimerLabel(timerLabel, remaining[0]);
                if (remaining[0] <= 0) {
                    countdown[0].stop();
                    JOptionPane.showMessageDialog(DosenPresensiDialog.this, "Waktu presensi berakhir.");
                    btnTutup.setEnabled(false);
                    btnStart.setEnabled(true);
                }
            });
            countdown[0].start();
        });

        btnTutup.addActionListener(e -> {
            if (countdown[0]!=null && countdown[0].isRunning()) countdown[0].stop();
            btnTutup.setEnabled(false);
            btnStart.setEnabled(true);
            timerLabel.setText("Presensi ditutup manual.");
            for (int r=0;r<model.getRowCount();r++) {
                if ("Belum Hadir".equals(model.getValueAt(r,3))) model.setValueAt("Alpha", r, 3);
            }
        });

        ActionListener markAct = ae -> {
            String cmd = ((JButton)ae.getSource()).getText();
            int[] sel = table.getSelectedRows();
            if (sel.length==0) {
                JOptionPane.showMessageDialog(this, "Pilih mahasiswa dulu pada tabel.");
                return;
            }
            String status = "Hadir";
            if (cmd.contains("Izin")) status = "Izin";
            else if (cmd.contains("Sakit")) status = "Sakit";
            for (int r : sel) model.setValueAt(status, r, 3);
        };
        btnHadir.addActionListener(markAct);
        btnIzin.addActionListener(markAct);
        btnSakit.addActionListener(markAct);

        btnSimpan.addActionListener(e -> {
            if (students != null) {
                for (int r=0;r<model.getRowCount();r++) {
                    String nim = model.getValueAt(r,1).toString();
                    String st = model.getValueAt(r,3).toString();
                    for (StudentRecord s : students) {
                        if (s.getNim().equals(nim)) { s.setStatus(st); break; }
                    }
                }
            }
            if (countdown[0]!=null && countdown[0].isRunning()) countdown[0].stop();
            JOptionPane.showMessageDialog(this, "Presensi disimpan.");
            
            if (host != null) host.addRiwayat(course.getCode());
            dispose();
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2) {
                    int r = table.getSelectedRow();
                    if (r>=0) {
                        String cur = model.getValueAt(r,3).toString();
                        model.setValueAt("Hadir".equals(cur) ? "Alpha" : "Hadir", r, 3);
                    }
                }
            }
        });

        setVisible(true);
    }

    private void updateTimerLabel(JLabel lbl, int seconds) {
        if (seconds <= 0) { lbl.setText("00:00"); return; }
        int m = seconds/60, s = seconds%60;
        lbl.setText(String.format("Waktu tersisa: %02d:%02d", m, s));
    }
}
