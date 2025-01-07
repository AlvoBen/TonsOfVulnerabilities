/**************************************************************************** 
Apex Class Name  : ESAPI_C_HUM 
Version          : 1.0  
Created Date     : 06/08/2021
Function         : Helper class for security fixes
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                          			06/08/2021                 Original Version
***************************************************************************************************/
/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2010 - Salesforce.com
 * 
 * The Apex ESAPI implementation is published by Salesforce.com under the New BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Yoel Gluck (securecloud .at. salesforce.com) <a href="http://www.salesforce.com">Salesforce.com</a>
 * @created 2010
 */

/**
 * ESAPI locator class is provided to make it easy to gain access to the current ESAPI classes in use.<br>
 * For example you can use the validator() function to access the validator methods. (i.e. <CODE>ESAPI.validator().isValidCreditCard(creditcard, false)</CODE>)
 */
global with sharing class ESAPI_C_HUM {   

    private static SFDCEncoder_C_HUM SFDC_encoder = null;
    
    /**
     * prevent instantiation of this class
     */
    
    /**
     * @return the current SFDCEncoder object. This gives the basic encoding functionality as those availabel in VisualForce (HTMLENCODE, JSENCODE, JSINHTMLENCODE and URLENCODE) 
     */
    global static SFDCEncoder_C_HUM encoder() {
        if (SFDC_encoder == null) {
            SFDC_encoder =  new SFDCEncoder_C_HUM();
        } 
        return SFDC_encoder;
    }
}