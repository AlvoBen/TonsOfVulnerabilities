/*
 *  Copyright:   Copyright (c) 2001
 *
 *  Company:     SAPMarkets, Inc.
 *               Palo Alto, California, 94303, U.S.A.
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAPMarkets Inc. ("Confidential Information").  You shall
 *               not disclose such Confidential Information and shall use it
 *               only in accordance with the terms of the license agreement
 *               you entered into with SAPMarkets.
 */
package com.sapmarkets.tpd.master;

import java.util.Iterator;

/**
 * An interface that contains a result set of partners
 *
 * @author Jimmy Wong
 * @created December 14, 2001
 */
public interface PartnerResultSet {
	// Returns an iterator of partners
	public Iterator partnerIterator();

	// Returns true if the PartnerResultSet of partners is complete otherwise false
	public boolean isComplete();

	// Returns the number partners returned in this PartnerResultSet.
	public int getSize();
}
