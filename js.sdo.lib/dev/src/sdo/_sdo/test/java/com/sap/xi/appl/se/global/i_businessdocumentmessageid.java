package com.sap.xi.appl.se.global;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "#BusinessDocumentMessageID",
    uri = "http://sap.com/xi/APPL/SE/Global"
)
public interface I_BusinessDocumentMessageId  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/APPL/SE/Global#\\#BusinessDocumentMessageID.Content",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "%schemeID",
        sdoType = "http://sap.com/xi/APPL/SE/Global#49115623E9F69CF2AB1111DDA32B003005F636A2",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdName = "schemeID"
        )
    )
    String get_SchemeId();
    void set_SchemeId(String p_SchemeId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoName = "%schemeAgencyID",
        sdoType = "http://sap.com/xi/APPL/SE/Global#49115623E9F9AA32AB1111DDBC3D003005F636A2",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdName = "schemeAgencyID"
        )
    )
    String get_SchemeAgencyId();
    void set_SchemeAgencyId(String p_SchemeAgencyId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        sdoName = "%schemeAgencySchemeAgencyID",
        sdoType = "http://sap.com/xi/APPL/SE/Global#\\#AgencyIdentificationCode",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdName = "schemeAgencySchemeAgencyID"
        )
    )
    String get_SchemeAgencySchemeAgencyId();
    void set_SchemeAgencySchemeAgencyId(String p_SchemeAgencySchemeAgencyId);

}
