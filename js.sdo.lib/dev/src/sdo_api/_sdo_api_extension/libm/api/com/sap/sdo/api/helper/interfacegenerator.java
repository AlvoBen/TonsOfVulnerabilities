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

import java.io.File;
import java.io.IOException;
import java.util.List;

import commonj.sdo.Type;

/**
 * Generates Java interfaces for SDO types.
 * Use {@link #addSchemaLocation(String, String)} to add a schema location
 * annotation to the interfaces.
 * The package names will be calculated from the uri of the types. If other
 * package names are required, use {@link #addPackage(String, String)}.
 * @author D042807
 *
 */
public interface InterfaceGenerator {

    /**
     * Defines a package for a namespace.
     * @param pNamespace the namespace.
     * @param pPackage the package.
     */
    void addPackage(String pNamespace, String pPackage);

    /**
     * Sets a schema location for a namespace or a single type.
     * @param pUriName a uri for a namespace or uri#name for a type.
     * @param pSchemaLocation The schema location that will be rendered as annotation.
     */
    void addSchemaLocation(String pUriName,
        String pSchemaLocation);

    /**
     * Generates java interfaces for the type and all referenced types.
     * @param type The type to generate.
     * @return The list of the generated class names.
     * @throws IOException if writing to the file system fails.
     */
    List<String> generate(final Type type) throws IOException;

    /**
     * Generates java interfaces for the list of types and all referenced types.
     * @param types The types to generate.
     * @return The list of the generated class names.
     * @throws IOException if writing to the file system fails.
     */
    List<String> generate(final List<Type> types)
        throws IOException;

    /**
     * Generates java interfaces for the types of the namespace and all referenced types.
     * @param namespace The namespace to generate.
     * @return The list of the generated class names.
     * @throws IOException if writing to the file system fails.
     */
    List<String> generate(String namespace) throws IOException;

    /**
     * Default is true.
     * @return True if the generator renders SAP specific annotations.
     */
    boolean getGenerateAnnotations();

    /**
     * Set to false, if no annotation should be generated.
     * Default is true.
     * @param pGenerateAnnotations false for no annotations
     */
    void setGenerateAnnotations(boolean pGenerateAnnotations);
    
    String getPackage(String namespace);
    File getDirectory(String packageName);
	String getFullClassName(Type detailType);
}
