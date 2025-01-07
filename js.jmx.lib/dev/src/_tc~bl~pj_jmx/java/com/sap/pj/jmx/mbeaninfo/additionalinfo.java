/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import java.util.Properties;


/**
 * The <code>AdditionalInfo</code> interface allows for retrieval of additional meta information
 * about MBeans which is not covered by the standard <code>MBeanInfo</code>,
 * <code>MBeanFeatureInfo</code>, ... classes. The interface is intended to be implemented by
 * subclasses of the standard <code>XxxInfo</code> classes. An example for meta data is
 * information for rendering of an MBean, e.g. default values for input fields or the name of a
 * custom control that should be used instead of a generic rendering. <br>
 * A client that wants to access the additional information first has to check if the
 * <code>MBeaInfo</code> returend by the MBeanServer is an instance of <code>AdditionalInfo</code>
 * before it can access the additional properties. The properties itself are key-value pairs.
 * Similar to <code>java.util.Properties</code>, only <code>String</code> values  are supported.
 * The name of the key should follow package name conventions, i.e. start with <code>com.sap</code>.<br>
 * The provider of additional information typically uses one of the <code>XxxInfoSupport</code>
 * classes which already implement the <code>AdditionalInfo</code> interface. Providers of
 * standard MBeans can use the <code>StandardMBeanWrapper</code> class to do this.
 *
 * @author d025700
 */
public interface AdditionalInfo {
  /**
   * Key for the standard description property which is mapped to
   * <code>MBeanInfo.getDescription()</code>, <code>MBeanFeatureInfo.getDescription()</code>. The
   * value is &quot;javax.management.Description&quot;.
   */
  public static final String DESCRIPTION_KEY = "javax.management.Description"; //$NON-NLS-1$

  /**
   * Key for the standard name property which is mapped to
   * <code>MBeanFeatureInfo.getName()</code>. The value is &quot;javax.management.Name"&quot;.
   */
  public static final String NAME_KEY = "javax.management.Name"; //$NON-NLS-1$

  /**
   * Key for the standard impact property which is mapped to
   * <code>MBeanOperationInfo.getImpact()</code>. The value is &quot;javax.management.Impact"&quot;.
   */
  public static final String IMPACT_KEY = "javax.management.Impact"; //$NON-NLS-1$

  /**
   * Returns all meta info as a <code>java.util.Properties</code> object. Since this is a copy
   * changing the Properties object does not change the AdditionalInfo.
   *
   * @return All properties.
   */
  public Properties getProperties();

  /**
   * Searches for the property with the specified key in this additional info. The method returns
   * <code>null</code> if the property is not found.
   *
   * @param key the name of the property.
   *
   * @return the value for the given key.
   */
  public String getProperty(String key);

  /**
   * Searches for the property with the specified key in this additional info. The method returns
   * the default value argument if the property is not found.
   *
   * @param key the name of the property.
   * @param value a default value.
   *
   * @return the value for the given key.
   */
  public String getProperty(String key, String value);
}