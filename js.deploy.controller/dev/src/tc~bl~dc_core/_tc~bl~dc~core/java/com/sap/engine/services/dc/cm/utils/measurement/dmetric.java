package com.sap.engine.services.dc.cm.utils.measurement;

import java.io.IOException;
import java.io.Serializable;

import com.sap.engine.services.dc.util.StringUtils;

public final class DMetric implements Serializable {

	private static final long serialVersionUID = 0L;

	// time
	public transient static final DMetric NANOSECONDS = new DMetric(
			new Integer(0), "NANOSECONDS");
	public transient static final DMetric MILLISECONDS = new DMetric(
			new Integer(1), "MILLISECONDS");
	public transient static final DMetric MICROSECONDS = new DMetric(
			new Integer(2), "MICROSECONDS");
	public transient static final DMetric SECONDS = new DMetric(new Integer(3),
			"SECONDS");
	//
	public transient static final DMetric BYTES = new DMetric(new Integer(10),
			"BYTES");
	public transient static final DMetric KILOBYTES = new DMetric(new Integer(
			11), "KILOBYTES");
	public transient static final DMetric MEGABYTES = new DMetric(new Integer(
			12), "MEGABYTES");
	// count
	public transient static final DMetric COUNT = new DMetric(new Integer(20),
			"COUNT");

	private final Integer id;
	private String name;

	private DMetric(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}

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

		if (!(obj instanceof DMetric)) {
			return false;
		}

		DMetric other = (DMetric) obj;

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
