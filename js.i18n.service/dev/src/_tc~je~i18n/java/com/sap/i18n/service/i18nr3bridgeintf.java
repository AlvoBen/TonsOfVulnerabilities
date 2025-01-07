package com.sap.i18n.service;

import java.util.ArrayList;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import com.sap.i18n.cache.BackendAvailability;
import com.sap.i18n.cache.BackendDO;
import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDO.TableProperties;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public abstract class I18NR3BridgeIntf {

	private static final Location m_oLoc = Location
			.getLocation("com.sap.i18n.service.I18NR3BridgeIntf");
	protected static final Category m_oLog = Category.SYS_SERVER;

	/**
	 * the name of the connection used by I18n to read data from the backend
	 */
	public static final String I18NDESTINATION = "I18NBackendConnection";

	protected int AVAILABILITYSTATUS = -1;

	protected JCoFunction m_oFunction = null;

	/**
	 * BRIDGE_STRENGTH is bridge property indicates if the bridge to the
	 * back-end system is essential (hard) or optional (WEAK - default value).
	 */
	protected static String BRIDGE_STRENGTH = "WEAK";

	/**
	 * Method returns a code as state of the sync of the Cache from the backend
	 * system. The availability of the language is dependent on:
	 * <ul>
	 * <li>if the destination is available on the engine</li>
	 * <li>if the connection to the backend system is built</li>
	 * <li>if the data cache is available on the backend system</li>
	 * <li>if the data exchange is performed successfully </li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getAvailabilityStatus() {
		return AVAILABILITYSTATUS;
	}

	/**
	 * read Internationalization settings from the backend
	 * 
	 * @return the backend data object
	 * @throws JCoException
	 */
	public BackendDO executeGetData() throws JCoException {

		/*
		 * Read data from the inputStream, if given. The data does not come from
		 * the backend via JCO.
		 */
		BackendDO i18nData = I18NSyncFrame.getDataFromFile();
		if (i18nData != null) {
			AVAILABILITYSTATUS = BackendAvailability.STATE_OK;
			return i18nData;
		}

		JCoDestination oDestination = null;
		if (m_oFunction == null) {
			try {
				if (System.getProperty("SAPVM") != null) {
					/*
					 * If true, that's mean we are on ABAP stack environment
					 * SPACE is the default destination on the ABAP stack
					 */
					oDestination = JCoDestinationManager
							.getDestination("SPACE");
				} else {
					oDestination = JCoDestinationManager
							.getDestination(I18NDESTINATION);

				}
			} catch (JCoException jco) {
				// $JL-I18N$
				if (BRIDGE_STRENGTH == "WEAK") {
					m_oLog
							.infoT(
									m_oLoc,
									"Destination "
											+ I18NDESTINATION
											+ " is not available. No data exchange will happen");
					AVAILABILITYSTATUS = BackendAvailability.STATE_NO_DESTINATION;
				} else {
					m_oLog
							.errorT(
									m_oLoc,
									"The Backend connection is not properly initialized; Backend information cannot be exchanged between ABAP and SAP J2EE Engine."
											+ " Destination "
											+ I18NDESTINATION
											+ " is not available.");
					m_oLoc
							.traceThrowableT(
									Severity.ERROR,
									"Backend information cannot be exchanged between ABAP and SAP J2EE Engine.",
									" Destination " + I18NDESTINATION
											+ " is not available.", jco);
					AVAILABILITYSTATUS = BackendAvailability.STATE_DATA_EXCHANGE_ERROR;
				}
				return null;
			}
			JCoRepository repository = oDestination.getRepository();
			if (repository != null) {
				m_oFunction = repository.getFunction(getFunctionName());
			}
		}
		BackendDO info = null;
		if (m_oFunction != null) {
			info = new BackendDO();
			m_oFunction.execute(oDestination);

			ArrayList<TableProperties> tableProps = getBackendDataCache()
					.getTableProperties();
			for (int i = 0; i < tableProps.size(); i++) {
				BackendDO.Table table = getJCOData(tableProps.get(i));
				info.addTable(table);
			}

			AVAILABILITYSTATUS = BackendAvailability.STATE_OK;
		} else {
			if (BRIDGE_STRENGTH == "WEAK") {
				m_oLog.infoT(m_oLoc, "Destination " + I18NDESTINATION
						+ " connects to a system where " + getFunctionName()
						+ " is not available.", "No data exchange will happen");
			} else {
				m_oLog
						.errorT(
								m_oLoc,
								"The Backend connection is not properly initialized; information cannot be exchanged between ABAP and SAP J2EE Engine."
										+ " Destination "
										+ I18NDESTINATION
										+ " connects to a system where "
										+ getFunctionName()
										+ " is not available.");
			}
		}

		/* reset function */
		m_oFunction = null;

		return info;
	}

	/**
	 * 
	 * @return the cache used for this bridge
	 */
	public abstract BackendDataCache getBackendDataCache();

	/**
	 * 
	 * @return the function name connected by JCO
	 */
	public String getFunctionName() {
		return getBackendDataCache().getFunctionModule();
	}


	/**
	 * Read a backend table via JCO.
	 * 
	 * @param tableProps
	 *            the table properties identifying the the ABAP table
	 * @return the backend table received via JCO
	 */
	protected BackendDO.Table getJCOData(TableProperties tableProps) {
		BackendDO.Table table = new BackendDO.Table(tableProps);

		if (m_oFunction != null) {
			JCoParameterList parameter = m_oFunction.getTableParameterList();
			JCoTable jcoTable = parameter.getTable(tableProps
					.getTableParamName());

			for (int i = 0; i < jcoTable.getNumRows(); i++, jcoTable.nextRow()) {

				ArrayList<String> content = new ArrayList<String>();
				for (int field = 0; field < tableProps.size(); field++) {
					content.add(field, jcoTable.getString(tableProps.get(field)
							.getFieldname()));
				}
				BackendDO.Row row = getBackendDataCache().createRow(tableProps,
						content);
				table.add(i, row);
			}
		}
		return table;
	}
}
