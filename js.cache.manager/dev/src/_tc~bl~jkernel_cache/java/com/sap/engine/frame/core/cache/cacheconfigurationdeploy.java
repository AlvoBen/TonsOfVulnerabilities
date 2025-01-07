/*
 * Created on 2005.3.10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.frame.core.cache;

import java.io.InputStream;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.util.cache.spi.storage.StoragePlugin;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CacheConfigurationDeploy {
  
  /**
   * Used to create the cache regions of a j2ee component (service or library only)
   * 
   * @param appName The name 
   * @param mRegions
   * @throws CacheContextException
   */
  public void createRegions(String appName, Map mRegions) throws CacheContextException;
  
  /**
   * Sets global configuration information for the cache management
   * 
   * @param mGlobal map containing the global configuration
   */
  public void setGlobalConfiguration(Map mGlobal);  
  
  /**
   * Deploys a cache-configuration.xml file which has to comply with cache-configuration.dtd
   * defined by <code>CacheXML</code> utility. 
   * 
   * @param appName The name of the application the caches are configured for
   * @param xmlStream An input stream containing XML data that will be parsed and deployed
   * @param configuration The configuration that will contain the property sheets for the regions described in the XML
   * 
   * @return An array of the deployed region names with "(appName):cache:" prepended
   * 
   * @throws ConfigurationException If the configuration does not exist or has no write access
   * @throws CacheContextException If the XML is not well formed or has corrupt data
   */
  public String[] deploy(String appName, InputStream xmlStream, Configuration configuration) throws ConfigurationException, CacheContextException;

  /**
   * Creates the regions for a specific application based on the configuration deployed for it.
   * 
   * @param appName The name of the application the cache regions are created for
   * @param configuration The configuration (read access) that has the property sheets for the regions
   * 
   * @throws ConfigurationException If the configuration does not exist
   * @throws CacheContextException If the configuration data is corrupt (e.i. non-existing plug-ins)
   */
  public void create(String appName, Configuration configuration) throws ConfigurationException, CacheContextException;
  
  /**
   * Destroys the previously created regions for a specific application.
   * 
   * @param appName The name of the application the cache regions are destroyed for
   * @param configuration The configuration (read access) that has the property sheets for the regions
   * 
   * @throws ConfigurationException If the configuration does not exist
   * @throws CacheContextException If some region could not be destroyed for some reason
   */
  public void destroy(String appName, Configuration configuration) throws ConfigurationException, CacheContextException;
  
  /**
   * Removes the configuration data for the regions of a specific application
   * 
   * @param appName The name of the application the cache region configuration data will be removed for
   * @param configuration The configuration (read access) that has the property sheets for the regions
   * @throws ConfigurationException If the configuration does not exist
   */
  public void remove(String appName, Configuration configuration) throws ConfigurationException;
  
  /**
   * Used to register plugin in the cache management library
   * 
   * @param pluginName The name under which the plugin will be registered
   * @param pluginImpl An instance of the class implementign the plugin
   */
  public void registerPlugin(String pluginName, StoragePlugin pluginImpl);
  
}
