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
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.applications.security.logon.ServletAccessToLogic;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * 
 * @author Krasimira Velikova (i032162)
 */
public class Utils {
  
	static final String DEFAULT_LOGON_UI_ALIAS = "/logon_ui_resources";
	static final String PROPERTY_LOGON_UI_ALIAS = "ume.logon.application.ui_resources_alias"; 
	
	static final String CERT_LOGON_PAGE = "/certLogonPage.jsp";
	static final String LOGON_PAGE = "/logonPage.jsp";
	static final String PASSWORD_CHANGE_PAGE = "/changePasswordPage.jsp";
	static final String HELP_PAGE = "/helpPage.jsp";
	static final String RESET_PASSWORD_PAGE = "/resetPasswordPage.jsp";
	static final String LOGON_PROBLEM_PAGE = "/logonProblemPage.jsp";
	static final String LOGOFF_PAGE = "/logoffPage.jsp";
  static final String SAML2_PAGE = "/idpSelection.jsp";
  
	public static final String POST_FORM_PAGE = "/postLogin.jsp";
	
	private static final String LOGON_APP_REAL_PATH = "logon.application.real.path";

	private static Boolean OVERWRITE_PORTAL_CSS;
	private static final String OVERWRITE_PORTAL_CSS_PROPERTY = "overwrite_portal_css";

	private static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION); 

	
	public static IAccessToLogic getProxy(HttpServletRequest request, HttpServletResponse response) {
		IAccessToLogic proxy = (IAccessToLogic) request.getAttribute(IAccessToLogic.class.getName());
		
		if (proxy == null) {
			proxy = new ServletAccessToLogic(request, response); 
			request.setAttribute(IAccessToLogic.class.getName(), proxy);
		}
		
		return proxy;
	}

	public static IAccessToLogic getProxy(HttpServletRequest request) {
		IAccessToLogic proxy = (IAccessToLogic) request.getAttribute(IAccessToLogic.class.getName());
		
		return proxy;
	}

	public static boolean inPortal(HttpServletRequest request) {
		IAccessToLogic proxy = getProxy(request);
		
		if (proxy == null) {
			return false;
		}
		
		if (proxy instanceof ServletAccessToLogic) {
			return false;
		}

		return true;
	}
	
	public static String getWebPath(HttpServletRequest request) {
		String webpath = (String) request.getAttribute(LOGON_APP_REAL_PATH);
		
		if (webpath == null) {
			webpath = setWebPath(request);
		}
		
		return webpath;
	}
	
	public static String setWebPath(HttpServletRequest request) {
		if (WEB_PATH == null) {
			WEB_PATH = Utils.getLogonUIAlias() + "/";
		}
		
		request.setAttribute(LOGON_APP_REAL_PATH, WEB_PATH);
		
		
		return WEB_PATH;
	}
	
	public static void initOverwritePortalCSS(ServletContext context) {
		if (OVERWRITE_PORTAL_CSS != null) {
			return;
		}
		
		ServletContext uiServletcontext = context.getContext(Utils.getLogonUIAlias());
		InputStream in = uiServletcontext.getResourceAsStream("/WEB-INF/classes/ui.properties");
		
		if (in == null) {
			return;
		}
		
		PropertyResourceBundle bundle;
		
		try {
			bundle = new PropertyResourceBundle(in);
		} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to read ui.properties", e);
			return;
		}
		
		String value = bundle.getString(OVERWRITE_PORTAL_CSS_PROPERTY);
		
		if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
			OVERWRITE_PORTAL_CSS = Boolean.TRUE;
		} else {
			OVERWRITE_PORTAL_CSS = Boolean.FALSE;
		}
	}
	
	static boolean isOverwritePortalCSS() {
		if (OVERWRITE_PORTAL_CSS != null) {
			return OVERWRITE_PORTAL_CSS.booleanValue();
		}
		
		return false;
	}
	
	/**
	 * @return dynamic value for UME property "ume.logon.application.ui_resources_alias"
	 */
	public static String getLogonUIAlias(){
	  IUMConfiguration umeConfig = InternalUMFactory.getConfiguration();
    return umeConfig.getStringDynamic(PROPERTY_LOGON_UI_ALIAS, DEFAULT_LOGON_UI_ALIAS);
	}
	
	/**
	 * method that checks URL for potentially dangerous symbols for XSS attacks 
	 * in a URL string and replace
	 * them with their URL encoded representation.
	 * This method encodes ':' to protect from using external URL sites.
	 * @param taggedString is the string that will be escaped
	 * @return encoded string only for symbols '<', '>', '"', '\'' and ':'
	 */
	static public String escapeURL(String taggedString) {
		
		if( null == taggedString ) {
			return null;
		}


		Pattern pattern = Pattern.compile("[><\"':]");
		Matcher matcher = pattern.matcher(taggedString);
		
		if (matcher.find()) {
			int length = taggedString.length();
			StringBuffer result = new StringBuffer( Math.round( length * 1.1f ) );
			
			for ( int i = 0; i < length; ++i ) {
				char c = taggedString.charAt( i );
				switch ( c ) {
				case '<':
					result.append( "%3C" );
					break;
				case '>':
					result.append( "%3E" );
					break;
				case '\"':
					result.append( "%22" );
					break;
				case '\'':
					result.append( "%27" );
					break;
				case ':':
					result.append( "%3A" );
					break;
				default:
					result.append( c );
				}
			}
			return result.toString();
		} 
		return taggedString; 
	}
	
	private static String WEB_PATH;
}
