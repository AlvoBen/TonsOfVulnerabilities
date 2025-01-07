package com.sap.engine.services.log_configurator.admin;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.engine.lib.logging.descriptors.LogControllerDescriptor;
import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.engine.lib.logging.descriptors.LogFormatterDescriptor;
import com.sap.tc.logging.*;

/**   
 * 
 * @author konstantin-m
 *
 * This class substituites LogXMLParser in that part, where it reads from a XML file. 
 * Instead, this class reads from a DB Configurations and creates a LogConfiguration
 * object, which has to be used in  com.sap.engine.logging.lib.LogConfigurationUpdater.update() 
 * method.In this update is received LogConfiguration object and is adjusted logging API settings.
 * 
 * This class is used when resetting to the default log-configuration settings.
 */
public class DbParser { //$JL-SEVERITY_TEST$
  
  private final static String PROPERTIES_NAME = "properties";
    
  private static Configuration fConfiguration;
  private static LogConfiguration fLogConfiguration;
  private static Location fLocation;

  /**
   * Main method, called to produce LogDescriptor object from DB settings  
   * @param configuration
   * @return
   */
  public static LogConfiguration parseDBConfiguration(Configuration configuration) {
    Location location = Location.getLocation(DbParser.class);
    
    if(configuration == null) {
        // $JL-SEVERITY_TEST$
        location.debugT("Error : Log-configuration : Configuration object was null. Default hardcoded log configuration wil obtained.");
        return null; //dont throw any exception - error will be found in LogManager while trying to process the configuration
    }

		fLogConfiguration = new LogConfiguration();
		fConfiguration = configuration;
		fLocation = location;
		
		loadFormatters();
		loadDestinations();
		loadControllers();
    
    return fLogConfiguration;
  }

  /**
   * Replaces slashes "\" in Category name with original backslashes "/"
   * @param original
   * @return
   */
  private static String replaceBackslashes(String original) {
    if(original.indexOf('\\') != -1) {
      StringBuffer buffer = new StringBuffer(original);
      for(int i = 0; i < buffer.length(); i++) { // faster than indexOf() and replace()
        if(buffer.charAt(i) == '\\') {
          buffer.setCharAt(i, '/');
        }
      }
      return buffer.toString();
    } else if("*".equals(original)) {
      return "";
    } else {
      return original;
    }
  }


	/**
   * Replaces slashes "/" in Category name with backslashes "\"
   * 
   * @param original
   * @return
   */
	public static String convertName(String original) {
		if (original.indexOf('/') != -1) {
			StringBuilder buffer = new StringBuilder(original);
			for (int i = 0; i < buffer.length(); i++) { //it is faster than indexOf() + replace()
				if (buffer.charAt(i) == '/') {
					buffer.setCharAt(i, '\\');
				}
			}
			return buffer.toString();
		} else if ("".equals(original)) {
			return "*";// for the root location
		} else {
			return original;
		}
	}
	
	private static void loadFormatters() {
		/*
		 * Loading Formatters
		 */
		LogFormatterDescriptor currentFormatter;
				 
		try {
			Configuration allFormatters = null;
			allFormatters = fConfiguration.getSubConfiguration("log-formatters");
			String[] formatterNames = allFormatters.getAllSubConfigurationNames();

			for (int i = 0; i < formatterNames.length; i++) {
				// $JL-SEVERITY_TEST$				
				currentFormatter = new LogFormatterDescriptor();
				Configuration currentConfigFormatter = null;
				try {
					currentConfigFormatter = allFormatters.getSubConfiguration(formatterNames[i]);
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Formatter with name " + formatterNames[i] + " not found in configuration :  " + e.getMessage());
					continue; // since we haven't found this  formatter we continue on the next one
				}

				PropertySheet ps = null;
				try {
					Configuration propertySheet = currentConfigFormatter.getSubConfiguration(PROPERTIES_NAME);
					ps = propertySheet.getPropertySheetInterface();
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Property sheet not found for formatter with name " + formatterNames[i]);
				}

				//name
				PropertyEntry pe = null;
				String name = null;
				try {
					pe = ps.getPropertyEntry("name");
					name = (String)pe.getValue();
					if (name == null) {
						// $JL-SEVERITY_TEST$						
						fLocation.debugT("  Formatter without name!");
					} else {
						currentFormatter.setName(name);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Formatter without name " + e.getMessage());
					continue; // it doesn't make sense to have
				}

				//type
				try {
					pe = ps.getPropertyEntry("type");
					String type = (String)pe.getValue();
					if (name == null) {
						// $JL-SEVERITY_TEST$						
						fLocation.debugT("  Formatter '" + name + "' have not type!");
					} else {
						currentFormatter.setType(type);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Formatter " + name + "  without type : " + e.getMessage());
				}

				//pattern
				try {
					pe = ps.getPropertyEntry("pattern");
					String pattern = (String)pe.getValue();
					currentFormatter.setPattern(pattern);
					if (currentFormatter.getType().equalsIgnoreCase("TraceFormatter") && currentFormatter.getPattern() == null) {
						currentFormatter.setPattern(LogFormatterDescriptor.DEFAULT_TRACEFORMATTER_PATTERN);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Formatter " + name + "  without pattern : " + e.getMessage());
				}
				fLogConfiguration.addLogFormatter(currentFormatter);
			}
		} catch (ConfigurationException e) {
			// $JL-EXC$			
			fLocation.debugT("Formatters were not found in initial configuration ");
		}
	}

  
	private static void loadDestinations() {
		/*
		 * Loading Destinations
		 */
		LogDestinationDescriptor currentDestination;
				 
		try {
			Configuration allDestinations = fConfiguration.getSubConfiguration("log-destinations");
			String[] destinationNames = allDestinations.getAllSubConfigurationNames();
			for (int i = 0; i < destinationNames.length; i++) {
				// $JL-SEVERITY_TEST$				
				//create Destination Descriptor for each configuration entry
				currentDestination = new LogDestinationDescriptor();

				PropertySheet ps = null;
				try {
					Configuration currentConfigDestination = allDestinations.getSubConfiguration(destinationNames[i]);
					Configuration propertySheet = currentConfigDestination.getSubConfiguration(PROPERTIES_NAME);
					ps = propertySheet.getPropertySheetInterface();
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + destinationNames[i] + "' not found in configuration !");
					continue; //it might happen if someone has deleted on runtime the configuration node; since we haven't found  it continue for the next one
				}

				//name
				PropertyEntry pe = null;
				String name = null;
				try {
					pe = ps.getPropertyEntry("name");
					name = (String)pe.getValue();
					if (name == null) {
						// $JL-SEVERITY_TEST$						
						fLocation.debugT("Destination without name!");
					} else {
						currentDestination.setName(name);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination without name");
					continue;
				}

				//type
				try {
					pe = ps.getPropertyEntry("type");
					String type = (String)pe.getValue();
					if (type == null) {
						// $JL-SEVERITY_TEST$						
						fLocation.debugT("Destination '" + name + "'doesn't have type!");
					} else {
						currentDestination.setType(type);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "'is without type");
				}

				//pattern
				try {
					pe = ps.getPropertyEntry("pattern");
					String pattern = (String)pe.getValue();
					currentDestination.setPattern(pattern);
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "' without pattern");
				}

				//severity
				try {
					pe = ps.getPropertyEntry("effective-severity");
					String effectiveSeverity = (String)pe.getValue();
					try {
						if (effectiveSeverity != null) {
							currentDestination.setEffectiveSeverity(Severity.parse(effectiveSeverity));
						}
					} catch (IllegalArgumentException e) {
						// $JL-EXC$						
						fLocation.debugT("Invalid value for effective severity '" + effectiveSeverity + "'!");
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "' without effective-severity");
				}

				//encoding
				try {
					pe = ps.getPropertyEntry("encoding");
					String encoding = (String)pe.getValue();
					currentDestination.setPattern(encoding);
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "' without encoding");
				}

				//count
				try {
					pe = ps.getPropertyEntry("count");
					String intCount = (String)pe.getValue();
					if (intCount != null) {
						try {
							currentDestination.setCount(Integer.parseInt(intCount));
						} catch (Exception e) {
							// $JL-EXC$							
							fLocation.debugT("Invalid integer value '" + intCount + "' for count in log destination '" + name + "'!");
						}
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "' without count");
				}

				//limit
				try {
					pe = ps.getPropertyEntry("limit");
					String intLimit = (String)pe.getValue();
					if (intLimit != null) {
						try {
							currentDestination.setLimit(Integer.parseInt(intLimit));
						} catch (Exception e) {
							// $JL-EXC$							
							fLocation.debugT("Invalid integer value '" + intLimit + "' for limit in log destination '" + name + "'!");
						}
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Destination with name '" + name + "' without limit");
				}
				fLogConfiguration.addLogDestination(currentDestination);
			}
		} catch (ConfigurationException e) {
			// $JL-EXC$			
			fLocation.debugT("Destinations not found in Configuration structure ! ");
		}		
	}
	
	private static void loadControllers() {
		/*
		 * Loading Controllers
		 */
		try {
			Configuration allControllers = fConfiguration.getSubConfiguration("log-controllers");
			String[] controllerNames = allControllers.getAllSubConfigurationNames();
			LogControllerDescriptor controllerDescriptor;
			for (int i = 0; i < controllerNames.length; i++) {
				// $JL-SEVERITY_TEST$				
				controllerDescriptor = new LogControllerDescriptor();

				Configuration currentConfigController = null;
				Configuration propertySheet = null;
				PropertySheet ps = null;
				try {
					currentConfigController = allControllers.getSubConfiguration(controllerNames[i]);
					propertySheet = currentConfigController.getSubConfiguration(PROPERTIES_NAME);
					ps = propertySheet.getPropertySheetInterface();
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Log-controller  with name " + controllerNames[i] + " not found in Configuration structure");
				}

				//Name
				PropertyEntry pe = null;
				String name = null;
				try {
					pe = ps.getPropertyEntry("name");
					name = replaceBackslashes((String)pe.getValue());

					if (name == null) {
						// $JL-SEVERITY_TEST$						
						fLocation.debugT("Controller without name!");
					} else {
						controllerDescriptor.setName(name);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Log-controller  " + controllerNames[i] + " doesn't have property 'name'");
					controllerDescriptor.setName(controllerNames[i]);
				}

				//Severity
				try {
					pe = ps.getPropertyEntry("effective-severity");
					String effectiveSeverity = (String)pe.getValue();
					controllerDescriptor.setEffectiveSeverity(effectiveSeverity);
				} catch (ConfigurationException e) {
					// $JL-EXC$ $JL-SEVERITY_TEST$				
					fLocation.debugT("For log-controller " + controllerNames[i] + "  effective severity not found. Now using min and max severities ... ");
					try {
						pe = ps.getPropertyEntry("minimum-severity");
						String minSeverity = (String)pe.getValue();
						controllerDescriptor.setMinSeverity(minSeverity);

						pe = ps.getPropertyEntry("maximum-severity");
						String maxSeverity = (String)pe.getValue();
						controllerDescriptor.setMaxSeverity(maxSeverity);
					} catch (ConfigurationException e1) {
						// $JL-EXC$						
						fLocation.debugT("For log-controller " + controllerNames[i] + " not found  min and max severities !");
					}
				}

        try {
          controllerDescriptor.setCopyToSubtree(
            Boolean.parseBoolean(
              (String) ps.getPropertyEntry("copy-to-subtree").getValue()));
        } catch(Exception e) {
          fLocation.traceThrowableT(
            Severity.DEBUG, 
            "Log-controller " + controllerNames[i] + " without copy-to-subtree", 
            e);
          controllerDescriptor.setCopyToSubtree(true);
        }

				//Bundle name
				try {
					pe = ps.getPropertyEntry("bundle-name");
					if (pe != null) {
						String bundleName = (String)pe.getValue();
						controllerDescriptor.setBundleName(bundleName);
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("For log-controller " + controllerNames[i] + " not found  min and max severities !");
					// - do nothing currently - there are no plans to support resource bundles
				}

				/*
				 * Retrieve "destination-ref" -s for current controller.
				 * They are sub-configuration of the current controler.
				 */
				try {
					Configuration allDestinationRefs = currentConfigController.getSubConfiguration("destination-refs");
					String[] destRefsNames = allDestinationRefs.getAllSubConfigurationNames();
					for (int j = 0; j < destRefsNames.length; j++) {
						try {
							Configuration currentConfigDestRef = allDestinationRefs.getSubConfiguration(destRefsNames[j]);
							propertySheet = currentConfigDestRef.getSubConfiguration(PROPERTIES_NAME);
							ps = propertySheet.getPropertySheetInterface();
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT(" Destination " + destRefsNames[j] + " not found configuration tree ");
							continue; // If we don't find  such configuration - it means that someone has deleted it -
							// -  it makes no  sense to extract its properties -> just continue to the next one
						}

						//Name
						try {
							pe = ps.getPropertyEntry("name");
							name = (String)pe.getValue();
							if (name == null) {
								// $JL-SEVERITY_TEST$								
								fLocation.debugT("Invalid destination reference - missing name!");
							}
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Name not found in properties of destination " + destRefsNames[j]);
							name = destRefsNames[j];
						}

						//Type
						String type = null;
						try {
							pe = ps.getPropertyEntry("association-type");
							type = (String)pe.getValue();
							if (type == null) {
								// $JL-SEVERITY_TEST$								
								fLocation.debugT("Log-configuration  Invalid destination reference - missing association type!");
							}
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Type not found in properties of destination " + destRefsNames[j]);
						}

						//Find the appropriate destination with this name
						LogDestinationDescriptor destination = fLogConfiguration.getLogDestination(name);
						if (type.equals("PRIVATE_LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_PRIVATE);
						} else if (type.equals("LOCAL_LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_LOCAL);
						} else if (type.equals("LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_PUBLIC);
						} else {
							// $JL-SEVERITY_TEST$							
							fLocation.debugT("Invalid association type '" + type + "' for destination reference '" + name + "'!");
						}

						//If this destination wasn't found in the collection, the reference is invalid
						if (destination == null) {
							// $JL-SEVERITY_TEST$							
							fLocation.debugT("Log-configuration  Invalid destination reference '" + name + "'!");
						}
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Not found destination-refs in configuration ");
				}

				/*
				 * Processing anonymous destinations -
				 * Problem - do they have unique ID( name, type, etc)? - to create unique sub-configuration
				 */
				try {
					Configuration allAnonymousDestinations = currentConfigController.getSubConfiguration("anonymous-destinations");
					String[] anonymousDestNames = allAnonymousDestinations.getAllSubConfigurationNames();

					//Iterate through array of anonymous destinations for each controller
					for (int j = 0; j < anonymousDestNames.length; j++) {
						Configuration currentAnonymousDestination = null;
						try {
							currentAnonymousDestination = allAnonymousDestinations.getSubConfiguration(anonymousDestNames[j]);
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Not found anonymous destination" + anonymousDestNames[j]);
							continue;
						}

						try {
							propertySheet = currentAnonymousDestination.getSubConfiguration(PROPERTIES_NAME);
							ps = propertySheet.getPropertySheetInterface();
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Property sheet not found for anonymous destination " + anonymousDestNames[j] + " - " + e.getMessage());
						}

						//Type
						String type = null;
						try {
							pe = ps.getPropertyEntry("type");
							type = (String)pe.getValue();
							if (name == null) {
								// $JL-SEVERITY_TEST$								
								fLocation.debugT("Log-configuration  Invalid anonymous destination - missing type!");
							}
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Invalid type for anonymous destination");
						}

						//Association-type
						String associationType = null;
						try {
							pe = ps.getPropertyEntry("association-type");
							associationType = (String)pe.getValue();
							if (associationType == null) {
								// $JL-SEVERITY_TEST$								
								fLocation.debugT("Log-configuration  Invalid anonymous destination - missing association type!");
							}
						} catch (ConfigurationException e) {
							// $JL-EXC$							
							fLocation.debugT("Invalid association type for anonymous destination");
						}

						LogDestinationDescriptor destination = new LogDestinationDescriptor();
						destination.setType(type);
						if (associationType.equals("PRIVATE_LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_PRIVATE);
						} else if (associationType.equals("LOCAL_LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_LOCAL);
						} else if (associationType.equals("LOG")) {
							controllerDescriptor.addDestination(destination, LogControllerDescriptor.ASSOCIATION_TYPE_PUBLIC);
						} else {
							// $JL-SEVERITY_TEST$							
							fLocation.debugT("Log-configuration  Invalid association type '" + associationType + "' for anonymous destination!");
						}
					}
				} catch (ConfigurationException e) {
					// $JL-EXC$					
					fLocation.debugT("Invalid association type for anonymous destination");
				}

				fLogConfiguration.addLogController(controllerDescriptor);
			}
		} catch (ConfigurationException e) {
			// $JL-EXC$			
			fLocation.debugT("CONTROLLERS NOT FOUND IN DB CONFIGURATIONS !!");
		} catch (IllegalArgumentException e) {
			// $JL-EXC$			
			fLocation.debugT(e.getMessage());
		}		
	}
}
