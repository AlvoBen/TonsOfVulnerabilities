package com.sap.i18n.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sap.conn.jco.JCoException;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.calendar.CalConfigCMBridge;
import com.sap.i18n.calendar.CalConfigR3Bridge;
import com.sap.i18n.countryformat.CountryFormatCMBridge;
import com.sap.i18n.countryformat.CountryFormatR3Bridge;
import com.sap.i18n.itsam.SAP_ITSAMI18NServiceWrapper;
import com.sap.i18n.itsam.SAP_ITSAMI18NService_Impl;
import com.sap.i18n.language.SAPLanguageCMBridge;
import com.sap.i18n.language.SAPLanguageR3Bridge;
import com.sap.i18n.saptimezone.SAPTimeZoneCMBridge;
import com.sap.i18n.saptimezone.SAPTimeZoneR3Bridge;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This service is designed for double stack system I18N service responsible for
 * sync SAP specific data (Time zone and Language) from the back-end system
 * (ABAP-Stack)
 * 
 * @author D046219
 * 
 */

public class I18NSyncFrame implements ApplicationServiceFrame {

	// Initialization of the location for SAP logging.
	private final static Location m_oLoc = Location
			.getLocation("com.sap.i18n.I18NSyncFrame");
	// Initialization of the category for SAP logging.
	private final static Category m_oLog = Category.SYS_SERVER;
	// default value of the system's property
	private final String DOUBLESTACKSYSTEM = "DS";

	private ObjectName m_MBeanName = null;

	private static BackendDO m_DataFromFile = null;

	private static ApplicationServiceContext m_ServiceContext = null;

	private boolean m_ITSAMMBeanRegistered = false;

	// The sole instance of this class. The instance is initialized when the
	// service is started and
	// set to null when it is stopped
	private static I18NSyncFrame instance = null;

	public static boolean m_IsUnitTestStatus = false;

	/**
	 * service start
	 * 
	 * @param ApplicationServiceContext
	 * @throws ServiceException
	 */
	public void start(ApplicationServiceContext serviceContext)
			throws ServiceException {
		m_ServiceContext = serviceContext;
		I18NSyncFrame.instance = this;

		// register mbeans into the Mbeanserver
		registerITSAMMBean();

		// register the current object as listener for configuration
		// "LANGUAGECONFIGURATION"
		SAPLanguageCMBridge languageCMBridge = SAPLanguageCMBridge
				.getInstance();
		languageCMBridge.getBackendDataCache().init(null,
				BackendAvailability.NOT_AVAILABLE);
		languageCMBridge.registerConfiguration();
		// register the current object as listener for configuration
		// "TIMEZONECONFIGURATION"
		SAPTimeZoneCMBridge timezonCMBridge = SAPTimeZoneCMBridge.getInstance();
		timezonCMBridge.getBackendDataCache().init(null,
				BackendAvailability.NOT_AVAILABLE);
		timezonCMBridge.registerConfiguration();
		// register the current object as listener for configuration of islamic
		// calendar data
		CalConfigCMBridge calConfigCMBridge = CalConfigCMBridge.getInstance();
		calConfigCMBridge.getBackendDataCache().init(null,
				BackendAvailability.NOT_AVAILABLE);
		calConfigCMBridge.registerConfiguration();
		// register the current object as listener for the country to format
		// mapping
		CountryFormatCMBridge countryFormatCMBridge = CountryFormatCMBridge
				.getInstance();
		countryFormatCMBridge.getBackendDataCache().init(null,
				BackendAvailability.NOT_AVAILABLE);
		countryFormatCMBridge.registerConfiguration();

		// check if the system is double stack system (java stack and abap
		// stack) or not
		if (isDoubleStackSystem()) {
			// if the system consists of abap and java stack
			// set the connection property as "HARD"
			I18NR3BridgeIntf.BRIDGE_STRENGTH = "HARD";
		}
		// initialization of language data
		initialData(languageCMBridge, SAPLanguageR3Bridge.getInstance(), null);
		// initialization of time zone data
		initialData(timezonCMBridge, SAPTimeZoneR3Bridge.getInstance(), null);
		// initialization of calendar data
		initialData(calConfigCMBridge, CalConfigR3Bridge.getInstance(), null);
		// initialization of country format mapping
		initialData(countryFormatCMBridge, CountryFormatR3Bridge.getInstance(),
				null);
	}

	public static void initialData(InputStream inputStream) {

	}

	/**
	 * check if the engine is running within a double stack environment.
	 * 
	 * @return true if the system is double-stack otherwise false
	 */
	private boolean isDoubleStackSystem() {
		ConfigurationHandlerFactory cfgHdlFkry;
		cfgHdlFkry = m_ServiceContext.getCoreContext()
				.getConfigurationHandlerFactory();
		String systemType = cfgHdlFkry.getSystemProfile().getProperty(
				"SYSTEM_TYPE");
		return DOUBLESTACKSYSTEM.equals(systemType) ? true : false;
	}

	public void stop() throws ServiceRuntimeException {

		// Unregister of Timezone's ConfigurationChangedListener
		SAPLanguageCMBridge languageCMBridge = SAPLanguageCMBridge
				.getInstance();
		languageCMBridge.removeListener();
		// Unregister of Timezone's ConfigurationChangedListener
		SAPTimeZoneCMBridge timezoneCMBridge = SAPTimeZoneCMBridge
				.getInstance();
		timezoneCMBridge.removeListener();
		// Unregister of Calendar's ConfigurationChangedListener
		CalConfigCMBridge calConfigCMBridge = CalConfigCMBridge.getInstance();
		calConfigCMBridge.removeListener();

		// Unregister ITSAMMbean
		unregisterITSAMMBean();
		// Unregister local service
		// servCtx.getServiceState().unregisterContainerEventListener();
		I18NSyncFrame.instance = null;

	}

	public static ApplicationServiceContext getServiceContext()
			throws IllegalStateException {
		assertNotStopped();
		return m_ServiceContext;
	}

	public static void initDataFromFile(InputStream inputStream) {
		m_DataFromFile = new BackendDO();
		try {
			m_DataFromFile.initByInputStream(inputStream);
		} catch (IOException e) {
			m_oLog.errorT(m_oLoc,
					"Error initializing backend data cache using "
							+ inputStream);
			m_oLoc.traceThrowableT(Severity.ERROR,
					"Error initializing backend data cache using "
							+ inputStream, e);
		}
	}

	public static BackendDO getDataFromFile() {
		return m_DataFromFile;
	}

	public static void initialData(I18NConfigMngBridge cmBridge,
			I18NR3BridgeIntf r3Bridge, InputStream inputStream) {
		initDataFromFile(inputStream);
		BackendDO data = null;
		int availabilityStatus = BackendAvailability.STATE_OK;
		try {
			data = cmBridge.readData();
			if (data == null) {
				// Build connection to the back-end system and sync these.
				data = r3Bridge.executeGetData();
				// overwrite the status with new value supplied by the bridge
				availabilityStatus = r3Bridge.getAvailabilityStatus();
				// if the data is valid
				if (data != null) {
					// create the root configuration
					cmBridge.createConfiguration();
					/* create sub-configuration for language */
					cmBridge.createSubConfiguration();
					/*
					 * save data in the language-sub-configuration within the
					 * configuration manager
					 */
					cmBridge.storeData(data);
				} else {
					// connection is made successfully
					// the back-end system doesn't contain the needed data.
					availabilityStatus = BackendAvailability.NOT_AVAILABLE;
				}
			}
		} catch (ConfigurationException cfgexception) {
			// in case of configuration Exception
			// initial library with object null and set the corresponding
			// availability status
			cmBridge.getBackendDataCache().init(null,
					BackendAvailability.STATE_DATA_EXCHANGE_ERROR);
			// enter error cause in the log
			m_oLog
					.errorT(m_oLoc,
							"Store/Read of backend data in the Configuration Manager is failed");
			m_oLoc
					.traceThrowableT(
							Severity.ERROR,
							"Store of backend data in the Configuration Manager is failed. Error caused by: ",
							cfgexception);
			/*
			 * It's not necessary to stop the service in this case since this
			 * exception has not to do with the service state.
			 */
		} catch (JCoException jcoexception) {
			// initial library with object null and set the corresponding
			// availability status
			cmBridge.getBackendDataCache().init(null,
					BackendAvailability.STATE_DATA_EXCHANGE_ERROR);
			// enter error cause in the log
			m_oLog.errorT(m_oLoc, "The start of the I18NService is failed");
			m_oLoc.traceThrowableT(Severity.ERROR,
					"Service start is failed. Error caused by: ", jcoexception);
		}
		// update cache for library
		cmBridge.getBackendDataCache().init(data, availabilityStatus);
	}

	/**
	 * Register MBean into the MBeanServer
	 * 
	 * @throws SerivceException
	 *             in case of registration problem
	 */
	private void registerITSAMMBean() throws ServiceException {
		// check if the MBean is already registered
		if (!m_ITSAMMBeanRegistered) {
			try {
				MBeanServer mbs = (MBeanServer) m_ServiceContext
						.getContainerContext().getObjectRegistry()
						.getServiceInterface("jmx");
				// build MBean's object-name
				ObjectName i18nObjName = getObjectNameForMBeanServer(mbs);
				if (!mbs.isRegistered(i18nObjName)) {
					SAP_ITSAMI18NServiceWrapper i18nServiceWrapper = new SAP_ITSAMI18NServiceWrapper(
							new SAP_ITSAMI18NService_Impl());
					mbs.registerMBean(i18nServiceWrapper, i18nObjName);
					m_ITSAMMBeanRegistered = true;
				}
			} catch (Exception excep) {
				m_oLog.errorT(m_oLoc,
						"Error while registering MBean in MBeanServer. Exception message: "
								+ excep.getMessage());
				m_oLoc
						.traceThrowableT(
								Severity.ERROR,
								"Error while registration of the MBean in MBeanServer. Error caused by: ",
								excep);
				// throw ServiceException to stop service start if the
				// registration of the service is failed
				throw new ServiceException(m_oLoc, excep);
			}
		}
	}

	private void unregisterITSAMMBean() {
		String method = "unregisterITSAMMBean";
		if (m_ITSAMMBeanRegistered) {
			MBeanServer mbs = (MBeanServer) m_ServiceContext
					.getContainerContext().getObjectRegistry()
					.getServiceInterface("jmx");
			if (mbs == null) {
				m_oLoc.exiting(method);
				return;
			}
			try {
				mbs.unregisterMBean(m_MBeanName);
				// set registration state to false
				m_ITSAMMBeanRegistered = false;
			} catch (Exception exc) {
				m_oLoc.catching(exc);
				m_oLoc.exiting(exc);
			}
		}
	}

	/*
	 * Build MBean's Objectname
	 */
	private ObjectName getObjectNameForMBeanServer(MBeanServer mbs)
			throws Exception {
		ObjectName j2eeClusterPattern = new ObjectName(
				"*:cimclass=SAP_ITSAMJ2eeCluster,*");

		Set<ObjectName> result = mbs.queryNames(j2eeClusterPattern, null);
		if ((result == null) || (result.size() == 0)) {
			throw new Exception("SAP_ITSAMJ2eeCluster MBean is missing");
		}
		ObjectName j2eeCluster = (ObjectName) result.iterator().next();
		String j2eeClusterName = j2eeCluster
				.getKeyProperty("SAP_ITSAMJ2eeCluster.Name");
		String j2eeClusterCreationClassName = j2eeCluster
				.getKeyProperty("SAP_ITSAMJ2eeCluster.CreationClassName");

		m_MBeanName = new ObjectName(
				"com.sap.default:version=3.3,"
						+ "cimclass=SAP_ITSAMI18NService,"
						+ "type=SAP_ITSAMJ2eeCluster.SAP_ITSAMI18NService,"
						+ "SAP_ITSAMI18NService.Name=I18NService,"
						+ "SAP_ITSAMI18NService.CreationClassName=SAP_ITSAMI18nService,"
						+ "SAP_ITSAMI18Nservice.SystemName=" + j2eeClusterName
						+ "," + "SAP_ITSAMI18NService.SystemCreationClassName"
						+ "=SAP_ITSAMJ2eeCluster,"
						+ "SAP_ITSAMJ2eeCluster.Name=" + j2eeClusterName + ","
						+ "SAP_ITSAMJ2eeCluster.CreattionClassName="
						+ j2eeClusterCreationClassName);
		return m_MBeanName;
	}

	private static void assertNotStopped() {
		if (instance == null && !m_IsUnitTestStatus) {
			// throw runtime exception it the service is in illegal state
			throw new IllegalStateException("I18NService is stopped");
		}
	}

}
