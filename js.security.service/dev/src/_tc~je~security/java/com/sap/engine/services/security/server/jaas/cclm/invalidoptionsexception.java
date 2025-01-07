package com.sap.engine.services.security.server.jaas.cclm;

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

/** 
 * Thrown to indicate that a method has been passed an illegal or 
 * inappropriate argument, where the argument is representing a
 * field of the ClientCertLoginModule rule.
 * 
 * @see	    com.sap.engine.services.security.server.jaas.cclm.RuleData;
 * 
 * @since   SP16
 * @version 1.00 2005-12-21
 * @author Rumen Barov i033802
 */


public class InvalidOptionsException extends IllegalArgumentException {

  public InvalidOptionsException( String s ){
    super( s );
  }

}