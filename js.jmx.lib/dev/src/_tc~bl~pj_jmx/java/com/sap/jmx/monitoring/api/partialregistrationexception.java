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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Wraps multiple exceptions during registration/un-registration of MBeans.
 * @author d025700
 *
 */
public class PartialRegistrationException extends MBeanManagerException {

  private transient HashMap failedRegistrations;
  
  /**
   * @param failedRegistrations
   */
  public PartialRegistrationException(final HashMap failedRegistrations) {
    super();
    this.failedRegistrations = failedRegistrations;
  }

  /**
   * @param message
   * @param failedRegistrations
   */
  public PartialRegistrationException(String message, final HashMap failedRegistrations) {
    super(message);
		this.failedRegistrations = failedRegistrations;
  }
  
  /**
   * @return the names of the MBeans the registration/un-registration of which failed.
   */
  public String[] getNamesOfFailedMBeans() {
		if (failedRegistrations == null) {
			return new String[0];
		}
  	Set keys = failedRegistrations.keySet();
  	String[] names = new String[keys.size()];
  	int i = 0;
  	for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
  		names[i++] = (String) iter.next();
  	}
  	return names;
  }

	/**
	 * @return the cause of the failure for a given MBean.
	 */
	public Throwable getCauseForName(String name) {
		if (failedRegistrations == null) {
			return null;
		}
		return (Throwable) failedRegistrations.get(name);
	}

}
