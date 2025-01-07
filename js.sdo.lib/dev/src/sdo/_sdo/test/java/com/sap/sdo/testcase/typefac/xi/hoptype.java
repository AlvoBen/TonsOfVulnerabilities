package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface HopType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Engine",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.EngineType getEngine();
    void setEngine(com.sap.sdo.testcase.typefac.xi.EngineType pEngine);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Adapter",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.QualifiedNameType getAdapter();
    void setAdapter(com.sap.sdo.testcase.typefac.xi.QualifiedNameType pAdapter);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "MessageId",
        propertyIndex = 2,
        sdoType = "http://sap.com/xi/XI/Message/30#GUIDType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMessageId();
    void setMessageId(String pMessageId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Branch",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.BranchType> getBranch();
    void setBranch(java.util.List<com.sap.sdo.testcase.typefac.xi.BranchType> pBranch);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 4,
        sdoType = "http://sap.com/xi/XI/Message/30#String20Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getTimeStamp();
    void setTimeStamp(String pTimeStamp);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 5
    )
    boolean isWasRead();
    void setWasRead(boolean pWasRead);

}
