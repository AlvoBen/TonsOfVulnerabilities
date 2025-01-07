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

import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * @author Krasimira Velikova (i032162)
 */
public class PasswordChangePageServlet extends HttpServlet {
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

	public static void process(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException, IOException {
		myLoc.entering("process");
		
		String webpath = Utils.setWebPath(request);

		boolean inPortal = Utils.inPortal(request);
		PrintWriter out = response.getWriter();

		//<%@ include file="logon_proxy.txt" %>
		CommonPageContent.writeLogonProxyContent(out, inPortal, webpath);		
		//END <%@ include file="logon_proxy.txt" %>
		
		CommonPageContent.writeHeadStartContent(out, request, response, inPortal, webpath);
		
		out.println("<script language=\"JavaScript\">");
		CommonPageContent.writeBasicJS(out);
		
		CommonPageContent.writeJSFunctionSetFocus(out, "changePasswordForm");

		out.println("function onClickCancel() {");
		out.print("  document.changePasswordForm.");
		out.print(ILoginConstants.NEW_PASSWORD);
		out.println(".value=\"\";");
		out.print("  document.changePasswordForm.");
		out.print(ILoginConstants.CONFIRM_PASSWORD);
		out.println(".value=\"\";");
		
		out.print("  document.changePasswordForm.");
		out.print(SAPMLogonServlet.CANCEL_PASSWORD_CHANGE_ACTION);
		out.print(".value=\"");
		out.print(SAPMLogonServlet.CANCEL_ON);
		out.println("\";");
		
		out.println("}");
		out.println("</script>");
		
		if (!inPortal) {
			out.println("</head>");
		}
		
		CommonPageContent.writeUMLogonTopArea(out, inPortal);
		CommonPageContent.writeUIPage(context, request, response, Utils.PASSWORD_CHANGE_PAGE);
		CommonPageContent.writeUMLogonBottomArea(out, inPortal);
		
		myLoc.exiting("process");
	}
}
