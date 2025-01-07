package com.sap.security.core.server.ume.service;

import java.util.Properties;

import com.sap.engine.frame.container.runtime.RuntimeConfiguration;

/**
 * Listener for changes to UME runtime properties (runtime -> instance level),
 * to be registered in the Service Framework.
 * 
 * <p>
 * <b>Note:</b> This type of listener is only notified about current modifications
 * of UME properties when the "basicadmin" service is started because it contains
 * the ConfigurationChangedListener which is responsible for notifying all
 * {@link RuntimeConfiguration} instances. As that service starts some time after
 * the UME service, we will not be notified about any change that happens between
 * startup of the UME service and startup of the "basicadmin" service. That's why
 * {@link com.sap.security.core.server.ume.service.UMEPropertiesListener} is preferred,
 * as it's directly registered in the Configuration Management /
 * Configuration Library and cannot miss any change notification because of such
 * issues.
 * </p>
 * 
 * @author d034567
 */
class UMERuntimeConfiguration extends RuntimeConfiguration {

    private SAPJ2EEConfiguration _umeConfigHandler;

    UMERuntimeConfiguration(SAPJ2EEConfiguration umeConfigHandler) {
        _umeConfigHandler = umeConfigHandler;
    }

    @Override
    public void updateProperties(Properties changedProperties) {
        _umeConfigHandler.updateOnlineModifiableProperties(changedProperties);
    }
    
}
