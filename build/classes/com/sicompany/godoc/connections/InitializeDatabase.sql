
CREATE DATABASE db_godoc;
USE db_godoc;



CREATE TABLE pasien (
    id_pasien VARCHAR(5) PRIMARY KEY NOT NULL,              
    nama_pasien VARCHAR(64),                                
    jenis_kelamin ENUM('L', 'P'),                           
    alamat VARCHAR(255),                                    
    no_hp VARCHAR(12)                                       
);


CREATE TABLE dokter (
    id_dokter VARCHAR(5) PRIMARY KEY NOT NULL,              
    nama_dokter VARCHAR(64),                                
    spesialis VARCHAR(64),                                  
    biaya_periksa INT,                                      
    jenis_kelamin ENUM('L', 'P'),                           
    alamat VARCHAR(255),                                    
    no_hp VARCHAR(12)                                       
);


CREATE TABLE akun (
    user_id VARCHAR(5) PRIMARY KEY NOT NULL,                
    username VARCHAR(64),                                  
    password VARCHAR(255),                                  
    role ENUM('Admin', 'Pasien')                            
);


CREATE TABLE jadwal_praktik (
    id_dokter VARCHAR(5),                                                               
    hari ENUM('Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu'),          
    jam_mulai TIME,                                                                     
    jam_selesai TIME,                                                                   
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter)
);


CREATE TABLE antrian (
    nomor_antrian INT PRIMARY KEY NOT NULL AUTO_INCREMENT,                          
    id_pasien VARCHAR(5),                                                           
    hari ENUM('Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu'),      
    tanggal_antri DATE,                                                             
    FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien)
);


CREATE TABLE keluhan (
    id_keluhan INT PRIMARY KEY NOT NULL AUTO_INCREMENT,         
    id_pasien VARCHAR(5),                                       
    nomor_antrian INT,                                          
    id_dokter VARCHAR(5),                                       
    deskripsi_keluhan VARCHAR(255),                             
    FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter)
);

------------------------------------------------------------------------------------------------------------