package com.sap.engine.services.dc.event.msg;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;

public abstract class MessageEventFactory {

	private static MessageEventFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.event.msg.impl.MessageEventFactoryImpl";

	private final static String EOL = System.getProperty("line.separator");

	protected MessageEventFactory() {
	}

	public static synchronized MessageEventFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static MessageEventFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (MessageEventFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DC.3455] An error occurred while creating an instance of "
					+ "class MessageEventFactory! " + EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract MessageEventDeploymentBatchItem createMessageEventDeploymentBatchItem(
			DeploymentBatchItem deploymentBatchItem);

	public abstract MessageEventUndeployItem createMessageEventUndeployItem(
			GenericUndeployItem undeployItem);

}
