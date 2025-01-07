/*==============================================================================
    File:         PluggableContext.java       
    Created:      21.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache.spi;

/**
 * The <code>PluginContext</code> interface encapsulates callback operations
 * for a <code>Pluggable</code> implementation in order to get information
 * about the context the <code>Pluggable</code> is running in.
 * 
 * @author Petio Petev, Michael Wintergerst
 */
public interface PluginContext {

    /**
     * Returns the cache region name the plugin is used for.
     * 
     * @return the cache region name the plugin is used for
     */
    public String getRegionName();

    /**
     * Returns the unique region identifier the plugin is used for.
     * 
     * @return the unique region identifier the plugin is used for
     */
    public int getRegionID();
}
