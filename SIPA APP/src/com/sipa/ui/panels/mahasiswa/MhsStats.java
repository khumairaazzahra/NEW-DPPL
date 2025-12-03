package com.sipa.ui.panels.mahasiswa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.sipa.SipaApp;
import com.sipa.model.*;
import com.sipa.ui.components.*;

// PERBAIKAN: Tambah 'public'
public class MhsStats extends JPanel {
    private User user;
    private SipaApp app;
    private MataKuliah selectedMk;
    private JPanel chartsContainer;
    private YearMonth currentMonth = YearMonth.now();

    public MhsStats(User u, SipaApp app) {
        this.user = u;
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);
        JLabel title = new JLabel("Data Kehadiran");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
        ShadowPanel mainCard = new ShadowPanel();
        mainCard.setLayout(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(10, 20, 0, 20));
        JLabel lblMk = new JLabel("Mata Kuliah: ");
        JComboBox<MataKuliah> cbMk = new JComboBox<>();
        for (MataKuliah mk : app.getData().mataKuliah) cbMk.addItem(mk);
        cbMk.addActionListener(e -> {
            selectedMk = (MataKuliah) cbMk.getSelectedItem();
            updateDashboard();
        });
        if (cbMk.getItemCount() > 0) selectedMk = (MataKuliah) cbMk.getItemAt(0);
        toolbar.add(lblMk);
        toolbar.add(cbMk);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblDetailTitle = new JLabel("Data Kehadiran");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailTitle.setBorder(new EmptyBorder(15, 20, 5, 0));
        headerPanel.add(lblDetailTitle, BorderLayout.WEST);
        headerPanel.add(toolbar, BorderLayout.EAST);
        mainCard.add(headerPanel, BorderLayout.NORTH);
        chartsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsContainer.setOpaque(false);
        chartsContainer.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainCard.add(chartsContainer, BorderLayout.CENTER);
        add(mainCard, BorderLayout.CENTER);
        updateDashboard();
    }

    private void updateDashboard() {
        chartsContainer.removeAll();
        List<PresensiLog> logs = app.getData().getLogsByMhs(user.id).stream()
                .filter(l -> selectedMk != null && l.kodeMk.equals(selectedMk.kode))
                .collect(Collectors.toList());
        int tHadir = 0, tIzin = 0, tSakit = 0, tAlpha = 0;
        for (PresensiLog l : logs) {
            if (l.status.equalsIgnoreCase("Hadir")) tHadir++;
            else if (l.status.equalsIgnoreCase("Izin") || l.status.equalsIgnoreCase("Sakit")) tIzin++;
            else if (l.status.equalsIgnoreCase("Alpha")) tAlpha++;
        }
        int fHadir = tHadir;
        int fIzin  = tIzin;
        int fAlpha = tAlpha;
        int fTotal = Math.max(1, tHadir + tIzin + tSakit + tAlpha); 
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        JPanel donut = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g2.setColor(new Color(230, 230, 235)); 
                g2.fillOval(x, y, s, s);
                int realSum = fHadir + fIzin + fAlpha; 
                if (realSum > 0) {
                    double angH = (double) fHadir / fTotal * 360;
                    double angI = (double) fIzin / fTotal * 360;
                    double angA = (double) fAlpha / fTotal * 360;
                    double cur = 90;
                    g2.setColor(SipaApp.COL_SUCCESS); g2.fill(new Arc2D.Double(x, y, s, s, cur, angH, Arc2D.PIE)); cur += angH;
                    g2.setColor(SipaApp.COL_WARNING); g2.fill(new Arc2D.Double(x, y, s, s, cur, angI, Arc2D.PIE)); cur += angI;
                    g2.setColor(SipaApp.COL_ACCENT);  g2.fill(new Arc2D.Double(x, y, s, s, cur, angA, Arc2D.PIE));
                }
                g2.setColor(Color.WHITE);
                g2.fillOval(x + s/4, y + s/4, s/2, s/2);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                String pct = "0%";
                if(realSum > 0) {
                    pct = (int)((double)fHadir/realSum * 100) + "%";
                }
                FontMetrics fm = g2.getFontMetrics();
                int strW = fm.stringWidth(pct);
                int strH = fm.getAscent();
                g2.drawString(pct, getWidth()/2 - strW/2, getHeight()/2 + strH/4);
            }
        };
        donut.setOpaque(false);
        JPanel legend = new JPanel(new GridLayout(3, 1, 5, 5));
        legend.setOpaque(false);
        legend.add(new JLabel("Hadir: " + fHadir));
        legend.add(new JLabel("Izin/Sakit: " + fIzin));
        legend.add(new JLabel("Alpha: " + fAlpha));
        JPanel topChart = new JPanel(new GridLayout(1, 2));
        topChart.setOpaque(false);
        topChart.add(donut);
        topChart.add(legend);
        JPanel barPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        barPanel.setOpaque(false);
        barPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        barPanel.add(createBar("Hadir", fHadir, fTotal, SipaApp.COL_SUCCESS));
        barPanel.add(createBar("Sakit/Izin", fIzin, fTotal, SipaApp.COL_WARNING));
        barPanel.add(createBar("Alpha", fAlpha, fTotal, SipaApp.COL_ACCENT));
        leftPanel.add(topChart);
        leftPanel.add(new JLabel("Grafik Kehadiran"));
        leftPanel.add(barPanel);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        JPanel calHeader = new JPanel(new BorderLayout());
        calHeader.setOpaque(false);
        JLabel lblMonth = new JLabel(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), SwingConstants.CENTER);
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnPrev = new JButton("<"); btnPrev.setBorderPainted(false); btnPrev.setContentAreaFilled(false);
        JButton btnNext = new JButton(">"); btnNext.setBorderPainted(false); btnNext.setContentAreaFilled(false);
        btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); updateDashboard(); });
        btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); updateDashboard(); });
        calHeader.add(btnPrev, BorderLayout.WEST);
        calHeader.add(lblMonth, BorderLayout.CENTER);
        calHeader.add(btnNext, BorderLayout.EAST);
        JPanel calGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        calGrid.setOpaque(false);
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for(String d : days) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            calGrid.add(l);
        }
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int gridStart = (dayOfWeek == 7) ? 0 : dayOfWeek;
        for(int i=0; i<gridStart; i++) calGrid.add(new JLabel(""));
        int daysInMonth = currentMonth.lengthOfMonth();
        for(int d=1; d<=daysInMonth; d++) {
            LocalDate date = currentMonth.atDay(d);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Color cellColor = null;
            for(PresensiLog l : logs) {
                if(l.tanggal.equals(dateStr)) {
                    if(l.status.equalsIgnoreCase("Hadir")) cellColor = SipaApp.COL_SUCCESS;
                    else if(l.status.equalsIgnoreCase("Alpha")) cellColor = SipaApp.COL_ACCENT;
                    else cellColor = SipaApp.COL_WARNING;
                    break;
                }
            }
            JLabel dayLbl = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            dayLbl.setOpaque(true);
            dayLbl.setBackground(cellColor != null ? cellColor : Color.WHITE);
            if(cellColor != null) dayLbl.setForeground(Color.WHITE); 
            calGrid.add(dayLbl);
        }
        rightPanel.add(calHeader, BorderLayout.NORTH);
        rightPanel.add(calGrid, BorderLayout.CENTER);
        chartsContainer.add(leftPanel);
        chartsContainer.add(rightPanel);
        chartsContainer.revalidate();
        chartsContainer.repaint();
    }
    
    private JPanel createBar(String label, int val, int max, Color c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                int barW = (max > 0) ? (int)((double)val/max * w) : 0;
                g.setColor(new Color(230,230,230));
                g.fillRoundRect(0, 0, w, h, 10, 10);
                g.setColor(c);
                g.fillRoundRect(0, 0, Math.max(10, barW), h, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(label, 10, h/2 + 5);
            }
        };
        bar.setPreferredSize(new Dimension(0, 30));
        p.add(bar, BorderLayout.CENTER);
        return p;
    }
}