package com.sap.engine.services.dc.cm.utils.measurement.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;
import com.sap.engine.services.dc.util.StringUtils;

class DMeasurementImpl implements DMeasurement {

	private static final long serialVersionUID = 0L;

	private final String tagName;
	private String dcName;
	private final int hashCode;
	private final Set statistics;
	private final List children;
	private final Boolean hasNewThreadStarted;
	private final static Boolean noNewThreadStarted = Boolean.FALSE;

	DMeasurementImpl(final String tagName, final String dcName, Set statistics) {
		this(tagName, dcName, statistics, noNewThreadStarted);
	}

	DMeasurementImpl(final String tagName, final String dcName, Set statistics,
			Boolean hasNewThreadStarted) {
		this.tagName = tagName;
		this.dcName = dcName;
		this.statistics = statistics;
		this.children = new ArrayList();
		this.hashCode = calculateHashCode();
		this.hasNewThreadStarted = hasNewThreadStarted;
	}

	public String getTagName() {
		return this.tagName;
	}

	public String getDcName() {
		return this.dcName;
	}

	public Set getStatistics() {
		return Collections.unmodifiableSet(statistics);
	}

	public List getChildrenMeasurments() {
		return Collections.unmodifiableList(children);
	}

	public Boolean hasNewThreadStarted() {
		return this.hasNewThreadStarted;
	}

	void addChild(DMeasurement dMeasurement) {
		children.add(dMeasurement);
	}

	public String toString() {
		return "tagName[" + getTagName() + "], dcName[" + getDcName()
				+ "], statistics[" + getStatistics()
				+ "], hasNewThreadStarted[" + hasNewThreadStarted()
				+ "], childrenMeasurementsCount["
				+ getChildrenMeasurments().size() + "]";
	}

	public Document toDocument() {
		return XmlUtil.toDocument(this);
	}

	public String toDocumentAsString() {
		return XmlUtil.toDocumentAsString(this);
	}

	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}

		if (this == other) {
			return true;
		}

		if (this.getClass() != other.getClass()) {
			return false;
		}

		final DMeasurement otherMeasurement = (DMeasurement) other;

		if (getDcName() == null) {
			if (otherMeasurement.getDcName() != null) {
				return false;
			}
		} else if (!getDcName().equals(otherMeasurement.getDcName())) {
			return false;
		}

		if (getTagName() == null) {
			if (otherMeasurement.getTagName() != null) {
				return false;
			}
		} else if (!getTagName().equals(otherMeasurement.getTagName())) {
			return false;
		}

		if (!hasNewThreadStarted().equals(
				otherMeasurement.hasNewThreadStarted())) {
			return false;
		}

		if (!getStatistics().equals(otherMeasurement.getStatistics())) {
			return false;
		}

		if (!getChildrenMeasurments().equals(
				otherMeasurement.getChildrenMeasurments())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	private int calculateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getHashCode(getDcName());
		result = result * multiplier + getHashCode(getTagName());
		result = result * multiplier + getHashCode(getStatistics());
		result = result * multiplier + getHashCode(getChildrenMeasurments());

		return result;
	}

	private int getHashCode(final Object obj) {
		if (obj == null) {
			return 0;
		}
		return obj.hashCode();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.dcName = StringUtils.intern(this.dcName);		
	}

}
