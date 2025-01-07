package com.sap.portletbrowser.portletpage.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.services.portletcontainer.api.PortletContainer;
import com.sap.portletbrowser.RequestEncodings;
import com.sap.portletbrowser.template.TemplateBuilder;

public class PortalPageContext {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PortletContainer portlet_container;
	private TemplateBuilder builder;
	private String targetedNode;
	private String requestType = RequestEncodings.RENDER_REQUEST;
	
	public PortalPageContext(HttpServletRequest request, HttpServletResponse response, PortletContainer container, TemplateBuilder template, String page_id){
		this.request = request;
		this.response = response;
		this.portlet_container = container;
		this.builder = template;
	}
	
	
	public HttpServletRequest getRequest(){
		return request;
	}
	
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	
	public PortletContainer getPortletContainer(){
		return portlet_container;
	}
	
	
	public StringBuilder getTemplate(String key){
		return builder.getTemplate(key);
	}
	
	
	public String getPageID(){
		return request.getParameter(RequestEncodings.PORTAL_PAGE);
	}
	
	 public String getTargetedNode(){
	    return targetedNode;
	 }
	
	public PageNodeRenderer getPortalPage(){
		return null;
	}


  public void setTargetedNode(String portletNodeContext) {
    this.targetedNode = portletNodeContext;
  }


  public String getRequestType() {
    return requestType;
  }


  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

}
