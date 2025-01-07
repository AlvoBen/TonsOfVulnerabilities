/*==============================================================================
    File:         ElementAttributes.java
    Created:      04.08.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/08/09 $
==============================================================================*/
package com.sap.util.cache.spi.policy;

import com.sap.util.cache.ElementConfiguration;

/**
 * The <code>ElementAttributes</code> interface encapsulates the attributes
 * of a specific object.
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public interface ElementAttributes extends ElementConfiguration {
    
    /**
     * Returns the size of the the cached object in bytes.
     * 
     * @return the size of the cached object in bytes
     */
    public int getSize();

    /**
     * Returns the attributes size of the cached object.
     *
     * @return the attributes size of the cached object in bytes
     */
    public int getAttributesSize();
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation timestamp.
     */
    public long getCreationTime();
    
    /**
     * Gets the last access timestamp.
     * 
     * @return the last access timestamp.
     */
    public long getLastAccessTime();
    
    /**
     * Sets the size of the the cached object in bytes.
     * 
     * @param size the size of the cached object in bytes to be set
     * 
     * @throws IllegalArgumentException if the <code>size</code> parameter
     *         is set to a negative value
     */
    public void setSize(int size);

    /**
     * Sets the attributes size of the cached object.
     *
     * @param size the attributes size of the cached object in bytes to be set
     * 
     * @throws IllegalArgumentException if the <code>size</code> parameter
     *         is set to a negative value
     */
    public void setAttributesSize(int size);
    
    /**
     * Sets the creation timestamp.
     * 
     * @param timestamp the creation timestamp to be set
     * 
     * @throws IllegalArgumentException if the <code>timestamp</code> parameter
     *         is set to a negative value
     */
    public void setCreationTime(long timestamp);
    
    /**
     * Sets the last access timestamp.
     * 
     * @param timestamp the last access timestamp to be set
     * 
     * @throws IllegalArgumentException if the <code>timestamp</code> parameter
     *         is set to a negative value
     */
    public void setLastAccessTime(long timestamp);
}