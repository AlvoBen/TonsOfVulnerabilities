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
package com.sap.engine.services.httpserver.lib.headers;

/*
 *
 * @author Galin Galchev
 * @version 4.0
 */
import java.util.Enumeration;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.exceptions.HttpNoSuchElementException;

public class MimeHeadersEnumerator implements Enumeration {

  private MimeHeaders headers;
  private int count;

  public MimeHeadersEnumerator(MimeHeaders mimeheaders) {
    headers = mimeheaders;
  }

  /**
   * Check if has more elements
   *
   * @return     true if has more elements
   */
  public boolean hasMoreElements() {
    return count < headers.size();
  }

  /**
   * Returns next element
   *
   * @return     next element
   */
  public Object nextElement() {
    String s = headers.getHeaderName(count++);

    if (s == null) {
      throw new HttpNoSuchElementException(HttpNoSuchElementException.ENUMERATION_IS_OVER);
    } else {
      return s;
    }
  }

}

