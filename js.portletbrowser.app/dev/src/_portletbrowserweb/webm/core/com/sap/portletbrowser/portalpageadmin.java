package com.sap.portletbrowser;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sap.engine.services.portletcontainer.PortletContainerInterface;
import com.sap.engine.services.portletcontainer.api.PortletAdmin;
import com.sap.portletbrowser.portletpage.PageBuilder;
import com.sap.portletbrowser.portletpage.component.LayoutNode.Direction;
import com.sap.portletbrowser.portletpage.xml.Layout;
import com.sap.portletbrowser.portletpage.xml.ObjectFactory;
import com.sap.portletbrowser.portletpage.xml.PortalPage;
import com.sap.portletbrowser.portletpage.xml.Portlet;
import com.sap.tc.logging.Severity;

@SuppressWarnings("serial")
public class PortalPageAdmin extends HttpServlet {

	PortletAdmin admin;
	PortalPage pp;
	transient PageBuilder builder;
	Map<String, List<String>> portlets;

	enum COMMANDS {
		addPortlet, removePortlet, addLayout, removeLayout, persistPortalPage;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			InitialContext context = new InitialContext();
			PortletContainerInterface pci = (PortletContainerInterface) context
					.lookup("java:comp/env/scrportletcontainer");
			admin = pci.getPortletAdmin();
		} catch (NamingException e) {
			throw new ServletException(e);
		}

		builder = new PageBuilder(config.getServletContext());
		pp = builder.loadLayout("");

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getAttribute("portlets") == null) {
			try {
				portlets = initPortlets(req);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		resp
				.getWriter()
				.write(
						"<html><head>"
								+ "<script language='javascript' src='"
								+ getServletContext().getContextPath()
								+ "/resources/js/pageadmin.js'></script>"
								+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/style/ur_ie6.css\">"
								+ "</head><body>");

		// if there is no request param to construct the portal page, construct
		// it from the local file system
		PortalPage pp = null;
		if (req.getParameter("portal page") != null) {
			pp = builder.constructLayout(req.getParameterMap());

		} else {
			pp = builder.loadLayout("");
		}

		// check for presence of an action
		String action = req.getParameter("action");
		if (action != null && action.length() > 0) {
			pp = processAction(action, pp, req.getSession(true));
		}

		// render
		resp.getWriter().write(
				"<form id='portalpageadmin' method='post' action='"
						+ getServletContext().getContextPath() + "/"
						+ "PortalPageAdmin'>");
		resp
				.getWriter()
				.write(
						"<input type='hidden' name='portal page' value='default'></input>");
		drawLayout(resp.getWriter(), pp.getLayout(), "0");
		// resp.getWriter().write("<button name='action' type='submit'
		// onclick='changeButtonValue(this,\""+COMMANDS.persistPortalPage+"\",\"\");'>Persist
		// Portal Page Layout</button>");
		resp
				.getWriter()
				.write(
						"<a href='javascript:void(0);' name='action' onclick='addButtonAction(\""
								+ COMMANDS.persistPortalPage
								+ ":"
								+ "\",document.forms[0]);document.forms[0].submit()' class='urBtnEmph' title='Persist Portal Page'><nobr>Persist Portal Page</nobr></a>");

		resp.getWriter().write("</form>");

		resp.getWriter().write("</body></html>");

	}

	private PortalPage processAction(String action, PortalPage pp,
			HttpSession session) {
		PortalPage result = pp;
		// parse the action param
		int idx = action.indexOf(':');
		if (idx == -1) {
			return result;
		}

		String com = action.substring(0, idx);
		String id = action.substring(idx + 1);

		COMMANDS command = COMMANDS.valueOf(com);

		switch (command) {
		case addLayout:
			pp = addLayout(id, pp);
			break;
		case removeLayout:
			pp = removeLayout(id, pp);
			break;
		case addPortlet:
			pp = addPortlet(id, pp);
			break;
		case removePortlet:
			pp = removePortlet(id, pp);
			break;
		case persistPortalPage:
			try {
				persistPortalPage(pp);
				session.setAttribute("RELOAD_PAGE", true);
			} catch (Exception e) {
				LogContext.PORTLETBROWSER_LOCATION.traceThrowableT(
						Severity.WARNING, "Error persisting portal page", e);
			}
			break;
		default:
			;
		}
		return pp;
	}

	private void persistPortalPage(PortalPage pp) throws Exception {
		builder.persist(pp);
	}

	private PortalPage removePortlet(String id, PortalPage pp) {
		PortalPage result = pp;
		if (id == null || pp == null) {
			return null;
		}
		Layout root = pp.getLayout();
		if (root == null) {
			return pp;
		}

		Layout parent = findParentLayout(id, root);
		Portlet p = findPortlet(id, root);
		List<Object> nodes = parent.getLayoutAndPortlet();
		boolean found = false;
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object tmp = iter.next();
				if (tmp instanceof Portlet) {
					Portlet r = (Portlet) tmp;
					if (r.getId().equals(id)) {
						found = true;
						break;
					}

				}
			}
			if (found) {
				parent.getLayoutAndPortlet().remove(p);
			}

		}

		return result;
	}

	Layout findParentLayout(String id, Layout root) {
		Layout result = null;
		if (id == null || root == null) {
			return null;
		}

		if (id.equals(root.getId())) {
			return root;
		}

		List<Object> nodes = root.getLayoutAndPortlet();
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object tmp = iter.next();
				if (tmp instanceof Layout) {
					result = findParentLayout(id, (Layout) tmp);
					if (result != null) {
						if (((Layout) tmp).getId().equals(id))
							return root;
						return result;
					}
				} else if (tmp instanceof Portlet) {
					Portlet p = (Portlet) tmp;
					if (id.equals(p.getId())) {
						return root;
					}
				}
			}
		}
		return result;
	}

	Portlet findPortlet(String id, Layout root) {
		Portlet result = null;
		if (id == null || root == null) {
			return null;
		}

		List<Object> nodes = root.getLayoutAndPortlet();
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object tmp = iter.next();
				if (tmp instanceof Layout) {
					result = findPortlet(id, (Layout) tmp);
					if (result != null) {
						return result;
					}
				} else if (tmp instanceof Portlet) {
					Portlet p = (Portlet) tmp;
					if (id.equals(p.getId())) {
						return p;
					}
				}
			}
		}
		return result;
	}

	Layout findLayout(String id, Layout root) {
		Layout result = null;
		if (id == null || root == null) {
			return null;
		}

		if (id.equals(root.getId())) {
			return root;
		}

		List<Object> nodes = root.getLayoutAndPortlet();
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object tmp = iter.next();
				if (tmp instanceof Layout) {
					result = findLayout(id, (Layout) tmp);
					if (result != null)
						break;
				}
			}
		}

		return result;
	}

	private PortalPage addPortlet(String id, PortalPage pp) {
		// id should be the parent layout
		// 
		Layout l = findLayout(id, pp.getLayout());
		if (l == null) {
			return pp;
		}
		// if the layout has sublayouts
		// the portlet should be wrapped in a layout
		List<Object> nodes = l.getLayoutAndPortlet();
		boolean hasLayouts = false;
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof Layout) {
					hasLayouts = true;
					break;
				}
			}
		}

		ObjectFactory factory = new ObjectFactory();
		Portlet p = factory.createPortlet();
		if (hasLayouts) {
			Layout subLayout = factory.createLayout();
			subLayout.setDirection("vertical");
			subLayout.getLayoutAndPortlet().add(p);
			l.getLayoutAndPortlet().add(subLayout);
		} else {
			l.getLayoutAndPortlet().add(p);
		}

		return pp;
	}

	private PortalPage removeLayout(String id, PortalPage pp) {
		PortalPage result = pp;
		Layout this_layout = findLayout(id, pp.getLayout());
		Layout parent = findParentLayout(id, pp.getLayout());

		// if the layout is the root layout than do not remove
		// just clear it.
		if (this_layout != null && this_layout == pp.getLayout()) {
			pp.getLayout().getLayoutAndPortlet().clear();
			return pp;
		}

		if (this_layout == null || parent == null) {
			return result;
		}

		List<Object> nodes = parent.getLayoutAndPortlet();
		if (nodes == null || nodes.size() < 1) {
			return result;
		}

		if (nodes.contains(this_layout)) {
			nodes.remove(this_layout);
		}

		return result;
	}

	private PortalPage addLayout(String id, PortalPage pp) {
		PortalPage result = pp;
		if (id == null) {
			return pp;
		}

		ObjectFactory factory = new ObjectFactory();
		Layout this_layout = findLayout(id, pp.getLayout());
		Layout l = factory.createLayout();
		l.setDirection(this_layout.getDirection());

		// if there are no child nodes
		// or those are layouts just add a new layout
		List<Object> nodes = this_layout.getLayoutAndPortlet();
		if (nodes == null || nodes.size() == 0
				|| (nodes.get(0) instanceof Layout)) {
			this_layout.getLayoutAndPortlet().add(l);
			return result;
		}

		// if this layout contains only portlets
		// create a new layout remove this from parent,
		// add new to the parent add this and one more to the new

		Layout subl = factory.createLayout();
		subl.setDirection(this_layout.getDirection());

		Layout parent = findParentLayout(id, pp.getLayout());
		int idx = parent.getLayoutAndPortlet().indexOf(this_layout);
		l.getLayoutAndPortlet().add(this_layout);
		l.getLayoutAndPortlet().add(subl);
		parent.getLayoutAndPortlet().add(idx, l);
		parent.getLayoutAndPortlet().remove(this_layout);

		return result;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	private void drawLayout(Writer writer, Layout l, String lvl)
			throws IOException {
		List<Object> nodes = l.getLayoutAndPortlet();
		String direction = l.getDirection();
		int pid = 0;
		int lid = 0;
		writer.write("<div id='" + lvl + "'>");
		writer
				.write("<input type='hidden' name='layout' value='" + lvl
						+ "'/>");
		writer.write("<label class='urLblStd' title='Level'>" + lvl
				+ "</label>");
		writer
				.write("<table border='1' cellpadding='0' cellspacing='0' width='100%'>");

		writer.write("<thead><tr>");
		writer.write("<td>");
		writer
				.write("<label class='urLblStd' title='Direction'>Direction</label>");
		writer
				.write("<select class='urSlbWhl1' name='direction' onchange='document.forms[0].submit();'>");
		writer
				.write("<option value='horizontal:"
						+ lvl
						+ "'"
						+ ((Boolean.valueOf(direction.equals("horizontal"))) ? "selected='selected'"
								: "") + "> horizontal </option>");
		writer
				.write("<option value='vertical:"
						+ lvl
						+ "'"
						+ ((Boolean.valueOf(direction.equals("vertical"))) ? "selected='selected'"
								: "") + "> vertical </option>");
		writer.write("</select>");
		writer.write("</td>");
		writer.write("<td>");
		writer
				.write("<a href='javascript:void(0);' name='action' onclick='addButtonAction(\""
						+ COMMANDS.addLayout
						+ ":"
						+ lvl
						+ "\",document.forms[0]);document.forms[0].submit()' class='urBtnEmph' title='Add Sub Layout'><nobr>Add SubLayout</nobr></a>");
		if (!lvl.equals("0")) {
			writer
					.write("<a href='javascript:void(0);' name='action' onclick='addButtonAction(\""
							+ COMMANDS.addPortlet
							+ ":"
							+ lvl
							+ "\",document.forms[0]);document.forms[0].submit()' class='urBtnEmph' title='Add Portlet'><nobr>Add Portlet</nobr></a>");
		}
		writer
				.write("<a href='javascript:void(0);' name='action' onclick='addButtonAction(\""
						+ COMMANDS.removeLayout
						+ ":"
						+ lvl
						+ "\",document.forms[0]);document.forms[0].submit()' class='urBtnEmph' title='Remove Layout'><nobr>Remove Layout</nobr></a>");
		writer.write("</td>");
		writer.write("</tr></thead>");
		writer.write("<tbody>");

		writer.write("<tr><td colspan='2'><table border='2' width='90%'>");

		if (l.getDirection().equalsIgnoreCase(Direction.HORIZONTAL.toString())) {
			writer.write("<tr>");
		}

		// <tr> if v
		if (nodes != null && nodes.size() > 0) {
			Iterator<Object> iter = nodes.iterator();
			while (iter.hasNext()) {
				Object node = iter.next();
				if (l.getDirection().equalsIgnoreCase(
						Direction.HORIZONTAL.toString())) {
					int i = nodes.size();
					if (i >= 2) {
						int width = new Double(100 / i).intValue();
						writer.write("<td width=\"" + width
								+ "%\" style=\"vertical-align:top\">");
					} else {
						writer.write("<td>");
					}
				} else {
					writer.write("<tr><td>");
				}

				if (node instanceof Layout) {
					String t = lvl + ":" + lid++;
					drawLayout(writer, (Layout) node, t);
				} else if (node instanceof Portlet) {
					drawPortlet(writer, (Portlet) node, lvl, ++pid, l
							.getDirection());
				}

				if (l.getDirection().equalsIgnoreCase(
						Direction.HORIZONTAL.toString())) {
					writer.write("</td>");
				} else {
					writer.write("</td></tr>");
				}
			}
		}

		if (l.getDirection().equalsIgnoreCase(Direction.HORIZONTAL.toString())) {
			writer.write("</tr>");
		}

		writer.write("</table></td></tr>");
		writer.write("</tbody>");
		writer.write("</table>");
		writer.write("</div>");

	}

	private void drawPortlet(Writer writer, Portlet p, String lvl, int pid,
			String direction) throws IOException {

		Set<String> apps = portlets.keySet();
		if (apps.isEmpty()) {
			// there are no deployed portlets;
			return;
		}

		String portlet_application_name = p.getApplication();
		// if portlet_application_name is null set the first
		if (portlet_application_name == null) {
			portlet_application_name = apps.toArray(new String[apps.size()])[0];
		}

		String portlet_name = p.getName();

		List<String> pnames = portlets.get(portlet_application_name);
		String idx = lvl + ":" + pid;

		writer.write("<div id='" + idx + "'>");
		writer.write("<input type='hidden' name='portlet' value='" + idx
				+ "'/>");
		writer.write("<label class='urLblStd' title='Level'>" + idx
				+ "</label>");
		writer.write("<table width='100%' cellpadding='0' cellspacing='0'>");
		writer.write("<thead><tr>");
		writer
				.write("<a href='javascript:void(0);' name='action' onclick='addButtonAction(\""
						+ COMMANDS.removePortlet
						+ ":"
						+ lvl
						+ "\",document.forms[0]);document.forms[0].submit()' class='urBtnEmph' title='Remove Portlet'><nobr>Remove Portlet</nobr></a>");
		writer.write("</tr><thead>");
		writer.write("<tr><td>");

		if (apps != null) {
			writer
					.write("<select class='urSlbWhl1' name='app' onchange='document.forms[0].submit();'>");
			for (String app_name : apps) {
				writer
						.write("<option value='"
								+ app_name
								+ ":"
								+ idx
								+ "'"
								+ ((Boolean.valueOf(app_name
										.equals(portlet_application_name))) ? " selected='selected'"
										: "") + ">" + app_name + "</option>");
			}
			writer.write("</select>");
		}

		if (pnames != null) {
			writer.write("<select class='urSlbWhl1' name='prt'>");
			for (String tmp : pnames) {
				writer
						.write("<option value='"
								+ tmp
								+ ":"
								+ idx
								+ "'"
								+ ((Boolean.valueOf(tmp.equals(portlet_name))) ? " selected='selected'"
										: "") + ">" + tmp + "</option>");
			}
			writer.write("</select>");
		}

		writer.write("</td></tr>");
		writer.write("</table>");
		writer.write("</div>");

	}

	private Map<String, List<String>> initPortlets(HttpServletRequest req)
			throws Exception {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		// get Vendors
		String[] vendors = admin.getVendors();
		Arrays.sort(vendors);
		if (vendors != null) {
			for (String vendor : vendors) {
				String[] apps = admin.getPortletApplicationNames(vendor);
				Arrays.sort(apps);
				for (String application : apps) {
					String[] modules = admin.getPortletModuleNames(application);
					Arrays.sort(modules);
					for (String pmodule : modules) {
						String[] prtl = admin.getAllPortlets(pmodule);
						Arrays.sort(prtl);
						result.put(pmodule, Arrays.asList(prtl));
					}
				}
			}
		}
		req.setAttribute("portlets", result);
		return result;
	}

}
