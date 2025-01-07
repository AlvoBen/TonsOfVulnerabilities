package com.sap.sdo.impl.xml;

public interface PathReferenceStep {
    
    String getUri();
    String getName();
    int getIndex();
    boolean isIdRef();
}
