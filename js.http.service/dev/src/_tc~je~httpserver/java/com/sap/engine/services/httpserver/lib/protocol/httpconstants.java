package com.sap.engine.services.httpserver.lib.protocol;

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

public interface HTTPConstants {
  /*
   * CTL = <any US-ASCII control character (octets 0 - 31) and DEL (127)>
   */
  public static final char[] CTLs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                     11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                                     21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                                     31, 127};
  public static final char[] non_token_characters = {'(',')', '<', '>', '@',
                      ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{',
                      '}', 32, 9,
                      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                      11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                      21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                      31, 127};

  public static final String non_token_characters_string = "()<>@,;:\\\"/[]?={} \t";
}
