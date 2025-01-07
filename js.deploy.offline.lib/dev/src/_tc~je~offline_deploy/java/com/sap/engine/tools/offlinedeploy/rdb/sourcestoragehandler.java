/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;

import java.io.InputStream;

/**
 * @author Petar Petrov (i030687)
 * @version 7.20
 *          Date: 2009-1-21
 */
public class SourceStorageHandler {

  // CAUTION!  In runtime, this class must not call other classes that are outside of
  //           bin/system directory.  This class is loaded online
  //           by the system:Frame classloader and it cannot access classes from the
  //           system:Kernel classloader (bin/kernel).  This includes some classes from
  //           the same package com.sap.engine.tools.offlinedeploy.rdb.

  // constants //

  // static fields //

  // fields //

  // public static methods //

  /**
   * Try to open and return a specified <code>Configuration</code> for <code>READ_ACCESS</code>.
   * If <code>ConfigurationLockedException</code> is thrown, it retries <code>retryCount</code>
   * times, sleeping 100 ms before trying again.  If after <code>retryCount</code> retries
   * the configuration open still throws <code>ConfigurationLockedException</code>, the
   * exception is rethrown from this method.  If <code>InterruptedException</code> is
   * catched during sleep, it is ignored.
   *
   * <p>The caller of this method is responsibie to close the obtained <code>Configuration</code>
   * instance after use.</p>
   *
   * @param handler  the handler used to open the configuration
   * @param configurationName  the configuration path
   * @param retryCount  number of attempts to open the configuration before rethrowing a <code>ConfigurationLockedException</code>, if any
   * @return  The specified Configuration in <code>READ_ACCESS</code> mode from the handler.
   * @throws ConfigurationLockedException  if the configuration cannot be open after <code>retryCount</code> attempts
   * @throws ConfigurationException  if an error occured during openConfiguration
   * @see com.sap.engine.frame.core.configuration.ConfigurationHandler#openConfiguration(String, int)
   */
  public static Configuration openConfigurationForRead(final ConfigurationHandler handler, final String configurationName, int retryCount) throws ConfigurationException {
    while (true) {
      try {
        return handler.openConfiguration(configurationName, ConfigurationHandler.READ_ACCESS);
      } catch (ConfigurationLockedException cle) {
        if (retryCount-- == 0) {
          throw cle;
        }
        try {
          Thread.sleep(100);
        } catch(InterruptedException ie) {
          // $JL-EXC$
        }
      }
    }
  }

  /**
   * Open and return an <code>InputStream</code> for reading the sources archive
   * (<code>"src.zip"</code>) file of the specified deployed component from the configuration.
   * The current method uses <code>openConfigurationForRead(ConfigurationHandler, String, int)</code>
   * method of this class.
   *
   * <p>The caller of this method is responsibie to close the obtained <code>InputStream</code>
   * instance after use.</p>
   *
   * @param handler  the handler used to open the configuration that gets the <code>InputStream</code>.
   *                 The <code>Configuration</code> itself is closed even when error occured.
   * @param componentName  configuration-safe deploy name of the desired component, e.g. <code>"tc~je~webcontainer"</code> (not <code>"tc/je/webcontainer"</code>)
   * @param vendor  configuration-safe name of the component vendor, e.g. <code>"sap.com"</code>
   * @param retryCount  number of attempts to open the configuration before rethrowing a <code>ConfigurationLockedException</code>, if any
   * @return  An <code>InputStream</code> for reading the sources archive file from configuration.
   * @throws ConfigurationLockedException  if the configuration cannot be open after <code>retryCount</code> attempts
   * @throws ConfigurationException  if an error occured during openConfiguration
   * @see #openConfigurationForRead(com.sap.engine.frame.core.configuration.ConfigurationHandler, String, int)
   * @see com.sap.engine.frame.core.configuration.Configuration#getFile(String)
   */
  public static InputStream getSrcZipStream(final ConfigurationHandler handler, final String componentName, final String vendor, final int retryCount) throws ConfigurationException {
    ensureValidComponentName(componentName);
    ensureValidComponentVendor(vendor);

    final String fileName = getSrcZipFileName(componentName, vendor);
    final String configurationName = getSrcZipConfigurationName(componentName, vendor);

    Configuration myCfg = null;
    try {
      myCfg = openConfigurationForRead(handler, configurationName, retryCount);
      return myCfg.getFile(fileName);
    } finally {
      if (myCfg != null) {
        myCfg.close();
      }
    }
  }

  /**
   * Check if the sources archive
   * (<code>"src.zip"</code>) file exists for the specified deployed component from the configuration.
   * The current method uses <code>openConfigurationForRead(ConfigurationHandler, String, int)</code>
   * method of this class.
   *
   * @param handler  the handler used to open the configuration.
   *                 The <code>Configuration</code> itself is closed even when error occured.
   * @param componentName  configuration-safe deploy name of the desired component, e.g. <code>"tc~je~webcontainer"</code> (not <code>"tc/je/webcontainer"</code>)
   * @param vendor  configuration-safe name of the component vendor, e.g. <code>"sap.com"</code>
   * @param retryCount  number of attempts to open the configuration before rethrowing a <code>ConfigurationLockedException</code>, if any
   * @return  <code>true</code> if the component is deployed and its source archive file exists in the configuration.
   * @throws ConfigurationLockedException  if the configuration cannot be open after <code>retryCount</code> attempts
   * @throws ConfigurationException  if an error occured during openConfiguration
   * @see #openConfigurationForRead(com.sap.engine.frame.core.configuration.ConfigurationHandler, String, int)
   * @see com.sap.engine.frame.core.configuration.Configuration#existsFile(String)
   */

  public static boolean hasSrcZip(final ConfigurationHandler handler, final String componentName, final String vendor, final int retryCount) throws ConfigurationException {
    ensureValidComponentName(componentName);
    ensureValidComponentVendor(vendor);

    final String fileName = getSrcZipFileName(componentName, vendor);
    final String configurationName = getSrcZipConfigurationName(componentName, vendor);

    Configuration myCfg = null;
    try {
      myCfg = openConfigurationForRead(handler, configurationName, retryCount);
      return myCfg.existsFile(fileName);
    } finally {
      if (myCfg != null) {
        myCfg.close();
      }
    }
  }

  // constructors //

  protected SourceStorageHandler() {
    super();
  }

  // public methods //

  // package methods //

  // protected methods //

  protected static void ensureValidComponentName(final String componentName) {
    if (componentName == null) {
      throw new IllegalArgumentException("Parameter [componentName] is null");
    }

    if (componentName.length() == 0) {
      throw new IllegalArgumentException("Parameter [componentName] is empty string");
    }
  }

  protected static void ensureValidComponentVendor(final String vendor) {
    if (vendor == null) {
      throw new IllegalArgumentException("Parameter [vendor] is null");
    }

    if (vendor.length() == 0) {
      throw new IllegalArgumentException("Parameter [vendor] is empty string");
    }
  }

  protected static String getSrcZipConfigurationName(final String componentName, final String vendor) {
    return Constants.SRC_ZIP + "/" + vendor;
  }

  protected static String getSrcZipFileName(final String componentName, final String vendor) {
    return componentName + ".zip";
  }

  // private static methods //

  // private methods //

  // inner classes //

}