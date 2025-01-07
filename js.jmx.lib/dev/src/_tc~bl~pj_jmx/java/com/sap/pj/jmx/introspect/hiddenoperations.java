/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.introspect;

/**
 * Defines the set of hidden operations provided by SAP-JMX. Clients cannot rely on the presence of
 * those methods in general since they are noly available for satndard MBeans that are registered
 * with the MBeanServer either directly or using one of the standard wrappers.
 *
 * @author d025700
 */
public interface HiddenOperations {
  /** Returns the class name of the MBean interface. */
  public static final String GET_MBEAN_INTERFACE = "_getMBeanInterface";
}