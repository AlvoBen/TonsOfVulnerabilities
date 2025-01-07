package com.sap.engine.session;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-10-6
 * Time: 13:54:21
 */
public abstract class GraphBuilder {
  protected static GraphBuilder builderImpl = null;

  public static void saveSession(Session session) {
    if (builderImpl != null) {
      builderImpl.saveSessionAsGraph(session);
    }
  }

  public abstract void saveSessionAsGraph(Session session);

  public static void setBuilderImpl(GraphBuilder impl) {
    builderImpl = impl;
  }
}
