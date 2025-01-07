/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.remote;

import java.rmi.RemoteException;
import java.rmi.Remote;

import com.sap.engine.services.security.remote.crypt.RemoteCryptography;
import com.sap.engine.services.security.remote.sessions.RemoteSecuritySessions;
import com.sap.engine.services.security.remote.login.RemoteLoginContextHelper;
import com.sap.engine.services.security.remote.domains.RemoteProtectionDomains;
import com.sap.engine.frame.state.ManagementInterface;

/**
 *  The interface of the security service outside the cluster..
 *
 * @author  Stephan Zlatarev
 * @version 4.0.3
 */
public interface RemoteSecurity extends ManagementInterface, Remote {

  /**
   *  Constant for invalid policy configuration type. //used for checking 
   */
  public final static byte TYPE_INVALID = 0;
  
  /**
   *  Constant for all policy configuration types that 
   *  (1) do not belong to any of the other types, 
   * 	or (2) have not been successfully migrated.
   */
  public final static byte TYPE_OTHER = 1;
  
  /**
   *  Constant for all standard policy configurations, 
   *  for example basic, form, client-cert, etc.
   */
  public final static byte TYPE_TEMPLATE = 2;
  
  /**
   *  Constant for the policy configuration type of a custom created policy configuration.
   */
  public final static byte TYPE_CUSTOM = 3;
  
  /**
   *  Constant for the policy configuration type of a service. 
   *  // such as service.iiop, service.telnet, etc 
   */
  public final static byte TYPE_SERVICE = 4;
  
  /**
   *  Constant for policy configuration type of  a web services component.
   */
  public final static byte TYPE_WEB_SERVICE = 5;
  
  /**
   *  Constant for the policy configuration type of a web component.
   */
  public final static byte TYPE_WEB_COMPONENT = 6;
  
  /**
   *  Constant for the policy configuration type of an ejb component.
   */
  public final static byte TYPE_EJB_COMPONENT = 7;
  
  /**
   *  Returns remote cryptography handler.
   *
   * @return  a remote cryptography instance.
   */
  public RemoteCryptography getCryptography() throws RemoteException;

  /**
   *  Returns policy configuration  with the given name.
   *
   * @param  name  the identifier of the policy configuration.
   *
   * @return  a remote policy configuration instance.
   */
  public RemotePolicyConfiguration getPolicyConfiguration(String name) throws RemoteException;

  /**
   *  Returns protection domains handler.
   *
   * @return  a remote protection domains instance.
   */
  public RemoteProtectionDomains getProtectionDomains() throws RemoteException;

  /**
   *  Returns remote security sessions handler.
   *
   * @return  a remote security sessions instance.
   */
  public RemoteSecuritySessions getSecuritySessions() throws RemoteException;

  /**
   *  Returns all registered policy configurations.
   *
   * @return  a list of the names of the policy configurations.
   */
  public String[] listPolicyConfigurations() throws RemoteException;

  /**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext() throws RemoteException;

  /**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext(String policyConfiguration) throws RemoteException;

  /**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @param  policyConfiguration  the name of the policy configuration to be used for authentication
   * @param  preparedCredentials  prepared credentials as answers to callbacks
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext(String policyConfiguration, Object[] preparedCredentials) throws RemoteException;
  
  /**
   * @deprecated 
   * use registerPolicyConfiguration(String configurationId, byte configurationType) instead
   */
  public void registerPolicyConfiguration(String configurationId) throws RemoteException;

  /**
   *  Registers a new policy configuration. This could represent a J2EE
   * application, J2EE component or JCA resource adapter.
   *  Note that policy configurations are not hierarhical
   *
   * @param  configurationId  identifier of the component
   * @param  configurationType  type of the component
   */
  public void registerPolicyConfiguration(String configurationId, byte configurationType) throws RemoteException;

  /**
   *  Unregisters a policy configuration.
   *
   * @param  configurationId  identifier of the policy configuration
   */
  public void unregisterPolicyConfiguration(String configurationId) throws RemoteException;

  /**
   *  Returns an interface to the user managing module.
   *
   * @return  an interface to the user managing module.
   */
  public RemoteUserStoreFactory getRemoteUserStoreFactory() throws RemoteException;


  /**
   *  Returns an interface to the integration management of the UME JACC Provider.
   *
   * @return  an interface to the integration management of the UME JACC Provider.
   */
  public RemoteJACCUMEIntegration getRemoteJACCUMEIntegration() throws RemoteException;
  
  // monitoring methods

  /**
   *  Returns the number of login calls to the active user store since server
   * start-up.
   *
   * @return the number of logins.
   */
  public long getUserStoreAccessCount();

  /**
   *  Returns the names of the authentication stacks available in the server.
   *
   * @return an array of java.lang.String
   */
  public String[] getAuthenticationStacks();

  /**
   *  Returns the number of currently active security sessions of the given
   * authentication stack.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  currently active sessions count
   */
  public int getActiveSessionsCount(String authenticationStack);

  /**
   *  Returns the number of different users of the active user store that have
   * active security sessions for the given authentication stack.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of different users with security sessions.
   */
  public int getLoggedUsersCount(String authenticationStack);

  /**
   *  Returns the total number of security sessions created for the given
   * authentication stack since start-up of the server.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the total number of created security sessions
   */
  public long getTotalSessionsCount(String authenticationStack);

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have timed out ( expired ) without the explicit logout call of
   * the user.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of secuerity sessions that timed out.
   */
  public long getTimedOutSessionsCount(String authenticationStack);

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have be invalidated with explicit logout call from the user
   * owner of the security session.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return the number of sessions with explicit logout call from client.
   */
  public long getLoggedOffSessionsCount(String authenticationStack);

  /**
   *  Returns the number of failed attempts to logon for the given
   * authentication stack.
   *  The attempt can fail because of wrong user name or credentials provided
   * by client.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of failed logon attempts.
   */
  public long getUnsuccessfullLogonAttemptsCount(String authenticationStack);

  /**
   *  Returns the number of invalidated sessions for the given
   * authentication stack.The session is invalidated either with logout and still not garbage
   * collected or with explicit invalidation call.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of invalidated sessions.
   */
  public long getInvalidSessionsCount(String authenticationStack);

  /**
   *  Returns the aggregated number of currently active security sessions on the server
   *
   * @return  currently active sessions count
   */
  public int getActiveSessionsCount();

  /**
   *  Returns the aggregated number of different users of the active user store that have
   * active security sessions on the server
   *
   * @return  the number of different users with security sessions.
   */
  public int getLoggedUsersCount();

  /**
   *  Returns the total number of security sessions created since start-up of the server.
   *
   * @return  the total number of created security sessions
   */
  public long getTotalSessionsCount();

  /**
   *  Returns the aggregated number of security sessions that have timed out ( expired ) without the explicit logout call of
   * the user.
   *    *
   * @return  the number of secuerity sessions that timed out.
   */
  public long getTimedOutSessionsCount();

  /**
   *  Returns the aggregated number of security sessions that have be invalidated with explicit logout call from the user
   * owner of the security session.
   *
   * @return the number of sessions with explicit logout call from client.
   */
  public long getLoggedOffSessionsCount();

  /**
   *  Returns the aggregated number of failed attempts to logon.
   *  The attempt can fail because of wrong user name or credentials provided
   * by client.
   *
   * @return  the number of failed logon attempts.
   */
  public long getUnsuccessfullLogonAttemptsCount();

  /**
   *  Returns the aggregated number of invalidated sessions for all the authentication stacks.
   * The session is invalidated either with logout and still not garbage
   * collected or with explicit invalidation call.
   *
   * @return  the number of invalidated sessions.
   */
  public long getInvalidSessionsCount();
}

