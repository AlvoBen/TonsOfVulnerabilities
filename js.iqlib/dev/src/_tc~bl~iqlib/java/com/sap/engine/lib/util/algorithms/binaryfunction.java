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
 * BinaryFunction is the interface that must be implemented by binary function objects.
 * Every BinaryFunction object must define a single method called function() that takes
 * two objects as its arguments and returns an object.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public interface BinaryFunction {

  /**
   * Returns the result of execution with two Object arguments.<p>
   *
   * @param object1 the first object parameter.
   * @param object2 the second object parameter.
   * @return the result of processing the input parameters.
   */
  public Object function(Object object1, Object object2);

}

