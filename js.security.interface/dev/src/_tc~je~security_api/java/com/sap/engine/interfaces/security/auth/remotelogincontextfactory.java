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
package com.sap.engine.interfaces.security.auth;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.cross.RemoteBroker;

/**
 *  Abstract class for creating of RemoteLoginContext for user authentication.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public abstract class RemoteLoginContextFactory {

  private static RemoteLoginContextFactory factory = null;

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
  public abstract RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port) throws LoginException;

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
  public abstract RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, String connectionType, String host, int port, int serverId) throws LoginException;

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
   * 
   * @deprecated
   */
  public abstract RemoteLoginContextInterface getRemoteLoginContext(CallbackHandler handler, String configurationId, RemoteBroker remoteBroker) throws LoginException;

  /**
   * Called to verify if the caller has permission for some actions.
   */
  public abstract void checkPermission();

  /**
   *  Gets an instance of this class.
   *
   * @return  an instance of this class.
   */
  public static RemoteLoginContextFactory getFactory() {
    return factory;
  }

  /**
   *  This method is called on starting of security service and sets an
   * instance that can be retrieved later via methog getFactory().
   *
   * @param loginContextFactory - an instance of class that implements this abstract class.
   */
  public static void setRemoteLoginContextFactory(RemoteLoginContextFactory loginContextFactory) {
    if (loginContextFactory != null) {
      if (factory == null) {
        factory = loginContextFactory;
      }
    } else if (factory != null) {
      factory.checkPermission();
      factory = null;
    }
  }

}