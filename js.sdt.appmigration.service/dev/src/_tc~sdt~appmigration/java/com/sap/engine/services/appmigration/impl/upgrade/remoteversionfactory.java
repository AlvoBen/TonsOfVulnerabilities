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
package com.sap.engine.services.appmigration.impl.upgrade;

import java.rmi.RemoteException;

import com.sap.engine.services.appmigration.api.upgrade.RemoteVersionFactoryIF;
import com.sap.engine.services.appmigration.api.util.VersionIF;
import com.sap.engine.services.appmigration.impl.util.MigrationConstantsIF;
import com.sap.engine.services.appmigration.impl.util.VersionFactory;

/**
 * This is the implementation of the RemoteVersionFactoryIF
 * It uses the same methods from the VersionFactoryIF, but
 * throws RemoteException, because this methods should be
 * called only remotely by the upgrade procedure
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class RemoteVersionFactory implements RemoteVersionFactoryIF
{
    static final long serialVersionUID = -8827691227062828792L;
    
    private VersionFactory factory = new VersionFactory();

    public VersionIF getVersion(String name, String vendor, String release,
        String spLevel, String patchLevel, String counter)
        throws RemoteException
    {
        VersionIF result = null;
        try
        {
            result = factory.getVersion(name, vendor, release, spLevel,
                    patchLevel, counter);
        }
        catch (Exception exc)
        {
            throw new RemoteException("The version cannot be set correctly ",
                exc);
        }
        return result;
    }

    public VersionIF getVersion(String name, String vendor, String versionString)
        throws RemoteException
    {
        VersionIF result = null;
        try
        {
            result = factory.getVersion(name, vendor, versionString);
        }
        catch (Exception exc)
        {
            throw new RemoteException("The version cannot be set correctly " +
                versionString, exc);
        }
        return result;
    }
    
 
    public VersionIF getSourceEngineVersion(String versionString)
        throws RemoteException
    {
        return getVersion(MigrationConstantsIF.ENGINE_VERSION_SOURCE, 
                          MigrationConstantsIF.VENDOR_SAP_COM,
                          versionString);    
    }
    
    public VersionIF getTargetEngineVersion(String versionString)
        throws RemoteException
    {
        return getVersion(MigrationConstantsIF.ENGINE_VERSION_TARGET, 
                          MigrationConstantsIF.VENDOR_SAP_COM,
                          versionString);    
    }
}
