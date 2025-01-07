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
package com.sap.engine.services.httpserver.lib.util;

import com.sap.engine.lib.util.HashMapIntObject;

public class EncodingUtils {
  private static HashMapIntObject languageToEncoding = new HashMapIntObject();

  static {
    languageToEncoding.put("ar".hashCode(), "ISO-8859-6");
    languageToEncoding.put("be".hashCode(), "ISO-8859-5");
    languageToEncoding.put("bg".hashCode(), "ISO-8859-5");
    languageToEncoding.put("ca".hashCode(), "ISO-8859-1");
    languageToEncoding.put("cs".hashCode(), "ISO-8859-2");
    languageToEncoding.put("da".hashCode(), "ISO-8859-1");
    languageToEncoding.put("de".hashCode(), "ISO-8859-1");
    languageToEncoding.put("el".hashCode(), "ISO-8859-7");
    languageToEncoding.put("en".hashCode(), "ISO-8859-1");
    languageToEncoding.put("es".hashCode(), "ISO-8859-1");
    languageToEncoding.put("et".hashCode(), "ISO-8859-1");
    languageToEncoding.put("fi".hashCode(), "ISO-8859-1");
    languageToEncoding.put("fr".hashCode(), "ISO-8859-1");
    languageToEncoding.put("hr".hashCode(), "ISO-8859-2");
    languageToEncoding.put("hu".hashCode(), "ISO-8859-2");
    languageToEncoding.put("is".hashCode(), "ISO-8859-1");
    languageToEncoding.put("it".hashCode(), "ISO-8859-1");
    languageToEncoding.put("iw".hashCode(), "ISO-8859-8");
    languageToEncoding.put("ja".hashCode(), "Shift_JIS");
    languageToEncoding.put("ko".hashCode(), "EUC-KR");
    languageToEncoding.put("lt".hashCode(), "ISO-8859-2");
    languageToEncoding.put("lv".hashCode(), "ISO-8859-2");
    languageToEncoding.put("mk".hashCode(), "ISO-8859-5");
    languageToEncoding.put("nl".hashCode(), "ISO-8859-1");
    languageToEncoding.put("no".hashCode(), "ISO-8859-1");
    languageToEncoding.put("pl".hashCode(), "ISO-8859-2");
    languageToEncoding.put("pt".hashCode(), "ISO-8859-1");
    languageToEncoding.put("ro".hashCode(), "ISO-8859-2");
    languageToEncoding.put("ru".hashCode(), "ISO-8859-5");
    languageToEncoding.put("sh".hashCode(), "ISO-8859-5");
    languageToEncoding.put("sk".hashCode(), "ISO-8859-2");
    languageToEncoding.put("sl".hashCode(), "ISO-8859-2");
    languageToEncoding.put("sq".hashCode(), "ISO-8859-2");
    languageToEncoding.put("sr".hashCode(), "ISO-8859-5");
    languageToEncoding.put("sv".hashCode(), "ISO-8859-1");
    languageToEncoding.put("tr".hashCode(), "ISO-8859-9");
    languageToEncoding.put("uk".hashCode(), "ISO-8859-5");
    languageToEncoding.put("zh".hashCode(), "GB2312");
    languageToEncoding.put("zh_TW".hashCode(), "Big5");
  }

  public static String getEncoding(String lang) {
    return (String)languageToEncoding.get(lang.hashCode());
  }
}
