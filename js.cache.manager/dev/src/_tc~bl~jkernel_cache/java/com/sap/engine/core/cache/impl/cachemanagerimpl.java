package com.sap.engine.core.cache.impl;

import java.util.*;
import java.lang.management.*;

import javax.management.*;

import com.sap.bc.proj.jstartup.sadm.ShmCache;
import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.admin.impl.RegionConfigurationImpl;
import com.sap.engine.cache.core.impl.CacheFactoryListener;
import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.core.impl.PluggableFramework;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.cluster.ClusterManager;
import com.sap.engine.core.cache.impl.test.bench.BenchFramework;
import com.sap.engine.core.cache.impl.mbeans.LocalCacheManagement;
import com.sap.engine.core.cache.impl.mbeans.GlobalCacheManagement;
import com.sap.engine.core.cache.pp.CacheManager;
import com.sap.engine.core.configuration.impl.addons.processors.CacheXML;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.core.cache.*;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.spi.Pluggable;

/**
 * @author Petev, Petio, i024139
 */
public class CacheManagerImpl implements CacheManager, CacheFactoryListener {

  /**
   * Location object used for tracing
   */
  private static final Location LOCATION = Location.getLocation(CacheManagerImpl.class.getName(), Names.KERNEL_DC_NAME, Names.CACHE_MANAGER_CSN_COMPONENT);

  // contains own monitors that aim to transfer data into the monitoring in shared memory
  private Hashtable monitors;

  // used to distribute events throughout the cluster
  private GlobalNotificationSafe gNotification;

  private boolean benchToolAvailable;
  private boolean monitoringEnabled;
  
  private ObjectName localCacheManagementMBeanName = null;
  private ObjectName globalCacheManagementMBeanName = null;

  private static BenchFramework benchInstance;
  private static CCDeployImpl deployInstance;
  private static RegionInquiryImpl inquiryInstance;

  private void createKernelRegions() {
    Map config = (Map) Framework.getKernelObject("CACHE_SETUP");//arg;
    // preventing a NullPointer in case of failed read from the Framework
    if (config != null) {
      if (!(config instanceof Hashtable)) {
        Map mGlobal = (Map) config.get(CacheXML.NAME_GLOBAL);
        Map mRegions = (Map) config.get(CacheXML.NAME_REGIONS);
        if (mGlobal != null) {
          deployInstance.setGlobalConfiguration(mGlobal);
        }
        if (mRegions != null) {
          try {
            deployInstance.createRegions(null, mRegions);
          } catch (CacheContextException e) {
            SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
                "createKernelRegions()", e);
            throw new RuntimeException(e); 
          }
        }
      } else {
        // old bootstrap!!!
        RuntimeException rte = new RuntimeException("Region configurations was not read because an old version of bootstrap was executed.");
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "createKernelRegions()", rte);
        throw rte; 
      }
    } else {
      RuntimeException rte = new RuntimeException("Region configurations was not read. Please check Bootstrap logs for any hints about the problem.");
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
          "createKernelRegions()", rte);
      throw rte; 
    }
  }
  

  public boolean init(Properties properties) {

    // parse properties
    monitoringEnabled = "true".equals(properties.getProperty("cache.monitoring.enabled"));
    benchToolAvailable = "true".equals(properties.getProperty("cache.benchmark.enabled"));
    
    // this hashtable will contain monitor hooks that will transfer the monitoring info
    // to shared memory based monitoring
    monitors = new Hashtable();

    new InternalRegionFactory();

    deployInstance = new CCDeployImpl(monitors);
    
    inquiryInstance = new RegionInquiryImpl();
    
    // hooking monitors for already existing regions
    CacheRegionFactory factory = InternalRegionFactory.getInstance(); 
    Iterator regions = factory.iterateRegions();
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      if (!("default".equals(regionName))) {
        CacheRegion region = factory.getCacheRegion(regionName);
        onFirstUseCacheRegion(region.getRegionConfigurationInfo().getName(), region);
      }
    }

    // registration as FactoryListener will provide control over
    // newly created cache regions
    InternalRegionFactory.setListener(this);
    
    // init default pluggables
    try {
		  factory.initDefaultPluggables();
	  } catch (CacheException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "initDefaultPluggables()", e);
	  }

    
    // global notification provides invalidation messages distribution using cluster
    // manager
    gNotification = new GlobalNotificationSafe();
    try {
      gNotification.construct();
    } catch (ClusterException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "init()", e);
    }

//    try {
//      PluggableFramework.putStoragePlugin("DBStorage", new DBStorage());
//    } catch (Exception e) {
//      PluggableFramework.putStoragePlugin("DBStorage", new HashMapStorage());
//      catServer.logThrowableT(Severity.ERROR, loc, "init()", e);
//    }
    
    // Hardcode for PRT
    
//    Pluggable prtStorage = new CombinatorStorageWriteTrue();
//    try {
//      Properties props = new Properties();
//      props.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage");
//      props.setProperty("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
//      prtStorage.init("PRTStorage", props);
//      PluggableFramework.putPluggable("PRTStorage", prtStorage);
//    } catch (PluginException e) {
//      catServer.logThrowableT(Severity.ERROR, loc, "init()", e);
//    }
//    
//    // Hardcode for PRT Application Cache
//
//    Pluggable prtAppStorage = new CombinatorStorageWriteTrue();
//    try {
//      Properties props = new Properties();
//      props.setProperty("CombinatorStorage.FRONTEND_STORAGE", "HashMapStorage"); //"CommonSCMappableStoragePlugin"
//      props.setProperty("CombinatorStorage.BACKEND_STORAGE", "DBStorage");
//      prtStorage.init("PRTAppStorage", props);
//      PluggableFramework.putPluggable("PRTAppStorage", prtAppStorage);
//    } catch (PluginException e) {
//      catServer.logThrowableT(Severity.ERROR, loc, "init()", e);
//    }
    
    createKernelRegions();

    registerMBeans();

    return true;
  }

  /**
   * Registers the mBeans for administration/configuration
   */
  private void registerMBeans() {
    ClusterManager clusterManager = (ClusterManager) Framework.getManager("ClusterManager");
    int clusterId = clusterManager.getClusterMonitor().getCurrentParticipant().getClusterId();

    try {
      localCacheManagementMBeanName = new ObjectName(CacheManagementNames.LOCAL_CACHE_MANAGEMENT_MBEAN_NAME + clusterId);
    } catch (MalformedObjectNameException e) {
      SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Incorrect MBean name for the LocalCacheManagementMBean; MBeans will not be registered", e);
      return;
    } 

    try {
      globalCacheManagementMBeanName = new ObjectName(CacheManagementNames.GLOBAL_CACHE_MANAGEMENT_MBEAN_NAME);
    } catch (MalformedObjectNameException e) {
      SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Incorrect MBean name for the GlobalCacheManagementMBean; MBeans will not be registered", e);
      return;
    } 

    LocalCacheManagement localCacheManagementMBean = new LocalCacheManagement(this, clusterId);
    GlobalCacheManagement globalCacheManagementMBean = new GlobalCacheManagement();

    MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

    String mbeanName ="LocalCacheManagementMBean";
    try {
      if(localCacheManagementMBeanName != null){
        mbeanServer.registerMBean(localCacheManagementMBean, localCacheManagementMBeanName);
      }
    } catch (InstanceAlreadyExistsException e) {
      SimpleLogger.traceThrowable(Severity.DEBUG, LOCATION, mbeanName + " already exists on the mbean server", e);
    } catch (MBeanRegistrationException e) {
      SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, mbeanName + " cannot be registered; cache on this node will not be manageable through mbeans", e);
    } catch (NotCompliantMBeanException e) {
      SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, mbeanName + " cannot be registered; cache on this node will not be manageable through mbeans", e);
    }
    
    mbeanName = "GlobalCacheManagementMBean";
    try {
        
        if(globalCacheManagementMBeanName != null){
          mbeanServer.registerMBean(globalCacheManagementMBean, globalCacheManagementMBeanName);
        }
      } catch (InstanceAlreadyExistsException e) {
        SimpleLogger.traceThrowable(Severity.DEBUG, LOCATION, mbeanName + " already exists on the mbean server", e);
      } catch (MBeanRegistrationException e) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, mbeanName + " cannot be registered; global cache operations will not be possible to be called from this node", e);
      } catch (NotCompliantMBeanException e) {
        SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, mbeanName + " cannot be registered; global cache operations will not be possible to be called from this node", e);
      }
  }
  
  /**
   * Unregisters the mBeans for administration/configuration
   */
  private void unregisterMBeans(){
	  String mbeanName = null;
	  try{
		  MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

		  mbeanName = "LocalCacheManagementMBean";

		  if(localCacheManagementMBeanName != null){
			  mbeanServer.unregisterMBean(localCacheManagementMBeanName);
		  }

		  mbeanName = "GlobalCacheManagementMBean";
		  if(globalCacheManagementMBeanName != null){
			  mbeanServer.unregisterMBean(globalCacheManagementMBeanName);
		  }
	  } catch (InstanceNotFoundException e) {
		  SimpleLogger.traceThrowable(Severity.DEBUG, LOCATION, mbeanName + " does not exist on the mbean server", e);
	  } catch (MBeanRegistrationException e) {
		  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, mbeanName + " cannot be unregistered", e);
	  }	catch (RuntimeOperationsException e) {
		  SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "The Cache management MBeans were not unregistered during the stop of the Cache Manager; this might cause problems when starting the manager again", e);
	  }
  }

  public static void traceT(Throwable t) {
    SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "<CacheManager>", t);
  }
  
  public void loadAdditional() {
  }

  public boolean setProperty(String s, String s1) throws IllegalArgumentException {
    return false;
  }

  public boolean setProperties(Properties properties) throws IllegalArgumentException {
    return false;
  }

  /** @see com.sap.engine.core.Manager#updateProperties(Properties) */
  public void updateProperties(Properties properties) {
    //todo impl
  }

  public String getCurrentProperty(String s) {
    return null;
  }

  public void shutDown(Properties properties) {
	unregisterMBeans();
	
    gNotification.destruct();
    Iterator shmMonIterator = monitors.values().iterator();
    while (shmMonIterator.hasNext()) {
      ShmMonitor shmMonitor = (ShmMonitor) shmMonIterator.next();
      shmMonitor.close(); 
    }
    InternalRegionFactory.getReferenceQueue().stop();
    if (benchInstance != null) {
      benchInstance.kill();
    }
  }

  public Properties getCurrentProperties() {
    return null;
  }

  public byte getStatus() {
    return 0;
  }

  public String getDebugInfo(int i) {
    // this is the information that one should be able to see using the shell
    // commands of the engine
    String result = null;
    if (benchToolAvailable) {
      if (benchInstance == null) {
        benchInstance = new BenchFramework();
      }
      // turn from DEC to HEX
      try {
        int transformed = new Integer(Integer.toHexString(i)).intValue();
        return benchInstance.parseCommand(transformed);
      } catch (NumberFormatException e) {
        traceT(e);
        return benchInstance.parseCommand(0);
      }
    } else {
      StringBuffer sb = new StringBuffer();
      sb.append("\n==================================================================");
      sb.append("\nCaches Information");
      Iterator regions = CacheRegionFactory.getInstance().iterateRegions();
      while (regions.hasNext()) {
        String regionName = (String) regions.next();
        CacheRegion cacheRegion = CacheRegionFactory.getInstance().getCacheRegion(regionName);
        RegionConfigurationInfo configuration = cacheRegion.getRegionConfigurationInfo();
        Monitor monitor = MonitorsAccessor.getMonitor(configuration.getName());
        sb.append("\n------------------------------------------------------------------");
        sb.append(configuration.toString());
        sb.append("\n");
        sb.append(monitor.toString());
      }
      sb.append("\n==================================================================");
      result = sb.toString();
    }
    return result;
  }

  public ManagementInterface getManagementInterface() {
    return null;
  }

//  public void onGetCacheRegion(String region, CacheRegion result) {
//    // this is FactoryListener method
//    // it gets the monitor of the region that has been
//    // just created and hooks an own one to it
//    // the hooked monitor is saved in a hashtable
//    if (monitoringEnabled) {
//      if (!monitors.containsKey(region)) {
//        Monitor monitor = MonitorsAccessor.getMonitor(region);
//        CacheRegionImpl regionImpl = (CacheRegionImpl) result;
//        RegionConfigurationInfo config = regionImpl.getRegionConfiguration();
//        String owner = "Unknown";
//        String description = config.getName();
//        if (config instanceof RegionConfigurationImpl) {
//          RegionConfigurationImpl extendedConfig = (RegionConfigurationImpl) regionImpl.getRegionConfiguration();
//          Properties props = extendedConfig.getProperties();
//          if (props != null) {
//            description = props.getProperty(CacheXML.PROP_DESCRIPTION, description);
//            // owner = props.getProperty(CacheXML.PROP_OWNER, owner);
//            owner = props.getProperty("_OWNER", owner);
//          }
//        }
//        ShmMonitor shmMonitor = new ShmMonitor(monitor, owner, description, 
//          result.getRegionConfigurationInfo().getRegionScope() < RegionConfigurationInfo.SCOPE_INSTANCE ? 
//          ShmCache.MODE_LOCAL : 
//          ShmCache.MODE_SHARED);
//        MonitorsAccessor.getMonitor(region).setHook(shmMonitor);
//        monitors.put(region, shmMonitor);
//      }
//    }
//
//  }

  public void onFirstUseCacheRegion(String region, CacheRegion result) {
    // this is FactoryListener method
    // it gets the monitor of the region that has been
    // just created and hooks an own one to it
    // the hooked monitor is saved in a hashtable
    if (monitoringEnabled) {
      if (!monitors.containsKey(region)) {
        Monitor monitor = MonitorsAccessor.getMonitor(region);
        CacheRegionImpl regionImpl = (CacheRegionImpl) result;
        RegionConfigurationInfo config = regionImpl.getRegionConfiguration();
        String owner = "Unknown";
        String description = config.getName();
        if (config instanceof RegionConfigurationImpl) {
          RegionConfigurationImpl extendedConfig = (RegionConfigurationImpl) regionImpl.getRegionConfiguration();
          Properties props = extendedConfig.getProperties();
          if (props != null) {
            description = props.getProperty(CacheXML.PROP_DESCRIPTION, description);
            // owner = props.getProperty(CacheXML.PROP_OWNER, owner);
            owner = props.getProperty("_OWNER", owner);
          }
        }
        ShmMonitor shmMonitor = new ShmMonitor(monitor, owner, description, 
          result.getRegionConfigurationInfo().getRegionScope() < RegionConfigurationInfo.SCOPE_INSTANCE ? 
          ShmCache.MODE_LOCAL : 
          ShmCache.MODE_SHARED);
        MonitorsAccessor.getMonitor(region).setHook(shmMonitor);
        monitors.put(region, shmMonitor);
      }
    }

  }


  private void _initPluggable(Pluggable pluggable, Properties properties) {
    PluggableHolder holder = PluggableHolder.createHolder(pluggable, properties);
    if (holder != null) {
      if (holder.init()) {
        PluggableFramework.putPluggable(holder.getName(), holder.getInstance());
      }
    }
  }

  public CacheContext getCacheContext() {
    return StaticCacheContext.getInstance();
  }
  
  private static class StaticCacheContext implements CacheContext {
    
    private static StaticCacheContext instance = new StaticCacheContext();
    
    public CacheConfigurationDeploy getDeploy() {
      return CacheManagerImpl.deployInstance;
    }
    
    public CacheRegionInquiry getInquiry() {
      return CacheManagerImpl.inquiryInstance;
    }
    
    public static CacheContext getInstance() {
      return instance;
    }
    
  }

}