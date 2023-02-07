package com.sicompany.godoc.accounts;

import com.sicompany.godoc.connections.DbConnection;
import java.util.ArrayList;


/**
 * 
 * @author Kelompok 2
 */
public class Pasien extends User {

    private String namaLengkap;
    private String jenisKelamin;
    private String noHp;        
    private String alamat;      

    public Pasien(String id, String username, String password) {
        super(id, username, password);
        this.setRole(Roles.PASIEN);
    }

    public void fetchPasienData() {
        String[] data = DbConnection.fetchPasienData(this.getId());

        this.setNamaLengkap(data[1]);
        this.setJenisKelamin(data[2]);
        this.setNoHp(data[3]);
        this.setAlamat(data[4]);
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
