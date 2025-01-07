/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */


package com.sap.engine.services.rmi_p4.classload;

import com.sap.engine.services.rmi_p4.StubImpl;

import java.util.Hashtable;

/**
 * The base class for P4 RemoteClassLoading
 * and for its feature - caching.
 */

public class ClassLoaderContext {

  public static final char[] begin_div = {'#', 'p', '4', '#'};  // #p4#
  public static final char[] end_div = {'#', 'e', 'n', 'd', '#'};  //#end#

  private static Hashtable loaders = new Hashtable(5);
  public static Hashtable classIndex = new Hashtable(2);
  
  static int position = 0;
  static int resourcePos = 0;

  public static boolean configuredFileCache = false;

  public static Class loadClass(String className, StubImpl stub) throws ClassNotFoundException {
    ClassLoader loader = lookupLoader(getContextLoader(), stub.getObjectInfo().ownerId);
    return loader.loadClass(className);
  }


  private static ClassLoader getContextLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  /**
   * This method returns DynamicClassloader with specified parent loader 
   * to specified destination with P4ObjectBroker ID, specifying one  
   * cluster.
   * It can return already created loader with the same destination and
   * parent loader. 
   * @param parent     The parent loader 
   * @param brokerId   OwnerId from the stub's RemoteObjectInfo; 
   *                   it is identical for a cluster and specify stub's destination.
   *                   It identifies P4ObjectBroker that owns current stub's implementation.
   * @return return    DynamicClassLoader to destination cluster
   */
  private static DynamicClassLoader lookupLoader(ClassLoader parent, int brokerId) {
    //Forbid multiple DynamicLoaders in the class-loader's tree
    if (parent instanceof DynamicClassLoader) {
      return (DynamicClassLoader)parent;
    }
    /* For one cluster it creates one unique loaderKey 
     * and should create only one DynamincLoader for one and the same 
     * parent class-loader.
     */
    ClassLoaderKey key = new ClassLoaderKey(parent, brokerId);
    synchronized (ClassLoaderContext.class) {
      DynamicClassLoader loader = (DynamicClassLoader) loaders.get(key); // if created before
      if (loader == null) {
        loader = new DynamicClassLoader(parent);  // create for this cluster DynamicLoader
        loaders.put(key, loader);
      }
      return loader;
    }
  }

  public static ClassLoader getDynamicLoader(int brokerId) {
    return lookupLoader(getContextLoader(), brokerId);
  }

  public static synchronized void freeClassLoaderContext() {
    loaders.clear();
  }

}
