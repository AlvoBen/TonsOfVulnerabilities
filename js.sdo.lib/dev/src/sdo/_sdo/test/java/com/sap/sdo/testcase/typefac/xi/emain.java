package com.sap.sdo.testcase.typefac.xi;
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+Main",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Main"
        )},
    elementFormDefault = true
)
public interface EMain  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "MessageClass",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#MessageClassType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMessageClass();
    void setMessageClass(String pMessageClass);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "ProcessingMode",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#ProcessingModeType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getProcessingMode();
    void setProcessingMode(String pProcessingMode);

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
        sdoName = "RefToMessageId",
        propertyIndex = 3,
        sdoType = "http://sap.com/xi/XI/Message/30#GUIDType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getRefToMessageId();
    void setRefToMessageId(String pRefToMessageId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "ConversationId",
        propertyIndex = 4,
        sdoType = "http://sap.com/xi/XI/Message/30#String60Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getConversationId();
    void setConversationId(String pConversationId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "TimeSent",
        propertyIndex = 5,
        sdoType = "commonj.sdo#DateTime",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getTimeSent();
    void setTimeSent(String pTimeSent);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Sender",
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.PartyAndServiceType getSender();
    void setSender(com.sap.sdo.testcase.typefac.xi.PartyAndServiceType pSender);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Receiver",
        propertyIndex = 7,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.PartyAndServiceType getReceiver();
    void setReceiver(com.sap.sdo.testcase.typefac.xi.PartyAndServiceType pReceiver);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Interface",
        propertyIndex = 8,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.QualifiedNameType getInterface();
    void setInterface(com.sap.sdo.testcase.typefac.xi.QualifiedNameType pInterface);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://schemas.xmlsoap.org/soap/envelope/#mustUnderstand",
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 9,
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
        propertyIndex = 10,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "0",
        propertyIndex = 11
    )
    int getVersionMajor();
    void setVersionMajor(int pVersionMajor);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "0",
        propertyIndex = 12
    )
    int getVersionMinor();
    void setVersionMinor(int pVersionMinor);

}
