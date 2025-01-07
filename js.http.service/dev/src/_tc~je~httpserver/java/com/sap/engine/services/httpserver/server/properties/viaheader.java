package com.sap.engine.services.httpserver.server.properties;

/**
 * Information for the searched header in the reverse proxy mapping configuration.
 * Header and value are used for determining the reverse proxy data.
 * @author I044270
 *
 */
public class ViaHeader {
  private String headerName;
  private String headerValue;
  private byte[] headerNameBytes;
  private byte[] headerValueBytes;
  
  public ViaHeader(String headerName, String headerValue) {

    if (headerName == null || headerName.length() == 0 ) {
      throw new IllegalArgumentException("Header name must not be null or empty string");
    }
    
    if (headerValue == null || headerValue.length() == 0 ) {
      throw new IllegalArgumentException("Header value must not be null or empty string");
    }
    
    this.headerName = headerName;
    this.headerValue = headerValue;
    this.headerNameBytes = headerName.getBytes();
    this.headerValueBytes = headerValue.getBytes();
  }

  public byte[] getHeaderNameBytes() {
    return headerNameBytes;
  }

  public byte[] getHeaderValueBytes() {
    return headerValueBytes;
  }
  
  public String toString() {
    return headerName+ ": " + headerValue;
  }
}
