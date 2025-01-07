package com.sap.engine.services.httpserver.server.management.beans;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_MBEANS;

import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroup;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

/**
 * LogonGroupOpenMBean provides operations found in logon_group telnet command
 * In general, the LogonGroupOpenMBean manages the logon groups.
 * 
 * @author Violeta Uzunova (I024174)
 *
 */
public class LogonGroupOpenMBean implements DynamicMBean, NotificationBroadcaster {
  private static Object lock = new Object();
  
  private MBeanInfo mbeanInfo;
  private LogonGroupsManager logonGroupsManager;
  
  
  private OpenType stringArrayType;
  private CompositeType logonGroupType;
  private ArrayType logonGroupTypeArray;
  private String[] itemNames;
  
  public LogonGroupOpenMBean() throws OpenDataException {
    //constructing data types
    stringArrayType = new ArrayType(1, SimpleType.STRING);
    
    itemNames = new String[] {"logonGroup", "instances", "aliases", "prefixes" };
    logonGroupType = new CompositeType("logonGroupType", //name 
        "Detail info for a logon group", //description
        itemNames, //itemNames
        itemNames, //itemDescription
        new OpenType[] {SimpleType.STRING, stringArrayType, stringArrayType, stringArrayType}); //itemTypes    
    logonGroupTypeArray = new ArrayType(1, logonGroupType);
    
    
    //Building OpenMBeanInfo
    OpenMBeanAttributeInfoSupport[] attributes = new OpenMBeanAttributeInfoSupport[0];
    OpenMBeanConstructorInfoSupport[] constructors  = new OpenMBeanConstructorInfoSupport[1];
    OpenMBeanOperationInfoSupport[] operations = new OpenMBeanOperationInfoSupport[11];
    MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[0];  
    
    //MBean constructor 
    constructors[0] = new OpenMBeanConstructorInfoSupport("LogonGroupOpenMBean",
            "Constructs a LogonGroupOpenMBean instance.",
            new OpenMBeanParameterInfoSupport[0]);

    //Parameters for listLogonGroup, addLogonGroup, removeLogonGroup operations
    OpenMBeanParameterInfo[] logonGroupParam = new OpenMBeanParameterInfoSupport[1];
    logonGroupParam[0] = new OpenMBeanParameterInfoSupport("logonGroup", "logon group name", SimpleType.STRING);

    //Parameters for addInstance, removeInstance operations
    OpenMBeanParameterInfo[] instanceParams = new OpenMBeanParameterInfoSupport[2];
    instanceParams[0] = new OpenMBeanParameterInfoSupport("logonGroup", "logon group name", SimpleType.STRING);
    instanceParams[1] = new OpenMBeanParameterInfoSupport("instance", "Instance", SimpleType.STRING);

    //Parameters for addAlias, removeAlias operation
    OpenMBeanParameterInfo[] aliasParams = new OpenMBeanParameterInfoSupport[2];
    aliasParams[0] = new OpenMBeanParameterInfoSupport("logonGroup", "logon group name", SimpleType.STRING);
    aliasParams[1] = new OpenMBeanParameterInfoSupport("alias", "Alias", SimpleType.STRING);
    
    //Parameters for addPrefix, removePrefix operation
    OpenMBeanParameterInfo[] prefixParams = new OpenMBeanParameterInfoSupport[2];
    prefixParams[0] = new OpenMBeanParameterInfoSupport("logonGroup", "logon group name", SimpleType.STRING);
    prefixParams[1] = new OpenMBeanParameterInfoSupport("prefix", "Prefix", SimpleType.STRING);
    
    //list_logon_groups
    operations[0] = new OpenMBeanOperationInfoSupport("listLogonGroup",
        "lists logon group details",
        logonGroupParam,
        logonGroupTypeArray,
        MBeanOperationInfo.INFO);

    operations[1] = new OpenMBeanOperationInfoSupport("addLogonGroup",
        "add the logon group",
        logonGroupParam,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);
    
    operations[2] = new OpenMBeanOperationInfoSupport("removeLogonGroup",
        "removes the logon group",
        logonGroupParam,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[3] = new OpenMBeanOperationInfoSupport("addInstance",
        "add a new instance to the logon group",
        instanceParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[4] = new OpenMBeanOperationInfoSupport("addAlias",
        "add a new alias to the logon group",
        aliasParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[5] = new OpenMBeanOperationInfoSupport("addPrefix",
        "add a new prefix to the logon group",
        prefixParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[6] = new OpenMBeanOperationInfoSupport("removeInstance",
        "removes the instance from the logon group",
        instanceParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[7] = new OpenMBeanOperationInfoSupport("removeAlias",
        "removes the alias from the logon group",
        aliasParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    operations[8] = new OpenMBeanOperationInfoSupport("removePrefix",
        "remove the prefix from the logon group",
        prefixParams,
        SimpleType.STRING,
        MBeanOperationInfo.ACTION);

    //for tests only
    operations[9] = new OpenMBeanOperationInfoSupport("listSimpleLogonGroup",
        "simple list",
        logonGroupParam,
        logonGroupType,
        MBeanOperationInfo.INFO);

    operations[10] = new OpenMBeanOperationInfoSupport("listLG",
        "lg details",
        logonGroupParam,
        stringArrayType,        
        MBeanOperationInfo.ACTION);  


    mbeanInfo = new OpenMBeanInfoSupport(this.getClass().getName(),
           "Logon Group Open MBean",
           attributes,
           constructors,
           operations,
           notifications);
    
  }
  
  public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException("No such attribute.");
  }

  public AttributeList getAttributes(String[] attributes) {
    return null;
  }

  /*
   * @see javax.management.DynamicMBean#getMBeanInfo()
   */
  public MBeanInfo getMBeanInfo() {    
    return mbeanInfo;
  }

  public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
    synchronized(lock){
      if (actionName == null) {
        throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot call invoke with null operation name on LogonGroupOpenMBean");
      } else if (actionName.equals("listLogonGroup")){
        checkSingleParam(params, true);
        return listLogonGroup((String)params[0]);
      } else if (actionName.equals("addLogonGroup")){
        checkSingleParam(params, false);
        return addLogonGroup((String)params[0]);
      } else if (actionName.equals("removeLogonGroup")){
        checkSingleParam(params, false);
        return removeLogonGroup((String)params[0]);          
      } else if (actionName.equals("addInstance")){
        checkDoubleParam(params);
        return addInstance((String)params[0], (String)params[1]);
      } else if (actionName.equals("addAlias")){
        checkDoubleParam(params);
        return addAlias((String)params[0], (String)params[1]);
      } else if (actionName.equals("addPrefix")){
        checkDoubleParam(params);
        return addPrefix((String)params[0], (String)params[1]);
      } else if (actionName.equals("removeInstance")){
        checkDoubleParam(params);
        return removeInstance((String)params[0], (String)params[1]);
      } else if (actionName.equals("removeAlias")){
        checkDoubleParam(params);
        return removeAlias((String)params[0], (String)params[1]);
      }else if (actionName.equals("removePrefix")){
        checkDoubleParam(params);
        return removePrefix((String)params[0], (String)params[1]);
      }
      return null;
    }
  }


  public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException("No such attribute.");

  }

  public AttributeList setAttributes(AttributeList attributes) {   
    return null;
  }

  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
    // TODO Auto-generated method stub
  }

  public MBeanNotificationInfo[] getNotificationInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
    // TODO Auto-generated method stub
  }

  public void setLogonGroupsManager(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
  }

  // operation methods
  private CompositeData[] listLogonGroup(String lgName){
    CompositeData[] result = new CompositeData[0];
    Vector<CompositeData> tempResult = new Vector<CompositeData>();
    
    if (lgName == null || lgName.equals("")) {
      LogonGroup logonGroups[] = logonGroupsManager.getAllLogonGroups();      
      for (int i = 0; i < logonGroups.length; i++) {
        try {
          tempResult.add(logonGroupToCompositeData(logonGroups[i]));
        } catch (OpenDataException e) {
          if (LOCATION_HTTP_MBEANS.beWarning()) {
            Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000324", 
              "LogonGroupOpenMBean.listLogonGroup(): Cannot convert the logon group to composite data.", e, null, null, null);
          }          
        }
      }
      result = new CompositeData[tempResult.size()];
      result = (CompositeData []) tempResult.toArray(result);
      return result;
    } else {
      try {
        result = new CompositeData[1];
        LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
        if (logonGroup == null) {
          if (LOCATION_HTTP_MBEANS.beWarning()) {
            Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000325", 
              "LogonGroupOpenMBean.listLogonGroup(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);
          }
          return result;
        }
        result[0] = logonGroupToCompositeData(logonGroup);
        return result;
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000326", 
            "LogonGroupOpenMBean.listLogonGroup(): Cannot convert the logon group to composite data.", e, null, null, null);
        }
        return result;
      }      
    }
  }
  
  /**
   * Adds a logon group 
   * 
   * @param lgName the name of the logon group
   * @return
   */
  private String addLogonGroup(String lgName) {
    if (logonGroupsManager.getLogonGroup(lgName) == null) {
      try {
        logonGroupsManager.registerLogonGroup(lgName);
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000327", "LogonGroupOpenMBean.addLogonGroup()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000328", 
          "LogonGroupOpenMBean.addLogonGroup(): {0} logon group already exists.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }        
  }
  
  
  /**
   * Removes a logon group 
   * 
   * @param lgName the name of the logon group
   * @return
   */
  private String removeLogonGroup(String lgName) {
    if (logonGroupsManager.getLogonGroup(lgName) != null) {
      try {
        logonGroupsManager.unregisterLogonGroup(lgName);
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000329", "LogonGroupOpenMBean.removeLogonGroup()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000330", 
          "LogonGroupOpenMBean.removeLogonGroup(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }        
  } 
  
  private String addInstance(String lgName, String instance) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (instance != null) {
          logonGroup.addInstance(instance);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000331", "LogonGroupOpenMBean.addInstance()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000332", 
          "LogonGroupOpenMBean.addInstance(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }    
  }  
  
  private String addAlias(String lgName, String alias) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (alias != null) {
          logonGroup.addAlias(alias);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000333", "LogonGroupOpenMBean.addAlias()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000334", 
          "LogonGroupOpenMBean.addAlias(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }
  }
  
  private String addPrefix(String lgName, String prefix) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (prefix != null) {
          logonGroup.addExactAlias(prefix);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000335", "LogonGroupOpenMBean.addPrefix()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000336", 
          "LogonGroupOpenMBean.addPrefix(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }
  }
  
  
  private String removeInstance(String lgName, String instance) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (instance != null) {
          logonGroup.removeInstance(instance);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000337", "LogonGroupOpenMBean.removeInstance()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000338", 
          "LogonGroupOpenMBean.removeInstance(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }
  }
  
  
  private String removeAlias(String lgName, String alias) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (alias != null) {
          logonGroup.removeAlias(alias);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000339", "LogonGroupOpenMBean.removeAlias()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000340", 
          "LogonGroupOpenMBean.removeAlias(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }
  }
  
  private String removePrefix(String lgName, String prefix) {
    LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
    if (logonGroup != null) {
      try {
        if (prefix != null) {
          logonGroup.removeExactAlias(prefix);
        }        
        return "success";
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000341 ", "LogonGroupOpenMBean.removePrefix()", e, null, null, null);          
        }
        return "error";
      }      
    } else {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000342", 
          "LogonGroupOpenMBean.removePrefix(): {0} logon group does not exist.", new Object[]{lgName}, null, null, null);          
      }
      return "ivoked";
    }
  }
  
  // helper methods
  private CompositeDataSupport logonGroupToCompositeData(LogonGroup logonGroup) throws OpenDataException {
      String[] instances = null;
      if (logonGroup.getInstances() != null && logonGroup.getInstances().size() != 0) {
        instances = new String[logonGroup.getInstances().size()];
        instances = (String[])logonGroup.getInstances().toArray(instances);
      }
      
      String[] aliases = null;
      if (logonGroup.getAliases() != null && logonGroup.getAliases().size() != 0) {
        aliases = new String[logonGroup.getAliases().size()];
        aliases = (String[])logonGroup.getAliases().toArray(aliases);
      }
      
      String[] exactAliases = null;
      if (logonGroup.getExactAliases() != null && logonGroup.getExactAliases().size() != 0) {
        exactAliases = new String[logonGroup.getExactAliases().size()];
        exactAliases = (String[])logonGroup.getExactAliases().toArray(exactAliases);
      }
      Object[] itemValues = new Object[] {logonGroup.getLogonGroupName(), instances, aliases, exactAliases };
      return new CompositeDataSupport(logonGroupType, itemNames, itemValues);   
  }  
  
  
  private void checkSingleParam(Object[] params, boolean nullAllowed) throws RuntimeOperationsException {
    if (params.length != 1){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal number of parameters"), 
          "Cannot invoke logon group mbean operation");
    }

    if (params[0] != null && !(params[0] instanceof String)){
        throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
          "The type is:" + params[0].getClass().getName() + ". Should be java.lang.String"),
          "Cannot invoke logon group mbean operation");
    }
    if (!nullAllowed && params[0] == null){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
          "The parameter can not be null"),
          "Cannot invoke logon group mbean operation");
    }
  }
  
  
  private void checkDoubleParam(Object[] params) throws RuntimeOperationsException {
    if (params.length != 2){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal number of parameters"),
      "Cannot invoke application alias mbean operation");
    }
    if (params[0] != null && !(params[0] instanceof String)){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter." +
          "The type is:"+params[0].getClass().getName()+". Should be java.lang.String"),
      "Cannot invoke application alias mbean operation");
    }
    if (params[1]!= null && !(params[1] instanceof String)){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the second parameter." +
          "The type is: "+params[1].getClass().getName()+". Should be java.lang.String"),
      "Cannot invoke application alias mbean operation");
    }
    if (params[1] == null){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the second parameter." +
        "The parameter can not be null"),
      "Cannot invoke application alias mbean operation");

    }
  }
}
