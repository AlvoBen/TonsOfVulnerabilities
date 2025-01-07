package com.sap.i18n.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDO.Table;
import com.sap.i18n.cache.BackendDO.TableProperties;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Abstract class as bridge to the configuration manager
 * 
 * @author D046219
 * 
 */
public abstract class I18NConfigMngBridge implements
		ConfigurationChangedListener {

	private static final Location m_oLoc = Location
			.getLocation("com.sap.i18n.saptimezone.I18NConfigMngBridge");
	protected static final Category m_oLog = Category.SYS_SERVER;

	// waiting time in ms in case the configuration was locked by other process.
	final int WAIT_TIME_OPEN_CONFIG = 300;
	// maximal open attempt
	protected final int OPEN_CONFIG_MAX_ATTEMPT = 1000; // 5 min

	public static final String CONFIGURATIONHEADER = "i18n";

	/**
	 * The ConfigurationHandlerFactory handles the access to the configuration.
	 * In case of a JUnit test this factory is null. Otherwise it must always
	 * exist.
	 */
	private static ConfigurationHandlerFactory configHandlerFactory = null;

	private ConfigurationHandlerFactory getConfigHandlerFactory() {
		if (configHandlerFactory == null) {
			ApplicationServiceContext serviceContext = I18NSyncFrame
					.getServiceContext();
			if (serviceContext != null) {
				configHandlerFactory = serviceContext.getCoreContext()
						.getConfigurationHandlerFactory();
			}
		}
		return configHandlerFactory;
	}

	/**
	 * For each configuration a unique key must exist. This key is used to store
	 * the data in the configuration manager.
	 * 
	 * @return the unique key for this configuration
	 */
	protected abstract String getConfigurationName();

	/**
	 * 
	 * @return the cache used for this bridge
	 */
	public abstract BackendDataCache getBackendDataCache();

	/**
	 * 
	 * @param tablename
	 *            the name of the table that is to be read from the property
	 *            sheet
	 * @param propertysheet
	 *            contains all data from the configuration manager
	 * @return the table object containing all the data from the configuration
	 *         manager belonging to the table with the given name
	 */
	protected Table readBackendTable(String tablename,
			PropertySheet propertysheet) {
		TableProperties tableProps = getBackendDataCache().getTableProperties(
				tablename);
		BackendDO.Table table = new BackendDO.Table(tableProps);
		// get propertysheet's entries. each entry correspond to table row
		try {
			PropertyEntry[] entries = propertysheet.getAllPropertyEntries();
			for (int i = 0; i < entries.length; i++) {
				PropertyEntry entry = entries[i];
				String propertyEntry = (String) entry.getValue();

				BackendDO.Row row = getBackendDataCache().createRow(tableProps,
						propertyEntry);
				table.add(i, row);
			}
		} catch (ConfigurationException e) {

		}
		return table;
	}

	/**
	 * caches the given data in the corresponding BackendDataCache.
	 * 
	 * @param data
	 *            the list of tables containing all data for this configuration
	 */
	public void initBackendData(BackendDO data) {
		if (data != null) {
			getBackendDataCache().init(data, BackendAvailability.STATE_OK);
		}
	}

	/**
	 * 
	 * @return the path in the configuration manager where this configuration's
	 *         data is stored
	 */
	public final String getPath() {
		return CONFIGURATIONHEADER + "/" + getConfigurationName();
	}

	protected Configuration i18nroot = null;

	/**
	 * create sub-configuration for data in the configuration manager
	 * 
	 * @throws ConfigurationException
	 */
	public void createSubConfiguration() throws ConfigurationException {
		createSubConfiguration(getConfigurationName());
	}

	/**
	 * delete the sub-configuration
	 * 
	 * @throws ConfigurationException
	 */
	public void deleteConfiguration() throws ConfigurationException {
		deleteConfiguration(getPath());
	}

	/**
	 * Create a I18N Sub-configuration
	 * 
	 * @param cfgName
	 *            name of the configuration
	 * @return true if the configuration is created successfully otherwise
	 *         false.
	 * @throws ConfigurationException
	 */
	public boolean createSubConfiguration(String cfgName)
			throws ConfigurationException {
		boolean created = false;
		if (getConfigHandlerFactory() != null) {
			ConfigurationHandler configHandler = getConfigHandlerFactory()
					.getConfigurationHandler();
			boolean accessed = false;
			int attemptCounter = 0;
			while (!accessed && attemptCounter < OPEN_CONFIG_MAX_ATTEMPT) {
				try {
					// open the root configuration for write access
					Configuration root = configHandler.openConfiguration(
							CONFIGURATIONHEADER,
							ConfigurationHandler.WRITE_ACCESS);
					accessed = true;
					root.createSubConfiguration(cfgName);
					configHandler.commit();
					created = true;
				} catch (ConfigurationLockedException lockexcp) {
					attemptCounter++;
					wait4Access();
				} catch (NameAlreadyExistsException nameexistexception) {
					return true;
				} finally {
					configHandler.closeAllConfigurations();
				}
			}
		}
		return created;
	}

	private BackendDO parseConfigurations(Map<String, Configuration> mapCfg)
			throws ConfigurationException {
		BackendDO data = new BackendDO();
		Iterator<Map.Entry<String, Configuration>> iterator = mapCfg.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Configuration> mapEntry = iterator.next();
			Configuration tmpConfiguration = mapEntry.getValue();
			if (tmpConfiguration.getConfigurationType() == Configuration.CONFIG_TYPE_PROPERTYSHEET) {
				String tablename = (String) mapEntry.getKey();
				PropertySheet tableAsPropertysheet = tmpConfiguration
						.getPropertySheetInterface();
				BackendDO.Table result = readBackendTable(tablename,
						tableAsPropertysheet);
				data.addTable(result);
			}
		}
		return data;
	}

	public void storeData(BackendDO data) throws ConfigurationException {
		if (getConfigHandlerFactory() != null) {
			ConfigurationHandler configHandler = getConfigHandlerFactory()
					.getConfigurationHandler();
			Configuration cfg = null;
			boolean accessed = false;
			int attemptCounter = 0;
			while (!accessed && attemptCounter < OPEN_CONFIG_MAX_ATTEMPT) {
				try {
					cfg = configHandler.openConfiguration(getPath(),
							ConfigurationHandler.WRITE_ACCESS);
					accessed = true;
					pushDataToConfiguration(cfg, data);
					configHandler.commit();
				} catch (ConfigurationLockedException lockexception) {
					attemptCounter++;
					wait4Access();
				} finally {
					configHandler.closeAllConfigurations();
				}
			}
		}
	}

	/**
	 * Delete I18N configuration
	 * 
	 * @param configuration
	 *            name of the which should be deleted
	 * @throws ConfigurationException
	 */
	public void deleteConfiguration(String configuration)
			throws ConfigurationException {
		if (getConfigHandlerFactory() != null) {
			ConfigurationHandler configHandler = getConfigHandlerFactory()
					.getConfigurationHandler();
			boolean accessed = false;
			while (!accessed) {
				try {
					Configuration configuratonInstance = configHandler
							.openConfiguration(configuration,
									ConfigurationHandler.WRITE_ACCESS);
					accessed = true;
					configuratonInstance.deleteConfiguration();
					configHandler.commit();
				} catch (NoWriteAccessException accessexception) {
					wait4Access();
				} catch (NameNotFoundException namenotfound) {
					return;
				} finally {
					configHandler.closeAllConfigurations();
				}
			}
		}
	}

	/**
	 * register the configuration
	 */
	public void registerConfiguration() {
		if (getConfigHandlerFactory() != null) {
			try {
				ConfigurationHandler configHandler = getConfigHandlerFactory()
						.getConfigurationHandler();
				configHandler.addConfigurationChangedListener(this, getPath());
			} catch (ConfigurationException configurationException) {
				m_oLog
						.warningT(
								m_oLoc,
								"The registration of the configuration finished with Exception. Exception message: "
										+ configurationException.getMessage());
				m_oLoc
						.traceThrowableT(
								Severity.WARNING,
								"The registration of the configuration finished with Exception. Error caused by: ",
								configurationException);
			}
		}
	}

	/**
	 * remove the Configuration Listener
	 */
	public void removeListener() {
		removeConfigurationChListener(this, getPath());
	}

	public void removeConfigurationChListener(
			ConfigurationChangedListener listener, String configuration) {
		if (getConfigHandlerFactory() != null) {
			try {
				ConfigurationHandler configHandler = getConfigHandlerFactory()
						.getConfigurationHandler();
				configHandler.removeConfigurationChangedListener(listener,
						configuration);
			} catch (ConfigurationException cfgException) {
				m_oLog.warningT(m_oLoc,
						"remove of listener throws Configuration Exception. "
								+ cfgException.getMessage());
			}
		}
	}

	/**
	 * push the data from the backend-system to a sub-configuration
	 * 
	 * @param cfg
	 *            the configuration where the backend-data is to be stored
	 * @param data
	 *            data from the backend-system
	 * @throws ConfigurationException
	 */
	protected void pushDataToConfiguration(Configuration cfg, BackendDO data)
			throws ConfigurationException {
		PropertySheet tmp_propertysheet = null;
		HashMap<String, BackendDO.Table> container = data.getTables();
		Iterator<Map.Entry<String, BackendDO.Table>> itera = container
				.entrySet().iterator();
		while (itera.hasNext()) {

			// read next table
			Map.Entry<String, BackendDO.Table> entry = itera.next();
			String key = entry.getKey();
			BackendDO.Table value = entry.getValue();

			try {
				tmp_propertysheet = cfg.createSubConfiguration(key,
						Configuration.CONFIG_TYPE_PROPERTYSHEET)
						.getPropertySheetInterface();
				saveBackendTable(tmp_propertysheet, value);
			} catch (NameAlreadyExistsException nameexception) {
				tmp_propertysheet = cfg.getSubConfiguration(key)
						.getPropertySheetInterface();
				saveBackendTable(tmp_propertysheet, value);
			}
		}
	}

	/**
	 * read data from the configuration manager
	 * 
	 * @return the data from the backend system which is stored in the
	 *         configuration manager
	 * @throws ConfigurationException
	 */
	public BackendDO readData() throws ConfigurationException {
		BackendDO data = null;
		if (getConfigHandlerFactory() != null) {
			ConfigurationHandler configHandler = getConfigHandlerFactory()
					.getConfigurationHandler();
			Map<String, Configuration> subConfigurations = readData(getPath(),
					configHandler);
			try {
				if (subConfigurations != null) {
					if (!subConfigurations.isEmpty()) {
						data = parseConfigurations(subConfigurations);
					}
				}
			} catch (ConfigurationException cfgException) {
				m_oLog
						.errorT(
								m_oLoc,
								"The read of data from the configuration handler failed.",
								cfgException.getMessage());
				m_oLoc
						.traceThrowableT(
								Severity.ERROR,
								"The read of data from the configuration handler failed",
								cfgException);
				// forward exception
				throw cfgException;
			} finally {
				configHandler.closeAllConfigurations();
			}
		}
		return data;
	}

	/**
	 * Read data from the configuration manager.
	 * 
	 * @param path
	 *            to the configuration which contains data
	 * @return Map with all Sub-configurations existing within the configuration
	 *         referenced by the parameter <code>path</code>.
	 * @throws ConfigurationException
	 */
	public Map<String, Configuration> readData(String path,
			ConfigurationHandler configHandler) throws ConfigurationException {
		Map<String, Configuration> subConfigurations = null;
		Configuration oConfiguration = null;
		boolean successfullyRead = false;
		int attemptCounter = 0;
		while (!successfullyRead && attemptCounter < OPEN_CONFIG_MAX_ATTEMPT) {
			try {
				oConfiguration = configHandler.openConfiguration(path,
						ConfigurationHandler.READ_ACCESS);
				subConfigurations = oConfiguration.getAllSubConfigurations();
				successfullyRead = true;
			} catch (ConfigurationLockedException lockedException) {
				attemptCounter++;
				wait4Access();
			} catch (InconsistentReadException readExcp) {
				oConfiguration.close();
			} catch (NameNotFoundException readexcp) {
				m_oLoc.infoT(path
						+ "doesn exist within the configuration manager.");
				return subConfigurations; // as null
			}
		}
		return subConfigurations;
	}

	/**
	 * save values in the property-sheet
	 * 
	 * @param propSheet
	 *            where the value should be stored
	 * @param key
	 *            table name
	 * @param values
	 *            list of all table's row
	 * @throws ConfigurationException
	 */
	protected void saveBackendTable(PropertySheet propSheet,
			BackendDO.Table table) throws ConfigurationException {

		for (int i = 0; i < table.size(); i++) {
			// each table consists of various rows
			BackendDO.Row row = table.get(i);

			// each row has a unique key and various values, which are combined
			// in one property-string
			String key = row.getKey();
			String value = row.getAsPropertyString();

			try {
				propSheet.createPropertyEntry(key, value, "none");
			} catch (NoWriteAccessException accessexcp) {
				// since we were waiting for the access, this exception should
				// not be occur.
				m_oLoc
						.debugT("update of entry wihtin the configuration failed. Error caused by access-exception: "
								+ accessexcp.getMessage());
			} catch (NameAlreadyExistsException nameexistsexcp) {
				updateEntry(propSheet, key, value);
			}
		}

	}

	/**
	 * update entry if it exists in the propertysheet
	 */
	protected void updateEntry(PropertySheet propsheet, String key, String value)
			throws ConfigurationException {
		try {
			propsheet.deletePropertyEntry(key);
			propsheet.createPropertyEntry(key, value, "none");
		} catch (NoWriteAccessException accessexcp) {
			m_oLoc
					.debugT("update of entry wihtin the configuration is failed. Error caused by access-exception: "
							+ accessexcp.getMessage());
		}
	}

	/**
	 * thread waits for access if the configuration is locked by another thread
	 */
	public void wait4Access() {
		long idleTime = WAIT_TIME_OPEN_CONFIG;
		long wakeupTime = System.currentTimeMillis() + idleTime;
		do {
			try {
				Thread.sleep(idleTime);
			} catch (InterruptedException threadexcp) {
				m_oLog.errorT(m_oLoc,
						"Thread error during the wait-for-access period. Exception message: "
								+ threadexcp.getMessage());
				m_oLoc
						.traceThrowableT(
								Severity.ERROR,
								"Thread error during wait-for-access period. Exception caused by: ",
								threadexcp);
			}
			idleTime = wakeupTime - System.currentTimeMillis();
		} while (idleTime > 0);
	};

	/**
	 * create root configuration of Internationalization. configuration name:
	 * <b>i18n</b>
	 * 
	 * @throws ConfigurationException
	 */
	public void createConfiguration() throws ConfigurationException {
		if (getConfigHandlerFactory() != null) {
			ConfigurationHandler configHandler = getConfigHandlerFactory()
					.getConfigurationHandler();
			try {
				i18nroot = configHandler.openConfiguration(CONFIGURATIONHEADER,
						ConfigurationHandler.READ_ACCESS);
			} catch (NameNotFoundException nnfexcp) {
				boolean created = false;
				int attemptCounter = 0;
				while (!created && attemptCounter < OPEN_CONFIG_MAX_ATTEMPT) {
					try {
						/* configuration doesn't exist. create it */
						i18nroot = configHandler
								.createRootConfiguration(CONFIGURATIONHEADER);
						configHandler.commit();
						created = true;
					} catch (ConfigurationLockedException lockexcp) {
						attemptCounter++;
						wait4Access();
					} catch (NameAlreadyExistsException namealreadyexist) {
						created = true;
					}
				}
			} finally {
				configHandler.closeAllConfigurations();
			}
		}
	}

	public void configurationChanged(ChangeEvent event) {
		/*
		 * If one configuration has been changed on one of all nodes in the
		 * cluster this method will be executed in all nodes (also in the node
		 * which has been changed) in order to update the library since the
		 * configuration manager is responsible to update the configuration(s)
		 * only.
		 * 
		 * read data from the configuration manager and update the library
		 */
		if (event.getAction() != ChangeEvent.ACTION_DELETED) {
			try {
				BackendDO data = readData();
				initBackendData(data);
				// keepData(data);
			} catch (ConfigurationException cfgException) {
				m_oLog.errorT(m_oLoc,
						"ConfigurationChanged. Error during update of the library. Exception message: "
								+ cfgException.getMessage());
				m_oLoc
						.traceThrowableT(
								Severity.ERROR,
								"ConfigurationChanged. Error during update of the library. Error caused by: ",
								cfgException);
			}
		}
	}

	// method just of test if the engine's node are synchronous
	protected void keepData(Object o) {
		BackendDO langInfo = (BackendDO) o;

		FileOutputStream foutput = null;
		OutputStreamWriter osw = null;

		Iterator<BackendDO.Table> tables = langInfo.getTables().values()
				.iterator();
		while (tables.hasNext()) {
			BackendDO.Table table = tables.next();
			String path = System.getProperty("java.io.tmpdir");
			File file = new File(path + "/" + table.getTableName() + ".txt");
			try {
				foutput = new FileOutputStream(file, true);
				osw = new OutputStreamWriter(foutput, "UTF-8");
				for (int i = 0; i < table.size(); i++) {
					// read table rows
					BackendDO.Row row = table.get(i);
					// create table row-object corresponding to T002V ABAP table
					String entrie = row.getAsPropertyString();
					osw.write(entrie + "\n");
				}
			} catch (Exception e) {
				m_oLoc.traceThrowableT(Severity.ERROR,
						"error during write to file: ", e);
			} finally {
				if (osw != null)
					try {
						osw.close();
					} catch (IOException e) {
					}
				if (foutput != null)
					try {
						foutput.close();
					} catch (IOException e) {
					}
			}
		}
	}
}
