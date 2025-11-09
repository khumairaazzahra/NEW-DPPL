

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CalendarPanel extends JPanel {
    public CalendarPanel(int year, int month) {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton prev = new JButton("<"); prev.setEnabled(false);
        JButton next = new JButton(">"); next.setEnabled(false);
        JLabel title = new JLabel("Februari 2025", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.add(prev, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(5,7,6,6));
        grid.setBorder(new EmptyBorder(8,8,8,8));
        String[] hdr = {"S","M","T","W","T","F","S"};
        for (String h : hdr) { grid.add(new JLabel(h, SwingConstants.CENTER)); }
        List<Integer> green = Arrays.asList(3,17);
        List<Integer> yellow = Arrays.asList(10);
        List<Integer> red = Arrays.asList(24);
        for (int d=1; d<=28; d++) {
            JButton day = new JButton(String.valueOf(d));
            day.setFocusPainted(false);
            day.setBorderPainted(false);
            day.setOpaque(true);
            if (green.contains(d)) day.setBackground(new Color(151,226,164));
            else if (yellow.contains(d)) day.setBackground(new Color(250,232,150));
            else if (red.contains(d)) day.setBackground(new Color(255,167,167));
            else day.setBackground(Color.WHITE);
            grid.add(day);
        }
        add(grid, BorderLayout.CENTER);
    }
}
