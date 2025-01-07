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

import java.util.List;
import java.util.Map;

import commonj.sdo.DataObject;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;

public interface SapXmlDocument extends XMLDocument {
    
    /**
     * Retrieves the list of types that are defined within an XML document.  This is useful, for instance,
     * when parsing WSDLs.  Use {@link TypeHelper#define(List)} to load the types into the type system.
     * @return a list of DataObjects, all of which will have the type {commonj.sdo}Type, corresponding to
     * complex or simple types defined directly or indirectly (that is, via includes).
     */
    List<DataObject> getDefinedTypes();
    /**
     * Retrieves the list of types that are defined within an XML document
     * but where not defined in the current context.
     * Use {@link TypeHelper#define(List)} to load the types into the type system.
     * @return a list of DataObjects, all of which will have the type {commonj.sdo}Type, corresponding to
     * complex or simple types new defined directly or indirectly (that is, via includes).
     */
    List<DataObject> getNewDefinedTypes();
    /**
     * Retrieves the list of global elements that are defined within an XML document.  This is useful, for instance,
     * when parsing WSDLs.  Use {@link TypeHelper#defineOpenContentProperty(String, DataObject)} to load the types into the type system.
     * @return maps URIs to the list of DataObjects, all of which will have the type {commonj.sdo}Property, corresponding to
     * global elements defined directly or indirectly (that is, via includes).
     */
    Map<String,List<DataObject>> getDefinedProperties();

}
