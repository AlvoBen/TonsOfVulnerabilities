package com.sap.jms.client.session;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import com.sap.jms.client.message.JMSMessage;

public interface AckHandler {
	
	void acknowledge(Long consumerId, JMSMessage message) throws JMSException;
	void scheduleForAcknowledge(Long consumerId, JMSMessage message) throws JMSException;
	void redistributeMessagesWithoutConsumer(Map<Long, List<JMSMessage>> messagesWithoutConsumers) throws JMSException;
}
