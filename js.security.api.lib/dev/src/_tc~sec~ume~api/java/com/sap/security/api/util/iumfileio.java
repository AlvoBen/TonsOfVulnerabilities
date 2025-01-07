package com.sap.security.api.util;

import java.io.InputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Properties;

import com.sap.security.api.UMException;

/**
 * Title:        User Management 60
 * Description:
 * Copyright:    Copyright (c) 2003
 * Company:      SAP
 * @author d021856
 * @version 1.0
 */

public interface IUMFileIO {

    /**
     * write <code>InputStream</code> to named file (depending on the
     * environment data will be written to file system, or WEB AS 630 database)
     * 
     * @param name Name of the file
     * @param in data as <code>InputStream</code>
     *
     * @throws  IOException if data could not be read, file could not be written
     */
    public void writeFile(String name, InputStream in) throws IOException;

	/**
	 * Deletes a file 
	 * (depending on the environment the file will be deleted in the file system
	 * or WEB AS 630 database)
	 * 
	 * @param name Name of the file
	 *
	 * @throws  IOException if the file could not be deleted
	 */
	public void deleteFile(String name) throws IOException;

    /**
     *
     * @param name of the file to be read (depending on the
     * environment data will be read from file system or WEB AS 630 database)
     *
     * @return data as <code>InputStream</code>
     * @throws IOException if file could not be read
     */
    public InputStream readFile(String name) throws IOException;

	/**
	 * 
	 * returns a string array with names of all available config files
	 * 
	 * @return names of config files as <code>String</code> Array
	 *
	 * @throws  IOException if data could not be read
	 */
	public String[] getFiles() throws IOException;

	/**
	 * @return The persistend properties. Only system administrators have access to the properties.
	 * @throws AccessControlException, UMException
	 */
	public Properties readConfiguration() throws AccessControlException, UMException;
	
	/**
	 * @param properties Properties to persist
	 * @throws AccessControlException, UMException
	 */
	public void writeConfiguration(Properties properties) throws AccessControlException, UMException;

}
