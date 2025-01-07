/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.lite;

import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

/**
 * This class is permanent cache organizer for Lean Client.
 * Used by P4LiteLauncher and DynamicClassLoader.
 * 
 * @author Tsvetko Trendafilov
 */
public class PermanentCacheOrganizer {
  
  public static final String TEMP_NAME = "CacheTemp";
  
  public File cacheFolder = null;
  private boolean activated = false;
  
  private HashMap<String, byte[]> cache = new HashMap<String, byte[]>();
  private CacheFileManupulator fileManipulator = null;

  /**
   * Default constructor is disabled. Constructor gets the name of
   * a folder, which will be used for permanent cache; <br>
   * NOTE: Permanent cache must be deleted every time, when files in server are changed;
   * e.g. after every update, deployment, ATS or CTS run. 
   * @param cacheFileName Valid folder. It can be empty or with classes from previous run.
   */
  public PermanentCacheOrganizer(String cacheFileName) {
    activated = isCorrect(cacheFileName);
    if (activated) {
      //read input from specified cache folder
      initialize(cacheFolder);
      fileManipulator = new CacheFileManupulator(cacheFolder);
      fileManipulator.start();
      if ( P4Logger.getLocation().beDebug() ){
        P4Logger.getLocation().debugT("PermanentCacheOrganizer(String)", "Permanent cache temporary folder is " + cacheFolder.getAbsolutePath());
      }
    } else {
      if ( P4Logger.getLocation().beError() ){
        P4Logger.trace(P4Logger.ERROR, "PermanentCacheOrganizer(String)", "Rermanent cache temporary folder could not be created. Check path and file system access \r\nPermanent cache will not store files permanently", "ASJ.rmip4.rt2034");
      }
    }
    
  }
  
  /**
   * Check if specified folder is existing and writable. <br>
   * Warning: if the folder does not exist, this method will try to create it.
   * @param cacheFileName The name of the cache folder.
   * @return true if name is valid and folder is writable; 
   *         false if name is invalid or the folder is not writable.<br>
   */
  private boolean isCorrect(String cacheFileName){
    cacheFileName = cacheFileName.replace('\\', File.separatorChar);
    cacheFileName = cacheFileName.replace('/', File.separatorChar);
    
    //If path is .\cache\CTS, but "cache" sub-folder does not exist, we have to create them.
    //But watch out for D:\cache\CTS or /usr/tmp/cache/CTS
    if (!cacheFileName.equals(".")){
      //Do not modify the original string anymore
      String cacheFolderName = new String(cacheFileName);
      String drive = "";
      String path = "";
      String token;

      //check for specified drive C: D: E: etc.
      if (cacheFolderName.contains(":")){
        drive = cacheFolderName.substring(0, cacheFolderName.indexOf(':') + 1);
        cacheFolderName = cacheFolderName.substring(cacheFolderName.indexOf(':') + 1, cacheFolderName.length());
      }
      
      //construct root folder path
      if (cacheFolderName.startsWith(File.separator)){
        path = new File(drive + File.separator).getAbsolutePath();
        path = path.substring(0, path.length()-1);
      } else {
        path = new File(".").getAbsolutePath();
        if (path.endsWith(".")){
          path = path.substring(0, path.length()-2);
        } else {
          path = path.substring(0, path.length()-1);
        }
      }
      //verify folders after root folder (".\" or "C:\" or "D:\" etc.)
      StringTokenizer tokenizer = new StringTokenizer(cacheFolderName, File.separator, false);
      while (tokenizer.hasMoreTokens()) { 
        token = tokenizer.nextToken();
        if (token.equals(".")){
          continue;
        }
        path = path + File.separatorChar + token;
        File pathParentFolder = new File(path);
        if (! pathParentFolder.exists()){
          boolean created = pathParentFolder.mkdir();
          if (!created && P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("PermanentCacheOrganizer.isCorrect()", "Cannot create not existing sub-folder " + pathParentFolder.getAbsolutePath());
          }
        }
      }
    }
    
    cacheFolder = new File(cacheFileName + File.separatorChar + TEMP_NAME);
    boolean created = true;
    if (! cacheFolder.exists()){
      //if folder does not exist, will override value of "created" 
      created = cacheFolder.mkdir();
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("PermanentCacheOrganizer.isCorrect()", "Created new temporary folder for cache: " + cacheFolder.getName() + " with success status: " + created);
      }
    }
    return created;
  }
  
  /**
   * This method store loaded from server process class in permanent cache.
   * @param name The name of the class including the package.
   * @param remoteBrokeID The broker ID of remote server where this class was taken from.
   * @param classData The class itself as special code 
   */
  public void record(String name, int remoteBrokeID, byte[] classData) {
    if (!activated || !allowStore(name)){
      return;
    }
    
    //Only if this file is not already stored
    if (cache.get(name) == null){ 
      cache.put(name, classData);
      fileManipulator.store(name, remoteBrokeID, classData);
    }
  }
  
  /**
   * Filter files that should not be stored never.
   * Additionally configurable with FileFilter.
   * @param name The name of the class that will be stored or loaded from the cache
   * @return true if this this file must be stored; 
   *         false when this file must not be stored in permanent cache
   */
  private boolean allowStore(String name){
    if (name.startsWith("iaik.security.ssl")){
      return false;
    }
    if (name.startsWith("com.sap.jvm.Capabilities")){
      return false;
    }
    if (name.startsWith("com.sap.jvm.impl.util.NativeCapabilities")){
      return false;
    }
    return true;
  }
  
  /**
   * This method has to load all classes that are stored in permanent cache as 
   * file names. It load only the names, without loading of their content.
   * @param cacheFolder2 The permanent cache folder.
   */
  private synchronized void initialize(File cacheFolder2) {
    if (!cacheFolder2.exists()) {
      return;
    }
    StringBuilder loadedClasses = null;
    if ( P4Logger.getLocation().beDebug() ){
      P4Logger.getLocation().debugT("PermanentCacheOrganizer.initialize(File)", "Begin initalization with classes stored in folder: " + cacheFolder2.getAbsolutePath());
      loadedClasses = new StringBuilder("Loaded from permanent cache classes statistic: \r\n");
    }
  
    //In cache folder there are folders for each broker
    File[] brokers = cacheFolder2.listFiles();
    for (File broker: brokers){
      String[] classes = broker.list();
      String key;
      int extPosition;
      for (String fileName: classes){
        //exclude ".class" extension before put in the cache 
        extPosition = fileName.lastIndexOf(CacheFileManupulator.EXTENSION);
        if (extPosition>0){
          key = fileName.substring(0, extPosition);
          cache.put(key, null);
          if ( P4Logger.getLocation().beDebug() ){
            loadedClasses.append(key + "\r\n");
          }
        }
      }
    }
    
    if ( P4Logger.getLocation().beDebug() ){
      P4Logger.getLocation().debugT("PermanentCacheOrganizer.initialize(File)", loadedClasses.toString());
    }
  }

  /**
   * This method checks the permanent cache for specified class and return the class if 
   * it is available in permanent cache or null, if it is not found. 
   * @param name The name of the class including the package
   * @param remoteBrokeID The broker ID of remote server where this class was taken from.
   * @return byte array with the class if it is found in permanent cache or null if it is nor found.
   */
  public byte[] readFromCache(String name, int remoteBrokeID) {
    if (!activated || !allowStore(name)){
      return null;
    }
    if (!cache.containsKey(name)){
      return null;
    }
    //we have cached such name, but we still do not know if it is loaded or not
    byte[] classData = cache.get(name); 
    if (classData != null){
      return classData;
    }
    //The data was not loaded yet; load it now
    classData = fileManipulator.read(name, remoteBrokeID);
    cache.put(name, classData);
    return classData;
  }

  /**
   * This will make file manipulator thread to exit
   */
  public void stop() {
    if (fileManipulator != null){
      fileManipulator.interrupt();
    }
  }
}