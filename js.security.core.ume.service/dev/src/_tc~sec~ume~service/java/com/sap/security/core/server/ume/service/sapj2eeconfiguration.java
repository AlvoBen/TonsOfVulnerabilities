package com.sap.security.core.server.ume.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.runtime.RuntimeConfiguration;
import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.state.PersistentContainer;
import com.sap.engine.lib.config.api.ClusterConfiguration;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.ComponentProperties;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.security.api.IMessage;
import com.sap.security.core.server.ume.service.configdiagtool.ConfigOverview;
import com.sap.security.core.util.config.ConfigChangeRejectedException;
import com.sap.security.core.util.config.IProperty;
import com.sap.security.core.util.config.InternalSaveResult;
import com.sap.security.core.util.config.UMConfigurationBase;
import com.sap.security.core.util.config.UMConfigurationException;
import com.sap.security.core.util.imp.Util;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author d034567
 * 
 * Implementation of UME configuration handling interface
 * {@link com.sap.security.core.util.config.IUMConfigFull}
 * which uses SAP AS Java's configuration manager as backend.
 * 
 * <p>
 * For runtime read access to UME configuration data, this implementation uses the
 * configuration branch for the current cluster instance. For administrative read
 * and write access, it refers to the "custom_global" configuration branch to make
 * sure that all changes performed using this implementation are applied to the
 * whole cluster consistently.
 * </p>
 *
 * <p>
 * This implementation registers a listener for modifications of UME properties
 * in the Configuration Management. There are two issues to remember:
 * <ul>
 * <li>Only properties flagged "online modifiable" can be modified at runtime.
 *     Modifications of all other properties are not visible to users of this
 *     implementation until the next server restart. (The "online modifiable"
 *     flag is evaluated at runtime, i.e. setting the flag and a new property
 *     value at runtime in one step will result in the property value being
 *     updated immediately.)</li>
 * <li>Only configuration changes made through the running server lead to
 *     notifications of this change listener. Changes applied using ConfigTool
 *     save their changes OFFLINE, i.e. bypassing the server, so these changes
 *     do NOT generate change events.</li> 
 * </ul>
 * </p>
 *
 * <p>
 * Additionally, it registers a listener for modifications of UME configuration
 * files in the "persistent" folder of the UME service configuration. As there is
 * not "online modifiable" flag for configuration files, all file modifications
 * (including adding and removing files) are applied "online" and are immediately
 * reflected in the data that is available from this configuration handler.
 * However, most configuration files are evaluated only once during server startup,
 * so changes of most files still require a restart to get effective.
 * </p>
 * 
 * <p>
 * This implementation enforces cluster wide identical property values for a set
 * of properties which have been explicitely declared as "may be defined per
 * cluster instance" in
 * {@link com.sap.security.core.util.config.InstanceSpecificProperties}.
 * This is realized by setting the "final" flag on the "custom_global" level for
 * all properties that have to be identical in the whole cluster.
 * </p>
 * 
 * <p>
 * Unfortunately, there is no "final" flag for configuration <i>files</i>, so
 * in theory it would be possible to change these files on template or instance
 * level. However, as there is no UI to do that except the advanced view of all
 * config manager structures in Visual Administrator's configuration adapter and
 * the advanced view in the config tool, we consider it rather unlikely that
 * someone will apply his modification to a UME configuration file on the template
 * or instance level by default. The usual scenario would rather be to use the
 * UME configuration application, which uses
 * {@link com.sap.security.core.util.config.IUMConfigAdmin},
 * which in turn reads from and writes to the "custom_global" level only, as
 * implemented here.
 * </p>
 * 
 * @author d034567
 */
public class SAPJ2EEConfiguration extends UMConfigurationBase {

    private static final Location _loc = Location.getLocation(SAPJ2EEConfiguration.class);
    private static final Category _cat = UMEServiceFrame.myCat;

    private static final boolean CFG_LEVEL_GLOBAL   = true;
    private static final boolean CFG_LEVEL_INSTANCE = false;

    private ServiceContext               _serviceContext;
    private String                       _serviceName;
    private ConfigurationHandlerFactory  _configHandlerFactory;

    private String                       _propertiesPathGlobal;
    private String                       _persistentContainerPathInstance;

    private ConfigurationChangedListener _propertiesListener;
    private ConfigurationChangedListener _persistentFilesListener;
    private RuntimeConfiguration         _runtimeConfigListener;

    private Set<String>                  _securePropertyNames;

    /* *********************************************************************************************
       **                                                                                         **
       **   Lifecycle functionality                                                               **
       **                                                                                         **
       *********************************************************************************************/

    public SAPJ2EEConfiguration(ServiceContext serviceContext) throws UMConfigurationException {
        final String method = "SAPJ2EEConfiguration(ServiceContext)";

        _loc.infoT(method, "Initializing UME configuration handler for SAP AS Java...");

        // Remember some central values.
        _serviceContext       = serviceContext;
        _serviceName          = _serviceContext.getServiceState().getServiceName();
        _configHandlerFactory = _serviceContext.getCoreContext().getConfigurationHandlerFactory();

        // Prepare some paths in the configuration manager.
        // TODO Replace these hard-coded paths by the respective calls to the config library
        //      as soon as they are available.
        _propertiesPathGlobal            = "cluster_config/system/custom_global/cfg/services/"              + _serviceName + "/properties";
        _persistentContainerPathInstance = "cluster_config/system/instances/current_instance/cfg/services/" + _serviceName + "/persistent";

        // Create and register listeners for configuration changes.
        //
        // Note: This assumes that there will be no configuration changes between reading
        // the initial = static configuration properties and registering the listener.
        //
        // Overview about all UME listeners:
        // 
        // 1) Listeners for properties changes.
        //
        //    a) Special listener for changes of "onlinemodifiable" properties.
        //       Note: Currently, this listener receives *asynchronous* notifications only, which
        //             does not fit the UME requirement for getting *synchronous* notifications.
        //             That's why we currently use option b) and register a dummy
        //             RuntimeConfiguration instance only. The only purpose of that instance is
        //             to avoid log and trace messages about having "onlinemodifiable" properties,
        //             but no RuntimeConfiguration instance. We consider this a valid behaviour
        //             because we use option b) to listen for changes of "onlinemodifiable"
        //             properties, so we don't miss anything in the end.
        // TODO Replace this dummy listener by UMERuntimeConfiguration as soon as RuntimeConfiguration
        //      instances can be notified *synchronously*. Until then, we continue using option b).
        _runtimeConfigListener = new UMEDummyRuntimeConfiguration();
        // This (dummy) listener is registered even *before* initially reading UME configuration data
        // because the AS Java service framework writes error log messages during updatePropertyMetadata()
        // if there is no RuntimeConfiguration instance registered for the UME service properties at
        // that time.
        // Registering it *before* init() does not matter because it has no productive use anyway.
        // This is just for expressing to the service framework that we care about our
        // "onlinemodifiable" properties.
        _serviceContext.getServiceState().registerRuntimeConfiguration(_runtimeConfigListener);

        // b) Based on config library.
        //    Note: We use the hard-coded pseudo instance ID "current_instance" to access the
        //          local instance's configuration while RuntimeConfiguration (a) doesn't need
        //          any such hard-coded paths or path segments.
        //    The listener requests *synchronous* notification about changes which makes sure the
        //    internal caches are updated when the setXXX() methods return, i.e. changes are
        //    applied *immediately*.
    	_propertiesListener = new UMEPropertiesListener(this);
    	// This (productive) listener will be registered after init().

    	// 2) Listener for modifications to contents of the "persistent" folder containing
        //    additional configuration files.
        _persistentFilesListener = new UMEPersistentFilesListener(this);

        // Update property metadata (default values, flags, ...) after installation and upgrade.
        updatePropertyMetadata();

        // Perform some internal initialization tasks.
        init();

        // Initialize super class UMConfigurationBase.
        super.initialize();

    	// Now register the productive configuration change listeners.
    	// 
    	// 1) Listener for properties changes.
        try {
        	getCurrentInstanceConfigLevel().getComponentAccess()
        		.addConfigurationChangedListener(_propertiesListener, _serviceName, ConfigurationChangedListener.MODE_SYNCHRONOUS);
        }
        catch(ClusterConfigurationException e) {
        	_loc.traceThrowableT(Severity.ERROR, method, "Could not register UME properties change listener.", e);
        	_cat.warningT(_loc, "Could not register change listener for UME properties. Online modifications " +
        		"of UME properties cannot be applied until the next server restart.");
        }
        // 2) Listener for configuration file changes.
        try {
            // TODO Use Config Library method for registration as soon as available.
            _configHandlerFactory.getConfigurationHandler().addConfigurationChangedListener(
            	_persistentFilesListener, _persistentContainerPathInstance, ConfigurationChangedListener.MODE_SYNCHRONOUS);
        }
        catch(ConfigurationException e) {
        	_loc.traceThrowableT(Severity.ERROR, method, "Could not register UME configuration files change listener.", e);
        	_cat.warningT(_loc, "Could not register change listener for UME configuration files. Online modifications " +
        		"of UME configuration files cannot be applied until the next server restart.");
        }

        _loc.infoT(method, "Initialization of UME configuration handler for SAP AS Java completed.");
    }

    /**
     * Clean up all used resources.
     */
    public void stop() {
        final String method = "stop()";

        _loc.infoT(method, "Cleaning up state of UME configuration handler for SAP AS Java for " +
            "UME service shutdown...");

    	// Unregister properties change listener.
        try {
			getCurrentInstanceConfigLevel().getComponentAccess().removeConfigurationChangedListener(_propertiesListener, _serviceName);
		}
        catch(ClusterConfigurationException e) {
        	_loc.traceThrowableT(Severity.DEBUG, method, "This exception can be ignored.", e);
		}

        // Unregister RuntimeConfiguration listener.
        _serviceContext.getServiceState().unregisterRuntimeConfiguration();

        // Unregister configuration files change listener.
        try {
			_configHandlerFactory.getConfigurationHandler()
				.removeConfigurationChangedListener(_persistentFilesListener, _persistentContainerPathInstance);
		}
        catch(ConfigurationException e) {
        	_loc.traceThrowableT(Severity.DEBUG, method, "This exception can be ignored.", e);
		}

        _loc.infoT(method, "UME configuration handler for SAP AS Java is ready for " +
            "UME service shutdown.");
    }

    /* *********************************************************************************************
       **                                                                                         **
       **   Implementations of abstract methods                                                   **
       **                                                                                         **
       *********************************************************************************************/

    @Override
    protected Properties[] readPropertiesFromStorage(boolean purpose) throws UMConfigurationException {
        final String method = "readPropertiesFromStorageInternal(boolean)";

        if(_loc.beInfo()) {
            _loc.infoT(method,
                "Reading UME properties from configuration manager (purpose: {0}).",
                new Object[] {
                    purpose == PURPOSE_USE_AT_RUNTIME ? "apply properties at runtime" : "administration"
                } );
        }

        Properties allProperties = null;
        // For runtime purposes, properties are read using the service frame's easy-to-use methods.
        if(purpose == PURPOSE_USE_AT_RUNTIME) {
            allProperties = _serviceContext.getServiceState().getProperties();
        }
        // For administrative purposes, we need to read the properties directly from the
        // configuration adapter because we use the "custom_global" branch for our administration
        // and there is no other way to access that level.
        else { // PURPOSE_ADMINISTRATION
            try {
                allProperties = getAdminProperties(true).getPropertySheet().getProperties();
            }
            catch(Exception e) {
                _loc.traceThrowableT(Severity.ERROR, method,
                    "An error occured while reading UME configuration properties.", e);
                throw new UMConfigurationException("Cannot read configuration properties.", e);
            }
        }

        Properties[] splitProperties = splitSecureAndNonsecureProperties(allProperties);
        Properties nonsecureProperties = splitProperties[PROPERTIES_ARRAY_INDEX_NONSECURE];
        Properties    secureProperties = splitProperties[PROPERTIES_ARRAY_INDEX_SECURE   ];

        if(_loc.beInfo()) {
            _loc.infoT(method,
                "UME properties have been read successfully.\n" +
                "Found {0} non-secure properties: {1}\n" +
                "Found {2} secure properties (only property names -> security reasons): {3}",
                new Object[] {
                    Integer.toString(nonsecureProperties.size()), nonsecureProperties.toString(),
                    Integer.toString(secureProperties   .size()), secureProperties.keySet().toString()
                } );
        }

        // Split properties into non-secure and secure properties.
        return splitProperties;
    }

    @Override
    protected Map<String, byte[]> readConfigFilesFromStorage(String[] fileNames, boolean purpose)
    throws UMConfigurationException {
        final String method = "readConfigFilesFromStorage(String[], boolean purpose)";

        // Determine required configuration level.
        boolean configLevel;
        if(purpose == PURPOSE_USE_AT_RUNTIME) {
            configLevel = CFG_LEVEL_INSTANCE;
        }
        else { // PURPOSE_ADMINISTRATION
            configLevel = CFG_LEVEL_GLOBAL;
        }

        if(_loc.beInfo()) {
            _loc.infoT(method,
                "Reading UME configuration files from the configuration manager " +
                "(purpose: {0}, configuration branch: {1}).",
                new Object[] {
                    purpose     == PURPOSE_USE_AT_RUNTIME ? "apply properties at runtime" : "administration",
                    configLevel == CFG_LEVEL_GLOBAL       ? "custom_global"               : "current_instance"
                } );
        }

        PersistentContainer persistentContainer = _serviceContext.getServiceState().getPersistentContainer();

        // Determine the files that should be read.
        if(fileNames == null) {
            try {
                fileNames = persistentContainer.listPersistentEntryNames(configLevel);
            }
            catch(ServiceException e) {
                _loc.traceThrowableT(Severity.ERROR, method,
                    "An error occured while listing all UME configuration files.", e);
                throw new UMConfigurationException("Cannot list configuration files.", e);
            }
        }

        // Prepare a cache Map to add the refreshed config file(s) to.
        Map<String, byte[]> configFilesRead = new HashMap<String, byte[]>();

        for(int i = 0; i < fileNames.length; i++) {
            String currentFileName = fileNames[i];

            if(_loc.beInfo()) {
                _loc.infoT(method, "Reading configuration file \"{0}\"...", new Object[] { currentFileName } );
            }

            try {
                // Read the file...
                InputStream currentFileStream = persistentContainer.getPersistentEntryStream(currentFileName, configLevel);
                byte[] currentFileData = Util.readInputStreamToBytes(currentFileStream);
                // ... and put it into the cache Map.
                configFilesRead.put(currentFileName, currentFileData);
            }
            catch(Exception e) {
                _loc.traceThrowableT(Severity.ERROR, method,
                    "An error occured while reading UME configuration file \"{0}\" ({1} configuration).",
                    new Object[] {
                        currentFileName,
                        configLevel == CFG_LEVEL_GLOBAL ? "global" : "instance"
                    }, e);
                throw new UMConfigurationException("Cannot read configuration file: " + currentFileName, e);
            }
        }

        if(_loc.beInfo()) {
            _loc.infoT(method, "Successfully read {0} configuration files.",
                new Object[] { Integer.toString(configFilesRead.size()) } );
        }

        return configFilesRead;
    }

    @Override
    protected InternalSaveResult writePropertiesToStorage(Properties nonsecureProperties, Properties secureProperties)
    throws UMConfigurationException {
        final String method = "writePropertiesToStorage(Properties, Properties)";

        if(_loc.beInfo()) {
            _loc.infoT(method, "Writing properties to configuration manager:\n" +
                "{0} non-secure properties: {1}\n" +
                "{2} secure properties (only property names -> security reasons): {3}",
                new Object[] {
                    Integer.toString(nonsecureProperties.size()), nonsecureProperties      .toString(),
                    Integer.toString(secureProperties   .size()), secureProperties.keySet().toString()
                } );
        }

        ComponentProperties adminProperties = null;
        try {
            adminProperties = getAdminProperties(false);
            PropertySheet adminPropertySheet = adminProperties.getPropertySheet();

            // Write both non-secure and secure properties to the configuration adapter
            // (commit() will be called afterwards to really write the data of both in one step).
            boolean serverRestartRequired = writePropertiesToStorageInternal(adminPropertySheet, nonsecureProperties, false);
            serverRestartRequired        |= writePropertiesToStorageInternal(adminPropertySheet,    secureProperties, true );

            // Finally commit the changes.
            adminProperties.applyChanges();

            if(_loc.beInfo()) {
                _loc.infoT(method,
                    "Changed properties have been successfully written to the configuration manager.");
            }

            // Result:
            // - Do not update the caches (because the change listener receives respective
            //   notifications and updates the caches on its own).
            // - The necessity for a server restart depends on whether all modified properties
            //   are online-modifiable or not.
            return new InternalSaveResult(false, serverRestartRequired);
        }
        catch(Exception e) {
            _loc.traceThrowableT(Severity.ERROR, method, e);
            _cat.warningT(_loc, method,
                "An error occured while setting new values for UME configuration properties. " +
                "All changes performed in the same step will be rolled back, so nothing " +
                "has been changed.");

            if(adminProperties != null) {
                try {
                    adminProperties.discardChanges();
                }
                catch(ClusterConfigurationException e1) {
                    _loc.traceThrowableT(Severity.DEBUG, method, "This exception can be ignored.", e1);
                }
            }

            throw new UMConfigurationException("Setting new property values failed.", e);
        }
    }

    @Override
    protected boolean writeConfigFileToStorage(String fileName, byte[] data)
    throws UMConfigurationException {
        final String method = "persistFileChanges(String, byte[])";

        PersistentContainer persistentContainer = _serviceContext.getServiceState().getPersistentContainer();

        // Should the file be written or deleted?
        if(data == null) {
            if(_loc.beInfo()) {
                _loc.infoT(method,
                    "Deleting configuration file \"{0}\" from the configuration manager...",
                    new Object[] { fileName } );
            }
            try {
                persistentContainer.removePersistentEntry(fileName, CFG_LEVEL_GLOBAL);

                if(_loc.beInfo()) {
                    _loc.infoT(method, "Successfully deleted configuration file \"{0}\".",
                        new Object[] { fileName } );
                }
            }
            catch(ServiceException e) {
                _loc.traceThrowableT(Severity.ERROR, method, e); // $JL-SEVERITY_TEST$
                _cat.warningT(_loc, method,
                    "An error occured while deleting UME configuration file \"{0}\". ",
                    new Object[] { fileName });
                throw new UMConfigurationException("Deleting UME configuration file failed.", e);
            }
        }
        else {
            if(_loc.beInfo()) {
                _loc.infoT(method,
                    "Writing configuration file \"{0}\" ({1} bytes) to the configuration manager...",
                    new Object[] {
                        fileName, Integer.toString(data.length)
                    } );
            }

            try {
                persistentContainer.setPersistentEntryStream(fileName, new ByteArrayInputStream(data), CFG_LEVEL_GLOBAL);

                if(_loc.beInfo()) {
                    _loc.infoT(method, "Successfully wrote configuration file \"{0}\".",
                        new Object[] { fileName } );
                }
            }
            catch(ServiceException e) {
                _loc.traceThrowableT(Severity.ERROR, method, e); // $JL-SEVERITY_TEST$
                _cat.warningT(_loc, method,
                    "An error occured while saving data to UME configuration file \"{0}\". ",
                    new Object[] { fileName });
                throw new UMConfigurationException("Saving data to UME configuration file failed.", e);
            }
        }

        // No cache updates are required because the change listener
        // receives respective notifications and updates the caches on its own.
        return false;
    }

    @Override
    protected String[] listConfigFiles(boolean purpose) throws UMConfigurationException {
        final String method = "listConfigFiles(boolean)"; 

        // Determine required configuration level.
        // Note: The following simple assignment is possible because the constants have been defined accordingly:
        // PURPOSE_ADMINISTRATION - CFG_LEVEL_GLOBAL   - true
        // PURPOSE_USE_AT_RUNTIME - CFG_LEVEL_INSTANCE - false
        boolean configLevel;
        if(purpose == PURPOSE_USE_AT_RUNTIME) {
            configLevel = CFG_LEVEL_INSTANCE;
        }
        else { // PURPOSE_ADMINISTRATION
            configLevel = CFG_LEVEL_GLOBAL;
        }

        if(_loc.beInfo()) {
            _loc.infoT(method,
                "Listing UME configuration files from the configuration manager " +
                "(purpose: {0}, configuration branch: {1}).",
                new Object[] {
                    purpose     == PURPOSE_USE_AT_RUNTIME ? "apply properties at runtime" : "administration",
                    configLevel == CFG_LEVEL_GLOBAL       ? "custom_global"               : "current_instance"
                } );
        }

        try {
            String[] fileNames =
                _serviceContext.getServiceState().getPersistentContainer().listPersistentEntryNames(configLevel);

            if(_loc.beInfo()) {
                _loc.infoT(method, "Found {0} configuration files: {1}",
                    new Object[] {
                        Integer.toString(fileNames.length),
                        Util.getArrayContentsAsString(fileNames)
                    } );
            }

            return fileNames;
        }
        catch(ServiceException e) {
            _loc.traceThrowableT(Severity.ERROR, method, e); // $JL-SEVERITY_TEST$
            _cat.warningT(_loc, method,
                "An error occured while listing UME configuration files. ");
            throw new UMConfigurationException("Saving data to UME configuration file failed.", e);
        }
    }

	public byte[] downloadConfiguration() throws UMConfigurationException {
		// Get *all* properties (both non-secure and secure) as current state of
		// the properties used at runtime.
		Properties runtimeProperties = getAllPropertiesDynamic();
		runtimeProperties.putAll(getAllSecurePropertiesDynamic());

		try {
			ConfigOverview configOverview = new ConfigOverview(
				_configHandlerFactory.getConfigurationHandler(),
				runtimeProperties);
			return configOverview.downloadConfiguration();
		}
		catch(ConfigurationException e) {
			throw new UMConfigurationException("Cannot access configuration manager.", e);
		}
		catch(IOException e) {
			throw new UMConfigurationException("Cannot generate configuration ZIP file.", e);
		}
	}

    public Collection<IProperty> getAllPropertiesExtendedAdmin(int flags) throws UMConfigurationException {
        final String method = "getAllPropertiesExtendedAdmin()";

        boolean retrieveDifferingInstanceValues = (flags & PROPERTY_INFO_DIFFERING_INSTANCE_VALUES) != 0;

        try {
            CommonClusterFactory clusterFactory = ClusterConfiguration.getClusterFactory(_configHandlerFactory);

            String[] instanceIDs = null;
            PropertySheet[] instanceProperties = null;
            if(retrieveDifferingInstanceValues) {
                instanceIDs = clusterFactory.listIdentifiers(CommonClusterFactory.LEVEL_INSTANCE);

                instanceProperties = new PropertySheet[instanceIDs.length];            
                for (int i = 0; i < instanceProperties.length; i++) {
                    instanceProperties[i] =
                        clusterFactory.openConfigurationLevel(CommonClusterFactory.LEVEL_INSTANCE, instanceIDs[i])
                            .getComponentAccess().getServiceProperties(_serviceName, true).getPropertySheet();
                }
            }

            PropertyEntry[] propertyEntries = clusterFactory.openGlobalConfigurationLevel().
                getComponentAccess().getServiceProperties(_serviceName, true).getPropertySheet()
                    .getAllPropertyEntries();

            List<IProperty> properties = new ArrayList<IProperty>(propertyEntries.length);
            
            for (PropertyEntry currentProperty : propertyEntries) {
                Object currentGlobalValue = currentProperty.getValue();

                Map<String, String> differingInstanceValues = null;
                if(retrieveDifferingInstanceValues && instanceIDs != null && instanceProperties != null) {
                    for(int i = 0; i < instanceProperties.length; i++) {
                        Object currentInstanceValue =
                            instanceProperties[i].getPropertyEntry(currentProperty.getName()).getValue();
                        
                        if((currentGlobalValue == null && currentInstanceValue != null)
                        || (currentGlobalValue != null && ! currentGlobalValue.equals(currentInstanceValue))) {
                            if(differingInstanceValues == null) {
                                differingInstanceValues = new HashMap<String, String>();
                            }

                            differingInstanceValues.put(instanceIDs[i],
                                currentInstanceValue != null ? currentInstanceValue.toString() : null);
                        }
                    }
                }

                properties.add(new PropertyAdapter(currentProperty, differingInstanceValues));
            }

            return properties;
        }
        catch(Exception e) {
            _loc.traceThrowableT(Severity.ERROR, method,
                "An error occured while reading UME configuration properties.", e);
            throw new UMConfigurationException("Cannot read configuration properties.", e);
        }
    }

    /* *********************************************************************************************
       **                                                                                         **
       **   Reimplementations overwriting default implementations in UMConfigurationBase          **
       **                                                                                         **
       *********************************************************************************************/

    @Override
    public boolean isPropertySecure(String property) {
        // Check arguments.
        if(property == null) throw new NullPointerException("The property name argument is null!" );

        return _securePropertyNames.contains(property);
    }

    /* *********************************************************************************************
       **                                                                                         **
       **   Utility methods for local usage                                                       **
       **                                                                                         **
       *********************************************************************************************/

    private ConfigurationLevel getCurrentInstanceConfigLevel() throws ClusterConfigurationException {
    	CommonClusterFactory clusterFactory = ClusterConfiguration.getClusterFactory(_configHandlerFactory);
    	return clusterFactory.openConfigurationLevel(CommonClusterFactory.LEVEL_INSTANCE, "current_instance");
    }

    private ConfigurationLevel getCustomGlobalConfigLevel() throws ClusterConfigurationException {
    	CommonClusterFactory clusterFactory = ClusterConfiguration.getClusterFactory(_configHandlerFactory);
    	return clusterFactory.openGlobalConfigurationLevel();
    }

    private ComponentProperties getAdminProperties(boolean readonly) throws ClusterConfigurationException {
        return getCustomGlobalConfigLevel().getComponentAccess().getServiceProperties(_serviceName, readonly);
    }

    /**
     * Make sure that recently deployed property metadata changes are applied to the
     * "custom_global" configuration level and set the "final" flag for each property that
     * must be identical in the whole cluster.
     * 
     * <p>
     * Note: Setting a flag for a property entry completely disables inheritance from the
     * superordinate property sheet. Any metadata changes (i.e new default value or
     * "onlinemodifiable" flag) deployed afterwards don't reach the configuration level
     * where the flag has been set because inheritance has been broken.
     * <br/>
     * That's why this method implements a workaround to make sure that recently deployed
     * metadata changes are applied even if the "final" flag has already been set.
     * In general, it first restores inheritance from the superordinate configuration level
     * to apply recently deployed metadata changes, restores a potentially existing custom
     * value and finally sets the \"final\" flag for all properties which currently have to
     * be identical in the whole cluster.
     * </p>
     */
    @SuppressWarnings("boxing")
	private void updatePropertyMetadata() {
        final String method = "updatePropertyMetadata()";

        // This method first checks whether updating properties metadata is necessary at all
        // based on the "last modification" time stamps of the deployed version of the service
        // property sheet and the one in the "custom_global" configuration level.
        // 
        // IMPORTANT: Updating properties metadata is performed on the "custom_global" (-> "admin") level!
        // 
    	ConfigurationHandler configHandler = null;

    	// If updating properties metadata is necessary, the already opened "top level" configuration
    	// of the UME property sheet is used to collect the names of all properties contained in the
    	// currently deployed property sheet.
        // This is used to check whether a property has been removed from the deployed service in the
        // meanwhile and only exists in the "custom_global" level (because of a custom value which is
    	// preserved by the configuration manager). In that case, the property entry on the
        // "custom_global" value is NOT deleted for restoring inheritance because
        // - the custom value could not be restored afterwards and
        // - there would not be any metadata (changes) to apply to the "custom_global" level anyway.
    	Set<String> deployedProperties;
    	try {
    		configHandler = _configHandlerFactory.getConfigurationHandler();
        	Configuration customGlobalConfig =
        		configHandler.openConfiguration(_propertiesPathGlobal, ConfigurationHandler.READ_ACCESS);

        	if((customGlobalConfig.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) != 0) {
        		DerivedConfiguration customGlobalConfigDerived = (DerivedConfiguration) customGlobalConfig;

        		Configuration customGlobalParentConfig = customGlobalConfigDerived.getLinkedConfiguration();

        	    // Check whether the UME service properties metadata needs to be updated at all
        		// (which is usually necessary after deployment of a new UME service property
        		// sheet, i.e. after system installation and upgrade or any other UME service
        		// deployment).

        		// Climb up the configuration link tree until the root node is reached.
        		// That's the property sheet written by the UME service deployment.
        		Configuration rootConfig;
        		{
            		Configuration currentSuperConfig = customGlobalParentConfig;
            		while((currentSuperConfig.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) != 0) {
            			currentSuperConfig = ((DerivedConfiguration) currentSuperConfig).getLinkedConfiguration();
            		}
            		rootConfig = currentSuperConfig;
        		}
        		long customGlobalTimestamp = customGlobalConfigDerived.getLastModificationTimeStamp();
        		long rootConfigTimestamp   = rootConfig               .getLastModificationTimeStamp();

        		// Compare the "last modification" time stamp of the deployed property sheet
        		// (which is updated on every UME service deployment) against the one in the
        		// "custom_global" branch (which is updated on every properties metadata update).
        		// 
        		// There are two cases:
        		// 1) After installation/update of the UME service: A new property sheet has been
        		//    deployed, which updated the time stamp of the deployed version of the
        		//    property sheet, so that time stamp is greater than the one of the property
        		//    sheet in the "custom_global" branch.
        		//    -> The property metadata in the "custom_global" branch is updated now, which
        		//       also updates the time stamp of the respective property sheet to a higher
        		//       value than the one of the deployed property sheet.
        		// 2) Normal system startup: As property metadata has been updated in 1), the time
        		//    stamp in the "custom_global" branch is higher than the one of the deployed
        		//    property sheet.
        		//    -> Updating property metadata is skipped.
        		// 
        		// Case 2:
        		if(rootConfigTimestamp < customGlobalTimestamp) {
        			_loc.infoT(method, "The last modification time stamp of the deployed UME " +
        				"property sheet is lower than the one of the UME property sheet in the " +
        				"\"custom_global\" configuration branch:\n" +
        				"{0} -> {1}\n" +
        				"{2} -> {3}\n" +
        				"Skipping update of UME properties metadata because no new revision of " +
        				"the property sheet has been deployed.",
        				new Object[] {
        					customGlobalConfig.getPath(), customGlobalTimestamp,
        					rootConfig        .getPath(), rootConfigTimestamp
        				} );

        			return;
        		}

        		// Case 1:
    			_loc.infoT(method, "The last modification time stamp of the deployed UME " +
        			"property sheet is higher than or equal to the one of the UME property " +
        			"sheet in the \"custom_global\" configuration branch:\n" +
        			"{0} -> {1}\n" +
        			"{2} -> {3}\n" +
        			"Updating UME properties metadata now because the new revision of the " +
        			"property sheet might contain relevant changes.",
    				new Object[] {
    					customGlobalConfig.getPath(), customGlobalTimestamp,
    					rootConfig        .getPath(), rootConfigTimestamp
    				} );

    			// As updating properties metadata is necessary, collect the names of all
    			// properties from the currently deployed property sheet now.
        		String[] propertyNames = customGlobalParentConfig.getPropertySheetInterface().getAllPropertyEntryNames();

        		deployedProperties = new HashSet<String>(propertyNames.length);
        		for(int i = 0; i < propertyNames.length; i++) {
        			deployedProperties.add(propertyNames[i]);
        		}
        	}
        	else {
        		_cat.warningT(_loc, method, "The UME property sheet in the \"custom_global\" configuration " +
        			"level is not a derived configuration.\n" +
        			"Although this does not affect UME functionality severely, this is not supposed to " +
        			"happen.\n" +
        			"Please inform SAP support on component BC-JAS-SEC-UME.");
        		deployedProperties = null;
        	}
    	}
    	catch(ConfigurationException e) {
    		_loc.traceThrowableT(Severity.ERROR, method, e);
    		deployedProperties = null;
    	}
    	finally {
    		if(configHandler != null) {
                try {
					configHandler.closeAllConfigurations();
				}
                catch(ConfigurationException e) {
					_loc.traceThrowableT(Severity.DEBUG, method, "This exception can be ignored", e);
				}
    		}
    	}

    	if(deployedProperties == null) {
    		_cat.warningT(_loc, method, "An internal error occured while preparing configuration " +
       			"of UME properties. This does not directly affect your running system, but might " +
    			"disable the feature of changing some UME properties without server restart.\n" +
				"Please check the log and trace for related messages to solve this issue.");

    		// This is not critical, so don't throw an exception, but simply return.
    		return;
    	}

    	// Now handle the real property metadata updates and set the "final" flags.
        ComponentProperties adminProperties = null;
    	try {
        	adminProperties = getAdminProperties(false);
        	PropertySheet globalPropertySheet = adminProperties.getPropertySheet();

        	// Iterate over all property entries.
            PropertyEntry[] propertyEntries = globalPropertySheet.getAllPropertyEntries();
            for(int i = 0; i < propertyEntries.length; i++) {
                PropertyEntry currentProperty = propertyEntries[i];
                String currentPropertyName = currentProperty.getName();

            	// Remember the custom value to be able to restore it after temporarily
            	// re-enabling inheritance from the superordinate propertysheet.
            	Object customValue = currentProperty.getCustom();

            	// Check whether the property needs to have the "final" flag set.
                boolean shouldPropertyBeFinal = ! mayPropertyBeConfiguredPerInstance(currentPropertyName);

                // If the property has local (meta)data on this configuration level, first
                // restore inheritance, then restore a potentially existing custom value.
                // Reason: This makes sure any updates to metadata like the default value,
                // the "onlinemodifiable" flag or the data type/range that have just been
                // deployed are "copied" into this configuration level. As inheritance is
                // currently broken between the local level and the superordinate level,
                // any deployed metadata changes are not automatically "transported" into
                // the local level, so it's necessary to force this using the described
                // procedure.
                if(currentProperty.isInherited()) {
                	if(_loc.beDebug()) {
                    	_loc.debugT(method, "Inheritance for property \"{0}\" is still intact. " +
                    		"No need to take over metadata changes from the deployed property " +
                    		"sheet if a new version of the UME service has been deployed.",
                    		new Object[] { currentPropertyName } );
                	}
                }
                else {
                	// Take over recently deployed metadata changes.

                	// It is possible that the property entry exists on the local configuration
                	// level, but not on the superordinate level (because it has been removed
                	// from the propertysheet that is contained in the service SDA).
                	// -> For compatibility reasons, such "local only" property entries are kept
                	//    if they have a custom value.
                	boolean existsOnSuperLevel = deployedProperties.contains(currentPropertyName);
                	if(! existsOnSuperLevel && (customValue != null)) {
                		if(_loc.beDebug()) {
                			_loc.debugT(method,
                				"UME configuration property \"{0}\" is no longer defined in the " +
                				"recently deployed version of the UME service. As there is a custom " +
                				"value maintained for that property, the the property's default " +
                				"value and flags cannot be updated. Only the \"final\" flag will " +
                				"be updated, if necessary.",
                				new Object[] { currentPropertyName } );
                		}

                		// Set the "final" flag and continue with the next property.
                		currentProperty.setFinal(shouldPropertyBeFinal);

                		continue;
                	}

                	// Restore inheritance from the superordinate property sheet.
                	// This makes sure we take over all metadata changes (i.e. new default value,
                	// modified flags) that have just been deployed.
                    globalPropertySheet.deletePropertyEntry(currentPropertyName);

                    // Get a new PropertyEntry instance for the inherited property after having
                    // deleted the local entry (only relevant if there is such an entry on the
                    // superordinate level).
                    if(existsOnSuperLevel) {
                        currentProperty = globalPropertySheet.getPropertyEntry(currentPropertyName);
                    }
                }

                // Restore a potentially existing custom value and set the "final" flag, if necessary.
            	// -> Only if the property entry is still there, i.e. not if the property entry has
                //    been removed from the deployed property sheet and did not have a custom value.
            	if(globalPropertySheet.existsPropertyEntry(currentPropertyName)) {
                    // If there was a custom value, restore it now.
                    if(customValue != null) {
                    	if(_loc.beDebug()) {
                    		_loc.debugT(method,
                    			"Restoring custom value for property \"{0}\".",
                    			new Object[] { currentPropertyName } );
                    	}

                    	currentProperty.setValue(customValue);
                    }

                    // Set the "final" flag for each property which has been explicitely
                    // declared as "must be identical in the whole cluster".
                    // (-> default: may be instance specific)
                    if(shouldPropertyBeFinal) {
                        if(_loc.beDebug()) {
                            _loc.debugT(method, "Setting the \"final\" flag of property \"{0}\".",
                                new Object[] { currentPropertyName } );
                        }

                        currentProperty.setFinal(true);
                    }
                    else {
                        if(_loc.beDebug()) {
                            _loc.debugT(method, "Property \"{0}\" does not require the \"final\" flag.",
                                new Object[] { currentPropertyName } );
                        }
                    }
            	}
            	else {
                	if(_loc.beInfo()) {
                		_loc.infoT(method,
               				"UME configuration property \"{0}\" is no longer defined in the " +
            				"recently deployed version of the UME service. As it does not have " +
            				"a custom value, it is simply deleted now.",
            				new Object[] { currentPropertyName } );
                	}
            	}
            }

            // Commit the changes for step 1.
            adminProperties.applyChanges();

            _loc.infoT(method, "Successfully restored inheritance from the superordinate configuration " +
                "level for all UME properties in the \"custom_global\" configuration which do not have " +
                "custom values.");
        }
        catch(Exception e) {
            _loc.traceThrowableT(Severity.ERROR, method, e);
            _cat.warningT(_loc, method,
                "An error occured while configuring UME properties (for preventing inconsistent " +
                "configuration and for applying recently deployed changes).\n" +
                "This is not critical for running the server. However, without the flags having " +
                "been set correctly, it is possible that you customize some properties per " +
                "template or per instance although they must be identical in the whole cluster. " +
                "This might lead to inconsistent behaviour between different cluster instances.");

            if(adminProperties != null) {
                try {
                    adminProperties.discardChanges();
                }
                catch(ClusterConfigurationException e1) {
                    _loc.traceThrowableT(Severity.DEBUG, method, "This exception can be ignored.", e);
                }
            }

            // Don't throw an exception because the server can run anyway, but return to make sure
            // nothing else is attempted to be changed.
            return;
        }
    }

    /**
     * Initialize some internal data.
     * 
     * @throws UMConfigurationException 
     */
    private void init() throws UMConfigurationException {
        final String method = "init()";

        _loc.infoT(method, "Collecting information about secure vs. non-secure properties...");

        _securePropertyNames = new HashSet<String>();

        // This assumes that the "secure" flag for each property is identical in the whole cluster,
        // which should be ok because it cannot be changed manually. If this assumption is wrong,
        // we must use the "current_instance" propertysheet instead of the "custom_global" one here.
        try {
            // This step can be performed on every configuration level. We use the
            // "custom_global" level here just because we already have the full path.
            ComponentProperties adminProperties = getAdminProperties(true);
            PropertySheet adminPropertySheet = adminProperties.getPropertySheet();
            PropertyEntry[] propertyEntries = adminPropertySheet.getAllPropertyEntries();

            for(int i = 0; i < propertyEntries.length; i++) {
                PropertyEntry currentProperty = propertyEntries[i];
                String currentPropertyName = currentProperty.getName();

                // Collect the names of all secure properties.
                if(currentProperty.isSecure()) {
                    _securePropertyNames.add(currentPropertyName);
                }
            }

            if(_loc.beInfo()) {
                _loc.infoT(method, "Found {0} secure properties: {1}",
                    new Object[] {
                        Integer.toString(_securePropertyNames.size()), _securePropertyNames.toString()
                    } );
            }
        }
        catch(Exception e) {
            _loc.traceThrowableT(Severity.ERROR, method,
                "An error occured while searching for all secure and online modifiable UME properties.", e);
            throw new UMConfigurationException("Searching secure and online modifiable properties failed.", e);
        }
    }

    private boolean writePropertiesToStorageInternal(PropertySheet propertySheet, Properties properties, boolean secure)
    throws ConfigurationException, UMConfigurationException {
        final String method = "PropertySheet, Properties, boolean)";

        boolean serverRestartRequired = false;
        Iterator<Entry<Object, Object>> propertiesIterator = properties.entrySet().iterator();
        while(propertiesIterator.hasNext()) {
            Entry<Object, Object> currentMapEntry = propertiesIterator.next();
            String currentPropertyName     = (String) currentMapEntry.getKey();
            String currentNewPropertyValue = (String) currentMapEntry.getValue();
        
            PropertyEntry currentPropertyEntry = propertySheet.getPropertyEntry(currentPropertyName);
            if(currentPropertyEntry == null) {
                // TODO Allow adding new properties? If yes, add the new PropertyEntry here.
                // In the meanwhile, trying to set a non-existing property is not possible.
                _cat.warningT(_loc, method,
                    "An application tried to set a UME configuration property which does not " +
                    "exist. Adding new properties is not possible, so the changed " +
                    "configuration cannot be saved.\n" +
                    "Affected property: \"{0}\"\n" +
                    "Value to be set  : \"{1}\"",
                    new Object[] {
                        currentPropertyName,
                        secure ? "(hidden because it is a secure property)" : currentNewPropertyValue
                    } );
                throw new UMConfigurationException("Property entry does not exist: " + currentPropertyName);
            }
        
            // Check whether the property entry's "secure" flag matches the respective context.
            boolean isCurrentPropertySecure = currentPropertyEntry.isSecure();
            if(secure != isCurrentPropertySecure) {
                _cat.warningT(_loc, method,
                    "An application tried to set a new value for a UME configuration property" +
                    "as if it was a {0} property, but it is defined as {1}. This is an internal " +
                    "error because handling of secure and non-secure properties cannot be mixed.\n" +
                    "The changed configuration cannot be saved.\n" +
                    "Affected property: \"{2}\"\n" +
                    "Value to be set  : \"{3}\"",
                    new Object[] {
                        secure ? "secure" : "non-secure",
                        isCurrentPropertySecure ? "secure" : "non-secure",
                        currentPropertyName,
                        secure ? "(hidden because it is a secure property)" : currentNewPropertyValue
                    } );
                throw new UMConfigurationException(
                    "The property's secure flag and the the method called to set the value " +
                    "do not match: " + currentPropertyName);
            }
        
            // Only process the property if its value has really been changed.
            // Reason: Even "restoreDefault()" breaks the property's inheritance link,
            //         so a single call to "setProperties(Properties)" containing all
            //         properties without any changes would break all inheritance links,
            //         although, in fact, the values have never been touched and the
            //         customer still wants to have the (inherited) default values.
            String previousValue = currentPropertyEntry.getValue().toString();
            if(! previousValue.equals(currentNewPropertyValue)) {
                // Get the property's default value to decide whether setValue()
                // or restoreDefault() should be called.
                String defaultValue = currentPropertyEntry.getDefault().toString();
        
                // Set custom value or reset to default value?
                if(currentNewPropertyValue.equals(defaultValue)) {
                    if(_loc.beInfo()) {
                        _loc.infoT(method, "The new value of property \"{0}\" is the default value. " +
                            "Restoring the default value instead of setting the value as custom value.",
                            new Object[] { currentPropertyName } );
                    }

                    // Note: This restores the default value, but it does NOT restore the
                    //       link to the same property in the superordinate property sheet.
                    currentPropertyEntry.restoreDefault();
                }
                else {
                    if(_loc.beInfo()) {
                        _loc.infoT(method, "The new value of property \"{0}\" is not the default value. " +
                            "Setting the value as custom value.",
                            new Object[] { currentPropertyName } );
                    }

                    currentPropertyEntry.setValue(currentNewPropertyValue);
                }

                if(! serverRestartRequired) serverRestartRequired = ! currentPropertyEntry.isOnlineModifiable();
            }
            else {
                if(_loc.beDebug()) {
                    _loc.infoT(method,
                        "Skipping update of property \"{0}\" because its value has not been changed.",
                        new Object[] { currentPropertyName } );
                }
            }
        }

        return serverRestartRequired;
    }

    synchronized private void updateOnlineModifiableProperties(
    	Properties modifiedNonsecureProperties, Properties modifiedSecureProperties) {
    	final String method = "updateOnlineModifiableProperties(Properties, Properties)";

        // Only update caches if there was a real change to a least one online-modifiable property.
        if(modifiedNonsecureProperties.size() == 0 && modifiedSecureProperties.size() == 0) {
        	_loc.infoT(method, "No online-modifiable property has been effectively changed. Nothing to do.");
        	return;
        }

        try {
            // Actually update the current properties caches.
	        IMessage[] results = updatePropertiesCaches(
	        	EMPTY_PROPERTIES, modifiedNonsecureProperties, EMPTY_SET,
		        EMPTY_PROPERTIES, modifiedSecureProperties   , EMPTY_SET);

	        // The returned IMessage objects cannot be passed further, so they are traced instead.
	        // Logging should not be necessary because they are only uncritical messages (critical
	        // messages are bound to a thrown exception).
	        // Additionally, if the changes have been applied through the UME configuration UI,
	        // the same messages should have been displayed already on the UI because the
	        // IPropertiesChangedListener.checkPropertiesChanges(...) methods have been called
	        // and should have returned the same messages.
	        if(results != null && results.length > 0 && _loc.beInfo()) {
	        	_loc.infoT(method, "Updating the properties caches returned {0} warnings. They " +
	        		"cannot be displayed on the UI because this code has been called as part of " +
	        		"a change listener which is only supposed to apply the changes.\n" +
	        		"These are the returned messages:",
	        		new Object[] { Integer.toString(results.length) } );

	        	for(int i = 0; i < results.length; i++) {
	        		IMessage currentMessage = results[i];
	        		_loc.infoT(method, "Message {0}/{1}: {2}",
	        			new Object[] {
	        				Integer.toString(i),
	        				Integer.toString(results.length),
	        				currentMessage
	        			} );
	        	}
	        }
		}
		catch(ConfigChangeRejectedException e) {
			_loc.traceThrowableT(Severity.ERROR, method,
				"Recent online modifications of UME properties have been rejected.", e);

			StringBuilder logMessage = new StringBuilder();
			logMessage.append("Could not apply recent online changes of UME properties because " +
				"they would cause problems in UME behaviour.\n");
			
			IMessage[] messages = e.getMessages();
			if(messages != null && messages.length > 0) {
				logMessage.append("Please check the following messages for more information about " +
					"which properties contain problematic values.");

				for(int i = 0; i < messages.length; i++) {
	        		IMessage currentMessage = messages[i];

	        		logMessage.append("Message ").append(i).append('/').append(messages.length);
	        		logMessage.append(" about problematic modifications to UME properties: ");
	        		logMessage.append(currentMessage.toString());

	        		if(i < messages.length - 1) {
	        			logMessage.append('\n');
	        		}
				}
			}
			else {
				logMessage.append("No additional information about problematic UME properties " +
					"changes is available. Please check whether the logs and traces contain " +
					"related messages.");
			}

			_cat.warningT(_loc, method, logMessage.toString());
		}
		catch(UMConfigurationException e) {
			_loc.traceThrowableT(Severity.ERROR, method,
				"An error occured while updating online-modifiable UME properties.", e);
			_cat.warningT(_loc, method, "Could not apply recent online changes of UME properties." +
				"Please check the log and trace files for related messages.");
		}
    }

    /* *********************************************************************************************
       **                                                                                         **
       **   Utility methods for configuration change listeners                                    **
       **                                                                                         **
       *********************************************************************************************/

    /**
     * Utility method for notifications about changed properties via
     * {@link UMERuntimeConfiguration#updateProperties(Properties)}.
     * 
     * <p>
     * This variant receives all modified properties in the argument. It splits the
     * mixed set of secure and non-secure properties into both groups and handles
     * them separately.
     * </p>
     * 
     * @param modifiedProperties Properties that have been changed.
     */
    synchronized void updateOnlineModifiableProperties(Properties modifiedProperties) {
        // We assume that "modifiedProperties" only contains changed properties, so we don't need
        // to check on our own which properties have been changed and which have not etc.
        // -> Directly update the caches with this information.
        Properties[] splitProperties = splitSecureAndNonsecureProperties(modifiedProperties);
        Properties modifiedNonsecureProperties = splitProperties[PROPERTIES_ARRAY_INDEX_NONSECURE];
        Properties modifiedSecureProperties    = splitProperties[PROPERTIES_ARRAY_INDEX_SECURE];

        updateOnlineModifiableProperties(modifiedNonsecureProperties, modifiedSecureProperties);
    }

    /**
     * Utility method for notifications about changed properties via
     * {@link UMEPropertiesListener#configurationChanged(ChangeEvent)}.
     * 
     * <p>
     * This variant checks all "online modifiable" properties for recent changes on its own.
     * </p>
     */
    synchronized void updateOnlineModifiableProperties() {
    	final String method = "updateOnlineModifiableProperties()";

    	_loc.infoT(method, "Updating all properties that currently have the \"onlinemodifiable\" flag set.");

    	PropertyEntry[] propertyEntries;
		try {
			ConfigurationLevel instanceConfig = getCurrentInstanceConfigLevel();
			ComponentProperties serviceProperties =
				instanceConfig.getComponentAccess().getServiceProperties(_serviceName, true);
			propertyEntries = serviceProperties.getPropertySheet().getAllPropertyEntries();

	        // Iterate over all property entries and collect the values of all online modifiable properties.
	        Properties modifiedNonsecureProperties = new Properties();
	        Properties modifiedSecureProperties    = new Properties();

	        for(int i = 0; i < propertyEntries.length; i++) {
	            PropertyEntry currentProperty = propertyEntries[i];
	            String  currentPropertyName     = currentProperty.getName();
	            String  currentPropertyValue    = currentProperty.getValue().toString();
	            boolean isCurrentPropertySecure = currentProperty.isSecure();

	            // Has the property's value been modified after the last update?
	            String previousPropertyValue;
	            if(isCurrentPropertySecure) {
	                previousPropertyValue = getSecurePropertyDynamicInternal(currentPropertyName);
	            }
	            else {
	                previousPropertyValue = getStringDynamic(currentPropertyName);
	            }
	            if(currentPropertyValue.equals(previousPropertyValue)) {
	                if(_loc.beDebug()) {
	                    _loc.debugT(method, "Property \"{0}\" has not been modified. Nothing to do.",
	                        new Object[] { currentPropertyName } );
	                }

	                continue;
	            }

	            // Now we know that the property value has been changed.
	            // 
	            // Replace all existing properties which (now) have the "onlinemodifiable" flag set.
	            // (We need to check both custom and default flags because the custom
	            //  flags are only available if there is a custom value.)
	            if(currentProperty.isOnlineModifiable()) {
	                if(_loc.beInfo()) {
	                    _loc.infoT(method,
	                        "Property \"{0}\" can be modified online. Applying its current value.",
	                        new Object[] { currentPropertyName } );
	                }

	                if(isCurrentPropertySecure) {
	                    modifiedSecureProperties   .setProperty(currentPropertyName, currentPropertyValue);
	                }
	                else {
	                    modifiedNonsecureProperties.setProperty(currentPropertyName, currentPropertyValue);
	                }
	            }
	            else {
	                if(_loc.beDebug()) {
	                    _loc.debugT(method, "Property \"{0}\" can be modified online. Nothing to do.",
	                        new Object[] { currentPropertyName } );
	                }
	            }
	        }

	        updateOnlineModifiableProperties(modifiedNonsecureProperties, modifiedSecureProperties);

	        _loc.infoT(method, "All \"onlinemodifiable\" properties have been updated.");
	    }
		catch(Exception e) {
			_loc.traceThrowableT(Severity.ERROR, method,
				"An internal error occured while updating online-modifiable UME properties.", e);
			_cat.warningT(_loc, method, "Could not apply recent online changes of UME properties." +
				"Please check the log and trace files for related messages.");

			return;
		}
    }

    synchronized void updateAllConfigurationFiles() {
    	final String method = "updateAllConfigurationFiles()";

    	try {
    		// Read all configuration files.
            Map<String, byte[]> allCurrentConfigFiles = readConfigFilesFromStorage(null, PURPOSE_USE_AT_RUNTIME);

            // Actually update the configuration file cache.
            IMessage[] results = replaceConfigFileCache(allCurrentConfigFiles);

            // The returned IMessage objects cannot be passed further, so they are traced instead.
            // Logging should not be necessary because they are only uncritical messages (critical
            // messages are bound to a thrown exception).
            // Additionally, if the changes have been applied through the UME configuration UI,
            // the same messages should have been displayed already on the UI because the
            // IConfigFilesChangedListener.checkFileChanges(...) methods have been called
            // and should have returned the same messages.
            if(results != null && results.length > 0 && _loc.beInfo()) {
            	_loc.infoT(method, "Updating the configuration file cache returned {0} warnings. " +
            		"They cannot be displayed on the UI because this code has been called as part " +
            		"of a change listener which is only supposed to apply the changes.\n" +
            		"These are the returned messages:",
            		new Object[] { Integer.toString(results.length) } );

            	for(int i = 0; i < results.length; i++) {
            		IMessage currentMessage = results[i];
            		_loc.infoT(method, "Message {0}/{1}: {2}",
            			new Object[] {
            				Integer.toString(i),
            				Integer.toString(results.length),
            				currentMessage
            			} );
            	}
            }
    	}
    	catch(ConfigChangeRejectedException e) {
    		_loc.traceThrowableT(Severity.ERROR, method,
    			"Recent online modifications of UME configuration files have been rejected.", e);

    		StringBuilder logMessage = new StringBuilder();
    		logMessage.append("Could not apply recent online changes of UME configuration files " +
    			"because they would cause problems in UME behaviour.\n");
    		
    		IMessage[] messages = e.getMessages();
    		if(messages != null && messages.length > 0) {
    			logMessage.append("Please check the following messages for more information about " +
    				"which configuration files contain problematic data.");

    			for(int i = 0; i < messages.length; i++) {
            		IMessage currentMessage = messages[i];

            		logMessage.append("Message ").append(i).append('/').append(messages.length);
            		logMessage.append(" about problematic modifications to UME configuration files: ");
            		logMessage.append(currentMessage.toString());

            		if(i < messages.length - 1) {
            			logMessage.append('\n');
            		}
    			}
    		}
    		else {
    			logMessage.append("No additional information about problematic UME configuration " +
    				"files changes is available. Please check whether the logs and traces contain " +
    				"related messages.");
    		}

    		_cat.warningT(_loc, method, logMessage.toString());
    	}
    	catch(UMConfigurationException e) {
    		_loc.traceThrowableT(Severity.ERROR, method,
    			"An error occured while updating UME configuration files.", e);
    		_cat.warningT(_loc, method, "Could not apply recent online changes of UME configuration " +
    			"files. Please check the log and trace files for related messages.");
    	}
    }

}
