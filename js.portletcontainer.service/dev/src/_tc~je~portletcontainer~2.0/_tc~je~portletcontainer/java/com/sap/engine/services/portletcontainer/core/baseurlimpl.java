/*
 * Copyright (c) 2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;

import com.sap.engine.services.portletcontainer.api.PortletConsumerURL;
import com.sap.engine.services.portletcontainer.spi.PortletNode;
import com.sap.engine.services.portletcontainer.spi.PortletURLProviderService;

/**
 * The <code>BaseURL</code> defines the basic capabilities of a portlet URL pointing back to the portlet. 
 * 
 * @author Diyan Yordanov
 * @version 7.12
 *
 */
public class BaseURLImpl implements BaseURL, PortletConsumerURL {

  /**
   * Encoding used to encode parameter names and values.
   */
  private static final String ENCODING = "UTF-8";

  /**
   * Indicated the security setting for this URL. 
   */
  private boolean secure = false;

  /**
   * Defines the PortletURL type:  action, render or resource
   */
  private String urlType = null;

  /**
   * Parameter map the portlet has set for this URL.
   */
  private HashMap<String, String[]> parameters = new HashMap<String, String[]>();

  /**
   * Properties can be used by portlets to set vendor specific information 
   * on the PortletURL object and thus use extended URL capabilities.
   */
  private Map<String, String> properties = new HashMap<String, String>();
  
  /**
   * Used to convert URLs to String 
   */
  private PortletURLProviderService urlService;

  
  /**
   * 
   * @param portletNode
   * @param urlType
   */
  public BaseURLImpl(PortletNode portletNode, String urlType) {
    this.urlType = urlType;
    this.urlService = portletNode.getPortletURLProviderService();
  }

  /**
   * Sets the given String parameter to this URL. 
   * <p>
   * This method replaces all parameters with the given key.
   * <p>
   * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
   * all  parameter names and values. Developers should not encode them.
   * @param name the parameter name.
   * @param value the parameter value.
   * @exception IllegalArgumentException if name or value are <code>null</code>.
   */
  public void setParameter(String name, String value) {
    if (name == null || value == null) {
      throw new IllegalArgumentException("name and value must not be null");
    }
    String[] values = new String[] { value };
    setParameter(name, values);
  }

  /**
   * Sets the given String array parameter to this URL. 
   * <p>
   * This method replaces all parameters with the given key.
   * <p>
   * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
   * all  parameter names and values. Developers should not encode them.
   * @param name the parameter name.
   * @param values the parameter values.
   * @exception IllegalArgumentException if name or values are <code>null</code>.
   */
  public void setParameter(String name, String[] values) {
    if (name == null || values == null || values.length == 0) {
      throw new IllegalArgumentException(
        "name and values must not be null or values be an empty array");
    }

    try {
      String newName = URLEncoder.encode(name, ENCODING);
      String[] newValues = new String[values.length];

      for (int i = 0; i < values.length; i++) {
        newValues[i] = URLEncoder.encode(values[i], ENCODING);
      }
      parameters.put(newName, newValues);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Sets a parameter map for this URL.
   * <p>
   * All previously set parameters are cleared.
   * <p>
   * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
   * all  parameter names and values. Developers should not encode them.
   * @param parameters Map containing parameter names for the render phase as
   * keys and parameter values as map values. The keys in the parameter
   * map must be of type String. The values in the parameter map must be of type
   * String array (<code>String[]</code>).
   * @exception	IllegalArgumentException if parameters is <code>null</code>, if
   * any of the key/values in the Map are <code>null</code>, if any of the keys 
   * is not a String, or if any of the values is not a String array.
   */
  public void setParameters(Map parameters) {
    if (parameters == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }

    if (parameters == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    
    Iterator<Map.Entry<?,?>> iterator = parameters.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<?,?> entry = iterator.next();
      //keys must be String
      if (!(entry.getKey() instanceof String)) {
        throw new IllegalArgumentException("Keys must be of type java.lang.String.");
      }
      //values must be String arrays
      if (!(entry.getValue() instanceof String[])) {
        throw new IllegalArgumentException("Values must be of type java.lang.String[].");
      }
    }

    HashMap<String, String[]> newParameters = new HashMap<String, String[]>(parameters);
    Iterator<Map.Entry<String, String[]>> iteratorNew = newParameters.entrySet().iterator();
    while (iteratorNew.hasNext()) {
      Map.Entry<String, String[]> entry = iteratorNew.next();
      String[] value = entry.getValue();
      int length = value.length;
      String[] newValue = new String[length];
      System.arraycopy(value, 0, newValue, 0, length);
      entry.setValue(newValue);
    }

    this.parameters = newParameters;
  }

  /**
   * Indicated the security setting for this URL. 
   * <p>
   * Secure set to <code>true</code> indicates that the portlet requests
   * a secure connection between the client and the portlet window for
   * this URL. Secure set to <code>false</code> indicates that the portlet 
   * does not need a secure connection for this URL. If the security is not
   * set for a URL, it will stay the same as the current request. 
   * @param  secure  true, if portlet requests to have a secure connection
   * between its portlet window and the client; false, if the portlet does not 
   * require a secure connection.
   * @throws PortletSecurityException  if the run-time environment does
   * not support the indicated setting.
   */
  public void setSecure(boolean secure) throws PortletSecurityException {
    this.secure = secure;
  }

  /**
   * Returns the portlet URL string representation to be embedded in the
   * markup.
   * <p>Note that the returned String may not be a valid URL, as it may
   * be rewritten by the portal before returning the markup to the client.
   * @return   the encoded URL as a string.
   */
  public String toString() {
    return urlService.convertPortalURLToString(this);
  }

  /**
   * Checks whether the portlet requests a secure conection between the client
   * and the porltet window for this URL.
   * @return <code>true</code>, if portlet requests to have a secure connection
   * between its portlet window and the client; <code>false</code>, if the portlet
   * does not require a secure connection.
   */
  public boolean isSecure() {
    return secure;
  }

  /**
   * Returns the parameter map the portlet has set for this URL.
   * @return <code>Map</code> containing parameter names for the render phase
   * as keys and parameter. 
   */
  public HashMap<String, String[]> getParameters() {
    return parameters;
  }

  /**
   * Adds a String property to an existing key on the URL.
   * <p>
   * This method allows URL properties to have multiple values.
   * <p>
   * Properties can be used by portlets to provide vendor specific information
   * to the URL.
   *
   * @param key
   *            the key of the property
   * @param value
   *            the value of the property
   *
   * @exception java.lang.IllegalArgumentException
   *                if key is <code>null</code>.
   *
   * @since 2.0
   */
  public void addProperty(String key, String value) {
    if(properties.containsKey(key)){
      String oldValue = properties.get(key);
      String newValue = oldValue + PROPERTIES_SEPARATOR + value;
      properties.put(key, newValue);
    }
    else{
      properties.put(key, value);
    }
  }

  public Map<String, String[]> getParameterMap() {
    return parameters;
  }

  /**
   * Adds a String property to an existing key on the URL.
   * <p>
   * This method allows URL properties to have multiple values.
   * <p>
   * Properties can be used by portlets to provide vendor specific information
   * to the URL.
   *
   * @param key
   *            the key of the property
   * @param value
   *            the value of the property
   *
   * @exception java.lang.IllegalArgumentException
   *                if key is <code>null</code>.
   *
   * @since 2.0
   */
  public void setProperty(String key, String value) {
    properties.put(key, value);
  }
  
  /**
   * Returns properties associated with this Portal URL
   * @return properties associated with this Portal URL 
   */
  public Map<String, String> getProperties() {
    return properties;
  }
  
  /**
   * Writes the portlet URL to the output stream using the provided writer.
   * <p>
   * Note that the URL written to the output stream may not be a valid URL, as it may
   * be rewritten by the portal/portlet-container before returning the 
   * markup to the client.
   * <p>
   * The URL written to the output stream is always XML escaped. For writing
   * non-escaped URLs use {@link #write(java.io.Writer, boolean)}.
   *  
   * @param out  the writer to write the portlet URL to
   * @throws java.io.IOException  if an I/O error occured while writing the URL
   *
   * @since 2.0
   */
  public void write(Writer writer) throws IOException {
    urlService.write(this, writer);
  }

  /**
   * Writes the portlet URL to the output stream using the provided writer.
   * If the parameter escapeXML is set to true the URL will be escaped to be
   * valid XML characters, i.e. &lt, &gt, &amp, &#039, &#034 will get converted
   * into their corresponding character entity codes (&lt to &&lt, &gt to &&gt, 
   * &amp to &&amp, &#039 to &&#039, &#034 to &&#034).
   * If escapeXML is set to false no escaping will be done.
   * <p>
   * Note that the URL written to the output stream may not be a valid URL, as it may
   * be rewritten by the portal/portlet-container before returning the 
   * markup to the client.
   *  
   * @param out       the writer to write the portlet URL to
   * @param escapeXML denotes if the URL should be XML escaped before written to the output
   *                  stream or not
   * @throws java.io.IOException  if an I/O error occurred while writing the URL
   *
   * @since 2.0
   */
  public void write(Writer writer, boolean escapeXML) throws IOException {
    urlService.write(this, writer, escapeXML);
  }


  /**
   * Returns string representation of this Portal URL type.
   * Possible values are <code>ACTION, RENDER, RESOURCE</code>
   *   
   * @return string representation of this Portal URL type.
   * @since 2.0
   */
  public String getType() {
    return urlType;
  }
}
