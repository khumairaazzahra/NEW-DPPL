package com.sipa.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.sipa.SipaApp;
import com.sipa.model.User;
import com.sipa.ui.components.ModernButton;

// PERBAIKAN: Tambah 'public'
public class LoginPanel extends JPanel {
    public LoginPanel(SipaApp app) {
        // ... (Kode sama persis) ...
        setLayout(new GridLayout(1, 2));
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(SipaApp.COL_PRIMARY);
        JLabel ilustrasi = new JLabel("<html><center><h1 style='color:white; font-size:40px;'>SIPA</h1><h2 style='color:white;'>Universitas Riau</h2></center></html>");
        ilustrasi.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(ilustrasi);
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(0, 50, 0, 50));
        JLabel lblTitle = new JLabel("Halaman Login");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtUser = new JTextField(20);
        txtUser.setBorder(BorderFactory.createTitledBorder("NIM / NIP / ID"));
        JPasswordField txtPass = new JPasswordField(20);
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));
        ModernButton btnLogin = new ModernButton("Login", SipaApp.COL_PRIMARY);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(400, 40));
        JLabel hint = new JLabel("<html><font color='gray'><b>Info:</b> Pastikan Database 'sipa_db' sudah aktif.</font></html>");
        btnLogin.addActionListener(e -> {
            User u = app.getData().auth(txtUser.getText(), new String(txtPass.getPassword()));
            if(u != null) app.loginSuccess(u);
            else JOptionPane.showMessageDialog(this, "Login Gagal! Cek ID/Password atau Koneksi DB.", "Error", JOptionPane.ERROR_MESSAGE);
        });
        form.add(lblTitle);
        form.add(Box.createVerticalStrut(30));
        form.add(txtUser);
        form.add(Box.createVerticalStrut(15));
        form.add(txtPass);
        form.add(Box.createVerticalStrut(20));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(20));
        form.add(hint);
        right.add(form);
        add(left); add(right);
    }
}