package com.sipa.ui.components;

import javax.swing.*;
import java.awt.*;

public class ModernButton extends JButton {
    private Color col;
    public ModernButton(String text, Color col) {
        super(text); this.col = col;
        setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
        setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? col.darker() : col);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
