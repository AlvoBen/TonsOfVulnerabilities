package com.sap.security.core.server.ume.service.configdiagtool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.security.core.util.config.UMConfigurationException;
import com.sap.security.core.util.imp.ByteArrayComparator;

/**
 * Utility class to get all UME configuration data in a ZIP file.
 * 
 * <p>
 * This class is used by
 * {@link com.sap.security.core.server.ume.service.SAPJ2EEConfiguration#downloadConfiguration()}
 * at runtime as well as by{@link com.sap.security.core.server.ume.service.configdiagtool.offline.ConfigDiagTool}
 * for standalone usage (if AS Java cannot start). The UME configuration properties file contains
 * information about the "custom_global" configuration level as well as differences on all instances
 * and, if the argument "runtimeProperties" is provided (at AS Java runtime), the differences to the
 * properties set that is currently available at runtime (there might be differences for properties
 * which are not "onlinemodifiable", but have been changed after the last server restart).
 * </p>
 */
public class ConfigOverview {

	private static final String LINE_SEPARATOR      = System.getProperty("line.separator");
    private static final char   FILE_PATH_SEPARATOR = '/';
    private static final String EMPTY_STRING        = "";

	private Properties              _runtimeProperties;
	private Map<String, Property>   _properties;
	private Map<String, ConfigFile> _configFiles;
	private String[]                _instanceIDs;
	private String                  _currentInstanceID;

	private ConfigOverview() { /* Must not be used. */ }

	/**
	 * Create a new {@link ConfigOverview} instances, which includes reading all
	 * necessary configuration data.
	 * 
     * @param configHandler The configuration handler that should be used to read
     *        configuration data from the configuration manager.
     * @param runtimeProperties Set of properties that are currently used at runtime.
     *        May be <code>null</code> if used standalone (outside of AS Java), in that
     *        case the ZIP file does not contain any information about the properties
     *        that are currently used at runtime.
	 * @throws UMConfigurationException
	 */
	public ConfigOverview(ConfigurationHandler configHandler, Properties runtimeProperties)
	throws UMConfigurationException {
		// Remember runtime properties.
		_runtimeProperties = runtimeProperties;

		// Collect all necessary data from the configuration manager.
    	try {
           	// 1) Collect information from "custom_global" configuration level.
    		{
            	Configuration customGlobalUMEConfig = configHandler.openConfiguration(
                    "cluster_config/system/custom_global/cfg/services/com.sap.security.core.ume.service",
                   	ConfigurationHandler.READ_ACCESS);

               	// 1.1) Properties
            	{
            		Configuration propertiesConfig  = customGlobalUMEConfig.getSubConfiguration("properties");
            		PropertySheet propertySheet     = propertiesConfig.getPropertySheetInterface();
            		PropertyEntry[] propertyEntries = propertySheet.getAllPropertyEntries();

            		_properties = new HashMap<String, Property>(propertyEntries.length);

            		for(int i = 0; i < propertyEntries.length; i++) {
            			PropertyEntry currentPropertyEntry = propertyEntries[i];
            			String        currentPropertyName  = currentPropertyEntry.getName();

            			Property currentProperty = new Property(currentPropertyName, currentPropertyEntry.isSecure());
            			currentProperty.setGlobalData(new PropertyData(currentPropertyEntry));
            			_properties.put(currentPropertyName, currentProperty);
            		}
            	}

            	// 1.2) Configuration files
        		{
            		Configuration filesConfig  = customGlobalUMEConfig.getSubConfiguration("persistent");

                    // Prepare map for configuration files.
            		_configFiles = new HashMap<String, ConfigFile>(filesConfig.getAllFileEntryNames().length);

                    // Add configuration files from local configuration and all subconfigurations.
                    readConfigFilesRecursively(_configFiles, null, "", filesConfig);
        		}
    		}

        	// 2) Collect information from instance configurations.
    		{
               	Configuration instancesConfig = configHandler.openConfiguration(
                    "cluster_config/system/instances", ConfigurationHandler.READ_ACCESS);

           		// Iterate over all instances.
               	{
                   	Set<String> instanceIDs = new HashSet<String>(instancesConfig.getAllSubConfigurationNames().length);

            		Map instanceConfigurations = instancesConfig.getAllSubConfigurations();
            		Iterator instanceConfigIterator = instanceConfigurations.entrySet().iterator();
            		while(instanceConfigIterator.hasNext()) {
            			Map.Entry   currentInstanceEntry    = (Map.Entry  )   instanceConfigIterator.next();
            			String      currentInstanceID       = (String     )   currentInstanceEntry.getKey();
            			Configuration currentInstanceConfig = (Configuration) currentInstanceEntry.getValue();

            			// The "instances" configuration contains
            			// 1) subconfigurations for all instances and
            			// 2) a subconfiguration "current_instance" pointing to the instance configuration
            			//    of the instance running this code
            			// The "current_instance" configuration does not contain any data, but is linked
            			// to the configuration of the instance the code runs on using a parameterized link.
            			// Obviously, this link only works in a running AS Java, but not using offline
            			// configuration access, so the "current_instance" configuration is ignored in the
            			// offline scenario. In the online scenario, on the other hand, it is only used to
            			// determine the ID of the current instance, which allows to highlight that instance
            			// in the configuration data being downloaded.
            			if(currentInstanceID.equals("current_instance")) {
            				if(runtimeProperties != null) {
                				// Determine the ID of the current instance.
                				if((currentInstanceConfig.getConfigurationType() & Configuration.CONFIG_TYPE_DERIVED) != 0) {
                					DerivedConfiguration derivedConfig = (DerivedConfiguration) currentInstanceConfig;
                					String linkedInstanceConfig = derivedConfig.getLink();
                					_currentInstanceID = linkedInstanceConfig.substring(linkedInstanceConfig.lastIndexOf('/') + 1);
                				}
            				}
            			}
            			else {
                			instanceIDs.add(currentInstanceID);

                			Configuration currentInstanceUMEConfig = currentInstanceConfig.getSubConfiguration(
                				"cfg/services/com.sap.security.core.ume.service");

                			// 2.1) Properties
            	        	{
            	        		Configuration propertiesConfig  = currentInstanceUMEConfig.getSubConfiguration("properties");
            	        		PropertySheet propertySheet     = propertiesConfig.getPropertySheetInterface();
            	        		PropertyEntry[] propertyEntries = propertySheet.getAllPropertyEntries();
            	        		for(int i = 0; i < propertyEntries.length; i++) {
            	        			PropertyEntry currentPropertyEntry = propertyEntries[i];
            	        			String        currentPropertyName  = currentPropertyEntry.getName();

            	        			// Maybe the property does not exist on the custom global level and
            	        			// the previous instance levels, so make sure there's a container
            	        			// for diagnostics.
            	        			Property currentProperty = _properties.get(currentPropertyName);
            	        			if(currentProperty == null) {
            	        				currentProperty = new Property(currentPropertyName, currentPropertyEntry.isSecure());
            	        				_properties.put(currentPropertyName, currentProperty);
            	        			}

            	        			currentProperty.setDataForInstance(currentInstanceID, new PropertyData(currentPropertyEntry));
            	        		}
            	        	}

            	        	// 2.2) Configuration files
            	    		{
                        		Configuration filesConfig  = currentInstanceUMEConfig.getSubConfiguration("persistent");

                                // Add configuration files from local configuration and all subconfigurations.
                                readConfigFilesRecursively(_configFiles, currentInstanceID, "", filesConfig);
            	    		}
            			}
            		}

            		_instanceIDs = instanceIDs.toArray(new String[instanceIDs.size()]);
            		Arrays.sort(_instanceIDs);
               	}
    		}
    	}
    	catch(ConfigurationException e) {
    		throw new UMConfigurationException("An error occured while reading configuration data.", e);
    	}
    	catch(IOException e) {
    		throw new UMConfigurationException("An error occured while reading configuration data.", e);
    	}
	}

	/**
	 * Evaluate all configuration data and create a ZIP file containing all results
	 * for SAP support.
	 * 
	 * @return The configuration ZIP file as byte array.
	 * @throws IOException
	 */
	public byte[] downloadConfiguration() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ZipOutputStream zipStream = new ZipOutputStream(byteStream);

		// Preparations for several steps.
		String[] propertyNames = _properties.keySet().toArray(new String[_properties.size()]);
		Arrays.sort(propertyNames);

		// 1) Iterate over all properties and create an overview document about them.
		//
		//    The document is created as a table with the following columns:
		//    1. General
		//       a) Property name
		//    2. "custom_global"
		//       b) Property value
		//       c) Property's "custom" status
		//       d) Property's "final" flag
		//       e) Property's "onlinemodifiable" flag
		//    3. Current instance (+ instance ID)
		//       f) Property value from configuration manager (difference)
		//       g) At runtime only: Property value at runtime (difference)
		//    4. Instance 1 (not current instance)
		//       h) Property status (identical, different, missing, ...)
		//       i) Property value, if different
		//    5. Instance 2 (not current instance)
		//       j) Property status (identical, different, missing, ...)
		//       k) Property value, if different
		//    ...
		//    x. x) Remarks (e.g. instance value differing from "custom_global" although
		//          the "final" flag is set on the "custom_global" level; runtime value
		//          differs from persisted value in the configuration manager)
		{
			// Create overview file.
			Table propertiesOverview = new Table();

			// Write table header...
			// ... top level -> header row 1.
			propertiesOverview.addCells(new String[] {
				"General", "Global", "", "", "" } );
			if(_runtimeProperties != null) {
				propertiesOverview.addCells(new String[] {
					"Current instance (" + _currentInstanceID + ")", "", "", "" } );
			}
			for(int i = 0; i < _instanceIDs.length; i++) {
				String currentInstance = _instanceIDs[i];
				// Skip the current (=local) instance because it has already been
				// handled in the previous block.
				if(! currentInstance.equals(_currentInstanceID)) {
					propertiesOverview.addCells(new String[] { "Instance " + currentInstance, "" });
				}
			}
			propertiesOverview.addCell("Additional");
			propertiesOverview.nextRow();
			// ... header row 2.
			propertiesOverview.addCells(new String[] {
				"Property", "Value", "Custom?", "Final?", "Onlinemod.?" } );
			if(_runtimeProperties != null) {
				propertiesOverview.addCells(new String[] {
					"Persistent Status", "Pers. Diff. Value", "Runtime Status", "Runtime Diff. Value" } );
			}
			for(int i = 0; i < _instanceIDs.length; i++) {
				String currentInstance = _instanceIDs[i];
				// Skip the current (=local) instance because it has already been
				// handled in the previous block.
				if(! currentInstance.equals(_currentInstanceID)) {
					propertiesOverview.addCells(new String[] { "Status", "Diff. Value" } );
				}
			}
			propertiesOverview.addCell("Remarks");
			propertiesOverview.nextRow();

			// Iterate over all properties.
			for(int i = 0; i < propertyNames.length; i++) {
				String currentPropertyName = propertyNames[i];
				Property currentProperty = _properties.get(currentPropertyName);

				// Add general information about the property to the table.
				propertiesOverview.addCell(currentPropertyName);

				// Prepare additional remarks about the property.
				List<String> remarks = new ArrayList<String>(0);

				// Global configuration.
				PropertyData currentGlobalData = currentProperty.getGlobalData();
				Object       currentGlobalValue;
				{
					String globalValueText;
					String globalCustomStatusText;
					String globalFinalFlagText;
					String globalOnlinemodifiableFlagText;
					if(currentGlobalData != null) {
						currentGlobalValue = currentGlobalData.getValue();

						if(isPropertyValueEmpty(currentGlobalValue)) {
							globalValueText = "(Empty)";
						}
						else {
							// Don't write secure values into the overview table!
							if(currentProperty.isSecure()) {
								globalValueText = "(Secure)";
							}
							else {
								globalValueText = currentGlobalValue.toString();
							}
						}

						globalCustomStatusText         = Boolean.toString(currentGlobalData.isCustom          ());
						globalFinalFlagText            = Boolean.toString(currentGlobalData.isFinal           ());
						globalOnlinemodifiableFlagText = Boolean.toString(currentGlobalData.isOnlineModifiable());
					}
					else {
						currentGlobalValue             = null;

						globalValueText                = "(Missing)";
						globalCustomStatusText         = "(n/a)";
						globalFinalFlagText            = "(n/a)";
						globalOnlinemodifiableFlagText = "(n/a)";
					}

					// Add status information for the global level.
					propertiesOverview.addCell(globalValueText               );
					propertiesOverview.addCell(globalCustomStatusText        );
					propertiesOverview.addCell(globalFinalFlagText           );
					propertiesOverview.addCell(globalOnlinemodifiableFlagText);
				}

				// If available: Runtime properties.
				if(_runtimeProperties != null) {
					// Get property data for persistent data in the current instance.
					PropertyData currentInstanceData = currentProperty.getDataForInstance(_currentInstanceID);

					// Compare the persistent value for the instance against the global value.
					addInstanceCells(propertiesOverview, remarks, _currentInstanceID, currentProperty, 
						currentGlobalData, currentInstanceData, false);

					// Get property data for runtime data in the current instance.
					String currentRuntimeValue = _runtimeProperties.getProperty(currentPropertyName);
					PropertyData currentRuntimeData = new PropertyData(currentRuntimeValue);

					// Compare the runtime value for the instance against the persistent value for the instance.
					addInstanceCells(propertiesOverview, remarks, null, currentProperty, currentInstanceData,
						currentRuntimeData, true);
				}

				// Instance specific configuration.
				{
					// Check the property's status for each instance.
					for(int j = 0; j < _instanceIDs.length; j++) {
						String currentInstanceID = _instanceIDs[j];

						// Skip the current (=local) instance because it has already been
						// handled in the previous block.
						if(! currentInstanceID.equals(_currentInstanceID)) {
							PropertyData currentInstanceData = currentProperty.getDataForInstance(currentInstanceID);

							addInstanceCells(propertiesOverview, remarks, currentInstanceID, currentProperty,
								currentGlobalData, currentInstanceData, false);
						}
					}
				}

				// Add additional remarks about the property, if existing.
				propertiesOverview.addMultiLineCell(remarks);

				// Next file -> next row.
				propertiesOverview.nextRow();
			}

			// Generate overview file for configuration files and add it to the ZIP file.
			ZipEntry currentZipEntry = new ZipEntry("OverviewProperties.csv");
			zipStream.putNextEntry(currentZipEntry);
			Writer zipWriter = new OutputStreamWriter(zipStream, "UTF-8");
			propertiesOverview.writeAsCSV(zipWriter);
			zipWriter.flush();
			zipStream.closeEntry();
		}

		// 2) Iterate over all configuration files.
		//    Put all global files into the ZIP file. Additionally, put all files into the ZIP
		//    file which either differ from the global files or exist only in some instance
		//    configuration. Finally create an overview document about all differences between
		//    configuration files on the "custom_global" levels and all instances.
		//
		//    The overview document is created as a table with the following columns:
		//    a) File name
		//    b) First instance: Information about whether the file is equal, different, missing
		//       or only available in this instance's configuration
		//    c) Second instance: ...
		//    ...
		{
			// Prepare comparator for configuration file differences.
			ByteArrayComparator fileComparator = new ByteArrayComparator();

			// Create overview file.
			Table filesOverview = new Table();

			// Write table header.
			filesOverview.addCell("File Name");
			filesOverview.addCell("Global Configuration");
			for(int i = 0; i < _instanceIDs.length; i++) {
				filesOverview.addCell("Instance " + _instanceIDs[i]);
			}
			filesOverview.nextRow();

			String[] configFileNames = _configFiles.keySet().toArray(new String[_configFiles.size()]);
			Arrays.sort(configFileNames);
			for(int i = 0; i < configFileNames.length; i++) {
				String currentFileName = configFileNames[i];
				ConfigFile currentConfigFile = _configFiles.get(currentFileName);

				filesOverview.addCell(currentFileName);

				// Global configuration.
				// If the file exists on the "custom_global" level, put its global data into
				// the "global" directory of the ZIP file.
				byte[] currentGlobalData = currentConfigFile.getGlobalData();
				{
					String globalStatus;
					if(currentGlobalData != null) {
						globalStatus = "Available";

                        String currentZipPath = "global" + FILE_PATH_SEPARATOR + currentFileName;
						ZipEntry currentZipEntry = new ZipEntry(currentZipPath);
						zipStream.putNextEntry(currentZipEntry);
						zipStream.write(currentGlobalData);
						zipStream.closeEntry();
					}
					else {
						globalStatus = "Not available";
					}

					// Add status information for the global level.
					filesOverview.addCell(globalStatus);
				}

				// Instance specific configuration.
				{
					// Check the file's status for each instance.
					for(int j = 0; j < _instanceIDs.length; j++) {
						String currentInstanceID = _instanceIDs[j];
						byte[] currentInstanceData = currentConfigFile.getDataForInstance(currentInstanceID);

						// Decide whether the file needs to be put into the configuration ZIP file or not.
						boolean addToZIP;

						// Does the file exist in the current instance's configuration?
						String currentFileStatus;
						if(currentInstanceData == null) {
							currentFileStatus = "Missing in instance";
							addToZIP          = false;
						}
						else {
							// Does it exist for the instance, but not on the global level?
							if(currentGlobalData == null) {
								currentFileStatus = "Only in instance";
								addToZIP          = true;
							}
							else {
								// Is it identical to the file on the global level?
								if(fileComparator.compare(currentGlobalData, currentInstanceData) == 0) {
									currentFileStatus = "Identical to global";
									addToZIP          = false;
								}
								else {
									currentFileStatus = "Different from global";
									addToZIP          = true;
								}
							}
						}

						// Add status information for the current instance.
						filesOverview.addCell(currentFileStatus);

						// Add the file's data for the current instance, if necessary.
						if(addToZIP) {
			    			// Put the configuration file into the ZIP file (subdirectory "instances/<INSTANCE_NAME>").
                            String currentZipPath =
                                "instances" + FILE_PATH_SEPARATOR + currentInstanceID + FILE_PATH_SEPARATOR+ currentFileName;
			    			ZipEntry currentZipEntry = new ZipEntry(currentZipPath);
			    			zipStream.putNextEntry(currentZipEntry);
			    			zipStream.write(currentInstanceData);
			    			zipStream.closeEntry();
						}
					}
				}

				// Next file -> next row.
				filesOverview.nextRow();
			}

			// Generate overview file for configuration files and add it to the ZIP file.
			ZipEntry currentZipEntry = new ZipEntry("OverviewConfigurationFiles.csv");
			zipStream.putNextEntry(currentZipEntry);
			Writer zipWriter = new OutputStreamWriter(zipStream, "UTF-8");
			filesOverview.writeAsCSV(zipWriter);
			zipWriter.flush();
			zipStream.closeEntry();
		}

		// 3) Iterate over all properties and generate a properties file containing the
		//    global configuration values.
		addPropertiesFile(zipStream, propertyNames, _properties, null);

		// 4) Iterate over all properties and generate a properties file containing the
		//    configuration values in the current instance (only in "online" mode).
		if(_currentInstanceID != null) {
			addPropertiesFile(zipStream, propertyNames, _properties, _currentInstanceID);
		}

		// Finalize the ZIP file.
		zipStream.flush();
		zipStream.close();
		byteStream.flush();
		byteStream.close();

		return byteStream.toByteArray();
	}

	private static void addPropertiesFile(ZipOutputStream zipStream, String[] propertyNames,
	Map<String, Property> properties, String instanceID)
	throws IOException {
		StringBuffer buffer = new StringBuffer(16 * 1024); // Initial buffer size of 16k characters

		// Generate properties file header.
		String title;
		if(instanceID == null) {
			title = "UME global configuration properties";
		}
		else {
			title = "UME configuration properties in current instance " + instanceID;
		}
		
		buffer.append("# "      ).append(title                ).append(LINE_SEPARATOR)
		      .append("# Date: ").append(new Date().toString()).append(LINE_SEPARATOR)
		      .append(LINE_SEPARATOR);

		// Iterate over all properties.
		for(int i = 0; i < propertyNames.length; i++) {
			String currentPropertyName = propertyNames[i];
			Property currentProperty = properties.get(currentPropertyName);

			PropertyData currentPropertyData;
			if(instanceID == null) {
				currentPropertyData = currentProperty.getGlobalData();
			}
			else {
				currentPropertyData = currentProperty.getDataForInstance(instanceID);
			}

			if(currentPropertyData != null) {
				Object currentValue = currentPropertyData.getValue();
				String currentDisplayValue = null;
				if(currentValue != null && ! currentValue.equals(EMPTY_STRING)) {
					if(currentProperty.isSecure()) {
						currentDisplayValue = "********";
					}
					else {
						currentDisplayValue = currentValue.toString();
					}
				}
				else {
					currentDisplayValue = EMPTY_STRING;
				}

				buffer.append(currentPropertyName).append('=').append(currentDisplayValue)
				      .append(LINE_SEPARATOR).append(LINE_SEPARATOR);
			}
		}

		// Generate overview file for configuration files and add it to the ZIP file.
		String propertiesFileName;
		if(instanceID == null) {
			propertiesFileName = "sapum-global.properties";
		}
		else {
			propertiesFileName = "sapum-instance-" + instanceID + ".properties";
		}

		ZipEntry currentZipEntry = new ZipEntry(propertiesFileName);
		zipStream.putNextEntry(currentZipEntry);
		Writer zipWriter = new OutputStreamWriter(zipStream, "UTF-8");
		zipWriter.write(buffer.toString());
		zipWriter.flush();
		zipStream.closeEntry();
	}

    /**
     * Read all configuration files from the specific configuration and all (direct
     * and indirect) subconfigurations and add them to the provided Map.
     * 
     * @param configFiles The Map to collect all configuration files in.
     * @param currentInstance The name of the current instance or <code>null</code> if
     *                        the files are read from the global level.
     * @param directoryPrefix The current "directory" of the configuration file (full
     *                        path starting from inside the "persistent" folder, ending
     *                        with '/' if not empty)
     * @param configuration The current subconfiguration to read configuration files
     *                      and further subconfigurations from.
     * @throws ConfigurationException
     * @throws IOException
     */
    private static void readConfigFilesRecursively(Map<String, ConfigFile> configFiles, String currentInstance,
    String directoryPrefix, Configuration configuration)
    throws ConfigurationException, IOException {
        Map      fileEntries       = configuration.getAllFileEntries();
        Iterator fileEntryIterator = fileEntries.entrySet().iterator();

        while(fileEntryIterator.hasNext()) {
            Map.Entry   currentFileEntry  = (Map.Entry  ) fileEntryIterator.next();
            String      currentFileName   = (String     ) currentFileEntry.getKey();
            InputStream currentFileStream = (InputStream) currentFileEntry.getValue();

            String currentFilePath = directoryPrefix + currentFileName;
            // Maybe the property does not exist on the custom global level and
            // the previous instance levels, so make sure there's a container
            // for diagnostics.
            ConfigFile currentConfigFile = configFiles.get(currentFilePath);
            if(currentConfigFile == null) {
                currentConfigFile = new ConfigFile(currentFileName);
                configFiles.put(currentFilePath, currentConfigFile);
            }

            // Set the read file content for the global level resp. the current instance,
            // depending on the context.
            if(currentInstance == null) {
                currentConfigFile.setGlobalData(currentFileStream);
            }
            else {
                currentConfigFile.setDataForInstance(currentInstance, currentFileStream);
            }
        }

        // Get files from potentially existing subconfigurations.
        Map subConfigs = configuration.getAllSubConfigurations();
        Iterator subConfigIterator = subConfigs.entrySet().iterator();

        while(subConfigIterator.hasNext()) {
            Map.Entry     currentSubConfigEntry = (Map.Entry)     subConfigIterator.next();
            String        currentSubConfigName  = (String)        currentSubConfigEntry.getKey();
            Configuration currentSubConfig      = (Configuration) currentSubConfigEntry.getValue();

            String currentDirectoryPrefix = directoryPrefix + currentSubConfigName + FILE_PATH_SEPARATOR;
            readConfigFilesRecursively(configFiles, currentInstance, currentDirectoryPrefix, currentSubConfig);
        }
    }

	/**
	 * Compare a property between a "local" and a "superordinate" level and add the
	 * comparison results to the OverviewTable.
	 * 
	 * @param propertiesOverview The OverviewTable table to add the results to.
         * @param remarks List of remarks for SAP support about the property; to be filled
         *        by this method, if relevant
         * @param currentInstanceName Name of the current instance, if appropriate
	 * @param property General information about the property
	 * @param superordinateData Level specific information about the property on the
	 *        "superordinate" level
	 * @param localData Level specific information about the property on the
	 *        "local" level
	 * @param isRuntimeNotPersistent Whether the "local" level applies to the
	 *        "runtime" properties of an instance (<code>true</code> as opposed to
	 *        <code>false</code> for the persistent properties of an instance)
	 */
	private void addInstanceCells(Table propertiesOverview, List<String> remarks, String currentInstanceName,
	Property property, PropertyData superordinateData, PropertyData localData, boolean isRuntimeNotPersistent) {
		// Prepare the table cells.
		List<String> localInformation = new ArrayList<String>();
		String localValueText;
		boolean isLocalValueRelevant;

		// Does the property exist in the local configuration?
		if(localData == null) {
			String infoText = isRuntimeNotPersistent ? "Missing at runtime" : "Missing in instance";
			localInformation.add(infoText);
			localValueText       = null;
			isLocalValueRelevant = false;
		}
		else {
			Object localValue = localData.getValue();

			// Compare the local property value to the superordinate value.
			// Does it exist locally, but not on the superordinate level?
			if(superordinateData == null) {
				String infoText = isRuntimeNotPersistent ? "Only at runtime" : "Only in instance" ;
				localInformation.add(infoText);
				isLocalValueRelevant = true;
			}
			else {
				// The property exists on the superordinate level, too.
				// Is the value identical on both levels?
				if(arePropertiesValueIdentical(superordinateData.getValue(), localValue)) {
					String infoText = isRuntimeNotPersistent ? "Identical to persistent" : "Identical to global";
					localInformation.add(infoText);
					isLocalValueRelevant = false;
				}
				else {
					String infoText = isRuntimeNotPersistent ? "Different from persistent" : "Different from global";
					localInformation.add(infoText);
					isLocalValueRelevant = true;

					// Check if the "final" flag has been set on the superordinate level,
					// but the local level has a different value anyway.
					// -> Only relevant for local level being instance(persistent) compared
					//    to the global level and if the property exists on both levels
					//    (-> true in this "else" block).
					if(! isRuntimeNotPersistent && superordinateData.isFinal()) {
						remarks.add(MessageFormat.format(
							"Instance {0} has a different value than the global level " +
							"although the property has the \"final\" flag set on the global " +
							"level.", new Object[] { currentInstanceName } ));
					}

					// Write a remark, too, if the runtime and persistent values of an instance differ
					// and the property is not online-modifiable.
					if(isRuntimeNotPersistent && ! superordinateData.isOnlineModifiable()) {
						remarks.add("The runtime and persistent values for the current instance differ " +
							"and the property does not have the \"onlinemodifiable\" flag. Note that it " +
							"is necessary to restart the server to apply changes to this property.");
					}
				}
			}

			// Determine whether to display the local value, "empty" or no value.
			if(isLocalValueRelevant) {
				// Is the local property empty or does it have a value?
				if(isPropertyValueEmpty(localValue)) {
					localInformation.add("empty");
					localValueText = null;
				}
				else {
					// Don't write secure values into the overview table!
					if(property.isSecure()) {
						localValueText = "(Secure)";
					}
					else {
						localValueText = localValue.toString();
					}
				}
			}
			else {
				localValueText = null;
			}
		}

		// Create the table cell value and add it to the table row.
		StringBuffer statusBuffer = new StringBuffer();
		statusBuffer.append('(');
		Iterator<String> currentLocalInfoIterator = localInformation.iterator();
		while(currentLocalInfoIterator.hasNext()) {
			statusBuffer.append(currentLocalInfoIterator.next());
			if(currentLocalInfoIterator.hasNext()) {
				statusBuffer.append(", ");
			}
		}
		statusBuffer.append(')');


		// Add status information for the local level.
		propertiesOverview.addCell(statusBuffer.toString());

		// Add the value, if relevant and available, or an empty cell.
		if(isLocalValueRelevant && localValueText != null) {
			propertiesOverview.addCell(localValueText);
		}
		else {
			propertiesOverview.addCell("");
		}
	}

	private static boolean isPropertyValueEmpty(Object value) {
		return (value == null || value.toString().equals(""));
	}

	private static boolean arePropertiesValueIdentical(Object value1, Object value2) {
		if(isPropertyValueEmpty(value1)) {
			return isPropertyValueEmpty(value2);
		}
		else {
			if(isPropertyValueEmpty(value2)) {
				return false;
			}
			else {
				return value1.toString().equals(value2.toString());
			}
		}
	}

}
