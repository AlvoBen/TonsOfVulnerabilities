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

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.Event;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.xml.namespace.QName;

/**
* The <code>PortletAdmin</code> interface allows the Portal application to get
* all the necessary information about deployed Portlet applications during the
* portlet administration phase.
* 
* @author Diyan Yordanov
* @version 7.12
*/
public interface PortletAdmin {  
  
  /**
   * Returns a list of all vendor names of the applications that contains portlet
   * modules.
   * 
   * @return a list of all vendor names of the applications that contains portlet
   * modules.
   */
  public String [] getVendors();
  
  /**
   * Return a list of j2ee applications that contains portlet web modules.
   * @param vendor the applications' vendor.
   * @return a list of j2ee applications that contains portlet web modules.
   */
  public String [] getPortletApplicationNames(String vendor);
  
  /**
   * Returns a list of all portlet-web-modules names, which belong to the 
   * specified portlet-j2ee-application. If the portlet application name is the 
   * full application name i.e. "vendor"/"application name" then the 
   * portlet-web-modules names corresponding to this application name and vendor
   * will be returned. If only the portlet application name (without vendor
   * information) is specified and there are deployed applications with one and
   * the same application name, but with different vendors then 
   * IllegalStateException will be thrown, else the portlet-web-modules names
   * corresponding to the specified portlet application name will be returned.
   *
   * @param porteltApplicationName the name of the portlet-j2ee-application.
   * @return a list of all portlet-web-modules that are within the specified
   * portlet-j2ee-application.
   */
  public String [] getPortletModuleNames(String porteltApplicationName);
  
  /**
   * Returns a list of all portlets in the specified portlet-web-module.
   * @param portletModuleName the name of the portlet-web-module.
   * @return a list of all portlets in the portlet-web-module.
   */
  public String [] getAllPortlets(String portletModuleName); 
  
  /**
   * Returns new object of the portlet initial preferences loaded from Deployment 
   * Descriptor
   * @param portletName the name of the portlet which preferences to be returned.
   * @param portletModuleName the name of the portlet-web-application that contains 
   * the portlet.
   * @return the initial portlet preverences loaded from the Deployment Descriptor.
   */
  public PortletPreferences getPortletPreferences(String portletName, String portletModuleName); 
  
  /**
   * Returns the expiration cache timeout defined in the portlet Deployment
   * Descriptor
   * for the specified portlet.
   * @param portletName the name of the portlet which expiration cache to be
   * returned.
   * @param portletModuleName the name of the portlet-web-application that contains
   * the portlet.
   * @return the expiration cache timeout. If expiration cache timeout value
   * returned is 0, caching is disabled for the portlet, if the value returned is
   * -1, the cache does not expire. If the value returned is <code>null</code> - 
   * portlet has not defined expiration cache and if the expiration cache property
   * in the <code>RenderResponse</code> is set, it must be ignored. 
   */
  public Integer getExpirationCache(String portletName, String portletModuleName);
  
  /**
   * Returns the caching scope of the portlet as defined in the deployment 
   * descriptor. The values returned by this method are the values defined for
   * the CACHE_SCOPE constant defined in the <code>MimeResponse</code> interface.
   * If no scope is provided in the deployment descriptor the method
   * will return PRIVATE_SCOPE as it is the default scope.  
   * @param portletName the name of the portlet which expiration cache scope to 
   * be returned.
   * @param portletModuleName the name of the portlet-web-application that 
   * contains the portlet.
   * @return Returns the caching scope of the portlet as defined in the deployment 
   * descriptor.
   */
  public String getExpirationCacheScope(String portletName, String portletModuleName);
  
  /**
   * Constructs a new java.util.Map object of mapped mime-types to arrays of 
   * PortletMode objects, as declared in the portlet's descriptor's <supports>
   * elements.
   * The map does not include the VIEW mode, if it is not declared in the
   * descriptor.
   * Note that the VIEW mode is required to be always supported, 
   * even if it is not declared in the descriptor.
   * If there are no supported types declared for a given mime type, its
   * value in the map is an array with length 0.
   * @param portletName the name of the portlet 
   * @param moduleName the name of the portlet-web-application that contains the
   * portlet.
   * @return Map containing mime-types String objects as keys and arrays 
   * of PortletMode as values, which the portlet declares to support for 
   * the corresponding mime-type, including the VIEW mode 
   */
  public Map<String, PortletMode[]> getSupportedModes(String portletName, String moduleName);
  
  /**
   * Returns the identifiers of the public render parameters supported by the 
   * portlet as an Enumeration of String objects, or an empty Enumeration if the
   * portlet has not defined public render parameters.
   * Public render parameters are defined in the portlet deployment descriptor 
   * with the supported-public-render-parameter element. 
   *
   * @param portletName the name of the portlet
   * @param portletModuleName the name of the portlet-web-application that 
   * contains the portlet.
   * @return list of the identifiers of the public render parameters supported by
   * the portlet
   * 
   * @since 2.0
   */
  public Enumeration<String> getPublicRenderParameterNames(String portletName, String portletModuleName);
  
  /**
   * Returns detailed type description for the specified public render parameter:
   * <ul>
   *  <li>description:String (0..*)</li>
   *  <li>identifier:String</li>
   *  <li>qname:QName ot name:NCName</li>
   *  <li>alias:QName (0..*)</li>
   * </ul>
   * The parameterId parameter is one of the identifiers returned by the <code>
   * getPublicRenderParameterIdentifiers</code> method.
   *  
   * @param parameterId the identifier of the parameter
   * @param portletModuleName the name of the portlet-web-application that
   * contains the portlet.
   * @return a PublicRenderParameterType object that fully describes the public
   * render parameter
   * 
   * @since 2.0
   */
  public PublicRenderParameterType getPublicRenderParameterType(String parameterId, String portletModuleName);
  
  /**
   * Returns the QNames of the publishing events supported by the portlet as an 
   * Enumeration of QName objects, or an empty Enumeration if the portlet has 
   * not defined any publishing events.
   * Publishing events are defined in the portlet deployment descriptor with the
   * supported-publishing-event element. 
   * Note that this call does not return any events published that have not been 
   * declared in the deployment descriptor as supported. 
   *
   * @param portletName the name of the portlet
   * @param portletModuleName the name of the portlet-web-application that
   * contains the portlet.
   * @return the QNames of the publishing events supported by the portlet as an 
   * Enumeration of QName objects
   * 
   * @since 2.0
   */
  public Enumeration<QName> getPublishingEventQNames(String portletName, String portletModuleName);
  
  /**
   * Returns the QNames of the processing events supported by the portlet as an 
   * Enumeration of QName objects, or an empty Enumeration if the portlet has 
   * not defined any processing events. 
   * Processing events are defined in the portlet deployment descriptor with the
   * supported-processing-event element. 
   *
   * @param portletName the name of the portlet
   * @param portletModuleName the name of the portlet-web-application that
   * contains the portlet.
   * @return the QNames of the processing events supported by the portlet as an 
   * Enumeration of QName objects
   * 
   * @since 2.0
   */
  public Enumeration<QName> getProcessingEventQNames(String portletName, String portletModuleName);
  
  /**
   * Returns an <code>Event</code> object with a specified <code>QName</code>. 
   * 
   * @param qname identifier of event
   * @param portletModuleName the name of the portlet-web-application that
   * contains the portlet.
   * @return <code>Event</code> with the specified qualified name
   * 
   * @since 2.0
   */
  public Event getEvent(QName qname, String portletModuleName);

  /**
   * Returns the container runtime options and values for this portlet. 
   * The portlet can set container runtime options in the portlet.xml via the 
   * container-runtime-option element with a name and a value on the application 
   * and portlet level.
   * If a container runtime option is set on the portlet application level and 
   * on the portlet level with the same name the setting on the portlet level 
   * takes precedence and overwrites the one set on the portal application level. 
   * 
   * The map returned from this method will provide the subset the portlet 
   * container supports of the options the portlet has specified in the 
   * portlet.xml. Options that the portlet container does not support will not 
   * be returned in this map. 
   * 
   * The map will contain name of the runtime option as key of type String and 
   * the runtime options as values of type String array (String[]) with the 
   * values specified in the portlet.xml deployment descriptor. 
   * 
   * @param portletName the name of the portlet
   * @param portletModuleName the name of the portlet-web-application that
   * contains the portlet.
   * @return the container runtime options and values for this portlet.
   * 
   * @since 2.0
   */
  public Map<String, String[]> getContainerRuntimeOptions(String portletName, String portletModuleName);
}
