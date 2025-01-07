package com.sap.httpclient.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;

import com.sap.httpclient.ChunkedInputStream;
import com.sap.httpclient.ContentLengthInputStream;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HeaderElement;
import com.sap.httpclient.http.HeaderGroup;
import com.sap.httpclient.NameValuePair;

/**
 * A generic HTTP request.
 */
public class SimpleRequest {

  public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";

  private RequestLine requestLine = null;
  private HeaderGroup headers = new HeaderGroup();
  private InputStream entity = null;

  public SimpleRequest() {
    super();
  }

  public SimpleRequest(final RequestLine requestLine,
                       final ArrayList<Header> headers,
                       final InputStream content) {
    super();
    if (requestLine == null) {
      throw new IllegalArgumentException("Request line may not be null");
    }
    this.requestLine = requestLine;
    if (headers != null) {
      this.headers.setHeaders(headers);
    }
    if (content != null) {
      // only PUT and POST have content
      String methodname = requestLine.getMethod();
      if ("POST".equalsIgnoreCase(methodname) || "PUT".equalsIgnoreCase(methodname)) {
        Header contentLength = this.headers.getFirstHeader("Content-Length");
        Header transferEncoding = this.headers.getFirstHeader("Transfer-Encoding");
        InputStream in = content;
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
  }

  public SimpleRequest(final RequestLine requestLine, final ArrayList<Header> headers) {
    this(requestLine, headers, null);
  }

  public RequestLine getRequestLine() {
    return this.requestLine;
  }

  public void setRequestLine(final RequestLine requestline) {
    if (requestline == null) {
      throw new IllegalArgumentException("Request line may not be null");
    }
    this.requestLine = requestline;
  }

  public boolean containsHeader(final String name) {
    return this.headers.containsHeader(name);
  }

  public ArrayList<Header> getHeaders() {
    return this.headers.getAllHeaders();
  }

  public Header getFirstHeader(final String s) {
    return this.headers.getFirstHeader(s);
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

  public Iterator getHeaderIterator() {
    return this.headers.getIterator();
  }

  public String getContentType() {
    Header contenttype = this.headers.getFirstHeader("Content-Type");
    if (contenttype != null) {
      return contenttype.getValue();
    } else {
      return "text/plain";
    }
  }

  public String getCharset() {
    String charset = null;
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
    if (charset != null) {
      return charset;
    } else {
      return DEFAULT_CONTENT_CHARSET;
    }
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