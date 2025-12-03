package com.sipa.ui.panels.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;

// --- PERBAIKAN PENTING: Import ini WAJIB ada agar app.getData() dikenali ---
import com.sipa.db.DataStore; 
import com.sipa.SipaApp;
import com.sipa.model.User;
import com.sipa.ui.components.ModernButton;
import com.sipa.ui.components.ShadowPanel;

public class AdminPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private SipaApp app;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    public AdminPanel(User u, SipaApp app) {
        this.app = app;
        setLayout(new BorderLayout(20,20));
        setOpaque(false);
        
        JLabel l = new JLabel("Manajemen Data Pengguna");
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        String[] col = {"ID/NIM", "Nama", "Role", "Email", "Password"};
        model = new DefaultTableModel(col, 0);
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // Panggil refresh data awal
        refreshTable();
        
        ShadowPanel p = new ShadowPanel();
        p.add(new JScrollPane(table));
        
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tools.setOpaque(false);
        
        ModernButton btnAdd = new ModernButton("+ Tambah", SipaApp.COL_SUCCESS);
        btnAdd.addActionListener(e -> showAddDialog());
        
        ModernButton btnEdit = new ModernButton("Edit", SipaApp.COL_WARNING);
        btnEdit.addActionListener(e -> showEditDialog());
        
        ModernButton btnDel = new ModernButton("Hapus", SipaApp.COL_ACCENT);
        btnDel.addActionListener(e -> deleteUser());
        
        txtSearch = new JTextField(20);
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        
        tools.add(btnAdd); tools.add(btnEdit); tools.add(btnDel);
        tools.add(new JLabel("  Cari:")); tools.add(txtSearch);
        
        add(l, BorderLayout.NORTH);
        add(tools, BorderLayout.SOUTH);
        add(p, BorderLayout.CENTER);
    }

    private void refreshTable() {
        model.setRowCount(0);
        
        // BAGIAN INI YANG SEBELUMNYA ERROR
        // Pastikan DataStore sudah di-import di atas, dan public class DataStore sudah benar
        DataStore ds = app.getData(); 
        ds.refreshUserList();
        
        for(User usr : ds.users) {
            model.addRow(new Object[]{usr.id, usr.name, usr.role, usr.email, usr.password});
        }
    }

    private void showAddDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah User", true);
        d.setSize(400, 400);
        d.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(new EmptyBorder(20,20,20,20));
        
        JTextField tfId = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfPass = new JTextField();
        JTextField tfEmail = new JTextField();
        String[] roles = {"MAHASISWA", "DOSEN", "ADMIN"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        
        p.add(new JLabel("ID / NIM:")); p.add(tfId);
        p.add(new JLabel("Nama:")); p.add(tfName);
        p.add(new JLabel("Password:")); p.add(tfPass);
        p.add(new JLabel("Email:")); p.add(tfEmail);
        p.add(new JLabel("Role:")); p.add(cbRole);
        
        ModernButton btnSave = new ModernButton("Simpan", SipaApp.COL_PRIMARY);
        
        btnSave.addActionListener(e -> {
            if(tfId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(d, "ID tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if(app.getData().userExists(tfId.getText().trim())) {
                JOptionPane.showMessageDialog(d, "ID User sudah ada!", "Error Duplikasi", JOptionPane.ERROR_MESSAGE);
                tfId.requestFocus();
                tfId.selectAll();
                return;
            }

            User newUser = new User(tfId.getText(), tfPass.getText(), tfName.getText(), 
                    (String)cbRole.getSelectedItem(), tfEmail.getText(), "-");
            app.getData().addUser(newUser);
            refreshTable();
            d.dispose();
            JOptionPane.showMessageDialog(this, "User Berhasil Ditambahkan!");
        });
        
        p.add(new JLabel("")); p.add(btnSave);
        d.add(p); d.setVisible(true);
    }
    
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Pilih user yang akan diedit!"); return; }
        String id = (String) table.getValueAt(row, 0);
        User target = null;
        for(User u : app.getData().users) if(u.id.equals(id)) target = u;
        if(target == null) return;
        
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        d.setSize(400, 400);
        d.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(new EmptyBorder(20,20,20,20));
        
        JTextField tfId = new JTextField(target.id); tfId.setEditable(false);
        JTextField tfName = new JTextField(target.name);
        JTextField tfPass = new JTextField(target.password);
        JTextField tfEmail = new JTextField(target.email);
        String[] roles = {"MAHASISWA", "DOSEN", "ADMIN"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        cbRole.setSelectedItem(target.role);
        
        p.add(new JLabel("ID / NIM:")); p.add(tfId);
        p.add(new JLabel("Nama:")); p.add(tfName);
        p.add(new JLabel("Password:")); p.add(tfPass);
        p.add(new JLabel("Email:")); p.add(tfEmail);
        p.add(new JLabel("Role:")); p.add(cbRole);
        
        ModernButton btnSave = new ModernButton("Update", SipaApp.COL_WARNING);
        btnSave.addActionListener(e -> {
            User newUser = new User(tfId.getText(), tfPass.getText(), tfName.getText(), 
                    (String)cbRole.getSelectedItem(), tfEmail.getText(), "-");
            app.getData().updateUser(tfId.getText(), newUser);
            refreshTable();
            d.dispose();
            JOptionPane.showMessageDialog(this, "Data User Diperbarui!");
        });
        
        p.add(new JLabel("")); p.add(btnSave);
        d.add(p); d.setVisible(true);
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Pilih baris dulu!"); return; }
        String id = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus user " + id + "?");
        if(confirm == JOptionPane.YES_OPTION) {
            app.getData().removeUser(id);
            refreshTable();
        }
    }
}