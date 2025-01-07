package com.sap.liteadmin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import javax.naming.InitialContext;

public class ServicePropertiesMBean {

  MBeanServerConnection mBeanServer = null;
  ObjectName j2eeServiceObjectName = null;
  
  String serviceName = null;

  List<PropertyEntry> servicePropertiesList = null;

  private boolean refresh = true;

  private boolean messages = false;

  public ServicePropertiesMBean() {
    try {
      messages = false;
      InitialContext ctx = new InitialContext();
      mBeanServer = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (Exception e) {
      // TODO add error message
      e.printStackTrace();
    }
  }
  
  // ========================== PUBLIC METHODS USED BY THE JSF PAGE ===============  
  
  /**
   * invoked by UI
   * 
   * @return Returns the serviceProperties.
   */
  public List getServicePropertiesList() {   
    if (servicePropertiesList == null || refresh) {      
      listServiceProperties();
      refresh = false;
    }
    
    return servicePropertiesList;
  }
  
  public String update() {    
    try {
      Properties props = listToProperties();           
      String result = updateServiceProperties(j2eeServiceObjectName, props);
      messages = true;
      FacesContext context = FacesContext.getCurrentInstance();      
      context.addMessage("", new FacesMessage("Changing service properties of the service " + serviceName + " finished with status: " + result));     
    } catch (Exception ex) {
      ex.printStackTrace();
      messages = true;
      FacesContext context = FacesContext.getCurrentInstance();      
      context.addMessage("", new FacesMessage("Error during changing properties of the service " + serviceName + ": " + ex.toString() + ". See log files for more details!"));      
    }   
    refresh = true;
    return "";
  }
  
  public String services() {
    return "services";
  }
  
  /**
   * @return Returns the serviceName.
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param serviceName
   *          The serviceName to set.
   */
  public void setServiceName(String serviceName) {
    messages = false;
    refresh = true;
    this.serviceName = serviceName;
  }
  
  public boolean isMessages() {
    return messages;
  }
  
  // ===================================== PRIVATE METHODS =========================
  
  private void listServiceProperties() {    
    try {
      initServiceON();      
      if (j2eeServiceObjectName == null) {
        messages = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage("Cannot init MBeans for " + serviceName + " service");
        context.addMessage("", msg);
        return;
      }      
      servicePropertiesList = new ArrayList<PropertyEntry>();
      
      ObjectName servicePropertiesObjectName[] = (ObjectName[]) mBeanServer.getAttribute(j2eeServiceObjectName, "SAP_ITSAMJ2eeServiceInstanceCfgDependent");
      if (servicePropertiesObjectName == null || servicePropertiesObjectName.length == 0) {
        messages = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage("Error during retrieving service properties for " + serviceName + " service. Cannot init attrinute SAP_ITSAMJ2eeServiceInstanceCfgDependent");
        context.addMessage("", msg);         
        return;
      }
      CompositeData servicePropertiesCompositeData[] = (CompositeData[])mBeanServer.getAttribute(servicePropertiesObjectName[0], "Properties");   
      
      for (int i = 0; i < servicePropertiesCompositeData.length; i++) {
        CompositeData serviceProp = (CompositeData)servicePropertiesCompositeData[i];        
        String key = (String)serviceProp.get("InstanceID");
        String value = (String)serviceProp.get("CustomCalcValue");        
        if (value == null) {  //no custom value set, use default value
          value = (String)serviceProp.get("CalculatedValue");
        }
        PropertyEntry propEntry = new PropertyEntry(key, value);
        propEntry.setDescription((String)serviceProp.get("ShortDescription"));
        int defaultValue = ((Integer)serviceProp.get("DefaultFlags")).intValue();
        propEntry.setOnlineModifable((defaultValue & PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE) == PropertyEntry.ENTRY_TYPE_ONLINE_MODIFIABLE);
        servicePropertiesList.add(propEntry);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      messages = true;
      FacesContext context = FacesContext.getCurrentInstance();
      FacesMessage msg = new FacesMessage("Error during retrieving service properties for " + serviceName + " service: " + e.toString() + ". See log file for more details!");
      context.addMessage("", msg);
      servicePropertiesList = null;
    }
  }

  //needed becuase of errors on displaying ~
  private String normalize(String serviceName) {
    return serviceName.replace('~', '.');
  }

  private void initServiceON() {
    j2eeServiceObjectName = null;
    if (serviceName == null) {
      return;
    }

    try {
      ObjectName j2eeNodeObjectName = getJ2eeNodeObjectName();
      ObjectName j2EEInstanceObjectName = getJ2EEInstanceObjectName(j2eeNodeObjectName);
      ObjectName[] j2eeServiceObjectNames = getJ2eeServiceObjectNames(j2EEInstanceObjectName);
      int i = 0;

      for (; i < j2eeServiceObjectNames.length; i++) {
        if (j2eeServiceObjectNames[i] == null) {
          continue;
        }
        if (normalize(j2eeServiceObjectNames[i].getKeyProperty("SAP_ITSAMJ2eeService.Name")).equals(serviceName)) {
          j2eeServiceObjectName = j2eeServiceObjectNames[i];
          return;
        }
      }
    } catch (Exception e) {
      j2eeServiceObjectName = null;
      FacesContext context = FacesContext.getCurrentInstance();
      FacesMessage msg = new FacesMessage("Error during initializaing MBeans for service " + serviceName + ": " + e.toString() + ". See log files for more details!");
      context.addMessage("", msg);
      e.printStackTrace();
    }
  }  
  
  // Getting all service configurations because of lazy loading (no JMX query possible)
  private ObjectName[] getJ2eeServiceObjectNames(ObjectName instanceObjectName) throws Exception {
    return (ObjectName[]) mBeanServer.getAttribute(instanceObjectName, "SAP_ITSAMJ2eeInstanceJ2eeServicePartComponent");
  }

  private ObjectName getJ2EEInstanceObjectName(ObjectName j2eeNodeObjectName) throws Exception {
    ObjectName[] result = (ObjectName[]) mBeanServer.getAttribute(j2eeNodeObjectName, "SAP_ITSAMJ2eeInstanceJ2eeNodeGroupComponent");
    if (result != null && result.length > 0) {
      return result[0];
    }
    return null;
  }

  private ObjectName getJ2eeNodeObjectName() throws MalformedObjectNameException, IOException {
    ObjectName pattern = new ObjectName(":type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode,SAP_J2EEClusterNode=\"\",*");
    Set set = mBeanServer.queryNames(pattern, null);
    if (set.size() > 0) {
      return (ObjectName) set.iterator().next();
    }
    return null;
  }
 
  private String updateServiceProperties(ObjectName j2eeServiceObjectName, Properties props) throws InstanceNotFoundException, MBeanException, ReflectionException, OpenDataException, IOException {
    CompositeData[] compositeData = createCompositeData(props);
    Integer result = (Integer) mBeanServer.invoke(j2eeServiceObjectName, "SaveInstanceConfigurationProperties", 
            new Object[] { compositeData, Long.valueOf(0), Boolean.valueOf(true), new String[] {} },
            new String[] { "[Ljavax.management.openmbean.CompositeData;", "long", "boolean", "[Ljava.lang.String;" });    
    switch (result.intValue()) {
      case 0: return "OK";
      case 1: return "Unknown Error";
      case 2: return "Properties Modified Meanwhile";
      case 3: return "Empty Properties Provided";
      case 4: return "Duplicated Properties for Update and Restore Provided";
      case 5: return "Attempt to Modify Non-Online Modifiable Property";
      case 6: return "No Such Service";
      case 7: return "Service does not provide runtime properties change";
      case 8: return "Unable to applay properties in DB";
      case 9: return "Unable to applay properties runtime";
      case 10: return "Unable to commit properties in DB";
      case 11: return "Unable to rollback properties in DB";
    }
    return "Unknown Return Code";
  }

  private CompositeData[] createCompositeData(Properties props) throws OpenDataException {
    String[] names =  new String[]{"DefaultValue", "CustomValue", "CalculatedValue", "DefaultCalcValue", "CustomCalcValue", "ShortDescription", "DefaultFlags", "CustomFlags", "Range", "Visibility", "Type", "Value", "Description", "InstanceID"};
    String[] descriptions = {".", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", "."}; //empty strings are not allowed for descriptions
    SimpleType[] types = {SimpleType.STRING,  SimpleType.STRING, SimpleType.STRING,  SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
                         SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.STRING, SimpleType.SHORT, SimpleType.BYTE, SimpleType.STRING,  SimpleType.STRING, SimpleType.STRING};

    String compositeTypeName = "SAP_ITSAMJ2eeConfigurationProperty";
    String compositeTypeDescription = "SAP_ITSAMJ2eeConfigurationProperty"; //reuse
    CompositeType compositeType = new CompositeType(compositeTypeName, compositeTypeDescription, names, descriptions, (javax.management.openmbean.OpenType[]) types);

    Iterator iterator = props.keySet().iterator();
    CompositeData[] result = new CompositeData[props.size()];
    int i = 0;
    while (iterator.hasNext()) {
      String key = (String)iterator.next();
      String value = props.getProperty(key);
      result[i++] = new CompositeDataSupport(compositeType, names, new Object[]{null, null, null, null, null, null, Integer.valueOf(-1), Integer.valueOf(-1), null, Short.valueOf((short)-1), Byte.valueOf((byte)-1), value, null, key});
    }
    return result;
  } 
  
  private Properties listToProperties() {
    if (servicePropertiesList == null) {
      return null;
    }
    
    Properties props = new Properties();    
    Object[] array = servicePropertiesList.toArray();
    for (int i = 0; i < array.length; i++) {
      PropertyEntry propEntry = (PropertyEntry)array[i];
      if (propEntry.isChanged()) {
        props.setProperty(propEntry.getKey(), propEntry.getValue());
      }
    }
   return props;
  }
}
