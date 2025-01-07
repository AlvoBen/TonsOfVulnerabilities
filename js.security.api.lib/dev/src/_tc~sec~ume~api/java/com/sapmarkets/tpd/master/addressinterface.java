package com.sapmarkets.tpd.master;

import java.util.*;
import java.io.Serializable;

/**
 *  Address type equivalent to C1 address. Creation date: (11/7/2000 3:23:37 PM)
 *
 *@author     i080580
 *@created    July 26, 2001
 *@author:    Martin Stein
 */
public interface AddressInterface extends Serializable {

	/**
	 *  Sets the country attribute of the AddressInterface object
	 *
	 *@param  s  The new country value
	 */
	public void setCountry(String s);


	/**
	 *  Sets the postalCode attribute of the AddressInterface object
	 *
	 *@param  s  The new postalCode value
	 */
	public void setPostalCode(String s);


	/**
	 *  Sets the streetSupplement attribute of the AddressInterface object
	 *
	 *@param  s  The new streetSupplement value
	 */
	public void setStreetSupplement(String s);


	/**
	 *  Sets the roomNumber attribute of the AddressInterface object
	 *
	 *@param  s  The new roomNumber value
	 */
	public void setRoomNumber(String s);


	/**
	 *  Sets the department attribute of the AddressInterface object
	 *
	 *@param  s  The new department value
	 */
	public void setDepartment(String s);


	/**
	 *  Sets the floor attribute of the AddressInterface object
	 *
	 *@param  s  The new floor value
	 */
	public void setFloor(String s);


	/**
	 *  Sets the building attribute of the AddressInterface object
	 *
	 *@param  s  The new building value
	 */
	public void setBuilding(String s);


	/**
	 *  Sets the poBox attribute of the AddressInterface object
	 *
	 *@param  s  The new poBox value
	 */
	public void setPoBox(String s);


	/**
	 *  Sets the houseNumber attribute of the AddressInterface object
	 *
	 *@param  s  The new houseNumber value
	 */
	public void setHouseNumber(String s);


	/**
	 *  Sets the district attribute of the AddressInterface object
	 *
	 *@param  s  The new district value
	 */
	public void setDistrict(String s);


	/**
	 *  Sets the mailStop attribute of the AddressInterface object
	 *
	 *@param  s  The new mailStop value
	 */
	public void setMailStop(String s);


	/**
	 *  Sets the street attribute of the AddressInterface object
	 *
	 *@param  strStreet  The new street value
	 */
	public void setStreet(String strStreet);


	/**
	 *  Sets the city attribute of the AddressInterface object
	 *
	 *@param  s  The new city value
	 */
	public void setCity(String s);


	/**
	 *  Sets the state attribute of the AddressInterface object
	 *
	 *@param  s  The new state value
	 */
	public void setState(String s);


	/**
	 *  Sets the createTimestamp attribute of the AddressInterface object
	 *
	 *@param  s  The new createTimestamp value
	 */
	public void setCreateTimestamp(java.util.Date s);


	/**
	 *  Sets the modifyTimestamp attribute of the AddressInterface object
	 *
	 *@param  s  The new modifyTimestamp value
	 */
	public void setModifyTimestamp(java.util.Date s);


	/**
	 *  Gets the externalAddressId attribute of the AddressInterface object
	 *
	 *@return    The externalAddressId value
	 */
	public String getExternalAddressId();


	/**
	 *  Gets the country attribute of the AddressInterface object
	 *
	 *@return    The country value
	 */
	public String getCountry();


	/**
	 *  Gets the postalCode attribute of the AddressInterface object
	 *
	 *@return    The postalCode value
	 */
	public String getPostalCode();


	/**
	 *  Gets the city attribute of the AddressInterface object
	 *
	 *@return    The city value
	 */
	public String getCity();


	/**
	 *  Gets the state attribute of the AddressInterface object
	 *
	 *@return    The state value
	 */
	public String getState();


	/**
	 *  Gets the streetSupplement attribute of the AddressInterface object
	 *
	 *@return    The streetSupplement value
	 */
	public String getStreetSupplement();


	/**
	 *  Gets the roomNumber attribute of the AddressInterface object
	 *
	 *@return    The roomNumber value
	 */
	public String getRoomNumber();


	/**
	 *  Gets the department attribute of the AddressInterface object
	 *
	 *@return    The department value
	 */
	public String getDepartment();


	/**
	 *  Gets the floor attribute of the AddressInterface object
	 *
	 *@return    The floor value
	 */
	public String getFloor();


	/**
	 *  Gets the street attribute of the AddressInterface object
	 *
	 *@return    The street value
	 */
	public List getStreet();


	/**
	 *  Sets the street attribute of the AddressInterface object
	 *
	 *@param  alStreet  The new street value
	 */
	public void setStreet(List alStreet);


	//why not? public String getStreet();
	/**
	 *  Gets the building attribute of the AddressInterface object
	 *
	 *@return    The building value
	 */
	public String getBuilding();


	/**
	 *  Gets the poBox attribute of the AddressInterface object
	 *
	 *@return    The poBox value
	 */
	public String getPoBox();


	/**
	 *  Gets the houseNumber attribute of the AddressInterface object
	 *
	 *@return    The houseNumber value
	 */
	public String getHouseNumber();


	/**
	 *  Gets the district attribute of the AddressInterface object
	 *
	 *@return    The district value
	 */
	public String getDistrict();


	/**
	 *  Gets the mailStop attribute of the AddressInterface object
	 *
	 *@return    The mailStop value
	 */
	public String getMailStop();


	/**
	 *  Gets the createTimestamp attribute of the AddressInterface object
	 *
	 *@return    The createTimestamp value
	 */
	public java.util.Date getCreateTimestamp();


	/**
	 *  Gets the modifyTimestamp attribute of the AddressInterface object
	 *
	 *@return    The modifyTimestamp value
	 */
	public java.util.Date getModifyTimestamp();


	/**
	 *  Sets the externalAddressId attribute of the AddressInterface object
	 *
	 *@param  s  The new externalAddressId value
	 */
	public void setExternalAddressId(String s);


	/**
	 *  Gets the addressType attribute of the AddressInterface object
	 *
	 *@return    The addressType value
	 */
	public String getAddressType();


	/**
	 *  Gets the fax attribute of the AddressInterface object
	 *
	 *@return    The fax value
	 */
	public String getFax();


	/**
	 *  Gets the province attribute of the AddressInterface object
	 *
	 *@return    The province value
	 */
	public String getProvince();


	/**
	 *  Gets the telephone attribute of the AddressInterface object
	 *
	 *@return    The telephone value
	 */
	public String getTelephone();


	/**
	 *  Sets the addressType attribute of the AddressInterface object
	 *
	 *@param  a  The new addressType value
	 */
	public void setAddressType(String a);


	/**
	 *  Sets the fax attribute of the AddressInterface object
	 *
	 *@param  f  The new fax value
	 */
	public void setFax(String f);


	/**
	 *  Sets the province attribute of the AddressInterface object
	 *
	 *@param  p  The new province value
	 */
	public void setProvince(String p);


	/**
	 *  Sets the telephone attribute of the AddressInterface object
	 *
	 *@param  t  The new telephone value
	 */
	public void setTelephone(String t);


	/**
	 *  Gets the email attribute of the AddressInterface object
	 *
	 *@return    The email value
	 */
	public String getEmail();


	/**
	 *  Sets the email attribute of the AddressInterface object
	 *
	 *@param  f  The new email value
	 */
	public void setEmail(String f);
}
