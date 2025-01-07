package com.sap.httpclient.server;

public class SimpleHost implements Cloneable {

  private String hostname = null;

  private int port = -1;

  public SimpleHost(final String hostname, int port) {
    super();
    if (hostname == null) {
      throw new IllegalArgumentException("Host name may not be null");
    }
    if (port < 0) {
      throw new IllegalArgumentException("Port may not be negative");
    }
    this.hostname = hostname;
    this.port = port;
  }

  public SimpleHost(final SimpleHost httphost) {
    super();
    this.hostname = httphost.hostname;
    this.port = httphost.port;
  }

  public Object clone() throws CloneNotSupportedException {
    return new SimpleHost(this);
  }

  public String getHostName() {
    return this.hostname;
  }

  public int getPort() {
    return this.port;
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder(50);
    buffer.append(this.hostname);
    buffer.append(':');
    buffer.append(this.port);
    return buffer.toString();
  }

  public boolean equals(final Object o) {

    if (o instanceof SimpleHost) {
      if (o == this) {
        return true;
      }
      SimpleHost that = (SimpleHost) o;
			return this.hostname.equalsIgnoreCase(that.hostname) && this.port == that.port;
		} else {
      return false;
    }
  }

  public int hashCode() {
    return this.hostname.hashCode() + this.port;
  }

}