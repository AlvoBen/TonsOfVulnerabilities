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
 * AddableIterator is the interface of all iterators which can add objects
 * to the underlying data structure.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface AddableIterator
  extends RootIterator {

  static final long serialVersionUID = 7621794395389129601L;    

  /**
   * Adds the specified object after the end of this iterator
   * and returns it.<p>
   *
   * @param obj the object to be added.
   * @return the added object.
   */
  public Object add(Object obj);

}

