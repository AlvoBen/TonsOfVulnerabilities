/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.compilation;

/**
 * Represents compiler exception, thrown only in compilation
 *
 * @author Nikolai Neichev
 */
public class CompilerException extends Exception {

  static final long serialVersionUID = -4660660822514335634L;

  /**
   * Constructor
   *
   * @param message The exception message
   */
  public CompilerException(String message) {
    super(message);
  }

}
