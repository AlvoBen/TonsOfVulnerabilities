package com.sap.sdo.testcase.typefac.xi;
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+Error",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Error"
        )},
    elementFormDefault = true
)
public interface EError  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Category",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#String20Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getCategory();
    void setCategory(String pCategory);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Code",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.ErrorCodeType getCode();
    void setCode(com.sap.sdo.testcase.typefac.xi.ErrorCodeType pCode);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "P1",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getP1();
    void setP1(String pP1);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "P2",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getP2();
    void setP2(String pP2);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "P3",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getP3();
    void setP3(String pP3);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "P4",
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getP4();
    void setP4(String pP4);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "AdditionalText",
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getAdditionalText();
    void setAdditionalText(String pAdditionalText);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "ApplicationFaultMessage",
        propertyIndex = 7,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.QualifiedNameType getApplicationFaultMessage();
    void setApplicationFaultMessage(com.sap.sdo.testcase.typefac.xi.QualifiedNameType pApplicationFaultMessage);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Stack",
        propertyIndex = 8,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getStack();
    void setStack(String pStack);

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

}
