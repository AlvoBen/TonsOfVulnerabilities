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
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

import org.xml.sax.XMLReader;

import com.sap.sdo.api.helper.util.IntegrationHandler;
import com.sap.sdo.api.helper.util.SDOResult;
import com.sap.sdo.api.helper.util.SDOSource;
import com.sap.sdo.api.helper.util.SDOContentHandler;

import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * The SapXmlHelper is an extension of the {@link XMLHelper} to provide extended
 * behavior in a standardized way.
 * It has additional methods to work with {@link XMLStreamReader} and
 * {@link XMLStreamWriter} that are given from outside.
 *
 * The load methods of the {@link XMLHelper} are extended to return a
 * {@link SapXmlDocument}.
 *
 * The <code>options</code> parameter has to be a {@link Map} to be understood
 * by this implementation.
 * <table border="2" bgcolor="#eeeeee">
 * <tr><th>Option key</th><th>Option value</th><th>Description</th></tr>
 * <tr><td>{@link #OPTION_KEY_SCHEMA_RESOLVER} =
 *          "com.sap.sdo.api.helper.SchemaResolver"</td>
 *     <td>Instance of a {@link SchemaResolver}</td>
 *     <td>Defines a schema resolver that resolves included or referenced schemas.</td>
 * </tr>
 * <tr><td rowspan="3">{@link #OPTION_KEY_DEFINE_SCHEMAS} =
 *          "com.sap.sdo.api.helper.SapXmlHelper.DefineSchemas"</td>
 *     <td>{@link #OPTION_VALUE_TRUE} = "true"</td>
 *     <td>If one or more schema elements are found or a schemaLocation,
 *         the types of that schemas will be defined.</td>
 * </tr>
 * <tr><td>{@link #OPTION_VALUE_FALSE} = "false"</td>
 *     <td>If one or more schema elements are found or a schemaLocation,
 *         the types of that schemas will not be defined.</td>
 * </tr>
 * <tr><td>{@link #OPTION_VALUE_AUTO} = "auto"</td>
 *     <td>If one or more schema elements are found or a schemaLocation,
 *         the types of that schemas will be only defined, if unknown properties
 *         and types are found in the xml and the definition could be in the
 *         schema. This is the default value.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_INDENT} =
 *          "com.sap.sdo.api.helper.SapXmlHelper.Indent"</td>
 *     <td>String to be used for indentation, if null neither indent nor new lines will be generated.</td>
 *     <td>Define a character sequence to be used for indentation.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_PREFIX_MAP} =
 *          "com.sap.sdo.api.helper.SapXmlHelper.PrefixMap"</td>
 *     <td>Map containing prefixes as keys and namespaces as values</td>
 *     <td>Predefine prefixes for known namespaces that will be used in the
 *         generated XML.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_SIMPLIFY_OPEN_CONTENT} =
 *          "com.sap.sdo.api.helper.SapXmlHelper.SimplifyOpenContent"</td>
 *     <td>{@link #OPTION_VALUE_FALSE} = "false"</td>
 *     <td>By default open content will be simpliefied at parsing. That means,
 *         open content properties will be many=false if possible and the type
 *         of open content properties will be a simple type if possible.
 *         With this option this behavior will be disabled.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_ERROR_HANDLER} =
 *          "com.sap.sdo.api.helper.ErrorHandler"</td>
 *     <td>Instance of an {@link ErrorHandler}</td>
 *     <td>Indicates what the implementation should do in case of certain failures.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_NAMING_HANDLER} =
 *          "com.sap.sdo.api.helper.NamingHandler"</td>
 *     <td>Instance of a {@link NamingHandler}</td>
 *     <td>Indicates how the implementation should name open content properties.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_VALIDATOR} =
 *          "com.sap.sdo.api.helper.Validator"</td>
 *     <td>Instance of a {@link Validator}</td>
 *     <td>Validation of the integrity of the parsed DataObjects.
 *         An implementation is provided by
 *         {@link com.sap.sdo.api.impl.SapHelperProvider#getValidator()}.</td>
 * </tr>
 * <tr><td>{@link #OPTION_KEY_INTEGRATION_HANDLER} =
 *          "com.sap.sdo.api.helper.util.IntegrationHandler"</td>
 *     <td>Instance of an {@link IntegrationHandler}</td>
 *     <td>The IntegrationHandler can be defined for {@link SDOContentHandler}
 *         based parsing.  It allows to integrate the incoming SAX events into
 *         an existing SDO graph.</td>
 * </tr>
 * </table>
 * @author D042774
 *
 */
public interface SapXmlHelper extends XMLHelper {

    public static final String OPTION_KEY_SCHEMA_RESOLVER = SchemaResolver.class.getName();
    public static final String OPTION_KEY_DEFINE_SCHEMAS = SapXmlHelper.class.getName() + ".DefineSchemas";
    public static final String OPTION_KEY_DEFINE_XML_TYPES = SapXmlHelper.class.getName() + ".DefineXmlTypes";
    public static final String OPTION_KEY_INDENT = SapXmlHelper.class.getName() + ".Indent";
    public static final String OPTION_KEY_PREFIX_MAP = SapXmlHelper.class.getName() + ".PrefixMap";
    public static final String OPTION_KEY_SIMPLIFY_OPEN_CONTENT = SapXmlHelper.class.getName() + ".SimplifyOpenContent";
    public static final String OPTION_KEY_ERROR_HANDLER = ErrorHandler.class.getName();
    public static final String OPTION_KEY_NAMING_HANDLER = NamingHandler.class.getName();
    public static final String OPTION_KEY_VALIDATOR = Validator.class.getName();
    public static final String OPTION_KEY_INTEGRATION_HANDLER = IntegrationHandler.class.getName();
    public static final String OPTION_VALUE_AUTO = "auto";
    public static final String OPTION_VALUE_TRUE = Boolean.TRUE.toString();
    public static final String OPTION_VALUE_FALSE = Boolean.FALSE.toString();

    /**
     * Method is utilized in case of parts with elements.
     * In this case they know all globally defined xsd elements,
     * their types and how to deserialize it.
     * The reader is queried for the element name.
     * Precondition: the current event is START_ELEMENT.
     * Postcondition: the current event is the corresponding END_ELEMENT.
     * @param reader The StAX reader.
     * @param options
     * @return
     */
    Object load(XMLStreamReader reader, Map options) throws XMLStreamException;

    /**
     * Method is utilized for parts with xsd type references.
     * In this case the SDO uses the xsdTypeUri and xsdTypeName to resolve
     * which SDO object needs to be created.
     * Precondition: the current event is START_ELEMENT.
     * Postcondition: the current event is the corresponding END_ELEMENT.
     * @param reader The StAX reader.
     * @param xsdTypeUri The namespace URI of the Type (not the prefix).
     * @param xsdTypeName The name of the Type.
     * @param options
     * @return
     */
    Object load(
        XMLStreamReader reader,
        String xsdTypeUri,
        String xsdTypeName,
        Map options) throws XMLStreamException;

    /**
     * The SDO outputs into the Writer an element with elementUri and elementName.
     * The 'data' should be serialized as:
     * <p>- if the 'data' is mapped from schema primitive type,
     *      that is in java representation Integer, String, Decimal, etc
     *      then based on the xsdTypeUri and xsdTypeName the SDO knows
     *      how to serialize it into XML (for example xs:Qname is mapped to String)
     * <p>- if the 'data' is DataObject then the SDO knows its XSD type and
     *      knows how to serialize it.
     *      The xsdType* params shouldn't be taken in consideration in this case
     * @param data
     * @param elementUri
     * @param elementName
     * @param xsdTypeUri
     * @param xsdTypeName
     * @param writer
     * @param options
     */
    void save(
        Object data,
        String elementUri,
        String elementName,
        String xsdTypeUri,
        String xsdTypeName,
        XMLStreamWriter writer,
        Map options) throws IOException;

    SapXmlDocument load(String inputString);

    SapXmlDocument load(InputStream inputStream) throws IOException;

    SapXmlDocument load(InputStream inputStream, String locationURI, Object options) throws IOException;

    SapXmlDocument load(Reader inputReader, String locationURI, Object options) throws IOException;

    SapXmlDocument load(Source inputSource, String locationURI, Object options) throws IOException;

    /**
     * Creates an StAX XMLStreamReader on top of an SDO tree.
     * The XMLStreamReader provides no indentation.
     * This is an example to create a {@link javax.xml.transform.stax.StAXSource}:
     * <pre>
     * XMLDocument xmlDoc = ...;
     * Source source = new StAXSource(xmlHelper.createXMLStreamReader(xmlDoc, options));
     * </pre>
     * @param xmlDocument The XMLDocument to read from.
     * @param options 
     * @return The XMLStreamReader on top of the XMLDocument.
     */
    XMLStreamReader createXMLStreamReader(XMLDocument xmlDocument, Map<String,Object> options);
    
    /**
     * Creates an SAX XMLReader on top of an SDO tree. The XMLReader doesn't
     * need any {@link org.xml.sax.InputSource}. Both parse methods 
     * {@link org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)} and
     * {@link org.xml.sax.XMLReader#parse(String)} can be used to fire the
     * {@link org.xml.sax.ContentHandler}. The parameter will be ignored.
     * To avoid indentation and increase the performance, pass the option
     * ({@link #OPTION_KEY_INDENT}, null).
     * If the used {@link org.xml.sax.ContentHandler} requires QNames but does
     * not set the feature "http://xml.org/sax/features/namespace-prefixes"=true
     * by itself, add ("http://xml.org/sax/features/namespace-prefixes",
     * {@link Boolean#TRUE}) to the options.
     * This is an example to create a {@link javax.xml.transform.sax.SAXSource}:
     * <pre>
     * XMLDocument xmlDoc = ...;
     * Map<String, Object> options = new HashMap<String, Object>();
     * options.put("http://xml.org/sax/features/namespace-prefixes", true);
     * options.put(SapXmlHelper.OPTION_KEY_INDENT, null);
     * Source source = new SAXSource(xmlHelper.createXMLReader(xmlDoc, options), new InputSource());
     * </pre>
     * @param xmlDocument The XMLDocument to read from.
     * @param options 
     * @return The XMLStreamReader on top of the XMLDocument.
     */
    XMLReader createXMLReader(XMLDocument xmlDocument, Map<String,Object> options);

    /**
     * Creates a {@link org.xml.sax.ContentHandler} that consumes XML events to
     * create an {@link XMLDocument} out of it. The SDO tree will be available
     * by {@link SDOContentHandler#getDocument()} when the parsing is finished.
     * This is an example to create a {@link javax.xml.transform.sax.SAXResult}:
     * <pre>
     * SdoContentHandler handler = xmlHelper.createContentHandler(null);
     * SAXResult result = new SAXResult(handler);
     * </pre>
     * @param options
     * @return The SdoContentHandler.
     */
    SDOContentHandler createContentHandler(Object options);

    /**
     * Creates an instance of a javax.xml.transform.Source. The SDOSource
     * provides an SDO data graph as source input for other frameworks.
     * @param xmlDocument The SDO graph.
     * @param options implementation-specific options.
     * @return The source instance.
     */
    SDOSource createSDOSource(XMLDocument xmlDocument, Object options);

    /**
     * Creates an instance of a javax.xml.transform.Result. The SDOResult
     * can be used as output target in other frameworks and represents the output
     * finally as SDO data graph.
     * @param options implementation-specific options.
     * @return The result instance.
     */
    SDOResult createSDOResult(Object options);
}
