package com.sap.sdo.testcase.typefac;

import java.util.List;

import javax.xml.namespace.QName;

import com.sap.sdo.api.util.URINamePair;

public interface QNameURINamePairIntf {
    
    QName getQName();
    void setQName(QName qName);
    
    List<QName> getQNames();
    void setQNames(List<QName> qNames);
    
    URINamePair getURINamePair();
    void setURINamePair(URINamePair uriNamePair);
    
    List<URINamePair> getURINamePairs();
    void setURINamePairs(List<URINamePair> uriNamePairs);

}
