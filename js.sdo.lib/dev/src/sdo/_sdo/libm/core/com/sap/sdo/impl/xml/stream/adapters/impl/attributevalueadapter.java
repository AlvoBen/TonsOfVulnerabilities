package com.sap.sdo.impl.xml.stream.adapters.impl;

import com.sap.sdo.impl.xml.stream.adapters.AttributeAdapter;


public class AttributeValueAdapter extends AbstractPropertyAdapter implements AttributeAdapter {

    protected AttributeValueAdapter(AdapterPool pAdapterPool) {
        super(pAdapterPool);
    }

    protected void fillIn(String pPrefix, String pUri, String pName, String pValue) {
        super.fillIn(pUri, pName, null, pValue);
        setNamespacePrefix(pPrefix);
    }

    @Override
    public void clear() {
        super.clear();
        _adapterPool.returnAdapter(this);
    }

}
