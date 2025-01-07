package com.sap.portletbrowser.portletpage.component;

import java.io.IOException;

public class PortalPageNode extends PageNodeRenderer {
	private static final String portalpage_place_holder = "~com.sap.portletbrowser.portlet_page~";
	

	public PortalPageNode() {
		
	}

	public void renderBefore(PortalPageContext ctx) throws IOException {
		String portal_page_template = ctx.getTemplate("/WEB-INF/templates/portal_page_template.html").toString();
		int idx = portal_page_template.indexOf(portalpage_place_holder);
		ctx.getResponse().getWriter().write(portal_page_template.substring(0,idx));

	}
	
	public void renderAfter(PortalPageContext ctx) throws IOException {
		String portal_page_template = ctx.getTemplate("/WEB-INF/templates/portal_page_template.html").toString();
		int idx = portal_page_template.indexOf(portalpage_place_holder);
		ctx.getResponse().getWriter().write(portal_page_template.substring(idx+portalpage_place_holder.length()));

	}



}
