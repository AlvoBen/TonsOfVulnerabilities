package com.sap.portletbrowser.portletpage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sap.portletbrowser.LogContext;
import com.sap.portletbrowser.RequestEncodings;
import com.sap.portletbrowser.portletpage.xml.Layout;
import com.sap.portletbrowser.portletpage.xml.ObjectFactory;
import com.sap.portletbrowser.portletpage.xml.PortalPage;
import com.sap.portletbrowser.portletpage.xml.Portlet;
import com.sap.tc.logging.Severity;

public class PageBuilder {

	private JAXBContext context;
	private ServletContext servlet_context;
	@SuppressWarnings("unchecked")
	private Map params;

	public PageBuilder(ServletContext context) {
		this.servlet_context = context;
	}

	/**
	 * Each user should have an associated layout, if not the default.
	 * PortalPage should be associated with each user session.
	 * 
	 * @param name
	 *            The unique identifier of the layout. It should be related to
	 *            the user.
	 * 
	 */
	public synchronized PortalPage loadLayout(String layout_id) {
		if (layout_id == null
				|| layout_id.length() == 0
				|| layout_id
						.equals(RequestEncodings.PORTAL_PAGE_DEFAULT_LAYOUT)) {
			// load the default page layout
			InputStream is = null;
			try {
				context = JAXBContext
						.newInstance("com.sap.portletbrowser.portletpage.xml");
				is = servlet_context
						.getResourceAsStream("/WEB-INF/layout/layout.xml");
				Object root = context.createUnmarshaller().unmarshal(is);
				if (root instanceof PortalPage)
					return (PortalPage) root;
			} catch (JAXBException e) {

				LogContext.PORTLETBROWSER_LOCATION.traceThrowableT(
						Severity.WARNING, "Error parsing portlet page layout",
						e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						LogContext.PORTLETBROWSER_LOCATION
								.traceThrowableT(
										Severity.WARNING,
										"Error closing the portlet page persistent unit",
										e);
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized PortalPage constructLayout(Map parameterMap) {
		// this parametMap is the requestParameter map

		ObjectFactory factory = new ObjectFactory();

		if (parameterMap.get("portal page") == null) {
			return null;
		}
		this.params = parameterMap;

		// construct PortalPage
		PortalPage page = factory.createPortalPage();

		// get Layouts
		Object ls = parameterMap.get("layout");
		if (ls instanceof String) {
			Layout l = createLayout((String) ls, factory);
			page.setLayout(l);
		} else if (ls instanceof String[]) {
			Layout l = createLayout((String[]) ls, factory);
			page.setLayout(l);
		}
		return page;
	}

	public synchronized void persist(PortalPage pp) throws Exception {
		// TODO so far we deal only with the default PortalPage layout
		// TODO add support per user.

		// there is nothing to persist
		if (pp == null) {
			return;
		}
		FileOutputStream fos = null;
		context = JAXBContext
				.newInstance("com.sap.portletbrowser.portletpage.xml");
		URL url = servlet_context.getResource("/WEB-INF/layout/layout.xml");
		File f = new File(url.getPath());
		if (f != null && f.canWrite() && f.isFile()) {
			fos = new FileOutputStream(f);
			context.createMarshaller().marshal(pp, fos);
		}
		if (fos != null) {
			fos.close();
		}
	}

	private List<Portlet> getPortlets(String layout_id, ObjectFactory factory) {
		List<Portlet> result = null;

		String[] pids = getPortletIds(layout_id);
		if (pids == null || pids.length < 1) {
			return null;
		}

		result = new ArrayList<Portlet>(pids.length);
		for (String pid : pids) {
			Portlet p = createPortlet(pid, factory);
			result.add(p);
		}

		return result;
	}

	private Layout createLayout(String id, ObjectFactory factory) {
		// create the layout
		Layout l = factory.createLayout();
		String direction = getDirection(id);
		l.setDirection(direction);
		l.setId(id);
		// check for portlets
		List<Portlet> portlets = getPortlets(id, factory);
		if (portlets != null) {
			l.getLayoutAndPortlet().addAll(portlets);
		}

		return l;
	}

	private List<Layout> getSubLayouts(String id, ObjectFactory factory) {
		List<Layout> result = null;
		// get sub layouts id

		List<String> subl = getValue("layout", id, false);
		// if there is no sublayout create this and return
		if (subl == null) {
			return null;
		}

		// there are sub layouts
		Iterator<String> iter = subl.iterator();
		if (iter != null && subl.size() > 0) {
			while (iter.hasNext()) {
				String subid = iter.next();
				List<Layout> lst = getSubLayouts(subid, factory);
				// there are no sub layouts
				Layout l = createLayout(subid, factory);
				// if there are sublayouts add those to this one
				if (lst != null) {
					l.getLayoutAndPortlet().addAll(lst);
				}
				if (result == null) {
					result = new ArrayList<Layout>(1);
				}
				result.add(l);
			}
		}

		return result;
	}

	private Layout createLayout(String[] layout_ids, ObjectFactory factory) {
		// create the list
		if (layout_ids == null || layout_ids.length < 1) {
			return null;
		}

		List<String> layouts = Arrays.asList(layout_ids);
		Collections.sort(layouts);
		List<Layout> ls = getSubLayouts(layouts.get(0), factory);
		String index = "";
		if (ls != null) {
			index = layouts.get(0);
		} else {
			index = layout_ids[0];
		}
		Layout l = createLayout(index, factory);
		l.setId(index);
		l.setDirection(getDirection(index));
		if (ls != null) {
			l.getLayoutAndPortlet().addAll(ls);
		}
		return l;
	}

	// creates a portlet with given portlet id
	private Portlet createPortlet(String portlet_id, ObjectFactory factory) {
		Portlet p = null;
		String application_name = getApplicationName(portlet_id);
		String portlet_name = getPortletName(portlet_id);
		if (application_name != null && portlet_name != null) {
			p = factory.createPortlet();
			p.setApplication(application_name);
			p.setName(portlet_name);
			p.setId(portlet_id);
		}
		return p;
	}

	// return order portlet ids belonging to a layout with layout_id
	private String[] getPortletIds(String layout_id) {
		String[] result = null;

		String[] search = prepareParams("prt");
		if (search == null || search.length < 1) {
			return null;
		}

		ArrayList<String> prts = new ArrayList<String>(search.length);
		for (String t : search) {
			int idx = t.indexOf(':');
			if (idx == -1)
				continue;
			String tmp = t.substring(idx + 1);
			String r = compare(layout_id, false, tmp);
			if (r != null)
				prts.add(r);
		}

		if (prts.size() < 1) {
			return null;
		} else if (prts.size() > 1) {
			Collections.sort(prts);
		}

		result = prts.toArray(new String[prts.size()]);
		return result;
	}

	private String getPortletName(String portlet_id) {
		return getStringValue("prt", portlet_id);
	}

	private String getApplicationName(String portlet_id) {
		return getStringValue("app", portlet_id);
	}

	private String getDirection(String layout_id) {

		String result = getStringValue("direction", layout_id);
		if (result == null) {
			// set the default value
			return "vertical";
		}
		return result;
	}

	private String getStringValue(String param_name, String id) {
		String result = null;
		String[] search = prepareParams(param_name);
		// search for the value
		for (String t : search) {
			int idx = t.indexOf(":");
			if (idx == -1)
				continue;
			if (id.equals(t.substring(idx + 1))) {
				result = t.substring(0, idx);
				return result;
			}

		}

		return result;
	}

	private String[] prepareParams(String param_name) {
		String[] result;
		Object rp = params.get(param_name);
		if (rp instanceof String) {
			result = new String[1];
			result[0] = (String) rp;
		} else if (rp instanceof String[]) {
			result = (String[]) rp;
		} else {
			return null;
		}
		return result;
	}

	// direction first(true) - last (false)
	private List<String> getValue(String param_name, String id,
			boolean direction) {
		List<String> result = null;
		String[] search = prepareParams(param_name);

		for (String p : search) {
			String t = compare(id, direction, p);
			if (t != null) {
				if (result == null) {
					result = new ArrayList<String>();
				}
				result.add(p);
			}
		}
		return result;
	}

	private String compare(String id, boolean direction, String tmp) {
		String d = tmp;
		int idx = -1;
		if (direction) {
			idx = tmp.indexOf(":");
			if (idx != -1) {
				d = tmp.substring(idx + id.length());
				if (d.startsWith(":") && d.length() > 1) {
					d = d.substring(1);
				}
			}
		} else {
			idx = tmp.lastIndexOf(":");
			if (idx != -1) {
				d = tmp.substring(0, idx);
				if (d.endsWith(":") && d.length() > 1) {
					d = d.substring(0, d.length() - 1);
				}
			}
		}

		if (d.equals(id) && idx != -1) {
			return tmp;
		}
		return null;
	}

}
