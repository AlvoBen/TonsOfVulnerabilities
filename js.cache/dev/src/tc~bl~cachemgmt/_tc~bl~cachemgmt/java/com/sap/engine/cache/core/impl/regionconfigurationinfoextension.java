package com.sap.engine.cache.core.impl;

import java.util.Properties;

import com.sap.util.cache.RegionConfigurationInfo;

/**
 * @author Petio Petev (i024139)
 *
 */
public interface RegionConfigurationInfoExtension extends RegionConfigurationInfo {
  
  public long getCleanupInterval();
  public Properties getProperties();
  
}
