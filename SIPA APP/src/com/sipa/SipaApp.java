package com.sipa;

import javax.swing.*;
import java.awt.*;
import com.sipa.db.DataStore;
import com.sipa.model.User;
import com.sipa.ui.panels.LoginPanel;
import com.sipa.ui.panels.MainDashboard;

public class SipaApp extends JFrame {

    public static final Color COL_PRIMARY = new Color(41, 128, 185);
    public static final Color COL_BG = new Color(240, 242, 245);
    public static final Color COL_ACCENT = new Color(231, 76, 60);
    public static final Color COL_SUCCESS = new Color(46, 204, 113);
    public static final Color COL_WARNING = new Color(241, 196, 15);

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DataStore dataStore;
    private User currentUser;

    public SipaApp() {
        setTitle("SIPA - Universitas Riau (Database Connected)");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inisialisasi DataStore
        dataStore = new DataStore();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "LOGIN");

        add(mainPanel);
    }

    public void loginSuccess(User user) {
        this.currentUser = user;
        dataStore.refreshMataKuliah(); 
        JPanel dashboard = new MainDashboard(this, user);
        mainPanel.add(dashboard, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            cardLayout.show(mainPanel, "LOGIN");
        }
    }

    public DataStore getData() { return dataStore; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan!\nMasukkan file .jar mysql-connector ke library project.");
            return;
        }

        SwingUtilities.invokeLater(() -> new SipaApp().setVisible(true));
    }
}