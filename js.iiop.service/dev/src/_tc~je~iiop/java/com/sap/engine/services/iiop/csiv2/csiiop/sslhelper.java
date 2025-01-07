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

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;


/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class SSLHelper {
  public static SSL read(InputStream inputstream) {
    SSL sslComp = new SSL();
    sslComp.target_supports = inputstream.read_ushort();
    sslComp.target_requires = inputstream.read_ushort();
    sslComp.port = (inputstream.read_ushort() << 0) & 0x0000FFFF;
    return sslComp;
  }

  public static void write(OutputStream outputstream, SSL sslComp) {
    outputstream.write_ushort(sslComp.target_supports);
    outputstream.write_ushort(sslComp.target_requires);
    outputstream.write_ushort((short) (sslComp.port & 0x0000FFFF));
  }
}
