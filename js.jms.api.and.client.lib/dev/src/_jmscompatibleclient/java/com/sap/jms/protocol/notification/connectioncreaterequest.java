/**
 * ConnectionCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
/**

import com.sap.jms.protocol.JMSPacketImpl;
 * ConnectionCreateRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class ConnectionCreateRequest extends PacketImpl {

	/** The ID for this packet. */
	public static final byte TYPE_ID = CONNECTION_CREATE_REQUEST;
	
	/** Position of the magic number in the stream */
	static final int  POS_MAGIC_NUMBER = 0;
	
	/** Position of the protocol version number */
	static final int  POS_PROTOCOL_VERSION = POS_MAGIC_NUMBER + SIZEOF_INT;

	/** Position of the credentials type */
	static final int  POS_CREDENTIALS_TYPE = POS_PROTOCOL_VERSION + SIZEOF_INT;
	
	/** Position of the credentials */				
	static final int  POS_CREDENTIALS = POS_CREDENTIALS_TYPE + SIZEOF_BYTE;
	
	static final int  SIZE = POS_CREDENTIALS;

	/** Magic number */
	static final int   MAGIC_NUMBER = 24081961;	 		

	static final byte CREDENTIALS_USER_PASSWORD = 0x0;
	static final byte CREDENTIALS_CERTIFICATE   = 0x1;
  
    /** Version of the protocol  
    * The version is made of 2 parts - major protocol version, which currently is not used for anything except
    * debug statements and a compatibility version - the last 3 digits of the protocol version.
    * In case the compatibility version between the client and the server is different an exception
    * will be thrown.
    * */
   // private static int  PROTOCOL_VERSION = 700110;  
	
  
	public ConnectionCreateRequest() {}
					
	/**
	 * Constructor for ConnectionCreateRequest.
	 * @param instance the name of the JMS server instance
	 * @param username the user's logon name
	 * @param password the user's password
	 * @exception JMSException if something went wrong
	 */	
	public ConnectionCreateRequest(String instance, String username, String password, String factoryName) throws JMSException
	{
		String[] value = new String[] {instance, username, password, factoryName};
		allocate(TYPE_ID, SIZE + PacketImpl.strlenUTF8(value));
		setInt(POS_MAGIC_NUMBER,      MAGIC_NUMBER);
		setInt(POS_PROTOCOL_VERSION,  PROTOCOL_VERSION);
		setByte(POS_CREDENTIALS_TYPE, CREDENTIALS_USER_PASSWORD);
		setUTF8Array(POS_CREDENTIALS, value);	
	}

	/**
	 * Constructor for ConnectionCreateRequest.
	 */					
	public ConnectionCreateRequest(byte[] buffer, int offset, int length)
	{
		super(buffer, offset, length);
	}
   /**
     *  Returns the major protocol version
     *  @return the major version number of the protocol version 
     */	
    public final int getProtocolMajorVersion() throws BufferUnderflowException
    {
        return getInt(POS_PROTOCOL_VERSION) / 1000;
    }
   

	/**
	 *  Returns the protocol revision number
	 *  @return the revision number of the protocol version
	 */
	public int getProtocolRevisionNumber() throws BufferUnderflowException
	{
		int version = getInt(POS_PROTOCOL_VERSION);
		int major = version / 10000;
		int minor = (version - major * 10000) / 100;
		return (version - major * 10000 - minor * 100);
	}
    /**
     *  Returns the compatibility protocol version as set by the client
     *  @return the minor version number of the protocol version 
     */
    public final int getProtocolCompatibilityVersion() throws BufferUnderflowException
    {
      int version = getInt(POS_PROTOCOL_VERSION);
      return version % 1000;
    }
 
   	/**
	 * Returns the name if the JMS server instance
	 * @return the instance name of the addressed JMS server
	 */			
	public String getJMSServerInstanceName() throws BufferUnderflowException
	{		
		String[] value = getStringArray(POS_CREDENTIALS);
		return ((value != null) && (value.length >= 1)) ? value[0] : null;
	}
		
	/**
	 * Returns the user name or <code>null</code> if not supplied.
	 * @return the user name
	 */			
	public String getUsername() throws BufferUnderflowException
	{
		if (getByte(POS_CREDENTIALS_TYPE) != CREDENTIALS_USER_PASSWORD) return null;		
		String[] value = getStringArray(POS_CREDENTIALS);
		return ((value != null) && (value.length >= 2)) ? value[1] : null;
	}

	/**
	 * Returns the password or <code>null</code> if not supplied.
	 * @return the password
	 */			
	public String getPassword() throws BufferUnderflowException
	{
		if (getByte(POS_CREDENTIALS_TYPE) != CREDENTIALS_USER_PASSWORD) return null;		
		String[] value = getStringArray(POS_CREDENTIALS);
		return ((value != null) && (value.length >= 3)) ? value[2] : null;
	}

	/**
	 * Returns the facroty name or <code>null</code> if not supplied.
	 * @return the factory name
	 */			
	public String getFactoryName() throws BufferUnderflowException
	{
		if (getByte(POS_CREDENTIALS_TYPE) != CREDENTIALS_USER_PASSWORD) return null;		
		String[] value = getStringArray(POS_CREDENTIALS);
		return ((value != null) && (value.length >= 3) && (!value[3].equals(""))) ? value[3] : null;
	}

	/**
	 * Returns certificate or <code>null</code> if not supplied
	 * @return the certificate as a byte array
	 */			
	public byte[] getCertificate() throws BufferUnderflowException
	{
		if (getByte(POS_CREDENTIALS_TYPE) != CREDENTIALS_CERTIFICATE) return null;
		return getByteArray(POS_CREDENTIALS);
	}	
	
	/**
	 *  Returns a string representation of the packet
	 *  @param out to writer to use to print the packet
	 */
	protected void toString(PrintWriter out) throws Exception
	{
		super.toString(out);
		//----------------------------------------------------------------
		// Print connection specific properties
		//----------------------------------------------------------------
		out.println("------------------------------ Connection Info ---------------------------------");
        out.printf("%30s %s\n%30s %s\n%30s %d\n%30s %d\n%30s %d\n", new Object[] {
        	"JMSServerInstanceName:", getJMSServerInstanceName(),
        	"Password:", getPassword(),
        	"ProtocolMajorVersion:", new Integer(getProtocolMajorVersion()),
        	"ProtocolCompatibilityVersion:", new Integer(getProtocolCompatibilityVersion())});
	}
	
	public int getExpectedResponsePacketType() {
		return PacketTypes.CONNECTION_CREATE_RESPONSE;
	}
}

