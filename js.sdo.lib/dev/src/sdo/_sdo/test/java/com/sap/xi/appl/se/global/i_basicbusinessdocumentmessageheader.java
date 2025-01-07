package com.sap.xi.appl.se.global;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "#BasicBusinessDocumentMessageHeader",
    uri = "http://sap.com/xi/APPL/SE/Global"
)
public interface I_BasicBusinessDocumentMessageHeader  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "$ID",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "ID"
        )
    )
    com.sap.xi.appl.se.global.I_BusinessDocumentMessageId getEId();
    void setEId(com.sap.xi.appl.se.global.I_BusinessDocumentMessageId pEId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "$UUID",
        sdoType = "http://sap.com/xi/APPL/SE/Global#\\#ATTRIBUTEINDEPENDENT_UUID",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "UUID"
        )
    )
    String getEUuid();
    void setEUuid(String pEUuid);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoName = "$ReferenceID",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "ReferenceID"
        )
    )
    com.sap.xi.appl.se.global.I_BusinessDocumentMessageId getEReferenceId();
    void setEReferenceId(com.sap.xi.appl.se.global.I_BusinessDocumentMessageId pEReferenceId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        sdoName = "$ReferenceUUID",
        sdoType = "http://sap.com/xi/APPL/SE/Global#\\#ATTRIBUTEINDEPENDENT_UUID",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "ReferenceUUID"
        )
    )
    String getEReferenceUuid();
    void setEReferenceUuid(String pEReferenceUuid);

}
