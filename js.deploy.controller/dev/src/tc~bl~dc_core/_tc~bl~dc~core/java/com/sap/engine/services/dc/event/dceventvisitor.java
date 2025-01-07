package com.sap.engine.services.dc.event;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface DCEventVisitor {

	public void visit(DeploymentEvent event);

	public void visit(UndeploymentEvent event);

	public void visit(ClusterEvent event);

	public void visit(LCEvent event);

}
