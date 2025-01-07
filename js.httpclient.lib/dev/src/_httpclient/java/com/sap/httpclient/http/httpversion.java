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

import com.sap.httpclient.exception.ProtocolException;

/**
 * Represents a Http version
 * </p>
 *     HTTP-Version   = "HTTP" "/" 1*DIGIT "." 1*DIGIT
 * <p/>
 *
 * @author Nikolai Neichev
 */
public class HttpVersion implements Comparable {

  /**
   * Major version
   */
  private int major = 0;

  /**
   * Minor version
   */
  private int minor = 0;

  /**
   * HTTP version 0.9
   */
  public static final HttpVersion HTTP_0_9 = new HttpVersion(0, 9);

  /**
   * HTTP version 0.9 string
   */
  public static final String  _HTTP_0_9 = "HTTP/0.9";

  /**
   * HTTP version 1.0
   */
  public static final HttpVersion HTTP_1_0 = new HttpVersion(1, 0);

  /**
   * HTTP version 1.0 string
   */
  public static final String  _HTTP_1_0 = "HTTP/1.0";

  /**
   * HTTP version 1.1
   */
  public static final HttpVersion HTTP_1_1 = new HttpVersion(1, 1);

  /**
   * HTTP version 1.1 string
   */
  public static final String  _HTTP_1_1 = "HTTP/1.1";

  /**
   * Instantiates a http version object with specified major and minor versions.
   *
   * @param major the major version
   * @param minor the minor version
   * @throws IllegalArgumentException if either major or minor version number is negative
   */
  public HttpVersion(int major, int minor) {
    if (major < 0) {
      throw new IllegalArgumentException("Major version is negative: " + major);
    }
    this.major = major;
    if (minor < 0) {
      throw new IllegalArgumentException("Minor version is negative: " + minor);
    }
    this.minor = minor;
  }

  /**
   * Returns the major version
   *
   * @return the major version
   */
  public int getMajor() {
    return major;
  }

  /**
   * Returns the minor version
   *
   * @return the minor version
   */
  public int getMinor() {
    return minor;
  }

  public int hashCode() {
    return this.major * 100000 + this.minor;
  }

  public boolean equals(Object obj) {
		return (this == obj) || ( obj instanceof HttpVersion && this.isEqualTo((HttpVersion) obj) );
	}

  /**
   * Compares this HTTP version object with another one.
   *
   * @param anotherVer the version to be compared with.
   * @return a negative integer, zero, or a positive integer as this version is
   *         less than, equal to, or greater than the specified version.
   */
  public int compareTo(HttpVersion anotherVer) {
    if (anotherVer == null) {
      throw new IllegalArgumentException("Version parameter is null");
    }
    int delta = getMajor() - anotherVer.getMajor();
    if (delta == 0) {
      delta = getMinor() - anotherVer.getMinor();
    }
    return delta;
  }

  public int compareTo(Object o) {
    return compareTo((HttpVersion) o);
  }

  /**
   * Test if the HTTP version is equal to the specified one.
	 * 
	 * @param version the version to compare
   * @return <tt>true</tt> if HTTP net version is specified to the specified, <tt>false</tt> otherwise.
   */
  public boolean isEqualTo(HttpVersion version) {
    return compareTo(version) == 0;
  }

  /**
   * Test if the HTTP version is greater or equal to the specified number.
	 *
	 * @param version the version to compare
   * @return <tt>true</tt> if HTTP version is greater or equal to the specified, <tt>false</tt> otherwise.
   */
  public boolean greaterEquals(HttpVersion version) {
    return compareTo(version) >= 0;
  }

  /**
   * Test if the HTTP version is less or equal to the specified.
	 *
	 * @param version the version to compare
   * @return <tt>true</tt> if HTTP version is less or equal to the specified, <tt>false</tt> otherwise.
   */
  public boolean lessEquals(HttpVersion version) {
    return compareTo(version) <= 0;
  }

  /**
   * Returns the string representation of the http version.
   * @return the http version
   */
  public String toString() {
    if (major == 0) {
      if (minor == 9) {
        return _HTTP_0_9;
      }
    } else if (major == 1) {
      if (minor == 0) {
        return _HTTP_1_0;
      } else if (minor == 1) {
        return _HTTP_1_1;
      }
    }
    // :) new version
    StringBuilder buffer = new StringBuilder();
    buffer.append("HTTP/");
    buffer.append(this.major);
    buffer.append('.');
    buffer.append(this.minor);
    return buffer.toString();
  }

  /**
   * Creates a http version object from the textual representation('HTTP/x.x')
   *
   * @param s HTTP Version string
   * @return HTTP version object.
   * @throws ProtocolException if the string is not a valid HTTP version.
   */
  public static HttpVersion parse(final String s) throws ProtocolException {
    if (s == null) {
      throw new IllegalArgumentException("String is null");
    }
    if (!s.startsWith("HTTP/")) {
      throw new ProtocolException("Invalid HTTP version string: " + s);
    }
    int major, minor;
    int i1 = "HTTP/".length();
    int i2 = s.indexOf(".", i1);
    if (i2 == -1) {
      throw new ProtocolException("Invalid HTTP version number: " + s);
    }
    try {
      major = Integer.parseInt(s.substring(i1, i2));
    } catch (NumberFormatException e) {
      throw new ProtocolException("Invalid HTTP major version number: " + s, e.getCause());
    }
    i1 = i2 + 1;
    i2 = s.length();
    try {
      minor = Integer.parseInt(s.substring(i1, i2));
    } catch (NumberFormatException e) {
      throw new ProtocolException("Invalid HTTP minor version number: " + s);
    }
    if (major == 0) {
      if (minor == 9) {
        return HTTP_0_9;
      }
    } else if (major == 1) {
      if (minor == 0) {
        return HTTP_1_0;
      } else if (minor == 1) {
        return HTTP_1_1;
      }
    }
    return new HttpVersion(major, minor);
  }

}