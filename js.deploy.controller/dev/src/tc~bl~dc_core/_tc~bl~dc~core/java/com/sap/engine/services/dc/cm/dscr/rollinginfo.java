package com.sap.engine.services.dc.cm.dscr;

import java.io.Serializable;

/**
 * Describes the proprties of the updated item.
 * 
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface RollingInfo extends Serializable {

	/**
	 * Gets the updated item name.
	 * 
	 * @return <code>String</code>
	 * @deprecated
	 */
	public String getItemName();

	/**
	 * Gets the updated item type.
	 * 
	 * @return <code>int</code>; -1 means type is not set.
	 * @deprecated
	 */
	public byte getItemType();

}
