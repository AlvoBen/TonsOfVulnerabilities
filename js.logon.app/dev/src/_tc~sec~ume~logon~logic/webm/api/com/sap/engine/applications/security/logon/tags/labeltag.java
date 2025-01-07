package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class LabelTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private String type;
	private String styleClass;
	private String flagReqClass;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public void setFlagReqClass(String flagReqClass) {
		this.flagReqClass = flagReqClass;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		JspWriter out = pageContext.getOut();
		ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
		String label = null;
		String labelFor = null;
		boolean isRequired = false;
		
		if ("authscheme".equalsIgnoreCase(type)) {
			boolean inPortal = BeanFactory.inPortal(pageContext);
			IAccessToLogic proxy = BeanFactory.getProxy(pageContext);
			String reqscheme = proxy.getRequiredAuthScheme();
			
			if (inPortal && reqscheme == null) {
				label = logonLocale.get("xfld_AUTHSCHEME");
	    	labelFor = "logonauthschemefield";
		  } else {
		  	return Tag.EVAL_BODY_INCLUDE;
		  }
		
		} else if ("password".equalsIgnoreCase(type)) {
			label = logonLocale.get("PASSWORD");
			isRequired = true;
			labelFor = "logonpassfield";

		} else if ("oldPassword".equalsIgnoreCase(type)) {
			label = logonLocale.get("OLD_PASSWORD");
			isRequired = true;
			labelFor = "logonoldpassfield";

		} else if ("newPassword".equalsIgnoreCase(type)) {
			label = logonLocale.get("NEW_PASSWORD");
			isRequired = true;
			labelFor = "logonnewpassfield";

		} else if ("confirmPassword".equalsIgnoreCase(type)) {
			label = logonLocale.get("CONFIRM_NEW_PASSWORD");
			isRequired = true;
			labelFor = "logonretypepassfield";
			
		} else if ("username".equalsIgnoreCase(type)) {
			label = logonLocale.get("USER");
			isRequired = true;			
		  //ACCESSIBILITY: We skip this due to Kaja, Kiran proposal:
      //If you want to make this work in PC cursor mode, you have to put everything in the title attribute and disassociate the label.
			//labelFor = "logonuidfield"; 
				
		} else if ("lastName".equalsIgnoreCase(type)) {
			label = logonLocale.get("LAST_NAME");
			isRequired = true;
			labelFor = "logonlastnamefield"; 
		
		} else if ("firstName".equalsIgnoreCase(type)) {
			label = logonLocale.get("FIRST_NAME");
			isRequired = true;
			labelFor = "logonfirstnamefield"; 

		} else if ("email".equalsIgnoreCase(type)) {
			label = logonLocale.get("EMAIL");
			isRequired = true;
			labelFor = "logonemailfield"; 

		} else if ("note".equalsIgnoreCase(type)) {
			label = logonLocale.get("NOTE_TO_ADMIN");
			isRequired = true;
			labelFor = "logonnotefield"; 
			
		} else if ("logout".equalsIgnoreCase(type)) {
			label = logonLocale.get("LOGOUT_SUCCEEDED");
			labelFor = "logoutfield"; 
      
    } else if ("createcert".equalsIgnoreCase(type)) {
      label = logonLocale.get("CREATE_CERTIFICATE");
      labelFor = "logoncreatecertfield";
		}
    else if ("FederateUserCheckbox".equalsIgnoreCase(type)) {
      label = logonLocale.get("FEDERATE_LOCAL_ACCOUNT");
      labelFor = LogonRequest.ATTRIBUTE_FederationRequired;
    }				
    else if ("saml2IdP".equalsIgnoreCase(type)) {
      label = logonLocale.get("SAML2_IDP");
      isRequired = false;      
      //ACCESSIBILITY: We skip this due to Kaja, Kiran proposal:
      //If you want to make this work in PC cursor mode, you have to put everything in the title attribute and disassociate the label.
      //labelFor = "logonuidfield";         
    } 
		
		try {
      out.print("<label class=\"");
      out.print(styleClass);
      out.print("\"");
      
      if (labelFor != null) {
      	out.print(" for=\"");
      	out.print(labelFor);
      	out.print("\"");
      }      
             
      out.print("><nobr>");
      
      out.print(label);
      
      if (isRequired) {        
      	out.print("<span class=");
      	out.print(flagReqClass);
      	out.print(">&nbsp;*</span>");      	
      }
      
      out.print("</nobr>");
      out.println("</label>");
			
		} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
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
