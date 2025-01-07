/*
 * Created on 14.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.security.api.session;

/**
 * @author d028305
 * @deprecated Must not be used any longer.
 */
public interface ITicketCacheInfo
{
    public long getMissCount ();
    public long getHitCount  ();
    public long getWeight    ();
    public long getReadCount ();
    
    /**
     *  Resets the statistics
     */
    public void reset        ();
    
    /**
     * Clears the cache.
     */
    public void clear        ();
}
