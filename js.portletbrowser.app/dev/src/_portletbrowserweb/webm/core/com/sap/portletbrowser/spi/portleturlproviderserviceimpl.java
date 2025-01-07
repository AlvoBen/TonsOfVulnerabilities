package com.sap.portletbrowser.spi;

import static com.sap.portletbrowser.LogContext.PORTLETBROWSER_LOCATION;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.portletcontainer.api.PortletConsumerURL;
import com.sap.engine.services.portletcontainer.api.PortletURL;
import com.sap.engine.services.portletcontainer.api.ResourceURL;
import com.sap.engine.services.portletcontainer.spi.PortletNode;
import com.sap.engine.services.portletcontainer.spi.PortletURLProviderService;
import com.sap.portletbrowser.RequestEncodings;


public class PortletURLProviderServiceImpl implements PortletURLProviderService {

  private PortletNode portletNode;

  private String portalPageId;

  private final String servletPath;

  public PortletURLProviderServiceImpl(PortletNode portletNode,
      String portalPageId, String servletPath) {
    this.portletNode = portletNode;
    this.portalPageId = portalPageId;
    this.servletPath = servletPath;
  }

  public String convertPortalURLToString(PortletConsumerURL portletConsumerURL) {
    Map<String, String[]> map = portletConsumerURL.getParameterMap();
    StringBuffer params = new StringBuffer();
//    portletNode.setRenderParameters(map);
    try {
      Set<String> keys = map.keySet();
      Iterator<String> it = keys.iterator();
      while (it.hasNext()) {
        Object key = it.next();
        Object val = map.get(key);
        if (PORTLETBROWSER_LOCATION.beDebug()) {
          PORTLETBROWSER_LOCATION.debugT(
            "*******[IPortletURL][key]:[value] " + key + ":"
            + val);
        }
        if (val instanceof String[]) {
          String[] tmp = (String[]) val;
          for (int i = 0; i < tmp.length; i++) {
            params.append("&" + key + "=" + tmp[i]);
          }
        } else {
          params.append("&" + key + "=" + val);
        }
      }
    } catch (NullPointerException e) {
      if (PORTLETBROWSER_LOCATION.beDebug()) {
        PORTLETBROWSER_LOCATION.debugT("*******[IPortletURL][null element in the properties]");
      }
    }

    // if the url is action we can specify the portlet mode
    String op = "";
    String wst = "";
    String md = "";
    String resourceId = "";
    String cachability = "";
    String portletId = "";
    String pp = RequestEncodings.PORTAL_PAGE + "=" + ((portalPageId != null) ? portalPageId : "");

    portletId = RequestEncodings.INODE_ID + "=" + portletNode.getContextName();
    
    if (portletConsumerURL instanceof PortletURL) {
      PortletURL portletURL = (PortletURL) portletConsumerURL;
      wst = RequestEncodings.WINDOW_STATUS + "=" + portletURL.getState();
      md = RequestEncodings.PORTLET_MODE + "=" + portletURL.getMode();
    }
    if (portletConsumerURL instanceof ResourceURL) {
      ResourceURL resourceURL = (ResourceURL) portletConsumerURL;
      if(resourceURL.getResourceID() != null){
        resourceId = RequestEncodings.RESOURCE_ID + "="
          + resourceURL.getResourceID();
      }
      if(resourceURL.getCacheability() != null) {
        cachability = RequestEncodings.CACHABILITY + "="
          + resourceURL.getCacheability();
      }
    }
    

    String result = servletPath + "?" + RequestEncodings.PORTLET_NAME + "="
    + portletNode.getPortletApplicationName() + "/" + portletNode.getPortletName() 
    + "&" + portletId;

    if (portletConsumerURL.getType().equals(PortletURL.ACTION_TYPE)) {
      op = RequestEncodings.REQUEST_TYPE + "="
        + RequestEncodings.ACTION_REQUEST;
      
      result = result + "&" + op + "&" + wst + "&" + md + "&"  
        + pp + params.toString();
      
      if (PORTLETBROWSER_LOCATION.beDebug()) {
        PORTLETBROWSER_LOCATION.debugT("*******[IPortletURL]----------->" + result);
      }
    } 
    else if (portletConsumerURL.getType().equals(ResourceURL.RESOURCE_TYPE)) {
      op = RequestEncodings.REQUEST_TYPE + "="
        + RequestEncodings.RESOURCE_REQUEST;
      
      result = result + "&" + op + "&" + wst + "&" + md + "&"  
        + resourceId + "&" + cachability + "&" + pp + params.toString();
    
      if (PORTLETBROWSER_LOCATION.beDebug()) {
        PORTLETBROWSER_LOCATION.debugT("*******[IPortletURL]----------->" + result);
      }
    } 
    else {
      op = RequestEncodings.REQUEST_TYPE + "="
        + RequestEncodings.RENDER_REQUEST;
      
      result = result + "&" + op + "&" + wst + "&" + md + "&"
        + pp + params.toString();
      if (PORTLETBROWSER_LOCATION.beDebug()) {
        PORTLETBROWSER_LOCATION.debugT("*******[IPortletURL]----------->" + result);
      }
    }

    return result;
  }

  public String encodeURL(String path) {
    // TODO Auto-generated method stub
    return path;
  }

  public void write(PortletConsumerURL portletConsumerURL, Writer out) {
    try {
      out.write(convertPortalURLToString(portletConsumerURL));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void write(PortletConsumerURL portletConsumerURL, Writer out,
      boolean escapeXML) {
    try {
      out.write(convertPortalURLToString(portletConsumerURL));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
