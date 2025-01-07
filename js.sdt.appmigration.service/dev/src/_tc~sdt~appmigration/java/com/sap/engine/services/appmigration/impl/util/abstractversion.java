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
 * Description
 * 
 * @author Svetla Tsvetkova
 * @version 1.0
 */

public abstract class AbstractVersion implements VersionCompareIF
{

    /**
     * A method to compare this Version with another one.
     *
     * @param  other  the Version to compare with. 
     * @return <pre>
     *      0  the versions of the Objects are equivalent<br>
     *     -1  the version of this Object is less than the other<br>
     *      1  the version of this Object is larger than the other
     * </pre>
     */
     
    abstract public int compare(VersionCompareIF other)
        throws VersionException;
    
    
    /**
     * Compare this version with another one,
     * return true, if the versions are equal
     *
     * @param  other  the Version to compare with. 
     * @return true, if the versions are equal
     */
    
    public boolean isEqual(VersionCompareIF other)
        throws VersionException
    {
        return (compare (other) == 0);
    }
    


    /**
     * Get the type of the version.
     *
     * @return the type of the version
     */

    abstract public int getType ();
    
    
    abstract public String getTypeStr ();

    /**
     * Check whether the supplied string represents a valid version.
     *
     * @param     vers  the string to be tested
     * @return    true, if the string represents a valid version
     */

    // abstract public boolean isValid (String vers);
    
    
    
    /**
     * Return a new instance of Version with identical content.
     * @return a new intance of Version with identical content.
     */
    public Object clone ()
    {
        return copy ();
    }

    /**
     * Create a copy of this version.
     * @param a copy of this version
     */
    abstract public VersionCompareIF copy ();

    /**
     * Print this version to a string.
     * @return the version as a sting
     */
    abstract public String toString ();
    
    /**
     * Get the name of the object with this version, or 'null'
     * if the version does not directly correspond to an object.
     * @return the name of the version
     */

    public String getName ()
    {
        return name;
    }

    /**
     * Set the name of the object with this version.
     * @param newName the name of the version
     */
    void setName (String newName)
    {
        name = newName;
    }
    
    private String name = null;

}
