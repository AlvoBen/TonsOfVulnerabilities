/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import java.util.*;
import com.sap.engine.lib.text.FastDateFormat;

/*
 *
 * @author Maria Jurova
 * @version 4.0
 */
public class Date {

  private static final Locale loc = Locale.US;
  private static final TimeZone localZone = TimeZone.getDefault();
  private static final FastDateFormat clfFormat = new FastDateFormat("dd/MMM/yyyy:HH:mm:ss", localZone, loc, false);
  private static final FastDateFormat listFormat = new FastDateFormat("dd-MMM-yyyy HH:mm", localZone, loc, false);

  public byte[] getDateCLF() {
    int offset = localZone.getRawOffset() / 3600000;
    int len = clfFormat.getLength();
    byte ret[] = new byte[len + 6];
    synchronized (clfFormat) {
      clfFormat.getDate(ret, 0);
    }
    ret[len] = (byte) ' ';

    if (offset > 0) {
      ret[len + 1] = (byte) '+';

      if (offset < 10) {
        ret[len + 2] = (byte) 48;
        ret[len + 3] = (byte) (offset + 48);
      } else {
        ret[len + 2] = (byte) (offset / 10 + 48);
        ret[len + 3] = (byte) (offset % 10 + 48);
      }

      ret[len + 4] = (byte) 48;
      ret[len + 5] = (byte) 48;
    } else {
      ret[len + 1] = (byte) '-';
      offset = -offset;

      if (offset < 10) {
        ret[len + 2] = (byte) 48;
        ret[len + 3] = (byte) (offset + 48);
      } else {
        ret[len + 2] = (byte) (offset / 10 + 48);
        ret[len + 3] = (byte) (offset % 10 + 48);
      }

      ret[len + 4] = (byte) 48;
      ret[len + 5] = (byte) 48;
    }

    return ret;
  }

  protected void getDateCLF(byte[] inByteArr, int off, long time) {
    int offset = localZone.getRawOffset() / 3600000;
    synchronized (listFormat) {
      listFormat.getDate(inByteArr, off, time);
    }
  }

}

