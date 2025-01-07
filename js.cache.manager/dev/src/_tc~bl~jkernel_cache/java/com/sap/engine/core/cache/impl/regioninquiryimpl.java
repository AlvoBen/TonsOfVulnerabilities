/*
 * Created on 2005.5.25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sap.engine.core.Names;
import com.sap.engine.cache.core.impl.CacheRegionImpl;
import com.sap.engine.cache.core.impl.InternalRegionFactory;
import com.sap.engine.frame.core.cache.CacheContextException;
import com.sap.engine.frame.core.cache.CacheRegionInquiry;
import com.sap.tc.logging.Location;
import com.sap.util.cache.CacheGroup;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RegionInquiryImpl implements CacheRegionInquiry {
  
  private InternalRegionFactory regionFactory = null;
  private static final Location LOCATION = Location.getLocation(RegionInquiryImpl.class.getName(), Names.KERNEL_DC_NAME, Names.CACHE_MANAGER_CSN_COMPONENT);
  
  public RegionInquiryImpl() {
    regionFactory = (InternalRegionFactory) InternalRegionFactory.getInstance();
  }

  public String[] listCacheRegionNames() {
    String[] result = null;
    Iterator regionNames = regionFactory.iterateRegions();
    Vector vectorForm = new Vector();
    while (regionNames.hasNext()) {
      vectorForm.add(regionNames.next());
    }
    result = new String[vectorForm.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = (String) vectorForm.get(i);
    }
    return result;
  }

  public String[] listCacheGroups(String cacheRegionName) throws CacheContextException {
    String[] result = null;
    CacheRegion region = null;
    try {
      region = regionFactory.getCacheRegion(cacheRegionName);
    } catch (NullPointerException e) {
      region = null;
    }
    if (region != null) { 
      Iterator groupNames = region.getCacheGroupNames().iterator();
      Vector vectorForm = new Vector();
      while (groupNames.hasNext()) {
        vectorForm.add(groupNames.next());
      }
      result = new String[vectorForm.size()];
      for (int i = 0; i < result.length; i++) {
        result[i] = (String) vectorForm.get(i);
      }
      return result;
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_REGION, new Object[] {cacheRegionName});
    }
  }

  public String[] listObjectKeys(String cacheRegionName, String cacheGroup) throws CacheContextException {
    String[] result = null;
    
    CacheRegion region = regionFactory.getCacheRegion(cacheRegionName);
    if (region != null) {
      Iterator objectKeys = null;
      if (cacheGroup == null) {
        objectKeys = region.getCacheFacade().keySet().iterator();
      } else {
        CacheGroup group = region.getCacheGroup(cacheGroup);
        if (group != null) {
          objectKeys = group.keySet().iterator();
        } else {
          throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_GROUP, new Object[] {cacheGroup, cacheRegionName});
        }
      }
      Vector vectorForm = new Vector();
      while (objectKeys.hasNext()) {
        vectorForm.add(objectKeys.next().toString());
      }
      result = new String[vectorForm.size()];
      for (int i = 0; i < result.length; i++) {
        result[i] = (String) vectorForm.get(i);
      }
      return result;
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_REGION, new Object[] {cacheRegionName});
    }
  }

  public Map getAdditionalObjectProperties(String cacheRegionName, String cacheGroup, String objectKey) throws CacheContextException {
    Map result = null;
    CacheRegion region = regionFactory.getCacheRegion(cacheRegionName);
    if (region != null) {
      CacheGroup group = null;
      if (cacheGroup == null) {
        group = region.getCacheFacade();
      } else {
        group = region.getCacheGroup(cacheGroup);
        if (group == null) {
          throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_GROUP, new Object[] {cacheGroup, cacheRegionName});
        }
      }
      Object cachedObject = group.get(objectKey);
      if (cachedObject != null) {
        result = new HashMap();
        StoragePlugin storagePlugin = ((CacheRegionImpl)region).getRegionConfiguration().getStoragePlugin(); // hack
        result.put(OP_OBJECT, cachedObject);
        result.put(OP_ATTRIBUTES, group.getAttributes(objectKey));
        getObjectVersionCount(cacheRegionName, objectKey, result);
        return result;
      } else {
        throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_KEY, new Object[] {objectKey, cacheGroup, cacheRegionName});
      }
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_REGION, new Object[] {cacheRegionName});
    }
  }
  
  void getObjectVersionCount(String cacheRegionName, String objectKey, Map properties) {
//    cacheRegionName = cacheRegionName.replace('/', '~');
//    objectKey = objectKey.replace('/', '~');
//    String domainName = "/vmc/cache/region/" + cacheRegionName + "/StorageData/Data";
//    SharingDomain dataDomain = SharingDomain.getSharingDomainByFullName(domainName);
//    if (dataDomain != null) {
//      SharedClosure closure = dataDomain.getMappableSharedClosure(objectKey);
//      if (closure != null) {
//        properties.put(OP_SIZE, Integer.toString(closure.getSize()));
//        SharedClosureInfo[] closureInfo = SharedClosureTableInfo.get().getSharedClosureInfo();
//        for (int i = 0; i < closureInfo.length; i++) {
//          if (domainName.equals(closureInfo[i].getDomainName())) {
//            if (objectKey.equals(closureInfo[i].getName())) {
//              SharedClosureInfo info = closureInfo[i];
//              SharedBlockInfo[] sharedBlockInfo = info.getSharedBlocks();
//              int totalSize = 0;
//              properties.put(OP_VERSION_COUNT, Integer.toString(sharedBlockInfo.length));
//              for (int j = 0; j < sharedBlockInfo.length; j++) {
//                totalSize += sharedBlockInfo[j].getSize();
//              }
//              properties.put(OP_TOTAL_SIZE, Integer.toString(totalSize));
//            }
//          }
//        }
//      }
//    }
  }
  
  public Map getAdditionalObjectProperties(String cacheRegionName, String cacheGroup, Set objectKeys) throws CacheContextException {
    Map result = null;
    CacheRegion region = regionFactory.getCacheRegion(cacheRegionName);
    if (region != null) {
      CacheGroup group = null;
      if (cacheGroup == null) {
        group = region.getCacheFacade();
      } else {
        group = region.getCacheGroup(cacheGroup);
        if (group == null) {
          throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_GROUP, new Object[] {cacheGroup, cacheRegionName});
        }
      }
      result = new HashMap();
      getObjectVersionCount(cacheRegionName, objectKeys, result);
      return result;
    } else {
      throw new CacheContextException(LOCATION, CacheContextException.NO_SUCH_REGION, new Object[] {cacheRegionName});
    }
  }
  
  void getObjectVersionCount(String cacheRegionName, Set objectKeys, Map properties) {
//    cacheRegionName = cacheRegionName.replace('/', '~');
//    String domainName = "/vmc/cache/region/" + cacheRegionName + "/StorageData/Data";
//    SharingDomain dataDomain = SharingDomain.getSharingDomainByFullName(domainName);
//    if (dataDomain != null) {
//      Iterator iter = objectKeys.iterator();
//      HashSet changedKeys = new HashSet();
//      HashMap originals = new HashMap();
//      while (iter.hasNext()) {
//        String objectKey = (String) iter.next();
//        originals.put(objectKey.replace('/', '~'), objectKey);
//        properties.put(objectKey, new HashMap());
//        objectKey = objectKey.replace('/', '~');
//        SharedClosure closure = dataDomain.getMappableSharedClosure(objectKey);
//        if (closure != null) {
//          Map perObject = (Map) properties.get(originals.get(objectKey));
//          perObject.put(OP_SIZE, Integer.toString(closure.getSize()));
//        }
//        changedKeys.add(objectKey);
//      }
//      SharedClosureInfo[] closureInfo = SharedClosureTableInfo.get().getSharedClosureInfo();
//
//      for (int i = 0; i < closureInfo.length; i++) {
//        if (domainName.equals(closureInfo[i].getDomainName())) {
//          String closureName = closureInfo[i].getName();
//          if (changedKeys.contains(closureName)) {
//            Map perObject = (Map) properties.get(originals.get(closureName));
//            SharedClosureInfo info = closureInfo[i];
//            SharedBlockInfo[] sharedBlockInfo = info.getSharedBlocks();
//            int totalSize = 0;
//            perObject.put(OP_VERSION_COUNT, Integer.toString(sharedBlockInfo.length));
//            for (int j = 0; j < sharedBlockInfo.length; j++) {
//              totalSize += sharedBlockInfo[j].getSize();
//            }
//            perObject.put(OP_TOTAL_SIZE, Integer.toString(totalSize));
//          }
//        }
//      }
//    }
  }  
}


