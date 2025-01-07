package com.sap.scheduler.runtime;

public class UserAccountException extends Exception {

    
    public UserAccountException(String msg) {
        super(msg);
    }
    
    public UserAccountException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public UserAccountException(Throwable reason) {
        super(reason);
    }
}
