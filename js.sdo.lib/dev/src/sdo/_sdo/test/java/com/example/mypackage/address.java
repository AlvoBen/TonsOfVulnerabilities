package com.example.myPackage;

public interface Address  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true
    )
    String getName();
    void setName(String pName);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true
    )
    String getStreet();
    void setStreet(String pStreet);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true
    )
    String getCity();
    void setCity(String pCity);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true
    )
    String getState();
    void setState(String pState);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        containment = true
    )
    java.math.BigDecimal getZip();
    void setZip(java.math.BigDecimal pZip);
    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "US",
        containment = true
    )
    String getCountry();
    void setCountry(String pCountry);

}
