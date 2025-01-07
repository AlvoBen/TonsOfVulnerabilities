package com.sap.engine.frame.container.runtime;

import com.sap.engine.frame.ServiceException;

import java.util.Properties;

/**
 * Abstract class provides a way configure service runtime properties.
 * Services which has runtime changeable properties must register
 * <code>RuntimeConfiguration</code> implementation in its <code>start()<code> method
 * and unregistered it in the service <code>stop()</code> method
 * @see com.sap.engine.frame.state.ServiceState#registerRuntimeConfiguration(RuntimeConfiguration)
 * @see com.sap.engine.frame.state.ServiceState#unregisterRuntimeConfiguration()
 *
 * @author Dimitar Kostadinov
 */
public abstract class RuntimeConfiguration {

  /**
   * Updates service runtime changeable properties. The properties set must be applied or
   * rejected if some of the values is not acceptable.
   * Hierarchical properties are supported and updated properties can be cast to NestedProperties object.
   * @see com.sap.engine.frame.NestedProperties
   *
   * @param properties a set of changed service properties
   * @throws ServiceException if there is incorrect value and the hole set is not applied
   */
  public abstract void updateProperties(Properties properties) throws ServiceException;

}
