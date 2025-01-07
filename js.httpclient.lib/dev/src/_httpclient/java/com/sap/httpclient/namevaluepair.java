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
package com.sap.httpclient;

import java.io.Serializable;

/**
 * <p>A simple class encapsulating a name/value pair.</p>
 *
 * @author Nikolai Neichev
 */
public class NameValuePair implements Serializable {

  private String name = null;

  private String value = null;

  /**
   * Default constructor.
   */
  public NameValuePair() {
    this(null, null);
  }

  /**
   * Constructor.
   *
   * @param name  The name.
   * @param value The value.
   */
  public NameValuePair(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Set the name.
   *
   * @param name The new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Return the name.
   *
   * @return String name The name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value.
   *
   * @param value The new value.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Return the current value.
   *
   * @return String value The current value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Get a String representation of this pair.
   *
   * @return A string representation.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder(60);
    sb.append("name=");
    sb.append(name);
    sb.append("value=");
    sb.append(value);
    return sb.toString();
  }

  public boolean equals(final Object object) {
    if (object == null) return false;
    if (this == object) return true;
    if (object instanceof NameValuePair) {
      NameValuePair that = (NameValuePair) object;
      return areEqual(this.name, that.name) && areEqual(this.value, that.value);
    } else {
      return false;
    }
  }

  private boolean areEqual(final Object obj1, final Object obj2) {
    return obj1 == null ? obj2 == null : obj1.equals(obj2);
  }

  public int hashCode() {
    int hash = 0;
    if (this.name != null) {
      hash += this.name.hashCode();
    }
    if (this.value != null) {
      hash += this.value.hashCode();
    }
    return hash;
  }
}