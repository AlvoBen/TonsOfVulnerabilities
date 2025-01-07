/**
 * 
 */
package com.sap.jms.protocol;

import com.sap.jms.util.compat.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;

/**
 * @author I032581
 *
 */



public class ConfigurationPropertiesPacket extends PacketWithConnectionIDAndSessionIDImpl implements PacketWithConsumerID {

	 /** The ID for this packet. */
   public static final byte TYPE_ID = CONFIGURATION_PROPERTIES_PACKET;
   
   protected HashMap propertiesMap = null;
   
   protected static final int POS_CONSUMER_ID = POS_SESSION_ID + SIZEOF_INT;
   
   /** Start of configuration properties */
   protected int POS_CONFIGURATION_PROPERTIES_OFFSET = POS_CONSUMER_ID + SIZEOF_LONG;
   

   public static final int SIZE  = POS_CONSUMER_ID + SIZEOF_INT;

	/**
	 * 
	 */
	public ConfigurationPropertiesPacket() {
	}

	/**
	 * @param session_id TODO
	 * @param packet_type
	 */
	public ConfigurationPropertiesPacket(long client_id, int session_id, HashMap properties) throws JMSException {
		super(TYPE_ID, SIZE, client_id,  session_id);
		writeProperties(properties);
	}

	public HashMap getProperties()  throws BufferOverflowException, BufferUnderflowException, JMSException {
		return getProperties(POS_CONFIGURATION_PROPERTIES_OFFSET + 1);
	}
	
    /**
     *  Computes the size needed by the properties in the supplied hash table
     *  @param properties the list of properties
     */
    private int strlenProperties(HashMap properties) throws MessageFormatException {
        //------------------------------------------------------------
        //  First, compute the size needed by the properties
        //------------------------------------------------------------
        int size = SIZEOF_INT;
        Iterator keys = properties.keySet().iterator();
        while(keys.hasNext()) {
            String name = (String) keys.next();
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

        if (value instanceof String) {
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
    
	
	private HashMap getProperties(int position)  throws BufferOverflowException, BufferUnderflowException, JMSException {
	    if (position == 0)
	        return null;
	    
	    int old_position = getPosition();
	    //setPosition(position);
	    setPosition(position - POS_PAYLOAD_START);
	    
	    HashMap properties = new HashMap();
	    Object value;
	    int num_properties = getInt(m_position);
	    setPosition(position + SIZEOF_INT);
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
	            default :
	                throw new JMSException("Non primitive property encountered");
	        } //switch
	        properties.put(name, value);
	    } //for
	
	    setPosition(old_position);
	    return properties;
	} //getProperties
	
	 private void writeProperty(String name, Object value) throws BufferOverflowException, MessageFormatException {
	        int length = strlenUTF8(name) + 1;

	        if (value instanceof String) {
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
	     *  Writes the hash map to the buffer
	     *  @param properties the values to write
	     *  @param position the start position in the buffer
	     */
	    private void writeProperties(HashMap properties) throws JMSException, MessageFormatException {
	    	int old_buffer_size = m_buffer.length;
	    	//POS_CONFIGURATION_PROPERTIES_OFFSET = old_buffer_size;
	    	int new_size  = strlenProperties(properties) + old_buffer_size;

			 allocate(TYPE_ID, new_size);
	    	 setPosition(POS_CONFIGURATION_PROPERTIES_OFFSET + 1);
	           writeInt(properties.size());
		        Set keys = properties.keySet();
		        Iterator keysIterator = keys.iterator(); 
		          while ( keysIterator.hasNext()) {
		            String name = (String) keysIterator.next();
		            Object value = properties.get(name);
		            writeProperty(name, value);
		          } //while
	        
	    } //writeProperties
	
	
	public void addProperty(Object key, Object value ){
		propertiesMap.put(key, value);
	}

	public long getConsumerID() throws BufferUnderflowException {
		 return getLong(POS_CONSUMER_ID);
	}

    /**
     *  Sets the consumer id
     *  @param consumer_id the consumer id
     */
    public final void setConsumerID(long consumer_id) throws BufferOverflowException {
        setLong(POS_CONSUMER_ID, consumer_id);
    }
    
    protected void toString(PrintWriter out) throws Exception {

        //----------------------------------------------------------------
        // Print configuration properties content
        //----------------------------------------------------------------
        out.println("------------------------------ Configuration Properties Content --------------------------------");
        super.toString(out);
        HashMap props = getProperties();
        if (props != null && props.size() > 0) {
	        Iterator keys = props.keySet().iterator();
			while(keys.hasNext()) {
	            String element = (String) keys.next();
	            Object value = props.get(element);
				out.printf("%30s %s\n%30s %d\n", 
					"PropertyKey: ", element,
					"Property Value: ", value);
			}
        }
        out.printf("%30s %s\n", "Type:", new Byte(CONFIGURATION_PROPERTIES_PACKET));
    }
}
