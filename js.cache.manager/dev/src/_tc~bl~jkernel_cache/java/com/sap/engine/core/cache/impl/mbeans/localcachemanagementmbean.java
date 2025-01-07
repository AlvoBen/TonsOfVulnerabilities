package com.sap.engine.core.cache.impl.mbeans;

import javax.management.openmbean.*;

public interface LocalCacheManagementMBean {
  /*
   The Name property defines the label by which the object is known. When subclassed, the Name property can be overridden to be a Key property.
   @return String
   */
  public String getName();

  /*
   The Caption property is a short textual description (one- line string) of the object.
   @return String
   */
  public String getCaption();

  /*
   The Description property provides a textual description of the object.
   @return String
   */
  public String getDescription();

  /*
   A user-friendly name for the object. This property allows each instance to define a user-friendly name IN ADDITION TO its key properties/identity data, and description information.
   Note that ManagedSystemElement's Name property is also defined as a user-friendly name. But, it is often subclassed to be a Key. It is not reasonable that the same property can convey both identity and a user friendly name, without inconsistencies. Where Name exists and is not a Key (such as for instances of LogicalDevice), the same information MAY be present in both the Name and ElementName properties.
   @return String
   */
  public String getElementName();

  /**
   * This method gets information about a specific region in the cache. The parameters, for which values are 
   * returned, are described in class <tt>CacheManagementNames</tt>. 
   */
  public CompositeData retrieveMonitoringData(String regionName);

  /**
   * Clear the specified region in the cache of the node.
   * VERY IMPORTANT: DUE TO CACHE SYNCHRONIZATION AMONG SERVER NODES IN CASE OF CLEARING OF A CACHE REGION,
   * CALLING THIS METHOD MAY CAUSE DELAY !!!!!!!
   */
  public void clearRegion(String regionName);

  /**
   * Change the size of the specified cache region.
   */
  public boolean resizeRegion(String regionName, int count1, int count2, int count3, int size1, int size2, int size3, boolean persist);

  /**
   * Makes an estimation of the size of the cache region.
   */
  public int calculateRegionSize(String regionName) throws IllegalAccessException;

  /**
   * Gets the names of all regions in the cache of the this node. 
   */
  public String[] retrieveRegionNames();
}
