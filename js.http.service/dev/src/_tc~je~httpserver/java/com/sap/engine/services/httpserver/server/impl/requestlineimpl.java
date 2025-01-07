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
package com.sap.engine.services.httpserver.server.impl;

import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.interfaces.client.RequestLine;
import com.sap.engine.services.httpserver.interfaces.exceptions.ParseException;

public class RequestLineImpl implements RequestLine, Cloneable {

  private MessageBytes requestLine = null;
  private MessageBytes requestLineDecoded = null;
  private byte[] method = null;
  private MessageBytes fullUrl = null;
  private MessageBytes urlDecoded = null;
  private MessageBytes urlNotDecoded = null;
  private String scheme = null;
  private boolean isSecure = false;
  private byte[] hostAndPort = null;
  private byte[] host = null;
  private int port = -1;
  private MessageBytes query = null;
  private int httpMinorVersion = -1;
  private int httpMajorVersion = -1;
  private boolean simpleRequest = false;

  private boolean isURLEncoded = false;
  private boolean hostAndPortParsed = false;
  private boolean urlNotDecodedParsed = false;

  public RequestLineImpl() {
    reset();
  }

  public void reset() {
    requestLine = null;
    requestLineDecoded = null;
    urlDecoded = null;
    fullUrl = null;
    hostAndPort = null;
    scheme = null;
    isSecure = false;
    query = null;
    urlNotDecoded = null;
    method = null;
    isURLEncoded = false;
    httpMinorVersion = -1;
    httpMajorVersion = -1;
    port = -1;
    host = null;
    hostAndPortParsed = false;
    urlNotDecodedParsed = false;
    simpleRequest = false;
  }

  public void init(RequestLineImpl olRequestLine) {
    requestLine = olRequestLine.requestLine;
    requestLineDecoded = olRequestLine.requestLineDecoded;
    urlDecoded = olRequestLine.urlDecoded;
    fullUrl = olRequestLine.fullUrl;
    hostAndPort = olRequestLine.hostAndPort;
    scheme = olRequestLine.scheme;
    isSecure = olRequestLine.isSecure;
    query = olRequestLine.query;
    urlNotDecoded = olRequestLine.urlNotDecoded;
    method = olRequestLine.method;
    isURLEncoded = olRequestLine.isURLEncoded;
    httpMinorVersion = olRequestLine.httpMinorVersion;
    httpMajorVersion = olRequestLine.httpMajorVersion;
    port = olRequestLine.port;
    host = olRequestLine.host;
    hostAndPortParsed = olRequestLine.hostAndPortParsed;
    urlNotDecodedParsed = olRequestLine.urlNotDecodedParsed;
    simpleRequest = olRequestLine.simpleRequest;
  }

  public Object clone() {
    RequestLineImpl newRequestLine = new RequestLineImpl();
    newRequestLine.requestLine = requestLine;
    newRequestLine.requestLineDecoded = requestLineDecoded;
    newRequestLine.urlDecoded = urlDecoded;
    newRequestLine.fullUrl = fullUrl;
    newRequestLine.hostAndPort = hostAndPort;
    newRequestLine.port = port;
    newRequestLine.scheme = scheme;
    newRequestLine.isSecure = isSecure;
    newRequestLine.query = query;
    newRequestLine.urlNotDecoded = urlNotDecoded;
    newRequestLine.method = method;
    newRequestLine.isURLEncoded = isURLEncoded;
    newRequestLine.httpMinorVersion = httpMinorVersion;
    newRequestLine.httpMajorVersion = httpMajorVersion;
    newRequestLine.host = host;
    newRequestLine.hostAndPortParsed = hostAndPortParsed;
    newRequestLine.urlNotDecodedParsed = urlNotDecodedParsed;
    newRequestLine.simpleRequest = simpleRequest;
    return newRequestLine;
  }

  public byte[] toByteArray() {
    return requestLine.getBytes();
  }

  public byte[] getMethod() {
    return method;
  }

  public int getHttpMinorVersion() {
    return httpMinorVersion;
  }

  public int getHttpMajorVersion() {
    return httpMajorVersion;
  }


  /**
   * Returns the URI of the request. I.e. the full first line of the request.
   *
   * @return
   */
  public MessageBytes getFullUrl() {
    return fullUrl;
  }

  public byte[] getHost() {
    if (!hostAndPortParsed) {
      parseHostAndPort();
    }
    return host;
  }

  public int getPort() {
    if (!hostAndPortParsed) {
      parseHostAndPort();
    }
    return port;
  }

  public MessageBytes getQuery() {
    return query;
  }


  /*
   * NOT DECODED
   */
  public MessageBytes getUrlNotDecoded() throws ParseException {
    if (!urlNotDecodedParsed) {
      parseUrlNotDecoded();
    }
    return urlNotDecoded;
  }


  public MessageBytes getUrlDecoded() {
    return urlDecoded;
  }

  public boolean isEncoded() {
    return isURLEncoded;
  }

  public String getScheme() {
    return scheme;
  }

  public boolean isSecure() {
    return isSecure;
  }

  public MessageBytes getRequestLine() {
    return requestLine;
  }

  // internal

  public void setScheme(boolean isSsl) {
    isSecure = isSsl;
    if (isSecure) {
      scheme = https;
    } else {
      scheme = http;
    }
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
    if (http.equalsIgnoreCase(scheme)) {
      isSecure = false;
    } else {
      isSecure = true;
    }
  }

  public boolean isSimpleRequest() {
    return simpleRequest;
  }

  public int parseRequestLine(byte[] input, int offset, int lengfth) throws ParseException {
    int read = readRequestLine(input, offset, lengfth);
    init();
    return read;
  }

  /*
   */
  private int readRequestLine(byte[] input, int inputOff, int originalInptuLen) throws ParseException {
    byte[] notDecodedInput = null;
    int inputLen = ByteArrayUtils.indexOf(input, (byte)'\n', inputOff) - inputOff - 1;
    while (inputLen == 0 && originalInptuLen > 1) {
      inputOff += 2;
      originalInptuLen -= 2;
      inputLen = ByteArrayUtils.indexOf(input, (byte)'\n', inputOff) - inputOff - 1;
    }
    if (originalInptuLen <= 1 || inputLen < 0) {
      throw new ParseException(ParseException.INCORRECT_REQUEST_LINE, new Object[]{""});
    }
    int offset = inputOff;

    boolean parsingQuery = false;
    byte currentByte;
    byte encodedByte;

    int decodedOff = inputOff;
    for (int endIndex = inputOff + inputLen; inputOff < endIndex; inputOff++, decodedOff++) {
      currentByte = input[inputOff];
      input[decodedOff] = currentByte;
      if (!parsingQuery && currentByte == 37 && inputOff < endIndex - 1) {
        encodedByte = Ascii.makeHexDigit(input, inputOff);
        if (encodedByte == 0) {
          //not a hexadecimal value (0-9 & a-f)
          input[decodedOff + 1] = input[inputOff + 1];
          input[decodedOff + 2] = input[inputOff + 2];
          decodedOff += 2;
        } else {
          if (!isURLEncoded) {
            notDecodedInput = new byte[inputLen];
            System.arraycopy(input, offset, notDecodedInput, 0, notDecodedInput.length);
          }
          isURLEncoded = true;
          input[decodedOff] = encodedByte;
        }
        inputOff += 2;
      } else if (currentByte == 63) {
        parsingQuery = true;
      }
    }

    if (isURLEncoded) {
      requestLine = new MessageBytes(notDecodedInput);
      byte[] requestLineDecodedBytes = new byte[decodedOff - offset];
      System.arraycopy(input, offset, requestLineDecodedBytes, 0, requestLineDecodedBytes.length);
      requestLineDecoded = new MessageBytes(requestLineDecodedBytes);
    } else {
      requestLine = new MessageBytes(input, offset, inputLen);
      requestLineDecoded = requestLine;
    }
    return offset + inputLen + 2;
  }

  private void init() throws ParseException {
    int firstSpace = requestLineDecoded.indexOf(' ');
    int lastSpace = requestLineDecoded.lastIndexOf(' ');
    if (firstSpace == -1) {
      throw new ParseException(ParseException.INCORRECT_REQUEST_LINE, new Object[]{requestLine});
    } else if (firstSpace == lastSpace) {
      lastSpace = requestLineDecoded.length();
      simpleRequest = true;
    } else {
      int dotInd = requestLineDecoded.indexOf('.', lastSpace + 1 + 5);
      if (dotInd == -1) {
        httpMajorVersion = Ascii.asciiArrToIntNoException(requestLineDecoded.getBytes(),
                lastSpace + 1 + 5,
                requestLineDecoded.length() - lastSpace - 1 - 5);
        httpMinorVersion = -1;
      } else {
        httpMajorVersion = Ascii.asciiArrToIntNoException(requestLineDecoded.getBytes(), lastSpace + 1 + 5, dotInd - lastSpace - 1 - 5);
        httpMinorVersion = Ascii.asciiArrToIntNoException(requestLineDecoded.getBytes(), dotInd + 1, requestLineDecoded.length() - dotInd - 1); ;
      }
    }

    method = requestLineDecoded.getBytes(0, firstSpace);
    urlDecoded = new MessageBytes(requestLineDecoded.getBytes(firstSpace + 1, lastSpace - firstSpace - 1));

    //will replace file separators
    int questionInd = urlDecoded.indexOf('?');
    int semiColonInd = urlDecoded.indexOf(';');
    int endInd = -1;
    if (questionInd == -1) {
      if (semiColonInd == -1) {
        endInd = urlDecoded.length();
      } else {
        endInd = semiColonInd;
      }
    } else {
      if (semiColonInd == -1) {
        endInd = questionInd;
      } else if (questionInd < semiColonInd) {
        endInd = questionInd;
      } else {
        endInd = semiColonInd;
      }
    }
    urlDecoded.replace((byte)'\\', ParseUtils.separatorByte, 0, endInd);

    byte[] schemeBytes = null;
    if (urlDecoded.startsWithIgnoreCase(scheme_http_)) {
      schemeBytes = scheme_http_;
      scheme = http;
    } else if (urlDecoded.startsWithIgnoreCase(scheme_https_)) {
      schemeBytes = scheme_https_;
      scheme = https;
    }
    int k;

    if (schemeBytes != null) {
      k = schemeBytes.length - 1;
    } else {
      if (!urlDecoded.startsWith(ParseUtils.separator)) {
        throw new ParseException(ParseException.INCORRECT_REQUEST_LINE, new Object[]{requestLine});
      }
      k = 0;
    }
    // According RFC-2396 path segment could contain zero or more chars, thus 
    // '//' are two different path segments, but in order to preserve backward 
    // compatibility we replace all occurrences of '//' with '/'
    while ((k = urlDecoded.indexOf("//", k)) > -1) {
      if (endInd > -1) {
        if (k >= endInd) {
          break;
        }
        questionInd--;
        semiColonInd--;
        endInd--;
      }
      urlDecoded.deleteByteAt(k);
    }

    if (schemeBytes != null) {
      k = urlDecoded.indexOf(ParseUtils.separatorChar, schemeBytes.length);
      if (k > -1 && k < endInd) {
        hostAndPort = urlDecoded.getBytes(schemeBytes.length, k - schemeBytes.length);
        urlDecoded = new MessageBytes(urlDecoded.getBytes(k, urlDecoded.length() - k));
      } else {
        k = endInd - 1;
        hostAndPort = urlDecoded.getBytes(schemeBytes.length, endInd - schemeBytes.length);
        urlDecoded = new MessageBytes(urlDecoded.getBytes(endInd));
        urlDecoded.appendBefore(ParseUtils.separatorBytes);
      }
      questionInd -= k;
      semiColonInd -= k;
      endInd -= k;
    }

    fullUrl = new MessageBytes(urlDecoded.getBytes());

    if (questionInd > -1) {
      query = new MessageBytes(urlDecoded.getBytes(questionInd + 1));
      urlDecoded.setLength(questionInd);
    }
    if (semiColonInd > -1 && (questionInd < 0  || semiColonInd < questionInd)) {
      urlDecoded.setLength(semiColonInd);
    }
  }

  private void parseHostAndPort() {
    hostAndPortParsed = true;
    if (hostAndPort == null) {
      return;
    }
    int colonInd = ByteArrayUtils.indexOf(hostAndPort, (byte)':');
    if (colonInd == -1) {
      host = hostAndPort;
      if (isSecure) {
        port = 443;
      } else {
        port = 80;
      }
    } else {
      host = new byte[colonInd];
      System.arraycopy(hostAndPort, 0, host, 0, colonInd);
      port = Ascii.asciiArrToIntNoException(hostAndPort, colonInd + 1, hostAndPort.length - colonInd - 1);
    }
  }

  private void parseUrlNotDecoded() throws ParseException {
    int firstSpace = requestLine.indexOf(' ');
    int lastSpace = requestLine.lastIndexOf(' ');
    if (firstSpace == lastSpace) {
      throw new ParseException(ParseException.INCORRECT_REQUEST_LINE, new Object[]{requestLine});
    }

    urlNotDecoded = new MessageBytes(requestLine.getBytes(firstSpace + 1, lastSpace - firstSpace - 1));
    int ind = urlNotDecoded.indexOf('?');
    if (ind > -1) {
      urlNotDecoded.setLength(ind);
    }
    ind = urlNotDecoded.indexOf(';');
    if (ind > -1) {
      urlNotDecoded.setLength(ind);
    }

    byte[] schemeBytes = null;
    if (urlNotDecoded.startsWithIgnoreCase(scheme_http_)) {
      schemeBytes = scheme_http_;
    } else if (urlNotDecoded.startsWithIgnoreCase(scheme_https_)) {
      schemeBytes = scheme_https_;
    } else {
      return;
    }
    int k = urlNotDecoded.indexOf(ParseUtils.separatorChar, schemeBytes.length);
    if (k != -1) {
      urlNotDecoded = new MessageBytes(urlNotDecoded.getBytes(k, urlNotDecoded.length() - k));
    } else {
      urlNotDecoded = new MessageBytes(ParseUtils.separatorBytes);
    }
  }
}
