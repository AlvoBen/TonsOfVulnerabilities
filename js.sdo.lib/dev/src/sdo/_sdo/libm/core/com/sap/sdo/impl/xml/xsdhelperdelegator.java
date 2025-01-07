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
package com.sap.sdo.impl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XSDHelper;

/**
 * @author D042774
 *
 */
public class XSDHelperDelegator implements SapXsdHelper {
    private static final XSDHelper INSTANCE = new XSDHelperDelegator();

    /**
     * 
     */
    private XSDHelperDelegator() {
        super();
    }
    
    public static XSDHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.io.InputStream, java.lang.String)
     */
    public List define(InputStream pXsdInputStream, String pSchemaLocation) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().define(pXsdInputStream, pSchemaLocation);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.io.Reader, java.lang.String)
     */
    public List define(Reader pXsdReader, String pSchemaLocation) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().define(pXsdReader, pSchemaLocation);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#define(java.lang.String)
     */
    public List define(String pXsd) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().define(pXsd);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#generate(java.util.List, java.util.Map)
     */
    public String generate(List pTypes, Map pNamespaceToSchemaLocation) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().generate(pTypes, pNamespaceToSchemaLocation);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#generate(java.util.List)
     */
    public String generate(List pTypes) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().generate(pTypes);
    }

    public Schema generateSchema(String pNamespace, Map<String, String> pNamespaceToSchemaLocation, Map pOptions) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).generateSchema(pNamespace, pNamespaceToSchemaLocation, pOptions);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getAppinfo(commonj.sdo.Property, java.lang.String)
     */
    public String getAppinfo(Property property, String pSource) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getAppinfo(property, pSource);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getAppinfo(commonj.sdo.Type, java.lang.String)
     */
    public String getAppinfo(Type pType, String pSource) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getAppinfo(pType, pSource);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getGlobalProperty(java.lang.String, java.lang.String, boolean)
     */
    public Property getGlobalProperty(String pUri, String propertyName, boolean pIsElement) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getGlobalProperty(pUri, propertyName, pIsElement);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getLocalName(commonj.sdo.Property)
     */
    public String getLocalName(Property property) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getLocalName(property);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getLocalName(commonj.sdo.Type)
     */
    public String getLocalName(Type pType) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getLocalName(pType);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getNamespaceURI(commonj.sdo.Property)
     */
    public String getNamespaceURI(Property property) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().getNamespaceURI(property);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#getNamespaceURI(commonj.sdo.Type)
     */
    public String getNamespaceURI(Type type) {
        //TODO remove cast!
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getNamespaceURI(type);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isAttribute(commonj.sdo.Property)
     */
    public boolean isAttribute(Property property) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().isAttribute(property);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isElement(commonj.sdo.Property)
     */
    public boolean isElement(Property property) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().isElement(property);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isMixed(commonj.sdo.Type)
     */
    public boolean isMixed(Type pType) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().isMixed(pType);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XSDHelper#isXSD(commonj.sdo.Type)
     */
    public boolean isXSD(Type pType) {
        return SapHelperProviderImpl.getDefaultContext().getXSDHelper().isXSD(pType);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.lang.String, java.util.Map)
     */
    public List<Type> define(String pXsd, Map pOptions) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).define(pXsd, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.Reader, java.lang.String, java.util.Map)
     */
    public List<Type> define(Reader pXsdReader, String pSchemaLocation, Map pOptions) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).define(pXsdReader, pSchemaLocation, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.io.InputStream, java.lang.String, java.util.Map)
     */
    public List<Type> define(InputStream pXsdInputStream, String pSchemaLocation, Map pOptions) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).define(pXsdInputStream, pSchemaLocation, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#define(java.util.List, java.util.Map)
     */
    public List<Type> define(List<Schema> pSchemas, Map pOptions) throws IOException {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).define(pSchemas, pOptions);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getXSDHelper());
        return buf.toString();
    }

	@Deprecated
    public SchemaResolver peekResolver() {
		return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).peekResolver();
	}

    @Deprecated
	public SchemaResolver popResolver() {
		return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).popResolver();
	}

    @Deprecated
	public void pushResolver(SchemaResolver resolver) {
		((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).pushResolver(resolver);
	}

    public SchemaResolver getDefaultSchemaResolver() {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getDefaultSchemaResolver();
    }

    public void setDefaultSchemaResolver(SchemaResolver pSchemaResolver) {
        ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).setDefaultSchemaResolver(pSchemaResolver);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getSdoName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getSdoName(URINamePair pUnp) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getSdoName(pUnp);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXsdHelper#getXsdName(com.sap.sdo.api.util.URINamePair)
     */
    public URINamePair getXsdName(URINamePair pUnp) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getXsdName(pUnp);
    }

	public boolean containsSchemaLocation(String targetNamespace, String schemaLocation) {
		return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).containsSchemaLocation(targetNamespace, schemaLocation);
	}

    public boolean isNil(DataObject pDataObject) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).isNil(pDataObject);
    }

    public void setNil(DataObject pDataObject, boolean pXsiNil) {
        ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).setNil(pDataObject, pXsiNil);
    }

    public Property getProperty(Type pType, String pUri, String pXsdName, boolean pIsElement) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getProperty(pType, pUri, pXsdName, pIsElement);
    }
    
    public Property getInstanceProperty(DataObject pDataObject, String pUri, String pXsdName, boolean pIsElement) {
        return ((XSDHelperImpl)SapHelperProviderImpl.getDefaultContext().getXSDHelper()).getInstanceProperty(pDataObject, pUri, pXsdName, pIsElement);
    }
    
}
