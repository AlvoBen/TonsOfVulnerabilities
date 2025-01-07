/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG, Walldorf. You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered into with SAP.
 */

package com.sap.engine.interfaces.security.auth;

import java.io.IOException;

import java.net.URLEncoder;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.tc.logging.Severity;

/**
 * Callback handler to be used by web applications.
 * 
 * @author NW F SIM Runtime BG
 * @version 7.1
 */
public class WebCallbackHandler extends AbstractWebCallbackHandler {

  private final static String AUTHENTICATE_HEADER = "WWW-Authenticate";
  private final static String AMPERSAND = "&";
  private final static String BASIC_REALM_HEADER = "Basic realm=\"";
  private final static String CLIENT_CERT_AUTH_METHOD = "CLIENT-CERT";
  private final static String EQUALS = "=";
  private final static String QUOTATION_MARK = "\"";
  private final static String QUESTION_MARK = "?";
  private final static String REALM_NAME = "realm_name";

  public WebCallbackHandler(HttpServletRequest request, HttpServletResponse response) {
    super(request, response);
  }

  public WebCallbackHandler(HttpServletRequest request, HttpServletResponse response, boolean sendResponse) {
    super(request, response, sendResponse);
  }

  protected void forwardToCertificateEnrollment() {
    final String METHOD_NAME = "forwardToEnrollApplication";
    String url = null;

    LOCATION.entering(METHOD_NAME);

    try {
      url = getOriginalURLEncryptedFromCookie(request);

      if (url == null) {
        LOCATION.debugT(METHOD_NAME, "Original URL is missing");

        if (request.getRequestURI().endsWith(J_SECURITY_CHECK)) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Direct access to {0}", new Object[] { J_SECURITY_CHECK });
          }

          sendError(HttpServletResponse.SC_NOT_FOUND);
        } // The opposite case means that the authentication succeeds during the
        // first request. Therefore, no response is needed.
      } else {
        url = url.substring(url.indexOf(REQUEST_METHOD_DELIMITER) + 1);
        url = cipher.decryptURL(url);
        url = CallbackHandlerHelper.getRelativeURL(url);
        clearOriginalPageURL();

        String RAappURL = RAAPP_START_PAGE;
        if (url != null) {
          if (RAappURL.indexOf(QUESTION_MARK) >= 0) {
            url = RAappURL + AMPERSAND + RAAPP_OLDSERVICE + EQUALS + URLEncoder.encode(url, REQUEST_INPUT_ENCODING);
          } else {
            url = RAappURL + QUESTION_MARK + RAAPP_OLDSERVICE + EQUALS + URLEncoder.encode(url, REQUEST_INPUT_ENCODING);
          }
        } else {
          url = RAAPP_START_PAGE;
        }

        this.forwardToEnrollApp(url);
      }
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot forward to enroll application", e);
    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected void forwardToFormErrorPage() throws IOException {
    final String METHOD_NAME = "forwardToFormErrorPage";

    LOCATION.entering(METHOD_NAME);

    try {
      String redirectURL = getFormErrorPage();

      saveOriginalPageURL();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Forward to error page: {0}", new Object[] { redirectURL });
      }

      forward(redirectURL);

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected void forwardToFormLoginPage() throws IOException {
    final String METHOD_NAME = "forwardToFormLoginPage";

    LOCATION.entering(METHOD_NAME);

    try {
      String redirectURL = getFormLoginPage();

      saveOriginalPageURL();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Forward to login page: ", new Object[] { redirectURL });
      }

      forward(redirectURL);
    } finally {

      LOCATION.exiting(METHOD_NAME);

    }
  }

  protected void forwardToLoginOrErrorPage(boolean showErrorOnLogonPage) throws IOException {
    final String METHOD_NAME = "forwardToLoginOrErrorPage";

    LOCATION.entering(METHOD_NAME);

    try {
      if (sendResponse) {
        if (isIdPDiscovery()) {
          forwardToFormLoginPage();
        }

        String authType = getAuthType();

        if (authType == null || HttpServletRequest.FORM_AUTH.equalsIgnoreCase(authType)) {
          if (this.response.isCommitted()) {
            LOCATION.debugT(METHOD_NAME, "Response is already committed");
            return;
          }

          if (!canRedirectToLoginPage) {
            LOCATION.debugT(METHOD_NAME, "Cannot redirect to login/error page. No username or password has been requested by the login modules.");
            return;
          }

          /* the login error page should be displayed in three cases
           * 1. that login context has stated that error should be displayed - showErrorOnLogonPage passed via the AuthStateCallback
           * 2. if the request comes from J_SECURITY_CHECK
           * 3. if this is not the first request to forward to login page - the original URL cookie already exists 
           */
           
          if (showErrorOnLogonPage || request.getRequestURI().endsWith(J_SECURITY_CHECK) || (getOriginalURLDecryptedFromCookie(request) != null)) {
            forwardToFormErrorPage();
          } else {
            forwardToFormLoginPage();
          }
        } else if (HttpServletRequest.BASIC_AUTH.equalsIgnoreCase(authType)) {
          response.addHeader(AUTHENTICATE_HEADER, BASIC_REALM_HEADER + getLoginRealmName() + QUOTATION_MARK);

          LOCATION.debugT(METHOD_NAME, "Added \"WWW-Authenticate\" header");

          sendError(HttpServletResponse.SC_UNAUTHORIZED);

        } else if (HttpServletRequest.CLIENT_CERT_AUTH.equalsIgnoreCase(authType) || CLIENT_CERT_AUTH_METHOD.equalsIgnoreCase(authType)) {
          if (!request.isSecure()) {
            // TODO: use methods for changing schemes
            request.setAttribute(REQUEST_SECURE_ACTION, CHANGE_SCHEMA_HTTPS);
            LOCATION.debugT(METHOD_NAME, "Changed schema to https");
          } else {
            sendError(HttpServletResponse.SC_UNAUTHORIZED);
          }

        } else if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Unknown authentication method {0}", new Object[] { authType });
        }
      } else if (LOCATION.beDebug()) {
        LOCATION.debugT("Cannot forward to login or login error page because calllback handler does not send response");
      }
    } finally {

      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected void forwardToOriginalPage() throws IOException {
    final String METHOD_NAME = "forwardToOriginalPage";
    String url = null;

    LOCATION.entering(METHOD_NAME);
    
    request.getSession(true);
    String originalUrl = (String) request.getAttribute(ORIGINAL_URL_ATTRIBUTE_NAME);

    try {
      url = getOriginalURLEncryptedFromCookie(request);

      if (url == null) {
        LOCATION.debugT(METHOD_NAME, "Original URL is missing");

        if (request.getRequestURI().endsWith(J_SECURITY_CHECK)) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD_NAME, "Direct access to {0}", new Object[] { J_SECURITY_CHECK });
          }

          sendError(HttpServletResponse.SC_NOT_FOUND);
        } // The oposite case means that the authentication succeeds during the
        // first request. Therefore, no response is needed.
      } else {
        String method = url.substring(0, url.indexOf(REQUEST_METHOD_DELIMITER));
        url = CallbackHandlerHelper.getRelativeURL(originalUrl);
        clearOriginalPageURL();

        sendRedirect(url, method);
      }
    } finally {

      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected void forwardToPasswordChangeErrorPage() throws IOException {
    final String METHOD_NAME = "goToPasswordChangeErrorPage";

    LOCATION.entering(METHOD_NAME);

    try {
      String redirectURL = getPasswordChangeErrorPage();

      saveOriginalPageURL();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Forward to password change error page: {0}", new Object[] { redirectURL });
      }

      forward(redirectURL);

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected void forwardToPasswordChangePage() throws IOException {
    final String METHOD_NAME = "goToPasswordChangePage";

    LOCATION.entering(METHOD_NAME);

    try {
      String redirectURL = getPasswordChangePage();

      saveOriginalPageURL();

      if (LOCATION.beDebug()) {
        LOCATION.debugT("Forward to password change page: {0}", new Object[] { redirectURL });
      }

      forward(redirectURL);

    } finally {
      LOCATION.exiting(METHOD_NAME);
    }
  }

  protected boolean isInitialPasswordChangePageRequest() {
    return !(request.getParameter(NEW_PASSWORD) != null);
  }

  private void forward(String url) throws IOException {
    if (sendResponse) {
      if (!response.isCommitted()) {
        try {
          request.getRequestDispatcher(url).forward(request, response);

          if (LOCATION.beDebug()) {
            LOCATION.debugT("Forwarded to {0}", new Object[] { url });
          }
        } catch (ServletException e) {
          LOCATION.traceThrowableT(Severity.ERROR, "Forward to {0} failed.", new Object[] { url }, e);
          throw new RuntimeException(e);
        }
      } else if (LOCATION.beDebug()) {
        LOCATION.debugT("Cannot forward because response is committed");
      }
    } else if (LOCATION.beDebug()) {
      LOCATION.debugT("Cannot forward because callback handler does not sent response");
    }
  }

  private void forwardToEnrollApp(String url) throws IOException, ServletException {
    if (sendResponse) {
      if (!response.isCommitted()) {
        ((SecurityRequest) request).getServletContext().getContext(RAAPP_CONTEXT).getRequestDispatcher(url).forward(request, response);

        if (LOCATION.beDebug()) {
          LOCATION.debugT("Forwarded to {0}", new Object[] { url });
        }
      } else if (LOCATION.beDebug()) {
        LOCATION.debugT("Cannot forward to enrollment application because response is committed");
      }
    } else if (LOCATION.beDebug()) {
      LOCATION.debugT("Cannot forward to enrollment application because calllback handler does not send response");
    }
  }

  private String getFormErrorPage() {
    String url = getProperty(FORM_ERROR_PAGE);
    return attachErrorMessageInURL(url);
  }

  private String getFormLoginPage() {
    return getProperty(FORM_LOGIN_PAGE);
  }

  private String getLoginRealmName() {
    return securityContext.getAuthenticationContext().getProperty(REALM_NAME);
  }

  private String getPasswordChangeErrorPage() {
    String url = getProperty(PASSWORD_CHANGE_ERROR_PAGE);
    return attachErrorMessageInURL(url);
  }

  private String getPasswordChangePage() {
    return getProperty(PASSWORD_CHANGE_LOGIN_PAGE);
  }

  private void sendRedirect(String url, String method) throws IOException {
    final String METHOD_NAME = "sendRedirect";

    if (LOCATION.beDebug()) {
      LOCATION.debugT(METHOD_NAME, "url = {0}, method = {1}", new Object[] { url, method });
    }

    if (!sendResponse) {
      LOCATION.debugT(METHOD_NAME, "Cannot send redirect because callback handler does not send response");
      return;
    }

    if (response.isCommitted()) {
      LOCATION.debugT(METHOD_NAME, "Cannot send redirect because response is committed");
      return;
    }

    String encodedRedirectURL = response.encodeRedirectURL(url);
    String requestURI = request.getRequestURI();
    //This check refers to the cases when requestURI ends with either J_SECURITY_CHECK or SAP_J_SECURITY_CHECK
    if (requestURI.endsWith(J_SECURITY_CHECK)) {
      if (GET_METHOD.equals(method)) {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("Redirect to {0}", new Object[] { url });
        }
        response.sendRedirect(encodedRedirectURL);
      } else if (POST_METHOD.equals(method)) {

        String formLoginPage = securityContext.getAuthenticationContext().getProperty(FORM_LOGIN_PAGE);
        String postFormPageProperty = getProperty(POST_FORM_PAGE);
        String postLogonJSP = postFormPageProperty;

        if (formLoginPage != null) {
          if (postFormPageProperty.indexOf(QUESTION_MARK) > 0) {
            postLogonJSP = postFormPageProperty + AMPERSAND + FORM_LOGIN_PAGE + EQUALS + URLEncoder.encode(formLoginPage, REQUEST_INPUT_ENCODING);
          } else {
            postLogonJSP = postFormPageProperty + QUESTION_MARK + FORM_LOGIN_PAGE + EQUALS + URLEncoder.encode(formLoginPage, REQUEST_INPUT_ENCODING);
          }
        }
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD_NAME, "Forward to {0}", new Object[] { postLogonJSP });
        }
        try {
          request.getRequestDispatcher(postLogonJSP).forward(request, response);
        } catch (ServletException e) {
          LOCATION.traceThrowableT(Severity.ERROR, "Forward to /postLogin.jsp failed.", e);
          throw new RuntimeException(e);
        }
      }
    } else {
      LOCATION.debugT(METHOD_NAME, "This is self submit case. No redirect or forward is needed.");
    }
  }
}
