/**
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

/**
 * This class is used for asynchronous file writing in LeanClient permanent cache.
 * And also provide reading from permanent cache.
 *   
 * @author I041949
 */
public class CacheFileManupulator extends Thread {
  
  public File cacheFolder = null;
  private HashMap<String, byte[]> queue = new HashMap<String, byte[]>(3);
  private HashMap<String, Boolean> checkedBrokers = new HashMap<String, Boolean>(1);
  
  public static final String TEMP_NAME = "CacheTemp";
  public static final String NAME_SEPARATOR = "_#_#_";
  /**
   * The extension of the cache files. 
   */
  public static final String EXTENSION = ".class"; 

  /**
   *  Constructor with File for the permanent cache
   */
  public CacheFileManupulator(File cacheFolder) {
    this.cacheFolder = cacheFolder;
  }

  /**
   * This method store given class as file in specified device.
   * @param cacheFolder Path to cache folder as String. Used to generate file name of specified entry.
   * @param className The name of the class including Object Broker ID.
   * @param classContent The class itself as special code 
   */
  private void addNewClass(String className, String brokerID, byte[] classContent){
    boolean brokerExists = validate(brokerID);
    if (!brokerExists){
      if ( P4Logger.getLocation().beInfo() ){
        P4Logger.getLocation().infoT("CacheFileManupulator.addNewClass(String)", "Cannot found and cannot create folder for server " + brokerID);
      }
      return;
    }
    File f = new File(cacheFolder.getAbsolutePath() + File.separatorChar + brokerID + File.separatorChar + className + EXTENSION);
    if (f.exists()){
      if ( P4Logger.getLocation().beDebug() ){
        P4Logger.getLocation().debugT("CacheFileManupulator.addNewClass(String)", "Class " + className + " already exists in permanent cache folder");
      }
      return;
    }
    
    RandomAccessFile file = null;
    try {
      file = new RandomAccessFile(f, "rw");
      file.write(classContent);
      file.close();
    } catch (FileNotFoundException fnf) {//$JL-EX
      //We have assured that the file already exists
      if ( P4Logger.getLocation().beWarning() ){
        P4Logger.trace(P4Logger.WARNING, "CacheFileManupulator.addNewClass(String)", "Permanent cache entry {0} was not found for initialization with content. Exception: {1}", "ASJ.rmip4.rt2029", new Object []{f.getName(), P4Logger.exceptionTrace(fnf)});
      }
      return;
    } catch (IOException e) {
      if ( P4Logger.getLocation().beInfo() ){
        P4Logger.getLocation().infoT("CacheFileManupulator.addNewClass(String)", "Permanent cache entry " + f.getName() + " could not be initialized with content: " + P4Logger.exceptionTrace(e));
      }
      return;
    }
  }

  /**
   * Validates existing /possibility to create/ sub-folder for current broker 
   * @param brokerID
   * @return
   */
  private boolean validate(String brokerID) {
    Boolean validated = checkedBrokers.get(brokerID);
    if (validated != null){
      return true;
    }
    File broker = new File(cacheFolder.getAbsolutePath() + File.separatorChar + brokerID);
    if (broker.exists()){
      checkedBrokers.put(brokerID, true);
      return true;
    }
    validated = broker.mkdir();
    if (validated){
      checkedBrokers.put(brokerID, true);
    }
    return validated;
  }

  /**
   * This method should exit as fast as possible.
   * @param name The name of stored class
   * @param remoteBrokeID The name of folder for Remote Broker 
   * @param classData The class as it should be stored
   */
  public void store(String name, int remoteBrokerID, byte[] classData) {
    String key = name + NAME_SEPARATOR + remoteBrokerID;
    queue.put(key, classData);
    synchronized(this){
      notify();
    }
  }
  
  /**
   * This method have to read the class from file system, if it is already stored;
   * It have to return it from queue, if the class was not stored to file system yet.
   * @param name The name of the class
   * @param remoteBrokerID P4 specific information from remote server
   * @return
   */
  public byte[] read(String name, int remoteBrokerID){
    //Check if this class is not in the queue for store
    byte[] queuedForStore = checkQueue(name,remoteBrokerID);
    if (queuedForStore != null){
      return queuedForStore;
    }
    //Check if the file exists in permanent cache
    File f = new File(cacheFolder.getAbsolutePath() + File.separatorChar + remoteBrokerID + File.separatorChar + name + EXTENSION);
    if (!f.exists()){
      return null;
    }
    //Read the existing file from permanent cache
    try {
      FileInputStream fis = new FileInputStream(f);
      int size = -1;
      int read = 0;
      size = fis.available();
      byte[] array = new byte[size];
      while (read != size){
        size = fis.available();
        read = fis.read(array, read, array.length);
      }
      return array; 
    } catch (FileNotFoundException fnf) {
      if ( P4Logger.getLocation().beDebug() ){
        P4Logger.getLocation().debugT("CacheFileManupulator.read()", "FileNotFoundException occurred: " + P4Logger.exceptionTrace(fnf));
      }
    }catch (IOException ioe) {
      if ( P4Logger.getLocation().beWarning() ){
        P4Logger.trace(P4Logger.WARNING, "CacheFileManupulator.read(File)", "IOException occurred while reading from permanent cache: {0}" , "ASJ.rmip4.rt2030", new Object []{f.getName(), P4Logger.exceptionTrace(ioe)});
      }
    }
    return null;
  }

  /**
   * Checks if the class is not in the queue waiting to be stored in the HDD.
   * Used in reading for not written files, that are still in the queue.
   * @param name The name of the class
   * @param remoteBrokerID The remote object broker id
   * @return null if the class is not in the queue; 
   *         the class as byte array if it is in the queue. 
   */
  private byte[] checkQueue(String name, int remoteBrokerID) {
    String key = name + NAME_SEPARATOR + remoteBrokerID;
    return queue.get(key);
  }
  
  public void run(){
    boolean isRunning = true;
    while (isRunning){
      synchronized (this){
        if (queue.keySet().isEmpty()){
          try {
            wait();
          } catch (InterruptedException e) {
            isRunning = false;
            if ( P4Logger.getLocation().beDebug() ){
              P4Logger.getLocation().debugT("CacheFileManupulator.run()", "Permanent Cache File Manipulator is exiting...");
            }
          }
        }
      }
      //Escape ConcurrentModificationException from removing key, that is held by iterator in generic loop 
      Set<String> mapKeys = queue.keySet();
      String[] keys = new String[mapKeys.size()];
      int i=0;
      for (String k : mapKeys){
        keys[i++] = k;
      }
      
      //Generic loop for writing queued classes
      for (String key : keys){
        String className = key.substring(0, key.indexOf(NAME_SEPARATOR));
        String id = key.substring(key.indexOf(NAME_SEPARATOR) + NAME_SEPARATOR.length(), key.length());
        byte[] classContent = queue.get(key);
        //System.out.println("Writing: name=" + className + "  id=" + id + "  has content: " + (classContent != null) );
        if (classContent != null){
          addNewClass(className, id, classContent);
        }
        queue.remove(key);
      }
    }
  }
  
}