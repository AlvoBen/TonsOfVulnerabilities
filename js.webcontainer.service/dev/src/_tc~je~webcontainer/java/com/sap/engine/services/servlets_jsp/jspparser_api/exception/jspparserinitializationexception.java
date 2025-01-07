/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.servlets_jsp.jspparser_api.exception;


/**
 * @author Todor Mollov, Bojidar Kadrev
 * DEV_tc_je_webcontainer
 * 2005-4-22
 * 
 */
public class JspParserInitializationException extends JspParseException{
  
  public final static String INVALID_INSTANCE_NAME="jsp_parser_0144"; 
  
  public JspParserInitializationException(String message, Object[] messageParameters){
    super(message,messageParameters);
  }
  
}
