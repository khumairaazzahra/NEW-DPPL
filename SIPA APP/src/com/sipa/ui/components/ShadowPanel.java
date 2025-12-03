package com.sipa.ui.components;

import javax.swing.*;
import java.awt.*;

public class ShadowPanel extends JPanel {
    public ShadowPanel() { setOpaque(false); setLayout(new BorderLayout()); }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0,0,0,30));
        g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
        g2.dispose();
    }
}
