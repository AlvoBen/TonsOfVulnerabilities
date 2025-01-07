package com.sap.sdo.api.types;

import com.sap.sdo.api.util.URINamePair;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public interface SapType extends Type {

    /**
     * Returns the URINamePair of the type.
     * @return the URINamePair.
     */
    URINamePair getQName();

	/**
	 * Return the index of this type's ID property, if there is one, or -1 if none is found.
	 * @return the index of the object's ID property, or -1 if there is none.
     * @deprecated Use the new key - features!
	 */
    @Deprecated
	int getIdPropertyIndex();

	/**
	 * Determines if the type represented by this
	 * <code>Type</code> object is either the same as, or is a supertype
	 * of, the type represented by the specified
	 * <code>Type</code> parameter. It returns <code>true</code> if so;
	 * otherwise it returns <code>false</code>. If this <code>Type</code>
	 * object represents a primitive type, this method returns
	 * <code>true</code> if the specified <code>Type</code> parameter is
	 * exactly this <code>Type</code> object; otherwise it returns
	 * <code>false</code>.
	 * 
	 * @param assignableFrom the <code>Type</code> object to be checked
	 * @return the <code>boolean</code> value indicating whether objects of the
	 * type <code>assignableFrom</code> can be assigned to objects of this type
	 */
	boolean isAssignableType(Type assignableFrom);

	Object getExtraData(String group, String item);

	/**
     * Returns the XML-name of this type. The result is never null.
     * @return the XML-name.
	 */
    String getXmlName();

    /**
     * Returns the URI of this type. The result is the same as {@link Type#getURI()}
     * returns ecxept the result would be null. In that case the result is an
     * empty String.
     * @return the XML-URI, never null.
     */
    String getXmlUri();

    /**
	 * Returns the HelperContext with the TypeHelper that has created this type.
	 * For build-in types the result is the core-HelperContext.
	 * @return The HelperContext.
	 */
	HelperContext getHelperContext();
    
    /**
     * Returns the key type of this type, or null, if the type does not have any key properties.
     * @return the key type of this type.
     */
    Type getKeyType();

}