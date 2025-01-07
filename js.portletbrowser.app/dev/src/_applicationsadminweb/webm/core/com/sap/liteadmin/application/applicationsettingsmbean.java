/*
 * ApplicationSettingsMBean.java
 *
 * Created on June 29, 2006, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sap.liteadmin.application;

import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import com.sap.engine.admin.model.itsam.jsr77.web.*;

/**
 * A backing bean for the web module info. It is a connection between MBean and the UI
 * 
 * @author nikolai-do
 */
public class ApplicationSettingsMBean {
  private String name;  
  private String applicationName;
    
  private MBeanServerConnection mbs;
  
  private Logger log = Logger.getLogger(ApplicationMBean.class.getName());
  
  private String displayName;
  private boolean distributable;
  private int sessionTimeOut;
  private String failoverMessage;
  private int failoverTimeout;
  private boolean urlSessionTracking;
  private int maxSessions;
  private String welcomeFiles[];
  private CookiesSettings cookies[];
  private ServletSettings servlets[];
  private ServletMappingsSettings servletMappings[];
  private FilterSettings filters[];
  private ListenerSettings listeners[];
  private FilterMappingsSettings filterMappings[];
  private ContextParamsSettings contextParams[];
  private JSPPropertiesGroupSettings[] jspPropertiesGroups;
  private ErrorPagesSettings[] errorPages;
  private ResponseStatusesSettings[] responseStatuses;
  private TaglibsSettings[] taglibs;
  private LocaleEncodingMappingsSettings[] localeEncodingMappings;
  private EJBRemoteReferencesSettings[] ejbRemoteReferences;
  private EJBLocalReferencesSettings[] ejbLocalReferences;
  private EnvironmentEntriesSettings[] environmentEntries;
  private MessageDestinationsSettings[] messageDestinations;
  private MessageDestinationReferencesSetting[] messageDestinationReferences;
  private ResourceEnvironmentReferencesSettings[] resourceEnvironmentReferences;
  private ResourceReferencesSettings[] resourceReferences;
  private ServiceReferencesSettings[] serviceReferences;
  private MIMEMappingsSettings[] mimeMappings;
   
  private boolean messages = false;
  
  public ApplicationSettingsMBean() {
    messages = false;
  }

  public String getdisplayName() {
    return displayName;
  }

  public boolean isDistributable() {
    return distributable;
  }

  public int getSessionTimeOut() {
    return sessionTimeOut;
  }

  public void setSessionTimeOut(int timeout) {
    this.sessionTimeOut = timeout;
  }

  public boolean isUrlSessionTracking() {
    return urlSessionTracking;
  }

  public void setUrlSessionTracking(boolean tracking) {
    this.urlSessionTracking = tracking;
  }

  public int getMaxSessions() {
    return maxSessions;
  }

  public void setMaxSessions(int sessions) {
    this.maxSessions = sessions;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    getSettings(name);
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }
  
  public boolean isMessages() {
    return messages;
  }
  
  private Object[] initMBS(String name) {
    try {
      InitialContext ctx = new InitialContext();
      mbs = (MBeanServerConnection) ctx.lookup("jmx");
      String pattern_str = "*:cimclass=SAP_ITSAMJ2eeWebModule,name=" + name + ",*";
      ObjectName pattern = null;
      if (pattern_str != null) { //2 
        pattern = new ObjectName(pattern_str);
      } //2
      Set result = mbs.queryNames(pattern, null); //THIS IS IMPORTANT - from JASEN
      return result.toArray();      
    } catch (Exception ex) {
      FacesMessage msg = new FacesMessage("ERROR! Cannot update web module info. The exception is: " + ex.toString());
      FacesContext context = FacesContext.getCurrentInstance();
      context.addMessage("", msg);  
      log.log(null, "ERROR! Cannot update web module info", ex);      
    }
    return null;
  }

  private void getSettings(String name) {
    try {
      Object[] res = initMBS(name);
      if (res.length < 1) {
        messages = true;
        FacesMessage msg = new FacesMessage("ERROR! Cannot find a MBean for web module " + name);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("", msg);  
        reset();
        return;
      }
      for (int i = 0; i < res.length; i++) { //2
        ObjectName on = (ObjectName) res[i];       

        Object obj = mbs.getAttribute(on, "LocalSettings"); //THIS IS IMPORTANT - from JASEN
        SAP_ITSAMJ2eeWebModuleSettings oldSettings = SAP_ITSAMJ2eeWebModuleWrapper
            .getSAP_ITSAMJ2eeWebModuleSettingsForCData((CompositeData) obj); //JASEN go kaza tva

        displayName = oldSettings.getDisplayName();
        distributable = oldSettings.getDistributable();
        sessionTimeOut = oldSettings.getSessionTimeout();        
        urlSessionTracking = oldSettings.getURLSessionTracking();
        maxSessions = oldSettings.getMaxSessions();
        failoverMessage = oldSettings.getFailoverMessage();
        failoverTimeout = oldSettings.getFailoverTimeout();        
        welcomeFiles = oldSettings.getWelcomeFiles();
        
        
        SAP_ITSAMJ2eeWebCookieSettings[] cookiesMBean = oldSettings.getCookies();        
        if (cookiesMBean == null) {
          cookies = null;
        } else {
          Vector <CookiesSettings>tempCookies = new Vector<CookiesSettings>();
          for (int j = 0; j < cookiesMBean.length; j++) {
            CookiesSettings temp = new CookiesSettings();
            temp.setType(cookiesMBean[j].getType());
            temp.setDomain(cookiesMBean[j].getDomain());
            temp.setPath(cookiesMBean[j].getPath());
            temp.setMaxAge(cookiesMBean[j].getMaxAge());
            // needed only in case of update
            temp.setCaption(cookiesMBean[j].getCaption());
            temp.setDescription(cookiesMBean[j].getDescription());
            temp.setElementName(cookiesMBean[j].getElementName());
            tempCookies.add(temp);
          }
          cookies = (CookiesSettings[])tempCookies.toArray(new CookiesSettings[0]);
        }
        
        SAP_ITSAMJ2eeServletSettings[] servletsMBean = oldSettings.getServlets();
        if (servletsMBean == null) {
          servlets = null;
        } else {
          Vector <ServletSettings>tempServlets = new Vector<ServletSettings>();
          for (int j = 0; j < servletsMBean.length; j++) {            
            ServletSettings temp = new ServletSettings();            
            temp.setElementName(servletsMBean[j].getElementName());
            temp.setDisplayName(servletsMBean[j].getDisplayName());            
            if (servletsMBean[j].getClassName() != null) {
              temp.setSource(servletsMBean[j].getClassName());
            } else {
              temp.setSource(servletsMBean[j].getJSPFile());
            }            
            temp.setLoadOnStartup(servletsMBean[j].getLoadOnStartup());
            temp.setDescription(servletsMBean[j].getDescription());
            //TODO - add init parameters            
            tempServlets.add(temp);
          }
          servlets = (ServletSettings[])tempServlets.toArray(new ServletSettings[0]);
        }       
        
        SAP_ITSAMJ2eeServletMappingSettings[] servletMappingsMBean = oldSettings.getServletMappings();
        if (servletMappingsMBean == null) {
          servletMappings = null;
        } else {
          Vector <ServletMappingsSettings>tempServletMappings = new Vector<ServletMappingsSettings>();
          for (int j = 0; j < servletMappingsMBean.length; j++) {
            ServletMappingsSettings temp = new ServletMappingsSettings();
            temp.setServletName(servletMappingsMBean[j].getServletName());
            temp.setUrlPattern(servletMappingsMBean[j].getURLPattern());
            temp.setDescription(servletMappingsMBean[j].getDescription());
            tempServletMappings.add(temp);
          }
          servletMappings = (ServletMappingsSettings[])tempServletMappings.toArray(new ServletMappingsSettings[0]);
        }       
                
        SAP_ITSAMJ2eeWebFilterSettings[] filtersMBean = oldSettings.getWebFilters();
        if (filtersMBean == null) {
          filters = null;
        } else {
          Vector <FilterSettings>tempFilters = new Vector<FilterSettings>();
          for (int j = 0; j < filtersMBean.length; j++) {            
            FilterSettings temp = new FilterSettings();  
            temp.setElementName(filtersMBean[j].getElementName());
            temp.setDisplayName(filtersMBean[j].getDisplayName());
            temp.setClassName(filtersMBean[j].getClassName());   
            temp.setDescription(filtersMBean[j].getDescription());
            //TODO - add init parameters            
            tempFilters.add(temp);
          }
          filters = (FilterSettings[])tempFilters.toArray(new FilterSettings[0]);
        }       

        SAP_ITSAMJ2eeWebFilterMappingSettings[] filterMappingsMBean = oldSettings.getWebFilterMappings();
        if (filterMappingsMBean == null) {
          filterMappings = null;
        } else {          
          Vector <FilterMappingsSettings>tempFilterMappings = new Vector<FilterMappingsSettings>();          
          for (int j = 0; j < filterMappingsMBean.length; j++) {
            FilterMappingsSettings temp = new FilterMappingsSettings();
            temp.setFilterName(filterMappingsMBean[j].getFilterName());            
            temp.setServletName(filterMappingsMBean[j].getServletName());
            temp.setUrlPattern(filterMappingsMBean[j].getURLPattern());
            temp.setDispatcherError(filterMappingsMBean[j].getDispatcherError());
            temp.setDispatcherForward(filterMappingsMBean[j].getDispatcherForward());
            temp.setDispatcherInclude(filterMappingsMBean[j].getDispatcherInclude());
            temp.setDispatcherRequest(filterMappingsMBean[j].getDispatcherRequest());           
            tempFilterMappings.add(temp);
          }
          filterMappings = (FilterMappingsSettings[])tempFilterMappings.toArray(new FilterMappingsSettings[0]);
        }       
        
        SAP_ITSAMJ2eeWebListenerSettings[] listenersMBean = oldSettings.getWebListeners();
        if (listenersMBean == null) {
          listeners = null;
        } else {
          Vector <ListenerSettings>tempListeners = new Vector<ListenerSettings>();
          for (int j = 0; j < listenersMBean.length; j++) {           
            ListenerSettings temp = new ListenerSettings();
            temp.setClasstName(listenersMBean[j].getClassName());
            temp.setDescription(listenersMBean[j].getDescription());
            temp.setDisplayName(listenersMBean[j].getDisplayName());
            temp.setElementName(listenersMBean[j].getElementName());            
            tempListeners.add(temp);
          }
          listeners = (ListenerSettings[])tempListeners.toArray(new ListenerSettings[0]);
        }       
        
        SAP_ITSAMJ2eeProperty[] contextParamsMBean = oldSettings.getContextParameters();
        if (contextParamsMBean == null) {
          contextParams = null;
        } else {
          Vector <ContextParamsSettings>tempContextParams = new Vector<ContextParamsSettings>();
          for (int j = 0; j < contextParamsMBean.length; j++) {           
            ContextParamsSettings temp = new ContextParamsSettings();
            temp.setName(contextParamsMBean[j].getName());
            temp.setValue(contextParamsMBean[j].getValue());
            temp.setDescription(contextParamsMBean[j].getDescription());
            temp.setElelemntName(contextParamsMBean[j].getElementName());
            tempContextParams.add(temp);
          }
          contextParams = (ContextParamsSettings[])tempContextParams.toArray(new ContextParamsSettings[0]);
        } 
        
        SAP_ITSAMJ2eeWebJSPPropertyGroupSettings[] jspPropsGroupsMBean = oldSettings.getJSPPropertyGroups();
        if (jspPropsGroupsMBean == null) {
          jspPropertiesGroups = null;
        } else {
          Vector <JSPPropertiesGroupSettings>tempJspPropertiesGroups = new Vector<JSPPropertiesGroupSettings>();
          for (int j = 0; j < jspPropsGroupsMBean.length; j++) {           
            JSPPropertiesGroupSettings temp = new JSPPropertiesGroupSettings();            
            temp.setPageEncoding(jspPropsGroupsMBean[j].getPageEncoding());
            temp.setURLPatterns(jspPropsGroupsMBean[j].getURLPatterns());
            temp.setScriptingInvalid(jspPropsGroupsMBean[j].getScriptingInvalid());            
            temp.setIncludePreludes(jspPropsGroupsMBean[j].getIncludePreludes());
            temp.setIncludeCodas(jspPropsGroupsMBean[j].getIncludeCodas());
            temp.isELIgnored(jspPropsGroupsMBean[j].getELIgnored());
            temp.setXML(jspPropsGroupsMBean[j].getXML());
            temp.setElementName(jspPropsGroupsMBean[j].getElementName());
            temp.setDisplayName(jspPropsGroupsMBean[j].getDisplayName());            
            temp.setDescritption(jspPropsGroupsMBean[j].getDescription());
            
            tempJspPropertiesGroups.add(temp);
          }
          jspPropertiesGroups = (JSPPropertiesGroupSettings[])tempJspPropertiesGroups.toArray(new JSPPropertiesGroupSettings[0]);
        } 

        SAP_ITSAMJ2eeWebErrorPageSettings[] errorPagesMBean = oldSettings.getErrorPages();
        if (errorPagesMBean == null) {
          errorPages = null;
        } else {
          Vector <ErrorPagesSettings>tempErrorPages = new Vector<ErrorPagesSettings>();
          for (int j = 0; j < errorPagesMBean.length; j++) {      
            ErrorPagesSettings temp = new ErrorPagesSettings();           
            temp.setErrorCode(errorPagesMBean[j].getErrorCode());
            temp.setLocation(errorPagesMBean[j].getLocation());
            temp.setType(errorPagesMBean[j].getType());            
            temp.setElementName(errorPagesMBean[j].getElementName());                        
            temp.setDescritption(errorPagesMBean[j].getDescription());            
            tempErrorPages.add(temp);
          }
          errorPages = (ErrorPagesSettings[])tempErrorPages.toArray(new ErrorPagesSettings[0]);
        } 

        SAP_ITSAMJ2eeWebResponseStatusSettings[] responseStatusesMBean = oldSettings.getResponseStatuses();
        if (responseStatusesMBean == null) {
          responseStatuses = null;
        } else {
          Vector <ResponseStatusesSettings>tempResponseStatuses = new Vector<ResponseStatusesSettings>();
          for (int j = 0; j < responseStatusesMBean.length; j++) {      
            ResponseStatusesSettings temp = new ResponseStatusesSettings();              
            temp.setStatusCode(responseStatusesMBean[j].getStatusCode());
            temp.setReasonPhrase(responseStatusesMBean[j].getReasonPhrase());                        
            temp.setElementName(responseStatusesMBean[j].getElementName());                        
            temp.setDescritption(responseStatusesMBean[j].getDescription()); 
            
            tempResponseStatuses.add(temp);
          }
          responseStatuses = (ResponseStatusesSettings[])tempResponseStatuses.toArray(new ResponseStatusesSettings[0]);
        }
        
        SAP_ITSAMJ2eeWebTaglibSettings[] taglibsMBean = oldSettings.getTaglibs();
        if (taglibsMBean == null) {
          taglibs = null;
        } else {
          Vector <TaglibsSettings>tempTaglibs = new Vector<TaglibsSettings>();
          for (int j = 0; j < taglibsMBean.length; j++) {      
            TaglibsSettings temp = new TaglibsSettings();              
            temp.setLocation(taglibsMBean[j].getLocation());
            temp.setURI(taglibsMBean[j].getURI());                        
            temp.setElementName(taglibsMBean[j].getElementName());                        
            temp.setDescritption(taglibsMBean[j].getDescription()); 
            
            tempTaglibs.add(temp);
          }
          taglibs = (TaglibsSettings[])tempTaglibs.toArray(new TaglibsSettings[0]);
        }
        
        SAP_ITSAMJ2eeWebLocaleEncodingMappingSettings[] localeEncodingMappingsMBean = oldSettings.getLocaleEncodingMappings();
        if (localeEncodingMappingsMBean == null) {
          localeEncodingMappings = null;
        } else {
          Vector <LocaleEncodingMappingsSettings>tempLocaleEncodingMappings = new Vector<LocaleEncodingMappingsSettings>();
          for (int j = 0; j < localeEncodingMappingsMBean.length; j++) {      
            LocaleEncodingMappingsSettings temp = new LocaleEncodingMappingsSettings();              
            temp.setEncoding(localeEncodingMappingsMBean[j].getEncoding());
            temp.setLocale(localeEncodingMappingsMBean[j].getLocale());                        
            temp.setElementName(localeEncodingMappingsMBean[j].getElementName());                        
            temp.setDescritption(localeEncodingMappingsMBean[j].getDescription()); 
            
            tempLocaleEncodingMappings.add(temp);
          }
          localeEncodingMappings = (LocaleEncodingMappingsSettings[])tempLocaleEncodingMappings.toArray(new LocaleEncodingMappingsSettings[0]);
        } 
        
        SAP_ITSAMJ2eeEJBRemoteReferenceSettings[] ejbRemoteReferencesMBean = oldSettings.getEJBRemoteReferences();
        if (ejbRemoteReferencesMBean == null) {
          ejbRemoteReferences = null;
        } else {
          Vector <EJBRemoteReferencesSettings>tempEJBRemoteReferences = new Vector<EJBRemoteReferencesSettings>();
          for (int j = 0; j < ejbRemoteReferencesMBean.length; j++) {      
            EJBRemoteReferencesSettings temp = new EJBRemoteReferencesSettings();              
            temp.setEJBName(ejbRemoteReferencesMBean[j].getEJBName());
            temp.setEJBLink(ejbRemoteReferencesMBean[j].getEJBLink());
            temp.setJNDIName(ejbRemoteReferencesMBean[j].getJNDIName());
            temp.setEJBName(ejbRemoteReferencesMBean[j].getName());
            temp.setRemote(ejbRemoteReferencesMBean[j].getRemote());
            temp.setRemoteHome(ejbRemoteReferencesMBean[j].getRemoteHome());
            temp.setType(ejbRemoteReferencesMBean[j].getType());
            temp.setElementName(ejbRemoteReferencesMBean[j].getElementName());                        
            temp.setDescritption(ejbRemoteReferencesMBean[j].getDescription()); 
            
            tempEJBRemoteReferences.add(temp);
          }
          ejbRemoteReferences = (EJBRemoteReferencesSettings[])tempEJBRemoteReferences.toArray(new EJBRemoteReferencesSettings[0]);
        } 

        SAP_ITSAMJ2eeEJBLocalReferenceSettings[] ejbLocalReferencesMBean = oldSettings.getEJBLocalReferences();
        if (ejbLocalReferencesMBean == null) {
          ejbLocalReferences = null;
        } else {
          Vector <EJBLocalReferencesSettings>tempEJBLocalReferences = new Vector<EJBLocalReferencesSettings>();
          for (int j = 0; j < ejbLocalReferencesMBean.length; j++) {      
            EJBLocalReferencesSettings temp = new EJBLocalReferencesSettings();  
            temp.setEJBName(ejbLocalReferencesMBean[j].getEJBName());
            temp.setEJBLink(ejbLocalReferencesMBean[j].getEJBLink());
            temp.setJNDIName(ejbLocalReferencesMBean[j].getJNDIName());
            temp.setEJBName(ejbLocalReferencesMBean[j].getName());
            temp.setLocal(ejbLocalReferencesMBean[j].getLocal());
            temp.setLocalHome(ejbLocalReferencesMBean[j].getLocalHome());
            temp.setType(ejbLocalReferencesMBean[j].getType());
            temp.setElementName(ejbLocalReferencesMBean[j].getElementName());                        
            temp.setDescritption(ejbLocalReferencesMBean[j].getDescription()); 
            
            tempEJBLocalReferences.add(temp);
          }
          ejbLocalReferences = (EJBLocalReferencesSettings[])tempEJBLocalReferences.toArray(new EJBLocalReferencesSettings[0]);
        }        

        SAP_ITSAMJ2eeEnvironmentEntrySettings[] environmentEntriesMBean = oldSettings.getEnvironmentEntries();
        if (environmentEntriesMBean == null) {
          environmentEntries = null;
        } else {
          Vector <EnvironmentEntriesSettings>tempEnvironmentEntries = new Vector<EnvironmentEntriesSettings>();
          for (int j = 0; j < environmentEntriesMBean.length; j++) {      
            EnvironmentEntriesSettings temp = new EnvironmentEntriesSettings();  
            
            temp.setJNDIName(environmentEntriesMBean[j].getJNDIName());
            temp.setName(environmentEntriesMBean[j].getName());
            temp.setValue(environmentEntriesMBean[j].getValue());
            temp.setType(environmentEntriesMBean[j].getType());
            temp.setElementName(environmentEntriesMBean[j].getElementName());                        
            temp.setDescritption(environmentEntriesMBean[j].getDescription());             
            tempEnvironmentEntries.add(temp);
          }
          environmentEntries = (EnvironmentEntriesSettings[])tempEnvironmentEntries.toArray(new EnvironmentEntriesSettings[0]);
        }        

        SAP_ITSAMJ2eeMessageDestinationSettings[] messageDestinationsMBean = oldSettings.getMessageDestinations();
        if (messageDestinationsMBean == null) {
          messageDestinations = null;
        } else {
          Vector <MessageDestinationsSettings>tempMessageDestinations = new Vector<MessageDestinationsSettings>();
          for (int j = 0; j < messageDestinationsMBean.length; j++) {      
            MessageDestinationsSettings temp = new MessageDestinationsSettings(); 
            temp.setJNDIName(messageDestinationsMBean[j].getJNDIName());
            temp.setName(messageDestinationsMBean[j].getName());
            temp.setElementName(messageDestinationsMBean[j].getElementName());                        
            temp.setDescritption(messageDestinationsMBean[j].getDescription());     
            
            tempMessageDestinations.add(temp);
          }
          messageDestinations = (MessageDestinationsSettings[])tempMessageDestinations.toArray(new MessageDestinationsSettings[0]);
        } 
        
        SAP_ITSAMJ2eeMessageDestinationReferenceSettings[] messageDestinationReferencesMBean = oldSettings.getMessageDestinationReferences();
        if (messageDestinationReferencesMBean == null) {
          messageDestinationReferences = null;
        } else {
          Vector <MessageDestinationReferencesSetting>tempMessageDestinationReferences = new Vector<MessageDestinationReferencesSetting>();
          for (int j = 0; j < messageDestinationReferencesMBean.length; j++) {      
            MessageDestinationReferencesSetting temp = new MessageDestinationReferencesSetting();            
            temp.setJNDIName(messageDestinationReferencesMBean[j].getJNDIName());
            temp.setName(messageDestinationReferencesMBean[j].getName());
            temp.setLink(messageDestinationReferencesMBean[j].getLink());
            temp.setType(messageDestinationReferencesMBean[j].getType());
            temp.setUsage(messageDestinationReferencesMBean[j].getUsage());
            temp.setElementName(messageDestinationReferencesMBean[j].getElementName());                        
            temp.setDescritption(messageDestinationReferencesMBean[j].getDescription());     
            
            tempMessageDestinationReferences.add(temp);
          }
          messageDestinationReferences = (MessageDestinationReferencesSetting[])tempMessageDestinationReferences.toArray(new MessageDestinationReferencesSetting[0]);
        }
        
        SAP_ITSAMJ2eeJNDIEnvironmentReferenceSettings[] resourceEnvironmentReferencesMBean = oldSettings.getResourceEnvironmentReferences();
        if (resourceEnvironmentReferencesMBean == null) {
          resourceEnvironmentReferences = null;
        } else {
          Vector <ResourceEnvironmentReferencesSettings>tempResourceEnvironmentReferences = new Vector<ResourceEnvironmentReferencesSettings>();
          for (int j = 0; j < resourceEnvironmentReferencesMBean.length; j++) {      
            ResourceEnvironmentReferencesSettings temp = new ResourceEnvironmentReferencesSettings();
            temp.setJNDIName(resourceEnvironmentReferencesMBean[j].getJNDIName());
            temp.setName(resourceEnvironmentReferencesMBean[j].getName());
            temp.setType(resourceEnvironmentReferencesMBean[j].getType());
            temp.setElementName(resourceEnvironmentReferencesMBean[j].getElementName());                        
            temp.setDescritption(resourceEnvironmentReferencesMBean[j].getDescription());     
            
            tempResourceEnvironmentReferences.add(temp);
          }
          resourceEnvironmentReferences = (ResourceEnvironmentReferencesSettings[])tempResourceEnvironmentReferences.toArray(new ResourceEnvironmentReferencesSettings[0]);
        }
        
        SAP_ITSAMJ2eeResourceReferenceSettings[] resourceReferencesMBean = oldSettings.getResourceReferences();
        if (resourceReferencesMBean == null) {
          resourceReferences = null;
        } else {
          Vector <ResourceReferencesSettings>tempResourceReferences = new Vector<ResourceReferencesSettings>();
          for (int j = 0; j < resourceReferencesMBean.length; j++) {      
            ResourceReferencesSettings temp = new ResourceReferencesSettings();            
            temp.setJNDIName(resourceReferencesMBean[j].getJNDIName());
            temp.setName(resourceReferencesMBean[j].getName());
            temp.setType(resourceReferencesMBean[j].getType());
            temp.setAuthType(resourceReferencesMBean[j].getAuthType());
            temp.setSharingScope(resourceReferencesMBean[j].getSharingScope());
            temp.setTransactional(resourceReferencesMBean[j].getTransactional());
            temp.setElementName(resourceReferencesMBean[j].getElementName());                        
            temp.setDescritption(resourceReferencesMBean[j].getDescription());           
            
            tempResourceReferences.add(temp);
          }
          resourceReferences = (ResourceReferencesSettings[])tempResourceReferences.toArray(new ResourceReferencesSettings[0]);
        }

        SAP_ITSAMJ2eeJNDIEnvironmentReferenceSettings[] serviceReferencesMBean = oldSettings.getServiceReferences();
        if (serviceReferencesMBean == null) {
          serviceReferences = null;
        } else {
          Vector <ServiceReferencesSettings>tempServiceReferences = new Vector<ServiceReferencesSettings>();
          for (int j = 0; j < serviceReferencesMBean.length; j++) {      
            ServiceReferencesSettings temp = new ServiceReferencesSettings();            
            temp.setJNDIName(serviceReferencesMBean[j].getJNDIName());
            temp.setName(serviceReferencesMBean[j].getName());
            temp.setType(serviceReferencesMBean[j].getType());
            temp.setElementName(serviceReferencesMBean[j].getElementName());                        
            temp.setDescritption(serviceReferencesMBean[j].getDescription());           
            
            tempServiceReferences.add(temp);
          }
          serviceReferences = (ServiceReferencesSettings[])tempServiceReferences.toArray(new ServiceReferencesSettings[0]);
        }
        
        SAP_ITSAMJ2eeWebMIMEMappingSettings[] mimeMappingsMBean = oldSettings.getMIMEMappings();
        if (mimeMappingsMBean == null) {
          mimeMappings = null;
        } else {
          Vector <MIMEMappingsSettings>tempMIMEMappings = new Vector<MIMEMappingsSettings>();
          for (int j = 0; j < mimeMappingsMBean.length; j++) {      
            MIMEMappingsSettings temp = new MIMEMappingsSettings();            
            temp.setExtension(mimeMappingsMBean[j].getExtension());
            temp.setMIMEType(mimeMappingsMBean[j].getMIMEType());
            temp.setElementName(mimeMappingsMBean[j].getElementName());                        
            temp.setDescritption(mimeMappingsMBean[j].getDescription());           
            
            tempMIMEMappings.add(temp);
          }
          mimeMappings = (MIMEMappingsSettings[])tempMIMEMappings.toArray(new MIMEMappingsSettings[0]);
        }
      }
    } catch (Exception e) {
      FacesMessage msg = new FacesMessage("ERROR! Cannot update web module info for module " + name + ". The exception is: " + e.toString());
      FacesContext context = FacesContext.getCurrentInstance();
      context.addMessage("", msg); 
      log.log(null, "ERROR! Cannot update web module info for module " + name, e);
    }
  }

  public String updateWebModule() {    
    try {
      Object[] res = initMBS(name);
      if (res.length < 1) {
        messages = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage("Could not find mbeans: " + name);
        context.addMessage("", msg);
        reset();
        return "";
      }

      for (int i = 0; i < res.length; i++) {
        ObjectName on = (ObjectName) res[i];
        javax.management.MBeanInfo mi = mbs.getMBeanInfo(on);
        MBeanOperationInfo[] mboi = mi.getOperations();        

        Object obj = mbs.getAttribute(on, "LocalSettings"); //THIS IS IMPORTANT - from JASEN
        SAP_ITSAMJ2eeWebModuleSettings settings = SAP_ITSAMJ2eeWebModuleWrapper
            .getSAP_ITSAMJ2eeWebModuleSettingsForCData((CompositeData) obj); //JASEN go kaza tva

        settings.setHasMaxSessionsValueAssigned(true);
        settings.setMaxSessions(maxSessions);        
        settings.setHasSessionTimeoutValueAssigned(true);
        settings.setSessionTimeout(sessionTimeOut);
        settings.setHasURLSessionTrackingValueAssigned(true);
        settings.setURLSessionTracking(urlSessionTracking);
        log.info("Change properties of " + name + " web module with new values:");
        log.info("                maxSessions = " + maxSessions);
        log.info("                sessionTimeOut = " + sessionTimeOut);
        log.info("                urlSessionTracking = " + urlSessionTracking);
        
        //set cookies
        if (cookies != null) {          
          Vector <SAP_ITSAMJ2eeWebCookieSettings> cookiesMBean = new Vector<SAP_ITSAMJ2eeWebCookieSettings>();
          for (int j = 0; j < cookies.length; j++) {  
//          public SAP_ITSAMJ2eeWebCookieSettings(String Type,String Path,String Domain,int MaxAge,String Caption,String Description,String ElementName){            
            SAP_ITSAMJ2eeWebCookieSettings temp = new SAP_ITSAMJ2eeWebCookieSettings(cookies[j].getType(), cookies[j].getPath(),
                cookies[j].getDomain(), cookies[j].getMaxAge(), cookies[j].getCaption(), cookies[j].getDescription(), cookies[j].getElementName());            
            log.info("                cookies = " + cookies[j].getType() + "  " + cookies[j].getPath() + "  " + cookies[j].getDomain() + "  " + cookies[j].getMaxAge());
            cookiesMBean.add(temp);
          }
          settings.setCookies((SAP_ITSAMJ2eeWebCookieSettings[])cookiesMBean.toArray(new SAP_ITSAMJ2eeWebCookieSettings[0]));
        }
        
        for (int j = 0; mboi != null && j < mboi.length; j++) { //4              
          if (!"ApplyChanges".equals(mboi[j].getName())) {
            continue;
          }

          CompositeData prameter = SAP_ITSAMJ2eeWebModuleWrapper.getCDataForSAP_ITSAMJ2eeWebModuleSettings(settings); //OT JASEN

          Object _result = mbs.invoke(on, "ApplyChanges", 
              new Object[] { prameter },
              new String[] { "javax.management.openmbean.CompositeData" });
          String errorCode = (String) ((javax.management.openmbean.CompositeDataSupport) _result).get("Code");

          messages = true;
          FacesContext context = FacesContext.getCurrentInstance();
          FacesMessage msg = new FacesMessage("Updating properties for " + name + " web module finieshed with status: " + errorCode);
          context.addMessage("", msg);
        }
      }
    } catch (Exception e) {
      FacesMessage msg = new FacesMessage("ERROR! Cannot update web module info. The exception is: " + e.toString());
      FacesContext context = FacesContext.getCurrentInstance();
      context.addMessage("", msg); 
      log.log(null, "ERROR! Cannot update web module " + name, e);
    }
    return "";
  }

  public String getFailoverMessage() {
    return failoverMessage;
  }

  public int getFailoverTimeout() {
    return failoverTimeout;
  }

  public String getWelcomeFilesString() {
    if (welcomeFiles == null) {
      return null;
    }
    
    String result = "";
    for (int i = 0; i < welcomeFiles.length; i++) {
      result = result + welcomeFiles[i] + "; ";
    }
    return result;    
  }

  public CookiesSettings[] getCookies() {
    return cookies;    
  }

  public ServletSettings[] getServlets() {
    return servlets;
  }

  public ServletMappingsSettings[] getServletMappings() {
    return servletMappings;
  }

  public FilterSettings[] getFilters() {
    return filters;
  }

  public FilterMappingsSettings[] getFilterMappings() {
    return filterMappings;
  }
  
  public ListenerSettings[] getListeners() {
    return listeners;
  }
  
  public ContextParamsSettings[] getContextParams() {
    return contextParams;
  }
  
  public JSPPropertiesGroupSettings[] getJspPropertiesGroups() {
    return jspPropertiesGroups;
  }
  
  public ErrorPagesSettings[] getErrorPages() {
    return errorPages;
  }
  
  public ResponseStatusesSettings[] getResponseStatuses() {
    return responseStatuses;
  }
  
  public TaglibsSettings[] getTaglibs() {
    return taglibs;
  }
  
  public LocaleEncodingMappingsSettings[] getLocaleEncodingMappings() {
    return localeEncodingMappings;
  }
  
  public EJBRemoteReferencesSettings[] getEjbRemoteReferences() {
    return ejbRemoteReferences;
  }
  
  public EJBLocalReferencesSettings[] getEjbLocalReferences() {
    return ejbLocalReferences;
  }
  
  public EnvironmentEntriesSettings[] getEnvironmentEntries() {
    return environmentEntries;
  }
  
  public MessageDestinationsSettings[] getMessageDestinations() {
    return messageDestinations;
  }
  
  public MessageDestinationReferencesSetting[] getMessageDestinationReferences() {
    return messageDestinationReferences;
  }
  
  public ResourceEnvironmentReferencesSettings[] getResourceEnvironmentReferences() {
    return resourceEnvironmentReferences;
  }
  
  public ResourceReferencesSettings[] getResourceReferences() {
    return resourceReferences;
  }
  
  public ServiceReferencesSettings[] getServiceReferences() {
    return serviceReferences;
  }
  
  public MIMEMappingsSettings[] getmimeMappings() {
    return mimeMappings;
  }
  
  private void reset() {
    name = "";  // do not use setName() because it invokes this method
    maxSessions = -1;    
    sessionTimeOut = -1;
    urlSessionTracking = false;    
    displayName = "";
    distributable = false;
    failoverMessage = null;
    failoverTimeout = -1;
    welcomeFiles = null;
    cookies = null;
    servlets = null;
    servletMappings = null;
    filters = null;
    listeners = null;
    filterMappings = null;
    contextParams = null;
    jspPropertiesGroups = null;
    errorPages = null;
    responseStatuses = null;
    taglibs = null;
    localeEncodingMappings = null;
    ejbRemoteReferences = null;
    ejbLocalReferences = null;
    environmentEntries = null;
    messageDestinations = null;
    messageDestinationReferences = null;
    resourceEnvironmentReferences = null;
    resourceReferences = null;
    serviceReferences = null;
    mimeMappings = null;
  }
}
