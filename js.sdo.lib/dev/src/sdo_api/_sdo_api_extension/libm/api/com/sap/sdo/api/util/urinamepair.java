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
package com.sap.sdo.api.util;

import java.io.Serializable;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.sap.sdo.api.types.SapType;

import commonj.sdo.Type;

public class URINamePair implements Comparable<URINamePair>, Serializable
{
    private static final long serialVersionUID = 6257176154033118124L;
    public final static String UTILTYPE_URI = "commonj.sdo";
    public final static String MODELTYPE_URI = "commonj.sdo";
    public final static String DATATYPE_URI = "commonj.sdo";
    public final static String DATATYPE_JAVA_URI = "commonj.sdo/java";
    public final static String DATATYPE_XML_URI = "commonj.sdo/xml";
    public final static String INTERNAL_URI = "com.sap.sdo";

    // http://www.w3.org/2001/XMLSchema
    public final static String SCHEMA_URI = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    // http://www.w3.org/2001/XMLSchema-instance
    public final static String XSI_URI = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
    // http://www.w3.org/2000/xmlns/
    public final static String XMLNS_URI = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    // http://www.w3.org/XML/1998/namespace
    public final static String XML_URI = XMLConstants.XML_NS_URI;

    public final static String CTX_URI = "http://sap.com/sdo/api/types/ctx";

    /* simple types */
    public final static URINamePair BOOLEAN             = new URINamePair(DATATYPE_URI,"Boolean");
    public final static URINamePair BOOLEANOBJECT       = new URINamePair(DATATYPE_JAVA_URI,"BooleanObject");
    public final static URINamePair BYTE                = new URINamePair(DATATYPE_URI,"Byte");
    public final static URINamePair BYTEOBJECT          = new URINamePair(DATATYPE_JAVA_URI,"ByteObject");
    public final static URINamePair BYTES               = new URINamePair(DATATYPE_URI,"Bytes");
    public final static URINamePair CHARACTER           = new URINamePair(DATATYPE_URI,"Character");
    public final static URINamePair CHARACTEROBJECT     = new URINamePair(DATATYPE_JAVA_URI,"CharacterObject");
    public final static URINamePair DATE                = new URINamePair(DATATYPE_URI,"Date");
    public final static URINamePair DATETIME            = new URINamePair(DATATYPE_URI,"DateTime");
    public final static URINamePair DAY                 = new URINamePair(DATATYPE_URI,"Day");
    public final static URINamePair DECIMAL             = new URINamePair(DATATYPE_URI,"Decimal");
    public final static URINamePair DOUBLE              = new URINamePair(DATATYPE_URI,"Double");
    public final static URINamePair DOUBLEOBJECT        = new URINamePair(DATATYPE_JAVA_URI,"DoubleObject");
    public final static URINamePair DURATION            = new URINamePair(DATATYPE_URI,"Duration");
    public final static URINamePair FLOAT               = new URINamePair(DATATYPE_URI,"Float");
    public final static URINamePair FLOATOBJECT         = new URINamePair(DATATYPE_JAVA_URI,"FloatObject");
    public final static URINamePair INT                 = new URINamePair(DATATYPE_URI,"Int");
    public final static URINamePair INTOBJECT           = new URINamePair(DATATYPE_JAVA_URI,"IntObject");
    public final static URINamePair INTEGER             = new URINamePair(DATATYPE_URI,"Integer");
    public final static URINamePair LONG                = new URINamePair(DATATYPE_URI,"Long");
    public final static URINamePair LONGOBJECT          = new URINamePair(DATATYPE_JAVA_URI,"LongObject");
    public final static URINamePair MONTH               = new URINamePair(DATATYPE_URI,"Month");
    public final static URINamePair MONTHDAY            = new URINamePair(DATATYPE_URI,"MonthDay");
    public final static URINamePair OBJECT              = new URINamePair(DATATYPE_URI,"Object");
    public final static URINamePair SHORT               = new URINamePair(DATATYPE_URI,"Short");
    public final static URINamePair SHORTOBJECT         = new URINamePair(DATATYPE_JAVA_URI,"ShortObject");
    public final static URINamePair STRING              = new URINamePair(DATATYPE_URI,"String");
    public final static URINamePair STRINGS             = new URINamePair(DATATYPE_URI,"Strings");
    public final static URINamePair TIME                = new URINamePair(DATATYPE_URI,"Time");
    public final static URINamePair URI                 = new URINamePair(DATATYPE_URI,"URI");
    public final static URINamePair YEAR                = new URINamePair(DATATYPE_URI,"Year");
    public final static URINamePair YEARMONTH           = new URINamePair(DATATYPE_URI,"YearMonth");
    public final static URINamePair YEARMONTHDAY        = new URINamePair(DATATYPE_URI,"YearMonthDay");
    /* built-in model types */
    public final static URINamePair TYPE                = new URINamePair(MODELTYPE_URI,"Type");
    public final static URINamePair PROPERTY            = new URINamePair(MODELTYPE_URI,"Property");
    public final static URINamePair CHANGESUMMARY_TYPE  = new URINamePair(MODELTYPE_URI,"ChangeSummaryType");

    /* built-in utility types */
    public final static URINamePair OPEN                = new URINamePair(INTERNAL_URI,"OpenType");
    public final static URINamePair CLASS               = new URINamePair(INTERNAL_URI,"Class");
    public static final URINamePair MIXEDTEXT_TYPE      = new URINamePair(MODELTYPE_URI,"Text");
    public static final URINamePair XML_PROPERTY_INFO   = new URINamePair(MODELTYPE_URI,"XmlInfo");
    public static final URINamePair FACETS              = new URINamePair(CTX_URI,"Facets");
    public static final URINamePair JAVA_PROPERTY_INFO  = new URINamePair(DATATYPE_JAVA_URI,"JavaInfo");
    public static final URINamePair DATAGRAPH_TYPE      = new URINamePair(MODELTYPE_URI,"DataGraphType");
    public static final URINamePair XSD_TYPE            = new URINamePair(MODELTYPE_URI,"XSDType");
    public static final URINamePair DATAOBJECT          = new URINamePair(MODELTYPE_URI,"DataObject");
    @Deprecated
    public final static URINamePair ID                  = new URINamePair(DATATYPE_URI,"ID");

    /* xsd types */
    public static final URINamePair SCHEMA_SCHEMA       = new URINamePair(SCHEMA_URI, "schema");
    public static final URINamePair SCHEMA_Q_NAME       = new URINamePair(SCHEMA_URI, "QName");
    public static final URINamePair SCHEMA_BASE64BINARY = new URINamePair(SCHEMA_URI, "base64Binary");
    public static final URINamePair SCHEMA_ID           = new URINamePair(SCHEMA_URI, "ID");

    /* built-in global properties */
    public static final URINamePair PROP_SDO_DATAGRAPH  = new URINamePair(DATATYPE_URI,"datagraph");
    public static final URINamePair PROP_SDO_DATA_OBJECT= new URINamePair(DATATYPE_URI,"dataObject");
    /**@deprecated just for internal use*/
    @Deprecated
    public static final URINamePair PROP_SDO_TEXT       = new URINamePair(DATATYPE_URI,"text");

    /* schema annotations */
    public static final URINamePair PROP_XML_NAME       = new URINamePair(DATATYPE_XML_URI,"name");
    public static final URINamePair PROP_XML_PROPERTY_TYPE = new URINamePair(DATATYPE_XML_URI,"propertyType");
    public static final URINamePair PROP_XML_OPPOSITE_PROPERTY = new URINamePair(DATATYPE_XML_URI,"oppositeProperty");
    public static final URINamePair PROP_XML_SEQUENCE   = new URINamePair(DATATYPE_XML_URI,"sequence");
    public static final URINamePair PROP_XML_STRING     = new URINamePair(DATATYPE_XML_URI,"string");
    public static final URINamePair PROP_XML_DATA_TYPE  = new URINamePair(DATATYPE_XML_URI,"dataType");
    public static final URINamePair PROP_XML_ALIAS_NAME = new URINamePair(DATATYPE_XML_URI,"aliasName");
    public static final URINamePair PROP_XML_READ_ONLY  = new URINamePair(DATATYPE_XML_URI,"readOnly");
    public static final URINamePair PROP_XML_MANY       = new URINamePair(DATATYPE_XML_URI,"many");
    public static final URINamePair PROP_XML_ORPHAN_HOLDER = new URINamePair(DATATYPE_XML_URI, "orphanHolder");
    public static final URINamePair PROP_XML_KEY        = new URINamePair(DATATYPE_XML_URI,"key");
    public static final URINamePair PROP_XML_KEY_TYPE   = new URINamePair(DATATYPE_XML_URI,"keyType");
    public static final URINamePair PROP_XML_EMBEDDED_KEY = new URINamePair(DATATYPE_XML_URI,"embeddedKey");

    /* SAP extensions: type and property meta-data */
    /** Property on type and property objects if the XML-name is different from the SDO-name. */
    public static final URINamePair PROP_XML_XML_NAME   = new URINamePair(DATATYPE_XML_URI, "xmlName");
    /** Property on type objects if the type allows mixed content (text and elements). */
    public static final URINamePair PROP_XML_MIXED      = new URINamePair(DATATYPE_XML_URI, "mixed");
    /** Property on type objects to define elementFormDefault (true for qualified, false for unqualified). */
    public static final URINamePair PROP_XML_ELEMENT_FORM_DEFAULT = new URINamePair(DATATYPE_XML_URI, "elementFormDefault");
    /** Property on type objects to define attributeFormDefault. (true for qualified, false for unqualified) */
    public static final URINamePair PROP_XML_ATTRIBUTE_FORM_DEFAULT = new URINamePair(DATATYPE_XML_URI, "attributeFormDefault");
    /** Property on property objects to define XML representation as element or attribute. */
    public static final URINamePair PROP_XML_XML_ELEMENT= new URINamePair(DATATYPE_XML_URI, "xmlElement");
    /** Property on property objects if the XSD-type can not be inferred unambiguously from the SDO-type. The value is a uri-name-pair as String.*/
    public static final URINamePair PROP_XML_XSD_TYPE   = new URINamePair(DATATYPE_XML_URI, "xsdType");
    /** Property on property objects to define the URI of the property. */
    public static final URINamePair PROP_XML_URI        = new URINamePair(DATATYPE_XML_URI, "uri");
    /** Property on property to define a "value"-property as simple content (no surrounding "value"-element will be rendered). */
    public static final URINamePair PROP_XML_SIMPLE_CONTENT = new URINamePair(DATATYPE_XML_URI, "simpleContent");

    public static final URINamePair PROP_JAVA_PACKAGE   = new URINamePair(DATATYPE_JAVA_URI,"package");
    public static final URINamePair PROP_JAVA_INSTANCE_CLASS = new URINamePair(DATATYPE_JAVA_URI,"instanceClass");
    public static final URINamePair PROP_JAVA_EXTENDED_INSTANCE_CLASS = new URINamePair(DATATYPE_JAVA_URI,"extendedInstanceClass");
    public static final URINamePair PROP_JAVA_NESTED_INTERFACES = new URINamePair(DATATYPE_JAVA_URI,"nestedInterfaces");

    /** Root element of a XML schema. */
    public static final URINamePair PROP_SCHEMA_SCHEMA  = new URINamePair(SCHEMA_URI,"schema");

    public static final URINamePair PROP_CTX_FACETS     = new URINamePair(CTX_URI,"facets");
    /** Property on type objects that references the XSD as SDO-objects if the type is defined by schema. */
    public static final URINamePair PROP_CTX_SCHEMA_REFERENCE = new URINamePair(CTX_URI,"schemaReference");
    public static final URINamePair PROP_CTX_ITEM_TYPE  = new URINamePair(CTX_URI,"itemType");


    private String _uri;
    private String _name;

    public URINamePair(String uri, String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        _name = name;
        _uri = (uri == null)? "": uri;
    }

    public URINamePair(URINamePair pair) {
        this(pair.getURI(), pair.getName());
    }

    public URINamePair(QName pQname) {
        this(pQname.getNamespaceURI(), pQname.getLocalPart());
    }

    public String getName() {
        return _name;
    }

    public String getURI() {
        return _uri;
    }

    public void setURI(String uri) {
        this._uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof URINamePair) && ((URINamePair) obj)._name.equals(_name) && ((URINamePair) obj)._uri.equals(_uri);
    }

    @Override
    public int hashCode() {
        return this._uri.hashCode() ^ this._name.hashCode();
    }

    @Override
    public String toString() {
        return toStandardSdoFormat();
    }
    public static URINamePair getQNameFromString(String string) {
        final int index = string.lastIndexOf(':');
        if (index<0) {
            return new URINamePair("",string);
        }
        final String uri = string.substring(0, index);
        final String name = string.substring(index+1);
        return new URINamePair(uri,name);
    }

    public String toStandardSdoFormat() {
        int index = _name.indexOf('#');
        if (index < 0) {
            return _uri+'#'+_name;
        }
        StringBuilder escaped = new StringBuilder(_uri.length() + _name.length() + 3);
        escaped.append(_uri);
        escaped.append('#');
        escaped.append(_name, 0, index);
        int newIndex;
        do {
            escaped.append('\\');
            escaped.append('#');
            newIndex = _name.indexOf('#', index + 1);
            if (newIndex > 0) {
                escaped.append(_name, index + 1, newIndex);
                index = newIndex;
            }
        } while (newIndex > 0);
        escaped.append(_name, index + 1, _name.length());
        return escaped.toString();
    }

    public static URINamePair fromStandardSdoFormat(String uriName) {
        int i = uriName.lastIndexOf('#');
        String name;
        if (i>1 && uriName.charAt(i-1)=='\\') {
            StringBuilder unescapedName = new StringBuilder(uriName.length() - 2);
            int delimiter = unescape(unescapedName, uriName, i);
            unescapedName.append(uriName, i, uriName.length());
            name = unescapedName.toString();
            i = delimiter;
        } else {
            name = uriName.substring(i+1);
        }
        String uri;
        if (i>=0) {
            uri = uriName.substring(0,i);
        } else {
            uri = "";
        }
        return new URINamePair(uri,name);
    }

    private static int unescape(StringBuilder pName, String pUriName, int pEscapeIndex) {
        int hashIndex = pUriName.lastIndexOf('#', pEscapeIndex - 2);
        int delimiter;
        if (hashIndex>1 && pUriName.charAt(hashIndex-1)=='\\') {
            delimiter = unescape(pName, pUriName, hashIndex);
            pName.append(pUriName, hashIndex, pEscapeIndex - 1);
        } else {
            delimiter = hashIndex;
            pName.append(pUriName, hashIndex + 1, pEscapeIndex - 1);
        }
        return delimiter;
    }

    public static URINamePair fromType(Type type) {
        return ((SapType)type).getQName();
    }

    /**
     * @param pUri
     * @param pName
     * @return
     */
    public boolean equalsUriName(String pUri, String pName) {
        return _name.equals(pName) && _uri.equals(pUri);
    }

    /**
     * @param pType
     * @return
     */
    public boolean equalsUriName(Type pType) {
        return pType != null && equals(((SapType)pType).getQName());
    }

	public void setName(String pName) {
		_name = pName;

	}

    public int compareTo(URINamePair pOther) {
        int comp = _uri.compareTo(pOther.getURI());
        if (comp != 0) {
            return comp;
        }
        return _name.compareTo(pOther.getName());
    }
}
