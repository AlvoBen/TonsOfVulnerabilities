package com.sap.engine.services.dc.manage.messaging;

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
public interface MessageSender {

	public void send(Message message) throws MessagingException;

	public void sendToParticipant(Message message) throws MessagingException;
	
	public void sendAndWait(Message message) throws MessagingException;

}
