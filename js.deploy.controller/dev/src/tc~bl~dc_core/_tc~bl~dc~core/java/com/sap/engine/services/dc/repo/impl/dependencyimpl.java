package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.DCReference;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.Sdu;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-10
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.00
 * 
 *        <xs:element name="dependency"> <xs:complexType> <xs:sequence>
 *        <xs:element ref="dc-ref"/> <xs:element ref="pp-ref" minOccurs="0"/>
 *        <xs:element ref="for-children" minOccurs="0"/> <xs:element
 *        ref="at-design-time" minOccurs="0"/> <xs:element ref="at-build-time"
 *        minOccurs="0"/> <xs:element ref="at-deploy-time" minOccurs="0"/>
 *        <xs:element ref="at-runtime" minOccurs="0"/> </xs:sequence>
 *        </xs:complexType> </xs:element>
 * 
 * 
 */
public class DependencyImpl implements Dependency {

	private static final long serialVersionUID = -5537730781923377222L;

	private final DCReference dcRef;
	private final int hashCode;
	private String toString;

	private String ppRef;
	private boolean forChildren;
	private boolean relevantAtDesignTime;
	private boolean relevantAtBuildTime;
	private boolean relevantAtDeployTime;
	private boolean relevantAtRuntimeTime;

	public DependencyImpl(String name, String vendor) {
		this(name, vendor, "");
	}

	public DependencyImpl(String name, String vendor, String scAlias) {
		this(new DCReferenceImpl(name, vendor, scAlias));
	}

	public DependencyImpl(DCReference dcRef) {

		this.dcRef = dcRef;
		this.hashCode = calcHashCode();
		this.toString = null; // build lazy
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.Dependency#getName()
	 */
	public String getName() {
		return dcRef.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.Dependency#getVendor()
	 */
	public String getVendor() {
		return dcRef.getVendor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.Dependency#getDCReference()
	 */
	public DCReference getDCReference() {
		return this.dcRef;
	}

	public String getPpRef() {
		return ppRef;
	}

	public void setPpRef(String ppRef) {
		this.ppRef = ppRef;
	}

	public void setForChildren(boolean forChildren) {
		this.forChildren = forChildren;
	}

	public boolean isForChildren() {
		return forChildren;
	}

	public void setRelevantAtDesignTime(boolean relevantAtDesignTime) {
		this.relevantAtDesignTime = relevantAtDesignTime;
	}

	public boolean isRelevantAtDesignTime() {
		return relevantAtDesignTime;
	}

	public void setRelevantAtBuildTime(boolean relevantAtBuildTime) {
		this.relevantAtBuildTime = relevantAtBuildTime;
	}

	public boolean isRelevantAtBuildTime() {
		return relevantAtBuildTime;
	}

	public void setRelevantAtDeployTime(boolean relevantAtDeployTime) {
		this.relevantAtDeployTime = relevantAtDeployTime;
	}

	public boolean isRelevantAtDeployTime() {
		return relevantAtDeployTime;
	}

	public void setRelevantAtRuntimeTime(boolean relevantAtRuntimeTime) {
		this.relevantAtRuntimeTime = relevantAtRuntimeTime;
	}

	public boolean isRelevantAtRuntimeTime() {
		return relevantAtRuntimeTime;
	}

	public boolean isResolvableBy(Sdu sdu) {
		return DependencyResolvabilityChecker.getInstance().isDepResolvableBy(
				this, sdu);
	}

	public String toString() {

		if (this.toString == null) {
			this.toString = buildToString();
		}
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Dependency)) {
			return false;
		}

		final Dependency otherDep = (Dependency) obj;

		if (!this.getName().equals(otherDep.getName())) {
			return false;
		}

		if (!this.getVendor().equals(otherDep.getVendor())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.Dependency#isClassloading()
	 */
	public boolean isClassloading() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repository.Dependency#isFunctional()
	 */
	public boolean isFunctional() {
		return true;
	}

	private int calcHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.getName().hashCode();

		return result * multiplier + this.getVendor().hashCode();
	}

	private String buildToString() {

		// return "name: '" + this.getName() + "', vendor: '" + this.getVendor()
		// + "'";

		// performance fix according to CSN 1052704 / 2007
		String[] args = { "name: '", this.getName(), "', vendor: '",
				this.getVendor(), "'" };

		// calculate the size
		int size = 0;
		for (int i = 0; i < args.length; i++) {
			size += args[i].length();
		}

		StringBuffer buf = new StringBuffer(size); // the initial size of this
		// buffer (created
		// internally) was
		// suboptimal
		for (int i = 0; i < args.length; i++) {
			buf.append(args[i]);
		}

		return buf.toString();
	}

}
