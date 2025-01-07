package com.sap.engine.objectprofiler.view.utils;

import com.sap.engine.objectprofiler.interfaces.CacheRegionAnalyzer;
import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.graph.Graph;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-4-28
 * Time: 17:41:46
 */
public class CacheBrowser implements CacheRegionAnalyzer {
  private CacheRegionAnalyzer analyzer = null;

  private String[] connectionProps = null;

  public CacheBrowser(String[] args) throws NamingException {
    setConnectionProps(args);
    connect();
  }

  public void setConnectionProps(String[] props) {
    connectionProps = props;
  }

  public void connect() throws NamingException {
    analyzer = null;
    if (connectionProps != null && connectionProps.length > 0) {
      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
      if (connectionProps.length == 4) {
        env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1]);
        env.put(Context.SECURITY_PRINCIPAL, connectionProps[2]);
        env.put(Context.SECURITY_CREDENTIALS, connectionProps[3]);
      } else if (connectionProps.length == 5) {
        if (connectionProps[2] == null || connectionProps[2].trim().equals("")) {
          env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1]);
        } else {
          env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1] + "#" + connectionProps[2]);
        }
        env.put(Context.SECURITY_PRINCIPAL, connectionProps[3]);
        env.put(Context.SECURITY_CREDENTIALS, connectionProps[4]);
      }

      InitialContext ctx = new InitialContext(env);
      Object obj = ctx.lookup(CACHE_REGION_ANALYZER_JNDI_NAME);
      //System.out.println(" CACHE REMOTE = "+obj.getClass());
      analyzer = (CacheRegionAnalyzer)ctx.lookup(CACHE_REGION_ANALYZER_JNDI_NAME);
    } else {
      throw new NamingException("Please, correct your connection settings!");
    }


  }

  public String[] listCacheRegionNames() throws RemoteException {
    String[] res = analyzer.listCacheRegionNames();

    return res;
  }

  public String[] listCacheGroups(String regionName) throws RemoteException {
    String[] res = analyzer.listCacheGroups(regionName);

    return res;
  }

  public String[] listObjectKeys(String regionName, String groupName) throws RemoteException {
    String[] res = analyzer.listObjectKeys(regionName, groupName);

    return res;
  }

  public Graph getCachedObjectGraph(String regionName, String groupName, String key) throws RemoteException {
    Graph graph = analyzer.getCachedObjectGraph(regionName, groupName, key);

    return graph;
  }

  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level) throws RemoteException {
    Graph graph = analyzer.getCachedObjectGraph(cacheRegionName, cacheGroup, objectKey, level);
    
    return graph;
  }
  
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level, ClassesFilter filter) throws RemoteException {
    Graph graph = analyzer.getCachedObjectGraph(cacheRegionName, cacheGroup, objectKey, level, filter);

    return graph;
  }
  
  public Graph getCachedObjectGraph(String cacheRegionName, String cacheGroup, String objectKey, int level, ClassesFilter filter, boolean includeTransients, boolean onlyNonshareable) throws RemoteException {
  	Graph graph = analyzer.getCachedObjectGraph(cacheRegionName, cacheGroup, objectKey, level, filter, includeTransients, onlyNonshareable);

    return graph;
  }
  
  
  public DefaultTreeModel buildTreeModel() throws Exception {
    CacheInfo rootInfo = CacheInfo.buildRoot();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootInfo);

    DefaultTreeModel treeModel = new DefaultTreeModel(root);

    String[] cacheRegions = listCacheRegionNames();
    if (cacheRegions != null) {
      for (int i=0;i<cacheRegions.length;i++) {
        //System.out.println("REGION="+cacheRegions[i]);
        CacheInfo regionInfo = new CacheInfo(cacheRegions[i], CacheInfo.TYPE_CACHE_REGION);
        DefaultMutableTreeNode region = new DefaultMutableTreeNode(regionInfo);
        root.add(region);

        String[] groups = listCacheGroups(cacheRegions[i]);
        if (groups != null) {
          for (int j=0;j<groups.length;j++) {
            if (groups[j] == null) {
              //System.out.println("  GROUP is NULL!");
            } else {
              //System.out.println("  GROUP="+groups[j]);
            }
            CacheInfo groupInfo = new CacheInfo(groups[j], CacheInfo.TYPE_CACHE_GROUP);
            DefaultMutableTreeNode group = new DefaultMutableTreeNode(groupInfo);
            region.add(group);

            String[] keys = listObjectKeys(cacheRegions[i], groups[j]);
            if (keys != null) {
              for (int k=0;k<keys.length;k++) {
                if (groups[j] == null) {
                  //System.out.println("     KEY is NULL!");
                } else {
                 // System.out.println("     KEY="+keys[k]);
                }
                CacheInfo keyInfo = new CacheInfo(cacheRegions[i], groups[j], keys[k], CacheInfo.TYPE_CACHE_NAME);
                group.add(new DefaultMutableTreeNode(keyInfo));
              }
            }
          }
        }
      }
    }

    return treeModel;
  }
}

