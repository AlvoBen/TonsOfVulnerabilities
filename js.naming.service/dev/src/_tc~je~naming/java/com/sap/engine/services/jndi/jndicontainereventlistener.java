/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi;

import java.util.Hashtable;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.interfaces.connector.ComponentExecutionContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.jndi.cache.CacheCommunicatorImpl;
import com.sap.engine.services.jndi.cluster.SecurityBase;
import com.sap.engine.services.jndi.itsam.SAP_ITSAMJNDIRegistry;
import com.sap.engine.services.jndi.itsam.SAP_ITSAMJNDIRegistryWrapper;
import com.sap.engine.services.jndi.itsam.SAP_ITSAMJNDIRegistry_Impl;
import com.sap.engine.services.jndi.shellcmd.JNDIAttrServ;
import com.sap.engine.services.jndi.shellcmd.JNDIBindServ;
import com.sap.engine.services.jndi.shellcmd.JNDICdServ;
import com.sap.engine.services.jndi.shellcmd.JNDIDelTreeServ;
import com.sap.engine.services.jndi.shellcmd.JNDIFindServ;
import com.sap.engine.services.jndi.shellcmd.JNDILookupServ;
import com.sap.engine.services.jndi.shellcmd.JNDIRLookupServ;
import com.sap.engine.services.jndi.shellcmd.JNDILsServ;
import com.sap.engine.services.jndi.shellcmd.JNDILsecServ;
import com.sap.engine.services.jndi.shellcmd.JNDIMAttrServ;
import com.sap.engine.services.jndi.shellcmd.JNDIMEnvServ;
import com.sap.engine.services.jndi.shellcmd.JNDIMSubServ;
import com.sap.engine.services.jndi.shellcmd.JNDIPwdServ;
import com.sap.engine.services.jndi.shellcmd.JNDIRSubServ;
import com.sap.engine.services.jndi.shellcmd.JNDIRebindServ;
import com.sap.engine.services.jndi.shellcmd.JNDISearchServ;
import com.sap.engine.services.jndi.shellcmd.JNDIUnbindServ;
import com.sap.engine.services.jndi.shellcmd.ListNamingServ;
import com.sap.engine.services.jndi.shellcmd.PrintNamingServ;
import com.sap.jmx.ObjectNameFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * JNDI Container Event Listener
 *
 * @author Elitsa Pancheva
 * @version 6.30
 */
public class JNDIContainerEventListener extends ContainerEventListenerAdapter {

  private final static Location LOG_LOCATION = Location.getLocation(JNDIContainerEventListener.class);

  static final String JMX_SERVICE = "jmx";
  static final String BASICADMIN_SERVICE = "basicadmin";
  static final String SHELL_INTERFACE = "shell";
  static final String SECURITY_INTERFACE = "security";
  static final String APPCONTEXT_INTERFACE = "appcontext";
  private static final String UNABLE_TO_REGISTER_ITSAM_MODEL_PROVIDER_MBEAN = "Unable to register model provider mbean for ITSAM Manager";
  private static final String ITSAM_MODEL_PROVIDER_MBEAN_ALREADY_REGISTERED = "The model provider mbean for ITSAM Manager is already registered";
  private static final String ERROR_UNREGISTERING_ITSAM_MODEL_PROVIDER_MBEAN = "Error occured when attempting to unregister the model provider mbean for ITSAM Manager";
  private static final String DOMAIN = "com.sap.default";

  private static final Location location = Location.getLocation(JNDIContainerEventListener.class);
  /**
   * Used to unregister the JNDI's shell commands
   */
  private int shellCommandsId;
  /**
   * Shell Interface
   */
  private ShellInterface shell = null;
  private String cluster;
  private String clusterNode;

  /**
   * object name for itsam cim mbean
   */
  private ObjectName _objNameITSAM;

  /**
   * Constructor
   *
   * @param p properties to use
   */
  public JNDIContainerEventListener(Properties p) {
    this.cluster = CacheCommunicatorImpl.clContext.getClusterMonitor().getClusterName();
    this.clusterNode = String.valueOf(CacheCommunicatorImpl.clContext.getClusterMonitor().getCurrentParticipant().getClusterId());
  }

  public void serviceStarted(String serviceName, Object serviceInterface) {
    if (location.bePath()) {
      location.pathT("servicestated event is received for service [" + serviceName + "], service object is [" + serviceInterface + "]");
    }

    if (serviceName.equals(BASICADMIN_SERVICE)) {
      registerITSAMModelProviderMBean();
    }
  }

  public void beginServiceStop(String serviceName) {
    if (location.bePath()) {
      location.pathT("beginServiceStop event is received for service [" + serviceName + "]");
    }

    if (serviceName.equals(JMX_SERVICE)) {
      unregisterITSAMModelProviderMBean();
    }
  }


  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (location.bePath()) {
      location.pathT("interfaceAvailable event is received for interface [" + interfaceName + "], interface object is [" + interfaceImpl + "]");
    }

    if (interfaceName.equals(SHELL_INTERFACE)) {
      shell = (ShellInterface) interfaceImpl;
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for registration of interface shell received.");
      }
      setCommands();
    } else if (interfaceName.equals(APPCONTEXT_INTERFACE)) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for registration of interface appcontext received.");
      }
      JNDIFrame.setAppContextProvider((ComponentExecutionContext) interfaceImpl);
    } else if (interfaceName.equals(SECURITY_INTERFACE)) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for registration of interface security received.");
      }
      try {
        JNDIFrame.initializeSecurity();
      } catch (ServiceException e) {
        // did not succeed - continuing w/o security
        if (LOG_LOCATION.beWarning()) {
          SimpleLogger.traceThrowable(Severity.WARNING,LOG_LOCATION,e, "ASJ.jndi.000024", "Failed to enable security checks for the JNDI Registry Service. Result: there will be no authentication and authorization checks for any client for this server process[{0}]", new Object[] { clusterNode});
        }

        JNDIFrame.loginContext = null;
        SecurityBase.WITHOUT_SECURITY = true;
      }
    }
  }

  public void interfaceNotAvailable(String interfaceName) {
    if (location.bePath()) {
      location.pathT("interfaceNotAvailable event is received for interface [" + interfaceName + "]");
    }

    if (interfaceName.equals(SHELL_INTERFACE)) {
      // Check if the shell was already obtained
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for interface shell not available received.");
      }
      if (shell != null) {
        shell.unregisterCommands(shellCommandsId);
        shell = null;
      }
    } else if (interfaceName.equals(APPCONTEXT_INTERFACE)) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for interface appcontext not available received.");
      }
      JNDIFrame.setAppContextProvider(null);
    } else if (interfaceName.equals(SECURITY_INTERFACE)) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Event for interface security not available received.");
      }
      JNDIFrame.loginContext = null;
      SecurityBase.WITHOUT_SECURITY = true;
    }
  }

  //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= //
  /**
   * Adds JNDI's shell commands
   */
  private void setCommands() {
        Command cmds[] = {new JNDIBindServ(), new JNDILookupServ(), new JNDIMSubServ(),
                new JNDIRebindServ(), new JNDIRSubServ(), new JNDIUnbindServ(),
                new ListNamingServ(), new PrintNamingServ(), new JNDICdServ(),
                new JNDIPwdServ(), new JNDILsServ(), new JNDIAttrServ(),
                new JNDIMAttrServ(), new JNDIMEnvServ(), new JNDISearchServ(),
                new JNDIFindServ(), new JNDIDelTreeServ(), new JNDILsecServ(),
                new JNDIRLookupServ()};
    shellCommandsId = shell.registerCommands(cmds);
  }

  public void unregisterResources() {
    // Check if the shell was already obtained
    if (shell != null) {
      shell.unregisterCommands(shellCommandsId);
    }

    JNDIFrame.setAppContextProvider(null);

  }

  private ObjectName getProviderMBeanObjectName() throws MalformedObjectNameException {
    if (_objNameITSAM == null) {
      String simClass = "SAP_ITSAMJNDIRegistry";
      String simParentClass = "SAP_ITSAMJ2eeCluster";

      String clusterNameKey = simParentClass + ".Name";
      String clusterCreationClassKey = simParentClass + ".CreationClassName";

      ManagementModelManager mmm = (ManagementModelManager) JNDIFrame.getContainerContext().getObjectRegistry().getServiceInterface(BASICADMIN_SERVICE);

      ObjectName onCluster = mmm.getManagementModelHelper().getSAP_ITSAMJ2eeClusterObjectName();

      String clusterNameValue = onCluster.getKeyProperty(clusterNameKey);
      String clusterCreationClassValue = onCluster.getKeyProperty(clusterCreationClassKey);

      Hashtable objectNameTable = new Hashtable();
      objectNameTable.put("cimclass", simClass);
      objectNameTable.put("version", "1.0");
      objectNameTable.put("type", simParentClass + "." + simClass);
      objectNameTable.put(clusterNameKey, clusterNameValue);
      objectNameTable.put(clusterCreationClassKey, clusterCreationClassValue);
      objectNameTable.put(simClass + ".ElementName", simClass);
      objectNameTable.put(ObjectNameFactory.NAME_KEY, SAP_ITSAMJNDIRegistry.MBEAN_NAME);
      objectNameTable.put(ObjectNameFactory.J2EETYPE_KEY, "JNDIResource");
      objectNameTable.put(ObjectNameFactory.J2EEServer, cluster);
      objectNameTable.put(ObjectNameFactory.SAP_J2EEClusterNode, clusterNode);
      objectNameTable.put(ObjectNameFactory.SAP_J2EECluster, cluster);
      _objNameITSAM = new ObjectName("", objectNameTable);
    }
    return _objNameITSAM;
  }

  private void registerITSAMModelProviderMBean() {
    String method = "registerITSAMModelProviderMBean";
    location.entering(method);
    try {
      MBeanServer mbs = (MBeanServer) new InitialContext().lookup(JMX_SERVICE);
      ObjectName objectName = getProviderMBeanObjectName();
      if (!mbs.isRegistered(objectName)) {
        SAP_ITSAMJNDIRegistry_Impl impl = new SAP_ITSAMJNDIRegistry_Impl(objectName.getCanonicalName());
        SAP_ITSAMJNDIRegistryWrapper wrapper = new SAP_ITSAMJNDIRegistryWrapper(impl);
        mbs.registerMBean(wrapper, objectName);
      } else {
        location.debugT(ITSAM_MODEL_PROVIDER_MBEAN_ALREADY_REGISTERED);
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.DEBUG, UNABLE_TO_REGISTER_ITSAM_MODEL_PROVIDER_MBEAN, e);
    }
    location.exiting(method);
  }

  private void unregisterITSAMModelProviderMBean() {
    String method = "unregisterITSAMModelProviderMBean";
    location.entering(method);
    try {
      MBeanServer mbs = (MBeanServer) new InitialContext().lookup(JMX_SERVICE);
      ObjectName objectName = getProviderMBeanObjectName();
      if (mbs.isRegistered(objectName)) {
        mbs.unregisterMBean(objectName);
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.DEBUG, ERROR_UNREGISTERING_ITSAM_MODEL_PROVIDER_MBEAN, e);
    }
    location.exiting(method);
  }
}

