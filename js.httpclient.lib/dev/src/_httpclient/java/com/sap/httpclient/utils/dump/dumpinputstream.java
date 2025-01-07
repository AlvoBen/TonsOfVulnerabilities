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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Logs all data read to the dump LOG.
 *
 * @author Nikolai Neichev
 */
public class DumpInputStream extends FilterInputStream {

  /**
   * Original incoming stream.
   */
  private InputStream in;

  /**
   * The dump log to use for writing.
   */
  private Dump dump;

  /**
   * Create an instance that wraps the specified input stream.
   *
   * @param in   the input stream.
   * @param dump the dump log to use.
   */
  public DumpInputStream(InputStream in, Dump dump) {
    super(in);
    this.in = in;
    this.dump = dump;
  }

  public int read(byte[] b, int off, int len) throws IOException {
    int l = this.in.read(b, off, len);
    if (l > 0) {
      dump.incoming(b, off, l);
    }
    return l;
  }

  public int read() throws IOException {
    int l = this.in.read();
    if (l > 0) {
      dump.incoming(l);
    }
    return l;
  }

  public int read(byte[] b) throws IOException {
    int l = this.in.read(b);
    if (l > 0) {
      dump.incoming(b, 0, l);
    }
    return l;
  }
}