package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.repo.Sda;
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
final class UndeployItemImpl implements  UndeployItem {

	private static final long serialVersionUID = -5412120648267760718L;

	private final String name;
	private final String vendor;
	private final String location;
	private final Version version;
	private final int hashCode;

	private UndeployItemStatus undeployItemStatus = UndeployItemStatus.INITIAL;
	private String description = "";
	private Sda sda;

	// do not serialize the following properties
	// serialize id to assure that new client will work with old engine
	private UndeployItemId id;
	private transient Set depending;// $JL-SER$ (!) transient because involve of large structure and StackOverFlowExc problems
	private Set dependingOnThis;// $JL-SER$ 
	private Map propsMap = new HashMap(0);// $JL-SER$

	private transient Set undeployItemObserverSet;

	UndeployItemImpl(String name, String vendor) {
		this(name, vendor, null, null);
	}

	UndeployItemImpl(String name, String vendor, String location,
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
					this.id = new UndeployItemIdImpl(this.name, this.vendor);
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
		if (this.getSda() != null && this.location == null) {
			return this.getSda().getLocation();
		}
		return this.location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getVersion()
	 */
	public Version getVersion() {
		if (this.getSda() != null && this.version == null) {
			return this.getSda().getVersion();
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
	public Sda getSda() {
		return this.sda;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#setSda(com.sap.engine
	 * .services.dc.repo.Sda)
	 */
	public void setSda(Sda sda) {
		this.sda = sda;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#getDependsOnUndeployItems
	 * ()
	 */
	public Set getDepending() {
		return this.depending;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#addDependsOnUndeployItem
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployItem)
	 */
	public void addDepending(UndeployItem sdaUndeployItem) {
		if (this.depending == null) {
			this.depending = new HashSet();// $JL-SER$
		}
		this.depending.add(sdaUndeployItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeployItem#
	 * removeDependsOnUndeployItem
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployItem)
	 */
	public void removeDepending(UndeployItem sdaUndeployItem) {
		if (this.depending == null) {
			return;
		}
		this.depending.remove(sdaUndeployItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#getDependingOnThis()
	 */
	public Set getDependingOnThis() {
		return this.dependingOnThis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#addDependingOnThis
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployItem)
	 */
	public void addDependingOnThis(UndeployItem sdaUndeployItem) {
		if (this.dependingOnThis == null) {
			this.dependingOnThis = new HashSet();
		}
		this.dependingOnThis.add(sdaUndeployItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#removeDependingOnThis
	 * (com.sap.engine.services.dc.cm.undeploy.UndeployItem)
	 */
	public void removeDependingOnThis(UndeployItem sdaUndeployItem) {
		if (this.dependingOnThis == null) {
			return;
		}
		this.dependingOnThis.remove(sdaUndeployItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#removeAllDependingOnThis
	 * ()
	 */
	public void removeAllDependingOnThis() {
		if (this.dependingOnThis == null) {
			return;
		}
		this.dependingOnThis.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeployItem#getProperties()
	 */
	public Map getProperties() {
		return this.propsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployItem#setProperties(java
	 * .util.Map)
	 */
	public void setProperties(Map propsMap) {
		if (this.propsMap == null) {
			propsMap = new HashMap(0);
		} else {
			this.propsMap.clear();
		}
		this.propsMap.putAll(propsMap);
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

		final GenericUndeployItem other = (GenericUndeployItem) obj;

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
		result.append("type: [SDA]").append(
				", name: [").append(this.getName()).append("]").append(
				", vendor: [").append(this.getVendor()).append("]").append(
				", location: [").append(this.getLocation()).append("]").append(
				", version: [").append(this.getVersion()).append("]").append(
				", status: [").append(this.getUndeployItemStatus()).append("]")
				.append(", description: [").append(this.getDescription())
				.append("]");
		// do not show dependings to avoid deadlock
		// if(this.depending!=null && this.depending.size()>0){
		// result.append( ", depending: '").append( this.depending).append("'")
		// ;
		// }
		if (this.dependingOnThis != null && this.dependingOnThis.size() > 0) {
			result.append(", dependingOnThis: ").append(getDepedingOnThisIds()) // if
					// 'depedingOnThis'
					// is
					// used
					// ,
					// then
					// OOM
					// is
					// produced
					// ,
					// because of large string with recursive repeated items.
					.append("]");
		}
		// result.append("sda: [").append( this.getSda()
		// ).append("]").append(Constants.EOL);

		return result.toString();
	}

	/*
	 * @return Set Ids of items from 1st level that depends on this item.
	 */
	private Set getDepedingOnThisIds() {
		Set dependingOnThisIds = new HashSet(this.dependingOnThis.size());

		Iterator iDependingOnThis = this.dependingOnThis.iterator();

		while (iDependingOnThis.hasNext()) {
			dependingOnThisIds.add(((UndeployItem) iDependingOnThis.next())
					.getId());
		}

		return dependingOnThisIds;
	}

	private int calcHashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.getName().hashCode();

		result = result * multiplier + this.getVendor().hashCode();
		result = result * multiplier + "sda".hashCode();

		// if ( this.getLocation() != null ) {
		// result = result * multiplier + this.getLocation().hashCode();
		// }
		//    
		// if ( this.getVersion() != null ) {
		// result = result * multiplier + this.getVersion().hashCode();
		// }

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
		return getSda();
	}
	
	
	
}
