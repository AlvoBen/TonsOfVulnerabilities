package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.repo.impl.VersionImpl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ScaUndeployItemImpl implements  ScaUndeployItem {

	private static final long serialVersionUID = 2157418190724129941L;
	
	private final String name;
	private final String vendor;
	private final String location;
	private final Version version;
	private final int hashCode;

	private UndeployItemStatus undeployItemStatus = UndeployItemStatus.INITIAL;
	private String description = "";
	private Sca sca;

	// do not serialize the following properties
	// serialize id to assure that new client will work with old engine
	private UndeployItemId id;

	private transient Set undeployItemObserverSet;

	ScaUndeployItemImpl(String name, String vendor) {
		this(name, vendor, null, null);
	}

	ScaUndeployItemImpl(String name, String vendor, String location,
			String versionAsString) {
		if (name == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3407] The argument specified for a name could not be null!");
		}
		if (vendor == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3408] The argument specified for a vendor could not be null!");
		}

		this.name = name;
		this.vendor = vendor;

		if (location != null && versionAsString == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DC.3409] A UndeployItem cannot be created with SL location different than null and "
							+ "version which is null.");
		} else if (location == null && versionAsString != null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DC.3410] A UndeployItem cannot be created with version different than null and "
							+ "SL location which is null.");
		}

		this.location = location;
		if (this.location != null && this.location.trim().equals("")) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DC.3411] The location could not be an empty string.");
		}

		if (versionAsString != null) {
			if (versionAsString.trim().equals("")) {
				throw new IllegalArgumentException(
						"[ERROR CODE DPL.DC.3412] The version could not be an empty string.");
			}

			this.version = new VersionImpl(versionAsString);
		} else {
			this.version = null;
		}

		this.hashCode = calcHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getId()
	 */
	public UndeployItemId getId() {
		if (this.id == null) {
			synchronized (this) {
				if (this.id == null) {// double check
					this.id = new ScaUndeployItemIdImpl(this.name, this.vendor);
				}
			}
		}
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getVendor()
	 */
	public String getVendor() {
		return this.vendor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getLocation()
	 */
	public String getLocation() {
		if (this.getSca() != null && this.location == null) {
			return this.getSca().getLocation();
		}
		return this.location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getVersion()
	 */
	public Version getVersion() {
		if (this.getSca() != null && this.version == null) {
			return this.getSca().getVersion();
		}
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#getUndeployItemStatus
	 * ()
	 */
	public UndeployItemStatus getUndeployItemStatus() {
		return this.undeployItemStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#setUndeployItemStatus
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus)
	 */
	public void setUndeployItemStatus(UndeployItemStatus undeployItemStatus) {
		UndeployItemStatus oldStatus = this.undeployItemStatus;
		this.undeployItemStatus = undeployItemStatus;

		if (undeployItemObserverSet == null) {
			return;
		} else {
			Iterator observers = undeployItemObserverSet.iterator();
			while (observers.hasNext()) {
				((UndeployItemObserver) observers.next()).statusChanged(this,
						oldStatus, undeployItemStatus);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#setDescription(java
	 * .lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getSda()
	 */
	public Sca getSca() {
		return this.sca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#setSda(com.sap.engine
	 * .services.dc.repo.Sda)
	 */
	public void setSca(Sca sca) {
		this.sca = sca;
	}


	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final ScaUndeployItem other = (ScaUndeployItem) obj;

		if (!this.getName().equals(other.getName())) {
			return false;
		}

		if (!this.getVendor().equals(other.getVendor())) {
			return false;
		}

		if (this.getLocation() != null && other.getLocation() != null
				&& !this.getLocation().equals(other.getLocation())) {
			return false;
		}

		if (this.getVersion() != null && other.getVersion() != null
				&& !this.getVersion().equals(other.getVersion())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		final StringBuffer result = new StringBuffer();
		result.append("type: [SCA]").append(
				", name: [").append(this.getName()).append("]").append(
				", vendor: [").append(this.getVendor()).append("]").append(
				", location: [").append(this.getLocation()).append("]").append(
				", version: [").append(this.getVersion()).append("]").append(
				", status: [").append(this.getUndeployItemStatus()).append("]")
				.append(", description: [").append(this.getDescription())
				.append("]");
		return result.toString();
	}

	private int calcHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.getName().hashCode();

		result = result * multiplier + this.getVendor().hashCode();
		result = result * multiplier + "sca".hashCode();

		return result;
	}

	public synchronized void addUndeployItemObserver(
			UndeployItemObserver observer) {

		if (this.undeployItemObserverSet == null) {
			this.undeployItemObserverSet = new HashSet();
		}
		undeployItemObserverSet.add(observer);
	}

	public void removeUndeployItemObserver(UndeployItemObserver observer) {
		undeployItemObserverSet.remove(observer);
	}
	
	public void accept(UndeployItemVisitor visitor) {
		visitor.visit(this);
	}

	public Sdu getSdu() {
		return getSca();
	}
	
	
	
}
