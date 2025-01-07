package com.sap.engine.services.httpserver.server.properties;

import java.util.HashMap;

import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.RequestUrlData;

/**
 * Contains ProxyConfiguration data for reverse proxy configuration 
 * @author I044270
 *
 */
public class ProxyConfiguration implements IProxyConfiguration {
  
  /**
   * not null or empty string
   */
  private String proxyAlias; //key
  /**
   * -1 if the port is not set
   */
  private int httpProxyPort=-1;
  /**
   * -1 if the port is not set
   */
  private int httpsProxyPort=-1;
  
  private String proxyHost;
  /**
   * -1 if the port is not set
   */
  private int icmHttpPort=-1;
  /**
   * -1 if the port is not set
   */
  private int icmHttpsPort=-1;  

  /**
   * if null or empty string it is not set in the property configuration
   */
  private String sslProxyRedirect;
  private ProxyConfiguration sslRedirectProxyConf;

  /**
   * if null or empty string it is not set in the property configuration
   */
  private String sslProxyReplace;
  private ProxyConfiguration sslReplaceProxyConf;
  
  private String httpProxyRedirect;
  private ProxyConfiguration httpProxyRedirectConf;
  private String httpProxyReplace;
  private ProxyConfiguration httpProxyReplaceConf;
  
  
  private boolean override=false;
  
  /**
   * if null it is not set in the property configuration
   */
  private ViaHeader viaHeader;
  
  /**
   * if null it is not set in the property configuration
   */
  private ClientProtocolHeader clientProtocol;
  
  ProxyConfiguration(ProxyConfigurationBuilder proxybuilder) {
    proxyAlias = proxybuilder.proxyAlias;
    httpProxyPort = proxybuilder.httpProxyPort;
    httpsProxyPort = proxybuilder.httpsProxyPort;
    proxyHost = proxybuilder.proxyHost;
    icmHttpPort = proxybuilder.icmHttpPort;
    icmHttpsPort = proxybuilder.icmHttpsPort;
    sslProxyRedirect = proxybuilder.sslProxyRedirect;
    sslProxyReplace = proxybuilder.sslProxyReplace;
    override = proxybuilder.override;
    viaHeader = proxybuilder.viaHeader;
    clientProtocol = proxybuilder.clientProtocol;
    httpProxyRedirect = proxybuilder.httpProxyRedirect;
    httpProxyReplace = proxybuilder.httpProxyReplace;
  }

  public String getHost() {
    return proxyHost;
  }
  
  public int getPlainPort() {
    return httpProxyPort;
  }

  public int getSslPort() {
    return httpsProxyPort;
  }
  
  public int getPort(MimeHeaders headers, int requestIcmPort) {
    //if exists ClientProtocol icm....Port - ClientProtocol is with highest priority
    //check if in the request exists client protocol header according proxy configuration
    if (clientProtocol != null) {
      String clientProtocolValue = headers.getHeader(this.clientProtocol.getHeaderName());
      if (clientProtocolValue != null) {
        if (clientProtocolValue.equalsIgnoreCase(clientProtocol.getHttpValue())) {          
          return httpProxyPort;
        } else if (clientProtocolValue.equalsIgnoreCase(clientProtocol
            .getHttpsValue())) {          
          return httpsProxyPort;
        } else {
          Log.logWarning("ASJ.http.000373", "Configuration of the header {0} is incorrect. Correct ReverseProxyMappings property or reverse proxy configuration.", new Object[] {clientProtocol}, null, null, null);
          return -1;
        }
      }
    }
    
    //check icm....Port
    if (requestIcmPort == icmHttpPort) {      
      return httpProxyPort;
    }
    else if (requestIcmPort == icmHttpsPort) {      
      return httpsProxyPort;
    }
    else {
      Log.logWarning("ASJ.http.000374", "Configuration of icm ports in ReverseProxyMappings is incorrect. Correct ReverseProxyMappings property.", null, null, null);
      return httpProxyPort;
    }
  }
  
  public String getScheme(MimeHeaders headers, int requestIcmPort) {
  //if exists ClientProtocol icm....Port - ClientProtocol is with highest priority
    //check if in the request exists client protocol header according proxy configuration
    if (clientProtocol != null) {
      String clientProtocolValue = headers.getHeader(this.clientProtocol.getHeaderName());
      if (clientProtocolValue != null) {
        if (clientProtocolValue.equalsIgnoreCase(clientProtocol.getHttpValue())) {
          return "http";
        } else if (clientProtocolValue.equalsIgnoreCase(clientProtocol
            .getHttpsValue())) {
          return "https";
        } else {
          Log.logWarning("ASJ.http.000375", "Configuration of the header {0} is incorrect. Correct ReverseProxyMappings property or reverse proxy configuration.", new Object[] {clientProtocol}, null, null, null);
          //TODO - What to return????
          return "http";
        }
      }
    }
    
    //check icm....Port
    if (requestIcmPort == icmHttpPort) {
      return "http";
    }
    else if (requestIcmPort == icmHttpsPort) {
      return "https";
    }
    else {
      Log.logWarning("ASJ.http.000376", "Configuration of icm ports in ReverseProxyMappings is incorrect. Correct ReverseProxyMappings property.", null, null, null);
    //TODO - What to return????
      return "http";
    }
  }
  
  public boolean isOverride() {
    return override;
  }
  
  protected void fillRedirectReplace(HashMap<String, ProxyConfiguration> proxyConf) {
    sslRedirectProxyConf = proxyConf.get(sslProxyRedirect);
    sslReplaceProxyConf = proxyConf.get(sslProxyReplace);
    httpProxyRedirectConf = proxyConf.get(httpProxyRedirect);
    httpProxyReplaceConf = proxyConf.get(httpProxyReplace);
    
    Log.logInfo("ASJ.http.000406" , "Reverse Proxy Mappings property configuration. " +
        "Setting redirect and replace properties with the following vales: " +
        "SSLProxyRedirect: Property set in the configuration - [{0}]. Proxy alias - [{1}]. " +
        "SSLProxyReplace: Property set in the configuration - [{2}]. Proxy alias - [{3}]. " +
        "HttpProxyRedirect: Property set in the configuration - [{4}]. Proxy alias - [{5}]. " +
        "HttpProxyReplace: Property set in the configuration - [{6}]. Proxy alias - [{7}]."
        ,new Object[]{sslProxyRedirect,sslRedirectProxyConf==null?"Not found":"Found",
            sslProxyReplace, sslReplaceProxyConf==null?"Not found":"Found",
            httpProxyRedirect, httpProxyRedirectConf==null?"Not found":"Found",
            httpProxyReplace, httpProxyReplaceConf==null?"Not found":"Found"},            
            null, null, null);
  }
  
  protected boolean check(MimeHeaders headers) {
    //if via header does not exist this is the correct proxy
    //this can be used when all request to one icm port should use this configuration (50000 = Proxy1)
    if (viaHeader == null) {      
      return true;
    }
    
    //check if via header exists in the headers
    byte[] viaHeaderValue = headers.getHeader(viaHeader.getHeaderNameBytes()); 
    //if viaHeaderValue does not exist  - this is not the correct proxy configuration 
    if (viaHeaderValue == null) {
      return false;
    }
    
    //if via header value is not equal to the one of the proxy configuration return false
    if (!ByteArrayUtils.equalsBytes(viaHeader.getHeaderValueBytes(), viaHeaderValue)) {
      return false;
    }
    
    return true;
  }  

  public ProxyConfiguration getSslRedirectProxyConf() {
    return sslRedirectProxyConf;
  }

  public ProxyConfiguration getSslReplaceProxyConf() {
    return sslReplaceProxyConf;
  }
  
  public RequestUrlData getPlainUrlData(String requestScheme) {
    int porturl = -1;
    String hosturl = null;
    if ("http".equalsIgnoreCase(requestScheme)) {
      if (httpProxyReplaceConf != null) {
        porturl = httpProxyReplaceConf.getPlainPort();
        hosturl = httpProxyReplaceConf.getHost();
      }
      else {
        porturl = httpProxyPort;
        hosturl = proxyHost;
      }    
    }
    else if ("https".equalsIgnoreCase(requestScheme)) {
      if (httpProxyRedirectConf != null) {
        porturl = httpProxyRedirectConf.getPlainPort();
        hosturl = httpProxyRedirectConf.getHost();
      }
      else {
        porturl = httpProxyPort;
        hosturl = proxyHost;
      }
    }
    
    return new RequestUrlData(porturl, "http", hosturl, override);    
  }
  
  public RequestUrlData getSslUrlData(String requestScheme) {
    int porturl = -1;
    String hosturl = null;
    
    if ("http".equalsIgnoreCase(requestScheme)) {
      if (sslProxyRedirect != null) {
        porturl = sslRedirectProxyConf.getSslPort();
        hosturl = sslRedirectProxyConf.getHost();
      }
      else {
        porturl = httpsProxyPort;
        hosturl = proxyHost;
      }    
    }
    else if ("https".equalsIgnoreCase(requestScheme)) {
      if (sslReplaceProxyConf != null) {
        porturl = sslReplaceProxyConf.getSslPort();
        hosturl = sslReplaceProxyConf.getHost();
      }
      else {
        porturl = httpsProxyPort;
        hosturl = proxyHost;
      }
    }
    
    return new RequestUrlData(porturl, "https", hosturl, override);
  }

  public ProxyConfiguration getHttpProxyReplaceConf() {
    return httpProxyReplaceConf;
  }
  
  public String toString() {
    StringBuilder res = new StringBuilder();
    
    res.append("Proxy configuration: ");
    res.append("httpProxyPort: " + httpProxyPort + ";");
    res.append("httpsProxyPort: " + httpsProxyPort + ";");
    res.append("proxyHost: " + proxyHost + ";");
    res.append("icmHttpPort: " + icmHttpPort + ";");
    res.append("icmHttpsPort: " + icmHttpsPort + ";");
    res.append("sslProxyRedirect: " + sslProxyRedirect + ";");
    res.append("sslProxyReplace: " + sslProxyReplace + ";");
    res.append("httpProxyRedirect: " + httpProxyRedirect + ";");
    res.append("httpProxyReplace: " + httpProxyReplace + ";");
    res.append("override: " + override + ";");
    res.append("viaHeader: " + viaHeader + ";");
    res.append("clientProtocol: " + clientProtocol + ";");
    return res.toString();
  }
}
