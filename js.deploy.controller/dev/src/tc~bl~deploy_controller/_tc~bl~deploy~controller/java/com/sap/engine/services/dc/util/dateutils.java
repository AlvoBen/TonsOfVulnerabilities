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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public final class DateUtils {

	public static boolean isOlderThan(int hours, Date creationDate,
			Date currDate) {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(creationDate);
		calendar.add(Calendar.HOUR, hours);
		final Date forCheckData = calendar.getTime();

		if (forCheckData.before(currDate)) {
			return true;
		}

		return false;
	}

}
