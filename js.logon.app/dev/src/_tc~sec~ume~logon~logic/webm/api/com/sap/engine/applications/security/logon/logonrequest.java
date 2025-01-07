/**
 * Copyright (c) 2009 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Jan 21, 2009 by I030665
 *   
 */
 
package com.sap.engine.applications.security.logon;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.sap.engine.applications.security.logon.beans.ErrorBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class LogonRequest {
  
  static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
  
  //Attributes  
  public static final String ATTRIBUTE_SEPARATOR = ";";
  
  public static final String ATTRIBUTE_FederationRequired = "federationRequired";  
  public static final String ATTRIBUTE_ServletForwardRequestURI = "javax.servlet.forward.request_uri";
  public static final String ATTRIBUTE_ServletForwardQueryString = "javax.servlet.forward.query_string";  
  public static final String ATTRIBUTE_LogonFormAction = "sap.com/logon_form_action";
  public static final String ATTRIBUTE_FederationIdP = "federationIdP";
  public static final String ATTRIBUTE_Saml2IdPDiscovery = "sap.com/idp_discovery";      
  public static final String ATTRIBUTE_Saml2IdPDiscoveryDefault = "sap.com/idp_discovery_default";
  public static final String ATTRIBUTE_Saml2IdPDiscoveryLocal = "sap.com/idp_discovery_local";
  public static final String ATTRIBUTE_IdPDiscoveryShowLocalLoginPage = "sap.com/idp_discovery_show_local_login_page";
  public static final String ATTRIBUTE_IdPDiscoveryShowSelectionPage = "sap.com/idp_discovery_show_idp_selection_page";

  
  //Parameters
  public static final String PARAMETER_ShowUidPasswordLogonPage = "showUidPasswordLogonPage";
  public static final String PARAMETER_SAML2Federation = "saml2fed";
  public static final String PARAMETER_JUserName = "j_username";
  public static final String PARAMETER_JPassword = "j_password";  
  public static final String PARAMETER_ErrorMessage = "error_message";
  public static final String PARAMETER_CreateCert = "createcert";  
  public static final String PARAMETER_NoCertStoring = "no_cert_storing";
  public static final String PARAMETER_LoginSubmit = "login_submit";
  public static final String PARAMETER_LoginDoRedirect = "login_do_redirect";
  public static final String PARAMETER_SaveCert = "save_cert";  
  public static final String PARAMETER_Saml2IdP = "saml2idp";
  public static final String PARAMETER_Saml2Disabled = "saml2";
  public static final String PARAMETER_Saml2Post = "saml2post";
  
  //Actions
  public static final String ACTION_JSecurityCheck = "j_security_check";
  public static final String ACTION_SAPJSecurityCheck = "sap_j_security_check";
  public static final String ACTION_SelfSubmit = "";
  
  
  private PageContext pageContext;
  private ServletRequest request;
  
  public LogonRequest(PageContext pageContext){
    this.pageContext = pageContext;
  }
  
  public LogonRequest(ServletRequest request){
    this.request = request;
  }
  
  private ServletRequest getRequest(){
    if(pageContext!=null){
      return pageContext.getRequest();
    }
    return request;
  }
  
  public boolean hasAttributeOrParameter(String name){    
    Object obj = getAttributeOrParameter(name);
    return obj!=null;
  }
  
  public Object getAttributeOrParameter(String name){
    Object obj = getAttribute(name);    
    if(obj==null){
      obj = getParameter(name);    
    }
    return obj;
  }
  
  public boolean hasAttribute(String name){    
    Object obj = getAttribute(name);
    return obj!=null;    
  }
  
  public Object getAttribute(String name){
    return getRequest().getAttribute(name);    
  }
  
  public boolean hasParameter(String name){    
    String obj = getParameter(name);
    return obj!=null;
  }
  
  public String getParameter(String name){
    return getRequest().getParameter(name);        
  }
  
  public boolean hasErrorBean(){
    ErrorBean obj = getErrorBean();    
    return obj!=null;
  }
  
  public ErrorBean getErrorBean(){
    if(pageContext!=null){
      return (ErrorBean)pageContext.getAttribute(ErrorBean.beanId, PageContext.REQUEST_SCOPE);
    }
    else{
      return null;
    }
  }
  
  public String getSelfRegistrationAlias(){
    String selfRegUrl = "/webdynpro/dispatcher/sap.com/tc~sec~ume~wd~enduser/SelfregApp?newWindowOpened=true";
    
    if(  hasAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationRequired)){

      String idpName = (String)getAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationIdP);
      selfRegUrl = selfRegUrl + "&" + LogonRequest.PARAMETER_SAML2Federation + "=true&" + LogonRequest.PARAMETER_Saml2IdP + "=" + idpName;

   }    
   
    return selfRegUrl;
  }
  
  public boolean isPostRequest(){
    try{
      HttpServletRequest request = (HttpServletRequest) getRequest();
      String method = request.getMethod();
      return (method != null) && method.equalsIgnoreCase("POST");       
    }
    catch(Exception e){
      myLoc.traceThrowableT(Severity.ERROR, "Failed to get http request method (get or post)", e);
    }
    return false;    
  }
  
  public boolean isIdPSelectionRequired(){
    return hasAttribute(ATTRIBUTE_IdPDiscoveryShowSelectionPage) && hasParameter(PARAMETER_ShowUidPasswordLogonPage);      
  }
  
}
