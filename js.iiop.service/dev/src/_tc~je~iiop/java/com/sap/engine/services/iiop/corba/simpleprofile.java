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
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;

/**
 * A structure used in IOR's prifile data in IIOP version 1.1 and above
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public final class SimpleProfile implements SimpleProfileInterface {

  private int tag;
  byte[] data;

  public SimpleProfile(SimpleProfile sp) {
    tag = sp.tag;
    data = new byte[sp.data.length];
    System.arraycopy(sp.data, 0, data, 0, data.length);
  }

  public SimpleProfile(int tag0, byte[] data0) {
    tag = tag0;
    data = data0;
  }

  public SimpleProfile(int tag0, CORBAInputStream is) {
    tag = tag0;
    int len = is.beginSequence();
    data = new byte[len];
    is.read_octet_array(data, 0, len);
  }

  public SimpleProfile(int tag0, byte[] data0, int pos, int len) {
    tag = tag0;
    data = new byte[len];
    System.arraycopy(data0, 0, data, pos, len);
  }

  public int getTag() {
    return tag;
  }

  public byte[] getData() {
    return data;
  }

}

