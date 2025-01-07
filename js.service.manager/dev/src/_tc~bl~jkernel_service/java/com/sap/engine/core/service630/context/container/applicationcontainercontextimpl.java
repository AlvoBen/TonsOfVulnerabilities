package com.sap.engine.core.service630.context.container;

import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.container.deploy.DeployContext;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.service630.context.container.deploy.DeployContextImpl;
import com.sap.engine.core.service630.context.container.registry.ObjectRegistryImpl;

/**
 * This class implements ApplicationContainerContext.
 *
 * @see com.sap.engine.frame.container.ApplicationContainerContext
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ApplicationContainerContextImpl implements ApplicationContainerContext {

  //service container
  private ServiceContainerImpl serviceContainer;
  //context owner
  private ServiceWrapper wrapper;

  //subcontexts
  private ObjectRegistry registry;
  private DeployContext deploy;

  public ApplicationContainerContextImpl(ServiceContainerImpl serviceContainer, ServiceWrapper wrapper) {
    this.serviceContainer = serviceContainer;
    this.wrapper = wrapper;
  }

  public SystemMonitor getSystemMonitor() {
    return serviceContainer;
  }

  public ObjectRegistry getObjectRegistry() {
    if (registry == null) {
      registry = new ObjectRegistryImpl(serviceContainer, wrapper);
    }
    return registry;
  }

  public synchronized DeployContext getDeployContext() {
    if (deploy == null) {
      deploy = new DeployContextImpl(serviceContainer, wrapper);
    }
    return deploy;
  }

}

