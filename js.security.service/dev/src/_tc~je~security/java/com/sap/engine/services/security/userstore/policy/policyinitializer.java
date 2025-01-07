package com.sap.engine.services.security.userstore.policy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.AuthorizationContext;
import com.sap.engine.interfaces.security.ModificationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.SecurityRoleContext;
import com.sap.engine.interfaces.security.userstore.config.AuthenticationDescriptor;
import com.sap.engine.interfaces.security.userstore.config.AuthorizationDescriptor;
import com.sap.engine.interfaces.security.userstore.config.PolicyDescriptor;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;


public class PolicyInitializer {
  private SecurityContext root = null;
  private final String TEMPLATE = "template";
  private final String SERVICE = "service";
  private final String WEB_SERVICE = "web_service";
  private final String WEB_COMPONENT = "web_component";
  private final String EJB_COMPONENT = "ejb_component";
  private final String CUSTOM = "custom";
  private final String OTHER = "other";

  public void initialize(SecurityContext root) throws ServiceException {
    InputStream in = null;
    try {
      this.root = root;
//      if ((new SchemaValidator()).validate("./persistent/security/policy.xml", "./persistent/security/policy.xsd")) {
      String xmldata = SecurityServerFrame.getServiceProperties().getProperty("policy.xml");
      if (xmldata == null) {
        SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000104", "Cannot initialize PolicyInitializer because of missing <policy.xml> from Security Provider service properties.");
        return;
      }
      while (xmldata.endsWith("=")) {
        xmldata = new String(Base64.decode(xmldata.getBytes()));
      }
      in = new ByteArrayInputStream(xmldata.getBytes());
      try {
        parse(in);
      } catch (Exception e) {
        SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000105", "Cannot initialize PolicyInitializer because <policy.xml> from Security Provider service properties is bad formatted.");
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.ERROR, "Cannot parse <policy.xml>", e);
      }
    } catch (ServiceException se) {
      SimpleLogger.traceThrowable(Severity.ERROR, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000151", "Cannot initialize PolicyInitializer", se);
      throw se;
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000151", "Cannot initialize PolicyInitializer", e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {
          Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "PolicyInitializer error", e);
        }
      }
    }
  }

  public void parse(InputStream in) throws Exception {
    SystemProperties.setProperty(StandardDOMParser.INQMY_PARSER, "yes");
    StandardDOMParser parser = new StandardDOMParser();
    Document document = parser.parse(in);
    Node descriptor = null;
    Node userstoreElement;
    NodeList descriptorList = document.getChildNodes();

    for (int i = 0; i < descriptorList.getLength(); i++) {
      descriptor = descriptorList.item(i);

      if (descriptor.getNodeType() == Node.ELEMENT_NODE) {
        break;
      }
    }

    if (descriptor != null) {
      NodeList storesList = descriptor.getChildNodes();
      for (int i = 0; i < storesList.getLength(); i++) {
        userstoreElement = storesList.item(i);

        if (userstoreElement.getNodeType() == Node.ELEMENT_NODE) {
          try {
            DefaultPolicyDescriptor policy = new DefaultPolicyDescriptor(storesList.item(i));
            initializeUserStore(root, policy);
            //to do initialization
          } catch (ServiceException se) {
            throw se;
          } catch (Exception e) {
            SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000106", "Cannot register user store policy configurations described in <policy.xml> from Security Provider service properties.");
            Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Cannot register userstore policy configurations described in <policy.xml>", e);
          }
        }
      }
    }
  }

  public void initializeUserStore(SecurityContext root, PolicyDescriptor policy) {
    this.root = root;
    Enumeration enumeration = policy.listPolicyConfigurations();
    while (enumeration.hasMoreElements()) {
      String config = (String) enumeration.nextElement();
      initializeConfiguration(policy, config);
    }
  }

  private void initializeConfiguration(PolicyDescriptor policy, String configuration) {
    SecurityContext sCtx = root.getPolicyConfigurationContext(configuration);
    if (sCtx == null) {
      while (true) {
        try {
          byte type = parseType(policy.getPolicyConfigurationType(configuration));
          root.registerPolicyConfiguration(configuration, type);
          sCtx = root.getPolicyConfigurationContext(configuration);
          break;
        } catch (StorageLockedException sle) {
          synchronized(this) {
            try {
              wait(100);
            } catch (InterruptedException ex) {
              break;
            }
          }
        }
      }
    }

    ModificationContext modifications = sCtx.getModificationContext();
    modifications.beginModifications();

    try {
      String userstore = policy.getUserStore();
      AuthenticationDescriptor authentication = policy.getAuthenticationDescriptor(configuration);
      AuthorizationDescriptor  authorization  = policy.getAuthorizationDescriptor(configuration);

      if (authentication != null) {
        AuthenticationContext authCtx = sCtx.getAuthenticationContext();
        if (authentication.getTemplate() != null) {
          authCtx.setLoginModules(authentication.getTemplate());
        } else {
          authCtx.setLoginModules(userstore, authentication.listAppConfigurationEntries());
        }
      }

      if (authorization != null) {
        AuthorizationContext authzCtx = sCtx.getAuthorizationContext();
        SecurityRoleContext srCtx = authzCtx.getSecurityRoleContext(userstore);
        String[] roles = authorization.listSecurityRoles();
        for (int i = 0; i < roles.length; i++) {
          SecurityRole role = srCtx.getSecurityRole(roles[i]);
          if (role == null) {
            role = srCtx.addSecurityRole(roles[i]);
          }
          String[] users = authorization.listUsersInRole(roles[i]);
          for (int j = 0; j < users.length; j++) {
            role.addUser(users[j]);
          }

          String[] groups = authorization.listGroupsInRole(roles[i]);
          for (int j = 0; j < groups.length; j++) {
            role.addGroup(groups[j]);
          }
        }
      }

      modifications.commitModifications();
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot initialize " + configuration + " policy configuration.", e);
    }

    try {
      AuthenticationContext authCtx = sCtx.getAuthenticationContext();
      AuthorizationContext authzCtx = sCtx.getAuthorizationContext();
      ((com.sap.engine.services.security.server.AuthorizationContextImpl) authzCtx).update();
      ((com.sap.engine.services.security.server.AuthenticationContextImpl) authCtx).update();
    } catch (Exception e) {
      throw new SecurityException("Cannot update authentication and authorization contexts of " + configuration + " policy configuration.", e);
    }
  }
  
  private byte parseType(String type) {
    if (type.equalsIgnoreCase(TEMPLATE)) {
      return SecurityContext.TYPE_TEMPLATE;
    } else if (type.equalsIgnoreCase(SERVICE)) {
      return SecurityContext.TYPE_SERVICE;
    } if (type.equalsIgnoreCase(WEB_COMPONENT)) {
      return SecurityContext.TYPE_WEB_COMPONENT;
    } else if (type.equalsIgnoreCase(EJB_COMPONENT)) {
      return SecurityContext.TYPE_EJB_COMPONENT;
    } else if (type.equalsIgnoreCase(CUSTOM)) {
      return SecurityContext.TYPE_CUSTOM;
    } else if (type.equalsIgnoreCase(OTHER)) {
      return SecurityContext.TYPE_OTHER;
    } else {
      throw new SecurityException("Invalid policy configuration type: " + type);
    }
  }
}