/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.services.security.userstore.emergency.search;

import com.sap.engine.interfaces.security.userstore.context.SearchResult;
import com.sap.engine.services.security.userstore.emergency.EmergencyUserContextImpl;

import java.util.Iterator;

public class SearchResultImpl implements SearchResult {
  private Iterator iterator = null;
  private int state = SEARCH_RESULT_OK;

  public SearchResultImpl(EmergencyUserContextImpl context) throws SecurityException {
    this.iterator = context.engineListUsers();
  }

  /**
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    return iterator.hasNext();
  }

  /**
   * @see java.util.Iterator#next()
   */
  public Object next() {
    return iterator.next();
  }

  /**
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    //not implemented
  }

  public int getState () {
    return state;
  }
}
