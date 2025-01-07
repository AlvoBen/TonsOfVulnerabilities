/**
 * Copyright (c) 2008 by SAP Labs Bulgaria, url: http://www.sap.com All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG, Walldorf. You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered into with SAP.
 * 
 * Created on Jun 18, 2008 by I032049
 * 
 */

package com.sap.engine.interfaces.security.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sap.engine.interfaces.security.SecurityContext;

import com.sap.engine.lib.xml.util.BASE64Decoder;

import com.sap.engine.lib.security.PasswordChangeCallback;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSecureSession;
import com.sap.engine.lib.security.http.HttpSetterCallback;

import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.api.util.IUMParameters;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Callback handler that does all common work.
 * 
 * The AbstractWebCallbackHandler class handles general types of callbacks and contains the common callback handler logic. Only the specific
 * functionality is left to be implemented by its subclasses - for example the navigation to resources.
 * 
 * @author NW F SIM Runtime BG
 * @version 7.1
 */
public abstract class AbstractWebCallbackHandler implements CallbackHandler {

  /**
   * The value of the request secure action property stored in the HTTP request.
   */
  public static final String CHANGE_SCHEMA_HTTPS = "change_schema_https";

  /**
   * The default authentication template value that is used in applications
   */
  public static final String DEFAULT_AUTH_TEMPLATE = "ticket";

  /**
   * The default character encoding used by web container.
   */
  public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  /**
   * The key to the property for error page of the web application.
   */
  public static final String FORM_ERROR_PAGE = "form_error_page";

  /**
   * The key to the property for login page of the web application.
   */
  public static final String FORM_LOGIN_PAGE = "form_login_page";

  /**
   * The key to the authentication method that is used when the application does not specify its own specific value.
   */
  public static final String GLOBAL_AUTH_METHOD = "ume.login.auth_method";

  /**
   * The key to the name of the authentication stack that is used when the application does not specify its own specific value.
   */
  public static final String GLOBAL_AUTH_TEMPLATE = "ume.login.context";

  /**
   * The global login page alias of the web application.
   */
  public static final String GLOBAL_FORM_LOGIN_PAGE = "/logonServlet?showUidPasswordLogonPage";

  /**
   * The global login error page alias of the web application.
   */
  public static final String GLOBAL_FORM_ERROR_PAGE = "/logonServlet?showUidPasswordErrorPage";

  /**
   * The global logout page alias of the web application.
   */
  public static final String GLOBAL_LOGOUT_PAGE = "/logonServlet?showLogoutPage";

  /**
   * The global password change page alias of the web application.
   */
  public static final String GLOBAL_PASSWORD_CHANGE_LOGIN_PAGE = "/logonServlet?changePassword";

  /**
   * The global password change error page alias of the web application.
   */
  public static final String GLOBAL_PASSWORD_CHANGE_ERROR_PAGE = "/logonServlet?changePasswordError";

  /**
   * The global post form page alias of the web application.
   */
  public static final String GLOBAL_POST_FORM_PAGE = "/logonServlet?showPostFormPage";

  /**
   * The name of the attribute which stores the initial post parameters data
   */
  public static final String INITIAL_POST_PARAMETERS = "sap.com/initial_post_parameters";

  /**
   * The name of the attribute which stores the login post parameters data
   */
  public static final String LOGIN_POST_PARAMETERS = "sap.com/login_post_parameters";

  /**
   * The name of the cookie that stores the original URL string
   */
  public static final String ORIGINAL_URL_COOKIE_NAME = "com.sap.engine.security.authentication.original_application_url";

  /**
   * The key to the property for password change error page of the web application.
   */
  public static final String PASSWORD_CHANGE_ERROR_PAGE = "password_change_error_page";

  /**
   * The key to the property for password change page of the web application.
   */
  public static final String PASSWORD_CHANGE_LOGIN_PAGE = "password_change_login_page";

  /**
   * The name of the checkbox in the login application used to activate the certificate enrollment application
   */
  public static final String RAAPP_CHECKBOX_NAME = "createcert";

  /**
   * The context of the certificate enrollment application
   */
  public static final String RAAPP_CONTEXT = "/enrollapp";

  /**
   * The name of the URL parameter that stores the original URL of the service passed to the certificate enrollment application.
   */
  public static final String RAAPP_OLDSERVICE = "OLDSERVICE";

  /**
   * The startup page of the certificate enrollment application
   */
  public static final String RAAPP_START_PAGE = "/genreq.jsp";

  /**
   * The default encoding set to the HTTP request
   */
  public static final String REQUEST_INPUT_ENCODING = "UTF-8";

  /**
   * The key of the request secure action property stored in the HTTP request.
   */
  public static final String REQUEST_SECURE_ACTION = "com.sap.engine.security.servlet.request.secure_action";

  /**
   * The action value of the login form in the logon application
   */
  protected static final String J_SECURITY_CHECK = "j_security_check";

  /**
   * The action value of the password change form in the logon application
   */
  protected static final String SAP_J_SECURITY_CHECK = "sap_j_security_check";

  /**
   * The username field used in the old logon application of the portal - kept for compatibility reasons.
   */
  protected static final String J_USER = "j_user";

  /**
   * The password field of the login form
   */
  protected static final String PASSWORD = "j_password";

  /**
   * The username field of the login form
   */
  protected static final String USERNAME = "j_username";

  /**
   * The Location used for tracing in the callback handlers
   */
  protected static final Location LOCATION = Location.getLocation(AuthenticationTraces.CALLBACK_HANDLER_LOCATION);

  /**
   * The key for original URL attribute stored in session after successful authentication
   */
  public static final String ORIGINAL_URL_ATTRIBUTE_NAME = "com.sap.security.original_url";

  /**
   * The key to the property for post login page of the web application.
   */
  protected static final String POST_FORM_PAGE = "post_form_page";

  /**
   * The String value for method delimiter used in the request
   */
  protected static final String REQUEST_METHOD_DELIMITER = "#";

  /**
   * Request attribute sent by SAML login module, in order to ask the client to choose one of the IdPs contained in the attribute value. The request
   * attribute value is of type String[].
   */
  public static final String IDP_DISCOVERY_REQUEST_ATTRIBUTE = "sap.com/idp_discovery";

  /**
   * This request attribute is used when the IdP selection list page should be displayed to the client and indicates if a link to login locally should
   * be displayed.
   */
  protected static final String IDP_DISCOVERY_SHOW_LOCAL_LOGIN_PAGE = "sap.com/idp_discovery_show_local_login_page";

  /**
   * This request attribute is used to notify the logon application that the IdP selection page should be displayed.
   */
  protected static final String IDP_DISCOVERY_SHOW_IDP_SELECTION_PAGE = "sap.com/idp_discovery_show_idp_selection_page";

  /**
   * The name of the cancel button used for password change
   */
  protected static final String SHOW_UID_PASSWORD_LOGON_PAGE = "showUidPasswordLogonPage";

  /**
   * The name of GET method
   */
  protected static final String GET_METHOD = "GET";

  /**
   * The name of POST method
   */
  protected static final String POST_METHOD = "POST";

  /**
   * The default value for the authentication type returned by the web container when the application does not specify its own specific value.
   */
  private static final String APPLICATION_AUTH_TYPE = "APPLICATION";

  private static final String AUTH_METHOD = "auth_method";

  private static final String BASIC_AUTH_METHOD = "Basic";

  /**
   * The name of confirm password field of the password change form
   */
  private static final String CONFIRM_PASSWORD = "j_sap_again";

  /**
   * The name of current password field of the password change form
   */
  private static final String CURRENT_PASSWORD = "j_sap_current_password";

  /**
   * The name of password field of the password change form
   */
  protected static final String NEW_PASSWORD = "j_sap_password";

  private static final String CLIENT_CERTIFICATE_CLASS = "javax.servlet.request.X509Certificate";

  /**
   * The value of an activated 'createcert' checkbox in the login form
   */
  private static final String CREATE_CERTIFICATE_ON = "on";

  /**
   * The default authentication method for applications
   */
  private static final String DEFAULT_AUTH_METHOD = "form";

  private static final String EMPTY_STRING_VALUE = "";

  /**
   * The parameter name string used to attach error messages to an URL
   */
  private static final String ERROR_PARAMETER = "error_message=";

  private static final String HEADER_AUTHORIZATION = "Authorization";

  private static final String HEADER_NEGOTIATE = "Negotiate";

  private static final String HEADER_NTLM_TOKEN = "NTLMSSP";

  /**
   * The name of MYSAPSSO2 cookie
   */
  private static final String MYSAPSSO2_COOKIE_NAME = "MYSAPSSO2";

  /**
   * The name of the authentication property for policy domain
   */
  private static final String POLICY_DOMAIN = "policy_domain";

  private static final char SEPARATOR_CHAR = ':';

  private static final char SPACE_CHAR = ' ';

  /**
   * Determines if the client can be redirected to login or error page
   */
  protected boolean canRedirectToLoginPage;

  /**
   * <code>URLCipher</code> object used to encode and decode the original URL value when it is saved in HTTP Cookie and restored from it
   */
  protected static URLCipher cipher;

  /**
   * Determines if there is pending password change request
   */
  protected boolean hasPasswordChange;

  /**
   * <code>HttpServletRequest</code> object provided by the container
   */
  protected HttpServletRequest request;

  /**
   * <code>HttpServletResponse</code> object provided by the container
   */
  protected HttpServletResponse response;

  /**
   * Current security context object
   */
  protected SecurityContext securityContext;

  /**
   * Determines if response to the client should be sent or not
   */
  protected boolean sendResponse = true;

  /**
   * The username of the current user
   */
  protected String userName = null;

  /**
   * The authentication type of the application
   */
  private String authType;

  /**
   * Contains all cookies from the request stored with their names in UpperCase.
   */
  private Map cookies = null;

  private String errorMessage = null;

  /**
   * The global authentication properties that are valid for all applications that do not set explicitly own specific values. The following properties
   * are contained here: FORM_LOGIN_PAGE, FORM_ERROR_PAGE, PASSWORD_CHANGE_LOGIN_PAGE, PASSWORD_CHANGE_ERROR_PAGE, GLOBAL_AUTH_METHOD,
   * GLOBAL_AUTH_TEMPLATE
   */
  private static Properties globalProperties = null;

  private boolean isConfirmPassword;

  private static boolean isInitialized = false;

  private static String logonApplicationAlias = "/logon_app";

  private static String logonURL;

  private char[] password = null;

  private static SecurityContext rootSecCtx;

  private String securityPolicyDomain;

  /**
   * Instantiates new <code>AbstractWebCallbackHandler</code> with the provided request and response objects
   * 
   * @param request HttpServletRequest object
   * @param response HttpServletResponse object
   */
  protected AbstractWebCallbackHandler( HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;

    securityContext = rootSecCtx;

    try {
      this.request.setCharacterEncoding(REQUEST_INPUT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      LOCATION.traceThrowableT(Severity.WARNING, "AbstractWebCallbackHandler", "Cannot set request character encoding.", e);
    }

    parseCookies();

    String appConfigId = null;
    SecurityRequest securityRequest = getSecurityRequest(request);
    if (securityRequest != null) {
      appConfigId = (String) (securityRequest.getServletContext().getAttribute(HttpSecureSession.POLICY_CONFIGURATION_NAME));
    } else {
      appConfigId = (String) request.getSession(true).getServletContext().getAttribute(HttpSecureSession.POLICY_CONFIGURATION_NAME);
    }

    if (appConfigId != null) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("AbstractWebCallbackHandler", "Policy configuration name: {0}", new Object[] { appConfigId });
      }

      securityContext = securityContext.getPolicyConfigurationContext(appConfigId);
    }

    securityPolicyDomain = CallbackHandlerHelper.getAuthenticationContextProperty(securityContext, POLICY_DOMAIN);
  }

  /**
   * Instantiates new <code>AbstractWebCallbackHandler</code> with the provided request and response objects and sendResponse parameter that defines
   * whether response to client should be sent
   * 
   * @param request HttpServletRequest object
   * @param response HttpServletResponse object
   * @param sendResponse boolean that define if response to the client should be sent
   */
  protected AbstractWebCallbackHandler( HttpServletRequest request, HttpServletResponse response, boolean sendResponse) {
    this(request, response);
    this.sendResponse = sendResponse;
  }

  /**
   * Returns the Internet Protocol (IP) address of the client that sent the request.
   * 
   * @return a String containing the IP address
   */
  public String getIpAddress() {
    return request.getRemoteAddr();
  }

  /**
   * Returns the logon application alias
   * 
   * @return a String value containing the alias
   */
  public static String getLogonApplicationAlias() {
    return logonApplicationAlias;
  }

  /**
   * @deprecated Extracts the original URL from the request
   */
  static public String getOriginalURL(HttpServletRequest request) {
    final String METHOD_NAME = "getOriginalURL";
    LOCATION.entering(METHOD_NAME);

    try {

      return getOriginalURLDecryptedFromCookie(request);

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  public static boolean isOriginalURLSet(HttpServletRequest request) {
    final String METHOD_NAME = "isOriginalURLSet";
    LOCATION.entering(METHOD_NAME);

    try {
      return getOriginalURLEncryptedFromCookie(request) != null;
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * @deprecated Returns the policy domain of the application
   * 
   * @return a String containing the security policy domain
   */
  public String getSecurityPolicyDomain() {
    return securityPolicyDomain;
  }

  /**
   * Retrieves or displays the information requested in the provided array of <code>Callback</code> objects.
   * 
   * @param callbacks an array of Callback objects information for which should be retrieved or displayed.
   * 
   * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
   * @throws IOException If an input or output error occurs.
   * @throws UnsupportedCallbackException - if the handler does not support one or more of the Callbacks specified in the callbacks parameter.
   */
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    final String METHOD_NAME = "handle";

    LOCATION.entering(METHOD_NAME);

    try {
      for (int i = 0; i < callbacks.length; i++) {
        Callback callback = callbacks[i];

        if (LOCATION.beDebug()) {
          LOCATION.debugT("handle", "Handle {0}", new Object[] { callback });
        }

        if (callback == null) {
          continue;
        }

        handle(callback, callbacks);
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Initialize the callback handler. This method is called by the security service.
   * 
   * @param context the root security context for the application
   * @param loginFormAction alias to the logon servlet
   * @param globalProperties this parameter is not used
   * @param logonApplicationAlias logon alias of the application
   */
  public static void initialize(SecurityContext ctx, String loginFormAction, Properties globalProperties, String logonApplicationAlias) {
    final String METHOD_NAME = "initialize";

    LOCATION.entering(METHOD_NAME);

    try {
      if (!isInitialized) {
        setRootSecurityContext(ctx);
        setLogonUrl(loginFormAction);
        setLogonApplicationAlias(logonApplicationAlias);
        setGlobalProperties();

        cipher = URLCipher.getInstance();
        isInitialized = true;

        if (LOCATION.beDebug()) {
          LOCATION.debugT("AbstractWebCallbackHandler is initialized.");
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("AbstractWebCallbackHandler is already initialized.");
        }
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Attaches error message as a parameter in the URL
   * 
   * @param url String containing the original URL
   * @return a String containing the URL with attached error message
   */
  protected String attachErrorMessageInURL(String url) {
    final String METHOD_NAME = "attachErrorMessageInURL";

    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME);
    }
    try {
      if (errorMessage != null) {
        try {
          if (url.indexOf("?") >= 0) {
            url = url + "&" + ERROR_PARAMETER + URLEncoder.encode((errorMessage), REQUEST_INPUT_ENCODING);
          } else {
            url = url + "?" + ERROR_PARAMETER + URLEncoder.encode((errorMessage), REQUEST_INPUT_ENCODING);
          }
        } catch (UnsupportedEncodingException e) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("attachErrorMessage", "Failed to encode the error message: {0}", new Object[] { errorMessage });
            LOCATION.traceThrowableT(Severity.WARNING, "Failed to encode the error message due to unsupported encoding.", e);
          }
        }

        if (LOCATION.beDebug()) {
          LOCATION.debugT("attachErrorMessage", "url: {0}", new Object[] { url });
        }
      }

      return url;
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Clears the cookie for original page URL.
   */
  protected void clearOriginalPageURL() {
    final String METHOD_NAME = "clearOriginalPageURL";

    LOCATION.entering(METHOD_NAME);
    try {
      Cookie oldOriginalUrlCookie = getCookie(ORIGINAL_URL_COOKIE_NAME);
      if (oldOriginalUrlCookie != null) {
        // Creates new original url cookie which is expired.
        // So with the next request the browser will not set it.

        Cookie newOriginalUrlCookie = new Cookie(ORIGINAL_URL_COOKIE_NAME, EMPTY_STRING_VALUE);
        newOriginalUrlCookie.setPath(oldOriginalUrlCookie.getPath());
        newOriginalUrlCookie.setMaxAge(0);
        response.addCookie(newOriginalUrlCookie);
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "New original URL cookie with name {0}, value {1}, for path {2}, max age 0 is set in the response.",
              new Object[] { ORIGINAL_URL_COOKIE_NAME, EMPTY_STRING_VALUE, oldOriginalUrlCookie.getPath() });
        }
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Forwards to the certificate enrollment application
   */
  abstract protected void forwardToCertificateEnrollment();

  /**
   * Forwards to the logon error page of the application
   */
  abstract protected void forwardToFormErrorPage() throws IOException;

  /**
   * Forwards to the logon page of the application
   */
  abstract protected void forwardToFormLoginPage() throws IOException;

  /**
   * Forwards either to the login or login error page of the application. The method is called in case of failed authentication.
   */
  abstract protected void forwardToLoginOrErrorPage(boolean showErrorOnLogonPage) throws IOException;

  /**
   * Forwards to the original page requested in the application
   */
  abstract protected void forwardToOriginalPage() throws IOException;

  /**
   * Forwards to the password change error page of the application
   */
  abstract protected void forwardToPasswordChangeErrorPage() throws IOException;

  /**
   * Forwards to the password change page of the application
   */
  abstract protected void forwardToPasswordChangePage() throws IOException;

  /**
   * Returns the authentication type of the application.
   * 
   * @return a String containing the authentication type
   */
  protected String getAuthType() {
    final String METHOD_NAME = "getAuthType";

    LOCATION.entering(METHOD_NAME);

    try {

      if (authType == null) {
        authType = request.getAuthType();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Authentication type of the request is {0} ", new Object[] { authType });
        }

        // The web container returns "APPLICATION" auth type when there is no auth
        // method for the corresponding application. When there is no authentication context at
        // all, then null is returned.
        if (APPLICATION_AUTH_TYPE.equalsIgnoreCase(authType)) {
          authType = null;
        }

        if (authType == null) {
          authType = securityContext.getAuthenticationContext().getProperty(AUTH_METHOD);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Authentication method from authentication context: {0}", new Object[] { authType });
          }

          if ((authType == null) || authType.equals(EMPTY_STRING_VALUE)) {
            IUMParameters umeConfig = UMFactory.getProperties();
            authType = umeConfig.getDynamic(ILoginConstants.GLOBAL_AUTH_METHOD, DEFAULT_AUTH_METHOD);
            if (LOCATION.beDebug()) {
              LOCATION.debugT(METHOD_NAME, "Use default authentication method: {0}", new Object[] { authType });
            }
          }
        }
      }

      return authType;
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Returns the decrypted original URL stored in the cookie.
   * 
   * @param request HttpServletRequest object
   * @return a String containing the decrypted value of the original URL
   */
  protected static String getOriginalURLDecryptedFromCookie(HttpServletRequest request) {
    final String METHOD_NAME = "getOriginalURLDecryptedFromCookie";

    LOCATION.entering(METHOD_NAME);

    try {
      String originalURLEncrypted = getOriginalURLEncryptedFromCookie(request);
      if (originalURLEncrypted == null) {
        return null;
      }

      String url = originalURLEncrypted.substring(originalURLEncrypted.indexOf(REQUEST_METHOD_DELIMITER) + 1);
      try {
        url = cipher.decryptURL(url);
      } catch (Exception e) {
        LOCATION.errorT("Cannot decrypt original URL: " + url);
        return null;
      }
      return url;
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Returns the encrypted original URL value stored in the cookie
   * 
   * @param request HttpServletRequest object
   * @return a String value containing the encrypted original URL if found in the request, null otherwise
   */
  protected static String getOriginalURLEncryptedFromCookie(HttpServletRequest request) {
    final String METHOD_NAME = "getOriginalURLEncryptedFromCookie";

    LOCATION.entering(METHOD_NAME);

    try {
      Cookie[] cookies = request.getCookies();
      if (cookies == null || cookies.length == 0) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("No cookies found in request.");
        }
        return null;
      }

      for (Cookie cookie : cookies) {
        if (AbstractWebCallbackHandler.ORIGINAL_URL_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
          // the cookie value is <METHOD>#<ENCRYPTED REQUEST URL>
          return cookie.getValue();
        }
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Cookie {0} not found in request.", new Object[] { AbstractWebCallbackHandler.ORIGINAL_URL_COOKIE_NAME });
      }
      return null;

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Returns authentication context page property for login page, error page, password change page, etc.
   * 
   * @param propName String containing the name of the page property
   * @return a String value of the page property
   */
  protected String getProperty(String propName) {
    final String METHOD_NAME = "getProperty";

    LOCATION.entering(METHOD_NAME);

    try {
      String str = null;

      if ((FORM_LOGIN_PAGE.equals(propName)) && isIdPDiscovery()) {
        LOCATION.debugT(METHOD_NAME, "Get the default login page because IdP selection has been requested.");

        str = logonURL + AbstractWebCallbackHandler.globalProperties.getProperty(FORM_LOGIN_PAGE);
      } else {
        str = CallbackHandlerHelper.getAuthenticationContextProperty(securityContext, propName);

        if (str == null) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("getProperty", "Does not have own {0} property, will use the global one.", new Object[] { propName });
          }

          str = logonURL + AbstractWebCallbackHandler.globalProperties.getProperty(propName);
        }
      }

      if (LOCATION.beDebug()) {
        LOCATION.debugT("getProperty", "{0} = {1}", new Object[] { propName, str });
      }

      return str;
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Use method handle(Callback[]) instead
   * 
   * @deprecated Handles a <code>Callback</code> object from a list of <code>Callback</code> objects
   */
  protected void handle(Callback callback, Callback[] allCallbacks) throws IOException, UnsupportedCallbackException {
    final String METHOD_NAME = "handle(Callback, Callback[])";

    if (callback instanceof HttpGetterCallback) {
      handle((HttpGetterCallback) callback);

    } else if (callback instanceof HttpSetterCallback) {
      handle((HttpSetterCallback) callback);

    } else if (callback instanceof PasswordChangeCallback) {
      handle((PasswordChangeCallback) callback);

    } else if (callback instanceof PasswordCallback) {
      checkForPasswordChange(allCallbacks);
      handle((PasswordCallback) callback);

    } else if (callback instanceof NameCallback) {
      checkForPasswordChange(allCallbacks);
      handle((NameCallback) callback);

    } else if (callback instanceof AuthStateCallback) {
      handle((AuthStateCallback) callback);

    } else if (callback instanceof TextOutputCallback) {
      handle((TextOutputCallback) callback);
    } else if (callback instanceof LanguageCallback) {
      handle((LanguageCallback) callback);
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Not supported callback: {0}", new Object[] { callback });
      }
      throw new UnsupportedCallbackException(callback);
    }
  }

  /**
   * Determines true if this is the first request to password change page
   * 
   * @return true if this is the initial password change request, false otherwise
   */
  abstract protected boolean isInitialPasswordChangePageRequest();

  /**
   * Saves the original page URL as a response header.
   */
  protected void saveOriginalPageURL() {
    final String METHOD_NAME = "saveOriginalPageURL";

    LOCATION.entering(METHOD_NAME);

    try {
      String originalURLFromCookie = getOriginalURLDecryptedFromCookie(request);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Original Page URL Cookie is currently stored as : {0}", new Object[] { originalURLFromCookie });
      }

      String originalURI = CallbackHandlerHelper.getOriginalURI(request);
      //This check refers to the cases when requestURI ends with either J_SECURITY_CHECK or SAP_J_SECURITY_CHECK
      if (originalURI.endsWith(J_SECURITY_CHECK)) {
        LOCATION.debugT(METHOD_NAME, "Original URL will not be saved. J_SECURITY_CHECK/SAP_J_SECURITY_CHECK is already set.");
        return;
      }

      String originalURLFromRequest = null;
      try {
        originalURLFromRequest = CallbackHandlerHelper.getOriginalUrlFromRequest(request);
      } catch (MalformedURLException e) {
        if (LOCATION.beDebug()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Cannot get original page URL from request.", e);
        }
      }

      if (originalURLFromCookie != null && originalURLFromCookie.equals(originalURLFromRequest)) {
        LOCATION.debugT(METHOD_NAME, "Original Page URL Cookie will not be changed. It is equal to current URL.");
        return;
      }

      // here starts the construction of the url cookie
      String urlCookieValue = request.getMethod() + REQUEST_METHOD_DELIMITER + cipher.encryptURL(originalURLFromRequest);

      StringBuilder header = new StringBuilder();
      header.append(ORIGINAL_URL_COOKIE_NAME);
      header.append("=" + urlCookieValue);
      header.append(";Path=" + CallbackHandlerHelper.getOriginalContextPath(request));
      header.append(";HttpOnly");

      response.addHeader("Set-Cookie", header.toString());
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Original Page URL Cookie for url = {0} is set to {1}",
            new Object[] { originalURLFromRequest, header.toString() });
      }

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected boolean isIdPDiscovery() {
    return (request.getAttribute(IDP_DISCOVERY_REQUEST_ATTRIBUTE) != null && (!request.getRequestURI().endsWith(J_SECURITY_CHECK) || (getOriginalURLDecryptedFromCookie(request) == null)));
  }

  /**
   * Sends error code in the response to the client
   * 
   * @param errorCode HTTP status code
   * @throws IOException If an input or output error occurs.
   */
  protected void sendError(int errorCode) throws IOException {
    final String METHOD_NAME = "sendError";

    LOCATION.entering(METHOD_NAME);

    try {
      if (sendResponse) {
        if (!response.isCommitted()) {
          response.sendError(errorCode);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Send error {0}", new Object[] { errorCode });
          }
        } else {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Cannot send error {0} because response is already committed", new Object[] { errorCode });
          }
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Cannot send error {0} because callback handler does not send response", new Object[] { errorCode });
        }
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Checks if there is PasswordChangeCallback provided
   */
  private void checkForPasswordChange(Callback[] callback) {
    if (hasPasswordChange) {
      return;
    }

    for (int k = 0; k < callback.length; k++) {
      if (callback[k] instanceof PasswordChangeCallback) {
        this.hasPasswordChange = true;
        LOCATION.debugT("checkForPasswordChange", "Found PasswordChangeCallback");
        break;
      }
    }
  }

  /**
   * Forwards to certificate enrollment application in case that it is activated. Otherwise forwards to the original page.
   */
  private void forwardToOriginalPageOrEnrollApp() throws IOException {
    final String METHOD_NAME = "goToOriginalPage";

    LOCATION.entering(METHOD_NAME);

    try {
      if (isCertificateEnrollmentActivated()) {
        forwardToCertificateEnrollment();
      } else {
        forwardToOriginalPage();
      }

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  private void forwardToPasswordChangeOrErrorPage() throws IOException {
    if (isInitialPasswordChangePageRequest()) {
      forwardToPasswordChangePage();
    } else {
      forwardToPasswordChangeErrorPage();
    }
  }

  /**
   * Returns the value of attribute stored in the HTTP session
   */
  private Object getAttributeFromSession(String name) {
    final String METHOD_NAME = "getSessionAttribute";

    if (name == null) {
      LOCATION.debugT(METHOD_NAME, "Name is null");
      return null;
    }

    HttpSession httpSession = request.getSession(false);

    if (httpSession != null) {
    if (httpSession instanceof HttpSecureSession) {
      Object ret = ((HttpSecureSession) httpSession).getSecurityAttribute(name);
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Security attribute {0} is {1}", new Object[] { name, ret });
      }
      return ret;
    }

    Object ret = httpSession.getAttribute(name);
    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Session attribute {0} is {1}", new Object[] { name, ret });
    }
    return ret;
    } else {
      return null;
    }
  }

  /**
   * Returns HTTP Cookie value stored in the current instance
   */
  private Cookie getCookie(String name) {
    // Search for a cookie by cookie name in UPPER CASE
    // The name parameter can be in any Upper or Lower Case
    Cookie cookie = null;
    if (name != null) {
      String cookieName = name.toUpperCase();
      // $JL-I18N$
      // This jlin test checks for possible problems concerning internationalization.
      // In this case the variable 'cookieName' is used internally as a constant - key for a cookie instance.
      // This does not lead to internationalization issues.
      cookie = (Cookie) this.cookies.get(cookieName);
    }
    return cookie;
  }

  private byte[] getInitialPostDataDecoded() {
    byte[] postData = null;
    String initialPostData = request.getParameter(INITIAL_POST_PARAMETERS);
    if (initialPostData != null) {
      // decode initial POST data parameter
      try {
        postData = BASE64Decoder.decode(initialPostData.getBytes());
        // $JL-I18N$
      } catch (Exception e) {
        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Decode of initial POST parameters data failed", e);
        }
      }
    }
    return postData;
  }

  /**
   * Extracts security request object from a servlet request and returns it
   */
  private SecurityRequest getSecurityRequest(ServletRequest request) {
    SecurityRequest securityRequest = null;
    if (request instanceof SecurityRequest) {
      securityRequest = (SecurityRequest) request;
    } else {
      while (request instanceof ServletRequestWrapper) {
        request = ((ServletRequestWrapper) request).getRequest();
        if (request instanceof SecurityRequest) {
          securityRequest = (SecurityRequest) request;
          break;
        }
      }
    }
    if (securityRequest == null) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Cannot extract security request object from servlet request");
      }
    }
    return securityRequest;
  }

  private void handle(AuthStateCallback callback) throws IOException {
    final String METHOD_NAME = "handleAuthStateCallback(AuthStateCallback)";

    String originalUrl = null;
    if (callback == AuthStateCallback.PASSED) {
      originalUrl = getOriginalPageURL();
      this.request.getSession(true).setAttribute(ORIGINAL_URL_ATTRIBUTE_NAME, originalUrl);
      this.request.setAttribute(ORIGINAL_URL_ATTRIBUTE_NAME, originalUrl);
    }

    // sendResponse is set to false only in handle(HttpSetterCallback) method in
    // case of BODY callback and in SAPJ2EEAuthenticator logon() and getLoggedInUser() methods
    if (sendResponse) {
      if (!response.isCommitted()) {
        if (callback == AuthStateCallback.PASSED) {

          // the same in the two handlers
          setAttributeInSession(USERNAME, null);

          // remove HTTP request attribute for special login POST parameters
          if (request.getAttribute(LOGIN_POST_PARAMETERS) != null) {
            request.removeAttribute(LOGIN_POST_PARAMETERS);
            if (LOCATION.beDebug()) {
              LOCATION.debugT(METHOD_NAME, "Request parameter removed: {0}", new Object[] { LOGIN_POST_PARAMETERS });
            }
          }

          if (POST_METHOD.equalsIgnoreCase(request.getMethod())) {
            // preserve POST parameters

            String uri = request.getRequestURI();
            //This check refers to the cases when requestURI ends with either J_SECURITY_CHECK or SAP_J_SECURITY_CHECK
            boolean isSelfSubmit = !(uri.endsWith(J_SECURITY_CHECK));
            if (LOCATION.beDebug()) {
              LOCATION.debugT(METHOD_NAME, "Is self submit case: {0}", new Object[] { isSelfSubmit });
            }

            if (isCustomLogonPageUsed()) {
              if (!isSelfSubmit) {
                String originalRequestMethod = getOriginalRequestMethod(request);
                if (POST_METHOD.equals(originalRequestMethod)) {
                  String url = CallbackHandlerHelper.getRelativeURL(originalUrl);

                  request.getSession(true).setAttribute(SecurityRequest.ORIGINAL_URL, url);
                  if (LOCATION.beDebug()) {
                    LOCATION.debugT(METHOD_NAME, "Store relative original url in request session attribute: {0}", new Object[] { url });
                  }
                }
              }
            } else {
              // case "SAP logon page"

              if (isSelfSubmit) {
                // case "self submit application and portal"
                byte[] initialPostData = getInitialPostDataDecoded();
                if (initialPostData != null) {
                  // decode parameter and restore POST parameters in HTTP request
                  SecurityRequest securityRequest = getSecurityRequest(request);
                  if (securityRequest != null) {
                    securityRequest.restorePostDataBytes(initialPostData);

                    LOCATION.debugT(METHOD_NAME, "Restore POST parameters from byte array.");

                  } else {
                    if (LOCATION.beWarning()) {
                      LOCATION.warningT(METHOD_NAME, "Cannot restore POST parameters data in HTTP request");
                    }
                  }

                } else {
                  // case "session prolongation"
                  // no parameters are stored so no restore will be done
                  LOCATION.debugT(METHOD_NAME, "No POST parameters are stored in request, no restore will be done.");
                }
              } else {
                // case "submit to J_SECURITY_CHECK"
                byte[] initialPostData = getInitialPostDataDecoded();
                if (initialPostData != null) {
                  // decode parameter and store POST parameters in HTTP session
                  SecurityRequest securityRequest = getSecurityRequest(request);
                  if (securityRequest != null) {
                    securityRequest.storePostDataBytes(initialPostData);
                    LOCATION.debugT(METHOD_NAME, "Store POST parameters from byte array.");

                  } else {
                    if (LOCATION.beWarning()) {
                      LOCATION.warningT(METHOD_NAME, "Cannot store POST parameters data in HTTP session");
                    }
                  }
                  
                  String originalRequestMethod = getOriginalRequestMethod(request);
                  if (POST_METHOD.equals(originalRequestMethod)) {
                    String url = CallbackHandlerHelper.getRelativeURL(originalUrl);
                    request.getSession(true).setAttribute(SecurityRequest.ORIGINAL_URL, url);

                    if (LOCATION.beDebug()) {
                      LOCATION.debugT(METHOD_NAME, "Store relative original url in request session attribute: {0}", new Object[] { url });
                    }
                  }                
                }
              }
            }
          }

          forwardToOriginalPageOrEnrollApp();
        } else if (callback == AuthStateCallback.FAILED) {

          // preserve POST parameters in case of custom logon page
          if (isCustomLogonPageUsed()) {
            // web container stores the POST parameters from HTTP request to Runtime session model
            storePostParameters(request);
          } else {
            // nothing to do
            // GET request or the POST parameters are stored in the SAP logon page
          }

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "hasPasswordChange = {0}", new Object[] { hasPasswordChange });
          }

          /**
           * 1. Authentication failed. Incorrect credentials have been submitted.
           * 
           * 2. When the user has to change his password the Portal navigates to the password change view containing 3 input fields - old password,
           * new password, confirm new password. After this form is submitted the old password is again checked and if it is incorrect
           * AuthStateCallback.FAILED is sent. In this case the user should be again forwarded to the password change page containing the
           * corresponding error message.
           */
          if (hasPasswordChange) {
            forwardToPasswordChangeErrorPage();
          } else {
            removeNTLMToken();
            boolean showErrorOnLogonPage = callback.getShowErrorOnLogonPage();

            if (isIdPDiscovery()) {
              LOCATION.debugT(METHOD_NAME, "SAML2 login module requested IdP selection list to be shown to the client.");

              request.setAttribute(IDP_DISCOVERY_SHOW_IDP_SELECTION_PAGE, true);

              if (canRedirectToLoginPage) {
                request.setAttribute(IDP_DISCOVERY_SHOW_LOCAL_LOGIN_PAGE, true);
                if (LOCATION.beDebug()) {
                  LOCATION.debugT(METHOD_NAME, "Link to local login should be displayed in the IdP selection page.");
                }
              }
            }

            forwardToLoginOrErrorPage(showErrorOnLogonPage);
          }

        } else if (callback == AuthStateCallback.PASSWORD_CHANGE_FAILED) {

          /**
           * The authentication is successful but the user has to change his password. If the cancel button of the password change page is pressed,
           * then the user should be forwarded to the login page.
           */
          boolean isCancel = request.getParameter(SHOW_UID_PASSWORD_LOGON_PAGE) != null;

          if (isCancel) {
            // user name should not be stored in the session
        	setAttributeInSession(USERNAME, null);  
            forwardToFormLoginPage();
          } else {
            removeNTLMToken();
            saveUserNameInSession();
            forwardToPasswordChangeOrErrorPage();
          }
        }
      } else {
        if (LOCATION.beInfo()) {
          LOCATION.infoT(METHOD_NAME, "The response is commited. No redirect is possible.");
        }
      }
    } else {
      if (LOCATION.beInfo()) {
        LOCATION.infoT(METHOD_NAME, "The callback handler does not send response. No redirect is possible.");
      }
    }
  }

  private void handle(HttpGetterCallback getterCallback) throws IOException {
    final String METHOD_NAME = "handle(HttpGetterCallback)";

    if (request == null) {

      LOCATION.debugT(METHOD_NAME, "Request is null");

      getterCallback.setValue(null);
      return;
    }

    switch (getterCallback.getType()) {
      case HttpCallback.HEADER: {
        String name = getterCallback.getName();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get header {0}", new Object[] { name });
        }

        getterCallback.setValue(request.getHeader(name));

        if (LOCATION.beDebug()) {
          if ((name != null) && !name.equalsIgnoreCase(MYSAPSSO2_COOKIE_NAME)) {
            LOCATION.debugT(METHOD_NAME, "Set value to {0}", new Object[] { request.getHeader(name) });
          } else {
            LOCATION.debugT(METHOD_NAME, "Header retrieved.");
          }
        }
        break;
      }

      case HttpCallback.COOKIE: {
        String name = getterCallback.getName();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get cookie {0}", new Object[] { name });
        }

        Cookie cookie = getCookie(name);

        if (cookie != null) {
          getterCallback.setValue(cookie.getValue());

          if (LOCATION.beDebug()) {
            if ((name != null) && !name.equalsIgnoreCase(MYSAPSSO2_COOKIE_NAME)) {
              LOCATION.debugT(METHOD_NAME, "Set value to {0}", new Object[] { cookie.getValue() });
            } else {
              LOCATION.debugT(METHOD_NAME, "Cookie retrieved.");
            }
          }
        } else {
          getterCallback.setValue(null);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Cookie {0} is not found", new Object[] { name });
          }
        }
        break;
      }

      case HttpCallback.REQUEST_PARAMETER: {
        String name = getterCallback.getName();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get request parameter {0}", new Object[] { name });
        }

        if (name != null) {
          String[] names = request.getParameterValues(name);
          getterCallback.setValue(names);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Set value to " + names);
          }
        }
        break;
      }

      case HttpCallback.CERTIFICATE: {
        Object cert = request.getAttribute(CLIENT_CERTIFICATE_CLASS);
        getterCallback.setValue(cert);

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get certificate from request: {0}", new Object[] { cert });
        }
        break;
      }

      case HttpCallback.CLIENT_IP: {
        getterCallback.setValue(request.getRemoteAddr());

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get client IP: {0}", new Object[] { request.getRemoteAddr() });
        }
        break;
      }

      case HttpCallback.METHOD_TYPE: {
        getterCallback.setValue(request.getMethod());

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get method type: {0}", new Object[] { request.getMethod() });
        }
        break;
      }

      case HttpCallback.IS_SECURE: {
        getterCallback.setValue(new Boolean(request.isSecure()));

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get is secure {0}", new Object[] { request.isSecure() });
        }
        break;
      }

      case HttpCallback.BODY: {
        getterCallback.setValue(request.getInputStream());
        LOCATION.debugT(METHOD_NAME, "Set input stream to request body as value");
        break;
      }

      case HttpCallback.SESSION_ATTRIBUTE: {
        String attributeName = getterCallback.getName();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get session attribute {0}", new Object[] { attributeName });
        }

        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
          Object attribute = httpSession.getAttribute(attributeName);
          getterCallback.setValue(attribute);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Set value to {0}", new Object[] { attribute });
          }
        }

        break;
      }

      case HttpCallback.ALL_SESSION_ATTRIBUTES: {
        LOCATION.debugT(METHOD_NAME, "Get all session attributes names");

        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
          Vector v = new Vector(15);
          Enumeration e = httpSession.getAttributeNames();

          while (e != null && e.hasMoreElements()) {
            v.add(e.nextElement());
          }

          getterCallback.setValue(v.toArray(new String[0]));
          LOCATION.debugT(METHOD_NAME, "Get all session attributes names");
        }

        break;
      }

      case HttpCallback.REQUEST_ATTRIBUTE: {
        String name = getterCallback.getName();

        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Get request attribute {0}", new Object[] { name });
        }

        if (name != null) {
          Object attr = request.getAttribute(name);
          getterCallback.setValue(attr);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Set value to {0}", new Object[] { attr });
          }
        }
        break;
      }

      case HttpCallback.BODY_READER: {
        LOCATION.debugT(METHOD_NAME, "Get body reader");

        getterCallback.setValue(request.getReader());
        break;
      }

      case HttpCallback.REQUEST_URL: {
        LOCATION.debugT(METHOD_NAME, "Get request url");

        String currentUrl = CallbackHandlerHelper.getOriginalUrlFromRequest(request);
        getterCallback.setValue(currentUrl);
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Set request url as value: {0}", new Object[] { currentUrl });
        }
        break;
      }

      case HttpCallback.POST_PARAMETERS: {
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Store post parameters");
        }
        storePostParameters(request);
        break;
      }
      
      case HttpCallback.RUNTIME_MODEL_DATA: {
        SecurityRequest securityRequest = getSecurityRequest(request);
        if (securityRequest != null) {
          String name = getterCallback.getName();
          Object data = securityRequest.getDataFromSessionRuntime(name);
          getterCallback.setValue(data);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Get data {0} from Runtime session model ", new Object [] { name });
          }
        } else {
          LOCATION.errorT("Get data from runtime session failed: cannot extract security request object from servlet request.");
        }
        break;
      }

	  case HttpCallback.DECRYPT_STRING: {
        Object encryptedString = getterCallback.getValue();
        if (encryptedString instanceof String) {
          String decryptedString = cipher.decryptURL((String) encryptedString);
          getterCallback.setValue(decryptedString);
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Encrypted string [{0}] successfully decrypted to plain string [{1}].", 
                new Object[] {encryptedString, decryptedString});
          }
        } else {
          LOCATION.errorT("Value of callback is not instance of String [{0}] and could not be decrypted.", 
              new Object[] {encryptedString});
        }
        break;
      }
      
      default: {
        throw new IllegalArgumentException("Getter callback type '" + getterCallback.getType() + "' not yet supported.");
      }
    }
  }

  private void handle(HttpSetterCallback setterCallback) throws IOException {
    final String methodName = "handle(HttpSetterCallback)";

    if (response == null) {
      LOCATION.debugT(methodName, "Response is null");
      return;
    }

    switch (setterCallback.getType()) {

      case HttpCallback.HEADER: {

        String headerName = setterCallback.getName();
        Object value = setterCallback.getValue();
        if (value instanceof String) {
          String headerValue = (String) value;

          if (response.isCommitted()) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "Response is already committed. Header with name {0} and value {1} will not be added.", new Object[] {
                  headerName, headerValue });
            }
          } else {
            if (headerName != null && headerValue != null) {
              this.response.addHeader(headerName, headerValue);

              if (LOCATION.beDebug()) {
                if (!headerValue.startsWith(MYSAPSSO2_COOKIE_NAME)) {
                  LOCATION.debugT(methodName, "Added header {0} with value {1}", new Object[] { headerName, headerValue });
                } else {
                  LOCATION.debugT(methodName, "Added header {0}", new Object[] { headerName });
                }
              }
            }
          }
        } else {
          if (value != null) {
            if (LOCATION.beWarning()) {
              LOCATION.warningT(methodName, "The value of HEADER callback with name {0} is not of type String: {1}. Header will not be added.", new Object[] {
                  headerName, value });
            }
          } else {
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "The value of HEADER callback with name {0} is null. Header will not be added.", new Object[] {
                  headerName });
            }
          }
        }
        break;
      }

      case HttpCallback.COOKIE: {
        String name = setterCallback.getName();
        if (response.isCommitted()) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(methodName, "Response is already committed. Cookie with name {0} will not be added.", new Object[] { name });
          }
        } else {

          if (name != null) {
            Object value = setterCallback.getValue();
            if ((value == null) || (value instanceof String)) {
              Cookie cookie = new Cookie(name, (String) value);
              this.response.addCookie(cookie);
  
              if (LOCATION.beDebug()) {
                if (!name.equalsIgnoreCase(MYSAPSSO2_COOKIE_NAME)) {
                  LOCATION.debugT(methodName, "Added cookie {0} with value {1}", new Object[] { name, setterCallback.getValue() });
                } else {
                  LOCATION.debugT(methodName, "Added cookie {0}", new Object[] { name });
                }
              }
            } else {
              if (LOCATION.beWarning()) {
                LOCATION.warningT(methodName, "The value of COOKIE callback with name {0} is not of type String: {1}. Cookie will not be added.", new Object[] { name, value });
              }
            }             
          }
        }
        break;
      }

      case HttpCallback.RESPONSE_CODE: {
        Object value = setterCallback.getValue();
        if (value instanceof String) {
          int status = Integer.parseInt((String) value);
          if (response.isCommitted()) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "Response is already committed. Status {0} cannot be set.", new Object[] { status });
            }
          } else {
            response.setStatus(status);
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "Set response code to {0}", new Object[] { status });
            }
          }
        } else {
          if (LOCATION.beWarning()) {
            LOCATION.warningT(methodName, "The value of the RESPONSE_CODE callback is not of type String or it is null: {0}. Response code will not be set.", new Object[] { value });
          }          
        } 
        break;
      }

      case HttpCallback.BODY: {
        if (!response.isCommitted()) {
          Object value = setterCallback.getValue();
          if (value instanceof String) {
            String body = (String) value;
            response.getWriter().print(body);
            response.getWriter().flush();
            response.getWriter().close();
            sendResponse = false;
            LOCATION.debugT(methodName, "Set response body");
          } else {
            if (LOCATION.beWarning()) {
              LOCATION.warningT(methodName, "The value of the BODY callback is not of type String or it is null: {0}. Response body will not be set.", new Object[] { value });
            }
          } 
        } else {
          LOCATION.debugT(methodName, "Cannot set response body because response is already committed");
        }
        break;
      }

      case HttpCallback.SESSION_ATTRIBUTE: {
        String attributeName = setterCallback.getName();

        if (attributeName != null) {
          Object attributeValue = setterCallback.getValue();
          this.request.getSession(true).setAttribute(attributeName, attributeValue);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(methodName, "Set session attribute {0} to {1}", new Object[] { attributeName, attributeValue });
          }
        }
        break;
      }

      case HttpCallback.REMOVE_SESSION_ATTRIBUTE: {
        String attributeName = setterCallback.getName();
        this.request.getSession(true).removeAttribute(attributeName);

        if (LOCATION.beDebug()) {
          LOCATION.debugT(methodName, "Removed session attribute {0}", new Object[] { attributeName });
        }
        break;
      }

      case HttpCallback.REQUEST_ATTRIBUTE: {
        String attributeName = setterCallback.getName();

        if (attributeName != null) {
          Object attributeValue = setterCallback.getValue();
          this.request.setAttribute(attributeName, attributeValue);

          if (LOCATION.beDebug()) {
            LOCATION.debugT(methodName, "Request attribute {0} is set in the request. ", new Object[] { attributeName });
          }
        }
        break;
      }

      case HttpCallback.SET_HEADER: {
        String headerName = setterCallback.getName();
        Object value = setterCallback.getValue();
        if (value instanceof String) {
          String headerValue = (String) value;
          if (response.isCommitted()) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "Response is already committed. Header with name {0} and value {1} will not be set.", new Object[] {
                  headerName, headerValue });
            }
          } else {
            this.response.setHeader(headerName, headerValue);

            if (LOCATION.beDebug()) {
              if ((headerValue != null) && !headerValue.startsWith(MYSAPSSO2_COOKIE_NAME)) {
                LOCATION.debugT(methodName, "Set header {0} to {1}", new Object[] { headerName, headerValue });
              } else {
                LOCATION.debugT(methodName, "Set header {0}", new Object[] { headerName });
              }
            }
          }
        } else {
          if (LOCATION.beWarning()) {
            LOCATION.warningT(methodName, "The value of SET_HEADER callback with name {0} is not of type String or it is null: {1}. Header will not be set.", new Object[] {
                headerName, value });
          }
        }
        break;
      }

      case HttpCallback.REMOVE_HEADER: {
        // TODO is it supported or not?
        break;
      }

      case HttpCallback.POST_PARAMETERS: {
        LOCATION.debugT(methodName, "Restore post parameters");
        restorePostParameters(request);
        break;
      }

      case HttpCallback.REDIRECT: {
        Object value = setterCallback.getValue();
        
        if (value instanceof String) {
          String url = (String) value;
          if (url != null && url.length() != 0) {
            if (!response.isCommitted()) {
              response.sendRedirect(url);
              if (LOCATION.beDebug()) {
                LOCATION.debugT("Sending redirect to: {0}", new Object[] { url });
              }
            } else {
              if (LOCATION.beDebug()) {
                LOCATION.debugT("Cannot send redirect because response is committed.");
              }
            }
          } else {
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Cannot send redirect to received url from HttpCallback: [{0}]", new Object[] { url });
            }
          }
        } else {
          if (LOCATION.beWarning()) {
            LOCATION.warningT(methodName, "The value of REDIRECT callback is not of type String or it is null: {0}. Redirect will not be set.", new Object[] { value });
          }
        }
        break;
      }
      
      case HttpCallback.RUNTIME_MODEL_DATA: {
        SecurityRequest securityRequest = getSecurityRequest(request);
        if (securityRequest != null) {
          String name = setterCallback.getName();
          Object data = setterCallback.getValue();
          securityRequest.storeDataInSessionRuntime(name, data);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(methodName, "Set data {0} from Runtime session model ", new Object [] { name });
          }
        } else {
          LOCATION.errorT("Set data in runtime session failed: cannot extract security request object from servlet request.");
        }
        break;
      }
      
      case HttpCallback.REMOVE_RUNTIME_MODEL_DATA: {
        SecurityRequest securityRequest = getSecurityRequest(request);
        if (securityRequest != null) {
          String name = setterCallback.getName();
          Object data = securityRequest.removeDataFromSessionRuntime(name);
          setterCallback.setValue(data);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(methodName, "Removed data {0} from Runtime session model ", new Object [] { name });
          }
        } else {
          LOCATION.errorT("Remove data from runtime session failed: cannot extract security request object from servlet request.");
        }
        break;
      }
      
      case HttpCallback.CHANGE_SCHEMA: {
        SecurityRequest securityRequest = getSecurityRequest(request);
        if (securityRequest != null) {
          String scheme = setterCallback.getName();
          String path = (String) setterCallback.getValue();
          String url = securityRequest.getURLForScheme(scheme, path);
          
          if (url != null && url.length() != 0) {
            if (LOCATION.beDebug()) {
              LOCATION.debugT(methodName, "New scheme url [{0}] returned for path [{1}] and scheme [{2}].", 
                  new Object [] { url, path, scheme });
            }
            
            
            if (!response.isCommitted()) {
              response.sendRedirect(url);
              if (LOCATION.beDebug()) {
                LOCATION.debugT(methodName, "Send redirect to new scheme URL: {0}", new Object [] { url });
              }
            } else {
              if (LOCATION.beDebug()) {
                LOCATION.debugT("Cannot sendRedirect to changed scheme url because response is committed.");
              }
            }
          } else {
            if (LOCATION.beWarning()) {
              LOCATION.warningT("Cannot get new scheme url [{0}] for path [{1}] and scheme [{2}]", new Object[] { url, path, scheme });
            }
          }
        } else {
          LOCATION.errorT("Change schema failed: cannot extract security request object from servlet request.");
        }
        break;
      }

	  case HttpCallback.ENCRYPT_STRING: {
        Object plainString = setterCallback.getValue();
        if (plainString instanceof String) {
          String encryptedString = cipher.encryptURL((String) plainString);
          setterCallback.setValue(encryptedString);
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Plain string [{0}] successfully encrypted to [{1}].", new Object[] {plainString, encryptedString});
          }
        } else {
          LOCATION.errorT("Value of callback is not instance of String [{0}] and could not be encrypted.", 
              new Object[] {plainString});
        }
        break;
      }

      default: {
        throw new IllegalArgumentException("Setter callback type '" + setterCallback.getType() + "' not yet supported");
      }
    }
  }

  /**
   * Gets the locale of the request and sets it in the LanguageCallback
   */
  private void handle(LanguageCallback languageCallback) {
    final String METHOD_NAME = "handle(LanguageCallback)";

    languageCallback.setLocale(request.getLocale());

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Set locale in LanguageCallback to {0}", new Object[] { request.getLocale().getDisplayName() });
    }
  }

  /**
   * Extracts the name from the request and sets it in the NameCallback
   */
  private void handle(NameCallback nameCallback) {
    final String METHOD_NAME = "handle(NameCallback)";

    parseUserNameAndPassword();
    nameCallback.setName(this.userName);

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Set name in NameCallback to {0}", new Object[] { userName });
    }
  }

  /**
   * Extracts the password from the request and sets it in the PasswordCallback
   */
  private void handle(PasswordCallback pwdCallback) {
    final String METHOD_NAME = "handle(PasswordCallback)";
    parseUserNameAndPassword();
    pwdCallback.setPassword(this.password);
    LOCATION.debugT(METHOD_NAME, "Set password in PasswordCallback");
  }

  private void handle(PasswordChangeCallback pwdChangeCallback) {
    final String METHOD_NAME = "handle(PasswordChangeCallback)";

    String pwd;

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "isConfirmPassword: {0}", new Object[] { isConfirmPassword });
    }

    if (isConfirmPassword) {
      pwd = request.getParameter(CONFIRM_PASSWORD);
    } else {
      pwd = request.getParameter(NEW_PASSWORD);
      // support old post parameters because portal uses the old logon app
      if (pwd == null) {
        pwd = request.getParameter(PASSWORD);
      }
    }

    isConfirmPassword = !isConfirmPassword;

    if (pwd != null) {
      pwdChangeCallback.setPassword(pwd.toCharArray());
      LOCATION.debugT(METHOD_NAME, "Set password in PasswordChangeCallback");
    } else {
      pwdChangeCallback.setPassword(null);
    }
  }

  /**
   * Gets the error message from TextOutputCallback the and sets it in the current instance
   */
  private void handle(TextOutputCallback textCallback) {
    final String METHOD_NAME = "handle(TextOutputCallback)";
    errorMessage = textCallback.getMessage();
    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Set error message from TextOutputCallback: {0}", new Object[] { errorMessage });
    }
  }

  /**
   * Extracts the user credentials information from the authorization header in case of BASIC authentication
   */
  private void initBasicCredentials() {
    final String methodName = "initBasicCredentials";

    String[] authHeader = parseAuthHeader(request.getHeader(HEADER_AUTHORIZATION));

    if (authHeader != null) {
      this.userName = authHeader[0];

      if (LOCATION.beDebug()) {
        LOCATION.debugT(methodName, "User name in Authorization header is {0}", new Object[] { userName });
      }

      if (this.password == null) {
        this.password = authHeader[1].toCharArray();
        LOCATION.debugT(methodName, "Get password from Authorization header");
      }
    }
  }

  /**
   * Extracts the user credentials information from the request parameters in case of FORM authentication
   */
  private void initFormCredentials() {
    final String methodName = "initFormCredentials";

    this.userName = request.getParameter(USERNAME);
    // old post parameters are supported because the portal uses the old logon
    // application
    if (this.userName == null) {
      this.userName = request.getParameter(J_USER);
    }

    if (LOCATION.beDebug()) {
      LOCATION.debugT(methodName, "User name in the request is {0}", new Object[] { userName });
    }

    String jPassword = this.request.getParameter(PASSWORD);

    if (jPassword != null) {
      this.password = jPassword.toCharArray();

      LOCATION.debugT(methodName, "Found password");
    }
  }

  /**
   * Checks if the certificate enrollment application is activated and the authentication is FORM. The application is activated either with the login
   * form 'createcert' checkbox or in the UME properties.
   */
  private boolean isCertificateEnrollmentActivated() {
    IUMParameters umeConfig = UMFactory.getProperties();
    boolean createCertificateChecked = (CREATE_CERTIFICATE_ON.equalsIgnoreCase(request.getParameter(RAAPP_CHECKBOX_NAME)));
    boolean isFormAuthentication = HttpServletRequest.FORM_AUTH.equalsIgnoreCase(getAuthType());
    boolean createCertificateActivatedInUME = umeConfig.getDynamic(ILoginConstants.CERTIFICATE_ENROLL,
        ILoginConstants.CERTIFICATE_ENROLL_DEFAULT_VALUE).equalsIgnoreCase(ILoginConstants.CERTIFICATE_ENROLL_ENFORCE_VALUE);
    boolean disabledCertificateInUME = umeConfig.getDynamic(ILoginConstants.CERTIFICATE_ENROLL, ILoginConstants.CERTIFICATE_ENROLL_DEFAULT_VALUE)
        .equalsIgnoreCase(ILoginConstants.CERTIFICATE_ENROLL_DEFAULT_VALUE);

    return (!disabledCertificateInUME && isFormAuthentication && (createCertificateChecked || createCertificateActivatedInUME));
  }

  private boolean isCustomLogonPageUsed() {
    final String METHOD_NAME = "isCustomLogonPageUsed";
    String customLogonPage = securityContext.getAuthenticationContext().getProperty(FORM_LOGIN_PAGE);
    String customLogonErrorPage = securityContext.getAuthenticationContext().getProperty(FORM_ERROR_PAGE);
    String customPasswordChangePage = securityContext.getAuthenticationContext().getProperty(PASSWORD_CHANGE_LOGIN_PAGE);
    String customPasswordChangeErrorPage = securityContext.getAuthenticationContext().getProperty(PASSWORD_CHANGE_ERROR_PAGE);
    boolean isCustomLogonPage = (customLogonPage != null) || (customLogonErrorPage != null) || (customPasswordChangePage != null) || (customPasswordChangeErrorPage != null);
    if (LOCATION.beDebug()) {
      if (isCustomLogonPage) {
        LOCATION.debugT(METHOD_NAME, " There are custom logon pages set - login page: {0}, login error page: {1}, password change page: {2}, password change error page: {3}", new Object[] { customLogonPage, customLogonErrorPage, customPasswordChangePage, customPasswordChangeErrorPage });
      } else {
        LOCATION.debugT(METHOD_NAME, "No custom logon page set");
      }
    }

    return isCustomLogonPage;
  }

  /**
   * Parses authorization header and extracts the credentials information - username and password in case of BASIC authentication
   */
  private String[] parseAuthHeader(String authorization) {
    final String METHOD_NAME = "parseAuthHeader";

    if (authorization == null) {
      LOCATION.debugT(METHOD_NAME, "Provided authorization header name is null");
      return null;
    }

    // According the Servlet specification the authorization header is:
    // Authorization: Method<space>data
    int startAuthSub = authorization.indexOf(SPACE_CHAR);

    // no method found
    if (startAuthSub <= 0) {
      LOCATION.debugT(METHOD_NAME, "No method found");
      return null;
    }

    String method = authorization.substring(0, startAuthSub);

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Method is {0}", new Object[] { method });
    }

    if (BASIC_AUTH_METHOD.equals(method)) {
      String credentialsString = authorization.substring(startAuthSub + 1);

      try {
        byte[] decodedBytes = BASE64Decoder.decode(credentialsString.getBytes());
        // $JL-I18N$

        credentialsString = new String(decodedBytes, DEFAULT_CHARACTER_ENCODING);
        // $JL-I18N$
        // This jlin test does not catch the case when the charset string is provided as a variable
      } catch (Exception e) {
        if (LOCATION.beError()) {
          LOCATION.traceThrowableT(Severity.ERROR, METHOD_NAME, "Cannot decode authorization credentials", e);
        }
        return null;
      }
      String[] credentials = new String[2];
      int index = credentialsString.indexOf(SEPARATOR_CHAR);
      credentials[0] = credentialsString.substring(0, index);
      credentials[1] = credentialsString.substring(index + 1);

      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Username: {0}", new Object[] { credentials[0] });
      }

      return credentials;
    }

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "Unsupported method: {0}", new Object[] { method });
    }
    return null;
  }

  /**
   * Extracts the cookies from the request and stores them
   */
  private void parseCookies() {
    if (this.cookies == null) {
      this.cookies = new HashMap();

      if (request != null) {
        Cookie[] requestCookies = request.getCookies();
        if (requestCookies != null) {
          for (int i = 0; i < requestCookies.length; i++) {
            // The cookie is always put with a key cookie's name in UPPER CASE
            this.cookies.put(requestCookies[i].getName().toUpperCase(), requestCookies[i]);
            // $JL-I18N$
            // This jlin test checks for possible problems concerning internationalization.
            // In this case the variable 'cookieName' is used internally as a constant - key for a cookie instance.
            // This does not lead to internationalization issues.
          }
        }
      }
    }
  }

  /**
   * Extracts the name and the password of the user from the request. They can be stored either in the request's parameters, header data or
   * attributes. If there is password change form submitted, then the user name is taken from the session.
   */
  private void parseUserNameAndPassword() {
    final String METHOD_NAME = "parseUserNameAndPassword";

    LOCATION.entering(METHOD_NAME);

    try {

      if (!hasPasswordChange && userName != null && password != null) {
        return;
      }

      if (!canRedirectToLoginPage) {
        canRedirectToLoginPage = true;
        LOCATION.debugT(METHOD_NAME, "Enabled redirect to login/error page");
      }

      String authType = getAuthType();
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD_NAME, "Authentication type: {0}", new Object[] { authType });
      }

      if (authType == null || HttpServletRequest.FORM_AUTH.equalsIgnoreCase(authType)) {
        initFormCredentials();

        if (request.getRequestURI().endsWith(J_SECURITY_CHECK) && getOriginalURLEncryptedFromCookie(request) == null) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Direct access to {0}", new Object[] { J_SECURITY_CHECK });
          }

          try {
            sendError(HttpServletResponse.SC_NOT_FOUND);
          } catch (IOException e) {
            if (LOCATION.beDebug()) {
              LOCATION.traceThrowableT(Severity.DEBUG, METHOD_NAME, e);
            }
          }
          return;
        }

        // temporary workarround for KMC (Portal)
        if (this.userName == null) {
          if (LOCATION.beInfo()) {
            LOCATION.infoT(METHOD_NAME, "Searching the username and password in the Authorization header although the auth method is FORM.");
          }
          initBasicCredentials();
        }

        // this is workaround - if not send via form, it's in attributes
        if (this.userName == null) {
          Object nameValue = this.request.getAttribute(USERNAME);
          if (nameValue instanceof String) {
            this.userName = (String) nameValue;
          } else {
            if (nameValue != null) {
              if (LOCATION.beWarning()) {
                LOCATION
                    .warningT(METHOD_NAME, "The attribute {0} set in the request is not of type String: {1}.", new Object[] { USERNAME, nameValue });
              }
            } else {
              if (LOCATION.beDebug()) {
                LOCATION
                    .debugT(METHOD_NAME, "The attribute {0} set in the request is null.", new Object[] { USERNAME });
              }
            }
          }

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "User name in the attributes is {0}", new Object[] { userName });
          }

          if (this.password == null) {
            Object passwordValue = request.getAttribute(PASSWORD);
            if (passwordValue instanceof String) {
              String passwordAttribute = (String) passwordValue;
              if (passwordAttribute != null) {
                this.password = passwordAttribute.toCharArray();
                LOCATION.debugT(METHOD_NAME, "Found password in the request attribute");
              }
            } else {
              if (passwordValue != null) {
                if (LOCATION.beWarning()) {
                  LOCATION.warningT(METHOD_NAME, "The attribute {0} set in the request is not of type String: {1}.", new Object[] { PASSWORD, passwordValue });
                }
              } else {
                if (LOCATION.beDebug()) {
                  LOCATION.debugT(METHOD_NAME, "The attribute {0} set in the request is null. ", new Object[] { PASSWORD });
                }
              }
            }
          }
        }
      } else if (HttpServletRequest.BASIC_AUTH.equalsIgnoreCase(authType)) {
        initBasicCredentials();

        // TODO: temporary workarround for KMC (Portal)
        if (this.userName == null) {
          if (LOCATION.beInfo()) {
            LOCATION.infoT(METHOD_NAME, "Searching the username and password in the request parameters although the auth method is BASIC.");
          }
          initFormCredentials();
        }
      }

      Object sessionAttr = getAttributeFromSession(USERNAME);
      
      // Check if password change form has been submitted.
      if (sessionAttr != null) {
        this.hasPasswordChange = true;

        if (this.userName == null) {
	        this.userName = (String) sessionAttr;

          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "User name in session attribute is {0}", new Object[] { userName });
          }
        }
      }
	
	    if (hasPasswordChange) {
	    String jPassword = this.request.getParameter(CURRENT_PASSWORD);
	
	    	if (jPassword != null) {
	    		this.password = jPassword.toCharArray();
	
	    		LOCATION.debugT(METHOD_NAME, "Current password received.");
	      }
	    }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  private void removeNTLMToken() {
    if (sendResponse && !response.isCommitted()) {
      String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

      if ((authorizationHeader != null) && authorizationHeader.startsWith(HEADER_NEGOTIATE)) {
        // "Negotiate".length()
        String authorizationData = authorizationHeader.substring(HEADER_NEGOTIATE.length()).trim();

        if (authorizationData != null) {
          authorizationData = CallbackHandlerHelper.decodeAuthorizationData(request, authorizationData);

          if ((authorizationData != null) && authorizationData.startsWith(HEADER_NTLM_TOKEN)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            if (LOCATION.beDebug()) {
              LOCATION.debugT("Response status set to 401");
            }
          }
        }
      }
    }
  }

  /**
   * Restore the POST parameters from HTTP session in the HTTP request. It is used in case that custom logon page is set.
   * 
   * @param request HttpServletRequest object
   */
  private void restorePostParameters(HttpServletRequest request) {
    final String METHOD_NAME = "restorePostParameters";

    LOCATION.entering(METHOD_NAME);

    try {
      SecurityRequest securityRequest = getSecurityRequest(request);
      if (securityRequest != null) {
        securityRequest.restorePostDataBytes();
        if (LOCATION.beDebug()) {
          LOCATION.debugT("POST parameters restored from HTTP session to HTTP request");
        }
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.warningT("Cannot restore POST parameters from HTTP session to HTTP request");
        }
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  private void storePostParameters(HttpServletRequest request) {
    final String METHOD_NAME = "storePostParameters";

    LOCATION.entering(METHOD_NAME);

    String originalUrlCookie = getOriginalURLDecryptedFromCookie(request);
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Original Url extracted from cookie: {0}", new Object[] { originalUrlCookie });
    }

    if (POST_METHOD.equalsIgnoreCase(request.getMethod()) && (originalUrlCookie == null)) {
      SecurityRequest securityRequest = getSecurityRequest(request);

      if (securityRequest != null) {
        securityRequest.storePostDataBytes();
        if (LOCATION.beDebug()) {
          LOCATION.debugT("POST parameters stored from HTTP request to HTTP session");
        }
      } else {
        if (LOCATION.beWarning()) {
          LOCATION.warningT("Cannot store POST parameters from HTTP request in HTTP session");
        }
      }
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Post parameters are not stored because request method is not POST or there is original url cookie.");
      }
    }

    LOCATION.exiting(METHOD_NAME);
  }

  /**
   * Extracts the name of current user from the request and stores it as a session attribute.
   */
  private void saveUserNameInSession() {
    final String METHOD_NAME = "saveUserNameInSession";

    LOCATION.entering(METHOD_NAME);

    try {
      parseUserNameAndPassword();
      if (userName != null) {
        setAttributeInSession(USERNAME, userName);
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Stores attributes in the HTTP session
   * 
   * @param name String value of the attribute name
   * @param value Object value of the attribute
   */
  private void setAttributeInSession(String name, Object value) {
    final String METHOD_NAME = "setAttributeInSession";

    LOCATION.entering(METHOD_NAME);

    try {

      if (name == null) {
        return;
      }

      HttpSession httpSession = request.getSession(true);

      if (httpSession instanceof HttpSecureSession) {
        HttpSecureSession secureSession = (HttpSecureSession) httpSession;
        if (value != null) {
          secureSession.setSecurityAttribute(name, value);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Set security attribute {0} to {1}", new Object[] { name, value });
          }
        } else {
          secureSession.removeSecurityAttribute(name);
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Removed security attribute {0}", new Object[] { name });
          }
        }
      } else if (value != null) {
        httpSession.setAttribute(name, value);
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Set session attribute {0} to {1}", new Object[] { name, value });
        }
      } else {
        httpSession.removeAttribute(name);
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Removed session attribute {0}", new Object[] { name });
        }
      }
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  /**
   * Sets the default values of the global properties for the application pages
   */
  private static void setGlobalProperties() {
    if (globalProperties == null) {
      globalProperties = new Properties();

      globalProperties.setProperty(FORM_LOGIN_PAGE, logonApplicationAlias + GLOBAL_FORM_LOGIN_PAGE);
      globalProperties.setProperty(FORM_ERROR_PAGE, logonApplicationAlias + GLOBAL_FORM_ERROR_PAGE);
      globalProperties.setProperty(PASSWORD_CHANGE_LOGIN_PAGE, logonApplicationAlias + GLOBAL_PASSWORD_CHANGE_LOGIN_PAGE);
      globalProperties.setProperty(PASSWORD_CHANGE_ERROR_PAGE, logonApplicationAlias + GLOBAL_PASSWORD_CHANGE_ERROR_PAGE);

      globalProperties.setProperty(POST_FORM_PAGE, logonApplicationAlias + GLOBAL_POST_FORM_PAGE);

      if (LOCATION.beDebug()) {
        LOCATION.debugT("The global properties are set to {0}", new Object[] { globalProperties });
      }
    }
  }

  /**
   * Sets the logon application alias of the application
   * 
   * @param alias the application alias
   */
  private static void setLogonApplicationAlias(String alias) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Logon application alias in configuration is {0}", new Object[] { alias });
    }

    if ((alias != null) && !EMPTY_STRING_VALUE.equals(alias)) {
      if (alias.startsWith("/")) {
        logonApplicationAlias = alias;
      } else {
        logonApplicationAlias = "/" + alias;
      }
    }

    if (LOCATION.beDebug()) {
      LOCATION.debugT("Logon application alias is se to {0}", new Object[] { logonApplicationAlias });
    }
  }

  /**
   * Sets the alias to the logon servlet that is used to redirect to the different pages used for login - login, login error, password change and
   * password change error pages
   * 
   * @param alias the alias to the logon servlet
   */
  private static void setLogonUrl(String alias) {
    if (alias == null || alias.startsWith("/")) {
      logonURL = alias;
    } else {
      logonURL = "/" + alias;
    }

    if (LOCATION.beDebug()) {
      LOCATION.debugT("setLogonUrl", "Set logon URL: {0}", new Object[] { logonURL });
    }
  }

  /**
   * Returns the original page URL
   */
  private String getOriginalPageURL() throws IOException {
    String originalURL = getOriginalURLDecryptedFromCookie(request);
    if (originalURL == null) {
      originalURL = CallbackHandlerHelper.getOriginalUrlFromRequest(request);
    }
    return originalURL;
  }

  /**
   * Sets the root of the security context. It can be set only once, in case it is already set does nothing.
   * 
   * @param ctx the root security context
   */
  private static void setRootSecurityContext(SecurityContext ctx) {
    if (rootSecCtx == null) {
      rootSecCtx = ctx;
    }
  }
  
  private String getOriginalRequestMethod(HttpServletRequest request) {
    String method = null;
    String url = getOriginalURLEncryptedFromCookie(request);
    if (url != null) {
      method = url.substring(0, url.indexOf(REQUEST_METHOD_DELIMITER));
    }
    return method;
  }
}
