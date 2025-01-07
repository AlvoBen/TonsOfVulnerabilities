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

import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.engine.lib.logging.descriptors.LogControllerDescriptor;

import java.util.Properties;

/**
 * @author Miroslav Petrov
 * @version 6.30
 */
public interface LogConfiguratorManagementInterface extends ManagementInterface { 

//	public static final String consoleLogsPath = "./log/console_logs/";

  /**
   * Applies the given log configuraiton to the current log configuration.
   *
   * @param configuration  the new (additional) configuraiton
   * @return  the version of the resulting log configuration
   */
  public long applyConfiguration(LogConfiguration configuration);

  /**
   * Applies the given log configuraiton to the current log configuration.
   *
   * @param addEditCfg  the new (additional) configuraiton
   * @param removeCfg  names of all removed elements
   * @return  the version of the resulting log configuration
   */
  public long applyConfiguration(LogConfiguration addEditCfg, LogConfiguration removeCfg);

  public long applyAndStoreConfiguration(
    LogConfiguration configuration);

  public long applyAndStoreConfiguration(
    LogConfiguration addEditCfg, LogConfiguration removeCfg);

  public long modifyConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings, boolean clusterWide);

  public void exportConfiguration(String xmlFileName);

  public long importConfiguration(String xmlFileName, boolean onScenarioLevel);

  /**
   * Modifies the currently effective runtime log configuration according to the given settings.
   *
   * @param newSettings  settings that have to be applied to the existing configuraiton
   * @param removedSettings  elements that have to be removed from the existing 
   * configuraiton
   * @param clusterWide  flag for distributing the changes to other nodes in the cluster
   * @return  the current runtime version of the resulting effective log configuration
   */
  public long applyConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings, boolean clusterWide);

  /**
   * Persistently modifies the currently effective log configuration according to the given 
   * settings.
   *
   * @param newSettings  settings that have to be applied to the existing configuraiton
   * @param removedSettings  elements that have to be removed from the existing 
   * configuraiton
   * @param clusterWide  flag for distributing the changes to other nodes in the cluster
   * @return  the current runtime version of the resulting effective log configuration
   */
  public long applyAndStoreConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings, boolean clusterWide);

  /**
   * Persistently modifies the installation default log configuration according to the given
   * settings and applies these changes to the currently effective one. A call to this method
   * is equivalent to invoking <code>modifyDefaultConfiguration(newSettings, 
   * removedSettings, true)</code>.
   *
   * @param newSettings  settings that have to be applied to the existing configuraiton
   * @param removedSettings  elements that have to be removed from the existing 
   * configuraiton
   * @return  the current runtime version of the resulting effective log configuration
   */
  public long modifyDefaultConfiguration(
    LogConfiguration newSettings, LogConfiguration removedSettings);

  /**
   * Persistently modifies the installation default log configuration according to the given
   * settings and optionally applies these changes to the currently effective one. The 
   * adjustment is performed cluster-wide on all nodes that have the same functional type 
   * as the element on which this method is invoked.
   *
   * @param newSettings  settings that have to be applied to the existing configuraiton
   * @param removedSettings  elements that have to be removed from the existing 
   * configuraiton
   * @param applyToCurrent  flag for modification of the currently effective log configuraiton
   * @return  the current runtime version of the resulting effective log configuration,
   *  or -1 if the applyToCurrent flag is false.
   */
  public long modifyDefaultConfiguration(
    LogConfiguration newSettings, 
    LogConfiguration removedSettings, 
    boolean applyToCurrent);

//	/**
//	 * Applies configuration opposite to the given log configuraiton over the current log 
//   * configuration.
//	 *
//	 * @param configuration the log configuration to be undone
//	 * @return the version of the resulting log configuraiton
//	 */
//	public long unconfigure(LogConfiguration configuration);

  /**
   * Retireves the current log configuration.
   *
   * @return  the current log configuration
   */
  public LogConfiguration getCurrentConfiguration();

  public LogControllerDescriptor getDefaultConfigurationPerController(
    String logControllerName);

  /**
   * Retireves the installation default log configuration.
   *
   * @return  the installation default log configuration
   */
  public LogConfiguration getDefaultConfiguration();

  /**
   * Resets the current log configuration to the installation default settings.
   * Any existing customizations are permanently discarded.
   *
   * @return the newly effective log configuration.
   */
  public LogConfiguration resetToDefaultConfiguration();

//	/**
//	 * Resets the current log configuration to the installation default settings.
//	 * Any existing node-local customizations are permanently discarded. Any existing
//	 * cluster-wide customizations are either permanently discarded, or left intact,
//	 * depending on the value of the &quot;leaveClusterWideChanges&quot; parameter.
//	 *
//	 * @param leaveClusterWideChanges flag for removal of cluster-wide customizations
//	 * @return the newly effective log configuration.
//	 */
//	public LogConfiguration resetToDefaultConfiguration(boolean leaveClusterWideChanges);

  /**
   * Retrieves the functional type of the cluster node on which this method is invoked
   * as defined in the <code>com.sap.engine.frame.cluster.ClusterElement</code> class.
   *
   * @return the functional type of this cluster element.
   * @see com.sap.engine.frame.cluster.ClusterElement#DISPATCHER
   * @see com.sap.engine.frame.cluster.ClusterElement#SERVER
   */
  public byte getClusterNodeType();

  /**
   * Applies a log configuration that is described in properties format.
   *
   * @param properties  the Properties object describing the log configuraiton
   * @deprecated  use xml-based configuration instead of the old properties-based
   * configuration (see also LogConfiguraiton.readFromXmlStream(InputStream is) method)
   * @return the version of the resulting configuration
   */
  public long applyConfiguration(Properties properties);


//	/**
//	 * Applies a log configuration that is described in properties format and is read from the 
//   * given file.
//	 *
//	 * @param fileName  the name of the file from which the properties describing the log 
//   * configuration are retrieved
//	 * @deprecated  use xml-based configuration instead of the old properties-based
//	 * configuration (see also LogConfiguraiton.readFromXmlStream(InputStream is) method)
//	 * @return the version of the resulting configuration
//	 */
  public long applyConfiguration(String configurationfileName);

  /**
   * Registers an event listener that is notified when a new version of the log configuration 
   * appears.
   *
   * @param listener the listener to be registered
   * @return  the id of the registered listener
   */
  public int registerListener(LogConfiguratorListener listener);

  /**
   * Unregisters a log configuration listener.
   *
   * @param listenerID the id of the listener to be unregistered
   */
  public void unregisterListener(int listenerID);

  /**
   * Globally enables the output for all configured log controllers.
   */
  public void enableLogging();

  /**
   * Globally disables the output for all configured log controllers.
   */
  public void disableLogging();

  /**
   * Returns the current output state for all configured log controllers.
   *
   * @return  true if logging is currently disabled, or false otherwise.
   */
  public boolean isLoggingDisabled();

  /**
   * Sets SQL Trace On/Off
   *
   * @param newStatus on / off
   */
  public void setSQLTrace( boolean newStatus );

  /**
   * Returns the status of SQL Trace
   *
   * @return on / off
   */
  public boolean isSQLTrace();

  /**
   * Returns a String array containing the names of all currently configured
   * log controllers or an empty array if none are present.
   *
   * @return  the names of all currently configured log controllers.
   */
  public String[] getLogControllersNames();

  /**
   * Returns a String array containing the names of all currently configured
   * physical files or an empty array if none are present.
   *
   * @return  the names of all currently configured physical files.
   */
  public String[] getPhysicalNames();

  /**
   * Returns the size of the physical file with the specified name
   * or zero if it is not on the file system.
   *
   * @return  the size of the physical file with the specified name
   *          or zero if no such file is present.
   */
  public long getFileSize(String fileName);

  /**
   * Returns the number of records written through the LogController
   * with the specified name or zero if it is not configured.
   *
   * @return  the number of records written through the LogController
   *          with the specified name or zero if it is not configured.
   */
  public int getLoggedCount(String controllerName);

  /**
   * Archives logs
   *
   * @param names names of all log files that must be archived
   * @param defaultTracesSelected true if default traces must be archived
   * @param archiveDir directory where the archive must be
   * @return the method output - logs and errors
   */
  public String archive( String[] names, boolean defaultTracesSelected, String archiveDir );

  /**
   * Archives logs
   *
   * @param archiveFileName the name of zip file that will be created
   * @param fileLogsZipPath the path in zip for file logs
   * @param fileLogPatterns the names of all file logs that must be archived
   * @param includeDefaultTraceFile shows whether default traces should be archived or not
   * @return error logs or "" if all operations are done without errors
   */
  public String archiveAllLogs(
    String archiveFileName, 
    String fileLogsZipPath, 
    String[] fileLogPatterns, 
    boolean includeDefaultTraceFile);

  /**
   * Reads bytes from file
   *
   * @param fileName the path and the name of file
   * @param offset the offset in file
   * @param length the count of bytes that must be readed
   * @return readed bytes
   */
  public byte[] readFromFile( String fileName, long offset, int length );

  /**
   * Delete file - uses for archives created with archiveAllLogs method,
   * but can delete every other file
   *
   * @param fileName the path and the name of file for delete
   */
  public void deleteArchive( String fileName );

  /**
   * Change state for several Log Controllers
   *
   * @param state new state
   * @param parent the name of first Log Controller in the hierarchy that must be changed
   * @param descriptors the names of Log Controllers which must be changed (all subtree 
   * from parent)
   */
  public void setState( byte state, String parent, String[] descriptors );

  /**
   * Returns full path to the all available archives made from logs from specific FileLog
   *
   * @param logFilePattern the pattern of the FileLog
   * @return list of Strings
   */
  public String[] getArchiveFileNames( String logFilePattern );

  /**
   * Returns the combined size in KB of all log and trace archives on the file system of the 
   * current cluster node.
   *
   * @return  the total size of all log and trace archive files.
   */
  public long getTotalArchiveSize();

  /**
   * Returns the combined size in KB of all log and trace files on the current cluster node 
   * exluding archives.
   *
   * @return  the total size of all log and trace files.
   */
  public long getTotalFileSize();

  public String getArchiveLogDirectory();
  
  public void resetSeverityCounters();
  
  public long getDebugCounter();

  public long getPathCounter();
  
  public long getInfoCounter();
  
  public long getWarningCounter();
  
  public long getErrorCounter();
  
  public long getFatalCounter();

  /**
   * 
   * @return count of the logs/traces that have been issued
   *         since last reset with resetSeverityCounters() method 
   */
  public long getTotalLogsCount();

  public void setLocationSeverityPerDC(String[] dcs, String severity);

  public Properties getLocationSeverityPerDC();
  
}