package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ButtonTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	private String type;
	private String style;
	private String styleClass;
	private String buttonType = "submit";
	
	public void setType(String type) {
		this.type = type;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
  	JspWriter out = this.pageContext.getOut();
  	ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
  	String name = null;
  	String value = null;
  	String onClick = null;
  	String thisButtonType = buttonType; 
  	String leftPadding = "";
  	
  	LogonRequest logonRequest = new LogonRequest(this.pageContext);
  	
  	if ("logon".equalsIgnoreCase(type) || "certLogon".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.UID_PASSWORD_LOGON_ACTION;
 			value = logonLocale.get("LOGON");
  		
  	} else if ("changePassword".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.CHANGE_PASSWORD_ACTION;
  		value = logonLocale.get("CHANGE");
  	
  	} else if ("cancelChangePassword".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_PAGE;
  		value = logonLocale.get("CANCEL");
  		onClick = "onClickCancel();";
  	
  	} else if ("submitHelpPage".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.SUBMIT_HELP_ACTION;
  		value = logonLocale.get("SUBMIT");  		
  	
  	} else if ("cancelHelpPage".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_PAGE;
  		value = logonLocale.get("CANCEL");
  		onClick = "onClickCancel();";
  		
  	} else if ("submitResetPassword".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.RESET_PASSWORD_EVENT;//"RPWFS";
  		value = logonLocale.get("SUBMIT");
  		
  	} else if ("cancelResetPassword".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_PAGE;
  		value = logonLocale.get("CANCEL");
  		onClick = "onClickCancel();";
  		
  	} else if ("submitLogonProblem".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.LOGON_PROBLEM_EVENT; //"OLPFS";
  		value = logonLocale.get("SUBMIT");
  		
  	} else if ("cancelLogonProblem".equalsIgnoreCase(type)) {
  		name = SAPMLogonServlet.SHOW_UID_PASSWORD_LOGON_PAGE;
  		value = logonLocale.get("CANCEL");
  		onClick = "onClickCancel();";
  	} else if ("logonAgain".equalsIgnoreCase(type)) {
  		value = logonLocale.get("RE_LOGIN");
  	}	else if ("GoBack".equalsIgnoreCase(type)) {
      value = logonLocale.get("GO_BACK");
    }else if ("saml2IdP".equalsIgnoreCase(type)) {
      value = logonLocale.get("GOTO");
    }else if ("skipIdP".equalsIgnoreCase(type)) {      
      if(!logonRequest.hasAttribute(LogonRequest.ATTRIBUTE_IdPDiscoveryShowLocalLoginPage)){
        return Tag.SKIP_BODY;
      }
      else{
        value = logonLocale.get("CANCEL");
        onClick = "document.skipIdPForm.submit();";
        thisButtonType = "button";
        leftPadding = "&nbsp;";
      }
    }
  		
  	try {
  	  out.print(leftPadding);
  		out.print("<input ");
  		
  		if (style != null) {
	  		out.print("style=\"");
	  		out.print(style);
	  		out.print("\" ");
  		}
  		
  		if (styleClass != null) {
  			out.print("class=\"");
  			out.print(styleClass);
  			out.print("\" ");
  		}
  		
  		out.print("type=\"");
  		out.print(thisButtonType);
  		out.print("\" ");
  		
  		if (name != null) {
  		  out.print("name=\"");
  		  out.print(name);
  		  out.print("\" ");
  		}
  		
  		if (value != null) {
  		  out.print("value=\"");
  		  out.print(value);
  		  out.print("\" ");
  		}
  		
  		if (onClick != null) {
  			out.print("onClick=\"");
  			out.print(onClick);
  			out.print("\" ");
  		}
  		
  		out.println(">");
  	} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add logon button", e);
			throw new JspTagException(e);
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
