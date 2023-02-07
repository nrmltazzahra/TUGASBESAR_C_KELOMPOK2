package com.sicompany.godoc.screens;

import com.sicompany.godoc.accounts.Admin;
import com.sicompany.godoc.accounts.Pasien;
import com.sicompany.godoc.accounts.Roles;
import com.sicompany.godoc.accounts.User;
import com.sicompany.godoc.connections.DbConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * 
 * @author Kelompok 2
 */
public class MainScreen extends javax.swing.JFrame {

    /**
     * 
     * @param user 
     */
    public MainScreen(User user) {

        initComponents();

        String[] pasienData = DbConnection.fetchPasienComboBox();
        DefaultComboBoxModel pasienComboBoxModel = new DefaultComboBoxModel();
        for(int i = 0; i < pasienData.length; i++) {
            pasienComboBoxModel.addElement(pasienData[i]);
        }
        pasienComboBox.setModel(pasienComboBoxModel);
        
        if(user.getRole() == Roles.ADMIN) {
            currentUser = new Admin(user.getId(), user.getUsername(), user.getPassword());
            tabsContainer.removeTabAt(3);
        }

        else if(user.getRole() == Roles.PASIEN) {

            currentUser = new Pasien(user.getId(), user.getUsername(), user.getPassword());
            currentUser.fetchPasienData();

            antrianCaptionLabel.setVisible(false);
            hapusAntrianButton.setVisible(false);

            searchField.setVisible(false);
            searchButton.setVisible(false);

            riwayatTitleLabel.setText("Riwayat Keluhan Saya");

            TableColumn kolomPasien = riwayatTable.getColumnModel().getColumn(0);
            riwayatTable.getColumnModel().removeColumn(kolomPasien);

            for(int i = 0; i < pasienComboBox.getItemCount(); i++) {
                String pasien = pasienComboBox.getItemAt(i);
                String id = pasien.replaceFirst(".*\\((.*)\\).*", "$1");

                if(id.equals(currentUser.getId())) {
                    pasienComboBox.setSelectedIndex(i);
                    pasienComboBox.setEnabled(false);
                    break;
                }
            }

            uidTextField.setText(currentUser.getId());
            namaLengkapTextField.setText(((Pasien) currentUser).getNamaLengkap());
            jenisKelaminField.setText(((Pasien) currentUser).getJenisKelamin());
            noHpTextField.setText(((Pasien) currentUser).getNoHp());
            alamatTextArea.setText(((Pasien) currentUser).getAlamat());
        }

        else {

            JOptionPane.showMessageDialog(null, "User tidak dikenali! Menutup aplikasi...", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("User tidak dikenali! Menutup aplikasi...");
            
            System.exit(0);
        }

        String today = DbConnection.getToday();
        tanggalHariIniLabel.setText(today);

        updateAntrianTable();
        updateJadwalDokterTable();
        updateRiwayatTable();
    }

    /**
     * 
     */
    private void updateAntrianTable() {

        String[][] data = DbConnection.fetchDataAntrian(filterHariCheckbox.isSelected());
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        antrianTable.setModel(model);

        model.addColumn("No. Antrian");
        model.addColumn("Nama Pasien");
        model.addColumn("Tanggal Antrian");

        for(int i = 0; i < data.length; i++) {
            model.addRow(new Object[]{data[i][0], data[i][1], data[i][2]});
        }
    }

    /**
     * 
     */
    private void updateJadwalDokterTable() {

        String[][] data = DbConnection.fetchJadwalPraktik();

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jadwalTable.setModel(model);

        model.addColumn("Jam Praktik");
        model.addColumn("Nama Dokter");
        model.addColumn("Spesialis");
        model.addColumn("Nomor Telepon");
        model.addColumn("Biaya Periksa");

        for(int i = 0; i < data.length; i++) {
            model.addRow(new Object[]{data[i][0], data[i][1], data[i][2], data[i][3], data[i][4]});
        }
    }

    /**
     * 
     */
    private void updateRiwayatTable() {

        String[][] data = null;

        if(currentUser.getRole() == Roles.PASIEN)
            data = DbConnection.fetchRiwayatSaya(currentUser.getId());

        else if(currentUser.getRole() == Roles.ADMIN) {
            data = DbConnection.fetchRiwayatPasien();
        }

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        riwayatTable.setModel(model);

        if(currentUser.getRole() == Roles.ADMIN)
            model.addColumn("Nama Pasien");

        model.addColumn("Keluhan");
        model.addColumn("Tanggal Diagnosa");

        for(int i = 0; i < data.length; i++) {
            if(currentUser.getRole() == Roles.ADMIN)
                model.addRow(new Object[]{data[i][0], data[i][1], data[i][2]});
            else
                model.addRow(new Object[]{data[i][1], data[i][2]});
        }
    }

    /**
     * 
     * 
     * 
     */
    public static String getNearestDay(String hari) {

        switch(hari) {
            case "Senin" -> hari = "Monday";
            case "Selasa" -> hari = "Tuesday";
            case "Rabu" -> hari = "Wednesday";
            case "Kamis" -> hari = "Thursday";
            case "Jumat" -> hari = "Friday";
            case "Sabtu" -> hari = "Saturday";
            case "Minggu" -> hari = "Sunday";           
        }
        System.out.println(hari);

        LocalDate today = LocalDate.now();
        LocalDate nearestDay = today;
        while(!nearestDay.getDayOfWeek().toString().equalsIgnoreCase(hari)) {
            
            if(nearestDay.isAfter(LocalDate.parse("2052-01-01"))) {
                JOptionPane.showMessageDialog(null, "Kesalahan internal! Silahkan hubungi developer.");
                return null;
            }
            nearestDay = nearestDay.plusDays(1);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return nearestDay.format(formatter);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabsContainer = new javax.swing.JTabbedPane();
        antrianPanel = new javax.swing.JPanel();
        AntrianTitleLabel = new javax.swing.JLabel();
        filterHariCheckbox = new javax.swing.JCheckBox();
        todayLabel = new javax.swing.JLabel();
        tanggalHariIniLabel = new javax.swing.JLabel();
        antrianScrollPanel = new javax.swing.JScrollPane();
        antrianTable = new javax.swing.JTable();
        buatAntrianButton = new javax.swing.JButton();
        hapusAntrianButton = new javax.swing.JButton();
        antrianCaptionLabel = new javax.swing.JLabel();
        antrianBaruPanel = new javax.swing.JPanel();
        antrianBaruTitleLabel = new javax.swing.JLabel();
        jadwalScrollContainer = new javax.swing.JScrollPane();
        jadwalTable = new javax.swing.JTable();
        formPengisianTitleLabel = new javax.swing.JLabel();
        jadwalTerpilihLabel = new javax.swing.JLabel();
        jadwalTerpilihTextField = new javax.swing.JTextField();
        pilihJadwalCaptionLabel = new javax.swing.JLabel();
        biayaPeriksaLabel = new javax.swing.JLabel();
        biayaPeriksaTextField = new javax.swing.JTextField();
        biayaCaptionLabel = new javax.swing.JLabel();
        pasienLabel = new javax.swing.JLabel();
        pasienComboBox = new javax.swing.JComboBox<>();
        pasienCaptionLabel = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        divider = new javax.swing.JSeparator();
        riwayatKeluhanPanel = new javax.swing.JPanel();
        riwayatTitleLabel = new javax.swing.JLabel();
        riwayatScrollContainer = new javax.swing.JScrollPane();
        riwayatTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        profilePanel = new javax.swing.JPanel();
        ProfileTitleLabel = new javax.swing.JLabel();
        uidLabel = new javax.swing.JLabel();
        uidTextField = new javax.swing.JTextField();
        uidCaptionLabel = new javax.swing.JLabel();
        namaLengkapTextField = new javax.swing.JTextField();
        ubahNamaLengkapButton = new javax.swing.JButton();
        namaLengkapLabel = new javax.swing.JLabel();
        namaLengkapCaptionLabel = new javax.swing.JLabel();
        jenisKelaminField = new javax.swing.JTextField();
        jenisKelaminCaptionLabel = new javax.swing.JLabel();
        jenisKelaminLabel = new javax.swing.JLabel();
        noHpCaptionLabel = new javax.swing.JLabel();
        noHpLabel = new javax.swing.JLabel();
        ubahNoHpButton = new javax.swing.JButton();
        noHpTextField = new javax.swing.JTextField();
        alamatCaptionLabel = new javax.swing.JLabel();
        alamatScrollContainer = new javax.swing.JScrollPane();
        alamatTextArea = new javax.swing.JTextArea();
        ubahAlamatButton = new javax.swing.JButton();
        alamatLabel = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();
        simpanButton = new javax.swing.JButton();
        logoutPanel = new javax.swing.JPanel();
        logoutButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GoDoc");

        tabsContainer.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsContainer.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabsContainer.setName(""); // NOI18N

        AntrianTitleLabel.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        AntrianTitleLabel.setText(" Daftar Antrian");

        filterHariCheckbox.setText("Tampilkan antrian hari ini saja");
        filterHariCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterHariCheckboxActionPerformed(evt);
            }
        });

        todayLabel.setForeground(new java.awt.Color(153, 153, 153));
        todayLabel.setText("Hari ini:");

        tanggalHariIniLabel.setText("(Hari ini, Tgl-Bulan-Tahun)");
        tanggalHariIniLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        antrianTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nomor Antrian", "Nama Pasien", "Tanggal Antrian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        antrianScrollPanel.setViewportView(antrianTable);

        buatAntrianButton.setText("Buat Antrian Baru");
        buatAntrianButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buatAntrianButtonActionPerformed(evt);
            }
        });

        hapusAntrianButton.setText("Hapus Antrian");
        hapusAntrianButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusAntrianButtonActionPerformed(evt);
            }
        });

        antrianCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        antrianCaptionLabel.setText("Klik data di atas untuk memilihnya, lalu klik tombol \"Hapus\" di bawah jika perlu.");

        javax.swing.GroupLayout antrianPanelLayout = new javax.swing.GroupLayout(antrianPanel);
        antrianPanel.setLayout(antrianPanelLayout);
        antrianPanelLayout.setHorizontalGroup(
            antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antrianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(antrianScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, antrianPanelLayout.createSequentialGroup()
                        .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filterHariCheckbox)
                            .addComponent(AntrianTitleLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(todayLabel)
                            .addComponent(tanggalHariIniLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, antrianPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(hapusAntrianButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buatAntrianButton))
                    .addGroup(antrianPanelLayout.createSequentialGroup()
                        .addComponent(antrianCaptionLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        antrianPanelLayout.setVerticalGroup(
            antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antrianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AntrianTitleLabel)
                    .addComponent(todayLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterHariCheckbox)
                    .addComponent(tanggalHariIniLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(antrianScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(antrianCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(antrianPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buatAntrianButton)
                    .addComponent(hapusAntrianButton))
                .addContainerGap())
        );

        tabsContainer.addTab("Daftar Antrian", antrianPanel);

        antrianBaruTitleLabel.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        antrianBaruTitleLabel.setText("Jadwal Praktik Dokter");

        jadwalTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Waktu", "Nama Dokter", "Spesialis", "Nomor Telepon", "Biaya (Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jadwalTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jadwalTableMouseClicked(evt);
            }
        });
        jadwalScrollContainer.setViewportView(jadwalTable);

        formPengisianTitleLabel.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        formPengisianTitleLabel.setText("Form Pengisian Antrian Baru");

        jadwalTerpilihLabel.setText("Jadwal Terpilih");

        jadwalTerpilihTextField.setEditable(false);
        jadwalTerpilihTextField.setText("Klik tabel jadwal praktik di atas untuk memilih jadwal");

        pilihJadwalCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        pilihJadwalCaptionLabel.setText("Isi form akan otomatis berubah sesuai data yang Anda klik.");

        biayaPeriksaLabel.setText("Biaya Periksa");

        biayaPeriksaTextField.setEditable(false);
        biayaPeriksaTextField.setText("Rp.0");

        biayaCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        biayaCaptionLabel.setText("Biaya yang harus Anda keluarkan untuk diperiksa oleh dokter ini.");

        pasienLabel.setText("Pasien");

        pasienComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama Lengkap (ID Pasien)" }));

        pasienCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        pasienCaptionLabel.setText("Antrian ini dibuat atas nama siapa.");

        submitButton.setText("Buat Antrian Baru");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout antrianBaruPanelLayout = new javax.swing.GroupLayout(antrianBaruPanel);
        antrianBaruPanel.setLayout(antrianBaruPanelLayout);
        antrianBaruPanelLayout.setHorizontalGroup(
            antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antrianBaruPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(divider, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jadwalScrollContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, antrianBaruPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(submitButton))
                    .addGroup(antrianBaruPanelLayout.createSequentialGroup()
                        .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jadwalTerpilihLabel)
                            .addComponent(biayaPeriksaLabel)
                            .addComponent(pasienLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(biayaPeriksaTextField)
                            .addComponent(pasienComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(antrianBaruPanelLayout.createSequentialGroup()
                                .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pasienCaptionLabel)
                                    .addComponent(biayaCaptionLabel)
                                    .addComponent(pilihJadwalCaptionLabel))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jadwalTerpilihTextField)))
                    .addGroup(antrianBaruPanelLayout.createSequentialGroup()
                        .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(antrianBaruTitleLabel)
                            .addComponent(formPengisianTitleLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        antrianBaruPanelLayout.setVerticalGroup(
            antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antrianBaruPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(antrianBaruTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jadwalScrollContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(formPengisianTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jadwalTerpilihTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jadwalTerpilihLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pilihJadwalCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(biayaPeriksaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(biayaPeriksaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(biayaCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(antrianBaruPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pasienComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pasienLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pasienCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(divider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(submitButton)
                .addContainerGap())
        );

        tabsContainer.addTab("Buat Antrian Baru", antrianBaruPanel);

        riwayatTitleLabel.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        riwayatTitleLabel.setText("Riwayat Keluhan");

        riwayatTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nama Pasien", "Keluhan", "Waktu Diagnosa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        riwayatScrollContainer.setViewportView(riwayatTable);

        refreshButton.setText("↻  Muat Ulang");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        searchField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        searchField.setText("Pencarian");

        searchButton.setText("Cari Pasien");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout riwayatKeluhanPanelLayout = new javax.swing.GroupLayout(riwayatKeluhanPanel);
        riwayatKeluhanPanel.setLayout(riwayatKeluhanPanelLayout);
        riwayatKeluhanPanelLayout.setHorizontalGroup(
            riwayatKeluhanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(riwayatKeluhanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(riwayatKeluhanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(riwayatScrollContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addGroup(riwayatKeluhanPanelLayout.createSequentialGroup()
                        .addComponent(riwayatTitleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(riwayatKeluhanPanelLayout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)))
                .addContainerGap())
        );
        riwayatKeluhanPanelLayout.setVerticalGroup(
            riwayatKeluhanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(riwayatKeluhanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(riwayatTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(riwayatKeluhanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshButton)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(riwayatScrollContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsContainer.addTab("Riwayat Keluhan", riwayatKeluhanPanel);

        ProfileTitleLabel.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        ProfileTitleLabel.setText("Profil Saya");

        uidLabel.setText("User ID");

        uidTextField.setEditable(false);

        uidCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        uidCaptionLabel.setText("User ID tidak akan pernah dapat diubah.");

        namaLengkapTextField.setEditable(false);

        ubahNamaLengkapButton.setText("Ubah");
        ubahNamaLengkapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahNamaLengkapButtonActionPerformed(evt);
            }
        });

        namaLengkapLabel.setText("Nama Lengkap");

        namaLengkapCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        namaLengkapCaptionLabel.setText("Klik tombol di kanan untuk mengubah nama.");

        jenisKelaminField.setEditable(false);

        jenisKelaminCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        jenisKelaminCaptionLabel.setText("Hubungi Admin untuk mengubah jenis kelamin Anda.");

        jenisKelaminLabel.setText("Jenis Kelamin");

        noHpCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        noHpCaptionLabel.setText("Hanya masukan nomor yang masih aktif.");

        noHpLabel.setText("Nomor HP");

        ubahNoHpButton.setText("Ubah");
        ubahNoHpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahNoHpButtonActionPerformed(evt);
            }
        });

        noHpTextField.setEditable(false);

        alamatCaptionLabel.setForeground(new java.awt.Color(153, 153, 153));
        alamatCaptionLabel.setText("Pastikan alamat ini adalah tempat tinggal Anda.");

        alamatTextArea.setEditable(false);
        alamatTextArea.setColumns(20);
        alamatTextArea.setRows(5);
        alamatScrollContainer.setViewportView(alamatTextArea);

        ubahAlamatButton.setText("Ubah");
        ubahAlamatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahAlamatButtonActionPerformed(evt);
            }
        });

        alamatLabel.setText("Alamat");

        simpanButton.setText("Simpan Perubahan");
        simpanButton.setEnabled(false);
        simpanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout profilePanelLayout = new javax.swing.GroupLayout(profilePanel);
        profilePanel.setLayout(profilePanelLayout);
        profilePanelLayout.setHorizontalGroup(
            profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, profilePanelLayout.createSequentialGroup()
                        .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uidLabel)
                            .addComponent(namaLengkapLabel)
                            .addComponent(jenisKelaminLabel)
                            .addComponent(noHpLabel)
                            .addComponent(alamatLabel))
                        .addGap(18, 18, 18)
                        .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jenisKelaminField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(uidTextField)
                            .addGroup(profilePanelLayout.createSequentialGroup()
                                .addComponent(namaLengkapTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ubahNamaLengkapButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profilePanelLayout.createSequentialGroup()
                                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(noHpTextField)
                                    .addComponent(alamatScrollContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ubahNoHpButton)
                                    .addComponent(ubahAlamatButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(profilePanelLayout.createSequentialGroup()
                                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jenisKelaminCaptionLabel)
                                    .addComponent(namaLengkapCaptionLabel)
                                    .addComponent(uidCaptionLabel)
                                    .addComponent(noHpCaptionLabel)
                                    .addComponent(alamatCaptionLabel))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, profilePanelLayout.createSequentialGroup()
                        .addComponent(ProfileTitleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(profilePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(simpanButton))
                    .addComponent(separator))
                .addContainerGap())
        );
        profilePanelLayout.setVerticalGroup(
            profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ProfileTitleLabel)
                .addGap(18, 18, 18)
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uidLabel)
                    .addComponent(uidTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uidCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namaLengkapTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ubahNamaLengkapButton)
                    .addComponent(namaLengkapLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namaLengkapCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jenisKelaminField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jenisKelaminLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jenisKelaminCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noHpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(noHpLabel)
                    .addComponent(ubahNoHpButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noHpCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(profilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(alamatScrollContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ubahAlamatButton))
                    .addComponent(alamatLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamatCaptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simpanButton)
                .addContainerGap())
        );

        tabsContainer.addTab("Profil Saya", profilePanel);

        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Keluar dari akun Anda dan kembali ke layar login?");

        javax.swing.GroupLayout logoutPanelLayout = new javax.swing.GroupLayout(logoutPanel);
        logoutPanel.setLayout(logoutPanelLayout);
        logoutPanelLayout.setHorizontalGroup(
            logoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutPanelLayout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addGroup(logoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutPanelLayout.createSequentialGroup()
                        .addComponent(logoutButton)
                        .addGap(196, 196, 196))))
        );
        logoutPanelLayout.setVerticalGroup(
            logoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutPanelLayout.createSequentialGroup()
                .addContainerGap(226, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logoutButton)
                .addGap(231, 231, 231))
        );

        tabsContainer.addTab("Keluar", logoutPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsContainer)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsContainer)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        
        JOptionPane.showMessageDialog(this, "Sampai jumpa, " + currentUser.getUsername() + "!", "Logout Berhasil", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Logout berhasil");

        this.dispose();
        new LoginScreen().setVisible(true);

    }//GEN-LAST:event_logoutButtonActionPerformed

    private void simpanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanButtonActionPerformed
        
        String nama = namaLengkapTextField.getText();
        String noHp = noHpTextField.getText();
        String alamat = alamatTextArea.getText();

        if(nama.isEmpty() || noHp.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan isi semua form!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(DbConnection.updateDataPasien(currentUser.getId(), nama, noHp, alamat)) {

            namaLengkapTextField.setEditable(false);
            noHpTextField.setEditable(false);
            alamatTextArea.setEditable(false);
            ubahNamaLengkapButton.setEnabled(true);
            ubahNoHpButton.setEnabled(true);
            ubahAlamatButton.setEnabled(true);
            simpanButton.setEnabled(false);

        } else {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui data pasien!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateAntrianTable();
    }//GEN-LAST:event_simpanButtonActionPerformed


    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        searchField.setText("");
        updateRiwayatTable();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void ubahNamaLengkapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahNamaLengkapButtonActionPerformed

        namaLengkapTextField.setEditable(true);
        ubahNamaLengkapButton.setEnabled(false);
        simpanButton.setEnabled(true);
    }//GEN-LAST:event_ubahNamaLengkapButtonActionPerformed

    private void ubahNoHpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahNoHpButtonActionPerformed

        noHpTextField.setEditable(true);
        ubahNoHpButton.setEnabled(false);
        simpanButton.setEnabled(true);
    }//GEN-LAST:event_ubahNoHpButtonActionPerformed

    private void ubahAlamatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahAlamatButtonActionPerformed

        alamatTextArea.setEditable(true);
        ubahAlamatButton.setEnabled(false);
        simpanButton.setEnabled(true);

    }//GEN-LAST:event_ubahAlamatButtonActionPerformed

    private void hapusAntrianButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusAntrianButtonActionPerformed

        int selectedRow = antrianTable.getSelectedRow();

        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih antrian yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int nomorAntrian = Integer.parseInt((String) antrianTable.getValueAt(selectedRow, 0));
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus antrian nomor " + nomorAntrian + "?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION) {
            try {
                DbConnection.hapusAntrian(nomorAntrian);
                JOptionPane.showMessageDialog(this, "Antrian nomor " + nomorAntrian + " berhasil dihapus!", "Penghapusan Berhasil", JOptionPane.INFORMATION_MESSAGE);
                updateAntrianTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus antrian!", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Terjadi kesalahan saat menghapus antrian: " + ex.getMessage());
            }
        }

    }//GEN-LAST:event_hapusAntrianButtonActionPerformed


    private void buatAntrianButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buatAntrianButtonActionPerformed

        tabsContainer.setSelectedIndex(1);
    }//GEN-LAST:event_buatAntrianButtonActionPerformed


    private void filterHariCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterHariCheckboxActionPerformed
        updateAntrianTable();
    }//GEN-LAST:event_filterHariCheckboxActionPerformed


    private void jadwalTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jadwalTableMouseClicked
        String namaDokter = (String) jadwalTable.getValueAt(jadwalTable.getSelectedRow(), 1);
        String jadwalPraktik = (String) jadwalTable.getValueAt(jadwalTable.getSelectedRow(), 0);
        String biaya = (String) jadwalTable.getValueAt(jadwalTable.getSelectedRow(), 4);

        jadwalTerpilihTextField.setText(namaDokter + " (" + jadwalPraktik + ")");
        biayaPeriksaTextField.setText(biaya);
        submitButton.setEnabled(true);
        
    }//GEN-LAST:event_jadwalTableMouseClicked


    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed

        String pasien = pasienComboBox.getSelectedItem().toString();
        String idPasien = pasien.replaceFirst(".*\\((.*)\\).*", "$1");
        String[] jadwal = jadwalTable.getValueAt(jadwalTable.getSelectedRow(), 0).toString().split(",");
        String hari = jadwal[0];
        System.out.println(hari);


        String tanggalAntri = getNearestDay(hari);

        DbConnection.tambahAntrian(idPasien, hari, tanggalAntri);
        updateAntrianTable();
        updateRiwayatTable();

        String[] pasienData = DbConnection.fetchPasienComboBox();
        DefaultComboBoxModel pasienComboBoxModel = new DefaultComboBoxModel();
        for(int i = 0; i < pasienData.length; i++) {
            pasienComboBoxModel.addElement(pasienData[i]);
        }
        pasienComboBox.setModel(pasienComboBoxModel);

        tabsContainer.setSelectedIndex(0);

    }//GEN-LAST:event_submitButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        
        String namaPasien = searchField.getText();
        
        if(namaPasien.isEmpty()) {
            updateRiwayatTable();
            return;
        }


        String[][] data = DbConnection.cariRiwayatKeluhan(namaPasien);

        if(data.length == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data keluhan yang ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        riwayatTable.setModel(model);

        if(currentUser.getRole() == Roles.ADMIN)
            model.addColumn("Nama Pasien");

        model.addColumn("Keluhan");
        model.addColumn("Tanggal Diagnosa");

        for(int i = 0; i < data.length; i++) {
            model.addRow(new Object[]{data[i][0], data[i][1], data[i][2]});
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private static User currentUser;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AntrianTitleLabel;
    private javax.swing.JLabel ProfileTitleLabel;
    private javax.swing.JLabel alamatCaptionLabel;
    private javax.swing.JLabel alamatLabel;
    private javax.swing.JScrollPane alamatScrollContainer;
    private javax.swing.JTextArea alamatTextArea;
    private javax.swing.JPanel antrianBaruPanel;
    private javax.swing.JLabel antrianBaruTitleLabel;
    private javax.swing.JLabel antrianCaptionLabel;
    private javax.swing.JPanel antrianPanel;
    private javax.swing.JScrollPane antrianScrollPanel;
    private javax.swing.JTable antrianTable;
    private javax.swing.JLabel biayaCaptionLabel;
    private javax.swing.JLabel biayaPeriksaLabel;
    private javax.swing.JTextField biayaPeriksaTextField;
    private javax.swing.JButton buatAntrianButton;
    private javax.swing.JSeparator divider;
    private javax.swing.JCheckBox filterHariCheckbox;
    private javax.swing.JLabel formPengisianTitleLabel;
    private javax.swing.JButton hapusAntrianButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jadwalScrollContainer;
    private javax.swing.JTable jadwalTable;
    private javax.swing.JLabel jadwalTerpilihLabel;
    private javax.swing.JTextField jadwalTerpilihTextField;
    private javax.swing.JLabel jenisKelaminCaptionLabel;
    private javax.swing.JTextField jenisKelaminField;
    private javax.swing.JLabel jenisKelaminLabel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JPanel logoutPanel;
    private javax.swing.JLabel namaLengkapCaptionLabel;
    private javax.swing.JLabel namaLengkapLabel;
    private javax.swing.JTextField namaLengkapTextField;
    private javax.swing.JLabel noHpCaptionLabel;
    private javax.swing.JLabel noHpLabel;
    private javax.swing.JTextField noHpTextField;
    private javax.swing.JLabel pasienCaptionLabel;
    private javax.swing.JComboBox<String> pasienComboBox;
    private javax.swing.JLabel pasienLabel;
    private javax.swing.JLabel pilihJadwalCaptionLabel;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel riwayatKeluhanPanel;
    private javax.swing.JScrollPane riwayatScrollContainer;
    private javax.swing.JTable riwayatTable;
    private javax.swing.JLabel riwayatTitleLabel;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JSeparator separator;
    private javax.swing.JButton simpanButton;
    private javax.swing.JButton submitButton;
    private javax.swing.JTabbedPane tabsContainer;
    private javax.swing.JLabel tanggalHariIniLabel;
    private javax.swing.JLabel todayLabel;
    private javax.swing.JButton ubahAlamatButton;
    private javax.swing.JButton ubahNamaLengkapButton;
    private javax.swing.JButton ubahNoHpButton;
    private javax.swing.JLabel uidCaptionLabel;
    private javax.swing.JLabel uidLabel;
    private javax.swing.JTextField uidTextField;
    // End of variables declaration//GEN-END:variables
}
