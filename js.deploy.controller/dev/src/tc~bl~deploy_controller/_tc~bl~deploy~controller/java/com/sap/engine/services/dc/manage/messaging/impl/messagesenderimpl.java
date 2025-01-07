package com.sap.engine.services.dc.manage.messaging.impl;

import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.manage.messaging.Message;
import com.sap.engine.services.dc.manage.messaging.MessageSender;
import com.sap.engine.services.dc.manage.messaging.MessagingException;

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
final class MessageSenderImpl implements MessageSender {

	private static final MessageSenderImpl INSTANCE = new MessageSenderImpl();

	private MessageSenderImpl() {
	}

	static MessageSenderImpl getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.manage.messaging.MessageSender#send(com.sap
	 * .engine.services.dc.manage.messaging.Message)
	 */
	public void send(Message message) throws MessagingException {
		final MessageContext messageCtx = ServiceConfigurer.getInstance()
				.getApplicationServiceContext().getClusterContext()
				.getMessageContext();

		try {
			messageCtx.send(message.getGroupId(), message.getNodeType(),
					message.getMessageType(), message.getBody(), message
							.getOffset(), message.getLength());
		} catch (ClusterException ce) {
			throw new MessagingException(
					"ASJ.dpl_dc.003328 An error occurred while sending the message "
							+ message, ce);
		}
	}

	public void sendToParticipant(Message message) throws MessagingException {
		final MessageContext messageCtx = ServiceConfigurer.getInstance()
				.getApplicationServiceContext().getClusterContext()
				.getMessageContext();

		try {
			messageCtx
					.send(message.getClusterId(), message.getMessageType(),
							message.getBody(), message.getOffset(), message
									.getLength());
		} catch (ClusterException ce) {
			throw new MessagingException(
					"An error occurred while sending the message " + message,
					ce);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.manage.messaging.MessageSender#send(com.sap
	 * .engine.services.dc.manage.messaging.Message)
	 */
	public void sendAndWait(Message message) throws MessagingException {
		final MessageContext messageCtx = ServiceConfigurer.getInstance()
				.getApplicationServiceContext().getClusterContext()
				.getMessageContext();

		try {
			messageCtx.sendAndWaitForAnswer(message.getGroupId(), message.getNodeType(),
					message.getMessageType(), message.getBody(),  message
							.getOffset(), message.getLength(), 0);
		} catch (ClusterException ce) {
			throw new MessagingException(
					"ASJ.dpl_dc.003332 An error occurred while sending the message "
							+ message, ce);
		}
	}
	
}
