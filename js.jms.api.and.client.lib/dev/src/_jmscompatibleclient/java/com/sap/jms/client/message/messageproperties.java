/**
 * MessageProperties.java
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

import com.sap.jms.protocol.message.MessageRequest;


public final class MessageProperties {
  
  private HashMap properties;
  
  public MessageProperties() {
    properties = new HashMap(7);
  }
  
  public MessageProperties(MessageRequest messagePacket) throws JMSException {
    if (messagePacket != null) {
      properties = messagePacket.getMessageProperties();
    }

    if (properties == null) {
      properties = new HashMap(7);
    }
  }

  /**
   * Method clearProperties. Deletes all message properties.
   */
  public void clearProperties() {
    properties.clear();
  }
  
  /* (non-Javadoc)
   * @see javax.jms.Message#getBooleanProperty(String)
   */
  public boolean getBooleanProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
    if (property != null) {
      if (property instanceof Boolean) {
        return ((Boolean)property).booleanValue();
      } else if (property instanceof String) {
        return Boolean.valueOf((String)property).booleanValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getByteProperty(String)
   */  
  public byte getByteProperty(String name) throws JMSException {  
    Object property = properties.get(name);
    
    if (property != null) {
      if (property instanceof Byte) {
        return ((Byte)property).byteValue();
      } else if (property instanceof String) {
        return Byte.valueOf((String)property).byteValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      throw new NumberFormatException("The property does not exist.");
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getDoubleProperty(String)
   */  
  public double getDoubleProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
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
      throw new NullPointerException("The property does not exist.");
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getFloatProperty(String)
   */  
  public float getFloatProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
    if (property != null) {
      if (property instanceof Float) {
        return ((Float)property).floatValue();
      } else if (property instanceof String) {
        return Float.valueOf((String)property).floatValue();
      } else {
        throw new MessageFormatException("Incorrect type conversion.");
      }
    } else {
      throw new NullPointerException("The property does not exist.");
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getIntProperty(String)
   */  
  public int getIntProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
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
      throw new NumberFormatException("The property does not exist.");
    }

  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getLongProperty(String)
   */  
  public long getLongProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
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
      throw new NumberFormatException("The property does not exist.");
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getObjectProperty(String)
   */  
  public Object getObjectProperty(String name) {
    return properties.get(name);
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getShortProperty(String)
   */  
  public short getShortProperty(String name) throws JMSException {
    Object property = properties.get(name);
    
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
      throw new NumberFormatException("The property does not exist.");
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getStringProperty(String)
   */  
  public String getStringProperty(String name) {
    Object property = properties.get(name);
    
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
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#getPropertyNames()
   */    
  public java.util.Enumeration getPropertyNames() {
    return Collections.enumeration(properties.keySet());
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#propertyExists()
   */  
  public boolean propertyExists(String name) {
    return (properties.containsKey(name));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setBooleanProperty(String, boolean)
   */  
  public void setBooleanProperty(String name, boolean value)  {
    properties.put(name, Boolean.valueOf(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setByteProperty(String, byte)
   */  
  public void setByteProperty(String name, byte value) {
    properties.put(name, new Byte(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setDoubleProperty(String, double)
   */  
  public void setDoubleProperty(String name, double value) {
    properties.put(name, new Double(value));
  }
  
  /* (non-Javadoc)
   * @see javax.jms.Message#setFloatProperty(String, float)
   */  
  public void setFloatProperty(String name, float value) {
    properties.put(name, new Float(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setIntProperty(String, int)
   */ 
  public void setIntProperty(String name, int value) {
    properties.put(name, new Integer(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setLongProperty(String, long)
   */  
  public void setLongProperty(String name, long value) {
    properties.put(name, new Long(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setObjectProperty(String, Object)
   */  
  public void setObjectProperty(String name, Object value) throws  MessageFormatException {
    if (!( value==null 
          ||(value instanceof String)
          || (value instanceof Byte)
          || (value instanceof Short)
          || (value instanceof Integer)
          || (value instanceof Long)
          || (value instanceof Boolean)
          || (value instanceof Float)
          || (value instanceof Double))) {
      throw new MessageFormatException("The property is not a wrapper of a primitive type.");
    } else {
      properties.put(name, value);
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setShortProperty(String, short)
   */  
  public void setShortProperty(String name, short value) {
    properties.put(name, new Short(value));
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#setStringProperty(String, String)
   */  
  public void setStringProperty(String name, String value) {
    properties.put(name, value);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    MessageProperties cloning = new MessageProperties();
    cloning.properties = (HashMap) properties.clone();
    return cloning;
  }
  
  public HashMap getPropertiesTable() {
    return properties;
  }
  
}

