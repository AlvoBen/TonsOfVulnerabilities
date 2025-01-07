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
package com.sap.portletbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import com.sap.engine.services.portletcontainer.spi.PortalContextExt;

/**
 * The <code>PortalContextImpl</code> class is an implementation of the <code>
 * PortalContext</code>
 * interface which gives the portlet the ability to retrieve information about
 * the portal calling this portlet.
 * 
 * The portlet can only read the PortalContext data.
 * 
 * @author Diyan Yordanov
 * @author Vera Buchkova
 * @author Nikolai Dokovski
 * @version 7.10
 */
public class PortalContextImpl implements PortalContextExt {

	/**
	 * Portlet Modes supported by the portal.
	 */
	private static List<PortletMode> supportedPortletModes;

	/**
	 * Window States supported by the portal.
	 */
	private static List<WindowState> supportedWindowStates;

	/**
   * Runtime options supported by the portal.
   */
	 private static List<String> supportedRuntimeOptions;
	 
	/**
	 * The PortalContext instance.
	 */
	private static PortalContextImpl contextInstance;

	private static Properties properties;

	/**
	 * Returns an instance to this <code>PortalContextImpl</code> object.
	 * 
	 * @return an instance to this <code>PortalContextImpl</code> object.
	 */
	public static PortalContextImpl getInstance() {
		if (contextInstance == null) {
			contextInstance = new PortalContextImpl();
		}
		return contextInstance;
	}

	/**
	 * Creates new <code>PortalContextImpl</code> instance.
	 */
	private PortalContextImpl() {
		if (supportedPortletModes == null) {
			supportedPortletModes = new ArrayList<PortletMode>(3);
			supportedPortletModes.add(PortletMode.VIEW);
			supportedPortletModes.add(PortletMode.EDIT);
			supportedPortletModes.add(PortletMode.HELP);
		}

		if (supportedWindowStates == null) {
			supportedWindowStates = new ArrayList<WindowState>(3);
			supportedWindowStates.add(WindowState.NORMAL);
			supportedWindowStates.add(WindowState.MINIMIZED);
			supportedWindowStates.add(WindowState.MAXIMIZED);
		}
		if (supportedRuntimeOptions == null) {
      supportedRuntimeOptions = new ArrayList<String>(0);
    }
		if (properties == null){
		  properties = new Properties();
		}
	}

	/**
	 * Returns the portal property with the given name, or a <code>null</code>
	 * if there is no property by that name.
	 * 
	 * @param name
	 *            property name.
	 * @return portal property with key <code>name</code>.
	 * @throws IllegalArgumentException
	 *             if name is <code>null</code>.
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * Returns all portal property names, or an empty <code>Enumeration</code>
	 * if there are no property names.
	 * 
	 * @return All portal property names as an <code>Enumeration</code> of
	 *         <code>String</code> objects.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getPropertyNames() {
		return properties.keys();
	}

	/**
	 * Returns all supported portlet modes by the portal as an enumertation of
	 * <code>PorltetMode</code> objects.
	 * <p>
	 * The portlet modes include the standard portlet modes
	 * <code>EDIT, HELP, VIEW</code>.
	 * 
	 * @return All supported portal modes by the portal as an enumertation of
	 *         <code>PorltetMode</code> objects.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getSupportedPortletModes() {
		return Collections.enumeration(supportedPortletModes);
	}

	/**
	 * Returns all supported window states by the portal as an enumertation of
	 * <code>WindowState</code> objects.
	 * <p>
	 * The window states include the standard window state <code>NORMAL</code>,
	 * <code>MINIMIZED</code> and <code>MAXIMIZED</code>.
	 * 
	 * @return All supported window states by the portal as an enumertation of
	 *         <code>WindowState</code> objects.
	 */
	@SuppressWarnings("unchecked")
	public Enumeration getSupportedWindowStates() {
		return Collections.enumeration(supportedWindowStates);
	}

	/**
	 * Returns information about the portal like vendor, version, etc.
	 * <p>
	 * The portlet container return <CODE>com.sap.portal/7.1.0.0.0</CODE>.
	 * 
	 * @return a <CODE>String</CODE> containing the portal name and version
	 *         number.
	 */
	public String getPortalInfo() {
		// Portal vendor - sap.com
		// Portal version - 7.1.0.0.0
		return "com.sap.portalbrowser/7.1.2.0.0";
	}

  public Enumeration<String> getSupportedRuntimeOptions() {
    return Collections.enumeration(supportedRuntimeOptions);
  }
}