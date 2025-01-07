package com.sap.engine.core.service630.context;

import com.sap.engine.core.service630.context.cluster.ApplicationClusterContextImpl;
import com.sap.engine.core.service630.context.container.ApplicationContainerContextImpl;
import com.sap.engine.core.service630.context.core.CoreContextImpl;
import com.sap.engine.core.service630.context.state.ServiceStateImpl;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.frame.cluster.ApplicationClusterContext;
import com.sap.engine.frame.container.ApplicationContainerContext;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.state.ServiceState;
import com.sap.engine.frame.core.CoreContext;

/**
 * This class implements ApplicationServiceContext.
 *
 * @see com.sap.engine.frame.ApplicationServiceContext
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ApplicationServiceContextImpl implements ApplicationServiceContext {

  //service container
  private ServiceContainerImpl container;
  //context owner
  private ServiceWrapper wrapper;

  //subcontexts
  private CoreContext coreContext;
  private ServiceState serviceState;
  private ApplicationContainerContext containerContext;
  private ApplicationClusterContextImpl clusterContext;

  public ApplicationServiceContextImpl(ServiceContainerImpl container, ServiceWrapper wrapper) {
    this.container = container;
    this.wrapper = wrapper;
  }

  public synchronized CoreContext getCoreContext() {
    if (coreContext == null) {
      coreContext = new CoreContextImpl(container, wrapper);
    }
    return coreContext;
  }

  public synchronized ServiceState getServiceState() {
    if (serviceState == null) {
      serviceState = new ServiceStateImpl(container, wrapper);
    }
    return serviceState;
  }

  public ApplicationContainerContext getContainerContext() {
    if (containerContext == null) {
      containerContext = new ApplicationContainerContextImpl(container, wrapper);
    }
    return containerContext;
  }

  public ApplicationClusterContext getClusterContext() {
    if (clusterContext == null) {
      clusterContext = new ApplicationClusterContextImpl(wrapper);
    }
    return clusterContext;
  }

}