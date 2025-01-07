/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.applications.security.logon.beans;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * @author Krasimira Velikova
 * @version 1.0
 */
public class ResourceBeanFactory {
	
  public static final String LOGON_MESSAGE_BEAN_ID = "logonMessage";
  
  public static final String LOGON_LABEL_BEAN_ID = "logonLocale";

  private static final String LOGON_LABEL_BASE_NAME = "logonLabels";

  private static final Map<Locale, ResourceBean> RESOURCE_BEANS = new HashMap<Locale, ResourceBean>();
  

  public static final ResourceBean createLogonMessageBean(Locale locale, ServletContext context) {
    return getResourceBean(locale, LOGON_LABEL_BASE_NAME, context);
  }

  public static final ResourceBean createLogonMessageBean(ServletContext context) {
    return getResourceBean(null, LOGON_LABEL_BASE_NAME, context);
  }
	
  public static final ResourceBean createLogonLabelBean(Locale locale, ServletContext context) {
    return getResourceBean(locale, LOGON_LABEL_BASE_NAME, context);
  }

  public static final ResourceBean createLogonLabelBean(ServletContext context) {
    return getResourceBean(null, LOGON_LABEL_BASE_NAME, context);
  }
  
  private static synchronized final ResourceBean getResourceBean(Locale locale, String beanId, ServletContext context){
    Locale currLocale = locale;
    if (currLocale == null) {
      currLocale = Locale.getDefault();
    }
    
    ResourceBean resourceBean = RESOURCE_BEANS.get(currLocale);
    if(resourceBean==null){
      resourceBean = new ResourceBean(currLocale, beanId, context);
      RESOURCE_BEANS.put(currLocale, resourceBean);
    }
    
    return resourceBean;
    
  }
}
