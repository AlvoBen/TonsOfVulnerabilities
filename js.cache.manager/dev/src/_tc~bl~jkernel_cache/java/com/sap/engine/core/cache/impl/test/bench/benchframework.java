/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.sap.bc.proj.jstartup.sadm.ShmCache;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.engine.cache.admin.Monitor;
import com.sap.engine.cache.admin.impl.MonitorsAccessor;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.cache.core.impl.PluggableFramework;
import com.sap.engine.cache.spi.policy.impl.DummyEvictionPolicy;
import com.sap.engine.cache.spi.storage.impl.DummyStorage;
import com.sap.engine.cache.util.Serializator;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.engine.core.cache.impl.test.BenchEntity;
import com.sap.engine.core.cache.impl.test.BenchResult;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.PluginException;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BenchFramework {
  
  // commands
  public static final String[][] commands = new String[][] {
    {"Configure Start of Eviction Threshold (Count)", "100", RegionConfigurationInfo.PROP_COUNT_START_OF_EVICTION_THRESHOLD},
    {"Configure Upper Limit Threshold (Count)", "101", RegionConfigurationInfo.PROP_COUNT_UPPER_LIMIT_THRESHOLD},
    {"Configure Critical Limit Threshold (Count)", "102", RegionConfigurationInfo.PROP_COUNT_CRITICAL_LIMIT_THRESHOLD},
    {"Configure Start of Eviction Threshold (Size)", "103", RegionConfigurationInfo.PROP_SIZE_START_OF_EVICTION_THRESHOLD},
    {"Configure Upper Limit Threshold (Size)", "104", RegionConfigurationInfo.PROP_SIZE_UPPER_LIMIT_THRESHOLD},
    {"Configure Critical Limit Threshold (Size)", "105", RegionConfigurationInfo.PROP_SIZE_CRITICAL_LIMIT_THRESHOLD},
    {"Configure Direct Invalidation Mode", "106", RegionConfigurationInfo.PROP_DIRECT_INVALIDATION_MODE},
    {"Configure Invalidation Scope", "107", RegionConfigurationInfo.PROP_INVALIDATION_SCOPE},
    {"Configure Region Scope", "108", RegionConfigurationInfo.PROP_REGION_SCOPE},
    {"Configure Logging Mode", "109", RegionConfigurationInfo.PROP_LOGGING_MODE},
    {"Configure Synchronous Mode", "110", RegionConfigurationInfo.PROP_SYNCHRONOUS},
    {"Configure Cleanup Interval", "111", RegionConfigurationInfo.PROP_CLEANUP_INTERVAL},
    {"Configure Client Dependent Mode", "112", RegionConfigurationInfo.PROP_IS_CLIENT_DEPENDENT},
    {"Configure Storage Plugin", "113", "__CONFIGURE_STORAGE_PLUGIN"},
    {"Configure Eviction Policy", "114", "__CONFIGURE_EVICTION_POLICY"},
    
    {"Set Benchmark Period (seconds)", "200", "__BENCHMARK_PERIOD"},
    {"Set Benchmark Scope", "201", "__BENCHMARK_SCOPE"},
    {"Set Benchmark Threads (per node)", "202", "__BENCHMARK_THREADS"},
    {"Set Benchmark Operations", "203", "__BENCHMARK_OPERATIONS"},
    {"Set Benchmark Factorization (n of cached objects)", "204", "__BENCHMARK_FACTOR"},
    {"Set Benchmark Object Type", "205", "__BENCHMARK_OBJECT"},
    
    {"Show Monitoring Info", "300", "__"},
    {"Show Benchmark Results", "301", "__"},
    {"Show Region Configuration", "302", "__"},
    {"Show Benchmark Settings", "303", "__"},

    {"Reset Region Configuration", "400", "__"},
    {"Reset Benchmark Settings", "401", "__"},
    
    {"List Storage Plugins", "500", "__"},
    {"List Eviction Policies", "501", "__"},
    {"List Object Types", "502", "__"},
    
    {"Start Benchmark", "600", "__"},
    {"Get Benchmark Status", "601", "__"},
    {"Interrupt Benchmark", "602", "__"},
    {"Redefine Benchmark Region", "603", "__"},
    {"Spread Benchmark Settings + Region Configuration", "604", "__"},

    {"List All Region Monitoring Infos", "700", "__"},
    {"List All Region Names", "701", "__"},
    
    {"Clear Region", "800", "__"},
    {"Dump Region", "801", "__"},
    {"Test SHM Monitors", "802", "__"}
  };
  
  public static HashMap commandMap;
  public static HashMap commandNames;
  
  public static final int MASK_GET        = 1;
  public static final int MASK_PUT        = 2;
  public static final int MASK_REMOVE     = 4;
  public static final int MASK_INVALIDATE = 8;
  public static final String REGION_NAME  = "BenchRegion";
  
  private BenchResult benchResult        = null;

  private Properties regionConfiguration = null;
  private String storagePluginName       = null;
  private String evictionPolicyName      = null;
  private int threads                    = -1;
  private int period                     = -1;
  private int scope                      = -1;
  private int operations                 = -1;
  
  private InternalRegionFactory factory    = null;
  private ClusterCommunicator communicator = null;

  private boolean interrupted    = false;
  private String status          = "<IDLE>";
  private Thread benchmarkThread = null;
  
  static {
    commandMap = new HashMap();
    commandNames = new HashMap();
    for (int i = 0; i < commands.length; i++) {
      commandMap.put(commands[i][1], commands[i][2]);
      commandNames.put(commands[i][1], commands[i][0]);
    }
  }
  
  public BenchFramework() {
    PluggableFramework.putPluggable("DummyStorage", new DummyStorage());
    PluggableFramework.putPluggable("DummyEviction", new DummyEvictionPolicy());
    new InternalRegionFactory();
    factory = (InternalRegionFactory) InternalRegionFactory.getInstance();
    resetRegionConfiguration();
    resetBenchmarkSettings();
    redefineRegion();
    CommonPool.setObjectType(1);
    communicator = new ClusterCommunicator(this);
  }
  
  private void resetRegionConfiguration() {
    regionConfiguration = new Properties();
    storagePluginName = "HashMapStorage";
    evictionPolicyName = "SimpleLRU";
  }
  
  private void resetBenchmarkSettings() {
    benchResult = new BenchResult();
    threads    = 1;
    period     = 5000;
    scope      = 1;
    operations = 1;
  }

  protected void redefineRegion() {
    factory.removeRegion(REGION_NAME);
    try {
      factory.defineRegion(REGION_NAME, storagePluginName, evictionPolicyName, regionConfiguration);
    } catch (CacheException e) {
      CacheManagerImpl.traceT(e);
    }
  }
  
  public String parseCommand(int command) {
    String result = "<unsolicited>";
    String stringCommand = "" + command;
    if (stringCommand.length() >= 3) {
      String commandNumber = stringCommand.substring(0, 3);
      String commandParams = stringCommand.substring(3);
      int intCommandNumber = new Integer(commandNumber).intValue();
      if (intCommandNumber >= 100 && intCommandNumber < 200) {
        // region configuration section
        switch (intCommandNumber) {
          case 106:
          case 109:
          case 110:
          case 112: // boolean flags
            String flag = "1".equals(commandParams) ? "true" : "false";
            regionConfiguration.setProperty((String)commandMap.get(commandNumber), flag);
            result = "<CONFIGURE> " + (String)commandMap.get(commandNumber) + " == " + flag;
            break;
          case 113:
            // storage plugin
            storagePluginName = chooseStoragePlugin(new Integer(commandParams).intValue());
            result = "<CONFIGURE> 'Storage Plugin Name' == " + storagePluginName; 
            break;
          case 114:
            // eviction policy plugin
            evictionPolicyName = chooseEvictionPlugin(new Integer(commandParams).intValue());
            result = "<CONFIGURE> 'Eviction Policy Name' == " + evictionPolicyName; 
            break;
          default: // other field configuration
            regionConfiguration.setProperty((String)commandMap.get(commandNumber), commandParams);
            result = "<CONFIGURE> " + (String)commandMap.get(commandNumber) + " == " + commandParams;
        }
      } else if (intCommandNumber < 300) {
        // bench configuration section
        switch (intCommandNumber) {
          case 200:
            period = (new Integer(commandParams).intValue()) * 1000;
            result = "<SET> 'Benchmark Period' (seconds) == " + period / 1000; 
            break;
          case 201:
            scope = (new Integer(commandParams).intValue());
            result = "<SET> 'Benchmark Scope' == " + scope; 
            break;
          case 202:
            threads = (new Integer(commandParams).intValue());
            result = "<SET> 'Benchmark Threads' == " + threads; 
            break;
          case 203:
            operations = 0;
            if (commandParams.charAt(3) == '1') operations |= MASK_GET; 
            if (commandParams.charAt(2) == '1') operations |= MASK_PUT; 
            if (commandParams.charAt(1) == '1') operations |= MASK_REMOVE; 
            if (commandParams.charAt(0) == '1') operations |= MASK_INVALIDATE; 
            result = "<SET> 'Benchmark Operations' == " + operations; 
            break;
          case 204:
            FacadeBenchAdaptor.factor = (new Integer(commandParams).intValue());
            result = "<SET> 'Benchmark Factorization' (n of cached objects) == " + FacadeBenchAdaptor.factor; 
            break;
          case 205:
            CommonPool.setObjectType(new Integer(commandParams).intValue());
            result = "<SET> 'Benchmark Object Type' == " + CommonPool.cachedObject; 
            break;
          default:
            break;
        }
      } else if (intCommandNumber < 400) {
        // show commands section
        switch (intCommandNumber) {
          case 300: // Show Monitoring Info
            result = showMonitoringInfo();
            break;
          case 301: // Show Benchmark Results
            result = benchResult.toString();
            break;
          case 302: // Show Region Configuration
            result = showRegionConfiguration();
            break;
          case 303: // Show Benchmark Settings
            result = showBenchSettings();
            break;
          default:
        }
      } else if (intCommandNumber < 500) {
        // reset commands section
        switch (intCommandNumber) {
          case 400: // Reset Region Configuration
            resetRegionConfiguration();
            result = "<OK> Reset Region Configuration";
            break;
          case 401:
            resetBenchmarkSettings();
            result = "<OK> Reset Benchmark Settings";
            break;
          default:
        }
      } else if (intCommandNumber < 600) {
        // list commands section
        switch (intCommandNumber) {
          case 500: // list storage plugins
            result = listStoragePlugins();
            break;
          case 501: // list ebviction policies
            result = listEvictionPlugins();
            break;
          case 502: // list available objects
            result = CommonPool.toStringStatic();
            break;
          default:
            break;
        }
      } else if (intCommandNumber < 700) {
        // bench lifecycle commands section
        switch (intCommandNumber) {
          case 600: // execute benchmark
            if (communicator.isFunctional()) {
              result = communicator.getStatus(scope);
              if (result.indexOf("<WORKING>") != -1) {
                StringBuffer sb = new StringBuffer();
                sb.append("<FAILED> Benchmark Already Running!");
                sb.append("\n");
                sb.append(result);
                result = sb.toString();
              } else {
                result = communicator.startBenchmark(scope);
              }
            } else {
              result = getStatus();
              if (result.indexOf("<WORKING>") != -1) {
                result = "<FAILED> Benchmark Already Running!";
              } else {
                executeBenchmark();
                result = "<OK> Execute Benchmark";
              }
            }
            break;
          case 601: // benchmark status
            if (communicator.isFunctional()) {
              result = communicator.getStatus(scope);
            } else {
              result = getStatus();
            }
            break;
          case 602: // interrupt benchmark
            if (communicator.isFunctional()) {
              result = communicator.interruptBenchmark(scope);
            } else {
              result = interruptBenchmark();
            }
            break;
          case 603: // redefine region
            if (communicator.isFunctional()) {
              result = communicator.redefineRegion(scope);
            } else {
              redefineRegion();
              result = "<OK> Redefine region";
            }
            break;
          case 604: // spread configuration
            if (scope > 1 && communicator.isFunctional()) {
              result = communicator.spreadConfig(scope);
            } else {
              redefineRegion();
              result = "<OK> Configuration Cannot Be Spreaded With Local Scope!";
            }
            break;
          default:
            break;
        }
      } else if (intCommandNumber < 800) {
        // external comands section
        switch (intCommandNumber) {
          case 700:
            result = listAll();
            break;
          case 701:
            result = listRegions();
            break;
          default:
            break;
        }
      } else if (intCommandNumber < 900) {
        // debug / admin
        switch (intCommandNumber) {
          case 800:
            clearRegion(new Integer(commandParams).intValue());
            result = "<OK> Clear Region";
            break;
          case 801:
            dumpRegion(new Integer(commandParams).intValue());
            result = "<OK> Dump Region";
            break;
          case 802:
            testShmMonitors();
            result = "<OK> Test SHM Monitors";
            break;
          default:
            break;
        }
      }
    }
    if ("<unsolicited>".equals(result)) {
      result = help();
    }
    return result;
  }

  private String showMonitoringInfo() { // 300
    String result = null;
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    sb.append((MonitorsAccessor.getMonitor(REGION_NAME)).toString());
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;    
  }

  private String showRegionConfiguration() { // 302
    String result = null;
    try {
      factory.defineRegion("TEMP", storagePluginName, evictionPolicyName, regionConfiguration);
      StringBuffer sb = new StringBuffer();
      sb.append("==================================================================");
      sb.append(factory.getCacheRegion("TEMP").getRegionConfigurationInfo().toString());
      sb.append("\n==================================================================");
      result = sb.toString();
    } catch (CacheException e) {
      CacheManagerImpl.traceT(e);
    }
    factory.removeRegion("TEMP");
    return result;
  }

  private String showBenchSettings() { // 303
    String result = null;
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    sb.append("\n Period (seconds): " + period / 1000);
    sb.append("\n Scope:            ");
    if (scope == 1) {
      sb.append("LOCAL");
    } else if (scope == 2) {
      sb.append("MACHINE");
    } else if (scope == 3) {
      sb.append("CLUSTER");
    } else {
      sb.append("UNDEFINED");
    }
    sb.append("\n Threads:          " + threads);
    sb.append("\n Operations:      ");
    if ((operations & MASK_GET) == MASK_GET) sb.append(" GET");
    if ((operations & MASK_PUT) == MASK_PUT) sb.append(" PUT");
    if ((operations & MASK_REMOVE) == MASK_REMOVE) sb.append(" REMOVE");
    if ((operations & MASK_INVALIDATE) == MASK_INVALIDATE) sb.append(" INVALIDATE");
    sb.append("\n Factorization:    " + FacadeBenchAdaptor.factor);
    sb.append("\n Object Type:      " + CommonPool.cachedObject);
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;
  }
  
  private String listStoragePlugins() { // 500
    return listPlugins(PluggableFramework.listStorageNames());
  }
  
  private String listEvictionPlugins() { // 501
    return listPlugins(PluggableFramework.listEvictionNames());
  }
  
  private String listPlugins(Set set) {
    String result = null;
    TreeSet sorted = new TreeSet();
    sorted.addAll(set);
    Iterator plugins = sorted.iterator();
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    int counter = 0;
    while (plugins.hasNext()) {
      String pluginName = (String) plugins.next();
      counter++;
      sb.append("\n" + counter + ". " + pluginName);
    }
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;
  }
  
  private String chooseStoragePlugin(int i) { // 113
    return choosePlugin(PluggableFramework.listStorageNames(), i);
  }
  
  private String chooseEvictionPlugin(int i) { // 114
    return choosePlugin(PluggableFramework.listEvictionNames(), i);
  }

  private String choosePlugin(Set set, int i) {
    String result = null;
    TreeSet sorted = new TreeSet();
    sorted.addAll(set);
    Iterator plugins = sorted.iterator();
    int counter = 0;
    while (plugins.hasNext()) {
      String pluginName = (String) plugins.next();
      counter++;
      if (counter == i) {
        result = pluginName;
      }
    }
    return result;
  }
  
  private void all() {
    CacheRegionFactory factory = CacheRegionFactory.getInstance();
    try {
      factory.defineRegion("TEST", "HashMapStorage", "SimpleLRU", new Properties());
      CacheRegion testRegion = factory.getCacheRegion("TEST");
      BenchEntity entity = null;
      BenchResult result = null;
      
      entity = new CollectiveEntity();
      entity.init(testRegion);
      result = entity.execute(2000, 10);
      entity.close();
      result.printResults();
    
      entity = new HashtableCollectiveEntity();
      entity.init(testRegion);
      result = entity.execute(2000, 10);
      entity.close();
      result.printResults();
    } catch (CacheException e) {
      CacheManagerImpl.traceT(e);
    }
  }
  
  private BenchResult singleEntity(BenchEntity entity) {
    entity.init(factory.getCacheRegion(REGION_NAME));
    BenchResult singleResult = entity.execute(period, threads);
    entity.close();
    return singleResult;
  }
  
  protected void executeBenchmark() {
    interrupted = false;
    status = "<WORKING>";
    Runnable benchMarkRunnable = new Runnable() {
      public void run() {
        benchResult = new BenchResult();
        BenchResult singleResult = null;
        int get;
        int put = 0;
        int putRemove;
        int remove;
        int putInvalidate;
        int invalidate;
        if (interrupted == false && (operations & MASK_GET) == MASK_GET) {
          // get
          singleResult = singleEntity(new GetEntity());
          get = singleResult.getSingleValue();
          benchResult.addAll(singleResult);
        }

        if (interrupted == false && ((operations & MASK_PUT) == MASK_PUT) ||
            ((operations & MASK_REMOVE) == MASK_REMOVE) ||
            ((operations & MASK_INVALIDATE) == MASK_INVALIDATE)) {
          // put
          singleResult = singleEntity(new PutEntity());
          put = singleResult.getSingleValue();
          benchResult.addAll(singleResult);
        }

        if (interrupted == false && (operations & MASK_REMOVE) == MASK_REMOVE) {
          // put + remove
          singleResult = singleEntity(new PutRemoveEntity());
          putRemove = singleResult.getSingleValue();
          benchResult.addAll(singleResult);
          // remove - this is calculated using put and put + remove velocities
          remove = (int) (((double)putRemove * (double)put) / ((double)put - (double)putRemove));
          benchResult.putValue("Remove Operations / Sec", remove);
        }

        if (interrupted == false && (operations & MASK_INVALIDATE) == MASK_INVALIDATE) {
          // put + invalidate
          singleResult = singleEntity(new PutInvalidateEntity());
          putInvalidate = singleResult.getSingleValue();
          benchResult.addAll(singleResult);
          // invalidate - this is calculated using put and put + remove velocities
          invalidate = (int) (((double)putInvalidate * (double)put) / ((double)put - (double)putInvalidate));
          benchResult.putValue("Invalidate Operations / Sec", invalidate);
        }
        
        status = "<IDLE>";
        benchmarkThread = null;
      }
    };
    benchmarkThread = new Thread(benchMarkRunnable);
    benchmarkThread.start();
  }
  
  private String listAll() {
    // this is the information that one should be able to see using the shell
    // commands of the engine
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
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
    return sb.toString();
  }
  
  private String listRegions() {
    // this is the information that one should be able to see using the shell
    // commands of the engine
    TreeSet set = new TreeSet();
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    Iterator regions = factory.iterateRegions();
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      set.add(regionName);
    }
    regions = set.iterator();
    int counter = 0;
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      counter++;
      sb.append("\n " + counter + ". " + regionName);
    }
    sb.append("\n==================================================================");
    return sb.toString();
  }
  
  private void clearRegion(int index) {
    // this is the information that one should be able to see using the shell
    // commands of the engine
    TreeSet set = new TreeSet();
    Iterator regions = factory.iterateRegions();
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      set.add(regionName);
    }
    regions = set.iterator();
    int counter = 0;
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      counter++;
      if (counter == index) {
        CacheRegion region = factory.getCacheRegion(regionName);
        region.getCacheFacade().clear();
      }
    }
  }

  private void dumpRegion(int index) {
    // this is the information that one should be able to see using the shell
    // commands of the engine
    TreeSet set = new TreeSet();
    Iterator regions = factory.iterateRegions();
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      set.add(regionName);
    }
    regions = set.iterator();
    int counter = 0;
    while (regions.hasNext()) {
      String regionName = (String) regions.next();
      counter++;
      if (counter == index) {
        CacheRegion region = factory.getCacheRegion(regionName);
        new RegionDump(region).dump("DUMP.REGION");
      }
    }
  }
  
  private void testShmMonitors() {
    ShmCache local = null;
    ShmCache instance = null;
    
    // create/remove them
    for (int i = 0; i < 100; i++) {
      try {
        local = new ShmCache("LOCAL_SHM_TEST", 1);
        local.onPut(1, 1, 1);
        local.close();
        instance = new ShmCache("INSTANCE_SHM_TEST", 2);
        instance.onPut(1, 1, 1);
        instance.close();
      } catch (ShmException e) {
        CacheManagerImpl.traceT(e);
      }
    }

    // create/find them
    for (int i = 0; i < 100; i++) {
      try {
        local = new ShmCache("LOCAL_SHM_TEST", 1);
        local.onPut(1, 1, 1);
        instance = new ShmCache("INSTANCE_SHM_TEST", 2);
        instance.onPut(1, 1, 1);
      } catch (ShmException e) {
        CacheManagerImpl.traceT(e);
      }
    }
    
    // remove/invalid remove them
    for (int i = 0; i < 100; i++) {
      try {
        local.close();
        instance.close();
      } catch (ShmException e) {
        CacheManagerImpl.traceT(e);
      }
    }

  }

  protected String getStatus() {
    return status;
  }
  
  protected String interruptBenchmark() {
    if ("<WORKING>".equals(status)) {
      interrupted = true;
      benchmarkThread.interrupt();
      return "<OK> Interrupt Benchmark";
    } else {
      return "<OK> Nothing to Interrupt";
    }
  }
  
  protected byte[] readConfiguration() {
    byte[] result = new byte[0];
    BenchConfig config = new BenchConfig();
    config.evictionPolicyName  = evictionPolicyName;
    config.regionConfiguration = regionConfiguration;
    config.storagePluginName   = storagePluginName;
    config.operations          = operations;
    config.period              = period;
    config.scope               = scope;
    config.threads             = threads;
    config.factor              = FacadeBenchAdaptor.factor;
    try {
      result =  Serializator.toByteArray(config);
    } catch (IOException e) {
      CacheManagerImpl.traceT(e);
    }
    return result;
  }
  
  protected void writeConfiguration(byte[] ba) {
    BenchConfig config;
    try {
      config = (BenchConfig)Serializator.toObject(ba);
      evictionPolicyName        = config.evictionPolicyName;
      regionConfiguration       = config.regionConfiguration;
      storagePluginName         = config.storagePluginName;
      operations                = config.operations;
      period                    = config.period;
      scope                     = config.scope;
      threads                   = config.threads;
      FacadeBenchAdaptor.factor = config.factor;
    } catch (StreamCorruptedException e) {
      CacheManagerImpl.traceT(e);
    } catch (IOException e) {
      CacheManagerImpl.traceT(e);
    } catch (ClassNotFoundException e) {
      CacheManagerImpl.traceT(e);
    }
  }
  
  private String help() {
    String result = null;
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    sb.append("\n 0-99 : Help");
    Set set = new TreeSet();
    set.addAll(commandNames.keySet());
    char oldFirstChar = '0';
    Iterator iterator = set.iterator();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      if (oldFirstChar != key.charAt(0)) {
        oldFirstChar = key.charAt(0);
        sb.append("\n------------------------------------------------------------------");
      }
      String name = (String) commandNames.get(key);
      sb.append("\n  " + key + " : " + name);
    }
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;
  }
  
  private void shell() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line = null;
    do {
      DumpWriter.dump(">");
      try {
        line = reader.readLine();
      } catch (IOException e) {
        CacheManagerImpl.traceT(e);
      }
      if (line != null) {
        int command = 0;
        try {
          command = new Integer(line).intValue();
          DumpWriter.dump(parseCommand(command));
        } catch (NumberFormatException nfe) {
          CacheManagerImpl.traceT(nfe);
          DumpWriter.dump(" (NAN)");
        } catch (Throwable t) {
          CacheManagerImpl.traceT(t);
          DumpWriter.dump("Problem, eh?");
        }
      }
    } while (!"exit".equals(line));
  }
  
  public static void main(String[] args) {
    BenchFramework framework = new BenchFramework();
    DumpWriter.dump(framework.parseCommand(0));
    framework.shell();
    System.exit(0);
  }

  public void kill() {
    communicator.kill();
  }
  
}
