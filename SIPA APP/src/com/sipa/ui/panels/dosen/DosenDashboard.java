package com.sipa.ui.panels.dosen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

public class DosenDashboard extends JPanel {
    private User user;
    private SipaApp app;
    private JPanel scheduleList;
    private JPanel activeSessionPanel;
    private JLabel lblStatHadir, lblStatIzin, lblStatAlpha, lblStatTotal;
    private JLabel lblPercentCircle;
    private JProgressBar progressBar;
    private ModernButton btnCloseSession;
    private JPanel circlePanel; 

    public DosenDashboard(User u, SipaApp app) {
        this.user = u;
        this.app = app;
        setLayout(new BorderLayout(25, 25));
        setOpaque(false);
        JPanel topSection = new JPanel(new GridLayout(1, 2, 25, 0));
        topSection.setOpaque(false);
        topSection.setPreferredSize(new Dimension(0, 230));
        ShadowPanel profileCard = new ShadowPanel();
        profileCard.setLayout(new GridBagLayout());
        JPanel avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(225, 230, 235)); g2.fillOval(5, 5, 90, 90);
                g2.setColor(new Color(160, 170, 180)); g2.fillOval(30, 20, 40, 40);
                g2.fillArc(15, 65, 70, 60, 0, 180);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(100, 100));
        avatarPanel.setOpaque(false);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel lblName = new JLabel("<html>" + user.name + "</html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblName.setForeground(SipaApp.COL_PRIMARY);
        JLabel lblNip = new JLabel("NIP: " + user.id);
        lblNip.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNip.setForeground(Color.DARK_GRAY);
        JLabel lblFak = new JLabel("Fakultas Ilmu Komputer");
        lblFak.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFak.setForeground(Color.GRAY);
        JLabel lblStatus = new JLabel(" Status: Aktif ");
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(220, 255, 220));
        lblStatus.setForeground(new Color(39, 174, 96));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setBorder(new EmptyBorder(2,5,2,5));
        textPanel.add(lblName); textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblNip); textPanel.add(lblFak);
        textPanel.add(Box.createVerticalStrut(10)); textPanel.add(lblStatus);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 20); 
        gbc.gridx = 0; gbc.gridy = 0; profileCard.add(avatarPanel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0; 
        profileCard.add(textPanel, gbc);
        ShadowPanel scheduleCard = new ShadowPanel();
        scheduleCard.setLayout(new BorderLayout());
        JLabel lblSchedTitle = new JLabel("Jadwal Mengajar");
        lblSchedTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSchedTitle.setBorder(new EmptyBorder(20, 20, 10, 20));
        scheduleList = new JPanel();
        scheduleList.setLayout(new BoxLayout(scheduleList, BoxLayout.Y_AXIS));
        scheduleList.setOpaque(false);
        JScrollPane scroll = new JScrollPane(scheduleList);
        scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scheduleCard.add(lblSchedTitle, BorderLayout.NORTH);
        scheduleCard.add(scroll, BorderLayout.CENTER);
        topSection.add(profileCard);
        topSection.add(scheduleCard);
        activeSessionPanel = new JPanel(new BorderLayout());
        activeSessionPanel.setOpaque(false);
        ShadowPanel sessionCard = new ShadowPanel();
        sessionCard.setLayout(new BorderLayout());
        sessionCard.setBorder(new EmptyBorder(15, 25, 20, 25)); 
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblSesiTitle = new JLabel("Monitor Sesi Aktif"); 
        lblSesiTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activeSessionPanel.putClientProperty("lblTitle", lblSesiTitle);
        btnCloseSession = new ModernButton("Akhiri Sesi", SipaApp.COL_ACCENT);
        btnCloseSession.setPreferredSize(new Dimension(130, 35));
        headerPanel.add(lblSesiTitle, BorderLayout.WEST);
        headerPanel.add(btnCloseSession, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        circlePanel = new JPanel() {
             @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                int thickness = 14; 
                int x = (getWidth()-size)/2; int y = (getHeight()-size)/2;
                g2.setColor(new Color(235, 235, 240));
                g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x+thickness/2, y+thickness/2, size-thickness, size-thickness, 0, 360);
                int total = progressBar.getMaximum(); int current = progressBar.getValue();
                if(total > 0) {
                    double angle = (double)current / total * 360.0;
                    g2.setColor(new Color(46, 204, 113)); 
                    g2.drawArc(x+thickness/2, y+thickness/2, size-thickness, size-thickness, 90, (int)-angle);
                }
                g2.dispose();
            }
        };
        circlePanel.setPreferredSize(new Dimension(130, 130));
        circlePanel.setOpaque(false);
        circlePanel.setLayout(new GridBagLayout());
        lblPercentCircle = new JLabel("0%");
        lblPercentCircle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPercentCircle.setForeground(SipaApp.COL_PRIMARY);
        circlePanel.add(lblPercentCircle);
        progressBar = new JProgressBar(0, 100);
        progressBar.setVisible(false);
        progressBar.addChangeListener(e -> {
             int pct = 0;
             if(progressBar.getMaximum() > 0) pct = (int)((double)progressBar.getValue()/progressBar.getMaximum()*100);
             lblPercentCircle.setText(pct + "%");
             circlePanel.repaint();
        });
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0)); 
        statsGrid.setOpaque(false);
        lblStatTotal = new JLabel("0", SwingConstants.CENTER);
        lblStatHadir = new JLabel("0", SwingConstants.CENTER);
        lblStatIzin = new JLabel("0", SwingConstants.CENTER);
        lblStatAlpha = new JLabel("0", SwingConstants.CENTER);
        statsGrid.add(createStatCard("TOTAL MHS", lblStatTotal, Color.GRAY));
        statsGrid.add(createStatCard("HADIR", lblStatHadir, SipaApp.COL_SUCCESS));
        statsGrid.add(createStatCard("IZIN / SAKIT", lblStatIzin, SipaApp.COL_WARNING));
        statsGrid.add(createStatCard("ALPHA", lblStatAlpha, SipaApp.COL_ACCENT));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.insets = new Insets(0, 10, 0, 40); 
        contentPanel.add(circlePanel, gc);
        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.BOTH;
        contentPanel.add(statsGrid, gc);
        sessionCard.add(headerPanel, BorderLayout.NORTH);
        sessionCard.add(contentPanel, BorderLayout.CENTER);
        sessionCard.add(progressBar, BorderLayout.SOUTH); 
        activeSessionPanel.add(sessionCard);
        add(topSection, BorderLayout.NORTH);
        add(activeSessionPanel, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(250, 250, 252)); 
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor), 
            BorderFactory.createLineBorder(new Color(230,230,230), 1)
        ));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBorder(new EmptyBorder(10, 0, 5, 0));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.DARK_GRAY);
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    public void refreshData() {
        scheduleList.removeAll();
        MataKuliah activeMk = null;
        
        // PERUBAHAN UTAMA: Filter mata kuliah berdasarkan nama dosen
        java.util.List<MataKuliah> myMatkul = app.getData().getMataKuliahByDosen(user.name);

        for (MataKuliah mk : myMatkul) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(10, 20, 10, 20)); 
            JPanel line = new JPanel(); line.setPreferredSize(new Dimension(0, 1)); line.setBackground(new Color(240,240,240));
            JPanel content = new JPanel(new BorderLayout()); content.setOpaque(false);
            JLabel lblMk = new JLabel("<html><b style='font-size:12px'>" + mk.nama + "</b><br><span style='color:gray; font-size:11px'>" + mk.waktu + " | " + mk.ruangan + "</span></html>");
            ModernButton btnAction;
            if (mk.sesiAktif) {
                activeMk = mk; btnAction = new ModernButton("Kelola", SipaApp.COL_PRIMARY);
            } else {
                btnAction = new ModernButton("Buka", SipaApp.COL_PRIMARY);
            }
            btnAction.setPreferredSize(new Dimension(80, 30)); btnAction.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnAction.addActionListener(e -> {
                 Container parent = getParent();
                 if(parent != null && parent.getLayout() instanceof CardLayout) ((CardLayout)parent.getLayout()).show(parent, "DOSEN_JADWAL");
            });
            content.add(lblMk, BorderLayout.CENTER); content.add(btnAction, BorderLayout.EAST);
            row.add(content, BorderLayout.CENTER); row.add(line, BorderLayout.SOUTH);
            scheduleList.add(row);
        }
        
        JLabel titleLbl = (JLabel) activeSessionPanel.getClientProperty("lblTitle");
        if (activeMk != null) {
            titleLbl.setText("Monitor Sesi: " + activeMk.nama);
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int hadir=0, izin=0, alpha=0;
            List<User> allMhs = app.getData().getListMahasiswa();
            for(User mhs : allMhs) {
                PresensiLog log = app.getData().getLogHarian(mhs.id, activeMk.kode, today);
                if(log != null) {
                    if(log.status.equalsIgnoreCase("Hadir")) hadir++;
                    else if(log.status.equalsIgnoreCase("Alpha")) alpha++;
                    else izin++;
                }
            }
            int total = allMhs.isEmpty() ? 40 : allMhs.size();
            progressBar.setMaximum(total); progressBar.setValue(hadir);
            lblStatTotal.setText(String.valueOf(total));
            lblStatHadir.setText(String.valueOf(hadir));
            lblStatIzin.setText(String.valueOf(izin));
            lblStatAlpha.setText(String.valueOf(alpha));
            btnCloseSession.setEnabled(true); btnCloseSession.setBackground(SipaApp.COL_ACCENT);
            MataKuliah finalMk = activeMk;
            for(ActionListener al : btnCloseSession.getActionListeners()) btnCloseSession.removeActionListener(al);
            btnCloseSession.addActionListener(e -> {
                int c = JOptionPane.showConfirmDialog(this, "Akhiri sesi presensi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if(c == JOptionPane.YES_OPTION) {
                    app.getData().updateSesiMatkul(finalMk.kode, false);
                    refreshData();
                }
            });
            activeSessionPanel.setVisible(true);
        } else {
            titleLbl.setText("Tidak ada sesi aktif");
            progressBar.setValue(0);
            lblStatTotal.setText("-"); lblStatHadir.setText("-"); 
            lblStatIzin.setText("-"); lblStatAlpha.setText("-");
            btnCloseSession.setEnabled(false); btnCloseSession.setBackground(Color.GRAY);
        }
        scheduleList.revalidate(); scheduleList.repaint();
    }
}