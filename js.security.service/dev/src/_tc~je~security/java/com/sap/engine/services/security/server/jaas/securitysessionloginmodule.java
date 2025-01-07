/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.server.jaas;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.services.security.login.TicketGenerator;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 *  Login Module for login by ticket.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class SecuritySessionLoginModule extends AbstractLoginModule {
  private static final String TICKET = "ticket";
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_P4_TICKET_LOCATION + ".SecuritySessionLoginModule");

  private CallbackHandler callbackHandler = null;
  private UserContext userContext = null;
  private SecurityContext securityContext = null;
  private UserInfo userInfo = null;
  private Subject subject = null;
  private String ticket = null;
  private String name = null;
  private Map sharedState = null;
  private boolean successful;
  private boolean shouldBeIgnored;
  private boolean nameSet = false;

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    
    this.callbackHandler = callbackHandler;
    this.subject = subject;
    this.sharedState = sharedState;
    this.securityContext = SecurityContextImpl.getRoot();
    this.userContext = securityContext.getUserStoreContext().getActiveUserStore().getUserContext();
    successful = false;
    shouldBeIgnored = false;
  }

  public boolean login() throws LoginException {
    HttpGetterCallback callback = new HttpGetterCallback();

    callback.setType(HttpCallback.REQUEST_PARAMETER);
    callback.setName(TICKET);

    try {
      callbackHandler.handle(new Callback[] {callback});
    } catch (UnsupportedCallbackException e) {
      shouldBeIgnored = true;
      return false;
    } catch (IOException e) {
      throwUserLoginException(e, LoginExceptionDetails.IO_EXCEPTION);
    }

    String[] parameters = (String[]) callback.getValue();
    if ((parameters != null) && (parameters.length > 0)) {
      ticket = parameters[0];
      byte[] byteTicket = convert(ticket);

      if (TicketGenerator.isTicketAnonymous(byteTicket)) {
        throwNewLoginException("The anonymous ticket is not accepted as valid credential.", LoginExceptionDetails.P4_TICKET_NOT_VALID);
      }

      name = TicketGenerator.getPrincipalName(byteTicket);

      if (name == null) {
        throwNewLoginException("The user name received from SecuritySessionLoginModule is null.", LoginExceptionDetails.P4_TICKET_NOT_VALID);
      }

      try {
        refreshUserInfo(name);
        userInfo = userContext.getUserInfo(name);
      } catch (Exception e) {
        throwUserLoginException(e, LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
      }

      if (userInfo == null) {
        throwNewLoginException("No such user " + name + " found in the userstore.", LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION);
      }

      if (!TicketGenerator.isTicketTrusted(byteTicket)) {
        throwNewLoginException("Ticket '" + ticket + "' is not trusted.", LoginExceptionDetails.P4_TICKET_NOT_VALID);
      }

      if (sharedState.get(AbstractLoginModule.NAME) == null) {
        sharedState.put(AbstractLoginModule.NAME, name);
        nameSet = true;
      }
      successful = true;
      return true;
    } else {
      throwNewLoginException("No ticket is provided.", LoginExceptionDetails.P4_TICKET_NOT_VALID);
    }

    return false;
  }

  public boolean commit() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        Principal principal = new Principal(name);

        subject.getPrincipals().add(principal);
        subject.getPrivateCredentials().add(ticket);

        if (nameSet) {
          sharedState.put(AbstractLoginModule.PRINCIPAL, principal);
        }
      } else {
        ticket = null;
        name = null;
        userInfo = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean abort() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        ticket = null;
        name = null;
        successful = false;
        userInfo = null;
      }

      return true;
    } else {
      shouldBeIgnored = false;
      return false;
    }
  }

  public boolean logout() throws LoginException {
    if (!shouldBeIgnored) {
      if (successful) {
        userContext.emptySubject(subject);
        removeCredentials();
        successful = false;
      }

      return true;
    } else {
      return false;
    }
  }

  private void removeCredentials() {
    try {
		  Iterator privateCredentials = subject.getPrivateCredentials(String.class).iterator();

		  while (privateCredentials.hasNext())  {
		    privateCredentials.next();
		    privateCredentials.remove();
		  }
    } catch (Exception e) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot remove security credentials of class java.lang.String from subject on logout.", e);
      }
    }
  }

  private final byte[] convert(String ticket) {
    final String HEX = "0123456789ABCDEF";

    int start = 0;
    int length = ticket.length();
    byte[] result = new byte[(length / 2) + (length % 2)];

    ticket = ticket.toUpperCase();
    if (length % 2 == 1) {
      result[0] = (byte) HEX.indexOf(ticket.charAt(0));
      start = 1;
    }

    for (int i = start; i < result.length; i++) {
      result[i] = (byte) ((HEX.indexOf(ticket.charAt(i*2 - start)) << 4) | HEX.indexOf(ticket.charAt(i*2 + 1 - start)));
    }

    return result;
  }

}
