/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class ConfigurationWrapper {

	// A configuration name
	private static final String CFG_PROPERTIES = "_properties";
	// A configuration entry
	private static final String CFG_DATE = "Created";

	private static final SimpleDateFormat sdFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private final Configuration cfg;

	/**
	 * 
	 * @param cfg
	 * @param connection
	 * @throws ConfigurationException
	 * @throws SQLException
	 */
	public ConfigurationWrapper(Configuration cfg, Connection connection,
			String description) throws ConfigurationException, SQLException {
		this.cfg = cfg;
		setProperties(connection, description);
	}

	private void setProperties(Connection connection, String description)
			throws ConfigurationException, SQLException {
		if (!this.cfg.existsSubConfiguration(CFG_PROPERTIES)) {
			final Configuration propCfg = this.cfg
					.createSubConfiguration(CFG_PROPERTIES);
			final String dateString = DBUtils.getFormatedDateFromDB(connection,
					description);
			propCfg.addConfigEntry(CFG_DATE, dateString);
		}
	}

	private Date getData() throws ConfigurationException, ParseException {
		final Configuration propCfg = this.cfg
				.getSubConfiguration(CFG_PROPERTIES);
		final String dateString = (String) propCfg.getConfigEntry(CFG_DATE);
		final Date date = sdFormat.parse(dateString);
		return date;
	}

	/**
	 * Returns true if this <code>Configuration</code> is older than 24 hours,
	 * otherwise false.
	 * 
	 * @param connection
	 * @return <code>boolean</code>
	 * @throws ConfigurationException
	 * @throws ParseException
	 * @throws SQLException
	 */
	public boolean isOlderThan24Hours(Connection connection, String description)
			throws ConfigurationException, ParseException, SQLException {
		final int hours = 24;
		final Date creationDate = getData();
		final Date currDate = DBUtils.getDateFromDB(connection, description);

		return DateUtils.isOlderThan(hours, creationDate, currDate);
	}

}
