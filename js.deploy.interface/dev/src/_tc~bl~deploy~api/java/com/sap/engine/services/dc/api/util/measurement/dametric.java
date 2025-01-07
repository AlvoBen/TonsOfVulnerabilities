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
package com.sap.engine.services.dc.api.util.measurement;

import java.io.Serializable;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Metric</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 11, 2009</DD>
 * </DL>
 * 
 * @author Radoslav Ivanov(i031258)
 * @version 1.0
 * @since 7.20
 */

public final class DAMetric implements Serializable {
	
	private static final long serialVersionUID = 0L;
	
	//time
	public transient static final DAMetric NANOSECONDS = new DAMetric(
			new Integer(0), "NANOSECONDS");
	public transient static final DAMetric MILLISECONDS = new DAMetric(
			new Integer(1), "MILLISECONDS");
	public transient static final DAMetric MICROSECONDS = new DAMetric(
			new Integer(2), "MICROSECONDS");
	public transient static final DAMetric SECONDS = new DAMetric(
			new Integer(3), "SECONDS");
	//
	public transient static final DAMetric BYTES = new DAMetric(
			new Integer(10), "BYTES");
	public transient static final DAMetric KILOBYTES = new DAMetric(
			new Integer(11), "KILOBYTES");
	public transient static final DAMetric MEGABYTES = new DAMetric(
			new Integer(12), "MEGABYTES");
	//count
	public transient static final DAMetric COUNT = new DAMetric(
			new Integer(20), "COUNT");

	private final Integer id;
	private final String name;

	private DAMetric(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Returns metric's name
	 * @return metric's name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DAMetric)) {
			return false;
		}

		DAMetric other = (DAMetric) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
	
	//TODO implement read object

}
