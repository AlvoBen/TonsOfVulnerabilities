package com.sap.engine.services.security.remoteimpl;

import java.rmi.RemoteException;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.interfaces.security.ModificationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.login.monitor.MonitorTable;
import com.sap.engine.services.security.remote.RemoteJACCUMEIntegration;
import com.sap.engine.services.security.remote.RemotePolicyConfiguration;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remote.RemoteUserStoreFactory;
import com.sap.engine.services.security.remote.crypt.RemoteCryptography;
import com.sap.engine.services.security.remote.domains.RemoteProtectionDomains;
import com.sap.engine.services.security.remote.login.RemoteLoginContextHelper;
import com.sap.engine.services.security.remote.sessions.RemoteSecuritySessions;
import com.sap.engine.services.security.remoteimpl.crypt.RemoteCryptographyImpl;
import com.sap.engine.services.security.remoteimpl.domains.RemoteProtectionDomainsImpl;
import com.sap.engine.services.security.remoteimpl.login.RemoteLoginContextHelperImpl;
import com.sap.engine.services.security.remoteimpl.sessions.RemoteSecuritySessionsImpl;
import com.sap.engine.services.security.server.PolicyConfigurationSecurityContext;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.userstore.RemoteUserStoreFactoryImpl;
import com.sap.tc.logging.Severity;

public class RemoteSecurityImpl extends javax.rmi.PortableRemoteObject implements RemoteSecurity {

  public final static String DEFAULT_POLICY_CONFIGURATION = "other";
  private SecurityContext security = null;
  private RemoteUserStoreFactory factory = null;
  private RemoteProtectionDomainsImpl domains = null;
  private RemoteSecuritySessions sessions = null;
  private RemoteCryptographyImpl crypt = null;
  private RemoteJACCUMEIntegrationImpl jacc = null;

  public RemoteSecurityImpl(SecurityContext security) throws RemoteException {
    this.security = security;
    this.factory = new RemoteUserStoreFactoryImpl(security.getUserStoreContext());
    this.domains = new RemoteProtectionDomainsImpl(security);
    this.sessions = new RemoteSecuritySessionsImpl();
    this.crypt = new RemoteCryptographyImpl();
    this.jacc  = new RemoteJACCUMEIntegrationImpl(security.getJACCSecurityRoleMappingContext());
  }

  /**
   *  Returns remote cryptography handler.
   *
   * @return  a remote cryptography instance.
   */
  public RemoteCryptography getCryptography() throws RemoteException {
    return crypt;
  }

  /**
   *  Returns policy configuration  with the given name.
   *
   * @param  name  the identifier of the policy configuration.
   *
   * @return  a remote policy configuration instance.
   */
  public RemotePolicyConfiguration getPolicyConfiguration(String name) throws RemoteException {
  	SecurityContext securityContext = security.getPolicyConfigurationContext(name);
  	
  	if (securityContext == null) {
  	  throw new RemoteException("No such policy configuration: " + name);
  	}
  	
  	return new RemotePolicyConfigurationImpl(this, securityContext, name);
  }

  /**
   *  Returns protection domains handler.
   *
   * @return  a remote protection domains instance.
   */
  public synchronized RemoteProtectionDomains getProtectionDomains() throws RemoteException {
    return domains;
  }

  /**
   *  Returns remote security sessions handler.
   *
   * @return  a remote security sessions instance.
   */
  public RemoteSecuritySessions getSecuritySessions() throws RemoteException {
    return sessions;
  }

  /**
   *  Returns all registered policy configurations.
   *
   * @return  a list of the names of the policy configurations.
   */
  public String[] listPolicyConfigurations() throws RemoteException {
    try {
      return security.listPolicyConfigurations();
    } catch (Throwable err) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "RemoteSecurityImpl", err);
    }
    
    return null;
  }

  /**
   * @deprecated use registerPolicyConfiguration(String name, byte type)
   * instead
   */
  public void registerPolicyConfiguration(String name) throws RemoteException {
    registerPolicyConfiguration(name, SecurityContext.TYPE_OTHER);    
  }
  
  /**
   *  Registers a new policy configuration. This could represent a J2EE
   * application, J2EE component or JCA resource adapter.
   *  Note that policy configurations are not hierarhical
   *
   * @param  configurationId  identifier of the component
   * @param  configurationType  type of the component
   */
  public void registerPolicyConfiguration(String name, byte type) throws RemoteException {
    ModificationContext modification = null;
    ConfigurationHandler handler = null;
    try {
      ConfigurationHandlerFactory configurationHandlerFactory = SecurityServerFrame.getServiceContext().getCoreContext().getConfigurationHandlerFactory();
      handler = configurationHandlerFactory.getConfigurationHandler();
      Configuration configuration = null;
      try {
        configuration = handler.openConfiguration(SecurityConfigurationPath.CUSTOM_POLICY_CONFIGURATION_PATH + "/" + name, ConfigurationHandler.WRITE_ACCESS);
      } catch (ConfigurationException ce) {
        try {
          configuration = handler.openConfiguration(SecurityConfigurationPath.CUSTOM_POLICY_CONFIGURATION_PATH, ConfigurationHandler.WRITE_ACCESS).createSubConfiguration(name);
        } catch (ConfigurationException cee) {
          try {
            configuration = handler.createRootConfiguration(SecurityConfigurationPath.CUSTOM_POLICY_CONFIGURATION_PATH).createSubConfiguration(name);
          } catch (ConfigurationException ceee) {
            Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "RemoteSecurityImpl", ceee);
          }
        }
      }
      modification = security.getModificationContext();
      modification.beginModifications(configuration).registerPolicyConfiguration(name, type);
      handler.commit();
    } catch (Throwable e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "RemoteSecurityImpl",  e);
    } finally {
      if (handler != null) {
        try {
          handler.closeAllConfigurations();
        } catch (ConfigurationException ce) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "RemoteSecurityImpl", ce);
        }
      }
    }
  }

  public void unregisterPolicyConfiguration(String name) throws RemoteException {
    try {
      security.unregisterPolicyConfiguration(name);
    } catch (Throwable err) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "RemoteSecurityImpl", err);
    }
  }

  /**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext() throws RemoteException {
    return new RemoteLoginContextHelperImpl(security);
  }

  /**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext(String policyConfiguration) throws RemoteException {
	SecurityContext policyConfigurationContext = security.getPolicyConfigurationContext(policyConfiguration);

    if (policyConfigurationContext == null) {
      if (Util.SEC_SRV_LOCATION.beInfo()) {
        Util.SEC_SRV_LOCATION.infoT("Cannot get \"{0}\" policy configuration. Policy configuration \"other\" is used instead.", new String[]{policyConfiguration});
      }
      
      policyConfigurationContext = getDefaultPolicyConfiguration();
    }

    return new RemoteLoginContextHelperImpl(policyConfigurationContext);
  }
  
/**
   *  Returns a remote interface to the JAAS login system of the server VM.
   *
   * @param  policyConfiguration  the name of the policy configuration to be used for authentication
   * @param  preparedCredentials  prepared credentials as answers to callbacks
   *
   * @return  an instance of RemoteLoginContext.
   */
  public RemoteLoginContextHelper getRemoteLoginContext(String policyConfiguration, Object[] preparedCredentials) throws RemoteException {
    SecurityContext policyConfigurationContext = security.getPolicyConfigurationContext(policyConfiguration);

    if (policyConfigurationContext == null) {
      if (Util.SEC_SRV_LOCATION.beInfo()) {
        Util.SEC_SRV_LOCATION.infoT("Cannot get \"{0}\" policy configuration. Policy configuration \"other\" is used instead.", new String[]{policyConfiguration});
      }
        
      policyConfigurationContext = getDefaultPolicyConfiguration();
    }

    return new RemoteLoginContextHelperImpl(policyConfigurationContext, preparedCredentials);
  }
  
  /**
   *  Returns an interface to the user managing module.
   *
   * @return  an interface to the user managing module.
   */
  public RemoteUserStoreFactory getRemoteUserStoreFactory() throws RemoteException {
    return factory;
  }


  public RemoteJACCUMEIntegration getRemoteJACCUMEIntegration() throws RemoteException {
	  return jacc;
  }
  
  public void registerManagementListener(ManagementListener managementListener) {
    // todo: implement monitoring methods
  }


  /**
   *  Returns the number of login calls to the active user store since server
   * start-up.
   *
   * @return the number of logins.
   */
  public long getUserStoreAccessCount() {
    return 0;
  }

  /**
   *  Returns the names of the authentication stacks available in the server.
   *
   * @return an array of java.lang.String
   */
  public String[] getAuthenticationStacks() {
    return MonitorTable.getAuthenticationStacks();
  }

  /**
   *  Returns the number of currently active security sessions of the given
   * authentication stack.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  currently active sessions count
   */
  public int getActiveSessionsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).successLogonCount;
  }

  /**
   *  Returns the number of different users of the active user store that have
   * active security sessions for the given authentication stack.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of different users with security sessions.
   */
  public int getLoggedUsersCount(String authenticationStack) {
    return 0;
  }

  /**
   *  Returns the total number of security sessions created for the given
   * authentication stack since start-up of the server.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the total number of created security sessions
   */
  public long getTotalSessionsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).totalSessionCount;
  }

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have timed out ( expired ) without the explicit logout call of
   * the user.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of secuerity sessions that timed out.
   */
  public long getTimedOutSessionsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).tiemoutSessionCount;
  }

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have be invalidated with explicit logout call from the user
   * owner of the security session.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return the number of sessions with explicit logout call from client.
   */
  public long getLoggedOffSessionsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).logoffSessionCount;
  }

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
  public long getUnsuccessfullLogonAttemptsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).failedLogonCount;
  }

  /**
   *  Returns the number of invalidated sessions for the given
   * authentication stack.The session is invalidated either with logout and still not garbage
   * collected or with explicit invalidation call.
   *
   * @param authenticationStack  the name of the authentication stack
   *
   * @return  the number of invalidated sessions.
   */
  public long getInvalidSessionsCount(String authenticationStack) {
    return MonitorTable.getMonitor(authenticationStack).invalidSessionCount;
  }

  /**
   *  Returns the number of currently active security sessions of the given
   * authentication stack.
   *
   * @return  currently active sessions count
   */
  public int getActiveSessionsCount() {
    return MonitorTable.getAggregatedMonitor().successLogonCount;
  }

  /**
   *  Returns the number of different users of the active user store that have
   * active security sessions for the given authentication stack.
   *
   * @return  the number of different users with security sessions.
   * @deprecated this functionality is not monitored anymore
   */
  public int getLoggedUsersCount() {
    return 0;
  }

  /**
   *  Returns the total number of security sessions created for the given
   * authentication stack since start-up of the server.
   *
   * @return  the total number of created security sessions
   */
  public long getTotalSessionsCount() {
    return MonitorTable.getAggregatedMonitor().totalSessionCount;
  }

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have timed out ( expired ) without the explicit logout call of
   * the user.
   *
   * @return  the number of secuerity sessions that timed out.
   */
  public long getTimedOutSessionsCount() {
    return MonitorTable.getAggregatedMonitor().tiemoutSessionCount;
  }

  /**
   *  Returns the number of security sessions for the given authentication
   * stack that have be invalidated with explicit logout call from the user
   * owner of the security session.
   *
   * @return the number of sessions with explicit logout call from client.
   */
  public long getLoggedOffSessionsCount() {
    return MonitorTable.getAggregatedMonitor().logoffSessionCount;
  }

  /**
   *  Returns the number of failed attempts to logon for the given
   * authentication stack.
   *  The attempt can fail because of wrong user name or credentials provided
   * by client.
   *
   * @return  the number of failed logon attempts.
   */
  public long getUnsuccessfullLogonAttemptsCount() {
    return MonitorTable.getAggregatedMonitor().failedLogonCount;
  }

  /**
   *  Returns the aggregated number of invalidated sessions for all the authentication stacks.
   * The session is invalidated either with logout and still not garbage
   * collected or with explicit invalidation call.
   *
   * @return  the number of invalidated sessions.
   */
  public long getInvalidSessionsCount() {
    return MonitorTable.getAggregatedMonitor().invalidSessionCount;
  }
  
  private SecurityContext getDefaultPolicyConfiguration() {
	Util.SEC_SRV_LOCATION.infoT("Trying to get policy configuration \"other\".");
	
    SecurityContext sc = security.getPolicyConfigurationContext(DEFAULT_POLICY_CONFIGURATION);
	
    if (sc == null) {
      sc = new PolicyConfigurationSecurityContext(security, DEFAULT_POLICY_CONFIGURATION, null);
    }

    return sc;
  }
}