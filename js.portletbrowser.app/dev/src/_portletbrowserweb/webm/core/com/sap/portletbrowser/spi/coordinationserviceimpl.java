package com.sap.portletbrowser.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.Event;

import com.sap.engine.services.portletcontainer.spi.CoordinationService;

public class CoordinationServiceImpl implements CoordinationService {
	
  private CoordinationManager manager;
	private List<Event> events;
	private Map<String, String[]> parameters;
	private String portletName;
  private String applicationName;
  private int eventsNumber = 0;
	
	public CoordinationServiceImpl(CoordinationManager manager, String portletName, String applicationName) {
	  this.portletName = portletName;
	  this.applicationName = applicationName;
	  this.manager = manager;
	  this.events = new ArrayList<Event>();
	  this.parameters = new HashMap<String, String[]>();
	}
	
	
	public Event getEvent() {
		return events.remove(0);
	}

	public Map<String, String[]> getPublicParameterMap() {
		return Collections.unmodifiableMap(parameters);
	}

	public String getPublicRenderParameter(String parameterId) {
		String[] values = parameters.get(parameterId);
		
		if(values != null && values.length > 0){
			return values[0];
		}
		
		return null;
	}

	public Enumeration<String> getPublicRenderParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	public String[] getPublicRenderParameterValues(String parameterId) {
		return parameters.get(parameterId);
	}

	public void removePublicRenderParameter(String parameterId) {
	  manager.removePublicRenderParameter(parameterId, this);
	}

	
	public void removePublicRenderParameterFromMap(String parameterId) {
    parameters.remove(parameterId);
  }
	
	
	public void setEvent(Event event) {
	  manager.setEvent(event, this);
	}
	
	public void setEventInList(Event event) {
    events.add(event);
    eventsNumber++;
  }
	

	public void setPublicRenderParameter(String parameterId, String value) {
		manager.setPublicParameter(parameterId, value, this);
	}

	

  public void setPublicRenderParameterInMap(String parameterId, String value) {
    setPublicRenderParameterInMap(parameterId, new String[]{value});
  }
	
	
	public void setPublicRenderParameter(String parameterId, String[] value) {
		manager.setPublicParameter(parameterId, value, this);
	}
	

  public void setPublicRenderParameterInMap(String parameterId, String[] value) {
    if(!parameters.containsKey(parameterId)){
      parameters.put(parameterId, value);
    }
    else {
      String[] oldValue = parameters.get(parameterId);
      
      Set<String> set = new LinkedHashSet<String>();
      
      for(String param : value){
        set.add(param);
      }
      
      for(String param : oldValue){
        set.add(param);
      }
      
      String[] newValue = new String[set.size()];
      newValue = set.toArray(newValue);
      
      parameters.put(parameterId, newValue);
    }
  }
  
  public int getEventsNumber(){
    return eventsNumber;
  }

  public String getPortletName(){
    return portletName;
  }
  
  public String getApplicationName(){
    return applicationName;
  }  
  
  public void process(){
    eventsNumber--;
  }

  @Override
  public int hashCode() {
    return portletName.hashCode() + applicationName.hashCode();
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj){
      return true;
    }
    if (obj == null){
      return false;
    }
    if (getClass() != obj.getClass()){
      return false;
    }
    final CoordinationServiceImpl other = (CoordinationServiceImpl) obj;
    return portletName.equals(other.portletName) && applicationName.equals(other.applicationName);    
  }
  
  public boolean isSupportedRenderParams(String parameter){
    return manager.isSupportedRenderParams(parameter, this);
  }
}
