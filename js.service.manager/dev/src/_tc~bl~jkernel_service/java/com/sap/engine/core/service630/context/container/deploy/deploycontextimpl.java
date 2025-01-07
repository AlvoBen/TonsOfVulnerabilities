package com.sap.engine.core.service630.context.container.deploy;

import com.sap.engine.frame.container.deploy.DeployContext;
import com.sap.engine.frame.container.deploy.ComponentDeploymentException;
import com.sap.engine.frame.container.deploy.ComponentNotDeployedException;
import com.sap.engine.frame.container.deploy.zdm.RollingPatch;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;

import java.io.File;

/**
 * @see com.sap.engine.frame.container.deploy.DeployContext
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class DeployContextImpl implements DeployContext {

  private ServiceContainerImpl serviceContainer;
  private String initiator;
  private static final Location location = Location.getLocation(DeployContextImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  public DeployContextImpl(ServiceContainerImpl serviceContainer, ServiceWrapper wrapper) {
    this.serviceContainer = serviceContainer;
    this.initiator = wrapper.getComponentName();
  }

  public String deployInterface(File jar) throws ComponentDeploymentException {
    try {
      return serviceContainer.getMemoryContainer().deployInterface(jar, initiator);
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public String deployLibrary(File jar) throws ComponentDeploymentException {
    try {
      return serviceContainer.getMemoryContainer().deployLibrary(jar, initiator);
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public String deployService(File jar) throws ComponentDeploymentException {
    try {
      return serviceContainer.getMemoryContainer().deployService(jar, initiator);
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public void removeInterface(String providerName, String intfName) throws ComponentDeploymentException, ComponentNotDeployedException {
    try {
      serviceContainer.getMemoryContainer().removeInterface(providerName, intfName, initiator);
    } catch (ComponentNotDeployedException e) {
      throw e;
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public void removeLibrary(String providerName, String libName) throws ComponentDeploymentException, ComponentNotDeployedException {
    try {
      serviceContainer.getMemoryContainer().removeLibrary(providerName, libName, initiator);
    } catch (ComponentNotDeployedException e) {
      throw e;
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public void removeService(String providerName, String serviceName) throws ComponentDeploymentException, ComponentNotDeployedException {
    try {
      serviceContainer.getMemoryContainer().removeService(providerName, serviceName, initiator);
    } catch (ComponentNotDeployedException e) {
      throw e;
    } catch (ServiceException e) {
      throw new ComponentDeploymentException(location, e);
    }
  }

  public void applicationsStarted() {
    ((ClusterManager) Framework.getManager(Names.CLUSTER_MANAGER)).applicationsStarted();
  }

  public RollingPatch getRollingPatch() {
    return new RollingPatchImpl(serviceContainer.getMemoryContainer(), initiator);
  }

}