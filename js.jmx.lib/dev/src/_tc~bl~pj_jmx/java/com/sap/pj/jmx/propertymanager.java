package com.sap.pj.jmx;

/**
 * @author Gregor Frey
 * @version 1.0
 */
public class PropertyManager implements ConstantDefinitions {

  /**
   * @return the default domain
   */
  public final static String getDefaultDomain() {
    String ret = DEFAULT_DOMAIN;
    try {
      ret = System.getProperty(DEFAULT_DOMAIN_PROPERTY, DEFAULT_DOMAIN);
    } catch (Exception e) { //$JL-EXC$
    }
    return ret;
  }

  /**
   * @return the class of the MBeanServer implementation
   */
  public final static String getMBeanServerClass() {
    return System.getProperty(MBEAN_SERVER_CLASS_PROPERTY, MBEAN_SERVER_CLASS);
  }

  /**
   * @return the class of the MBeanServerDelegate
   */
  public static String getMBeanServerDelegateClass() {
    return System.getProperty(MBEAN_SERVER_DELEGATE_CLASS_PROPERTY, MBEAN_SERVER_DELEGATE_CLASS);
  }

  /**
   * @return the specification name
   */
  public final static String getSpecificationName() {
    return System.getProperty(SPECIFICATION_NAME_PROPERTY, SPECIFICATION_NAME);
  }

  /**
   * @return the specification version
   */
  public final static String getSpecificationVersion() {
    return System.getProperty(SPECIFICATION_VERSION_PROPERTY, SPECIFICATION_VERSION);
  }

  /**
   * @return the specification vendor
   */
  public final static String getSpecificationVendor() {
    return System.getProperty(SPECIFICATION_VENDOR_PROPERTY, SPECIFICATION_VENDOR);
  }

  /**
   * @return the implementation name
   */
  public final static String getImplementationName() {
    return System.getProperty(IMPLEMENTATION_NAME_PROPERTY, IMPLEMENTATION_NAME);
  }

  /**
   * @return the implementation version
   */
  public final static String getImplementationVersion() {
    return System.getProperty(IMPLEMENTATION_VERSION_PROPERTY, IMPLEMENTATION_VERSION);
  }

  /**
   * @return the vendor name
   */
  public final static String getImplementationVendor() {
    return System.getProperty(IMPLEMENTATION_VENDOR_PROPERTY, IMPLEMENTATION_VENDOR);
  }

  /**
   * @return the class name of the mbean server builder
   */
  public static String getMBeanServerBuilderClass() {
    return System.getProperty(MBEAN_SERVER_BUILDER_CLASS_PROPERTY, MBEAN_SERVER_BUILDER_CLASS);
  }

  /**
   * @return the class name of the timer factory
   */
  public static String getTimerFactoryClass() {
    return System.getProperty(TIMER_FACTORY_CLASS_PROPERTY, TIMER_FACTORY_CLASS);
  }

  /**
   * @return the class name of the introspector factory
   */
  public static String getMBeanIntrospectorFactoryClass() {
    return System.getProperty(INTROSPECTOR_FACTORY_CLASS_PROPERTY, INTROSPECTOR_FACTORY_CLASS);
  }

  /**
   * @return the class name of the proxy listener factory
   */
  public static String getProxyListenerFactoryClass() {
    return System.getProperty(PROXY_LISTENER_FACTORY_CLASS_PROPERTY, PROXY_LISTENER_FACTORY_CLASS);
  }

}
