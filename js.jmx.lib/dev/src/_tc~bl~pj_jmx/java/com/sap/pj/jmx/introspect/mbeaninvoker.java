/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.introspect;

import javax.management.*;


/**
 * An <code>MBeanInvoker</code> can invoke methods of MBean implementations that comply with the
 * MBean interface represented by the invoker. An invoker for a certain MBean interface is
 * returned by {@link com.sap.pj.jmx.introspect.MBeanIntrospector#getInvoker()
 * MBeanIntrospector#getInvoker()}. The <code>MBeanInvoker</code> provides an interface similar to
 * {@link javax.management.DynamicMBean <code>DynamicMBean</code>} except that each method has the
 * MBean implementation to be invoked as an additional parameter.
 *
 * @author d025700
 */
public interface MBeanInvoker {
  /**
   * @see javax.management.DynamicMBean#invoke(String, Object[], String[])
   */
  public Object invoke(Object implementation, String operationName, Object[] params,
                       String[] signature) throws MBeanException, ReflectionException;

  /**
   * @see javax.management.DynamicMBean#getAttribute(String)
   */
  public Object getAttribute(Object implementation, String attribute)
          throws MBeanException, AttributeNotFoundException, ReflectionException;

  /**
   * @see javax.management.DynamicMBean#setAttribute(Attribute)
   */
  public void setAttribute(Object implementation, Attribute attribute)
          throws MBeanException, AttributeNotFoundException,
          InvalidAttributeValueException, ReflectionException;
}