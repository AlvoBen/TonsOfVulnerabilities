package com.sap.portletbrowser.portletpage.component;

import java.io.IOException;

public class DefaultNode  extends PageNodeRenderer{
	
	public DefaultNode(){
		
	}
	
	
	public  void renderBefore(PortalPageContext ctx) throws IOException{
		StringBuilder template = ctx.getTemplate("/WEB-INF/templates/default_portal_template.html"); 
        ctx.getResponse().getWriter().write(template.toString());				
	}
	
	public  void renderAfter(PortalPageContext ctx){
	}
	
	
	
}
