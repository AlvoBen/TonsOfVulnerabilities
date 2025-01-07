package com.sap.engine.lib.xml.signature.generator;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.transform.Transformation;

/**
 * This is the API (along with <code> com.sap.engine.lib.xml.verfier.XMLVerifier</code>)
 * for the XML Signature implementation. It can be used to generate a digital signature
 * for arbitary data that can be referenced to by an URI.
 */
public interface XMLSigner {

  /**
   *  Exclusive Canonicalization - when document subsets are canonicalized the namespace
   *  prefixes that are not "visualy utilized" are not included. (see http://www.w3.org/TR/xml-exc-c14n)
   */
  public static final String EXCLUSIVE_CANONICALIZATION_NO_COMMMENTS = Constants.TR_C14N_EXCL_OMIT_COMMENTS;
  /**
   *  Exclusive canonicalization that retains comment nodes.
   */
  public static final String EXCLUSIVE_CANONICALIZATION_WITH_COMMMENTS = Constants.TR_C14N_EXCL_OMIT_COMMENTS;
  /**
   *  "Standard" canonicalization (http://www.w3.org/TR/xml-c14n). It is not advisory to
   *  sign parts of a document and use this canonicalization if it is likely that the
   *  document is to undergo (however minor) changes.
   */
  public static final String CANONICALIZATION_NO_COMMENTS = Constants.TR_C14N_OMIT_COMMENTS;
  /**
   *  Canonicalization that does not exclude comment nodes.
   */
  public static final String CANONICALIZATION_WITH_COMMENTS = Constants.TR_C14N_WITH_COMMENTS;
  /**
   *  XPath transformation.
   */
  public static final String XPATH = Constants.TR_XPATH;
  /**
   *  Enveloped transformation (as specified at http://www.w3.org/TR/xmldsig-core/).
   */
  public static final String ENVELOPED = Constants.TR_ENVELOPED_SIGNATURE;
  /**
   *  DSA signature algorithm uti.
   */
  public static final String DSA_SIGNATURE = Constants.SIGN_DSA;
  /**
   *  RSA signature algorithm uti.
   */
  public static final String RSA_SIGNATURE = Constants.SIGN_RSA;


  /**
   *  Adds a reference that is to be signed after the transformations are applied.
   *
   * @param   uri URI referencing the data to be signed - empty string stands for a same-document reference.
   * @param   transforms  an array of <code> com.sap.engine.lib.xml.signature.Transform</code> objects representing the transformation that ther data will undergo.
   * @param   digestMethod  the result form the last transformation is digested with this method.
   * @exception   SignatureException
   */
  public void addReference(String uri, Transformation[] transforms, String digestMethod) throws SignatureException;


  /**
   *  Adds a reference that is to be signed after the transformations are applied.
   *
   * @param   uri  uri URI referencing the data to be signed - empty string stands for a same-document reference.
   * @param   transforms  an array of <code> com.sap.engine.lib.xml.signature.Transform</code> objects representing the transformation that ther data will undergo.
   * @param   digestMethod  the result form the last transformation is digested with this method.
   * @param   proxyHost  if you need to dereference the uri and are behind an http firewall, specify the proxy host here.
   * @param   proxyPort  if you need to dereference the uri and are behind an http firewall, specify the proxy port here.
   * @exception   SignatureException
   */
  public void addReference(String uri, Transformation[] transforms, String digestMethod, String proxyHost, String proxyPort) throws SignatureException;


  /**
   *  Adds an Object element.
   *
   * @param   object  the child of the Object element
   * @exception   SignatureException
   */
  public void addObject(Node object) throws SignatureException;


  /**
   *  Adds an Object element with a given id.
   *
   * @param   object  the child of the Object element
   * @param   id      id of the Object element
   * @exception   SignatureException
   */
  public void addObject(Node object, String id) throws SignatureException;


  /**
   *  If the reference is going to contain same document references with ids (e.g. the
   *  reference element looks like: <ds:Reference URI="#someID">) it is a responsibilty
   *  of the application to specify which attribure should be considered an ID attribute.
   *  This method allows the user to specify the local name and uri of the id attribute. Note
   *  that the receiving application should have this information or it may be unable to
   *  verify the signature.
   *
   * @param   localName  the local name of the attribute that is to be treated as an ID
   * @param   uri  the URI of the attribute that is to be treated as an ID
   */
  public void setIDAttribute(String localName, String uri);


  /**
   *  Specifies what signature method will be used. The default is DSA.
   *
   * @param   signatureAlgorithmURI  URI of the signature algorithm
   */
  public void setSignatureAlgorithmURI(String signatureAlgorithmURI);


  /**
   *  Specifies the private key that will be used to sign the message.
   *
   * @param   privateKey  the private key that the message will be signed with
   */
  public void setPrivateKey(PrivateKey privateKey);


  /**
   *  Specifies the public key, corresponding to the private key that signs
   *  the message. It is not necessary to include the public key, but then the
   *  receiving application should have this information from some external source
   *  or else it would be unable to verify the signature.
   *
   * @param   publicKey
   */
   public void setPublicKey(PublicKey publicKey);

  /**
   *  Specifies the key name.
   *
   * @param   keyName
   */
  public void setKeyName(String keyName);

  /**
   *  An x.509 cerificate for the public key.
   *
   * @param   cert  an x.509 certificate for the public key
   */
  public void setCertificate(Certificate cert);
   
   /**
   *  Aset of x.509 cerificates for the public key.
   *
   * @param   cert  an x.509 certificate for the public key
   */
  public void setCertificates(Certificate[] certs);



  /**
   *  Specifes the algorithm that will be used to digest the SignedInfo element.
   *
   * @param   digestAlgorithmURI  URI for the corresponding algorithm
   */
  public void setDigestAlgorithm(String digestAlgorithmURI);


  /**
   *  Specifes the algorithm that will be used to canonicalize the SignedInfo element.
   *
   *  NOTE Though exclusive canonicalization (http://www.w3.org/2001/10/xml-exc-c14n#)
   *  is not required by the XML Signature specification, but it is highly recommended
   *  to use it (when possible) for signaures over parts of documents that are likely
   *  to have new namespace declarations added in the future.
   *
   * @param   cAlgorithmURI
   */
  public void setCanonicalizationAlgorithm(String cAlgorithmURI);
   
  
  /**
   *  Specifes whether KeyValue is to be included.
   *
   * @param   cAlgorithmURI
   */
  public void showKeyValue(boolean showKeyValue);



  /**
   *  Generates the XMLSignature and applies it as a last child to the
   *  DOM element. Note that this XMLSigner object should have been
   *  properly configured (using the "set"-methods).
   *
   * @param   parent  the home for the signature element
   * @return the signature element
   * @exception   SignatureException
   */
  public Node generate(Element parent) throws SignatureException;


  /**
   *  Generates the XMLSignature and applies it as a child node to the
   *  DOM element. Note that this XMLSigner object should have been
   *  properly configured (using the "set"-methods).
   *
   * @param   nextSibling  the node that will be the next sibling of the signaure
   * @return the signature element
   * @exception   SignatureException
   */
  public Node generateBefore(Element nextSibling) throws SignatureException;


  /**
   *  Generates the XMLSignature and replaces the dummy element
   *  Note that this XMLSigner object should have been
   *  properly configured (using the "set"-methods).
   *
   * @param   parent  the element to be replaced by the signature element
   * @return the signature element
   * @exception   SignatureException
   */
  public Node generateHere(Element dummy) throws SignatureException;

}

