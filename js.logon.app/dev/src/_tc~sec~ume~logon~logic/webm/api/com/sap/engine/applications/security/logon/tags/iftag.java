package com.sap.engine.applications.security.logon.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.pages.LogoutPageServlet;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.security.api.UMException;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class IfTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private String display;
	
	public void setDisplay(String type) {
		this.display = type;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {

	  LogonRequest logonRequest = new LogonRequest(this.pageContext);
	  
		if ("FederateUserWarning".equalsIgnoreCase(display)) {
			if(logonRequest.hasAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationRequired) && !logonRequest.hasErrorBean()){
				return Tag.EVAL_BODY_INCLUDE;
			}			
			return Tag.SKIP_BODY;
		}
		
		if ("FederateUserChkBox".equalsIgnoreCase(display)) {			
			if(logonRequest.hasAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationRequired)){ 
				return Tag.EVAL_BODY_INCLUDE;
			}			
			return Tag.SKIP_BODY;
		}

		if ("SecondBrandImage".equalsIgnoreCase(display)) {
			try {
	    	HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
				String secondImage = TenantFactory.getInstance().getLogonBrandingImage2(request );	    	
				if ((secondImage != null) && !"".equals(secondImage)) {
					return Tag.EVAL_BODY_INCLUDE;
	    	}				
				return Tag.SKIP_BODY;
			} 
			catch (UMException e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to check condition " + display, e);
				throw new JspTagException(e);
			}
		} 
		
		if ("CertLogonLink".equalsIgnoreCase(display)) {
			if (SAPMLogonServlet.getAllowCertLogon() ) {
				return Tag.EVAL_BODY_INCLUDE;
			}			
			return Tag.SKIP_BODY;
		} 
		
		if ("LogonHelpLink".equalsIgnoreCase(display)) {
			if (SAPMLogonServlet.getLogonHelp()) {
				return Tag.EVAL_BODY_INCLUDE;
			}			
			return Tag.SKIP_BODY;
		}
		
		if ("errormsg".equalsIgnoreCase(display)) {
		  if (logonRequest.hasErrorBean()) {
		  	return Tag.EVAL_BODY_INCLUDE;
		  } 
		  return Tag.SKIP_BODY;
		}
		
		if ("selfreg".equalsIgnoreCase(display)) {
			if (SAPMLogonServlet.getSelfReg()) {
				return Tag.EVAL_BODY_INCLUDE;
			}			
			return Tag.SKIP_BODY;
		}
		
		if ("authscheme".equalsIgnoreCase(display)) {
			boolean inPortal = BeanFactory.inPortal(pageContext);
			IAccessToLogic proxy = BeanFactory.getProxy(pageContext);
			
			if (inPortal) {
		    String reqscheme = proxy.getRequiredAuthScheme();		  
		    if (reqscheme == null) {
		    	return Tag.EVAL_BODY_INCLUDE;
		    }
			}			
			return Tag.SKIP_BODY;
		}
		
		if ("LogonAgainButton".equalsIgnoreCase(display)) {
			if (LogoutPageServlet.getRedirectURL(pageContext.getRequest()) != null) {
				return Tag.EVAL_BODY_INCLUDE;
			} 
			else {
				if (myLoc.beInfo()) {
					myLoc.infoT("Redirect URL is missing. The logon button will not be shown.");
				}				
				return Tag.SKIP_BODY;
			}
		} 
    
    if ("createcert".equalsIgnoreCase(display)) {
    	IUMConfiguration umeDynamicProps = InternalUMFactory.getConfiguration();
	    String value = umeDynamicProps.getStringDynamic(ILoginConstants.CERTIFICATE_ENROLL, ILoginConstants.CERTIFICATE_ENROLL_DEFAULT_VALUE);
	    if ("opt-in".equalsIgnoreCase(value) || "opt-out".equalsIgnoreCase(value)) {
	      return Tag.EVAL_BODY_INCLUDE;
	    }      
      return Tag.SKIP_BODY;
    }
		
	  if ("logoutErrorMsg".equalsIgnoreCase(display)) {
		  String  logoutStatus = logonRequest.getParameter(SAPMLogonServlet.LOGOUT_SUCCESS_STATUS);
		  if ("false".equalsIgnoreCase(logoutStatus)) {
			 return Tag.EVAL_BODY_INCLUDE;
		  } 
		  return Tag.SKIP_BODY;
		}
		
		if ("logoutSuccessMsg".equalsIgnoreCase(display)) {
		  String  logoutStatus = logonRequest.getParameter(SAPMLogonServlet.LOGOUT_SUCCESS_STATUS);
		  if (!"false".equalsIgnoreCase(logoutStatus)) {
		    return Tag.EVAL_BODY_INCLUDE;
		  } 
		  return Tag.SKIP_BODY;
		}
	    
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() {
		return Tag.EVAL_PAGE;
	}

}
