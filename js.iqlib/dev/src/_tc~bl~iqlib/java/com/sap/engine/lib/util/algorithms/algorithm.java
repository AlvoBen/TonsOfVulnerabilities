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
 * Parent class for all algorithms applied over iterators.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public abstract class Algorithm {

  protected UnaryPredicate predicate = null;
  protected UnaryFunction function = null;

  public void clear() {
    predicate = null;
    function = null;
  }

  /**
   * Sets the UnaryPredicate used with algorithm.<p>
   *
   * @param   predicate an unary predicate.
   */
  public void setUnaryPredicate(UnaryPredicate predicate) {
    this.predicate = predicate;
  }

  /**
   * Sets the UnaryFunction used with algorithm.<p>
   *
   * @param   function an unary function.
   */
  public void setUnaryFunction(UnaryFunction function) {
    this.function = function;
  }

}

