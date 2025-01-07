/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;


/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class SSL implements IDLEntity {
  public int port;
  public short target_supports;
  public short target_requires;

  public SSL() {
    port = 0;
    target_requires = 0;
    target_supports = 0;
  }

  public SSL(int port, short target_requires, short target_supports) {
    this.port = port;
    this.target_requires = target_requires;
    this.target_supports = target_supports;
  }

  public String toString() {
    String s = "<<SSL>>\r\n";
    s += "target_requires " + target_requires + "\r\n";
    s += "target_requires " + target_supports + "\r\n";
    s += "port  " + port + "\r\n";
    s += "<<\\SSL>>\r\n";
    return s;
  }
}
