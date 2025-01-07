/**
 * PacketImpl.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import com.sap.jms.util.compat.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import com.sap.jms.client.Util;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.JMSConstants;

/**
 * The base class for a universal binary protocol.
 */

public class PacketImpl implements PacketReader, PacketWriter, Packet, PacketTypes, JMSConstants {
    
	
    private static final String LOG_COMPONENT = "PacketImpl";
    protected static final LogService log = LogServiceImpl.getLogService(LogServiceImpl.PROTOCOL_LOCATION);


    /** Map between packet IDs and packet names */
	protected static final Map/*<Byte, String>*/ PACKET_NAMES = new HashMap/*<Byte, String>*/();

	static {
		PACKET_NAMES.put(new Byte(JMS_BYTES_MESSAGE), "JMSBytesMessage");
		PACKET_NAMES.put(new Byte(JMS_TEXT_MESSAGE), "JMSTextMessage");
		PACKET_NAMES.put(new Byte(JMS_STREAM_MESSAGE), "JMSStreamMessage");
		PACKET_NAMES.put(new Byte(JMS_MAP_MESSAGE), "JMSMapMessage");
		PACKET_NAMES.put(new Byte(JMS_OBJECT_MESSAGE), "JMSObjectMessage");
		PACKET_NAMES.put(new Byte(JMS_GENERIC_MESSAGE), "JMSGenericMessage");

		PACKET_NAMES.put(new Byte(XA_START_REQUEST), "XAStartRequest");
		PACKET_NAMES.put(new Byte(XA_END_REQUEST), "XAEndRequest");
		PACKET_NAMES.put(new Byte(XA_COMMIT_REQUEST), "XACommitRequest");
		PACKET_NAMES.put(new Byte(XA_ROLLBACK_REQUEST), "XARollbackRequest");
		PACKET_NAMES.put(new Byte(XA_PREPARE_REQUEST), "XAPrepareRequest");
		PACKET_NAMES.put(new Byte(XA_RESPONSE), "XAResponse");
		PACKET_NAMES.put(new Byte(XA_RECOVER_REQUEST), "XARecoverRequest");
		PACKET_NAMES.put(new Byte(XA_RECOVER_RESPONSE), "XARecoverResponse");
		PACKET_NAMES.put(new Byte(XA_PREPARE_RESPONSE), "XAPrepareResponse");
		PACKET_NAMES.put(new Byte(XA_FORGET_REQUEST), "XAForgetRequest");
		PACKET_NAMES.put(new Byte(XA_TIMEOUT_REQUEST), "XATimeoutRequest");
		PACKET_NAMES.put(new Byte(XA_TIMEOUT_RESPONSE), "XATimeoutResponse");
		PACKET_NAMES.put(new Byte(SUBSCRIPTION_REMOVE_RESPONSE), "SubscriptionRemoveResponse");
		PACKET_NAMES.put(new Byte(SUBSCRIPTION_REMOVE_REQUEST), "SubscriptionRemoveRequest");
		PACKET_NAMES.put(new Byte(START_MESSAGE_DELIVERY_RESPONSE), "StartMessageDeliveryResponse");
		PACKET_NAMES.put(new Byte(START_MESSAGE_DELIVERY_REQUEST), "StartMessageDeliveryRequest");
		PACKET_NAMES.put(new Byte(SESSION_RECOVER_RESPONSE), "SessionRecoverResponse");
		PACKET_NAMES.put(new Byte(SERVER_EXCEPTION_RESPONSE), "ServerExceptionResponse");
		PACKET_NAMES.put(new Byte(SESSION_ROLLBACK_REQUEST), "SessionRollbackRequest");
		PACKET_NAMES.put(new Byte(SESSION_RECOVER_REQUEST), "SessionRecoverRequest");
		PACKET_NAMES.put(new Byte(SESSION_COMMIT_REQUEST), "SessionCommitRequest");
		PACKET_NAMES.put(new Byte(MESSAGE_ACKNOWLEDGE_REQUEST), "MessageAcknowledgeRequest");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_ENUMERATION_RESPONSE), "QueueBrowserEnumerationResponse");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_ENUMERATION_REQUEST), "QueueBrowserEnumerationRequest");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_CREATE_RESPONSE), "QueueBrowserCreateResponse");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_CREATE_REQUEST), "QueueBrowserCreateRequest");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_CLOSE_RESPONSE), "QueueBrowserCloseResponse");
		PACKET_NAMES.put(new Byte(QUEUEBROWSER_CLOSE_REQUEST), "QueueBrowserCloseRequest");
		PACKET_NAMES.put(new Byte(PRODUCER_CREATE_RESPONSE), "ProducerCreateResponse");
		PACKET_NAMES.put(new Byte(PRODUCER_CREATE_REQUEST), "ProducerCreateRequest");
		PACKET_NAMES.put(new Byte(PRODUCER_CLOSE_RESPONSE), "ProducerCloseResponse");
		PACKET_NAMES.put(new Byte(PRODUCER_CLOSE_REQUEST), "ProducerCloseRequest");
		PACKET_NAMES.put(new Byte(CONSUMER_REFRESH_REQUEST), "ConsumerRefreshRequest");
		PACKET_NAMES.put(new Byte(CONSUMER_CREATE_RESPONSE), "ConsumerCreateResponse");
		PACKET_NAMES.put(new Byte(CONSUMER_CREATE_REQUEST), "ConsumerCreateRequest");
		PACKET_NAMES.put(new Byte(CONSUMER_CLOSE_RESPONSE), "ConsumerCloseResponse");
		PACKET_NAMES.put(new Byte(CONSUMER_CLOSE_REQUEST), "ConsumerCloseRequest");
		PACKET_NAMES.put(new Byte(MESSAGE_RESPONSE), "MessageResponse");
		PACKET_NAMES.put(new Byte(SESSION_CREATE_REQUEST), "SessionCreateRequest");
		PACKET_NAMES.put(new Byte(SESSION_STOP_RESPONSE), "SessionStopResponse");
		PACKET_NAMES.put(new Byte(SESSION_STOP_REQUEST), "SessionStopRequest");
		PACKET_NAMES.put(new Byte(SESSION_START_RESPONSE), "SessionStartResponse");
		PACKET_NAMES.put(new Byte(SESSION_START_REQUEST), "SessionStartRequest");
		PACKET_NAMES.put(new Byte(SESSION_ROLLBACK_RESPONSE), "SessionRollbackResponse");
		PACKET_NAMES.put(new Byte(SESSION_CREATE_RESPONSE), "SessionCreateResponse");
		PACKET_NAMES.put(new Byte(SESSION_COMMIT_RESPONSE), "SessionCommitResponse");
		PACKET_NAMES.put(new Byte(SESSION_CLOSE_RESPONSE), "SessionCloseResponse");
		PACKET_NAMES.put(new Byte(SESSION_CLOSE_REQUEST), "SessionCloseRequest");
		PACKET_NAMES.put(new Byte(CONFIGURATION_PROPERTIES_PACKET), "ConfigurationPropertiesPacket");
		PACKET_NAMES.put(new Byte(DESTINATION_DELETE_RESPONSE), "DestinationDeleteResponse");
		PACKET_NAMES.put(new Byte(DESTINATION_DELETE_REQUEST), "DestinationDeleteRequest");
		PACKET_NAMES.put(new Byte(DESTINATION_CREATE_RESPONSE), "DestinationCreateResponse");
		PACKET_NAMES.put(new Byte(DESTINATION_CREATE_REQUEST), "DestinationCreateRequest");
		PACKET_NAMES.put(new Byte(CONNECTION_STOP_RESPONSE), "ConnectionStopResponse");
		PACKET_NAMES.put(new Byte(CONNECTION_STOP_REQUEST), "ConnectionStopRequest");
		PACKET_NAMES.put(new Byte(CONNECTION_START_RESPONSE), "ConnectionStartResponse");
		PACKET_NAMES.put(new Byte(CONNECTION_START_REQUEST), "ConnectionStartRequest");
		PACKET_NAMES.put(new Byte(CONNECTION_CREATE_RESPONSE), "ConnectionCreateResponse");
		PACKET_NAMES.put(new Byte(CONNECTION_CLOSE_RESPONSE), "ConnectionCloseResponse");
		PACKET_NAMES.put(new Byte(CONNECTION_CLOSE_REQUEST), "ConnectionCloseRequest");
		PACKET_NAMES.put(new Byte(MESSAGE_ACKNOWLEDGE_RESPONSE), "MessageAcknowledgeResponse");
		PACKET_NAMES.put(new Byte(DESTINATION_NAME_RESPONSE), "DestinationNameResponse");
		PACKET_NAMES.put(new Byte(DESTINATION_NAME_REQUEST), "DestinationNameRequest");
		PACKET_NAMES.put(new Byte(CONSUMER_REFRESH_RESPONSE), "ConsumerRefreshResponse");
		PACKET_NAMES.put(new Byte(CONNECTION_CREATE_REQUEST), "ConnectionCreateRequest");
	}
	
	
    /** Empty array */
    protected static byte[] EMPTY_ARRAY = null;
    
    /** Empty array size */
    private final int chunkSize = 102400;

    /** Flag if the implementation of the pool manager is empty */
    protected static boolean POOL_USED = false;

    /** The buffer which holds the data */
    protected byte[] m_buffer;

    /**
     *  The start index of the buffer.  This offset specifies the start
     *  of the memory area which is being used by the object. The memory
     *  area before this address will never be touched.
     */
    protected int m_start;

    /**
     *  The end of the buffer.  This offset specifies the end
     *  of the memory area which is being used by this object.
     *  The memory area beyond address will never be touched.
     */
    protected int m_end;

    /**
     *  Points to the first byte of the buffer for the payload
     */
    protected int m_payload_start;

    /**
     *  The current offset into the buffer.
     *  The offset lies in the range of m_start &lt;= m_position &lt m_end
     */
    protected int m_position;

    /**
     *  Creates a new JMSPacketImpl object
     */
    public PacketImpl() {
        super();
    } //PacketImpl

    /**
     *  Creates a new JMSPacketImpl object
     */
    protected PacketImpl(byte packet_type) {
        try {
            allocate(packet_type, 0);
        }
        catch (JMSException ex) {
            log.errorTrace(LOG_COMPONENT, "Could not construct PacketImpl.");
            log.exception(LOG_COMPONENT, ex);
        }
    } //PacketImpl

    /**
     *  Creates a new Packet object
     *  @param packet_type the packet type ID
     *  @param packet_size the size of the packet without the header
     */
    protected PacketImpl(byte packet_type, int packet_size) throws JMSException {
        allocate(packet_type, packet_size);
    } //PacketImpl

    /**
     * @throws JMSException
     *  Creates a new Packet object
     *  @param buffer the buffer for the protocol data
     *  @param offset the offset into the buffer
     *  @param length the length of the buffer
     */
    protected PacketImpl(byte[] buffer, int offset, int length) {
        setBuffer(buffer, offset, length);
    } //PacketImpl

    /**
     *  Returns a clone of the packet
     *  @return a clone of the packet
     */
    public Object clone() {
        PacketImpl packet = null;
        try {
            int length = getLength();
            byte[] buffer = Util.getMemoryAllocator().malloc(length);
            System.arraycopy(m_buffer, m_start, buffer, 0, Math.min((m_end - m_start), length));
            Class packet_class = getClass();
            packet = (PacketImpl) packet_class.newInstance();
            packet.setBuffer(buffer, 0, length);
        }
        catch (Exception ex) {
            packet = null;
        } //try
        return packet;

    } //clone

    /**
     *  Allocates memory and initializes the packet type and size fields
     *  @param packet_type the packet type ID
     *  @param packet_size the size of the packet without the header
     */
    protected void allocate(byte packet_type, int packet_size) throws JMSException {
        int size = LEN_PACKET_HEADER + packet_size;
        byte[] buffer = Util.getMemoryAllocator().malloc(size);
        if (m_buffer != null) {
            System.arraycopy(m_buffer, m_start, buffer, 0, Math.min((m_end - m_start), size));
            Util.getMemoryAllocator().free(m_buffer);
        } else {
        	if(POOL_USED) {
                synchronized (PacketImpl.class) {
                    if (EMPTY_ARRAY == null) {
                        EMPTY_ARRAY = new byte[chunkSize];
                    }
                }
                for(int i = 0; i < size; i += chunkSize) {
                    System.arraycopy(EMPTY_ARRAY, 0, buffer, i, Math.min(size - i, chunkSize));
                }
			}
        } //if
        setBuffer(buffer, 0, size);
        setIntInternal(POS_PACKET_SIZE, size);
        setByteInternal(POS_PACKET_TYPE, packet_type);
        //setByteInternal(POS_PACKET_FLAGS, (byte)0);
    } //allocate

    /**
     * @throws BufferUnderflowException
     *  Sets the buffer on which the protocol operates
     *
     *  @param buffer the buffer for the protocol data
     *  @param start the offset into the buffer
     *  @param capacity the length of the buffer
     */
    public void setBuffer(byte[] buffer, int start, int capacity) {
        m_buffer = buffer;
        m_start = start;
        m_end = start + capacity;
        m_payload_start = m_start + POS_PAYLOAD_START;
        if (m_end > m_buffer.length)
            m_end = m_buffer.length;
        m_position = m_payload_start;
    } //setBuffer

    /**
     *  Clears the buffer on which the protocol operates
     */
    public void releaseBuffer() {
        if (m_buffer != null)
        	Util.getMemoryAllocator().free(m_buffer);
        clearBuffer();
    } //releaseBuffer

    /**
     *  Clears the buffer on which the protocol operates
     */
    public void clearBuffer() {
        m_buffer = null;
        m_start = 0;
        m_end = 0;
        m_payload_start = 0;
        m_position = 0;
    } //clearBuffer

    /**
     *  Returns the start index into the packet's backing byte array
     *  @return the start index of the buffer
     */
    public final int getOffset() {
        return m_start;
    }

    /**
     *  Returns the overall size the packet's backing byte array
     *  @return the length of the byte buffer
     *  @throws JMSException
     */
    public final int getLength() throws JMSException {
        flush();
        return (m_end - m_start);
    }

    /**
     *  Returns a reference to the backing byte array
     *  @return the packet buffer
     *  @throws JMSException
     */
    public final byte[] getBuffer() throws JMSException {
        flush();
        return m_buffer;
    }

    /**
     *  Returns the capacity of the backing byte array
     *  @return the capacity of the backing byte array
     *  @throws JMSException
     */
    public final int getCapacity() throws JMSException {
        // The method is invoked only by BytesMesage().read - however as a result
        // of flushing the message the internal position in the packet may change dramatically
        // and point to different values (for example if setJMSCorrelationID is invoked - something that the current JMS supports.).
        // That's why we will remove the flushing, whenever an
        // application attempts to read the bytes, the application will read only values from
        // the existing byte array. 
        //flush();        
        return (m_end - m_payload_start);
    }

    /**
     *  Returns the current position in the buffer
     *  @return the buffer's position
     */
    public final int getPosition() {
        return (m_position - m_payload_start);
    }

    /**
     *  Sets the position in the buffer
     *  @param position the new position of the buffer's backing array
     *  @exception JMSException thrown of the position if out of range
     */
    public void setPosition(int position) throws BufferUnderflowException, BufferOverflowException {
        if ((m_payload_start + position) > m_end) {
        	log.errorTrace(LOG_COMPONENT, "PacketImpl.setPosition(). Try to set in position {0} .", new Integer(position));
        	printInfo();
			throw new BufferOverflowException("pos: " + position + " start: " + m_payload_start + " end: " + m_end);  

        }
        else if (((m_payload_start + position) < m_payload_start)) {
          log.errorTrace(LOG_COMPONENT, "PacketImpl.setPosition(). Try to set in position {0} .", new Integer(position));
        	printInfo();
			throw new BufferUnderflowException("pos: " + position + " start: " + m_payload_start + " end: " + m_end);  
        	
        } //if
        m_position = m_payload_start + position;
    }

    /**
     *  Checks whether this packet is a notification (system message) packet.
     *  @return <code>true</code> if this is a notification packet;
     * 			<code>false</code> otherwise.
     *  @exception JMSException thrown if the buffer has reached its end
     */
    public final boolean isNotification() throws JMSException {
        return !isMessage();
    }

    /**
     *  Checks whether this packet is a JMS message packet.
     *  @return <code>true</code> if this is a JMS message packet; <code>false</code> otherwise;
     *  @exception JMSException thrown if the buffer has prematurely reached its end
     */
    public final boolean isMessage() throws JMSException {
        return (getByteInternal(m_start + POS_PACKET_TYPE) > 0);
    }

    /**
     *  Returns the packet type
     *  @return the packet type
     *  @exception JMSException thrown if the buffer has prematurely reached its end
     */
    public final int getPacketType() throws JMSException {
        return getByteInternal(m_start + POS_PACKET_TYPE);
    }

    /**
     *  Sets the packet type
     *  @param packet_type the packet type ID
     */
    protected final void setPacketType(byte packet_type) throws JMSException {
        setByteInternal(m_start + POS_PACKET_TYPE, packet_type);
    }

    /**
     *  Returns the packet type as a string
     *  @return the packet type as a string
     *  @exception JMSException thrown if the buffer has prematurely reached its end
     */
    public final String getPacketTypeAsString() throws JMSException {
        byte type = getByteInternal(m_start + POS_PACKET_TYPE);
        return Util.getPacketTypeAsString(type);
    }

    /**
     *  Returns the request ID
     *  @return the request ID
     *  @exception JMSException thrown if the buffer has prematurely reached its end
     */
    public final long getRequestID() throws JMSException {
        return getLongInternal(m_start + POS_REQUEST_ID);
    }

    /**
     *  Sets the request ID
     *  @param request_id the request ID
     *  @exception JMSException thrown if the buffer has prematurely reached its end
     */
    public final void setRequestID(long request_id) throws JMSException {
        setLongInternal(m_start + POS_REQUEST_ID, request_id);
    }

    /**
     *  Checks the size of the buffer
     *  @param position the position where to store the data
     *  @param size the number of bytes which are going to be
     *              inserted into the buffer
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified amount of data
     */
    private int checkOverflow(int position, int size) throws BufferOverflowException {
        position += m_payload_start;
        if ((position < m_payload_start) || ((position + size) > m_end)) {
        	log.errorTrace(LOG_COMPONENT, "PacketImpl.checkOverflow(). Try to read {0},  bytes from position: {1}",  new Object[] {new Integer(size), new Integer(position)});
        	printInfo();
        	throw new BufferOverflowException("size: " + size + " pos: " + position + " start: " + m_payload_start + " end: " + m_end);  


        } //if
        return position;
    }

    /**
     *  Checks if the buffer contains at least the specified number of bytes
     *  @param position the position from which to start reading
     *  @param size the number of bytes which are going to be
     *              read into the buffer
     *  @exception BufferUnderflowException thrown of the buffer if not
     *              large enough to hold the specified amount of data
     */
    private int checkUnderflow(int position, int size) throws BufferUnderflowException {
        position += m_payload_start;
        if ((position < m_payload_start) || ((position + size) > m_end)) {
        	log.errorTrace(LOG_COMPONENT, "PacketImpl.checkUnderflow(). Try to read  {0},  bytes from position: {1}",  new Object[] {new Integer(size), new Integer(position)});
        	printInfo();
        	throw new BufferUnderflowException("size: " + size + " pos: " + position + " start: " + m_payload_start + " end: " + m_end);  

        } //if
        return position;
    }

    /**
     *  Resets the buffer's position to the start of the payload area
     */
    public final void reset() {
        m_position = m_payload_start;
    }

    /**
     *  Returns the byte from the specified location without moving the buffer's position
     *  @param position the position where to read the byte from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    protected final byte getByteInternal(int position) {
        return m_buffer[position];
    }

    /**
     *  Returns the byte from the specified location without moving the buffer's position
     *  @param position the position where to read the byte from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final byte getByte(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_BYTE);
        return m_buffer[position];
    }

    /**
     *  Returns the byte from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the byte as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final byte readByte() throws BufferUnderflowException {
        byte value = getByte(m_position - m_payload_start);
        m_position += SIZEOF_BYTE;
        return value;
    }

    /**
     *  Sets the byte at the specified position without moving the buffer's position
     *  @param position the position where to set the byte
     *  @param value the byte to set
     *  @exception BufferOverflowException thrown if the position is out of range
     */
    protected final PacketWriter setByteInternal(int position, byte value) throws BufferOverflowException {
        m_buffer[position] = value;
        return this;
    }

    /**
     *  Sets the byte at the specified position without moving the buffer's position
     *  @param position the position where to set the byte
     *  @param value the byte to set
     *  @exception BufferOverflowException thrown if the position is out of range
     */
    public final PacketWriter setByte(int position, byte value) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_BYTE);
        m_buffer[position] = value;
        return this;
    }

    /**
     *  Sets the byte at the current offset and advances the buffer's position
     *  @param value the byte value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeByte(byte value) throws BufferOverflowException {
        setByte(m_position - m_payload_start, value);
        m_position += SIZEOF_BYTE;
        return this;
    }

    /**
     *  Returns the byte array from the specified location without moving the buffer's position
     *  @param position the position where to read the bytes from
     *  @param length the number of bytes to read
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final byte[] getBytes(int position, int length) throws BufferUnderflowException {
        position = checkUnderflow(position, length);
        byte[] value = new byte[length];
        System.arraycopy(m_buffer, position, value, 0, length);
        return value;
    }

    /**
     *  Stores the bytes at the specified position without moving the buffer's position.
     *  @param position the position where to store the bytes
     *  @param value the bytes to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setBytes(int position, byte[] value) throws BufferOverflowException {
        int length = (value != null ? value.length : 0);
        return setBytes(position, value, 0, length);
    }

    /**
     *  Stores the bytes at the specified position without moving the buffer's position.
     *  @param position the position where to store the bytes
     *  @param value the bytes to store
     *  @param value the bytes to set
     *  @param offset the offset into the bytes array
     *  @param length the number of bytes to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setBytes(int position, byte[] value, int offset, int length) throws BufferOverflowException {
        m_position = encodeByteArray(position, value, offset, length, false);
        return this;
    }

    /**
     *  Returns the byte from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the byte as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final byte[] readByteArray() throws BufferUnderflowException {
        byte[] value = getByteArray(m_position - m_payload_start);
        m_position += value.length + SIZEOF_INT;
        return value;
    }

    /**
     *  Returns the byte array from the specified location without moving the buffer's position
     *  @param position the position where to read the byte array from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final byte[] getByteArray(int position) throws BufferUnderflowException {
        int length = getInt(position);
        position = checkUnderflow(position + SIZEOF_INT, length);
        byte[] value = new byte[length];
        System.arraycopy(m_buffer, position, value, 0, length);
        return value;
    }

    /**
     *  Stores the byte array at the specified position without moving the buffer's position.
     *  @param position the position where to store the byte array
     *  @param value the byte array to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setByteArray(int position, byte[] value) throws BufferOverflowException {
        int length = (value != null ? value.length : 0);
        return setByteArray(position, value, 0, length);
    }

    /**
     *  Stores the byte array at the specified position without moving the buffer's position.
     *  @param position the position where to store the byte array
     *  @param value the byte array to store
     *  @param value the byte array to set
     *  @param offset the offset into the byte array
     *  @param length the number of bytes to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setByteArray(int position, byte[] value, int offset, int length) throws BufferOverflowException {
        m_position = encodeByteArray(position, value, offset, length, true);
        return this;
    }

    /**
     *  Appends a byte array to the buffer without moving the buffer's position.
     *  @param position the position in the stream
     *  @param value the byte array to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeByteArray(byte[] value) throws BufferOverflowException {
        int length = (value != null ? value.length : 0);
        return writeByteArray(value, 0, length);
    }

    /**
     *  Stores the byte array at the current position and advances the buffer's position.
     *  @param value the byte array to store
     *  @param value the byte array to set
     *  @param offset the offset into the byte array
     *  @param length the number of bytes to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeByteArray(byte[] value, int offset, int length) throws BufferOverflowException {
        m_position = encodeByteArray(m_position - m_payload_start, value, offset, length, true);
        return this;
    }

    /**
     *  Stores the byte array at the specified position without moving the buffer's position.
     *  @param position the position where to store the byte array
     *  @param value the byte array to store
     *  @param value the byte array to set
     *  @param offset the offset into the byte array
     *  @param length the number of bytes to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    private final int encodeByteArray(int position, byte[] value, int offset, int length, boolean with_length)
        throws BufferOverflowException {
        length = (value != null ? (value.length < length ? value.length : length) : 0);
        position = checkOverflow(position, (length + (with_length ? SIZEOF_INT : 0)));
        if (with_length) {
            setIntInternal(position, length);
            position += SIZEOF_INT;
        } //if
        System.arraycopy(value, offset, m_buffer, position, length);
        return (position + length);
    }

    /**
     *  Returns the char from the specified location without moving the buffer's position
     *  @param position the position where to start reading
     *  @return the char as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has
     *      reached its end
     */
    public final char getChar(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_CHAR);
        return (char) ((m_buffer[position++] << 8) | m_buffer[position]);
    }

    /**
     *  Returns the char from the current location and advances the buffer's position
     *  @return the char as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final char readChar() throws BufferUnderflowException {
        char value = getChar(m_position - m_payload_start);
        m_position += SIZEOF_CHAR;
        return value;
    }

    /**
     *  Stores the char at the specified position without moving the buffer's position
     *  The character will be stored as a sequence of two bytes with the
     *  high byte coming first.
     *  @param value the character to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setChar(int position, char value) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_CHAR);
        m_buffer[position++] = (byte) ((value >>> 8) & 0xFF);
        m_buffer[position] = (byte) (value & 0xFF);
        return this;
    }

    /**
     *  Sets the char at the current offset and advances the buffer's position
     *  @param value the char value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeChar(char value) throws BufferOverflowException {
        setChar(m_position - m_payload_start, value);
        m_position += SIZEOF_CHAR;
        return this;
    }

    /**
     *  Returns the short from the specified location without moving the buffer's position
     *  @param position the position where to start reading
     *  @return the short as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has
     *      reached its end
     */
    public final short getShort(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_SHORT);
        return (short) (((m_buffer[position++] & 0xFF) << 8) | (m_buffer[position] & 0xFF));
    }

    /**
     *  Returns the short from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the short as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final short readShort() throws BufferUnderflowException {
        short value = getShort(m_position - m_payload_start);
        m_position += SIZEOF_SHORT;
        return value;
    }

    /**
     *  Stores the short at the specified position without moving the buffer's position
     *  The short will be stored as a sequence of two bytes with the
     *  high byte coming first.
     *  @param value the short to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setShort(int position, short value) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_SHORT);
        m_buffer[position++] = (byte) ((value >>> 8) & 0xFF);
        m_buffer[position] = (byte) (value & 0xFF);
        return this;
    }

    /**
     *  Sets the short at the current offset and advances the buffer's position
     *  @param value the short value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeShort(short value) throws BufferOverflowException {
        setShort(m_position - m_payload_start, value);
        m_position += SIZEOF_SHORT;
        return this;
    }

    /**
     *  Returns the int from the specified location without moving the buffer's position
     *  @param position the position where to read the int from
     */
    protected final int getIntInternal(int position) {
        return getIntInternal(m_buffer, position);
    }

    /**
     *  Returns the int from the specified location without moving the buffer's position
     *  @param position the position where to read the int from
     */
    public static final int getIntInternal(byte[] buffer, int position) {
        return (((buffer[position++] & 0xFF) << 24)
                | ((buffer[position++] & 0xFF) << 16)
                | ((buffer[position++] & 0xFF) << 8)
                | (buffer[position] & 0xFF));
    }

    /**
     *  Stores the int at the specified position without moving the buffer's position
     *  @param position the position where to store the int
     *  @param value the int to store
     *  @return the buffer's new position
     *  @param position the position where to read the int from
     */
    protected static final int setIntInternal(byte[] buffer, int position, int value) {
        buffer[position++] = (byte) ((value >>> 24) & 0xFF);
        buffer[position++] = (byte) ((value >>> 16) & 0xFF);
        buffer[position++] = (byte) ((value >>> 8) & 0xFF);
        buffer[position++] = (byte) (value & 0xFF);
        return position;
    }

    /**
     *  Returns the int from the specified location without moving the buffer's position
     *  @param position the position where to read the int from
     *  @exception BufferUnderflowException thrown if the buffer has
     *      reached its end
     */
    public final int getInt(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_INT);
        return getIntInternal(position);
    }

    /**
     *  Returns the int from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the int as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final int readInt() throws BufferUnderflowException {
        int value = getInt(m_position - m_payload_start);
        m_position += SIZEOF_INT;
        return value;
    }

    /**
     *  Stores the int at the specified position without moving the buffer's position
     *  @param position the position where to store the int
     *  @param value the int to store
     *  @return the buffer's new position
     *  @exception BufferOverflowException thrown if the buffer is not
     *              large enough to hold the specified data
     */
    protected final int setIntInternal(int position, int value) {
        m_buffer[position++] = (byte) ((value >>> 24) & 0xFF);
        m_buffer[position++] = (byte) ((value >>> 16) & 0xFF);
        m_buffer[position++] = (byte) ((value >>> 8) & 0xFF);
        m_buffer[position++] = (byte) (value & 0xFF);
        return position;
    }

    /**
     *  Stores the int at the specified position without moving the buffer's position
     *  @param position the position where to store the int
     *  @param value the int to store
     *  @exception BufferOverflowException thrown if the buffer is not
     *              large enough to hold the specified data
     */
    public final PacketWriter setInt(int position, int value) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_INT);
        setIntInternal(position, value);
        return this;
    }

    /**
     *  Sets the int at the current offset and advances the buffer's position
     *  @param value the int value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeInt(int value) throws BufferOverflowException {
        setInt(m_position - m_payload_start, value);
        m_position += SIZEOF_INT;
        return this;
    }

    /**
     *  Returns the long from the specified location without moving the buffer's position
     *  @param position the position where to read the long from
     */
    protected final long getLongInternal(int position) {
        return (((m_buffer[position++] & 0xFFL) << 56)
                | ((m_buffer[position++] & 0xFFL) << 48)
                | ((m_buffer[position++] & 0xFFL) << 40)
                | ((m_buffer[position++] & 0xFFL) << 32)
                | ((m_buffer[position++] & 0xFFL) << 24)
                | ((m_buffer[position++] & 0xFFL) << 16)
                | ((m_buffer[position++] & 0xFFL) << 8)
                | (m_buffer[position] & 0xFFL));
    }

    /**
     *  Returns the long from the specified location without moving the buffer's position
     *  @param position the position where to read the long from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final long getLong(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_LONG);
        return getLongInternal(position);
    }

    /**
     *  Returns the long from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the long as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final long readLong() throws BufferUnderflowException {
        long value = getLong(m_position - m_payload_start);
        m_position += SIZEOF_LONG;
        return value;
    }

    /**
     *  Stores the long at the specified position without moving the buffer's position
     *  @param position the position where to store the long
     *  @param value the long to store
     *  @exception BufferOverflowException thrown if the position is out of range
     */
    public final PacketWriter setLong(int position, long value) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_LONG);
        return setLongInternal(position, value);
    }

    /**
     *  Stores the long at the specified position without moving the buffer's position
     *  @param position the position where to store the long
     *  @param value the long to store
     *  @exception BufferOverflowException thrown if the position is out of range
     */
    protected final PacketWriter setLongInternal(int position, long value) {
        m_buffer[position++] = (byte) ((value >> 56) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 48) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 40) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 32) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 24) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 16) & 0xFF);
        m_buffer[position++] = (byte) ((value >> 8) & 0xFF);
        m_buffer[position] = (byte) (value & 0xFF);
        return this;
    }

    /**
     *  Sets the long at the current offset and advances the buffer's position
     *  @param value the long value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeLong(long value) throws BufferOverflowException {
        setLong(m_position - m_payload_start, value);
        m_position += SIZEOF_LONG;
        return this;
    }

    /**
     *  Returns the float from the specified location without moving the buffer's position
     *  @param position the position where to start reading
     *  @return the float as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final float getFloat(int position) throws BufferUnderflowException {
        return Float.intBitsToFloat(getInt(position));
    }

    /**
     *  Returns the float from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the float as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final float readFloat() throws BufferUnderflowException {
        float value = getFloat(m_position - m_payload_start);
        m_position += SIZEOF_FLOAT;
        return value;
    }

    /**
     *  Stores the float at the specified position without moving the buffer's position
     *  @param position the position where to store the float
     *  @param value the float to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setFloat(int position, float value) throws BufferOverflowException {
        return setInt(position, Float.floatToIntBits(value));
    }

    /**
     *  Sets the float at the current offset and advances the buffer's position
     *  @param value the float value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeFloat(float value) throws BufferOverflowException {
        setFloat(m_position - m_payload_start, value);
        m_position += SIZEOF_FLOAT;
        return this;
    }

    /**
     *  Returns the double from the specified location without moving the buffer's position
     *  @param position the position where to start reading
     *  @return the double as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final double getDouble(int position) throws BufferUnderflowException {
        return Double.longBitsToDouble(getLong(position));
    }

    /**
     *  Returns the double from the specified location and advances the buffer's position
     *  @param position the position where to start reading
     *  @return the double as read from the buffer
     *  @exception BufferUnderflowException thrown if the buffer has reached its end
     */
    public final double readDouble() throws BufferUnderflowException {
        double value = getDouble(m_position - m_payload_start);
        m_position += SIZEOF_DOUBLE;
        return value;
    }

    /**
     *  Stores the double at the specified position without moving the buffer's position
     *  @param position the position where to store the double
     *  @param value the double to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setDouble(int position, double value) throws BufferOverflowException {
        return setLong(position, Double.doubleToLongBits(value));
    }

    /**
     *  Sets the double at the current offset and advances the buffer's position
     *  @param value the double value to set
     *  @exception BufferOverflowException thrown of the buffer if not
     *   		   large enough to hold the specified data
     */
    public final PacketWriter writeDouble(double value) throws BufferOverflowException {
        setDouble(m_position - m_payload_start, value);
        m_position += SIZEOF_DOUBLE;
        return this;
    }

    /**
     *  Returns the string from the specified location without moving the buffer's position
     *  @param position the position where to read the string from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final String getString(int position) throws BufferUnderflowException {
        return getStringInternal(position, false);
    }

    /**
     *  Returns the string from the current location and advances the buffer's position
     *  @param position the position where to read the string from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final String readString() throws BufferUnderflowException {
        return getStringInternal(m_position - m_payload_start, true);
    }

    /**
     *  Return the no. of bytes occupied by the string in the buffer
     *  @param position the position where to read the string from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    protected final int strlen(int position) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_BYTE + SIZEOF_INT) + SIZEOF_BYTE;
        return getIntInternal(position) + SIZEOF_BYTE + SIZEOF_INT;
    }

    /**
     *  Returns the string from the specified location without moving the buffer's position
     *  @param position the position where to read the string from
     *  @param advance if <code>true</code> advance the pointer
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    protected final String getStringInternal(int position, boolean advance) throws BufferUnderflowException {
        position = checkUnderflow(position, SIZEOF_BYTE + SIZEOF_INT);

        //-----------------------------------------------------------------
        //  Get the string encoding followed by the string length
        //-----------------------------------------------------------------
        byte encoding = m_buffer[position++];
        int length = getIntInternal(position);
        position += SIZEOF_INT;
        if (length < 0)
            length = 0;
        char[] chars = new char[length];

        //-----------------------------------------------------------------
        //  Decode UTF8 character
        //-----------------------------------------------------------------
        if (encoding == UTF8) {
            int i = 0, end = position + length, char1, char2, char3;
            while (position < end) {
                char1 = (m_buffer[position++] & 0xFF);
                switch (char1 >> 4) {
                    case 0 :
                    case 1 :
                    case 2 :
                    case 3 :
                    case 4 :
                    case 5 :
                    case 6 :
                    case 7 : // 0xxxxxxx
                        chars[i++] = (char) char1;
                        break;

                    case 12 :
                    case 13 : // 110x xxxx   10xx xxxx
                        char2 = m_buffer[position++];
                        if ((char2 & 0xC0) != 0x80) {
                            chars[i++] = '?';
                        }
                        else {
                            chars[i++] = (char) (((char1 & 0x1F) << 6) | (char2 & 0x3F));
                        } //if
                        break;

                    case 14 : // 1110 xxxx  10xx xxxx  10xx xxxx
                        char2 = m_buffer[position++];
                        char3 = m_buffer[position++];
                        if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                            chars[i++] = '?';
                        }
                        else {
                            chars[i++] = (char) (((char1 & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
                        } //if
                        break;
                    default : // 10xx xxxx,  1111 xxxx
                        chars[i++] = '?';
                } //switch
            } //while
            length = i;
        }
        //-----------------------------------------------------------------
        //  Decode UCS2 characters
        //-----------------------------------------------------------------
        else if (encoding == UCS2) {
            for (int i = 0; i < length; i++) {
                chars[i] = (char) ((m_buffer[position++] << 8) | m_buffer[position++]);
            } //for
        }
        //-----------------------------------------------------------------
        //  Decode 7bit ASCII characters
        //-----------------------------------------------------------------
        else if (encoding == ASCII) {
            for (int i = 0; i < length; i++) {
                chars[i] = (char) m_buffer[position++];
            } //for
        } //if

        if (advance) {
            m_position = position;
        }//if

        return new String(chars, 0, length);
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setUTF8(int position, String value) throws BufferOverflowException {
        encodeUTF8(position, value);
        return this;
    }

    /**
     *  Stores the string at the current pointer and advances the buffer's position.
     *  The string will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeUTF8(String value) throws BufferOverflowException {
        m_position = encodeUTF8(m_position - m_payload_start, value);
        return this;
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    private final int encodeUTF8(int position, String value) throws BufferOverflowException {
        int c, length_utf8 = 0, length = (value != null) ? value.length() : 0;

        //-----------------------------------------------------------------
        //  Compute the string length and check if the buffer is
        //  large enough to hold the string.
        //-----------------------------------------------------------------
        length_utf8 = strlenUTF8(value);
        position = checkOverflow(position, length_utf8);

        //-----------------------------------------------------------------
        //  Write the string encoding followed by the string length
        //-----------------------------------------------------------------
        m_buffer[position++] = UTF8;
        setIntInternal(position, length_utf8 - 5);
        position += SIZEOF_INT;

        //-----------------------------------------------------------------
        //  Write the utf8 data
        //-----------------------------------------------------------------
        for (int i = 0; i < length; i++) {
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                m_buffer[position++] = (byte) c;
            }
            else if (c > 0x07FF) {
                m_buffer[position++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                m_buffer[position++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                m_buffer[position++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
            else {
                m_buffer[position++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                m_buffer[position++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } //if
        } //for

        return position;
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using UCS2 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UCS2 data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setUCS2(int position, String value) throws BufferOverflowException {
        encodeUCS2(position, value);
        return this;
    }

    /**
     *  Stores the string at the current offset and advances the buffer's position.
     *  The string will be serialized using ASCII encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the ASCII data.
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeUCS2(String value) throws BufferOverflowException {
        m_position = encodeUCS2(m_position - m_payload_start, value);
        return this;
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using UCS2 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UCS2 data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    private final int encodeUCS2(int position, String value) throws BufferOverflowException {
        //-----------------------------------------------------------------
        //  Compute the string length and check if the buffer is
        //  large enough to hold the character data.
        //-----------------------------------------------------------------
        int c, length = value.length();
        position = checkOverflow(position, length * 2 + SIZEOF_BYTE + SIZEOF_INT);

        //-----------------------------------------------------------------
        //  Write the string encoding followed by the string length
        //-----------------------------------------------------------------
        m_buffer[position++] = UCS2;
        setIntInternal(position, length);
        position += SIZEOF_INT;

        //-----------------------------------------------------------------
        //  Write the characters in UCS2 encoding
        //-----------------------------------------------------------------
        for (int i = 0; i < length; i++) {
            c = value.charAt(i);
            m_buffer[position++] = (byte) ((c >>> 8) & 0xFF);
            m_buffer[position++] = (byte) (c & 0xFF);
        } //for

        return position;
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using ASCII encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the ASCII data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setASCII(int position, String value) throws BufferOverflowException {
        encodeASCII(position, value);
        return this;
    }

    /**
     *  Stores the string at the current offset and advances the buffer's position.
     *  The string will be serialized using ASCII encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the ASCII data.
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeASCII(String value) throws BufferOverflowException {
        m_position = encodeASCII(m_position - m_payload_start, value);
        return this;
    }

    /**
     *  Stores the string at the specified position without moving the buffer's position.
     *  The string will be serialized using ASCII encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the ASCII data.
     *  @param position the position where to store the string
     *  @param value the string to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    private final int encodeASCII(int position, String value) throws BufferOverflowException {
        //-----------------------------------------------------------------
        //  Compute the string length and check if the buffer is
        //  large enough to hold the character data.
        //-----------------------------------------------------------------
        int length = value.length();
        position = checkOverflow(position, length + SIZEOF_BYTE + SIZEOF_INT);

        //-----------------------------------------------------------------
        //  Write the string encoding followed by the string length
        //-----------------------------------------------------------------
        m_buffer[position++] = ASCII;
        setIntInternal(position, length);
        position += SIZEOF_INT;

        //-----------------------------------------------------------------
        //  Write the characters in ASCII encoding
        //-----------------------------------------------------------------
        for (int i = 0; i < length; i++) {
            m_buffer[position++] = (byte) (value.charAt(i));
        } //for
        return position;
    }

    /**
     *  Returns the string array from the specified location without moving the buffer's position
     *  @param position the position where to read the string array from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final String[] getStringArray(int position) throws BufferUnderflowException {
        return getStringArrayInternal(position, false);
    }

    /**
     *  Returns the string array from the current location and advances the buffer's position
     *  @param position the position where to read the string array from
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    public final String[] readStringArray() throws BufferUnderflowException {
        return getStringArrayInternal(m_position - m_payload_start, true);
    }

    /**
     *  Returns the string array from the specified location without moving the buffer's position
     *  @param position the position where to read the string from
     *  @param advance if <code>true</code> advance the pointer
     *  @exception BufferUnderflowException thrown if the position is out of range
     */
    protected final String[] getStringArrayInternal(int position, boolean advance) throws BufferUnderflowException {
        int position_save = m_position;
        String[] strings = null;
        //position = checkUnderflow(position, SIZEOF_INT);
        try {

            position = checkUnderflow(position, SIZEOF_INT);

            //-----------------------------------------------------------------
            //  Get the size of the string array
            //-----------------------------------------------------------------
            int length = getIntInternal(position);
            position += SIZEOF_INT;
            strings = new String[length];

            //-----------------------------------------------------------------
            //  Read one string after the other
            //-----------------------------------------------------------------
            setPosition(position - m_payload_start);
            for (int i = 0; i < length; i++) {
                strings[i] = readString();
            } //for
            return strings;
        }
        catch (BufferOverflowException ex) {
            log.errorTrace(LOG_COMPONENT, "Could not get string array.");
            log.exception(LOG_COMPONENT, ex);
        }
        finally {
            if (!advance)
                m_position = position_save;
        } //try
        return strings;
    }

    /**
     *  Stores the string array at the specified position without moving the buffer's position.
     *  The strings will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string array to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter setUTF8Array(int position, String[] value) throws BufferOverflowException {
        encodeUTF8Array(position, value, false);
        return this;
    }

    /**
     *  Stores the string array at the current pointer and advances the buffer's position.
     *  The string will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string array to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    public final PacketWriter writeUTF8Array(String[] value) throws BufferOverflowException {
        encodeUTF8Array(m_position - m_payload_start, value, true);
        return this;
    }

    /**
     *  Stores the string array at the specified position without moving the buffer's position.
     *  The strings will be serialized using UTF8 encoding. A byte which specifies the
     *  string's encoding followed by the string's length (int) preceeds the UTF8 data.
     *  @param position the position where to store the string
     *  @param value the string array to store
     *  @exception BufferOverflowException thrown of the buffer if not
     *              large enough to hold the specified data
     */
    private final int encodeUTF8Array(int position, String[] value, boolean advance) throws BufferOverflowException {
        position = checkOverflow(position, SIZEOF_INT);
        int position_save = m_position;
        int length = (value == null ? 0 : value.length);
        try {
            setIntInternal(position, length);
            position += SIZEOF_INT;
            setPosition(position - m_payload_start);
            for (int i = 0; i < length; i++) {
                position = encodeUTF8(position - m_payload_start, value[i]);
            } //for
        }
        catch (BufferUnderflowException ex) {
            log.errorTrace(LOG_COMPONENT, "Could not encode UTF8 array.");
            log.exception(LOG_COMPONENT, ex);
        }
        finally {
            if (!advance)
                m_position = position_save;
        } //try

        return (m_position - m_payload_start);
    }

    /**
     *  Returns the number of bytes which a ASCII encoded string would occupy.
     *  The length contains the space needed for the characters as well as
     *  the preceeding length variable.
     *  @param value the string to compute the length of
     *  @return the number of bytes need to encode the string in ASCII format
     */
    public static final int strlenASCII(String value) {
        return (value == null ? 5 : (value.length() + 5));
    }

    /**
     *  Returns the number of bytes which a UCS2 encoded string would occupy.
     *  The length contains the space needed for the characters as well as
     *  the preceeding length variable.
     *  @param value the string to compute the length of
     *  @return the number of bytes need to encode the string in UCS2 format
     */
    public static final int strlenUCS2(String value) {
        return (value == null ? 5 : (value.length() * 2 + 5));
    }

    /**
     *  Returns the number of bytes which a UTF8 encoded string array would occupy.
     *  The length contains the space needed for the characters as well as
     *  the preceeding length variable.
     *  @param value the string array to compute the length of
     *  @return the number of bytes need to encode the string array in UTF8 format
     */
    public static final int strlenUTF8(String[] value) {
        int length_utf8 = 4;
        if (value == null)
            return length_utf8;

        for (int i = 0; i < value.length; i++) {
            length_utf8 += strlenUTF8(value[i]);
        } //for
        return length_utf8;
    }

    /**
     *  Returns the number of bytes which a UTF8 encoded string would occupy.
     *  The length contains the space needed for the characters as well as
     *  the preceeding length variable.
     *  @param value the string to compute the length of
     *  @return the number of bytes need to encode the string in UTF8 format
     */
    public static final int strlenUTF8(String value) {
        int length_utf8 = 5;

        if (value == null)
            return length_utf8;

        //------------------------------------------------------------
        // Compute the string length of the utf8 encoded string
        //------------------------------------------------------------
        int c, length = value.length();
        for (int i = 0; i < length; i++) {
            c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                length_utf8++;
            else if (c > 0x07FF)
                length_utf8 += 3;
            else
                length_utf8 += 2;
        } //for
        return length_utf8;
    }

    static final char[] HEXCHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	static final String HEXSTRING = "0123456789ABCDEFabcdef";
    protected final static String toHexString(byte[] value) {
        char s[] = new char[value.length * 2 + (value.length - 1) / 2];
        for (int i = 0, k = 0; i < value.length; i++) {
            s[k++] = HEXCHARS[(value[i] >> 4) & 0x0F];
            s[k++] = HEXCHARS[value[i] & 0x0F];
            if ((i % 2) == 0 && i > 0 && i < (value.length - 1))
                s[k++] = '-';
        } //for
        return new String(s);
    }

	protected final static byte[] fromHexString(String value) {
		char s[] = value.toCharArray();
		int l = 0;
		for (int i = 0; i < s.length; i++) {
			if (HEXSTRING.indexOf(s[i]) >= 0) l++;
		}//for
		byte b[] = new byte[l];
		l = 0;
		for (int i = 0; i < s.length; i++) {
			if (HEXSTRING.indexOf(s[i]) > 0) {
				b[l++] = (byte)Character.digit(s[i], 16);
			}//if
		}//for
		return b;
	} //fromHexString

    /**
     *  Returns a string representation of the packet
     *  @return a string representation of the packet
     */
    public String toString() {
		StringWriter out = new StringWriter();
        PrintWriter printf_writer = new PrintWriter(out);
        try {
            printf_writer.println("============================== Start of Packet =================================");
            int pos = getPosition();
            toString(printf_writer);
            setPosition(pos);
            printf_writer.println("============================== End of Packet ===================================");

        }
        catch (Exception ex) {
        	StringWriter sw = new StringWriter();
        	ex.printStackTrace(new PrintWriter(sw));
        	String stackTrace = sw.toString();
            log.exception("protocol.PacketImpl", ex);
            return stackTrace;
        } //try
        return out.toString();
    }
    
/**
     * @return the compatibility protocol version of that class,
     * (In case the class is instantiated with another library, the
     * number could be different from the getProtocolCompatibilityNumber()
     * that is set by a remote client in some packets.
     */  
      public final static int getClassCompatibilityVersion()
      {
          return PROTOCOL_VERSION % 1000;
      }    

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to used to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        flush();

        out.printf("%30s %30s\n%30s %d\n%30s %x\n",
        	"Type:", getPacketTypeAsString(),
        	"Size:", new Integer(getLength()), 
        	"Request-ID:", new Long(getRequestID()));
    }
    
    private void printInfo() {
    	log.errorTrace(LOG_COMPONENT, "m_start position is: {0}. m_position is: {1}. Payload start is: {2}. The messsage size is: {3} ",
    	    new Object[] {new Integer(m_start), new Integer(m_position), new Integer(m_payload_start), new Integer(m_end)});
    	if (m_buffer != null) {
    		log.errorTrace(LOG_COMPONENT, "m_buffer size is: " + m_buffer.length);
        } else {
        	log.errorTrace(LOG_COMPONENT, "Buffer is null.");
        }

    }
    /**
     *  Flushed any intermediate values to the buffer
     *  @throws JMSException
     */
    protected void flush() throws JMSException {
        // nothing to do
    }

	/**
	 * Returns the expected packet type of the response packet.
	 * For request packets this method returns {@link PacketTypes#INVALID_PACKET_TYPE} 
	 */
	public int getExpectedResponsePacketType() {
		return PacketTypes.INVALID_PACKET_TYPE;
	}
}
