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

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SapXmlHelperMock implements SapXmlHelper {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.util.Map)
     */
    public Object load(XMLStreamReader pReader, Map pOptions)
        throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.stream.XMLStreamReader, java.lang.String, java.lang.String, java.util.Map)
     */
    public Object load(XMLStreamReader pReader, String pXsdTypeUri,
        String pXsdTypeName, Map pOptions) throws XMLStreamException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(java.lang.String)
     */
    public SapXmlDocument load(String pInputString) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(java.io.InputStream)
     */
    public SapXmlDocument load(InputStream pInputStream) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(java.io.InputStream, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(InputStream pInputStream, String pLocationURI,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(java.io.Reader, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Reader pInputReader, String pLocationURI,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#load(javax.xml.transform.Source, java.lang.String, java.lang.Object)
     */
    public SapXmlDocument load(Source pInputSource, String pLocationURI,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#save(java.lang.Object, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.xml.stream.XMLStreamWriter, java.util.Map)
     */
    public void save(Object pData, String pElementUri, String pElementName,
        String pXsdTypeUri, String pXsdTypeName, XMLStreamWriter pWriter,
        Map pOptions) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#createDocument(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public XMLDocument createDocument(DataObject pDataObject,
        String pRootElementURI, String pRootElementName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String)
     */
    public String save(DataObject pDataObject, String pRootElementURI,
        String pRootElementName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.DataObject, java.lang.String, java.lang.String, java.io.OutputStream)
     */
    public void save(DataObject pDataObject, String pRootElementURI,
        String pRootElementName, OutputStream pOutputStream) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.OutputStream, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, OutputStream pOutputStream,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, java.io.Writer, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Writer pOutputWriter,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLHelper#save(commonj.sdo.helper.XMLDocument, javax.xml.transform.Result, java.lang.Object)
     */
    public void save(XMLDocument pXmlDocument, Result pOutputResult,
        Object pOptions) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#createXMLStreamReader(commonj.sdo.helper.XMLDocument, java.util.Map)
     */
    public XMLStreamReader createXMLStreamReader(XMLDocument xmlDocument,
        Map<String, Object> options) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#createContentHandler(java.util.Map)
     */
    @Override
    public SDOContentHandler createContentHandler(Object pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapXmlHelper#createXMLReader(commonj.sdo.helper.XMLDocument, java.util.Map)
     */
    @Override
    public XMLReader createXMLReader(XMLDocument pXmlDocument,
        Map<String, Object> pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SDOResult createSDOResult(Object pOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SDOSource createSDOSource(XMLDocument pXmlDocument, Object pOptions) {
        // TODO Auto-generated method stub
        return null;
    }
}
