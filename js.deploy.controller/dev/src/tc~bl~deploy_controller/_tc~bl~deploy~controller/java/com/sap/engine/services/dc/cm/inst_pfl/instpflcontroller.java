package com.sap.engine.services.dc.cm.inst_pfl;

import java.util.Set;

import com.sap.engine.services.dc.util.backup.BackUp;

/**
 * Updates the instance profile file used from the current instance as a
 * persistent storage also for its ICM server ports.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public interface InstPflController extends BackUp {

	/**
	 * Backs up the instance profile file and overwrites old one, if any.
	 * 
	 * @throws InstPflException
	 */
	public void backUp() throws InstPflException;

	/**
	 * Restores the backup on the instance profile file and deletes the backup
	 * file.
	 * 
	 * @throws InstPflException
	 */
	public void restoreBackUp() throws InstPflException;

	/**
	 * Lists the <code>InstPflServerPort</code>s defined in the instance
	 * profile.
	 * 
	 * @return
	 */
	public Set<InstPflServerPort> listServerPorts() throws InstPflException;

	/**
	 * Updates the server ports with the given <code>index</code>, which can be
	 * positive and negative.
	 * 
	 * @return used index
	 * @throws InstPflException
	 */
	public void updateServerPorts(int index) throws InstPflException;

}
