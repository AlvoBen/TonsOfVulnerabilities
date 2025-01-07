/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import java.lang.reflect.Method;
import java.util.Properties;


/**
 * The <code>MBeanOperationInfoSupport</code> class describes an MBean operation exposed for
 * management. It is a subclass of <code>javax.management.MBeanOperationInfo</code>, and it
 * implements the <code>AdditionalInfo</code> interface which allows for provision of additional
 * meta information about the operation.
 *
 * @author d025700
 */
public class MBeanOperationInfoSupport extends MBeanOperationInfo
        implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates an MBeanOperationInfoSupport instance, which describes an operation of an MBean.
   *
   * @param name the name of the operation.
   * @param description a human readable description of the operation.
   * @param signature MBeanParameterInfo objects describing the parameters(arguments) of the
   *        operation.
   * @param type the type of the operation's return value.
   * @param impact the impact of the method, one of INFO, ACTION, ACTION_INFO, UNKNOWN.
   * @param properties a set of additional properties that describe the operation.
   */
  public MBeanOperationInfoSupport(String name, String description, MBeanParameterInfo[] signature,
                                   String type, int impact, Properties properties) {
    super(name, description, signature, type, impact);
    if (properties == null) {
      this.properties = new Properties();
    } else {
      this.properties = (Properties) properties.clone();
    }
  }

  /**
   * Creates an MBeanOperationInfoSupport instance, which describes an operation of an MBean.
   *
   * @param description a human readable description of the operation.
   * @param method the java.lang.reflect.Method object describing the MBean operation.
   * @param properties a set of additional properties that describe the operation.
   */
  public MBeanOperationInfoSupport(String description, Method method, Properties properties) {
    super(description, method);
    this.properties = (Properties) properties.clone();
  }

  /**
   * Creates an MBeanOperationInfoSupport instance, which describes an operation of an MBean.
   * This is a shortcut useful to provide additional meta data with the
   * <code>StandardMBeanWrapper</code>.
   *
   * @param name the name of the operation.
   * @param description a human readable description of the operation.
   * @param signature MBeanParameterInfo objects describing the parameters(arguments) of the
   *        operation.
   * @param properties a set of additional properties that describe the operation.
   */
  public MBeanOperationInfoSupport(String name, String description, MBeanParameterInfo[] signature,
                                   Properties properties) {
    this(name, description, signature, "void", MBeanOperationInfo.UNKNOWN, properties); //$NON-NLS-1$
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
              && this.properties.equals(((MBeanOperationInfoSupport) obj).properties);
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