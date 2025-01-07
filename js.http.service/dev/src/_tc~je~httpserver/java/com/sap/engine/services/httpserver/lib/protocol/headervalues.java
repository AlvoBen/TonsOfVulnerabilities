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
package com.sap.engine.services.httpserver.lib.protocol;

import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.lib.util.ConcurrentHashMapIntObject;

public class HeaderValues {
  public static final String chunked = "chunked";
  public static final String close = "close";
  public static final String gzip = "gzip";
  public static final String no_cache = "no-cache";
  public static final String keep_alive = "Keep-Alive";
  public static final String _100_continue = "100-continue";
  public static final String text_html = "text/html";
  public static final String text_plain = "text/plain";
  public static final String content_unknown = "content/unknown";
  public static final String x_powered_by = "Servlet/2.5 JSP/2.1";
  public static final String tyt = "Servlet/2.5 JSP/2.1";
  public static final String ETAG_INQMY = "J2EE/";

  //byte array

  public static final byte[] chunked_ = chunked.getBytes();
  public static final byte[] close_ = close.getBytes();
  public static final byte[] gzip_ = gzip.getBytes();
  public static final byte[] no_cache_ = no_cache.getBytes();
  public static final byte[] keep_alive_ = keep_alive.getBytes();
  public static final byte[] _100_continue_ = _100_continue.getBytes();
  public static final byte[] text_html_ = text_html.getBytes();
  public static final byte[] text_palin_ = text_plain.getBytes();
  public static final byte[] content_unknown_ = content_unknown.getBytes();
  public static final byte[] x_powered_by_ = x_powered_by.getBytes();

  //ICM cache
  private static final byte[] ETAG_INQMY_ = ETAG_INQMY.getBytes();
  private static final int SAP_ETAGS_LENGTH = 32;

  private static ConcurrentHashMapIntObject aliasNameHashToETagName = 
    new ConcurrentHashMapIntObject(); //int - byte[]

  public static byte[] getSapIscEtag(String aliasName) {
    if (aliasName == null) {
      return ETAG_INQMY_;
    } else {
      byte[] etag = null;

      int hash = aliasName.hashCode();
      byte[] aliasb = getTagAlias(hash);

      if (aliasb == null) {
        byte[] etagValue = null;
        if (aliasName.length() <= SAP_ETAGS_LENGTH - ETAG_INQMY_.length) {
          etagValue = aliasName.getBytes();
        } else {
          etagValue = String.valueOf(hash).getBytes();
        }
        addTagAlias(hash, etagValue);
        aliasb = getTagAlias(hash);
      }

      etag = new byte[ETAG_INQMY_.length + aliasb.length];
      System.arraycopy(ETAG_INQMY_, 0, etag, 0, ETAG_INQMY_.length);
      System.arraycopy(aliasb, 0, etag, ETAG_INQMY_.length, aliasb.length);

      return etag;
    }
  }

  private static void addTagAlias(int hashCode, byte[] tagAlias) {
    aliasNameHashToETagName.put(hashCode, tagAlias);
  }

  private static void removeTagAlias(int hashCode) {
    aliasNameHashToETagName.remove(hashCode);
  }

  private static byte[] getTagAlias(int hashCode) {
    return (byte[]) aliasNameHashToETagName.get(hashCode);
  }

  public static byte[][] getAllETags() {
    Object[] temp = aliasNameHashToETagName.getAllValues();
    byte[][] res = new byte[temp.length][];
    System.arraycopy(temp, 0, res, 0, temp.length);
    return res;
  }

  public void removeETagsForAliases(String aliases[]) {
    for (int i = 0; i < aliases.length; i++) {
      if (!"".equals(aliases[i])) {
        removeTagAlias(new MessageBytes(aliases[i].getBytes()).hashCode());
      }
    }
  }
}
