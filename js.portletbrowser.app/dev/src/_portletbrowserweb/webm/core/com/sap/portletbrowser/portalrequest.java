/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.portletbrowser;

import java.util.LinkedList;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * This is a helper class for parsing a portlet requests
 */
public class PortalRequest {
	
	private static final String PORTLET_NAME = "portletName";
  private HttpServletRequest request;
	private PortletDescription[] res = new PortletDescription[0];
	
	/**
	 * @param request
	 */
	public PortalRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean  isPortletRequest(){
		return ((request.getParameter(PORTLET_NAME) != null) && 
		        (request.getParameter(PORTLET_NAME).length() > 0)) ||
		        ((request.getParameter(RequestEncodings.PORTLET_NAME) != null) && 
		         (request.getParameter(RequestEncodings.PORTLET_NAME).length() > 0));
	}

	/**
	 * @return
	 */
	public boolean isActionRequest() {
	  return isType(RequestEncodings.ACTION_REQUEST);
	}
	

  public boolean isResourceRequest() {
    return isType(RequestEncodings.RESOURCE_REQUEST);
  }
  
  public boolean isRenderRequest() {
    return isType(RequestEncodings.RENDER_REQUEST);
  }
  
	
	public PortletDescription[] getDescription(){
		if (res.length == 0){
		    LinkedList<PortletDescription> lst = new LinkedList<PortletDescription>();
            String[] portletNames = new String[0];
            if (request.getParameter(PORTLET_NAME) != null && request.getParameter(PORTLET_NAME).length() > 0){
			    portletNames = request.getParameterValues(PORTLET_NAME);
            }else if(request.getParameter(RequestEncodings.PORTLET_NAME) != null && request.getParameter(RequestEncodings.PORTLET_NAME).length() > 0){
                 portletNames = request.getParameterValues(RequestEncodings.PORTLET_NAME);
            }
			if (portletNames != null && portletNames.length > 0){
			 for (int i = 0; i < portletNames.length; i++) {
			   int idx = portletNames[i].indexOf('/');	
			   String application = portletNames[i].substring(0,idx);
			   String portlet = portletNames[i].substring(idx+1,portletNames[i].length());
                 
			   PortletDescription desc = new PortletDescription(portlet,application);

			   lst.add(desc);
			 }  
	        }
			if (lst.size() > 0){
			  res =  (PortletDescription[]) lst.toArray(res);
			}
		}	
		return res;
	}
	/**
	 * @return Returns the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public WindowState getRequestedPortletWindowState(){
		if (request.getParameter(RequestEncodings.WINDOW_STATUS) != null ) {
			if (request.getParameter(RequestEncodings.WINDOW_STATUS).equals(WindowState.MAXIMIZED.toString())){
				return WindowState.MAXIMIZED;
			}
			if (request.getParameter(RequestEncodings.WINDOW_STATUS).equals(WindowState.MINIMIZED.toString())){
				return WindowState.MINIMIZED;
			}
			if (request.getParameter(RequestEncodings.WINDOW_STATUS).equals(WindowState.NORMAL.toString())){
				return WindowState.NORMAL;
			}
			
		}
		return null;
	}	
	
	public PortletMode getRequestedPortletMode(){
		if (request.getParameter(RequestEncodings.PORTLET_MODE) != null ) {
			if (request.getParameter(RequestEncodings.PORTLET_MODE).equals(PortletMode.EDIT.toString())){
				return PortletMode.EDIT;
			}
			if (request.getParameter(RequestEncodings.PORTLET_MODE).equals(PortletMode.HELP.toString())){
				return PortletMode.HELP;
			}
			if (request.getParameter(RequestEncodings.PORTLET_MODE).equals(PortletMode.VIEW.toString())){
				return PortletMode.VIEW;
			}
			
		}
		return null;
	}

	public String getResourceID(){
	  return request.getParameter(RequestEncodings.RESOURCE_ID);
	}

	public String getCachability(){
    return request.getParameter(RequestEncodings.CACHABILITY);
  }
	
    public String getPortalPageID(){
        return request.getParameter(RequestEncodings.PORTAL_PAGE);
    }

    
    private boolean isType(String type){
      String parameter = request.getParameter(RequestEncodings.REQUEST_TYPE);
      return ((parameter != null) && (parameter.equals(type)));
    }
    
    
}


