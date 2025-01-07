/*
 * Created on 26.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.scheduler.runtime;

/**
 * <b>Note: This is not a public API. It may be modified or can 
 * disappear without notice.</b>
 * 
 * @author Dirk Marwinski
 */
public class SchedulerAlreadyDefinedException extends SchedulerException {

	/**
	 * 
	 */
	public SchedulerAlreadyDefinedException() {
		super();
	}

	/**
	 */
	public SchedulerAlreadyDefinedException(String arg0) {
		super(arg0);
	}

    public SchedulerAlreadyDefinedException(String arg0, Throwable t) {
        super(arg0,t);
    }

}
