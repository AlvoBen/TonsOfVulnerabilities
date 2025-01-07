package com.sap.engine.core.service630.context.core;

import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.licensing.LicensingContext;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.cache.CacheContext;
import com.sap.engine.frame.core.database.DatabaseContext;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.reflect.ReflectContext;
import com.sap.engine.frame.core.pool.PoolContext;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.core.service630.container.ServiceContainerImpl;
import com.sap.engine.core.service630.container.ServiceWrapper;
import com.sap.engine.core.service630.context.core.monitor.CoreMonitorImpl;
import com.sap.engine.core.service630.context.core.thread.ThreadSystemImpl;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cache.pp.CacheManager;

/**
 * @see com.sap.engine.frame.core.CoreContext
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class CoreContextImpl implements CoreContext {

  private ServiceContainerImpl container;
  private ServiceWrapper wrapper;

  private CoreMonitor coreMonitor;
  private ThreadSystemImpl threadSystem;

  public CoreContextImpl(ServiceContainerImpl container, ServiceWrapper wrapper) {
    this.container = container;
    this.wrapper = wrapper;
  }

  //return load context wrapper
  public LoadContext getLoadContext() {
    return container.getLoadContext();
  }

  /**
   * @deprecated
   */
  public PoolContext getPoolContext() {
    return (PoolContext) Framework.getManager(Names.POOL_MANAGER);
  }

  public ReflectContext getReflectContext() {
    return container;
  }

  public LockingContext getLockingContext() {
    return (LockingContext) Framework.getManager(Names.LOCKING_MANAGER);
  }

  public LicensingContext getLicensingContext() {
    return (LicensingContext) Framework.getManager(Names.LICENSING_MANAGER);
  }

  public DatabaseContext getDatabaseContext() {
    return (DatabaseContext) Framework.getManager(Names.DATABASE_MANAGER);
  }

  public ConfigurationHandlerFactory getConfigurationHandlerFactory() {
    return (ConfigurationHandlerFactory) Framework.getManager(Names.CONFIGURATION_MANAGER);
  }

  public CacheContext getCacheContext() {
    return ((CacheManager) Framework.getManager(Names.CACHE_MANAGER)).getCacheContext();
  }

  public ThreadSystem getThreadSystem() {
    if (threadSystem == null) {
      threadSystem = new ThreadSystemImpl();
    }
    return threadSystem;
  }

  public CoreMonitor getCoreMonitor() {
    if (coreMonitor == null) {
      coreMonitor = new CoreMonitorImpl(container, wrapper);
    }
    return coreMonitor;
  }

}