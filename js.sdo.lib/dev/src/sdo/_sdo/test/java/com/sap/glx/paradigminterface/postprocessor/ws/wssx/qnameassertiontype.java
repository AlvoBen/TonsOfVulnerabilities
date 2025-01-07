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
            name = "AbsXPath"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic128"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic128Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic128Sha256"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic128Sha256Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic192"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic192Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic192Sha256"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic192Sha256Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic256"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic256Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic256Sha256"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Basic256Sha256Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EncryptBeforeSigning"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EncryptSignature"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "HashPassword"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "HttpBasicAuthentication"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "HttpDigestAuthentication"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "IncludeTimestamp"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "InclusiveC14N"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Lax"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "LaxTsFirst"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "LaxTsLast"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustNotSendAmend"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustNotSendCancel"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustNotSendRenew"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportClientChallenge"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportIssuedTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefEmbeddedToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefEncryptedKey"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefExternalURI"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefIssuerSerial"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefKeyIdentifier"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportRefThumbprint"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MustSupportServerChallenge"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "NoPassword"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "OnlySignEntireHeadersAndBody"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ProtectTokens"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireAppiesTo"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireClientCertificate"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireClientEntropy"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireDerivedKeys"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireEmbeddedTokenReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireExplicitDerivedKeys"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireExternalReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireExternalUriReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireImpliedDerivedKeys"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireInternalReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireIssuerSerialReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireKeyIdentifierReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireRequestSecurityTokenCollection"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireServerEntropy"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireSignatureConfirmation"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequireThumbprintReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RsaKeyValue"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SC13SecurityContextToken"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SOAPNormalization10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "STRTransform10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Strict"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TripleDes"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TripleDesRsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TripleDesSha256"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "TripleDesSha256Rsa15"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssGssKerberosV5ApReqToken11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssKerberosV5ApReqToken11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssRelV10Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssRelV10Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssRelV20Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssRelV20Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssSamlV11Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssSamlV11Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssSamlV20Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssUsernameToken10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssUsernameToken11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509Pkcs7Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509Pkcs7Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509PkiPathV1Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509PkiPathV1Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509V1Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509V3Token10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "WssX509V3Token11"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "XPath10"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "XPathFilter20"
        )},
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface QNameAssertionType  {

}
