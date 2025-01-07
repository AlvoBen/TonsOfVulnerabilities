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
package com.sap.sdo.impl.types.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.api.util.URINamePair;

/**
 * provides types based on schemas
 *
 */
public class SchemaTypeFactory
{
    private static final SchemaTypeFactory INSTANCE = new SchemaTypeFactory();
    
    private final Map<URINamePair,URINamePair> _xsdToSdoName = new HashMap<URINamePair,URINamePair>(); 
    private final Map<URINamePair,URINamePair> _sdoToXsdName = new HashMap<URINamePair,URINamePair>(); 
    private void putBothWays(final URINamePair xsd, final URINamePair sdo) {
    	_xsdToSdoName.put(xsd,sdo);
    	_sdoToXsdName.put(sdo,xsd);
    }
    private SchemaTypeFactory() {
    	putBothWays(new URINamePair(URINamePair.SCHEMA_URI, "anySimpleType"),URINamePair.OBJECT);
    	putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"anyType"),URINamePair.DATAOBJECT);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"base64Binary"),URINamePair.BYTES);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"boolean"),URINamePair.BOOLEAN);
        _sdoToXsdName.put(URINamePair.BOOLEANOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"boolean"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"byte"),URINamePair.BYTE);
        _sdoToXsdName.put(URINamePair.BYTEOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"byte"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"date"),URINamePair.YEARMONTHDAY);
        _sdoToXsdName.put(URINamePair.DATE, new URINamePair(URINamePair.SCHEMA_URI,"dateTime"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"dateTime"),URINamePair.DATETIME);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"decimal"),URINamePair.DECIMAL);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"double"),URINamePair.DOUBLE);
        _sdoToXsdName.put(URINamePair.DOUBLEOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"double"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"duration"),URINamePair.DURATION);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"ENTITY"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"ENTITIES"),URINamePair.STRINGS);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"float"),URINamePair.FLOAT);
        _sdoToXsdName.put(URINamePair.FLOATOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"float"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"gDay"),URINamePair.DAY);        
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"gMonth"),URINamePair.MONTH);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"gMonthDay"),URINamePair.MONTHDAY);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"gYear"),URINamePair.YEAR);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"gYearMonth"),URINamePair.YEARMONTH);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"hexBinary"),URINamePair.BYTES);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"ID"),URINamePair.STRING);
        _sdoToXsdName.put(URINamePair.ID, URINamePair.SCHEMA_ID);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"IDREF"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"IDREFS"),URINamePair.STRINGS);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"NMTOKEN"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"NMTOKENS"),URINamePair.STRINGS);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"int"),URINamePair.INT);
        _sdoToXsdName.put(URINamePair.INTOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"int"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"integer"),URINamePair.INTEGER);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"long"),URINamePair.LONG);
        _sdoToXsdName.put(URINamePair.LONGOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"long"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"short"),URINamePair.SHORT);
        _sdoToXsdName.put(URINamePair.SHORTOBJECT, new URINamePair(URINamePair.SCHEMA_URI,"short"));
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"time"),URINamePair.TIME);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"string"),URINamePair.STRING);
        _sdoToXsdName.put(URINamePair.STRINGS, new URINamePair(URINamePair.SCHEMA_URI,"string"));        
        _sdoToXsdName.put(URINamePair.CHARACTER, new URINamePair(URINamePair.SCHEMA_URI,"string"));        
        _sdoToXsdName.put(URINamePair.CHARACTEROBJECT, new URINamePair(URINamePair.SCHEMA_URI,"string"));        
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"positiveInteger"),URINamePair.INTEGER);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"nonPositiveInteger"),URINamePair.INTEGER);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"negativeInteger"),URINamePair.INTEGER);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"nonNegativeInteger"),URINamePair.INTEGER);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"normalizedString"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"NCName"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"QName"), URINamePair.URI);
        putBothWays(new URINamePair(URINamePair.SCHEMA_URI,"anyURI"),URINamePair.URI);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"token"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"language"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"Name"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"NOTATION"),URINamePair.STRING);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"unsignedByte"),URINamePair.SHORT);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"unsignedInt"),URINamePair.LONG);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"unsignedShort"),URINamePair.INT);
        _xsdToSdoName.put(new URINamePair(URINamePair.SCHEMA_URI,"unsignedLong"),URINamePair.INTEGER);
    }
    
    public static SchemaTypeFactory getInstance() {
        return INSTANCE;
    }
    public URINamePair getXsdName(final URINamePair sdoName) {
    	final URINamePair ret = _sdoToXsdName.get(sdoName);
    	if (ret == null) {
    		return ret;
    	}
    	return new URINamePair(ret);
    }
    public URINamePair getSdoName(final URINamePair xsdName) {
    	final URINamePair ret = _xsdToSdoName.get(xsdName);
    	if (ret == null) {
    		return ret;
    	}
    	return new URINamePair(ret);
    }

	public URINamePair getXsdName(final String uri, final String name) {
		return getXsdName(new URINamePair(uri,name));
	}
    
    public Map<URINamePair,URINamePair> getXsdToSdoNames() {
        return Collections.unmodifiableMap(_xsdToSdoName);
    }

}
