/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.api.upgrade;

import java.io.Serializable;

/**
 * Description
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface MigrationModuleInfoIF extends Serializable
{
    /**
     * Get the name of the component
     * @return the component name
     */
    public String getComponentName();
    
    /**
     * Get the component vendor
     * @return the component vendor
     */
    public String getComponentVendor();
    
    /**
     * Get the text of the exception. This text is set in
     * the config manager by the migration service if case
     * the migration of this migration module fails
     * 
     * @return the exception text if any, null if the
     * module is not already passed or it passed successfully
     */
    public String getExceptionText();
    
    /**
     * Get the CSN component of the migration module
     * 
     * @return the CSN component for the migration module
     */
    public String getCSNComponent();
    
    /**
     * Get the execution status of the migration module
     * 
     * @return the status of the migration module
     */
     public String getStatus();
	
    /**
     * Return string representation of this object in
     * format vendor/componentname
     * @return string respresentation of this object
     */
    public String toString();

}
