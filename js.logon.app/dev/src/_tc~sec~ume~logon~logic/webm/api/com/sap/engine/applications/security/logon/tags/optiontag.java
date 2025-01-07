package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class OptionTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private String type;
	private String styleClass;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		ResourceBean logonLocale = BeanFactory.getLogonLabelBean(pageContext);
		String value = null;
		String text = null;
		
		
		if ("requestPasswordReset".equalsIgnoreCase(type)) {
			if ( SAPMLogonServlet.getPasswordReset() ) {
				value = SAPMLogonServlet.RESET_PASSWORD_ACTION;
				text = logonLocale.get("REQUEST_PASSWORD_RESET");
			} else {
				return Tag.SKIP_BODY;
			}
		} else if ("otherLogonProblem".equalsIgnoreCase(type)) {
			value = SAPMLogonServlet.LOGON_PROBLEM_ACTION;
			text = logonLocale.get("OTHER_LOGON_PROBLEM");
		} else {
			return Tag.SKIP_BODY;
		}
		
		try {
			out.print("<option ");
			
			if (styleClass != null) {
				out.print("class=\"");
				out.print(styleClass);
				out.print("\" ");
			}
			out.print("value=\"");
			out.print(value);
			out.print("\">");
			out.print(text);
								
  	} catch (Exception e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add form page", e);
			throw new JspTagException(e);
		}
		
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		
		try {
			out.println("</option>");
		} catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to write select end tag", e);
			throw new JspTagException(e);
		}
		return Tag.EVAL_PAGE;
	}
	

}
