package com.progdan.edmis.error.user;

public class ExistingUserException extends Exception {
    public ExistingUserException() {
        super();
    }

    public ExistingUserException(String msg) {
        super(msg);
    }
}
