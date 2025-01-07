package com.sap.jms.util;

/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2006.
 * All rights reserved.
 */

public interface Task {
	public void execute();
	public String getName();
}
