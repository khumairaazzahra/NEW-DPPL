import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.BiConsumer;

public class LoginPanel {
    private JPanel panel;
    private BiConsumer<String, String> onLogin; // (nama, nim/nip)

    public LoginPanel(BiConsumer<String, String> onLogin) {
        this.onLogin = onLogin;
        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(94, 165, 214)); 
        panel.setBorder(new EmptyBorder(40, 60, 40, 60));
        init();
    }

    private void init() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        container.setPreferredSize(new Dimension(900, 500));
        container.setOpaque(true);

        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(210, 210, 210));
        leftPanel.setPreferredSize(new Dimension(500, 400));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel logoText = new JLabel("<html><div style='text-align:center;'><h1>Portal<br>Akademik</h1><p>Universitas Y</p></div></html>", SwingConstants.CENTER);
        logoText.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoText.setForeground(new Color(60, 60, 60));
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(logoText);

        container.add(leftPanel, BorderLayout.WEST);

        
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        
        JLabel profileCircle = new JLabel();
        profileCircle.setPreferredSize(new Dimension(100, 100));
        profileCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileCircle.setOpaque(false);
        profileCircle.setBorder(BorderFactory.createLineBorder(new Color(83, 156, 204), 3));
        profileCircle.setMaximumSize(new Dimension(100, 100));

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(profileCircle);
        rightPanel.add(Box.createVerticalStrut(30));

        
        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(83,156,204)),
                "Nama"));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rightPanel.add(nameField);
        rightPanel.add(Box.createVerticalStrut(15));

        
        JTextField idField = new JTextField();
        idField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(83,156,204)),
                "NIM / NIP"));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rightPanel.add(idField);
        rightPanel.add(Box.createVerticalStrut(20));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(83,156,204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(100, 40));
        loginBtn.setMaximumSize(new Dimension(200, 45));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> {
            String nama = nameField.getText().trim();
            String id = idField.getText().trim();
            if (nama.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Masukkan Nama dan NIM/NIP terlebih dahulu.");
                return;
            }
            onLogin.accept(nama, id);
        });

        rightPanel.add(loginBtn);
        rightPanel.add(Box.createVerticalGlue());

        container.add(rightPanel, BorderLayout.CENTER);
        panel.add(container, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }
}
