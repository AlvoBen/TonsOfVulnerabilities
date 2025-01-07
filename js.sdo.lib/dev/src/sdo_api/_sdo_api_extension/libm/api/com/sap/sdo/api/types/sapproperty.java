package com.sap.sdo.api.types;

import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;

public interface SapProperty extends Property {

	/**
     * Returns true if the property is not represented as an attribute in XML.
     * That means the property will be rendered as an element or is an inlined
     * "value"-property.
     * @return false if it is an attribute, true otherwise.
	 */
    boolean isXmlElement();

    /**
     * Returns the XML-name of this property. The result is never null.
     * @return the XML-name.
     */
	String getXmlName();

	/**
     * If the original XSD type of the property can not be derived form the
     * property's type, the result is the URINamePair of XSD, otherwise the
     * result is null.
     * @return the XSD type or null.
	 */
    URINamePair getXsdType();

	/**
     * Returns the Java name of this property if it is different from the
     * property name.
     * @return the Java name or null.
	 */
    String getJavaName();

	/**
     * Returns Boolean.TRUE if the property is declared as form="qualified" and
     * Boolean.FALSE if the property is declared as form="unqualified" in the
     * XML schema. If the property is not defined by schema or the form-attribute
     * is not set the result is null.
     * @return true for "qualified", false for "unqualified", null for default.
	 */
    Boolean getFormQualified();

	/**
     * Returns the index of the property in the type. If it is a global property,
     * the index is < 0.
     * @return the property index.
	 */
    int getIndex();

	/**
     * Returns the Java class of the property. In most cases this will be equal
     * to {@link commonj.sdo.Type#getInstanceClass()}
     * @return The Java class that represents the properties value.
	 */
    Class<?> getJavaClass();

    /**
     * Returns the HelperContext with the TypeHelper that has created this property.
     * For build-in global properties and properties of build-in types the
     * result is the core-HelperContext.
     * @return The HelperContext.
     */
	HelperContext getHelperContext();
    
	/**
     * Returns whether or not this property is a key property for its containing Type.
     * @return true if this property is a key property.
     */
    boolean isKey();

}