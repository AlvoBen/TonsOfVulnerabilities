/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.auth;

/**
 * Factory for creating assertion tickets.
 * 
 * @author Svetlana Stancheva
 * @version 7.10
 */
public abstract class AssertionTicketFactory {
  
  private static AssertionTicketFactory factory = null;
  
  /**
   * Get an instance of AssertionTicketFactory.
   * 
   * @return  an instance of AssertionTicketFactory.
   */
  public static AssertionTicketFactory getFactory() {
    return factory;
  }
  
  /**
   * Used internaly by the security service for initialization.
   * 
   * @param assertionTicketFactory - an instance of AssertionTicketFactory
   */
  public static void setFactory(AssertionTicketFactory assertionTicketFactory) {
    if (factory == null) {
      factory = assertionTicketFactory;
    }
  }
  
  /**
   * Method to create SAP Assertion Tickets.
   *
   * @param localUser - user of SAP Assertion Ticket
   * @param authscheme - the auth scheme for which the ticket is created
   * @param recipientSID - system identifier of the ticket recipient system
   * @param recipientClient - client identifier of the ticket recipient system
   *
   * @return  the created SAPAuthenticationAssertionTicket.
   * 
   * @deprecated  use method {@link com.sap.engine.interfaces.security.auth.AssertionTicketFactory#createAssertionTicket(String, String)} instead.
   */
  public abstract String createAssertionTicket(String localUser, String authscheme, String recipientSID, String recipientClient) throws Exception;
  
  /**
   * Method to create SAP Assertion Tickets.
   *
   * @param recipientSID - system identifier of the ticket recipient system
   * @param recipientClient - client identifier of the ticket recipient system
   *
   * @return  the created SAPAuthenticationAssertionTicket.
   */
  public abstract String createAssertionTicket(String recipientSID, String recipientClient) throws Exception;

}
