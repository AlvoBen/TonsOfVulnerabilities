package com.sap.engine.services.httpserver.server.properties;

/**
 * Information about Client protocol header in reverse proxy configuration property.
 * The client protocol header determines the scheme of the request. It can be added by the proxy server
 * to determine the original scheme of the request.
 * @author I044270
 *
 */
public class ClientProtocolHeader {
  private String headerName;
  private String httpValue;
  private String httpsValue;
  
  public String getHeaderName() {
    return headerName;
  }

  public String getHttpValue() {
    return httpValue;
  }

  public String getHttpsValue() {
    return httpsValue;
  }

  public ClientProtocolHeader(String headerName, String httpValue,
      String httpsValue) {
    if (headerName == null || httpValue == null || httpsValue == null) {
      throw new IllegalArgumentException("Problem configuring client protocol property of the reverse proxy."
          + "Header name, Http value and Https value must not be null. " +
          		"Values: Header name: " + headerName + "; Http value: " + httpValue + "; Https value: " +
          				httpsValue +".");
    }
    
    this.headerName = headerName;
    this.httpValue = httpValue;
    this.httpsValue = httpsValue;
  }
  
  public String toString(){
    return "headerName: " + headerName +
      "; httpValue: " + httpValue + " httpsValue: " + httpsValue;
  }
  
}
