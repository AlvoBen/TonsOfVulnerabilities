package com.sap.engine.services.log_configurator.admin;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.core.log.api.LogManagerManagementInterface;
import com.sap.engine.frame.NestedProperties;
import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.cluster.ClusterContext;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.CoreContext;
import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.AdditionalConfiguration;
import com.sap.engine.lib.config.api.component.ComponentProperties;
import com.sap.engine.lib.config.api.component.ManagerHandler;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.logging.LogConfigurationUpdater;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.engine.lib.logging.descriptors.LogControllerDescriptor;
import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.engine.lib.logging.descriptors.LogFormatterDescriptor;
import com.sap.engine.lib.util.EnumerationInt;
import com.sap.engine.lib.util.HashMapIntObject;
import com.sap.engine.services.log_configurator.bam.CCMSTemplate;
import com.sap.sql.trace.SQLTrace;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Formatter;
import com.sap.tc.logging.FormatterType;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Log;
import com.sap.tc.logging.LogController;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.TraceFormatter;


/**
 * @author Nikola Marchev
 * @version 6.30
 */
public class LogConfigurator 
implements LogConfiguratorManagementInterface, ConfigurationChangedListener {
  //$JL-EXC$ $JL-SEVERITY_TEST$ 

  public static final String LOG_MANAGER_NAME = "LogManager";
  private static final String DC_NAME = "log_configurator";
  private static final String CSN_COMPONENT = "BC-JAS-ADM-LOG-LCF";
  
  private static final Location TRACER = Location.getLocation(LogConfigurator.class);
  private static final Location log_config = Location.getLocation("com.sap.engine.services.log_configurator",DC_NAME,CSN_COMPONENT);
  
  private static final String PROPERTIES_NAME = "properties";
  private static final String NAME = "name";
//  private static final String DESTINATION_REFS = "destination-refs";
//  private static final String ASSOCIATION_TYPE = "association-type";
  private static final String EFFECTIVE_SEVERITY = "effective-severity";
//  private static final String INHERIT_FROM_PARENT = "inherit-from-parent";
  private static final String COPY_TO_SUBTREE = "copy-to-subtree";
//  private static final String LOG = "LOG";
//  private static final String LOCAL_LOG = "LOCAL_LOG";
//  private static final String PRIVATE_LOG = "PRIVATE_LOG";
  
  private final LogConfiguration mainConfiguration;
  private final LogManagerManagementInterface logManager;
  private final HashMapIntObject listeners;
  private final byte nodeType;

  private boolean noLogging = false;
  private long configVersion = 0;
  private int nextListenerId = 0;

  private String defaultTemplateId;
  private String customTemplateId;
  //key -> template ID; value -> corresponding instance IDs
  private HashMap<String, Collection<String>> customTemplates;
  private String thisInstanceId;
  private ClusterMonitor clusterMonitor;
  private CommonClusterFactory clusterFactory;
  private LogArchivesInfo archivesInfo;
  
  /**
   * Constructor
   *
   * @param serviceContext
   * @param clusterContext
   */
  public LogConfigurator(ServiceContext serviceContext, ClusterContext clusterContext) {
    listeners = new HashMapIntObject();
    archivesInfo = new LogArchivesInfo(serviceContext);
    clusterMonitor = clusterContext.getClusterMonitor();
    ClusterElement currentNode = clusterMonitor.getCurrentParticipant();
    nodeType = currentNode.getType();
    thisInstanceId = "ID" + currentNode.getGroupId();
    CoreContext coreContext = serviceContext.getCoreContext();
    CoreMonitor coreMonitor = coreContext.getCoreMonitor();
    
    logManager = (LogManagerManagementInterface) 
      coreMonitor.getManagementInterface(LOG_MANAGER_NAME);
    
    mainConfiguration = logManager.getLogConfiguration();
    Properties serviceStateProps = serviceContext.getServiceState().getProperties();
    init(coreContext, currentNode, serviceStateProps);
  }
  
  
  private void init(
    CoreContext coreContext, ClusterElement currentNode, Properties serviceStateProps) {
    
    ClusterElement[] nodes = clusterMonitor.getParticipants();
    AdditionalConfiguration additionalCfg = null;
    try {
      ConfigurationHandlerFactory cfgHandlerFactory = 
        coreContext.getConfigurationHandlerFactory();
      
      clusterFactory = ClusterConfiguration.getClusterFactory(cfgHandlerFactory); 
      customTemplates = new HashMap<String, Collection<String>>();
      for(int i = 0; i < nodes.length; i++) {
        String instId = "ID" + nodes[i].getGroupId();
        
        ConfigurationLevel instLevel = 
          clusterFactory.openConfigurationLevel(
            CommonClusterFactory.LEVEL_INSTANCE, instId);
        
        String templateId = instLevel.getParent().getLevelIdentifier();
        if(instId.equals(thisInstanceId)) {
          customTemplateId = templateId;
        }
        Collection<String> templateInstIds = customTemplates.get(templateId);
        if(templateInstIds == null ) {
          templateInstIds = new ArrayList<String>();
          customTemplates.put(templateId, templateInstIds);
        }
        templateInstIds.add(instId);
        
        if(defaultTemplateId == null) {
          defaultTemplateId = instLevel.getParent().getParent().getLevelIdentifier();
        }
      }
      
      additionalCfg = 
        getAdditionalCfg(
          CommonClusterFactory.LEVEL_INSTANCE, thisInstanceId, true);
      
      Configuration cfg = additionalCfg.getConfiguration();
      ConfigurationHandler cfgHandler = cfgHandlerFactory.getConfigurationHandler();
      cfgHandler.addConfigurationChangedListener(this, cfg.getPath());
      
      //write corresponding CCMSTemplates for each logDestination
      writeCCMSTemplates(currentNode, serviceStateProps);
    } catch(Exception e) {
      TRACER.traceThrowableT(Severity.ERROR, "Could not initialize log configurator", e);
    } finally {
      try {
        if(additionalCfg != null) {
          additionalCfg.discardChanges();
        }
      } catch(ClusterConfigurationException e) {
        TRACER.traceThrowableT(Severity.ERROR, "Could not close configurations", e);
      }
    }
  }
  
  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public boolean existsLog(String name) {
    return mainConfiguration.containsLogDetination(name);
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public boolean existsFormatter(String name) {
    return mainConfiguration.containsLogFormatter(name);
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public boolean existsLogController(String name) {
    return getCurrentConfiguration().containsLogController(name);
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public boolean existsLogControllerAll(String name) {
    return getCurrentConfigurationAll().containsLogController(name);
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public Log getLog(String name) {
    LogDestinationDescriptor descriptor = getLogDescriptor(name);
    if(descriptor != null) {
      return descriptor.getRealLog();
    }
    return null;
  }

  /**
   * Method for ...
   * 
   * @return
   */
  public FileLog getDefaultTraceFile() {
    return logManager.getDefaultTraceFile();
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public Formatter getFormatter(String name) {
    LogFormatterDescriptor descriptor = getFormatterDescriptor(name);
    return descriptor != null ? descriptor.getRealFormatter() : null;
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public LogController getLogController(String name) {
    if(name.startsWith(Category.ROOT_NAME)) {
      return Category.getCategory(name);
    } else {
      return Location.getLocation(name);
    }
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public LogDestinationDescriptor getLogDescriptor(String name) {
    LogDestinationDescriptor result;
    synchronized(this) {
      result = mainConfiguration.getLogDestination(name);
    }
    if(result != null) {
      result = (LogDestinationDescriptor) result.clone();
    }
    return result;
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public LogFormatterDescriptor getFormatterDescriptor(String name) {
    LogFormatterDescriptor result = mainConfiguration.getLogFormatter(name);
    if(result != null) {
      result = (LogFormatterDescriptor) result.clone();
    }
    return result;
  }

  /**
   * Method for ...
   * 
   * @param name
   * @return
   */
  public LogControllerDescriptor getLogControllerDescriptor(String name) {
    return getCurrentConfiguration().getLogController(name);
  }

  /**
   * Method for ...
   * 
   * @param out
   */
  public void listLogsNames(PrintStream out) {
    String[] names;
    synchronized(this) {
      names = mainConfiguration.getLogDestinationsNames();
    }
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    out.println("\n  ********* Log Destinations *********\n");
    for(String name: sortedNames) {
      out.println("  " + name);
    }
    out.println("\n  ************************************\n");
  }

  /**
   * Method for ...
   * 
   * @param out
   */
  public void listFormattersNames(PrintStream out) {
    String[] names = mainConfiguration.getLogFormattersNames();
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    out.println("\n  ********** Log Formatters **********\n");
    for(String name: sortedNames) {
      out.println("  " + name);
    }
    out.println("\n  ************************************\n");
  }

  /**
   * Method for ...
   * 
   * @param out
   */
  public void listLogControllersNames(PrintStream out) {
    listLogControllersNames(out, getCurrentConfiguration());
  }

  /**
   * Method for ...
   * 
   * @param out
   */
  public void listLogControllersNamesAll(PrintStream out) {
    listLogControllersNames(out, getCurrentConfigurationAll());
  }

  /**
   * Method for ...
   * 
   * @param out
   */
  private void listLogControllersNames(PrintStream out, LogConfiguration cfg) {
    String[] names = cfg.getLogControllersNames();
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    out.println("\n  ********** LogControllers **********\n");
    for(String name: sortedNames) {
      String severity = Severity.toString(getLogController(name).getEffectiveSeverity());
      out.println("  " + name + "  (Severity = " + severity + ")");
    }
    out.println("\n  ************************************\n");
  }

  /**
   * Method for ...
   * 
   * @param formatter
   * @param out
   * @param offset
   */
  private void printFormatter(Formatter formatter, PrintStream out, String offset) {
    out.println(offset + formatter.getFormatterName());
    out.println(offset + "  type = " + formatter.getFormatterType());
    if(formatter instanceof TraceFormatter) {
      out.println(offset + "  pattern = " + ((TraceFormatter) formatter).getPattern());
    }
  }

  /**
   * Method for ...
   * 
   * @param log
   * @param out
   * @param offset
   */
  private void printLog(Log log, PrintStream out, String offset) {
    out.println(offset + log.getName());
    out.println(offset + "  type = " + log.getClass().getName());
    String encoding = log.getEncoding();
    if(encoding != null) {
      out.println(offset + "  encoding = " + encoding);
    }

    out.println(offset + "  effective severity = "
                + Severity.toString(log.getEffectiveSeverity()));

    if(log instanceof FileLog) {
      out.println(offset + "  pattern = " + ((FileLog) log).getPattern());
      out.println(offset + "  limit = " + ((FileLog) log).getLimit());
      out.println(offset + "  count = " + ((FileLog) log).getCnt());
    }
    Object[] filters = log.getFilters().toArray();
    for(int i = 0; i < filters.length; i++) {
      out.println(offset + "  filter No " + i + " = " + filters[i]);
    }
    out.print(offset + "  Formatter:");
    printFormatter(log.getFormatter(), out, offset + "  ");
  }

  /**
   * Method for ...
   * 
   * @param controller
   * @param out
   */
  private void printController(LogController controller, PrintStream out) {
    out.println("  name = " + controller.getName());

    out.println(
      "    minimum severity = " + Severity.toString(controller.getMinimumSeverity()));
    
    out.println(
      "    maximum severity = " + Severity.toString(controller.getMaximumSeverity()));
    
    out.println(
      "    effective severity = " + Severity.toString(controller.getEffectiveSeverity()));

    Object[] filters = controller.getFilters().toArray();
    for(int i = 0; i < filters.length; i++) {
      out.println("    filter No " + i + " = " + filters[i]);
    }
    for(Log log: controller.getPrivateLogs()) {
      out.print("    Private Log Destination:");
      printLog(log, out, "    ");
    }
    for(Log log: controller.getLocalLogs()) {
      out.print("    Local Log Destination:");
      printLog(log, out, "    ");
    }
    for(Log log: controller.getLogs()) {
      out.print("    Public Log Destination:");
      printLog(log, out, "    ");
    }
  }

  /**
   * Method for ...
   * 
   * @param names
   * @param out
   */
  public synchronized void listLogs(String[] names, PrintStream out) {
    out.println();
    out.println("  ********* Log Destinations *********");
    out.println();
    if(names == null) {
      names = mainConfiguration.getLogDestinationsNames();
    }
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    for(String name: sortedNames) {
      printLog(getLog(name), out, "  ");
    }
    out.println();
    out.println("  ************************************");
    out.println();
  }

  /**
   * Method for ...
   * 
   * @param names
   * @param out
   */
  public synchronized void listFormatters(String[] names, PrintStream out) {
    out.println();
    out.println("  ********** Log Formatters **********");
    out.println();
    if(names == null) {
      names = mainConfiguration.getLogFormattersNames();
    }
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    for(String name: sortedNames) {
      printFormatter(getFormatter(name), out, "  ");
    }
    out.println();
    out.println("  ************************************");
    out.println();
  }

  /**
   * Method for ...
   * 
   * @param names
   * @param out
   */
  public synchronized void listLogControllers(String[] names, PrintStream out) {
    listLogControllers(names, out, getCurrentConfiguration());
  }
  
  /**
   * Method for ...
   * 
   * @param names
   * @param out
   */
  public synchronized void listLogControllersAll(String[] names, PrintStream out) {
    listLogControllers(names, out, getCurrentConfigurationAll());
  }
  
  /**
   * Method for ...
   * 
   * @param names
   * @param out
   */
  private synchronized void listLogControllers(
    String[] names, PrintStream out, LogConfiguration cfg) {
    out.println();
    out.println("  ********** LogControllers **********");
    out.println();
    if(names == null) {
      names = cfg.getLogControllersNames();
    }
    Collection<String> sortedNames = new TreeSet<String>(Arrays.asList(names));
    for(String name: sortedNames) {
      printController(getLogController(name), out);
    }
    out.println();
    out.println("  ************************************");
    out.println();
  }
  
  /**
   * Method for ...
   * 
   * @param logCfg
   * @param save
   */
  private void removeConfiguration(LogConfiguration logCfg, boolean clusterWide) {
    if(logCfg == null) {
      return;
    }
    String[] controllerNames = logCfg.getLogControllersNames();
    if(controllerNames == null || controllerNames.length == 0) {
      return;
    }
    Collection<AdditionalConfiguration> dbConfigs = null;
    try {
      dbConfigs = openDbConfigs(clusterWide);
      for(String name: controllerNames) {
        removeLogController(name, dbConfigs);
      }
    } catch(Exception e) {
      TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
    } finally {
      closeDbConfigs(dbConfigs);
    }
  }
  
  /**
   * Method for ...
   * 
   * @param name
   * @param dbConfigs
   * @param save
   * @throws Exception
   */
  private void removeLogController(
    String name, Collection<AdditionalConfiguration> dbConfigs) 
  throws Exception {
    LogController lc = getLogController(name);
    lc.release();
    for(AdditionalConfiguration addCfg: dbConfigs) {
      Configuration cfg = addCfg.getConfiguration();
      cfg = cfg.getSubConfiguration("log-controllers");
      String dbName = DbParser.convertName(name);
      cfg.deleteConfiguration(dbName);
    }
  }
  
  /**
   * Method for ...
   * 
   * @return
   */
  private Collection<String> getAllInstanceIds() {
  	Collection<String> instanceIds = new HashSet<String>();
    for(ClusterElement node: clusterMonitor.getParticipants()) {
      instanceIds.add("ID" + node.getGroupId());
    }
    return instanceIds;
  }
  
  /**
   * Method for ...
   * 
   * @param clusterWide
   * @return
   * @throws Exception
   */
  private Collection<AdditionalConfiguration> openDbConfigs(boolean clusterWide) 
  throws Exception {
    Collection<AdditionalConfiguration> res = new ArrayList<AdditionalConfiguration>();
    AdditionalConfiguration addCfg = null;
    if(clusterWide) {
      Collection<String> instanceIds = getAllInstanceIds();
      for(String instId: instanceIds) {
        addCfg = getAdditionalCfg(CommonClusterFactory.LEVEL_INSTANCE, instId, false);
        res.add(addCfg);
      }
    } else {
      addCfg = getAdditionalCfg(
        CommonClusterFactory.LEVEL_INSTANCE, thisInstanceId, false);
      
      res.add(addCfg);
    }
    return res;
  }
  
  /**
   * Method for ...
   * 
   * @param dbConfigs
   */
  private void closeDbConfigs(Collection<AdditionalConfiguration> dbConfigs) {
    if(dbConfigs == null) {
      return;
    }
    for(AdditionalConfiguration addCfg: dbConfigs) {
      try {
        addCfg.applyChanges();
      } catch(ClusterConfigurationException e) {
        TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
      }
    }
  }
  
  
  /**
   * Method for ...
   * 
   * @param cfg
   * @param save
   */
  private void addEditConfiguration(LogConfiguration cfg, boolean clusterWide) {
    if(cfg == null) {
      return;
    }
    Collection<AdditionalConfiguration> dbConfigs = null;
    try {
      dbConfigs = openDbConfigs(clusterWide);
      
      Collection<LogControllerDescriptor> controllers = 
        new TreeSet<LogControllerDescriptor>(Arrays.asList(cfg.getLogControllers()));
      
      synchronized (LogController.class) {
        for(LogControllerDescriptor lcd: controllers) {
          String name = lcd.getName();
          LogController lc = getLogController(name);
          int oldSeverity = lc.getEffectiveSeverity();
          int newSeverity = cfg.calculateEffectiveSeverity(lcd.getName());
          if(newSeverity != oldSeverity) {
            lc.setEffectiveSeverity(newSeverity, false);
          } else if (lcd.isCopyToSubtree()) {
            lc.setEffectiveSeverity(newSeverity);                
          }
          boolean isCopy = lcd.isCopyToSubtree();
          lc.setCopyToSubtree(isCopy);
          if ((newSeverity != oldSeverity) && !isCopy) {
            //if copyToSubtree is false then save all the children explicitly
            // in order to avoid the case when children are not saved in DB and
            // take the severity of their parent
            saveLogControllerAndItsChildren(lc, dbConfigs, false);
          } else if (isCopy) {
            // also recursively check if any of the children comes from template
            // and if this is the case explicitly saves it to preserve the custom value 
            saveLogControllerAndRemoveAllItsChildren(lc, dbConfigs, false);                
          } else if(!lc.isInheritFromParent() && isCopy) {
            saveLogController(lc, dbConfigs, false);
          } else {
            saveLogController(lc, dbConfigs, true);
          }
        }
      }
    } catch(Exception e) {
      TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
    } finally {
      closeDbConfigs(dbConfigs);
    }
  }
  
  
  /**
   * Method for ...
   * 
   * @param controller
   */
  private void saveLogController(
    LogController controller, 
    Collection<AdditionalConfiguration> dbConfigs, 
    boolean checkDataBase)
  throws Exception {
    for(AdditionalConfiguration addCfg: dbConfigs) {
      Configuration logCfg = addCfg.getConfiguration();
      logCfg = logCfg.getSubConfiguration("log-controllers");
      saveLogController(controller, logCfg, checkDataBase);
    }
  }

  private void saveLogControllerAndItsChildren(
          LogController controller, 
          Collection<AdditionalConfiguration> dbConfigs, 
          boolean checkDataBase)
        throws Exception {
          for(AdditionalConfiguration addCfg: dbConfigs) {
            Configuration logCfg = addCfg.getConfiguration();
            logCfg = logCfg.getSubConfiguration("log-controllers");
            saveLogControllerAndItsChildren(controller, logCfg, checkDataBase);
          }
        }
  
  private void saveLogControllerAndRemoveAllItsChildren(
          LogController controller, 
          Collection<AdditionalConfiguration> dbConfigs, 
          boolean checkDataBase) throws Exception {
      for(AdditionalConfiguration addCfg: dbConfigs) {
          Configuration logCfg = addCfg.getConfiguration();
          logCfg = logCfg.getSubConfiguration("log-controllers");
          saveLogControllerAndRemoveAllItsChildren(controller, logCfg, checkDataBase);
        }    
  }
  
  /**
   * Method for ...
   * 
   * @param level
   * @param instId
   * @return
   */
  private AdditionalConfiguration getAdditionalCfg(
    int level, String instId, boolean readOnly) 
  throws ClusterConfigurationException {
    ConfigurationLevel cfgLevel = clusterFactory.openConfigurationLevel(level, instId);
    ManagerHandler managerHandler = cfgLevel.getManagerAccess();
    return managerHandler.getAdditionalConfiguration("LogManager", readOnly); 
  }
  
  /**
   * Method for ...
   * 
   * @param name
   * @param severity
   * @param id
   * @param cfgHandler
   * @throws ConfigurationException
   */
  private void saveLogControllerAndItsChildren(
          LogController controller, Configuration logCfg, boolean checkDataBase) 
        throws Exception {
          String name = DbParser.convertName(controller.getName());
          boolean exists = logCfg.existsSubConfiguration(name);
          if(checkDataBase && exists) {
            return;
          }
          if(exists) {
            modifySubConfiguration(logCfg, name, controller, false);
          } else {
            createSubConfiguration(logCfg, name, controller);
          }
          saveChildren(controller, logCfg);
        }

  private void saveLogControllerAndRemoveAllItsChildren(
          LogController controller, Configuration logCfg, boolean checkDataBase) 
        throws Exception {
          String name = DbParser.convertName(controller.getName());
          boolean exists = logCfg.existsSubConfiguration(name);
          if(checkDataBase && exists) {
            return;
          }
          if(exists) {
            modifySubConfiguration(logCfg, name, controller, false);
          } else {
            createSubConfiguration(logCfg, name, controller);
          }
          removeAllChildren(controller, logCfg);
        }  
  
  private void saveLogController(
    LogController controller, Configuration logCfg, boolean checkDataBase) 
  throws Exception {
    //remove childred with same settings - they will inherit from this controller
    String name = DbParser.convertName(controller.getName());
    boolean exists = logCfg.existsSubConfiguration(name);
    if(checkDataBase && exists) {
      return;
    }
    checkChildren(controller, logCfg);
    if(exists) {
      modifySubConfiguration(logCfg, name, controller, false);
    } else {
      createSubConfiguration(logCfg, name, controller);
    }
  }
  
  
  private void createSubConfiguration(
    Configuration logCfg, String name, LogController controller) 
  throws Exception {
    String severity = Severity.toString(controller.getEffectiveSeverity());
    boolean copy = controller.isCopyToSubtree();
    Configuration controllerCfg = logCfg.createSubConfiguration(name);
    
    Configuration psCfg = 
      controllerCfg.createSubConfiguration(
        PROPERTIES_NAME, Configuration.CONFIG_TYPE_PROPERTYSHEET);
    
    PropertySheet ps = psCfg.getPropertySheetInterface();
    ps.createPropertyEntry(NAME, name, null);
    ps.createPropertyEntry(EFFECTIVE_SEVERITY, severity, null);
    ps.createPropertyEntry(COPY_TO_SUBTREE, String.valueOf(copy), null);
    
//    saveLogs(controllerCfg, controller.getLogs(), LOG);
//    saveLogs(controllerCfg, controller.getLocalLogs(), LOCAL_LOG);
//    saveLogs(controllerCfg, controller.getPrivateLogs(), PRIVATE_LOG);
  }
  
  
  private void modifySubConfiguration(
    Configuration logCfg, String name, LogController controller, boolean isTemplate) 
  throws Exception {
    boolean inherit = controller.isInheritFromParent();
    boolean copy = controller.isCopyToSubtree();
    
    String lcName = controller.getName();
    boolean isRoot = 
      lcName.equals(Category.ROOT_NAME) || lcName.equals(Location.ROOT_NAME); 
    
    if(!isRoot && inherit && copy && !isTemplate) {
      logCfg.deleteConfiguration(name);
      return;
    }
    String severity = Severity.toString(controller.getEffectiveSeverity());
    Configuration controllerCfg = logCfg.getSubConfiguration(name);
    Configuration psCfg = controllerCfg.getSubConfiguration(PROPERTIES_NAME);
    PropertySheet ps = psCfg.getPropertySheetInterface();
    ps.getPropertyEntry(EFFECTIVE_SEVERITY).setValue(severity);
    try {
      PropertyEntry pe = ps.getPropertyEntry(COPY_TO_SUBTREE);
      Boolean oldCopy = Boolean.parseBoolean((String) pe.getValue());
      pe.setValue(String.valueOf(copy));
    } catch(NameNotFoundException e) {
      ps.createPropertyEntry(COPY_TO_SUBTREE, String.valueOf(copy), null);
    }
    
//    saveLogs(controllerCfg, controller.getLogs(), LOG);
//    saveLogs(controllerCfg, controller.getLocalLogs(), LOCAL_LOG);
//    saveLogs(controllerCfg, controller.getPrivateLogs(), PRIVATE_LOG);
  }
  
  
//  private void saveLogs(
//    Configuration controllerCfg, Collection<Log> logs, String type) 
//  throws Exception {
//    if(!logs.isEmpty()) {
//      Configuration destRefsCfg = null;
//      if(controllerCfg.existsSubConfiguration(DESTINATION_REFS)) {
//        destRefsCfg = controllerCfg.getSubConfiguration(DESTINATION_REFS);
//      } else {
//        destRefsCfg = controllerCfg.createSubConfiguration(DESTINATION_REFS);
//      }
//      for(Log log: logs) {
//        saveLog(destRefsCfg, log, type);
//      }
//    }
//  }
  
  
  
//  private void saveLog(
//    Configuration destRefsCfg, Log log, String type) 
//  throws Exception {
//    String name = DbParser.convertName(log.getName());
//    if(destRefsCfg.existsSubConfiguration(name)) {//modify
//      Configuration logCfg = destRefsCfg.getSubConfiguration(name);
//      Configuration psCfg = logCfg.getSubConfiguration(PROPERTIES_NAME);
//      PropertySheet ps = psCfg.getPropertySheetInterface();
//      ps.getPropertyEntry(ASSOCIATION_TYPE).setValue(type);
//    } else {//create
//      Configuration logCfg = destRefsCfg.createSubConfiguration(name);
//      
//      Configuration psCfg = 
//        logCfg.createSubConfiguration(
//          PROPERTIES_NAME, Configuration.CONFIG_TYPE_PROPERTYSHEET);
//      
//      PropertySheet ps = psCfg.getPropertySheetInterface();
//      ps.createPropertyEntry(NAME, name, null);
//      ps.createPropertyEntry(ASSOCIATION_TYPE, type, null);
//    }
//  }
  
  
  private void checkChildren(LogController parent, Configuration cfg) throws Exception {
    if(parent.isCopyToSubtree()) {
      String severity = Severity.toString(parent.getEffectiveSeverity());
      for(LogController child: parent.getChildren()) {
        String name = DbParser.convertName(child.getName());
        if(cfg.existsSubConfiguration(name)) {
          cfg.deleteConfiguration(name);
          if(cfg.existsSubConfiguration(name)) {
            Configuration subCfg = cfg.getSubConfiguration(name);
            Configuration psCfg = subCfg.getSubConfiguration(PROPERTIES_NAME);
            PropertySheet ps = psCfg.getPropertySheetInterface();
            ps.getPropertyEntry(EFFECTIVE_SEVERITY).setValue(severity);
            try {
              ps.getPropertyEntry(COPY_TO_SUBTREE).setValue(String.valueOf(true));
            } catch(NameNotFoundException e) {
              ps.createPropertyEntry(COPY_TO_SUBTREE, String.valueOf(true), null);
            }
          }          
        }
        checkChildren(child, cfg);
      }
    }
  }
  
  
  
  private void removeAllChildren(LogController parent, Configuration cfg) throws Exception {
      for (LogController child: parent.getChildren()) {
          String name = DbParser.convertName(child.getName());
          boolean exists = cfg.existsSubConfiguration(name);
          if(exists) {
            cfg.deleteConfiguration(name);
            if (cfg.existsSubConfiguration(name)) {
                child.setCopyToSubtree(true);
                modifySubConfiguration(cfg, name, child, true);
            }
          }
          removeAllChildren(child, cfg);
      }
  }  
  
  private void saveChildren(LogController parent, Configuration cfg) throws Exception {
      for (LogController child: parent.getChildren()) {
          String name = DbParser.convertName(child.getName());
          boolean exists = cfg.existsSubConfiguration(name);
          if(exists) {
            modifySubConfiguration(cfg, name, child, false);
          } else {
            createSubConfiguration(cfg, name, child);
          }
//          saveChildren(child, cfg);
      }
  }
    
  /**
   * Method for ...
   * 
   * @return
   */
  private long getNextVersion() {
    configVersion++;
    configVersion = configVersion & Long.MAX_VALUE;
    synchronized(listeners) {
      for(EnumerationInt keyEnum = listeners.keys(); keyEnum.hasMoreElements();) {
        int key = keyEnum.nextElement();
        LogConfiguratorListener listener = (LogConfiguratorListener) listeners.get(key);
        try {
          listener.configurationChanged(configVersion);
        } catch(RemoteException e) {
          TRACER.traceThrowableT(
            Severity.ERROR,
            "Failed to notify one of the listeners for changed log configuration", 
            e);
          
          listeners.remove(key); 
        }
      }
    }
    return configVersion;
  }

  /**
   * @deprecated
   */
  public synchronized long unconfigure(LogConfiguration configuration) {
    return -1;
  }

  /**
   * Method for ...
   * 
   * @param controller
   * @param apiConfiguration
   */
  private void processLogController(
    LogController controller, LogConfiguration apiConfiguration, boolean processAll) {
    
    if(processAll || controller.isConfigurationChanged()) {
      LogControllerDescriptor desc = new LogControllerDescriptor();
      desc.setName(controller.getName());
      desc.setEffectiveSeverity(controller.getEffectiveSeverity());
      desc.setCopyToSubtree(controller.isCopyToSubtree()); 
      desc.setOriginOfSeverity(controller.getOriginOfSeverity());  
      
      
      apiConfiguration.addLogController(desc);
      apiConfiguration.addLogDestinations(getDestinations(controller, desc));
    }
    for(LogController child: controller.getChildren()) {
      processLogController(child, apiConfiguration, processAll);
    }
  }
  
  /**
   * @param controller
   * @return
   */
  private LogDestinationDescriptor[] getDestinations(
    LogController controller, LogControllerDescriptor descriptor) {
    
    Collection<LogDestinationDescriptor> res = new ArrayList<LogDestinationDescriptor>();
    for(Log log: controller.getAllLogs()) {
      LogDestinationDescriptor ldd = createDestinationDescriptor(log);
      res.add(ldd);
      byte associationType;
      switch(log.getLogTypeAccessibility()) {
        case Log.LOG_ACCESSABILITY_TYPE_LOCAL_LOG: 
          associationType = LogControllerDescriptor.ASSOCIATION_TYPE_LOCAL;
          break;
        case Log.LOG_ACCESSABILITY_TYPE_PRIVATE_LOG:
          associationType = LogControllerDescriptor.ASSOCIATION_TYPE_PRIVATE;
          break;
        default:
          associationType = LogControllerDescriptor.ASSOCIATION_TYPE_PUBLIC;
      }
      descriptor.addDestination(ldd, associationType);
    }
    return res.toArray(new LogDestinationDescriptor[res.size()]);
  }
  
  /**
   * @param log
   * @return
   */
  private LogDestinationDescriptor createDestinationDescriptor(Log log) {
    LogDestinationDescriptor res = new LogDestinationDescriptor();
    res.setName(log.getName());
    res.setEncoding(log.getEncoding());
    res.setRealLog(log);
    res.setEffectiveSeverity(log.getEffectiveSeverity());
    if(log instanceof FileLog) {
      FileLog fileLog = (FileLog) log;
      res.setType("FileLog");
      res.setCount(fileLog.getCnt());
      res.setLimit(fileLog.getLimit());
      res.setPattern(fileLog.getPattern());
      res.setFormatter(createFormatterDescriptor(fileLog.getFormatter()));
    } else {
      res.setType("ConsoleLog");
    }
    return res;
  }
  
  /**
   * @param formatter
   * @return
   */
  private LogFormatterDescriptor createFormatterDescriptor(Formatter formatter) {
    LogFormatterDescriptor res = new LogFormatterDescriptor();
    res.setRealFormatter(formatter);
    res.setName(formatter.getFormatterName());
    FormatterType formatterType = formatter.getFormatterType();
    String type = null;
    if(formatterType.equals(FormatterType.LISTFORMAT)) {
      type = "ListFormatter";
    } else if(formatterType.equals(FormatterType.TRACEFORMAT)) {
      type = "TraceFormatter";
    } else if(formatterType.equals(FormatterType.XMLFORMAT)) {
      type = "XMLFormatter";
    }
    res.setType(type);
    if(formatter instanceof TraceFormatter) {
      res.setPattern(((TraceFormatter) formatter).getPattern());
    }
    return res;
  }

  /** 
   *  
   */
  public synchronized LogConfiguration getCurrentConfiguration() {  
    LogConfiguration apiConfiguration = new LogConfiguration(configVersion);
    processLogController(Category.getRoot(), apiConfiguration, true);
    processLogController(Location.getRoot(), apiConfiguration, true);
//    processLogController(Category.getRoot(), apiConfiguration, false);
//    processLogController(Location.getRoot(), apiConfiguration, false);
    return apiConfiguration;
  }

  /** 
   *  
   */
  public synchronized LogConfiguration getCurrentConfigurationAll() {
    LogConfiguration apiConfiguration = new LogConfiguration(configVersion);
    processLogController(Category.getRoot(), apiConfiguration, true);
    processLogController(Location.getRoot(), apiConfiguration, true);
    return apiConfiguration;
  }

  /** 
   *  
   */
  public LogConfiguration getDefaultConfiguration() {
    return 
      loadConfiguration(
        CommonClusterFactory.LEVEL_CUSTOM_TEMPLATES, customTemplateId);
  }

  /** 
   *  
   */
  public synchronized LogConfiguration resetToDefaultConfiguration() {
		Collection<AdditionalConfiguration> dbConfigs = null;
		boolean skipWait = false;
		try {
			dbConfigs = openDbConfigs(true);
			for (AdditionalConfiguration addCfg : dbConfigs) {
				if(addCfg.getConfiguration().getPath().contains(thisInstanceId) &&
						((DerivedConfiguration)addCfg.getConfiguration()).getLocalSubConfigurations().size() == 0){
					skipWait = true;
				}else{
					addCfg.getConfiguration().deleteAllSubConfigurations();
				}
			}
		} catch (Exception e) {
			TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
		} finally {
			closeDbConfigs(dbConfigs);
		}
		try {
			if(!skipWait){
				wait();
			}
		} catch (InterruptedException e) {
			// do nothing here
		}
		return getCurrentConfiguration();
  }
  
  /**
   * Method for ...
   * 
   * @param level
   * @param id
   * @param inMemory
   * @return
   */
  private LogConfiguration loadConfiguration(int level, String id) {
    LogConfiguration res = new LogConfiguration();
    AdditionalConfiguration additionalCfg = null;
    try {
      additionalCfg = getAdditionalCfg(level, id, true);
      Configuration cfg = additionalCfg.getConfiguration();
      res = DbParser.parseDBConfiguration(cfg);
    } catch(Exception e) {
      TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
    } finally {
      try {
        if(additionalCfg != null) {
          additionalCfg.discardChanges();
        }
      } catch(ClusterConfigurationException e) {
        TRACER.traceThrowableT(Severity.ERROR, "Could not close configurations", e);
      }
    }
    return res;
  }

  /** 
   *  
   */
  public LogControllerDescriptor getDefaultConfigurationPerController(
    String controllerName) {
    
    LogControllerDescriptor result = null;
    AdditionalConfiguration additionalCfg = null;
    try {
      additionalCfg = 
        getAdditionalCfg(
          CommonClusterFactory.LEVEL_DEFAULT_TEMPLATES, defaultTemplateId, true);
      
      String name = DbParser.convertName(controllerName);
      Configuration defaultCfg = additionalCfg.getConfiguration();
      defaultCfg = defaultCfg.getSubConfiguration("log-controllers/" + name);
      Configuration psCfg = defaultCfg.getSubConfiguration(PROPERTIES_NAME);
      PropertySheet ps = psCfg.getPropertySheetInterface();
      
      result = new LogControllerDescriptor();
      result.setName(controllerName);
      
      result.setEffectiveSeverity(
        (String) ps.getPropertyEntry("effective-severity").getValue());
      
      try {
        result.setCopyToSubtree(
          Boolean.parseBoolean(
            (String) ps.getPropertyEntry("copy-to-subtree").getValue()));
      } catch(RuntimeException e) {
        result.setCopyToSubtree(true);
      }
    } catch(Exception e) {
      TRACER.traceThrowableT(Severity.ERROR, e.getMessage(), e);
    } finally {
      try {
        if(additionalCfg != null) {
          additionalCfg.discardChanges();
        }
      } catch(ClusterConfigurationException e) {
        TRACER.traceThrowableT(Severity.ERROR, "Could not close configurations", e);
      }
    }
    return result;
  }

  /** 
   *  
   */
  public void registerManagementListener(ManagementListener managementListener) {
  }

  /** 
   *  
   */
  public int registerListener(LogConfiguratorListener listener) {
    synchronized(listeners) {
      listeners.put(nextListenerId, listener);
      return nextListenerId++;
    }
  }

  /** 
   *  
   */
  public void unregisterListener(int listenerID) {
    synchronized(listeners) {
      listeners.remove(listenerID);
    }
  }

  /**
   * @deprecated
   */
  public synchronized void enableLogging() {
  } // </enableLogging()>

  /**
   * @deprecated
   */
  public synchronized void disableLogging() {
  }

  /** 
   *  
   */
  public synchronized boolean isLoggingDisabled() {
    return noLogging;
  }

  /** 
   *  
   */
  public boolean isSQLTrace() {
    return SQLTrace.isOn();
  }

  /**
   * @deprecated
   */
  public void setSQLTrace(boolean newStatus) {
  }

  /** 
   *  
   */
  public synchronized String[] getLogControllersNames() {
    return getCurrentConfiguration().getLogControllersNames();
  }

  /** 
   *  
   */
  public synchronized String[] getPhysicalNames() {
    Collection<String> names = new TreeSet<String>();
    for(LogDestinationDescriptor ldd: mainConfiguration.getLogDestinations()) {
      Log realLog = ldd.getRealLog();
      if(realLog instanceof FileLog) {
        List fileNames = ((FileLog) realLog).calculateFileNames();
        for(int i = 0; i < fileNames.size(); i++) {
          names.add((String) fileNames.get(i));
        }
      }
    }
    FileLog defaultTraceFile = logManager.getDefaultTraceFile();
    if(defaultTraceFile != null) {
      List fileNames = defaultTraceFile.calculateFileNames();
      for(int i = 0; i < fileNames.size(); i++) {
        names.add((String) fileNames.get(i));
      }
    }
    return names.toArray(new String[names.size()]);
  }

  /** 
   *  
   */
  public long getFileSize(String fileName) {
    return new File(fileName).length();
  }

  /** 
   *  
   */
  public synchronized int getLoggedCount(String controllerName) {
    LogController controller = getLogController(controllerName);
    if(controller == null) {
      return 0;
    } else {
      return controller.getLoggedCount();
    }
  }

  /**
   * @deprecated
   */
  public void unregisterLogController(String name) {
  }

  /**
   * @deprecated
   */
  public void unregisterLog(String name) {
  }




  /* (non-Javadoc)
   * @see com.sap.engine.services.log_configurator.admin.LogConfiguratorManagementInterface#archive(java.lang.String[], boolean, java.lang.String)
   */
  public String archive(
    String[] names, boolean defaultTracesSelected, String archiveDir) {
    
    LogDestinationDescriptor[] ldd = new LogDestinationDescriptor[names.length];
    for(int i = 0; i < names.length; i++) {
      ldd[i] = mainConfiguration.getLogDestination(names[i]);
    }
    return new Archivator(this).archive(
      ldd, defaultTracesSelected, archiveDir, false, null, null);
  }

  /**
   * @deprecated
   */
  public long applyConfiguration(Properties properties) {
    return -1;
  }

  /**
   * @deprecated
   */
  public long applyConfiguration(String configurationFileName) {
    return -1;
  }

  /**
   * @deprecated
   */
  public long applyConfiguration(LogConfiguration configuration) {
    return -1;
  }

  /**
   * @deprecated
   */
  public long applyConfiguration(
    LogConfiguration addEditCfg, LogConfiguration removeCfg) {
    
    return -1;
  }

  /**
   * @deprecated
   */
 /* public long applyConfiguration(
    LogConfiguration addEditCfg, 
    LogConfiguration removeCfg, 
    String componentName, 
    PersistenceAdapter adapter) {
    
    return -1;
  }*/

  /**
   * @deprecated
   */
  public long applyAndStoreConfiguration(LogConfiguration configuration) {
    return -1;
  }

  /**
   * @deprecated
   */
  public long applyAndStoreConfiguration(
    LogConfiguration addEditCfg, LogConfiguration removeCfg) {
    
    return -1;
  }
  
  /**
   * This method sets a given severity to a list of DCs. It is
   * intended to be used from the Application Manager for the
   * purposes of Application Centric Administration
   * 
   * @param dcs A list for DCs whose severities will be changed
   * @param Severity The new severity value (if <code>null</code>
   * a reset to previous severity is assumed)
   */
  public void setLocationSeverityPerDC(String[] dcs, String severity) {

		try {
			// construct a set of dcs to be updated
			Set<String> dcsToUpdate = new HashSet<String>(Arrays.asList(dcs));
			TRACER.debugT("ACA Configuration : The severity of the following dcs :" 
					+ dcsToUpdate.toString() + " is being " + (severity != null ? "set to " + severity : "reset"));

			// read the LogManagerPropertySheet
			Properties logManagerProps = getClusterManagerProperties(LOG_MANAGER_NAME);

			String acaValue = logManagerProps.getProperty("ACAConfiguration");
			if (acaValue == null) {
				acaValue = "";
			}

			String[] acaEntries = acaValue.split(";");
			String newACAValue = "";

			for (int i = 0; i < acaEntries.length; i++) {
				// ignore empty entries
				if (acaEntries[i].length() == 0) {
					continue;
				}

				String[] dcSeverityPair = acaEntries[i].split("-");

				//verify the dc-severity format
				if (dcSeverityPair.length == 2) {
					String dcName = dcSeverityPair[0];
					if (severity == null) {
						// reset						
						if (!dcsToUpdate.contains(dcName)) {
							// if not to be reset keep the entry
							newACAValue += acaEntries[i] + ";";
						}
					} else {
						// add new dcs to ACAConfiguration
						if (dcsToUpdate.contains(dcName)) {
							// if DC already present update severity
							newACAValue += dcName + "-" + severity + ";";
							// remove used entry
							dcsToUpdate.remove(dcName);
						} else {
							// keep old setting
							newACAValue += acaEntries[i] + ";";
						}
					}
				}
			}

			if (severity != null) {
				// if this is not a reset case - append what's left in the update set
				if (!dcsToUpdate.isEmpty()) {
					Iterator<String> it = dcsToUpdate.iterator();
					while (it.hasNext()) {
						String key = it.next();
						newACAValue += key + "-" + severity + ";";
					}
				}
			}

			// set new property to log manager
			Properties acaProperties = new Properties();
			acaProperties.setProperty("ACAConfiguration", newACAValue);

			// access the InstanceAdmin remote object through JNDI and
			// reflection (triggers event)
			InitialContext initialCtx = new InitialContext();
			Object instAdmin = initialCtx.lookup("admin/InstanceAdmin");
			Class<?> instAdminClass = instAdmin.getClass();
			Method updateTemplateProperties = instAdminClass.getMethod(
					"updateTemplateProperties", Integer.TYPE, String.class,
					String.class, Properties.class, String[].class, Long.TYPE,
					Boolean.TYPE);
			int componentTypeManager = instAdminClass.getField(
					"COMPONENT_TYPE_MANAGER").getInt(instAdmin);
			updateTemplateProperties.invoke(instAdmin, componentTypeManager,
					"sap.com", LOG_MANAGER_NAME, acaProperties, null,
					new Date().getTime(), true);

		} catch (Exception e) {
			TRACER.traceThrowableT(Severity.ERROR, "Unexpected Exception:", e);
		}
	}
  
  /**
	 * This method returns the Properties describing the current
	 * state of Application Centric Administration confiuration. The keys are
	 * the names of affected DCs and the values are the custom severities per DC
	 * set by the Application Manager.
	 * 
	 * @return a Properties object (key = dcName, value = severity set by ACA)
	 */
	public Properties getLocationSeverityPerDC() {
	  Properties acaConfigurationProps = new Properties();

	  String acaCfgValue = 
			getClusterManagerProperties(LOG_MANAGER_NAME).getProperty("ACAConfiguration");
		if (acaCfgValue != null) {
	    String[] acaValues = acaCfgValue.split(";");
	    if (acaValues != null) {
	      for (int i = 0; i < acaValues.length; i++) {
	        int dashIdx = acaValues[i].indexOf("-");
	        if (dashIdx > -1) {
	          String dcName = acaValues[i].substring(0, dashIdx);
	          String dcSeverity = acaValues[i].substring(dashIdx + 1);
	          acaConfigurationProps.setProperty(dcName, dcSeverity);
	        }
	      }
	    }
		}

		return acaConfigurationProps;
	}
  
  private Properties getClusterManagerProperties(String managerName) {
	  Properties logManagerProps = new Properties();
	  try {
		  
		  //read an instance ID and, get the corresponding instance template and get
		  //its parent - the active custom template
		  String[] instanceIDs = 
			  clusterFactory.listIdentifiers(CommonClusterFactory.LEVEL_INSTANCE); 
		  if (instanceIDs.length == 0) {
			  throw new Error("No instance templates found on system!");
		  }
		  String instanceID = instanceIDs[0];
		  	  
		  //gets the parent of instance configuration level for instance instanceID
		  ConfigurationLevel cfgLevel = clusterFactory.openConfigurationLevel(
				  CommonClusterFactory.LEVEL_INSTANCE, instanceID).getParent();
			ManagerHandler managerHandle = cfgLevel.getManagerAccess();
			ComponentProperties compProps = managerHandle.getProperties(
					managerName, true);
			logManagerProps = compProps.getPropertySheet().getProperties();
	  } catch (Exception e) {
		  TRACER.traceThrowableT(Severity.ERROR, "Unexpected Exception:", e);
	  }
	  return logManagerProps;
	}

  /** 
   *  
   */
  public synchronized long modifyConfiguration(
    LogConfiguration newSettings, 
    LogConfiguration removedSettings, 
    boolean clusterWide) {
    
    removeConfiguration(removedSettings, clusterWide);
    addEditConfiguration(newSettings, clusterWide);
    return getNextVersion();
  }

  /**
   * @deprecated
   */
  public void exportConfiguration(String xmlFileName) {
  }

  /**
   * @deprecated
   */
  public long importConfiguration(String xmlFileName, boolean onScenarioLevel) {
    return 0;
  }

  /**
   * @see LogConfiguratorManagementInterface#applyConfiguration(LogConfiguration
   *      newSettings, LogConfiguration removedSettings, boolean clusterWide)
   * @deprecated
   */
  public long applyConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings, boolean clusterWide) {

    return -1;
  }

  /**
   * @see LogConfiguratorManagementInterface#applyAndStoreConfiguration(LogConfiguration
   *      newSettings, LogConfiguration removedSettings, boolean clusterWide)
   * @deprecated
   */
  public long applyAndStoreConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings, boolean clusterWide) {

    return -1;
  }

  /**
   * @see LogConfiguratorManagementInterface#modifyDefaultConfiguration(LogConfiguration
   *      newSettings, LogConfiguration removedSettings)
   * @deprecated
   */
  public long modifyDefaultConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings) {
    
    return modifyDefaultConfiguration(newSettings, removedSettings, true);
  }

  /**
   * @see LogConfiguratorManagementInterface#modifyDefaultConfiguration(LogConfiguration
   *      newSettings, LogConfiguration removedSettings, boolean applyToCurrent)
   * @deprecated
   */
  public synchronized long modifyDefaultConfiguration(
    LogConfiguration newSettings, 
    LogConfiguration removedSettings, 
    boolean applyToCurrent) {
    
    return -1;
  }

  /** 
   * 
   */
  public String archiveAllLogs(
    String archiveFileName, 
    String fileLogsZipPath,
    String[] fileLogPatterns, 
    boolean includeDefaultTraceFile) {
    
    try {
      ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(archiveFileName));
      String result = "";
      byte[] buffer = new byte[1024];

      if(includeDefaultTraceFile) {
        FileLog defaultTraceFile = logManager.getDefaultTraceFile();
        if(defaultTraceFile != null) {
          result += addToZip(defaultTraceFile, fileLogsZipPath, zout, buffer);
        }
      }

      for(LogDestinationDescriptor ldd: mainConfiguration.getLogDestinations()) {
        Log dst = ldd.getRealLog();
        if(dst instanceof FileLog) {
          for(String filePattern: fileLogPatterns) {
            if(ldd.getPattern().equals(filePattern)) {
              result += addToZip((FileLog) dst, fileLogsZipPath, zout, buffer);
              break;
            }
          }
        }
      }

      zout.close();
      return result;
    } catch(Exception e) {
      TRACER.traceThrowableT(
        Severity.ERROR, 
        "Failed to create archive file \""+ archiveFileName + "\"!", 
        e);
      
      return "Failed to create archive file \"" + archiveFileName + "\" due to: " + e;
    }
  }

  /**
   * Method for ...
   * 
   * @param log
   * @param zipEntryPrefix
   * @param zout
   * @param buffer
   * @return
   */
  private static String addToZip(
    FileLog log, String zipEntryPrefix, ZipOutputStream zout, byte[] buffer) {
    
    String result = "";
    List fileNames = log.calculateFileNames();
    for(int i = 0; i < fileNames.size(); i++) {
      String fileName = (String) fileNames.get(i);
      FileInputStream in = null;
      try {
        File f = new File(fileName);
        if(f.length() == 0) {
          continue;
        }

        in = new FileInputStream(f);
        zout.putNextEntry(new ZipEntry(zipEntryPrefix + f.getPath()));
        int bytesRead = 0;

        while((bytesRead = in.read(buffer)) != -1) {
          zout.write(buffer, 0, bytesRead);
        }
      } catch(Exception e) {
        TRACER.traceThrowableT(
          Severity.ERROR, 
          "Failed to archive \"" + fileName + "\" file!", 
          e);
        
        result += "Failed to archive \"" + fileName + "\" file due to: " + e + "\n";
      } finally {
        try {
          if(in != null) in.close();
        } catch(IOException e) {
        }
      }
    }
    return result;
  }

  /** 
   *  
   */
  public byte[] readFromFile(String fileName, long offset, int length) {
    FileInputStream in = null;
    try {
      in = new FileInputStream(fileName);
      in.skip(offset);
      byte[] buffer = new byte[length];
      int readed = in.read(buffer);
      if(readed != length) {
        byte[] result = new byte[readed];
        System.arraycopy(buffer, 0, result, 0, readed);
        return result;
      } else {
        return buffer;
      }
    } catch(Exception e) {
      return null;
    } finally {
      try {
        if(in != null) in.close();
      } catch(IOException e) {
        TRACER.traceThrowableT(
          Severity.ERROR, "Exception while closing FileInputStream", e);
      }
    }
  }

  /** 
   *  
   */
  public void deleteArchive(String fileName) {
    new File(fileName).delete();
  }

  /**
   * @deprecated
   */
  public void setState(byte state, String parent, String[] descriptorNames) {
  }

  // the method is used from LogViewer in online case (i.e. when server is running)
  public String[] getArchiveFileNames(String logFilePattern) {
    return archivesInfo.getArchiveFileNames(logFilePattern);
  }

  /** 
   *  
   */
  public long getTotalArchiveSize() {
    return archivesInfo.getArchivesSize();
  }

  /** 
   *  
   */
  public long getTotalFileSize() {
    long size = 0;
    for(String fileName: getPhysicalNames()) {
      size += getFileSize(fileName);
    }
    // size is in bytes - convert into KB
    return (size / 1024);
  }

  /*
   *  the method is used from LogViewer in online case (i.e. when server is running)
   */
  public String getArchiveLogDirectory() {
    return archivesInfo.getArchiveLogDirectory();
  }

  /*
   * the method is used from LogViewer in offline case (i.e. when server is not running)
   */
  public static String[] getArchiveFileNames(String archiveLogDirectory, String pattern) {
    return LogArchivesInfo.getArchiveFileNames(archiveLogDirectory, pattern);
  }
  
  /**
   * @see LogConfiguratorManagementInterface#getClusterNodeType()
   */
  public byte getClusterNodeType() {
    return nodeType;
  }
  
  public void configurationChanged(ChangeEvent changeEvent) {
    LogConfiguration cfg = 
      loadConfiguration(CommonClusterFactory.LEVEL_INSTANCE, thisInstanceId);
    
    synchronized(this) {
      LogConfigurationUpdater.updateLoggingAPI(cfg, false);
      LogController.setSeverityFromDC(getLocationSeverityPerDC());
      notifyAll();
    }
  }
  
  
  private void writeCCMSTemplates(ClusterElement currentNode, Properties properties) {
  	CCMSTemplate.init(currentNode, properties);
  	for(LogDestinationDescriptor ldd: mainConfiguration.getLogDestinations()) {  		
  		writeCCMSTemplate(ldd);  		
  	}
  }
  
  private void writeCCMSTemplate(LogDestinationDescriptor ldd){
  	try {
  		CCMSTemplate.getCCMSTemplate().writeTemplate(ldd);
  	} catch(Exception e) {
  		TRACER.traceThrowableT(
        Severity.WARNING, 
        "Could not create a CCMS Template for log: " + ldd.getName(), 
        e);
  	}
  }
  
  public void resetSeverityCounters() {
      LogController.resetSeverityCounters();
  }
  
  public long getDebugCounter() {
      return LogController.getDebugCounter();
  }
  
  public long getPathCounter() {
      return LogController.getPathCounter();
  }
  
  public long getInfoCounter() {
      return LogController.getInfoCounter();
  }
  
  public long getWarningCounter() {
      return LogController.getWarningCounter();
  }
  
  public long getErrorCounter() {
      return LogController.getErrorCounter();
  }
  
  public long getFatalCounter() {
     return LogController.getFatalCounter();
  }

  /**
   * 
   * @return count of the logs/traces that have been issued
   *         since last reset with resetSeverityCounters() method 
   */
  public long getTotalLogsCount() {
      return LogController.getTotalLogsCount();
  }

}
