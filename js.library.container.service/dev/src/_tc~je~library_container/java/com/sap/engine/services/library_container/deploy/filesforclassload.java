/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container.deploy;

import java.io.File;
import java.util.ArrayList;

/**
 * Container class which holds information for all application library files
 * that has to be added to the CLASSPATH of the application classloader.
 * 
 * @author Luchesar Cekov
 */
public class FilesForClassLoad {
	private ArrayList<File> filesForClassLoad = new ArrayList<File>();
	private ArrayList<File> heavyFilesForClassLoad = new ArrayList<File>();

	/**
	 * 
	 * @return list of application library files that has to be added to the
	 *         CLASSPATH of the application classloader; this list may or may
	 *         not include the file for the so called heavy classloading; to get
	 *         all files for classload, use the <code>getAll()</code> method
	 */
	public ArrayList<File> getFilesForClassLoad() {
		return filesForClassLoad;
	}

	/**
	 * 
	 * @return list of application library files used for the so called <i>heavy
	 *         classloading</i>, i.e. files which belong to /lib folder or to
	 *         the folder designated with the <library-directory> tag in the
	 *         deployment descriptor.
	 */
	public ArrayList<File> getHeavyFilesForClassLoad() {
		return heavyFilesForClassLoad;
	}

	/**
	 * 
	 * @return number of application library files
	 */
	public int size() {
		return filesForClassLoad.size() + heavyFilesForClassLoad.size();
	}

	/**
	 * 
	 * @return the full list of application library files added to the CLASSPATH
	 *         of the applicaiton classloader.
	 */
	public File[] getAll() {
		File[] all = new File[filesForClassLoad.size()
				+ heavyFilesForClassLoad.size()];
		int counter = 0;
		for (File f : filesForClassLoad) {
			all[counter++] = f;
		}
		for (File f : heavyFilesForClassLoad) {
			all[counter++] = f;
		}
		return all;
	}
}
