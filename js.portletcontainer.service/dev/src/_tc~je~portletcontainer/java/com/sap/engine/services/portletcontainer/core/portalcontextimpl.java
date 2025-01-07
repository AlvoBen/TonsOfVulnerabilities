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
package com.sap.engine.services.portletcontainer.core;
import java.util.Enumeration;
import java.util.Vector;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

/**
 * The <code>PortalContextImpl</code> class is an implementation of the <code>
 * PortalContext</code> interface which gives the portlet the ability to 
 * retrieve information about the portal calling this portlet. 
 *
 * The portlet can only read the PortalContext data.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortalContextImpl implements PortalContext {
  
  /**
   * Supported Portlet Modes by the portal.
   */
  private static Vector supportedPortletModes ;
  
  /**
   * Supported Window States by the portal.
   */
  private static Vector supportedWindowStates;
  
  /**
   * The PortalContext instance.
   */
  private static PortalContextImpl contextInstance ;

  /**
   * Returns an instance to this <code>PortalContextImpl</code> object.
   * @return an instance to this <code>PortalContextImpl</code> object.
   */
  public static PortalContextImpl getInstance() {
    if (contextInstance == null) {
      contextInstance = new PortalContextImpl();
    }
    return contextInstance ;
  }

  /**
   * Creates new <code>PortalContextImpl</code> instance.
   */
  private PortalContextImpl() {
    Vector v = new Vector();
    v.add(PortletMode.VIEW);
    v.add(PortletMode.EDIT);
    v.add(PortletMode.HELP);
    supportedPortletModes = v;

    //Not implemented with EP 6.0
    v = new Vector();
    v.add(WindowState.NORMAL);
    v.add(WindowState.MINIMIZED);
    v.add(WindowState.MAXIMIZED);
    supportedWindowStates = v;
  }

  /**
   * Returns the portal property with the given name, or a <code>null</code> 
   * if there is no property by that name.
   * @param name property name.
   * @return portal property with key <code>name</code>.
   * @throws IllegalArgumentException if name is <code>null</code>.
   */
  public String getProperty(String name) {
    //TODO: impl properties
    return null;
  }

  /**
   * Returns all portal property names, or an empty 
   * <code>Enumeration</code> if there are no property names.
   * @return All portal property names as an <code>Enumeration</code> of 
   * <code>String</code> objects.
   */
  public Enumeration getPropertyNames() {
    //TODO: impl properties
    return (new Vector()).elements();
  }

  /**
   * Returns all supported portlet modes by the portal as an enumertation of 
   * <code>PorltetMode</code> objects.
   * <p>
   * The portlet modes include the standard portlet modes 
   * <code>EDIT, HELP, VIEW</code>.
   * @return All supported portal modes by the portal as an enumertation of 
   * <code>PorltetMode</code> objects.
   */
  public Enumeration getSupportedPortletModes() {
    return supportedPortletModes.elements();
  }

  /**
   * Returns all supported window states by the portal as an enumertation of 
   * <code>WindowState</code> objects.
   * <p>
   * The window states include the standard window state <code>NORMAL</code>,
   * <code>MINIMIZED</code> and <code>MAXIMIZED</code>.
   * @return  All supported window states by the portal as an enumertation of 
   * <code>WindowState</code> objects.
   */
  public Enumeration getSupportedWindowStates() {
    return supportedWindowStates.elements();
  }

  /**
   * Returns information about the portal like vendor, version, etc.
   * <p>
   * The portlet container return <CODE>com.sap.portal/7.1.0.0.0</CODE>.
   * 
   * @return a <CODE>String</CODE> containing the portal name and version number.
   */
  public String getPortalInfo() {
    //Portal vendor - sap.com
    //Portal version - 7.1.0.0.0
    return "com.sap.portal/7.1.0.0.0";
  }
}
