/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;
import java.lang.reflect.Constructor;
import java.util.Properties;

/**
 * Describes a constructor exposed by an MBean. It is a subclass of
 * <code>javax.management.MBeanConstructorInfo</code>, and it implements the
 * <code>AdditionalInfo</code> interface which allows for provision of additional meta information
 * about the constructor.
 *
 * @author d025700
 */
public class MBeanConstructorInfoSupport extends MBeanConstructorInfo implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates a new MBeanConstructorInfoSupport object.
   *
   * @param name the name of the constructor.
   * @param description a human readable description of the constructor.
   * @param signature MBeanParameterInfo objects describing the parameters(arguments) of the
   *        constructor.
   * @param properties a set of additional properties that describe the constructor.
   */
  public MBeanConstructorInfoSupport(
          String name,
          String description,
          MBeanParameterInfo[] signature,
          Properties properties) {
    super(name, description, signature);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = (Properties) properties.clone();
    }
  }

  /**
   * Creates a new MBeanConstructorInfoSupport object.
   *
   * @param description a human readable description of the constructor.
   * @param constructor the java.lang.reflect.Constructor object describing the MBean constructor.
   * @param properties a set of additional properties that describe the constructor.
   */
  public MBeanConstructorInfoSupport(
          String description,
          Constructor constructor,
          Properties properties) {
    super(description, constructor);
    this.properties = (Properties) properties.clone();
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
              && this.properties.equals(((MBeanConstructorInfoSupport) obj).properties);
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