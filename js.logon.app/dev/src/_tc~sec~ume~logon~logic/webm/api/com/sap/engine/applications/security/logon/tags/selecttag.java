package com.sap.engine.applications.security.logon.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.sap.engine.applications.security.logon.LogonRequest;
import com.sap.engine.applications.security.logon.SAPMLogonServlet;
import com.sap.engine.applications.security.logon.beans.ErrorBean;
import com.sap.engine.applications.security.logon.beans.ResourceBean;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SelectTag extends TagSupport {
	static final Location myLoc = Location.getLocation(AuthenticationTraces.LOGON_APPLICATION_LOCATION);
	
	private String type;
	private String styleClass;
	private String size;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public void setSize(String size) {
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		
		LogonRequest logonRequest = new LogonRequest(this.pageContext);
		
		try {
			if ("helpAction".equalsIgnoreCase(type)) {
				out.print("<select name=\"");
				out.print(SAPMLogonServlet.HELP_TYPE_ACTION);
				out.print("\" id=\"helptypefield\" ");

				if (styleClass != null) {
					out.print("class=\"");
					out.print(styleClass);
					out.print("\" "); 
				}
				
				if (size != null) {
					out.print("size=\"");
					out.print(size);
					out.print("\" "); 
				}
				
			  out.println(">");
			}
  	} catch (Exception e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to add select tag", e);
			throw new JspTagException(e);
		}
  	
  	try {
      if ("saml2IdP".equalsIgnoreCase(type)) {
        out.print("<select name=\"");
        out.print(LogonRequest.PARAMETER_Saml2IdP);
        out.print("\" id=\"saml2idpfield\" style=\"width:220px\" ");

        if (styleClass != null) {
          out.print(" class=\"");
          out.print(styleClass);
          out.print("\" "); 
        }
        
        if (size != null) {
          out.print(" size=\"");
          out.print(size);
          out.print("\" "); 
        }
        
        //ACCESSIBILITY: We skip this due to Kaja, Kiran proposal:
        //If you want to make this work in PC cursor mode, you have to put everything in the title attribute and disassociate the label.
        //The idp selection list in html is on the focus initially.                       
        ResourceBean logonLocale = BeanFactory.getLogonLabelBean(this.pageContext);              
        
        String msg = null;
        ErrorBean error = logonRequest.getErrorBean();      
        if (error != null) {      
          ResourceBean logonMessage = BeanFactory.getLogonMessageBean(pageContext);
          if(logonMessage!=null){
            msg = logonMessage.print(error);                                                                       
          }
        }             
        else if (logonRequest.hasAttribute(LogonRequest.ATTRIBUTE_Saml2IdPDiscovery)) {          
          msg = logonLocale.get("DISCOVER_IDP");
        } 
        
        String label = logonLocale.get("SAML2_IDP");
        if(label==null){
          label = "";
        }
        /*
        else{
          label = label + " *";
        } 
        */               
        
        if(msg!=null){
          msg = msg.replaceAll("\"", "'");                        
          label = label + " - " + msg;
        }
        
        out.print(" title=\"");
        out.print(label);
        out.print("\"");
                
        out.println(">");
      }
    } 
  	catch (Exception e) {
      myLoc.traceThrowableT(Severity.ERROR, "Failed to add select tag", e);
      throw new JspTagException(e);
    }
		
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspTagException {
		JspWriter out = this.pageContext.getOut();
		
		if ("saml2IdP".equalsIgnoreCase(type)) {
		  
		  try{
  		  LogonRequest logonRequest = new LogonRequest(this.pageContext);
  		  
  		  Map< String, String > saml2IdPs = null;
  		  Object objSaml2IdPs = logonRequest.getAttribute(LogonRequest.ATTRIBUTE_Saml2IdPDiscovery);  		  
  		  
  		  if(objSaml2IdPs!=null){
    		  if(objSaml2IdPs instanceof Map){
    		    saml2IdPs = (Map)objSaml2IdPs;  		      		    
    		  }
    		  else if(objSaml2IdPs instanceof String[]){
    		    String[] idps = (String[])objSaml2IdPs;
    		    saml2IdPs = new HashMap< String, String >();
    		    for(String idp : idps){
    		      saml2IdPs.put(idp, idp);
    		    }
    		  }
    		  else if(objSaml2IdPs instanceof String){
    		    String[] idps = ((String)objSaml2IdPs).split(LogonRequest.ATTRIBUTE_SEPARATOR);
    		    saml2IdPs = new HashMap< String, String >();
            for(String idp : idps){
              saml2IdPs.put(idp, idp);
            }
    		  }
  		  }
  		    		    		  
  		  if(saml2IdPs!=null){
  		    
  		    String selectedIdP = (String)logonRequest.getAttribute(LogonRequest.ATTRIBUTE_Saml2IdPDiscoveryLocal);
  		    if(selectedIdP==null){
  		      selectedIdP = (String)logonRequest.getAttribute(LogonRequest.ATTRIBUTE_Saml2IdPDiscoveryDefault);
  		    }
  		    
  		    Set< String > idpKeys = saml2IdPs.keySet();
    		  for(String saml2IdP : idpKeys){  		    
      		  out.print("<option value=\"");
            out.print(saml2IdP);
            out.print("\"");
            if(selectedIdP!=null && selectedIdP.equals(saml2IdP)){
              out.print(" selected=\"selected\"");
            }
            out.print(">");
            out.print(saml2IdPs.get(saml2IdP));
            out.println("</option>");
    		  }
  		  }
		  }
		  catch (Exception e) {
	      myLoc.traceThrowableT(Severity.ERROR, "Failed to write select end tag for SAML2 IdP Selection", e);
	      throw new JspTagException(e);
	    }
		}
		
		try {
			out.println("</select>");
		} 
		catch (IOException e) {
			myLoc.traceThrowableT(Severity.ERROR, "Failed to write select end tag", e);
			throw new JspTagException(e);
		}
		return Tag.EVAL_PAGE;
	}
	

}
