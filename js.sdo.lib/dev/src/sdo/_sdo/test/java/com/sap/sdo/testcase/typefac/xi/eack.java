package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+Ack",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Ack"
        )},
    elementFormDefault = true
)
public interface EAck  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Status",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#AckStatusType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getStatus();
    void setStatus(String pStatus);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Category",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#AckCategoryType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getCategory();
    void setCategory(String pCategory);

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
        sdoName = "SystemAckNotSupported",
        propertyIndex = 4
    )
    boolean isSystemAckNotSupported();
    void setSystemAckNotSupported(boolean pSystemAckNotSupported);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "SystemErrorAckNotSupported",
        propertyIndex = 5
    )
    boolean isSystemErrorAckNotSupported();
    void setSystemErrorAckNotSupported(boolean pSystemErrorAckNotSupported);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "ApplicationAckNotSupported",
        propertyIndex = 6
    )
    boolean isApplicationAckNotSupported();
    void setApplicationAckNotSupported(boolean pApplicationAckNotSupported);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "ApplicationErrorAckNotSupported",
        propertyIndex = 7
    )
    boolean isApplicationErrorAckNotSupported();
    void setApplicationErrorAckNotSupported(boolean pApplicationErrorAckNotSupported);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        sdoName = "ArriveAtFinalReceiver",
        propertyIndex = 8
    )
    boolean isArriveAtFinalReceiver();
    void setArriveAtFinalReceiver(boolean pArriveAtFinalReceiver);

}
