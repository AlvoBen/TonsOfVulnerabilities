/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;

import com.sap.engine.lib.descriptors.portlet.ExpirationCacheType;
import com.sap.engine.lib.descriptors.portlet.MimeTypeType;
import com.sap.engine.lib.descriptors.portlet.PortletModeType;
import com.sap.engine.lib.descriptors.portlet.PortletPreferencesType;
import com.sap.engine.lib.descriptors.portlet.PortletType;
import com.sap.engine.lib.descriptors.portlet.SupportsType;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.PortletContainerServiceFrame;
import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.api.IPortletAdmin;
import com.sap.engine.services.portletcontainer.container.descriptor.PortletDeploymentDescriptor;
import com.sap.engine.services.portletcontainer.core.PortletPreferencesImpl;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationConfig;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.engine.services.portletcontainer.exceptions.WCEDeploymentException;
import com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtensionContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IModuleDescriptor;
import com.sap.engine.services.servlets_jsp.webcontainer_api.module.IWebModule;
import com.sap.tc.logging.Location;

/**
 * Implementation of <code>IPortletAdmin</code> interface.
 * 
 * @version 7.20
 * @author vera-b
 */
public class PortletAdmin implements IPortletAdmin {
  private Location currentLocation = Location.getLocation(getClass());
  private IWebContainerExtensionContext ctx = null;
  
  /**
   * Constructs new <code>PortletAdmin</code> object.
   */
  public PortletAdmin() {
   
  }

  /**
   * Returns a list of all vendor names of the j2ee applications that contains portlet web modules.
   * 
   * @return a list of all vendor names of the j2ee applications that contains portlet web modules.
   */
  public String[] getVendors() {
  	if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug("Get all portlet applications' vendors.");
    }
    if (ctx == null) {
      initContext();
    }
    String [] myPortletModules = ctx.getMyWebModules();
    Set result = new HashSet();
    for (int i = 0; i < myPortletModules.length; i++) {
      IWebModule webModule = ctx.getWebModule(myPortletModules[i]);
      result.add(webModule.getVendor());
    }
    String[] resultArr = new String[result.size()]; 
    resultArr = (String[]) result.toArray(resultArr);
    if (currentLocation.beDebug()) {
    	LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug("Get all portlet applications' vendors=[" + resultArr + "]");
    }
    return resultArr;
  }

  /**
   * Return a list of j2ee applications that contains portlet web modules.
   * @param vendor the applications' vendor.
   * @return a list of j2ee applications that contains portlet web modules.
   */
  public String[] getPortletApplicationNames(String vendor) {
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
          "Get portlet j2ee application names for vendor=[" + vendor + "]");
    }
    if (vendor == null) {
      return new String[0];
    }
    if (ctx == null) {
      initContext();
    }
    String [] myPortletModules = ctx.getMyWebModules();
    Set result = new HashSet();
    for (int i = 0; i < myPortletModules.length; i++) {
      IWebModule webModule = ctx.getWebModule(myPortletModules[i]);
      if (vendor.equals(webModule.getVendor())) {
        result.add(webModule.getApplicationName());
      }
    }
    String[] resultArr = new String[result.size()]; 
    resultArr = (String[]) result.toArray(resultArr);
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
        "Get portlet vendors=[" + resultArr + "]");
  }
    return resultArr;
  }

  /**
   * Returns a list of all portlet-web-modules names, which belong to the specified 
   * portlet-j2ee-application. If the portlet application name is the full application
   * name i.e. "vendor"/"application name" then the portlet-web-modules names 
   * corresponding to this application name and vendor will be returned.
   * If only the portlet application name (without vendor information) is specified and
   * there are deployed applications with one and the same application name,
   * but with different vendors then IllegalStateException will be thrown,
   * else the portlet-web-modules names corresponding to the specified portlet 
   * application name will be returned.
   *
   * @param porteltApplicationName the name of the portlet-j2ee-application.
   * @return a list of all portlet-web-modules that are within the specified portlet-j2ee-application.
   * @throws IllegalStateException if the paramter is application name without a specified vendor
   * 	and there are more than one deployed applications with the same name and different vendors.
   */
  public String[] getPortletModuleNames(String porteltApplicationName) {
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
          "Get portlet module names for j2ee application with porteltApplicationName=[" + porteltApplicationName + "]");
    }
    if (porteltApplicationName == null) {
      return new String[0];
    }
    if (ctx == null) {
      initContext();
    }
    String[] result = null;
    try {
			result = ctx.getMyWebModules(porteltApplicationName);
		} catch (IllegalStateException e) {
			LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceError("ASJ.portlet.000003", "Failed to get portlet module names for j2ee application with porteltApplicationName=[{0}].", new Object[]{porteltApplicationName}, e, null, null);
			throw e;
		}
    return result; 
  }  
  
  /**
   * Returns a list of all portlets in the specified portlet-web-module.
   * @param portletModuleName the name of the portlet-web-module.
   * @return a list of all portlets in the portlet-web-module.
   */
  public String[] getAllPortlets(String portletModuleName) {
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
          "Get all portlets for web application with alias portletModuleName=[" + portletModuleName + "]");
    }
    if (portletModuleName == null) {
      return new String[0];
    }
    if (ctx == null) {
	    initContext();
    }
    String[] result = null;    
    IWebModule webModule = ctx.getWebModule(portletModuleName);
    boolean appStarted = false;
    
    if (webModule == null) {
      return new String[0];
    }
    
    //the name to be passed to WCE should contain vendor and web module name
    String fullAppName = webModule.getVendor() + "/" + webModule.getApplicationName();
    PortletApplicationContext ctx = 
      ServiceContext.getServiceContext().getPortletContainerExtension().getPortletApplicationContext(portletModuleName);
    appStarted = (ctx != null);
    
    if (appStarted) {
      PortletApplicationConfig appCfg = ctx.getPortletApplicationConfig();
      result = appCfg.getPortletsNames();
    } else {
      //application not started, load from descriptor: (slow)
      try {        
        PortletDeploymentDescriptor desc = getDeploymentDescriptor(webModule);
        PortletType[] allPortlets = desc.getPortletApp().getPortlet();
        result = new String[allPortlets.length];
        for (int i = 0; i < allPortlets.length; i++) {
          result[i] = allPortlets[i].getPortletName().get_value();
        }
      } catch (WCEDeploymentException e) {
        if (currentLocation.beDebug()) {
          LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
              "Unable to get deployment descriptor for web application with alias portletModuleName=[" + portletModuleName + 
              "] for portlet-web-application with fullAppName=[" + fullAppName + "] : " + e);
        }
        result = new String[0];
      }
    }
    
    return result;
  }

  /**
   * Returns new object of the portlet initial preferences loaded from Deployment Descriptor
   * @param portletName the name of the portlet which preferences to be returned.
   * @param portletModuleName the name of the portlet-web-application that contains the portlet.
   * @return the initial portlet preverences loaded from the Deployment Descriptor. If no 
   * preferences are specified an empty PortletPreferences object is returned. Returns 
   * <code>null</code> if an exception is thrown when processing the Deployment Descriptor.
   */
  public PortletPreferences getPortletPreferences(String portletName, String portletModuleName) {
    PortletPreferences result = new PortletPreferencesImpl(); 
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
          "Get preferences for portletName=[" + portletName + "] and web applicaton alias portletModuleName=[" + portletModuleName + "]");
    }
    if (portletName == null || portletModuleName == null) {
      return result;
    }
    if (ctx == null) {
	    initContext();
    }
     
    IWebModule webModule = ctx.getWebModule(portletModuleName);
    boolean appStarted = false;
    
    if (webModule == null) {
      return result;
    }
    
    String fullAppName = webModule.getVendor() + "/" + webModule.getApplicationName();
    PortletApplicationContext ctx = 
      ServiceContext.getServiceContext().getPortletContainerExtension().getPortletApplicationContext(portletModuleName);
    appStarted = (ctx != null);
    
    if (appStarted) {
      PortletApplicationConfig appCfg = ctx.getPortletApplicationConfig();
      result = appCfg.getPortletsPreferences(portletName);
    } else {
      //application not started, load from descriptor: (slow)
      try {        
        PortletDeploymentDescriptor desc = getDeploymentDescriptor(webModule);
        PortletType[] allPortlets = desc.getPortletApp().getPortlet();
        for (int i = 0; i < allPortlets.length; i++) {
          if (portletName.equals(allPortlets[i].getPortletName().get_value())) {
            PortletPreferencesType prefsType = allPortlets[i].getPortletPreferences();
            if (prefsType != null) {
              //Preference Valiator will be loaded at starttime and used at runtime
              result = new PortletPreferencesImpl(prefsType, null, portletName, portletModuleName);
            }
            break;
          }
        }
      } catch (WCEDeploymentException e) {
        if (currentLocation.beDebug()) {
          LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
              "Unable to get deployment descriptor for portletModuleName=[" + portletModuleName + 
              "] for application [" + fullAppName + "]: " + e);
        }
        result = null;
      }
    }
    
    return result;
  }

  /**
   * Returns the expiration cache timeout defined in the portlet deployment descriptor
   * for the specified portlet.
   * @param portletName the name of the portlet which expiration cache to be returned.
   * @param portletModuleName the name of the portlet-web-application that contains the portlet.
   * @return the expiration cache timeout. If expiration cache timeout value returned 
   * is 0, caching is disabled for the portlet, if the value returned is -1, 
   * the cache does not expire. If the value returned is <code>null</code> - portlet
   * has not defined expiration cache and if the expiration cache property in 
   * the <code>RenderResponse</code> is set, it must be ignored. 
   */
  public Integer getExpirationCache(String portletName, String portletModuleName) {
    Integer expirationCache = null; 
    if (currentLocation.beDebug()) {
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
          "Get expiration cache for portletName=[" + portletName + "] and web applicaton alias portletModuleName=[" + portletModuleName + "]");
    }
    if (portletName == null || portletModuleName == null) {
      return expirationCache;
    }
    if (ctx == null) {
	    initContext();
    }
     
    IWebModule webModule = ctx.getWebModule(portletModuleName);
    boolean appStarted = false;
    
    if (webModule == null) {
      return expirationCache;
    }
    
    String fullAppName = webModule.getVendor() + "/" + webModule.getApplicationName();
    PortletApplicationContext ctx = 
      ServiceContext.getServiceContext().getPortletContainerExtension().getPortletApplicationContext(portletModuleName);
    appStarted = (ctx != null);
    
    if (appStarted) {
      PortletApplicationConfig appCfg = ctx.getPortletApplicationConfig();
      expirationCache = appCfg.getExpirationCache(portletName);
    } else {
      //application not started, load from descriptor: (slow)
      try {        
        PortletDeploymentDescriptor desc = getDeploymentDescriptor(webModule);
        PortletType[] allPortlets = desc.getPortletApp().getPortlet();
        for (int i = 0; i < allPortlets.length; i++) {
          if (portletName.equals(allPortlets[i].getPortletName().get_value())) {
          	ExpirationCacheType expirationCacheType = allPortlets[i].getExpirationCache();
            if (expirationCacheType!= null) {
              expirationCache = new Integer(expirationCacheType.get_value());
            }
            break;
          }
        }
      } catch (WCEDeploymentException e) {
        if (currentLocation.beDebug()) {
          LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
              "Unable to get expiration cache for portletName=[" + portletName + 
              "] for application [" + fullAppName + "]: " + e);
        }
        expirationCache = null;
      }
    }
    
    return expirationCache;
  }

  /**
   * 
   */
  private void initContext() {
    PortletContainerExtension pce = ServiceContext.getServiceContext().getPortletContainerExtension();
    ctx = pce.getPortletContainerExtensionContext();
  }
  
  private PortletDeploymentDescriptor getDeploymentDescriptor(IWebModule webModule) throws WCEDeploymentException {
    //Parse portlet.xml in order get portlet deployment descriptor.
    PortletDeploymentDescriptor portletDD = new PortletDeploymentDescriptor();
    IModuleDescriptor moduleDescriptor = webModule.getDescriptor(PortletContainerServiceFrame.PORTLET_CONTAINER_DESCRIPTOR_NAME);
    InputStream inputStream = null;
    try {
      inputStream = moduleDescriptor.getInputStream();
      portletDD.loadDescriptorFromStream(inputStream, false);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000004", "Error in loading portlet.xml file of the portlet application [{0}].", new Object[]{webModule.getModuleName()}, e, null, null);
      throw new WCEDeploymentException(WCEDeploymentException.ERROR_LOADING_PORTLET_XML, new Object[]{webModule.getModuleName()}, e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException io) {
          LogContext.getCategory(LogContext.CATEGORY_SERVICE).logError(currentLocation, "ASJ.portlet.000005", "Cannot close portlet.xml file for application [{0}].", new Object[]{webModule.getModuleName()}, io, null, null);
        }
      }
    }
    return portletDD;
  }
  
  /**
   * Constructs a new java.util.Map object of mapped mime-types to arrays of 
   * PortletMode objects, as declared in the portlet's descriptor's <supports>
   * elements.
   * The map does not include the VIEW mode, if it is not declared in the descriptor.
   * Note that the VIEW mode is required to be always supported, 
   * even if it is not declared in the descriptor.
   * If there are no supported types declared for a given mime type, its
   * value in the map is an array with length 0.
   * @param portletName
   * @param moduleName
   * @return Map containing mime-types String objects as keys and arrays 
   *   of PortletMode as values, which the portlet declares to support for 
   *   the corresponding mime-type, including the VIEW mode 
   */
  public Map getSupportedModes(String portletName, String moduleName) {
	  Map result = new HashMap();
	  if (currentLocation.beDebug()) {
		  LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
				  "Get map of supported modes per mime-type for portletName=[" + 
				  portletName + "] and web applicaton alias portletModuleName=[" + moduleName + "]");
	  }
	  if (portletName == null || moduleName == null) {
	    return result;
	  }
	  if (ctx == null) {
	    initContext();
	  }
	  
	  IWebModule webModule = ctx.getWebModule(moduleName);
	  boolean appStarted = false;
	  if (webModule == null) {
	    return result;
	  }

	  String fullAppName = webModule.getVendor() + "/" + webModule.getApplicationName();
	  PortletApplicationContext ctx = 
        ServiceContext.getServiceContext().getPortletContainerExtension().getPortletApplicationContext(moduleName);
	  appStarted = (ctx != null);

	  if (appStarted) {
	    PortletApplicationConfig appCfg = ctx.getPortletApplicationConfig();
	    Map tmp = appCfg.getSupportedModesMap(portletName);
      for (Iterator it = tmp.keySet().iterator(); it.hasNext(); ) {
        String type = (String) it.next();
        List modesStr = (List) tmp.get(type);
        PortletMode[] modes = new PortletMode[modesStr.size()];
        modes = (PortletMode[]) modesStr.toArray(modes);
        result.put(type, modes);
      } 
	  } else {
	    //application not started, load from descriptor: (slow)
	    try {
	      PortletDeploymentDescriptor desc = getDeploymentDescriptor(webModule);
	      PortletType[] allPortlets = desc.getPortletApp().getPortlet();
	      for (int i = 0; i < allPortlets.length; i++) {
	        if (portletName.equals(allPortlets[i].getPortletName().get_value())) {
	          SupportsType[] supportsTypes = allPortlets[i].getSupports();
            for (int j = 0; supportsTypes != null && j < supportsTypes.length; j++) {
              MimeTypeType mimeType = supportsTypes[j].getMimeType();
              List modesList = new ArrayList();
              PortletModeType[] portletModes = supportsTypes[j].getPortletMode();
              for (int k = 0; portletModes != null && k < portletModes.length; k++) {
                if (portletModes[k].get_value() != null && !"".equals(portletModes[k].get_value().trim())) {
                  PortletMode current = new PortletMode(portletModes[k].get_value());
                  if (!modesList.contains(current)) {
                    modesList.add(current);
                  }
                }
              }
              PortletMode[] modes = new PortletMode[modesList.size()];
              modes = (PortletMode[]) modesList.toArray(modes);
              result.put(mimeType.get_value(), modes);
            }
            break;
	        }
	      }
  	  } catch (WCEDeploymentException e) {
  	    if (currentLocation.beDebug()) {
  	      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
  	          "Unable to get deployment descriptor for portletModuleName=[" + moduleName + 
  	          "] for application [" + fullAppName + "]: " + e);
  	    }
  	    result = null;
  	  }
    }
    return result;
  }
}
