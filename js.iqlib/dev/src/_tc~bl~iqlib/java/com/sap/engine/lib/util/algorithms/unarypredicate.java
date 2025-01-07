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
package com.sap.engine.lib.util.algorithms;

/**
 * UnaryPredicate is the interface that must be implemented by unary predicate objects.
 * Every UnaryPredicate object must define a single method called predicate() that takes
 * a single object as its argument and returns a boolean.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public interface UnaryPredicate {

  /**
   * Return the result of executing with a single object.<p>
   *
   * @param object the object to process.
   * @return the result of processing the input object.
   */
  public boolean predicate(Object object);

}

