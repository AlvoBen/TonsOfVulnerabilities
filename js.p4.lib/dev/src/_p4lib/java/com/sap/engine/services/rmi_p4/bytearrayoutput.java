/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author  Georgi Stanev
 * @version  7.0
 */
public class ByteArrayOutput extends ByteArrayOutputStream {

  public ByteArrayOutput(int offset) {
    buf = new byte[offset + 512];
    count = offset;
  }

  public byte[] getBuffer() {
    return buf;
  }

  protected int getSize() {
    return count;
  }

  protected void writeData(byte[] target, int offset) {
    System.arraycopy(buf, 0, target, offset, count);
    //    buf = new byte[512];
    //    count = 0;
  }

}

