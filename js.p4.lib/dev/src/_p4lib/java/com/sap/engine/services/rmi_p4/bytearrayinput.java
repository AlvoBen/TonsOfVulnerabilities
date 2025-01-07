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

import java.io.ByteArrayInputStream;

/**
 *
 * @author  Georgi Stanev
 * @version  7.0
 */
public class ByteArrayInput extends ByteArrayInputStream {

  public ByteArrayInput(byte[] buf) {
    super(buf);
  }
  
	public ByteArrayInput(byte buf[], int offset, int length) {
		super(buf, offset, length);
	}
  
  public byte[] getBuffer() {
    return buf;
  }

  protected int getSize() {
    return count;
  }
  public void setBuffer(byte[] buffer){
    buf = buffer;
    this.count = buf.length;
  }
}

