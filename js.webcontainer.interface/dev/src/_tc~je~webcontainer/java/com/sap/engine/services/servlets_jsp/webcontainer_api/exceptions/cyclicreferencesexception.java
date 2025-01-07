/*
* Copyright (c) 2006 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*/
package com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions;

import com.sap.localization.LocalizableTextFormatter;

/**
 * This exception is thrown if one or more cyclic references are detected.
 *  
 * @author Violeta Georgieva
 * @version 7.10
 */
public class CyclicReferencesException extends WebContainerExtensionDeploymentException {

  /**
   * Constructs an exception with a specified message, parameters and nested throwable.
   *
   * @param s    a message for this exception to be set.
   * @param args the parameters.
   * @param t    the nested throwable.
   */
  public CyclicReferencesException(String s, Object[] args, Throwable t) {
    super(s, args, t);
  }//end of constructor

  /**
   * Constructs an exception with a specified message and nested throwable.
   *
   * @param s a message for this exception to be set.
   * @param t the nested throwable.
   */
  public CyclicReferencesException(String s, Throwable t) {
    super(s, t);
  }//end of constructor

  /**
   * Constructs an exception with a specified message and parameters.
   *
   * @param s    a message for this exception to be set.
   * @param args the parameters.
   */
  public CyclicReferencesException(String s, Object[] args) {
    super(s, args);
  }//end of constructor

  /**
   * Constructs an exception with a specified message.
   *
   * @param s a message for this exception to be set.
   */
  public CyclicReferencesException(String s) {
    super(s);
  }//end of constructor

  /**
   * Constructs an exception with a specified localizable text formatter and nested throwable.
   *
   * @param localizableTextFormatter the localizable text formatter.
   * @param t                        the nested throwable.
   */
  public CyclicReferencesException(LocalizableTextFormatter localizableTextFormatter, Throwable t) {
    super(localizableTextFormatter, t);
  }//end of constructor

  /**
   * Constructs an exception with a specified localizable text formatter.
   *
   * @param localizableTextFormatter the localizable text formatter.
   */
  public CyclicReferencesException(LocalizableTextFormatter localizableTextFormatter) {
    super(localizableTextFormatter);
  }//end of constructor

}//end of class
