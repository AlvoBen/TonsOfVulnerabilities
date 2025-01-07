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
import java.net.URISyntaxException;

/**
 * @author D042774
 *
 */
public interface SchemaResolver {
    /**
     * Fetches an imported XSD schema for the given target namespace and schema
     * location.
     * Supported return types are:
     * {@link java.io.InputStream}
     * {@link java.io.Reader}
     * {@link javax.xml.transform.Source}
     * 
     * @param targetNamespace
     *            is the target namespace as given in the xsd:import
     *            declaration.
     * @param schemaLocation
     *            is the schema location URI as given in the xsd:import
     *            declaration.
     * @return the imported XSD schema or null if the targetNamespace and
     *         schemaLocation cannot be resolved to a schema.
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public Object resolveImport(String targetNamespace, String schemaLocation) throws IOException, URISyntaxException;

    /**
     * Fetches an included XSD schema for the given schemaLocation.
     * Supported return types are:
     * {@link java.io.InputStream}
     * {@link java.io.Reader}
     * {@link javax.xml.transform.Source}
     * 
     * @param schemaLocation
     *            is the schema location URI as given in the xsd:import
     *            declaration.
     * @return the included XSD schema or null if the schemaLocation cannot be
     *         resolved to a schema.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public Object resolveInclude(String schemaLocation) throws IOException, URISyntaxException;
    
    public String getAbsoluteSchemaLocation(String relativeSchemaLocation, String currentAbsoluteLocation) throws URISyntaxException;
}
