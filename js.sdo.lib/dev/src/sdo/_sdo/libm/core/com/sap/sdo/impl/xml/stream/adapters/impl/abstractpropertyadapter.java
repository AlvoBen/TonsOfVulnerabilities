package com.sap.sdo.impl.xml.stream.adapters.impl;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.xml.stream.adapters.PropertyAdapter;

public abstract class AbstractPropertyAdapter implements PropertyAdapter {
    protected final AdapterPool _adapterPool;
    private String _uri;
    private String _name;
    private GenericDataObject _dataObject;
    private String _value;

    private String _prefix = null;

    protected AbstractPropertyAdapter(AdapterPool pAdapterPool) {
        super();
        _adapterPool = pAdapterPool;
    }

    protected void fillIn(String pUri, String pName, GenericDataObject pDataObject, String pValue) {
        _uri = (pUri != null ? pUri : "");
        _name = pName;
        _dataObject = pDataObject;
        _value = pValue;
    }

    public void clear() {
        _uri = null;
        _name = null;
        _dataObject = null;
        _value = null;
        _prefix = null;
    }

    public boolean isText() {
        return _value != null;
    }

    public String getValue() {
        return _value;
    }

    public String getLocalName() {
        return _name;
    }

    protected void setNamespacePrefix(String pPrefix) {
        _prefix = pPrefix;
    }

    public String getNamespacePrefix() {
        return _prefix;
    }

    public String getNamespaceURI() {
        return _uri;
    }

    /**
     * @return the _dataObject
     */
    public GenericDataObject getDataObject() {
        return _dataObject;
    }
}
