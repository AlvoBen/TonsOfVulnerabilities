package com.sap.portletbrowser;

import static com.sap.portletbrowser.LogContext.PORTLETBROWSER_LOCATION;
import static com.sap.portletbrowser.RequestEncodings.ADD;
import static com.sap.portletbrowser.RequestEncodings.EQ;
import static com.sap.portletbrowser.RequestEncodings.FS;
import static com.sap.portletbrowser.RequestEncodings.PARAMS;
import static com.sap.portletbrowser.RequestEncodings.PORTAL_PAGE;
import static com.sap.portletbrowser.RequestEncodings.PORTAL_PAGE_DEFAULT_LAYOUT;
import static com.sap.portletbrowser.RequestEncodings.PORTLET_NAME;
import static com.sap.portletbrowser.RequestEncodings.RENDER_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.REQUEST_TYPE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.services.portletcontainer.PortletContainerInterface;
import com.sap.engine.services.portletcontainer.api.PortletAdmin;
import com.sap.engine.services.portletcontainer.api.PortletContainer;

public class PortletBrowser extends HttpServlet{

	/**
	 * JLIN check
	 */
	  private static final long serialVersionUID = 6567127395194241393L;
	  private PortletAdmin admin;
	  private PortletContainer portlet_container;
	  private String servlet_path;
	  private String portal_page_admin_path;
	  
	  public void init(ServletConfig cfg) throws ServletException {
	    super.init(cfg);
	    servlet_path = cfg.getServletContext().getContextPath()+FS+"RenderInvoker";
	    portal_page_admin_path = cfg.getServletContext().getContextPath()+FS+"PortalPageAdmin";
		PortletContainerInterface pci = null;

	    try {
	      Context context = new InitialContext();
	      pci = (PortletContainerInterface) context.lookup("java:comp/env/scrportletcontainer");
	      admin = pci.getPortletAdmin();
	      portlet_container = pci.getPortletContainer();
	    } catch (Exception e) {
	      throw new ServletException("Cannot lookup PortletContainerInterface", e);
	    }


			if (portlet_container == null){
				throw new ServletException("Can not obtain reference to the portlet container.");
			}

			if (admin == null){
				throw new ServletException("Can not obtain reference to the admin interface of the portlet container.");
			}
	  }

	  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    StringBuilder buffer = new StringBuilder();
		    buffer.append("<html><head>");
			buffer.append("<title>PortletBrowser</title>");
			buffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/style/ur_ie6.css\">");
			buffer.append("<script src=\"resources/js/sampleHandler.js\" type=\"text/javascript\"></script>");
			buffer.append("<script src=\"resources/js/sapUrMapi_ie6.js\" type=\"text/javascript\"></script>");
			buffer.append("<body>");
			buffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" bgcolor=\"#d7e5f0\">");
		    
			//add portal page to the links
			addPortlePageToResponseBuffer(buffer);
			addPortalPageAdmintoResponseBuffer(buffer);
			
			
		    //get Vendors
			String[] vendors = admin.getVendors();
			Arrays.sort(vendors);
			if (vendors != null){
				for (String vendor: vendors) {
					String[] apps = admin.getPortletApplicationNames(vendor);
					Arrays.sort(apps);
					for(String application: apps){
					      String[] modules = admin.getPortletModuleNames(application);
					      Arrays.sort(modules);
					  	  for (String pmodule : modules) {
							    String[] prtl = admin.getAllPortlets(pmodule);
							    Arrays.sort(prtl);
							    for (String pname: prtl) {
								    if (PORTLETBROWSER_LOCATION.beDebug()){
								    	PORTLETBROWSER_LOCATION.debugT("Portlet Found: "+pname);
								    }
								    addPortletToResponseBuffer(buffer,pmodule,pname);
							    }
						    }
					    }
				}
			
			}else{
			  buffer.append("<tr><td>No portlets found</td></tr>");
		  }
		   
		   
	    buffer.append("</table>");
	    buffer.append("</body>");
	    buffer.append("</html>");
	    writeresponse(buffer,response);
	    response.getWriter().flush();
	  }

	  private void addPortletToResponseBuffer(StringBuilder buffer, String pModule, String pname) {
		buffer.append("</tr>");
	    buffer.append("<td width=\"300px;\" valign=\"top\">");
	    buffer.append("<div class=\"style21\">");
	    buffer.append("<a class=\"urLnkPB\" target=\"portlet\" href=\""+servlet_path+PARAMS+PORTLET_NAME+EQ+pModule+FS+pname+ADD+REQUEST_TYPE+EQ+RENDER_REQUEST+"\">");
	    buffer.append(pname);
	    buffer.append("</a>");
	    buffer.append("</div>");
	    buffer.append("</td>");
	    buffer.append("</tr>");
		
	 }
	  
	  
	  private void addPortlePageToResponseBuffer(StringBuilder buffer) {
		  
		    
		    
		    //
			buffer.append("</tr>");
		    buffer.append("<td width=\"300px;\" valign=\"top\">");
		    buffer.append("<div class=\"style21\">");
		    buffer.append("<a class=\"urLnkPB\" target=\"portlet\" href=\""+servlet_path+PARAMS+PORTAL_PAGE+EQ+getUserPortalPageID()+"\">");
		    buffer.append("PortalPage");
		    buffer.append("</a>");
		    buffer.append("</div>");
		    buffer.append("</td>");
		    buffer.append("</tr>");
			
		 }
	  
	  
	  private void addPortalPageAdmintoResponseBuffer(StringBuilder buffer){
		    buffer.append("</tr>");
		    buffer.append("<td width=\"300px;\" valign=\"top\">");
		    buffer.append("<div class=\"style21\">");
		    buffer.append("<a class=\"urLnkPB\" target=\"portlet\" href=\""+portal_page_admin_path+"\">");
		    buffer.append("PortalPageAdmin");
		    buffer.append("</a>");
		    buffer.append("</div>");
		    buffer.append("</td>");
		    buffer.append("</tr>");
		  
	  }


	private String getUserPortalPageID() {
		//here we should get the portlet page layout id associated with the user.
	    // if there is no such id the default will be used
	    String pid = PORTAL_PAGE_DEFAULT_LAYOUT;
		return pid;
	}

	private void writeresponse(final StringBuilder buffer, final HttpServletResponse response) throws IOException {
			response.getWriter().write(buffer.toString());
	        buffer.delete(0,buffer.length());
	    }

	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException,
	    IOException {
	      doGet(request, response);
	  }

	  public static String getExceptionStackTrace(Throwable t) {
	    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
	    t.printStackTrace(new PrintStream(ostr));
	    return ostr.toString();
	  }


}
