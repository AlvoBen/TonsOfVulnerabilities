/**
 * Property of SAP AG, Walldorf (c) Copyright SAP AG, Walldorf, 2000-2002. All
 * rights reserved.
 */

package com.sap.engine.services.security.server;

import java.lang.reflect.Method;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.RemoteLoginContextFactory;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.userstore.UserStoreService;
import com.sap.engine.interfaces.security.*;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.services.security.server.jaas.LoginModuleHelperImpl;
import com.sap.engine.services.security.login.RemoteLoginContextFactoryImpl;
import com.sap.tc.logging.Severity;

/**
 * The root context for security in J2EE Engine.
 * 
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class SecurityContextImpl extends PolicyConfigurations {

  public final static String J2EE_ENGINE_CONFIGURATION = ROOT_POLICY_CONFIGURATION;

  private static SecurityContext reference = null;

  private String name = null;

  private byte type = TYPE_TEMPLATE;

  // fields
  private AuthenticationContext authentication = null;

  private AuthorizationContext authorization = null;

  private CryptographyContext cryptography = null;

  private static UserStoreFactory userFactory = null;

  private static JACCSecurityRoleMappingContext jaccMappingContext = null;

  private LoginModuleHelperImpl moduleHelper = null;

  private ServiceContext environment = null;

  private static String systemID = null;

  public SecurityContextImpl( ServiceContext environment) throws ServiceException {
    super();
    this.reference = this;
    this.environment = environment;
    this.name = J2EE_ENGINE_CONFIGURATION;
    moduleHelper = new LoginModuleHelperImpl(this);
    AbstractLoginModule.setLoginModuleHelper(moduleHelper);
    RemoteLoginContextFactory.setRemoteLoginContextFactory(new RemoteLoginContextFactoryImpl());

    super.init();

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "New root security context successfully created.");
    }
  }

  public static String getSystemID() {
    if (systemID == null) {
      synchronized (SecurityServerFrame.context) {
        systemID = SecurityServerFrame.context.getClusterContext().getClusterMonitor().getMessageServerBridge().getSystemID();
        
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.debugT("getSystemID=" + systemID);
        }
        
        setSID(systemID);
      }
    }
    return systemID;
  }

  private static void setSID(String systemID) {
    try {
      Class sap_logon_ticket_helper = Class.forName("com.sap.security.core.server.jaas.SAPLogonTicketHelper");
      Method set_system_ID = sap_logon_ticket_helper.getMethod("setSystemID", new Class[] { String.class });
      set_system_ID.invoke(sap_logon_ticket_helper, new Object[] { systemID });
    } catch (Exception e) {
      PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Cannot initialize the jaas library with SID.", e);
    }
  }

  public static SecurityContext getRoot() {
    return reference;
  }

  /**
   * Retireve a context for mapping jacc roles to ume roles.
   * 
   * @return JACCSecurityRoleMappingContext
   */
  public JACCSecurityRoleMappingContext getJACCSecurityRoleMappingContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering SecurityContextImpl.getJACCSecurityRoleMappingContext()");
    }
    try {
      if (jaccMappingContext == null) {
        try {
          jaccMappingContext = (JACCSecurityRoleMappingContext) Class.forName("com.sap.security.core.server.ume.service.jacc.JACCSecurityRoleMapperContextImpl").newInstance();
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "JACCSecurityRoleMappingContext successfully created for root configuration [{0}].", new Object[] { J2EE_ENGINE_CONFIGURATION });
          }
        } catch (InstantiationException e) {
          // $JL-EXC$
          // to do
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "InstantiationException occurred during creation of JACCSecurityRoleMappingContext for root configuration [{0}].",
                new Object[] { J2EE_ENGINE_CONFIGURATION });
          }
        } catch (IllegalAccessException e) {
          // $JL-EXC$
          // to do
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "IllegalAccessException occurred during creation of JACCSecurityRoleMappingContext for root configuration [{0}].",
                new Object[] { J2EE_ENGINE_CONFIGURATION });
          }
        } catch (ClassNotFoundException e) {
          // $JL-EXC$
          // to do
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "ClassNotFoundException occurred during creation of JACCSecurityRoleMappingContext for root configuration [{0}].",
                new Object[] { J2EE_ENGINE_CONFIGURATION });
          }
        }
      }
      return jaccMappingContext;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting SecurityContextImpl.getJACCSecurityRoleMappingContext()");
      }
    }
  }

  /**
   * Retireve a context for managing authentication configuration.
   * 
   * @return authentication context
   */
  public AuthenticationContext getAuthenticationContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering SecurityContextImpl.getAuthenticationContext()");
    }
    try {
      if (authentication == null) {
        synchronized (this) {
          if (authentication == null) {
            authentication = new AuthenticationContextImpl(J2EE_ENGINE_CONFIGURATION, this, null);
            ((AuthenticationContextImpl) authentication).update();
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "AuthenticationContext successfully created for root configuration [{0}].", new Object[] { J2EE_ENGINE_CONFIGURATION });
            }
          }
        }
      }
      return authentication;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting SecurityContextImpl.getAuthenticationContext()");
      }
    }
  }

  /**
   * Retireve a context for managing authorization configuration.
   * 
   * @return authorization context
   */
  public AuthorizationContext getAuthorizationContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering SecurityContextImpl.getAuthorizationContext()");
    }
    try {
      if (authorization == null) {
        synchronized (this) {
          if (authorization == null) {
            authorization = new AuthorizationContextImpl(J2EE_ENGINE_CONFIGURATION, this);
            ((AuthorizationContextImpl) authorization).update();
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "AuthorizationContext successfully created for root configuration [{0}].", new Object[] { J2EE_ENGINE_CONFIGURATION });
            }
          }
        }
      }
      return authorization;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting SecurityContextImpl.getAuthorizationContext()");
      }
    }
  }

  /**
   * Retireve a context for access to configured cryptography modules.
   * 
   * @return cryptography context
   */
  public CryptographyContext getCryptographyContext() {
    if (cryptography == null) {
      synchronized (this) {
        if (cryptography == null) {
          cryptography = new CryptographyContextImpl(J2EE_ENGINE_CONFIGURATION, this);
        }
      }
    }
    return cryptography;
  }

  public JACCContext getJACCContext(String policyConfiguration) {
    return new JACCContextImpl(policyConfiguration);
  }

  /**
   * Returns the service context of security service.
   * 
   * @return service context
   */
  public ServiceContext getEnvironment() {
    return environment;
  }

  /**
   * Retireve the name of this security context.
   * 
   * @return the name of the security context.
   */
  public String getPolicyConfigurationName() {
    return name;
  }

  /**
   * Retireve the type of this security context.
   * 
   * @return the type of the security context.
   */
  public byte getPolicyConfigurationType() {
    return type;
  }

  /**
   * Sets the specified name for this security context.
   * 
   * @return the name of the security context.
   */
  public void setPolicyConfigurationName(String name) {

  }

  /**
   * Retireve a context for bundling of modification operations.
   * 
   * @return modification bundling context
   */
  public ModificationContext getModificationContext() {
    return new ModificationContextImpl(this);
  }

  /**
   * Retireve a context for managing user stores.
   * 
   * @return user store context
   */
  public UserStoreFactory getUserStoreContext() {
    if (userFactory == null) {
      synchronized (this) {
        if (userFactory == null) {
          try {
            UserStoreService userService = new UserStoreService(this);
            userFactory = userService.getUserStoreFactory();

            userService.initializePolicy(this);
          } catch (ServiceException se) {
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.traceThrowableT(Severity.DEBUG, "Exception occurred during get of user store context.", se);
            }
            throw new SecurityException(se.toString());
          }
        }
      }
    }

    return userFactory;
  }

  public LoginModuleHelperImpl getLoginModuleHelper() {
    return moduleHelper;
  }

  public SecurityContext getPolicyConfigurationContext(String configId) {
    if (configId.equals(name)) {
      return this;
    }
    return super.getPolicyConfigurationContext(configId);
  }

  public String getConfigurationPath(String configurationId) {
    return SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH + '/' + configurationId;
  }
}
