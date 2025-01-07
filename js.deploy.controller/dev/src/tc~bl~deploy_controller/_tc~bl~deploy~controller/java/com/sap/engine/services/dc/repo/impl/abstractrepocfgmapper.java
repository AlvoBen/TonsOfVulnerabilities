package com.sap.engine.services.dc.repo.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.sql.Connection;
import java.sql.SQLException;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.dc.cm.utils.db.DBPoolSystemDataSourceBuilder;
import com.sap.engine.services.dc.cm.utils.db.SystemDataSourceBuildingException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.tc.logging.Location;

/**
 * Extends the configuration functionality for the repository needs.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
abstract class AbstractRepoCfgMapper {
	
	private Location location = getLocation(this.getClass());

	// ///////////////////////////////////////
	// //// CONFIGURATION //////
	// ///////////////////////////////////////

	/**
	 * Gets new <code>ConfigurationHandler</code>.
	 * 
	 * @return <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if a problem occurs. or
	 *             <code>ApplicationServiceContext</code> are null.
	 */
	protected ConfigurationHandler getCfgHandler() throws RepositoryException,
			NullPointerException {
		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = ServiceConfigurer.getInstance()
					.getConfigurationHandler();

			// initCfgListeners(cfgHandler);
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.ERROR_GETTING,
					new String[] { "ConfigurationHandler" }, ce);
		}

		return cfgHandler;
	}

	// private void initCfgListeners(ConfigurationHandler cfgHandler) {
	// final ConfigurationChangedListener dcCfgListener =
	// RepositoryComponentsFactory.getInstance().createRepoDCCfgListener();
	//      
	// final ConfigurationChangedListener scCfgListener =
	// RepositoryComponentsFactory.getInstance().createRepoSCCfgListener();
	//      
	// cfgHandler.addConfigurationChangedListener(dcCfgListener,
	// LocationConstants.ROOT_REPO_DC,
	// ConfigurationChangedListener.MODE_SYNCHRONOUS);
	//                                               
	// cfgHandler.addConfigurationChangedListener(scCfgListener,
	// LocationConstants.ROOT_REPO_SC,
	// ConfigurationChangedListener.MODE_SYNCHRONOUS);
	// }

	/**
	 * Commits the given <code>ConfigurationHandler</code>, if it is not null.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>.
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void commit(ConfigurationHandler cfgHandler)
			throws RepositoryException {
		if (cfgHandler == null) {
			return;
		}
		try {
			cfgHandler.commit();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(
					DCExceptionConstants.CANNOT_CFG_HANDLER,
					new String[] { "commit" }, ce);
		}
	}

	/**
	 * Rolls back the given <code>ConfigurationHandler</code>, if it is not
	 * null.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>.
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void rollback(ConfigurationHandler cfgHandler)
			throws RepositoryException {
		if (cfgHandler == null) {
			return;
		}
		try {
			cfgHandler.rollback();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(
					DCExceptionConstants.CANNOT_CFG_HANDLER,
					new String[] { "rollback" }, ce);
		}
	}

	/**
	 * Closes all opend <code>Configuration</code> the given
	 * <code>ConfigurationHandler</code>, if it si not null.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>.
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void closeAllConfigurations(ConfigurationHandler cfgHandler)
			throws RepositoryException {
		if (cfgHandler == null) {
			return;
		}
		try {
			cfgHandler.closeAllConfigurations();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(
					DCExceptionConstants.CANNOT_CFG_HANDLER,
					new String[] { "close all configuration in" }, ce);
		}
	}

	/**
	 * Deletes <code>Configuration</code> described with <code>String</code>
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void deleteCfg(ConfigurationHandler cfgHandler, String fullCfgPath)
			throws RepositoryException {
		try {
			Configuration cfg = CfgUtils.openCfgWrite(cfgHandler, fullCfgPath);
			cfg.deleteConfiguration();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "delete", fullCfgPath }, ce);
		}
	}

	/**
	 * Deletes, if exists, <code>Configuration</code> described with
	 * <code>String</code>
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void deleteCfgIfExists(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws RepositoryException {
		try {
			Configuration cfg = CfgUtils.openCfgWrite(cfgHandler, fullCfgPath);
			cfg.deleteConfiguration();
		} catch (NameNotFoundException nnfe) {// $JL-EXC$
			if (location.beDebug()) {
				traceDebug(
						location,
						"Configuration does not exist and will not be deleted: [{0}]",
						new Object[] { fullCfgPath });
			}
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "delete", fullCfgPath }, ce);
		}
	}

	/**
	 * Deletes the given <code>Configuration</code> and closes it.
	 * 
	 * @param cfg
	 *            <code>Configuration</code>
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void deleteCfg(Configuration cfg) throws RepositoryException {
		try {
			cfg.deleteConfiguration();
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "delete", cfg.getPath() }, ce);
		}
	}

	/**
	 * Closes <code>Configuration</code>
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param cfg
	 *            <code>Configuration</code>
	 * @throws RepositoryException
	 *             if an error occurs.
	 */
	protected void closeCfg(ConfigurationHandler cfgHandler, Configuration cfg)
			throws RepositoryException {
		try {
			cfgHandler.closeConfiguration(cfg);
		} catch (ConfigurationException ce) {
			throw new RepositoryException(DCExceptionConstants.CANNOT_CFG,
					new String[] { "close", cfg.getPath() }, ce);
		}
	}

	protected Connection getDBConnection() throws RepositoryException {
		try {
			return DBPoolSystemDataSourceBuilder.getInstance()
					.buildSystemDataSource().getConnection();
		} catch (SystemDataSourceBuildingException sdsbe) {
			throw new RepositoryException(DCExceptionConstants.ERROR_CREATING,
					new String[] { "db connection" }, sdsbe);
		} catch (SQLException sqle) {
			throw new RepositoryException(DCExceptionConstants.ERROR_CREATING,
					new String[] { "db connection" }, sqle);
		}
	}

	protected void closeDBConnection(Connection connection)
			throws RepositoryException {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqle) {
				throw new RepositoryException(
						DCExceptionConstants.CANNOT_CLOSE_X,
						new String[] { "db connection" }, sqle);
			}
		}
	}

	protected void commit(Connection connection) throws RepositoryException {
		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException sqle) {
				throw new RepositoryException(
						DCExceptionConstants.CANNOT_DB_CONN,
						new String[] { "commit" }, sqle);
			}
		}
	}

	protected void rollback(Connection connection) throws RepositoryException {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException sqle) {
				throw new RepositoryException(
						DCExceptionConstants.CANNOT_DB_CONN,
						new String[] { "rollback" }, sqle);
			}
		}
	}

}