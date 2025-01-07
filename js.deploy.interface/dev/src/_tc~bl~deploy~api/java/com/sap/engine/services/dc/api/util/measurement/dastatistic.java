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
package com.sap.engine.services.dc.api.util.measurement;

import java.io.Serializable;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Statistic</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 11, 2009</DD>
 * </DL>
 * 
 * @author Radoslav Ivanov(i031258)
 * @version 1.0
 * @since 7.20
 */

public interface DAStatistic extends Serializable {
	/**
	 * Returns statistic's type
	 * @return statistic's type
	 */
	DAStatisticType getType();
	
	/**
	 * Returns statistic's consumed value
	 * @return statistic's consumed value
	 */
	Long getValue();

}
