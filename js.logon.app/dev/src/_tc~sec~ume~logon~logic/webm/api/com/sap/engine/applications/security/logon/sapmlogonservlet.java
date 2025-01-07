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
package com.sap.engine.applications.security.logon;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.applications.security.logon.beans.ErrorBean;
import com.sap.engine.applications.security.logon.pages.HelpPageServlet;
import com.sap.engine.applications.security.logon.pages.LogonPageServlet;
import com.sap.engine.applications.security.logon.pages.LogonProblemPageServlet;
import com.sap.engine.applications.security.logon.pages.LogoutPageServlet;
import com.sap.engine.applications.security.logon.pages.PasswordChangePageServlet;
import com.sap.engine.applications.security.logon.pages.ResetPasswordPageServlet;
import com.sap.engine.applications.security.logon.pages.Utils;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.security.api.FeatureNotAvailableException;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.UserSearchFilter;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.security.core.util.imp.LogonUtils;
import com.sap.security.core.util.notification.SendMailAsynch;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sapmarkets.tpd.TradingPartnerDirectoryCommon;
import com.sapmarkets.tpd.master.PartnerID;
import com.sapmarkets.tpd.master.TradingPartnerInterface;

/**
 *
 */
public class SAPMLogonServlet extends HttpServlet {
  public final static String UID_PASSWORD_LOGON_ACTION = "uidPasswordLogon";
  public final static String SET_LANGUAGE_ACTION = "setLanguage";
  public final static String SHOW_CHANGE_PASSWORD_PAGE = "changePassword";
  public final static String SHOW_CHANGE_PASSWORD_ERROR_PAGE = "changePasswordError";
  public final static String CHANGE_PASSWORD_ACTION = "performChangePassword";
  public final static String LOGON_AGAIN_ACTION = "logonAgain";

  public final static String UID_PASSWORD_CLEAR_ACTION = "uidPasswordClear";
  public final static String SHOW_HELP_PAGE = "gotoHelpPage";
  public final static String SHOW_RESET_PASSWORD_PAGE = "gotoResetPasswordPage";
  public final static String HELP_TYPE_ACTION = "helpActionPage";
  public final static String RESET_PASSWORD_ACTION = "PASSWORD_RESET";
  public final static String LOGON_PROBLEM_ACTION = "LOGON_PROBLEM";
  public final static String RESET_PASSWORD_ACTION_2 = "forgotPassword";
  public final static String CANCEL_RESET_PASSWORD_ACTION = "cancelResetPassword";
  public final static String CANCEL_HELP_ACTON = "cancelHelp";
  public final static String CANCEL_LOGON_PROBLEM_ACTION = "cancelLogonProblem";
  public final static String CANCEL_PASSWORD_CHANGE_ACTION = "cancelPasswordChange";
  public final static String SHOW_UID_PASSWORD_LOGON_PAGE = "showUidPasswordLogonPage";
  public final static String SHOW_UID_PASSWORD_LOGON_ERROR_PAGE = "showUidPasswordErrorPage";
  private final static String SHOW_LOGOUT_PAGE = "showLogoutPage";
  public final static String LOGOUT_SUCCESS_STATUS = "isSuccessfulLogout";
  public final static String SHOW_POST_FORM_PAGE = "showPostFormPage";
  
  public final static String SUBMIT_HELP_ACTION = "submitHelpPage";
  public final static String RESET_PASSWORD_EVENT = "RPWFS";//PORTAL EVENT
  public final static String LOGON_PROBLEM_EVENT = "OLPFS";//PORTAL EVENT
  public final static String SUBMIT_HELP_EVENT = "HPFS";//PORTAL EVENT

  public final static String CANCEL_ON = "on";
  public final static String CANCEL_OFF = "off";
  
  private final static String SELF_REGISTRATION_PERMISSION = "SELF_REGISTRATION";
  private final static String LOGON_HELP_PERMISSION = "LOGON_HELP";
  private final static String REQUEST_ATTRIBUTE_URI = "javax.servlet.include.request_uri";
  
  private static Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
  private static boolean IS_SERVLET_23;
  
  static {
    Class _class = javax.servlet.http.HttpServletRequest.class;
    Class[] args = {String.class};

    try {
      _class.getMethod("setCharacterEncoding", args);
      IS_SERVLET_23 = true;
    } catch (NoSuchMethodException nsme) {
    	IS_SERVLET_23 = false;
      myLoc.warningT("doPost", "Servlet 2.3 not available, character encoding could not be set!");
    }
  } 
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (IS_SERVLET_23) {
      Object requestUriAttribute = request.getAttribute(REQUEST_ATTRIBUTE_URI);
      if (requestUriAttribute == null) {
        response.setContentType("text/html; charset=utf-8");
        request.setCharacterEncoding("UTF8");
      }
    }

    try {
      executeRequest(request, response);
    } catch (Exception e) {
      myLoc.traceThrowableT(Severity.ERROR, "doPost", "Fatal Logon error", e);
    }
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    doPost(request, response);
  }
  
  void executeRequest(HttpServletRequest request, HttpServletResponse response) {
    final String methodName = "executeRequest";
    if (myLoc.bePath()) {
    	myLoc.entering(methodName);
    }
    
    try {
      IAccessToLogic proxy = Utils.getProxy(request, response);
      Utils.initOverwritePortalCSS(this.getServletContext());
      
      // TODO: when is this used?
      if (proxy.isAction(SET_LANGUAGE_ACTION)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: set language");
        }
        setLanguage(request);
      }

      if (proxy.isAction("ume.logon.locale")) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: ume.logon.locale");
        }
        setLanguage(request);
        setLanguageCookie(request, response);
      }

      if (proxy.isAction(SHOW_CHANGE_PASSWORD_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: change password");
        }
        PasswordChangePageServlet.process(request, response, getServletContext());
        
      } else if (proxy.isAction(SHOW_HELP_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: goto help page");
        }
        HelpPageServlet.process(request, response, getServletContext());

      } else if (proxy.isAction(SHOW_RESET_PASSWORD_PAGE) || proxy.isAction(RESET_PASSWORD_ACTION)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: goto reset password page");
        }
        
        if (CANCEL_ON.equalsIgnoreCase(request.getParameter(CANCEL_HELP_ACTON))) {
        	LogonPageServlet.process(request, response, getServletContext());
        } else {
        	ResetPasswordPageServlet.process(request, response, getServletContext());
        }

      } else if (proxy.isAction(LOGON_PROBLEM_ACTION)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: help logon problem page");
        }
        
        if (CANCEL_ON.equalsIgnoreCase(request.getParameter(CANCEL_HELP_ACTON))) {
        	LogonPageServlet.process(request, response, getServletContext());
        } else {
        	LogonProblemPageServlet.process(request, response, getServletContext());
        }
        
      } else if (proxy.isAction(RESET_PASSWORD_EVENT) || proxy.isAction(RESET_PASSWORD_ACTION_2)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: perform reset password");
        }
        
        if (CANCEL_ON.equalsIgnoreCase(request.getParameter(CANCEL_RESET_PASSWORD_ACTION))) {
        	LogonPageServlet.process(request, response, getServletContext());
        } else {
	        // formsubmit on reset password page
	        performResetPassword(request, response);
        }        
      } else if (proxy.isAction(LOGON_PROBLEM_EVENT)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: perform logon problem");
        }
        
        if (CANCEL_ON.equalsIgnoreCase(request.getParameter(CANCEL_LOGON_PROBLEM_ACTION))) {
        	LogonPageServlet.process(request, response, getServletContext());
        } else {
	        // formsubmit on other logon problem page
	        performLogonProblem(request, response);
        }
        
      } else if (proxy.isAction(SHOW_UID_PASSWORD_LOGON_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: show uid password logon page");
        }
        LogonPageServlet.process(request, response, getServletContext());
        
      } else if (proxy.isAction(SHOW_UID_PASSWORD_LOGON_ERROR_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: show uid password error page");
        }
        showUidPasswordErrorPage(request, response);
        
      } else if (proxy.isAction(SHOW_CHANGE_PASSWORD_ERROR_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: change password error");
        }
        
        if (CANCEL_ON.equalsIgnoreCase(request.getParameter(CANCEL_PASSWORD_CHANGE_ACTION))) {
        	LogonPageServlet.process(request, response, getServletContext());
        } else {
        	showPasswordChangeErrorPage(request, response);
        }
        
      } else if (proxy.isAction(UID_PASSWORD_CLEAR_ACTION)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: clear uid password");
        }
        LogonPageServlet.process(request, response, getServletContext());
        
      } else if (proxy.isAction(SHOW_LOGOUT_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: show logout page");
        }
        
        LogoutPageServlet.process(request, response, getServletContext());
        
      } else if (proxy.isAction(SHOW_POST_FORM_PAGE)) {
        if (myLoc.beDebug()) {
          myLoc.debugT(methodName, "Found action: show post form page");
        }
        showPostFormPage(request, response);

      } else {
      	showUidPasswordErrorPage(request, response);
      }
    } catch (Exception e) {
      myLoc.traceThrowableT(Severity.ERROR, methodName, "Fatal Logon error", e);
    } finally {
      if (myLoc.bePath()) {
        myLoc.exiting(methodName);
      }
    }
  }
  
  private static void setLanguage(HttpServletRequest request) {
    final String methodName = "setLanguage";
    Locale locale = null; 
    String localeParam = request.getParameter("ume.logon.locale");

    if (localeParam != null) {
      StringTokenizer st = new StringTokenizer(localeParam, "_");
      String [] params = {"", "", ""};
      int counter = 0;
      
      while (st.hasMoreTokens()) {
        params[counter++] = st.nextToken();
        
        if (counter >= 3) {
          break;
        }
      }
      
      locale = new Locale(params[0], params[1], params[2]);
      
      if (myLoc.beDebug()) {
        myLoc.debugT(methodName, "Locale is obtained from request parameter \"ume.logon.locale\"");
      }
    }

    if (null == locale) {
      locale = request.getLocale();
      
      if (myLoc.beDebug()) {
        myLoc.debugT(methodName, "Locale is obtained from \"Accept-Language\" header");
      }
    }
    
    if (null != locale) {
    	request.setAttribute(SET_LANGUAGE_ACTION, locale);
      
      if (myLoc.beDebug()) {
        myLoc.debugT(methodName, "Set request attribute " + SET_LANGUAGE_ACTION + " to " + locale);
      }
    }
  }
  
  /**
   * sets the cookie "ume.logon.locale" with the chosen locale
   */
  private static void setLanguageCookie(HttpServletRequest request, HttpServletResponse response) {
    String locale = request.getParameter("ume.logon.locale");
    
    if (locale != null) {
      Cookie lc = new Cookie("ume.logon.locale", locale);
      // TODO: how setting cookie
      response.addCookie(lc);
      
      if (myLoc.beDebug()) {
        myLoc.debugT("setLanguageCookie", "sets the cookie \"ume.logon.locale\" with the chosen locale" + locale);
      }
    }
  }
  
  private void performLogonProblem(HttpServletRequest request, HttpServletResponse response) throws IOException, UMException, FeatureNotAvailableException, ServletException {
    final String methodname = "performLogonProblem";
  
    if (myLoc.bePath()) {
      myLoc.entering(methodname);
    }

    String longUid = request.getParameter(ILoginConstants.LOGON_USER_ID);

    if (longUid != null) {
     longUid = longUid.trim();
    }

    String email = request.getParameter("email");
    String lastName = request.getParameter("lastname");
    String firstName = request.getParameter("firstname");
    String noteToAdmin = request.getParameter("notetoadmin");
    IUser userFrom = null;

    try {
      userFrom = UMFactory.getUserFactory().getUserByLogonID(longUid);

      if (myLoc.beDebug()){
        myLoc.debugT(methodname, "Sending email to administrator from: " + longUid);
      }

      if (sendAdminEmail(userFrom, SendMailAsynch.USER_LOGON_PROBLEM_REQUEST, 
                         noteToAdmin)) {
        request.setAttribute(ErrorBean.beanId, 
            new ErrorBean("LOGON_PROBLEM_EMAIL_SENT"));
    
        if (myLoc.beDebug()) {
          myLoc.debugT(methodname, "Email sent to administrator");
        }
      }
    } catch (Exception ex) {
      if (myLoc.beDebug()) {
        myLoc.traceThrowableT(Severity.ERROR, methodname, "Failed to send email to administrator", ex);
      }
      
      // search for user base on name and email
      UserSearchFilter userSearchFilter = new UserSearchFilter();
      userSearchFilter.setLastName(lastName, ISearchAttribute.EQUALS_OPERATOR, false);
      userSearchFilter.setFirstName(firstName, ISearchAttribute.EQUALS_OPERATOR, false);
      userSearchFilter.setEmail(email, ISearchAttribute.EQUALS_OPERATOR, false);

      ISearchResult foundUsers = UMFactory.getUserFactory().searchUsers(userSearchFilter);

      if (foundUsers.size() == 1) {
        userFrom = UMFactory.getUserFactory().getUser((String)foundUsers.next());
      }
    
      if (sendAdminEmail(userFrom,
                          SendMailAsynch.USER_LOGON_PROBLEM_REQUEST,
                          noteToAdmin)) {
        request.setAttribute(ErrorBean.beanId, 
            new ErrorBean("LOGON_PROBLEM_EMAIL_SENT"));

        if (myLoc.beDebug()) {
          myLoc.debugT(methodname, "Email sent to administrator");
        }
      } else {
        request.setAttribute(ErrorBean.beanId,
            new ErrorBean("LOGON_PROBLEM_INFO_ERROR"));
            
        if (myLoc.beDebug()) {
          myLoc.debugT(methodname, "Unable to send email to administrator");
        }
        
        LogonPageServlet.process(request, response, getServletContext());
        
        if (myLoc.bePath()) {
          myLoc.exiting(methodname);
        }
    
        return;
      }
    }

    LogonPageServlet.process(request, response, getServletContext());
    
    if (myLoc.bePath()) {
      myLoc.exiting(methodname);
    }
  }
  
  private static boolean sendAdminEmail(IUser userFrom, String event, String message) {
    final String methodname = "sendEmail";
  
    if (myLoc.beDebug()){
      myLoc.debugT(methodname, "Sending email to administrator from: " + 
      		userFrom == null ? "unknown" : userFrom.getDisplayName());
    }
    
    try {
      TradingPartnerInterface company = null;
      
      if (userFrom != null && userFrom.getCompany() != null 
          && userFrom.getCompany().length() > 0) {
        company = TradingPartnerDirectoryCommon.getTPD().getPartner(
            PartnerID.instantiatePartnerID(userFrom.getCompany()));
      }
          
      if (company != null) {
        SendMailAsynch.generateEmailToAdminOnUMEvent(userFrom, company, event, message);
      } else {
        SendMailAsynch.generateEmailToAdminOnUMEvent(userFrom, event, message);
      }
                
      if (myLoc.beDebug()) {
        myLoc.debugT(methodname,
                     "Sent email: from " + userFrom.getDisplayName() 
                     + " to administrator of company: " + userFrom.getCompany());
      }
    } catch (Exception ex) {
      if (myLoc.beDebug()) {
        myLoc.traceThrowableT(Severity.ERROR, methodname, "No email sent.", ex);
      }
            
      return false;
    }

    return true;
  }

  private void performResetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    final String methodname = "performResetPassword";
    
    if (myLoc.bePath()) {
      myLoc.entering(methodname);
    }

    try {
      String longUid = request.getParameter(ILoginConstants.LOGON_USER_ID);

      if (longUid != null) {
        longUid = longUid.trim();
      }

      // check for email id
      String email = request.getParameter("email");
      String lastName = request.getParameter("lastname");
      String firstName = request.getParameter("firstname");
      IUser userFrom = UMFactory.getUserFactory().getUserByLogonID(longUid);

      if (email.equalsIgnoreCase(userFrom.getEmail()) 
            && firstName.equalsIgnoreCase(userFrom.getFirstName()) 
            && lastName.equalsIgnoreCase(userFrom.getLastName())) {
        // email matched, assign a new password and email to user
        //String newPass = PasswordGen.generate();
        String newPass = UMFactory.getSecurityPolicy().generatePassword();
        IUserAccount ua = 
            UMFactory.getUserAccountFactory().getUserAccountByLogonId(longUid);
        IUserAccount mua = 
            UMFactory.getUserAccountFactory().getMutableUserAccount(ua.getUniqueID());

        //                ua.prepare();
        mua.setPassword(newPass);
        mua.save();
        mua.commit();

        SendMailAsynch.generateEmailOnUMEvent(userFrom, userFrom,
            SendMailAsynch.USER_PASSWORD_RESET_PERFORMED, null, newPass);
        
        request.setAttribute(ErrorBean.beanId, 
            new ErrorBean("NEW_PSWD_ASSIGNED"));
    
        if (myLoc.beDebug()){
          myLoc.debugT(methodname,
                       "Send reset pswd email to " + userFrom.getDisplayName() 
                       + "/" + userFrom.getEmail() + "(" + newPass + ")");
          myLoc.debugT(methodname, "New password assigned...email to user");
        }
      } else {
        // email did not match, send message to administrator(s)
        sendAdminEmail(userFrom, SendMailAsynch.USER_PASSWORD_RESET_REQUEST, email);
        
        if (myLoc.beDebug()) {
          myLoc.debugT(methodname,
              "Sent error email: from " + userFrom.getDisplayName() 
              + " to administrator of company: " + userFrom.getCompany());
        }
        
        request.setAttribute(ErrorBean.beanId,
            new ErrorBean("RESET_PASSWORD_INFO_ERROR"));
    
        if (myLoc.beDebug()) {
          myLoc.debugT(methodname, "Error info entered...email to administrator");
        }
      }
    } catch (Exception ex) {
      request.setAttribute(ErrorBean.beanId,
          new ErrorBean("RESET_PASSWORD_INFO_ERROR"));
      myLoc.traceThrowableT(Severity.ERROR, methodname, "Error exception: ", ex);
    }

    ResetPasswordPageServlet.process(request, response, getServletContext());
    
    if (myLoc.bePath()) {
      myLoc.exiting(methodname);
    }
  }

  static ErrorBean createPwdChangeErrorBean(Exception ex) {
    byte reason = ((LoginExceptionDetails) ex).getExceptionCause();
    String msg = ex.getMessage();
    
    return createPwdChangeErrorBean(reason, msg);
  }
  
  private static ErrorBean createPwdChangeErrorBean(byte exceptionCause, String msg) {
    if (exceptionCause == LoginExceptionDetails.CHANGE_PASSWORD_TOO_SHORT) {
      return new ErrorBean(msg, new Integer(UMFactory.getSecurityPolicy().getPasswordMinLength()));
    } 
    
    if (exceptionCause == LoginExceptionDetails.CHANGE_PASSWORD_TOO_LONG) {
      return new ErrorBean(msg, new Integer(UMFactory.getSecurityPolicy().getPasswordMaxLength()));
    }
    
    if (exceptionCause == LoginExceptionDetails.CHANGE_PASSWORD_ALPHANUM_REQUIRED) {
      return new ErrorBean(msg,
          new Integer(UMFactory.getSecurityPolicy().getPasswordAlphaNumericRequired()));
    }
    
    if (exceptionCause == LoginExceptionDetails.CHANGE_PASSWORD_MIXED_CASE_REQUIRED) {
      return new ErrorBean(msg,
                new Integer(UMFactory.getSecurityPolicy().getPasswordMixCaseRequired()));
    }
    
    if (exceptionCause == LoginExceptionDetails.CHANGE_PASSWORD_SPEC_CHARS_REQUIRED) {
      return new ErrorBean(msg,
                new Integer(UMFactory.getSecurityPolicy().getPasswordSpecialCharRequired()));
    }
    
    if (exceptionCause == LoginExceptionDetails.WRONG_USERNAME_PASSWORD_COMBINATION) {
      return new ErrorBean("WRONG_OLD_PASSWORD");
    }
    
    return new ErrorBean(msg);
  }

  private void showUidPasswordErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    setErrorMessage(request);

    if (myLoc.beDebug()) {
      myLoc.debugT("showUidPasswordErrorPage", "Going to logon page");
    }
    
    LogonPageServlet.process(request, response, getServletContext());
  }

  private void showPasswordChangeErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    setErrorMessage(request);
    
    if (myLoc.beDebug()) {
      myLoc.debugT("changePasswordErrorAction", "Going to password change page");
    }
    
    PasswordChangePageServlet.process(request, response, getServletContext());
  }
  
  private void showPostFormPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (myLoc.beDebug()) {
      myLoc.debugT("showPostFormPage", "Going to post form page");
    }
    
    request.getRequestDispatcher(Utils.POST_FORM_PAGE).include(request, response);
  }

  private static void setErrorMessage(HttpServletRequest request) {
    String message = request.getParameter(LogonRequest.PARAMETER_ErrorMessage);

    if (message != null) {
      request.setAttribute(ErrorBean.beanId, new ErrorBean(message));
    }
  }
  
  public static boolean getSelfReg() {
    return checkPermission(SELF_REGISTRATION_PERMISSION);
  }

  public static boolean getAllowCertLogon() {
    IUMConfiguration umeConfig = InternalUMFactory.getConfiguration();
  	boolean isCertLogonAllowed = umeConfig.getBooleanDynamic(LogonUtils.ALLOW_CERT_LOGON, false);
  	
  	if (myLoc.beDebug()) {
  		myLoc.debugT("getAllowCertLogon", "isCertLogonAllowed=" + isCertLogonAllowed);
  	}
  	
    return isCertLogonAllowed;
  }

  public static boolean getLogonHelp() {
    return checkPermission(LOGON_HELP_PERMISSION);
  }

  public static boolean getPasswordReset() {
  	boolean isPasswordResetAllowed = UMFactory.getProperties().getBoolean(LogonUtils.UM_ADMIN_LOGON_PWD_RESET, true);
  	
  	if (myLoc.beDebug()) {
  		myLoc.debugT("getPasswordReset", "isPasswordResetAllowed=" + isPasswordResetAllowed);
  	}
  	
    return isPasswordResetAllowed;
  }
  
  public static String getLogonURL(String params) {
    String logonURL = ServletAccessToLogic.LOGON_SERVLET_ALIAS;
    
    String paramURL = null;
    
    if (!(params == null || params.equals(""))) {
      if (paramURL == null) {
        paramURL = params;
      } else {
        paramURL += "&" + params;
      }
    }
    
    if (paramURL != null) {
      if (logonURL.indexOf("?") >= 0) {
        logonURL = logonURL + "&" + paramURL;
      } else {
        logonURL = logonURL + "?" + paramURL;
      }
    }
    
    if (myLoc.beDebug()) {
      myLoc.debugT("getLogonURL", "logonURL = " + logonURL);
    }
    
    return logonURL;
  }
  
  public static String encode(String str) {
  	if (str == null) {
  		return null;
  	}
    return URLEncoder.encode(URLDecoder.decode(str));
  }
  
  private static boolean checkPermission(String permissionName) {
    final String METHOD = "checkPermission";
    
    boolean hasPermission = false;
    
    if (myLoc.bePath()) {
      myLoc.entering(METHOD, new Object[] {permissionName});
    }
    
    try {
	    IUser user = UMFactory.getAuthenticator().getLoggedInUser();
	
	    if (myLoc.beDebug()) {
	      myLoc.debugT(METHOD, "The current user is [{0}].", new Object [] {user});
	    }
	    
	    if (user != null) {
	      LogonApplicationPermission permission = new LogonApplicationPermission(permissionName); 
	      
	      hasPermission = user.hasPermission(permission);
	    } else {
	      myLoc.errorT(METHOD, "The current user is null.");
	    }
	    
	    return hasPermission;
    } finally {
      if (myLoc.bePath()) {
        myLoc.exiting(METHOD, new Boolean(hasPermission));
      }
    }
  }
}
