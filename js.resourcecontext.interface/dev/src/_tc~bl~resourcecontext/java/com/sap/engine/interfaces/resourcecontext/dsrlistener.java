/**
 *
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria AG.
 *
 * Created on 2004-12-10 
 * Created by ralitsa-v (e-mail: ralitsa.vassileva@sap.com)
 */
package com.sap.engine.interfaces.resourcecontext;

/**
 * @author Ralitsa Vassileva
 * @version 7.0
 */
public interface DSRListener {

   /**

    * Sets the current component-, application- and method names

    * @param componentName component name for the EJB

    * @param applicationName name of the application which the called EJB belongs to

    * @param methodName business method name. This can only be the name of one of the EJB Home Interface methods

    */

   public void setApplicationStatisticsContext(String componentName, String applicationName, String methodName);

}
