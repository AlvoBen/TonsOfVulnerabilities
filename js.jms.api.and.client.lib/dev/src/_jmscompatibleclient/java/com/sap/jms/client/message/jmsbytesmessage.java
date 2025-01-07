/**
 * BytesMessage.java
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
import com.sap.engine.lib.util.ArrayByte;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.Packet;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.message.MessageRequest;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.JMSConstants;


/** A <CODE>BytesMessage</CODE> object is used to send a message containing a 
* stream of uninterpreted bytes. It inherits from the <CODE>Message</CODE> 
* interface and adds a bytes
* message body. The receiver of the message supplies the interpretation
* of the bytes.
*
* <P>The <CODE>BytesMessage</CODE> methods are based largely on those found in
* <CODE>java.io.DataInputStream</CODE> and
* <CODE>java.io.DataOutputStream</CODE>.
*
* <P>This message type is for client encoding of existing message formats. 
* If possible, one of the other self-defining message types should be used 
* instead.
*
* <P>Although the JMS API allows the use of message properties with byte 
* messages, they are typically not used, since the inclusion of properties 
* may affect the format.
*
* <P>The primitive types can be written explicitly using methods
* for each type. They may also be written generically as objects.
* For instance, a call to <CODE>BytesMessage.writeInt(6)</CODE> is
* equivalent to <CODE>BytesMessage.writeObject(new Integer(6))</CODE>.
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
* the message body is cleared and the message is in write-only mode.
*
* <P>If a client attempts to read a message in write-only mode, a 
* <CODE>MessageNotReadableException</CODE> is thrown.
*
* <P>If a client attempts to write a message in read-only mode, a 
* <CODE>MessageNotWriteableException</CODE> is thrown.
*
*
* @see         javax.jms.Session#createBytesMessage()
* @see         javax.jms.MapMessage
* @see         javax.jms.Message
* @see         javax.jms.ObjectMessage
* @see         javax.jms.StreamMessage
* @see         javax.jms.TextMessage
*/
public final class JMSBytesMessage extends JMSMessage implements javax.jms.BytesMessage, JMSConstants {

	private static final LogService logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private final static String LOG_COMPONENT = "message.JMSBytesMessage";
	private static final int STEP = 512;
	private int index;
  private int endIndex;
  private byte[] body = null;

  public JMSBytesMessage() throws JMSException {
    super(PacketTypes.JMS_BYTES_MESSAGE);
    init();
  }
  
  public JMSBytesMessage(MessageRequest messagePacket) throws JMSException {
    super(messagePacket);
  }
  
  protected JMSBytesMessage(javax.jms.BytesMessage message) throws JMSException { 
    super(PacketTypes.JMS_BYTES_MESSAGE);
    clearBody();
    ArrayByte bodyBytes = new ArrayByte();
    copyPropertiesFrom(message);

    try {
      message.reset();
      while (true) {
        bodyBytes.addElement(message.readByte());
      }
    } catch (Exception e)  {    //(JMSException jmse) {
      message.clearBody();
      // until MessageEOFException occurs there are more bytes left
    }
      
    body = bodyBytes.toArray();
    setMode(R_MODE);
    message.writeBytes(body);
    setMode(W_MODE);
    
    index = 0;
    endIndex = body.length;
  }
  
  protected void init() {
    setMode(W_MODE);
    body = new byte[STEP];
    index = 0;
    endIndex = 0;
  }
  
  public Object clone() {
    try {
      if (body != null) {
        messagePacket.setMessageBodyAndType(body, endIndex, PacketTypes.JMS_BYTES_MESSAGE);
      }
      
      if (messageProperties != null) {
        messagePacket.setMessageProperties(messageProperties.getPropertiesTable());
      }
      
      return new JMSBytesMessage((MessageRequest)messagePacket.clone());
    } catch (JMSException e) {
      logService.errorTrace(LOG_COMPONENT, "Could not clone BytesMessage.");
      logService.exception(LOG_COMPONENT, e);
      return null;
    }
  }

  /* (non-Javadoc)
   * @see javax.jms.Message#clearBody()
   */
  public void clearBody() throws JMSException {
   	body = new byte[STEP];
		index = 0;
    endIndex = 0;
		setMode(W_MODE);
	}

  
  /** Reads a <code>boolean</code> from the bytes message stream.
    *
    * @return the <code>boolean</code> value read
    *
    * @exception JMSException if the JMS provider fails to read the message 
    *                         due to some internal error.
    * @exception MessageEOFException if unexpected end of bytes stream has 
    *                                been reached.
    * @exception MessageNotReadableException if the message is in write-only 
    *                                        mode.
    */
	public boolean readBoolean() throws JMSException {
		if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}

    boolean b = false;
    if (body == null) {
      byte temp = 0; 
      
      try{
        temp = messagePacket.readByte();
      } catch (BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
      
      if (temp == (byte)0) {
        b = false;
      } else if (temp == (byte)1) {
        b = true;
      } else {
        throw new JMSException("Cannot convert to boolean.");
      }
    } else if (index < endIndex) {
      if (body[index]==(byte) 0) {
        b = false;
      } else if (body[index]==(byte) 1) {
        b = true;
      } else {
			  throw new JMSException("Cannot convert to boolean.");
		  }
      
      index++;
    } else {
      throw new MessageEOFException("End of stream reached.");
    }
                   
		
		return b;
	}

/** Reads a signed 8-bit value from the bytes message stream.
  *
  * @return the next byte from the bytes message stream as a signed 8-bit
  * <code>byte</code>
  *
  * @exception JMSException if the JMS provider fails to read the message 
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of bytes stream has 
  *                                been reached.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */ 
  public byte readByte() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		byte b = -1;
    
    if (body == null) {
      try {
        b = messagePacket.readByte();
      } catch (BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else if (index < endIndex) {
      b = body[index];
      index++;
    } else {
      throw new MessageEOFException("End of stream reached.");
    }
        
		return b;
	}
  
/** Reads a byte array from the bytes message stream.
	*
	* <P>If the length of array <code>value</code> is less than the number of 
	* bytes remaining to be read from the stream, the array should 
	* be filled. A subsequent call reads the next increment, and so on.
	* 
	* <P>If the number of bytes remaining in the stream is less than the 
	* length of 
	* array <code>value</code>, the bytes should be read into the array. 
	* The return value of the total number of bytes read will be less than
	* the length of the array, indicating that there are no more bytes left 
	* to be read from the stream. The next read of the stream returns -1.
	*
	* @param value the buffer into which the data is read
	*
	* @return the total number of bytes read into the buffer, or -1 if 
	* there is no more data because the end of the stream has been reached
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public int readBytes(byte[] value) throws JMSException {
    return readBytes(value, value.length);
	}
  
/** Reads a portion of the bytes message stream.
	*
	* <P>If the length of array <code>value</code> is less than the number of
	* bytes remaining to be read from the stream, the array should 
	* be filled. A subsequent call reads the next increment, and so on.
	* 
	* <P>If the number of bytes remaining in the stream is less than the 
	* length of 
	* array <code>value</code>, the bytes should be read into the array. 
	* The return value of the total number of bytes read will be less than
	* the length of the array, indicating that there are no more bytes left 
	* to be read from the stream. The next read of the stream returns -1.
	*
	* <p> If <code>length</code> is negative, or
	* <code>length</code> is greater than the length of the array
	* <code>value</code>, then an <code>IndexOutOfBoundsException</code> is
	* thrown. No bytes will be read from the stream for this exception case.
	*  
	* @param value the buffer into which the data is read
	* @param length the number of bytes to read; must be less than or equal to
	*        <code>value.length</code>
	* 
	* @return the total number of bytes read into the buffer, or -1 if
	* there is no more data because the end of the stream has been reached
	*  
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public int readBytes(byte[] value, int length) throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
    if (length < 0 || length > value.length) {
      throw new ArrayIndexOutOfBoundsException();
    }
    
    int temp = 0;
    int pos = messagePacket.getPosition();
    
    if (body == null) {
      temp = messagePacket.getCapacity() - pos;     
      messagePacket.setPosition(pos);
      
      if (temp > 0) {
          if (length <= temp) {
            temp = length;
          }
          
          try {
            for (int i = 0; i < temp; i++) {
              value[i] = messagePacket.readByte();
            }
          } catch (BufferUnderflowException bue) {
            //this exception should not be thrown
            messagePacket.setPosition(pos);
            throw new MessageEOFException("End of stream reached.");
          }
       } else { 
         // Signal EOF   
         return -1;
         //throw new MessageEOFException("End of stream reached.");
       }
       
    } else {
      temp = endIndex - index;
      
      if (index < endIndex) {
        if (length <= temp) {
          temp = length;          
        }
        
        System.arraycopy(body, index, value, 0, temp);
        index += temp;
      } else {
        return -1;
        //throw new MessageEOFException("End of stream reached.");
      }
    }
    
		return temp;
	}
  
/** Reads a Unicode character value from the bytes message stream.
	*
	* @return the next two bytes from the bytes message stream as a Unicode
	* character
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public char readChar() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
    int b = -1;
    
    if (body == null) {
      try {
        b = messagePacket.readChar();
      } catch (BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
  		if (index < endIndex + 1) {
  			b = (int) (
  				(unsigned(body[index])*256) +
  				unsigned(body[index+1])
  			);
  			index += 2;
  		} else {
        throw new MessageEOFException("End of stream reached.");
      }
    }
    
		return (char)b;
	}
  
/** Reads a <code>double</code> from the bytes message stream.
	*
	* @return the next eight bytes from the bytes message stream, interpreted
	* as a <code>double</code>
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public double readDouble() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
    if (body == null) {
      try {
        return messagePacket.readDouble();
      } catch (BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
      long b = -1;
      
  		if (index < endIndex + 7) {
  	  	b = (long) (unsigned(body[index])<<56);
        b += (long) (unsigned(body[index+1])<<48);
        b += (long) (unsigned(body[index+2])<<40);
        b += (long) (unsigned(body[index+3])<<32);
        b += (long) (unsigned(body[index+4])<<24);
        b += (long) (unsigned(body[index+5])<<16);
        b += (long) (unsigned(body[index+6])<<8);
        b += (long) (unsigned(body[index+7]));
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
      
  		index += 8;
  		return Double.longBitsToDouble(b);
    }
	}
  
/** Reads a <code>float</code> from the bytes message stream.
	*
	* @return the next four bytes from the bytes message stream, interpreted
	* as a <code>float</code>
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public float readFloat() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
  
		if (body == null) {
      try{
        return messagePacket.readFloat();
      } catch(BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
      int b = -1;
      
      if (index < endIndex + 3) {
  	  	b = (int)(unsigned(body[index])<<24);
        b += (int)(unsigned(body[index+1])<<16);
        b += (int)(unsigned(body[index+2])<<8);
        b += (int)(unsigned(body[index+3]));
  			index += 4;
  		} else {
        throw new MessageEOFException("End of stream reached.");
      }
      
  		return Float.intBitsToFloat(b);
    }
	}
 
/** Reads a signed 32-bit integer from the bytes message stream.
	*
	* @return the next four bytes from the bytes message stream, interpreted
	* as an <code>int</code>
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public int readInt() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		int b = -1;
    
    if (body == null) {
      try{
        b = messagePacket.readInt();
      } catch(BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
  		if (index < endIndex + 3) {
  	  	b = (int)(unsigned(body[index])<<24);
        b += (int)(unsigned(body[index+1])<<16);
        b += (int)(unsigned(body[index+2])<<8);
        b += (int)(unsigned(body[index+3]));
  			index += 4;
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    }
    
		return b;
	}
  
/** Reads a signed 64-bit integer from the bytes message stream.
	*
	* @return the next eight bytes from the bytes message stream, interpreted
	* as a <code>long</code>
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
  public long readLong() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		long b = -1;
    if (body == null) {
      try{
        b = messagePacket.readLong();
      } catch(BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
      if (index < endIndex + 7) {
  	  	b = (long) (unsigned(body[index])<<56);
  	  	b += (long) (unsigned(body[index+1])<<48);
  	  	b += (long) (unsigned(body[index+2])<<40);
  	  	b += (long) (unsigned(body[index+3])<<32);
  	  	b += (long) (unsigned(body[index+4])<<24);
  	  	b += (long) (unsigned(body[index+5])<<16);
  	  	b += (long) (unsigned(body[index+6])<<8);
  	  	b += (long) (unsigned(body[index+7]));
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
      
  		index += 8;
    }
    
		return b;		
	}
  
/** Reads a signed 16-bit number from the bytes message stream.
  *
  * @return the next two bytes from the bytes message stream, interpreted as
  * a signed 16-bit number
  *
  * @exception JMSException if the JMS provider fails to read the message 
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of bytes stream has 
  *                                been reached.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */   
	public short readShort() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		short b = -1;
    
    if (body == null) {
      try{
        b = messagePacket.readShort();
      } catch(BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
  		if (index < endIndex + 1) {
        b = (short) (unsigned(body[index])*256 + unsigned(body[index+1]));
  			index += 2;
  		} else {
        throw new MessageEOFException("End of stream reached.");
      }
    }

		return b;
	}
  
/** Reads an unsigned 8-bit number from the bytes message stream.
  *  
  * @return the next byte from the bytes message stream, interpreted as an
  * unsigned 8-bit number
  *
  * @exception JMSException if the JMS provider fails to read the message 
  *                         due to some internal error.
  * @exception MessageEOFException if unexpected end of bytes stream has 
  *                                been reached.
  * @exception MessageNotReadableException if the message is in write-only 
  *                                        mode.
  */	
  public int readUnsignedByte() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		int b = -1;
    
    if (body == null) {
      try{
        b = (int) unsigned(messagePacket.readByte());
      } catch(BufferUnderflowException bue) {
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
  		if (index < endIndex) {
  			b = (int) unsigned(body[index]);
  			index += 1;
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    }
    
    return b;
	}

/** Reads an unsigned 16-bit number from the bytes message stream.
	*  
	* @return the next two bytes from the bytes message stream, interpreted as
	* an unsigned 16-bit integer
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public int readUnsignedShort() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
		}
    
		int b = -1;
    int pos = 0;
    
    if (body == null) {
      try{
        pos = messagePacket.getPosition();
        b = (int) unsigned((int) (unsigned(messagePacket.readByte())*256 + unsigned(messagePacket.readByte())));
      } catch(BufferUnderflowException bue) {
        messagePacket.setPosition(pos);
        throw new MessageEOFException("End of stream reached.");
      }
    } else {
      if (index < endIndex + 1) {
  			b = (int) unsigned((int) (unsigned(body[index])*256 + unsigned(body[index+1])));
  			index += 2;
      } else {
        throw new MessageEOFException("End of stream reached.");
      }
    }
    
		return (int) b;
	}
  
/** Reads a string that has been encoded using a modified UTF-8
	* format from the bytes message stream.
	*
	* <P>For more information on the UTF-8 format, see "File System Safe
	* UCS Transformation Format (FSS_UTF)", X/Open Preliminary Specification,
	* X/Open Company Ltd., Document Number: P316. This information also
	* appears in ISO/IEC 10646, Annex P.
	*
	* @return a Unicode string from the bytes message stream
	*
	* @exception JMSException if the JMS provider fails to read the message 
	*                         due to some internal error.
	* @exception MessageEOFException if unexpected end of bytes stream has 
	*                                been reached.
	* @exception MessageNotReadableException if the message is in write-only 
	*                                        mode.
	*/ 
	public String readUTF() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }
		StringBuffer b = new StringBuffer();
		int len = 0;
    int pos = 0; 
    
    if (body == null) {
      pos = messagePacket.getPosition();
      byte[] temp = new byte[3];
      
      try {
        len = ((readByte() & 0xFF) << 8);
        len |= (readByte() & 0xFF);
      } catch (BufferUnderflowException bue) {
        messagePacket.setPosition(pos);
        throw new MessageEOFException("End of stream reached.");
      }
      
      try {
        int totalRead = 0;
        
        while (totalRead < len) {
          temp[0] = messagePacket.readByte();
          
          if ((temp[0] & 0x80) == 0) {
            b.append((char) temp[0]);
            totalRead++;
            continue;
          }

          temp[1] = messagePacket.readByte();
          
          if ((temp[0] & 0x20) == 0) {
            b.append((char) (((temp[0] & 0x1F) << 6) & (temp[1] & 0xFF3F)));
            totalRead += 2;
            continue;
          }
          
          temp[2] = messagePacket.readByte();
          b.append((char) (((temp[0] & 0x0F) << 12) & ((temp[1] & 0xFF3F) << 6)) & (temp[2] & 0xFF3F));
          totalRead += 3;
        }
      } catch (BufferUnderflowException bue) {
        messagePacket.setPosition(pos - Packet.SIZEOF_INT);
        throw new MessageEOFException("End of stream reached.");
      }
    } else if (index < endIndex + Packet.SIZEOF_INT) {
      pos = index;
			len = Convert.byteArrToInt(body, index);
      index += Packet.SIZEOF_INT;
      
      for (int i = 0; i < len; i++) {
        if (index >= endIndex) {
          index = pos;
          throw new MessageEOFException("End of stream reached.");
        }
        
        if ((body[index] & 0x80) == 0) {
          b.append(body[index++]);
          continue;
        }

        if (index + 1 >= endIndex) {
          index = pos;
          throw new MessageEOFException("End of stream reached.");
        }
        
        if ((body[index] & 0x20) == 0) {
          b.append((char) (((body[index] & 0x1F) << 6) & (body[index + 1] & 0xFF3F)));
          index += 2;
          continue;
        }
        
        if (index + 2 >= endIndex) {
          index = pos;
          throw new MessageEOFException("End of stream reached.");
        }
        
        b.append((char) (((body[index] & 0x0F) << 12) & ((body[index + 1] & 0xFF3F) << 6)) & (body[index + 2] & 0xFF3F));
        index += 3;
      }
    } else {
      throw new MessageEOFException("End of stream reached.");
    }
    
		return b.toString();
	}
  
/** Puts the message body in read-only mode and repositions the stream of 
  * bytes to the beginning.
  *  
  * @exception JMSException if the JMS provider fails to reset the message
  *                         due to some internal error.
  * @exception MessageFormatException if the message has an invalid
  *                         format.
  */ 
 	public void reset() throws JMSException {
    setMode(R_MODE);
    if (body == null) {
      messagePacket.setPosition(messagePacket.getMessageBodyOffset() - MessageRequest.LEN_PACKET_HEADER + Packet.SIZEOF_INT);
    } else {
		  index = 0;
    }
	}
  
/** Writes a <code>boolean</code> to the bytes message stream as a 1-byte 
	* value.
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
    
    try {
      if (endIndex != body.length) {
        body[endIndex] = (value)? (byte) 1: (byte) 0;
      } else {
        increaseBody();
		    body[endIndex] = (value)? (byte) 1: (byte) 0;
      }
    } catch (Exception e) {
      throw new JMSException("Cannot write byte value.");
    }
    
    endIndex++;
	}
  
/** Writes a <code>byte</code> to the bytes message stream as a 1-byte 
	* value.
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

		try {
      if (endIndex != body.length) {
        body[endIndex++] = value;
      } else {
			  byte[] b = new byte[body.length + STEP];
			  System.arraycopy(body, 0, b, 0, body.length);
			  b[endIndex++] = value;
			  body = b;
      }
		} catch(Exception e) {
			throw new JMSException("Cannot write byte value.");
		}
	}
  
/** Writes a byte array to the bytes message stream.
	*
	* @param value the byte array to be written
	*
	* @exception JMSException if the JMS provider fails to write the message
	*                         due to some internal error.
	* @exception MessageNotWriteableException if the message is in read-only 
	*                                         mode.
	*/ 
	public void writeBytes(byte[] value) throws JMSException {
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    if (value == null) {
      throw new NullPointerException("Null pointer exception");
    }
    
    try {
		  if((body.length - endIndex) < value.length) {
			  byte[] b = new byte[body.length + value.length];
			  System.arraycopy(body, 0, b, 0, endIndex);
        System.arraycopy(value, 0, b, endIndex, value.length);
        body = b;        
		  } else  {
        System.arraycopy(value, 0, body, endIndex, value.length);
		  } 
      
      endIndex += value.length;
    } catch(Exception e) {
			throw new JMSException("Cannot write byte value.");
		}
	}	
  
/** Writes a portion of a byte array to the bytes message stream.
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
  
  // opitmize it
 	public void writeBytes(byte[] value, int offset, int length) throws JMSException {
    int size = length < (value.length - offset) ? length : (value.length - offset);
    byte[] temp = new byte[size];
    System.arraycopy(value, offset, temp, 0, size);
	  writeBytes(temp);
 	}
  
/** Writes a <code>char</code> to the bytes message stream as a 2-byte
	* value, high byte first.
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
    
    try {
      if (body.length - endIndex < 2) {
        increaseBody();
      } 
		  
      body[endIndex] = (byte) (((int) value)/ 256);
		  body[endIndex + 1] = (byte) (((int) value)%256);
    } catch(Exception e) {
			throw new JMSException("Cannot write byte value.");
		}
    
    endIndex += 2;
	}

/** Converts the <code>double</code> argument to a <code>long</code> using 
  * the
  * <code>doubleToLongBits</code> method in class <code>Double</code>,
  * and then writes that <code>long</code> value to the bytes message
  * stream as an 8-byte quantity, high byte first.
  *
  * @param value the <code>double</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */   
	public void writeDouble(double value) throws JMSException {
    long longValue = Double.doubleToLongBits(value);
    internalWrite(longValue, 8);
	}	
  
/** Converts the <code>float</code> argument to an <code>int</code> using 
  * the
  * <code>floatToIntBits</code> method in class <code>Float</code>,
  * and then writes that <code>int</code> value to the bytes message
  * stream as a 4-byte quantity, high byte first.
  *
  * @param value the <code>float</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
 	public void writeFloat(float value) throws JMSException {
    int intValue = Float.floatToIntBits(value);
    internalWrite(intValue, 4);
	}			
  
/** Writes an <code>int</code> to the bytes message stream as four bytes, 
	* high byte first.
	*
	* @param value the <code>int</code> to be written
	*
	* @exception JMSException if the JMS provider fails to write the message
	*                         due to some internal error.
	* @exception MessageNotWriteableException if the message is in read-only 
	*                                         mode.
	*/ 
	public void writeInt(int value) throws JMSException {
    internalWrite(value, 4);
	}
  
/** Writes a <code>long</code> to the bytes message stream as eight bytes, 
  * high byte first.
  *
  * @param value the <code>long</code> to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
 	public void writeLong(long value) throws JMSException {		
    internalWrite(value, 8);
	}
  
/** Writes an object to the bytes message stream.
	*
	* <P>This method works only for the objectified primitive
	* object types (<code>Integer</code>, <code>Double</code>, 
	* <code>Long</code>&nbsp;...), <code>String</code> objects, and byte 
	* arrays.
	*
	* @param value the object in the Java programming language ("Java 
	*              object") to be written; it must not be null
	*
	* @exception JMSException if the JMS provider fails to write the message
	*                         due to some internal error.
	* @exception MessageFormatException if the object is of an invalid type.
	* @exception MessageNotWriteableException if the message is in read-only 
	*                                         mode.
	* @exception java.lang.NullPointerException if the parameter 
	*                                           <code>value</code> is null.
	*/ 
	public void writeObject(Object obj) throws JMSException {		
    if (obj == null) {
      throw new NullPointerException("Null pointer exception");
    }
    
    if (obj instanceof Boolean) {
      writeBoolean(((Boolean) obj).booleanValue());
    } else if (obj instanceof Byte) {
      writeByte(((Byte) obj).byteValue());
    } else if (obj instanceof Character) {
      writeChar(((Character) obj).charValue());
    } else if (obj instanceof Double) {
      writeDouble(((Double) obj).doubleValue());
    } else if (obj instanceof Float) {
      writeFloat(((Float) obj).floatValue());
    } else if (obj instanceof Integer) {
      writeInt(((Integer) obj).intValue());
    } else if (obj instanceof Long) {
      writeLong(((Long) obj).longValue());
    } else if (obj instanceof Short) {
      writeShort(((Short) obj).shortValue());
    } else if (obj instanceof String) {
      writeUTF((String) obj);
    } else {
      try {
        byte[] b = (byte[]) obj;
        writeBytes(b);
      } catch (ClassCastException cce) {
        throw new MessageFormatException("The property is not a wrapper of a primitive type.");
      }
    }
	}
  
/** Writes a <code>short</code> to the bytes message stream as two bytes,
	* high byte first.
	*
	* @param value the <code>short</code> to be written
	*
	* @exception JMSException if the JMS provider fails to write the message
	*                         due to some internal error.
	* @exception MessageNotWriteableException if the message is in read-only 
	*                                         mode.
	*/ 
	public void writeShort(short value) throws JMSException {
		internalWrite(value, 2);
	}
  
/** Writes a string to the bytes message stream using UTF-8 encoding in a 
  * machine-independent manner.
  *
  * <P>For more information on the UTF-8 format, see "File System Safe 
  * UCS Transformation Format (FSS_UTF)", X/Open Preliminary Specification,       
  * X/Open Company Ltd., Document Number: P316. This information also 
  * appears in ISO/IEC 10646, Annex P. 
  *
  * @param value the <code>String</code> value to be written
  *
  * @exception JMSException if the JMS provider fails to write the message
  *                         due to some internal error.
  * @exception MessageNotWriteableException if the message is in read-only 
  *                                         mode.
  */ 
	public void writeUTF(String value) throws JMSException {	
  	if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    int len = value.length();
    try {
      if (endIndex + 2 + (len * 3) > body.length) {
        byte[] b = new byte[body.length + Packet.SIZEOF_INT + (len * 3) + STEP];
        System.arraycopy(body, 0, b, 0, endIndex);
        body = b;
      }
      
      int start = endIndex;
      endIndex += 2;
      int UTFSize = 0;
      int currentLetter;
      
		  for(int i=0; i < len ; i++) {
        currentLetter = value.charAt(i) & 0x0000FFFF;
        UTFSize = ((0x0000F800 & currentLetter) == 0) ? (((0x00000780 & currentLetter) == 0) ? 1 : 2) : 3;
        
        if (UTFSize == 1) {
          body[endIndex++] = (byte) currentLetter;
        } else if (UTFSize == 2) {
          body[endIndex + 1] = (byte) (0x80 & (0x3F & currentLetter));
          body[endIndex] = (byte) (0xC0 & (0x1F & (currentLetter >>> 6))); 
          endIndex+=2;
        } else {
          body[endIndex + 2] = (byte) (0x80 & (0x3F & currentLetter));
          body[endIndex + 1] = (byte) (0x80 & (0x3F & (currentLetter >>> 6)));
          body[endIndex] = (byte) (0xE0 & (0x0F & (currentLetter >>> 12)));
        }        
		  }
      
      body[start] = (byte) (0xFF & ((endIndex - start - 2) >> 8));
      body[start + 1] = (byte) (0xFF & (endIndex - start - 2));
    } catch(Exception e) {
			throw new JMSException("Cannot write byte value.");
		}
	}

  
  /**
   * Method unsigned. Helper method that returns the value of a byte considered to be unsigned as long.
   * @param signed  a byte considered to be unsigned
   * @return long  the actual value of the byte as long
   */
  private long unsigned(byte signed) {
		return (signed<0)? (long) signed+256: (long) signed;
	}

  /**
   * Method unsigned. Helper method that returns the value of an int considered to be unsigned as long.
   * @param signed  an int considered to be unsigned
   * @return long  the actual value of the int as long
   */
  private long unsigned(int signed) {
		return (signed<0)? (long) signed+256*256: (long) signed;
	}
  
  /**
   * Method internalWrite. Helper method that writes a specified number of bytes form 
   * a long varible into a the body of the message.
   * @param longValue  the variable from which the bytes are extracted
   * @param numBytes  how many bytes to write (must be less than 8) 
   * @throws JMSException  thrown when a byte extracted from the long variable cannot be written to the body array
   */
  private void internalWrite(long longValue, int numBytes) throws JMSException{
    if (mode == R_MODE) {
      throw new MessageNotWriteableException("Message is read-only.");
    }
    
    try {
      if (body.length - endIndex < numBytes) {
        increaseBody();
      } 
		  
      for (int j=numBytes-1; j > -1; j--) {
        body[endIndex + j] = (byte)longValue;
        longValue = longValue >>> 8;
      }
    } catch(Exception e) {
			throw new JMSException("Cannot write byte value.");
		}
    
    endIndex += numBytes;
  }

  /**
   * Method increaseBody. Increases the body of the message with the specified in STEP
   * number of bytes.
   */
  private void increaseBody() {
    byte[] temp = new byte[body.length + STEP];
    System.arraycopy(body, 0, temp, 0, endIndex);
    body = temp;
  }

  /* (non-Javadoc)
   * @see javax.jms.BytesMessage#getBodyLength()
   */
  public long getBodyLength() throws JMSException {
    if (mode == W_MODE) {
      throw new MessageNotReadableException("Message is write-only.");
    }    
    
    if (body == null) {
      return messagePacket.getMessageBodySize() - Packet.SIZEOF_INT;
    } else {
      return endIndex;
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