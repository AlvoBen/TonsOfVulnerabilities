package com.sapmarkets.tpd.master;

import java.util.Enumeration;

import com.sapmarkets.tpd.util.TpdException;
import java.util.*;
/**
 *  Description of the Interface
 *
 *@author     i080580
 *@author     Jimmy Wong
 *@created    July 25, 2001
 */
public interface TradingPartnerDirectoryInterface {

    public PartnerID createPartnerID(String partnerID)
            throws TpdException;

	/**
	 *  Returns a <code>TradingPartner</code> object.
	 *
	 *@param  OrgID             Description of Parameter
	 *@return                   <code>TradingPartner</code> object containing
	 *      information for the trading partner identified by the id
	 *@exception  TpdException  Description of Exception
	 *@deprecated use getPartner(PartnerID)
	 */
	public TradingPartnerInterface getPartner(String OrgID) throws TpdException;


  	/**
  	 *  Returns an <code>Enumeration</code> of all <code>TradingPartner</code>s in
  	 *  the TPD.
  	 *
  	 *@return                   all the trading partners in the TPD
  	 *@exception  TpdException  Description of Exception
	 *@deprecated using <code>getPartners(STring serachString, int max)</code>
  	 */
  	public Enumeration getPartners() throws TpdException;
	
	/**
	 *@deprecated using <code>getPartners(STring serachString, int max)</code>
	 */
  	public Enumeration getPartners(String searchString) throws TpdException;

	/**
	 *  Returns an <code>Enumeration</code> of <code>TradingPartner</code> objects
	 *  that match the search string provided.
	 *
	 *@param  searchString      Description of Parameter
	 *@param  max               max number of partners returned
	 *@return                   an <code>Enumeration</code> of <code>TradingPartner</code>
	 *      objects
	 *@exception  TpdException  Description of Exception
	 */
	public PartnerResultSet getPartners(String searchString, int max) throws TpdException;

    /**
     * Returns true if a partner with the specified ID exists in the directory.
     *
     * @param partnerID the partner ID
     * @exception TpdException TpdException
     */
    public boolean containsPartner(PartnerID partinerID) throws TpdException;

	/**
	 *  Gets the partnersByAddress attribute of the
	 *  TradingPartnerDirectoryInterface object
	 *
	 *@param  address                                   Description of Parameter
	 *@param  city                                      Description of Parameter
	 *@param  state                                     Description of Parameter
	 *@param  country                                   Description of Parameter
	 *@param  postal                                    Description of Parameter
	 *@param  email                                     Description of Parameter
	 *@return                                           The partnersByAddress value
	 *@exception  com.sapmarkets.tpd.util.TpdException  Description of Exception
	 *@deprecated
	 */
	public Enumeration getPartnersByAddress(
			String address,
			String city,
			String state,
			String country,
			String postal,
			String email)
			 throws com.sapmarkets.tpd.util.TpdException;


	/**
	 *  Gets the partner attribute of the TradingPartnerDirectoryInterface object
	 *
	 *@param  anID              Description of Parameter
	 *@return                   The partner value
	 *@exception  TpdException  Description of Exception
	 */
	public TradingPartnerInterface getPartner(PartnerID anID) throws TpdException;


	/**
	 *  Gets the partnerName attribute of the TradingPartnerDirectoryInterface
	 *  object
	 *
	 *@param  _id               Description of Parameter
	 *@return                   The partnerName value
	 *@exception  TpdException  Description of Exception
	 */
	public String getPartnerName(PartnerID _id) throws TpdException;


	/**
	 *  Search for a trading partner given a sample TP. Create the sampel TP, fill
	 *  in the data you are looking for and then call this method.
	 *
	 *@param  sampleTP          Description of Parameter
	 *@return                   Description of the Returned Value
	 *@exception  TpdException  Description of Exception
	 *@return                   List of TradingPartnerInterface objects.
	 *@deprecated
	 */
	public List findTradingPartners(TradingPartnerInterface sampleTP) throws TpdException;


	/**
	 *  Gets the allBuyers attribute of the TradingPartnerDirectoryInterface object
	 *
	 *@param  buyertp           Description of Parameter
	 *@return                   The allBuyers value
	 *@exception  TpdException  Description of Exception
	 *@deprecated
	 */
	public List getAllBuyers(TradingPartnerInterface buyertp) throws TpdException;

}
