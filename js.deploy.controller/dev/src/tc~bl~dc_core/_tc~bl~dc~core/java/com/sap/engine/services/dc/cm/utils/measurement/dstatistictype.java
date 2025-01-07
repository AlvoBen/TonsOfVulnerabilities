package com.sap.engine.services.dc.cm.utils.measurement;

import java.io.IOException;
import java.io.Serializable;

import com.sap.engine.services.dc.util.StringUtils;

public final class DStatisticType implements Serializable {

	private static final long serialVersionUID = 0L;

	/* Elapsed time */
	public transient static final DStatisticType ELAPSED_TIME_MS = new DStatisticType(
			new Integer(0), "ELAPSED_TIME_MS", DMetric.MILLISECONDS);
	public transient static final DStatisticType ELAPSED_TIME_SECONDS = new DStatisticType(
			new Integer(1), "ELAPSED_TIME_SECONDS", DMetric.SECONDS);
	/* Cpu */
	public transient static final DStatisticType CPU_TIME_NANOSECONDS = new DStatisticType(
			new Integer(10), "CPU_TIME_NANOSECONDS", DMetric.NANOSECONDS);
	public transient static final DStatisticType CPU_TIME_MICROSECONDS = new DStatisticType(
			new Integer(11), "CPU_TIME_MICROSECONDS", DMetric.MICROSECONDS);
	public transient static final DStatisticType CPU_TIME_SECONDS = new DStatisticType(
			new Integer(12), "CPU_TIME_SECONDS", DMetric.SECONDS);
	/* Memory */
	public transient static final DStatisticType ALLOCATED_MEMORY_BYTES = new DStatisticType(
			new Integer(20), "ALLOCATED_MEMORY_BYTES", DMetric.BYTES);

	/* IO */
	/* File */
	public transient static final DStatisticType FILEIO_BYTES_READ = new DStatisticType(
			new Integer(30), "FILEIO_BYTES_READ", DMetric.BYTES);
	public transient static final DStatisticType FILEIO_BYTES_WRITTEN = new DStatisticType(
			new Integer(31), "FILEIO_BYTES_WRITTEN", DMetric.BYTES);
	public transient static final DStatisticType FILES_OPENED = new DStatisticType(
			new Integer(32), "FILES_OPENED", DMetric.COUNT);
	public transient static final DStatisticType FILES_STILL_OPEN = new DStatisticType(
			new Integer(33), "FILES_STILL_OPEN", DMetric.COUNT);

	/* Net */
	public transient static final DStatisticType NETIO_BYTES_READ = new DStatisticType(
			new Integer(40), "NETIO_BYTES_READ", DMetric.BYTES);
	public transient static final DStatisticType NETIO_BYTES_WRITTEN = new DStatisticType(
			new Integer(41), "NETIO_BYTES_WRITTEN", DMetric.BYTES);
	/* Socket */
	public transient static final DStatisticType SOCKETS_OPENED = new DStatisticType(
			new Integer(50), "SOCKETS_OPENED", DMetric.COUNT);
	public transient static final DStatisticType SOCKETS_STILL_OPEN = new DStatisticType(
			new Integer(51), "SOCKETS_STILL_OPEN", DMetric.COUNT);
	/* Other */
	public transient static final DStatisticType OTHERIO_BYTES_READ = new DStatisticType(
			new Integer(60), "OTHERIO_BYTES_READ", DMetric.BYTES);
	public transient static final DStatisticType OTHERIO_BYTES_WRITTEN = new DStatisticType(
			new Integer(61), "OTHERIO_BYTES_WRITTEN", DMetric.BYTES);
	/* end IO */
	;

	private final Integer id;
	private String name;
	private final DMetric metric;

	private DStatisticType(Integer id, String name, DMetric metric) {
		this.id = id;
		this.name = name;
		this.metric = metric;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public DMetric getMetric() {
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

		if (!(obj instanceof DStatisticType)) {
			return false;
		}

		DStatisticType other = (DStatisticType) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern(this.name);
	}

}