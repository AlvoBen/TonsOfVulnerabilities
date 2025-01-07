package com.sap.httpclient.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.ArrayList;

import com.sap.httpclient.ChunkedInputStream;
import com.sap.httpclient.ContentLengthInputStream;
import com.sap.httpclient.http.HeaderGroup;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.http.StatusLine;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HeaderElement;

/**
 * A generic HTTP response.
 */
public class SimpleResponse {

  public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";

  private HttpVersion ver = HttpVersion.HTTP_1_1;
  private int statuscode = HttpStatus.SC_OK;
  private String phrase = HttpStatus.getReasonPhrase(HttpStatus.SC_OK);
  private HeaderGroup headers = new HeaderGroup();
  private InputStream entity = null;

  public SimpleResponse() {
    super();
  }

  public SimpleResponse(final StatusLine statusline,
                        final ArrayList<Header> headers,
                        final InputStream content)
          throws IOException {
    super();
    if (statusline == null) {
      throw new IllegalArgumentException("Status line may not be null");
    }
    setStatusLine(HttpVersion.parse(statusline.getHttpVersion()),
            statusline.getStatusCode(), statusline.getReasonPhrase());
    setHeaders(headers);
    if (content != null) {
      InputStream in = content;
      Header contentLength = this.headers.getFirstHeader("Content-Length");
      Header transferEncoding = this.headers.getFirstHeader("Transfer-Encoding");
      if (transferEncoding != null) {
        if (transferEncoding.getValue().indexOf("chunked") != -1) {
          in = new ChunkedInputStream(in);
        }
      } else if (contentLength != null) {
        long len = getContentLength();
        if (len >= 0) {
          in = new ContentLengthInputStream(in, len);
        }
      }
      this.entity = in;
    }
  }

  public void setStatusLine(final HttpVersion ver, int statuscode, final String phrase) {
    if (ver == null) {
      throw new IllegalArgumentException("HTTP version may not be null");
    }
    if (statuscode <= 0) {
      throw new IllegalArgumentException("Status code may not be negative or zero");
    }
    this.ver = ver;
    this.statuscode = statuscode;
    if (phrase != null) {
      this.phrase = phrase;
    } else {
      this.phrase = HttpStatus.getReasonPhrase(statuscode);
    }
  }

  public void setStatusLine(final HttpVersion ver, int statuscode) {
    setStatusLine(ver, statuscode, null);
  }

  public String getPhrase() {
    return this.phrase;
  }

  public int getStatuscode() {
    return this.statuscode;
  }

  public HttpVersion getHttpVersion() {
    return this.ver;
  }

  public String getStatusLine() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(this.ver);
    buffer.append(' ');
    buffer.append(this.statuscode);
    if (this.phrase != null) {
      buffer.append(' ');
      buffer.append(this.phrase);
    }
    return buffer.toString();
  }

  public boolean containsHeader(final String name) {
    return this.headers.containsHeader(name);
  }

  public ArrayList<Header> getHeaders() {
    return this.headers.getAllHeaders();
  }

  public Header getFirstHeader(final String name) {
    return this.headers.getFirstHeader(name);
  }

  public void removeHeaders(final String s) {
    if (s == null) {
      return;
    }
    ArrayList<Header> headers = this.headers.getHeaders(s);
    for (Header header : headers) {
      this.headers.removeHeader(header);
    }
  }

  public void addHeader(final Header header) {
    if (header == null) {
      return;
    }
    this.headers.addHeader(header);
  }

  public void setHeader(final Header header) {
    if (header == null) {
      return;
    }
    removeHeaders(header.getName());
    addHeader(header);
  }

  public void setHeaders(final ArrayList<Header> headers) {
    if (headers == null) {
      return;
    }
    this.headers.setHeaders(headers);
  }

  public Iterator getHeaderIterator() {
    return this.headers.getIterator();
  }

  public String getCharset() {
    String charset = DEFAULT_CONTENT_CHARSET;
    Header contenttype = this.headers.getFirstHeader("Content-Type");
    if (contenttype != null) {
      HeaderElement values[] = contenttype.getElements();
      if (values.length == 1) {
        NameValuePair param = values[0].getParameterByName("charset");
        if (param != null) {
          charset = param.getValue();
        }
      }
    }
    return charset;
  }

  public long getContentLength() {
    Header contentLength = this.headers.getFirstHeader("Content-Length");
    if (contentLength != null) {
      try {
        return Long.parseLong(contentLength.getValue());
      } catch (NumberFormatException e) {
        return -1;
      }
    } else {
      return -1;
    }
  }

  public void setBodyString(final String string) {
    if (string != null) {
      byte[] raw;
      try {
        raw = string.getBytes(DEFAULT_CONTENT_CHARSET);
      } catch (UnsupportedEncodingException e) {
        raw = string.getBytes();
      }
      this.entity = new ByteArrayInputStream(raw);
      if (!containsHeader("Content-Type")) {
        setHeader(new Header("Content-Type", "text/plain"));
      }
      setHeader(new Header("Content-Length", Long.toString(raw.length)));
    } else {
      this.entity = null;
    }
  }

  public void setBody(final InputStream instream) {
    this.entity = instream;
  }

  public InputStream getBody() {
    return this.entity;
  }

  public byte[] getBodyBytes() throws IOException {
    InputStream in = getBody();
    if (in != null) {
      byte[] tmp = new byte[4096];
      int bytesRead;
      ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
      while ((bytesRead = in.read(tmp)) != -1) {
        buffer.write(tmp, 0, bytesRead);
      }
      return buffer.toByteArray();
    } else {
      return null;
    }
  }

  public String getBodyString() throws IOException {
    byte[] raw = getBodyBytes();
    if (raw != null) {
      return new String(raw, getCharset());
    } else {
      return null;
    }
  }
}