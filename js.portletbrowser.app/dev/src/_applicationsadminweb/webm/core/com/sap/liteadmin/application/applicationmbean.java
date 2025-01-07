/*
 * ApplicationMBean.java
 *
 * Created on June 27, 2006, 6:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sap.liteadmin.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * @author nikolai-do
 */
public class ApplicationMBean {

  private List<ApplicationInfo> app = null;
  private HashMap<String,ObjectName> mBeansApp = null;

  private InitialContext ctx = null;

  private MBeanServerConnection mbs = null;

  private String applicationName = null;
  private ApplicationInfo applicationDetailed = null;

  private List<SelectItem> applist = null;

  private Logger log = Logger.getLogger(ApplicationMBean.class.getName());

  private HashMap<String, String> appMap = new HashMap<String, String>();
  
  private boolean messages = false;
  
  private boolean goDS = false;
  private boolean goDSAlias = false;
  private boolean go = false;

  /** Creates a new instance of ApplicationMBean */
  public ApplicationMBean() {
    try {
      ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
    } catch (NamingException ex) {
      ex.printStackTrace();
    }
  }

  public String getNavigationPath() {
    String result = "Applications";
    if (applicationDetailed != null) {
      result = result + " > " + applicationDetailed.getName();
    }
    return result;
  }
  
  public List getApp() {
    if (app == null) {
      try {
        updateApplications();
      } catch (Exception e) {
        log.log(null, "Error during updating applications info", e);
        FacesMessage msg = new FacesMessage("ERROR! Error during updating applications info. The exception is: " + e.toString());
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("", msg);    
        return null;        
      }
    }
    return app;
  }

  private void updateApplications() throws NamingException,
      MalformedObjectNameException, IOException, InstanceNotFoundException,
      IntrospectionException, ReflectionException, MBeanException,
      AttributeNotFoundException {    
    app = new ArrayList<ApplicationInfo>();
    mBeansApp = new HashMap<String,ObjectName>();
    
    
    // change here the name of the application
    String pattern_str = "*:cimclass=SAP_ITSAMJ2eeApplication,*";
    ObjectName pattern = null;
    if (pattern_str != null) { // 2
      pattern = new ObjectName(pattern_str);
    } // 2

    Set result = mbs.queryNames(pattern, null); // THIS IS IMPORTANT - from JASEN
    Object applications[] = result.toArray();

    for (int i = 0; i < applications.length; i++) { // 2
      ObjectName on = (ObjectName) applications[i];    
      
      ApplicationInfo app_info = new ApplicationInfo();
      
      String applicationName = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
      applicationName = normalize(applicationName);
      app_info.setName(applicationName);
            
      CompositeData applicationSettings = (CompositeData) mbs.getAttribute(on, "Settings"); // THIS IS IMPORTANT - from JASEN
      String softwareType = (String)applicationSettings.get("SoftwareType");
      if (softwareType != null && (!softwareType.equalsIgnoreCase("DBSC") || 
          !softwareType.equalsIgnoreCase("FS") || !softwareType.equalsIgnoreCase("CFS") ||
          !softwareType.equalsIgnoreCase("JDDSCHEMA"))) {
        // add a filter which does not list applications of types DBSC and FS, CFS, JDDSCHEMA
        // in this way, applications listed by telnet command list_app and here will be the same 
        continue;
      }
      app_info.setApplicationFailover((String)applicationSettings.get("ApplicationFailover"));
      app_info.setArchiveSize((Long)applicationSettings.get("ArchiveSize"));
      app_info.setCaption((String)applicationSettings.get("Caption"));
      app_info.setSoftwareType(softwareType);
      app_info.setRemoteSupport((String[])applicationSettings.get("RemoteSupport"));
      app_info.setElementName((String)applicationSettings.get("ElementName"));
      app_info.setVendor((String)applicationSettings.get("Vendor"));
      
      Integer applicationState = (Integer) mbs.getAttribute(on, "state"); // THIS IS IMPORTANT - from JASEN     
      app_info.setState(applicationState);        
      
      String moduleNames[] = (String[]) mbs.getAttribute(on, "moduleNames"); // THIS IS IMPORTANT - from JASEN
      Vector<ModulesInfo> _v = new Vector<ModulesInfo>();
      for (int j = 0; moduleNames != null && j < moduleNames.length; j++) {
        String currentModule = moduleNames[j];
        String currentModuleName = currentModule.substring(currentModule.indexOf(",module=") + ",module=".length(), currentModule.indexOf(",type="));

        String currentModuleType = currentModule.substring(currentModule.indexOf(",type=") + ",type=".length());
        String _stmp = normalize(currentModuleName);        
        appMap.put(_stmp, currentModuleName);
        ModulesInfo mi = new ModulesInfo();
        mi.setApplicationName(applicationName);
        mi.setName(_stmp);
        mi.setType(currentModuleType);
        _v.add(mi);
      } 
      
      if (_v.size() > 0) {
        ModulesInfo[] info = (ModulesInfo[])_v.toArray(new ModulesInfo[0]);
        app_info.setModules(info);        
      }            
      
      if (app == null) {
        app = new ArrayList<ApplicationInfo>();
      }
      app.add(app_info);
      
      if (mBeansApp == null) {
        mBeansApp = new HashMap<String,ObjectName>();
      }
      mBeansApp.put(applicationName,on);
    }
  }

  public List getApplist() {
    FacesContext context = FacesContext.getCurrentInstance();
    try {
      updateApplications();
    } catch (Exception e) {
      log.log(null, "Error during updating applications info", e);
      FacesMessage msg = new FacesMessage("ERROR! Error during updating applications info. The exception is: " + e.toString());
      context.addMessage("", msg);           
      return null;
    }
    return applist;
  }

  public String listProperties() {
    return "view";
  }

  public String listApplications() {
    return "";
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public void setApplicationName(String name) {
    this.applicationName = name;
  }

  private String normalize(String applicationName) {
    char ch = '~';
    applicationName = applicationName.replace(ch, '.');
    return applicationName;
  }

  public void applicationNameListener(ActionEvent event) {
    // for application info  applicationDetailed
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    String applicationNameDetailed = obj.toString();    
    
    for (int i = 0; i < app.size(); i++) {     
        ApplicationInfo currentAI = app.get(i);
        if (!currentAI.getName().equals(applicationNameDetailed)) {
          continue;
        }
        applicationDetailed = currentAI;
    }
  }
    
  public void webModuleNameListener(ActionEvent event) {
//  for web modules info    
	messages = false;
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding binding = context.getApplication().createValueBinding("#{ApplicationSettingsBean}");
    ApplicationSettingsMBean settings = (ApplicationSettingsMBean) binding.getValue(context);
    if (appMap.get(obj.toString()) != null) {
      settings.setName(appMap.get(obj.toString()));
      settings.setApplicationName(applicationDetailed.getName());
      go = true;
    }
  }

  public void dsModulesListener(ActionEvent event) {    
    messages = false;
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding binding = context.getApplication().createValueBinding("#{DataSourceSettings}");
    DataSourceSettingsMBean settings = (DataSourceSettingsMBean) binding.getValue(context);
    if (appMap.get(obj.toString()) != null) {
      settings.setDsName(appMap.get(obj.toString()));
      settings.setApplicationName(applicationDetailed.getName());
      settings.init();
      goDS = true;
    }    
  }
  
  public void dsAliasModulesListener(ActionEvent event) {    
    messages = false;
    Object obj = ((HtmlCommandLink) event.getComponent()).getValue();
    FacesContext context = FacesContext.getCurrentInstance();
    ValueBinding binding = context.getApplication().createValueBinding("#{DataSourceAliasSettings}");
    DataSourceAliasSettingsMBean settings = (DataSourceAliasSettingsMBean) binding.getValue(context);
    if (appMap.get(obj.toString()) != null) {
      settings.setDsName(appMap.get(obj.toString()));
      settings.setApplicationName(applicationDetailed.getName());
      settings.init();
      goDSAlias = true;
    }    
  }
  
  public ApplicationInfo getApplicationDetailed() {
	messages = false;
    return applicationDetailed;
  }
    
  public String view() {
    if (go) {
      go = false;
      return "view";
    }
    return "";
  }
  
  public String viewDS() {
    if (goDS) {
      goDS = false;
      return "viewDS";
    }
    return "";
  }
  
  public String viewDSAlias() {
    if (goDSAlias) {
      goDSAlias = false;
      return "viewDSAlias";
    }
    return "";
  }
  
  public String startApplications() {
	FacesContext context = FacesContext.getCurrentInstance();
    Vector<String> _app = new Vector<String>();
    if (app == null){
    	return "";
    }    
    messages = true;
    ApplicationInfo currentAI = null;
    for (int i = 0; i < app.size(); i++) {
      try {
        currentAI = app.get(i);
        if (!currentAI.isSelected()) {  //check if the application is selected for stopping
          continue;
        }        
        log.info("Starting application " + currentAI.getName() + " ...");
        if (currentAI.getState().intValue() != 1) {  // applicationMode != Started
          ObjectName on = mBeansApp.get(currentAI.getName());
          if (on == null){
            context.addMessage("", new FacesMessage("ERROR! Problem during starting application " + currentAI.getName() + ". Cannot find the corresponding mbean."));        	  
        	continue;
          }
          String appNameMB = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
          if (appNameMB == null) {
            context.addMessage("", new FacesMessage("ERROR! Problem during starting application " + currentAI.getName() + ". Cannot find the corresponding mbean."));           
            continue;
          }
          
          javax.management.MBeanInfo mi = mbs.getMBeanInfo(on);
          MBeanOperationInfo[] mboi = mi.getOperations();        

          for (int j = 0; mboi != null && j < mboi.length; j++) { //4           
            if (!"stop".equals(mboi[j].getName())) {
              continue;
            }

            Object _result = mbs.invoke(on, "start", null, null);  
            if (_result != null) {
              String returnCode = (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
              FacesMessage msg = new FacesMessage("Result: " + returnCode);
              context.addMessage("", msg);
            }
            _app.add(currentAI.getName());
          }
        }           
      } catch (Exception e) {
        FacesMessage msg = new FacesMessage("ERROR! Problem during starting application: " + currentAI.getName() + ". The exception is: " + e.toString());
        context.addMessage("", msg);        
        log.log(null, "ERROR! Problem during starting application: " + currentAI.getName(), e);
      }
    }
    
    // reset the table with applications
    int count = _app.size()*4;
    try {
      while (_app.size() > 0 && count > 0){
        updateApplications();
        count--;
        for (int i = 0; i < app.size(); i++) {
          currentAI = app.get(i);
          if (currentAI.isSelected() && currentAI.getStateString().equals("Started")) {  //check if the application is selected for stopping
            _app.remove(0);
          }
        }
      }     
    } catch (Exception e) {
      FacesMessage msg = new FacesMessage("ERROR! Problem during starting applications. The exception is: " + e.toString());
      context.addMessage("", msg);        
      log.log(null, "ERROR! Problem during starting applications", e);
    }
    // reset the varaible
    applicationDetailed = null;
    return "";
  }
  
  public String stopApplications() {
    FacesContext context = FacesContext.getCurrentInstance();
    Vector<String> _app = new Vector<String>();
    if(app == null){
    	return "";
    	//TODO should say no application is selected
    }
    ApplicationInfo currentAI = null;
    messages = true;
    for (int i = 0; i < app.size(); i++) {
      try {
        currentAI = app.get(i);
        if (!currentAI.isSelected()) {  //check if the application is selected for stopping
          continue;
        }
        
        log.info("Stopping application " + currentAI.getName() + " ...");
        if (currentAI.getState().intValue() != 3) {  // applicationMode != Stopped
          ObjectName on = mBeansApp.get(currentAI.getName());
          if (on == null){
            context.addMessage("", new FacesMessage("ERROR! Problem during stopping application " + currentAI.getName() + ". Cannot find the corresponding mbean."));
        	continue;
          }
          String appNameMB = (String) mbs.getAttribute(on, "Name"); // THIS IS IMPORTANT - from JASEN
          if (appNameMB == null) {
            context.addMessage("", new FacesMessage("ERROR! Problem during stopping application " + currentAI.getName() + ". Cannot find the corresponding mbean."));
            continue;
          }
          
          javax.management.MBeanInfo mi = mbs.getMBeanInfo(on);
          MBeanOperationInfo[] mboi = mi.getOperations();        

          for (int j = 0; mboi != null && j < mboi.length; j++) { //4           
            if (!"stop".equals(mboi[j].getName())) {
              continue;
            }

            Object _result = mbs.invoke(on, "stop", null, null);
            if (_result != null){
            	String returnCode = (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");
            	FacesMessage msg = new FacesMessage("Result: " + returnCode);
                context.addMessage("", msg);
            }
            _app.add(currentAI.getName());
          }
        }           
      } catch (Exception e) {
        FacesMessage msg = new FacesMessage("ERROR! Problem during stopping application: " + currentAI.getName() + ". The exception is: " + e.toString());
        context.addMessage("", msg);        
        log.log(null, "ERROR! Problem during stopping application: " + currentAI.getName(), e);
      }
    }
    
    //reset the table with applications 
    try {
    	int count = _app.size()*4;
       	while (_app.size() > 0 && count > 0){
           updateApplications();
           count--;
           for (int i = 0; i < app.size(); i++) {
                currentAI = app.get(i);
                if (currentAI.isSelected() && currentAI.getStateString().equals("Stopped")) {  //check if the application is selected for stopping
                _app.remove(0);
                }
           }
        } 
    } catch (Exception e) {
      FacesMessage msg = new FacesMessage("ERROR! Problem during stopping applications. The exception is: " + e.toString());
      context.addMessage("", msg);        
      log.log(null, "ERROR! Problem during stopping applications", e);
    }
    // reset the varaible
    applicationDetailed = null;
    return "";
  }
  
  public void clearAll() {
    applicationDetailed = null;
    applicationName = null;
    //return "";
  }

  public boolean isMessages() {
	return messages;
  }  
}
