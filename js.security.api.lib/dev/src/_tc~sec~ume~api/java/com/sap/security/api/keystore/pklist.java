package com.sap.security.api.keystore;

import iaik.x509.X509Certificate;
import iaik.asn1.structures.Name;


/**
 *  PKList is an interface to the PKList object from the SECUDE PSE format.
 *  PKList has the purpose to store trusted certificates.
 */
public interface PKList extends secudePSEObject
{
    /** Allows to remove certificates from the PKList.
     *  @param subject Distinguished name of the issuer of the certificate
     */
    public void     removeCertificate (Name subject);

    /** Adds a X.509 certificate to the PKList.
     *  @param cert certificate to be added.
     */
    public void     addCertificate (X509Certificate cert);
}
