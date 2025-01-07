package com.sap.engine.services.dc.repo.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduVisitor;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.repo.Version;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SdaImpl extends SduImpl implements Sda {

	private static final long serialVersionUID = 6347185030267903471L;

	private final SoftwareType softwareType;
	private final Set dependencies;// $JL-SER$
	private final Set dependingFrom;// $JL-SER$
	private final SduId id;
	private ScaId scaId;

	SdaImpl(final SduId sduId, final String location, final Version version,
			final String componentElementXML, final String softwareType,
			final String softwareSubType, final String csnComponent) {
		this(sduId, location, version, componentElementXML, softwareType,
				softwareSubType, csnComponent, new HashSet());
	}

	SdaImpl(final SduId sduId, final String location, final Version version,
			final String componentElementXML, final String softwareType,
			final String softwareSubType, final String csnComponent,
			final Set dependencies) {
		this(sduId, location, version, componentElementXML, softwareType,
				softwareSubType, csnComponent, dependencies, null);
	}

	SdaImpl(final SduId sduId, final String location, final Version version,
			final String componentElementXML, final String softwareType,
			final String softwareSubType, final String csnComponent,
			final Set dependencies, final ScaId scaId) {
		this(sduId, location, version, componentElementXML, softwareType,
				softwareSubType, csnComponent, dependencies, scaId, null);
	}

	SdaImpl(final SduId sduId, final String location, final Version version,
			final String componentElementXML, final String softwareType,
			final String softwareSubType, final String csnComponent,
			final Set dependencies, final ScaId scaId, final Map properties) {
		super(sduId, location, version, csnComponent, componentElementXML,
				properties);

		paramCheck(softwareType, "SdaImpl", "softwareType");

		this.scaId = scaId;
		this.softwareType = SoftwareType.getSoftwareTypeByName(softwareType,
				softwareSubType);
		this.dependencies = dependencies != null ? dependencies : new HashSet();
		this.dependingFrom = new HashSet();

		this.id = sduId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sda#getScaId()
	 */
	public ScaId getScaId() {
		return this.scaId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sda#getSoftwareType()
	 */
	public SoftwareType getSoftwareType() {
		return this.softwareType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sda#getDependencies()
	 */
	public Set getDependencies() {
		return this.dependencies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Sda#addDependency(com.sap.engine.services
	 * .dc.repo.Dependency)
	 */
	public void addDependency(Dependency dependency) {
		this.dependencies.add(dependency);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sdu#getId()
	 */
	public SduId getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Sdu#accept(com.sap.engine.services.dc
	 * .repo.SduVisitor)
	 */
	public void accept(SduVisitor visitor) {
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.impl.SduImpl#isComparable(com.sap.engine
	 * .services.dc.repo.Sdu)
	 */
	public boolean isComparable(Sdu otherSdu) {
		paramCheck(otherSdu, "isComparable", "otherSdu");

		if (!this.getId().equals(otherSdu.getId())) {
			final String errText = "[ERROR CODE DPL.DC.3424] SdaImpl.isComparable(Sdu): "
					+ "other SDU "
					+ otherSdu
					+ " has different SDU ID than "
					+ this;
			// TODO: log fatal type: errText
			throw new IllegalArgumentException(errText);
		}

		return this.getLocation().equals(otherSdu.getLocation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.impl.SduImpl#isSameSdu(com.sap.engine
	 * .services.dc.repo.Sdu)
	 */
	public boolean isSameSdu(Sdu otherSdu) {
		paramCheck(otherSdu, "isSameSdu", "otherSdu");

		// if ( this.getClass() != otherSdu.getClass() ) {
		// final String errText = "SdaImpl.isSameSdu(Sdu): "
		// + "the otherSdu object '" + otherSdu
		// + "' is a different type than " + this;
		// //TODO: log fatal type: errText
		// throw new IllegalArgumentException(errText);
		// }
		//
		// if ( !this.getId().equals( otherSdu.getId() ) ) {
		// final String errText = "SdaImpl.isSameSdu(Sdu): "
		// + "other SDU " + otherSdu
		// + " has different SDU ID than " + this;
		// //TODO: log fatal type: errText
		// throw new IllegalArgumentException(errText);
		// }

		if (!isComparable(otherSdu)) {
			return false;
		}

		final Sda otherSda = (Sda) otherSdu;

		if (!this.getVersion().equals(otherSdu.getVersion())) {
			return false;
		}

		if (!this.getDependencies().equals(otherSda.getDependencies())) {
			return false;
		}

		if (!this.getSoftwareType().equals(otherSda.getSoftwareType())) {
			return false;
		}

		return true;
	}

	public String toString() {
		return "name: [" + getName() + "], vendor: [" + getVendor()
				+ "], location: [" + getLocation() + "], version: ["
				+ getVersion() + "], software type: [" + getSoftwareType()
				+ "], csn component: [" + getCsnComponent()
				+ "], dependencies: [" + getDependencies() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Sda#getDependingFrom()
	 */
	public Set getDependingFrom() {
		return this.dependingFrom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Sda#addDependingFrom(com.sap.engine.services
	 * .dc.repo.SduId)
	 */
	public void addDependingFrom(Dependency _dependingFrom) {
		this.dependingFrom.add(_dependingFrom);
	}
	
	public void setScaId(ScaId scaId){
		this.scaId = scaId;
	}
}
