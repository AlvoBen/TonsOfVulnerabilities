package com.sap.engine.services.dc.manage.handle.impl;

import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.services.dc.manage.messaging.Message;
import com.sap.engine.services.dc.manage.messaging.MessageProcessor;
import com.sap.engine.services.dc.manage.messaging.MessagingException;
import com.sap.engine.services.dc.manage.messaging.MessagingFactory;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

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
final class MessageHandler implements MessageListener {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static final MessageHandler INSTANCE = new MessageHandler();

	private MessageHandler() {
	}

	static MessageHandler getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.frame.cluster.message.MessageListener#receive(int,
	 * int, byte[], int, int)
	 */
	public void receive(int clusterId, int messageType, byte[] messageBody,
			int offset, int length) {
		final Message message = MessagingFactory.getInstance().createMessage(
				clusterId, messageType, messageBody, offset, length);

		final MessageProcessor msgProcessor = MessagingFactory.getInstance()
				.createMessageProcessor();

		try {
			msgProcessor.process(message);
		} catch (MessagingException me) {
			DCLog
					.logErrorThrowable(location, null,
							"An error occurred while processing the message "
									+ message, me);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.frame.cluster.message.MessageListener#receiveWait(int,
	 * int, byte[], int, int)
	 */
	public MessageAnswer receiveWait(int clusterId, int messageType,
			byte[] messageBody, int offset, int length) throws Exception {
		final Message message = MessagingFactory.getInstance().createMessage(
				clusterId, messageType, messageBody, offset, length);

		final MessageProcessor msgProcessor = MessagingFactory.getInstance()
				.createMessageProcessor();

		try {
			msgProcessor.process(message);
		} catch (Exception e) {
			DCLog
					.logErrorThrowable(location, null,
							"An error occurred while processing the message "
									+ message, e);
		} 
		return new MessageAnswer();
	}

}
