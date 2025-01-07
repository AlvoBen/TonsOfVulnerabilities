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
package com.sap.engine.services.appmigration.impl.upgrade;

import com.sap.engine.services.appmigration.api.upgrade.MigrationModuleInfoIF;

/**
 * Contains information about the migration modules
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public class MigrationModuleInfo implements MigrationModuleInfoIF
{
	static final long serialVersionUID = 8325926226332096568L;
	
	
    private String vendor;
    private String componentName;
    private String csnComponent;
    private String exceptionTxt;
    private String status;
    /**
     * Creates object that is passed to the upgrade procedure
     * 
     * @param vendor
     * @param componentName
     * @param csnComponent
     */
    public MigrationModuleInfo(String vendor, String componentName, String csnComponent)
    {
        this.vendor = vendor;
        this.componentName = componentName;
        this.csnComponent = csnComponent;
    }

    public String getComponentName()
    {
        return componentName;
    }

    public String getComponentVendor()
    {
        return vendor;
    }
    
    public String getCSNComponent()
    {
    	return csnComponent;
    }
    
    public String getExceptionText()
    {
    	return exceptionTxt;
    }
    
    public void setExceptionText(String exceptionTxt)
    {
    	this.exceptionTxt = exceptionTxt;
    }
    
    public String toString()
    {
        if (vendor == null)
        {
            return componentName;
        }
        return vendor + "/" + componentName;
    }

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

    
}
