/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implclient;

import javax.naming.NamingException;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.exception.BaseRuntimeException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.naming.CompositeName;


/*Parses CORBA and jndi URL Schemas,if URL is given as a
 *name attribute to the InitialContext.Example Schemas are:
 *
 *corbaname::exampleHost.com/Prod/TradingService
 *corbaname:iiop:1.1@host.com:999/Prod/TradingService
 *corbaname:rir:#a/local/obj
 *ldap://host.com:900
 *
 *
 */
public class Schema extends CompositeName {

	private final static Location LOG_LOCATION = Location.getLocation(Schema.class);

  private String schema = "corbaname";
  private String name = null;
  private String host = null;
  private String objName = null;
  private String objKey = null;
  private String proto = "";
  public boolean isCname = false;
  private int port;
  private int minor;
  private int major;
  int index = 0;
  int[] found;
  char delim = ':';
  static final long serialVersionUID = -8579756623211706227L;

  public Schema(String name) throws javax.naming.NamingException {
    super(name);
    this.name = name;
    parseSchema();
  }

  public void parseSchema() throws javax.naming.NamingException {

    init(name);
    int index = 0;
    try {
      if ((found.length != 0) && (name.substring(0, found[index])).equals("corbaname")) {
        isCname = true;
        proto = name.substring((found[index] + 1), found[index + 1]);
        index++;

        if (index < (found.length - 1)) {
          parseWithPort(name, index);
        } else {
          parseWithoutP(name, index);
        }
      }
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Unable to parse name.", e);
      }
    }
  }

  public void parseWithoutP(String s1, int index) throws javax.naming.NamingException {
    int next = 0;
    int next2 = 0;

    if ((next = s1.indexOf("#")) != -1) {
      host = s1.substring((found[index] + 1), next);
      objKey = s1.substring(next);
    } else {
      next = s1.indexOf("/");
      host = s1.substring((found[index] + 1), next);
      objKey = s1.substring(next);
    }

    if ((next2 = host.indexOf("@")) != -1) {
      major = Integer.parseInt(host.substring(0, 1));
      minor = Integer.parseInt(host.substring(2, 3));
      char maj = host.substring(0, 1).charAt(0);
      char min = host.substring(2, 3).charAt(0);

      if (!Character.isDigit(maj) || (!Character.isDigit(min))) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("The version number is not a digit.");
        }
        throw new NamingException("The version number is not a digit.");
      }

      if ((major > 1) || (major < 0) || (minor > 3) || (minor < 0)) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Incorrect version number.");
        }
        throw new NamingException("Incorrect version number.");
      }

      host = host.substring((next2 + 1));
    }

    objName = objKey;
  }

  public void parseWithPort(String s1, int index) throws javax.naming.NamingException {
    host = s1.substring((found[index] + 1), found[index + 1]);
    int next = 0;

    if ((next = host.indexOf("@")) != -1) {
      major = Integer.parseInt(host.substring(0, 1));
      minor = Integer.parseInt(host.substring(2, 3));
      char maj = host.substring(0, 1).charAt(0);
      char min = host.substring(2, 3).charAt(0);

      if (!Character.isDigit(maj) || (!Character.isDigit(min))) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("The version number is not a digit.");
        }
        throw new NamingException("The version number is not a digit.");
      }

      if ((major > 1) || (major < 0) || (minor > 3) || (minor < 0)) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Incorrect version number.");
        }
        throw new NamingException("Incorrect version number.");
      }

      host = host.substring(next + 1);
    }

    int next2 = 0;

    if ((next2 = s1.indexOf("#")) != -1) {
      port = Integer.parseInt(s1.substring((found[index + 1] + 1), next2));
    } else {
      next2 = s1.indexOf("/");
      port = Integer.parseInt(s1.substring((found[index + 1] + 1), next2));
    }

    objKey = s1.substring(next2);
    objName = objKey;
  }

  public void init(String s) {
    int count = 0;

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == delim) {
        count++;
      }
    }

    found = new int[count];
    count = 0;

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == delim) {
        found[count] = i;
        count++;
      }
    }
  }

  public int getMinor() {
    return minor;
  }

  public int getMajor() {
    return major;
  }

  public String getObjKey() {
    return objKey;
  }

  public String getObjName() {
    return objName;
  }

  public String getHost() {
    return host;
  }

  public String getProtocol() {
    return proto;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name;
  }

  public String getSchema() {
    return schema;
  }

  public Object clone() {
    try {
      return new Schema(name);
    } catch (javax.naming.NamingException ne) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      throw new BaseRuntimeException(ne);
    }
  }

}

