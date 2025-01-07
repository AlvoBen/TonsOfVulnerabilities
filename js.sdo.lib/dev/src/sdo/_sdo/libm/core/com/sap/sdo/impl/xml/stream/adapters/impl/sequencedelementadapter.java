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
package com.sap.sdo.impl.xml.stream.adapters.impl;

import static com.sap.sdo.api.util.URINamePair.CHANGESUMMARY_TYPE;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_JAVA_URI;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static com.sap.sdo.api.util.URINamePair.PROP_SCHEMA_SCHEMA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.SchemaTranslator;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class SequencedElementAdapter extends AbstractElementAdapter implements ElementAdapter{
    private Sequence _sequence;
    private int _index = 0;

    /**
     * @param pAdapterPool
     */
    protected SequencedElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    protected void fillIn(
        SdoProperty pProperty,
        GenericDataObject pDataObject,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pProperty.getUri(), pProperty.getXmlName(), pDataObject, null, pParent, pContext);
        _sequence = pDataObject.getSequence();
        init(pProperty, pDataObject, null, (SdoType<?>)pDataObject.getType(), null);
    }

    /**
     * @param uri
     * @param name
     * @param dataObject
     * @param value
     */
    protected void fillIn(
        String uri,
        String name,
        GenericDataObject dataObject,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(uri, name, dataObject, null, pParent, pContext);
        _sequence = dataObject.getSequence();
        init(null, dataObject, null, (SdoType<?>)dataObject.getType(), null);
    }

    @Override
    public void clear() {
        super.clear();
        _index = 0;
        _sequence = null;
        _adapterPool.returnAdapter(this);
    }

    @Override
    protected void init(SdoProperty pProp, GenericDataObject pData, SdoProperty pValueProp, SdoType<?> pType, String pValue) {
        List<Property> props = pData.getInstanceProperties();
        for (int i=0; i<props.size(); ++i) {
            SdoProperty prop = (SdoProperty)props.get(i);
            if (pData.isSet(prop) && !prop.isXmlElement() && !prop.isOppositeContainment()) {
                addAttribute(prop);
            }
        }
        if (XsdType.getInstance().isInstance(pData)) {
            createPrefix(DATATYPE_JAVA_URI);
            createPrefix(DATATYPE_XML_URI);

            if (_sequence.size() == 0) {
                DataObject root = getRootElement().getDataObject();
                if (DataGraphType.getInstance().isInstance(root)) {
                    DataObject dataGraphRoot = null;
                    List<Property> dataGraphProps = root.getInstanceProperties();
                    for (int i=0; i<dataGraphProps.size(); ++i) {
                        SdoProperty prop = (SdoProperty)dataGraphProps.get(i);
                        if (prop.isXmlElement() && prop.isOpenContent()) {
                            dataGraphRoot = root.getDataObject(prop);
                            break;
                        }
                    }
                    if (dataGraphRoot != null) {
                        String targetNamespace = ((SdoType<?>)dataGraphRoot.getType()).getXmlUri();
                        final Set<Type> tset = new HashSet<Type>();
                        DataObjectBehavior.addUsedTypes(dataGraphRoot, tset, targetNamespace);
                        SchemaTranslator translator = new SchemaTranslator(_helperContext);
                        Map<String, String> nsDefinitions = Collections.emptyMap();
                        DataObject schema = translator.getSchema(new ArrayList<Type>(tset), nsDefinitions);
                        pData.setList(
                            _helperContext.getTypeHelper().getOpenContentProperty(
                                PROP_SCHEMA_SCHEMA.getURI(), PROP_SCHEMA_SCHEMA.getName()),
                            Collections.singletonList(schema));
                    }
                }
            }
        }
        super.init(pProp, pData, pValueProp, pType, pValue);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#getNextChild()
     */
    public ElementAdapter nextChild() {
        ElementAdapter result = null;
        SdoProperty prop = (SdoProperty)_sequence.getProperty(_index);
        Object value = _sequence.getValue(_index);
        if (prop != null) {
            SdoType<Object> type = (SdoType<Object>)prop.getType();
            if (CHANGESUMMARY_TYPE.equalsUriName(type)) {
                result = _adapterPool.getChangeSummaryAdapter();
                ((ChangeSummaryAdapter)result).fillIn(
                    prop, (ChangeSummary)value, this, _helperContext);
            } else if (type.isDataType()) {
                result = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)result).fillIn(
                    prop, type.convertToJavaClass(value, String.class), this, _helperContext);
                if (JavaSimpleType.OBJECT == type) {
                    ((SimpleContentElementAdapter)result).addXsiType(value.getClass());
                }
            } else {
                result = internGetElementAdapter(prop, (DataObject)value, _helperContext);
            }
        } else {
            result = _adapterPool.getSimpleContentElementAdapter();
            ((SimpleContentElementAdapter)result).fillIn(
                getNamespaceURI(), getLocalName(), (String)value, this, _helperContext);
        }
        _index++;
        return result;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#hasChild()
     */
    public boolean hasChild() {
        return _index < _sequence.size();
    }

}
