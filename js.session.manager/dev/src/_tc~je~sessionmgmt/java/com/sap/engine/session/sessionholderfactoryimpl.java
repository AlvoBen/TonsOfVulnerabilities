/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;

/**
 * Author: georgi-s
 * Date: 2005-4-1
 */
public class SessionHolderFactoryImpl implements SessionHolderFactory {

  static SessionHolderFactory holderFactory = new SessionHolderFactoryImpl();

  public static SessionHolderFactory getDefaultFactory() {
    return holderFactory;
  }

  public AbstractSessionHolder getInstance(String name, SessionDomain domain) {
    return new SessionHolderImpl(name, domain);
  }
}
