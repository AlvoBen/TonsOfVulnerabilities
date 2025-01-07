/*==============================================================================
    File:         Pluggable.java
    Created:      21.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.spi;

import java.util.Properties;

import com.sap.util.cache.exception.PluginException;

/**
 * The <code>Pluggable</code> interface consitutes the base interface 
 * for all service provider interfaces (i.e., eviction policy, storage
 * plug-ins and cache readers).
 * 
 * <p>
 * For each global name specified in an application configuration file one 
 * instance of the pluggable is loaded and constructed. This instance is
 * called base instance. Therefore, the pluggable must have a default 
 * constructor. When instantiated the <code>init(String, Properties)</code> 
 * method is called providing the global name and the properties defined in the 
 * configuration file.
 * 
 * <p>
 * When a pluggable instance for a specific cache region region is needed
 * the <code>getInstance()</code> method is called. Thus, the pluggable is
 * free to choose their own creation process for plug-ins. For example, 
 * if two cache regions should use the same plug-in instance, the
 * <code>getInstance</code> might return the same plug-in instance. However,
 * in general cases, the <code>getInstance()</code> method returns a new 
 * plug-in for each cache region.
 * 
 * <p>
 * After getting an instance the <code>setPluginContext(PluginContext)</code>
 * method is called which informs the plug-in about the region. Before
 * performing any further operations, the <code>start()</code> method is
 * called.  
 *
 * <p>
 * If the <code>stop()</code> method is called, no further operations
 * are perfomed on the plug-in until the <code>start()</code> is called again.
 * Within the <code>stop()</code> method connections to databases may be 
 * released.
 * 
 * <p>
 * If the plug-in data has to be discarded, the <code>shutdown</code> is
 * called. 
 * 
 * @see com.sap.util.cache.spi.policy.EvictionPolicy
 * @see com.sap.util.cache.spi.storage.StoragePlugin
 *
 * @author Petio Petev, Michael Wintergerst
 */

public interface Pluggable {

    /**
     * Called by the infrastructure in order to get a concrete instance for
     * a cache region. 
     *
     * @return a plug-in instance for a specific cache region; may be 
     *         different from the base instance
     * 
     * @throws PluginException if the creation process fails
     */
    public Pluggable getInstance() throws PluginException;

    /**
     * Called by the cache implementation direct after the plugin instantiation
     * and before the <code>start()</code> method is called.
     * 
     * @param context information about the context the plugin is running in 
     */
    public void setPluginContext(PluginContext context);
    
    /**
     * Initializes the plug-in. This method is called exactly once per
     * plug-in. A plug-in is defined by a global name stored in the application
     * configuration file.
     * <br>
     * Note that the method is not called on plug-in instances returned
     * by the <code>getInstance</code> method.
     *
     * @param globalName the global name for the pluggable defined in 
     *                   the configuration file
     * @param props initialization parameters for the plug-in
     *
     * @throws PluginException if the initialization did not succeed
     */
    public void init(String globalName, Properties props) 
            throws PluginException;

    /**
     * Called for each instance bound to a region
     *
     * @throws PluginException if the initialization did not succeed
     */
    public void start() throws PluginException;

    /**
     * Called when a region bound with this pluggable is closed. Pluggables
     * should release resources and acquire it again after the 
     * <code>start</code> is called once more again.
     */
    public void stop();

    /**
     * Called once, before shutting down a node.
     */
    public void shutdown();

    /**
     * Returns the name of the component.
     *
     * @return name of the component
     */
    public String getName();

    /**
     * Returns a short description of the component.
     *
     * @return Short description of the component
     */
    public String getDescription();
}