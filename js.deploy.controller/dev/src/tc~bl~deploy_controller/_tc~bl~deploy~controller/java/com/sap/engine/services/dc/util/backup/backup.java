package com.sap.engine.services.dc.util.backup;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public interface BackUp {

	/**
	 * Backs up and overwrites old one, if any.
	 * 
	 * @throws CMException
	 */
	public void backUp() throws CMException;

	/**
	 * Restores the backup and deletes the backup file.
	 * 
	 * @throws CMException
	 */
	public void restoreBackUp() throws CMException;

}
