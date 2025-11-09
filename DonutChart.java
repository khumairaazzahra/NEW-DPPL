
import javax.swing.*;
import java.awt.*;

public class DonutChart extends JPanel {
    private final int percent;
    public DonutChart(int percent) {
        this.percent = percent;
        setPreferredSize(new Dimension(160,160));
        setOpaque(false);
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int r = Math.min(w,h)/2 - 10;
        int cx = w/2, cy = h/2;
        g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(230,230,230));
        g2.drawOval(cx-r, cy-r, 2*r, 2*r);
        g2.setColor(new Color(139,184,223));
        int angle = (int)(360 * percent / 100.0);
        g2.drawArc(cx-r, cy-r, 2*r, 2*r, 90, -angle);
        g2.setColor(Color.WHITE);
        int inner = r - 26;
        g2.fillOval(cx-inner, cy-inner, inner*2, inner*2);
        g2.setColor(new Color(50,100,160));
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String txt = percent + "%";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, cx - fm.stringWidth(txt)/2, cy + fm.getAscent()/2 - 4);
    }
}
