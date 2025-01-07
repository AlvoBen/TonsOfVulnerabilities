package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.applications.security.logon.pages.Utils;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class LinkTag extends TagSupport {
  static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private String type;
	private String linkClass;
	private String textClass;
	private String infoTextClass;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setLinkClass(String linkClass) {
		this.linkClass = linkClass;
	}
	
	public void setTextClass(String textClass) {
		this.textClass = textClass;
	}
	
	public void setInfoTextClass(String infoTextClass) {
		this.infoTextClass = infoTextClass;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		
		if ("certlogon".equalsIgnoreCase(type)) {
			if (SAPMLogonServlet.getAllowCertLogon()) {
				String alias = getCurrentAbsolutePath();
				
				if (myLoc.beDebug()) {
					myLoc.debugT("Go to certificate mapping login page.");
				}
				
				alias = getURLForScheme("https", alias);
				writeLink(alias, "GOTO_CERT_LOGON_PAGE1", "logonAppPage");
			}

		} 
		else if ("logonhelp".equalsIgnoreCase(type)) {
			if (SAPMLogonServlet.getLogonHelp()) {
				writeInfoText("LOGON_IN_PROBLEM");
				writeLink("/webdynpro/dispatcher/sap.com/tc~sec~ume~wd~enduser/LogonHelpApp?newWindowOpened=true", "GET_SUPPORT", "_blank");
			}
	
		} 
		else if ("userPasswordLogon".equalsIgnoreCase(type)) {
			if (SAPMLogonServlet.getAllowCertLogon()) {
				writeInfoText("xmsg_SAVE_CERT_INFO");
				
				try {
					JspWriter out = pageContext.getOut();
					out.println("<br>");
				} catch (IOException e) {
					myLoc.traceThrowableT(Severity.ERROR, "Failed to write", e);
				}
				
				String alias = getCurrentAbsolutePath();
				
				if (myLoc.beDebug()) {
					myLoc.debugT("Return to normal login page.");
				}
				
				alias = getURLForScheme("http", alias);
				writeLink(alias, "xlnk_goto_normal_logon", "logonAppPage");
			}
		} 
		else if ("selfReg".equalsIgnoreCase(type)) {
			if (SAPMLogonServlet.getSelfReg()) {
				writeInfoText("NEW_USERS");				
				
				LogonRequest logonRequest = new LogonRequest(this.pageContext);
				String selfRegUrl = logonRequest.getSelfRegistrationAlias();
				
				writeLink(selfRegUrl, "SIGN_UP", "_blank");
			}
		}
		
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() {
		return Tag.EVAL_PAGE;
	}
  
	private String getCurrentAbsolutePath() {
		String alias = null;
		
		LogonRequest logonRequest = new LogonRequest(pageContext);
		String URI = (String) logonRequest.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardRequestURI);
		String queryString = (String) logonRequest.getAttribute(LogonRequest.ATTRIBUTE_ServletForwardQueryString);
		
		if (queryString != null) {
			queryString = Utils.escapeURL(queryString);
			alias = URI + "?" + queryString;
		} else {
			alias = URI;
		}
		
		if (myLoc.beDebug()) {
			myLoc.debugT("The current absolute path is " + alias);
		}
		
		return alias;
	}
	
  private String getURLForScheme(String scheme, String path) {
  	
    //if path is NULL then method respClass.getURLForScheme throws WebIllegalArgumentException
    //instead of stack trace we will only trace a warning
    if( path == null ) {
      myLoc.warningT("Path can not be null.");
      return null;
    }
    
    boolean inPortal = BeanFactory.inPortal(pageContext);
  	ServletResponse response = pageContext.getResponse();
  	
  	if (inPortal) {
  		try {
  			response = ((ServletResponseWrapper) response).getResponse();
  		} catch (Exception e) {
  			myLoc.traceThrowableT(Severity.ERROR, "Cannot get the actual response from the portal.", e);
  		}
  	}
  	 
    Class respClass = response.getClass();
    Method m;
    //TODO - remove reflection 
    try {
    	m = respClass.getMethod("getURLForScheme", new Class[] {String.class, String.class});
    } catch (Exception e) {
    	myLoc.traceThrowableT(Severity.ERROR, "Response is not instanceof SapHttpServletResponse", e);
    	return path;
    }
    
    String ret;
    try {
    	ret = (String) m.invoke(response, new Object[] {scheme, path});
    } catch (Exception e) {
    	myLoc.traceThrowableT(Severity.ERROR, "Failed to get URL for Scheme: " + scheme, e);
    	return path;
    }  
    
    if (myLoc.beDebug()) {
    	myLoc.debugT("The URL with changed scheme is " + ret);
    }   
    
    return ret;
  }
	
	private void writeLink(String alias, String textKey, String target) throws JspTagException {
		JspWriter out = pageContext.getOut();
		ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);

		try {
			out.print("<a href=\"");
			out.print(alias);
			out.print("\"");
			out.print(" target=\"" + target + "\"");
			
			if (linkClass != null) {
				out.print(" class=\"");
				out.print(linkClass);
				out.print("\"");
			}
			
			out.print(">");

			if (textClass != null) {
				out.print("<span class=\"");
				out.print(textClass);
				out.print("\">");
			}
			
	    out.print(logonLocale.get(textKey));
	    
	    if (textClass != null) {
	    	out.print("</span>");
	    }
	    
	    out.print("</a>");
		} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
			throw new JspTagException(e);
		}
		
	}
	
	private void writeInfoText(String textKey) throws JspTagException {
		JspWriter out = pageContext.getOut();
		ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
		
		try {
			if (infoTextClass != null) {
				out.print("<span class=\"");
				out.print(infoTextClass);
				out.print("\">");
			}
			
	    out.print(logonLocale.get(textKey));
	    
	    if (infoTextClass != null) {
	    	out.print("</span>");
	    }
		} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to write additional text for " + type, e);
			throw new JspTagException(e);
		}
	}
}
