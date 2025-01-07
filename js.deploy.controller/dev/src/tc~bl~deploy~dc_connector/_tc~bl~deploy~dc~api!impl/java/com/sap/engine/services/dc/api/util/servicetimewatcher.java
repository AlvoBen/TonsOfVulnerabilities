/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.util;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Chronometer implementation used as measure for distinct operations.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-17</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public class ServiceTimeWatcher {
	private static long counter = 0;
	private long id;
	private long time, start;
	private static Object mutex = new Object();

	/**
	 * When the instance is created the time beging to run.
	 * 
	 */
	public ServiceTimeWatcher() {

		synchronized (mutex) {
			this.id = counter++;
			if (counter == Long.MAX_VALUE) {
				counter = 0;
			}
		}

		clearElapsed();
		this.start = this.time;
	}

	/**
	 * 
	 * @return the time between creation or last invoking the method
	 */
	public String getElapsedTimeAsString() {
		long tmp = System.currentTimeMillis();
		long elapsed = (tmp - this.time) /* / 1000L */;
		String ret = "[id:#" + this.id + ", elapsed: " + elapsed + " ms.]";
		this.time = tmp;
		return ret;
	}

	/**
	 * clears elapsed time
	 */
	public void clearElapsed() {
		this.time = System.currentTimeMillis();
	}

	/**
	 * @return elapsed time since the timer was created
	 */
	public String getTotalElapsedTimeAsString() {
		double totalElapsed = (System.currentTimeMillis() - this.start) / 1000D;
		return "[#" + this.id + ": " + totalElapsed + " sec]";
	}

	public long getId() {
		return this.id;
	}

}