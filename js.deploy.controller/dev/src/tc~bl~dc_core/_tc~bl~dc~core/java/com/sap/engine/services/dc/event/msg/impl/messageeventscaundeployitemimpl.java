package com.sap.engine.services.dc.event.msg.impl;

import java.util.Set;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.event.msg.MessageEventUndeployItem;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.Version;

final class MessageEventScaUndeployItemImpl implements MessageEventUndeployItem, ScaUndeployItem {

	private static final long serialVersionUID = -8469763406360046120L;

	private static final String errorMsg = "[ERROR CODE DPL.DC.3459] Method is not supported for message event item.";

	private final UndeployItemId id;
	private final String location;
	private final Sca sca;
	private final String name;
	private final String vendor;
	private final Version version;
	private final UndeployItemStatus undeployItemStatus;
	private final String description;

	MessageEventScaUndeployItemImpl(ScaUndeployItem scaUndeployItem) {
		this.id = scaUndeployItem.getId();
		this.location = scaUndeployItem.getLocation();
		this.sca = scaUndeployItem.getSca();
		this.name = scaUndeployItem.getName();
		this.vendor = scaUndeployItem.getVendor();
		this.version = scaUndeployItem.getVersion();
		this.undeployItemStatus = scaUndeployItem.getUndeployItemStatus();
		this.description = scaUndeployItem.getDescription();
	}

	public void addDepending(GenericUndeployItem undeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public Set getDepending() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public Sca getSca() {
		return this.sca;
	}

	public UndeployItemStatus getUndeployItemStatus() {
		return this.undeployItemStatus;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void removeDepending(GenericUndeployItem undeployItem) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void addDependingOnThis(GenericUndeployItem undeployItem) {
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

	public void removeDependingOnThis(GenericUndeployItem arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setSca(Sca arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setUndeployItemStatus(UndeployItemStatus arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void accept(UndeployItemVisitor visitor) {
		visitor.visit(this);
	}

	public Sdu getSdu() {
		return getSca();
	}

	public void addUndeployItemObserver(UndeployItemObserver observer) {
		throw new UnsupportedOperationException(errorMsg);		
	}

	public void removeUndeployItemObserver(UndeployItemObserver observer) {
		throw new UnsupportedOperationException(errorMsg);		
	}
	
	
}
