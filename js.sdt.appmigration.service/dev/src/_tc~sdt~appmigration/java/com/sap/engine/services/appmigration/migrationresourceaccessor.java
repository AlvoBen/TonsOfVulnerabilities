/* ==========================================================================*/
/* SAP AG Walldorf                                                           */
/* Systeme, Anwendungen, Produkte in der Datenverarbeitung                   */
/* (C) Copyright SAP AG 2000 - 2004                                          */
/* ==========================================================================*/
package com.sap.engine.services.appmigration;

import com.sap.localization.ResourceAccessor;


/**
 * MigrationResourceAccessor class is necessary to determine
 * the category and location of MigrationResourceBundle in order
 * Migration service to log its exceptions.
 *
 * @author Svetla Tsvetkova
 * @version 1.0
 */
public class MigrationResourceAccessor
    extends ResourceAccessor
{
    //~ Static fields/initializers ---------------------------------------------
    private static final long serialVersionUID = -1162836050222980320L;
    
    private static String BUNDLE_NAME =
        "com.sap.engine.services.appmigration.AppmigrationResourceBundle";
    private static MigrationResourceAccessor resourceAccessor = null;


    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs MigrationResourceAccessor for the MigrationResourceBundle.
     */
    private MigrationResourceAccessor ()
    {
        super(BUNDLE_NAME);
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * Returns this resource accessor. If it is null,
     * a new one is created and returned.
     *
     * @return this resource accessor.
     */
    public static synchronized MigrationResourceAccessor getResourceAccessor ()
    {
        if (resourceAccessor == null)
        {
            resourceAccessor = new MigrationResourceAccessor();
        }

        return resourceAccessor;
    }
}
