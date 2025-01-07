package com.sap.engine.cache.core.impl;

import com.sap.util.cache.spi.PluginContext;

/**
 * @author Petev, Petio, i024139
 */
public class PluginContextImpl implements PluginContext {

  private String regionName = null;

  private int regionId = -1;

  protected PluginContextImpl() {
  }

  protected PluginContextImpl(String regionName, int regionId) {
    this.regionName = regionName;
    this.regionId = regionId;
  }

  public String getRegionName() {
    return regionName;
  }

  public int getRegionID() {
    return regionId;
  }

  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }

  public void setRegionID(int regionId) {
    this.regionId = regionId;
  }

}
