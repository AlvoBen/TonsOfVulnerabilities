package com.sap.jms.util;

/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2006.
 * All rights reserved.
 */

public interface TaskManager {
    
	public void schedule(Task task);
    
	public void start();
	public void stop();	
}

