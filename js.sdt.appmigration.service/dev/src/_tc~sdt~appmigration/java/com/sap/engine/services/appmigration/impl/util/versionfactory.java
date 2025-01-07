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

import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.api.exception.VersionException;
import com.sap.engine.services.appmigration.api.util.VersionFactoryIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;

/**
 * This is the implementation of the VersionFactoryIF
 * This class is used to create VersionIF objects
 * from string representation of version
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class VersionFactory implements VersionFactoryIF
{
    private static final long serialVersionUID = -5263959499640068410L;
    
    public VersionIF getVersion(String name, String vendor, String release,
        String spLevel, String patchNumber, String counter)
        throws VersionException
    {
        CompVersion version = new CompVersion();
        version.setName(name);
        version.setVendor(vendor);

        if ((name == null) || (vendor == null) || (release == null) ||
            (release.indexOf('.') == -1))
        {
            throw new VersionException(MigrationResourceAccessor.getResourceAccessor(),
                ExceptionConstants.CANNOT_CREATE_VERSION);
        }

        version.setRelease(release);
        version.setSpLevel(spLevel);
        version.setPatchNumber(patchNumber);

        if ((counter == null) || counter.equals("0"))
        {
            counter = "1000." + release + "." + spLevel + "." + patchNumber;
        }

        version.setCounter(counter);

        return version;
    }

    public VersionIF getVersion(String name, String vendor, String versionString)
        throws VersionException
    {
        CompVersion version = new CompVersion();
        version.setName(name);
        version.setVendor(vendor);

        if (versionString.startsWith("1000."))
        {
            versionString = versionString.substring(5);
        }

        boolean isCorrect = versionString.matches(
                "[0-9]*\\.([0-9]*|[0-9]*\\.[0-9]*\\.([0-9]*|[0-9]*.[0-9]*))");
        if (isCorrect)
        {
            // parse 
            int index = versionString.indexOf('.');
            int index2 = versionString.indexOf('.', index + 1);

            if (index2 == -1)
            {
                version.setRelease(versionString);
            }
            else
            {
                String release = versionString.substring(0, index2);
                versionString = versionString.substring(index2 + 1);

                index = versionString.indexOf('.');
                String sp = versionString.substring(0, index);
                versionString = versionString.substring(index + 1);

                index = versionString.indexOf('.');
                String patch = null;
                String timestamp = null;
                if (index != -1)
                {
                    patch = versionString.substring(0, index);
                    versionString = versionString.substring(index + 1);
                    timestamp = versionString;
                }
                else
                {
                    patch = versionString;
                }

                version.setRelease(release);
                version.setSpLevel(sp);
                version.setPatchNumber(patch);

                if ((timestamp == null) || timestamp.equals("0"))
                {
                    timestamp = "1000." + release + "." + sp + "." + patch;
                }

                version.setCounter(timestamp);
            }
        }
        else
        {
            throw new VersionException(MigrationResourceAccessor.getResourceAccessor(),
                ExceptionConstants.CANNOT_CREATE_VERSION);
        }
        return version;
    }
}
