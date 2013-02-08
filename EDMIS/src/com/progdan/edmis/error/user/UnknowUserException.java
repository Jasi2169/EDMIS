package com.progdan.edmis.error.user;

public class UnknowUserException extends Exception {
    public UnknowUserException() {
        super();
    }

    public UnknowUserException(String msg) {
        super(msg);
    }
}
