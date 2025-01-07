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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.sdo.api.helper.SapXmlDocument;

import commonj.sdo.DataObject;

/**
 * @author D042774
 *
 */
public class XMLDocumentImpl implements SapXmlDocument {
    private boolean _xmlDeclaration = true;
    private String _encoding = "UTF-8";
    private String _xmlVersion = "1.0";

    private DataObject _rootObject = null;
    private String _rootElementURI = null;
    private String _rootElementName = null;
    private String _schemaLocation = null;
    private String _noNamespaceSchemaLocation = null;
    private List<XsdToTypesTranslator> _xsdToTypesTranslators;
    private List<DataObject> _xmlTypes;
    
    public XMLDocumentImpl(DataObject pDataObject, String pRootElementURI, String pRootElementName) {
        super();
        _rootObject = pDataObject;
        _rootElementURI = pRootElementURI;
        _rootElementName = pRootElementName;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getRootObject()
     */
    public DataObject getRootObject() {
        return _rootObject;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getRootElementURI()
     */
    public String getRootElementURI() {
        return _rootElementURI;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getRootElementName()
     */
    public String getRootElementName() {
        return _rootElementName;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getEncoding()
     */
    public String getEncoding() {
        return _encoding;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#setEncoding(java.lang.String)
     */
    public void setEncoding(String pEncoding) {
        _encoding = pEncoding;

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#isXMLDeclaration()
     */
    public boolean isXMLDeclaration() {
        return _xmlDeclaration;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#setXMLDeclaration(boolean)
     */
    public void setXMLDeclaration(boolean pXmlDeclaration) {
        _xmlDeclaration = pXmlDeclaration;

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getXMLVersion()
     */
    public String getXMLVersion() {
        return _xmlVersion;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#setXMLVersion(java.lang.String)
     */
    public void setXMLVersion(String pXmlVersion) {
        _xmlVersion = pXmlVersion;

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getSchemaLocation()
     */
    public String getSchemaLocation() {
        return _schemaLocation;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#setSchemaLocation(java.lang.String)
     */
    public void setSchemaLocation(String pSchemaLocation) {
        _schemaLocation = pSchemaLocation;

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#getNoNamespaceSchemaLocation()
     */
    public String getNoNamespaceSchemaLocation() {
        return _noNamespaceSchemaLocation;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.XMLDocument#setNoNamespaceSchemaLocation(java.lang.String)
     */
    public void setNoNamespaceSchemaLocation(String pSchemaLocation) {
        _noNamespaceSchemaLocation = pSchemaLocation;

    }

    public Map<String, List<DataObject>> getDefinedProperties() {
        Map<String, List<DataObject>> result = new HashMap<String, List<DataObject>>();
        for (XsdToTypesTranslator xsdToTypesTranslator: getXsdToTypesTranslators()) {
            result.putAll(xsdToTypesTranslator.getNamespaceToProperties());
        }
        return result;
    }

    public List<DataObject> getDefinedTypes() {
        List<DataObject> result = new ArrayList<DataObject>();
        for (XsdToTypesTranslator xsdToTypesTranslator: getXsdToTypesTranslators()) {
            result.addAll(xsdToTypesTranslator.getTypes());
        }
        if (_xmlTypes != null) {
            result.addAll(_xmlTypes);
        }
        return result;
    }
    
    public List<DataObject> getNewDefinedTypes() {
        List<DataObject> result = new ArrayList<DataObject>();
        for (XsdToTypesTranslator xsdToTypesTranslator: getXsdToTypesTranslators()) {
            result.addAll(xsdToTypesTranslator.getNewDefinedTypes());
        }
        return result;
    }

    private List<XsdToTypesTranslator> getXsdToTypesTranslators() {
        if (_xsdToTypesTranslators == null) {
            return Collections.emptyList();
        }
        if (!_xsdToTypesTranslators.isEmpty()) {
            // The last one could be not translated
            XsdToTypesTranslator lastXsdToTypesTranslator = _xsdToTypesTranslators.get(_xsdToTypesTranslators.size() - 1);
            try {
                // lazy translation of the schemas
                lastXsdToTypesTranslator.translateSchemas();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return _xsdToTypesTranslators;
    }

    public void setXsdToTypesTranslators(List<XsdToTypesTranslator> pXsdToTypesTranslators) {
        _xsdToTypesTranslators = pXsdToTypesTranslators;
    }
    
    public void setXmlTypes(List<DataObject> pXmlTypes) {
        _xmlTypes = pXmlTypes;
    }

}
