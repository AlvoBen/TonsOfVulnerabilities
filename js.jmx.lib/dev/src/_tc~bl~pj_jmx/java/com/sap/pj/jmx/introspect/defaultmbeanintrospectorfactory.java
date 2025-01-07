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

import javax.management.NotCompliantMBeanException;

/**
 * Creates {@link DefaultMBeanIntrospector <code>DefaultMBeanIntrospector</code>}.
 *
 * @author d025700
 *
 */
public class DefaultMBeanIntrospectorFactory implements MBeanIntrospectorFactory {

  /**
   * @see com.sap.pj.jmx.introspect.MBeanIntrospectorFactory#getMBeanIntrospector(Object, Class)
   */
  public MBeanIntrospector getMBeanIntrospector(Object mbean, Class mbeanInterface)
          throws NotCompliantMBeanException {
    return new DefaultMBeanIntrospector(mbean, mbeanInterface);
  }

}
