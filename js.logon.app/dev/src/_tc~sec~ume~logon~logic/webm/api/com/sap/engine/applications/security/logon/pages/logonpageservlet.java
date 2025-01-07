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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.imp.TenantFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.security.core.util.taglib.EncodeHtmlTag;

/**
 * 
 * @author Krasimira Velikova (i032162)
 */
public class LogonPageServlet extends HttpServlet {
	private static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp, getServletContext());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request, response, getServletContext());
	}

	public static void process(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws IOException, ServletException {
		myLoc.entering("process");
		
		if ("https".equalsIgnoreCase(request.getScheme())) {
			processHttps(request, response, context);
			return;
		}
		
		String webpath = Utils.setWebPath(request);

		IAccessToLogic proxy = Utils.getProxy(request, response);
		boolean inPortal = Utils.inPortal(request);
		PrintWriter out = response.getWriter();
		
		LogonRequest logonRequest = new LogonRequest(request);
		boolean isIdPSelectionRequired = logonRequest.isIdPSelectionRequired();
		
		CommonPageContent.writeLogonProxyContent(out, inPortal, webpath);
		
		
		CommonPageContent.writeHeadStartContent(out, request, response, inPortal, webpath);

		out.println("<script language=\"JavaScript\">");
		CommonPageContent.writeBasicJS(out);
		
	  out.println("function clearEntries() {");
		out.println("  document.logonForm.longUid.value=\"\";");
		out.println("  document.logonForm.password.value=\"\";");
	  out.println("}");
		  
    if(isIdPSelectionRequired){
      CommonPageContent.writeJSFunctionSetFocus(out, "discoverIdPForm");
    }
    else{
      CommonPageContent.writeJSFunctionSetFocus(out, "logonForm");
    }
	
	  out.println("function addTenantPrefix() {");
		
	  String tenantPrefix = null;
		
		try {
			tenantPrefix = TenantFactory.getInstance().getTenantLogonPrefix(request);
		} catch (UMException e) {
			myLoc.traceThrowableT(Severity.DEBUG, "Failed to get tennant logon prefix", e);
		}
	
		if (!"".equals(tenantPrefix))	{
			tenantPrefix = EncodeHtmlTag.encode(tenantPrefix);
			out.print("var userlogonid = document.logonForm.");
			out.print(ILoginConstants.LOGON_USER_ID);
			out.println(".value;");
			out.print("if (userlogonid.toLowerCase().indexOf(\"");
			out.print(tenantPrefix);
			out.println("\".toLowerCase()) != 0) {");
			out.print("document.logonForm.");
			out.print(ILoginConstants.LOGON_USER_ID);
			out.print(".value = \"");
			out.print(tenantPrefix);
			out.println("\" + userlogonid;");
			out.println("}");
		}
	
		out.println("  return true;");
		out.println("}");
	
		out.println("</script>");
		
		if( !inPortal ) {
			out.println("</head>");
		}
	
		CommonPageContent.writeUMLogonTopArea(out, inPortal);
				
		if(isIdPSelectionRequired){
		  CommonPageContent.writeUIPage(context, request, response, Utils.SAML2_PAGE);
		}
		else{
		  CommonPageContent.writeUIPage(context, request, response, Utils.LOGON_PAGE);
		}
		
		CommonPageContent.writeUMLogonBottomArea(out, inPortal);
		
		myLoc.exiting("process");
	}

	public static void processHttps(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws IOException, ServletException {
		myLoc.entering("processHttps");
		String webpath = Utils.setWebPath(request);
		
		boolean inPortal = Utils.inPortal(request);
		PrintWriter out = response.getWriter();
		
		LogonRequest logonRequest = new LogonRequest(request);
    boolean isIdPSelectionRequired = logonRequest.isIdPSelectionRequired();
		
		CommonPageContent.writeLogonProxyContent(out, inPortal, webpath);
		 
		CommonPageContent.writeHeadStartContent(out, request, response, inPortal, webpath);
		
		out.println("<script language=\"JavaScript\">");
		CommonPageContent.writeBasicJS(out);
		
		if(isIdPSelectionRequired){
		  CommonPageContent.writeJSFunctionSetFocus(out, "discoverIdPForm");
		}
		else{
		  CommonPageContent.writeJSFunctionSetFocus(out, "certLogonForm");
		}
		
	  out.println("</script>");
	  
	  String stop = (String) request.getAttribute("STOP");
	  
	  if (stop != null) {
	    request.removeAttribute("STOP");
	  }

	  CommonPageContent.writeUMLogonTopArea(out, inPortal);
	  	  
    if(isIdPSelectionRequired){
      CommonPageContent.writeUIPage(context, request, response, Utils.SAML2_PAGE);
    }
    else{
      CommonPageContent.writeUIPage(context, request, response, Utils.CERT_LOGON_PAGE);
    }
    	  
	  CommonPageContent.writeUMLogonBottomArea(out, inPortal);
	  
	  myLoc.exiting("processHttps");
	}
	
}
