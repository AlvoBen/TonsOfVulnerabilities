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
package com.sap.engine.services.appmigration.api.util;

import com.sap.engine.services.appmigration.api.exception.ConfigException;


/**
 * This interface is used to get the version
 * of the components
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface ComponentVersionsIF
{
     /** Gets the component version
      * 
      * @param name the name of the component 
      * which version is needed
      * @return the component version or null if it is not available
      */
     public VersionIF getComponentVersion(String name) throws ConfigException;

     /**
      * Gets the component version
      * 
      * @param vendor the component vendor (e.g. sap.com) 
      * @param name the name of the component in compvers table 
      * @param componentType the component type (SC or DC)
      * @param subsystem the subsystem parameter (NO_SUBSYS at the moment)
      * @return the component version, if not available null is returned
      */
     public VersionIF getComponentVersion(String vendor, String name, String componentType, String subsystem)
         throws ConfigException;
         
    
}
