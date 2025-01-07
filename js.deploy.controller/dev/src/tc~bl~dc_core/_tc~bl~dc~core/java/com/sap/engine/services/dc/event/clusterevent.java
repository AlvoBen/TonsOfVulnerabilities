package com.sap.engine.services.dc.event;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public class ClusterEvent extends DCEvent {

	private static final long serialVersionUID = 7266220152448507621L;

	private final ClusterEventAction clusterEventAction;
	private final String toString;

	public ClusterEvent(ClusterEventAction clusterEventAction) {
		super(null, clusterEventAction);

		this.clusterEventAction = clusterEventAction;
		this.toString = genToString();
	}

	public ClusterEventAction getClusterEventAction() {
		return this.clusterEventAction;
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
		return "Cluster Event, action: " + clusterEventAction;
	}

}
