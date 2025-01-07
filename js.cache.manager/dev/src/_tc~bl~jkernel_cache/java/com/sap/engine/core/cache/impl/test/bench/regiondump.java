/*
 * Created on 2005.2.16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.core.cache.impl.CacheManagerImpl;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RegionDump {
  
  private CacheRegion region = null;

  public RegionDump(CacheRegion region) {
    this.region = region;
  }
  
  public void dump(String fileName) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(new FileOutputStream(fileName));
      writer.println("<REGION DUMP>");
      writer.println("<NAME> " + region.getRegionConfigurationInfo().getName());
      writer.println("<DATE> [" + new Date().toString() + "]");
      writer.flush();
      synchronized (region) { // sync at least locally
        CacheFacade facade = region.getCacheFacade();
        // group info
        Set groupNames = region.getCacheGroupNames();
        if (!groupNames.isEmpty()) { // at least one named group
          Iterator groupNamesIterator = groupNames.iterator();
          while (groupNamesIterator.hasNext()) {
            String currentGroupName = (String) groupNamesIterator.next();
            CacheGroup currentGroup = region.getCacheGroup(currentGroupName);
            StringBuffer sb = new StringBuffer();
            sb.append("\n<GROUP> ");
            sb.append(currentGroupName);
            sb.append("\n============================");
            Set childrenSet = currentGroup.getChildren();
            if (childrenSet != null) {
              if (!childrenSet.isEmpty()) { // at least one child
                sb.append("\n  <CHILDREN>");
                sb.append("\n  ----------");
                Iterator childrenIterator = childrenSet.iterator();
                while (childrenIterator.hasNext()) {
                  String childName = (String) childrenIterator.next();
                  sb.append("\n    ");
                  sb.append(childName);
                }
              }
            }
            Set keySet = currentGroup.keySet();
            if (keySet != null) {
              if (!keySet.isEmpty()) { // at least one key
                sb.append("\n  <KEYS>");
                sb.append("\n  ------");
                Iterator keyIterator = keySet.iterator();
                while (keyIterator.hasNext()) {
                  String keyName = (String) keyIterator.next();
                  sb.append("\n    ");
                  sb.append(keyName);
                } 
              }
            }
            writer.print(sb.toString());
            writer.flush();
          }
        } // group info
        StoragePlugin storagePlugin = ((CacheRegionImpl)region).getRegionConfiguration().getStoragePlugin(); // hack
        Set keySet = storagePlugin.keySet();
        if (!keySet.isEmpty()) { // at least one key
          StringBuffer sb = new StringBuffer();
          sb.append("\n<FACADE>");
          sb.append("\n============================");
          writer.print(sb.toString());
          writer.flush();
          Iterator keyIterator = keySet.iterator();
          while (keyIterator.hasNext()) {
            String keyName = (String) keyIterator.next();
            sb = new StringBuffer();
            
            sb.append("\n----------------------------");
            sb.append("\n<KEY>             : ");
            sb.append(keyName);
            
            sb.append("\n<OBJECT>          : ");
            String toString = "" + storagePlugin.get(keyName, true);
            sb.append(toString);
            
            sb.append("\n<CLASS>           : ");
            toString = "" + storagePlugin.get(keyName, true).getClass().getName();
            sb.append(toString);
            
            sb.append("\n<ATTRIBUTES>      : ");
            toString = "" + storagePlugin.getAttributes(keyName, false);
            sb.append(toString);
            
            sb.append("\n<SYS. ATTRIBUTES> : ");
            toString = "" + storagePlugin.getAttributes(keyName, true);
            sb.append(toString);

            sb.append("\n<SIZE>            : ");
            toString = "" + storagePlugin.getSize(keyName);
            sb.append(toString);
            
            sb.append("\n<ATTRIBUTES SIZE> : ");
            toString = "" + storagePlugin.getAttributesSize(keyName);
            sb.append(toString);

            writer.print(sb.toString());
            writer.flush();
          } 
        }
      }
      writer.close();
    } catch (FileNotFoundException e) {
      CacheManagerImpl.traceT(e);
    } catch (CacheException e) {
      CacheManagerImpl.traceT(e);
    }
  }

}
