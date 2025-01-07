package com.sap.engine.core.cache.impl.mbeans;

import java.lang.management.*;
import java.util.*;
import javax.management.*;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

import com.sap.engine.frame.core.cache.CacheManagementNames;
import com.sap.engine.frame.core.cache.CacheManagementNames.PARAMETERS;

import static com.sap.engine.frame.core.cache.CacheManagementNames.OPERATIONS;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class GlobalCacheManagement implements GlobalCacheManagementMBean {
  private String name = null;
  private String caption = null;
  private String description = null;
  private String elementName = null;
  private static final Location LOCATION = Location.getLocation(GlobalCacheManagement.class);
  private MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
  private static final String CLUSTER_NODE_STR = "SAP_J2EEClusterNode";

  /**
   * Clears the region with the specified name from the cache of all nodes in the cluster. For this purpose it
   * calls the respective methods of the local mbeans from all nodes in the cluster. The method returns <tt>null</tt>
   * if there are no local mbeans. The method returns an array of Strings with the cluster id's of all nodes,
   * on which the clearing failed. The method returns an empty String array if the clearing of the region was 
   * unsuccessful on all nodes. 
   */
  public String[] clearRegion(String regionName) {
    final String method = "public void clearRegion(String regionName)";
    Set<ObjectName> mbeanNames = getMBeanNames(CacheManagementNames.LOCAL_CACHE_MANAGEMENT_MBEAN_NAME_PATTERN);
    if (mbeanNames == null || mbeanNames.size() == 0) {
      return null;
    }
    
    ArrayList<String> nodesWithFailedClear = new ArrayList<String>();
    
    for (ObjectName name : mbeanNames) {
    	try {
    		mbeanServer.invoke(name, OPERATIONS.CLEAR_REGION.getName(), new Object[]{regionName}, new String[]{String.class.getName()});
    	} catch (InstanceNotFoundException e) {
    		SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "LocalCacheManagementMBean for node " + name.getKeyProperty(CLUSTER_NODE_STR) + " not present on the MBean server", e);
    		nodesWithFailedClear.add(name.getKeyProperty(CLUSTER_NODE_STR));
    	} catch (MBeanException e) {
    		SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + name.getKeyProperty("SAP_J2EEClusterNode"), e);
    		nodesWithFailedClear.add(name.getKeyProperty(CLUSTER_NODE_STR));
    	} catch (RuntimeMBeanException e){
    		SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + name.getKeyProperty("SAP_J2EEClusterNode"), e);
    		nodesWithFailedClear.add(name.getKeyProperty(CLUSTER_NODE_STR));
    	} catch (ReflectionException e) {
    		SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + name.getKeyProperty("SAP_J2EEClusterNode"), e);
    		nodesWithFailedClear.add(name.getKeyProperty(CLUSTER_NODE_STR));
    	}
    }
    
    return nodesWithFailedClear.toArray(new String[nodesWithFailedClear.size()]);
  }


  public String getCaption() {
    if (caption == null) {
      caption = "An MBean for managing the cache in the cluster";
    }
    return null;
  }


  public String getDescription() {
    if (description == null) {
      description = "This MBean provides functionality for " +
                    " resizing a cache region and clearing a cache region.";
    }
    return null;
  }


  public String getElementName() {
    if (elementName == null) {
      elementName = "GlobalCacheManagement";
    }
    return null;
  }


  public String getName() {
    if (name == null) {
      name = "GlobalCacheManagement";
    }

    return name;
  }

  /**
   * Resizes the regions with the specified name on all cluster nodes. Returns <tt>null</tt> if there are no local mbeans.
   * Returns a <tt>CompositeData</tt> object, containing two lists with node ids. 
   * The one list contains the nodes, on which the region is not resized. These are actually the nodes, on which the region
   * does not exist. It also tries to persist the resize in the database. This is done with a boolean parameter of the 
   * resize method of the local mBean. The persisting of the resize is done once. If on the first successful resize the resize is  
   * not persisted successfully, the resize process is stopped. Otherwise a resize on all other nodes is attempted, without 
   * trying to persist it. If on a node the resize is not successful, the node is included in the list of failed nodes.  
   * The other list contains node ids of the nodes, on which the resize is performed, but with automatic adjustment of the 
   * thresholds, passed as parameters. If such automatic adjustment is done, the actual threshold values after the resize are
   * different from the ones, given by the user. For more on the automatic adjustment of the thresholds see <tt>CCDeployImpl.resizeRegions</tt>. 
   */
  public CompositeData resizeRegion(String regionName, int count1, int count2,
		  int count3, int size1, int size2, int size3) {
	  final String method = "public boolean resizeRegion(String regionName, int count1, int count2, " +
	  "int count3, int size1, int size2, int size3)";
	  Set<ObjectName> mbeanNames = getMBeanNames(CacheManagementNames.LOCAL_CACHE_MANAGEMENT_MBEAN_NAME_PATTERN);
	  if (mbeanNames == null || mbeanNames.size() == 0) {
		  return null;
	  }

	  ObjectName[] names = new ObjectName[mbeanNames.size()];
	  mbeanNames.toArray(names);
	  ArrayList<String> nodesWithFailedResize = new ArrayList<String>();
	  ArrayList<String> nodesWithAutomaticallyAdjustedThresholds = new ArrayList<String>();
	  // TODO: Currently this variable stays always false. When we implement persistent cache regions it will change value.
	  boolean shouldPersist = false;
	  String intType = int.class.getName();
	  String stringType = String.class.getName();
	  String booleanType = boolean.class.getName();
	  CompositeData regionData = null;
	  boolean isRegionFound = false;
	  int nameIdx = 0;
	  boolean areThresholdsAdjusted = false;
	  
	  // Currently this variable does not change value; when persistence is implemented it will be used in the Exception handling
	  // of the first call of the MBean.
	  boolean isFirstTimeFail = false;
	  
	  while(!isRegionFound){
		  try{
			  regionData = (CompositeData)mbeanServer.invoke(names[nameIdx], OPERATIONS.RETRIVE_MONITORING_DATA.getName(), 
					  new Object[]{regionName}, new String[]{String.class.getName()});

			  isRegionFound = true;
			  
			  if(regionData.get(PARAMETERS.PERSISTENT_STR.toString()).equals("true")){
				  shouldPersist = true;
			  }

			  areThresholdsAdjusted = !(Boolean)mbeanServer.invoke(names[nameIdx], OPERATIONS.RESIZE_REGION.getName(), new Object[]{regionName, count1,
					  count2, count3,
					  size1, size2,
					  size3, shouldPersist},
					  new String[]{stringType, intType, intType, intType,
					  intType, intType, intType, booleanType});
			  
			  if(areThresholdsAdjusted == true){
				  nodesWithAutomaticallyAdjustedThresholds.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
			  }
			  
			  shouldPersist = false;
		  } catch (InstanceNotFoundException e) {
			  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR) + " not present on the MBean server", e);
			  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
		  } catch(MBeanException e){
			  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
			  /*if(e.getCause() instanceOf <cache persisting exception>){
			   * isFirstTimeFail = true
			   * }
			   * */
			  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
		  } catch(RuntimeMBeanException e){
			  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
			  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
		  } catch(ReflectionException e){
			  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
			  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
		  } finally {
			  nameIdx++;
		  }
	  }
	  
	  if(isFirstTimeFail == false){
		  for(; nameIdx < names.length; nameIdx++){
			  try{
				  areThresholdsAdjusted = !(Boolean)mbeanServer.invoke(names[nameIdx], OPERATIONS.RESIZE_REGION.getName(), new Object[]{regionName,
					  count1, count2,
					  count3, size1,
					  size2, size3,
					  false},
					  new String[]{stringType, intType, intType,
					  intType, intType, intType, intType, booleanType});

				  if(areThresholdsAdjusted == true){
					  nodesWithAutomaticallyAdjustedThresholds.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
				  }

			  }catch (InstanceNotFoundException e) {
				  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR) + " not present on the MBean server", e);
				  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
			  } catch (MBeanException e) {
				  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
				  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
			  } catch (RuntimeMBeanException e) {
				  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
				  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
			  } catch (ReflectionException e){
				  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, "Cannot perform operation " + method + " on LocalCacheManagementMBean for node " + names[nameIdx].getKeyProperty(CLUSTER_NODE_STR), e);
				  nodesWithFailedResize.add(names[nameIdx].getKeyProperty(CLUSTER_NODE_STR));
			  }
		  }
	  }
	  
	  CompositeType resizeResultType = null;
	  CompositeData resizeResult = null;
	  try{
		  String[] itemNames = new String[]{CacheManagementNames.NO_SUCH_REGION_REASON_STR, 
				  CacheManagementNames.AUTOMATICALLY_ADJUSTED_REASON_STR};
		  String[] itemDescriptions = new String[]{"nodes, on which the region is not present",
		  "nodes, on which during the resize of the region one or more of the thresholds are automatically adjusted"};
		  ArrayType[] itemTypes = new ArrayType[]{
				  new ArrayType(1, SimpleType.STRING),
				  new ArrayType(1, SimpleType.STRING)
		  };
		  
		  resizeResultType = new CompositeType("ResizeResult", 
				  "Result of resizing a cache region", itemNames, itemDescriptions, itemTypes);
		  resizeResult = new CompositeDataSupport(resizeResultType, itemNames, 
				  new Object[]{nodesWithFailedResize.toArray(new String[nodesWithFailedResize.size()]),
						  nodesWithAutomaticallyAdjustedThresholds.toArray(new String[nodesWithAutomaticallyAdjustedThresholds.size()])});
		  
	  }catch(OpenDataException e){
		  SimpleLogger.traceThrowable(Severity.WARNING, LOCATION, method + ": Error in constructing open data", e);
	  }
	  
	  
	  return resizeResult;
  }

  private Set<ObjectName> getMBeanNames(String name) {
    final String method = "getMBeanNames(String name)";
    ObjectName objName = null;
    try {
      objName = new ObjectName(name);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.DEBUG, LOCATION, method, e);
    }

    Set<ObjectName> mbeanNames = null;

    if (objName != null) {
      mbeanNames = mbeanServer.queryNames(objName, null);
    }

    return mbeanNames;
  }

}
