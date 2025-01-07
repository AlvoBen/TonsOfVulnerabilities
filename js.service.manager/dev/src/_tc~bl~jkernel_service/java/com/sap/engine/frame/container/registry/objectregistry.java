/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.frame.container.registry;

/**
 * ObjectRegistry provides functionality to register service interfaces and implementation providers.
 * This class gives the services, the opportunity to use a centralized system, offering the functionality
 * of other, already loaded services and managers.
 *
 * @author Dimitar Kostadinov
 */
public interface ObjectRegistry {

  /**
   * Registers service interface
   *
   * @param serviceInterface interface implementation
   */
  public void registerInterface(Object serviceInterface);

  /**
   * Unregisters service interface
   */
  public void unregisterInterface();

  /**
   * Returns the service interface by a given service name
   *
   * @param name String representing the name of the service
   * @return An interface to the service corresponding to the given name
   */
  public Object getServiceInterface(String name);

  /**
   * Registers provider interface
   *
   * @param interfaceName String representing service name
   * @param interfaceImpl implementation of the provider
   */
  public void registerInterfaceProvider(String interfaceName, Object interfaceImpl);

  /**
   * Unregisters provider interface
   *
   * @param interfaceName String representing interface name to unregister
   */
  public void unregisterInterfaceProvider(String interfaceName);

  /**
   * Returns the provided interface
   *
   * @param interfaceName String representing the name of the interface
   * @return interface to the implementation of the provider
   */
  public Object getProvidedInterface(String interfaceName);

}
