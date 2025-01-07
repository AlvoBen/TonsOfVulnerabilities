package com.sap.engine.tools.offlinedeploy.rdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * Holds common data objact when autocommit mode is off.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
class CommonData {

  /**
   * holds undeploy mapping
   */
  Properties undeployMapping = null;

  /**
   * holds template mapping
   */
  Properties templateMapping = null;

  /**
   * holds component repository as object
   */
  SCRepository serviceRepository = null;

  /**
   * holds native map
   */
  HashMap<String, String[]> nativeDescriptorMap = null;

  /**
   * holds open zip archives
   */
  ArrayList<ZipFile> zipFiles = null;

}