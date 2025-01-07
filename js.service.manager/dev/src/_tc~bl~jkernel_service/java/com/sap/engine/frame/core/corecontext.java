/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame.core;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.pool.PoolContext;
import com.sap.engine.frame.core.reflect.ReflectContext;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.database.DatabaseContext;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.core.licensing.LicensingContext;
import com.sap.engine.frame.core.cache.CacheContext;

/**
 * This context provides access to the core of the system.
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface CoreContext {

  /**
   * Get ThreadSystem interface that is used for work with threads. It is
   * server specific thread pooling.
   *
   * @return  ThreadSystem for work with treads
   */
  public ThreadSystem getThreadSystem();


  /**
   * Get LoadContext for accessing internal classloaders hierarchy.
   *
   * @return  LoadContext for use when access to internal classloading is
   * needed.
   */
  public LoadContext getLoadContext();


  /**
   * Get ReflectContext for direct access to the core.
   *
   * @return  ReflectContext for direct access to the core
   */
  public ReflectContext getReflectContext();


  /**
   * PoolContext is used to access a kernel pools of byte arrays with
   * different (fixed) size. The sizes are: 1K; 2K; 4K; 8K; 16K; 32K; 64K; 128K
   *
   * @return  PoolContext for access to the kernel byte array pools
   */
  public PoolContext getPoolContext();


  /**
   * LockingContext is used for handling of shared and exclusive locks.
   *
   * @return LockingContext for handling of shared and exclusive locks.
   */
  public LockingContext getLockingContext();


  /**
   * Database context is used for accessing the database.
   * 
   * @return DatabaseContext for accessing the database
   */
  public DatabaseContext getDatabaseContext();


  /**
   * ConfigurationHandlerFactory is used for managing persistent configuration.
   *
   * @return ConfigurationHandlerFactory for managing persistent configuration.
   */
  public ConfigurationHandlerFactory getConfigurationHandlerFactory();


  /**
   * ConfigurationMonitor is used for get/set properties of the system kernel.
   * Through this context service can stop or restart current node from the
   * system.
   *
   * @return ConfigurationMonitor for administration of the kernel.
   */
  public CoreMonitor getCoreMonitor();

  /**
  * LicensingContext is used for administration of licenses.
  *
  * @return LicensingContext for administration of licenses.
  */
  public LicensingContext getLicensingContext();
  
  /**
   * Cache context is used by containers that will deploy and configure
   * cache regions
   * 
   * @return CacheContext singleton (no service differentiation)
   */
  public CacheContext getCacheContext();

}

