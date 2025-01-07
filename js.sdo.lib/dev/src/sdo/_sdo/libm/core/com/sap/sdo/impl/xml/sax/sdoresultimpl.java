package com.sap.sdo.impl.xml.sax;

import javax.xml.transform.sax.SAXResult;

import com.sap.sdo.api.helper.util.SDOResult;

import commonj.sdo.helper.XMLDocument;

public class SDOResultImpl extends SAXResult implements SDOResult {
    
    public SDOResultImpl(SdoContentHandlerImpl pHandler) {
        super(pHandler);
    }

    @Override
    public XMLDocument getDocument() {
        return getHandler().getDocument();
    }

    @Override
    public Object getOptions() {
        return getHandler().getOptions();
    }

    @Override
    public SdoContentHandlerImpl getHandler() {
        return (SdoContentHandlerImpl)super.getHandler();
    }

}
