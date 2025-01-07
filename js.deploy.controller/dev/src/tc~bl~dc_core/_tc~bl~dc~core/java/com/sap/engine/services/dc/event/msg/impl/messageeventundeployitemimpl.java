package com.sap.engine.services.dc.event.msg.impl;

import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.event.msg.MessageEventUndeployItem;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.Version;

final class MessageEventUndeployItemImpl implements MessageEventUndeployItem, UndeployItem {

	private static final long serialVersionUID = 8913862369009499364L;

	private static final String errorMsg = "[ERROR CODE DPL.DC.3459] Method is not supported for message event item.";

	private final UndeployItemId id;
	private final String location;
	private final Sda sda;
	private final String name;
	private final String vendor;
	private final Version version;
	private final UndeployItemStatus undeployItemStatus;
	private final String description;
	private final Map properties;

	MessageEventUndeployItemImpl(UndeployItem sdaUndeployItem) {
		this.id = sdaUndeployItem.getId();
		this.location = sdaUndeployItem.getLocation();
		this.sda = sdaUndeployItem.getSda();
		this.name = sdaUndeployItem.getName();
		this.vendor = sdaUndeployItem.getVendor();
		this.version = sdaUndeployItem.getVersion();
		this.undeployItemStatus = sdaUndeployItem.getUndeployItemStatus();
		this.description = sdaUndeployItem.getDescription();
		this.properties = sdaUndeployItem.getProperties();
	}

	public void addDepending(UndeployItem sdaUndeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public Set getDepending() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public Sda getSda() {
		return this.sda;
	}

	public UndeployItemStatus getUndeployItemStatus() {
		return this.undeployItemStatus;
	}

	public String getDescription() {
		return this.description;
	}

	public Map getProperties() {
		return this.properties;
	}

	public void setDescription(String description) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setProperties(Map properties) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void removeDepending(UndeployItem sdaUndeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void addDependingOnThis(UndeployItem sdaUndeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public Set getDependingOnThis() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public UndeployItemId getId() {
		return this.id;
	}

	public String getLocation() {
		return this.location;
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public Version getVersion() {
		return this.version;
	}

	public void removeAllDependingOnThis() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void removeDependingOnThis(UndeployItem sdaUndeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setSda(Sda arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setUndeployItemStatus(UndeployItemStatus arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void accept(UndeployItemVisitor visitor) {
		visitor.visit(this);
	}

	public Sdu getSdu() {
		return getSda();
	}

	public void addUndeployItemObserver(UndeployItemObserver observer) {
		throw new UnsupportedOperationException(errorMsg);	
	}

	public void removeUndeployItemObserver(UndeployItemObserver observer) {
		throw new UnsupportedOperationException(errorMsg);
	}
	
	
	
}
