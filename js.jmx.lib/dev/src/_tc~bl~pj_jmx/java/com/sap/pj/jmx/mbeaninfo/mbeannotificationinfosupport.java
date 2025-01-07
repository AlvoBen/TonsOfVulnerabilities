/*
 * Copyright (c) 2002 by SAP AG,
 * All rights reserved.
 */
package com.sap.pj.jmx.mbeaninfo;

import javax.management.MBeanNotificationInfo;
import java.util.Properties;


/**
 * The <code>MBeanNotificationInfoClass</code> class is a subclass of
 * <code>javax.management.MBeanNotificationInfo</code>, and it implements the
 * <code>AdditionalInfo</code> interface which allows for provision of additional meta information
 * about the notification.<br>
 * It is used to describe the characteristics of the different notification instances emitted by
 * an MBean, for a given Java class of notification. If an MBean emits notifcations that can be
 * instances of different Java classes, then the metadata for that MBean should provide an
 * <code>MBeanNotificationInfoSupport</code> object for each of these notification Java classes.<br>
 * This class extends the <code>javax.management.MBeanFeatureInfo</code> and thus provides
 * <code>name</code> and <code>description</code> fields. The name field should be the fully
 * qualified Java class name of the notification objects described by this class.<br>
 * The <code>getNotifTypes</code> method returns an array of strings containing the notification
 * types that the MBean may emit. The notification type is a dot notation string which describes
 * what the emitted notification is about, not the Java class of the notification. A single
 * generic notification class can be used to send notifications of several types. All of these
 * types are returned in the string array result of the <code>getNotifTypes</code> method.
 *
 * @author d025700
 */
public class MBeanNotificationInfoSupport extends MBeanNotificationInfo
        implements AdditionalInfo {
  /** Stores additional meta information */
  private Properties properties;

  /**
   * Creates a new MBeanNotificationInfoSupport object.
   *
   * @param notifTypes the array of strings (in dot notation) containing the notification types
   *        that the MBean may emit.
   * @param name the fully qualified Java class name of the described notifications.
   * @param description a human readable description of the notifications.
   * @param properties a set of additional properties that describe the notifications.
   */
  public MBeanNotificationInfoSupport(String[] notifTypes, String name, String description,
                                      Properties properties) {
    super(notifTypes, name, description);
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
              && this.properties.equals(((MBeanNotificationInfoSupport) obj).properties);
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