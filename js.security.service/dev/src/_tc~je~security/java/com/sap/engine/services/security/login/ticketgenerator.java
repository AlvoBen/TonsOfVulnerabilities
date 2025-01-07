package com.sap.engine.services.security.login;

import java.security.Principal;
import java.util.Arrays;

import javax.security.auth.Subject;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.lib.lang.Convert;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class TicketGenerator {
  
  private final static Location TRACER = SecurityContext.TRACER;
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  
  protected final static int OFFSET_SIZE = 0;
  protected final static int OFFSET_CLUSTER_ID = 4;
  protected final static int OFFSET_SESSION_TICKET_SIZE = 8;
  protected final static int OFFSET_SESSION_TICKET = 12;

  protected final static int OFFSET_NUMBER = 0;
  protected final static int OFFSET_CREATION = 8;
  protected final static int OFFSET_EXPIRATION = 16;
  protected final static int OFFSET_TICKET_VALID_BEFORE = 24;
  protected final static int OFFSET_PRINCIPAL = 32;

  protected final static byte[] EMPTY_TICKET = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
  protected static byte[] ANONYMOUS_TICKET = null;

  private final static int ANONYMOUS_OFFSET_SIZE = 0;
  private final static int ANONYMOUS_OFFSET_EMPTY_TICKET = 4;
  private final static int ANONYMOUS_OFFSET_PRINCIPAL = 8;

  /**
   *  Format of the ticket is:
   *
   *  issuer_cluster_element: long not yet
   *  signature: long not yet
   *  creation_time: long
   *  expiration_time: long
   *  principal_name: Unicode String
   */
  public static byte[] generateTicket(byte[] ticket, SecuritySession session) {
    if (!IN_SERVER) {
      if (ticket == null) {
        return getEmptyTicket();
      } else {
        return ticket;
      }
    }

    Principal principal = null;

    if (session.isAnonymous()) {
      byte[] anonTicket = getAnonymousTicket();
      return anonTicket;
    }

    if (session != null) {
      principal = session.getPrincipal();
    }

    TicketSignature ticketSignature = new TicketSignature(ticket, false);
    
    if (regenerateTicket(ticket, principal, ticketSignature.getSessionAlias())) {
      String name = principal.getName();
      int ticketSize = OFFSET_PRINCIPAL + name.length() * 2;
      byte[] unsignedTicket = new byte[ticketSize];

      Convert.writeLongToByteArr(unsignedTicket, OFFSET_NUMBER, session.getSessionNumber());
      Convert.writeLongToByteArr(unsignedTicket, OFFSET_CREATION, session.getCreationTime());
      Convert.writeLongToByteArr(unsignedTicket, OFFSET_EXPIRATION, session.getExpirationPeriod());
      Convert.writeLongToByteArr(unsignedTicket, OFFSET_TICKET_VALID_BEFORE, System.currentTimeMillis() + session.getExpirationPeriod());
      Convert.writeUStringToByteArr(unsignedTicket, OFFSET_PRINCIPAL, name);

      byte[] signature = ticketSignature.createSignature(session);
      byte[] signedTicket = new byte[OFFSET_SESSION_TICKET + ticketSize + signature.length];

      Convert.writeIntToByteArr(signedTicket, OFFSET_SIZE, signedTicket.length);
      Convert.writeIntToByteArr(signedTicket, OFFSET_CLUSTER_ID, com.sap.engine.services.security.SecurityServerFrame.currentParticipant);
      Convert.writeIntToByteArr(signedTicket, OFFSET_SESSION_TICKET_SIZE, unsignedTicket.length);
      System.arraycopy(unsignedTicket, 0, signedTicket, OFFSET_SESSION_TICKET, unsignedTicket.length);
      System.arraycopy(signature, 0, signedTicket, OFFSET_SESSION_TICKET + ticketSize, signature.length);
      
      if (IN_SERVER && TRACER.beInfo()) {
        TRACER.infoT("Security session ticket {0} generated", new Object[] {Arrays.toString(signedTicket)});
      }
      
      return signedTicket;
    } else if (principal == null || principal.getName() == null) {
      byte[] emptyTicket = getEmptyTicket();
      return emptyTicket;
    } else {
      return ticket;
    }
  }
  
  private static boolean regenerateTicket(byte[] ticket, Principal principal, String clientIdInTicket) {
    if (principal == null) {
      return false;
    }
    
    if (principal.getName() == null) {
      return false;
    }
    
    if (!principal.getName().equals(getPrincipalName(ticket))) {
      return true;
    }
    
    String clientId = SecurityContext.getLoginAccessor().getClientId();
    
    if (clientId == null) {
      return true;
    }
    
    return !clientId.equals(clientIdInTicket);
  }

  /**
   *  Returns a security session instance if there is such registered with session management for the given ticket.
   * Otherwise returns <code>null</code>.
   * 
   * @param ticket
   * @param pool
   * @return
   */
  static SecuritySession getSecuritySession(byte[] ticket) {
    return getSession(ticket);
  }

  public static boolean isTicketAnonymous(byte[] ticket) {
    for (int i = 0; i < EMPTY_TICKET.length; i++) {
      if (ticket[ANONYMOUS_OFFSET_EMPTY_TICKET + i] != EMPTY_TICKET[i]) {
        return false;
      }
    }
    
    return true;
  }

  private static String getAnonymousPrincipalFromTicket(byte[] ticket) {
    int nameSize = (ticket.length - ANONYMOUS_OFFSET_PRINCIPAL)/2;
    String anonPrincipal = Convert.byteArrToUString(ticket, ANONYMOUS_OFFSET_PRINCIPAL,  nameSize);
    return anonPrincipal;
  }

  public final static byte[] getEmptyTicket() {
    return EMPTY_TICKET;
  }

  public static byte[] getAnonymousTicket() {
    return ANONYMOUS_TICKET;
  }

  protected static void setAnonymousPrincipal(String anonymousPrincipal) {
    int size = ANONYMOUS_OFFSET_PRINCIPAL + 2 * (anonymousPrincipal == null ? 0 : anonymousPrincipal.length());
    byte[] newanon = new byte[size];
    Convert.writeIntToByteArr(newanon, ANONYMOUS_OFFSET_SIZE, size);
    System.arraycopy(EMPTY_TICKET, 0, newanon, ANONYMOUS_OFFSET_EMPTY_TICKET, EMPTY_TICKET.length);
    Convert.writeUStringToByteArr(newanon, ANONYMOUS_OFFSET_PRINCIPAL, anonymousPrincipal);
    ANONYMOUS_TICKET = newanon;
  }

  public static String getPrincipalName(byte[] ticket) {
    String name = null;
    
    if (ticket != null) {
      if (!isTicketAnonymous(ticket)) {
        int session_size = Convert.byteArrToInt(ticket, OFFSET_SESSION_TICKET_SIZE);
        name = Convert.byteArrToUString(ticket, OFFSET_SESSION_TICKET + OFFSET_PRINCIPAL, (session_size - OFFSET_PRINCIPAL ) / 2);
      } else {
        name = getAnonymousPrincipalFromTicket(ticket);
      }
    }
    
    return name;
  }

  public static int getClusterId(byte[] ticket) {
    return Convert.byteArrToInt(ticket, OFFSET_CLUSTER_ID);
  }

  public static long getCreationTime(byte[] ticket) {
    long time = -1;
    
    if (ticket != null) {
      time = Convert.byteArrToLong(ticket, OFFSET_SESSION_TICKET + OFFSET_CREATION);
    }
    
    return time;
  }

  public static long getSessionNumber(byte[] ticket) {
    long sessionNum = -1;
    
    if (ticket != null) {
      sessionNum = Convert.byteArrToLong(ticket, OFFSET_SESSION_TICKET + OFFSET_NUMBER);
    }
    
    return sessionNum;
  }

  public static byte[] getTicket(byte[] from, int offset) {
    int size = Convert.byteArrToInt(from, offset + OFFSET_SIZE);
    byte[] ticket = null;

    if (size > EMPTY_TICKET.length) {
      ticket = new byte[size];
      System.arraycopy(from, offset, ticket, 0, ticket.length);
    } else {
      ticket = EMPTY_TICKET;
    }
    
    return ticket;
  }

  public static boolean isTicketTrusted(byte[] ticket) {
    SecuritySession session = getSession(ticket);
    return (session != null) && !session.isAnonymous();
  }

  // returns a valid non-null security session if the session management recognizes the alias
  // carried with the ticket, otherwise returns an anonymous security session.
  private final static SecuritySession getSession(byte[] ticket) {
    if ((ticket == null) || (ticket.length <= getEmptyTicket().length)) {
      return null;
    }
    
    if (!IN_SERVER) {
      Subject subject = getSubject(getPrincipalName(ticket));
      
      if (subject != null) {
        return new SecuritySession(SecurityContext.getLoginAccessor(), subject);
      }
      
      return new SecuritySession(SecurityContext.getLoginAccessor());
    }
    
    TicketSignature ticketSignature = new TicketSignature(ticket, true);
    String clientId = ticketSignature.getSessionAlias();

    if (clientId != null) {
      SecurityContext.getLoginAccessor().applyClientContextToCurrentThreadById(clientId);
      
      String impersonatedUser = ticketSignature.getImpersonatedUser();
      
      if (impersonatedUser != null) {
        Subject impersonatedSubject = getSubject(impersonatedUser);
        return new SecuritySession(SecurityContext.getLoginAccessor(), impersonatedSubject);
      }
      
      return new SecuritySession(SecurityContext.getLoginAccessor());
    }

    if (TRACER.beDebug()) {
      if (clientId != null) {
        TRACER.debugT("Client ID '{0}' is not known by CSM. Will use anonymous security session.", new Object[] {clientId});
      } else {
        TRACER.debugT("No client ID received. Will use anonymous security session.");
      }
    }
    
    return null;
  }

  private final static Subject getSubject(String userName) {
    if (userName == null) {
      return null;
    }
    
    Subject subject = new Subject();
    subject.getPrincipals().add(new com.sap.engine.lib.security.Principal(userName));
    return subject;
  }
  
}
