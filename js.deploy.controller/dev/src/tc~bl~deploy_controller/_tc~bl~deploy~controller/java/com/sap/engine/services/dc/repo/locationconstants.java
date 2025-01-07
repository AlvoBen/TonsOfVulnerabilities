package com.sap.engine.services.dc.repo;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-1
 * 
 * @author Dimitar Dimitrov,
 * @author Anton Georgiev
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.0
 * 
 */
public final class LocationConstants {

	// the root
	public static final String DEPLOY_CONTROLLER = "deploy_controller";
	// the repository root. repository is the location where is persisted the
	// current
	// of what is deployed on the system.
	public static final String REPO = "repo";
	// the storage root. storage is the location where the files are persisted
	public static final String STORAGE = "storage";
	// the root of the migration data. the migration data is used for upgrade
	public static final String MIGRATION = "migration";
	// the name for the migration properties
	public static final String MIGRATION_PROPS = "mig";
	// the history root. history is the location where is persisted which
	// components
	// have already been updated or deleted
	public static final String HISTORY = "history";
	// the offline root.
	public static final String OFFLINE = "offline";
	// location for a development component
	public static final String DC = "dc";
	// location for a software component
	public static final String SC = "sc";
	// location for a deployment
	public static final String DEPLOY = "deploy";
	// location for an undeployment
	public static final String UNDEPLOY = "undeploy";
	// location for dependencies
	public static final String DEPS = "deps";
	// location for deploy time dependencies
	public static final String DT = "dt";
	// location for the actual dependencis to which component depends on
	public static final String TO = "to";
	// location for the actual components dependencies which depend on the
	// component
	public static final String FROM = "from";
	// former lock location constants
	public static final String LOCK = "lock";

	public static final String RUN_MODE = "runMode";

	public static final String RUN_ACTION = "runAction";

	// Path separator - !!! do not change it !!!
	public static final String PATH_SEPARATOR = Constants.CFG_PATH_SEPARATOR;

	public static final String PATH_DC_PATH;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(PATH_SEPARATOR);
		sb.append(DC);
		sb.append(PATH_SEPARATOR);
		PATH_DC_PATH = sb.toString();
	}
	public static final String PATH_SC_PATH;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(PATH_SEPARATOR);
		sb.append(SC);
		sb.append(PATH_SEPARATOR);
		PATH_SC_PATH = sb.toString();
	}

	// The root element of repo DC components.
	public static final String ROOT_REPO_DC;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(REPO);
		sb.append(PATH_SEPARATOR);
		sb.append(DC);
		ROOT_REPO_DC = sb.toString();
	}
	// The root element of storage DC components.
	public static final String ROOT_STORAGE_DC;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(STORAGE);
		sb.append(PATH_SEPARATOR);
		sb.append(DC);
		ROOT_STORAGE_DC = sb.toString();
	}
	/*
	 * //The root element of history DC components. public static final String
	 * ROOT_HISTORY_DC; static { final StringBuffer sb = new StringBuffer();
	 * sb.append(DEPLOY_CONTROLLER);sb.append(PATH_SEPARATOR);
	 * sb.append(HISTORY);sb.append(PATH_SEPARATOR); sb.append(DC);
	 * ROOT_HISTORY_DC = sb.toString(); }
	 */
	// The root element of repo SC components.
	public static final String ROOT_REPO_SC;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(REPO);
		sb.append(PATH_SEPARATOR);
		sb.append(SC);
		ROOT_REPO_SC = sb.toString();
	}
	// The root element of storage SC components.
	public static final String ROOT_STORAGE_SC;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(STORAGE);
		sb.append(PATH_SEPARATOR);
		sb.append(SC);
		ROOT_STORAGE_SC = sb.toString();
	}
	/*
	 * //The root element of history SC components. public static final String
	 * ROOT_HISTORY_SC; static { final StringBuffer sb = new StringBuffer();
	 * sb.append(DEPLOY_CONTROLLER);sb.append(PATH_SEPARATOR);
	 * sb.append(HISTORY);sb.append(PATH_SEPARATOR); sb.append(SC);
	 * ROOT_HISTORY_SC = sb.toString(); }
	 */

	// Deploy offline root.
	public static final String ROOT_OFFLINE_DEPLOY;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(OFFLINE);
		sb.append(PATH_SEPARATOR);
		sb.append(DEPLOY);
		ROOT_OFFLINE_DEPLOY = sb.toString();
	}
	// Undeploy offline root.
	public static final String ROOT_OFFLINE_UNDEPLOY;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(DEPLOY_CONTROLLER);
		sb.append(PATH_SEPARATOR);
		sb.append(OFFLINE);
		sb.append(PATH_SEPARATOR);
		sb.append(UNDEPLOY);
		ROOT_OFFLINE_UNDEPLOY = sb.toString();
	}

	public static final String ROOT_LOCK = new StringBuffer(DEPLOY_CONTROLLER)
			.append(PATH_SEPARATOR).append(LOCK).toString();

	private LocationConstants() {
	}

}