/**
 * Util.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client;

import java.io.IOException;
import java.io.InputStream;
import com.sap.jms.util.compat.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;


public final class Util {

    public final static Map/*<Byte, PacketClassInfo>*/ packet_classes = new HashMap/*<Byte, PacketClassInfo>*/();
    
    protected static class PacketClassInfo {
		public final Class packetClass;
		
		public final String packetName;
		
		public PacketClassInfo(String packetName, Class packetClass) {
			this.packetClass = packetClass;
			this.packetName = packetName;
		}
	}
	
	private static void registerPacket(byte typeId, String packetName, Class packetClass) {
		packet_classes.put(new Byte(typeId), new PacketClassInfo(packetName, packetClass));
	}
	    
    static {
        //------------------------------------------------------------
        //  Build a hashtable which associates message type ids
        //  with packet classes.
        //------------------------------------------------------------
        
        //------------------------------------------------------------
        //  Connection related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.CONNECTION_STOP_RESPONSE, "ConnectionStopResponse", com.sap.jms.protocol.notification.ConnectionStopResponse.class);
		registerPacket(PacketTypes.CONNECTION_STOP_REQUEST, "ConnectionStopRequest", com.sap.jms.protocol.notification.ConnectionStopRequest.class);
		registerPacket(PacketTypes.CONNECTION_START_RESPONSE, "ConnectionStartResponse", com.sap.jms.protocol.notification.ConnectionStartResponse.class);
		registerPacket(PacketTypes.CONNECTION_START_REQUEST, "ConnectionStartRequest", com.sap.jms.protocol.notification.ConnectionStartRequest.class);
		registerPacket(PacketTypes.CONNECTION_CREATE_RESPONSE, "ConnectionCreateResponse", com.sap.jms.protocol.notification.ConnectionCreateResponse.class);
		registerPacket(PacketTypes.CONNECTION_CLOSE_RESPONSE, "ConnectionCloseResponse", com.sap.jms.protocol.notification.ConnectionCloseResponse.class);
		registerPacket(PacketTypes.CONNECTION_CLOSE_REQUEST, "ConnectionCloseRequest", com.sap.jms.protocol.notification.ConnectionCloseRequest.class);
		registerPacket(PacketTypes.CONNECTION_CREATE_REQUEST, "ConnectionCreateRequest", com.sap.jms.protocol.notification.ConnectionCreateRequest.class);
		
        //------------------------------------------------------------
        //  Session related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.SESSION_RECOVER_RESPONSE, "SessionRecoverResponse", com.sap.jms.protocol.notification.SessionRecoverResponse.class);
		registerPacket(PacketTypes.SESSION_ROLLBACK_REQUEST, "SessionRollbackRequest", com.sap.jms.protocol.notification.SessionRollbackRequest.class);
		registerPacket(PacketTypes.SESSION_RECOVER_REQUEST, "SessionRecoverRequest", com.sap.jms.protocol.notification.SessionRecoverRequest.class);
		registerPacket(PacketTypes.SESSION_COMMIT_REQUEST, "SessionCommitRequest", com.sap.jms.protocol.notification.SessionCommitRequest.class);
		registerPacket(PacketTypes.SESSION_CREATE_REQUEST, "SessionCreateRequest", com.sap.jms.protocol.notification.SessionCreateRequest.class);
		registerPacket(PacketTypes.SESSION_STOP_RESPONSE, "SessionStopResponse", com.sap.jms.protocol.notification.SessionStopResponse.class);
		registerPacket(PacketTypes.SESSION_STOP_REQUEST, "SessionStopRequest", com.sap.jms.protocol.notification.SessionStopRequest.class);
		registerPacket(PacketTypes.SESSION_START_RESPONSE, "SessionStartResponse", com.sap.jms.protocol.notification.SessionStartResponse.class);
		registerPacket(PacketTypes.SESSION_START_REQUEST, "SessionStartRequest", com.sap.jms.protocol.notification.SessionStartRequest.class);
		registerPacket(PacketTypes.SESSION_ROLLBACK_RESPONSE, "SessionRollbackResponse", com.sap.jms.protocol.notification.SessionRollbackResponse.class);
		registerPacket(PacketTypes.SESSION_CREATE_RESPONSE, "SessionCreateResponse", com.sap.jms.protocol.notification.SessionCreateResponse.class);
		registerPacket(PacketTypes.SESSION_COMMIT_RESPONSE, "SessionCommitResponse", com.sap.jms.protocol.notification.SessionCommitResponse.class);
		registerPacket(PacketTypes.SESSION_CLOSE_RESPONSE, "SessionCloseResponse", com.sap.jms.protocol.notification.SessionCloseResponse.class);
		registerPacket(PacketTypes.SESSION_CLOSE_REQUEST, "SessionCloseRequest", com.sap.jms.protocol.notification.SessionCloseRequest.class);
		registerPacket(PacketTypes.SESSION_AFTER_COMPLETION_REQUEST, "SessionAfterCompletionRequest", com.sap.jms.protocol.notification.SessionAfterCompletionRequest.class);
		registerPacket(PacketTypes.SESSION_AFTER_COMPLETION_RESPONSE, "SessionAfterCompletionResponse", com.sap.jms.protocol.notification.SessionAfterCompletionResponse.class);
		registerPacket(PacketTypes.SESSION_BEFORE_COMPLETION_REQUEST, "SessionBeforeCompletionRequest", com.sap.jms.protocol.notification.SessionBeforeCompletionRequest.class);
		registerPacket(PacketTypes.SESSION_BEFORE_COMPLETION_RESPONSE, "SessionBeforeCompletionResponse", com.sap.jms.protocol.notification.SessionBeforeCompletionResponse.class);
		registerPacket(PacketTypes.SESSION_AFTER_BEGIN_REQUEST, "SessionAfterBeginRequest", com.sap.jms.protocol.notification.SessionAfterBeginRequest.class);
		registerPacket(PacketTypes.SESSION_AFTER_BEGIN_RESPONSE, "SessionAfterBeginResponse", com.sap.jms.protocol.notification.SessionAfterBeginResponse.class);

        //------------------------------------------------------------
        //  Producer related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.PRODUCER_CREATE_RESPONSE, "ProducerCreateResponse", com.sap.jms.protocol.notification.ProducerCreateResponse.class);
		registerPacket(PacketTypes.PRODUCER_CREATE_REQUEST, "ProducerCreateRequest", com.sap.jms.protocol.notification.ProducerCreateRequest.class);
		registerPacket(PacketTypes.PRODUCER_CLOSE_RESPONSE, "ProducerCloseResponse", com.sap.jms.protocol.notification.ProducerCloseResponse.class);
		registerPacket(PacketTypes.PRODUCER_CLOSE_REQUEST, "ProducerCloseRequest", com.sap.jms.protocol.notification.ProducerCloseRequest.class);

        //------------------------------------------------------------
        //  Consumer related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.CONSUMER_CREATE_RESPONSE, "ConsumerCreateResponse", com.sap.jms.protocol.notification.ConsumerCreateResponse.class);
		registerPacket(PacketTypes.CONSUMER_CREATE_REQUEST, "ConsumerCreateRequest", com.sap.jms.protocol.notification.ConsumerCreateRequest.class);
		registerPacket(PacketTypes.CONSUMER_CLOSE_RESPONSE, "ConsumerCloseResponse", com.sap.jms.protocol.notification.ConsumerCloseResponse.class);
		registerPacket(PacketTypes.CONSUMER_CLOSE_REQUEST, "ConsumerCloseRequest", com.sap.jms.protocol.notification.ConsumerCloseRequest.class);

        //------------------------------------------------------------
        //  QueueBrowser related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.QUEUEBROWSER_ENUMERATION_RESPONSE, "QueueBrowserEnumerationResponse", com.sap.jms.protocol.notification.QueueBrowserEnumerationResponse.class);
		registerPacket(PacketTypes.QUEUEBROWSER_ENUMERATION_REQUEST, "QueueBrowserEnumerationRequest", com.sap.jms.protocol.notification.QueueBrowserEnumerationRequest.class);
		registerPacket(PacketTypes.QUEUEBROWSER_CREATE_RESPONSE, "QueueBrowserCreateResponse", com.sap.jms.protocol.notification.QueueBrowserCreateResponse.class);
		registerPacket(PacketTypes.QUEUEBROWSER_CREATE_REQUEST, "QueueBrowserCreateRequest", com.sap.jms.protocol.notification.QueueBrowserCreateRequest.class);
		registerPacket(PacketTypes.QUEUEBROWSER_CLOSE_RESPONSE, "QueueBrowserCloseResponse", com.sap.jms.protocol.notification.QueueBrowserCloseResponse.class);
		registerPacket(PacketTypes.QUEUEBROWSER_CLOSE_REQUEST, "QueueBrowserCloseRequest", com.sap.jms.protocol.notification.QueueBrowserCloseRequest.class);
		registerPacket(PacketTypes.CONSUMER_REFRESH_REQUEST, "ConsumerRefreshRequest", com.sap.jms.protocol.notification.ConsumerRefreshRequest.class);
		registerPacket(PacketTypes.CONSUMER_REFRESH_RESPONSE, "ConsumerRefreshResponse", com.sap.jms.protocol.notification.ConsumerRefreshResponse.class);
		
        //------------------------------------------------------------
        //  Subscription related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.SUBSCRIPTION_REMOVE_RESPONSE, "SubscriptionRemoveResponse", com.sap.jms.protocol.notification.SubscriptionRemoveResponse.class);
		registerPacket(PacketTypes.SUBSCRIPTION_REMOVE_REQUEST, "SubscriptionRemoveRequest", com.sap.jms.protocol.notification.SubscriptionRemoveRequest.class);

        //------------------------------------------------------------
        //  Destination related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.DESTINATION_DELETE_RESPONSE, "DestinationDeleteResponse", com.sap.jms.protocol.notification.DestinationDeleteResponse.class);
		registerPacket(PacketTypes.DESTINATION_DELETE_REQUEST, "DestinationDeleteRequest", com.sap.jms.protocol.notification.DestinationDeleteRequest.class);
		registerPacket(PacketTypes.DESTINATION_CREATE_RESPONSE, "DestinationCreateResponse", com.sap.jms.protocol.notification.DestinationCreateResponse.class);
		registerPacket(PacketTypes.DESTINATION_CREATE_REQUEST, "DestinationCreateRequest", com.sap.jms.protocol.notification.DestinationCreateRequest.class);
		registerPacket(PacketTypes.DESTINATION_NAME_RESPONSE, "DestinationNameResponse", com.sap.jms.protocol.notification.DestinationNameResponse.class);
		registerPacket(PacketTypes.DESTINATION_NAME_REQUEST, "DestinationNameRequest", com.sap.jms.protocol.notification.DestinationNameRequest.class);

        //------------------------------------------------------------
        //  Exception related packets
        //------------------------------------------------------------
		registerPacket(PacketTypes.XA_RESPONSE, "XAResponse", com.sap.jms.protocol.notification.XAResponse.class);
		registerPacket(PacketTypes.XA_RECOVER_RESPONSE, "XARecoverResponse", com.sap.jms.protocol.notification.XARecoverResponse.class);
		registerPacket(PacketTypes.XA_RECOVER_REQUEST, "XARecoverRequest", com.sap.jms.protocol.notification.XARecoverRequest.class);
		registerPacket(PacketTypes.XA_START_REQUEST, "XAStartRequest", com.sap.jms.protocol.notification.XAStartRequest.class);
		registerPacket(PacketTypes.XA_ROLLBACK_REQUEST, "XARollbackRequest", com.sap.jms.protocol.notification.XARollbackRequest.class);
		registerPacket(PacketTypes.XA_PREPARE_RESPONSE, "XAPrepareResponse", com.sap.jms.protocol.notification.XAPrepareResponse.class);
		registerPacket(PacketTypes.XA_PREPARE_REQUEST, "XAPrepareRequest", com.sap.jms.protocol.notification.XAPrepareRequest.class);
		registerPacket(PacketTypes.XA_TIMEOUT_RESPONSE, "XATimeoutResponse", com.sap.jms.protocol.notification.XATimeoutResponse.class);
		registerPacket(PacketTypes.XA_TIMEOUT_REQUEST, "XATimeoutRequest", com.sap.jms.protocol.notification.XATimeoutRequest.class);
		registerPacket(PacketTypes.XA_END_REQUEST, "XAEndRequest", com.sap.jms.protocol.notification.XAEndRequest.class);
		registerPacket(PacketTypes.XA_COMMIT_REQUEST, "XACommitRequest", com.sap.jms.protocol.notification.XACommitRequest.class);
		registerPacket(PacketTypes.XA_FORGET_REQUEST, "XAForgetRequest", com.sap.jms.protocol.notification.XAForgetRequest.class);
		registerPacket(PacketTypes.START_MESSAGE_DELIVERY_RESPONSE, "StartMessageDeliveryResponse", com.sap.jms.protocol.notification.StartMessageDeliveryResponse.class);
		registerPacket(PacketTypes.START_MESSAGE_DELIVERY_REQUEST, "StartMessageDeliveryRequest", com.sap.jms.protocol.notification.StartMessageDeliveryRequest.class);
		registerPacket(PacketTypes.SERVER_EXCEPTION_RESPONSE, "ServerExceptionResponse", com.sap.jms.protocol.notification.ServerExceptionResponse.class);
		registerPacket(PacketTypes.MESSAGE_ACKNOWLEDGE_REQUEST, "MessageAcknowledgeRequest", com.sap.jms.protocol.notification.MessageAcknowledgeRequest.class);
		registerPacket(PacketTypes.MESSAGE_RESPONSE, "MessageResponse", com.sap.jms.protocol.message.MessageResponse.class);
		registerPacket(PacketTypes.CONFIGURATION_PROPERTIES_PACKET, "ConfigurationPropertiesPacket", com.sap.jms.protocol.ConfigurationPropertiesPacket.class);
		registerPacket(PacketTypes.MESSAGE_ACKNOWLEDGE_RESPONSE, "MessageAcknowledgeResponse", com.sap.jms.protocol.notification.MessageAcknowledgeResponse.class);

        //------------------------------------------------------------
        //  Message related packets
        //------------------------------------------------------------
    	registerPacket(PacketTypes.JMS_BYTES_MESSAGE, "JMSBytesMessage", com.sap.jms.protocol.message.MessageRequest.class);
		registerPacket(PacketTypes.JMS_TEXT_MESSAGE, "JMSTextMessage", com.sap.jms.protocol.message.MessageRequest.class);
		registerPacket(PacketTypes.JMS_STREAM_MESSAGE, "JMSStreamMessage", com.sap.jms.protocol.message.MessageRequest.class);
		registerPacket(PacketTypes.JMS_MAP_MESSAGE, "JMSMapMessage", com.sap.jms.protocol.message.MessageRequest.class);
		registerPacket(PacketTypes.JMS_OBJECT_MESSAGE, "JMSObjectMessage", com.sap.jms.protocol.message.MessageRequest.class);
		registerPacket(PacketTypes.JMS_GENERIC_MESSAGE, "JMSGenericMessage", com.sap.jms.protocol.message.MessageRequest.class);
    }
	
	protected static MemoryAllocator MEMORY_ALLOCATOR = new MemoryAllocator();
	/**
	 *  Default implementation of the memory allocator
	 */
    public static class MemoryAllocator {
    	public byte[] malloc(int size) {
    		return new byte[size];
    	} 

    	public void free(byte[] memory) {
    	}
    }

	/**
	 *  Returns the memory allocator used by the factory
	 * 	@return the memory allocator used by the factory
	 */
	public static MemoryAllocator getMemoryAllocator() {
		return MEMORY_ALLOCATOR;
	}

	/**
     * Returns the packet type as a string
	 */
    public static String getPacketTypeAsString(byte packetType) {
        PacketClassInfo packetClassInfo = (PacketClassInfo) packet_classes.get(new Byte(packetType));
        return packetClassInfo == null ? "Unknown" : packetClassInfo.packetName;
    }
	
    /**
     *  Creates a new packet from the data supplied by the buffer
     *  @param buffer the buffer which holds the packet's data
     *  @param offset the offset into the data buffer
     *  @param length the length of the data in the buffer
     * @exception JMSException thrown if something went wrong
     */
    public static Packet createPacket(byte[] buffer, int offset, int length) throws JMSException {
        byte type = buffer[offset + PacketImpl.POS_PACKET_TYPE];

        PacketClassInfo packetClassInfo = (PacketClassInfo) packet_classes.get(new Byte(type));
        PacketImpl packet = null;
        try {
            packet = (PacketImpl) packetClassInfo.packetClass.newInstance();
            packet.setBuffer(buffer, offset, length);
        }
        catch (Exception ex) {
        	StringWriter buf = new StringWriter();
        	ex.printStackTrace(new PrintWriter(buf));
            JMSException ex2 = new JMSException("Could not create packet for packet type = " + type + "\n" + buf.toString());
            ex2.initCause(ex);
            ex2.setLinkedException(ex);
            throw ex2;
        }
        return packet;
    }

    /**
     *  Reads data from a stream and creates a packet from the data
     *  @param is the stream to read the data from
     *  @param packet_length_buffer a four bytes array in which the length
     *  of the incoming message will be read
     *  @return the packet which holds the data
     * 
     *  @exception IOException thrown if an i/o related exception occured during reading the data
     *  @exception JMSException thrown if a JMS related exception occured
     */
    public static Packet createPacket(InputStream is, byte[] packet_length_buffer) throws IOException, JMSException {
        int offset, packet_length, n;

        //---------------------------------------------------------------
        //  First, read length of the packet
        //---------------------------------------------------------------
        offset = is.read(packet_length_buffer);

        if (offset < Packet.SIZEOF_INT) {
            throw new IOException("Unexpected end of stream");
        } //if

        packet_length = PacketImpl.getIntInternal(packet_length_buffer, 0);
        byte[] buffer = MEMORY_ALLOCATOR.malloc(packet_length);
        System.arraycopy(packet_length_buffer, 0, buffer, 0, packet_length_buffer.length);

        n = offset;
        while (offset < packet_length) {
            n = is.read(buffer, offset, packet_length - offset);
            if (n < 0) break;

            offset += n;
        } //while

        if (offset < packet_length || offset < Packet.LEN_PACKET_HEADER) {
            throw new IOException("Unexpected end of stream");
        } //if

        return createPacket(buffer, 0, packet_length);
    }

    /**
     * Releases a data packet.
     * @param packet the data packet
     * @exception JMSException thrown if something went wrong
     */
    public static void releasePacket(Packet packet) {
    	if(packet != null){
    		((PacketImpl)packet).releaseBuffer();
    	}
    }

    ////////////////////// END OF MOVED FROM PacketFactoryBaseImpl
    
    public static final LogService LOG_SERVICE = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
   
	/**
	 * Method getJMSExceptionStackTrace. Returns the stack trace of a jms exceptin followed by
	 * the stack traces of all linked exceptions recursively.
	 * @param jmse the jms exception for which we need the full stack trace
	 * @return String the resultin stack trace
	 */
  	public static String getJMSExceptionStackTrace(JMSException jmse) {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		jmse.printStackTrace(writer);
		Exception ex = jmse.getLinkedException();

		while (ex != null) {
			ex.printStackTrace(writer);

			if (ex instanceof JMSException) {
				ex = ((JMSException) ex).getLinkedException();
			} else {
				ex = null;
			}
		}

		return sw.toString();
	}
  
  	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		t.printStackTrace(writer);
		return sw.toString();
	}
}
