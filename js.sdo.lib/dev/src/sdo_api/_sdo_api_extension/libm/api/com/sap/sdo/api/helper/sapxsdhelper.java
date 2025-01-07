/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XSDHelper;

/**
 * Extension interface of {@link XSDHelper}.
 * Contains SAP internal extensions.
 *
 * @author D042774
 *
 */
public interface SapXsdHelper extends XSDHelper {
    /**
     * Define the XML Schema as Types.
     * The Types are available through TypeHelper and DataGraph getType() methods.
     * Same as define(new StringReader(xsd), null)
     * @param xsd the XML Schema.
     * @param options a Map of options, see {@link SapXmlHelper}.
     * @return the defined Types.
     * @throws IllegalArgumentException if the Types could not be defined.
     */
    List<Type> define(String xsd, Map options);

    /**
     * Define XML Schema as Types.
     * The Types are available through TypeHelper and DataGraph getType() methods.
     * @param xsdReader reader to an XML Schema.
     * @param schemaLocation the URI of the location of the schema, used
     *   for processing relative imports and includes.  May be null if not used.
     * @param options a Map of options, see {@link SapXmlHelper}.
     * @return the defined Types.
     * @throws IllegalArgumentException if the Types could not be defined.
     */
    List<Type> define(Reader xsdReader, String schemaLocation, Map options);

    /**
     * Define XML Schema as Types.
     * The Types are available through TypeHelper and DataGraph getType() methods.
     * @param xsdInputStream input stream to an XML Schema.
     * @param schemaLocation the URI of the location of the schema, used
     *   for processing relative imports and includes.  May be null if not used.
     * @param options a Map of options, see {@link SapXmlHelper}.
     * @return the defined Types.
     * @throws IllegalArgumentException if the Types could not be defined.
     */
    List<Type> define(InputStream xsdInputStream, String schemaLocation, Map options);

    /**
     * Translates and defines a list of parsed XML-Schemas as Types.
     * The list items are DataObjects that represent parsed schemas. The instance
     * class of the root DataObjects is {@link Schema}.
     * The schema-DataObjects can be created by using the {@link commonj.sdo.helper.XMLHelper} and
     * parsing an XSD-file.
     * @param schemas the list of Schema-DataObjects.
     * @param options a Map of options, see {@link SapXmlHelper}.
     * @return the defined Types.
     * @throws IOException if there are problems while including referenced schemas.
     * @throws IllegalArgumentException if the Types could not be defined.
     */
    List<Type> define(List<Schema> schemas, Map options) throws IOException;

    /**
     * Generates a schema as SDO of type {@link Schema}.
     * @param namespace target namespace of an XML schema.
     * @param namespaceToSchemaLocation map from namespace to schema location for rendering imports.
     * @param options optional parameters.
     * @return
     */
    Schema generateSchema(String namespace, Map<String, String> namespaceToSchemaLocation, Map options);

	/**
     * @deprecated Use {@link #getDefaultSchemaResolver()} and {@link #setDefaultSchemaResolver(SchemaResolver)}
	 */
    @Deprecated
    SchemaResolver popResolver();

    /**
     * @deprecated Use {@link #getDefaultSchemaResolver()} and {@link #setDefaultSchemaResolver(SchemaResolver)}
     */
	@Deprecated
    void pushResolver(SchemaResolver resolver);

    /**
     * @deprecated Use {@link #getDefaultSchemaResolver()} and {@link #setDefaultSchemaResolver(SchemaResolver)}
     */
	@Deprecated
    SchemaResolver peekResolver();

	/**
	 * Returns instance of default implementation of {@link SchemaResolver}
	 * @return default schema resolver
	 */
    SchemaResolver getDefaultSchemaResolver();

    /**
     * Exchanges default schema resolver.
     * @param pSchemaResolver schema resolver to be used.
     */
    void setDefaultSchemaResolver(SchemaResolver pSchemaResolver);

    /**
     * Maps URI and name of a built-in XML datatype to its equivalent in SDO.
     * @param pXsdUriName URI and name of a build-in XML datatype.
     * @return URI and name of a built-in SDO type that corresponds to the XML datatype.
     */
    URINamePair getSdoName(URINamePair pXsdUriName);

    /**
     * Maps URI and name of a built-in SDO type to its corresponding XML datatype.
     * @param pSdoUriName URI and name of a build-in SDO type.
     * @return URI and name of XML datatype that is represented by the SDO type.
     */
    URINamePair getXsdName(URINamePair pSdoUriName);

    /**
     * Checks if the XML schema defined by targetNamespace and schemaLocation was
     * already loaded by this helper.
     * @param targetNamespace target namespace of an XML schema.
     * @param schemaLocation location of an XML schema
     * @return true if the XML schema was already loaded.
     */
	boolean containsSchemaLocation(String targetNamespace, String schemaLocation);

    /**
     * If this function returns true, the DataObject will be rendered in a
     * nullable element-property with xsi:nil="true".
     * This function could also return true if this DataObject is not contained
     * in a nullable element property.
     * @param dataObject The DataObject.
     * @return true, if the DataObject is nil in a nillable context.
     */
    boolean isNil(DataObject dataObject);

    /**
     * This method can override the default-behavior of {@link #isNil(DataObject)}.
     * If the nil-parameter is true, all element-properties will be unset of
     * if there is a "value"-property that represents simple content it will be
     * set to null.
     * @param dataObject The DataObject.
     * @param xsiNil If the DataObject should be rendered with xsi:nil in a nillable context.
     */
    void setNil(DataObject dataObject, boolean xsiNil);

    /**
     * This is a convenient and fast way to get a property of a type for users
     * who know the XML representation of a property.
     * @param type The type.
     * @param uri The URI of the property in XML, "" if it has no URI.
     * @param xsdName The name of the property in XML.
     * @param isElement false for attributes, true for elements and the simple content "value"-property.
     * @return The property that matches these parameters.
     */
    Property getProperty(Type type, String uri, String xsdName, boolean isElement);

    /**
     * This is a convenient and fast way to get an instance property of a
     * DataObject for users who know the XML representation of a property.
     * @param dataObject The DataObject.
     * @param uri The URI of the property in XML, "" if it has no URI, null for onDemand-properties.
     * @param xsdName The name of the property in XML.
     * @param isElement false for attributes, true for elements and the simple content "value"-property.
     * @return The property that matches these parameters.
     */
    Property getInstanceProperty(DataObject dataObject, String uri, String xsdName, boolean isElement);
}
