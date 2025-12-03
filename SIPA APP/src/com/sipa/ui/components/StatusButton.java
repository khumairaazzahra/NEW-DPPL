package com.sipa.ui.components;

import javax.swing.*;
import java.awt.*;

public class StatusButton extends JButton {
    private boolean isActive = false;
    private Color activeColor;
    
    public StatusButton(String text, Color activeColor) {
        super(text);
        this.activeColor = activeColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setActive(false);
    }

    public void setActive(boolean b) {
        this.isActive = b;
        if (isActive) {
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            setBackground(activeColor);
            setForeground(Color.WHITE);
            setOpaque(true);
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
            setBackground(Color.WHITE);
            setForeground(Color.DARK_GRAY);
            setOpaque(false);
        }
        repaint();
    }
    public boolean isActive() { return isActive; }
}
