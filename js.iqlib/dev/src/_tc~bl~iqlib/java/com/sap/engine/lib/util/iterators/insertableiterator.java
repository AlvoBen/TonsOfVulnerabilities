/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util.iterators;

/**
 * InsertableIterator is the interface of all iterators which can
 * insert objects on their current position.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface InsertableIterator
  extends RootIterator {
  static final long serialVersionUID = -7974771218398426103L;
    
  /**
   * Inserts the specified object to the current iterator position and returns it.<p>
   *
   * @param object the object which has to be inserted.
   * @return the inserted object.
   */
  public Object insert(Object object);

}

