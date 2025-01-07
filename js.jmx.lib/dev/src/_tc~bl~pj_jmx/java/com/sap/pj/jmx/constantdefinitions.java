/*
 * (c) Copyright 2002 SAP.
 * All Rights Reserved.
 */

package com.sap.pj.jmx;

/**
 * @version 	1.0
 * @author Gregor Frey
 */
public interface ConstantDefinitions {
  /**
   * Default Domain
   */
  public static final String DEFAULT_DOMAIN_PROPERTY =
          "com.sap.pj.jmx.DefaultDomain";
  public static final String DEFAULT_DOMAIN = "DefaultDomain";

  /**
   * ObjectName of the InterceptorCahin
   */
  public static final String MBEAN_SERVER_INTERCEPTOR_CHAIN_NAME 
  = "JMImplementationHidden:type=MBeanServerInterceptorChain";

  /**
   * Implementation class for the MBeanServer,
   * an implementation of javax.management.MBeanServer
   */
  public static final String MBEAN_SERVER_CLASS_PROPERTY =
          "com.sap.pj.jmx.MBeanServerClass";
  public static final String MBEAN_SERVER_CLASS =
          "com.sap.pj.jmx.server.MBeanServerImpl";

  /**
   * Implementation class for the MBeanServer,
   * an implementation of javax.management.MBeanServer
   */
  public static final String MBEAN_SERVER_DELEGATE_CLASS_PROPERTY =
          "com.sap.pj.jmx.MBeanServerDelegateClass";
  public static final String MBEAN_SERVER_DELEGATE_CLASS =
          "javax.management.MBeanServerDelegate";

  /**
   * The specification name of the implementation. This value can be retrieved from the MBean server delegate.
   */
  public static final String SPECIFICATION_NAME_PROPERTY =
          "com.sap.pj.jmx.SpecificationName";
  public static final String SPECIFICATION_NAME =
          "Java Management Extensions";

  /**
   * The specification version of the implementation. This value can be retrieved from the MBean server delegate.
   */
  public static final String SPECIFICATION_VERSION_PROPERTY =
          "com.sap.pj.jmx.SpecificationVersion";
  public static final String SPECIFICATION_VERSION = "1.2 Maintenance Release";

  /**
   * The specification vendor name. This value can be retrieved from the MBean server delegate.
   */
  public static final String SPECIFICATION_VENDOR_PROPERTY =
          "com.sap.pj.jmx.SpecificationVendor";
  public static final String SPECIFICATION_VENDOR = "Sun Microsystems";

  /**
   * The name of the implementation. This value can be retrieved from the MBean server delegate.
   */
  public static final String IMPLEMENTATION_NAME_PROPERTY =
          "com.sap.pj.jmx.ImplementationName";
  public static final String IMPLEMENTATION_NAME = "com.sap.pj.jmx";

  /**
   * The version of the implementation. This value can be retrieved from the MBean server delegate.
   */
  public static final String IMPLEMENTATION_VERSION_PROPERTY =
          "com.sap.pj.jmx.ImplementationVersion";
  public static final String IMPLEMENTATION_VERSION = "6.30";

  /**
   * The vendor of the implementation. This value can be retrieved from the MBean server delegate.
   */
  public static final String IMPLEMENTATION_VENDOR_PROPERTY =
          "com.sap.pj.jmx.ImplementationVendor";
  public static final String IMPLEMENTATION_VENDOR = "sap.com";

  /**
   * ObjectName of the MBeanServerDelegate
   */
  public static final String MBEAN_SERVER_DELEGATE_NAME =
          "JMImplementation:type=MBeanServerDelegate";

  /**
   * Expose interceptors.
   */
  public static final String EXPOSE_INTERCEPTORS_PROPERTY = "com.sap.pj.jmx.ExposeInterceptors";

  /**
   * References the property that specifies the directory where
   * the native libraries will be stored before the MLet Service
   * loads them into memory.
   * <p>
   * Property Name: <B>jmx.mlet.library.dir</B>
   */
  public static final String MLET_LIB_DIR_PROPERTY = "jmx.mlet.library.dir";

  /**
   * Initial MBeanServerBuilder
   */
  public static final String MBEAN_SERVER_BUILDER_CLASS_PROPERTY =
          "javax.management.builder.initial";

  public static final String MBEAN_SERVER_BUILDER_CLASS =
          "javax.management.MBeanServerBuilder";

  public static final String INTERCEPTOR_CHAIN_BUILDER_CLASS =
          "com.sap.pj.jmx.server.interceptor.InterceptorChainBuilder";

  /**
   * Name of the system. Should be globally unique.
   */
  public static final String SYSTEM_NAME =
          "com.sap.pj.jmx.SystemName";

  /**
   * Name of the component. Identical to the implementation title of the applications
   * archive.
   */
  public static final String COMPONENT_NAME =
          "com.sap.pj.jmx.ComponentName";

  /**
   * Implementation class for the TimerFactory,
   * an implementation of com.sap.pj.jmx.timer.TimerFactory.
   */
  public static final String TIMER_FACTORY_CLASS_PROPERTY =
          "com.sap.pj.jmx.TimerFactory";
  public static final String TIMER_FACTORY_CLASS =
          "com.sap.pj.jmx.timer.j2ee.TimerFactoryImpl";

  /**
   * Implementation class for the MBeanIntrospectorFactory,
   * an implementation of com.sap.pj.jmx.introspect.MBeanIntrospectorFactory.
   */
  public static final String INTROSPECTOR_FACTORY_CLASS_PROPERTY =
          "com.sap.pj.jmx.MBeanIntrospectorFactory";
  public static final String INTROSPECTOR_FACTORY_CLASS =
          "com.sap.pj.jmx.introspect.DefaultMBeanIntrospectorFactory";

  /**
   * Implementation class for the ProxyListenerFactory,
   * an implementation of com.sap.pj.jmx.server.ProxyListenerFactory
   */
  public static final String PROXY_LISTENER_FACTORY_CLASS_PROPERTY =
          "com.sap.pj.jmx.ProxyListenerFactory";
  public static final String PROXY_LISTENER_FACTORY_CLASS =
          "com.sap.pj.jmx.server.SynchronousProxyListenerFactory";

}