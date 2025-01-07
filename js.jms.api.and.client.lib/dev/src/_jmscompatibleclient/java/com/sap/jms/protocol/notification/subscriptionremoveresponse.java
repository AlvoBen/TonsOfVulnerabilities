/**
 * SubscriptionRemoveResponse.java
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

/**
 * @author  Dr. Bernd Follmeg
 * @version 1.0
 */
public class SubscriptionRemoveResponse extends PacketImpl {

  public static final byte TYPE_ID = SUBSCRIPTION_REMOVE_RESPONSE;
  static final int POS_SUBSCRIPTION_NAME = 0;
  static final int SIZE = POS_SUBSCRIPTION_NAME;

  public SubscriptionRemoveResponse() {}
  
  /**
   * Constructor for SubscriptionRemoveResponse
   * @param  subscription_name the name of the subscription which has been removed
   * @throws JMSException thrown if something went wrong
   */
  public SubscriptionRemoveResponse(String subscription_name) throws JMSException {
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
}
