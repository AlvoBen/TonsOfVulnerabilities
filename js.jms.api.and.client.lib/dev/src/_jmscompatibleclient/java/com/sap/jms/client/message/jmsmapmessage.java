/**
 * MapMessage.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import java.util.Collections;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.message.MessageRequest;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.JMSConstants;


/** A <CODE>MapMessage</CODE> object is used to send a set of name-value pairs.
  * The names are <CODE>String</CODE> objects, and the values are primitive 
  * data types in the Java programming language. The entries can be accessed 
  * sequentially or randONLY by name. The order of the entries is undefined.
  * <CODE>MapMessage</CODE> inherits from the <CODE>Message</CODE> interface
  * and adds a message body that contains a Map.
  *
  * <P>The primitive types can be read or written explicitly using methods
  * for each type. They may also be read or written generically as objects.
  * For instance, a call to <CODE>MapMessage.setInt("foo", 6)</CODE> is 
  * equivalent to <CODE>MapMessage.setObject("foo", new Integer(6))</CODE>.
  * Both forms are provided, because the explicit form is convenient for
  * static programming, and the object form is needed when types are not known
  * at compile time.
  *
  * <P>When a client receives a <CODE>MapMessage</CODE>, it is in read-only 
  * mode. If a client attempts to write to the message at this point, a 
  * <CODE>MessageNotWriteableException</CODE> is thrown. If 
  * <CODE>clearBody</CODE> is called, the message can now be both read from and 
  * written to.
  *
  * <P><CODE>MapMessage</CODE> objects support the following conversion table. 
  * The marked cases must be supported. The unmarked cases must throw a 
  * <CODE>JMSException</CODE>. The <CODE>String</CODE>-to-primitive conversions 
  * may throw a runtime exception if the primitive's <CODE>valueOf()</CODE> 
  * method does not accept it as a valid <CODE>String</CODE> representation of 
  * the primitive.
  *
  * <P>A value written as the row type can be read as the column type.
  *
  * <PRE>
  * |        | boolean byte short char int long float double String byte[]
  * |----------------------------------------------------------------------
  * |boolean |    X                                            X
  * |byte    |          X     X         X   X                  X
  * |short   |                X         X   X                  X
  * |char    |                     X                           X
  * |int     |                          X   X                  X
  * |long    |                              X                  X
  * |float   |                                    X     X      X
  * |double  |                                          X      X
  * |String  |    X     X     X         X   X     X     X      X
  * |byte[]  |                                                        X
  * |----------------------------------------------------------------------
  * </PRE>
  *
  * <P>Attempting to read a null value as a primitive type must be treated
  * as calling the primitive's corresponding <code>valueOf(String)</code> 
  * conversion method with a null value. Since <code>char</code> does not 
  * support a <code>String</code> conversion, attempting to read a null value 
  * as a <code>char</code> must throw a <code>NullPointerException</code>.
  *
  *
  * @see         javax.jms.Session#createMapMessage()
  * @see         javax.jms.BytesMessage
  * @see         javax.jms.Message
  * @see         javax.jms.ObjectMessage
  * @see         javax.jms.StreamMessage
  * @see         javax.jms.TextMessage
  */
public final class JMSMapMessage extends JMSMessage implements javax.jms.MapMessage, JMSConstants {
  
  private static final LogService logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
  private final static String LOG_COMPONENT = "message.JMSMapMessage";
  private HashMap body = null;
  private int sizeInBytes = 0;


  public JMSMapMessage() throws JMSException {
    super(PacketTypes.JMS_MAP_MESSAGE);
	body = new HashMap(7);
	setMode(RW_MODE);
  }
  
  /**
   * Method MapMessage. Constructs a com.sap.jms.message.Message given an arbitrary
   * javax.jms.Message.
   * @param alien  the javax.jms.Message from whish com.sap.jms.message.Message will be
   * created
   * @throws JMSException  if an error occurs while converting
   */
  JMSMapMessage(javax.jms.MapMessage alien) throws JMSException {
    this();
    copyPropertiesFrom(alien);
    copyBodyPropertiesFrom(alien);
  }
  
  public JMSMapMessage(MessageRequest messagePacket) {
    super(messagePacket);
  }

/* (non-Javadoc)
 * @see javax.jms.Message#clearBody()
 */
  public void clearBody() throws JMSException {
    setMode(RW_MODE);
    messagePacket.setMessageBody(null);
    
    if (body != null) {
      body.clear();
    } else {
      body = new HashMap(7);
    }
    
  }

 /**
   *  Makes a copy of the message.
   *
   * @return  a copy of this message.
   */
  public Object clone() {
    try {
      if (body != null) {
        messagePacket.setMessageBodyAndType(body, PacketTypes.JMS_MAP_MESSAGE);
      }
      
      if (messageProperties != null) {
        messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
      }
      
      return new JMSMapMessage((MessageRequest)messagePacket.clone());
    } catch (JMSException e) {
      logService.errorTrace(LOG_COMPONENT, "Could not clone MapMessage.");
      logService.exception(LOG_COMPONENT, e);
      return null;
    }
  }

  
/** Returns the <CODE>boolean</CODE> value with the specified name.
	*
	* @param name the name of the <CODE>boolean</CODE>
	*
	* @return the <CODE>boolean</CODE> value with the specified name
	*
	* @exception JMSException if the JMS provider fails to read the message
	*                         due to some internal error.
	* @exception MessageFormatException if this type conversion is invalid.      
	*/
	public boolean getBoolean(String name) throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Boolean) {
        return ((Boolean)property).booleanValue();
      } else if (property instanceof String) {
        return Boolean.valueOf((String)property).booleanValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Boolean.valueOf(null).booleanValue();
    }
	}   

/** Returns the <CODE>byte</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>byte</CODE>
  *
  * @return the <CODE>byte</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.
  */ 
  public byte getByte(String name) throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Byte) {
        return ((Byte)property).byteValue();
      } else if (property instanceof String) {
        return Byte.valueOf((String)property).byteValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Byte.valueOf(null).byteValue();
    }
	}

/** Returns the byte array value with the specified name.
	*
	* @param name the name of the byte array
	*
	* @return a copy of the byte array value with the specified name; if there
	* is no
	* item by this name, a null value is returned.
	*
	* @exception JMSException if the JMS provider fails to read the message
	*                         due to some internal error.
	* @exception MessageFormatException if this type conversion is invalid.      
	*/ 
  public byte[] getBytes(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof byte[]) {
        return ((byte[])property);
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return null;
    }
  }

/** Returns the Unicode character value with the specified name.
  *
  * @param name the name of the Unicode character
  *
  * @return the Unicode character value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.     
  */ 
  public char getChar(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Character) {
        return ((Character)property).charValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      throw new NullPointerException("Null pointer exception");
    }
  }

/** Returns the <CODE>double</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>double</CODE>
  *
  * @return the <CODE>double</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.      
  */ 
  public double getDouble(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Double) {
        return ((Double)property).doubleValue();
      } else if (property instanceof Float) {
        return ((Float)property).floatValue();
      } else if (property instanceof String) {
        return Double.valueOf((String)property).doubleValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Double.valueOf(null).doubleValue();
    }
  }

/** Returns the <CODE>float</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>float</CODE>
  *
  * @return the <CODE>float</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.     
  */ 
  public float getFloat(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Float) {
        return ((Float)property).floatValue();
      } else if (property instanceof String) {
        return Float.valueOf((String)property).floatValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Float.valueOf(null).floatValue();
    }
	}

/** Returns the <CODE>int</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>int</CODE>
  *
  * @return the <CODE>int</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.      
  */ 
  public int getInt(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Integer) {
        return ((Integer)property).intValue();
      } else if (property instanceof Byte) {
        return ((Byte)property).byteValue();
      } else if (property instanceof Short) {
        return ((Short)property).shortValue();
      } else if (property instanceof String) {
        return Integer.valueOf((String)property).intValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Integer.valueOf(null).intValue();
    }
	}

/** Returns the <CODE>long</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>long</CODE>
  *
  * @return the <CODE>long</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.      
  */ 
  public long getLong(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Long) {
        return ((Long)property).longValue();
      } else if (property instanceof Integer) {
        return ((Integer)property).intValue();
      } else if (property instanceof Byte) {
        return ((Byte)property).byteValue();
      } else if (property instanceof Short) {
        return ((Short)property).shortValue();
      } else if (property instanceof String) {
        return Long.valueOf((String)property).longValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Long.valueOf(null).longValue();
    }
	}

/** Returns an <CODE>Enumeration</CODE> of all the names in the 
  * <CODE>MapMessage</CODE> object.
  *
  * @return an enumeration of all the names in this <CODE>MapMessage</CODE>
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  */
  public java.util.Enumeration getMapNames() throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    return Collections.enumeration(body.keySet());
  }

/** Returns the value of the object with the specified name.
  *
  * <P>This method can be used to return, in objectified format,
  * an object in the Java programming language ("Java object") that had 
  * been stored in the Map with the equivalent
  * <CODE>setObject</CODE> method call, or its equivalent primitive
  * <CODE>set<I>type</I></CODE> method.
  *
  * <P>Note that byte values are returned as <CODE>byte[]</CODE>, not 
  * <CODE>Byte[]</CODE>.
  *
  * @param name the name of the Java object
  *
  * @return a copy of the Java object value with the specified name, in 
  * objectified format (for example, if the object was set as an 
  * <CODE>int</CODE>, an <CODE>Integer</CODE> is returned); if there is no 
  * item by this name, a null value is returned
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  */ 
  public Object getObject(String name) throws JMSException{
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    return body.get(name);
  }

/** Returns the <CODE>short</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>short</CODE>
  *
  * @return the <CODE>short</CODE> value with the specified name
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.      
  */ 
  public short getShort(String name)  throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof Short) {
        return ((Short)property).shortValue();
      } else if (property instanceof Byte) {
        return ((Byte)property).byteValue();
      } else if (property instanceof String) {
        return Short.valueOf((String)property).shortValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return Short.valueOf(null).shortValue();
    }
	}

/** Returns the <CODE>String</CODE> value with the specified name.
  *
  * @param name the name of the <CODE>String</CODE>
  *
  * @return the <CODE>String</CODE> value with the specified name; if there 
  * is no item by this name, a null value is returned
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageFormatException if this type conversion is invalid.      
  */ 
  public String getString(String name) throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
    Object property = body.get(name);
    
    if (property != null) {
      if (property instanceof String) {
        return (String)property;
      } else if (property instanceof Integer) {
        return ((Integer)property).toString();
      } else if (property instanceof Byte) {
        return ((Byte)property).toString();
      } else if (property instanceof Short) {
        return ((Short)property).toString();
      } else if (property instanceof Long) {
        return ((Long)property).toString();
      } else if (property instanceof Boolean) {
        return ((Boolean)property).toString();
      } else if (property instanceof Float) {
        return ((Float)property).toString();  
      } else if (property instanceof Double) {
        return ((Double)property).toString();  
      } else if (property instanceof Character) {
        return ((Character)property).toString();  
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return null;
    }
  }

/** Indicates whether an item exists in this <CODE>MapMessage</CODE> object.
  *
  * @param name the name of the item to test
  *
  * @return true if the item exists
  *
  * @exception JMSException if the JMS provider fails to determine if the 
  *                         item exists due to some internal error.
  */ 
  public boolean itemExists(String name) throws JMSException {
    if (body == null) {
      body = (HashMap)messagePacket.getMessageBody();
    }
    
		return body.containsKey(name);
  }

/** Sets a <CODE>boolean</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>boolean</CODE>
  * @param value the <CODE>boolean</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error. 
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string. 
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */
  public void setBoolean(String name, boolean value) throws MessageNotWriteableException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 2;
    body.put(name, Boolean.valueOf(value));
  }

/** Sets a <CODE>byte</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>byte</CODE>
  * @param value the <CODE>byte</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setByte(String name, byte value)  throws MessageNotWriteableException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 2;
    body.put(name, new Byte(value));
  }
   
/** Sets a byte array value with the specified name into the Map.
  *
  * @param name the name of the byte array
  * @param value the byte array value to set in the Map; the array
  *              is copied so that the value for <CODE>name</CODE> will
  *              not be altered by future modifications
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                          is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setBytes(String name, byte[] value)  throws MessageNotWriteableException {
   	if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    //note that there seems to be errata in the javadoc distributed by sun - according to it
    //a NullPointerException must be thrown in case the name is empty or null, according to the spec 
    //IllegalArgumentException must be thrown
    checkName(name);    
    
    if (value != null) {
      sizeInBytes += PacketImpl.strlenUTF8(name);
      sizeInBytes += value.length + 5;
      body.put(name, value);
    }
  }
   
/** Sets a portion of the byte array value with the specified name into the 
  * Map.
  *  
  * @param name the name of the byte array
  * @param value the byte array value to set in the Map
  * @param offset the initial offset within the byte array
  * @param length the number of bytes to use
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string. 
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setBytes(String name, byte[] value, int offset, int length) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    if (value != null) {
      byte[] temp = new byte[length];
      System.arraycopy(value, offset, temp, 0, length);
    
      sizeInBytes += PacketImpl.strlenUTF8(name);
      sizeInBytes += value.length + 5;

      body.put(name, temp);
    }
  }
   
/** Sets a Unicode character value with the specified name into the Map.
  *
  * @param name the name of the Unicode character
  * @param value the Unicode character value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setChar(String name, char value)  throws MessageNotWriteableException {
		if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 3;
    
    body.put(name, new Character(value));
  }
  
/** Sets a <CODE>double</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>double</CODE>
  * @param value the <CODE>double</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setDouble(String name, double value) throws MessageNotWriteableException {
		if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);  
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 9;
    
    body.put(name, new Double(value));
  }
  
/** Sets a <CODE>float</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>float</CODE>
  * @param value the <CODE>float</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string. 
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setFloat(String name, float value)  throws MessageNotWriteableException {
   	if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 5;

    body.put(name, new Float(value));
  }
  
/** Sets an <CODE>int</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>int</CODE>
  * @param value the <CODE>int</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setInt(String name, int value)  throws MessageNotWriteableException {
		if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 5;
    
    body.put(name, new Integer(value));
  }
  
/** Sets a <CODE>long</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>long</CODE>
  * @param value the <CODE>long</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setLong(String name, long value)  throws MessageNotWriteableException {
		if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 9;
    
    body.put(name, new Long(value));
  }
  
/** Sets an object value with the specified name into the Map.
  *
  * <P>This method works only for the objectified primitive
  * object types (<code>Integer</code>, <code>Double</code>, 
  * <code>Long</code>&nbsp;...), <code>String</code> objects, and byte 
  * arrays.
  *
  * @param name the name of the Java object
  * @param value the Java object value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageFormatException if the object is invalid.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setObject(String name, Object obj) throws MessageNotWriteableException{
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    

    sizeInBytes += PacketImpl.strlenUTF8(name);
    if ( obj == null ) {
      //just the indicator (1  byte) that the value is null will be written
      sizeInBytes += 1;
    } else if (obj instanceof Boolean) {
      sizeInBytes += 2;
    } else if (obj instanceof Byte) {
      sizeInBytes += 2;
    } else if (obj instanceof byte[]) {
      byte[] temp = (byte[]) obj;
      sizeInBytes += temp.length + 5;
    } else if (obj instanceof Character) {
      sizeInBytes += 3;
    } else if (obj instanceof Double) {
      sizeInBytes += 9;
    } else if (obj instanceof Float) {
      sizeInBytes += 5;
    } else if (obj instanceof Integer) {
      sizeInBytes += 5;
    } else if (obj instanceof Long) {
      sizeInBytes += 9;
    } else if (obj instanceof Short) {
      sizeInBytes += 3;
    } else if (obj instanceof String) {
      String string = (String) obj;
      sizeInBytes += PacketImpl.strlenUTF8(string) + 1;
    } else {
      throw new MessageNotWriteableException("Map message is corrupted; contains non-primitive data.");
    }  
    body.put(name, obj);
  }

/** Sets a <CODE>short</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>short</CODE>
  * @param value the <CODE>short</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setShort(String name, short value)  throws MessageNotWriteableException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);
      
    sizeInBytes += PacketImpl.strlenUTF8(name);
    sizeInBytes += 3;
    
    body.put(name, new Short(value));
  }

/** Sets a <CODE>String</CODE> value with the specified name into the Map.
  *
  * @param name the name of the <CODE>String</CODE>
  * @param value the <CODE>String</CODE> value to set in the Map
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception IllegalArgumentException - if the name is null or if the name 
  *                         is an empty string.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void setString(String name, String value) throws MessageNotWriteableException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    checkName(name);    
    
    body.put(name, value);
    sizeInBytes += PacketImpl.strlenUTF8(name);
    int valueLength = value != null ? PacketImpl.strlenUTF8(value) : 0;
    sizeInBytes += valueLength;
    sizeInBytes += 1; //the byte indicating the type of the value (string or null)
  }

  /**
   * Method copyBodyPropertiesFrom. Copies the properties of an arbitrary javax.jms.MapMessage.
   * @param alien  them messge from which the properties will be copied
   * @throws JMSException  if en error while copying occurs
   */
  private void copyBodyPropertiesFrom(javax.jms.MapMessage alien) throws JMSException {
    java.util.Enumeration enumeration = null;
    String name;

    try {
      enumeration = alien.getMapNames();
      while(enumeration.hasMoreElements()) {
        name = (String) enumeration.nextElement();
        setObject(name, alien.getObject(name));
      }
    } catch (Exception e) {
      JMSException jmse = new JMSException("Unable to copy the alien properties.");
      jmse.initCause(e);
      jmse.setLinkedException(e);
      throw jmse;
    }
  }
  
  /**
   * Method flush. Used by senders to perform serialization of the message body 
   * into the message buffer.
   */
  public void flush() throws JMSException {
    flushToBuffer(body);
  }

}