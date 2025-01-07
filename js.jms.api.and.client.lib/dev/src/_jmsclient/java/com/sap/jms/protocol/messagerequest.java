/**
 * MessageRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;

import com.sap.jms.JMSConstants;
import com.sap.jms.client.message.JMSMessage;
import javax.jms.MessageFormatException;

import com.sap.jms.client.message.JMSBytesMessage;
import com.sap.jms.client.message.JMSMapMessage;
import com.sap.jms.client.message.JMSObjectMessage;
import com.sap.jms.client.message.JMSStreamMessage;
import com.sap.jms.client.message.JMSTextMessage;
import com.sap.jms.client.message.MessageProperties;
import com.sap.jms.util.Logging;
import com.sap.jms.util.MessageID;

/**
 * Low level container of a JMS message.
 * A message consists of four different areas:
 * <ll>
 * <li>an area which holds message header fields with a fixed size
 * <li>an area which holds message header fields with variable size
 * <li>an area which hold the message properties (variable size)
 * <li>The message body
 * </ll>
 *
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class MessageRequest extends PacketWithConnectionIDAndSessionIDImpl implements Cloneable, PacketWithDestinationID, PacketWithConsumerID {
    /** The message is persitent */
    static final byte BIT_PERSISTENT = (byte) 0x01;

    /** The message has been redelivered */
    protected static final byte BIT_REDELIVERED = (byte) 0x02;

    /** The destination is a topic */
    static final byte BIT_TOPIC = (byte) 0x04;

    /** The destination is temporary */
    static final byte BIT_TEMPORARY = (byte) 0x08;

    /** The JMSReplyTo destination is topic */
    static final byte BIT_REPLYTO_TOPIC = (byte) 0x10;

    /** The JMSReplyTo destination is temporary */
    static final byte BIT_REPLYTO_TEMPORARY = (byte) 0x20;

    /** The Message is over optimized session */
    static final byte BIT_OPTIMIZED_MODE = (byte) 0x40;

    /*
     *  Implementation note:
     *  Consumer and Destination ID take the same space in the buffer.
     *  The destination id will be used for producers while the
     *  consumer id will be utilized by consumers
     */
    static final int POS_CONSUMER_ID = POS_SESSION_ID + SIZEOF_INT;
    // TODO DUUUUH!!!! desination and consumer are on the same position
    static final int POS_DESTINATION_ID = POS_SESSION_ID + SIZEOF_INT;

    static final int POS_TIMESTAMP = POS_DESTINATION_ID + SIZEOF_LONG;
    static final int POS_EXPIRATION = POS_TIMESTAMP + SIZEOF_LONG;
    static final int POS_MESSAGE_ID = POS_EXPIRATION + SIZEOF_LONG;
    protected static final int POS_BITSET = POS_MESSAGE_ID + SIZEOF_MESSAGE_ID;
    static final int POS_PRIORITY = POS_BITSET + SIZEOF_BYTE;

    /** Start of header values */
    protected static final int POS_HEADER_VALUES_OFFSET = POS_PRIORITY + SIZEOF_BYTE;
    /** Start of message properties */
    protected static final int POS_MESSAGE_PROPERTIES_OFFSET = POS_HEADER_VALUES_OFFSET + SIZEOF_INT;

	/** Start of message body */
    public static final int POS_MESSAGE_BODY_OFFSET       = POS_MESSAGE_PROPERTIES_OFFSET + SIZEOF_INT;

    public static final int SIZE = POS_MESSAGE_BODY_OFFSET + SIZEOF_INT;

    /** Temporary storage for the variable header fields */
    transient HashMap m_header_values = null;

    /** Temporary storage for the properties  */
    transient HashMap m_properties = null;

    /** Temporary storage for the body  */
    transient Object m_body = null;
    transient int m_body_length = 0;

    /**An empty byte array, used to check whether the JMSMessageId is empty (was set as disabled from the provider)*/
    private static final byte[] DISABLED_MESSAGE_ID_BYTES = new byte[SIZEOF_MESSAGE_ID];


    /**
     *  Default constructor
     */
    public MessageRequest() {
        //super(JMS_BYTES_MESSAGE, (SIZE + LEN_PACKET_HEADER), 0);
        super();
    }

    /**
     * Constructor for MessageRequest.
     * @param type_id
     * @param packet_size
     * @param connection_id If no connection exists supply 0 as connection_id
     * @param session_id
     */
    public MessageRequest(byte type_id, int packet_size, long connection_id, int session_id) throws JMSException {
        super(type_id, packet_size <= (SIZE + LEN_PACKET_HEADER) ? SIZE : packet_size, connection_id, session_id);
    }

    /**
     *  Flushed any intermediate values to the buffer
     *  @throws JMSException
     */
    public void flush() throws JMSException {
        encode();
    }

    /**
     *  Returns the offset of the header values
     *  @return the offset into the packet or 0 if none present
     */
    private final int getHeaderValuesOffset() throws BufferUnderflowException {
        return getInt(POS_HEADER_VALUES_OFFSET);
    }

    /**
     *  Returns the size of the header values in bytes
     *  @return the size of the header values in bytes
     */
    private final int getHeaderValuesSize() throws BufferUnderflowException {
        int hoffset, poffset, boffset;

        hoffset = getHeaderValuesOffset();
        if (hoffset == 0)
            return 0;

        poffset = getMessagePropertiesOffset();
        if (poffset == 0) {
            boffset = getMessageBodyOffset();
            return (((boffset == 0) ? m_end : boffset) - hoffset);
        } //if
        return (poffset - hoffset);
    }

    /**
     *  Returns the offset of the message properties
     *  @return the offset into the packet or 0 if none present
     */
    private final int getMessagePropertiesOffset() throws BufferUnderflowException {
        return getInt(POS_MESSAGE_PROPERTIES_OFFSET);
    }

    /**
     *  Returns the size of the message properties in bytes
     *  @return the size of the message properties area
     */
    private final int getMessagePropertiesSize() throws BufferUnderflowException {
        int poffset, boffset;

        poffset = getMessagePropertiesOffset();
        if (poffset == 0)
            return 0;

        boffset = getMessageBodyOffset();
        return (((boffset == 0) ? m_end : boffset) - poffset);
    }

    /**
     *  Returns the offset of the message body
     *  @return the offset into the message body or 0 if not present
     */
    public final int getMessageBodyOffset() throws BufferUnderflowException {
        return getInt(POS_MESSAGE_BODY_OFFSET);
    }

    /**
     *  Returns the size of the message body
     *  @return the size of the message body
     */
    public final int getMessageBodySize() throws BufferUnderflowException {
        int boffset = getMessageBodyOffset();
        return (((boffset == 0) ? 0 : (m_end - boffset)));
    }

    /**
     *  Writes the hash map to the buffer
     *  @param properties the values to write
     *  @param position the start position in the buffer
     */
    private void writeProperties(HashMap properties) throws JMSException, MessageFormatException {
        writeInt(properties.size());
        for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            Object value = properties.get(name);
            writeProperty(name, value);
        }
    }

    /**
     *  Reads the properties from the specified position
     *  @param position the position in the buffer to read from
     */
    private HashMap getProperties(int position)
        throws JMSException, BufferUnderflowException, MessageFormatException {
        if (position == 0||getMessagePropertiesSize()==0)
            return null;

        int old_position = getPosition();
        setPosition(position - POS_PAYLOAD_START);

        int num_properties = readInt();
		HashMap properties = new HashMap(num_properties);
		Object value;

        for (int i = 0; i < num_properties; i++) {
            byte type = readByte();
            String name = readString();
            switch (type) {
                case UTF8 :
                    value = readString();
                    break;
                case BYTE :
                    value = new Byte(readByte());
                    break;
                case SHORT :
                    value = new Short(readShort());
                    break;
                case INT :
                    value = new Integer(readInt());
                    break;
                case LONG :
                    value = new Long(readLong());
                    break;
                case FLOAT :
                    value = new Float(readFloat());
                    break;
                case DOUBLE :
                    value = new Double(readDouble());
                    break;
                case BOOLEAN :
                    byte bool = readByte();
                    value = bool == (byte) 1 ? Boolean.TRUE : Boolean.FALSE;
                    break;
                case BYTE_ARRAY :
                    value = readByteArray();
                    break;
                case CHAR :
                    value = new Character(readChar());
                    break;
                case NULL :
                  value = null;
                  break;
                default :
                    throw new MessageFormatException("Non primitive property encountered");
            } //switch
            properties.put(name, value);
        } //for

        setPosition(old_position);
        return properties;
    } //getProperties

    /**
     *  Computes the size needed by the properties in the supplied hash table
     *  @param properties the list of properties
     */
    private int strlenProperties(HashMap properties) throws MessageFormatException {
        //------------------------------------------------------------
        //  First, compute the size needed by the properties
        //------------------------------------------------------------
        int size = SIZEOF_INT;
        for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            Object value = properties.get(name);
            size += strlenProperty(name, value);
        } //for
        return size;
    }

    /**
     *  Computes the memory of a typed property
     *  @param name the name of the property
     *  @param value the value to write
     *  @throws JMSException if something went wrong
     */
    private int strlenProperty(String name, Object value) throws MessageFormatException {
        int length = strlenUTF8(name) + 1;
        if (value == null) {
          //do nothing - nothing will be written further
        }
        else if (value instanceof String) {
            length += strlenUTF8((String) value);
        }
        else if (value instanceof Byte) {
            length += SIZEOF_BYTE;
        }
        else if (value instanceof Character) {
            length += SIZEOF_CHAR;
        }
        else if (value instanceof Short) {
            length += SIZEOF_SHORT;
        }
        else if (value instanceof Integer) {
            length += SIZEOF_INT;
        }
        else if (value instanceof Long) {
            length += SIZEOF_LONG;
        }
        else if (value instanceof Float) {
            length += SIZEOF_FLOAT;
        }
        else if (value instanceof Double) {
            length += SIZEOF_DOUBLE;
        }
        else if (value instanceof Boolean) {
            length += SIZEOF_BYTE;
        }
        else if (value instanceof byte[]) {
            length += ((byte[]) value).length + SIZEOF_INT;
        }
        else {
            throw new MessageFormatException("Non primitive property encountered");
        } //if

        return length;
    }

    /**
     *  Reads the body and returns it as an object
     *  @return the body object or <code>null</code> if not set
     *  @exception JMSException thrown if the body could not be retrieved
     */
    private final Object readBody() throws JMSException {
        try {
            int type = getPacketType();
            int position = getInt(POS_MESSAGE_BODY_OFFSET);
            if (position == 0 || getMessageBodySize()==0)
                return null;

            setPosition(m_start + position - m_payload_start);

            Object body = null;
            if (type == JMS_BYTES_MESSAGE || type == JMS_STREAM_MESSAGE) {
                body = readByteArray();
            }
            else if (type == JMS_TEXT_MESSAGE) {
                body = readString();
            }
            else if (type == JMS_MAP_MESSAGE) {
                body = getProperties(position);
            }
            else if (type == JMS_OBJECT_MESSAGE) {
                body = readByteArray();
                ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) body);
                JMSInputStream ois = new JMSInputStream(bis, Thread.currentThread().getContextClassLoader());
                body = ois.readObject();
            } //if
            return body;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception e) {
			JMSException ex2 = new JMSException(e.getMessage());
			ex2.initCause(e);
			ex2.setLinkedException(e);
			throw ex2;
        } //try
    }

    /**
     *  Writes a typed property to the packet and advances the pointer
     *  @param name the name of the property
     *  @param value the value to write
     *  @throws JMSException if something went wrong
     */
    private void writeProperty(String name, Object value) throws BufferOverflowException, MessageFormatException {
        int length = strlenUTF8(name) + 1;
        if (value == null){
            writeByte(NULL);
            writeUTF8(name);
            //no need to change the length since no content more will be written
        }
        else if (value instanceof String) {
            length += strlenUTF8((String) value);
            writeByte(UTF8);
            writeUTF8(name);
            writeUTF8((String) value);
        }
        else if (value instanceof Byte) {
            length += SIZEOF_BYTE;
            writeByte(BYTE);
            writeUTF8(name);
            writeByte(((Byte) value).byteValue());
        }
        else if (value instanceof Short) {
            length += SIZEOF_SHORT;
            writeByte(SHORT);
            writeUTF8(name);
            writeShort(((Short) value).shortValue());
        }
        else if (value instanceof Integer) {
            length += SIZEOF_INT;
            writeByte(INT);
            writeUTF8(name);
            writeInt(((Integer) value).intValue());
        }
        else if (value instanceof Long) {
            length += SIZEOF_LONG;
            writeByte(LONG);
            writeUTF8(name);
            writeLong(((Long) value).longValue());
        }
        else if (value instanceof Float) {
            length += SIZEOF_FLOAT;
            writeByte(FLOAT);
            writeUTF8(name);
            writeFloat(((Float) value).floatValue());
        }
        else if (value instanceof Double) {
            length += SIZEOF_DOUBLE;
            writeByte(DOUBLE);
            writeUTF8(name);
            writeDouble(((Double) value).doubleValue());
        }
        else if (value instanceof Boolean) {
            length += SIZEOF_BYTE;
            writeByte(BOOLEAN);
            writeUTF8(name);
            writeByte((byte) (((Boolean) value).booleanValue() ? 1 : 0));
        }
        else if (value instanceof byte[]) {
            length += SIZEOF_INT + ((byte[]) value).length;
            writeByte(BYTE_ARRAY);
            writeUTF8(name);
            writeByteArray((byte[]) value);
        }
        else if (value instanceof Character) {
            length += SIZEOF_CHAR;
            writeByte(CHAR);
            writeUTF8(name);
            writeChar(((Character) value).charValue());
        }
        else {
            throw new MessageFormatException("Non primitive property encountered");
        } //if
    }

    /**
     *  Returns the destination id
     *  @return the destination id
     */
    public int getDestinationID() throws BufferUnderflowException {
        return (int) getLong(POS_DESTINATION_ID);
    }

    /**
     *  Returns the destination id
     *  @return the destination id
     */
    public final int getJMSDestinationID() throws BufferUnderflowException {
        return getDestinationID();
    }

    public final boolean isDestinationTopic() throws BufferUnderflowException {
        return ((byte) (getByte(POS_BITSET) & BIT_TOPIC)) != 0;
    }

    public final boolean isDestinationTemporary() throws BufferUnderflowException {
        return ((byte) (getByte(POS_BITSET) & BIT_TEMPORARY)) != 0;
    }

    public final void setDestinationTopic(boolean isTopic) throws JMSException {
      try {
        if (isTopic) {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) | BIT_TOPIC));
        } else {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) & (~BIT_TOPIC)));
        }
      } catch (BufferUnderflowException bue) {
        JMSException boe = new JMSException(bue.getMessage());
        boe.initCause(bue);
        boe.setLinkedException(bue);
        throw boe;
      }

    }

    public final void setDestinationTemporary(boolean isTemporary) throws JMSException {
      try {
        if (isTemporary) {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) | BIT_TEMPORARY));
        } else {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) & (~BIT_TEMPORARY)));
        }
      } catch (BufferUnderflowException bue) {
        JMSException boe = new JMSException(bue.getMessage());
        boe.initCause(bue);
        boe.setLinkedException(bue);
        throw boe;
      }
    }

    public final void setJMSReplyToTopic(boolean isTopic) throws JMSException {
      try {
        if (isTopic) {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) | BIT_REPLYTO_TOPIC));
        } else {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) & (~BIT_REPLYTO_TOPIC)));
        }
      } catch (BufferUnderflowException bue) {
        JMSException boe = new JMSException(bue.getMessage());
        boe.initCause(bue);
        boe.setLinkedException(bue);
        throw boe;
      }
    }

    public final boolean isJMSReplyToTopic() throws JMSException {
        return ((byte) (getByte(POS_BITSET) & BIT_REPLYTO_TOPIC)) != 0;
    }

    public final void setJMSReplyToTemporary(boolean isTemporary) throws JMSException {
      try {
        if (isTemporary) {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) | BIT_REPLYTO_TEMPORARY));
        } else {
            setByte(POS_BITSET, (byte) (getByte(POS_BITSET) & (~BIT_REPLYTO_TEMPORARY)));
        }
      } catch (BufferUnderflowException bue) {
        JMSException boe = new JMSException(bue.getMessage());
        boe.initCause(bue);
        boe.setLinkedException(bue);
        throw boe;
      }
    }

    public final void setOptimizedMode(boolean mode) throws JMSException {
        try {
            if (mode) {
                setByte(POS_BITSET, (byte) (getByte(POS_BITSET) | BIT_OPTIMIZED_MODE));
            } else {
                setByte(POS_BITSET, (byte) (getByte(POS_BITSET) & (~BIT_OPTIMIZED_MODE)));
            }
      } catch (BufferUnderflowException bue) {
        JMSException boe = new JMSException(bue.getMessage());
        boe.initCause(bue);
        boe.setLinkedException(bue);
        throw boe;
      }
    }

    public final boolean isOptimizedMode() throws BufferUnderflowException {
        return ((byte) (getByte(POS_BITSET) & BIT_OPTIMIZED_MODE)) != 0;
    }

    public final boolean isJMSReplyToTemporary() throws BufferUnderflowException {
        return ((byte) (getByte(POS_BITSET) & BIT_REPLYTO_TEMPORARY)) != 0;
    }

    /**
     *  Sets the destination id
     *  @param destination_id the destination id
     */
    public final void setJMSDestinationID(int destination_id) throws BufferOverflowException {
        setLong(POS_DESTINATION_ID, destination_id);
    }

    /**
     *  Returns the consumer id
     *  @return the consumer id
     */
    public final long getJMSConsumerID() throws BufferUnderflowException {
        return getConsumerID();
    }

    /**
     *  Returns the consumer id
     *  @return the consumer id
     */
    public final long getConsumerID() throws BufferUnderflowException {
        return getLong(POS_CONSUMER_ID);
    }

    /**
     *  Sets the consumer id
     *  @param consumer_id the consumer id
     */
    public final void setConsumerID(long consumer_id) throws BufferOverflowException {
        setLong(POS_CONSUMER_ID, consumer_id);
    }

    /**
     *  Returns the message's timestamp
     *  @return the timestamp
    	 */
    public final long getJMSTimestamp() throws BufferUnderflowException {
        return getLong(POS_TIMESTAMP);
    }

    /**
     *  Sets the message's timestamp
     *  @param timestamp the timestamp
     */
    public final void setJMSTimestamp(long timestamp) throws BufferOverflowException {
        setLong(POS_TIMESTAMP, timestamp);
    }

    /**
     *  Returns the message's expiration timestamp
     *  @return the expiration timestamp
    	 */
    public final long getJMSExpiration() throws BufferUnderflowException {
        return getLong(POS_EXPIRATION);
    }

    /**
     *  Sets the message's expiration timestamp
     *  @param timestamp the expiration timestamp
     */
    public final void setJMSExpiration(long timestamp) throws BufferOverflowException {
        setLong(POS_EXPIRATION, timestamp);
    }

    /**
     *  Returns the message's priority level
     *  @return the message priority
     */
    public final int getJMSPriority() throws BufferUnderflowException {
        //----------------------------------------------------------
        // Upper nibble used for priority
        //----------------------------------------------------------
        return ((getByte(POS_PRIORITY) >>> 4) & 0x0F);
    }

    /**
     *  Sets the message's priority level
     *  @param priority the message priority
     */
    public final void setJMSPriority(int priority) throws JMSException {
        //----------------------------------------------------------
        // Clear upper nibble
        //----------------------------------------------------------
        byte bitset = 0;

        try {
            bitset = (byte) (getByte(POS_PRIORITY) & 0x0F);
        }
        catch (BufferUnderflowException ex) {
          JMSException ex2 = new JMSException(ex.getMessage());
            ex2.initCause(ex);
            ex2.setLinkedException(ex);
            throw ex2;
        } //if
        //----------------------------------------------------------
        // Upper nibble used for priority
        //----------------------------------------------------------
        setByte(POS_PRIORITY, (byte) (bitset | (priority << 4)));
    }

    /**
     *  Returns <code>javax.jms.DeliveryMode.PERSITENT</code> if the message is persistent;
     *  <code>javax.jms.DeliveryMode.NON_PERSITENT</code> otherwise
     *  @return the delivery mode for this message
     */
    public int getJMSDeliveryMode() throws BufferUnderflowException {
        if ((getByte(POS_BITSET) & BIT_PERSISTENT) != 0) {
            return DeliveryMode.PERSISTENT;
        }
        return DeliveryMode.NON_PERSISTENT;
    }

    /**
     *  Sets the message's delivery mode, i.e. either <code>java.jms.DeliveryMode.PERSISTENT</code>
     *  or <code>java.jms.DeliveryMode.NON_PERSISTENT</code>
     *  @param delivery_mode the messages's delivery mode
     */
    public void setJMSDeliveryMode(int delivery_mode) throws JMSException {
        byte bitset = 0;

        try {
            bitset = getByte(POS_BITSET);
        }
        catch (BufferUnderflowException ex) {
          JMSException ex2 = new JMSException(ex.getMessage());
            ex2.initCause(ex);
            ex2.setLinkedException(ex);
            throw ex2;
        } //try

        if (delivery_mode == javax.jms.DeliveryMode.PERSISTENT) {
            bitset |= BIT_PERSISTENT;
        }
        else {
            bitset &= ~BIT_PERSISTENT;
        } //if
        setByte(POS_BITSET, bitset);
    }

    /**
     *  Returns an indication of whether this message is being redelivered.
     *  @return <code>true</code> if the message is being redelived; <code>false</code> otherwise.
     */
    public final boolean getJMSRedelivered() throws BufferUnderflowException {
        return ((getByte(POS_BITSET) & BIT_REDELIVERED) != 0);
    }

    /**
     *  Specifies whether this message is being redelivered.
     *  @param redelivered <code>true</code> if the message's redelivered flag should be set;
     *  <code>false</code> otherwise.
     */
    public final void setJMSRedelivered(boolean redelivered) throws JMSException {
        byte bitset;

        try {
            bitset = getByte(POS_BITSET);
        }
        catch (BufferUnderflowException ex) {
          JMSException ex2 = new JMSException(ex.getMessage());
            ex2.initCause(ex);
            ex2.setLinkedException(ex);
            throw ex2;
        } //try

        if (redelivered) {
            bitset |= BIT_REDELIVERED;
        }
        else {
            bitset &= ~BIT_REDELIVERED;
        } //if
        setByte(POS_BITSET, bitset);
    }

    /**
     *  Returns the message id internal format
     *  @return the message id
     */
    public final byte[] getJMSMessageIDAsBytes() throws BufferUnderflowException {
        return getBytes(POS_MESSAGE_ID, SIZEOF_MESSAGE_ID);
    }

    /**
     *  Sets the message ID in internal format
     *  @param message_id the message_id to set
     */
    public final void setJMSMessageIDAsBytes(byte[] message_id) throws BufferOverflowException {
        setBytes(POS_MESSAGE_ID, message_id, 0, Math.min(message_id.length, SIZEOF_MESSAGE_ID));
    }

    /**
     *  Returns the string representation of the message ID in hex format, e.g.
     *  <code>1FCA-637B-417C-CE</code>
     *  in case the message ID was disabled by a hint from the provider
     * (i.e. there are only 0-s in the buffer for the message ID) then
     *  a null value will be returned.
     *  @return the message ID in string format or null
     */
    public String getJMSMessageID() throws BufferUnderflowException {
        byte[] JMSMessageIDAsBytes = getJMSMessageIDAsBytes();
        String result;
        if (Arrays.equals(DISABLED_MESSAGE_ID_BYTES,JMSMessageIDAsBytes)) {
            result = null;
        } else {
            result = "ID:" + MessageID.toString(JMSMessageIDAsBytes);
        }
        return result;
    }

    /**
     *  Read the variable header fields from the packet
     */
    private final HashMap readHeaderValues() throws BufferUnderflowException {
        try {
            return getProperties(getHeaderValuesOffset());
        } catch (Exception ex) {
            Logging.exception(this, ex);
            BufferUnderflowException ex2 = new BufferUnderflowException("Protocol error");
            ex2.setLinkedException(ex);
            throw ex2;
        } //try
    }

    /**
     *  Returns the correlation id internal format
     *  @return the correlation id
     */
    public final String getJMSCorrelationID() throws BufferUnderflowException {
        //changed in SP14 And AP7. The invocation of the getter will not produce any
        //side effects and the data will remain the same
        boolean wasNull = m_header_values == null;
        String result = null;
        if (m_header_values == null) {
            m_header_values = readHeaderValues();
        }
        //null after reading it is used for empty header
        if (m_header_values != null) {
           result = (String) m_header_values.get("JMSCorrelationID");
        }
        if (wasNull) {
            //it is important to restore it whatever it was otherwise
            // the code in encode() and flush() will update the internal structure
            // As a final result code was behaving different after a getter was invoked
            // (see OSS 649154.)
            // Although the problem was in another place we will restore the internal state
            // of the collection so that side effects will not be performed
            m_header_values =null;
        }
        return result;
    }

    /**
     *  Sets the correlation ID in internal format
     *  @param correlation_id the correlation_id to set
     */
    public final void setJMSCorrelationID(String correlation_id) throws BufferOverflowException, BufferUnderflowException {
        //it is good to check the internal data below, otherwise strange effects may happen
        // for example the sender invokes setJMSCorrelationID, flush is called (from toString for example) then setJMSType in the message
        // and the value for JMSCorrelationID disappears
        if (m_header_values == null)
            m_header_values = readHeaderValues();
        if (m_header_values == null)
            m_header_values = new HashMap(11);

        m_header_values.put("JMSCorrelationID", correlation_id);
    }

    /**
     *  Returns the message type identifier supplied by the client when the message was sent.
     *  @return the type identifier or <code>null</code> if not defined.
     */
    public final String getJMSType() throws BufferUnderflowException {
        //for implementation comments see getJMSCorrelationId
        boolean wasNull = m_header_values == null;
        String result = null;
        if (m_header_values == null) {
            m_header_values = readHeaderValues();
        }
        if (m_header_values != null) {
            result = (String) m_header_values.get("JMSType");
        }

        if (wasNull) {
            m_header_values =null;
        }

        return result;
    }

    /**
     *  Returns the message type identifier supplied by the client when the message was sent.
     *  @return the type identifier
     */
    public final void setJMSType(String type) throws BufferOverflowException, BufferUnderflowException {
        if (m_header_values == null)
            m_header_values = readHeaderValues();
        if (m_header_values == null)
            m_header_values = new HashMap(11);
        m_header_values.put("JMSType", type);
    }

    /**
     * Method setJMSReplyToID. Sets the ID of the JMSReplyTo destination
    * @param destination_id the ID of the JMSReplyTo destination
    * @throws BufferOverflowException
    */
    public final void setJMSReplyToID(int destination_id) throws BufferOverflowException, BufferUnderflowException {
        if (m_header_values == null)
            m_header_values = readHeaderValues();
        if (m_header_values == null)
            m_header_values = new HashMap(11);
        m_header_values.put("JMSReplyToID", new Integer(destination_id));
    }

    /**
     * Method getJMSReplyToID. Returns the ID of the JMSReplyTo destination.
     * @return int the ID of the JMSReplyTo destination.
     * @throws BufferUnderflowException
     */
    public final int getJMSReplyToID() throws BufferUnderflowException {
        int result = 0;
        boolean wasNull = m_header_values == null;
        if (m_header_values == null) {
            m_header_values = readHeaderValues();
        }
        if (m_header_values != null) {
            Integer obj = (Integer) m_header_values.get("JMSReplyToID");
            result = (obj != null ? obj.intValue() : 0);
        }
        if (wasNull) {
            m_header_values =null;
        }
        return result;
    }

    /**
     *  Returns the message properties as hash table
     *  @return properties the properties as a hash table
     */
    public final HashMap getMessageProperties() throws JMSException, MessageFormatException {
        if (m_properties == null) {
            m_properties = getProperties(getMessagePropertiesOffset());
        } //if
        return m_properties;
    }

    /**
     *  Sets the message properties
     *  @param properties the properties as a hash table
     */
    public final void setMessageProperties(HashMap properties) throws JMSException, MessageFormatException {
        m_properties = properties;
    }

    /**
     *  Returns the message body an object.
     *  Depending on the message type the follwing objects will be returned:<p>
     *  <table>
     *  <tr><td>JMSTextMessage</td><td>String</td></tr>
     *  <tr><td>JMSByteMessage</td><td>byte[]</td></tr>
     *  <tr><td>JMSStreamMessage</td><td>byte[]</td></tr>
     *  <tr><td>JMSMapMessage</td><td>HashTableObjectObject</td></tr>
     *  <tr><td>JMSObjectMessage</td><td>Object</td></tr>
     *  </table>
     *  @return the body object or <code>null</code> if not set
     *  @exception JMSException thrown if the body could not be retrieved
     */
    public final Object getMessageBody() throws JMSException {
        if (m_body == null)
            m_body = readBody();
        return m_body;
    }

    /**
     *  Sets the message body
     *  @param body the message body
     */
    public final void setMessageBody(Object body) throws JMSException {
        m_body = body;
    }

    public final void setMessageBody(byte[] body, int length) throws JMSException {
        m_body = body;
        m_body_length = length;
    }

    /**
     *  Sets the message body
     *  @param body the message body
     */
    public final void setMessageBodyAndType(Object body, byte packet_type) throws JMSException {
        m_body = body;
        setPacketType(packet_type);
    }

    public final void setMessageBodyAndType(byte[] body, int length, byte packet_type) throws JMSException {
        m_body = body;
        m_body_length = length;
        setPacketType(packet_type);
    }

    /**
     *  Returns the header values and properteis which are suitable
     *  for a message filter.
     *  @return the properties suitable for message filtering
     *  @exception JMSException thrown if the properties could not be retrieved
     */
    public final Map<String, Object> getMessageFilterProperties() throws JMSException {
        String name;
        Object value;

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put("JMSDeliveryMode", (getJMSDeliveryMode() == DeliveryMode.PERSISTENT ? "PERSISTENT" : "NON_PERSISTENT"));
        properties.put("JMSPriority", new Integer(getJMSPriority()));
        properties.put("JMSTimestamp", new Long(getJMSTimestamp()));
        properties.put("JMSExpiration", new Long(getJMSExpiration()));
        properties.put("JMSRedelivered", getJMSRedelivered()?Boolean.TRUE:Boolean.FALSE);
        value = getJMSMessageID();
        if (value != null) {
            properties.put("JMSMessageID", value);
        }


        value = getJMSCorrelationID();
        if (value != null)
            properties.put("JMSCorrelationID", value.toString());

        value = getJMSType();
        if (value != null)
            properties.put("JMSType", value.toString());

        int position = getMessagePropertiesOffset();
        if (position == 0)
            return properties;

        int old_position = getPosition();
        setPosition(position - m_payload_start);

        int num_properties = readInt();
        for (int i = 0; i < num_properties; i++) {
            byte type = readByte();
            name = readString();
            switch (type) {
                case UTF8 :
                    value = readString();
                    break;
                case BYTE :
                    value = new Byte(readByte());
                    break;
                case SHORT :
                    value = new Short(readShort());
                    break;
                case INT :
                    value = new Integer(readInt());
                    break;
                case LONG :
                    value = new Long(readLong());
                    break;
                case FLOAT :
                    value = new Float(readFloat());
                    break;
                case DOUBLE :
                    value = new Double(readDouble());
                    break;
                case BOOLEAN :
                    byte bool = readByte();
                    value = Boolean.valueOf(bool == (byte) 1);
                    break;
                case CHAR :
                    value = new Character(readChar());
                    break;
                case NULL :
                  value = null;
                  break;
                default :
                    throw new JMSException("Non primitive property encountered");
            } //switch
            //according to spec null properties should be treated the same way as non-existing,
            //we will filter them
            if (value != null) {
              properties.put(name, value);
            }

        } //for

        setPosition(old_position);
        return properties;
    }

    /**
     *  Serializes the non-fixed header fields, the properties and the body
     */
    private final void encode() throws JMSException {
        if (m_header_values == null && m_properties == null && m_body == null)
            return;

        int old_header_size, header_size = 0;
        int old_properties_size = 0, properties_size = 0;
        int old_body_size, body_size = 0;
        int total_size = LEN_PACKET_HEADER + SIZE;
        int offsetInOldBuffer = total_size;
        int type = getPacketType();
        int oldBufferPointer = 0;
        int newBufferPointer = 0;
        boolean write_header, write_properties, write_body;

        try {
            //------------------------------------------------------------
            //  Size of the non-fixed header values
            //------------------------------------------------------------
            old_header_size = getHeaderValuesSize();
            if (m_header_values != null) {
                header_size = strlenProperties(m_header_values);
                write_header = true;
            }
            else {
                // This extra check is performed due to OSS 649154 if the header_size is equal to 0 then the header
                // collection will not be written, as a result the header collection will have the same offset as
                // the property ones. Whenever an application invokes getJmsCorrelationId() or any other value from the
                // headers then the properties collection will be loaded, m_header_values will be different than null
                // the consecutive flush will result in writing  the propties on the place of headers and thus corrupting everything.
                // As result of the fix the null collection will be created as an empty one isntead of the null value.
                // That change will not break the existing compatibility since both parties should be able to deserialize
                // correctly messages with empty collections. The only negative effect would be that the package size will be slighlty
                // enlarged (4 bytes more), which should not cause any visible performance delays.

                if (old_header_size !=0 ) {
                    write_header = false;
                    header_size = old_header_size;
                } else {
                     write_header = true;
                     m_header_values = new HashMap();
                     //the code bellow should cause header_size to be equal to SIZEOF_INT since the map is empty
                     header_size = strlenProperties(m_header_values);
                }

            } //if

            //------------------------------------------------------------
            //  Size of the message properties
            //------------------------------------------------------------
            old_properties_size = getMessagePropertiesSize();
            if (m_properties != null) {
                properties_size = strlenProperties(m_properties);
                write_properties = (properties_size > 0);
            }
            else {
                properties_size = old_properties_size;
                write_properties = false;
            } //if

            //TODO: Do a switch on the Type instead if-else
            //------------------------------------------------------------
            //  Size of a byte[] message
            //------------------------------------------------------------
            old_body_size = getMessageBodySize();
            if (m_body == null) {
                body_size = old_body_size;
                write_body = false;
            }
            else if (m_body instanceof byte[]) {
                body_size = m_body_length + SIZEOF_INT;
                write_body = true;
            }
            //------------------------------------------------------------
            //  Size of a text message
            //------------------------------------------------------------
            else if (m_body instanceof String && (type != PacketTypes.JMS_OBJECT_MESSAGE)) {
                body_size = strlenUTF8(((String) m_body));
                write_body = true;
            }
            //------------------------------------------------------------
            //  Size of a map message
            //------------------------------------------------------------
            else if (m_body instanceof HashMap  && (type != PacketTypes.JMS_OBJECT_MESSAGE)) {
                body_size = strlenProperties((HashMap) m_body);
                write_body = true;
            }
            //------------------------------------------------------------
            //  Size of an object message
            //------------------------------------------------------------
            else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(m_body);
                oos.flush();
                m_body = bos.toByteArray();
                m_body_length = ((byte[]) m_body).length;
                body_size = m_body_length + SIZEOF_INT;
                write_body = true;
            } //if

            //------------------------------------------------------------
            //  Allocate memory
            //------------------------------------------------------------
            total_size += header_size + properties_size + body_size;
            byte[] newBuffer = new byte[total_size];
            byte[] oldBuffer = m_buffer;
            int length = offsetInOldBuffer;

            if (m_buffer != null) {
              System.arraycopy(m_buffer, oldBufferPointer, newBuffer, newBufferPointer, length);
            }

            oldBufferPointer = length;
            newBufferPointer = length;
            int nextOffsetInNewBuffer = length;

            setBuffer(newBuffer, 0, newBuffer.length);
            setIntInternal(POS_PACKET_SIZE, newBuffer.length);
            setByteInternal(POS_PACKET_TYPE, (byte) type);
            newBuffer = null;
            //------------------------------------------------------------
            //  Write header values
            //------------------------------------------------------------
            if (write_header) {
              oldBufferPointer += old_header_size;
              setInt(POS_HEADER_VALUES_OFFSET, nextOffsetInNewBuffer);
              setPosition(newBufferPointer - m_payload_start);
              writeProperties(m_header_values);
              m_header_values = null;
              newBufferPointer += header_size;
            } else {
              setInt(POS_HEADER_VALUES_OFFSET, nextOffsetInNewBuffer);
            }

            nextOffsetInNewBuffer += header_size;
            offsetInOldBuffer += old_header_size;

            if (write_properties) {
              if (offsetInOldBuffer > oldBufferPointer) {
                length = offsetInOldBuffer - oldBufferPointer;
                System.arraycopy(oldBuffer, oldBufferPointer, m_buffer, newBufferPointer, length);
                oldBufferPointer += length;
                newBufferPointer += length;
              }

              oldBufferPointer += old_properties_size;
              setInt(POS_MESSAGE_PROPERTIES_OFFSET, newBufferPointer);
              setPosition(newBufferPointer - m_payload_start);
              writeProperties(m_properties);
              m_properties = null;
              newBufferPointer += properties_size;
            } else {
              setInt(POS_MESSAGE_PROPERTIES_OFFSET, nextOffsetInNewBuffer);
            }

            nextOffsetInNewBuffer += properties_size;
            offsetInOldBuffer += old_properties_size;

            if (write_body) {
              if (offsetInOldBuffer > oldBufferPointer) {
                length = offsetInOldBuffer - oldBufferPointer;
                System.arraycopy(oldBuffer, oldBufferPointer, m_buffer, newBufferPointer, length);
                newBufferPointer += length;
              }

              setInt(POS_MESSAGE_BODY_OFFSET, newBufferPointer);
              setPosition(newBufferPointer - m_payload_start);

              //----------------------------------------------------
              //  Write byte[] and object messages
              //----------------------------------------------------
              if (m_body instanceof byte[]) {
                  writeByteArray((byte[]) m_body, 0, m_body_length);
              }
              //----------------------------------------------------
              //  Handle text message
              //----------------------------------------------------
              else if (m_body instanceof String) {
                  writeUTF8((String) m_body);
              }
              //----------------------------------------------------
              //  Handle map message
              //----------------------------------------------------
              else if (m_body instanceof HashMap) {
                  writeProperties((HashMap) m_body);
              } //if
              m_body = null;

            } else {
              offsetInOldBuffer += old_body_size;
              length = offsetInOldBuffer - oldBufferPointer;
              System.arraycopy(oldBuffer, oldBufferPointer, m_buffer, newBufferPointer, length);
              setInt(POS_MESSAGE_BODY_OFFSET, nextOffsetInNewBuffer);
            }
        }
        catch (Exception ex) {
			JMSException ex2 = new JMSException(ex.getMessage());
			ex2.initCause(ex);
			ex2.setLinkedException(ex);
			throw ex2;
        } //try
    }

    /**
     * Returns a pure JMS message, converted ("casted") from this MessageRequest
     * @return a representation of this MessageRequest in client usable view (javax.jms.Message)
     * @throws JMSException if the packet type of this MessageRequest is not a message packet
     */
    public javax.jms.Message getJMSMessage() throws JMSException {

    	int packetType = this.getPacketType();
    	if (!(packetType > 0)) {
    		JMSException jmse = new JMSException("The packet is not a message packet");
    		throw jmse;
    	}

    	JMSMessage message = null;

    	switch (packetType) {
    		case JMS_BYTES_MESSAGE : {
    			message = new JMSBytesMessage(this);
    			break;
    		}

    		case JMS_MAP_MESSAGE : {
    			message = new JMSMapMessage(this);
    			break;
    		}

    		case JMS_TEXT_MESSAGE : {
    			message = new JMSTextMessage(this);
    			break;
    		}

    		case JMS_STREAM_MESSAGE : {
    			message = new JMSStreamMessage(this);
    			break;
    		}

    		case JMS_OBJECT_MESSAGE : {
    			message = new JMSObjectMessage(this);
    			break;
    		}

    		case JMS_GENERIC_MESSAGE : {
    			message = new JMSMessage(this);
    			break;
    		}

    		default : {
    			javax.jms.JMSException jmse = new javax.jms.JMSException("The packet type is not a proper message type. Packet type is : " + packetType);
    			throw jmse;
    		}

    	}

    	return message;
    }


    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);

        //----------------------------------------------------------------
        // Print fixed header values
        //----------------------------------------------------------------
        out.println("------------------------------ Message Header ----------------------------------");
        out.printf("%30s %s\n%30s %d\n%30s %s\n%30s %s\n%30s %s\n%30s %s\n%30s %08x\n",
        	"MessageID:", getJMSMessageID(),
        	"Priority:", getJMSPriority(),
        	"DeliveryMode:", (getJMSDeliveryMode() == DeliveryMode.PERSISTENT ? "PERSISTENT" : "NON-PERSISTENT"),
        	"Timestamp:", DateFormat.getDateInstance().format(new Date(getJMSTimestamp())),
        	"Expiration:", getJMSExpiration()==0 ? "never" : DateFormat.getDateInstance().format(new Date(getJMSExpiration())),
        	"Redelivered:", getJMSRedelivered() ? "true" : "false",
        	"DestinationID:", getJMSDestinationID());

        //----------------------------------------------------------------
        // Print non-fixed header values
        //----------------------------------------------------------------
        HashMap header_values = (m_header_values != null) ? m_header_values : readHeaderValues();
        if (header_values != null && header_values.size() > 0) {
            for (Iterator i = header_values.keySet().iterator(); i.hasNext();) {
                String name = (String) i.next();
                Object value = header_values.get(name);
                out.printf("%30s %s\n", name + ":", value.toString());
            }
        }

        //----------------------------------------------------------------
        // Print properties
        //----------------------------------------------------------------
        HashMap properties = (m_properties != null) ? m_properties : getProperties(getMessagePropertiesOffset());
        if (properties != null && properties.size() > 0) {
            out.println("------------------------------ Message Properties ------------------------------");
            for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
                String name = (String) i.next();
                Object data = properties.get(name);
                String valueAsString = data != null ? data.toString() : " NULL ";
                if (valueAsString.length() > 80) {
                    out.printf("%30s %66s[...truncated]\n", name + ":", valueAsString.substring(0, 66));
                } else {
                    out.printf("%30s %s\n", name + ":", valueAsString);
                }
            }
        }

        //----------------------------------------------------------------
        // Print the body
        //----------------------------------------------------------------
        int type = getPacketType();
        Object body = (m_body != null) ? m_body : (type == JMS_OBJECT_MESSAGE ? "ObjectMessage" : readBody());
        if (body != null) {
            out.println("------------------------------ Message Body ------------------------------------");
            if (body instanceof String) {
                if (((String) body).length() <= 80) {
                    out.printf("%80s\n", body);
                }
                else {
                    out.printf("%66s[...truncated]\n", body);
                }
            }
            else if (body instanceof byte[]) {
        		final String hexChars = "0123456789ABCDEF";
            	final String strTruncated = "[...truncated]";
            	final int maxCharsPerLine = 80;
            	final int maxBytesPerLine = (int)Math.floor((double)maxCharsPerLine / 2.5);

            	byte bbody[] = new byte[80];
            	for (int i = 0; i < bbody.length; i++)
            		bbody[i] = (byte)(i);

        		int max = bbody.length;
        		if (bbody.length > maxBytesPerLine)
        			max = maxBytesPerLine - (int)Math.ceil((double)strTruncated.length() / 2.5);

        		StringBuffer buf = new StringBuffer(80);
        		for (int i = 0; i < max; i++) {
        			int cur = (int)bbody[i] & 0xff;
        			// Integer.toHexString() produces variable string length output
        			buf.append(hexChars.charAt(cur >> 4));
        			buf.append(hexChars.charAt(cur & 0x0f));
        			if ((i % 2 != 0) && (i > 0))
        				buf.append(' ');
        		}
        		out.print(buf.toString());
        		if (bbody.length > maxBytesPerLine)
        			out.print(strTruncated);
        		out.println();
            }
            else if (body instanceof HashMap && (getPacketType() != PacketTypes.JMS_OBJECT_MESSAGE)) {
                HashMap hashmap = (HashMap) body;
                for (Iterator i = hashmap.keySet().iterator(); i.hasNext();) {
                    String name = (String) i.next();
                    Object value = hashmap.get(name);
                    out.printf("%30s %49s\n", name + ":", value.toString());
                }
            }
            else {
                String s = body.toString();
                if (s.length() <= 80) {
                    out.printf("%80s\n", s);
                } else {
                    out.printf("%68s[...truncated]\n", s);
                }
            }
        }
    } //toString

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone(){
        return super.clone();
    }

    public int getExpectedResponsePacketType() {
  	  return PacketTypes.MESSAGE_RESPONSE;
    }
    
    /**
     *  Returns the size of the message packet as encoded in the supplied buffer
     *  @param buffer the buffer from which to read the size
     *  @param offset the start of the packet data
     */
    public static int getTotalSize(byte[] buffer, int offset) {
        int size = getIntInternal(buffer, offset + POS_PACKET_SIZE);
        return size;
    } 
        
    /**
     *  Returns the size of the message header  as encoded in the supplied buffer
     *  @param buffer the buffer from which to read the size
     *  @param offset the start of the packet data
     */
    public static int getHeaderSize(byte[] buffer, int offset) {
        int body_offset = getIntInternal(buffer, offset + LEN_PACKET_HEADER + POS_MESSAGE_BODY_OFFSET);
        int size = body_offset != 0 ? body_offset : getIntInternal(buffer, offset + POS_PACKET_SIZE);
        return size;
    } 
    
    public static Map getMessageFilterProperties(com.sap.jms.protocol.MessageRequest request) throws JMSException {
        Map result = new Hashtable();        
        Map<String, Object> properties = request.getMessageFilterProperties();
        
        for (String key: properties.keySet()) {
        	result.put(key, properties.get(key));
        }
        return result;
    }
    
    public static com.sap.jms.protocol.MessageRequest cloneMessageAndProperties(com.sap.jms.protocol.MessageRequest oldMessageRequest) throws JMSException {
    	com.sap.jms.protocol.MessageRequest newMessageRequest = (com.sap.jms.protocol.MessageRequest) oldMessageRequest.clone();
    	

    	MessageProperties properties = new MessageProperties(newMessageRequest);
        properties.setStringProperty(JMSConstants.JMSX_SAP_DEAD_MSG_ID, newMessageRequest.getJMSMessageID());
        properties.setIntProperty(JMSConstants.JMSX_SAP_DEAD_DST_ID, newMessageRequest.getJMSDestinationID());
        properties.setLongProperty(JMSConstants.JMSX_SAP_DEAD_MSG_EXPIRATION, newMessageRequest.getJMSExpiration());
        properties.setLongProperty(JMSConstants.JMSX_SAP_DEAD_MSG_TIMESTAMP, newMessageRequest.getJMSTimestamp());
        properties.setStringProperty(JMSConstants.JMSX_SAP_DEAD_MSG_CORRELATIONID, newMessageRequest.getJMSCorrelationID());
        properties.setLongProperty(JMSConstants.JMSX_SAP_DEAD_MSG_CONNECTIONID, newMessageRequest.getConnectionID());
        
       
        newMessageRequest.setMessageProperties(properties.getPropertiesTable());
    	newMessageRequest.flush();
    	return newMessageRequest;
    }
    
} 
