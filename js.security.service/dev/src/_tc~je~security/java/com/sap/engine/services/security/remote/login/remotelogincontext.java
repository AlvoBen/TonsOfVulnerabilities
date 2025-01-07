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
package com.sap.engine.services.security.remote.login;

import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.StubBaseInfo;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remoteimpl.login.RemoteCallbackHandlerImpl;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextInterface;
import com.sap.engine.interfaces.cross.RemoteBroker;

import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 *  Extension of JAAS login context to provide JAAS authentication contract for remote clients.
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class RemoteLoginContext implements RemoteLoginContextInterface {

  private int serverId;
  private RemoteCallbackHandler handler = null;
  private RemoteLoginContextHelper login = null;


  /**
   *  Constructs and connects a remote login context with the given connection information.
   *
   * @param handler         JAAS callback handler
   * @param connectionType  RMI/P4 supported connection type.
   * @param host            host name
   * @param port            port
   *
   * @throws LoginException  thrown if connection fails
   *
   * @deprecated
   */
  public RemoteLoginContext(CallbackHandler handler, String connectionType, String host, int port) throws LoginException {
    try {
      P4ObjectBroker broker = P4ObjectBroker.init();

      /////
      // resolveInitialReference(String connectionType, String Name, String hos, int port)
      StubBaseInfo info = (StubBaseInfo) broker.resolveInitialReference(connectionType, "security", host, port);

      serverId = info.server_id;

      RemoteSecurity security = (RemoteSecurity) broker.narrow(info, RemoteSecurity.class, broker.transportType);

      this.login = security.getRemoteLoginContext();
      this.handler = new RemoteCallbackHandlerImpl(handler);
    } catch (RemoteException e) { 
      throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	} catch (IOException e) {
  	  throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	}
  }


  /**
   *  Constructs and connects a remote login context with the given connection information.
   *
   * @param handler         JAAS callback handler
   * @param configurationId security policy configuration to be used for authentication
   * @param connectionType  RMI/P4 supported connection type.
   * @param host            host name
   * @param port            port
   *
   * @throws LoginException  thrown if connection fails
   *
   * @deprecated
   */
  public RemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port) throws LoginException {
    try {
      P4ObjectBroker broker = P4ObjectBroker.init();

      /////
      // resolveInitialReference(String connectionType, String Name, String hos, int port)
      StubBaseInfo info = (StubBaseInfo) broker.resolveInitialReference(connectionType, "security", host, port);

      serverId = info.server_id;

      RemoteSecurity security = (RemoteSecurity) broker.narrow(info, RemoteSecurity.class, broker.transportType);

      this.login = security.getRemoteLoginContext(configurationId);
      this.handler = new RemoteCallbackHandlerImpl(handler);
    } catch (RemoteException e) { 
      throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	} catch (IOException e) {
  	  throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	}
     
  }

  /**
   *  Constructs and connects a remote login context with the given connection information.
   *
   * @param handler          JAAS callback handler
   * @param configurationId  security policy configuration to be used for authentication
   * @param connectionType   RMI/P4 supported connection type.
   * @param host             host name
   * @param port             port
   * @param serverId         Id of the destination server
   *
   * @throws LoginException  thrown if connection fails
   *
   * @deprecated
   */
  public RemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port, int serverId) throws LoginException {
   try {
      P4ObjectBroker broker = P4ObjectBroker.init();

      StubBaseInfo info = (StubBaseInfo) broker.resolveInitialReference(connectionType, "security", host, port, serverId);

      this.serverId = info.server_id;

      RemoteSecurity security = (RemoteSecurity) broker.narrow(info, RemoteSecurity.class, broker.transportType);

      this.login = security.getRemoteLoginContext(configurationId);
      this.handler = new RemoteCallbackHandlerImpl(handler);
    } catch (RemoteException e) { 
      throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	} catch (IOException e) {
  	  throw new BaseLoginException("Cannot create new RemoteLoginContext instance.", e);
  	}
  }

  /**
   *  Constructs and connects a remote login context with the given connection information.
   *
   * @param handler          JAAS callback handler
   * @param configurationId  Security policy configuration to be used for authentication.
   * @param remoteBroker     Broker for the RMI_P4 connection.
   *
   * @throws LoginException  Thrown if connection fails or if there is no policy configuration with provided name.
   */
  public RemoteLoginContext(CallbackHandler handler, String configurationId, RemoteBroker remoteBroker) throws LoginException {
   try {
      RemoteSecurity security = (RemoteSecurity) remoteBroker.resolveInitialReference("security", RemoteSecurity.class);

      this.login = security.getRemoteLoginContext(configurationId);
      if (this.login == null) {
        throw new LoginException("Cannot create RemoteLoginContext for non-existing policy configuration '" + configurationId + "'.");
      }

      this.handler = new RemoteCallbackHandlerImpl(handler);
    } catch (LoginException e) {
      throw e;
    } catch (Exception e) {
      throw new BaseLoginException("Exception in creating new RemoteLoginContext instance.", e);
    }
  }

  /**
   * Performes authentication using the authentication stack set for the login context.
   *
   * @throws LoginException  thrown if authentication fails on remote side or communication fails.
   */
  public void login() throws LoginException {
    try {
      login.login(handler);
    } catch (RemoteException e) {
      throw new BaseLoginException("Exception in remote login.", e);
    }
  }


  /**
   * Performes logout using the authentication stack set for the login context.
   *
   * @throws LoginException  thrown if session removal on remote side or communication fails.
   */
  public void logout() throws LoginException {
    try {
      login.logout();
    } catch (RemoteException e) {
      throw new BaseLoginException("Exception in remote logout.", e);
    }
  }

  public int getServerId() {
    return serverId;
  }

}