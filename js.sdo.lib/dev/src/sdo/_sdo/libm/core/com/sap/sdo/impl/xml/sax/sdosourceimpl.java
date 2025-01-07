package com.sap.sdo.impl.xml.sax;

import javax.xml.transform.sax.SAXSource;

import commonj.sdo.helper.XMLDocument;

public class SDOSourceImpl extends SAXSource implements
    com.sap.sdo.api.helper.util.SDOSource {

    public SDOSourceImpl(XmlReaderImpl pReader) {
        super(pReader, null);
    }

    @Override
    public XMLDocument getDocument() {
        return getXMLReader().getDocument();
    }

    @Override
    public Object getOptions() {
        return getXMLReader().getOptions();
    }

    @Override
    public XmlReaderImpl getXMLReader() {
        return (XmlReaderImpl)super.getXMLReader();
    }
}
