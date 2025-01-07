package com.sap.engine.services.dc.util;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sap.sql.DatabaseServices;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-15
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class DBUtils {
	
	private static Location location = getLocation(DBUtils.class);

	private static final SimpleDateFormat sdFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public static Date getDateFromDB(Connection connection, String description)
			throws SQLException {
		long ms = System.currentTimeMillis();
		try {
			return DatabaseServices.getUTCTimestamp(connection);
		} finally {
			if (location.beDebug()) {
				traceDebug(location,
						"[{0}] Date taken from DB for [{1}] ms.",
						new Object[] { description,
								(System.currentTimeMillis() - ms) });
			}
		}
	}

	public static String getFormatedDateFromDB(Connection connection,
			String description) throws SQLException {
		final Date date = getDateFromDB(connection, description);

		return sdFormat.format(date);
	}

}
