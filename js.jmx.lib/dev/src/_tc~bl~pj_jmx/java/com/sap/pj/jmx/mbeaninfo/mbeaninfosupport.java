/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.*;
import java.util.Properties;


/**
 * The <code>MBeanInfoSupport</code> class describes the management information of an MBean. It is
 * a subclass of <code>javax.management.MBeanInfo</code>, and it implements the
 * <code>AdditionalInfo</code> interface which allows for provision of additional meta information
 * about an MBean and its features.
 *
 * @author d025700
 */
public class MBeanInfoSupport extends MBeanInfo implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates an MBeanInfoSupport instance, which describes an MBeans with the specified className,
   * description, attributes, constructors , operations, notifications, and properties.
   *
   * @param className the fully qualified Java class name of the MBean described by this
   *        MBeanInfoSupport instance.
   * @param description a human readable description of the MBean described by this
   *        MBeanInfoSupport instance.
   * @param attributes the list of exposed attributes of the described MBean; Must be an array of
   *        instances of a subclass of MBeanAttributeInfo.
   * @param contructors the list of exposed public constructors of the described MBean; Must be an
   *        array of instances of a subclass of MBeanConstructorInfo.
   * @param operations the list of exposed operations of the described MBean. Must be an array of
   *        instances of a subclass of MBeanOperationInfo.
   * @param notifications the list of notifications emitted by the described MBean.
   * @param properties a set of additional properties that describe the MBean.
   */
  public MBeanInfoSupport(String className, String description, MBeanAttributeInfo[] attributes,
                          MBeanConstructorInfo[] contructors, MBeanOperationInfo[] operations,
                          MBeanNotificationInfo[] notifications, Properties properties) {

    super(className, description, attributes, contructors, operations, notifications);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = (Properties) properties.clone();
    }
  }




  /**
   * Creates an MBeanInfoSupport instance, which describes an MBeans with the specified
   * description, attributes, constructors , operations, notifications, and properties. This is a
   * shortcut useful to provide additional meta data with the  <code>StandardMBeanWrapper</code>.
   *
   * @param description a human readable description of the MBean described by this
   *        MBeanInfoSupport instance.
   * @param attributes the list of exposed attributes of the described MBean; Must be an array of
   *        instances of a subclass of MBeanAttributeInfo.
   * @param contructors the list of exposed public constructors of the described MBean; Must be an
   *        array of instances of a subclass of MBeanConstructorInfo.
   * @param operations the list of exposed operations of the described MBean. Must be an array of
   *        instances of a subclass of MBeanOperationInfo.
   * @param notifications the list of notifications emitted by the described MBean.
   * @param properties a set of additional properties that describe the MBean.
   */
/* public MBeanInfoSupport(String description, MBeanAttributeInfo[] attributes,
                           MBeanConstructorInfo[] contructors, MBeanOperationInfo[] operations,
                           MBeanNotificationInfo[] notifications, Properties properties) {
     this("", description, attributes, contructors, operations, notifications, properties);
   }
 */
  /**
   * @see com.sap.pj.jmx.mbeaninfo.AdditionalInfo#getProperties()
   */
  public Properties getProperties() {
    return (Properties) properties.clone();
  }

  /**
   * @see com.sap.pj.jmx.mbeaninfo.AdditionalInfo#getProperty(String)
   */
  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  /**
   * @see com.sap.pj.jmx.mbeaninfo.AdditionalInfo#getProperty(String, String)
   */
  public String getProperty(String key, String value) {
    return properties.getProperty(key, value);
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object obj) {
    try {
      return super.equals(obj)
              && this.properties.equals(((MBeanInfoSupport) obj).properties);
    } catch (ClassCastException e) { //$JL-EXC$
      return false;
    }
  }

  /* We do not include properties in the hashcode. We assume that
     if two mbeans are different they'll probably have different
     types or features.  The penalty we pay when this assumption is
     wrong should be less than the penalty we would pay if it were
     right and we needlessly hashed in the properties.  */
  public int hashCode() {
    return super.hashCode();
  }

}