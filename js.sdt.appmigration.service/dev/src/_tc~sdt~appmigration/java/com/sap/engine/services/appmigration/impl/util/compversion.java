/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.impl.util;

import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.VersionException;
import com.sap.engine.services.appmigration.api.util.VersionIF;

/**
 * This class implements the VersionIF and
 * provides information about the version of the
 * components
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class CompVersion implements VersionIF
{
    static final long serialVersionUID = 951813860003822L;
    
    private String release;
    private String patchNumber;
    private String spLevel;
    private String vendor;
    private String name;

    // private String compLocation;
    private String counter;

    public String getName()
    {
        return name;
    }

    public String getRelease()
    {
        return release;
    }

    public String getPatchNumber()
    {
        return patchNumber;
    }

    public String getSPLevel()
    {
        return spLevel;
    }

    public String getVendor()
    {
        return vendor;
    }

    /*  public String getCompLocation()
      {
          return compLocation;
      }*/
    public String getCounter()
    {
        return counter;
    }

    /**
     * @param string
     */

    /* public void setCompLocation(String string)
     {
         compLocation = string;
     }*/

    /**
     * @param string
     */
    public void setCounter(String string)
    {
        counter = string;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @param string
     */
    public void setPatchNumber(String string)
    {
        patchNumber = string;
    }

    /**
     * @param string
     */
    public void setSpLevel(String string)
    {
        spLevel = string;
    }

    /**
     * @param string
     */
    public void setVendor(String string)
    {
        vendor = string;
    }

    /**
     * @param string
     */
    public void setRelease(String string)
    {
        release = string;
    }

    private String buildVersionNumber()
    {
        String result = "";

        if (release != null && release.trim().length() > 0)
        {
            result += release;
        }
        else
        {
            result += "0.00";
        }
        if (spLevel != null && spLevel.trim().length() > 0)
        {
            result = result + "." + spLevel;
        }
        else
        {
            result += ".0";
        }
        if (patchNumber != null && patchNumber.trim().length() > 0)
        {
            result = result + "." + patchNumber;
        }
        else
        {
            result += ".0";
        }

        return result;
    }

    /**
     * Implements this method of the version interface.
     * @param otherVersion The version to which this one is compared.
     * @return True if versions are equal.
     */
    public boolean equals(Object otherVersion)
    {
        if (!(otherVersion instanceof CompVersion))
        {
            return false;
        }

        CompVersion j2eeEngVers = (CompVersion) otherVersion;
        boolean areEqual = false;
        try
        {
            areEqual =
                (new DecimalVersion(buildVersionNumber())).isEqual(new DecimalVersion(
                        j2eeEngVers.buildVersionNumber()));
        }
        catch (VersionException ve)
        {
            //$JL-EXC$
            return false;
        }

        //System.out.println("areEqual = " + areEqual);
        return areEqual;
    }

    //(ST 700.09)
    public int hashCode()
    {
        return (name + vendor + release + spLevel + patchNumber).hashCode();
    }

    /**
     * Implements this method of the version interface.
     * @param otherVersion The version to which this one is compared.
     * @return 0 if versions are equal, -1 if other version is greater and 1 if
     * this version is greater.
     * @throws VersionNotComparableException Thrown when the other version is not
     * of CompVersion type or if the two version strings are not correctly
     * formatted.
     */
    public int compareTo(VersionIF otherVersion) throws VersionException
    {
        if (!(otherVersion instanceof CompVersion))
        {
            throw new VersionException(MigrationResourceAccessor.getResourceAccessor(),
                ExceptionConstants.VERSION_CAST_EXCEPTION,
                new Object[] { otherVersion });
        }

        CompVersion j2eeEngVers = (CompVersion) otherVersion;
        int compare = 0;

        String buildVersion = buildVersionNumber();
        String anotherBuildVersion = j2eeEngVers.buildVersionNumber();
        try
        {
            compare =
                (new DecimalVersion(buildVersion)).compare(new DecimalVersion(
                        anotherBuildVersion));
        }
        catch (VersionException vnce)
        {
            //$JL-EXC$
            throw new VersionException(MigrationResourceAccessor.getResourceAccessor(),
                ExceptionConstants.CANNOT_COMPARE_COMP_VERSIONS,
                new Object[] { buildVersion, anotherBuildVersion });
        }

        return compare;
    }

    public String toString()
    {
        return ("name " + name + ", vendor " + vendor + ", release " + release +
        ", SP " + spLevel + ", Patch " + patchNumber + ", counter " + counter);
    }
}
