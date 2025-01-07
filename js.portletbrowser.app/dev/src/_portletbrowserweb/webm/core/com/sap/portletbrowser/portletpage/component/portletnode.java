package com.sap.portletbrowser.portletpage.component;

import static com.sap.portletbrowser.RequestEncodings.ADD;
import static com.sap.portletbrowser.RequestEncodings.EQ;
import static com.sap.portletbrowser.RequestEncodings.FS;
import static com.sap.portletbrowser.RequestEncodings.INODE_ID;
import static com.sap.portletbrowser.RequestEncodings.PARAMS;
import static com.sap.portletbrowser.RequestEncodings.PORTAL_PAGE;
import static com.sap.portletbrowser.RequestEncodings.PORTLET_MODE;
import static com.sap.portletbrowser.RequestEncodings.PORTLET_NAME;
import static com.sap.portletbrowser.RequestEncodings.RENDER_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.REQUEST_TYPE;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;

import com.sap.portletbrowser.LogContext;
import com.sap.portletbrowser.RequestEncodings;
import com.sap.portletbrowser.spi.PortletNodeImpl;

public class PortletNode extends PageNodeRenderer {

	private PortletNodeImpl node;
	private static final String portlet_render_place_holder = "~com.sap.portletbrowser.portlet_render~";
	private static final String portlet_name_place_holder = "~com.sap.portletbrowser.portlet_name~";
	private static final String portlet_view_mode_place_holder = "~com.sap.portletbrowser.portlet_view_mode~";
	private static final String portlet_edit_mode_place_holder = "~com.sap.portletbrowser.portlet_edit_mode~";
	private static final String portlet_help_mode_place_holder = "~com.sap.portletbrowser.portlet_help_mode~";

	public PortletNode(PortletNodeImpl node) {
		this.node = node;
	}

	public void renderBefore(PortalPageContext ctx) throws IOException {
		String temp = ctx.getTemplate(
				"/WEB-INF/templates/portlet_node_template.html").toString();

		// substitue portlet name
		int index = temp.indexOf(portlet_name_place_holder);
		if (index >= 0) {
			temp = temp.replace(portlet_name_place_holder, node
					.getPortletName());
		}

		// substitue portlet mode links
		// prepare view link
		// servlet_path+PARAMS+PORTLET_NAME+EQ+pModule+FS+pname+ADD+REQUEST_TYPE+EQ+RENDER_REQUEST
		String url = ctx.getRequest().getContextPath()
				+ ctx.getRequest().getServletPath() + PARAMS + PORTLET_NAME
				+ EQ + node.getPortletApplicationName() + FS
				+ node.getPortletName() + ADD + REQUEST_TYPE + EQ
				+ RENDER_REQUEST + ADD;

		// add page id if present;
		if (ctx.getPageID() != null && ctx.getPageID().length() > 0) {
			url = url + PORTAL_PAGE + EQ + ctx.getPageID() + ADD;
		}

		url = url + INODE_ID + EQ + node.getContextName() + ADD;

		url = url + PORTLET_MODE + EQ;
		String view_url = url + PortletMode.VIEW.toString();
		String edit_url = url + PortletMode.EDIT.toString();
		String help_url = url + PortletMode.HELP.toString();

		index = temp.indexOf(portlet_view_mode_place_holder);
		if (index >= 0) {
			temp = temp.replace(portlet_view_mode_place_holder, view_url);
		}

		index = temp.indexOf(portlet_edit_mode_place_holder);
		if (index >= 0) {
			temp = temp.replace(portlet_edit_mode_place_holder, edit_url);
		}

		index = temp.indexOf(portlet_help_mode_place_holder);
		if (index >= 0) {
			temp = temp.replace(portlet_help_mode_place_holder, help_url);
		}
		index = temp.indexOf(portlet_render_place_holder);
		ctx.getResponse().getWriter().write(temp.substring(0, index));

	}

	public void renderChildren(PortalPageContext ctx) throws IOException {
		if (LogContext.PORTLETBROWSER_LOCATION.beDebug()) {
			LogContext.PORTLETBROWSER_LOCATION
					.debugT(">>>>>>>>>>>>>PORTLET RENDER: "
							+ node.getPortletName() + " with id: "
							+ node.getContextName());
		}

		try {
			if (RequestEncodings.RESOURCE_REQUEST.equals(ctx.getRequestType()) &&
			    this.node.getContextName().equals(ctx.getTargetedNode())){
	      ctx.getPortletContainer().serveResource(ctx.getRequest(),
          ctx.getResponse(), node);
			  
			}
			else {
			  ctx.getPortletContainer().render(ctx.getRequest(),
					ctx.getResponse(), node);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PortletException e) {
			ctx.getResponse().getWriter().write(e.getMessage());
		} catch (Exception e) {
			ctx.getResponse().getWriter().write(e.getMessage());
		}
	}

	public void renderAfter(PortalPageContext ctx) throws IOException {
		String temp = ctx.getTemplate(
				"/WEB-INF/templates/portlet_node_template.html").toString();
		int to = temp.indexOf(portlet_render_place_holder);
		ctx.getResponse().getWriter().write(
				temp.substring(to + portlet_render_place_holder.length()));
	}

}
