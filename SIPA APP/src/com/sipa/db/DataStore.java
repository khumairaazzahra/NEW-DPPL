package com.sipa.db;

// PERBAIKAN: Import Model
import com.sipa.model.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// PERBAIKAN: Tambah 'public'
public class DataStore {
    private final String DB_URL = "jdbc:mysql://localhost:3306/sipa_db";
    private final String DB_USER = "root"; 
    private final String DB_PASS = "";     

    public List<MataKuliah> mataKuliah = new ArrayList<>();
    public List<User> users = new ArrayList<>(); 

    public DataStore() {
        refreshMataKuliah();
        refreshUserList(); 
    }

    // ... (Sisa kode ke bawah SAMA PERSIS dengan yang Anda buat, tidak ada perubahan logika)
    // Cukup pastikan method getConnection(), auth(), userExists(), refreshUserList(), dll tetap ada di sini.
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public User auth(String id, String pass) {
        String sql = "SELECT * FROM users WHERE id = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("id"), rs.getString("password"), rs.getString("name"),
                    rs.getString("role"), rs.getString("email"), rs.getString("phone")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean userExists(String id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM users WHERE id=?")) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public void refreshUserList() {
        users.clear();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                users.add(new User(rs.getString("id"), rs.getString("password"), rs.getString("name"), 
                                 rs.getString("role"), rs.getString("email"), rs.getString("phone")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addUser(User u) {
        String sql = "INSERT INTO users (id, password, name, role, email, phone) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.id); ps.setString(2, u.password); ps.setString(3, u.name);
            ps.setString(4, u.role); ps.setString(5, u.email); ps.setString(6, u.phone);
            ps.executeUpdate();
            refreshUserList();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateUser(String oldId, User u) {
        String sql = "UPDATE users SET name=?, password=?, role=?, email=?, phone=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.name); ps.setString(2, u.password); ps.setString(3, u.role);
            ps.setString(4, u.email); ps.setString(5, u.phone); ps.setString(6, oldId);
            ps.executeUpdate();
            refreshUserList();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void removeUser(String id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
            refreshUserList();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<User> getListMahasiswa() {
        refreshUserList(); 
        return users.stream().filter(u -> u.role.equals("MAHASISWA")).collect(Collectors.toList());
    }

    public void refreshMataKuliah() {
        mataKuliah.clear();
        String sql = "SELECT * FROM matakuliah";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                mataKuliah.add(new MataKuliah(
                    rs.getString("kode"), rs.getString("nama"), rs.getString("ruangan"),
                    rs.getString("waktu"), rs.getString("dosen"), rs.getBoolean("sesi_aktif")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateSesiMatkul(String kodeMk, boolean aktif) {
        String sql = "UPDATE matakuliah SET sesi_aktif = ? WHERE kode = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aktif ? 1 : 0);
            ps.setString(2, kodeMk);
            ps.executeUpdate();
            refreshMataKuliah(); 
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int hitungTotalAlpha(String nim) {
        String sql = "SELECT COUNT(*) FROM presensi_logs WHERE nim_mhs=? AND status='Alpha'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<PresensiLog> getLogsByMhs(String nim) {
        List<PresensiLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM presensi_logs WHERE nim_mhs = ? ORDER BY tanggal DESC, waktu_checkin DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                logs.add(new PresensiLog(
                    rs.getString("nim_mhs"), rs.getString("nama_mhs"), rs.getString("kode_mk"),
                    rs.getString("status"), rs.getString("tanggal"), rs.getString("waktu_checkin"), rs.getString("catatan")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    public List<PresensiLog> getLogsByMatkul(String kodeMk) {
        List<PresensiLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM presensi_logs WHERE kode_mk = ? ORDER BY tanggal DESC, nama_mhs ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeMk);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                logs.add(new PresensiLog(
                    rs.getString("nim_mhs"), rs.getString("nama_mhs"), rs.getString("kode_mk"),
                    rs.getString("status"), rs.getString("tanggal"), rs.getString("waktu_checkin"), rs.getString("catatan")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    public List<RekapHarianLog> getRekapLogByMatkul(String kodeMk) {
        List<RekapHarianLog> list = new ArrayList<>();
        
        String sql = "SELECT tanggal, " +
                     "GROUP_CONCAT(CASE WHEN status = 'Hadir' THEN nama_mhs END SEPARATOR ', ') as names_hadir, " +
                     "COUNT(CASE WHEN status = 'Hadir' THEN 1 END) as count_hadir, " +
                     "GROUP_CONCAT(CASE WHEN status IN ('Izin', 'Sakit') THEN nama_mhs END SEPARATOR ', ') as names_izin, " +
                     "COUNT(CASE WHEN status IN ('Izin', 'Sakit') THEN 1 END) as count_izin, " +
                     "GROUP_CONCAT(CASE WHEN status = 'Alpha' THEN nama_mhs END SEPARATOR ', ') as names_alpha, " +
                     "COUNT(CASE WHEN status = 'Alpha' THEN 1 END) as count_alpha " +
                     "FROM presensi_logs WHERE kode_mk = ? " +
                     "GROUP BY tanggal ORDER BY tanggal DESC";
                     
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeMk);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new RekapHarianLog(
                    rs.getString("tanggal"),                            
                    rs.getString("names_hadir"), rs.getInt("count_hadir"), 
                    rs.getString("names_izin"), rs.getInt("count_izin"),   
                    rs.getString("names_alpha"), rs.getInt("count_alpha")  
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
        return list;
    }

    public PresensiLog getLogHarian(String nim, String kodeMk, String tanggal) {
        String sql = "SELECT * FROM presensi_logs WHERE nim_mhs=? AND kode_mk=? AND tanggal=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim); ps.setString(2, kodeMk); ps.setString(3, tanggal);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new PresensiLog(
                    rs.getString("nim_mhs"), rs.getString("nama_mhs"), rs.getString("kode_mk"),
                    rs.getString("status"), rs.getString("tanggal"), rs.getString("waktu_checkin"), rs.getString("catatan")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void upsertLog(PresensiLog log) {
        String cekSql = "SELECT id FROM presensi_logs WHERE nim_mhs=? AND kode_mk=? AND tanggal=?";
        String insertSql = "INSERT INTO presensi_logs (nim_mhs, nama_mhs, kode_mk, status, tanggal, waktu_checkin, catatan) VALUES (?,?,?,?,?,?,?)";
        String updateSql = "UPDATE presensi_logs SET status=?, catatan=?, waktu_checkin=? WHERE id=?";

        try (Connection conn = getConnection()) {
            PreparedStatement psCek = conn.prepareStatement(cekSql);
            psCek.setString(1, log.nimMhs); psCek.setString(2, log.kodeMk); psCek.setString(3, log.tanggal);
            ResultSet rs = psCek.executeQuery();

            if (rs.next()) {
                int idLog = rs.getInt("id");
                PreparedStatement psUp = conn.prepareStatement(updateSql);
                psUp.setString(1, log.status); psUp.setString(2, log.catatan);
                psUp.setString(3, log.waktuCheckIn); psUp.setInt(4, idLog);
                psUp.executeUpdate();
            } else {
                PreparedStatement psIn = conn.prepareStatement(insertSql);
                psIn.setString(1, log.nimMhs); psIn.setString(2, log.namaMhs); psIn.setString(3, log.kodeMk);
                psIn.setString(4, log.status); psIn.setString(5, log.tanggal); psIn.setString(6, log.waktuCheckIn);
                psIn.setString(7, log.catatan);
                psIn.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Tambahkan method ini ke dalam class DataStore Anda
public List<MataKuliah> getMataKuliahByDosen(String namaDosen) {
    List<MataKuliah> list = new ArrayList<>();
    // Query filter berdasarkan nama dosen
    String sql = "SELECT * FROM matakuliah WHERE dosen = ?"; 
    
    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, namaDosen);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            list.add(new MataKuliah(
                rs.getString("kode"), rs.getString("nama"), rs.getString("ruangan"),
                rs.getString("waktu"), rs.getString("dosen"), rs.getBoolean("sesi_aktif")
            ));
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    public List<String> getAvailableDates(String kodeMk) {
        List<String> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT tanggal FROM presensi_logs WHERE kode_mk = ? ORDER BY tanggal DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeMk);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                dates.add(rs.getString("tanggal"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dates;
    }
    
    public List<PresensiLog> getLogsByMatkulAndDate(String kodeMk, String date) {
        List<PresensiLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM presensi_logs WHERE kode_mk = ? AND tanggal = ? ORDER BY nama_mhs ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeMk);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                logs.add(new PresensiLog(
                    rs.getString("nim_mhs"), rs.getString("nama_mhs"), rs.getString("kode_mk"),
                    rs.getString("status"), rs.getString("tanggal"), rs.getString("waktu_checkin"), rs.getString("catatan")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }
}