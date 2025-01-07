package com.sap.sdo.impl.xml.stream.adapters.impl;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;


public class SimpleContentElementAdapter extends AbstractElementAdapter implements ElementAdapter {
    private SimpleContentElementAdapter _simpleContent = null;

    protected SimpleContentElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    protected void fillIn(String pValue, AbstractElementAdapter pParent, HelperContext pContext) {
        super.fillIn(null, null, null, pValue, pParent, pContext);
    }

    protected void fillIn(
        String pUri, String pName, String pValue, AbstractElementAdapter pParent, HelperContext pContext) {

        super.fillIn(pUri, pName, null, pValue, pParent, pContext);
    }

    protected void fillIn(
        SdoProperty pProperty,
        String pValue,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pProperty.getUri(), pProperty.getXmlName(), null, null, pParent, pContext);
        String value = normalizeValue(pProperty, pValue);
        init(pProperty, null, null, null, value);
        wrapValue(value, pContext);
    }

    protected void fillIn(
        SdoProperty pProperty,
        GenericDataObject pChild,
        SdoProperty pValueProp,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pProperty.getUri(), pProperty.getXmlName(), pChild, null, pParent, pContext);
        String value = normalizeValue(pValueProp, pChild.getString(pValueProp));
        init(pProperty, pChild, pValueProp, (SdoType<?>)pChild.getType(), value);
        wrapValue(value, pContext);
    }

    protected void fillIn(
        String pUri,
        String pName,
        GenericDataObject pChild,
        SdoProperty pValueProp,
        String pValue,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pUri, pName, pChild, null, pParent, pContext);
        init(null, pChild, pValueProp, (SdoType<?>)pChild.getType(), pValue);
        wrapValue(pValue, pContext);
    }

    protected void fillReference(
        SdoProperty pProp,
        GenericDataObject pGdo,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        try {
            super.fillIn(pProp.getUri(), pProp.getXmlName(), null, null, pParent, pContext);
            String value = getReferenceHandler().generateReference(pGdo, null);
            init(pProp, null, null, null, value);
            wrapValue(value, pContext);
        } catch (XMLStreamException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public void clear() {
        super.clear();
        if (_simpleContent != null) {
            _simpleContent.clear();
            _simpleContent = null;
        }
        _adapterPool.returnAdapter(this);
    }

    @Override
    protected void init(SdoProperty pProp, GenericDataObject pData, SdoProperty pValueProp, SdoType<?> pType, String pValue) {
        if (pProp == null || pProp.isContainment() || pProp.getType().isDataType()) {
            if (pData != null) {
                List<Property> props = pData.getInstanceProperties();
                for (int i=0; i<props.size(); ++i) {
                    SdoProperty prop = (SdoProperty)props.get(i);
                    if (pData.isSet(prop) && !prop.isXmlElement()) {
                        addAttribute(prop);
                    }
                }
            }
            super.init(pProp, pData, pValueProp, pType, pValue);
        }
    }

    private void wrapValue(String pValue, HelperContext pContext) {
        if (pValue != null && pValue.length() > 0) {
            _simpleContent = _adapterPool.getSimpleContentElementAdapter();
            _simpleContent.fillIn(getNamespaceURI(), getLocalName(), pValue, this, pContext);
        }
    }

    public boolean hasChild() {
        return _simpleContent != null;
    }

    public ElementAdapter nextChild() {
        ElementAdapter content = _simpleContent;
        _simpleContent = null;
        return content;
    }
}
