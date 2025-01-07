/**
 * Copyright (c) 2007 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on 2007-1-31 by I024187
 *   
 */
package com.sap.engine.services.security.login;

import java.util.Arrays;
import java.util.StringTokenizer;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.lib.lang.Convert;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class TicketSignature {

  private final static Location TRACER = SecurityContext.TRACER;
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  private final static String SEPARATOR = "*";

  private String sessionAlias = null;
  private String impersonatedUser = null;

  /**
   *  Constructs a ticket signature representation instance by parsing the given byte array of the transfered security context object.
   *
   * @param ticket  the given P4 ticket.
   * @param verifyImpersonation  if true parsing will check for user impersonation and its proof. If false, impersonated user will be null. 
   */
  TicketSignature(byte[] ticket, boolean verifyImpersonation) {
    if ((ticket != null) && (ticket.length > TicketGenerator.EMPTY_TICKET.length) && !TicketGenerator.isTicketAnonymous(ticket)) {
      int sessionSize = Convert.byteArrToInt(ticket, TicketGenerator.OFFSET_SESSION_TICKET_SIZE);
      int signatureSize = ticket.length - sessionSize - TicketGenerator.OFFSET_SESSION_TICKET;
      String signature = new String(ticket, TicketGenerator.OFFSET_SESSION_TICKET + sessionSize, signatureSize);
      StringTokenizer tokens = new StringTokenizer(signature, SEPARATOR);

      sessionAlias = tokens.nextToken();

      if (verifyImpersonation && tokens.hasMoreTokens()) {
        impersonatedUser = tokens.nextToken();

        if(IN_SERVER && TRACER.beDebug()) {
          TRACER.debugT("The user in the ticket is '{0}'", new Object[] {impersonatedUser});
        }
        
        if (tokens.hasMoreTokens()) {
          String impersonationProof = tokens.nextToken();

          if (!Signer.verify(TicketGenerator.getClusterId(ticket), (sessionAlias + SEPARATOR + impersonatedUser), impersonationProof)) {
            if(IN_SERVER && TRACER.beDebug()) {
              TRACER.debugT("User '{0}' not accepted - verification failed", new Object[] {impersonatedUser});
            }
            
            impersonatedUser = null;
            impersonationProof = null;
          } else if(IN_SERVER && TRACER.beDebug()) {
            TRACER.debugT("User '{0}' accepted", new Object[] {impersonatedUser});
          }
        } else {
          // either there is an impersonated user and proof for it, or there is no impersonated user at all
          if(IN_SERVER && TRACER.beDebug()) {
            TRACER.debugT("User '{0}' not accepted - no impersonation proof in the ticket", new Object[] {impersonatedUser});
          }
          
          impersonatedUser = null;
        }
      }
    }
  }

  /**
   * @return the portion of the signature that is the alias (or user id) known by Common Session Management.
   */
  protected String getSessionAlias() {
    return sessionAlias;
  }

  /**
   * @return the portion of the signature that is the impersonated user (if any). It is only given if <code>verifyImpersonation</code> was true,
   *         a name for the impersonated user is provided and the proof of impersonation within the P4 ticket is verified successfully.
   */
  protected String getImpersonatedUser() {
    return impersonatedUser;
  }

  /**
   * Creates a signature for the session that can be parsed on another server node
   * 
   * @param session the current security session
   * @return the signature that can be parsed on another server node.
   */
  protected byte[] createSignature(SecuritySession session) {
    sessionAlias = SecurityContext.getLoginAccessor().getClientId();
    
    if (sessionAlias == null) {
      sessionAlias = Signer.generateAlias();

      try {
        SecurityContext.getLoginAccessor().setAliasToClientContext(sessionAlias);
        SecurityContext.getLoginAccessor().setClientIdToClientContext(sessionAlias);
      } catch (Exception e) {
        if (IN_SERVER && TRACER.beError()) {
          SimpleLogger.traceThrowable(Severity.ERROR, TRACER, "ASJ.secsrv.000203", "Unable to set alias to user context!", e);
        }
        
        throw new SecurityException("Cannot register security session: " + session, e);
      }
    } else if (SecurityContext.getLoginAccessor().getAlias() == null) {
      SecurityContext.getLoginAccessor().setAliasToClientContext(sessionAlias);
    }
    
    if (SecurityContext.isInSubjectDoAs()) {
      impersonatedUser = session.getUserName();
      String aliasAndUser = sessionAlias + SEPARATOR + impersonatedUser;
      String impersonationProof = Signer.sign(aliasAndUser);

      if (IN_SERVER && TRACER.beDebug()) {
        TRACER.debugT("Subject.doAs() user '{0}' stored in the ticket", new Object[] {impersonatedUser});
      }
      
      return (aliasAndUser + SEPARATOR + impersonationProof).getBytes();
    }
    
    return sessionAlias.getBytes();
  }

}
