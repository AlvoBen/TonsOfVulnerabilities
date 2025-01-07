package com.sap.engine.core.cache.impl.mbeans;

import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

public interface GlobalCacheManagementMBean {
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
   * Clears the region with the specified name from the cache of all nodes in the cluster. For this purpose it
   * calls the respective methods of the local mbeans from all nodes in the cluster. The method returns <tt>null</tt>
   * if there are no local mbeans. The method returns an array of Strings with the cluster id's of all nodes,
   * on which the clearing failed. The method returns an empty String array if the clearing of the region was 
   * unsuccessful on all nodes. 
   */
  public String[] clearRegion(String regionName);

  /**
   * Resizes the regions with the specified name on all cluster nodes. Returns <tt>null</tt> if there are no local mbeans.
   * Returns a list with the nodes, on which the region is not persisted. These are actually the nodes, on which the region 
   * does not exist. It also tries to persist the resize in the database. This is done with a boolean parameter of the resize 
   * method of the local mBean. The persisting of the resize is done once. If on the first successful resize the resize is 
   * not persisted successfully, the clear process is stopped. Otherwise a resize on all other nodes is attempted, without 
   * trying to persist it. If on a node the resize is not successful, the node is included in the list of failed nodes.  
   */
  public CompositeData resizeRegion(String regionName, int count1, int count2, int count3, int size1, int size2, int size3) throws MBeanException, ReflectionException;
}
