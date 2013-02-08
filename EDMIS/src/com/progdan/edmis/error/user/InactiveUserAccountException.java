package com.progdan.edmis.error.user;

public class InactiveUserAccountException extends Exception{
    public InactiveUserAccountException() {
        super();
    }

    public InactiveUserAccountException(String msg) {
        super(msg);
    }
}
