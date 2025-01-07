package com.sap.engine.objectprofiler.view.utils;

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
 * Date: 2005-5-27
 * Time: 14:25:44
 */
public class CacheInfo {
  public static final int TYPE_ROOT = -1;
  public static final int TYPE_CACHE_REGION = 0;
  public static final int TYPE_CACHE_GROUP = 1;
  public static final int TYPE_CACHE_NAME = 2;

  public static final String EMPTY_STRING = "J2EE Engine";

  private String name = null;
  private String group = null;
  private String region = null;
  private int type = TYPE_CACHE_NAME;

  public CacheInfo(String name, int type) {
    this.name = name;
    this.type = type;
  }

  public CacheInfo(String region, String group, String name, int type) {
    this.region = region;
    this.group = group;
    this.name = name;
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getGroup() {
    return group;
  }

  public String getRegion() {
    return region;
  }

  public static CacheInfo buildRoot() {
    return new CacheInfo(EMPTY_STRING, TYPE_ROOT);
  }

  public String toString() {
    return name;
  }
}
