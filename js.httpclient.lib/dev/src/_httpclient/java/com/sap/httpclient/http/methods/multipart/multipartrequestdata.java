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

import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Implements a request entity suitable for an HTTP multipart POST method.
 *
 * @author Nikolai Neichev
 */
public class MultipartRequestData implements RequestData {

  private static final Location LOG = Location.getLocation(MultipartRequestData.class);

  /**
   * The Content-Type for multipart/form-data.
   */
  private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";

  /**
   * The pool of ASCII chars to be used for generating a multipart boundary.
   */
  private static byte[] MULTIPART_CHARS =
          EncodingUtil.getASCIIBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

  /**
   * The MIME parts as set by the constructor
   */
  protected Part[] parts;

  private byte[] multipartBoundary;

  private HttpClientParameters params;

  /**
   * Generates a random multipart boundary string.
   *
   * @return
   */
  private static byte[] generateMultipartBoundary() {
    Random rand = new Random();
    byte[] bytes = new byte[rand.nextInt(11) + 30]; // a random size from 30 to 40
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
    }
    return bytes;
  }

  /**
   * Creates a new multipart entity containing the specified parts.
   *
   * @param parts  The parts to include.
   * @param params The params of the HttpMethod using this entity.
   */
  public MultipartRequestData(Part[] parts, HttpClientParameters params) {
    if (parts == null) {
      throw new IllegalArgumentException("parts cannot be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("params cannot be null");
    }
    this.parts = parts;
    this.params = params;
  }

  /**
   * Returns the MIME boundary string that is used to demarcate boundaries of
   * this part. The first call to this method will implicitly create a new
   * boundary string. To create a boundary string first the
   * HttpClientParameters.MULTIPART_BOUNDARY parameter is considered. Otherwise
   * a random one is generated.
   *
   * @return The boundary string of this entity in ASCII encoding.
   */
  protected byte[] getMultipartBoundary() {
    if (multipartBoundary == null) {
      String temp = (String) params.getParameter(HttpClientParameters.MULTIPART_BOUNDARY);
      if (temp != null) {
        multipartBoundary = EncodingUtil.getASCIIBytes(temp);
      } else {
        multipartBoundary = generateMultipartBoundary();
      }
    }
    return multipartBoundary;
  }

  /**
   * Returns <code>true</code> if all parts are repeatable, <code>false</code> otherwise.
   */
  public boolean isRepeatable() {
    for (Part part : parts) {
      if (!part.isRepeatable()) {
        return false;
      }
    }
    return true;
  }

  public void writeRequest(OutputStream out) throws IOException {
    Part.sendParts(out, parts, getMultipartBoundary());
  }

  public long getContentLength() {
    try {
      return Part.getLengthOfParts(parts, getMultipartBoundary());
    } catch (Exception e) {
      LOG.traceThrowableT(Severity.ERROR, "An exception occurred while getting the length of the parts", e);
      return 0;
    }
  }

  public String getContentType() {
    StringBuilder buffer = new StringBuilder(MULTIPART_FORM_CONTENT_TYPE);
    buffer.append("; boundary=");
    buffer.append(EncodingUtil.getASCIIString(getMultipartBoundary()));
    return buffer.toString();
  }

}

//Example usage:
//
//File f = new File("/path/fileToUpload.txt");
//POST filePost = new POST("http://host/some_path");
//Part[] parts = {new StringPart("param_name", "value"), new FilePart(f.getName(), f)};
//filePost.setRequestData(new MultipartRequestData(parts, filePost.getParams()));
//HttpClient client = new HttpClient();
//int status = client.executeMethod(filePost);