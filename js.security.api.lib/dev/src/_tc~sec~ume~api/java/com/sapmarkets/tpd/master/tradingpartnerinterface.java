package com.sapmarkets.tpd.master;

import java.io.Serializable;
import com.sapmarkets.tpd.util.TpdException;

/**
 *  Describes a trading partner. Access methods to read one only. No creation.
 *
 *@author     i080580
 *@created    July 25, 2001
 */

public interface TradingPartnerInterface extends Serializable {


	/**
	 *  Gets the partnerID attribute of the TradingPartnerInterface object.
	 *
	 *@return    The partnerID value
	 */
	public PartnerID getPartnerID();

	/**
	 *  Gets the displayName attribute of the TradingPartnerInterface object
	 *
	 *@return    The displayName value
	 */
	public String getDisplayName();

	/**
	 * Gets the technically role IDs of the TradingPartnerInterface object
	 *
	 * @return the role IDs
	 * @exception TpdException
	 */
	public String[] getRoleIDs() throws TpdException;

	/**
	 *  Compare one TP to another.
	 *
	 *@param  otherPartner  Description of Parameter
	 *@return               The sameTradingPartner value
	 */
	public boolean isSameTradingPartner(TradingPartnerInterface otherPartner);

	/**
	 * Gets the group names of this trading partner
	 *
	 * @return array of group names
	 * @deprecated
	 */
	public String[] getGroupNames();


	// New methods.
	/**
	 *  Gets the address attribute of the TradingPartnerInterface object
	 *
	 *@return    The address value
	 *@deprecated not used or supported by UME
	 */
	public AddressInterface getAddress();


	/**
	 *  Gets the createTimestamp attribute of the TradingPartnerInterface object
	 *
	 *@return    The createTimestamp value
	 *@deprecated not used or supported by UME
	 */
	public String getCreateTimestamp();


	/**
	 *  Gets the currency attribute of the TradingPartnerInterface object
	 *
	 *@return    The currency value
	 *@deprecated not used or supported by UME
	 */
	public String getCurrency();


	/**
	 *  Gets the description attribute of the TradingPartnerInterface object
	 *
	 *@return    The description value
	 *@deprecated not used or supported by UME
	 */
	public String getDescription();


	/**
	 *  Gets the disabled attribute of the TradingPartnerInterface object
	 *
	 *@return    The disabled value
	 *@deprecated not used or supported by UME
	 */
	public boolean getDisabled();



	/**
	 *  Gets the email attribute of the TradingPartnerInterface object
	 *
	 *@return    The email value
	 *@deprecated not used or supported by UME
	 */
	public String getEmail();


	/**
	 *  Gets the fax attribute of the TradingPartnerInterface object
	 *
	 *@return    The fax value
	 *@deprecated not used or supported by UME
	 */
	public String getFax();


	/**
	 *  Gets the language attribute of the TradingPartnerInterface object
	 *
	 *@return    The language value
	 *@deprecated not used or supported by UME
	 */
	public String getLanguage();


	/**
	 *  Gets the logoURL attribute of the TradingPartnerInterface object
	 *
	 *@return    The logoURL value
	 *@deprecated not used or supported by UME
	 */
	public String getLogoURL();


	/**
	 *  Gets the modifyTimestamp attribute of the TradingPartnerInterface object
	 *
	 *@return    The modifyTimestamp value
	 *@deprecated not used or supported by UME
	 */
	public String getModifyTimestamp();



	/**
	 *  Gets the tandC attribute of the TradingPartnerInterface object
	 *
	 *@return    The tandC value
	 *@deprecated not used or supported by UME
	 */
	public String getTandC();


	/**
	 *  Gets the telephone attribute of the TradingPartnerInterface object
	 *
	 *@return    The telephone value
	 *@deprecated not used or supported by UME
	 */
	public String getTelephone();


	/**
	 *  Gets the timezone attribute of the TradingPartnerInterface object
	 *
	 *@return    The timezone value
	 *@deprecated not used or supported by UME
	 */
	public String getTimezone();


	/**
	 *  Gets the tPOrderBy attribute of the TradingPartnerInterface object
	 *
	 *@return    The tPOrderBy value
	 *@deprecated not used or supported by UME
	 */
	public String getTPOrderBy();


	/**
	 *  Gets the tPShortName attribute of the TradingPartnerInterface object
	 *
	 *@return    The tPShortName value
	 *@deprecated not used or supported by UME
	 */
	public String getTPShortName();


	/**
	 *  Gets the type attribute of the TradingPartnerInterface object
	 *
	 *@return    The type value
	 *@deprecated not used or supported by UME
	 */
	public String getType();


	/**
	 *  Gets the visibility attribute of the TradingPartnerInterface object
	 *
	 *@return    The visibility value
	 *@deprecated not used or supported by UME
	 */
	public String getVisibility();


	/**
	 *  Gets the website attribute of the TradingPartnerInterface object
	 *
	 *@return    The website value
	 *@deprecated not used or supported by UME
	 */
	public String getWebsite();


	/**
	 *  Get Dun & Bradstreet (DUNS) number.
	 *
	 *@return    The dUNS value
	 *@deprecated not used or supported by UME
	 */
	public String getDUNS();


	// Old methods.  The following methods are deprecated
	/**
	 * Gets the id attribute of the TradingPartnerInterface object
	 *
	 * @return The id value
	 * @deprecated Please use getPartnerID instead.  Then call
     *  PartnerID.toString() to get the String representation of the ID.
	 */
	public java.lang.String getId();

	/**
	 * Gets the name attribute of the TradingPartnerInterface object
	 *
	 * @return The name value
     * @deprecated Use getDisplayName instead.
	 */
	public java.lang.String getName();

	/**
     * Get the marketplace ID.
	 * @return The mpid value
	 * @deprecated Please use getPartnerID instead.  Then call
     *  PartnerID.toString() to get the String representation of the ID.
	 */
	public String getMpid();

	/**
	 * Gets the orgId attribute of the TradingPartnerInterface object
	 *
	 * @return The orgId value
	 * @deprecated Please use getPartnerID instead.  Then call
     *  PartnerID.toString() to get the String representation of the ID.
	 */
	public String getOrgId();
}
