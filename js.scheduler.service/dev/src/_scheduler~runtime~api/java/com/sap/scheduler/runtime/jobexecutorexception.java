
package com.sap.scheduler.runtime;

public class JobExecutorException extends Exception {
    
    public JobExecutorException(String message) {
        super (message);
    }
    public JobExecutorException(String message, Throwable reason) {
        super (message, reason);
    }
}