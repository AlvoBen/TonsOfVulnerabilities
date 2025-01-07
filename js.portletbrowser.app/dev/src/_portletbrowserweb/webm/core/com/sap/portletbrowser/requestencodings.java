/*
 * Created on 2006-1-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.portletbrowser;

/**
 * @author diyan-y
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface RequestEncodings {
	public static final String REQUEST_TYPE = "com.sap.engine.portletcontainer.op";
	public static final String PORTLET_MODE = "com.sap.engine.portletcontainer.md";
	public static final String WINDOW_STATUS = "com.sap.engine.portletcontainer.st";
	
	public static final String ACTION_REQUEST = "com.sap.engine.portletcontainer.action";
	public static final String RENDER_REQUEST = "com.sap.engine.portletcontainer.render";
	public static final String EVENT_REQUEST = "com.sap.engine.portletcontainer.event";
  public static final String RESOURCE_REQUEST = "com.sap.engine.portletcontainer.resource";
  
  public static final String PORTAL_PAGE = "com.sap.engine.portletcontainer.pid";
  public static final String PORTAL_PAGE_DEFAULT_LAYOUT = "default_layout";
  public static final String PORTLET_NAME = "com.sap.engine.portletcontainer.nm";
  public static final String INODE_ID = "com.sap.engine.portletcontainer.inode";
  public static final String EXTRA_PARAMETER_PREFIX = "com.sap.engine.portletcontainer.";
  
  public static final String PARAMS = "?";
  public static final String EQ = "=";
  public static final String ADD = "&";
  public static final String FS = "/";
    
  public static final String RESOURCE_ID = "com.sap.engine.portletcontainer.resourceId";
  public static final String CACHABILITY = "com.sap.engine.portletcontainer.cachability";
}
