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
import com.sap.engine.frame.core.configuration.NameNotFoundException;

import java.util.zip.ZipFile;
import java.io.IOException;

/**
 * @author Petar Petrov (i030687)
 * @version 7.20
 *          Date: 2009-1-22
 */
public class KernelSourceStorageHandler extends SourceStorageHandler {

  // CAUTION!  In runtime, this class must not be called from classes that are outside
  //           of bin/kernel directory.  This class is loaded online by the system:Kernel
  //           classloader and it cannot be accessed by classes from the system:Frame
  //           classloader (bin/system).  This includes some classes from
  //           the same package com.sap.engine.tools.offlinedeploy.rdb.

  // constants //

  // static fields //

  // fields //

  // public static methods //

  static void storeSrcZip(final ConfigurationHandler handler, final ZipFile sda, final String componentName, final String vendor) throws ConfigurationException, IOException, DeploymentException {
    ensureValidComponentName(componentName);
    ensureValidComponentVendor(vendor);

    Configuration srcZipRoot = Utils.openConfiguration(handler, Constants.SRC_ZIP);
    Configuration vendorConf = Utils.createSubConfiguration(srcZipRoot, vendor);
    final String fileName = getSrcZipFileName(componentName, vendor);
    Utils.addFileInDB(sda, Constants.SRC_ZIP, vendorConf, fileName, true);
  }

  static void removeSrcZip(final ConfigurationHandler handler, final String componentName, final String vendor) throws ConfigurationException {
    ensureValidComponentName(componentName);
    ensureValidComponentVendor(vendor);

    Configuration srcZipRoot = Utils.openConfiguration(handler, Constants.SRC_ZIP);
    Configuration vendorConf = Utils.createSubConfiguration(srcZipRoot, vendor);
    final String fileName = getSrcZipFileName(componentName, vendor);
    try {
      vendorConf.deleteFile(fileName);
    } catch (NameNotFoundException ignored) {
      // $JL-EXC$  No sources to delete on component remove
    }
  }

  // constructors //

  protected KernelSourceStorageHandler() {
    super();
  }

  // public methods //

  // package methods //

  // protected methods //

  // private static methods //

  // private methods //

  // inner classes //

}