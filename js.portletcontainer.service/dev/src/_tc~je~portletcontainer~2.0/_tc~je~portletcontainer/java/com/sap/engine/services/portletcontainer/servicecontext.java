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
package com.sap.engine.services.portletcontainer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sap.bc.proj.jstartup.JStartupFramework;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.processor.SchemaProcessorFactory;
import com.sap.engine.services.portletcontainer.container.PortletAdmin;
import com.sap.engine.services.portletcontainer.container.PortletContainer;
import com.sap.engine.services.portletcontainer.container.PortletContainerExtension;

/**
 * Keeps the <code>ApplicationServiceContext</code> and the <code>ThreadSystem
 * </code> objects.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class ServiceContext {
  public static final String INSTANCE_NAME_KEY = "SAPMYNAME";
  public static final String SAPSTART = "SAPSTART";

  private static ServiceContext activeServiceContext = null;

  private PortletContainer portletContainer = null;
  private PortletAdmin portletAdmin = null;
  private ApplicationServiceContext applicationServiceContext = null;
  private ThreadSystem threadSystem = null;
  private ClassLoader serviceLoader = Thread.currentThread().getContextClassLoader();

  private String fullVersion = null;
  private byte[] version = null;
  private String instanceName = null;

  private PortletContainerExtension portletContainerExtension = null;
  private SchemaProcessor portletSchemaProcessor = null;
  private SAXParserFactory factory = null;

  /**
   * Creates new <code>ServiceContext</code> object.
   * @param applicationServiceContext the <code>ApplicationServiceContext</code>
   * object with which the Portlet Container service is created.
   */
  public ServiceContext(ApplicationServiceContext applicationServiceContext) {
    this.applicationServiceContext = applicationServiceContext;
    this.threadSystem = applicationServiceContext.getCoreContext().getThreadSystem();
    activeServiceContext = this;
    portletContainer = new PortletContainer(this);
    portletAdmin = new PortletAdmin();
    initVersion(applicationServiceContext.getCoreContext().getCoreMonitor());
  }

  /**
   * Returns the <code>ApplicationServiceContext</code> context.
   * @return the <code>ApplicationServiceContext</code> context.
   */
  public ApplicationServiceContext getApplicationServiceContext() {
    return applicationServiceContext;
  }

  /**
   * Returns this context.
   * @return this context.
   */
  public static ServiceContext getServiceContext() {
    return activeServiceContext;
  }

  /**
   * Returns the <code>ThreadSystem</code> object that can be used for starting
   * new application threads.
   * @return the <code>ThreadSystem</code> object.
   */
  public ThreadSystem getThreadSystem() {
    return threadSystem;
  }

  /**
   * Sets the <code>PortletConatinerExtension</code> for which this service context
   * was created.
   * @param portletContainerExtension the <code>PortletContainerExtension</code>
   * to be set.
   */
  public void setPortletContainerExteinsion(PortletContainerExtension portletContainerExtension) {
    this.portletContainerExtension = portletContainerExtension;
  }

  /**
   * Returns the <code>PortletContainerExtension</code> object.
   * @return the <code>PortletContainerExtension</code> object.
   */
  public PortletContainerExtension getPortletContainerExtension() {
    return portletContainerExtension;
  }

  /**
   * Returns the application class loader.
   * @return the application class loader.
   */
  public ClassLoader getServiceLoader() {
    return serviceLoader;
  }

  /**
   * Returns a reference to the implementation of the IPortletNode interface.
   * @return a reference to the implementation of the IPortletNode interface.
   */
  public PortletContainer getPortletContainer() {
    return portletContainer;
  }

  /**
   * Returns a reference to the implementation of the PortletAdmin interface.
   * @return a reference to the implementation of the PortletAdmin interface.
   */
  public PortletAdmin getPortletAdmin() {
    return portletAdmin;
  }

  /**
   * Returns the name and version of the Portlet Container.
   * @return the name and version of the Portlet Container.
   */
  public String getFullServerVersion() {
    return fullVersion;
  }

  /**
   * Inits the name and version of the Portlet Container.
   * @param ver
   * @param majorVer
   */
  private void initVersion(CoreMonitor coreMonitor) {
    String serverVersion = JStartupFramework.getParam("is/server_name") + " " +
    JStartupFramework.getParam("is/server_version");
    fullVersion = serverVersion + " / AS Java Portlet Container " +
      coreMonitor.getCoreVersion();
    version = (serverVersion + " / AS Java Portlet Container/" + coreMonitor.getCoreMajorVersion() +
      "." + coreMonitor.getCoreMinorVersion()).getBytes();
    String sapstart = SystemProperties.getProperty(SAPSTART);
    if ((sapstart != null) && sapstart.equals("1")) {
      instanceName = SystemProperties.getProperty(INSTANCE_NAME_KEY);
      if ((instanceName != null) && instanceName.equalsIgnoreCase("null")) {
        instanceName = null;
      }
    } else {
      instanceName = null;
    }
  }

  private void initParserFactory() {
    try {
      factory = (SAXParserFactory) Class.forName(
          "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl")
          .newInstance();
    } catch (Throwable ignore) {
      // $JL-EXC$
    }
    if (factory == null) {
      try {
        factory = (SAXParserFactory) Class.forName(
            "com.sap.engine.lib.jaxp.SAXParserFactoryImpl").newInstance();
        //sapXmlToolkitSAXParser = true;
      } catch (Throwable ignore) {
        // $JL-EXC$
      }
    }
    if (factory == null) {
      factory = SAXParserFactory.newInstance();
    }
  }

  public SAXParserFactory getSaxParserFactory() {
    if (factory == null) {
      initParserFactory();
    }
    return factory;
  }
  
  /**
   * Load  schema processor
   */
  private void initSchemaProcessor() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    try {
      portletSchemaProcessor = SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.PORTLET2);
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * Returns the portletSchemaProcessor.
   * @return the portletSchemaProcessor.
   */
  public SchemaProcessor getPortletSchemaProcessor() {
    if (portletSchemaProcessor == null) {
      initSchemaProcessor();
    }
    return portletSchemaProcessor;
  }
}
