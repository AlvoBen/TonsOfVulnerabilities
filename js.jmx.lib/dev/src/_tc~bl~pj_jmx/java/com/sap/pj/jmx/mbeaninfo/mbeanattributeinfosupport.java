/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * The <code>MBeanAttributeInfoSupport</code> class describes an MBean attribute exposed for
 * management. It is a subclass of <code>javax.management.MBeanAttributeInfo</code>, and it
 * implements the <code>AdditionalInfo</code> interface which allows for provision of additional
 * meta information about the attribute.
 *
 * @author d025700
 */
public class MBeanAttributeInfoSupport extends MBeanAttributeInfo implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates an MBeanAttributeInfoSupport instance, which describes an attribute of an MBean.
   *
   * @param name the name of the attribute.
   * @param type the type or class name of the attribute.
   * @param description a human readable description of the attribute.
   * @param isReadable true if the attribute has a getter method, false otherwise.
   * @param isWritable true if the attribute has a setter method, false otherwise.
   * @param isIs true if the attribute has an "is" getter method, false otherwise.
   * @param properties a set of additional properties that describe the attribute.
   */
  public MBeanAttributeInfoSupport(
          String name,
          String type,
          String description,
          boolean isReadable,
          boolean isWritable,
          boolean isIs,
          Properties properties) {
    super(name, type, description, isReadable, isWritable, isIs);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = (Properties) properties.clone();
    }
  }

  /**
   * Creates a new MBeanAttributeInfoSupport object.
   *
   * @param name the name of the attribute.
   * @param description a human readable description of the attribute.
   * @param getter the method used for reading the attribute value. May be null if the property is
   *        write-only.
   * @param setter the method used for writing the attribute value. May be null if the attribute is
   *        read-only.
   * @param properties a set of additional properties that describe the attribute.
   *
   * @throws IntrospectionException there is a consistency problem in the definition of this
   *         attribute.
   */
  public MBeanAttributeInfoSupport(
          String name,
          String description,
          Method getter,
          Method setter,
          Properties properties)
          throws IntrospectionException {
    super(name, description, getter, setter);
    this.properties = (Properties) properties.clone();
  }

  /**
   * Creates an MBeanAttributeInfoSupport instance, which describes an attribute of an MBean.
   * This is a shortcut useful to provide additional meta data with the
   * <code>StandardMBeanWrapper</code>.
   *
   * @param name the name of the attribute.
   * @param description a human readable description of the attribute.
   * @param properties a set of additional properties that describe the attribute.
   */
  public MBeanAttributeInfoSupport(String name, String description, Properties properties) {
    this(name, "void", description, true, false, false, properties); //$NON-NLS-1$
  }

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
              && this.properties.equals(((MBeanAttributeInfoSupport) obj).properties);
    } catch (ClassCastException e) { //$JL-EXC$
      return false;
    }
  }
  
  /* We do not include properties in the hashcode. We assume that
     if two features are different they'll probably have different
     names or types.  The penalty we pay when this assumption is
     wrong should be less than the penalty we would pay if it were
     right and we needlessly hashed in the properties.  */
  public int hashCode() {
    return super.hashCode();
  }

}