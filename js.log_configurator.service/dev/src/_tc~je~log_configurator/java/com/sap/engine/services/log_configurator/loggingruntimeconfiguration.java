package com.sap.engine.services.log_configurator;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import java.util.Properties;

public class LoggingRuntimeConfiguration extends RuntimeConfiguration {
    
    public void updateProperties(Properties properties) throws ServiceException {
        if (properties.getProperty("IncludeStackTraceForEachRecord") != null) {
            if (properties.getProperty("IncludeStackTraceForEachRecord").equalsIgnoreCase("false")) {
                com.sap.tc.logging.LoggingManager.getLoggingManager().setIncludeStackTraceForEachRecord(false);
            } else if (properties.getProperty("IncludeStackTraceForEachRecord").equalsIgnoreCase("true")) {
                com.sap.tc.logging.LoggingManager.getLoggingManager().setIncludeStackTraceForEachRecord(true);
            }
        }
        String specificLocations = properties.getProperty("SpecificLocations");
        com.sap.tc.logging.LoggingManager.getLoggingManager().setListOfLocationsIncludeStackTrace(specificLocations);
    }

}
