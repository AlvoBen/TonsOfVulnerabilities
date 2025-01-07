package com.sap.security.core.server.ume.service;

import java.util.Properties;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.tc.logging.Location;

/**
 * Listener for UME configuration files, to be registered either directly via the
 * Configuration Manager API or the wrapping Configuration Library.
 * 
 * @author d034567
 */
public class UMEPersistentFilesListener implements ConfigurationChangedListener {

    private static final Location _loc = Location.getLocation(UMEPropertiesListener.class);

    private SAPJ2EEConfiguration _umeConfigHandler;

    private UMEPersistentFilesListener() { /* Must not be used. */ }

    public UMEPersistentFilesListener(SAPJ2EEConfiguration umeConfigHandler) {
        _umeConfigHandler = umeConfigHandler;
    }

    /**
     * Change listener method called for changes to UME configuration files (on instance level).
     * 
     * @param changeEvent The change event created by the configuration adapter.
     * @see UMERuntimeConfiguration#updateProperties(Properties) 
     */
    public void configurationChanged(ChangeEvent changeEvent) {
        // Note: Only configuration changes made using the running server lead to
        //       notifications of this change listener. Changes applied using ConfigTool
        //       save their changes OFFLINE, i.e. bypassing the server, so these changes
        //       do NOT generate change events.

        final String method = "configurationChanged(ChangeEvent)";

        // Check arguments.
        if(changeEvent == null) throw new NullPointerException("The configuration changed event is null!");

    	if(_loc.beDebug()) {
        	_loc.debugT(method, "Received change event for configuration path \"{0}\".",
            		new Object[] { changeEvent.getPath() } );
    	}

    	// Normally, the path having been changed should be evaluated here. As this listener
    	// has only been registered for modifications of the UME persistent folder in the current
    	// instance, checking the path can be skipped here.
    	// If the path should be checked some time, note that
    	// - the path in the original changeEvent is the path for which the listener has been
    	//   registered and
    	// - the path(s) in changeEvent.getDetailedChangeEvents() list the exact configuration(s)
    	//   that has/have been modified.

    	// Update all configuration files.
    	_umeConfigHandler.updateAllConfigurationFiles();
    }

}
