/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.usr;

import javax.security.auth.Subject;
import java.security.Principal;
import java.io.Serializable;

/**
 * Author: Georgi-S
 * Date: 2005-4-13
 */
public interface LoginSession extends Serializable {

  public Object identity();

  public Subject getSubject();

  public String getUserName();

  public long getExpirationPeriod();

  public void setExpirationPeriod(long period);

  public boolean isAnonymous();

  public Principal getPrincipal();

}