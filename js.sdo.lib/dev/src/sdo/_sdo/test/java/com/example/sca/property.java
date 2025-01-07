package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    sequenced = true
)
public interface Property  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName",
            xmlElement = false
        ),
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#QName",
            xmlElement = false
        ),
        sdoName = "type",
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getPropertyType();
    void setPropertyType(String pPropertyType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 2
    )
    boolean getMany();
    void setMany(boolean pMany);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 3
    )
    boolean getRequired();
    void setRequired(boolean pRequired);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getDefault();
    void setDefault(String pDefault);

}
