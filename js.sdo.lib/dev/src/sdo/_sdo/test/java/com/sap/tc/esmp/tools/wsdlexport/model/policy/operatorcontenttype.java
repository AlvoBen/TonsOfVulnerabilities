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
            name = "ExactlyOne"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "All"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface OperatorContentType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Policy",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.Policy> getPolicy();
    void setPolicy(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.Policy> pPolicy);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "All",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType> getAll();
    void setAll(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType> pAll);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "ExactlyOne",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType> getExactlyOne();
    void setExactlyOne(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType> pExactlyOne);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "PolicyReference",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyReference> getPolicyReference();
    void setPolicyReference(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyReference> pPolicyReference);

}
