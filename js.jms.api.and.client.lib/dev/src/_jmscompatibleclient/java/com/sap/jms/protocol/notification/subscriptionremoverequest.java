/**
 * SubscriptionRemoveRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import com.sap.jms.util.compat.PrintWriter;

import javax.jms.JMSException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConnectionID;
import com.sap.jms.protocol.BufferOverflowException;

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class SubscriptionRemoveRequest extends PacketImpl implements PacketWithConnectionID {

  public static final byte TYPE_ID = SUBSCRIPTION_REMOVE_REQUEST;

  static final int POS_CONNECTION_ID = 0;
  static final int POS_SUBSCRIPTION_NAME = POS_CONNECTION_ID + SIZEOF_LONG;
  static final int SIZE = POS_SUBSCRIPTION_NAME;

  public SubscriptionRemoveRequest() {}
  
  /**
   * Constructor for SubscriptionRemoveRequest.
   * @param  subscription_name the name of the subscription which should be removed
   * @throws JMSException thrown if something went wrong
   */
  public SubscriptionRemoveRequest(String subscription_name) throws JMSException {
    super(TYPE_ID, SIZE + strlenUTF8(subscription_name));
    setUTF8(POS_SUBSCRIPTION_NAME, subscription_name);
  }

  /**
   *  Returns the name of the subscription which should be removed
   *  @return the subscription's name
   */
  public String getSubscriptionName() throws BufferUnderflowException {
    return getString(POS_SUBSCRIPTION_NAME);
  }

  /**
   * Sets the connection ID
   * @param connection_id
   * @throws BufferOverflowException
   */
  public void setConnectionID(long connection_id) throws BufferOverflowException {
    setLong(POS_CONNECTION_ID, connection_id);
  }

  /**
   * Retunrs the connection ID
   * @return
   * @throws BufferUnderflowException
   */
  public long getConnectionID() throws BufferUnderflowException {
    return getLong(POS_CONNECTION_ID);
  }

 /**
   *  Returns a string representation of the packet
   *  @param out to writer to use to print the packet
   */
  protected void toString(PrintWriter out) throws Exception 
  {
		super.toString(out);
		//----------------------------------------------------------------
		// Print subscription content
		//----------------------------------------------------------------
		out.println("------------------------------ Subscription Content ----------------------------");
		out.printf("%30s %s\n", "SubscriptionName:", getSubscriptionName());
  }

	public int getExpectedResponsePacketType() {
		return PacketTypes.SUBSCRIPTION_REMOVE_RESPONSE;
	}
}
