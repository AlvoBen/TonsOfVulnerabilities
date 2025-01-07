package com.sap.liteadmin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * @author Violeta Uzunova
 */
public class ServicesMBean {

  private MBeanServerConnection mbs = null;
  private List<ServiceInfo> services = null;
  private Logger log = Logger.getLogger(ServicesMBean.class.getName());

  public ServicesMBean() {
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (Exception e) {
      log.log(null, "Exception during ServicesMBean constructor execution", e);
    }
  }

  public List getServices() {
    if (services == null) {
      try {
        updateServices();
      } catch (Exception e) {
        FacesMessage msg = new FacesMessage("ERROR! Cannot update services info. The exception is: " + e.toString());
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("", msg);  
        log.log(null, "Exception during ServicesMBean init", e);
        return null;        
      }
    }
    return services;
  }

  private void updateServices() {
    try {
      ObjectName j2eeNodeObjectName = getJ2eeNodeObjectName();
      ObjectName j2EEInstanceObjectName = getJ2EEInstanceObjectName(j2eeNodeObjectName);
      ObjectName[] j2eeServiceObjectNames = getJ2eeServiceObjectNames(j2EEInstanceObjectName);

      for (int i = 0; i < j2eeServiceObjectNames.length; i++) { // 2
        ObjectName on = (ObjectName) j2eeServiceObjectNames[i];
        String serviceName = normalize(on.getKeyProperty("SAP_ITSAMJ2eeService.Name"));        
        
        String serviceStatus = (String)mbs.getAttribute(on, "LocalState");
        Boolean coreService = (Boolean)mbs.getAttribute(on, "Core");

        if (serviceName != null && serviceStatus != null && coreService != null) {
          ServiceInfo serviceInfo = new ServiceInfo(serviceName, serviceStatus, coreService);
          if (services == null) {
            services = new ArrayList<ServiceInfo>();
          }
          services.add(serviceInfo);
        }
      }
    } catch (Exception e) {
      FacesMessage msg = new FacesMessage("ERROR! Cannot update services info. The exception is: " + e.toString());
      FacesContext context = FacesContext.getCurrentInstance();
      context.addMessage("", msg); 
      log.log(null, "Exception during ServicesMBean init", e);
      e.printStackTrace();
    }
  }

  public String view() {
    return "view";
  }

  public void nameListener(ActionEvent event) {
    Object link = ((HtmlCommandLink) event.getComponent()).getValue();
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding binding = context.getApplication().createValueBinding("#{ServicePropertiesBean}");
    ServicePropertiesMBean servicePropsBean = (ServicePropertiesMBean) binding.getValue(context);
    servicePropsBean.setServiceName(link.toString());
  }

  // Getting all service configurations because of lazy loading (no JMX query possible)
  private ObjectName[] getJ2eeServiceObjectNames(ObjectName instanceObjectName) throws Exception {
    return (ObjectName[]) mbs.getAttribute(instanceObjectName, "SAP_ITSAMJ2eeInstanceJ2eeServicePartComponent");
  }

  private ObjectName getJ2EEInstanceObjectName(ObjectName j2eeNodeObjectName) throws Exception {
    ObjectName[] result = (ObjectName[]) mbs.getAttribute(j2eeNodeObjectName, "SAP_ITSAMJ2eeInstanceJ2eeNodeGroupComponent");
    if (result != null && result.length > 0) {
      return result[0];
    }
    return null;
  }

  private ObjectName getJ2eeNodeObjectName() throws MalformedObjectNameException, IOException {
    ObjectName pattern = new ObjectName(":type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode,SAP_J2EEClusterNode=\"\",*");
    Set set = mbs.queryNames(pattern, null);
    if (set.size() > 0) {
      return (ObjectName) set.iterator().next();
    }
    return null;
  }
  
  //needed becuase of errors on displaying ~  
  private String normalize(String serviceName) {    
    return serviceName.replace('~', '.');    
  }
}
