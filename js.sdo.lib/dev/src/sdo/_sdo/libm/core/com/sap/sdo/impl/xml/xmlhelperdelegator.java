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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.xml.sax.XMLReader;

import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.util.SDOResult;
import com.sap.sdo.api.helper.util.SDOSource;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * @author D042774
 *
 */
public class XMLHelperDelegator implements SapXmlHelper {
    private static final XMLHelper INSTANCE = new XMLHelperDelegator();

    /**
     *
     */
    private XMLHelperDelegator() {
        super();
    }

    public static XMLHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#createDocument(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public XMLDocument createDocument(DataObject pDataObject, String pRootElementURI, String pRootElementName) {
        return SapHelperProviderImpl.getDefaultContext().getXMLHelper().createDocument(pDataObject, pRootElementURI, pRootElementName);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(InputStream pInputStream, String pLocationURI, Object pOptions) throws IOException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pInputStream, pLocationURI, pOptions);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.InputStream)
     */
    public SapXmlDocument load(InputStream pInputStream) throws IOException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pInputStream);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.io.Reader, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Reader pInputReader, String pLocationURI, Object pOptions) throws IOException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pInputReader, pLocationURI, pOptions);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(java.lang.String)
     */
    public SapXmlDocument load(String pInputString) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pInputString);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#load(javax.xml.transform.Source, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Source pInputSource, String pLocationURI, Object pOptions) throws IOException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pInputSource, pLocationURI, pOptions);
    }


    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.lang.Object)
     */
    public Object load(XMLStreamReader pReader, Map pOptions) throws XMLStreamException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper()).load(pReader, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.lang.String, java.lang.String, java.lang.Object)
     */
    public Object load(XMLStreamReader pReader, String pXsdTypeUri, String pXsdTypeName, Map pOptions) throws XMLStreamException {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
                .load(pReader, pXsdTypeUri, pXsdTypeName, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#save(java.lang.Object, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.xml.stream.XMLStreamWriter, java.lang.Object)
     */
    public void save(Object pData, String pElementUri, String pElementName, String pXsdTypeUri, String pXsdTypeName, XMLStreamWriter pWriter, Map pOptions) throws IOException {
        ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
            .save(pData, pElementUri, pElementName, pXsdTypeUri, pXsdTypeName, pWriter, pOptions);
    }
    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String, java.io.OutputStream)
     */
    public void save(DataObject pDataObject, String pRootElementURI, String pRootElementName, OutputStream pOutputStream) throws IOException {
        SapHelperProviderImpl.getDefaultContext().getXMLHelper().save(pDataObject, pRootElementURI, pRootElementName, pOutputStream);
    }
    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public String save(DataObject pDataObject, String pRootElementURI, String pRootElementName) {
        return SapHelperProviderImpl.getDefaultContext().getXMLHelper().save(pDataObject, pRootElementURI, pRootElementName);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.OutputStream, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, OutputStream pOutputStream, Object pOptions) throws IOException {
        SapHelperProviderImpl.getDefaultContext().getXMLHelper().save(pXmlDocument, pOutputStream, pOptions);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.Writer, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Writer pOutputWriter, Object pOptions) throws IOException {
        SapHelperProviderImpl.getDefaultContext().getXMLHelper().save(pXmlDocument, pOutputWriter, pOptions);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, javax.xml.transform.Result, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Result pOutputResult, Object pOptions) throws IOException {
        SapHelperProviderImpl.getDefaultContext().getXMLHelper().save(pXmlDocument, pOutputResult, pOptions);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#createXMLStreamReader(commonj.sdo.helper.XMLDocument, java.util.Map)
     */
    public XMLStreamReader createXMLStreamReader(XMLDocument xmlDocument, Map<String, Object> options) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
                .createXMLStreamReader(xmlDocument, options);
    }

    public XMLReader createXMLReader(XMLDocument pXmlDocument, Map<String, Object> pOptions) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
                .createXMLReader(pXmlDocument, pOptions);
    }

    public SDOContentHandler createContentHandler(Object pOptions) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
                .createContentHandler(pOptions);
    }

    @Override
    public SDOResult createSDOResult(Object pOptions) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
            .createSDOResult(pOptions);
    }

    @Override
    public SDOSource createSDOSource(XMLDocument pXmlDocument, Object pOptions) {
        return ((SapXmlHelper)SapHelperProviderImpl.getDefaultContext().getXMLHelper())
            .createSDOSource(pXmlDocument, pOptions);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getXMLHelper());
        return buf.toString();
    }

}
