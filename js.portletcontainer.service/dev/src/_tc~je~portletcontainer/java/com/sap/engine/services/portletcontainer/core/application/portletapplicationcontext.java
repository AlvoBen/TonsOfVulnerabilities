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
package com.sap.engine.services.portletcontainer.core.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.PortletContainerServiceFrame;
import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.container.descriptor.DescriptorHandler;
import com.sap.engine.services.portletcontainer.container.descriptor.PortletDeploymentDescriptor;
import com.sap.engine.services.portletcontainer.core.PortletContextImpl;
import com.sap.engine.services.portletcontainer.exceptions.WCEDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModuleContext;
import com.sap.tc.logging.Location;

/**
 * Defines a set of methods that a portlet uses to communicate with its portlet
 * container.
 * 
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletApplicationContext {
  
  private Location currentLocation = Location.getLocation(getClass());
  //portlet alias
  private String portletModuleName = null;
  //portlet application name
  private String portletApplicationName = null;
  private boolean isDefault = false;
  private IWebModuleContext webModuleContext = null;
  private PortletApplicationConfig portletApplicationConfig = null;
  private PortletContext portletContext = null;
  private ServletContext servletContext = null;
  private ClassLoader applicationClassLoader = null;
  private PortletApplicationComponents portletComponents = null;
  /**
   * if application is stopping all current requests have to be processed
   */
  private boolean isDestroying = false;
  /**
   *  Counts all request to this portlet application
   */
  private long currentRequests = 0;
  /**
   *  object used for synhronization while counting requests
   */
  private Object requestsMonitor = new Object();
  private Object synchObject = new Object();
  private Vector warnings = new Vector();
  
  private String displayName = ""; 
  
  public PortletApplicationContext(String portletModuleName, IWebModuleContext webModuleContext) {
    this.portletModuleName = portletModuleName;
    this.portletApplicationName = webModuleContext.getWebModule().getApplicationName();
    this.webModuleContext = webModuleContext;
    isDefault = portletModuleName.equals("/");
    servletContext = webModuleContext.getServletContext();
    portletApplicationConfig = new PortletApplicationConfig(this);
    applicationClassLoader = webModuleContext.getPublicClassLoader(); 
    portletContext = new PortletContextImpl(servletContext, portletApplicationConfig, this);
  }

  public PortletApplicationConfig getPortletApplicationConfig() {
    return portletApplicationConfig;
  }

  /**
   * 
   */
  public void init(PortletDeploymentDescriptor portletPP) throws WebContainerExtensionDeploymentException {
    if (applicationClassLoader == null) {
      throw new WCEDeploymentException(WCEDeploymentException.APPLICATION_CLASSLOADER_FOR_APPLICATION_IS_NULL, new Object[]{portletModuleName});
    }
    portletComponents = new PortletApplicationComponents(portletModuleName, this, applicationClassLoader);
    portletApplicationConfig.init(portletPP);
    portletComponents.init(portletApplicationConfig);
    displayName = extractDispalyName();   
  }
  
  public void destroy() {
    applicationClassLoader = null;
    // TODO 
    
  }

  /**
   * @return the portlet module name (the alias)
   */
  public String getPortletModuleName() {
    return portletModuleName;
  }

  /**
   * 
   * @return the portlet application name
   */
  public String getPortletApplicationName() {
    return portletApplicationName;  
  }
  
  public String getPortletAlias() {
    return convertAlias(portletModuleName);  
  }
  
  /**
   * 
   * @return true if the the portlet application is rooted at the base of the web server URL namespace
   */
  public boolean isDefault() {
    return isDefault;  
  }
  
  /**
   * @return the application class loader.
   */
  public ClassLoader getClassLoader() {
    return applicationClassLoader;
  }

  /**
   * @param isDestroying
   */
  public void setDestroyingMode(boolean isDestroying) {
    this.isDestroying = isDestroying;
  }

  /**
   * 
   * @return 
   */
  public boolean isDestroying() {
    return isDestroying;
  }
  
  public long getAllCurrentRequests() { 
    return currentRequests;
  }

  public Object getSynchObject() {
    return synchObject;
  }

  public Vector getWarnings() {
    return warnings;
  }
  
  public PortletApplicationComponents getPortletComponents() {
    return portletComponents;
  }
  
  public PortletContext getPortletContext() {
    return portletContext;
  }

  public void addRequest() {
    synchronized (requestsMonitor) {
     currentRequests++;
    }
  }

  public void removeRequest() {
    synchronized (requestsMonitor) {
      currentRequests--;
    }
  }
  
  public String getDisplayName() {
    return displayName;
  }
  
  private String convertAlias(String alias) {
    if (alias.equals("/") || alias.equals("\\")) {
      return "/";
    }
    char separatorChar = '/';
    return alias.replace('/', separatorChar).replace('\\', separatorChar);
  }

  private String extractDispalyName() throws WebContainerExtensionDeploymentException {
    String name = null;
    SAXParser parser = null;
    InputStream inputStream = null;
    try {
      parser = ServiceContext.getServiceContext().getSaxParserFactory().newSAXParser();
      DescriptorHandler eventHandler = new DescriptorHandler();
      inputStream = webModuleContext.getWebModule().getDescriptor(PortletContainerServiceFrame.WEB_DEPLOYMENT_DESCRIPTOR_NAME).getInputStream();
      parser.parse(inputStream, eventHandler);
      name = eventHandler.getDisplayName();
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000036", "Error in reading web.xml file for application [{0}].", new Object[]{portletApplicationName}, e, null, null);
        throw new WCEDeploymentException(WCEDeploymentException.ERROR_READING_WEB_XML, new Object[]{portletApplicationName}, e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException io) {
          LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000037", "Cannot close web.xml file for application [{0}].", new Object[]{portletApplicationName}, io, null, null);
        }
      }
    }

    return name;
  }
}
