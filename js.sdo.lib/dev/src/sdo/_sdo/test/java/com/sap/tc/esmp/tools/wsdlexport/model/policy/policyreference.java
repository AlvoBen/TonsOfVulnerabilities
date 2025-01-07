package com.sap.tc.esmp.tools.wsdlexport.model.policy;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-policy.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://schemas.xmlsoap.org/ws/2004/09/policy",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "PolicyReference"
        )},
    elementFormDefault = true
)
public interface PolicyReference  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "URI",
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getUri();
    void setUri(String pUri);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Digest",
        propertyIndex = 1
    )
    byte[] getDigest();
    void setDigest(byte[] pDigest);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "http://schemas.xmlsoap.org/ws/2004/09/policy/Sha1Exc",
        sdoName = "DigestAlgorithm",
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getDigestAlgorithm();
    void setDigestAlgorithm(String pDigestAlgorithm);

}
