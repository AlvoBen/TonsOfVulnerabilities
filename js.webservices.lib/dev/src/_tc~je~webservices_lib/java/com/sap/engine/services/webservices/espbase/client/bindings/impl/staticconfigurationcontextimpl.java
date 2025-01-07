/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.webservices.espbase.client.bindings.impl;

import com.sap.engine.services.webservices.espbase.ConfigurationContextImpl;
import com.sap.engine.services.webservices.espbase.client.bindings.StaticConfigurationContext;
import com.sap.engine.services.webservices.espbase.configuration.BindingData;
import com.sap.engine.services.webservices.espbase.configuration.InterfaceData;
import com.sap.engine.services.webservices.espbase.mappings.InterfaceMapping;

/**
 * Client static config implementation.
 * @version 1.0
 * @author Chavdar Baikov, chavdar.baikov@sap.com
 */
public class StaticConfigurationContextImpl extends ConfigurationContextImpl implements StaticConfigurationContext {
  
  public static final String DT_CONFIG = "DTConfig";
  public static final String RT_CONFIG = "RTConfig";
  public static final String INTERFACE_MAPPING = "IMapping"; 
  
  /**
   * Default Constructor.
   */  
  public StaticConfigurationContextImpl() {
    super("StaticConfig",null,ConfigurationContextImpl.STATIC_MODE);
  }

  /**
   * Returns DTConfig information.
   * @return
   */
  public InterfaceData getDTConfig() {
    return (InterfaceData) super.getProperty(DT_CONFIG);
  }
  
  /**
   * Sets DTConfig information.
   * @param idata
   */
  public void setDTConfig(InterfaceData idata) {
    properties.put(DT_CONFIG,idata);
  }

  /**
   * Returns interface mapping information.
   * @return
   */
  public InterfaceMapping getInterfaceData() {
    return (InterfaceMapping) super.getProperty(INTERFACE_MAPPING);
  }
  
  /**
   * Sets the interface mapping information.
   * @param iMapping
   */
  public void setInterfaceData(InterfaceMapping iMapping) {
    properties.put(INTERFACE_MAPPING,iMapping);
  }

  /**
   * Returns the binding configuration.
   * @return
   */
  public BindingData getRTConfig() {
    return (BindingData) super.getProperty(RT_CONFIG);
  }
  
  /**
   * Sets the binding configuration.
   * @param bindingData
   */
  public void setRTConfig(BindingData bindingData) {
    properties.put(RT_CONFIG,bindingData);
  }

}
