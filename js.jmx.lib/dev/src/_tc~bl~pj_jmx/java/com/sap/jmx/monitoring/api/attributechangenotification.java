/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.monitoring.api;

/**
 * Convenience class to create javax.management.AttributeChangeNotification. This is
 * useful only in conjunction with the ResourceMBeanWrapper which automatically adds
 * missing values like source, sequenceNumber, and timeStamp.
 * 
 * @author d025700
 *
 */
public class AttributeChangeNotification extends javax.management.AttributeChangeNotification {

  /**
   * @param attributeName
   * @param attributeType
   * @param newValue
   */
  public AttributeChangeNotification(String attributeName, String attributeType, Object oldValue, Object newValue) {
    super("dummy_source", 0, 0, "Value of attribute " + attributeName + " changed.", attributeName, attributeType, oldValue, newValue);
  }

}
