package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.beans.ErrorBean;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.IAccessToLogic;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.IAuthScheme;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.security.core.util.taglib.EncodeHtmlTag;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class InputTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	private String type;
	private String styleClass;
	private String style;
	private String rows;
	private String cols;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public void setRows(String rows) {
		this.rows = rows;
	}
	
	public void setCols(String cols) {
		this.cols = cols;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		
		LogonRequest logonRequest = new LogonRequest(this.pageContext);
		
		if ("FederateUserCheckbox".equalsIgnoreCase(type)) {						
			try {
				writeInput("checkbox", LogonRequest.ATTRIBUTE_FederationRequired, LogonRequest.ATTRIBUTE_FederationRequired, " UNCHECKED");
			} 
			catch (Exception e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add input for " + type, e);
				throw new JspTagException(e);
			}
			
			try {
			  String federationIdP = (String)logonRequest.getAttributeOrParameter(LogonRequest.ATTRIBUTE_FederationIdP);
			  if(federationIdP!=null){
			    writeSimpleInput("hidden", LogonRequest.ATTRIBUTE_FederationIdP, LogonRequest.ATTRIBUTE_FederationIdP, federationIdP, null);
			  }
      } 
			catch (Exception e) {
        myLoc.traceThrowableT(Severity.ERROR, "Failed to add input for " + type, e);
        throw new JspTagException(e);
      }
			
		} 
		else if ("authscheme".equalsIgnoreCase(type)) {
			boolean inPortal = BeanFactory.inPortal(pageContext);
			
			if (inPortal) {
				IAccessToLogic proxy = BeanFactory.getProxy(pageContext);
		    String reqscheme = proxy.getRequiredAuthScheme();

		    try {
			    if (reqscheme != null) {
			      out.print("<input name=\"");
			      out.print(ILoginConstants.LOGON_AUTHSCHEME_ALIAS);
			      out.print("\" type=\"hidden\" value=\"");
			      out.print(EncodeHtmlTag.encode(reqscheme));
			      out.print("\">");
		      } else {
			      IAuthScheme[] asarr = proxy.getAuthSchemes();
			       
			      out.print("<select name=\"");
			      out.print(ILoginConstants.LOGON_AUTHSCHEME_ALIAS);
			      out.print("\" id=\"logonauthschemefield\" ");
	
						writeStyleClassIfProvided(out);

						out.print(">");	
			       
	          for (int i = 0; i < asarr.length; i++) {
	            if (!"anonymous".equals(asarr[i].getName()) ) {
			          out.print("<option value=\"");
			          out.print(asarr[i].getName());
			          out.print("\">");
			          out.print(asarr[i].getName());
			          out.print("</option>");
	            }
	          }
			        
	          out.print("</select>");
			    }
				} catch (IOException e) {
					myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
					throw new JspTagException(e);
				}
		  }
		
		} else if ("password".equalsIgnoreCase(type)) {
			writeSimpleInput("password", "logonpassfield", ILoginConstants.LOGON_PWD_ALIAS);

		} else if ("oldPassword".equalsIgnoreCase(type)) {
			writeSimpleInput("password", "logonoldpassfield", ILoginConstants.OLD_PASSWORD);

		} else if ("newPassword".equalsIgnoreCase(type)) {
			writeSimpleInput("password", "logonnewpassfield", ILoginConstants.NEW_PASSWORD);

		} else if ("confirmPassword".equalsIgnoreCase(type)) {
			writeSimpleInput("password", "logonretypepassfield", ILoginConstants.CONFIRM_PASSWORD);
			
		} else if ("username".equalsIgnoreCase(type)) {
			String longUid = getLongUid();
			
			try {
				String value = longUid != null
				? longUid
				: UMFactory.getProperties().getBoolean(TenantFactory.MULTI_TENANCY_PREFIXING, true)
						? ""
						: TenantFactory.getInstance().getTenantLogonPrefix((HttpServletRequest) pageContext.getRequest());
						
				writeSimpleTextInput("logonuidfield", ILoginConstants.LOGON_USER_ID, value);

			} catch (Exception e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
				throw new JspTagException(e);
			}
		
		} else if ("lastName".equalsIgnoreCase(type)) {
			writeSimpleTextInput("logonlastnamefield", "lastname");
		
		} else if ("firstName".equalsIgnoreCase(type)) {
			writeSimpleTextInput("logonfirstnamefield", "firstname");
			
		} else if ("email".equalsIgnoreCase(type)) {
			writeSimpleTextInput("logonemailfield", "email");
			
		} else if ("note".equalsIgnoreCase(type)) {
			try {
				out.print("<textarea id=\"logonnotefield\" name=\"notetoadmin\"");
				writeStyleClassIfProvided(out);
				
				if (cols != null) {
					out.println(" cols=\"");
					out.print(cols);
					out.print("\"");
				}
				
				if (rows != null) {
					out.println(" rows=\"");
					out.print(rows);
					out.print("\"");
					
				}
				out.print("></textarea>");
			} catch (Exception e) {
				myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
				throw new JspTagException(e);
			}
      
    } 
		else if ("createcert".equalsIgnoreCase(type)) {		  
      if (logonRequest.hasParameter(LogonRequest.PARAMETER_JUserName)) {
        if (logonRequest.hasParameter(LogonRequest.PARAMETER_CreateCert)) {
          writeInput("checkbox", "logoncreatecertfield", LogonRequest.PARAMETER_CreateCert, " CHECKED");
        } 
        else {
          writeInput("checkbox", "logoncreatecertfield", LogonRequest.PARAMETER_CreateCert, " UNCHECKED");
        }
      } 
      else {
        IUMConfiguration umeDynamicProps = InternalUMFactory.getConfiguration();
        String value = umeDynamicProps.getStringDynamic(ILoginConstants.CERTIFICATE_ENROLL, ILoginConstants.CERTIFICATE_ENROLL_DEFAULT_VALUE);
        
        if ("opt-in".equalsIgnoreCase(value)) {
          writeInput("checkbox", "logoncreatecertfield", LogonRequest.PARAMETER_CreateCert, " UNCHECKED");
        } 
        else if ("opt-out".equalsIgnoreCase(value)) {
          writeInput("checkbox", "logoncreatecertfield", LogonRequest.PARAMETER_CreateCert, " CHECKED");
        }
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
	
	private void writeSimpleTextInput(String id, String name) throws JspTagException {
		writeSimpleInput("text", id, name);
	}
	
	private void writeSimpleTextInput(String id, String name, String value) throws JspTagException {
		writeSimpleInput("text", id, name, value, null);
	}
	
	private void writeSimpleInput(String type, String id, String name) throws JspTagException {
		writeSimpleInput(type, id, name, null, null);
	}
  
  private void writeInput(String type, String id, String name, String additionalAttributes) throws JspTagException {
    writeSimpleInput(type, id, name, null, additionalAttributes);
  }
	
	private void writeSimpleInput(String type, String id, String name, String value, String additionalAttributes) throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		
		try {
			out.print("<input type=\"");
			out.print(type);
			out.print("\"");
			
			out.print(" id=\"");
			out.print(id);
			out.print("\" name=\"");
			out.print(name);
			out.print("\"");

			if (value != null) {
				out.print(" value=\"");
				out.print(EncodeHtmlTag.encode(value));
				out.print("\"");
			}

			//ACCESSIBILITY: We skip this due to Kaja, Kiran proposal:
			//If you want to make this work in PC cursor mode, you have to put everything in the title attribute and disassociate the label.
		  //The user name html input field is on the focus initially.		       			
		  if(name.equals(ILoginConstants.LOGON_USER_ID)){
  			ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);            
  			String label = logonLocale.get("USER");
  			if(label==null){
  			  label = "";
  			}
  			else{
  			  label = label + " *";
  			}        
  			
  			LogonRequest logonRequest = new LogonRequest(this.pageContext);
  			ErrorBean error = logonRequest.getErrorBean();      
  			if (error != null) {      
  			  ResourceBean logonMessage = BeanFactory.getLogonMessageBean(pageContext);
  			  if(logonMessage!=null){
  				String msg = logonMessage.print(error);                        
  				msg = msg.replaceAll("\"", "'");                        
  				label = label + " - " + msg;                                    
  			  }
  			}               
  			out.print(" title=\"");
  			out.print(label);
  			out.print("\"");
		  }

			writeStyleClassIfProvided(out);
      
      if (additionalAttributes != null) {
        out.print(additionalAttributes);
      }
			
			out.print(">");
		} catch (Exception e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add label for " + type, e);
			throw new JspTagException(e);
		}
	}
	
	private void writeStyleClassIfProvided(JspWriter out) throws IOException {
		if (styleClass != null) {
			out.print(" class=\"");
			out.print(styleClass);
			out.print("\"");
		}

		if (style != null) {
			out.print(" style=\"");
			out.print(style);
			out.print("\"");
		}
	}
	
	private String getLongUid() {
    String uid = null;
    
    try {
      uid = this.pageContext.getRequest().getParameter(ILoginConstants.LOGON_USER_ID);
    
      if (uid != null) {
        uid = uid.trim();
      }
    } catch (Exception e) {
      myLoc.traceThrowableT(Severity.ERROR, "getLongUid", "Exception occured- Message: LongUid is null", e);
    }
    
    if (myLoc.beDebug()) {
      myLoc.debugT("getLongUid", "uid = " + uid);
    }
    
    return uid;
		
	}
}
