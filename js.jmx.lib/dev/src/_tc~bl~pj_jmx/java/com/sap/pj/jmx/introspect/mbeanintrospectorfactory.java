/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.introspect;

import javax.management.NotCompliantMBeanException;

/**
 * Used to retrieve the <code>MBeanIntrospector</code> for a given MBean.
 *
 * @author d025700
 */
public interface MBeanIntrospectorFactory {
  /**
   * Returns the <code>MBeanIntrospector</code> for a given MBean.
   *
   * @param mbean The implementation of the MBean.
   * @param mbeanInterface The Management Interface exported by the <var>mbean</var>. If
   *        <code>null</code>, then this object will use standard JMX design pattern to determine
   *        the management interface associated with the given mbean.
   *
   * @return the <code>MBeanIntrospector</code> for a given MBean.
   *
   * @throws NotCompliantMBeanException if the <var>mbeanInterface</var> does not follow JMX design
   *         patterns for Management Interfaces, or if the given <var>mbean</var> does not
   *         implement the specified interface.
   */
  public MBeanIntrospector getMBeanIntrospector(Object mbean, Class mbeanInterface)
          throws NotCompliantMBeanException;
}