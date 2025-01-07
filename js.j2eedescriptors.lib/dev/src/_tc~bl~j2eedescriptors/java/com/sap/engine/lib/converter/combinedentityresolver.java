/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.processor.SchemaEntityResolver;
import com.sap.engine.lib.xml.StandardEntityResolver;

/**
 * Utility EntityResolver for combining
 * 
 * @see com.sap.engine.lib.processor.SchemaEntityResolver and
 * @see com.sap.engine.lib.xml.StandardEntityResolver. Some entries in
 *      StandardEntityResolver are out of date, so SchemaEntityResolver has
 *      precedence.
 * 
 * @author d037913
 */
public class CombinedEntityResolver implements EntityResolver {

	private static final EntityResolver SCHEMA_RESOLVER = new SchemaEntityResolver();
	private static final EntityResolver STANDARD_RESOLVER = new StandardEntityResolver();
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
	 *      java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		InputSource source = null;
		// try schema resolver first because it should override
		// outdated (=>wrong) mappings in standard resolver
		source = SCHEMA_RESOLVER.resolveEntity(publicId, systemId);
		if (source != null) {
			return source;
		}
		return STANDARD_RESOLVER.resolveEntity(publicId, systemId);
	}

}