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
            name = "PolicyAttachment"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface PolicyAttachment  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "AppliesTo",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.policy.AppliesTo getAppliesTo();
    void setAppliesTo(com.sap.tc.esmp.tools.wsdlexport.model.policy.AppliesTo pAppliesTo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Policy",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.Policy> getPolicy();
    void setPolicy(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.Policy> pPolicy);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "PolicyReference",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyReference> getPolicyReference();
    void setPolicyReference(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyReference> pPolicyReference);

}
