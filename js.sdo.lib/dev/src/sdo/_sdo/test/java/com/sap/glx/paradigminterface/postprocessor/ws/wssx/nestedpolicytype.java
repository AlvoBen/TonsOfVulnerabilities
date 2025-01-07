package com.sap.glx.paradigmInterface.postprocessor.ws.wssx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-securitypolicy-1.2.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "AlgorithmSuite"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "AsymmetricBinding"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "BootstrapPolicy"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EncryptedSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EncryptionToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EndorsingEncryptedSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EndorsingSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "InitiatorEncryptionToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "InitiatorSignatureToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "InitiatorToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Layout"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ProtectionToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RecipientEncryptionToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RecipientSignatureToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RecipientToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignatureToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedEncryptedSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedEndorsingEncryptedSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedEndorsingSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedSupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SupportingTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SymmetricBinding"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TransportBinding"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TransportToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Trust13"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Wss10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Wss11"
        )},
    sequenced = true,
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface NestedPolicyType  {

}
