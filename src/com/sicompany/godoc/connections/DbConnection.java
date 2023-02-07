package com.sicompany.godoc.connections;

import com.sicompany.godoc.accounts.Pasien;
import com.sicompany.godoc.accounts.Roles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * 
 * @author Kelompok 2
 */
public class DbConnection {
    
    private static Connection c;
    private static Statement s;
    private static ResultSet rs;

    private static String URL = "jdbc:mysql://localhost:3306/db_godoc";
    private static String USERNAME = "root";
    private static String PASSWORD = "";

    private static void openDb() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    private static void runQuery(String query, boolean producesResultSets) {
        try {
            openDb();
            if(producesResultSets)
                rs = s.executeQuery(query);
            else
                s.executeUpdate(query);
        }
        catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: Kesalahan pada query! (" + e.getMessage() + ")");
            e.printStackTrace();
            return;
        }
        catch(ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: Driver JDBC tidak ditemukan!");
            e.printStackTrace();
            return;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        finally {
            closeDb();
        }
    }

    private static void closeDb() {

        if(c == null) {

            try {
                c.close();
                s.close();
                rs.close();
            } 
            
            catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static boolean login(String username, String password) {

        if(username.equals("") || password.equals("")) {
            JOptionPane.showMessageDialog(null, "Username atau password tidak boleh kosong");
            return false;
        }

        runQuery(
            "SELECT username, password FROM akun WHERE username = '" + username + "' AND password = '" + password + "'",
            true
        );
        try {
            return rs.next();
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String[] fetchUserData(String username) {
            
        runQuery("SELECT * FROM akun WHERE username = '" + username + "'", true);

        try {

            if(rs.next()) {
                String[] data = new String[5];
                data[0] = rs.getString("user_id");
                data[1] = rs.getString("username");
                data[2] = rs.getString("password");
                data[3] = rs.getString("role");

                return data;
            }
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String[] fetchPasienData(String id) {
                
        runQuery("SELECT * FROM pasien WHERE id_pasien = '" + id + "'", true);

        try {
            
            if(rs.next()) {
                String[] data = new String[5];
                data[0] = rs.getString("id_pasien");
                data[1] = rs.getString("nama_pasien");
                data[2] = rs.getString("jenis_kelamin");
                data[3] = rs.getString("no_hp");
                data[4] = rs.getString("alamat");

                return data;
            }
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isUsernameExist(String username) {

        runQuery("SELECT * FROM akun WHERE username = '" + username + "'", true);

        try {

            if(rs.next()) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String generateUserID(Roles role) {

        String roleQuery = role == Roles.PASIEN ? "Pasien" : "Admin";
        String prefix = role == Roles.PASIEN ? "P" : "A";

        try {

            runQuery("SELECT COUNT(user_id) AS result FROM akun WHERE role = '" + roleQuery + "'", true);
            
            int count = 0;
            if(rs.next()) {
                count = rs.getInt("result");
            }

            return prefix + String.format("%03d", count +1);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static boolean register(String id, String username, String password, Roles role) {

        String roleQuery = role == Roles.PASIEN ? "Pasien" : "Admin";

        if(username.equals("") || password.equals("")) {
            JOptionPane.showMessageDialog(null, "Username atau password tidak boleh kosong");
            return false;
        } else if(password.length() < 6) {
            JOptionPane.showMessageDialog(null, "Password harus lebih dari 6 karakter");
            return false;
        } else if(isUsernameExist(username)) {
            JOptionPane.showMessageDialog(null, "Username sudah terdaftar");
            return false;
        } else {
            runQuery("INSERT INTO akun VALUES ('" + id + "', '" + username + "', '" + password + "', '" + roleQuery + "')", false);
            return true;
        }
    }

    public static void tambahPasien(Pasien user) {
        try {
            runQuery("INSERT INTO pasien VALUES ('" + user.getId() + "', '" + user.getNamaLengkap() + "', '" + user.getJenisKelamin() + "', '" + user.getAlamat() + "', '" + user.getNoHp() + "')", false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String translateDayName(String hari) {
        switch (hari) {
            case "Monday" -> hari = "Senin";
            case "Tuesday" -> hari = "Selasa";
            case "Wednesday" -> hari = "Rabu";
            case "Thursday" -> hari = "Kamis";
            case "Friday" -> hari = "Jumat";
            case "Saturday" -> hari = "Sabtu";
            case "Sunday" -> hari = "Minggu";
            default -> hari = "Error!";
        }
        return hari;
    }

    private static String translateMonthName(String bulan) {
        switch (bulan) {
            case "January" -> bulan = "Januari";
            case "February" -> bulan = "Februari";
            case "March" -> bulan = "Maret";
            case "April" -> bulan = "April";
            case "May" -> bulan = "Mei";
            case "June" -> bulan = "Juni";
            case "July" -> bulan = "Juli";
            case "August" -> bulan = "Agustus";
            case "September" -> bulan = "September";
            case "October" -> bulan = "Oktober";
            case "November" -> bulan = "November";
            case "December" -> bulan = "Desember";
            default -> bulan = "Error!";
        }
        return bulan;
    }

    public static String getToday() {

        try {
            runQuery("SELECT CONCAT(DAYNAME(NOW()), ', ', DATE_FORMAT(NOW(), '%d %M %Y')) AS hari_tanggal", true);
            rs.next();
            String hariTanggal = rs.getString("hari_tanggal");
            
            String hari = hariTanggal.split(",")[0].trim();
            String tanggal = hariTanggal.split(",")[1].trim();
            hari = translateDayName(hari);
            String bulan = tanggal.split(" ")[1].trim();
            bulan = translateMonthName(bulan);

            return hari + ", " + tanggal.replace(tanggal.split(" ")[1].trim(), bulan);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String[][] fetchDataAntrian(boolean isFiltered) {

        try {

            String query;

            if(isFiltered)
                query = "SELECT nomor_antrian, "
                            + "nama_pasien, "
                            + "CONCAT(DAYNAME(tanggal_antri), ', ', DATE_FORMAT(tanggal_antri, '%d %M %Y')) AS tanggal "
                        + "FROM antrian a "
                        + "JOIN pasien p "
                            + "ON a.id_pasien = p.id_pasien "
                        + "WHERE tanggal_antri = CURDATE() ";

            else
                query = "SELECT nomor_antrian, "
                            + "nama_pasien, "
                            + "CONCAT(DAYNAME(tanggal_antri), ', ', DATE_FORMAT(tanggal_antri, '%d %M %Y')) AS tanggal "
                        + "FROM antrian a "
                        + "JOIN pasien p "
                            + "ON a.id_pasien = p.id_pasien "
                        + "ORDER BY tanggal_antri DESC";

            runQuery(query, true);

            rs.last();
            String[][] antrian = new String[rs.getRow()][3];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {

                antrian[i][0] = rs.getString("nomor_antrian");
                antrian[i][1] = rs.getString("nama_pasien");
                antrian[i][2] = rs.getString("tanggal");

                String hari = antrian[i][2].split(",")[0].trim();
                String tanggal = antrian[i][2].split(",")[1].trim();
                String bulan = tanggal.split(" ")[1].trim();
                hari = translateDayName(hari);
                bulan = translateMonthName(bulan);
                antrian[i][2] = hari + ", " + tanggal.replace(tanggal.split(" ")[1].trim(), bulan);

                i++;
            }

            return antrian;
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void hapusAntrian(int nomorAntrian) {
        try {
            runQuery("DELETE FROM antrian WHERE nomor_antrian = " + nomorAntrian, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String[][] fetchJadwalPraktik() {
        try {

            runQuery(
                "SELECT CONCAT(hari, ', ', jam_mulai, ' - ', jam_selesai) AS jam, "
                    + "nama_dokter, "
                    + "spesialis, "
                    + "no_hp, "
                    + "CONCAT('Rp', biaya_periksa) AS biaya_periksa "
                + "FROM jadwal_praktik jp "
                + "JOIN dokter d "
                    + "ON jp.id_dokter = d.id_dokter "
                    + "ORDER BY "
                        + "CASE hari "
                            + "WHEN 'Senin' THEN 1 "
                            + "WHEN 'Selasa' THEN 2 "
                            + "WHEN 'Rabu' THEN 3 "
                            + "WHEN 'Kamis' THEN 4 "
                            + "WHEN 'Jumat' THEN 5 "
                            + "WHEN 'Sabtu' THEN 6 "
                            + "WHEN 'Minggu' THEN 7 "
                        + "END, jam_mulai",
                true
            );

            rs.last();
            String[][] jadwal = new String[rs.getRow()][5];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {
                jadwal[i][0] = rs.getString("jam");
                jadwal[i][1] = rs.getString("nama_dokter");
                jadwal[i][2] = rs.getString("spesialis");
                jadwal[i][3] = rs.getString("no_hp");
                jadwal[i][4] = rs.getString("biaya_periksa");
                i++;
            }

            return jadwal;
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String[] fetchPasienComboBox() {

        try {

            runQuery("SELECT nama_pasien, id_pasien FROM pasien", true);

            rs.last();
            String[] pasien = new String[rs.getRow()];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {
                pasien[i] = rs.getString("nama_pasien") + " (" + rs.getString("id_pasien") + ")";
                i++;
            }

            return pasien;
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String[][] fetchRiwayatSaya(String id) {

        try {

            runQuery(
                "SELECT nama_pasien, "
                    + "deskripsi_keluhan, "
                    + "tanggal_antri "
                + "FROM pasien p "
                + "JOIN keluhan k "
                    + "ON p.id_pasien = k.id_pasien "
                + "JOIN antrian a "
                    + "ON k.nomor_antrian = a.nomor_antrian "
                + "WHERE p.id_pasien = '" + id + "'"
                + "ORDER BY tanggal_antri DESC",
                true
            );

            rs.last();
            String[][] riwayat = new String[rs.getRow()][3];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {
                riwayat[i][0] = rs.getString("nama_pasien");
                riwayat[i][1] = rs.getString("deskripsi_keluhan");
                riwayat[i][2] = rs.getString("tanggal_antri");
                i++;
            }

            return riwayat;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String[][] fetchRiwayatPasien() {

        try {

            runQuery(
                "SELECT nama_pasien, "
                    + "deskripsi_keluhan, "
                    + "tanggal_antri "
                + "FROM pasien p "
                + "JOIN keluhan k "
                    + "ON p.id_pasien = k.id_pasien "
                + "JOIN antrian a "
                    + "ON k.nomor_antrian = a.nomor_antrian "
                + "ORDER BY tanggal_antri DESC",
                true
            );

            rs.last();
            String[][] riwayat = new String[rs.getRow()][3];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {
                riwayat[i][0] = rs.getString("nama_pasien");
                riwayat[i][1] = rs.getString("deskripsi_keluhan");
                riwayat[i][2] = rs.getString("tanggal_antri");
                i++;
            }

            return riwayat;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static String[][] cariRiwayatKeluhan(String namaPasien) {

        try {

            runQuery(
                "SELECT nama_pasien, "
                    + "deskripsi_keluhan, "
                    + "tanggal_antri "
                + "FROM pasien p "
                + "JOIN keluhan k "
                    + "ON p.id_pasien = k.id_pasien "
                + "JOIN antrian a "
                    + "ON k.nomor_antrian = a.nomor_antrian "
                + "WHERE nama_pasien REGEXP '" + namaPasien + "' "
                + "ORDER BY tanggal_antri DESC",
                true
            );

            rs.last();
            String[][] riwayat = new String[rs.getRow()][3];

            rs.beforeFirst();
            int i = 0;
            while(rs.next()) {
                riwayat[i][0] = rs.getString("nama_pasien");
                riwayat[i][1] = rs.getString("deskripsi_keluhan");
                riwayat[i][2] = rs.getString("tanggal_antri");
                i++;
            }

            return riwayat;
        }

        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public static boolean updateDataPasien(String id, String nama, String noHp, String alamat) {

        try {

            runQuery(
                "UPDATE pasien "
                + "SET nama_pasien = '" + nama + "', "
                    + "no_hp = '" + noHp + "', "
                    + "alamat = '" + alamat + "' "
                + "WHERE id_pasien = '" + id + "'",
                false
            );

            JOptionPane.showMessageDialog(null, "Data profil berhasil diubah!");

            return true;
        }

        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    public static boolean tambahAntrian(String idPasien, String hari, String tanggalAntri) {

        try {

            runQuery(
                "INSERT INTO antrian (id_pasien, hari, tanggal_antri) "
                + "VALUES ('" + idPasien + "', '" + hari + "', '" + tanggalAntri + "')",
                false
            );

            JOptionPane.showMessageDialog(null, "Antrian berhasil ditambahkan!");

            return true;
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }
}