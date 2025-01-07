package com.sap.engine.applications.security.logon.tags;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.applications.security.logon.beans.ResourceBeanFactory;
import com.sap.engine.applications.security.logon.pages.Utils;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;

class BeanFactory {
	private static Locale getLocale(PageContext pageContext) {
		Locale locale = (Locale) pageContext.getAttribute(SAPMLogonServlet.SET_LANGUAGE_ACTION, PageContext.REQUEST_SCOPE);

    if (locale == null) {
      locale = pageContext.getRequest().getLocale();
    }
    
    return locale;
	}
	
	public static ResourceBean getLogonLabelBean(PageContext pageContext) {
		ResourceBean bean = (ResourceBean) pageContext.getAttribute(ResourceBeanFactory.LOGON_LABEL_BEAN_ID, PageContext.REQUEST_SCOPE);
		
		if (bean == null) {
			Locale locale = getLocale(pageContext);
			bean = ResourceBeanFactory.createLogonLabelBean(locale, pageContext.getServletContext());
			pageContext.setAttribute(ResourceBeanFactory.LOGON_LABEL_BEAN_ID, bean, PageContext.REQUEST_SCOPE);
		}
		
		return bean;
	}
	
	public static ResourceBean getLogonMessageBean(PageContext pageContext) {
		ResourceBean bean = (ResourceBean) pageContext.getAttribute(
				ResourceBeanFactory.LOGON_MESSAGE_BEAN_ID, PageContext.REQUEST_SCOPE);
		
		if (bean == null) {
			Locale locale = getLocale(pageContext);
			bean = ResourceBeanFactory.createLogonMessageBean(locale, pageContext.getServletContext());
			pageContext.setAttribute(ResourceBeanFactory.LOGON_MESSAGE_BEAN_ID, bean, PageContext.REQUEST_SCOPE);
		}
		
		return bean;
	}
	
	public static IAccessToLogic getProxy(PageContext pageContext) {
		return Utils.getProxy((HttpServletRequest) pageContext.getRequest());
	}
	
	public static boolean inPortal(PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		boolean inPortal = Utils.inPortal(request);
		return inPortal;
	}
	
	public static String getWebPath(PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		return Utils.getWebPath(request );
	}
	
}
