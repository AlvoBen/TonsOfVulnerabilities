/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.http.methods.webdav;

import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.Parameters;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.exception.URIException;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.uri.URI;

import java.io.IOException;

/**
 * Implements the HTTP webdav COPY method.
 *
 * @author Nikolai Neichev
 */
public class COPY extends PropertyXMLRequest {

  public static final String BEHAVIOUR_OMIT ="omit";
  public static final String BEHAVIOUR_KEEPALIVE ="keepalive";

  private String propBehavior = null;

  // overwrite header values are : TRUE = "T" / FALSE = "F"
  // default is "T" or no header at all
  private boolean failOverwrite = false;

  /**
   * No-arg constructor.
   */
  public COPY() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public COPY(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"COPY"</tt>.
   *
   * @return <tt>"COPY"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_COPY;
  }

  /**
   * Set the copy destination
   * @param dest the destination as string (URI)
   * @throws URIException if the passed string is not valid URI
   */
  public void setDestination(String dest) throws URIException {
    setRequestHeader(Header.DESTINATION, new URI(dest).toString());
  }

  /**
   * Set the copy destination
   *
   * @param destUri the destination as URI
   */
  public void setDestination(URI destUri) {
    setRequestHeader(Header.DESTINATION, destUri.toString());
  }

  /**
   * If there is data on the destination, it won't be overwritten
   * Adds header <tt>Overwrite:F</tt>
   */
  public void doNotOverwrite() {
    failOverwrite = true;
  }

  /**
   * Sets the property behaviour value
   *
   * @param propBehavior the property behaviour value
   * Use COPY.BEHAVIOUR_OMIT or COPY.BEHAVIOUR_KEEPALIVE
   */
  public void setPropertyBehavior(String propBehavior) {
    if (!propBehavior.equals(BEHAVIOUR_OMIT) && !propBehavior.equals(BEHAVIOUR_KEEPALIVE) ) {
      throw new IllegalArgumentException("Incorrect property behavuour set : " + propBehavior);
    }
    this.propBehavior = propBehavior;
  }

  /**
   * Adds a keep alive property to the request
   *
   * @param keepAliveProp the property with the namespace prefix
   */
  public void addPropertyKeepAlive(String keepAliveProp) {
    addProperty(keepAliveProp);
    setPropertyBehavior(BEHAVIOUR_KEEPALIVE);
  }

  /**
   * Adds the specified keep alive properties to the request
   *
   * @param keepAlivePropArr the property with the namespace prefix
   */
  public void addPropertyKeepAlive(String[] keepAlivePropArr) {
    addProperties(keepAlivePropArr);
    setPropertyBehavior(BEHAVIOUR_KEEPALIVE);
  }

  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    super.addRequestHeaders(state, conn);
    if (failOverwrite) {
      setRequestHeader(Header.OVERWRITE , "F");
    } else if (getParams().getBoolean(Parameters.USE_OVERWRITE_HEADER, false)) {
      setRequestHeader(Header.OVERWRITE , "T");
    }
  }

  /**
   * Prepares the request data.
   *
   * @return A request entity for this PROPFIND request.
   */
	public RequestData generateRequestData() {
    if (!isDataSet() && propBehavior != null) { // there is a property behaviour specified
      StringBuilder body = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
      body.append("<D:propertybehavior xmlns:D=\"DAV:\"");
      if (!getNamespaces().isEmpty()) { // has specified namespaces
        String[] keys = new String[getNamespaces().size()];
        getNamespaces().keySet().toArray(keys);
        for (String key: keys) {
          body.append(" xmlns:").append(key).append("=\"").append(getNamespaces().get(key)).append("\"");
        }
      }
      body.append(">"); // propfind
      if (propBehavior.equals(BEHAVIOUR_OMIT)) {
        body.append("<omit/>");
      } else { // BEHAVIOUR_KEEPALIVE
        body.append("<D:keepalive>");
        if (getProperties().isEmpty()) {
          body.append("*");
        } else {
          for (String prop:getProperties()) {
            body.append("<D:href>");
            body.append("<" + prop + "/>");
            body.append("</D:href>");
          }

        }
        body.append("</D:keepalive>");
      }
      body.append("</D:propertybehavior>");
      RequestData data = new StringRequestData(body.toString());
      setRequestData(data);
      return data;
    } else {
      return super.generateRequestData();
    }
	}

//  public static void main(String[] args) {
//    COPY c = new COPY();
//    c.setPropertyBehavior(COPY.BEHAVIOUR_KEEPALIVE);
//    c.addNamespace("R","http://kukuruku");
//    c.addPropertyKeepAlive("R:NWN");
//    StringRequestData data = (StringRequestData) c.getRequesData();
//    System.out.println(data.getContent());
//  }

}