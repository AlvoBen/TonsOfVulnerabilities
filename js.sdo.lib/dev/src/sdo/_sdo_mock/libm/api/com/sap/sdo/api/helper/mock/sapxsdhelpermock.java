/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.helper.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class SapXsdHelperMock implements SapXsdHelper {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#containsSchemaLocation(java.lang.String, java.lang.String)
     */
    public boolean containsSchemaLocation(String pTargetNamespace,
        String pSchemaLocation) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.lang.String, java.util.Map)
     */
    public List<Type> define(String pXsd, Map pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.Reader, java.lang.String, java.util.Map)
     */
    public List<Type> define(Reader pXsdReader, String pSchemaLocation,
        Map pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.InputStream, java.lang.String, java.util.Map)
     */
    public List<Type> define(InputStream pXsdInputStream,
        String pSchemaLocation, Map pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.util.List, java.util.Map)
     */
    public List<Type> define(List<Schema> pSchemas, Map pOptions)
        throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getDefaultSchemaResolver()
     */
    public SchemaResolver getDefaultSchemaResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getSdoName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getSdoName(URINamePair pUnp) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getXsdName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getXsdName(URINamePair pUnp) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#peekResolver()
     */
    public SchemaResolver peekResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#popResolver()
     */
    public SchemaResolver popResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#pushResolver(com.sap.sdo.api.helper.SchemaResolver)
     */
    public void pushResolver(SchemaResolver pResolver) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#setDefaultSchemaResolver(com.sap.sdo.api.helper.SchemaResolver)
     */
    public void setDefaultSchemaResolver(SchemaResolver pSchemaResolver) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.lang.String)
     */
    public List define(String pXsd) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.io.Reader, java.lang.String)
     */
    public List define(Reader pXsdReader, String pSchemaLocation) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.io.InputStream, java.lang.String)
     */
    public List define(InputStream pXsdInputStream, String pSchemaLocation) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#generate(java.util.List)
     */
    public String generate(List pTypes) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#generate(java.util.List, java.util.Map)
     */
    public String generate(List pTypes, Map pNamespaceToSchemaLocation) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getAppinfo(commonj.sdo.Type, java.lang.String)
     */
    public String getAppinfo(Type pType, String pSource) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getAppinfo(commonj.sdo.Property, java.lang.String)
     */
    public String getAppinfo(Property property, String pSource) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getGlobalProperty(java.lang.String, java.lang.String, boolean)
     */
    public Property getGlobalProperty(String pUri, String propertyName,
        boolean pIsElement) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getLocalName(commonj.sdo.Type)
     */
    public String getLocalName(Type pType) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getLocalName(commonj.sdo.Property)
     */
    public String getLocalName(Property property) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getNamespaceURI(commonj.sdo.Property)
     */
    public String getNamespaceURI(Property property) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getNamespaceURI(commonj.sdo.Type)
     */
    public String getNamespaceURI(Type type) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isAttribute(commonj.sdo.Property)
     */
    public boolean isAttribute(Property property) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isElement(commonj.sdo.Property)
     */
    public boolean isElement(Property property) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isMixed(commonj.sdo.Type)
     */
    public boolean isMixed(Type pType) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isXSD(commonj.sdo.Type)
     */
    public boolean isXSD(Type pType) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNil(DataObject pDataObject) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setNil(DataObject pDataObject, boolean pXsiNil) {
        // TODO Auto-generated method stub
    }

    public Property getInstanceProperty(DataObject pDataObject, String pUri, String pXsdName, boolean pIsElement) {
        // TODO Auto-generated method stub
        return null;
    }

    public Property getProperty(Type pType, String pUri, String pXsdName, boolean pIsElement) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#generateSchema(java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public Schema generateSchema(String pNamespace,
        Map<String, String> pNamespaceToSchemaLocation, Map pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

}
