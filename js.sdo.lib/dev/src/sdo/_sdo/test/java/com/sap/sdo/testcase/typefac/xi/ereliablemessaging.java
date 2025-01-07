package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+ReliableMessaging",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ReliableMessaging"
        )},
    elementFormDefault = true
)
public interface EReliableMessaging  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "QualityOfService",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#QualityOfServiceType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getQualityOfService();
    void setQualityOfService(String pQualityOfService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "QueueId",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#QueueIdType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getQueueId();
    void setQueueId(String pQueueId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://schemas.xmlsoap.org/soap/envelope/#mustUnderstand",
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 2,
        sdoType = "http://schemas.xmlsoap.org/soap/envelope/#@mustUnderstand"
    )
    boolean isMustUnderstand();
    void setMustUnderstand(boolean pMustUnderstand);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd#Id",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "SystemAckRequested",
        propertyIndex = 4
    )
    boolean isSystemAckRequested();
    void setSystemAckRequested(boolean pSystemAckRequested);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "SystemErrorAckRequested",
        propertyIndex = 5
    )
    boolean isSystemErrorAckRequested();
    void setSystemErrorAckRequested(boolean pSystemErrorAckRequested);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "ApplicationAckRequested",
        propertyIndex = 6
    )
    boolean isApplicationAckRequested();
    void setApplicationAckRequested(boolean pApplicationAckRequested);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "ApplicationErrorAckRequested",
        propertyIndex = 7
    )
    boolean isApplicationErrorAckRequested();
    void setApplicationErrorAckRequested(boolean pApplicationErrorAckRequested);

}
