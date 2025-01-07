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
 * <DD>Statistic Type</DD>
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
public final class DAStatisticType implements Serializable {

	private static final long serialVersionUID = 0L;
	
	/* Elapsed time */
	public transient static final DAStatisticType ELAPSED_TIME_MS = new DAStatisticType(
			new Integer(0), "ELAPSED_TIME_MS", DAMetric.MILLISECONDS);
	public transient static final DAStatisticType ELAPSED_TIME_SECONDS = new DAStatisticType(
			new Integer(1), "ELAPSED_TIME_SECONDS", DAMetric.SECONDS);
	/* Cpu */
	public transient static final DAStatisticType CPU_TIME_NANOSECONDS = new DAStatisticType(
			new Integer(10), "CPU_TIME_NANOSECONDS", DAMetric.NANOSECONDS);
	public transient static final DAStatisticType CPU_TIME_MICROSECONDS = new DAStatisticType(
			new Integer(11), "CPU_TIME_MICROSECONDS", DAMetric.MICROSECONDS);
	public transient static final DAStatisticType CPU_TIME_SECONDS = new DAStatisticType(
			new Integer(12), "CPU_TIME_SECONDS", DAMetric.SECONDS);
	/* Memory */
	public transient static final DAStatisticType ALLOCATED_MEMORY_BYTES = new DAStatisticType(
			new Integer(20), "ALLOCATED_MEMORY_BYTES", DAMetric.BYTES);
	/* IO */
	/* File */
	public transient static final DAStatisticType FILEIO_BYTES_READ = new DAStatisticType(
			new Integer(30), "FILEIO_BYTES_READ", DAMetric.BYTES);
	public transient static final DAStatisticType FILEIO_BYTES_WRITTEN = new DAStatisticType(
			new Integer(31), "FILEIO_BYTES_WRITTEN", DAMetric.BYTES);
	public transient static final DAStatisticType FILES_OPENED = new DAStatisticType(
			new Integer(32), "FILES_OPENED", DAMetric.COUNT);
	public transient static final DAStatisticType FILES_STILL_OPEN = new DAStatisticType(
			new Integer(33), "FILES_STILL_OPEN", DAMetric.COUNT);

	/* Net */
	public transient static final DAStatisticType NETIO_BYTES_READ = new DAStatisticType(
			new Integer(40), "NETIO_BYTES_READ", DAMetric.BYTES);
	public transient static final DAStatisticType NETIO_BYTES_WRITTEN = new DAStatisticType(
			new Integer(41), "NETIO_BYTES_WRITTEN", DAMetric.BYTES);
	/* Socket */
	public transient static final DAStatisticType SOCKETS_OPENED = new DAStatisticType(
			new Integer(50), "SOCKETS_OPENED", DAMetric.COUNT);
	public transient static final DAStatisticType SOCKETS_STILL_OPEN = new DAStatisticType(
			new Integer(51), "SOCKETS_STILL_OPEN", DAMetric.COUNT);
	/* Other */
	public transient static final DAStatisticType OTHERIO_BYTES_READ = new DAStatisticType(
			new Integer(60), "OTHERIO_BYTES_READ", DAMetric.BYTES);
	public transient static final DAStatisticType OTHERIO_BYTES_WRITTEN = new DAStatisticType(
			new Integer(61), "OTHERIO_BYTES_WRITTEN", DAMetric.BYTES);
	/* end IO */
	;

	private final Integer id;
	private final String name;
	private final DAMetric metric;

	private DAStatisticType(Integer id, String name, DAMetric metric) {
		this.id = id;
		this.name = name;
		this.metric = metric;
	}

	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Returns statistic's type name
	 * @return statistic's type name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns statistic's type metric
	 * @return Returns statistic's type metric
	 */
	public DAMetric getMetric() {
		return this.metric;
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

		if (!(obj instanceof DAStatisticType)) {
			return false;
		}

		DAStatisticType other = (DAStatisticType) obj;

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