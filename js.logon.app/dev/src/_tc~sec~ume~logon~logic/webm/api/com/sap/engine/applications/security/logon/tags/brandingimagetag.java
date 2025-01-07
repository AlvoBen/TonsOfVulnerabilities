package com.sap.engine.applications.security.logon.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.ServletAccessToLogic;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.security.core.imp.TenantFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Krasimira Velikova (i032162)
 *
 */
public class BrandingImageTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	private static final String MAIN_IMAGE = "main";
	private static final String SECOND_IMAGE = "second";
	
	private String type;
	
	public void setType(String type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
  	JspWriter out = this.pageContext.getOut();
  	HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
  	
  	String webpath = BeanFactory.getWebPath(pageContext);
  	
  	if (webpath.length() > 0 && !webpath.endsWith("/")) {
  		webpath += "/";
  	}
  	
  	if (MAIN_IMAGE.equalsIgnoreCase(type)) {
			try {
			String imageURL = TenantFactory.getInstance().getLogonBrandingImage1(request);
			
			if ((imageURL != null) && !"".equals(imageURL)) {
	  			out.print("<img src=\"");
	  			out.print(ServletAccessToLogic.getAbsoluteURL(webpath, imageURL));
	  			out.print("\" alt=\"Branding Image\" border=\"0\">");
			}
	  	} catch (Exception e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add logon button", e);
				throw new JspTagException(e);
			}
  	
  	} else if (SECOND_IMAGE.equalsIgnoreCase(type)) {
			try {
      	String secondImage = TenantFactory.getInstance().getLogonBrandingImage2(request);
      	
      	if ((secondImage != null) && !"".equals(secondImage)) {
      		out.print("<img src=\"");
      		out.print(ServletAccessToLogic.getAbsoluteURL(webpath,secondImage));
      		out.print("\" alt=\"\" border=\"0\">");
      	}
	  	} catch (Exception e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add logon button", e);
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
