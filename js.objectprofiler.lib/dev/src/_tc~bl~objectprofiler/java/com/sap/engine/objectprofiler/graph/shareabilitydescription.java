package com.sap.engine.objectprofiler.graph;

import java.io.Serializable;
import java.util.HashMap;

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
 * Date: 2005-6-17
 * Time: 10:56:54
 */
public class ShareabilityDescription implements Serializable {
  private String className = null;
  private boolean shareable = true;
  private String reason = null;
  private HashMap flags = new HashMap();

  static final long serialVersionUID = -5283773381458098827L;

  public ShareabilityDescription(String className, boolean shareable, String reason) {
    this.className = className;
    this.shareable = shareable;
    this.reason = reason;
  }

  public String getClassName() {
    return className;
  }

  public boolean getShareable() {
    return shareable;
  }

  public String getReason() {
    return reason;
  }

  public void setFlags(HashMap map) {
    this.flags = map;
  }

  public HashMap getFlags() {
    return flags;
  }
}
