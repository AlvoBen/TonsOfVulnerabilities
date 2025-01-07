package com.sap.engine.services.dc.event.msg.impl;

import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.event.msg.MessageEventDeploymentBatchItem;
import com.sap.engine.services.dc.event.msg.MessageEventFactory;
import com.sap.engine.services.dc.event.msg.MessageEventUndeployItem;

public final class MessageEventFactoryImpl extends MessageEventFactory implements UndeployItemVisitor {

	private MessageEventUndeployItem eventUndeployItem;
	
	public MessageEventFactoryImpl() {
	}

	public MessageEventDeploymentBatchItem createMessageEventDeploymentBatchItem(
			DeploymentBatchItem deploymentBatchItem) {
		return new MessageEventDeploymentBatchItemImpl(deploymentBatchItem);
	}

	public MessageEventUndeployItem createMessageEventUndeployItem(
			GenericUndeployItem undeployItem) {
		undeployItem.accept(this);
		return eventUndeployItem;
	}

	public void visit(UndeployItem undeployItem) {
		eventUndeployItem = new MessageEventUndeployItemImpl(undeployItem);
	}

	public void visit(ScaUndeployItem undeployItem) {
		eventUndeployItem = new MessageEventScaUndeployItemImpl(undeployItem);		
	}
	
	
}
