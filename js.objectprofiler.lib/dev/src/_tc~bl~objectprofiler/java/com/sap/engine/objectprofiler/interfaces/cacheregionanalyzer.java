/*
 * Created on 2005.5.20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.objectprofiler.interfaces;

import java.rmi.RemoteException;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.graph.Graph;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CacheRegionAnalyzer {
  
  public static final String CACHE_REGION_ANALYZER_JNDI_NAME = "CacheRegionAnalyzer";

  public String[] listCacheRegionNames() throws RemoteException;
  public String[] listCacheGroups(String cacheRegionName) throws RemoteException;
  public String[] listObjectKeys(String cacheRegionName, String cacheGroup) throws RemoteException;
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey) throws RemoteException;
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level) throws RemoteException;
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level, ClassesFilter filter) throws RemoteException;
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level, ClassesFilter filter, boolean includeTransients, boolean onlyNonshareable) throws RemoteException;
  
}
