package com.progdan.edmis.error.user;

public class PasswdErrorException extends Exception {
    public PasswdErrorException() {
        super();
    }

    public PasswdErrorException(String msg) {
        super(msg);
    }
}
