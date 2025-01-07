/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduleradapter.jobdeploy;

/**
 * This class is used for diferent falidations
 *
 * @author Nikolai Neichev
 */
public class Validator {

  public static String VALID = "OK";

  /**
   * Validates a name
   * @param name the name to validate
   * @param alowedLength allowed name length in chars
   * @param minLength 
   * @return "OK" if the name is valid ot the corrensponding text if the name is not valid
   */
  public static String validateName(String name, int alowedLength, int minLength) {
      
    if (name.length() < minLength) {
        return "the length of the name '" + name + "'must be at least '" + minLength + "' characters but it is '" + name.length() + "' characters.";
    }
      
    // length check
    if (!validateLength(name, alowedLength)) {
      return "name is longer than the alowed " + alowedLength + " chars";
    }

    // first symbol check (can be letter or '_')
    if (!(Character.isLetter(name.charAt(0))) && (name.charAt(0) != '_')) {
      return "name must start with letter or '_'";
    }

    // check other symbols(alowed are : letters, numbers, '_' and '-')
    for (int i = 1; i < name.length(); i++) {
      if (!(Character.isLetterOrDigit(name.charAt(i))) && (name.charAt(i) != '_') && (name.charAt(i) != '-')) {
        return "illegal symbol at index: " + i + " -> '" + name.charAt(i) + "', alowed symbols are : letters, numbers, '_' and '-'";
      }

    }

    return VALID;
  }

  /**
   * Validate a name, minimum length is defaulted to zero
   * 
   * @param name
   * @param alowedLength
   * @return
   */
  public static String validateName(String name, int alowedLength) {
      return validateName(name, alowedLength, 0);
  }


  /**
   * Checks length of the specified value
   * @param val the specified value
   * @param alowedLength the alowed length
   * @return TRUE if the value length is alowed, FALSE if not
   */
  public static boolean validateLength(String val, int alowedLength) {
    return (val.length() <= alowedLength);
  }

}
