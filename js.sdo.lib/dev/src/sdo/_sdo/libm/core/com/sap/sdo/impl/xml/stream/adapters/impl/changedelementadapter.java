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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.util.DataObjectBehavior;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class ChangedElementAdapter extends AbstractElementAdapter implements
    ElementAdapter {

    private ChangeSummary _cs;
    private final List<Setting> _elementSettings = new ArrayList<Setting>();
    private int _settingsIndex = 0;
    private List<Object> _currentValueList = null;
    private int _currentValueIndex = 0;
    private Sequence _oldSequence;
    private int _sequenceIndex = 0;
    private SimpleContentElementAdapter _simpleContent;

    /**
     * @param pAdapterPool
     */
    protected ChangedElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    /**
     * @param elementUri
     * @param elementName
     * @param _cs
     * @param decorator
     * @param changeSummaryAdapter
     * @param context
     */
    protected void fillIn(
        String elementUri,
        String elementName,
        ChangeSummary cs,
        DataObjectDecorator decorator,
        AbstractElementAdapter parent,
        HelperContext context) {

        super.fillIn(elementUri, elementName, null, null, parent, context);
        _cs = cs;
        init(decorator, null);
    }

    private void fillIn(
        SdoProperty element,
        ChangeSummary cs,
        DataObjectDecorator decorator,
        AbstractElementAdapter parent,
        HelperContext context) {

        super.fillIn(element.getUri(), element.getXmlName(), null, null, parent, context);
        _cs = cs;
        init(decorator, element);
    }



    @Override
    public void clear() {
        super.clear();
        _elementSettings.clear();
        _cs = null;
        _currentValueIndex = 0;
        _currentValueList = null;
        _oldSequence = null;
        _sequenceIndex = 0;
        _settingsIndex = 0;

        if (_simpleContent != null) {
            _simpleContent.clear();
            _simpleContent = null;
        }

        _adapterPool.returnAdapter(this);
    }

    /**
     * @param decorator
     */
    private void init(DataObjectDecorator decorator, SdoProperty prop) {
        SdoType<?> type = (SdoType<?>)decorator.getType();
        SdoProperty valueProp = TypeHelperImpl.getSimpleContentProperty(decorator);
        if (_cs.isDeleted(decorator)) {
            if (!URINamePair.MIXEDTEXT_TYPE.equalsUriName(type)) {
                if (prop == null
                        || (type != null && prop.getType() != type)
                        || (prop.isOpenContent()
                                && getRootElement().getDataObject() != decorator.getInstance()
                                && prop.getContainingType()==null)) {
                    addXsiType(decorator, prop, valueProp);
                }
            } else if (prop != null && prop.get(PropertyType.getXsdTypeProperty()) != null) {
                addXsiType(decorator, prop, valueProp);
            }

        } else {
            if (valueProp == null || type != OpenType.getInstance()) {
                if (!type.isLocal()) {
                    addAttribute(URINamePair.XSI_URI, "type", getQname(type));
                }
            } else {
                URINamePair xsdUnp = valueProp.getXsdType();
                if (xsdUnp != null) {
                    addAttribute(
                        URINamePair.XSI_URI,
                        "type",
                        getQname(xsdUnp.getURI(), xsdUnp.getName()));
                }
            }
            try {
                addAttribute(
                    URINamePair.DATATYPE_URI, "ref",
                    getReferenceHandler().generateReference(decorator, _cs));
            } catch (XMLStreamException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        StringBuilder unsetProperties = new StringBuilder();
        _oldSequence = _cs.getOldSequence(decorator);
        for (Setting setting : (List<Setting>)_cs.getOldValues(decorator)) {
            SdoProperty sdoProp = (SdoProperty)setting.getProperty();
            if (!setting.isSet()) {
                if (!sdoProp.isOpenContent() || sdoProp.getContainingType()==null) {
                    unsetProperties.append(sdoProp.getXmlName());
                } else {
                    String uri = ((SdoType<?>)sdoProp.getContainingType()).getXmlUri();
                    String prefix = getPrefix(uri);
                    if (prefix == null) {
                        prefix = createPrefix(uri);
                    }
                    unsetProperties.append(prefix+":"+sdoProp.getXmlName());
                }
                unsetProperties.append(' ');
            } else if (sdoProp.isXmlElement()) {
                if (valueProp == null && _oldSequence == null) {
                    _elementSettings.add(setting);
                }
            } else {
                SdoType<Object> sdoType = (SdoType<Object>)sdoProp.getType();
                if (sdoType.isDataType()) {
                    String value = sdoType.convertToJavaClass(setting.getValue(), String.class);
                    if (value != null) {
                        value = DataObjectBehavior.encodeBase64Binary(sdoProp, value);
                        if (DataObjectBehavior.isQnameProperty(sdoProp)) {
                            if (value.indexOf(' ') > 0) {
                                final StringBuilder ret = new StringBuilder();
                                StringTokenizer tokenizer = new StringTokenizer(value, " ");
                                while (tokenizer.hasMoreTokens()) {
                                    String token = tokenizer.nextToken();
                                    ret.append(getQnameFromUriProperty(token));
                                    ret.append(' ');
                                }
                                value = ret.toString().trim();
                            } else {
                                value = getQnameFromUriProperty(value);
                            }
                        }
                    }
                    addAttribute(sdoProp.getUri(), sdoProp.getXmlName(), value);
                } else if (!sdoProp.isOppositeContainment()) {
                    DataObject dataValue = (DataObject)setting.getValue();
                    try {
                        addAttribute(
                            sdoProp.getUri(),
                            sdoProp.getXmlName(),
                            getReferenceHandler().generateReference(dataValue, _cs));
                    } catch (XMLStreamException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }
        }
        if (unsetProperties.length()>0) {
            addAttribute(
                URINamePair.DATATYPE_URI, "unset", unsetProperties.toString().trim());
        }
        if (valueProp != null) {
            String value;
            Setting oldSetting = _cs.getOldValue(decorator, valueProp);
            if (oldSetting == null) {
                value = decorator.getString(valueProp);
            } else {
                value = ((SdoType<Object>)valueProp.getType())
                    .convertToJavaClass(oldSetting.getValue(), String.class);
            }
            _simpleContent = _adapterPool.getSimpleContentElementAdapter();
            _simpleContent.fillIn(normalizeValue(valueProp, value), this, _helperContext);
        }

    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#hasChild()
     */
    public boolean hasChild() {
        return _simpleContent != null
            || (_oldSequence != null && _sequenceIndex < _oldSequence.size())
            || _settingsIndex < _elementSettings.size();
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.xml.stream.adapters.ElementAdapter#nextChild()
     */
    public ElementAdapter nextChild() {
        final ElementAdapter content;
        if (_simpleContent != null) {
            content = _simpleContent;
            _simpleContent = null;
        } else if (_oldSequence != null) {
            SdoProperty element = (SdoProperty)_oldSequence.getProperty(_sequenceIndex);
            Object value = _oldSequence.getValue(_sequenceIndex++);

            // TODO Text property shouldn't be passed
            if (element == null) {
                content = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)content).fillIn((String)value, this, _helperContext);
            } else if ("text".equals(element.getName()) && URINamePair.STRING.equalsUriName(element.getType())) {
                // mixed test
                content = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)content).fillIn(element, (String)value, this, _helperContext);
            } else {
                content = handlePropertyInCs(element, value, _cs);
            }
        } else {
            Setting setting = _elementSettings.get(_settingsIndex);
            SdoProperty element = (SdoProperty)setting.getProperty();
            Object value = setting.getValue();
            if (element.isMany()) {
                if (_currentValueList == null) {
                    _currentValueList = (List<Object>)value;
                }
                Object currentValue = _currentValueList.get(_currentValueIndex);
                content = handlePropertyInCs(element, currentValue, _cs);
                _currentValueIndex++;
                if (_currentValueIndex == _currentValueList.size()) {
                    _currentValueList = null;
                    _currentValueIndex = 0;
                    _settingsIndex++;
                }
            } else {
                content = handlePropertyInCs(element, value, _cs);
                _settingsIndex++;
            }
        }
        return content;
    }

    private ElementAdapter handlePropertyInCs(
        SdoProperty pProperty,
        Object pValue,
        ChangeSummary pChangeSummary) {

        SdoProperty element = pProperty;
        String elementUri = null;
        URINamePair refPropUnp = element.getRef();
        if (refPropUnp != null) {
            if ((pValue != null) && pValue instanceof DataObject && (element.getType() != ((DataObject)pValue).getType())) {
                final Property headProp = _helperContext.getXSDHelper().getGlobalProperty(refPropUnp.getURI(), refPropUnp.getName(), true);
                if (headProp != null) {
                    final Property best = findBestMatch((DataObject)pValue, headProp, _helperContext);
                    element = (SdoProperty)best;
                    if (best != headProp) {
                        elementUri = _helperContext.getXSDHelper().getNamespaceURI(best);
                    }
                }
            }
            elementUri = refPropUnp.getURI();
        }

        Type type = element.getType();
        if (elementUri == null) {
            elementUri = element.getUri();
        }
        if (pValue == null && element.isNullable()) {
            addAttribute(URINamePair.XSI_URI, "nil", "true");
        }
        if (URINamePair.XSD_TYPE.equalsUriName(type)) {
            // XSD was not changed
            // TODO is empty XSD in ChangeSummary right?
            return getSimpleElement(element, "");
        } else if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(type)) {
            // when the logging begun the ChangeSummary was empty
            return getSimpleElement(element, "");
        } else if (type.isDataType()) {
            return getSingleValuedElement(element, pValue);
        } else {
            if (pChangeSummary.isDeleted((DataObject)pValue) && element.isContainment()) {
                ChangedElementAdapter changed = _adapterPool.getChangedElementAdapter();
                changed.fillIn(element, _cs, (DataObjectDecorator)pValue, this, _helperContext);
                return changed;
            } else {
                ReferencedElementAdapter referenced = _adapterPool.getReferencedElementAdapter();
                referenced.fillIn(element, (DataObject)pValue, _cs, this, _helperContext);
                return referenced;
            }
        }
    }

    private ElementAdapter getSimpleElement(SdoProperty element, String pValue) {
        if (element.isOpenContent() && element.getContainingType() == null) {
            addXsiType(null, element, null);
        }
        if (pValue != null) {
            SimpleContentElementAdapter simpleContent = _adapterPool.getSimpleContentElementAdapter();
            simpleContent.fillIn(element, pValue, this, _helperContext);
            return simpleContent;
        }
        return null;
    }

    private ElementAdapter getSingleValuedElement(SdoProperty pElement, Object pValue) {
        ElementAdapter adapter = null;
        SdoType<Object> propType = (SdoType<Object>)pElement.getType();
        if (propType.isDataType()) {
            adapter = getSimpleElement(pElement, propType.convertToJavaClass(pValue, String.class));
        } else if (pElement.isContainment()) {
            adapter = _adapterPool.getNonSequencedElementAdapter();
            ((NonSequencedElementAdapter)adapter).fillIn(
                pElement,
                ((DataObjectDecorator)pValue).getInstance(),
                this, _helperContext);
        } else if (URINamePair.TYPE.equalsUriName(propType)) {
            if (!(pValue instanceof Namespace)) {
                final URINamePair value = propType.convertToJavaClass(pValue, URINamePair.class);
                adapter = getSimpleElement(pElement, value.toStandardSdoFormat());
            }
        } else  {
            try {
                adapter = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)adapter).fillIn(
                    pElement,
                    getReferenceHandler().generateReference((DataObject)pValue, null),
                    this, _helperContext);
            } catch (XMLStreamException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        return adapter;
    }
}
