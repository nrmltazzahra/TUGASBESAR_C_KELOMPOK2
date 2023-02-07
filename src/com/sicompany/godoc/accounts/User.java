package com.sicompany.godoc.accounts;

import com.sicompany.godoc.connections.DbConnection;

/**
 * 
 * @author Kelompok 2
 */
public class User {
    private String id;
    private String username;
    private String password;
    private Roles role;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public boolean isRegistered() {
        return DbConnection.login(this.username, this.password);
    }

    public void fetchData() {

        String[] data = DbConnection.fetchUserData(this.username);
        this.setId(data[0]);
        
        if(this.getId().charAt(0) == 'P') {
            this.setRole(Roles.PASIEN);
        }
        
        else if(this.getId().charAt(0) == 'A') {
            this.setRole(Roles.ADMIN);
        }
    }
    
    public void fetchPasienData() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
    
}