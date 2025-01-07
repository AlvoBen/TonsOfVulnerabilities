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
package com.sap.sdo.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SchemaInfo {
    /**
     * The schema location must be resolvable at runtime.<br/>
     * A schema location can be an absolute URL.<br/>
     * Example: "http://www.w3.org/2001/XMLSchema.xsd"<br/>
     * If the schema location is relative, the schema is searched by the class loader.
     * If the relative schema location starts with '/' the search starts at the
     * root package, otherwhise the search starts at the current package.<br/>
     * Example: Annotation in class com.sap.example.SdoBean<br/>
     * "bean.xsd" -> "com/sap/example/bean.xsd"<br/>
     * "/bean.xsd" -> "bean.xsd"<br/>
     * "/com/sap/example/schemas/bean.xsd" -> "com/sap/example/schemas/bean.xsd"<br/>
     * "schemas/bean.xsd" -> "com/sap/example/schemas/bean.xsd"<br/>
     * "../bean.xsd" -> "com/sap/bean.xsd"<br/>
     */
	String schemaLocation();
}
