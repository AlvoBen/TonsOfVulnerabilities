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
package com.sap.engine.lib.xml;

/**
 * Class for specifying errors when errors in xml structure occurred.
 *
 * @author Monika Kovachka
 * @version 4.0.0
 *
 */
public class WrongStructureException extends Exception {

  /**
   * Empty constructor of the class.
   */
  public WrongStructureException() {
    super();
  }

  /**
   * Constructs exception with the specified message.
   */
  public WrongStructureException(String s) {
    super(s);
  }

}

