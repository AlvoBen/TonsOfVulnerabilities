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
package com.sap.httpclient.utils.dump;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Logs all data written to the dump LOG.
 *
 * @author Nikolai Neichev
 */
public class DumpOutputStream extends FilterOutputStream {

  /**
   * Original incoming stream.
   */
  private OutputStream out;

  /**
   * The dump log to use.
   */
  private Dump dump;

  /**
   * Create an instance that wraps the specified outgoing stream.
   *
   * @param out  The outgoing stream.
   * @param dump The Dump log to use.
   */
  public DumpOutputStream(OutputStream out, Dump dump) {
    super(out);
    this.out = out;
    this.dump = dump;
  }

  public void write(byte[] b, int off, int len) throws IOException {
    this.out.write(b, off, len);
    dump.outgoing(b, off, len);
  }

  public void write(int b) throws IOException {
    this.out.write(b);
    dump.outgoing(b);
  }

  public void write(byte[] b) throws IOException {
    this.out.write(b);
    dump.outgoing(b);
  }
}