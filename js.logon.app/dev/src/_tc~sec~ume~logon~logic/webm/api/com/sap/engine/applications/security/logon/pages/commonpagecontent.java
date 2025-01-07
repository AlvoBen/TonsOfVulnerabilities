/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.applications.security.logon.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.ServletAccessToLogic;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.applications.security.logon.beans.ResourceBeanFactory;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.security.api.UMException;
import com.sap.security.core.imp.TenantFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Provides methods for writing common content which is written in every page. 
 * 
 * @author Krasimira Velikova (i032162)
 */
class CommonPageContent {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	static void writeLogonProxyContent(PrintWriter out, boolean inPortal, String webpath) {
		out.println("<script>");
		out.print("  var inPortalScript = ");
		out.println(inPortal);
		out.print("  var webpath = \"");
		out.print(webpath);
		out.println("\"");
		out.println("</script>");
		out.println();
	}
	
	static void writeUMLogonTopArea(PrintWriter out, boolean inPortal) {
		//<%@ include file="/umLogonTopArea.txt"%>
		if (inPortal) {
			out.println("<span id=\"UMELogon\">");
			out.println("<script language=\"JavaScript\">");
			out.println("if( window.EPCM != null ) {");
		  out.println("  EPCM.subscribeEvent( \"urn:com.sapportals.portal:browser\", \"load\", setFocusToFirstField );");
		  out.println("}");
		  out.println("</script>");
		} else {
			out.println("<body class=\"urBdyStd\" bgcolor=\"#F7F9FB\" onLoad=\"setFocusToFirstField()\" onUnload=\"restoreWindow()\">");
		} 
		//END OF <%@ include file="/umLogonTopArea.txt"%>
	}
	
	static void writeUMLogonTopAreaLogout(PrintWriter out, boolean inPortal) {
		//<%@ include file="/umLogonTopArea.txt"%>
		if (inPortal) {
			out.println("<span id=\"UMELogon\">");
		} else {
			out.println("<body class=\"urBdyStd\" bgcolor=\"#F7F9FB\" onUnload=\"restoreWindow()\">");
		} 
		//END OF <%@ include file="/umLogonTopArea.txt"%>
	}
	
	static void writeUMLogonBottomArea(PrintWriter out, boolean inPortal) {
		if (inPortal) { 
			out.println("</span>");
		} else {
			out.println("</body>");
			out.println("</html>");
		}
	}
	
	static void writeBasicJS(PrintWriter out) {
		out.println("function putFocus(formInst, elementInst) {");
		out.println("  if (document.forms.length > 0) {");
		out.println("    document.forms[formInst].elements[elementInst].focus();");
		out.println("  }");
		out.println("}");
		out.println("");
		 
		out.flush();
	}
	
	static void writeHeadStartContent(PrintWriter out, HttpServletRequest request, 
			HttpServletResponse response, boolean inPortal, String webpath) throws ServletException {
		
		if (!inPortal) {
			out.println("<html>");
			out.println("<head>");
			out.println("<BASE target=\"_self\">");
		}
		
		if (!inPortal || Utils.isOverwritePortalCSS()) {
			out.print("<link rel=stylesheet href=\"");
			
			try {
				String logonBrandStyle = TenantFactory.getInstance().getLogonBrandingStyle(request);
				String url = ServletAccessToLogic.getAbsoluteURL(webpath, logonBrandStyle);
				out.print(url);
			} catch (UMException e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to get logon brand style", e);
				throw new ServletException(e);
			}
			
			out.println("\">");
		}
		
		if (!inPortal) {
			
			out.println("<title>User Management, SAP AG</title>");
			
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-cache");
			response.setHeader("expires", "0");
		} 
		
		out.println("<script language=\"javascript\">");
		out.println("var originWindowName=window.name;");
		out.println("window.name=\"logonAppPage\";");

		out.println("function restoreWindow() {");
		out.println("try{");
		out.println("window.name=originWindowName;");    
		out.println("} catch(ex){}");
		out.println("}");		
		out.println("</script>");
	}
	
  static void initBeans(HttpServletRequest request, ServletContext context) {
    final String methodName = "initBeans";
    
    Locale locale = (Locale) request.getAttribute(SAPMLogonServlet.SET_LANGUAGE_ACTION);

    if (locale == null) {
      locale = request.getLocale();
      
      if (myLoc.beDebug()) {
        myLoc.debugT(methodName, "Get locale from request");
      }
    }
    
    if (myLoc.beDebug()) {
      myLoc.debugT(methodName, "Locale is " + locale);
    }
      
    ResourceBean localeBean = (ResourceBean) request.getAttribute(ResourceBeanFactory.LOGON_LABEL_BEAN_ID);

    if (localeBean == null || !locale.equals(localeBean.getLocale())) {
    	localeBean = ResourceBeanFactory.createLogonLabelBean(locale, context); 
      
    	request.setAttribute(ResourceBeanFactory.LOGON_LABEL_BEAN_ID, localeBean);

      request.setAttribute(
      		ResourceBeanFactory.LOGON_MESSAGE_BEAN_ID, 
      		ResourceBeanFactory.createLogonMessageBean(locale, context));
    
      if (myLoc.beInfo()) {
        myLoc.infoT(methodName, "LogonLocaleBean and LogonMessageBean created");
      }
    }

    request.setAttribute(ResourceBeanFactory.LOGON_LABEL_BEAN_ID, localeBean);
    
    if (myLoc.beInfo()){
      myLoc.infoT(methodName, "LanguagesBean created");
    }
  }
  
  static void writeUIPage(ServletContext context, HttpServletRequest request, HttpServletResponse response, String page) {
		try {
			ServletContext customContext = context.getContext(Utils.getLogonUIAlias());		
			RequestDispatcher reqDisp = customContext.getRequestDispatcher(page);
			CommonPageContent.initBeans(request, customContext);
			reqDisp.include(request, response);
		} catch (ServletException sex) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to include " + page + " from " + Utils.getLogonUIAlias(), sex);
		} catch (IOException ioex) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to include " + page + " from " + Utils.getLogonUIAlias(), ioex);
		}
  	
  }
  
  static void writeHelpPagesOnCancelClickJS(PrintWriter out, String cancelId) {
		out.println("function onClickCancel() {");
		out.print("  document.helpForm.");
		out.print(cancelId);
		out.print(".value=\"");
		out.print(SAPMLogonServlet.CANCEL_ON);
		out.println("\";");
		out.println("}");
  }
  
  static final void writeJSFunctionSetFocus(PrintWriter out, String formName){
    out.println("function setFocusToFirstField() {");
    out.println(" myform = document." + formName + ";");
    out.println(" try{");
    out.println("   for (i=0; i<myform.length; i++) {");
    out.println("    elem = myform.elements[i];");
    out.println("    if (!elem.disabled) {");
    out.println("      elemType = elem.type;");
    out.println("      if (elemType==\"text\" || elemType==\"password\") {");
    out.println("       if (!elem.readOnly) {");    
    out.println("          elem.focus();");
    out.println("          break;");
    out.println("       }");
    out.println("      }");
    out.println("      if (elemType==\"select-one\" || elemType==\"select-multiple\" || elemType==\"checkbox\" || elemType==\"radio\") {");
    out.println("        elem.focus();");
    out.println("        break;");
    out.println("      }");
    out.println("    }");
    out.println("   }");
    out.println(" } catch(ex){");
    out.println(" }");
    out.println("}");    
  }
}

