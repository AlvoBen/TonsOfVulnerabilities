/*
 *  SAPMarkets Copyright (c) 2001
 *  All rights reserved
 *
 *  @version $Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sapmarkets/tpd/master/PartnerID.java#1 $
 */
package com.sapmarkets.tpd.master;

import java.io.Serializable;
import com.sapmarkets.tpd.TradingPartnerDirectoryCommon;
import com.sapmarkets.tpd.util.TpdException;

/**
 *  Base class for business/trading partner IDs. Used in the getPartnerID() and
 *  getPartner() function of the TradingPartner and TradingPartnerDirectory
 *  interfaces. Allows us to pass IDs in a typesafe manner.
 *
 *@author     i080580
 *@author     Jimmy Wong
 *@created    June 21, 2001
 *@see        TradingPartner and TradingPartnerDirectory interfaces. Creation
 *      date: (11/28/00 2:10:25 PM)
 *@author:    Martin Stein
 */
public abstract class PartnerID implements Serializable {

	private static final long serialVersionUID = 6841711258768774322L;
	/**
	 *  ID constructor.
	 */
	public PartnerID() {
		super();
	}



	/**
	 *  Returns a String that represents the value of this object. Should not be
	 *  called.
	 *
	 *@return    a string representation of the receiver
	 */
	public String toString() {
		return super.toString();
	}


	/**
	 *  Comparison method. FIXME: Not sure if ok.
	 *
	 *@param  obj  TBD: Description of the incoming method parameter
	 *@return      TBD: Description of the outgoing return value
	 */
	public abstract boolean equals(Object obj);


	/**
	 *  Factory method. Accepts a String and creates a new object of PartnerID
	 *
	 *@param  partnerIDasString  The ID as a String.
	 *@return                    A type-safe ID object base class type.
	 *@exception  TpdException   Description of Exception
	 */
	public static PartnerID instantiatePartnerID(String partnerIDasString)
			 throws TpdException {
        return TradingPartnerDirectoryCommon.createPartnerID(partnerIDasString);
	}
}
