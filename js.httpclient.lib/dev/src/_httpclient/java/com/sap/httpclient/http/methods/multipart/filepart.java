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
package com.sap.httpclient.http.methods.multipart;

import com.sap.httpclient.uri.EncodingUtil;
import com.sap.tc.logging.Location;

import java.io.*;

/**
 * This class implements a part of a Multipart post object that consists of a file.
 *
 * @author Nikolai Neichev
 */
public class FilePart extends PartBase {

  /**
   * Default content encoding of file attachments.
   */
  private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

  /**
   * Default charset of file attachments.
   */
  private static final String DEFAULT_CHARSET = "ISO-8859-1";

  /**
   * Default transfer encoding of file attachments.
   */
  private static final String DEFAULT_TRANSFER_ENCODING = "binary";

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(FilePart.class);

  /**
   * Attachment's file name
   */
  protected static final String FILE_NAME = "; filename=";

  /**
   * Attachment's file name as a byte array
   */
  private static final byte[] FILE_NAME_BYTES = EncodingUtil.getASCIIBytes(FILE_NAME);

  /**
   * Source of the file part.
   */
  private PartSource source;

  /**
   * FilePart Constructor.
   *
   * @param name        the name for this part
   * @param partSource  the source for this part
   * @param contentType the content type for this part
   * @param charset     the charset encoding for this part
   */
  public FilePart(String name, PartSource partSource, String contentType, String charset) {
    super(name,
          contentType == null ? DEFAULT_CONTENT_TYPE : contentType,
          charset == null ? DEFAULT_CHARSET : charset,
          DEFAULT_TRANSFER_ENCODING);

    if (partSource == null) {
      throw new IllegalArgumentException("Source is null");
    }
    this.source = partSource;
  }

  /**
   * FilePart Constructor.
   *
   * @param name       the name for this part
   * @param partSource the source for this part
   */
  public FilePart(String name, PartSource partSource) {
    this(name, partSource, null, null);
  }

  /**
   * FilePart Constructor.
   *
   * @param name the name of the file part
   * @param file the file to post
   * @throws FileNotFoundException if the <i>file</i> is not a normal file or if it is not readable.
   */
  public FilePart(String name, File file) throws FileNotFoundException {
    this(name, new FilePartSource(file), null, null);
  }

  /**
   * FilePart Constructor.
   *
   * @param name        the name of the file part
   * @param file        the file to post
   * @param contentType the content type for this part
   * @param charset     the charset encoding for this part
   * @throws FileNotFoundException if the <i>file</i> is not a normal file or if it is not readable.
   */
  public FilePart(String name, File file, String contentType, String charset) throws FileNotFoundException {
    this(name, new FilePartSource(file), contentType, charset);
  }

  /**
   * FilePart Constructor.
   *
   * @param name     the name of the file part
   * @param fileName the file name
   * @param file     the file to post
   * @throws FileNotFoundException if the <i>file</i> is not a normal file or if it is not readable.
   */
  public FilePart(String name, String fileName, File file) throws FileNotFoundException {
    this(name, new FilePartSource(fileName, file), null, null);
  }

  /**
   * FilePart Constructor.
   *
   * @param name        the name of the file part
   * @param fileName    the file name
   * @param file        the file to post
   * @param contentType the content type for this part
   * @param charset     the charset encoding for this part
   * @throws FileNotFoundException if the <i>file</i> is not a normal file or if it is not readable.
   */
  public FilePart(String name, String fileName, File file, String contentType, String charset)
          throws FileNotFoundException {
    this(name, new FilePartSource(fileName, file), contentType, charset);
  }

  /**
   * Write the disposition header to the outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an I/O problem occurs
   */
  protected void sendDispositionHeader(OutputStream out) throws IOException {
    super.sendDispositionHeader(out);
    String filename = this.source.getFileName();
    if (filename != null) {
      out.write(FILE_NAME_BYTES);
      out.write(QUOTE_BYTES);
      out.write(EncodingUtil.getASCIIBytes(filename));
      out.write(QUOTE_BYTES);
    }
  }

  /**
   * Write the data in "source" to the specified stream.
   *
   * @param out The outgoing stream.
   * @throws IOException if an IO problem occurs.
   */
  protected void sendData(OutputStream out) throws IOException {
    if (lengthOfData() == 0) {
      LOG.debugT("No data to send.");
      return;
    }
    byte[] tmp = new byte[4096];
    InputStream instream = source.createInputStream();
    try {
      int len;
      while ((len = instream.read(tmp)) >= 0) {
        out.write(tmp, 0, len);
      }
    } finally {
      instream.close();
    }
  }

  /**
   * Returns the source of the file part.
   *
   * @return The source.
   */
  protected PartSource getSource() {
    return this.source;
  }

  /**
   * Return the length of the data.
   *
   * @return The length.
   * @throws IOException if an IO problem occurs
   */
  protected long lengthOfData() throws IOException {
    return source.getLength();
  }

}