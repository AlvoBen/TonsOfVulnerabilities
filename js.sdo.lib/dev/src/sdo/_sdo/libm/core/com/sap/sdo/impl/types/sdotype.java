/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.types;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import com.sap.sdo.api.types.SapType;
import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public interface SdoType<S> extends SapType, TypeAndContext, Serializable
{
    /**
	 * convert to local type if possible or throw an IllegalArgumentException
	 * otherwise. Abstracts <code>set&lt;T&gt;(...)</code>calls.
	 * <br>
     * If the conversion cannot be made, the method should return <code>null</code>
	 */
	S convertFromJavaClass(Object data);
    /**
     * convert to external type if possible or throw an IllegalArgumentException
     * otherwise. Abstracts <code>get&lt;T&gt;(...)</code>calls.
     * <br>
     * If the conversion cannot be made, the method should return <code>null</code>
     */
    <T> T convertToJavaClass(S data, Class<T> targetType);

    /**
     * create a deep copy of the object given that is expected a representation
     * of this type. Note: In case of immutables, it is legal to return the
     * very same instance.
     * @param shallow TODO
     *
     * @return a copy of the given representation of this data type
     */
    S copy(S o, boolean shallow);

    Invoker getInvokerForMethod(Method method);

    /**
     * Turns on caching of the PropertyName-Property map.  Should be called from
     * define().
     *
     */

    boolean defined();

    void useCache();
    /**
     * Determines if the type allows mixed content.  In the SDO MetaModel, this means,
     * that the type is descended from TextType.
     * @return true if mixed content is allowed.
     */
	boolean isMixedContent();

	/**
	 * Gets a string that uniquely identifies the given object among instances of this type.
	 * The value will be either that of the object's first property of type "ID" or, if no
	 * such property is found, will be the value of Object.hashCode.
	 * @param dataObject
	 * @return a string.
	 * @throws IllegalArgumentException if the argement is not of the specified type.
     * @deprecated Does not work with complex keys.
	 */
    @Deprecated
	String getId(DataObject dataObject);

    /**
     * Return the index of this type's ChangeSummary property, if there is one, or -1 if none is found.
     * @return the index of the object's ChangeSummary property, or -1 if there is none.
     */
    int getCsPropertyIndex();

    /**
     * Return the index of this type's XSD property, if there is one, or -1 if none is found.
     * @return the index of the object's XSD property, or -1 if there is none.
     */
    int getXsdPropertyIndex();

    /**
	 * Return the default value for data of this type. In most cases this will
	 * be <code>null</null>. For Java simple types like <code>int</null> the
	 * result is the default value in Java e.g. <code>Integer.valueOf(0)</code>
	 * and not <code>null</code>.
	 * @return the default value for data of this type.
	 */
	S getDefaultValue();

    SdoProperty getPropertyFromXmlName(String uri, String xmlName, boolean isElement);
    Property getPropertyFromJavaMethodName(String javaMethodName);
    Property getPropertyFromJavaName(final String javaPropertyName);
    Object putExtraData(String group, String item, Object value);

    boolean getAttributeFormDefaultQualified();
    boolean getElementFormDefaultQualified();

    DataObject getFacets();

    List<SdoProperty> getSingleOppositeProperties();

    boolean isLocal();

    List<SdoProperty> getOrphanHolderProperties();

    /**
     * If the type has a key type, this method returns the type, the key is
     * unique for. This could be this type or a base type.
     * If the id-property has the xsd-type "xsd:id" then the result is
     * the type commonj.sdo#DataObject.
     * @return null if there is no key or the type as described above.
     */
    SdoType getTypeForKeyUniqueness();
    List<SdoProperty> getKeyProperties();

    /**
     * Fast check if the Type has an XML friendly key.
     * null means no key.
     * true means the parser can determine the key at the start element.
     * false means the parser can determine the key first at the end element.
     * @return null for no key, true or false.
     */
    Boolean hasXmlFriendlyKey();

    /**
     * If this is a complex type that is an extension of a simple type, this
     * method is a fast way to return the simple-content "value"-property.
     * @return the simple-content "value"-property or null.
     */
    SdoProperty getSimpleContentValueProperty();

    /**
     * If this (simple) type restricts either xsd:QName or xsd:base64Binary,
     * this method is a fast way to return that special base type.
     * @return the special base type or null.
     */
    URINamePair getSpecialBaseType();
}
