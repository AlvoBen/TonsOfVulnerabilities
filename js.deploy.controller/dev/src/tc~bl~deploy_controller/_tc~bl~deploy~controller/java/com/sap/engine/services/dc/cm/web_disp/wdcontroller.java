package com.sap.engine.services.dc.cm.web_disp;

import java.util.Set;

import com.sap.engine.services.dc.util.backup.BackUp;

/**
 * Updates the configuration file used from the web dispatcher for the load
 * balancing.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public interface WDController extends BackUp {

	/**
	 * Backs up the web dispatcher profile file and overwrites old one, if any.
	 * 
	 * @throws WDException
	 */
	public void backUp() throws WDException;

	/**
	 * Restores the backup on the web dispatcher profile file and deletes the
	 * backup file.
	 * 
	 * @throws WDException
	 */
	public void restoreBackUp() throws WDException;

	/**
	 * Activates instance in the web dispatcher configuration file, but have to
	 * wait before the configuration file is reloaded.
	 * 
	 * @param instanceID
	 * @throws WDException
	 */
	public void activate(int instanceID) throws WDException;

	/**
	 * Deactivates instance in the web dispatcher configuration file, but have
	 * to wait before the configuration file is reloaded.
	 * 
	 * @param instanceID
	 * @throws WDException
	 */
	public void deActivate(int instanceID) throws WDException;

	public Set<WDICM> list() throws WDException;

	/**
	 * Adds instance in the web dispatcher configuration file, but have to wait
	 * before the configuration file is reloaded.
	 * 
	 * @param wdICM
	 * @throws WDException
	 */
	public void add(WDICM wdICM) throws WDException;

	/**
	 * Removes instance in the web dispatcher configuration file, but have to
	 * wait before the configuration file is reloaded.
	 * 
	 * @param instanceID
	 * @return <code>WDICM</code>
	 * @throws WDException
	 */
	public WDICM remove(int instanceID) throws WDException;

}
