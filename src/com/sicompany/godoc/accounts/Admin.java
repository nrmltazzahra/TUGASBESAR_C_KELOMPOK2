package com.sicompany.godoc.accounts;

/**
 * 
 * @author Kelompok 2
 */
public class Admin extends User {

    public Admin(String id, String username, String password) {
        super(id, username, password);
        setRole(Roles.ADMIN);
    }
}