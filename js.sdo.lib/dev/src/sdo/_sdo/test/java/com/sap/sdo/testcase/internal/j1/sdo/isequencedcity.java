package com.sap.sdo.testcase.internal.j1.sdo;

import java.util.List;

import com.sap.sdo.api.SdoTypeMetaData;

@SdoTypeMetaData(sequenced=true, open=false)
public interface ISequencedCity {
    String getZip();
    void setZip(String zip);
    String getName();
    void setName(String name);
    String getState();
    void setState(String state);
    
    List<String> getStreets();
    void setStreets(List<String> streets);
}
