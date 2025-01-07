package com.sap.engine.services.dc.event;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class UndeploymentEvent extends DCEvent {

	private static final long serialVersionUID = -4108399230657983655L;

	private final GenericUndeployItem undeployItem;
	private final UndeploymentEventAction undeploymentEventAction;
	private final String toString;

	public UndeploymentEvent(GenericUndeployItem undeployItem,
			UndeploymentEventAction undeploymentEventAction) {
		super(undeployItem, undeploymentEventAction);

		this.undeployItem = undeployItem;
		this.undeploymentEventAction = undeploymentEventAction;
		this.toString = genToString();
	}

	public UndeployItem getUndeployItem() {
		return (undeployItem instanceof UndeployItem)?(UndeployItem)undeployItem:null;
	}
	
	public GenericUndeployItem getGenericUndeployItem() {
		return undeployItem;
	}
	

	public UndeploymentEventAction getUndeploymentEventAction() {
		return this.undeploymentEventAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.event.DCEvent#accept(com.sap.engine.services
	 * .dc.event.DCEventVisitor)
	 */
	public void accept(DCEventVisitor visitor) {
		visitor.visit(this);
	}

	public String toString() {
		return this.toString;
	}

	private String genToString() {
		return "Undeployment Event, action: " + undeploymentEventAction
				+ ", component: " + undeployItem;
	}

}
