
USE db_godoc;


INSERT INTO akun (user_id, username, password, role) VALUES
('A001', 'admin', 'admin', 'Admin'),
('A002', 'admin_sehat', 'adminsehat333', 'Admin'),
('A003', 'admin_cerdas', 'admincerdas444', 'Admin'),
('A004', 'admin_kuat', 'adminkuat555', 'Admin'),
('A005', 'admin_setia', 'adminsetia666', 'Admin'),
('P001', 'andi001', '123', 'Pasien'),
('P002', 'buddy', 'abc', 'Pasien'),
('P003', 'candy', 'qwerty', 'Pasien'),
('P004', 'dani3L', 'wasd456', 'Pasien'),
('P005', 'ek4', 'aiueo', 'Pasien'),
('P006', 'fahmi006', 'fahmi456', 'Pasien'),
('P007', 'gita007', 'gita789', 'Pasien'),
('P008', 'heri008', 'heri123', 'Pasien'),
('P009', 'indah009', 'indah456', 'Pasien'),
('P010', 'joko10', 'joko789', 'Pasien');

INSERT INTO pasien (id_pasien, nama_pasien, jenis_kelamin, alamat, no_hp) VALUES
("P001", "Andi", "L", "Jl. Merdeka No. 1, Jakarta", "081234567891"),
("P002", "Budi", "L", "Jl. Gatot Subroto No. 2, Jakarta", "081234567892"),
("P003", "Cindy", "P", "Jl. Jenderal Sudirman No. 3, Jakarta", "081234567893"),
("P004", "Dani", "L", "Jl. MH Thamrin No. 4, Jakarta", "081234567894"),
("P005", "Eka", "P", "Jl. Prof. Dr. Satrio No. 5, Jakarta", "081234567895"),
("P006", "Fahmi", "L", "Jl. Jenderal Gatot Subroto No. 6, Jakarta", "081234567896"),
("P007", "Gita", "P", "Jl. HR Rasuna Said No. 7, Jakarta", "081234567897"),
("P008", "Heri", "L", "Jl. Sudirman No. 8, Jakarta", "081234567898"),
("P009", "Indah", "P", "Jl. Jenderal Gatot Subroto Barat No. 9, Jakarta", "081234567899"),
("P010", "Joko", "L", "Jl. Jenderal Soedirman No. 10, Jakarta", "081234567891");

INSERT INTO dokter (id_dokter, nama_dokter, spesialis, biaya_periksa, jenis_kelamin, alamat, no_hp) VALUES
('D001', 'Dr. Ahmad', 'Gigi', 100000, 'L', 'Jl. Raya Cibubur No. 56, Jakarta Timur', '081234567891'),
('D002', 'Dr. Rina', 'Jantung', 150000, 'P', 'Jl. Raya Cinere No. 45, Jakarta Selatan', '081234567892'),
('D003', 'Dr. Aris', 'Mata', 120000, 'L', 'Jl. Raya Bogor No. 56, Jakarta Barat', '081234567893'),
('D004', 'Dr. Dina', 'THT', 125000, 'P', 'Jl. Raya Bekasi No. 32, Jakarta Timur', '081234567894'),
('D005', 'Dr. Fikri', 'Orthopedi', 145000, 'L', 'Jl. Raya Tangerang No. 23, Jakarta Selatan', '081234567895'),
('D006', 'Dr. Shinta', 'Anak', 135000, 'P', 'Jl. Raya Depok No. 12, Jakarta Barat', '081234567896'),
('D007', 'Dr. Arief', 'Bedah', 200000, 'L', 'Jl. Raya Bintaro No. 34, Jakarta Selatan', '081234567897'),
('D008', 'Dr. Rani', 'Penyakit Dalam', 185000, 'P', 'Jl. Raya Cirebon No. 67, Jakarta Barat', '081234567898'),
('D009', 'Dr. Adi', 'Kulit & Kelamin', 190000, 'L', 'Jl. Raya Purwakarta No. 89, Jakarta Timur', '081234567899'),
('D010', 'Dr. Yulia', 'Syaraf', 170000, 'P', 'Jl. Raya Karawang No. 45, Jakarta Barat', '08123456789');

INSERT INTO jadwal_praktik (id_dokter, hari, jam_mulai, jam_selesai) VALUES
('D001', 'Senin', '08:00:00', '11:00:00'),
('D002', 'Senin', '12:00:00', '15:00:00'),
('D003', 'Senin', '14:00:00', '17:00:00'),
('D004', 'Selasa', '08:00:00', '11:00:00'),
('D005', 'Selasa', '12:00:00', '15:00:00'),
('D001', 'Rabu', '08:00:00', '11:00:00'),
('D002', 'Rabu', '12:00:00', '15:00:00'),
('D006', 'Kamis', '08:00:00', '11:00:00'),
('D005', 'Jumat', '08:00:00', '11:00:00'),
('D007', 'Sabtu', '08:00:00', '11:00:00');

INSERT INTO antrian (id_pasien, hari, tanggal_antri) VALUES
('P001', 'Senin', '2022-12-26'),
('P002', 'Selasa', '2022-12-27'),
('P003', 'Rabu', '2022-12-28'),
('P004', 'Kamis', '2022-12-29'),
('P005', 'Jumat', '2022-12-30'),
('P006', 'Sabtu', '2022-12-31'),
('P007', 'Minggu', '2023-01-01'),
('P008', 'Senin', '2023-01-02'),
('P009', 'Selasa', '2023-01-03'),
('P010', 'Rabu', '2023-01-04'),
('P001', 'Kamis', '2023-01-05'),
('P002', 'Jumat', '2023-01-06'),
('P003', 'Sabtu', '2023-01-07'),
('P004', 'Minggu', '2023-01-08'),
('P005', 'Senin', '2023-01-09'),
('P006', 'Selasa', '2023-01-10'),
('P007', 'Rabu', '2023-01-11'),
('P008', 'Kamis', '2023-01-12'),
('P009', 'Jumat', '2023-01-13'),
('P010', 'Sabtu', '2023-01-14');

INSERT INTO keluhan (id_pasien, nomor_antrian, id_dokter, deskripsi_keluhan) VALUES
('P001', 1, 'D001', 'Sakit gigi'),
('P002', 2, 'D002', 'Sakit jantung'),
('P003', 3, 'D003', 'Buta mata'),
('P004', 4, 'D004', 'Sakit telinga'),
('P005', 5, 'D005', 'Sakit tulang'),
('P006', 6, 'D006', 'Masalah pada anak'),
('P007', 7, 'D007', 'Perlu operasi'),
('P008', 8, 'D008', 'Sakit dalam'),
('P009', 9, 'D009', 'Sakit kulit'),
('P010', 10, 'D010', 'Masalah syaraf'),
('P001', 11, 'D003', 'Buta mata'),
('P002', 12, 'D005', 'Sakit tulang'),
('P003', 13, 'D008', 'Sakit dalam'),
('P004', 14, 'D001', 'Sakit gigi'),
('P005', 15, 'D004', 'Sakit telinga'),
('P006', 16, 'D002', 'Sakit jantung'),
('P007', 17, 'D006', 'Masalah pada anak'),
('P008', 18, 'D009', 'Sakit kulit'),
('P009', 19, 'D007', 'Perlu operasi'),
('P010', 20, 'D010', 'Masalah syaraf');
