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
package com.sap.engine.services.portletcontainer.api;

import java.util.Map;

import javax.portlet.PortletPreferences;

/**
* The <code>IPortletAdmin</code> interface allows the Portal application to get
* all the necessary information about deployed Portlet applications during the
* portlet administration phase.
* 
* @author Diyan Yordanov
* @version 7.10
*/
public interface IPortletAdmin {  
  
  /**
   * Returns a list of all vendor names of the applications that contains portlet modules.
   * 
   * @return a list of all vendor names of the applications that contains portlet modules.
   */
  public String [] getVendors();
  
  /**
   * Return a list of j2ee applications that contains portlet web modules.
   * @param vendor the applications' vendor.
   * @return a list of j2ee applications that contains portlet web modules.
   */
  public String [] getPortletApplicationNames(String vendor);
  
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
   */
  public String [] getPortletModuleNames(String porteltApplicationName);
  
  /**
   * Returns a list of all portlets in the specified portlet-web-module.
   * @param portletModuleName the name of the portlet-web-module.
   * @return a list of all portlets in the portlet-web-module.
   */
  public String [] getAllPortlets(String portletModuleName); 
  
  /**
   * Returns new object of the portlet initial preferences loaded from Deployment Descriptor
   * @param portletName the name of the portlet which preferences to be returned.
   * @param portletModuleName the name of the portlet-web-application that contains the portlet.
   * @return the initial portlet preverences loaded from the Deployment Descriptor.
   */
  public PortletPreferences getPortletPreferences(String portletName, String portletApplicationName); 
  
  /**
   * Returns the expiration cache timeout defined in the portlet Deployment Descriptor
   * for the specified portlet.
   * @param portletName the name of the portlet which expiration cache to be returned.
   * @param portletModuleName the name of the portlet-web-application that contains the portlet.
   * @return the expiration cache timeout. If expiration cache timeout value returned 
   * is 0, caching is disabled for the portlet, if the value returned is -1, 
   * the cache does not expire. If the value returned is <code>null</code> - portlet
   * has not defined expiration cache and if the expiration cache property in 
   * the <code>RenderResponse</code> is set, it must be ignored. 
   */
  public Integer getExpirationCache(String portletName, String portletModuleName);
  
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
  public Map getSupportedModes(String portletName, String moduleName);
  
}
