package com.sap.httpclient.http.methods.webdav;

import com.sap.httpclient.http.methods.DataContainingRequest;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.Parameters;
import com.sap.httpclient.net.connection.HttpConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public abstract class PropertyXMLRequest extends DataContainingRequest {

  private ArrayList<String> properties = new ArrayList<String>();
  private HashMap<String, String> namespaces = new HashMap<String, String>();

  public static final int DEPTH_0 = 0;
  public static final int DEPTH_1 = 1;
  public static final int DEPTH_INFINITY = -1;

  // 0, 1, or infinity(-1)
  private int depth = DEPTH_INFINITY;

  /**
   * Used to identify if the data has been set
   */
  private boolean dataSet = false;

  public PropertyXMLRequest() {
  }

  public PropertyXMLRequest(String uri) {
    super(uri);
  }

  public boolean isDataSet() {
    return dataSet;
  }

  /**
   * Gets the PROPFIND depth setting
   * @return the depth value
   */
  public int getDepth() {
    return depth;
  }

  /**
   * Sets the depth setting
   * "0", "1" or "-1" for infinity
   * @param depth the value of the depth
   */
  public void setDepth(int depth) {
    if ( (depth < -1) || (depth > 1)) {
      throw new IllegalArgumentException("depth is illegal : " + depth);
    }
    this.depth = depth;
    if (depth == -1) {
      setRequestHeader(Header.DEPTH, "infinity");
    } else {
      setRequestHeader(Header.DEPTH, "" + depth);
    }
  }

  public HashMap<String, String> getNamespaces() {
    return namespaces;
  }

	/**
	 * Adds a namespace to the method specified by a prefix and the namespace URI.
   *
	 * @param prefix  the namespace prefix. Prefix 'D' is reserved for "DAV"
	 * @param namespaceURI  the namespace URI.
	 */
	public void addNamespace(String prefix, String namespaceURI) {
		if (prefix.endsWith(":")) {
      prefix = prefix.substring(0, prefix.length()-1);
    }
    if (prefix.equals("D")) {
      throw new IllegalArgumentException("Prefix 'D' is reserved for the \"DAV\" namespace.");
    }
		namespaces.put(prefix, namespaceURI);
	}

  /**
   * Clears all previously set namespaces
   */
  public void clearNamespaces() {
    namespaces.clear();
  }

  /**
   * Adds a single property to the PROPFIND method
   *
   * @param propertyName the property name including it's namespace prefix
   */
	public void addProperty(String propertyName) {
    if (propertyName.indexOf(":") == -1) {
      propertyName = "D:" + propertyName;
    }
	  properties.add(propertyName);
	}

  /**
   * Adds all of the specified properties to the PROPFIND method
   *
   * @param propertyNames the property names array
   */
	public void addProperties(String[] propertyNames) {
    for(String prop:propertyNames) {
      if (prop.indexOf(":") == -1) {
        prop = "D:" + prop;
      }
	    properties.add(prop);
    }
	}

  /**
   * Clears all previously set properties
   */
  public void clearProperties() {
    properties.clear();
  }

  /**
   * Gets the specified properties of the method
   * @return the properties; may be empty ArrayList
   */
  public ArrayList<String> getProperties() {
    return properties;
  }

  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    super.addRequestHeaders(state, conn);
    setRequestHeader(Header._CONTENT_TYPE, "text/xml; charset=\"utf-8\"");
    if (getDepth() == -1) {
      if (getParams().getBoolean(Parameters.USE_DEPTH_HEADER, false)) {
        setRequestHeader(Header.DEPTH, "infinity");
      } else {
        // deafult value is infinity, so we won't add the header
      }
    } else {
      setRequestHeader(Header.DEPTH, "" + getDepth());
    }
  }

  /**
   * Sets the request body of the PROPFIND method.
   * @param requestData the xml
   */
  public void setRequestData(RequestData requestData) {
    super.setRequestData(requestData);
    dataSet = true;
  }

  protected void clearRequestBody() {
    super.clearRequestBody();
    clearNamespaces();
    clearProperties();
    dataSet = false;
  }

}
