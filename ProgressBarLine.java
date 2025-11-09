
import javax.swing.*;
import java.awt.*;

public class ProgressBarLine extends JPanel {
    public ProgressBarLine(String label, int percent) {
        setLayout(new BorderLayout(8,0));
        setOpaque(false);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(120,20));
        JPanel bg = new JPanel(null);
        bg.setPreferredSize(new Dimension(300,18));
        bg.setBackground(new Color(240,240,240));
        bg.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        JPanel fill = new JPanel();
        int w = Math.max(2, (int)(bg.getPreferredSize().width * percent / 100.0));
        fill.setBounds(0,0,w,18);
        if (label.toLowerCase().contains("hadir")) fill.setBackground(new Color(119,209,98));
        else if (label.toLowerCase().contains("sakit")) fill.setBackground(new Color(244,220,108));
        else fill.setBackground(new Color(245,140,140));
        bg.add(fill);
        add(l, BorderLayout.WEST);
        add(bg, BorderLayout.CENTER);
    }
}
