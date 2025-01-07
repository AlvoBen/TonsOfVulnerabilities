package com.sap.jms.client.message;

import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.jms.JMSException;

import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.protocol.PacketImpl;

public class JMSMessageProxy implements Serializable {
	
	PacketImpl packet = null;
	
	public JMSMessageProxy(PacketImpl packet) {
		this.packet = packet;
	}

	public Object readResolve() throws ObjectStreamException, JMSException {
		JMSMessage message = null;

		switch (packet.getPacketType()) {
			case MessageRequest.JMS_BYTES_MESSAGE :
				message = new JMSBytesMessage((MessageRequest) packet);
				break;
			case MessageRequest.JMS_MAP_MESSAGE :
				message = new JMSMapMessage((MessageRequest) packet);
				break;
			case MessageRequest.JMS_TEXT_MESSAGE :
				message = new JMSTextMessage((MessageRequest) packet);
				break;
			case MessageRequest.JMS_STREAM_MESSAGE :
				message = new JMSStreamMessage((MessageRequest) packet);
				break;
			case MessageRequest.JMS_OBJECT_MESSAGE :
				message = new JMSObjectMessage((MessageRequest) packet);
				break;
			case MessageRequest.JMS_GENERIC_MESSAGE :
				message = new JMSMessage((MessageRequest) packet);
				break;
			default :
				javax.jms.JMSException jmse = new javax.jms.JMSException("Incorrect message received.");
				throw jmse;
		}		
		return message;
	}
}
