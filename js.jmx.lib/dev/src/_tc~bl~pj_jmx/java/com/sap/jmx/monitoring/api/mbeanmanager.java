/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.monitoring.api;

import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.sap.jmx.ObjectNameFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Registers/un-registers resource MBeans of an application from the MBeanServer.
 *
 * @author d025700
 */
public class MBeanManager {
  private static final String NAME_KEY = ObjectNameFactory.NAME_KEY;
  private static final Location LOCATION = Location.getLocation(MBeanManager.class);
	private static final String REGISTER_MBEANS_METHOD = "registerMBeans(String[],Object[])"; //$NON-NLS-1$
	private static final String UNREGISTER_MBEANS_METHOD = "unregisterMBeans()"; //$NON-NLS-1$

  private ObjectName[] jmxObjectNames;
  private MBeanServer mbs;

  /**
   * Registers the given list of resource MBeans with the MBeanServer.
   * @param mbeanNames
   * @param mbeans
   * @throws MBeanManagerException
   */
  synchronized public void registerMBeans(String[] mbeanNames, Object[] mbeans)
    throws MBeanManagerException {

    if (mbeanNames == null || mbeans == null || mbeanNames.length != mbeans.length) {
      throw new IllegalArgumentException("parameters mbeanNames and mbeans must not be null and must have same lenght");
    }

    int objectNameOff = 0;
    if (jmxObjectNames == null) {
      jmxObjectNames = new ObjectName[mbeanNames.length];
    }
    else {
      ObjectName[] newJmxObjectNames = new ObjectName[jmxObjectNames.length + mbeanNames.length];
      System.arraycopy(jmxObjectNames, 0, newJmxObjectNames, 0, jmxObjectNames.length);
      objectNameOff = jmxObjectNames.length;
      jmxObjectNames = newJmxObjectNames;
    }

    /*
     * Create a JMX ObjectNames for the resource MBeans
     */
    for (int i = 0; i < mbeanNames.length; i++) {
      try {
        jmxObjectNames[i + objectNameOff] =
          ObjectNameFactory.getNameForApplicationResourcePerNode(
            mbeanNames[i],
            null,
            null,
            null);
      }
      catch (MalformedObjectNameException e) {
        jmxObjectNames = null;
				MBeanManagerException mbmex = new MBeanManagerException(
          "Unable to create object name for resource MBean " + mbeanNames[i],
          e);
        LOCATION.traceThrowableT(Severity.PATH, REGISTER_MBEANS_METHOD, mbmex.getMessage(), mbmex);
				throw mbmex;
      }
    }

    /*
     * Get the MBeanServer
     */
    try {
      InitialContext initCtx = new InitialContext();
      mbs = (MBeanServer) initCtx.lookup("jmx"); //$NON-NLS-1$
    }
    catch (Exception e) {
      mbs = null;
      jmxObjectNames = null;
      MBeanManagerException mbmex = new MBeanManagerException("Unable to get the MBeanServer", e);
      LOCATION.traceThrowableT(Severity.PATH, REGISTER_MBEANS_METHOD, mbmex.getMessage(), mbmex);
			throw mbmex;
    }

    /*
     * Register resource MBeans with MBeanServer
     */
    HashMap failures = new HashMap();
    for (int i = 0; i < mbeans.length; i++) {
      ObjectName currName = jmxObjectNames[i + objectNameOff];
      try {
        mbs.registerMBean(mbeans[i], currName);
      }
      catch (Exception e) {
        failures.put(currName.getKeyProperty(NAME_KEY), e);
        LOCATION.traceThrowableT(Severity.PATH, REGISTER_MBEANS_METHOD, "Unable to register MBean " + currName, e);
      }
    }
    if (failures.size() != 0) {
      throw new PartialRegistrationException(
        "Registration of " + failures.size() + " MBeans failed. Switch on the trace for com.sap.jmx in order to see detailed exceptions.",
        failures);
    }
  }

  /**
   * Registers a single resource MBean.
   * @param mbeanName
   * @param mbean
   * @throws MBeanManagerException if the registration fails
   */
  public void registerMBean(String mbeanName, Object mbean) throws MBeanManagerException {
    registerMBeans(new String[] { mbeanName }, new Object[] { mbean });
  }

  /**
   * Removes all resource MBeans registered by this MBeanManager.
   * @throws PartialRegistrationException if the de-registration of some of the MBeans failed
   */
  synchronized public void unregisterMBeans() throws PartialRegistrationException {
    if (mbs != null && jmxObjectNames != null) {
      HashMap failures = new HashMap();
      for (int i = 0; i < jmxObjectNames.length; i++) {
        ObjectName currName = jmxObjectNames[i];
        try {
          mbs.unregisterMBean(currName);
        }
        catch (Exception e) {
          failures.put(currName.getKeyProperty(NAME_KEY), e);
          LOCATION.traceThrowableT(Severity.PATH, UNREGISTER_MBEANS_METHOD, "Unable to unregister MBean " + currName, e);
        }
      }
      if (failures.size() != 0) {
        throw new PartialRegistrationException(
          "Un-registration of " + failures.size() + " MBeans failed. Switch on the trace for com.sap.jmx in order to see detailed exceptions.",
          failures);
      }
    }
    mbs = null;
  }

  /**
   * Returns the names of the currently registered resource MBeans.
   * @return the names.
   */
  synchronized public String[] getMBeanNames() {
    if (jmxObjectNames == null) {
      return new String[0];
    }
    String[] mbeanNames = new String[jmxObjectNames.length];
    for (int i = 0; i < jmxObjectNames.length; i++) {
      mbeanNames[i] = jmxObjectNames[i].getKeyProperty(ObjectNameFactory.NAME_KEY);
    }
    return mbeanNames;
  }

}
