package com.sap.portletbrowser.spi;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.Event;
import javax.xml.namespace.QName;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.sap.engine.services.portletcontainer.api.PortletAdmin;
import com.sap.engine.services.portletcontainer.spi.PortletNode;

public class CoordinationManager implements Serializable {
	
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private PortletAdmin portletAdmin;
  
	private Map<QName, Set<CoordinationServiceImpl>> processingEventServices;
	
	private Map<QName, Set<CoordinationServiceImpl>> publishingEventServices;
	
	private Map<String, Set<CoordinationServiceImpl>> renderParameterServices;
	
	private Set<CoordinationServiceImpl> allCoordinationServices;
	
	public CoordinationManager(PortletAdmin admin) {
		this.portletAdmin = admin;
	  this.processingEventServices = new HashMap<QName, Set<CoordinationServiceImpl>>();
	  this.publishingEventServices = new HashMap<QName, Set<CoordinationServiceImpl>>();
	  this.renderParameterServices = new HashMap<String, Set<CoordinationServiceImpl>>();
	  this.allCoordinationServices = new HashSet<CoordinationServiceImpl>();
	}
	
	public void subscribe(PortletNodeImpl node){
	  String name = node.getPortletName();
	  String moduleName = node.getPortletApplicationName();
    CoordinationServiceImpl coordinationServices = node.getCoordinationService();
		
    // Add service to allServices list
    allCoordinationServices.add(coordinationServices);
    
    // Add processing events subscribers
	  Enumeration<QName> processingEvents = portletAdmin.getProcessingEventQNames(name, moduleName);
	  
	  while (processingEvents.hasMoreElements()){
	    QName eventQName = processingEvents.nextElement();
	    
	    Set<CoordinationServiceImpl> servicesForEvent = processingEventServices.get(eventQName);
	    if (servicesForEvent == null) {
	      servicesForEvent = new HashSet<CoordinationServiceImpl>();
	    }
	    servicesForEvent.add(coordinationServices);
	    processingEventServices.put(eventQName, servicesForEvent);
	  }
	  
	  // Add processing events subscribers
    Enumeration<QName> publishingEvents = portletAdmin.getPublishingEventQNames(name, moduleName);

    while (publishingEvents.hasMoreElements()) {
      QName eventQName = publishingEvents.nextElement();

      Set<CoordinationServiceImpl> services = publishingEventServices.get(eventQName);
      if (services == null) {
        services = new HashSet<CoordinationServiceImpl>();
      }
      services.add(coordinationServices);
      publishingEventServices.put(eventQName, services);
    }
    
    // Add render parameter subscribers
    Enumeration<String> parameters = portletAdmin.getPublicRenderParameterNames(name, moduleName);

    while (parameters.hasMoreElements()) {
      String parameter = parameters.nextElement();

      Set<CoordinationServiceImpl> services = renderParameterServices.get(parameter);
      if (services == null) {
        services = new HashSet<CoordinationServiceImpl>();
      }
      services.add(coordinationServices);
      renderParameterServices.put(parameter, services);
    }
  }
	
	public void setPublicParameter(String key, String value, CoordinationServiceImpl sender){
	  Set<CoordinationServiceImpl> services = renderParameterServices.get(key);
		
	  // check if sender service has rights to change this parameters
		if ((services == null) || !services.contains(sender)) {
		  return;
		}
		
	  for(CoordinationServiceImpl service : services){
			service.setPublicRenderParameterInMap(key, value);
		}
	}
	
	
	public void setPublicParameter(String key, String[] value, CoordinationServiceImpl sender){
	  Set<CoordinationServiceImpl> services = renderParameterServices.get(key);
    
	  // check if sender service has rights to change this parameters
    if ((services == null) || !services.contains(sender)) {
      return;
    }
    for(CoordinationServiceImpl service : services){
			service.setPublicRenderParameterInMap(key, value);
		}
	}
	
	
	public void removePublicRenderParameter(String key, CoordinationServiceImpl sender){
	  Set<CoordinationServiceImpl> services = renderParameterServices.get(key);
    
	  // check if sender service has rights to remove this parameters
	  if ((services == null) || !services.contains(sender)) {
      return;
    }
    
	  for(CoordinationServiceImpl service : services){
			service.removePublicRenderParameterFromMap(key);
		}
	}
	
	
	public void setEvent(Event event, CoordinationServiceImpl sender){
	  Set<CoordinationServiceImpl> publishingServices = publishingEventServices.get(event.getQName()); 
	  
	  // check if sender service has rights to publish this event
	  if ((publishingServices == null) || !publishingServices.contains(sender)) {
	    return;
	  }

	  Set<CoordinationServiceImpl> processingServices = processingEventServices.get(event.getQName());
	  
	  // no services are processing this event
	  if (processingServices == null){
	    return;
	  }
	  
		for(CoordinationServiceImpl service : processingServices){
			service.setEventInList(event);
		}
	}
	
	
	public Set<CoordinationServiceImpl> getServicesList(){
	  return allCoordinationServices;
	}
	
	
	public CoordinationServiceImpl getService(String portletName, String appName){
	  CoordinationServiceImpl result = null;
	  
	  for (CoordinationServiceImpl service : allCoordinationServices){
	    if (portletName.equals(service.getPortletName()) &&
	        appName.equals(service.getApplicationName())){
	      result = service;
	      break;
	    }
	  }
	  
	  return result;
	}
	
	public  boolean isSupportedRenderParams(String parameter, CoordinationServiceImpl service){
	  Set<CoordinationServiceImpl> set = renderParameterServices.get(parameter);
	  if(set == null){
	    return false;
	  }
	  return set.contains(service);
	}
}
