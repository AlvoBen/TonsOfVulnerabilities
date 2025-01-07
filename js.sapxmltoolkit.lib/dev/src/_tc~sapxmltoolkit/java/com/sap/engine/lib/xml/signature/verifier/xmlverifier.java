package com.sap.engine.lib.xml.signature.verifier;

import java.security.PublicKey;
import java.security.cert.Certificate;

import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.Reference;
import com.sap.engine.lib.xml.signature.transform.TransformationFactory;

/**
 * This is the API (along with <code> com.sap.engine.lib.xml.generator.XMLSigner</code>)
 * for the XML Signature implementation. It can be used to generate a digital signature
 * for arbitary data that can be referenced to by an URI.
 */
public interface XMLVerifier {
  /**
   *  If the verifying application has external acces to the public key that should
   *  be used to validate the signature (or it doesn't wish to rely on the public key
   *  that is included in the signature), this is the method that allows it to manually
   *  specify/override the public key.
   *
   * @param   publicKey  the public key that will be used to verify the signature
   */
  public void setPublicKey(PublicKey publicKey);


  /**
   *  If the verifying application has external acces to a certificate that contains
   *  the public key to be used to validate the signature (or it doesn't wish to rely on
   *  the public key that is included in the signature), this is the method that allows
   *  it to manually specify/override the public key.
   *
   * @param   certificate  the certificate that is to be used to get the public key
   */
  public void setCertificate(Certificate certificate);


  /**
   *  If it is expected that the signature will contain references by ID attribute this
   *  is the way to specify which attribute is ti be considered ID.
   *
   *  NOTE: This knowledge can not be obtained from the signature alone, it is necessary
   *  that the sender and receiver use the same convention for IDs - either adherring to
   *  some other XML protocol (like SOAP Signature: http://www.w3.org/TR/SOAP-dsig/) or
   *  based upon some other mutual agreement.
   *
   * @param   localName
   * @param   uri
   */
  public void setIDAttribute(String localName, String uri);

  
  /**
   *  If it is expected that the signature will contain references by ID attribute this
   *  is the way to specify which attribute is ti be considered ID.
   *
   *  NOTE: This knowledge can not be obtained from the signature alone, it is necessary
   *  that the sender and receiver use the same convention for IDs - either adherring to
   *  some other XML protocol (like SOAP Signature: http://www.w3.org/TR/SOAP-dsig/) or
   *  based upon some other mutual agreement.
   *
   * @param   localName
   * @param   uri
   */
  public void setTransformationFactory(TransformationFactory fact);


  /**
   *  After the XMLVerifier object has been properly configured (if such configuration
   *  is necessary) this method can determine whether the signature is valid or not.
   *
   * @return     true if the signature is valid and false otherwise
   * @exception   SignatureException
   */
  public boolean verify() throws SignatureException;


  /**
   *  If verify() has returned <code>false</code> use this method to get the reason.
   *
   * @return     the status code of the verification
   * @exception   IllegelStateException thrown if verify() has not been called
   */
  public int getSignatureValidationResult() throws IllegalStateException;


  /**
   *  This method helps the receiving application to extract the key info (if included)
   *  from the signature. If a previous invocation of setPublicKey() has overriden the
   *  key from the signature then the overriding key will be returned.
   *
   * @return     the public key that should be corresponding to the private key, used to sign the message
   * @exception   SignatureException
   * @exception   GeneralSecurityException
   */
  public PublicKey getPublicKey() throws SignatureException, java.security.GeneralSecurityException;

  
  /**
   *    
   *
   * @return     the key name ot null if there is no KeyName element
   * @exception   SignatureException
   */
  public String getKeyName() throws SignatureException;

  
  /**
   *  This method helps the receiving application to extract the certificate (if included)
   *  from the signature. If a previous invocation of setCertificate() has overriden the
   *  certificate from the signature then the overriding certificate will be returned.
   *
   * @return     the certificate that is represented in the signature, or the one that overrides it
   * @exception   SignatureException
   * @exception   GeneralSecurityException
   */
  public Certificate getCertificate() throws SignatureException, java.security.GeneralSecurityException;
   
   
  /**
   * Gets all the certificates from the signature. 
   *
   * @return     the certificates that are represented in the signature
   * @exception   SignatureException
   * @exception   GeneralSecurityException
   */
  public Certificate[] getCertificates() throws SignatureException, java.security.GeneralSecurityException;

  
  /**
   *  After <code>verify()</code> call this method to get detailed refernce validation results.
   *  Each reference contains a field named <code>status<code>. It represents the status of the
   *  reference validation for the corresponding reference.
   *
   *  NOTE: An invalid reference automatically invalidates the whole signature but does NOT
   *  prevent attempts to validate the remaining references.
   *
   * @return an array representing the references from the signature; the <code>status</code>
   * field contains the validation status of each reference
   */
  public Reference[] getReferenceValidationResults() throws IllegalStateException;
  
  /**
   * Sets additional attribute for the XML verifier. Supported attributes are <ol>
   * <li> @link #ADDITIONAL_NAMESPACE_MAPPINGS
   * </ol> 
   * @param name attribute name
   * @param object attribute value 
   * @throws SignatureException if this attribute is not supported or the attribute value is incorrect.
   */
  
  public void setAttribute(String name, Object object) throws SignatureException;
  
  /**
   * Returns the value of this attribute
   * @param name attribute name
   * @return attribute value
   */
  public Object getAttribute(String name);

}

