/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.services.memory.MemoryManager;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

import java.util.Properties;

public class MemoryApplicationFrame implements ApplicationServiceFrame {

  private ObjectRegistry objectRegistry = null;
  private ApplicationServiceContext serviceContext = null;
  private MemoryManagerImpl memory = null;

  /**
   * Category used for logging
   */
  public static Category category = Category.getCategory("/System/Server");

  /**
   * Location used for logging
   */
  public static Location location = Location.getLocation(MemoryApplicationFrame.class);

  public void start(ApplicationServiceContext serviceContext) throws ServiceException {
    try {
      this.serviceContext = serviceContext;
      objectRegistry = serviceContext.getContainerContext().getObjectRegistry();
      memory = new MemoryManagerImpl();
      memory.init(serviceContext.getServiceState().getProperties()); //, (TimeoutManager)objectRegistry.getServiceInterface("timeout"));
      serviceContext.getServiceState().registerManagementInterface(memory.manInterface);
      objectRegistry.registerInterface((MemoryManager) memory);
    } catch (Exception e) {
      category.errorT(location, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_STARTUP_EXCEPTION));
      throw new ServiceException(e);
    }
    location.pathT(category, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_SERVICE_STARTED));
  }

  public boolean changeProperties(Properties properties) throws IllegalArgumentException {
    return memory.changeProperties(properties);
  }

  public void stop() {
    try {
      memory.shutDown(serviceContext.getServiceState().getProperties());
    } finally {
      objectRegistry.unregisterInterface();
    }
    location.pathT(category, MemoryResourceAccessor.getString(MemoryResourceAccessor.MEMORY_SERVICE_STOPPED));
  }

  public ManagementInterface getManagementInterface() {
    return memory.manInterface;
  }

}

