/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container.deploy;

/**
 * Container class which lists the keys for the exception templates stored in
 * Library Container's resource bundle.
 * 
 * @author Rumiana Angelova
 * @version 7.1
 */
public interface DeploymentExceptionConstants {
	public static final String DEPLOY_ERROR_CFG = "lc_010"; // error during
	// deploy of
	// application {0}
	// because of a
	// configuration
	// error.
	public static final String DEPLOY_ERROR_IO = "lc_011"; // error during
	// deploy of
	// application {0}
	// because of a I/O
	// error.
	public static final String UPDATE_ERROR_CFG = "lc_020"; // error during
	// update of
	// application {0}
	// because of a
	// configuration
	// error.
	public static final String UPDATE_ERROR_IO = "lc_021"; // error during
	// update of
	// application {0}
	// because of an I/O
	// error.
	public static final String UPDATE_ERROR_MAPPING = "lc_022";	// Update of
	// application
	// {0} failed
	// because no
	// file was
	// found among
	// the archive
	// files for
	// mapping {1},
	// stored in
	// comparison
	// result.
	public static final String START_ERROR_CFG = "lc_030"; // error during start
	// of application
	// {0} because of a
	// configuration
	// error.
	public static final String START_ERROR_IO = "lc_031"; // error during start
	// of application
	// {0} because of an
	// I/O error.
	public static final String START_ERROR_SAX = "lc_032"; // error during start
	// of application
	// {0} while parsing
	// the application
	// service
	// descriptor.
	public static final String STOP_ERROR = "lc_040"; // error during stop of
	// application {0}
	public static final String GET_CLIENT_JAR_ERROR = "lc_050"; // error while
	// getting
	// client jar of
	// application
	// {0}

	public static final String GET_MY_WORK_DIR = "lc_100"; // error while
	// obtaining the
	// container work
	// directory of
	// application {0}
	public static final String NOTIFY = "lc_102"; // error during execution of
	// notification with
	// class-name {0},
	// method-name {1} for
	// application {2}
	public static final String MANIFEST_RESOLVE = "lc_103"; // Class-path entry
	// {0} specified in
	// Manifest.mf file
	// cannot be
	// resolved.
	public static final String MANIFEST_ERROR = "lc_104"; // Error while reading
	// manifest file of
	// archive {0}.
	public static final String EXIT_RESOURCE_CONTEXT = "lc_105"; // Error
	// occurred
	// during
	// exiting
	// method
	// {0} of
	// resource
	// context
	// for
	// application
	// {1}.
	public static final String LIFE_CYCLE_HOOK_THREAD_INTERRUPTED = "lc_106"; // The
	// thread
	// ,
	// executing
	// the
	// life
	// cycle
	// hook
	// for
	// application
	// {
	// 0
	// }
	// ,
	// was
	// interrupted
	// .

}
