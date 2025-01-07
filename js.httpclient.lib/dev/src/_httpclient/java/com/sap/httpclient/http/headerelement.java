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
package com.sap.httpclient.http;

import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.utils.ParameterParser;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one element of an HTTP header's value.
 *
 * @author Nikolai Neichev
 */
public class HeaderElement extends NameValuePair {

  /**
   * The parameters
   */
  private NameValuePair[] parameters = null;

  /**
   * Default constructor.
   */
  public HeaderElement() {
    this(null, null, null);
  }

  /**
   * Constructor with specified name and value.
   *
   * @param name  the name
   * @param value the value
   */
  public HeaderElement(String name, String value) {
    this(name, value, null);
  }

  /**
   * Constructor with name, value and parameters.
   *
   * @param name       the name
   * @param value      the value
   * @param parameters the parameters
   */
  public HeaderElement(String name, String value, NameValuePair[] parameters) {
    super(name, value);
    this.parameters = parameters;
  }

  /**
   * Constructor with sub array of characters.
   *
   * @param chars  the array of characters
   * @param offset - the initial offset.
   * @param length - the length.
   */
  public HeaderElement(char[] chars, int offset, int length) {
    this();
    if (chars == null) {
      return;
    }
    ParameterParser parser = new ParameterParser();
    List<NameValuePair> params = parser.parse(chars, offset, length, ';');
    if (params.size() > 0) {
      NameValuePair element = params.remove(0);
      setName(element.getName());
      setValue(element.getValue());
      if (params.size() > 0) {
        this.parameters = params.toArray(new NameValuePair[params.size()]);
      }
    }
  }

  /**
   * Constructor with array of characters.
   *
   * @param chars the array of characters
   */
  public HeaderElement(char[] chars) {
    this(chars, 0, chars.length);
  }

  /**
   * Returns the parameters, if any.
   *
   * @return parameters as an array of {@link NameValuePair}s
   */
  public NameValuePair[] getParameters() {
    return this.parameters;
  }

  /**
   * Parses the header's value.
   *
   * @param headerValue char array representing the header value
   * @return array of {@link HeaderElement}s.
   */
  public static HeaderElement[] parseElements(char[] headerValue) {
    if (headerValue == null) {
      return new HeaderElement[]{};
    }
    List<HeaderElement> elements = new ArrayList<HeaderElement>();
    int index = 0;
    int from = 0;
    int len = headerValue.length;
    boolean qouted = false;
    while (index < len) {
      char ch = headerValue[index];
      if (ch == '"') {
        qouted = !qouted;
      }
      HeaderElement element = null;
      if ((!qouted) && (ch == ',')) {
        element = new HeaderElement(headerValue, from, index);
        from = index + 1;
      } else if (index == len - 1) {
        element = new HeaderElement(headerValue, from, len);
      }
      if ((element != null) && (element.getName() != null)) {
        elements.add(element);
      }
      index++;
    }
    return elements.toArray(new HeaderElement[elements.size()]);
  }

  /**
   * Parses the header's value.
   *
   * @param headerValue the string representation of the header value
   * @return array of {@link HeaderElement}s.
   */
  public static HeaderElement[] parseElements(String headerValue) {
    if (headerValue == null) {
      return new HeaderElement[]{};
    }
    return parseElements(headerValue.toCharArray());
  }

  /**
   * Returns parameter with the specified name.
   *
   * @param name the name to search by.
   * @return NameValuePair parameter with the specified name, <code>null</code> if not found
   */

  public NameValuePair getParameterByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name is null");
    }
    NameValuePair found = null;
    NameValuePair parameters[] = getParameters();
    if (parameters != null) {
      for (NameValuePair parameter : parameters) {
        if (parameter.getName().equalsIgnoreCase(name)) {
          found = parameter;
          break;
        }
      }
    }
    return found;
  }

}