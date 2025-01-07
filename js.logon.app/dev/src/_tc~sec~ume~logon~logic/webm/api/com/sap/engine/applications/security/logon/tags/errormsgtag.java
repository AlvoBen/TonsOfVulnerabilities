package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ErrorBean;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.security.core.util.taglib.EncodeHtmlTag;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ErrorMsgTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	private String styleClass;
	private String imageClass;
	private String imageHeight;
	private String imageWidth;
	
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageClass(String imageClass) {
		this.imageClass = imageClass;
	}

	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		
	  JspWriter out = this.pageContext.getOut();
		
	  String msg = null;
	  
	  LogonRequest logonRequest = new LogonRequest(this.pageContext);
	  
	  ErrorBean error = logonRequest.getErrorBean();
	  ResourceBean logonMessage = BeanFactory.getLogonMessageBean(pageContext);

	  if (error != null) {
	  	msg = EncodeHtmlTag.encode(logonMessage.print(error));
	  } 
	  else if (logonRequest.hasAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationRequired)) {
	    ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
	    msg = logonLocale.get("FEDERATION_MESSAGE");
	    if(msg!=null){
	      String federationIdP = (String)logonRequest.getAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationIdP);
	      if(federationIdP!=null){
	       msg = msg.replaceFirst("0", federationIdP);
	      }
	    }
	  } 
	  else if ("false".equalsIgnoreCase(logonRequest.getParameter(SAPMLogonServlet.LOGOUT_SUCCESS_STATUS))) {
		  ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
		  msg = logonLocale.get("LOGOUT_FAILED");
	  }
	  else if (logonRequest.hasAttribute(LogonRequest.ATTRIBUTE_Saml2IdPDiscovery)) {
      ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);
      msg = logonLocale.get("DISCOVER_IDP");
    }

	  if (msg != null) {
	    String webpath = BeanFactory.getWebPath(pageContext);
	    try {
		    out.print("<span class=\"");
		    out.print(imageClass);
		    out.print("\">");

		    out.print("<img");
		    
		    if (imageHeight != null) {
			    out.print(" height=\"");
			    out.print(imageHeight);
			    out.print("\"");
		    }
		    
		    if (imageWidth != null) {
			    out.print(" width=\"");
			    out.print(imageWidth);
			    out.print("\"");
		    }
		    
		    out.print(" src=\"");
		    out.print(webpath);
		    out.print("css/common/1x1.gif\"></span>");
		    
		    out.print("<span");
		    if (styleClass != null) {
			    out.print(" class=\"");
			    out.print(styleClass);
			    out.print("\"");
		    }
		    
		    out.print(" tabindex=\"0\">");
		    out.print(msg);
		    out.print("</span>");
	    } catch (IOException e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add error message", e);
				throw new JspTagException(e);
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

}
