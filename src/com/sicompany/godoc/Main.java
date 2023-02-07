package com.sicompany.godoc;

import com.sicompany.godoc.screens.LoginScreen;
import javax.swing.JFrame;

/**
 * 
 * @author Kelompok 2
 */

public class Main {
    public static void main(String[] args) {
        
        JFrame activeScreen = new LoginScreen();
        activeScreen.setVisible(true);
    }
}
