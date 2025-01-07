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
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;

/**
 * Implements the HTTP webdav PROPFIND method.
 * default depth is : PROPFIND.DEPTH_INFINITY
 * default option is : PROPFIND.OPTION_ALLPROP
 *
 * @author Nikolai Neichev
 */
public class PROPFIND extends PropertyXMLRequest {

  public static final String OPTION_ALLPROP = "<D:allprop/>";
  public static final String OPTION_PROPNAME = "<D:propname/>";
  public static final String OPTION_PROP = "D:prop";

  private String propOption = OPTION_ALLPROP;

  /**
   * No-arg constructor.
   */
  public PROPFIND() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public PROPFIND(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"PROPFIND"</tt>.
   *
   * @return <tt>"PROPFIND"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_PROPFIND;
  }

  /**
   * Adds a single property to the PROPFIND method
   *
   * @param propertyName the property name including it's namespace prefix
   */
	public void addProperty(String propertyName) {
    super.addProperty(propertyName);
		propOption = OPTION_PROP;
	}

  /**
   * Adds all of the specified properties to the PROPFIND method
   *
   * @param propertyNames the property names array
   */
	public void addProperties(String[] propertyNames) {
    super.addProperties(propertyNames);
		propOption = OPTION_PROP;
	}

	/**
	 * Sets the option of the PROPFIND method.
   *
	 * @param propOption  Use PROPFIND.OPTION_ALLPROP, PROPFIND.OPTION_PROPNAME or PROPFIND.OPTION_PROP
	 */
	public void setOption(String propOption) {
    if ( !propOption.equals(OPTION_ALLPROP) &&
         !propOption.equals(OPTION_PROPNAME) &&
         !propOption.equals(OPTION_PROP) ) {
      throw new IllegalArgumentException("Please, use the PROPFIND.OPTION_xxx constants, not string : " + propOption);
    }
		this.propOption = propOption;
	}

  /**
   * Prepares the request data.
   *
   * @return A request entity for this PROPFIND request.
   */
	public RequestData generateRequestData() { // WEBDAF.pdf (8.1 PROPFIND)
    if (!isDataSet()) {
      StringBuilder body = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
      body.append("<D:propfind xmlns:D=\"DAV:\"");
      if (!getNamespaces().isEmpty()) { // has specified namespaces
        String[] keys = new String[getNamespaces().size()];
        getNamespaces().keySet().toArray(keys);
        for (String key: keys) {
          body.append(" xmlns:").append(key).append("=\"").append(getNamespaces().get(key)).append("\"");
        }
      }
      body.append(">"); // propfind
      if (propOption.equals(OPTION_ALLPROP) || propOption.equals(OPTION_PROPNAME)) {
        body.append(propOption);
      } else {
        if (!getProperties().isEmpty()) { // has properties
  				body.append("<").append(OPTION_PROP).append(">");
  				for (String prop : getProperties()) {
  					body.append("<").append(prop).append("/>");
  				}
  				body.append("</").append(OPTION_PROP).append(">");
  			}
  		}
  		body.append("</D:propfind>");
      RequestData data = new StringRequestData(body.toString());
      setRequestData(data);
      return data;
    } else {
      return super.generateRequestData();
    }
	}

//  public static void main(String[] args) {
//    PROPFIND p = new PROPFIND();
//    p.addNamespace("R", "http://www.foo.bar/boxschema");
//    p.setOption(PROPFIND.OPTION_PROP);
//    p.addProperty("R:bigbox");
//    p.addProperty("R:author");
//    p.addProperty("R:DingALong");
//    p.addProperty("R:Random");
//    p.addProperty("nwnTest");
//    StringRequestData data = (StringRequestData) p.getRequesData();
//    System.out.println(data.getContent());
//  }

}