package com.sap.portletbrowser;

import static com.sap.portletbrowser.LogContext.PORTLETBROWSER_LOCATION;
import static com.sap.portletbrowser.RequestEncodings.ACTION_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.EVENT_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.RENDER_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.RESOURCE_REQUEST;
import static com.sap.portletbrowser.RequestEncodings.INODE_ID;
import static com.sap.portletbrowser.RequestEncodings.PORTAL_PAGE;
import static com.sap.portletbrowser.RequestEncodings.PORTLET_NAME;
import static com.sap.portletbrowser.RequestEncodings.EXTRA_PARAMETER_PREFIX;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sap.engine.services.portletcontainer.PortletContainerInterface;
import com.sap.engine.services.portletcontainer.api.PortletAdmin;
import com.sap.engine.services.portletcontainer.api.PortletContainer;
import com.sap.portletbrowser.portletpage.PageBuilder;
import com.sap.portletbrowser.portletpage.component.DefaultNode;
import com.sap.portletbrowser.portletpage.component.LayoutNode;
import com.sap.portletbrowser.portletpage.component.PageNodeRenderer;
import com.sap.portletbrowser.portletpage.component.PortalPageContext;
import com.sap.portletbrowser.portletpage.component.PortalPageNode;
import com.sap.portletbrowser.portletpage.component.PortletNode;
import com.sap.portletbrowser.portletpage.xml.Layout;
import com.sap.portletbrowser.portletpage.xml.PortalPage;
import com.sap.portletbrowser.portletpage.xml.Portlet;
import com.sap.portletbrowser.spi.CoordinationManager;
import com.sap.portletbrowser.spi.CoordinationServiceImpl;
import com.sap.portletbrowser.spi.PortletNodeImpl;
import com.sap.portletbrowser.spi.PortletPreferencesServiceImpl;
import com.sap.portletbrowser.template.TemplateBuilder;

public class RenderInvoker extends HttpServlet {

	/**
	 * JLIN check
	 */
	private static final long serialVersionUID = 7717823095347590234L;
  
	private static final String PORTAL_PAGE_SESSION_KEY = "portal_page_id";
	
  private static final int MAX_EVENT_COUNT = 10;
  
	private static int t = 0;

	private PortletAdmin admin;
	private PortletContainer portlet_container;
	private CoordinationManager coordinationManager;

	// A hashtable for caching the portletNodes. Its key is the request
	// parameter
	// which indentifies the portlet and has the following format
	// module_name/portlet_name
	private HashMap<String, PortletNodeImpl> portletNodes = new HashMap<String, PortletNodeImpl>();

	private boolean debug_mode = false;

	// A key for storing portlet page layout into the user http session
	// if we want to test session failover the portletpage should be
	// serializable

	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		PortletContainerInterface pci = null;

		try {
			Context context = new InitialContext();
			pci = (PortletContainerInterface) context
					.lookup("java:comp/env/scrportletcontainer");
			admin = pci.getPortletAdmin();
			portlet_container = pci.getPortletContainer();
		} catch (Exception e) {
			throw new ServletException(
					"Cannot lookup PortletContainerInterface", e);
		}

		if (portlet_container == null) {
			throw new ServletException(
					"Can not obtain reference to the portlet container.");
		}
		if (admin == null) {
			throw new ServletException(
					"Can not obtain reference to the admin interface of the portlet container.");
		}

		this.coordinationManager = new CoordinationManager(admin);
		
		debug_mode = Boolean.valueOf("debug".equals(cfg.getServletContext()
				.getInitParameter("APPLICATION_MODE")
				+ ""));
		if (PORTLETBROWSER_LOCATION.beDebug()) {
			PORTLETBROWSER_LOCATION.debugT("PortletBrowser debug mode: "
					+ debug_mode);
		}

	}

	/**
	 * @return
	 */
	private String getID() {
		// check the request param if not create new one
		return "id" + (t++);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PortalRequest portalRequest = new PortalRequest(request);
    PortalResponse portalResponse = new PortalResponse(response);

    List<PortletNodeImpl> portletNodesOnPage = new ArrayList<PortletNodeImpl>();

    // if we have PORTAL_PAGE id as a request param
    // we should build/store/retrieve the layout and render the portlets
    // comprising the page
    //
    PageNodeRenderer tree = null;
    PortletNodeImpl node = null;
    TemplateBuilder builder = (TemplateBuilder) getServletContext()
      .getAttribute("template_builder");
    PortalPageContext ctx = new PortalPageContext(request, response,
      portlet_container, builder, "");

    String portal_page_id = request.getParameter(PORTAL_PAGE);
    String portlet_name = request.getParameter(PORTLET_NAME);
    String portlet_id = request.getParameter(INODE_ID);
    
    String servletPath = request.getContextPath() + request.getServletPath();
    if (portal_page_id != null && portal_page_id.length() > 0) {
      tree = constructPortalPage(request, response, portal_page_id, servletPath);
      node = getPortletNode(portlet_name, portlet_id, portal_page_id, servletPath);
    } else {
      // construct a tree having a single portlet
      node = getPortletNode(portlet_name, portlet_id, "", servletPath);
      if (node == null) {
        // TODO Handle default page
        tree = new PortalPageNode();
        tree.add(new DefaultNode());
        tree.render(ctx);
        return;
      }
      tree = new PortalPageNode();
      tree.add(new PortletNode(node));
      portletNodesOnPage.add(node);
    }

    try {
      if (node != null) {
        String portletNodeContext = node.getContextName();
        ctx.setTargetedNode(portletNodeContext);
        
        // Trying to set portlet mode and window state
        setModeAndState(portalRequest, node);

        // Process action.
        if (portalRequest.isActionRequest()) {
          doAction(portalRequest, portalResponse, node, portletNodesOnPage);
        }
        else if(portalRequest.isResourceRequest()){
          prepairResource(portalRequest, portalResponse, node);
          ctx.setRequestType(RequestEncodings.RESOURCE_REQUEST);
        }
        else if(portalRequest.isRenderRequest()){
          prepairRender(portalRequest, portalResponse, node);
          ctx.setRequestType(RequestEncodings.RENDER_REQUEST);
        }
        if (PORTLETBROWSER_LOCATION.beDebug() && portal_page_id != null) {
          PORTLETBROWSER_LOCATION.debugT(">>>>>>>>>>>>>>PORTLET PAGE RENDER: "
            + portal_page_id);
        }
        
        boolean isResourceServing = portalRequest.isRenderRequest();
        
        for (PortletNodeImpl tempNode: portletNodesOnPage){
          if (isResourceServing && tempNode.getContextName().equals(portletNodeContext))
          {
            continue;
          }
          // set only for nodes which will be rendered.
          PortletPreferences preferences = admin.getPortletPreferences(
              tempNode.getPortletName(), tempNode.getPortletApplicationName());
          PortletPreferencesServiceImpl prefService = ((PortletPreferencesServiceImpl) 
              tempNode.getPortletPreferencesService());
          prefService.setPortletPreferences(preferences);
        }
        
        tree.render(ctx);
      }
    } catch (Exception e) {
      // TODO handle portlet render exceptions
      response.getWriter().write(
        "<br> render portlet [" + node.getPortletName()
          + "] with exception: <br>" + getExceptionStackTrace(e));

    }

    response.getWriter().flush();
  }

	private void prepairResource(PortalRequest portalRequest,
      PortalResponse portalResponse, PortletNodeImpl node) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(">>>>>>>>>>>>>>PORTLET RESOURCE: "
        + node.getPortletName());
    }
    node.setCachability(portalRequest.getCachability());
    node.setResourceID(portalRequest.getResourceID());
    node.setRequestParameters(portalRequest.getRequest().getParameterMap());
  }

  private void prepairRender(PortalRequest portalRequest,
      PortalResponse portalResponse, PortletNodeImpl node) {
    if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(">>>>>>>>>>>>>>PORTLET RENDER: "
        + node.getPortletName());
    }
    Map<String, String[]> parameterMap = portalRequest.getRequest().getParameterMap();
    
    Map<String, String[]> requestParameters = new HashMap<String, String[]>();
    copyParameters(parameterMap, requestParameters);
    
    Iterator<Map.Entry<String, String[]>> iterator = requestParameters.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, String[]> entry = iterator.next();
      
      // remove extra parameters
      String key = entry.getKey();
      if (key == null || key.startsWith(EXTRA_PARAMETER_PREFIX)) {
        iterator.remove();
      }
    }
    setParameters(node, requestParameters);
  }

  private void doAction(PortalRequest portalRequest,
      PortalResponse portalResponse, PortletNodeImpl node, 
      List<PortletNodeImpl> portletNodesOnPage ) {
	  if (PORTLETBROWSER_LOCATION.beDebug()) {
      PORTLETBROWSER_LOCATION.debugT(">>>>>>>>>>>>>>PORTLET ACTION: "
        + node.getPortletName());
    }
    node.setRequestParameters(portalRequest.getRequest()
      .getParameterMap());
    process(portalRequest, portalResponse, node, ACTION_REQUEST);
    
    // Used to mark if an event has been found on the last loop.
    // If found, no more loops should be done.
    boolean checkForEvents = true;
    int count = 0;
    while (checkForEvents && count++ <= MAX_EVENT_COUNT) {
      checkForEvents = false;

      for (PortletNodeImpl portlet : portletNodesOnPage) {
        CoordinationServiceImpl service = portlet
          .getCoordinationService();

        if (service.getEventsNumber() > 0) {
          // an event was found, so another loop should be done.
          checkForEvents = true;

          if (portlet != null) {
            setModeAndState(portalRequest, portlet);
            if (PORTLETBROWSER_LOCATION.beDebug()) {
              PORTLETBROWSER_LOCATION.debugT(">>>>>>>>>>>>>>PORTLET EVENT: "
                + portlet.getPortletName());
            }
            process(portalRequest, portalResponse, portlet, EVENT_REQUEST);
            service.process();
          }
        }
      }
    }
  }

  private void process(PortalRequest portalRequest, PortalResponse portalResponse,
      PortletNodeImpl target, String requestType) {
    HttpServletRequest httpRequest = portalRequest.getRequest();
    HttpServletResponse httpResponse = portalResponse.getResponse();
    
    try {
      if (RENDER_REQUEST.equals(requestType)){
        PortletPreferences preferences = admin.getPortletPreferences(target
          .getPortletName(), target.getPortletApplicationName());
        ((PortletPreferencesServiceImpl) target.getPortletPreferencesService())
          .setPortletPreferences(preferences);
        portlet_container.render(httpRequest, httpResponse, target);
      }
      else if (ACTION_REQUEST.equals(requestType)){
        portlet_container.processAction(httpRequest, httpResponse, target);
      }
      else if (EVENT_REQUEST.equals(requestType)){
        portlet_container.processEvent(httpRequest, httpResponse, target);
      }
      else if (RESOURCE_REQUEST.equals(requestType)){
        portlet_container.serveResource(httpRequest, httpResponse, target);
      }
    } catch (IOException e) {
      try {
        portalResponse.getResponse().getWriter().write(
          "IOException: " + e.getMessage());
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      e.printStackTrace();
    } catch (PortletException e) {
      try {
        portalResponse.getResponse().getWriter().write(
          "PortletException: " + e.getMessage());
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
  }
	
  private void copyParameters(Map<String, String[]> source, Map<String, String[]> destination){
    Iterator<Map.Entry<String, String[]>> iterator = source.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, String[]> entry = iterator.next();
      
      String key = entry.getKey();
      String[] value = entry.getValue();
      String[] newValue = new String[value.length];
      
      System.arraycopy(value, 0, newValue, 0, value.length);
      
      destination.put(key, newValue);
    }
  }
  
  private void setParameters(PortletNodeImpl portletNode, Map<String, String[]> requestParameters) {
    CoordinationServiceImpl coordinationService = portletNode.getCoordinationService();
    
    Map<String, String[]> privateRenderParameters = new HashMap<String, String[]>();
    
    for(Map.Entry<String, String[]> entry : requestParameters.entrySet()){
      String parameterName = entry.getKey();
      String[] values = entry.getValue();
      
      if (coordinationService.isSupportedRenderParams(parameterName)){
        coordinationService.setPublicRenderParameter(parameterName, values);
      }
      else {
        privateRenderParameters.put(parameterName, values);
      }
    }
    
    portletNode.setPrivateRenderParameters(privateRenderParameters);
  }

  
  private void setModeAndState(PortalRequest portalRequest, PortletNodeImpl node) {
		if (node == null || portalRequest == null)
			return;
		
    if (portalRequest.getRequestedPortletWindowState() != null) {
      node.setWindowState(portalRequest.getRequestedPortletWindowState());
    }
    if (portalRequest.getRequestedPortletMode() != null) {
      node.setPortletMode(portalRequest.getRequestedPortletMode());
    }
	}
	
	private PageNodeRenderer constructPortalPage(HttpServletRequest request,
			HttpServletResponse response, String portal_page_id, String servletPath) {
		// if we dont have the PortalPage in the http session
		// we should try to create and store it using the PageBuilder
		HttpSession session = request.getSession(true);
		boolean reload = Boolean.parseBoolean(""
				+ session.getAttribute("RELOAD_PAGE"));

		PageNodeRenderer portal_page = (PageNodeRenderer) session
				.getAttribute(PORTAL_PAGE_SESSION_KEY);

		if (portal_page != null && !reload)
			return portal_page;

		// create the portal page and store the layout in the session
		PageBuilder builder = new PageBuilder(this.getServletContext());
		PortalPage portal_page_layout = builder.loadLayout(portal_page_id);
		session.setAttribute("RELOAD_PAGE", false);

		// Now start rendering the layout and the portlets
		// it is 2 phase process
		// Phase 1 creating PortletNodeImpls for each of the portlets
		// Here we should optimize and not create different PortletNode each
		// time.
		// A possible issue could be setting the preferences of the portlet
		// because the PortletNodeImpl
		// should be recreated each time a change in the preferences is made -
		// TODO PortletNode update after preferences are changed.
		// Phase 2 executing if present action on a portlet
		// Phase 3 executing render no each portlet interweaving the content
		// with the portal page

		// process the PortalPage

		PageNodeRenderer tree = new PortalPageNode();

		PageNodeRenderer portlets = processPortalPage(portal_page_layout
				.getLayout(), portal_page_id, servletPath);
		tree.add(portlets);
		session.setAttribute(PORTAL_PAGE_SESSION_KEY, tree);
		return tree;
	}

	private PageNodeRenderer processPortalPage(Layout l, String portal_page_id, String servletPath) {
		if (l == null) {
			return null;
		}
		PageNodeRenderer result = new LayoutNode(l.getDirection());
		List<Object> nodes = l.getLayoutAndPortlet();
		Iterator<Object> iter = nodes.iterator();

		while (iter != null && iter.hasNext()) {
			Object node = iter.next();
			if (node != null && node instanceof Layout) {
				PageNodeRenderer sub = processPortalPage((Layout) node,
						portal_page_id, servletPath);
				result.add(sub);
				continue;
			} else if (node != null && node instanceof Portlet) {
				Portlet p = (Portlet) node;
				result.add(new PortletNode(getPortletNode(p.getApplication()
						+ "/" + p.getName(), null, portal_page_id, servletPath)));
			}

		}
		return result;
	}

	private PortletNodeImpl getPortletNode(String portletFullName,
			String portletId, String portalPageId, String servletPath) {
		// this parameter should be in module_name/portlet_name format

		if (portletNodes.containsKey(portletId)) {
			return portletNodes.get(portletId);
		}

		if (portletFullName == null) {
			return null;
		}

		String[] portlet = portletFullName.split("/");
		if (portlet == null || portlet.length != 2) {
			return null;
		}
		
		String portletApplication = portlet[0];
		String portletName = portlet[1];
		
		String[] portletsInApplication = admin.getAllPortlets(portletApplication);
		if (portletsInApplication.length <= 0) {
			return null;
		} else {
			for (String pname : portletsInApplication) {
				if (pname.equals(portletName)) {
					if (PORTLETBROWSER_LOCATION.beDebug()) {
						PORTLETBROWSER_LOCATION.debugT("Portlet Found: "
								+ pname);
					}

					portletId = getID();
					PortletPreferences preferences = admin
							.getPortletPreferences(portletName, portletApplication);
					 PortletNodeImpl node = new PortletNodeImpl(preferences,portletName,
             portletApplication, portletId ,null, portalPageId, servletPath, 
             coordinationManager);
					
					portletNodes.put(portletId, node);
					return node;

				}
			}
		}

		return null;
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public static String getExceptionStackTrace(Throwable t) {
		ByteArrayOutputStream ostr = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(ostr));
		return ostr.toString();
	}

}
