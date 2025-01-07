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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;


public final class Util {

    public final static Map<Byte, PacketClassInfo> packet_classes = new HashMap<Byte, PacketClassInfo>();
    
    protected static class PacketClassInfo {
		public final Class packetClass;
		
		public final String packetName;
		
		public PacketClassInfo(String packetName, Class packetClass) {
			this.packetClass = packetClass;
			this.packetName = packetName;
		}
	}
	
	private static void registerPacket(byte typeId, String packetName, Class packetClass) {
		packet_classes.put(typeId, new PacketClassInfo(packetName, packetClass));
	}
	    
    static {
    	registerPacket(PacketTypes.JMS_BYTES_MESSAGE, "JMSBytesMessage", com.sap.jms.protocol.MessageRequest.class);
		registerPacket(PacketTypes.JMS_TEXT_MESSAGE, "JMSTextMessage", com.sap.jms.protocol.MessageRequest.class);
		registerPacket(PacketTypes.JMS_STREAM_MESSAGE, "JMSStreamMessage", com.sap.jms.protocol.MessageRequest.class);
		registerPacket(PacketTypes.JMS_MAP_MESSAGE, "JMSMapMessage", com.sap.jms.protocol.MessageRequest.class);
		registerPacket(PacketTypes.JMS_OBJECT_MESSAGE, "JMSObjectMessage", com.sap.jms.protocol.MessageRequest.class);
		registerPacket(PacketTypes.JMS_GENERIC_MESSAGE, "JMSGenericMessage", com.sap.jms.protocol.MessageRequest.class);
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
        PacketClassInfo packetClassInfo = (PacketClassInfo) packet_classes.get(packetType);
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

        PacketClassInfo packetClassInfo = (PacketClassInfo) packet_classes.get(type);
        PacketImpl packet = null;
        try {
            packet = (PacketImpl) packetClassInfo.packetClass.newInstance();
            packet.setBuffer(buffer, offset, length);
        } catch (Exception ex) {
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
