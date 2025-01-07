package com.sap.sdo.impl.xml.stream.adapters.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.xml.stream.adapters.ElementAdapter;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;


public class NonSequencedElementAdapter extends AbstractElementAdapter implements ElementAdapter {
    private final List<SdoProperty> _elements = new ArrayList<SdoProperty>();
    private int _currentElementIndex = 0;
    private int _currentValueIndex = 0;
    private List<Object> _currentValueList = null;

    /**
     * @param pAdapterPool
     */
    protected NonSequencedElementAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    protected void fillIn(
        SdoProperty pProperty,
        GenericDataObject pChild,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pProperty.getUri(), pProperty.getXmlName(), pChild, null, pParent, pContext);
        init(pProperty, pChild, null, (pChild != null ? (SdoType<?>)pChild.getType() : null), null);
    }

    protected void fillIn(
        SdoProperty pProperty,
        GenericDataObject pChild,
        boolean pIsKey,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pProperty.getUri(), pProperty.getXmlName(), pChild, pIsKey, pParent, pContext);
        init(pProperty, pChild, null, (pChild != null ? (SdoType<?>)pChild.getType() : null), null);
    }

    protected void fillIn(
        String pUri,
        String pName,
        GenericDataObject pChild,
        AbstractElementAdapter pParent,
        HelperContext pContext) {

        super.fillIn(pUri, pName, pChild, null, pParent, pContext);
        init(null, pChild, null, (SdoType<?>)pChild.getType(), null);
    }

    @Override
    public void clear() {
        super.clear();
        _elements.clear();
        _currentElementIndex = 0;
        _currentValueIndex = 0;
        _currentValueList = null;
        _adapterPool.returnAdapter(this);
    }

    @Override
    protected void init(SdoProperty pProp, GenericDataObject pData, SdoProperty pValueProp, SdoType<?> pType, String pValue) {
        if (pData != null) {
            List<Property> props = pData.getInstanceProperties();
            for (int i=0; i<props.size(); ++i) {
                SdoProperty prop = (SdoProperty)props.get(i);
                if (pData.isSet(prop)) {
                    if (prop.isOppositeContainment()) {
                        continue;
                    }
                    if (prop.isXmlElement()) {
                        _elements.add(prop);
                    } else {
                        addAttribute(prop);
                    }
                } else if (prop.isOrphanHolder()) {
                    _elements.add(prop);
                }
            }
        }
        super.init(pProp, pData, pValueProp, pType, pValue);
    }

    public boolean hasChild() {
        if (_currentElementIndex >= _elements.size()) {
            return false;
        }
        SdoProperty prop = _elements.get(_currentElementIndex);
        if (prop.isMany() && prop.isOrphanHolder() && _currentValueList == null) {
            _currentValueList = (List)getOrphanHandler().getOrphanList(getDataObject(), prop);
            if (_currentValueList == null || _currentValueList.isEmpty()) {
                _currentElementIndex++;
                _currentValueList = null;
                return hasChild();
            }
        }
        return true;
    }

    public ElementAdapter nextChild() {
        ElementAdapter adapter = null;
        SdoProperty prop = _elements.get(_currentElementIndex);
        SdoType<Object> type = (SdoType<Object>)prop.getType();

        if (prop.isMany()) {
            if (_currentValueList == null) {
                if (prop.isOrphanHolder()) {
                    // fallback should already be loaded by hasChild()
                    _currentValueList = (List)getOrphanHandler().getOrphanList(getDataObject(), prop);
                    if (_currentValueList == null || _currentValueList.isEmpty()) {
                        _currentElementIndex++;
                        _currentValueList = null;
                        return nextChild();
                    }
                } else {
                    _currentValueList = getDataObject().getList(prop);
                }
            }
            Object value = _currentValueList.get(_currentValueIndex);
            if (type.isDataType()) {
                adapter = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)adapter).fillIn(
                    prop,
                    type.convertToJavaClass(value, String.class),
                    this,
                    _helperContext);
                if (JavaSimpleType.OBJECT == type) {
                    ((SimpleContentElementAdapter)adapter).addXsiType(value.getClass());
                }
            } else {
                adapter = internGetElementAdapter(prop, (DataObject)value, _helperContext);
            }
            _currentValueIndex++;
            if (_currentValueIndex == _currentValueList.size()) {
                _currentValueList = null;
                _currentValueIndex = 0;
                _currentElementIndex++;
            }
        } else {
            if (URINamePair.CHANGESUMMARY_TYPE.equalsUriName(type)) {
                adapter = _adapterPool.getChangeSummaryAdapter();
                ((ChangeSummaryAdapter)adapter).fillIn(
                    prop, (ChangeSummary)getDataObject().get(prop), this, _helperContext);
            } else if (type.isDataType()) {
                adapter = _adapterPool.getSimpleContentElementAdapter();
                ((SimpleContentElementAdapter)adapter).fillIn(
                        prop,
                        getDataObject().getString(prop),
                        this,
                        _helperContext);
                if (JavaSimpleType.OBJECT == type) {
                    Object value = getDataObject().get(prop);
                    ((SimpleContentElementAdapter)adapter).addXsiType(value.getClass());
                }
            } else {
                DataObject dataObject = getDataObject().getDataObject(prop);
                if (dataObject != null) {
                    adapter = internGetElementAdapter(prop, dataObject, _helperContext);
                }
            }
            _currentElementIndex++;
        }
        return adapter;
    }
}
