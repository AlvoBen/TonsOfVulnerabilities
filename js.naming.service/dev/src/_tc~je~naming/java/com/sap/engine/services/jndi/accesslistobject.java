/*
 * Copyright (c) 2000 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.jndi;

import java.io.Serializable;

/**
 * @author Nikolay Dimitrov
 * @version 6.30
 */
public class AccessListObject implements Serializable {

  private String name;
  boolean isUser;
  static final long serialVersionUID = 1627211751986979529L;

  public AccessListObject(String name, boolean isUser) {
    this.name = name;
    this.isUser = isUser;
  }

  public boolean isUser() {
    return isUser;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return name;
  }
}

