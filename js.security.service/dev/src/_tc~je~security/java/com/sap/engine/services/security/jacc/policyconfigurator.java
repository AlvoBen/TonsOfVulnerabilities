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
 */
package com.sap.engine.services.security.jacc;

import java.security.Policy;
import javax.security.jacc.PolicyConfigurationFactory;

import com.sap.engine.system.SystemPolicy;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.jacc.provider.PolicyConfigurationFactoryLoader;
import com.sap.security.core.role.persistence.PersistenceLayer;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *  A class responsible to set the policy class if there is an external
 * policy specified via system property "javax.security.jacc.policy.provider".
 *  If no policy is specified a default one is used.
 */
public final class PolicyConfigurator {

  // make these service properties
  private final static String POLICY_PROPERTY = "javax.security.jacc.policy.provider";
  private final static String FACTORY_PROPERTY = "javax.security.jacc.PolicyConfigurationFactory.provider";

  private final static String LOADER_POLICY = "com.sap.engine.system.SystemPolicy";

  // The array contains the chain of class names of Policy in the order they delegate to each other
  private final static String[] POLICY_IMPL = {
    "com.sap.engine.services.security.jacc.provider.PolicyImpl",
    "com.sap.security.core.UmePolicy",
    "com.sap.security.core.role.jacc.JACCPolicy",
    System.getProperty(POLICY_PROPERTY)
  };

  // The array contains the chain of class names of PolicyConfigurationFactory in the order they delegate to each other
  private final static String[] FACTORY_IMPL = {
    "com.sap.security.core.role.jacc.UmePolicyConfigurationFactory",
    "com.sap.security.core.role.jacc.JACCPolicyConfigurationFactory",
    System.getProperty(FACTORY_PROPERTY)
  };

  private final static Location LOCATION = Location.getLocation(PolicyConfigurator.class.getName());

  /**
   *  Set up the chain of Policy instances and the chain of PolicyConfigurationFactory instances.
   */
  public static void setPolicy() {
  	if (LOCATION.bePath()) {
  	  LOCATION.logT(Severity.PATH, "Entering setPolicy()");
    }

  	try {
      // Set the policies in their order of delegation
      // When each is constructed the policy to delegate to is visible via Policy.getPolicy()
      Policy policyProvider = null;
      for (String policyProviderClass: POLICY_IMPL) {
        if (policyProviderClass != null) {
          policyProvider = getPolicy(policyProviderClass);

          if (policyProvider != null) {
            Policy.setPolicy(policyProvider);
          }
        }
      }

      if (policyProvider != null) {
        SystemPolicy.setPolicy((Object) policyProvider);
        System.setProperty(POLICY_PROPERTY, LOADER_POLICY);
      }

      // Set the policy configuration factories in their order of delegation
      // When each is constructed the policy to delegate to is visible via PolicyConfigurationFactory.getPolicyConfigurationFactory()
      PolicyConfigurationFactory factoryProvider = null;
      for (String factoryProviderClass: FACTORY_IMPL) {
        if (factoryProviderClass != null) {
          factoryProvider = (PolicyConfigurationFactory) getImplementation(factoryProviderClass);

          if (factoryProvider != null) {
            PolicyConfigurationFactoryLoader.setPolicyConfigurationFactory(factoryProvider);
          }
        }
      }
    } catch (Exception exception) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000204", "Unable to select Policy or PolicyConfigurationFactory!", exception);
    } catch (NoClassDefFoundError error) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000204", "Unable to select Policy or PolicyConfigurationFactory!", error);
    } 
      
    try {
      // log policy is ...
      if (LOCATION.beInfo()) {
        LOCATION.logT(Severity.INFO, "Policy is {0}", new Object[] { Policy.getPolicy().getClass().getName() });
        LOCATION.logT(Severity.INFO, "JACC policy configuration factory is {0}", new Object[] { PolicyConfigurationFactory.getPolicyConfigurationFactory().getClass().getName() });
      }
    } catch (Exception exception) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000205", "Unable to identify Policy or PolicyConfigurationFactory!", exception);
    } catch (NoClassDefFoundError error) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000205", "Unable to identify Policy or PolicyConfigurationFactory!", error);
    } 
      

    // workaround for problems of CTS tests
    // the tests depend on each other !?!
    try {
      Policy.getPolicy().refresh();
      PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration("CTSTEST", false);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000206", "Unexpected exception when accessing Policy and JACC Policy Configuration Factory!", e);
    }

    PersistenceLayer.useServiceContext(SecurityServerFrame.getServiceContext());
  }

  private final static Policy getPolicy(String className) {
    Object policy = getImplementation(className);

    if (policy instanceof Policy) {
      return (Policy) policy;
    } else {
      if (LOCATION.bePath()) {
        LOCATION.logT(Severity.PATH, "Policy class {0} is not instance of java.security.Policy!", new Object[] { className });
      }
      return null;
    }
  }

  private final static Object getImplementation(String className) {
    if (LOCATION.bePath()) {
      LOCATION.logT(Severity.PATH, "Looking for implementation {0}", new Object[] { className });
    }

    if (className != null) {
      try {
        return PolicyConfigurator.class.getClassLoader().loadClass(className).newInstance();
      }catch (Exception exception) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, exception, "ASJ.secsrv.000207", "Unexpected exception on attempt to instantiate {0}!", new Object[] { className });
      } catch (NoClassDefFoundError error) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, error, "ASJ.secsrv.000207", "Unexpected exception on attempt to instantiate {0}!", new Object[] { className });
      } 
    }

    return null;
  }

}
