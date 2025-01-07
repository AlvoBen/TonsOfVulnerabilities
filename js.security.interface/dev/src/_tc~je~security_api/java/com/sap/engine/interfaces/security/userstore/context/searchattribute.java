/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.context;

/**
 * SearchAttribute represents exactly one entry in the search criteria, which
 * can be combined using class SearchFilter. 
 * 
 * @author  d031387
 * @version 6.40
 *
 */
public interface SearchAttribute extends java.io.Serializable {

	public static final int EQUALS_OPERATOR         = 0; 
	public static final int LIKE_OPERATOR           = 1; 
//	public static final int GREATER_THAN_OPERATOR   = 2; 
//	public static final int LESS_THAN_OPERATOR      = 3; 

//	public static final String WILDCARD_ONE         = "?";
	public static final String WILDCARD_MANY        = "*";

   /**
	*  returns the attribute name of this search element
	*
	* @return  The attribute name
	*/
	public String getAttributeName();

   /**
	*  returns the value search element
	*
	* @return  The value
	*/
	public Object getAttributeValue();

   /**
	*  returns the operator of this search element
	*
	* @return  The operator
	*/
	public int getOperator();
	
}
