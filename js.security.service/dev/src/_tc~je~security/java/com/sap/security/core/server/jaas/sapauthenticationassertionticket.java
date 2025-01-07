package com.sap.security.core.server.jaas;

import com.sap.security.core.ticket.imp.Ticket;
import com.sap.security.api.ticket.TicketException;
import com.sap.security.api.ticket.InfoUnit;

/**
 *  SAPAuthenticationAssertionTicket designed for usage in ABAP -> J2EE calls
 * via HTTP and RFC. It is issued for one time usage.
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public class SAPAuthenticationAssertionTicket extends Ticket {

  /**
   *  Gets the ticket flag that indicates whether the ticket is cached.
   * This method can be used only when the ticket is in verify mode.
   *
   * @return true if the ticket is not cached, otherwise returns false.
   * @throws TicketException - if some exception occurs on extracting the flag from the ticket.
   */
    public boolean getDoNotCacheTicket() throws TicketException {
      InfoUnit flags = getInfoUnit(InfoUnit.ID_FLAGS);

      if (flags != null) {
        byte[] bytes = flags.getContent();

        if (bytes != null) {
          return ((bytes[0] & 0x01) == 0x01);
        }
      }

      return false;
    }

  /**
   *  Sets the flag that indicates whether the ticket should be cached.
   * This method can be used only when the ticket is in create mode.
   *
   * @param doNotCacheTicket - the flag indicating whether the ticket should be cached.
   * @throws TicketException - if some exception occurs on adding the flag to the ticket.
   */
    public void setDoNotCacheTicket(boolean doNotCacheTicket) throws TicketException {
      byte[] flag;

      if (doNotCacheTicket) {
        flag = new byte[] {0x01};
      } else {
        flag = new byte[] {0x00};
      }

      addInfoUnit(new InfoUnit(InfoUnit.ID_FLAGS, flag));
    }

    /**
     *  Write extended information about this Ticket to a String.
     *
     *  @return A string containing detailed information about the ticket
     *  (makes only sense after successful verification).
     */
    public String toString() {
      return "  SAP Assertion Authentication Ticket  \n" + super.toString();
    }

}
