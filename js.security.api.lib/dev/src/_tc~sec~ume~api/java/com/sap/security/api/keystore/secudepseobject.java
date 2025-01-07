package com.sap.security.api.keystore;

import iaik.asn1.ASN1Object;

public interface secudePSEObject
{
    public String    getName     ();
    public ASN1Object getObject   ();
    public byte []   getEncoded  ();
}
