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
package com.sap.security.core.server.jaas;

import com.sap.engine.interfaces.security.auth.AssertionTicketFactory;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;

/**
 * Factory for creating assertion tickets.
 * 
 * @author Svetlana Stancheva
 * @version 7.10
 */
public class AssertionTicketFactoryImpl extends AssertionTicketFactory {

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION);
  
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
  public String createAssertionTicket(String localUser, String authscheme, String recipientSID, String recipientClient) throws Exception {
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Creating assertion ticket with parameters: localUser - [{0}], authscheme - [{1}], recipientSID - [{2}], recipientClient - [{3}]", new Object[] {localUser, authscheme, recipientSID, recipientClient});
    }
    
    SAPAuthenticationAssertionTicket ticket = SAPLogonTicketHelper.createAssertionTicket(localUser, authscheme, recipientSID, recipientClient);
    String ticketString = ticket.getTicket();
    
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Assertion ticket created: [{0}].", new Object[] {ticket.toString()});
    }
    
    return ticketString;
  }
  
  /**
   * Method to create SAP Assertion Tickets.
   *
   * @param recipientSID - system identifier of the ticket recipient system
   * @param recipientClient - client identifier of the ticket recipient system
   *
   * @return  the created SAPAuthenticationAssertionTicket.
   */
  public String createAssertionTicket(String recipientSID, String recipientClient) throws Exception {
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Creating assertion ticket with parameters: recipientSID - [{0}], recipientClient - [{1}]", new Object[] {recipientSID, recipientClient});
    }
    
    SAPAuthenticationAssertionTicket ticket = SAPLogonTicketHelper.createAssertionTicket(recipientSID, recipientClient);
    String ticketString = ticket.getTicket();
    
    if (LOCATION.beInfo()) {
      LOCATION.infoT("Assertion ticket created: [{0}].", new Object[] {ticket.toString()});
    }
    
    return ticketString;
  }

}
