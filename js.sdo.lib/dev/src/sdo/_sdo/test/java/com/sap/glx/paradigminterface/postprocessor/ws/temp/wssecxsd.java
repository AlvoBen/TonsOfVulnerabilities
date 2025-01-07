package com.sap.glx.paradigmInterface.postprocessor.ws.temp;

public class WSSecXsd {

	static final String xsd = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	"<xs:schema\n" +
	"	targetNamespace='http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702'\n" +
	"  xmlns:tns='http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702'\n" +
	"	xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"\n" +
	"  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
	"	elementFormDefault=\"qualified\"\n" +
	"	blockDefault=\"#all\" >\n" +

	"  <xs:import namespace=\"http://www.w3.org/2005/08/addressing\" \n" +
	"		schemaLocation=\"ws-addr.xsd\" />\n" +

	"  <xs:element name=\"SignedParts\" type=\"tns:SePartsType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        4.1.1 SignedParts Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"EncryptedParts\" type=\"tns:SePartsType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        4.2.1 EncryptedParts Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"SePartsType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:element name=\"Body\" type=\"tns:EmptyType\" minOccurs=\"0\" />\n" +
	"      <xs:element name=\"Header\" type=\"tns:HeaderType\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\n" +
	"      <xs:element name=\"Attachments\" type=\"tns:EmptyType\" minOccurs=\"0\" />\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\"/>\n" +
	"    </xs:sequence>\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +
	"  <xs:complexType name=\"EmptyType\" />\n" +
	"  <xs:complexType name=\"HeaderType\" >\n" +
	"    <xs:attribute name=\"Name\" type=\"xs:QName\" use=\"optional\" />\n" +
	"    <xs:attribute name=\"Namespace\" type=\"xs:anyURI\" use=\"required\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"SignedElements\" type=\"tns:SerElementsType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\" >\n" +
	"        4.1.2 SignedElements Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"EncryptedElements\" type=\"tns:SerElementsType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        4.2.2 EncryptedElements Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequiredElements\" type=\"tns:SerElementsType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\" >\n" +
	"        4.3.1 RequiredElements Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"SerElementsType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:element name=\"XPath\" type=\"xs:string\" minOccurs=\"1\" maxOccurs=\"unbounded\" />\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\"/>\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute name=\"XPathVersion\" type=\"xs:anyURI\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:attribute name=\"IncludeToken\" type=\"tns:IncludeTokenOpenType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.1 Token Inclusion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:attribute>\n" +
	"  <xs:simpleType name=\"IncludeTokenOpenType\">\n" +
	"    <xs:union memberTypes=\"tns:IncludeTokenType xs:anyURI\" />\n" +
	"  </xs:simpleType>\n" +
	"  <xs:simpleType name=\"IncludeTokenType\">\n" +
	"    <xs:restriction base=\"xs:anyURI\" >\n" +
	"      <xs:enumeration value=\"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Never\" />\n" +
	"      <xs:enumeration value=\"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Once\" />\n" +
	"      <xs:enumeration value=\"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/AlwaysToRecipient\" />\n" +
	"      <xs:enumeration value=\"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/AlwaysToInitiator\" />\n" +
	"      <xs:enumeration value=\"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Always\" />\n" +
	"    </xs:restriction>\n" +
	"  </xs:simpleType>\n" +

	"  <xs:element name=\"UsernameToken\" type=\"tns:TokenAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\" >\n" +
	"        5.4.1 UsernameToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"TokenAssertionType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:choice minOccurs=\"0\">\n" +
	"        <xs:element name=\"Issuer\" type=\"wsa:EndpointReferenceType\" />\n" +
	"        <xs:element name=\"IssuerName\" type=\"xs:anyURI\" />\n" +
	"      </xs:choice>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\"/>\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute ref=\"tns:IncludeToken\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"NoPassword\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.1 UsernameToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"HashPassword\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.1 UsernameToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssUsernameToken10\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.1 UsernameToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssUsernameToken11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.1 UsernameToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:complexType name=\"QNameAssertionType\">\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"IssuedToken\" type=\"tns:IssuedTokenType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"IssuedTokenType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:choice minOccurs=\"0\">\n" +
	"        <xs:element name=\"Issuer\" type=\"wsa:EndpointReferenceType\" />\n" +
	"        <xs:element name=\"IssuerName\" type=\"xs:anyURI\" />\n" +
	"      </xs:choice>\n" +
	"      <xs:element name=\"RequestSecurityTokenTemplate\" type=\"tns:RequestSecurityTokenTemplateType\" />\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\" />\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute ref=\"tns:IncludeToken\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +
	"  <xs:complexType name=\"RequestSecurityTokenTemplateType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\" />\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute name=\"TrustVersion\" type=\"xs:anyURI\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"RequireDerivedKeys\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireImpliedDerivedKeys\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireExplicitDerivedKeys\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireExternalReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireInternalReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.2 IssuedToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"X509Token\" type=\"tns:TokenAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"  <xs:element name=\"RequireKeyIdentifierReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireIssuerSerialReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireEmbeddedTokenReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireThumbprintReference\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509V3Token10\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509Pkcs7Token10\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509PkiPathV1Token10\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509V1Token11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509V3Token11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509Pkcs7Token11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssX509PkiPathV1Token11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.3 X509Token Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"KerberosToken\" type=\"tns:TokenAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.4 KerberosToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"WssKerberosV5ApReqToken11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.4 KerberosToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssGssKerberosV5ApReqToken11\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.4 KerberosToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SpnegoContextToken\" type=\"tns:SpnegoContextTokenType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\" >\n" +
	"        5.4.5 SpnegoContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"SpnegoContextTokenType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:choice minOccurs=\"0\">\n" +
	"        <xs:element name=\"Issuer\" type=\"wsa:EndpointReferenceType\" />\n" +
	"        <xs:element name=\"IssuerName\" type=\"xs:anyURI\" />\n" +
	"      </xs:choice>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\" />\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute ref=\"tns:IncludeToken\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +
	"  <xs:element name=\"MustNotSendCancel\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.5 SpnegoContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustNotSendAmend\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.5 SpnegoContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustNotSendRenew\" type=\"tns:QNameAssertionType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.5 SpnegoContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SecurityContextToken\" type=\"tns:TokenAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.6 SecurityContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"RequireExternalUriReference\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.6 SecurityContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"SC13SecurityContextToken\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.6 SecurityContextToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SecureConversationToken\" type=\"tns:SecureConversationTokenType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.7 SecureConversationToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"SecureConversationTokenType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:choice minOccurs=\"0\">\n" +
	"        <xs:element name=\"Issuer\" type=\"wsa:EndpointReferenceType\" />\n" +
	"        <xs:element name=\"IssuerName\" type=\"xs:anyURI\" />\n" +
	"      </xs:choice>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\" />\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute ref=\"tns:IncludeToken\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"BootstrapPolicy\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.7 SecureConversationToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SamlToken\" type=\"tns:TokenAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\" >\n" +
	"        5.4.8 SamlToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"WssSamlV11Token10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.8 SamlToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssSamlV11Token11\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.8 SamlToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssSamlV20Token11\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.8 SamlToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"RelToken\" type=\"tns:TokenAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.9 RelToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"WssRelV10Token10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.9 RelToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssRelV20Token10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.9 RelToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssRelV10Token11\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.9 RelToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"WssRelV20Token11\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.9 RelToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"HttpsToken\" type=\"tns:TokenAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.10 HttpsToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"HttpBasicAuthentication\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.10 HttpsToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"HttpDigestAuthentication\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.10 HttpsToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireClientCertificate\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.10 HttpsToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"  <xs:element name=\"KeyValueToken\" type=\"tns:KeyValueTokenType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.11 KeyValueToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"KeyValueTokenType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\" />\n" +
	"    </xs:sequence>\n" +
	"    <xs:attribute ref=\"tns:IncludeToken\" use=\"optional\" />\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +
	"  <xs:element name=\"RsaKeyValue\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        5.4.11 KeyValueToken Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"  <xs:element name=\"AlgorithmSuite\" type=\"tns:NestedPolicyType\" >\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:complexType name=\"NestedPolicyType\">\n" +
	"    <xs:sequence>\n" +
	"      <xs:any minOccurs=\"0\" maxOccurs=\"unbounded\" namespace=\"##other\" processContents=\"lax\"/>\n" +
	"    </xs:sequence>\n" +
	"    <xs:anyAttribute namespace=\"##any\" processContents=\"lax\" />\n" +
	"  </xs:complexType>\n" +

	"  <xs:element name=\"Basic256\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic192\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic128\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"TripleDes\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic256Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic192Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic128Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"TripleDesRsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic256Sha256\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic192Sha256\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic128Sha256\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"TripleDesSha256\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic256Sha256Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic192Sha256Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Basic128Sha256Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"TripleDesSha256Rsa15\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"InclusiveC14N\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"SOAPNormalization10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"STRTransform10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"XPath10\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"XPathFilter20\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"AbsXPath\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.1 AlgorithmSuite Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"Layout\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.2 Layout Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"Strict\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.2 Layout Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"Lax\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.2 Layout Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"LaxTsFirst\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.2 Layout Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"LaxTsLast\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.2 Layout Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"TransportBinding\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.3 TransportBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"TransportToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.3 TransportBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"IncludeTimestamp\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.3 TransportBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SymmetricBinding\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"EncryptionToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"SignatureToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8=7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"ProtectionToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"EncryptBeforeSigning\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"EncryptSignature\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"ProtectTokens\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"OnlySignEntireHeadersAndBody\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.4 SymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"AsymmetricBinding\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"InitiatorToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"InitiatorSignatureToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"InitiatorEncryptionToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"RecipientToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"RecipientSignatureToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"RecipientEncryptionToken\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        7.5 AsymmetricBinding Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.1 SupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SignedSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.2 SignedSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"EndorsingSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.3 EndorsingSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SignedEndorsingSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.4 SignedEndorsingSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SignedEncryptedSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.5 SignedEncryptedSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"EncryptedSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.6 EncryptedSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"  <xs:element name=\"EndorsingEncryptedSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.7 EndorsingEncryptedSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"SignedEndorsingEncryptedSupportingTokens\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        8.8 SignedEndorsingEncryptedSupportingTokens Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"  <xs:element name=\"Wss10\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.1 Wss10 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"MustSupportRefKeyIdentifier\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.1 Wss10 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportRefIssuerSerial\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.1 Wss10 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportRefExternalURI\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.1 Wss10 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportRefEmbeddedToken\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.1 Wss10 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"Wss11\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.2 Wss11 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"MustSupportRefThumbprint\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.2 Wss11 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportRefEncryptedKey\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.2 Wss11 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireSignatureConfirmation\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        9.2 Wss11 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"Trust13\" type=\"tns:NestedPolicyType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +

	"  <xs:element name=\"MustSupportClientChallenge\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportServerChallenge\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireClientEntropy\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireServerEntropy\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"MustSupportIssuedTokens\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireRequestSecurityTokenCollection\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  <xs:element name=\"RequireAppiesTo\" type=\"tns:QNameAssertionType\">\n" +
	"    <xs:annotation>\n" +
	"      <xs:documentation xml:lang=\"en\">\n" +
	"        10.1 Trust13 Assertion\n" +
	"      </xs:documentation>\n" +
	"    </xs:annotation>\n" +
	"  </xs:element>\n" +
	"  \n" +
	"</xs:schema>\n";
	
	static final String xsd_wsa = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
	"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:tns=\"http://www.w3.org/2005/08/addressing\" targetNamespace=\"http://www.w3.org/2005/08/addressing\" blockDefault=\"#all\" elementFormDefault=\"qualified\" finalDefault=\"\" attributeFormDefault=\"unqualified\">\n" +
	"	\n" +
	"	<xs:element name=\"EndpointReference\" type=\"tns:EndpointReferenceType\"/>\n" +
	"	<xs:complexType name=\"EndpointReferenceType\" mixed=\"false\">\n" +
	"		<xs:sequence>\n" +
	"			<xs:element name=\"Address\" type=\"tns:AttributedURIType\"/>\n" +
	"			<xs:element ref=\"tns:ReferenceParameters\" minOccurs=\"0\"/>\n" +
	"			<xs:element ref=\"tns:Metadata\" minOccurs=\"0\"/>\n" +
	"			<xs:any namespace=\"##other\" processContents=\"lax\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
	"		</xs:sequence>\n" +
	"		<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:element name=\"ReferenceParameters\" type=\"tns:ReferenceParametersType\"/>\n" +
	"	<xs:complexType name=\"ReferenceParametersType\" mixed=\"false\">\n" +
	"		<xs:sequence>\n" +
	"			<xs:any namespace=\"##any\" processContents=\"lax\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
	"		</xs:sequence>\n" +
	"		<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:element name=\"Metadata\" type=\"tns:MetadataType\"/>\n" +
	"	<xs:complexType name=\"MetadataType\" mixed=\"false\">\n" +
	"		<xs:sequence>\n" +
	"			<xs:any namespace=\"##any\" processContents=\"lax\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
	"		</xs:sequence>\n" +
	"		<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:element name=\"MessageID\" type=\"tns:AttributedURIType\"/>\n" +
	"	<xs:element name=\"RelatesTo\" type=\"tns:RelatesToType\"/>\n" +
	"	<xs:complexType name=\"RelatesToType\" mixed=\"false\">\n" +
	"		<xs:simpleContent>\n" +
	"			<xs:extension base=\"xs:anyURI\">\n" +
	"				<xs:attribute name=\"RelationshipType\" type=\"tns:RelationshipTypeOpenEnum\" use=\"optional\" default=\"http://www.w3.org/2005/08/addressing/reply\"/>\n" +
	"				<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"			</xs:extension>\n" +
	"		</xs:simpleContent>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:simpleType name=\"RelationshipTypeOpenEnum\">\n" +
	"		<xs:union memberTypes=\"tns:RelationshipType xs:anyURI\"/>\n" +
	"	</xs:simpleType>\n" +
	"	\n" +
	"	<xs:simpleType name=\"RelationshipType\">\n" +
	"		<xs:restriction base=\"xs:anyURI\">\n" +
	"			<xs:enumeration value=\"http://www.w3.org/2005/08/addressing/reply\"/>\n" +
	"		</xs:restriction>\n" +
	"	</xs:simpleType>\n" +
	"	\n" +
	"	<xs:element name=\"ReplyTo\" type=\"tns:EndpointReferenceType\"/>\n" +
	"	<xs:element name=\"From\" type=\"tns:EndpointReferenceType\"/>\n" +
	"	<xs:element name=\"FaultTo\" type=\"tns:EndpointReferenceType\"/>\n" +
	"	<xs:element name=\"To\" type=\"tns:AttributedURIType\"/>\n" +
	"	<xs:element name=\"Action\" type=\"tns:AttributedURIType\"/>\n" +

	"	<xs:complexType name=\"AttributedURIType\" mixed=\"false\">\n" +
	"		<xs:simpleContent>\n" +
	"			<xs:extension base=\"xs:anyURI\">\n" +
	"				<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"			</xs:extension>\n" +
	"		</xs:simpleContent>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:attribute name=\"IsReferenceParameter\" type=\"xs:boolean\"/>\n" +
	"	\n" +
	"	<xs:simpleType name=\"FaultCodesOpenEnumType\">\n" +
	"		<xs:union memberTypes=\"tns:FaultCodesType xs:QName\"/>\n" +
	"	</xs:simpleType>\n" +
	"	\n" +
	"	<xs:simpleType name=\"FaultCodesType\">\n" +
	"		<xs:restriction base=\"xs:QName\">\n" +
	"			<xs:enumeration value=\"tns:InvalidAddressingHeader\"/>\n" +
	"			<xs:enumeration value=\"tns:InvalidAddress\"/>\n" +
	"			<xs:enumeration value=\"tns:InvalidEPR\"/>\n" +
	"			<xs:enumeration value=\"tns:InvalidCardinality\"/>\n" +
	"			<xs:enumeration value=\"tns:MissingAddressInEPR\"/>\n" +
	"			<xs:enumeration value=\"tns:DuplicateMessageID\"/>\n" +
	"			<xs:enumeration value=\"tns:ActionMismatch\"/>\n" +
	"			<xs:enumeration value=\"tns:MessageAddressingHeaderRequired\"/>\n" +
	"			<xs:enumeration value=\"tns:DestinationUnreachable\"/>\n" +
	"			<xs:enumeration value=\"tns:ActionNotSupported\"/>\n" +
	"			<xs:enumeration value=\"tns:EndpointUnavailable\"/>\n" +
	"		</xs:restriction>\n" +
	"	</xs:simpleType>\n" +
	"	\n" +
	"	<xs:element name=\"RetryAfter\" type=\"tns:AttributedUnsignedLongType\"/>\n" +
	"	<xs:complexType name=\"AttributedUnsignedLongType\" mixed=\"false\">\n" +
	"		<xs:simpleContent>\n" +
	"			<xs:extension base=\"xs:unsignedLong\">\n" +
	"				<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"			</xs:extension>\n" +
	"		</xs:simpleContent>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:element name=\"ProblemHeaderQName\" type=\"tns:AttributedQNameType\"/>\n" +
	"	<xs:complexType name=\"AttributedQNameType\" mixed=\"false\">\n" +
	"		<xs:simpleContent>\n" +
	"			<xs:extension base=\"xs:QName\">\n" +
	"				<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"			</xs:extension>\n" +
	"		</xs:simpleContent>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"	<xs:element name=\"ProblemIRI\" type=\"tns:AttributedURIType\"/>\n" +
	"	\n" +
	"	<xs:element name=\"ProblemAction\" type=\"tns:ProblemActionType\"/>\n" +
	"	<xs:complexType name=\"ProblemActionType\" mixed=\"false\">\n" +
	"		<xs:sequence>\n" +
	"			<xs:element ref=\"tns:Action\" minOccurs=\"0\"/>\n" +
	"			<xs:element name=\"SoapAction\" minOccurs=\"0\" type=\"xs:anyURI\"/>\n" +
	"		</xs:sequence>\n" +
	"		<xs:anyAttribute namespace=\"##other\" processContents=\"lax\"/>\n" +
	"	</xs:complexType>\n" +
	"	\n" +
	"</xs:schema>\n";

}
