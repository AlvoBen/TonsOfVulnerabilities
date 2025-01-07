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
package com.sap.jmx.modelhelper;

import com.sap.pj.jmx.exception.ResourceAccessor;
import com.sap.exception.BaseException;

/**
 * MBeanProxyFactoryException
 */
public class MBeanProxyFactoryException extends BaseException {

  public MBeanProxyFactoryException() {
    super();
  }

  public MBeanProxyFactoryException(Throwable cause) {
    super(cause);
  }

  public MBeanProxyFactoryException(String resourceId) {
    super(ResourceAccessor.getInstance(), resourceId, null, null);
  }

  public MBeanProxyFactoryException(String resourceId, Throwable cause) {
    super(ResourceAccessor.getInstance(), resourceId, null, cause);
  }

  public MBeanProxyFactoryException(String resourceId, Object[] args) {
    super(ResourceAccessor.getInstance(), resourceId, args, null);
  }

  public MBeanProxyFactoryException(String resourceId, Object[] args, Throwable cause) {
    super(ResourceAccessor.getInstance(), resourceId, args, cause);
  }

}
