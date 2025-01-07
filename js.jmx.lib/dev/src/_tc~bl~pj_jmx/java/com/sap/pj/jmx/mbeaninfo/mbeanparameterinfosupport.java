/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.MBeanParameterInfo;
import java.util.Properties;


/**
 * The <code>MBeanParameterInfoSupport</code> class describes an argument of an operation exposed
 * by an MBean. It is a subclass of <code>javax.management.MBeanParameterInfo</code>, and it
 * implements the <code>AdditionalInfo</code> interface which allows for provision of additional
 * meta information about the parameter.
 *
 * @author d025700
 */
public class MBeanParameterInfoSupport extends MBeanParameterInfo
        implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates a new MBeanParameterInfoSupport object.
   *
   * @param name the name of the parameter.
   * @param type the type of the parameter.
   * @param description a human readable description of the operation.
   * @param properties a set of additional properties that describe the operation.
   */
  public MBeanParameterInfoSupport(String name, String type, String description,
                                   Properties properties) {
    super(name, type, description);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = (Properties) properties.clone();
    }
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
              && this.properties.equals(((MBeanParameterInfoSupport) obj).properties);
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