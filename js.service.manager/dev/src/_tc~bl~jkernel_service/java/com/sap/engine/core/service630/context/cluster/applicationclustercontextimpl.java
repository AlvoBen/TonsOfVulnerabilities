package com.sap.engine.core.service630.context.cluster;

import com.sap.engine.frame.cluster.ApplicationClusterContext;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.cluster.impl6.ClusterManagerImpl;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;

/**
 * @see com.sap.engine.frame.cluster.ApplicationClusterContext
 *
 * @author Kaloyan Raev, Dimitar Kostadinov
 * @version 710
 */
public class ApplicationClusterContextImpl implements ApplicationClusterContext {

  private ServiceWrapper wrapper;
  private ClusterManagerImpl clusterManager;

  private MessageContext messageContext;
  private ClusterMonitor clusterMonitor;

  public ApplicationClusterContextImpl(ServiceWrapper wrapper) {
    this.wrapper = wrapper;
    clusterManager = (ClusterManagerImpl) Framework.getManager(Names.CLUSTER_MANAGER);
  }

  /** Use synchronization because only one instance per service must be created */
  public synchronized MessageContext getMessageContext() {
    if (messageContext == null) {
      messageContext = clusterManager.getMessageContext(wrapper.getComponentName());
    }
    return messageContext;
  }

  public ClusterMonitor getClusterMonitor() {
    if (clusterMonitor == null) {
      clusterMonitor = clusterManager.getClusterMonitor(wrapper.getComponentName());
    }
    return clusterMonitor;
  }

}