/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.login;


import com.sap.engine.interfaces.cross.RemoteBroker;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextFactory;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextInterface;
import com.sap.engine.services.security.remote.login.RemoteLoginContext;
import com.sap.engine.services.security.restriction.Restrictions;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

/**
 *  Factory for creating of RemoteLoginContext instances.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class RemoteLoginContextFactoryImpl extends RemoteLoginContextFactory {

  /**
   *  Gets a new instance of RemoteLoginContext.
   *
   * @param handler - The callback handler which will retrieve the needed credentials.
   * @param configurationId - The name of the deployed component or service,
   *                          whish login module stack will be used for authentication.
   * @param connectionType - type (protocol) of the connection.
   *                         Possible values are all types supported by p4 service. For example :
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_NONE,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_HTTP,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_SSL,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_HTTPS
   * @param host - the host for the remote connection
   * @param port - the port for the remote connection
   *
   * @return  new RemoteLoginContext instance.
   *
   * @throws LoginException  If some exception occures on creating the new RemoteLoginContext.
   * 
   * @deprecated
   */
  public RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port) throws LoginException {
    return new RemoteLoginContext(handler, configurationId, connectionType, host, port);
  }

  /**
   *  Gets a new instance of RemoteLoginContext.
   *
   * @param handler - The callback handler which will retrieve the needed credentials.
   * @param configurationId - The name of the deployed component or service,
   *                          whish login module stack will be used for authentication.
   * @param connectionType - type (protocol) of the connection.
   *                         Possible values are all types supported by p4 service. For example :
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_NONE,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_HTTP,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_SSL,
   *                         ConnectionParameters.TRANSPORT_LAYER_VALUE_HTTPS
   * @param host - the host for the remote connection
   * @param port - the port for the remote connection
   * @param serverId - the id of the destination server
   *
   * @return  new RemoteLoginContext instance.
   *
   * @throws LoginException  If some exception occures on creating the new RemoteLoginContext.
   * 
   * @deprecated
   */
  public RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port, int serverId) throws LoginException {
    return new RemoteLoginContext(handler, configurationId, connectionType, host, port, serverId);
  }
  
  /**
   *  Gets a new instance of RemoteLoginContext.
   *
   * @param handler - The callback handler which will retrieve the needed credentials.
   * @param configurationId - The name of the deployed component or service,
   *                          whish login module stack will be used for authentication.
   * @param remoteBroker - Broker for the RMI_P4 connection.
   *
   * @return  new RemoteLoginContext instance.
   *
   * @throws LoginException  If some exception occures on creating the new RemoteLoginContext.
   */
  public RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, RemoteBroker remoteBroker) throws LoginException {
    return new RemoteLoginContext(handler, configurationId, remoteBroker);
  }

  /**
   * Called to verify if the caller has permission for some actions.
   */
  public void checkPermission() {
    Restrictions.checkSystemPermission();
  }
}
