/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.introspect;

import javax.management.*;


/**
 * <p>
 * The <code>MBeanInrospector</code> provides information retrieved from a standard MBean via
 * introspection. That include the information needed to construct the {@link
 * javax.management.MBeanInfo <code>MBeanInfo</code>} as well as an {@link MBeanInvoker
 * <code>MBeanInvoker</code>} which is able to invoke an implementation that complies with the
 * MBean interface. The {@link com.sap.pj.jmx.introspect.MBeanIntrospectorFactory
 * <code>MBeanIntrospectorFactory</code>} is responsible to provide an instance of
 * <code>MBeanInrospector</code> for a given standard MBean.
 * </p>
 *
 * <p>
 * Since most of the MBeanInfo can be derived from the MBean interface and is independent of the
 * implementation class of the MBean an instance of MBeanIntrospector can be used for multiple
 * instances of the same MBean. The current MBean implementation to be introspected can be set
 * using {@link #setImplementation(Object) <code>setImplementation(Object)</code>}. Calling
 * <code>setImplementation(Object)</code> causes the regeneration of <var>Implementation</var>,
 * <var>ClassName</var>,  <var>ConstructorInfo</var>, and  <var>NotificationInfo</var>. All other
 * properties remain unchanged during the lifecycle of an <code>MBeanInrospector</code> instance.
 * </p>
 *
 * <p></p>
 *
 * @author d025700
 */
public interface MBeanIntrospector {
  /**
   * Returns the {@link MBeanInvoker <code>MBeanInvoker</code>} for the introspected MBean
   * interface.
   *
   * @return the <code>MBeanInvoker</code>.
   */
  public MBeanInvoker getInvoker();

  /**
   * Returns the {@link MBeanAttributeInfo <code>MBeanAttributeInfo</code>} for all attributes of
   * the introspected MBean interface.
   *
   * @return an array of <code>MBeanAttributeInfo</code>.
   */
  public MBeanAttributeInfo[] getAttributeInfo();

  /**
   * Returns the {@link MBeanConstructorInfo <code>MBeanConstructorInfo</code>} for all
   * constructors of the introspected MBean implementation.
   *
   * @return an array of <code>MBeanConstructorInfo</code>.
   *
   * @see #setImplementation(Object)
   */
  public MBeanConstructorInfo[] getConstructorInfo();

  /**
   * Returns the {@link MBeanOperationInfo <code>MBeanOperationInfo</code>} for all operations of
   * the introspected MBean interface.
   *
   * @return an array of <code>MBeanOperationInfo</code>.
   */
  public MBeanOperationInfo[] getOperationInfo();

  /**
   * Returns the {@link MBeanNotificationInfo <code>MBeanNotificationInfo</code>} for all
   * notifications of the introspected MBean implementation.
   *
   * @return an array of <code>MBeanNotificationInfo</code>.
   *
   * @see #setImplementation(Object)
   */
  public MBeanNotificationInfo[] getNotificationInfo();

  /**
   * Returns the class name of the introspected MBean implementation.
   *
   * @return the class name of the current implementation.
   *
   * @see #setImplementation(Object)
   */
  public String getClassName();

  /**
   * Replaces the implementation currently introspected.
   *
   * @param implementation The new implementation to be introspected. The <var>implementation</var>
   *        object must implement the MBean interface that was supplied when this
   *        <code>MBeanIntrospector</code> was constructed.
   *
   * @throws NotCompliantMBeanException if the given <var>implementation</var> does not implement
   *         the MBean interface that was supplied at construction.
   */
  public void setImplementation(Object implementation)
          throws NotCompliantMBeanException;

  /**
   * Returns the implementation currently introspected.
   *
   * @return the MBean implementation.
   *
   * @see #setImplementation(Object)
   */
  public Object getImplementation();

  /**
   * Returns the MBean interface introspected.
   *
   * @return the MBean interface.
   */
  public Class getMBeanInterface();
}