/**
 * StreamMessage.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.message;

import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import com.sap.engine.lib.lang.Convert;
import com.sap.jms.protocol.MessageRequest;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.util.Logging;
import com.sap.jms.util.LogUtil;
import com.sap.jms.JMSConstants;


/** A <CODE>StreamMessage</CODE> object is used to send a stream of primitive
  * types in the Java programming language. It is filled and read sequentially.
  * It inherits from the <CODE>Message</CODE> interface
  * and adds a stream message body. Its methods are based largely on those
  * found in <CODE>java.io.DataInputStream</CODE> and
  * <CODE>java.io.DataOutputStream</CODE>.
  *
  * <P>The primitive types can be read or written explicitly using methods
  * for each type. They may also be read or written generically as objects.
  * For instance, a call to <CODE>StreamMessage.writeInt(6)</CODE> is
  * equivalent to <CODE>StreamMessage.writeObject(new Integer(6))</CODE>.
  * Both forms are provided, because the explicit form is convenient for
  * static programming, and the object form is needed when types are not known
  * at compile time.
  *
  * <P>When the message is first created, and when <CODE>clearBody</CODE>
  * is called, the body of the message is in write-only mode. After the 
  * first call to <CODE>reset</CODE> has been made, the message body is in 
  * read-only mode. 
  * After a message has been sent, the client that sent it can retain and 
  * modify it without affecting the message that has been sent. The same message
  * object can be sent multiple times.
  * When a message has been received, the provider has called 
  * <CODE>reset</CODE> so that the message body is in read-only mode for the client.
  * 
  * <P>If <CODE>clearBody</CODE> is called on a message in read-only mode, 
  * the message body is cleared and the message body is in write-only mode.
  * 
  * <P>If a client attempts to read a message in write-only mode, a 
  * <CODE>MessageNotReadableException</CODE> is thrown.
  * 
  * <P>If a client attempts to write a message in read-only mode, a 
  * <CODE>MessageNotWriteableException</CODE> is thrown.
  *
  * <P><CODE>StreamMessage</CODE> objects support the following conversion 
  * table. The marked cases must be supported. The unmarked cases must throw a 
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
  * @see         javax.jms.Session#createStreamMessage()
  * @see         javax.jms.BytesMessage
  * @see         javax.jms.MapMessage
  * @see         javax.jms.Message
  * @see         javax.jms.ObjectMessage
  * @see         javax.jms.TextMessage
  */
public final class JMSStreamMessage extends JMSMessage implements javax.jms.StreamMessage {
	
  private static final int STEP = 100;
  private static final byte BYTE_VALUE = 0x01;
  private static final byte BOOLEAN_VALUE = 0x02;
  private static final byte CHAR_VALUE = 0x03;
  private static final byte SHORT_VALUE = 0x04;
  private static final byte INT_VALUE = 0x05;
  private static final byte LONG_VALUE = 0x06;
  private static final byte FLOAT_VALUE = 0x07;
  private static final byte DOUBLE_VALUE = 0x08;
  private static final byte STRING_VALUE = 0x09;
  private static final byte BYTEARRAY_VALUE = 0x0A;
  private static final byte NULL_VALUE = 0x0B;
  
  private byte[] body = null;
  private int index = 0;
  private int endIndex = 0;
  private int readBytesCount = -1;
  
  public JMSStreamMessage() throws JMSException {
    super(PacketTypes.JMS_STREAM_MESSAGE);
    init();
  }
  
  public JMSStreamMessage(MessageRequest messagePacket) throws JMSException {
    super(messagePacket);
  }
  
  protected JMSStreamMessage(javax.jms.StreamMessage alien) throws JMSException {
    super(PacketTypes.JMS_STREAM_MESSAGE);
   
    copyPropertiesFrom(alien);
    copyBodyStreamFrom(alien);
  }
  
  protected void init() {
    body = new byte[STEP];
    setMode(W_MODE);
  }
  
  public void clearBody() throws JMSException {
    body = new byte[STEP];
    endIndex = 0;
    index = 0;
    setMode(W_MODE);
  }
  
  /**
   *  Makes a copy of the message.
   *
   * @return  a copy of this message.
   */
  public Object clone() {
    try {
      if (body != null) {
        messagePacket.setMessageBodyAndType(body, endIndex, PacketTypes.JMS_STREAM_MESSAGE);
      }
      
      if (messageProperties != null) {
        messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
      }
      
      return new JMSStreamMessage((MessageRequest)messagePacket.clone());
    } catch (JMSException e) {
      Logging.exception(this, e, "Could not clone StreamMessage.");
      return null;
    }
  }

  
/** Reads a <code>boolean</code> from the stream message.
  *
  * @return the <code>boolean</code> value read
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */
  public boolean readBoolean() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
  
    boolean s = false;

    try {
      byte theSwitch = 0;
      if (endIndex - index > 0) {
        theSwitch = body[index];
        switch (theSwitch) {
          case BOOLEAN_VALUE: {
            if (endIndex - index > 1) {
              s = Convert.byteArrToBoolean(body, index + 1);
              index += 2;
            } else {
              throw new MessageEOFException("End of stream reached.");
            }
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = temp.equalsIgnoreCase("true") ? true : false;
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a <code>byte</code> value from the stream message.
  *
  * @return the next byte from the stream message as a 8-bit
  * <code>byte</code>
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public byte readByte() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
  
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    byte s = 0x00;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      
      if (endIndex - index > 0) {
        theSwitch = body[index];
        switch (theSwitch) {
          case BYTE_VALUE: {
            if (endIndex - index > 1) {
              s = body[index + 1];
              index += 2;
            } else {
              throw new MessageEOFException("End of stream reached.");
            }
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            s = Byte.parseByte(temp);
            index++;
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (NumberFormatException nfe) {
      index = oldIndex;
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a byte array field from the stream message into the 
  * specified <CODE>byte[]</CODE> object (the read buffer). 
  * 
  * <P>To read the field value, <CODE>readBytes</CODE> should be 
  * successively called 
  * until it returns a value less than the length of the read buffer.
  * The value of the bytes in the buffer following the last byte 
  * read is undefined.
  * 
  * <P>If <CODE>readBytes</CODE> returns a value equal to the length of the 
  * buffer, a subsequent <CODE>readBytes</CODE> call must be made. If there 
  * are no more bytes to be read, this call returns -1.
  * 
  * <P>If the byte array field value is null, <CODE>readBytes</CODE> 
  * returns -1.
  *
  * <P>If the byte array field value is empty, <CODE>readBytes</CODE> 
  * returns 0.
  * 
  * <P>Once the first <CODE>readBytes</CODE> call on a <CODE>byte[]</CODE>
  * field value has been made,
  * the full value of the field must be read before it is valid to read 
  * the next field. An attempt to read the next field before that has 
  * been done will throw a <CODE>MessageFormatException</CODE>.
  * 
  * <P>To read the byte field value into a new <CODE>byte[]</CODE> object, 
  * use the <CODE>readObject</CODE> method.
  *
  * @param value the buffer into which the data is read
  *
  * @return the total number of bytes read into the buffer, or -1 if 
  * there is no more data because the end of the byte field has been 
  * reached
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  * 
  * @see #readObject()
  */ 
  public int readBytes(byte[] value) throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    switch (readBytesCount) {
      case -1: {
        try {
          byte theSwitch = 0;
          int available = endIndex - index;
          if (available > 0) {
            theSwitch = body[index];
            available--;
            switch (theSwitch) {
              case NULL_VALUE: {
                index++;
                return -1;
              }
    
              case BYTEARRAY_VALUE: {
                if (available < 4) {
                  throw new MessageEOFException("End of stream reached.");
                }
      
                int len = Convert.byteArrToInt(body, index + 1);
     
                available -= 4;
                if (len <= available) {
                  if (len < value.length) {
                    System.arraycopy(body, index + 5, value, 0, len);
                    index = index + 5 + len;
                    return len;
                  } else {
                    int temp = value.length;
                    System.arraycopy(body, index + 5, value, 0, temp);
                    index = index + 5 + temp;
                    readBytesCount = len - temp;
                    return temp;
                  }
                } else  {
                  throw new MessageEOFException("End of stream reached.");
                }
              }
              default: {
                  throw throwIncorrectTypeMessageFormatException();
              }
            }
          } else {
            throw new MessageEOFException("End of stream reached.");
          }
        } catch (MessageFormatException mfe) {
          throw mfe;
        } catch (MessageEOFException mee) {
          throw mee;
        } catch (Exception e) {
          throw new JMSException("Error reading from stream message.");
        }
      }
      
      case 0: {
        readBytesCount--;
        return -1;
      }
      
      default: {
        if (readBytesCount < value.length) {
          System.arraycopy(body, index, value, 0, readBytesCount);
          index += readBytesCount;
          int read = readBytesCount;
          readBytesCount = -1;
          return read;
        } else {
          int temp = value.length;
          System.arraycopy(body, index, value, 0, temp);
          index += temp;
          readBytesCount -= temp;
          return temp;
        }
      }
    }
  }

/** Reads a Unicode character value from the stream message.
  *
  * @return a Unicode character from the stream message
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid      
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public char readChar() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
  
    char s = 0x00;
    try {
      byte theSwitch = 0;
      if (endIndex - index > 0) {
        theSwitch = body[index];
        switch (theSwitch) {
          case CHAR_VALUE: {
            if (endIndex - index > 2) {
              s = Convert.byteArrToChar(body, index + 1);
              index += 3;
            } else {
              throw new MessageEOFException("End of stream reached.");
            }
            break;
          }
          
          case NULL_VALUE: {
            throw new NullPointerException();
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (NullPointerException npe) {
      throw npe;
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a <code>double</code> from the stream message.
  *
  * @return a <code>double</code> value from the stream message
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public double readDouble() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }

    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
        
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    double s = 0.0;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      int available = endIndex - index;
      if (available > 0) {
        theSwitch = body[index];
        available--;
        
        switch (theSwitch) {
          case DOUBLE_VALUE: {
            if (available >= 8) {
              s = Convert.byteArrToDouble(body, index + 1);
              index += 9;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case FLOAT_VALUE: {
            if (available >= 4) {
              s = Convert.byteArrToFloat(body, index + 1);
              index += 5;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = Double.parseDouble(temp);
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (NumberFormatException nfe) {
      index = oldIndex;
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a <code>float</code> from the stream message.
  *
  * @return a <code>float</code> value from the stream message
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public float readFloat() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    float s = 0.0f;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      int available = endIndex - index;
      if (available > 0) {
        theSwitch = body[index];
        available--;
        
        switch (theSwitch) {
          case FLOAT_VALUE: {
            if (available >= 4) {
              s = Convert.byteArrToFloat(body, index + 1);
              index += 5;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = Float.parseFloat(temp);
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (NumberFormatException nfe) {
      index = oldIndex;
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a 32-bit integer from the stream message.
  *
  * @return a 32-bit integer value from the stream message, interpreted
  * as an <code>int</code>
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public int readInt() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    int s = 0;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      int available = endIndex - index;
      if (available > 0) {
        theSwitch = body[index];
        available--;
        
        switch (theSwitch) {
          case INT_VALUE: {
            if (available >= 4) {
              s = Convert.byteArrToInt(body, index + 1);
              index += 5;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case SHORT_VALUE: {
            if (available >= 2) {
              s = Convert.byteArrToShort(body, index + 1);
              index += 3;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          
          case BYTE_VALUE: {
            if (available > 0) {
              s = body[index + 1];
              index += 2;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = Integer.parseInt(temp);
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (NumberFormatException nfe) {
      index = oldIndex;    
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a 64-bit integer from the stream message.
  *
  * @return a 64-bit integer value from the stream message, interpreted as
  * a <code>long</code>
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public long readLong() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    long s = 0L;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      int available = endIndex - index;
      if (available > 0) {
        theSwitch = body[index];
        available--;
        
        switch (theSwitch) {
          case LONG_VALUE: {
            if (available >= 8) {
              s = Convert.byteArrToLong(body, index + 1);
              index += 9;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case INT_VALUE: {
            if (available >= 4) {
              s = Convert.byteArrToInt(body, index + 1);
              index += 5;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case SHORT_VALUE: {
            if (available >= 2) {
              s = Convert.byteArrToShort(body, index + 1);
              index += 3;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          
          case BYTE_VALUE: {
            if (available > 0) {
              s = body[index + 1];
              index += 2;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = Long.parseLong(temp);
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException mee) {
      throw mee;
    } catch (NumberFormatException nfe) {
      index = oldIndex;
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads an object from the stream message.
  *
  * <P>This method can be used to return, in objectified format,
  * an object in the Java programming language ("Java object") that has 
  * been written to the stream with the equivalent
  * <CODE>writeObject</CODE> method call, or its equivalent primitive
  * <CODE>write<I>type</I></CODE> method.
  *  
  * <P>Note that byte values are returned as <CODE>byte[]</CODE>, not 
  * <CODE>Byte[]</CODE>.
  *
  * <P>An attempt to call <CODE>readObject</CODE> to read a byte field 
  * value into a new <CODE>byte[]</CODE> object before the full value of the
  * byte field has been read will throw a 
  * <CODE>MessageFormatException</CODE>.
  *
  * @return a Java object from the stream message, in objectified
  * format (for example, if the object was written as an <CODE>int</CODE>, 
  * an <CODE>Integer</CODE> is returned)
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  * 
  * @see #readBytes(byte[] value)
  */ 
  public Object readObject() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    try {
      byte theSwitch = 0; 
      if (endIndex - index > 0) {
        theSwitch = body[index];
        
        switch (theSwitch) {
          case NULL_VALUE: {
            index++;
            return null;
          }
          case STRING_VALUE: {
            return readString();
          }
          case BOOLEAN_VALUE: {
            return Boolean.valueOf(readBoolean());
          }
          case BYTE_VALUE: {
            return new Byte(readByte());
          }
          case SHORT_VALUE: {
            return new Short(readShort());
          }
          case INT_VALUE: {
            return new Integer(readInt());
          }
          case LONG_VALUE: {
            return new Long(readLong());
          }
          case FLOAT_VALUE: {
            return new Float(readFloat());
          }
          case DOUBLE_VALUE: {
            return new Double(readDouble());
          }
          case CHAR_VALUE: {
            return new Character(readChar());
          }
          case BYTEARRAY_VALUE: {
            if (mode == W_MODE) {
              throw new MessageNotReadableException("Message is write-only.");
            }
            
            if (endIndex - index + 1 >= 4) {
              int len = Convert.byteArrToInt(body, index + 1);
              if (len <= endIndex - index + 5) {
                byte[] temp = new byte[len];
                System.arraycopy(body, index + 5, temp, 0, len);
                index = index + 5 + len;
                return temp;
              } else {
                throw new MessageEOFException("End of stream reached.");
              }
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
          }
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageNotReadableException mnre) {
      throw mnre;
    } catch (MessageFormatException mfe) {
      throw mfe;
    } catch (MessageEOFException meo) {
      throw meo;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
  }

/** Reads a 16-bit integer from the stream message.
  *
  * @return a 16-bit integer from the stream message
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public short readShort() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    short s = 0;
    int oldIndex = index;
    
    try {
      byte theSwitch = 0;
      int available = endIndex - index;
      if (available > 0) {
        theSwitch = body[index];
        available--;
        
        switch (theSwitch) {
          case SHORT_VALUE: {
            if (available >= 2) {
              s = Convert.byteArrToShort(body, index + 1);
              index += 3;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          
          case BYTE_VALUE: {
            if (available > 0) {
              s = body[index + 1];
              index += 2;
            } else  {
              throw new MessageEOFException("End of stream reached.");
            }
            
            break;
          }
          case STRING_VALUE: {
            String temp = getString(index + 1);
            index++;
            s = Short.parseShort(temp);
            break;
          }
          
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch  (MessageEOFException mee) {
      throw mee;
    } catch (MessageFormatException mfe) {  
      throw mfe;
    } catch (NumberFormatException nfe) {
      index = oldIndex;
      throw nfe;
    } catch (Exception e) {
      throw new JMSException("Error reading from stream message.");
    }
    
    return s;
  }

/** Reads a <CODE>String</CODE> from the stream message.
  *
  * @return a Unicode string from the stream message
  *
  * @exception JMSException if the JMS provider fails to read the message
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of message stream has
  *                                been reached.     
  * @exception MessageFormatException if this type conversion is invalid.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public String readString() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
    
    if (readBytesCount != -1) {
        throw throwIncorrectTypeMessageFormatException();
    }
    
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
    }
    
    try {
      byte theSwitch = 0;
      if (endIndex - index > 0) {
        theSwitch = body[index];
        
        switch (theSwitch) {
          case STRING_VALUE: {
            String s = getString(index + 1);
            index++;
            return s;
          }
          case NULL_VALUE: {
            index++;
            return null;
          }
          case BOOLEAN_VALUE: {
            return "" + readBoolean();
          }
          case BYTE_VALUE: {
            return "" + readByte();
          }
          case SHORT_VALUE: {
            return "" + readShort();
          }
          case INT_VALUE: {
            return "" + readInt();
          }
          case LONG_VALUE: {
            return "" + readLong();
          }
          case FLOAT_VALUE: {
            return "" + readFloat();
          }
          case DOUBLE_VALUE: {
            return "" + readDouble();
          }
          case CHAR_VALUE: {
            return "" + readChar();
          }
          default: {
              throw throwIncorrectTypeMessageFormatException();
          }
        }
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    } catch (MessageFormatException mfe) {
      index--;
      throw mfe;
    } catch (MessageEOFException meo) {
      index--;
      throw meo;
    } catch (Exception e) {
      index--;
      throw new JMSException("Error reading from stream message.");
    }
    
  }

/** Puts the message body in read-only mode and repositions the stream
  * to the beginning.
  *  
  * @exception JMSException if the JMS provider fails to reset the message
  *                         due to some internal error.
  * @exception MessageFormatException if the message has an invalid
  *                                   format.
  */ 
  public void reset() throws JMSException {
    if (body == null) {
      body = (byte[]) messagePacket.getMessageBody();
      endIndex = messagePacket.getMessageBodySize() - Packet.SIZEOF_INT;
    }
    
    setMode(R_MODE);
    index = 0;
  }

/** Writes a <code>boolean</code> to the stream message.
  * The value <code>true</code> is written as the value 
  * <code>(byte)1</code>; the value <code>false</code> is written as 
  * the value <code>(byte)0</code>.
  *
  * @param value the <code>boolean</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */
  public void writeBoolean(boolean value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (body.length - endIndex < 2) {
      increaseBody();
    }

    try {
      body[endIndex] = BOOLEAN_VALUE;
      Convert.writeBooleanToByteArr(body, endIndex + 1, value);
      endIndex += 2;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
 }

/** Writes a <code>byte</code> to the stream message.
  *
  * @param value the <code>byte</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeByte(byte value) throws JMSException { 
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (body.length - endIndex < 2) {
      increaseBody();
    }
    
    try {
      body[endIndex] = BYTE_VALUE;
      body[endIndex + 1] = value;
      endIndex += 2;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes a byte array field to the stream message.
  *
  * <P>The byte array <code>value</code> is written to the message
  * as a byte array field. Consecutively written byte array fields are 
  * treated as two distinct fields when the fields are read.
  * 
  * @param value the byte array value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeBytes(byte[] value) throws JMSException {
    if (value != null) {
      writeBytes(value, 0, value.length);
    } else {
      writeBytes(value, 0, 0);
    }
  }

/** Writes a portion of a byte array as a byte array field to the stream 
  * message.
  *  
  * <P>The a portion of the byte array <code>value</code> is written to the
  * message as a byte array field. Consecutively written byte 
  * array fields are treated as two distinct fields when the fields are 
  * read.
  *
  * @param value the byte array value to be written
  * @param offset the initial offset within the byte array
  * @param length the number of bytes to use
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeBytes(byte[] value, int offset, int length) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    } 
    
    if (value == null) {
      if (endIndex == body.length) {
        increaseBody();
      }
      body[endIndex++] = NULL_VALUE;
      return;
    }
    
    if (length < 0 || length > value.length) {
      throw new JMSException("Cannot write data to stream message."); 
    }
    
    while ((body.length - endIndex < length +1) && (length <= value.length - offset)) {
      increaseBody();
    }

    try {
      body[endIndex] = BYTEARRAY_VALUE;
      Convert.writeIntToByteArr(body, endIndex + 1, length);
      System.arraycopy(value, offset, body, endIndex + 5, length);
      endIndex = endIndex + 5 + length;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  
  }

/** Writes a <code>char</code> to the stream message.
  *
  * @param value the <code>char</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeChar(char value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (body.length - endIndex < 3) {
      increaseBody();
    }

    try {
      body[endIndex] = CHAR_VALUE;
      Convert.writeCharToByteArr(body, endIndex + 1, value);
      endIndex += 3;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes a <code>double</code> to the stream message.
  *
  * @param value the <code>double</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeDouble(double value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }

    if (body.length - endIndex < 9) {
      increaseBody();
    }

    try {
      body[endIndex] = DOUBLE_VALUE;
      Convert.writeDoubleToByteArr(body, endIndex + 1, value);
      endIndex += 9;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes a <code>float</code> to the stream message.
  *
  * @param value the <code>float</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeFloat(float value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (body.length - endIndex < 5) {
      increaseBody();
    }

    try {
      body[endIndex] = FLOAT_VALUE;
      Convert.writeFloatToByteArr(body, endIndex + 1, value);
      endIndex += 5;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes an <code>int</code> to the stream message.
  *
  * @param value the <code>int</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeInt(int value)  throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (body.length - endIndex < 5) {
      increaseBody();
    }

    try {
      body[endIndex] = INT_VALUE;
      Convert.writeIntToByteArr(body, endIndex + 1, value);
      endIndex += 5;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes a <code>long</code> to the stream message.
  *
  * @param value the <code>long</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeLong(long value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }

    if (body.length - endIndex < 9) {
      increaseBody();
    }

    try {
      body[endIndex] = LONG_VALUE;
      Convert.writeLongToByteArr(body, endIndex + 1, value);
      endIndex += 9;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes an object to the stream message.
  *
  * <P>This method works only for the objectified primitive
  * object types (<code>Integer</code>, <code>Double</code>, 
  * <code>Long</code>&nbsp;...), <code>String</code> objects, and byte 
  * arrays.
  *
  * @param value the Java object to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageFormatException if the object is invalid.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeObject(Object obj) throws JMSException {
    if (obj == null) {
      if (endIndex == body.length) {
        increaseBody();
      }
      body[endIndex++] = NULL_VALUE;
      return;
    }
    
    if (obj instanceof Boolean) {
      writeBoolean(((Boolean)obj).booleanValue());
    } else if (obj instanceof Byte) {
      writeByte(((Byte)obj).byteValue());
    } else if (obj instanceof Short) {
      writeShort(((Short)obj).shortValue());
    } else if (obj instanceof Integer) {
      writeInt(((Integer)obj).intValue());
    } else if (obj instanceof Long) {
      writeLong(((Long)obj).longValue());
    } else if (obj instanceof Character) {
      writeChar(((Character)obj).charValue());
    } else if (obj instanceof Float) {
      writeFloat(((Float)obj).floatValue());
    } else if (obj instanceof Double) {
      writeDouble(((Double)obj).doubleValue());
    } else if (obj instanceof String) {
      writeString((String)obj);
    } else if (obj instanceof byte[]) {
      writeBytes((byte[])obj);
    } else {
        throw new MessageFormatException(LogUtil.getFailedInComponentByCaller() + "The property is not a wrapper of a primitive type.");
    }
  }

/** Writes a <code>short</code> to the stream message.
  *
  * @param value the <code>short</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeShort(short value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }

    if (body.length - endIndex < 3) {
      increaseBody();
    }

    try {
      body[endIndex] = SHORT_VALUE;
      Convert.writeShortToByteArr(body, endIndex + 1, value);
      endIndex += 3;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

/** Writes a <code>String</code> to the stream message.
  *
  * @param value the <code>String</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
  public void writeString(java.lang.String value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (value == null) {
      if (endIndex == body.length) {
        increaseBody();
      }
      body[endIndex++] = NULL_VALUE;
      return;
    }
    
    int bytesLength = 2 * value.length();

    while (body.length - endIndex < bytesLength + 5) {
      increaseBody();
    }

    try {
      body[endIndex] = STRING_VALUE;
      Convert.writeIntToByteArr(body, endIndex + 1, value.length());
      Convert.writeUStringToByteArr(body, endIndex + 5, value);
      endIndex = endIndex + 5 + bytesLength;
    } catch (Exception e) {
      throw new JMSException("Cannot write data to stream message.");
    }
  }

  /**
   * Method increaseBody. Increases the size of the buffer of the message by a number
   * of bytes specified in STEP.
   */
  private void increaseBody() {
    byte[] b = new byte[body.length + STEP];
    System.arraycopy(body, 0, b, 0, endIndex);
    body = b;
  }
  
  /**
   * Method getString. Helper method for this class. Reads a string from the byte
   * array given starting index.
   * @param fromIndex  starting index from where to read the string
   * @return String  the read string
   * @throws MessageEOFException  thrown if unexpected end of file is encountered
   */
  private String getString(int fromIndex) throws MessageEOFException {
    int available = endIndex - fromIndex;
    int len = 0;
    
    if (available >= 4) {
      len = Convert.byteArrToInt(body, fromIndex);
      available -= 4;
    } else {
      throw new MessageEOFException("End of stream reached.");
    }
    
    if (available >= len) {
      String s =  Convert.byteArrToUString(body, fromIndex + 4, len);
      index = index + 4 + 2*len;
      return s;
    } else {
      throw new MessageEOFException("End of stream reached.");
    }
  }

  /**
   * Method copyBodyStreamFrom. Copies the stream from another javax.jms.Message.
   * @param alien  the source message from which the stream will be copied
   * @throws JMSException  thrown if internal error occurs during the copy process
   */
  public void copyBodyStreamFrom(javax.jms.StreamMessage alien) throws JMSException {
    clearBody();
    alien.reset();
    java.util.Vector v = new java.util.Vector(5,5);
    Object o = null;
    try {
      while (true) {
        o = alien.readObject();
        v.add(o);
        writeObject(o);
      }
    } catch(MessageEOFException meof) {
      setMode(R_MODE);
      alien.clearBody();
      while (v.size() > 0) {
        alien.writeObject(v.remove(0));
      }
      setMode(W_MODE);
    }
  }
  
	/**
   * Method flush. Used by senders to perform serialization of the message body 
   * into the message buffer.
   */
  public void flush() throws JMSException {
    flushToBuffer(body, endIndex);
  }
}