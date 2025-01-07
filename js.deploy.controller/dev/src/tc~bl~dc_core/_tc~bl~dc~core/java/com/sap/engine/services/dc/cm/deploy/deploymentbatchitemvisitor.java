package com.sap.engine.services.dc.cm.deploy;

/**
 * 
 * Title: J2EE Deployment Team Description: A general interface which provides
 * operation for visiting a deployment item. It depends on the concrete
 * implementation how the visiting operation will behave. The type
 * <code>DeploymentBatchItemVisitor</code> acts as the design pattern Visitor.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 * @see com.sap.engine.services.dc.cm.deploy.DeploymentItem
 * 
 */
public interface DeploymentBatchItemVisitor {

	public void visit(DeploymentItem deploymentItem);

	public void visit(CompositeDeploymentItem deploymentItem);

}
