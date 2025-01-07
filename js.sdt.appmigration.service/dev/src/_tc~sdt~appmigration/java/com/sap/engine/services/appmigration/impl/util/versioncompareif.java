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
package com.sap.engine.services.appmigration.impl.util;

import com.sap.engine.services.appmigration.api.exception.VersionException;

/**
 * VersionCompareIF
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public interface VersionCompareIF
{
    /**
     * A method to compare this Version with another one.
     *
     * @param  other  the Version to compare with. 
     * @return
     *  <pre> 
     *        0  the versions of the Objects are equivalent<br>
     *  &lt; 0, -1  the version of this Object is less than the other<br>
     *  &gt; 0,  1  the version of this Object is larger than the other
     * </pre>
     */
     
    public int compare (VersionCompareIF other)
        throws VersionException;
    
    
    /**
     * Compare this version with another one,
     * return true, if the versions are equal
     *
     * @param  other  the Version to compare with. 
     * @return true, if the versions are equal
     */
    
    public boolean isEqual (VersionCompareIF other)
        throws VersionException;
   


    /**
     * Get the type of the version.
     *
     * @return the type of the version
     */
     
    public int getType ();

    /**
     * Get the type of the version.
     *
     * @return the type of the version as a string
     */
     
    public String getTypeStr ();


    /**
     * Clone the version.
     *
     * @return the new version class
     */

    public Object clone ();
   
    public VersionCompareIF copy ();
    
    /**
     * Print the version (without the versions name).
     *
     * @return the new version class
     */

    public String toString ();
}
