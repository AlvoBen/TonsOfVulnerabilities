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
package com.sap.sdo.impl.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.api.helper.MappingSchemaResolver;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class DefaultSchemaResolver implements MappingSchemaResolver {
    private final Map<String,String> _schemaMappings = new HashMap<String,String>();
    private final SapHelperContext _helperContext;

    /**
     *
     */
    public DefaultSchemaResolver(HelperContext pHelperContext) {
        _helperContext = (SapHelperContext)pHelperContext;
        _schemaMappings.put("http://www.w3.org/2001/xml.xsd", "com/sap/sdo/impl/xml/XmlNamespace.xsd");
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SchemaResolver#getAbsoluteSchemaLocation(java.lang.String, java.lang.String)
     */
    public String getAbsoluteSchemaLocation(
        String pRelativeSchemaLocation,
        String pCurrentAbsoluteLocation) throws URISyntaxException {
        
        String relativeReplaced = replaceSpaces(pRelativeSchemaLocation);
        String uri = relativeReplaced;
        URI relativeUri;
        try {
            relativeUri = new URI(relativeReplaced);
        } catch (URISyntaxException e) {
            File file = new File(pRelativeSchemaLocation);
            if (!file.isAbsolute()) {
                throw e;
            }
            return file.toURI().toString();
        }
        if (relativeReplaced.startsWith("/")) {
            //this means it is an absolute path in the classpath
            uri = relativeReplaced.substring(1);
        } else if (pCurrentAbsoluteLocation != null && !relativeUri.isAbsolute()) {
            URI currentUri = createUri(pCurrentAbsoluteLocation);
            if (currentUri.isAbsolute()) {
                //take advantage of URL-behavior
                try {
                    uri = new URL(currentUri.toURL(), relativeReplaced).toString();
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                //take advantage of URI-behavior
                uri = currentUri.resolve(relativeUri).toString();
            }
        } 
        return uri;
    }
    
    private URI createUri(String pLocation) throws URISyntaxException {
        URI uri;
        try {
            uri = new URI(replaceSpaces(pLocation));
        } catch (URISyntaxException e) {
            File file = new File(pLocation);
            if (!file.isAbsolute()) {
                throw e;
            }
            uri = file.toURI();
        }
        return uri;
    }

    private String replaceSpaces(String pLocation) {
        return pLocation.replace(" ", "%20");
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SchemaResolver#resolveImport(java.lang.String, java.lang.String)
     */
    public InputStream resolveImport(String pTargetNamespace, String pSchemaLocation) throws IOException, URISyntaxException {
        String schemaLocation = getInternalSchemaLocation(pSchemaLocation);
        if (schemaLocation == null) {
            return null;
        }
        // if this schema was already defined, return null
        if (((XSDHelperImpl)_helperContext.getXSDHelper()).containsSchemaLocation(new SchemaLocation(pTargetNamespace, schemaLocation))) {
            return null;
        }

        InputStream is;
        URI uri = createUri(schemaLocation);
        if (uri.isAbsolute()) {
            try {
                URL url = uri.toURL();
                URLConnection connection = url.openConnection();
                if ("jar".equals(url.getProtocol())) {
                    connection.setUseCaches(false);
                }
                is = connection.getInputStream();
            } catch (IOException e) {
                return null;
            }
        } else {
            is = getResourceAsStream(schemaLocation);
        }
        return is;
    }

	protected InputStream getResourceAsStream(String pSchemaLocation) {
        String schemaLocation = pSchemaLocation.replace("%20", " ");
        ClassLoader classLoader1 = _helperContext.getClassLoader();
        InputStream inputStream = classLoader1.getResourceAsStream(schemaLocation);
        if (inputStream != null) {
            return inputStream;
        }
		ClassLoader classLoader2 = SapHelperProviderImpl.getClassLoader();
        if (classLoader1 == classLoader2) {
            return null;
        }
        return classLoader2.getResourceAsStream(schemaLocation);
	}

    /**
     * @param pSchemaLocation
     * @return
     */
    private String getInternalSchemaLocation(String pSchemaLocation) {
        if (_schemaMappings.containsKey(pSchemaLocation)) {
            return _schemaMappings.get(pSchemaLocation);
        }
        return pSchemaLocation;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SchemaResolver#resolveInclude(java.lang.String)
     */
    public InputStream resolveInclude(String pSchemaLocation) throws IOException, URISyntaxException {
        return resolveImport(null, pSchemaLocation);
    }

    public void defineSchemaLocationMapping(String pAbsoluteSchemaLocation, String pInternalSchemaLocation) {
        _schemaMappings.put(pAbsoluteSchemaLocation, pInternalSchemaLocation);
    }

}
