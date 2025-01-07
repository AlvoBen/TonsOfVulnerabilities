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
package com.sap.engine.services.security.userstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.remote.RemoteUserStoreFactory;
import com.sap.engine.services.security.server.UserStoreFactoryCache;
import com.sap.engine.services.security.server.UserStoreFactoryImpl;
import com.sap.engine.services.security.userstore.descriptor.DefaultUserStoreConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class UserStoreService {
  
  private static final Location LOCATION = Location.getLocation(UserStoreService.class);
  
  private UserStoreFactoryCache cache;
  private UserStoreFactoryImpl factory;
  private RemoteUserStoreFactory remote;
  private boolean initialized = false;

  public UserStoreService(SecurityContext root) throws ServiceException {
    InputStream in = null;
    try {
      cache = new UserStoreFactoryCache();
      factory = new UserStoreFactoryImpl(cache);
      initialized = factory.isInitialized();
      if (!initialized) {
        String xmldata = SecurityServerFrame.getServiceProperties().getProperty("userstore.xml");
        if (xmldata == null) {
          SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000100", "Cannot initialize list of user stores because of missing <userstore.xml> from Security Provider service properties.");
          return;
        } 
        while (xmldata.endsWith("=")) {
          xmldata = new String(Base64.decode(xmldata.getBytes()));
        }
        in = new ByteArrayInputStream(xmldata.getBytes());
        try {
          parse(in);
        } catch (Exception e) {
          SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000101", "Cannot initialize list of user stores because <userstore.xml> from Security Provider service properties is bad formatted.");
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000101", "Cannot initialize list of user stores because <userstore.xml> from Security Provider service properties is bad formatted.", e);
          return;
        }
      }
      remote = new RemoteUserStoreFactoryImpl(factory);
    } catch (Throwable e) {
      SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000102", "Cannot initialize list of user stores.");
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000102", "Cannot initialize list of user stores.", e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          LOCATION.traceThrowableT(Severity.INFO, "UserStoreService error", e);
        }
      }
    }
  }

  public void initializePolicy(SecurityContext sc) throws ServiceException {
    if (!initialized) {
      try {
        new com.sap.engine.services.security.userstore.policy.PolicyInitializer().initialize(sc);
      } catch (ServiceException se) {
        throw se;
      } catch (Throwable e) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot initialize PolicyInitializer", e);
      }
    }
  }

  public UserStoreFactory getUserStoreFactory() {
    return factory;
  }

  public RemoteUserStoreFactory getRemoteUserStoreFactory() {
    return remote;
  }

  private void parse(InputStream in) throws Exception {
    SystemProperties.setProperty(StandardDOMParser.INQMY_PARSER, "yes");
    ClassLoader loader = this.getClass().getClassLoader();
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
            DefaultUserStoreConfiguration config = new DefaultUserStoreConfiguration(storesList.item(i));
            logUserStoreConfiguration(config);
            factory.registerUserStore(config, loader);

            if (config.isActive()) {
              factory.setActiveUserStore(config.getName());
            }
          } catch (Exception e) {
            SimpleLogger.log(Severity.ERROR, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000103", "Cannot register user store described in <userstore.xml> from Security Provider service properties.");
            SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000103", "Cannot register user store described in <userstore.xml> from Security Provider service properties.", e);
          }
        }
      }
    }
  }

  private void logUserStoreConfiguration(DefaultUserStoreConfiguration config)  {
    LOCATION.logT(Severity.INFO, "UserStoreConfiguration " + config.getName());
    LOCATION.logT(Severity.INFO, "UserContextSpi " + config.getUserSpiClassName());
    LOCATION.logT(Severity.INFO, "GroupContextSpi " + config.getGroupSpiClassName());
    LOCATION.logT(Severity.INFO, "IsActive " + config.isActive());
    LoginModuleConfiguration[] loginModules = config.getLoginModules();
    for (int i = 0; i < loginModules.length; i++) {
      LOCATION.logT(Severity.INFO, "LoginModuleConfiguration " + loginModules[i].getName());
      LOCATION.logT(Severity.INFO, "LoginModuleClassName " + loginModules[i].getLoginModuleClassName());
    }
  }
}

