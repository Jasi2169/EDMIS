package com.progdan.edmis.control.crypt;

import java.applet.*;

public class EncryptApplet extends Applet {
    public void init() {
    }

    public String encryptPassword(String pass) {
        return JCrypt.crypt("any string", pass);
    }

    public boolean comparePasswords(String pass1, String pass2) {
        if (pass1.equals(pass2)) {
            return true;
        } else {
            return false;
        }
    }
}
