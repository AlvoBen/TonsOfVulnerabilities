package com.sap.engine.core.service630.container;

import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.tc.logging.Location;

import java.util.Properties;

/**
 * RuntimeConfiguration wrapper is used to track the caller trace.
 *
 * @author Dimitar Kostadinov
 *
 * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
 */
public class RuntimeConfigurationWrapper extends RuntimeConfiguration  {

  //for update trace 
  private static final Location location = Location.getLocation(RuntimeConfigurationWrapper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  private RuntimeConfiguration runtimeConfiguration;
  private ServiceWrapper service;

  RuntimeConfigurationWrapper(RuntimeConfiguration runtimeConfiguration, ServiceWrapper service) {
    this.runtimeConfiguration = runtimeConfiguration;
    this.service = service;
  }

  /**
   * @see com.sap.engine.frame.container.runtime.RuntimeConfiguration
   */
  public void updateProperties(Properties properties) throws ServiceException {
    NestedProperties currentProperties = service.getCurrentProperties();
    if (currentProperties == null) {
      runtimeConfiguration.updateProperties(properties);
    } else {
      runtimeConfiguration.updateProperties((Properties) properties.clone());
      PropertiesEventHandler.applyChanges(currentProperties, properties);
    }
    if (location.beInfo()) {
      location.infoT(ResourceUtils.formatString(ResourceUtils.TRACE_PROPERTIES_UPDATE, new Object[] {service.getComponentName(), properties.toString()}));
    }
  }

}