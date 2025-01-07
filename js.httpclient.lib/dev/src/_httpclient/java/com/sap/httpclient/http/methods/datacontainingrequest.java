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
package com.sap.httpclient.http.methods;

import com.sap.httpclient.ChunkedOutputStream;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.Parameters;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.exception.ProtocolException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.tc.logging.Location;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * This class serves as a base for HTTP methods that can contain data.
 *
 * @author Nikolai Neichev
 */
public abstract class DataContainingRequest extends ExpectingContinueRequest {

  /**
   * LOG object for this class.
   */
  private static final Location LOG = Location.getLocation(DataContainingRequest.class);

  /**
   * The unbuffered request body, if any.
   */
  private InputStream requestStream = null;

  /**
   * The request body as string, if any.
   */
  private String requestString = null;

  private RequestData requestData;

  /**
   * Counts how often the request was sent to the server.
   */
  private int repeatCount = 0;

  private boolean chunked = false;

  /**
   * Default constructor.
   */
  public DataContainingRequest() {
    super();
    setFollowRedirects(false);
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public DataContainingRequest(String uri) {
    super(uri);
    setFollowRedirects(false);
  }

  /**
   * Checks wether there is a request content
   *
   * @return boolean <tt>true</tt> if there is a request body to be sent, <tt>false</tt> otherwise
   */
  protected boolean hasRequestContent() {
    return (this.requestData != null) || (this.requestStream != null) || (this.requestString != null);
  }

  /**
   * Clears the request body.
   */
  protected void clearRequestBody() {
    this.requestStream = null;
    this.requestString = null;
    this.requestData = null;
  }

  /**
   * Generates the request body.
   *
   * @return request body as an array of bytes, <tt>null</tt> if not set
   */
  protected byte[] generateRequestBody() {
    return null;
  }

  protected RequestData generateRequestData() {
    byte[] requestBody = generateRequestBody();
    if (requestBody != null) {
      this.requestData = new ByteArrayRequestData(requestBody);
    } else if (this.requestStream != null) {
      this.requestData = new InputStreamRequestData(requestStream);
      this.requestStream = null;
    } else if (this.requestString != null) {
      String charset = getRequestCharSet();
      try {
        this.requestData = new StringRequestData(requestString, null, charset);
      } catch (UnsupportedEncodingException e) {
        if (LOG.beWarning()) {
          LOG.warningT(charset + " not supported");
        }
        this.requestData = new StringRequestData(requestString);
      }
    }
    return this.requestData;
  }

  /**
   * Entity enclosing requests cannot be redirected without user intervention according to RFC 2616.
   *
   * @return <code>false</code>.
   */
  public boolean getFollowRedirects() {
    return false;
  }

  /**
   * Entity enclosing requests cannot be redirected without user intervention according to RFC 2616.
   *
   * @param followRedirects must always be <code>false</code>
   */
  public void setFollowRedirects(boolean followRedirects) {
    if (followRedirects) {
      throw new IllegalArgumentException("Can't set the follow redirects to TRUE to this request");
    }
    super.setFollowRedirects(false);
  }

  /**
   * Returns the request's charset.
   * @return  the charset
   */
  public String getRequestCharSet() {
    if (getRequestHeader(Header._CONTENT_TYPE) == null) {
      if (this.requestData != null) {
        return getContentCharSet(new Header(Header._CONTENT_TYPE, requestData.getContentType()));
      } else {
        return super.getRequestCharSet();
      }
    } else {
      return super.getRequestCharSet();
    }
  }

  /**
   * Sets whether or not the content should be chunked.
   *
   * @param chunked <code>true</code> if the content should be chunked
   */
  public void setContentChunked(boolean chunked) {
    this.chunked = chunked;
  }

  /**
   * Returns the length of the request body.
   *
   * @return number of bytes in the request body
   */
  protected long getRequestContentLength() {
    if (!hasRequestContent()) {
      return 0;
    }
    if (this.chunked) {
      return -1;
    }
    if (this.requestData == null) {
      this.requestData = generateRequestData();
    }
    return (this.requestData == null) ? 0 : this.requestData.getContentLength();
  }

  /**
   * Adds the request headers to the specified {@link HttpConnection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    super.addRequestHeaders(state, conn);
    addContentLengthRequestHeader(state, conn);
    if (getRequestHeader(Header._CONTENT_TYPE) == null) { // if not set manually
      RequestData requestData = getRequesData();
      if (requestData != null && requestData.getContentType() != null) {
        setRequestHeader(Header._CONTENT_TYPE, requestData.getContentType());
      }
    }
  }

  /**
   * Generates <tt>Content-Length</tt> or <tt>Transfer-Encoding: Chunked</tt>
   * request header, as long as no <tt>Content-Length</tt> request header already exists.
   *
   * @param state current state of http requests
   * @param conn  the connection to use for I/O
   * @throws IOException   when errors occur reading or writing to/from the connection
   * @throws com.sap.httpclient.exception.HttpException when a recoverable error occurs
   */
  protected void addContentLengthRequestHeader(HttpState state, HttpConnection conn) throws IOException {
    if ((getRequestHeader(Header._CONTENT_LENGTH) == null)
         && (getRequestHeader(Header.__TRANSFER_ENCODING) == null)) {
      long len = getRequestContentLength();
      if (len < 0) {
        if (getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1)) {
          addRequestHeader(Header.__TRANSFER_ENCODING, "chunked");
        } else {
          throw new ProtocolException(getEffectiveVersion() + " does not support chunk encoding");
        }
      } else {
        addRequestHeader(Header._CONTENT_LENGTH, String.valueOf(len));
      }
    }
  }

  /**
   * Writes the request body to the specified {@link HttpConnection connection}.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @return <tt>true</tt>
   * @throws IOException   if an I/O error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException {
    if (!hasRequestContent()) { // nothing to send
      return true;
    }
    if (this.requestData == null) {
      this.requestData = generateRequestData();
    }
    if (requestData == null) { // no request data
      return true;
    }
    long contentLength = getRequestContentLength();
    if ((this.repeatCount > 0) && !requestData.isRepeatable()) {
      throw new ProtocolException("Data is not repeatable.");
    }
    this.repeatCount++;
    OutputStream outstream = conn.getOutputStream();

    ChunkedOutputStream chunked = null;
    if (contentLength < 0) {
      chunked = new ChunkedOutputStream(outstream);
      outstream = chunked;
    }

    // checking if there is a set parameter for data encoding
    String contentEncoding = (String) getParams().getParameter(Parameters.FORCE_CONTENT_ENCODING);
    LOG.infoT("Request content encoding : " + contentEncoding);
    if ("gzip".equalsIgnoreCase(contentEncoding)) {  // gzip encoded
      outstream = new GZIPOutputStream(outstream);
      setRequestHeader(Header._CONTENT_ENCODING, "gzip");
    } else if ("deflate".equalsIgnoreCase(contentEncoding)) { // deflate encoded
      outstream = new DeflaterOutputStream(outstream);
      setRequestHeader(Header._CONTENT_ENCODING, "deflate");
    }


    requestData.writeRequest(outstream);
    if (chunked != null) { // we have a chunked stream, so :
      chunked.finish();
    }
    outstream.flush();
    if (LOG.beDebug()) {
      LOG.debugT("Request body sent");
    }
    return true;
  }

  /**
   * Gets the request data
   * @return the requestData.
   */
  public RequestData getRequesData() {
    return generateRequestData();
  }

  /**
   * Sets the reques entity
   * @param requestData The requestData set.
   */
  public void setRequestData(RequestData requestData) {
    clearRequestBody();
    this.requestData = requestData;
  }

}