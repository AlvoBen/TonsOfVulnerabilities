package com.sap.engine.services.dc.manage.messaging;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class MessagingFactory {

	private static MessagingFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.manage.messaging.impl.MessagingFactoryImpl";

	protected MessagingFactory() {
	}

	public static synchronized MessagingFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static MessagingFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (MessagingFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003333 An error occurred while creating an instance of "
					+ "class MessagingFactory. "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Message createMessage(int clusterId, int messageType,
			byte[] body, int offset, int length);

	public abstract Message createMessage(int groupId, byte nodeType,
			int messageType, byte[] body, int offset, int length);

	public abstract MessageProcessor createMessageProcessor();

	public abstract MessageSender createMessageSender();

}