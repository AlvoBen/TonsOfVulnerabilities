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
package com.sap.engine.services.security.login;

import com.sap.engine.frame.client.ClientFactory;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.services.security.exceptions.BaseLoginException;

import java.util.*;
import java.security.Principal;

import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import javax.naming.*;
import javax.resource.spi.security.PasswordCredential;

/**
 * @deprecated Must not be used any longer.
 */
public class RemoteLoginModule implements LoginModule {
  private String namingFactory = null;

  private Subject subject;
  private CallbackHandler callbackHandler;

  private String username;
  private char[] password;

  private boolean succeeded = false;
  private boolean commitSucceeded = false;

  private Principal principal = null;
  private PasswordCredential passwordCredential = null;

  private boolean withAppClient = false;
  private Context context = null;

  public RemoteLoginModule() {
  }

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    this.subject = subject;
    this.callbackHandler = callbackHandler;

    // initialize the naming factory to use ...
    namingFactory = (String) options.get(InitialContext.INITIAL_CONTEXT_FACTORY);
    if (namingFactory == null) {
      namingFactory = (String) sharedState.get(InitialContext.INITIAL_CONTEXT_FACTORY);
    }
    if (namingFactory == null) {
      namingFactory = System.getProperty(InitialContext.INITIAL_CONTEXT_FACTORY);
    }
    if (namingFactory == null) {
      namingFactory = "com.sap.engine.services.jndi.InitialContextFactoryImpl";
    }

    String appClientValue = (String) options.get("appclient");
    if (appClientValue != null) {
      if (appClientValue.equals("true")) {
        withAppClient = true;
      } else {
        withAppClient = false;
      }
    }
  }

  public boolean login() throws LoginException {
    Callback[] callbacks;

    // prompt for a username and password
    if (callbackHandler == null) {
      throw new BaseLoginException("No CallbackHandler available to garner authentication information from the user.");
    }
    try {
      callbacks = new Callback[2];
      callbacks[0] = new NameCallback("username: ");
      callbacks[1] = new PasswordCallback("password: ", false);
      callbackHandler.handle(callbacks);
      username = ((NameCallback) callbacks[0]).getName();

      char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();

      if (tmpPassword == null) {
        // treat a NULL password as an empty password
        tmpPassword = new char[0];
      }

      password = new char[tmpPassword.length];
      System.arraycopy(tmpPassword, 0, password, 0, tmpPassword.length);
      ((PasswordCallback) callbacks[1]).clearPassword();
    } catch (UnsupportedCallbackException uce) {
      //$JL-EXC$
      throw new BaseLoginException("Not available to garner authentication information from the user.");
    } catch (Exception ioe) {
      throw new BaseLoginException("Remote login error.", ioe);
    }

    try {
      internalLogin(username, password);
      succeeded = true;
    } catch (Exception e) {
      throw new BaseLoginException("Unable to authenticate user.", e);
    }

    return succeeded;
  }

  public boolean commit() throws LoginException {
    if (succeeded == true) {
      principal = new com.sap.engine.lib.security.Principal(username);

      if (!subject.getPrincipals().contains(principal)) {
        subject.getPrincipals().add(principal);
      }

      passwordCredential = new PasswordCredential(username, password);
      subject.getPrivateCredentials().add(passwordCredential);

      commitSucceeded = true;

      try {
        // changes the security session on the client.
        SecurityContextObject current = ((SecurityContextObject) ClientFactory.getThreadContextFactory().getThreadContext().getContextObject("security"));
        SecuritySession session = (SecuritySession) current.getSession();
        session.setSubjectHolder(new SubjectWrapper(subject, principal));
        session.setAuthenticationConfiguration("");
      } catch (Exception e) {
        //$JL-EXC$
        e.printStackTrace();
      }

    }

    // in any case, clean out state
    username = null;
    for (int i = 0; i < password.length; i++) {
      password[i] = ' ';
    }
    password = null;

    return commitSucceeded;
  }

  public boolean abort() throws LoginException {
    if (succeeded == false) {
      return false;
    } else if (succeeded == true && commitSucceeded == false) {
      // login succeeded but overall authentication failed
      succeeded = false;
      username = null;
      if (password != null) {
        for (int i = 0; i < password.length; i++)
          password[i] = ' ';
        password = null;
      }
      principal = null;
      passwordCredential = null;
    } else {
      // overall authentication succeeded and commit succeeded,
      // but someone else's commit failed
      logout();
    }
    return true;
  }

  public boolean logout() throws LoginException {
    subject.getPrincipals().remove(principal);
    subject.getPrivateCredentials().remove(passwordCredential);
    try {
      internalLogout();
      succeeded = false;
      commitSucceeded = false;
      username = null;
      if (password != null) {
        for (int i = 0; i < password.length; i++) {
          password[i] = ' ';
        }
        password = null;
      }
      principal = null;
      passwordCredential = null;
    } catch (SecurityException e) {
      //$JL-EXC$
      return false;
    } catch (Exception e) {
      //$JL-EXC$
      return false;
    }
    return true;
  }

  private void internalLogin(String username, char[] password) throws Exception {
    Properties properties = new Properties();
    properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, namingFactory);
    properties.put(InitialContext.SECURITY_PRINCIPAL, username);
    properties.put(InitialContext.SECURITY_CREDENTIALS, new String(password));
    if (withAppClient) {
      properties.put("appclient", "true");
    }
    context = new InitialContext(properties);
  }

  private void internalLogout() throws Exception {
    context.close();
  }
}