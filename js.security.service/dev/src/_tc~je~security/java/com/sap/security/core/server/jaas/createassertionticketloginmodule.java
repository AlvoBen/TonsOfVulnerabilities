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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.tc.logging.*;

/**
 * <p>JAAS Login Module for the Creation of SAP Assertion Tickets.
 * </p>
 * <p>This login module does not authenticates the user.
 * Instead it uses the sharedState to find out if a user
 * has been successfully authenticated by another
 * {@link javax.security.auth.spi.LoginModule}.
 * If <code>AbstractLoginModule.NAME</code> is
 * set, a SAP Assertion Ticket for this user will be
 * created. Otherwise the {@link CreateAssertionTicketLoginModule}
 * will do nothing. </p>
 * <p>After succeesful authentication the ticket is stored in the
 * subject. </p>
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public class CreateAssertionTicketLoginModule extends AbstractLoginModule {

  /** subject representing the user */
  Subject m_subject;

  /** shared state with the other login modules */
  Map m_sharedState;

  /** callback handler for retrieving logon data */
  CallbackHandler m_handler;

  /** modified options for this login module */
  Properties m_options= new Properties();

  /** ticket credential for subject */
  SAPLogonTicketCredential m_credential= null;

  /** key for the user cache entry */
  Object m_cacheKey= null;

  /** internal state of login */
  boolean m_succeeded= false;

  /** Exception dummy */
  Exception m_exception= null;

  /** for extended UME ticket features */
  private UMEAdapter m_umeadapter = null;
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION + ".CreateAssertionTicketLoginModule");

  /** default severity */
  static final int SEVERITY;

  static {
    SEVERITY = Severity.INFO;
  }

  /**
   * <p>Initialize the <code>CreateAssertionTicketLoginModule</code>.
   * This method is called by the {@link javax.security.auth.login.LoginContext}.
   * The purpose of this method is to initialize the
   * <code>LoginModule</code> with all relevant information. </p>
   *
   * @param subject the subject to be authenticated.
   * @param handler callback handler for communication
   *			with the user
   * @param sharedState shared state used to pass information
   * from one login module to another.
   * @param options options that are specified in the login
   * {@link javax.security.auth.login.Configuration}
   */
  public void initialize(
    Subject subject,
    CallbackHandler handler,
    Map sharedState,
    Map options) {
    final String METHOD= "initialize()";
    try {
      // logging info
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[] { subject, handler, sharedState, options });
      }

      super.initialize(subject, handler, sharedState, options);

      // store informations passed by the LoginContext
      m_subject= subject;
      m_handler= handler;
      m_sharedState= sharedState;
      m_options.putAll(options);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateAssertionTicketLoginModule in [{0}] authentication stack are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

	    m_umeadapter = new UMEAdapter(sharedState, m_options, true);
      m_options = m_umeadapter.getMergedOptions();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateAssertionTicketLoginModule in [{0}] authentication stack after merge with UME properties are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }

      // set default values for properties that
      // have not been specified in the login module options
      Object option;

      option = m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME);
      if (option != null) {
        m_options.put(SAPLogonTicketHelper.AUTH_NAME, option);
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The options of CreateAssertionTicketLoginModule in [{0}] authentication stack after adding the default values are: [{1}].", new Object[] {sharedState.get(SAPLogonTicketHelper.AUTH_NAME), m_options});
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD);
    }
  } //end initialize


  /** Method to login the user. <br>
   * This login module does not authenticates the user by itself.
   * The login method returns true if a user has successfully
   * been authenticated by
   * another login module so that this login module will be able
   * to create a SAP Assertion Ticket for this user in the commit method. <br>
   * The indication that a user has successfully been authenticated by
   * anothers login modules login method is that the user name has been written in
   * the shared state, key <code>AbstractLoginModule.NAME</code>.
   * @see javax.security.auth.spi.LoginModule#login()*/
  public boolean login() throws javax.security.auth.login.LoginException {
    final String METHOD = "login()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // check wether authenticated user is available
      Object option = m_sharedState.get(AbstractLoginModule.NAME);

      if (option != null && !option.equals("")) {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("Authenticated user found: user={0}. Authentication stack: [{1}].", new Object[] {option, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
        }

        m_options.put("user", option);

        refreshUserInfo((String) option);

        m_succeeded = true;
        b = new Boolean(true);
        return true;
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT("No authenticated user found.");
        }

        b = new Boolean(false);
        return false;
      }

    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end login()


  /** Method to commit the logged in user. <br>
   * This login module does not authenticates the user.
   * It simply creates a SAP Logon Ticket for a user that has successfully
   * been authenticated by another login module.
   * The indication that also the commit method of the other
   * login module succeeded is that the username in
   * the shared state, key <code>AbstractLoginModule.NAME</code>
   * is still the same as in the login method.
   * If the SAP Logon Ticket has been created successfully it will
   * be passed to the http response. The user will be cached for faster
   * retrieval, key SAP Logon Ticket.
   * @see javax.security.auth.spi.LoginModule#commit()
   */
  public boolean commit() throws javax.security.auth.login.LoginException {
    final String METHOD= "commit()";

    String user;
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // if login failed, only internal data needs to be reset
      if (m_succeeded == false) {
        m_subject = null;
        m_handler = null;
        m_sharedState = null;
        m_options = null;
        b = new Boolean(false);
        return false;
      }

      SAPAuthenticationAssertionTicket ticket;
      String ticketString= null;

      // check wether the username is still in shared state
      // otherwise the commit method of the login module doing the
      // authentication failed and no SAP Assertion Ticket shall be created
	    Object option = m_sharedState.get(AbstractLoginModule.NAME);

      if ((option != null) && option.equals(m_options.get("user"))) {
        user = (String) option;

        if (LOCATION.beDebug()) {
          LOCATION.debugT("Authenticated user still in shared state.");
        }
      } else {
        m_exception = new DetailedLoginException("Commit method of login module doing the actual authentication seems to have failed. Authentication stack: " + m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw m_exception;
      }

      // get system id
	    option = m_options.get("system");
      if (option != null) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("System ID found: [{0}].", new Object[] {option});
        }
      } else {
        m_exception = new DetailedLoginException("Can not create SAP Logon Ticket. SID not available.", LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
        throw m_exception;
      }

      // Sets the R/3 user into
      // the m_options
      m_umeadapter.setMappedUser();

      // create SAP Logon Ticket
      ticket = SAPLogonTicketHelper.createAssertionTicket(m_options, m_sharedState);
      ticketString = ticket.getTicket();

      if (LOCATION.beInfo()) {
        LOCATION.infoT("New SAP Logon Ticket for user [{0}] has been created. Authentication stack: [{1}].", new Object[] {user, m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }
      if (LOCATION.beDebug()) {
        LOCATION.debugT("The created ticket is: \n [{0}]. \nAuthentication stack: [{1}].", new Object[] {ticket.toString(), m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)});
      }

      // add ticket credential to subject
      m_credential = new SAPAuthenticationAssertionTicketCredential(ticketString, user);
      m_subject.getPrivateCredentials().add(m_credential);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Assertion Ticket added to private credentials.");
      }

      b = new Boolean(true);
      return true;
    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_CREATE_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end commit()


  /** Method to abort the login of the user.
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws javax.security.auth.login.LoginException {
    final String METHOD = "abort()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // remove all things changed
      rollBack();

      b = new Boolean(true);
      return true;
    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end abort()


  /** Method to logout the user.
   * @see javax.security.auth.spi.LoginModule#logout()*/
  public boolean logout() throws javax.security.auth.login.LoginException {
    final String METHOD = "logout()";
    Boolean b = null;

    try {
      // logging info
      LOCATION.entering(METHOD);

      // remove all things changed
      rollBack();

      b = new Boolean(true);
      return true;
    } catch (Throwable e) {
      if (e instanceof Error) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (Error) e;
      } else if (e instanceof RuntimeException) {
        LOCATION.traceThrowableT(Severity.ERROR, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        throw (RuntimeException) e;
      } else if (e instanceof LoginException) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw (LoginException) e;
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Authentication stack: [{0}].", new Object[] {m_sharedState.get(SAPLogonTicketHelper.AUTH_NAME)}, e);
        }
        throw new DetailedLoginException(e.toString(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
      }
    } finally {
      // logging info
      LOCATION.exiting(METHOD, b);
    }
  } //end logout


  /**
   * internal method to reset all data for this login module
   */
  void rollBack() throws LoginException, UnsupportedCallbackException, IOException {
    // delete ticket credential if they have been set
    if ((m_subject != null) && (m_credential != null)){
      m_subject.getPrivateCredentials().remove(m_credential);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("SAP Assertion Ticket has been removed from private credentials.");
      }
    }

    // set all member variables to null resp. false
    m_subject= null;
    m_handler= null;
    m_sharedState= null;
    m_options= null;
    m_credential= null;
    m_succeeded= false;
    m_umeadapter = null;
  }

}
