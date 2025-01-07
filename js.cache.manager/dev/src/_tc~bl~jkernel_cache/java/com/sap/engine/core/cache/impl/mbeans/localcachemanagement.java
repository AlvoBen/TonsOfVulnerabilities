package com.sap.engine.core.cache.impl.mbeans;

import java.util.*;
import javax.management.openmbean.*;

import com.sap.engine.core.cache.impl.CCDeployImpl;
import com.sap.engine.core.cache.pp.CacheManager;
import static com.sap.engine.frame.core.cache.CacheManagementNames.PARAMETERS;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class LocalCacheManagement implements LocalCacheManagementMBean {
  private String name = null;
  private String caption = null;
  private String description = null;
  private String elementName = null;
  private int clusterId;
  private CacheManager cacheManager;
  private static final Location LOCATION = Location.getLocation(LocalCacheManagement.class);

  public LocalCacheManagement(CacheManager cacheManager, int clusterId) {
    this.cacheManager = cacheManager;
    this.clusterId = clusterId;
  }

  /**
   * Makes an estimation of the size of the cache region.
   */
  public int calculateRegionSize(String regionName) throws IllegalAccessException {
    int regionSize = 0;
    try {
		regionSize = ((CCDeployImpl) cacheManager.getCacheContext().getDeploy()).calculateRegionSize(regionName);
	} catch (IllegalAccessException e) {
		SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Error in calculating region size", e);
		throw(e);
	}
    return regionSize;
  }

  /**
   * Clear the specified region in the cache of the node.
   * VERY IMPORTANT: DUE TO CACHE SYNCHRONIZATION AMONG SERVER NODES IN CASE OF CLEARING OF A CACHE REGION,
   * CALLING THIS METHOD MAY CAUSE DELAY !!!!!!!
   */
  public void clearRegion(String regionName) {
    ((CCDeployImpl) cacheManager.getCacheContext().getDeploy()).clearRegion(regionName);
  }

  public String getCaption() {
    if (caption == null) {
      caption = "An MBean for monitoring and modifying the cache";
    }
    return null;
  }

  public String getDescription() {
    if (description == null) {
      description = "This MBean provides functionality for retrieving information about a cache region," +
                    " resizing a cache region, clearing a cache region and getting an estimation of the size of a cache retion.";
    }
    return null;
  }

  public String getElementName() {
    if (elementName == null) {
      elementName = "LocalCacheManagement";
    }
    return null;
  }

  public String getName() {
    if (name == null) {
      name = "LocalCacheManagement";
    }

    return name;
  }

  /**
   * Change the size of the specified cache region.
   */
  public boolean resizeRegion(String regionName, int count1, int count2,
                              int count3, int size1, int size2, int size3, boolean persist) {
    return ((CCDeployImpl) cacheManager.getCacheContext().getDeploy()).resizeRegion(
            regionName, count1, count2, count3, size1, size2, size3, persist);
  }

  /**
   * This method gets information about a specific region in the cache. The parameters, for which values are 
   * returned, are described in class <tt>CacheManagementNames</tt>. 
   */
  public CompositeData retrieveMonitoringData(String regionName) {
    Map<String, String> regionData = ((CCDeployImpl) cacheManager.getCacheContext().getDeploy()).getMonitoringData(regionName);
    if(regionData == null){
    	return null;
    }
    regionData.put(PARAMETERS.SERVER_PROCESS_STR.toString(), String.valueOf(clusterId));

    // Construct the parts of the CompositeData object
    int paramsCount = regionData.size();
    String[] itemNames = new String[paramsCount];
    String[] itemDescriptions = new String[paramsCount];
    SimpleType[] itemTypes = new SimpleType[paramsCount];
    int i = 0;
    for (String name : regionData.keySet()) {
      itemNames[i] = name;
      itemDescriptions[i] = PARAMETERS.valueOf(name).getDescription();
      itemTypes[i] = SimpleType.STRING;
      i++;
    }

    CompositeType monitoringDataType = null;
    CompositeDataSupport monitoringData = null;

    try {
      monitoringDataType = new CompositeType("MonitoringData",
                                             "Monitoring data about a cache region", itemNames, itemDescriptions, itemTypes);
      monitoringData = new CompositeDataSupport(monitoringDataType, regionData);
    } catch (OpenDataException e) {
      // TODO Auto-generated catch block
      SimpleLogger.traceThrowable(Severity.DEBUG, LOCATION, "Error in constructing open data", e);
    }

    return monitoringData;
  }

  /**
   * Gets the names of all regions in the cache of the this node. 
   */
  public String[] retrieveRegionNames() {
    Iterator<String> regionNamesIterator = (Iterator<String>) ((CCDeployImpl) cacheManager.getCacheContext().getDeploy()).iterateRegions();
    ArrayList<String> regionNames = new ArrayList<String>();
    while (regionNamesIterator.hasNext()) {
      regionNames.add(regionNamesIterator.next());
    }

    return regionNames.toArray(new String[regionNames.size()]);
  }

}
