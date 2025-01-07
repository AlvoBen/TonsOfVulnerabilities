
package com.sap.engine.lib.xml.signature.generator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomSignature;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.KeyInfo;
import com.sap.engine.lib.xml.signature.elements.Object;
import com.sap.engine.lib.xml.signature.elements.Reference;
import com.sap.engine.lib.xml.signature.elements.SignedInfo;
import com.sap.engine.lib.xml.signature.elements.XMLSignature;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.signature.transform.TransformationFactory;
import com.sap.engine.lib.xml.signature.transform.algorithms.Canonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.ExclusiveCanonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.XPathTransformation;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class SignatureGenerator implements XMLSigner {

  private boolean showKeyValue = true;

  private String keyName = null;

  private Certificate[] certs = null;

  private Key publicKey = null;

  private Key privateKey = null;

  private String cAlgorithmURI = Constants.TR_C14N_OMIT_COMMENTS;

  private String signatureAlgorithmURI = Constants.SIGN_DSA;

  // customization of the ID used to dereference same document URI's
  private String idNamespaceURI = null;

  private String idLocalName = "Id";

  public boolean skipConsistencyCheck = false;

  public boolean showKeyInfo = true;

  public static String defaultSignatureAlgorithm = Constants.SIGN_DSA;

  private Node parent = null;

  private Vector references = new Vector();

  private Vector objects = new Vector(); // contains the contents of the future
                                          // Object elements

  private Vector objectIDs = new Vector();

  private OutputStream out;

  private String digestAlgorithmURI;

  protected XMLSignature signature = null;

  protected SignedInfo sInfo = null;

  protected GenericElement sValue;

  protected boolean generateNewLine = true;

  protected boolean skipPrefixList = true;

  private boolean generateSignature = true;

  public void addReference(String uri, Transformation[] transforms, String digestMethod) throws SignatureException {
    addReference(null, uri, transforms, digestMethod, null, null);
  }

  public void addReference(String uri, Transformation[] transforms, String digestMethod, String proxyHost, String proxyPort) throws SignatureException {
    addReference(null, uri, transforms, digestMethod, proxyHost, proxyPort);
  }

  public void addReference(String canonicalizationURI, String uri, Transformation[] transforms, String digestMethod) throws SignatureException {
    addReference(canonicalizationURI, uri, transforms, digestMethod, null, null);
  }

  public void addReference(String canonicalizationURI, String uri, Transformation[] transforms, String digestMethod, String proxyHost, String proxyPort) throws SignatureException {
    // Florian remark - null same as ""
    if (uri == null) {
      uri = "";
    }
    if (!skipConsistencyCheck) {
      checkConsistency(canonicalizationURI, uri, transforms, digestMethod);
    }

    Reference ref = new Reference(); // now all the Reference elements are
                                      // orphans!!
    ref.setURI(uri);
    ref.setTransforms(transforms);
    ref.setDigestURI(digestMethod);
    ref.setProxy(proxyHost, proxyPort);
    references.add(ref);
  }

  public void addObject(Node object) {
    objects.add(object);
    objectIDs.add(null);
  }

  public void addObject(Node object, String id) {
    objects.add(object);
    objectIDs.add(id);
  }

  public void setIDAttribute(String localName, String uri) {
    idLocalName = localName;
    idNamespaceURI = uri;
  }

  public void setSignatureAlgorithmURI(String signatureAlgorithmURI) {
    this.signatureAlgorithmURI = signatureAlgorithmURI;
  }

  public void setPrivateKey(PrivateKey privateKey) {
    SignatureException.traceKey("Private key for signing", privateKey);

    this.privateKey = privateKey;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public void setPublicKey(PublicKey publicKey) {
    SignatureException.traceKey("Public key for signing", publicKey);

    this.publicKey = publicKey;
  }

  public void setCertificate(Certificate cert) {
    SignatureException.traceCertificate("Setting certificate", cert);

    this.certs = new Certificate[] { cert };
  }

  public void setCertificates(Certificate[] certs) {
    SignatureException.traceCertificateArray("Setting certificate ", certs);

    this.certs = certs;
  }

  public void setOutputStream(OutputStream $out) {
    this.out = $out;
  }

  public void setDigestAlgorithm(String digestAlgorithmURI) {
    this.digestAlgorithmURI = digestAlgorithmURI;
  }

  public void setCanonicalizationAlgorithm(String cAlgorithmURI) {
    this.cAlgorithmURI = cAlgorithmURI;
  }

  public void showKeyValue(boolean $showKeyValue) {
    this.showKeyValue = $showKeyValue;
  }

  public static void signDocument(Element parent) throws SignatureException {
    signDocument(parent, null, null, null);
  }

  public static void signDocument(Element parent, PrivateKey privateKey, Certificate cert) throws SignatureException {
    signDocument(parent, privateKey, null, cert);
  }

  public static void signDocument(Element parent, PrivateKey privateKey, PublicKey publicKey) throws SignatureException {
    signDocument(parent, privateKey, publicKey, null);
  }

  public static void signDocument(Element parent, PrivateKey privateKey, PublicKey publicKey, Certificate cert) throws SignatureException {
    if (publicKey == null && privateKey == null) {
      KeyPairGenerator gen;
      KeyPair pair;
      try {
        if (defaultSignatureAlgorithm.equals(Constants.SIGN_DSA)) {
          gen = KeyPairGenerator.getInstance("DSA", Configurator.getProviderName());
        } else if (defaultSignatureAlgorithm.equals(Constants.SIGN_DSA)) {
          gen = KeyPairGenerator.getInstance("RSA", Configurator.getProviderName());
        } else {
          throw new SignatureException("Unrecognized default signature algorithm: " + defaultSignatureAlgorithm, new java.lang.Object[] { parent, privateKey, publicKey, cert });
        }
      } catch (SignatureException e) {
        throw e;
      } catch (Exception e) {
        throw new SignatureException("Error signing document", new java.lang.Object[] { parent, privateKey, publicKey, cert }, e);
      }
      pair = gen.genKeyPair();
      privateKey = pair.getPrivate();
      publicKey = pair.getPublic();
    }

    SignatureGenerator generator = new SignatureGenerator();
    Transformation tr1 = TransformationFactory.newInstance().getInstance(Constants.TR_ENVELOPED_SIGNATURE, new Object[] {(Object) parent});

    generator.setPrivateKey(privateKey);
    generator.setCertificate(cert);
    generator.setPublicKey(publicKey);
    generator.addReference(Constants.TR_C14N_OMIT_COMMENTS, "", new Transformation[] { tr1 }, Constants.DIGEST_SHA1);
    generator.generate(parent);
  }

  public Node generateHere(Element dummy) throws SignatureException {
    Node localParent = dummy.getParentNode();

    if (localParent == null) {
      throw new SignatureException("Parent element is null!", new java.lang.Object[] { dummy });
    }

    this.parent = localParent;
    signature = new XMLSignature(localParent, dummy, true);
    generate(signature);
    return signature.getDomRepresentation();
  }

  public Node generate(Element $parent) throws SignatureException {
    if ($parent == null) {
      throw new SignatureException("Parent element is null!", new java.lang.Object[] { $parent });
    }
    this.parent = $parent;
    signature = new XMLSignature($parent);
    generate(signature);
    return signature.getDomRepresentation();
  }

  public Node generateBefore(Element sibling) throws SignatureException {
    Node localParent = sibling.getParentNode();

    if (localParent == null) {
      throw new SignatureException("Parent element is null!", new java.lang.Object[] { sibling });
    }

    this.parent = localParent;
    signature = new XMLSignature(localParent, sibling, false);
    generate(signature);
    return signature.getDomRepresentation();
  }

  private void generate(XMLSignature sig) throws SignatureException {
    try {
      appendObjects(sig);
      sInfo = new SignedInfo(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "SignedInfo", sig);
      sValue = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "SignatureValue", sig);
      GenericElement canonURI = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "CanonicalizationMethod", sInfo);
      GenericElement algURI = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "SignatureMethod", sInfo);
      canonURI.setAttribute("Algorithm", cAlgorithmURI);
      if (!/* ExclusiveCanonicalization. */skipPrefixList && cAlgorithmURI.startsWith(Constants.TR_C14N_EXCL_OMIT_COMMENTS)) {
        GenericElement inclusiveNamespaces = new GenericElement(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "c14:InclusiveNamespaces", canonURI);
        inclusiveNamespaces.setAttribute("PrefixList", "c14");
      }
      algURI.setAttribute("Algorithm", signatureAlgorithmURI);

      for (int i = 0; i < references.size(); i++) {
        Reference ref = (Reference) references.get(i);
        ref.setEnvelopingElement(parent);
        ref.setIDAttribute(idLocalName, idNamespaceURI);
        ref.setSkipPrefixList(skipPrefixList);
        ref.digest(sInfo);
      }
      if (generateSignature) {
        sValue.appendTextChild(new String(sign(sInfo, privateKey))); //$JL-I18N$
      }

      if (showKeyInfo) {
        KeyInfo kInfo = new KeyInfo(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyInfo", sig);
        kInfo.setPrivateKey(privateKey);
        kInfo.setPublicKey((PublicKey) publicKey);

        if (keyName != null) {
          kInfo.setKeyName(keyName);
          kInfo.addKeyName();
        }

        if (certs != null) {
          kInfo.setCertificates(certs);
          kInfo.addCertificateInfo();
        }

        if (showKeyValue) {
          if (publicKey != null || (certs != null && certs.length > 0)) {
            kInfo.addKeyValue(privateKey.getAlgorithm());
          }
        }
      }

    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Generating signature exception", new java.lang.Object[] { sig }, e);
    }
  }

  public static Hashtable nsMappings = new Hashtable();
  static {
    nsMappings.put("ds", Constants.SIGNATURE_SPEC_NS);
  }

  private byte[] sign(SignedInfo sInfo, Key $privateKey) throws NoSuchAlgorithmException, SignatureException {

    // String newSignatureLocator = "ancestor-or-self::ds:SignedInfo and
    // count(ancestor::*/child::node()[name()='ds:SignatureValue'][string()!=''])
    // = 0";

    boolean[] commExcl = detCommExcl(cAlgorithmURI);
    boolean retainComments = commExcl[0];
    boolean exclusive = commExcl[1];

    // InputStream is = new
    // ByteArrayInputStream(Canonicalization.canonicalize(sInfo.getOwner(), true
    // ));
    // byte[] canonInfo;
    //    
    // if (!exclusive) {
    //
    // canonInfo = XPathTransformation.filterSet(is, newSignatureLocator,
    // nsMappings, retainComments);
    // } else {
    //
    // canonInfo = XPathTransformation.filterSetSpecial(is, newSignatureLocator,
    // nsMappings, true, new String[] {}, retainComments);
    // }
    // InputStream is = new ByteArrayInputStream();
    byte[] canonInfo;

    if (exclusive) {
      canonInfo = ExclusiveCanonicalization.canonicalize(sInfo.getDomRepresentation(), retainComments, null);
    } else {
      String newSignatureLocator = "ancestor-or-self::ds:SignedInfo and count(ancestor::*/child::node()[name()='ds:SignatureValue'][string()!='']) = 0";
      InputStream is = new ByteArrayInputStream(Canonicalization.canonicalize(sInfo.getOwner(), true));
      canonInfo = XPathTransformation.filterSet(is, newSignatureLocator, nsMappings, retainComments);
      // canonInfo = Canonicalization.canonicalize(sInfo.getDomRepresentation(),
      // retainComments );
    }

    SignatureException.traceByteAsString("Canonicalized signature", canonInfo);
    Reusable reusable = null;
    byte[] xmlSpecific;
    try {
      reusable = Reusable.getInstance(signatureAlgorithmURI);
      CustomSignature sig = (CustomSignature) reusable;// SignatureContext.getCryptographicPool().getSignatureFromPool(signatureAlgorithmURI);
      sig.initSign($privateKey);
      sig.update(canonInfo);
      if (signatureAlgorithmURI.equals(Constants.SIGN_DSA)) {
        xmlSpecific = Asn1toXMLsignture(sig.sign());
      } else {
        xmlSpecific = sig.sign();
      }
      SignatureException.traceByte("Signature result", xmlSpecific);
      return generateNewLine ? BASE64Encoder.encode(xmlSpecific) : BASE64Encoder.encodeN(xmlSpecific);
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error while signing", new java.lang.Object[] { sInfo, $privateKey }, e);
    } finally {
      reusable.release();
    }
  }

  private void appendObjects(XMLSignature sig) throws SignatureException {
    for (int i = 0; i < objects.size(); i++) {
      Object o = new Object(sig);
      o.getDomRepresentation().appendChild(((Node) objects.get(i)).cloneNode(true));
      String id = (String) objectIDs.get(i);

      if (id != null) {
        if (idNamespaceURI == null) {
          ((Element) o.getDomRepresentation()).setAttribute("Id", (String) objectIDs.get(i));
        } else {
          Hashtable nsMappingsLocal = DOM.getNamespaceMappingsInScopeSpecial((Node) o);
          Enumeration keys = nsMappingsLocal.keys();

          while (keys.hasMoreElements()) {
            String nextPrefix = (String) keys.nextElement();
            String nextUri = (String) nsMappingsLocal.get(nextPrefix);

            if (nextUri.equals(idNamespaceURI)) {
              ((Element) o.getDomRepresentation()).setAttributeNS(nextUri, nextPrefix + ":" + nextUri, (String) objectIDs.get(i));
            }
          }
        }
      }
    }
  }

  public static boolean[] detCommExcl(String canonURI) throws SignatureException {
    if (Constants.TR_C14N_EXCL_OMIT_COMMENTS.equals(canonURI)){
      return new boolean[]{false, true};
    } else if (Constants.TR_C14N_EXCL_WITH_COMMENTS.equals(canonURI)){
      return new boolean[]{true, true};
    } else if (Constants.TR_C14N_WITH_COMMENTS.equals(canonURI)){
      return new boolean[]{true, false};
    } else if (Constants.TR_C14N_OMIT_COMMENTS.equals(canonURI)){
      return new boolean[]{false, false};
    }
//      
//    boolean[] commExcl = new boolean[2];
//    
//    int sharpIndex = canonURI.indexOf('#');
//    String url;
//
//    if (sharpIndex >= 0) {
//      commExcl[0] = canonURI.substring(sharpIndex).equals("WithComments");
//      url = canonURI.substring(0, sharpIndex);
//    } else {
//      commExcl[0] = false;
//      url = canonURI;
//    }
//
//    if (url.equals(Constants.TR_C14N_EXCL_OMIT_COMMENTS)) {
//      commExcl[1] = true;
//      return commExcl;
//    }
//
//    if (url.equals(Constants.TR_C14N_OMIT_COMMENTS)) {
//      commExcl[1] = false;
//      return commExcl;
//    }
//
//    if (canonURI.equals(Constants.TR_C14N_EXCL)) {
//      commExcl[0] = true;
//      commExcl[1] = true;
//      return commExcl;
//    }
//
//    if (canonURI.equals(Constants.TR_C14N_EXCL_OMIT_COMMENTS)) {
//      commExcl[0] = false;
//      commExcl[1] = true;
//      return commExcl;
//    }

    throw new SignatureException("Unrecognized canonicalization method uri : " + canonURI, new java.lang.Object[] { canonURI });
  }

  private void checkConsistency(String canonicalizationURI, String uri, Transformation[] transforms, String digestMethod) throws SignatureException {
    if (!"".equals(uri) && !uri.startsWith("#")) {
      for (int i = 0; i < transforms.length; i++) {
        if (transforms[i].uri.equals(Constants.TR_ENVELOPED_SIGNATURE)) {
          throw new SignatureException("Enveloped signatures can be done only on same-document references.", new java.lang.Object[] { canonicalizationURI, uri, transforms, digestMethod });
        }
      }
    }
    if ("".equals(uri) && transforms.length < 1) {
      throw new SignatureException("An empty uri (same document) reference without a transformation would surely be invalid!", new java.lang.Object[] { canonicalizationURI, uri, transforms,
          digestMethod });
    }

    return;
  }

  private static byte[] Asn1toXMLsignture(byte asn1Octets[]) throws SignatureException {
    byte rLength = asn1Octets[3];
    int i;
    for (i = rLength; (i > 0) && (asn1Octets[(4 + rLength) - i] == 0); i--) {
      ;
    }
    byte sLength = asn1Octets[5 + rLength];
    int j;
    for (j = sLength; (j > 0) && (asn1Octets[(6 + rLength + sLength) - j] == 0); j--) {
      ;
    }

    if ((asn1Octets[0] != 48) || (asn1Octets[1] != asn1Octets.length - 2) || (asn1Octets[2] != 2) || (i > 20) || (asn1Octets[4 + rLength] != 2) || (j > 20)) {
      throw new SignatureException("Invalid format for DSA", new java.lang.Object[] { asn1Octets });
    }
    byte signatureOctets[] = new byte[40];
    System.arraycopy(asn1Octets, (4 + rLength) - i, signatureOctets, 20 - i, i);
    System.arraycopy(asn1Octets, (6 + rLength + sLength) - j, signatureOctets, 40 - j, j);
    return signatureOctets;
  }

  public void setSharedKey(Key sharedKey) {
    publicKey = sharedKey;
    privateKey = sharedKey;
  }

  /**
   * @return Returns the skipPrefixList.
   */
  public boolean isPrefixListAdded() {
    return !skipPrefixList;
  }

  /**
   * @param skipPrefixList The skipPrefixList to set.
   */
  public void addPrefixList(boolean addPrefixList) {
    this.skipPrefixList = !addPrefixList;
  }

  public Node sign() throws NoSuchAlgorithmException, SignatureException {
    if (generateSignature)
      throw new SignatureException("Document already signed");
    sValue.appendTextChild(new String(sign(sInfo, privateKey))); //$JL-I18N$
    return signature.getDomRepresentation();
  }

  public boolean isGenerateSignature() {
    return generateSignature;
  }

  public void setGenerateSignature(boolean generateSignature) {
    this.generateSignature = generateSignature;
  }

  public XMLSignature getSignature() {
    return signature;
  }

  public boolean isGenerateNewLine() {
    return generateNewLine;
  }

  public void setGenerateNewLine(boolean generateNewLine) {
    this.generateNewLine = generateNewLine;
  }
}
