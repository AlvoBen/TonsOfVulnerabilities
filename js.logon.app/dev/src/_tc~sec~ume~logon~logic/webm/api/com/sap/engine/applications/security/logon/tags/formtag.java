
package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.lib.xml.util.BASE64Encoder;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.pages.LogoutPageServlet;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.engine.interfaces.security.auth.AbstractWebCallbackHandler;
import com.sap.engine.interfaces.security.auth.SecurityRequest;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.util.taglib.EncodeHtmlTag;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class FormTag extends TagSupport {

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);    

  private static final List SPECIAL_PARAMETERS_LIST = Arrays.asList(new String[] { SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_PAGE,
      SAPMLogonServlet.SHOW_CHANGE_PASSWORD_PAGE, SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_ERROR_PAGE, SAPMLogonServlet.UID_PASSWORD_LOGON_ACTION,
      SAPMLogonServlet.CANCEL_LOGON_PROBLEM_ACTION, SAPMLogonServlet.CANCEL_RESET_PASSWORD_ACTION, SAPMLogonServlet.CANCEL_HELP_ACTON, LogonRequest.PARAMETER_JUserName,
      LogonRequest.PARAMETER_JPassword, LogonRequest.PARAMETER_ErrorMessage, LogonRequest.PARAMETER_NoCertStoring, LogonRequest.PARAMETER_LoginSubmit, 
      LogonRequest.PARAMETER_LoginDoRedirect, LogonRequest.PARAMETER_CreateCert, LogonRequest.PARAMETER_SaveCert });

  private static final String SET_AUTOCOMPLETE_OFF = " AUTOCOMPLETE=\"off\" ";
 
  private String type;

  public void setType(String type) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("setType( " + type + " )");
    }
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.jsp.tagext.Tag#doStartTag()
   */
  public int doStartTag() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doStartTag(), type = " + type);
    }

    if ("logon".equalsIgnoreCase(type)) {
      return doLogonForm();
    }
    
    if ("discoverIdPForm".equalsIgnoreCase(type)) {
      return doDiscoverIdPForm();
    }
    
    if ("skipIdPForm".equalsIgnoreCase(type)) {
      return doSkipIdPForm();
    }    

    if ("certlogon".equalsIgnoreCase(type)) {
      return doCertLogonForm();
    }

    if ("changepassword".equalsIgnoreCase(type)) {
      return doChangePasswordForm();
    }

    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    IAccessToLogic proxy = BeanFactory.getProxy(this.pageContext);
    String name = null;
    String method = "post";
    String action = null;
    String hiddenFieldForCancelFlag = null;
    String actionQueryString = null;

    if ("helpForm".equalsIgnoreCase(type)) {
      name = "helpForm";
      hiddenFieldForCancelFlag = SAPMLogonServlet.CANCEL_HELP_ACTON;

      if (inPortal) {
        action = proxy.getAlias(SAPMLogonServlet.SUBMIT_HELP_EVENT);
      } else {
        action = LogonRequest.ACTION_JSecurityCheck;
      }

    } else if ("resetPassword".equalsIgnoreCase(type)) {
      name = "helpForm";
      hiddenFieldForCancelFlag = SAPMLogonServlet.CANCEL_RESET_PASSWORD_ACTION;

      if (inPortal) {
        action = proxy.getAlias(SAPMLogonServlet.RESET_PASSWORD_EVENT);
      } else {
        action = LogonRequest.ACTION_JSecurityCheck;
      }

    } else if ("logonProblem".equalsIgnoreCase(type)) {
      name = "helpForm";
      hiddenFieldForCancelFlag = SAPMLogonServlet.CANCEL_LOGON_PROBLEM_ACTION;

      if (inPortal) {
        action = proxy.getAlias(SAPMLogonServlet.LOGON_PROBLEM_EVENT);
      } else {
        action = LogonRequest.ACTION_JSecurityCheck;
      }

    } else if ("logoff".equalsIgnoreCase(type)) {
      name = "logoffForm";
      action = LogoutPageServlet.getRedirectURL(pageContext.getRequest());
      method = "get";
      
      if(action!=null && action.trim().length()>0){
        String actionSplit[] = action.split("\\?");
        if(actionSplit.length==2){
          action = actionSplit[0];
          actionQueryString = actionSplit[1];
        }
      }
      
    }

    try {
      out.print("<FORM ");
      out.print(SET_AUTOCOMPLETE_OFF);
      out.print(" name=\"");
      out.print(name);
      out.print("\" target=\"logonAppPage\" method=\"");
      out.print(method);
      out.print("\" action=\"");
      out.print(action);

      out.println("\">");

      if (hiddenFieldForCancelFlag != null) {
        out.print("<input name=\"");
        out.print(hiddenFieldForCancelFlag);
        out.print("\" type=\"hidden\" value=\"");
        out.print(SAPMLogonServlet.CANCEL_OFF);
        out.println("\">");
      }

      addPostParameters();      
      addQueryStringParameters(actionQueryString, null);      
    } 
    catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doStartTag()");
    }
    return Tag.EVAL_BODY_INCLUDE;
  }

  private int doChangePasswordForm() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doChangePasswordForm()");
    }

    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    boolean isSelfSubmitAction = inPortal || isSelfSubmitCase();

    try {
      out.print("<FORM ");
      out.print(SET_AUTOCOMPLETE_OFF);
      out.print(" name=\"changePasswordForm\" target=\"logonAppPage\" method=\"post\" action=\"");
      out.print(isSelfSubmitAction ? LogonRequest.ACTION_SelfSubmit : LogonRequest.ACTION_SAPJSecurityCheck);
      out.println("\">");

      out.print("<input name=\"");
      out.print(SAPMLogonServlet.CANCEL_PASSWORD_CHANGE_ACTION);
      out.print("\" type=\"hidden\" value=\"");
      out.print(SAPMLogonServlet.CANCEL_OFF);
      out.println("\">");

      if (inPortal) {
        out.println("<input name=\"" + LogonRequest.PARAMETER_LoginSubmit + "\" type=\"hidden\" value=\"on\">");

        if (UMFactory.getProperties().getBoolean("ume.login.do_redirect", true)) {
          out.println("<input type=\"hidden\" name=\"" + LogonRequest.PARAMETER_LoginDoRedirect + "\" value=\"1\" />");
        }
      }

      addPostParameters();
      addCreateCert();
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add password change form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doChangePasswordForm()");
    }
    return Tag.EVAL_BODY_INCLUDE;
  }

  private int doCertLogonForm() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doCertLogonForm()");
    }

    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    boolean isSelfSubmitAction = inPortal || isSelfSubmitCase();

    try {
      out.print("<FORM ");
      out.print(SET_AUTOCOMPLETE_OFF);
      out.print(" name=\"certLogonForm\" target=\"logonAppPage\" method=\"post\" action=\"");
      out.print(isSelfSubmitAction ? LogonRequest.ACTION_SelfSubmit : LogonRequest.ACTION_JSecurityCheck);
      out.println("\" >");

      if (inPortal) {
        out.println("<input name=\"" + LogonRequest.PARAMETER_LoginSubmit + "\" type=\"hidden\" value=\"on\">");

        if (UMFactory.getProperties().getBoolean("ume.login.do_redirect", true)) {
          out.println("<input type=\"hidden\" name=\"" + LogonRequest.PARAMETER_LoginDoRedirect + "\" value=\"1\" />");
        }
      }

      addPostParameters();
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doCertLogonForm()");
    }
    return Tag.EVAL_BODY_INCLUDE;
  }

  private int doLogonForm() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doLogonForm()");
    }

    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    boolean isSelfSubmitAction = inPortal || isSelfSubmitCase();

    try {
      out.print("<FORM ");
      out.print(SET_AUTOCOMPLETE_OFF);
      out.print(" name=\"logonForm\" target=\"logonAppPage\" method=\"post\" action=\"");
      out.print(isSelfSubmitAction ? LogonRequest.ACTION_SelfSubmit : LogonRequest.ACTION_JSecurityCheck);
      out.print("\" ");

      if (TenantFactory.getInstance().isBPOEnabled() && UMFactory.getProperties().getBoolean(TenantFactory.MULTI_TENANCY_PREFIXING, true)) {
        out.print("\"onSubmit=\"javascript:addTenantPrefix();\"");
      }

      out.println(">");

      if (inPortal) {
        out.print("<input name=\"" + LogonRequest.PARAMETER_LoginSubmit + "\" type=\"hidden\" value=\"on\">");

        if (UMFactory.getProperties().getBoolean("ume.login.do_redirect", true) == true) {
          out.print("<input type=\"hidden\" name=\"" + LogonRequest.PARAMETER_LoginDoRedirect + "\" value=\"1\" />");
        }
      }

      out.print("<input name=\"" + LogonRequest.PARAMETER_NoCertStoring + "\" type=\"hidden\" value=\"on\">");

      addPostParameters();
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doLogonForm()");
    }

    return Tag.EVAL_BODY_INCLUDE;
  }
  
  private int doSkipIdPForm() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doSkipIdPForm()");
    }

    LogonRequest logonRequest = new LogonRequest(this.pageContext);
    
    /*
    if(!logonRequest.hasAttribute(LogonRequest.ATTRIBUTE_ShowLocalLoginPage)){
      return Tag.SKIP_BODY;
    }
    */
    
    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    boolean isSelfSubmitAction = inPortal || isSelfSubmitCase();

    try {                    
      if(logonRequest.isPostRequest()){
        out.println("<FORM name=\"skipIdPForm\" target=\"logonAppPage\" method=\"post\" action=\"" + LogonRequest.ACTION_SelfSubmit + "\">");
        addPostParameters();
      }
      else{
        out.println("<FORM name=\"skipIdPForm\" target=\"logonAppPage\" method=\"get\" action=\"" + LogonRequest.ACTION_SelfSubmit + "\">");        
      }      
      
      String queryString = (String)logonRequest.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardQueryString);
      if(queryString==null){      
        ServletRequest servletRequest = this.pageContext.getRequest();
        if(servletRequest instanceof HttpServletRequest){
          HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;   
          queryString = httpRequest.getQueryString();
        }          
      }      
      addQueryStringParameters(queryString, 
          new String[]{LogonRequest.PARAMETER_Saml2Disabled, LogonRequest.PARAMETER_Saml2IdP}); 
      
      out.println("<input name=\"" + LogonRequest.PARAMETER_Saml2Disabled + "\" type=\"hidden\" value=\"disabled\">");      
    } 
    catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doSkipIdPForm()");
    }

    return Tag.EVAL_BODY_INCLUDE;
  }
  
  private int doDiscoverIdPForm() throws JspTagException {

    if (LOCATION.bePath()) {
      LOCATION.entering("doDiscoverIdPForm()");
    }

    LogonRequest logonRequest = new LogonRequest(this.pageContext);
    
    JspWriter out = this.pageContext.getOut();

    boolean inPortal = BeanFactory.inPortal(pageContext);
    boolean isSelfSubmitAction = inPortal || isSelfSubmitCase();

    try {                  
      out.println("<FORM name=\"discoverIdPForm\" target=\"logonAppPage\" method=\"get\" action=\"" + logonRequest.ACTION_SelfSubmit + "\">");
      if(logonRequest.isPostRequest()){
        out.println("<input name=\"" + LogonRequest.PARAMETER_Saml2Post + "\" type=\"hidden\" value=\"true\">");
      }
      /*
      else{
        out.println("<input name=\"" + LogonRequest.PARAMETER_Saml2Post + "\" type=\"hidden\" value=\"false\">");           
      }
      */
      
      String queryString = (String)logonRequest.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardQueryString);
      if(queryString==null){      
        ServletRequest servletRequest = this.pageContext.getRequest();
        if(servletRequest instanceof HttpServletRequest){
          HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;          
          queryString = httpRequest.getQueryString();
        }                
      }      
      addQueryStringParameters(queryString, 
          new String[]{LogonRequest.PARAMETER_Saml2Disabled, LogonRequest.PARAMETER_Saml2IdP, LogonRequest.PARAMETER_Saml2Post});
    } 
    catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
      throw new JspTagException(e);
    }

    if (LOCATION.bePath()) {
      LOCATION.exiting("doDiscoverIdPForm()");
    }

    return Tag.EVAL_BODY_INCLUDE;
  }

  private boolean isSelfSubmitCase() {

    String formTarget = null;
    try {
      LogonRequest logonRequest = new LogonRequest(pageContext);
      formTarget = (String) logonRequest.getAttribute(LogonRequest.ATTRIBUTE_LogonFormAction);
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Error in pageContext.getRequest().getAttribute(\"" + LogonRequest.ATTRIBUTE_LogonFormAction + "\")", e);
    }

    if (LOCATION.beDebug()) {
      LOCATION.debugT("Attribute \"" + LogonRequest.ATTRIBUTE_LogonFormAction + "\" = " + formTarget);
    }

    boolean result = "_self".equalsIgnoreCase(formTarget);

    if (LOCATION.beDebug()) {
      LOCATION.debugT("isSelfSubmitCase() = " + result);
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.jsp.tagext.Tag#doEndTag()
   */
  public int doEndTag() throws JspTagException {

    JspWriter out = pageContext.getOut();

    try {
      if ("certlogon".equalsIgnoreCase(type)) {
        if (SAPMLogonServlet.getAllowCertLogon()) {
          out.println("<input type=\"hidden\" name=\"" + LogonRequest.PARAMETER_SaveCert + "\" value=\"1\" />");
        }
      }

      out.println("</form>");
    } catch (Exception e) {
      LOCATION.traceThrowableT(Severity.ERROR, "Failed to form end tag", e);
      throw new JspTagException(e);
    }

    return Tag.EVAL_PAGE;
  }

  private void addPostParameters() throws JspTagException {
        
    HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
    
    LogonRequest logonRequest = new LogonRequest(this.pageContext);
    
    if (logonRequest.isPostRequest()) {
      JspWriter out = this.pageContext.getOut();

      String initialParametersString = request.getParameter(AbstractWebCallbackHandler.INITIAL_POST_PARAMETERS);
      if (initialParametersString == null || initialParametersString.trim().length() == 0) {
        if (AbstractWebCallbackHandler.isOriginalURLSet(request)) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Original URL is already set. This is not the first request. Current POST parameters will not be stored.");
          }
        } else {
          // This is the first request
          // Get POST parameters, encode them and set them in the hidden field
          SecurityRequest securityRequest = getSecurityRequest(request);
          if (securityRequest != null) {
            byte[] postData = securityRequest.getPostDataBytes();
            if (postData != null) { 
               if (postData.length > 0) {
                try {
                  initialParametersString = new String(BASE64Encoder.encodeN(postData));
                } catch (Exception e) {
                  LOCATION.traceThrowableT(Severity.ERROR, "Failed to encode POST parameters from the request.", e);
                  throw new JspTagException(e);
                }
               } else {  
                 if (LOCATION.beDebug()) {
                   LOCATION.debugT("No initial POST parameters received.");
                 }
               }
            } else {
              LOCATION.errorT("Failed to get POST parameters from the request.");
            } 
          } else {
            if (LOCATION.beWarning()) {
              LOCATION.warningT("Cannot get POST parameters, security request object is null.");
            }
          }
        }
      }

      try {
        // add initial POST parameters to html form
        if (initialParametersString != null) {
          out.println("<input type=\"hidden\" name=\"" + AbstractWebCallbackHandler.INITIAL_POST_PARAMETERS + "\" value=\"" + initialParametersString
              + "\" />");
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Initial POST parameters stored in the html form.");
          }
        }
      } catch (IOException e) {
        LOCATION.traceThrowableT(Severity.ERROR, "Failed to add initial POST parameters to the html form.", e);
        throw new JspTagException(e);
      }

      //handle additional login parameters 
      Set loginParameters = null;
      Object loginParamsAttribute = request.getAttribute(AbstractWebCallbackHandler.LOGIN_POST_PARAMETERS);
      if ((loginParamsAttribute != null) && (loginParamsAttribute instanceof Set)) {
        loginParameters = (Set) loginParamsAttribute;
      }

      if ((loginParameters != null) && (loginParameters.size() > 0)) {
        try {
          // add login POST parameters to the html form
          Iterator iter = loginParameters.iterator();
          while (iter.hasNext()) {
            Object param = iter.next();
            if ((param != null) && (param instanceof String)) {
              String paramName = (String) param;
              if (!isQueryParameter(request, paramName)) {
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues != null) {
                  for (int i = 0; i < paramValues.length; i++) {
                    String paramValue = paramValues[i];
                    if (paramValue != null) {
                      out.println("<input type=\"hidden\" name=\"" + paramName + "\" value=\"" + paramValue + "\" />");
                      if (LOCATION.beDebug()) {
                        LOCATION.debugT("Additional POST parameter stored in the html form: {0}", new Object[] {paramName});
                      }
                    }
                  }
                }
              }
            }
          }          
        } catch (Exception e) {
          LOCATION.traceThrowableT(Severity.ERROR, "Failed to add additional post parameters to the html form.", e);
          throw new JspTagException(e);
        }
      }
    }
  }

  private void addCreateCert() throws JspTagException {
    LogonRequest logonRequest = new LogonRequest(this.pageContext);
    String createcert = logonRequest.getParameter(LogonRequest.PARAMETER_CreateCert);
    if (createcert != null) {
      try {
        this.pageContext.getOut().println("<input type=\"hidden\" name=\"" + LogonRequest.PARAMETER_CreateCert + "\" value=\"" + createcert + "\" />");
      } catch (IOException e) {
        LOCATION.traceThrowableT(Severity.ERROR, "Failed to add the username to the html form.", e);
        throw new JspTagException(e);
      }
    }
  }

  private static boolean isSpecialParameter(String parameterName) {
    if (parameterName == null) {
      return false;
    }

    // do not replicate the special parameters because they are also added by
    // doStartTag(), otherwise conflicts may happen
    if (SPECIAL_PARAMETERS_LIST.contains(parameterName)) {
      return true;
    }
    return false;
  }

  private static boolean isQueryParameter(HttpServletRequest request, String parameterName) {

    if (parameterName == null) {
      return false;
    }
    String queryString = (String) request.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardQueryString);

    if (queryString == null) {
      return false;
    }

    if (queryString.startsWith(parameterName + "=") || queryString.indexOf("&" + parameterName + "=") > 0) {
      return true;
    }

    return false;
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
  
  private void addQueryStringParameters(String queryString, String[] excludeParametersList) throws JspTagException {
    if(queryString!=null && queryString.trim().length()>0){
      
      Set< String > excludeParametersSet = new HashSet< String >();
      if(excludeParametersList!=null){
        for(String s : excludeParametersList){
          excludeParametersSet.add(s);
        }
      }
      
      JspWriter out = this.pageContext.getOut();
        
      String[] queryParams = queryString.split("&");            
      for (String queryParam : queryParams) {   
        String[] param = queryParam.split("=");
        if(param.length==2){
          String name = param[0];
          String value = param[1];
          if(name.trim().length()>0){
            if(!excludeParametersSet.contains(name)){
              try{
                out.println("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\" />");
                if (LOCATION.beDebug()) {
                  LOCATION.debugT("Initial Query parameters are stored in the html form.");
                }
              }
              catch (Exception e) {
                LOCATION.traceThrowableT(Severity.ERROR, "Failed to add additional query parameters to the html form.", e);
                throw new JspTagException(e);
              }
            }
          }
        }                       
      }   
    }
  }
  
}

