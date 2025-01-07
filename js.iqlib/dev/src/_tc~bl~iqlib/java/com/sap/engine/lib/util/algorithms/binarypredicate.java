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
 * BinaryPredicate is the interface that must be implemented by all binary predicate objects.
 * Every BinaryPredicate object must define a single method called predicate() that takes
 * two objects as its arguments and returns a boolean. <p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public interface BinaryPredicate {

  /**
   * Returns the result of execution with two Object arguments.<p>
   *
   * @param object1 the first object parameter.
   * @param object2 the second object parameter.
   * @return the result of processing the input parameters.
   */
  public boolean predicate(Object object1, Object object2);

}

