package com.sap.engine.applications.security.logon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.security.api.IUser;
import com.sap.security.api.UMFactory;
import com.sap.security.api.UMRuntimeException;
import com.sap.security.api.logon.IAuthScheme;
import com.sap.tc.logging.Location;
import com.sap.engine.applications.security.logon.pages.Utils;


public class ServletAccessToLogic implements IAccessToLogic {

  private HttpServletRequest req;
  private HttpServletResponse resp;

  final static String LOGON_SERVLET_ALIAS      = "/logon_app/logonServlet";
  final static Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);

  public ServletAccessToLogic(HttpServletRequest req, HttpServletResponse resp) {
    this.req = req;
    this.resp = resp;
  } 

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#getContextURI()
   */
  public String getContextURI() {
    return this.req.getContextPath();
  } 

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#getActiveUser()
   */
  public IUser getActiveUser() {
    return UMFactory.getAuthenticator().forceLoggedInUser(this.req, this.resp);
  } 

  /**
   * Constructs the absolute path for the given alias. If the given alias is 
   * null it reconstructs the current absolute path containing the current 
   * request parameters.
   * 
   * @param alias - The alias that points to a specific login page. It's not protected 
   * against XSS attacks.
   * @return The absolute path for the given alias.
   */
  public String getAlias(String alias) {
  	final String METHOD = "getAlias";
  	String result = null;
  	
  	if (myLoc.bePath()) {
  		myLoc.entering(METHOD, new Object[] {alias});
  	}
  	
  	try {
  		if (alias == null) {
  			String URI = (String) this.req.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardRequestURI);
  			String queryString = (String) this.req.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardQueryString);
  			
  			if (queryString != null) {
  				queryString = Utils.escapeURL(queryString);
  				result = URI + "?" + queryString;
  			} else {
  				result = URI;
  			}
  		} else {
  			String requestURI = (String) this.req.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardRequestURI);
  			result = requestURI + "?" + alias;
  		}
  	} finally {
  		if (myLoc.bePath()) {
  			myLoc.exiting(METHOD, result);
  		}
  	}
  	
  	return result;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#getAlias(java.lang.String, java.lang.String)
   */
  public String getAlias( String context, String event ) {
    return context;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#getRequiredAuthScheme()
   */
  public String getRequiredAuthScheme() {
    //return mySAPProperties.get(CERT_AUTHSCHEME, CERT_LOGIN_DEFAULT);
    // no use for authscheme in standalone.
    return null;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#getAuthSchemes()
   */
  public IAuthScheme[] getAuthSchemes() {
    throw new UMRuntimeException("getAuthSchemes not implemented");
  }

  /* (non-Javadoc)
   * @see com.sap.engine.interfaces.security.auth.IAccessToLogic#isAction(java.lang.String)
   */
  public boolean isAction(String s) {
    // which type of help?
    String action = this.req.getParameter(s);

    if (action == null) {
      String helpAction = this.req.getParameter("helpActionPage");
      action = (s.equals(helpAction) ? helpAction : null);
    }

    return (action != null);
  }

  public static String getAbsoluteURL(String webpath, String url) {
    if (webpath == null || "".equals(webpath)) {
      //nothing to add
      return url;
    }

    if (url == null || "".equals(url)) {
      //nothing to add
      return webpath;
    }

    String tmpcont = url.toLowerCase();

    if (tmpcont.startsWith("/") || tmpcont.startsWith("http:")
        || tmpcont.startsWith("https:")) {
      //url is already absolute
      return url;
    }

    //url is relative to web path
    StringBuffer result = new StringBuffer(webpath.length() + url.length());
    result.append(webpath);
    result.append(url);
    return result.toString();
  }
}