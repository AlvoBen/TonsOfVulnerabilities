package com.sap.jms.util;

import com.sap.tc.logging.Severity;


public class Command implements Runnable {
	
	Task task = null;
    	
    public Command(Task task) {
    	this.task = task;
    }
	
    public void run() {
    	try {
    		task.execute();
    	} catch (Exception e) {
    		if (Logging.isWritable(this, Severity.DEBUG)) {
                Logging.log(this, Severity.DEBUG, "The following exception has occurred during task ", (task != null ? task.getName() : " task is null"), " exection");
            }
            Logging.exception(this, e);
    	}
	}	    	
}
