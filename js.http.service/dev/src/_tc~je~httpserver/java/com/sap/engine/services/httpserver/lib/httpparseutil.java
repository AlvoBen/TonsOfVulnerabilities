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
package com.sap.engine.services.httpserver.lib;

/*
 *
 * @author Galin Galchev
 * @version 4.0
 */
import com.sap.engine.services.httpserver.lib.exceptions.HttpIllegalArgumentException;

import java.util.*;

public class HttpParseUtil {

  public static Hashtable stringToHashtable(String sProps) {
    Hashtable hashTable = new Hashtable();
    int iBegin = sProps.indexOf("{");
    int iEnd = sProps.lastIndexOf("}");

    if (iBegin == -1 && iEnd == -1) {
      return hashTable;
    }

    if (iBegin == -1 || iEnd == -1) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.MIME_MAPPINGS_SHOULD_START_WITH_AND_END_WITH, new Object[]{"{", "}"});
    }

    String sRestList = sProps.substring(iBegin + 1, iEnd);

    while (sRestList.indexOf("{") != -1) {
      int iElemBegin = sRestList.indexOf("{");
      int iElemEnd = sRestList.indexOf("}");

      if (iElemEnd != -1) {
        String sSubElem = sRestList.substring(iElemBegin + 1, iElemEnd);
        sRestList = sRestList.substring(iElemEnd + 1);
        int index = sSubElem.indexOf(",");
        hashTable.put(sSubElem.substring(0, index).trim(), sSubElem.substring(index + 1).trim());
      }
    }

    return hashTable;
  }

  public static Vector commpressedStringToExtensions(String str) {
    Vector vReturn = new Vector();
    StringTokenizer user = new StringTokenizer(str, ",");

    while (user.hasMoreElements()) {
      String nextEl = (String)user.nextElement();
      if (nextEl.startsWith("*.")) {
        vReturn.add(nextEl.substring(1));
      }
    }

    return vReturn;
  }

  public static Vector commpressedStringToMIMEType(String str) {
    Vector vReturn = new Vector();
    StringTokenizer user = new StringTokenizer(str, ",");

    while (user.hasMoreElements()) {
      String nextEl = (String)user.nextElement();
      if (!nextEl.startsWith("*.")) {
        vReturn.add(nextEl);
      }
    }

    return vReturn;
  }


  public static String[] stringToArray(String sProps) {
    Vector vect = new Vector();
    int iBegin = sProps.indexOf("{");
    int iEnd = sProps.lastIndexOf("}");

    if (iBegin == -1 && iEnd == -1) {
      return new String[0];
    }

    if (iBegin == -1 || iEnd == -1) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.INFERNAMES_SHOULD_START_WITH_AND_END_WITH, new Object[]{"{", "}"});
    }

    StringTokenizer tokenizer = new StringTokenizer(sProps.substring(iBegin + 1, iEnd), ",");

    while (tokenizer.hasMoreTokens()) {
      vect.addElement(tokenizer.nextToken());
    }

    String[] sArray = new String[vect.size()];
    vect.copyInto(sArray);
    return sArray;
  }

  public static String[] stringToList(String sProps) {
    Vector vect = new Vector();
    int iBegin = sProps.indexOf("{");
    int iEnd = sProps.lastIndexOf("}");

    if (iBegin != -1 && iEnd != -1) {
      StringTokenizer tokenizer = new StringTokenizer(sProps.substring(iBegin + 1, iEnd), "{");

      while (tokenizer.hasMoreTokens()) {
        String next = tokenizer.nextToken();
        next = next.trim();
        if (next.endsWith(",")) {
          next = next.substring(0, next.length() - 1);
          next = next.trim();
        }
        if (next.endsWith("}")) {
          vect.addElement(next.substring(0, next.length() - 1));
        }
      }
    }

    String[] sArray = new String[vect.size()];
    vect.copyInto(sArray);
    return sArray;
  }
}

