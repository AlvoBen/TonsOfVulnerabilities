package com.sap.engine.services.httpserver.lib.util;


/*
 *
 * @author Bojidar Kadrev
 * @version 4.0
 */
public class MaskUtils {
  public static final byte QUERY_SEPARATOR = '?';
  public static final byte COOKIE_SEPARATOR = ';';
  public static final byte EQUALS_SEPARATOR = '=';
  public static final byte HEADER_SEPARATOR = ':';
  public static final byte AND_SEPARATOR = '&';
  public static final byte SPACE_SEPARATOR = ' ';
  public static final byte[] PROTOCOL_VERSION = " HTTP/".getBytes();
  public static final byte[] _JSESSIONID = "JSESSIONID".getBytes();
  public static final byte[] _MYSAPSSO2 = "MYSAPSSO2".getBytes();
  public static final byte[] JSESSIONID = "jsessionid".getBytes();
  public static final byte[] MYSAPSSO2 = "mysapsso2".getBytes();
  public static final byte[] J_PASSWORD = "j_password".getBytes();
  public static final byte[] J_USERBAME = "j_username".getBytes();
  public static final byte[] J_SAP_PASSWORD = "j_sap_password".getBytes();
  public static final byte[] J_SAP_CURRENT_PASSWORD ="j_sap_current_password".getBytes();
  public static final byte[] J_SAP_AGAIN = "j_sap_again".getBytes();
  public static final byte[] OLDPASSWORD = "oldPassword".getBytes();
  public static final byte[] CONFIRMNEWPASSWORD = "confirmNewPassword".getBytes();
  public static final byte[] AUTHORIZATION = "Authorization".getBytes();
  public static final byte[] COOKIE = "Cookie".getBytes();
  public static final byte[] TICKET = "ticket".getBytes();
  private static final byte[] MASK = ".....".getBytes();
  private static final int COOKIE_TYPE = 0;
  private static final int QUERY_TYPE = 1;
  private static final int HEADER_TYPE = 3;

  /*
   * This method is used for mask request line logged int http acces log.
   * All security related values in request line are replaced with ....
   */
  public static byte[] maskRequestLine(byte[] requestLine) {
    int quierySepBegin = ByteArrayUtils.indexOf(requestLine, QUERY_SEPARATOR);
    int cookieSepBegin = ByteArrayUtils.indexOf(requestLine, COOKIE_SEPARATOR);
    if (quierySepBegin == -1 && cookieSepBegin == -1) {
      return requestLine;
    }
    byte[] requestLineEnd = null;
    int spaceSepBegin = ByteArrayUtils.lastIndexOf(requestLine, requestLine.length, SPACE_SEPARATOR);
    if (spaceSepBegin > -1) {
      requestLineEnd = new byte[requestLine.length - spaceSepBegin];
      System.arraycopy(requestLine, spaceSepBegin, requestLineEnd, 0, requestLineEnd.length);
      if (ByteArrayUtils.startsWith(requestLineEnd, PROTOCOL_VERSION)) {
        byte[] requestLineNew = new byte[spaceSepBegin];
        System.arraycopy(requestLine, 0, requestLineNew, 0, requestLineNew.length);
        requestLine = requestLineNew;
      } else {
        requestLineEnd = null;
      }
    }
    byte[] secBytes = null;
    byte[] resBytes = null;
    if (quierySepBegin > -1 && cookieSepBegin > -1) {
      if (quierySepBegin > cookieSepBegin) {
        secBytes = new byte[requestLine.length - cookieSepBegin];
        System.arraycopy(requestLine, cookieSepBegin, secBytes, 0, secBytes.length);
        byte[] cookieMasked = new byte[ByteArrayUtils.indexOf(secBytes, QUERY_SEPARATOR)];
        System.arraycopy(secBytes, 0, cookieMasked, 0, cookieMasked.length);
        cookieMasked = maskNameValuePairs(cookieMasked, COOKIE_TYPE);
        byte[] queryMasked = new byte[secBytes.length - ByteArrayUtils.indexOf(secBytes, QUERY_SEPARATOR)];
        System.arraycopy(secBytes, ByteArrayUtils.indexOf(secBytes, QUERY_SEPARATOR), queryMasked, 0, queryMasked.length);
        queryMasked = maskNameValuePairs(queryMasked, QUERY_TYPE);
        resBytes = new byte[cookieSepBegin + cookieMasked.length + queryMasked.length];
        System.arraycopy(requestLine, 0, resBytes, 0, cookieSepBegin);
        System.arraycopy(cookieMasked, 0, resBytes, cookieSepBegin, cookieMasked.length);
        System.arraycopy(queryMasked, 0, resBytes, cookieSepBegin + cookieMasked.length, queryMasked.length);
      } else {
        secBytes = new byte[requestLine.length - quierySepBegin];
        System.arraycopy(requestLine, quierySepBegin, secBytes, 0, secBytes.length);
        byte[] queryMasked = new byte[ByteArrayUtils.indexOf(secBytes, COOKIE_SEPARATOR)];
        System.arraycopy(secBytes, 0, queryMasked, 0, queryMasked.length);
        queryMasked = maskNameValuePairs(queryMasked, QUERY_TYPE);
        byte[] cookieMasked = new byte[secBytes.length - ByteArrayUtils.indexOf(secBytes, COOKIE_SEPARATOR)];
        System.arraycopy(secBytes, ByteArrayUtils.indexOf(secBytes, COOKIE_SEPARATOR), cookieMasked, 0, cookieMasked.length);
        cookieMasked = maskNameValuePairs(cookieMasked, COOKIE_TYPE);
        resBytes = new byte[quierySepBegin + cookieMasked.length + queryMasked.length];
        System.arraycopy(requestLine, 0, resBytes, 0, quierySepBegin);
        System.arraycopy(queryMasked, 0, resBytes, quierySepBegin, queryMasked.length);
        System.arraycopy(cookieMasked, 0, resBytes, quierySepBegin + queryMasked.length, cookieMasked.length);
      }
    } else if (quierySepBegin > -1) {
      secBytes = new byte[requestLine.length - quierySepBegin];
      System.arraycopy(requestLine, quierySepBegin, secBytes, 0, secBytes.length);
      secBytes = maskNameValuePairs(secBytes, QUERY_TYPE);
      resBytes = new byte[quierySepBegin + secBytes.length];
      System.arraycopy(requestLine, 0, resBytes, 0, quierySepBegin);
      System.arraycopy(secBytes, 0, resBytes, quierySepBegin, secBytes.length);
    } else {
      secBytes = new byte[requestLine.length - cookieSepBegin];
      System.arraycopy(requestLine, cookieSepBegin, secBytes, 0, secBytes.length);
      secBytes = maskNameValuePairs(secBytes, COOKIE_TYPE);
      resBytes = new byte[cookieSepBegin + secBytes.length];
      System.arraycopy(requestLine, 0, resBytes, 0, cookieSepBegin);
      System.arraycopy(secBytes, 0, resBytes, cookieSepBegin, secBytes.length);
    }
    return requestLineEnd == null ? resBytes : ByteArrayUtils.append(resBytes, requestLineEnd);
  }

  /*
   * This method is used for mask header logged int http acces log.
   * All security related values are replaced with ....
   */
  public static byte[] maskHeader(byte[] headerName, byte[] headerValue) {
    if (ByteArrayUtils.equalsIgnoreCase(headerName, AUTHORIZATION) || ByteArrayUtils.equalsIgnoreCase(headerName, _MYSAPSSO2)) {
      return MASK;
    } else if (ByteArrayUtils.equalsIgnoreCase(headerName, COOKIE)) {
      return maskNameValuePairs(headerValue, HEADER_TYPE);
    }
    return headerValue;
  }

  // name=value
  private static byte[] maskNameValuePair(byte[] namevalue, int type) {
    int ind = ByteArrayUtils.indexOf(namevalue, EQUALS_SEPARATOR);
    if (ind == -1) {
      return namevalue;
    }
    byte[] name = new byte[ind];
    System.arraycopy(namevalue, 0, name, 0, name.length);
    name = ByteArrayUtils.trim(name);
    switch (type) {
      case COOKIE_TYPE : {
        if (!ByteArrayUtils.equalsBytes(name, JSESSIONID)) {
        //|| ByteArrayUtils.equalsBytes(name, MYSAPSSO2))) { it is not needed because it is never sent as query param
          return namevalue;
        }
        break;
      }
      case QUERY_TYPE : {
        if (!(ByteArrayUtils.equalsBytes(name, J_PASSWORD) || ByteArrayUtils.equalsBytes(name, J_USERBAME)
          || ByteArrayUtils.equalsBytes(name, J_SAP_PASSWORD) || ByteArrayUtils.equalsBytes(name, J_SAP_AGAIN)
          || ByteArrayUtils.equalsBytes(name, OLDPASSWORD) || ByteArrayUtils.equalsBytes(name, CONFIRMNEWPASSWORD)
          || ByteArrayUtils.equalsBytes(name, TICKET) || ByteArrayUtils.equalsBytes(name, J_SAP_CURRENT_PASSWORD)
          )) {
          return namevalue;
        }
        break;
      }
      case HEADER_TYPE : {
        if (!(ByteArrayUtils.equalsBytes(name, _JSESSIONID) || ByteArrayUtils.equalsBytes(name, _MYSAPSSO2))) {
          return namevalue;
        }
      }
    }
    byte[] result = new byte[ind + 1 + MASK.length];
    System.arraycopy(namevalue, 0, result, 0, ind + 1);
    System.arraycopy(MASK, 0, result, ind + 1, MASK.length);
    return result;
  }
  // ;name=value;name=value
  private static byte[] maskNameValuePairs(byte[] value, int type) {
    byte separator = COOKIE_SEPARATOR;
    switch (type) {
      case COOKIE_TYPE: {
        separator = COOKIE_SEPARATOR;
        break;
      }
      case QUERY_TYPE:{
        separator = AND_SEPARATOR;
        break;
      }
      case HEADER_TYPE: {
        separator = COOKIE_SEPARATOR;
        break;
      }
    }
    byte[] result = new byte[0];
    int ind = -1;
    int tempInd = 0;
    while ((ind = ByteArrayUtils.indexOf(value, separator, tempInd + 1)) > -1) {
      byte[] namevalue = null;
      if (tempInd == 0 && type == HEADER_TYPE) {
        namevalue = new byte[ind - tempInd];
        System.arraycopy(value, tempInd, namevalue, 0, namevalue.length);
      } else {
        namevalue = new byte[ind - tempInd - 1];
        System.arraycopy(value, tempInd + 1, namevalue, 0, namevalue.length);
        result = ByteArrayUtils.append(result, new byte[] {value[tempInd]});
      }
      result = ByteArrayUtils.append(result, maskNameValuePair(namevalue, type));
      tempInd = ind;
    }
    if (tempInd == 0 && (type == HEADER_TYPE)) {
      byte[] namevalue = new byte[value.length - tempInd];
      System.arraycopy(value, tempInd, namevalue, 0, namevalue.length);
      result = ByteArrayUtils.append(result, maskNameValuePair(namevalue, type));
    } else {
      byte[] namevalue = new byte[value.length - tempInd - 1];
      System.arraycopy(value, tempInd + 1, namevalue, 0, namevalue.length);
      result = ByteArrayUtils.append(result, new byte[] {value[tempInd]});
      result = ByteArrayUtils.append(result, maskNameValuePair(namevalue, type));
    }
    if (result.length == 0) {
      return value;
    }
    return result;
  }

}
