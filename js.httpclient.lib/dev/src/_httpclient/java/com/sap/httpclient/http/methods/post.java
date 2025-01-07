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
package com.sap.httpclient.http.methods;

import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.tc.logging.Location;

import java.util.Iterator;
import java.util.Vector;

/**
 * Implements the HTTP POST method.
 *
 * @author Nikolai Neichev
 */
public class POST extends DataContainingRequest {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(POST.class);

  /**
   * The Content-Type for www-form-urlencoded.
   */
  public static final String FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

  /**
   * The buffered request body consisting of <code>NameValuePair</code>s.
   */
  private Vector<NameValuePair> params = new Vector<NameValuePair>();

  /**
   * Default constructor.
   */
  public POST() {
    super();
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public POST(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"POST"</tt>.
   *
   * @return <tt>"POST"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_POST;
  }

  /**
   * checks if there is a request body to send
   *
   * @return boolean <tt>true</tt> if there is a request body to be sent, <tt>false</tt> otherwise
   */
  protected boolean hasRequestContent() {
		return !this.params.isEmpty() || super.hasRequestContent();
	}

  /**
   * Clears request body.
   */
  protected void clearRequestBody() {
    this.params.clear();
    super.clearRequestBody();
  }

  /**
   * Generates a request data from the post parameters, if present.
   * @return the request data
   */
  protected RequestData generateRequestData() {
    if (!this.params.isEmpty()) {
      String content = EncodingUtil.getURLEncodedString(getParameters(), getRequestCharSet());
			return new ByteArrayRequestData(EncodingUtil.getASCIIBytes(content), FORM_URL_ENCODED_CONTENT_TYPE);
    } else {
      return super.generateRequestData();
    }
  }

  /**
   * Sets the value of parameter with parameterName to parameterValue.
   *
   * @param parameterName  name of the parameter
   * @param parameterValue value of the parameter
   */
  public void setParameter(String parameterName, String parameterValue) {
    removeParameter(parameterName);
    addParameter(parameterName, parameterValue);
  }

  /**
   * Gets the parameter of the specified name
   *
   * @param paramName name of the parameter
   * @return the corresponding NameValuePair is returned, <code>null</code> if not found
   */
  public NameValuePair getParameter(String paramName) {
    if (paramName == null) {
      return null;
    }
    for(NameValuePair parameter : this.params) {
      if (paramName.equals(parameter.getName())) {
        return parameter;
      }
    }
    return null;
  }

  /**
   * Gets the parameters currently added to the POST method.
   *
   * @return An array of the current parameters
   */
  public NameValuePair[] getParameters() {
    int numPairs = this.params.size();
    Object[] objectArr = this.params.toArray();
    NameValuePair[] nvPairArr = new NameValuePair[numPairs];
    for (int i = 0; i < numPairs; i++) {
      nvPairArr[i] = (NameValuePair) objectArr[i];
    }
    return nvPairArr;
  }

  /**
   * Adds a new parameter to be used in the POST request body.
   *
   * @param paramName  The parameter name to add.
   * @param paramValue The parameter value to add.
   * @throws IllegalArgumentException if either argument is null
   */
  public void addParameter(String paramName, String paramValue) throws IllegalArgumentException {
    if ((paramName == null) || (paramValue == null)) {
      throw new IllegalArgumentException("Null argument found : " + paramName + " , " + paramValue);
    }
    super.clearRequestBody();
    this.params.add(new NameValuePair(paramName, paramValue));
  }

  /**
   * Adds a new parameter to be used in the POST request body.
   *
   * @param param The parameter to add.
   * @throws IllegalArgumentException if the argument is null or contains null values
   */
  public void addParameter(NameValuePair param) throws IllegalArgumentException {
    if (param == null) {
      throw new IllegalArgumentException("NameValuePair is null");
    }
    addParameter(param.getName(), param.getValue());
  }

  /**
   * Adds an array of parameters to be used in the POST request body.
   *
   * @param parameters The array of parameters to add.
   */
  public void addParameters(NameValuePair[] parameters) {
    if (parameters == null) {
      LOG.warningT("Attempt to addParameters(null) ignored");
    } else {
      super.clearRequestBody();
      for (NameValuePair param : parameters) {
        this.params.add(param);
      }
    }
  }

  /**
   * Removes all parameters with the specified paramName.
   *
   * @param paramName The parameter name to remove.
   * @return true if at least one parameter was removed
   * @throws IllegalArgumentException if the parameter name passed is null
   */
  public boolean removeParameter(String paramName) throws IllegalArgumentException {
    if (paramName == null) {
      throw new IllegalArgumentException("paramName is null");
    }
    boolean removed = false;
    Iterator iter = this.params.iterator();
    while (iter.hasNext()) {
      NameValuePair pair = (NameValuePair) iter.next();
      if (paramName.equals(pair.getName())) {
        iter.remove();
        removed = true;
      }
    }
    return removed;
  }

  /**
   * Removes all parameter with the specified paramName and paramValue.
   *
   * @param paramName  The parameter name to remove.
   * @param paramValue The parameter value to remove.
   * @return true if a parameter was removed.
   * @throws IllegalArgumentException when param name or value are null
   */
  public boolean removeParameter(String paramName, String paramValue) throws IllegalArgumentException {
    if (paramName == null) {
      throw new IllegalArgumentException("paramName is null");
    }
    if (paramValue == null) {
      throw new IllegalArgumentException("paramValue is null");
    }
    Iterator iter = this.params.iterator();
    while (iter.hasNext()) {
      NameValuePair pair = (NameValuePair) iter.next();
      if (paramName.equals(pair.getName()) && paramValue.equals(pair.getValue())) {
        iter.remove();
        return true;
      }
    }
    return false;
  }

  /**
   * Sets an array of parameters to be used in the POST request body
   *
   * @param parametersBody The array of parameters to add.
   * @throws IllegalArgumentException when parameters are null
   */
  public void setRequestBody(NameValuePair[] parametersBody) throws IllegalArgumentException {
    if (parametersBody == null) {
      throw new IllegalArgumentException("parametersBody is null");
    }
    clearRequestBody();
    addParameters(parametersBody);
  }
}