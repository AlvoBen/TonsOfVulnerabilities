/*
 * SAPMarkets Copyright (c) 2001
 * All rights reserved
 *
 * @version $Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sapmarkets/tpd/TradingPartnerDirectoryCommon.java#1 $
 */

package com.sapmarkets.tpd;

//  import com.sapmarkets.tpd.master.tradingpartner.TradingPartnerDirectory;
//  import com.sapmarkets.tpd.master.businesspartner.BusinessPartnerDirectory;

import com.sapmarkets.tpd.master.PartnerID;
import com.sapmarkets.tpd.master.TradingPartnerDirectoryInterface;
import com.sapmarkets.tpd.util.TpdException;
//import com.sap.security.core.InternalUMFactory;
//import com.sap.security.core.util.IUMTrace;

/**
 * TPD Factory.
 *
 * @created   June 21, 2001
 * @author    Rajeev Madnawat
 * @author    Martin Stein
 * @author    Jimmy Wong
 *
 * <h3>Usage: </h3> <p>
 *
 *      Will load correct Trading Partner directory depending on setting in mysap.properties file.
 *      If an entry <font face="Courier">UM_USE_SAP_BD</font> exists, the SAP Business Directory
 *      will be used, otherwise the CommerceOne TPD. </p> <code>TradingPartnerDirectoryInterface t =
 *      TradingPartnerDirectoryCommon.getTPD ();<br>
 *      t.getPartners();</code> <h3><br>
 *      Compilation:<br>
 *      </h3> <code>make -k COLLECTIONSJAR=//pal100792/c1_integration/collections.jar
 *      SERVLETJAR=//pal100792/c1_integration/servlet.jar
 *      JSPJAR=../../../../../jars/tradingPartnerDirectory/jsp.jar </code> <h3> <br>
 *      Dependencies:</h3> <p>
 *
 *      msb_tpdapi.jar - C1 Marketsite Builder TPD API<br>
 *      ccs_util.jar - C1 ?<br>
 *      COLLECTIONSJAR - Sun JDK 118 collections package<br>
 *      SERVLETJAR - servlet.jar<br>
 *      JSPJAR - jsp.jar<br>
 *      LDAP - ldapjdk.jar<br>
 *      SAX - parser.jar</p> <p>
 *
 *      You need to set properties for the MSB TPD API. It needs to know its database driver
 *      (UNA2000_Application.zip) and the connection to the database through properties. The
 *      mysap.properties file will be loaded by the factory.</p>
 */

public class TradingPartnerDirectoryCommon
{
	public final static String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sapmarkets/tpd/TradingPartnerDirectoryCommon.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
	//private final static IUMTrace trace ;

//      private static TradingPartnerDirectoryInterface tpd = null;
	private static TradingPartnerDirectoryInterface tpdi;
	
	static
	{
		//trace = InternalUMFactory.getTrace(VERSIONSTRING);
	}

    public static void initialize(Class tpdclass) throws InstantiationException, IllegalAccessException
    {
    		tpdi = (TradingPartnerDirectoryInterface) tpdclass.newInstance();
    }


    public synchronized static PartnerID createPartnerID(String partnerID)
            throws TpdException {
        if (tpdi == null) {
            throw new TpdException("Please call getTPD() first.");
        }
        return tpdi.createPartnerID(partnerID);
    }

    /**
     * Factory method to get the currently active TPD. Depending on the properties file the
     * appropriate TPD is selected.
     *
     * @return   The TPD value
     */
    public synchronized static TradingPartnerDirectoryInterface getTPD()
    {
		return tpdi;
    }
}
