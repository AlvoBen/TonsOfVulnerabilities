package com.sap.engine.lib.xml.signature.verifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomSignature;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.Reference;
import com.sap.engine.lib.xml.signature.elements.XMLSignature;
import com.sap.engine.lib.xml.signature.generator.SignatureGenerator;
import com.sap.engine.lib.xml.signature.transform.TransformationFactory;
import com.sap.engine.lib.xml.signature.transform.algorithms.Canonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.ExclusiveCanonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.XPathTransformation;
import com.sap.engine.lib.xml.util.BASE64Decoder;

public class SignatureVerifier implements XMLVerifier {
  public boolean exclusive = false;
  private static final String[] certTypes = {"X509"};

  private Certificate cert = null;
  private Key publicKey = null;
  private XMLSignature signature = null;
  private Reference[] references = new Reference[0];
  private TransformationFactory trFact = TransformationFactory.newInstance();
  // customization of the ID used to dereference same document URI's
  private String idNamespaceURI = null;
  private String idLocalName = "Id";
  private int statusCode = Constants.VERIFIER_INITIAL_STATE;

  public SignatureVerifier(Document doc) throws SignatureException {
    NodeList nl = doc.getElementsByTagNameNS(Constants.SIGNATURE_SPEC_NS, "Signature");
    this.signature = new XMLSignature((Element) nl.item(0), true);
    this.signature.initializeDescendants();
  }

  public SignatureVerifier(Element domSignature) throws SignatureException {
    this.signature = new XMLSignature(domSignature, true);
    this.signature.initializeDescendants();
  }

  public void setPublicKey(PublicKey publicKey) {
    SignatureException.traceKey("Public key for verification", publicKey);

    this.publicKey = publicKey;
  }

  public void setCertificate(Certificate cert) {
    SignatureException.traceCertificate("Certificate for verification", cert);

    this.cert = cert;
  }

  public void setIDAttribute(String localName, String uri) {
    idLocalName = localName;
    idNamespaceURI = uri;
  }

  public void setTransformationFactory(TransformationFactory trFact) {
    this.trFact = trFact;
  }

  public int getSignatureValidationResult() throws IllegalStateException {
    if (statusCode == Constants.VERIFIER_INITIAL_STATE) {
      throw new IllegalStateException("Must invoke verify() before getSignatureValidationResult().");
    }

    return statusCode;
  }

  public Reference[] getReferenceValidationResults() throws IllegalStateException {
    if (statusCode == Constants.VERIFIER_INITIAL_STATE) {
      throw new IllegalStateException("Must invoke verify() before getReferenceValidationResult().");
    }

    return references;
  }

  public void setAttribute(String name, Object value) throws SignatureException {
    throw new SignatureException("Incorrect attribute name or value:" + name, new Object[]{name, value});
  }

  public Object getAttribute(String name) {
    return null;
  }

  public boolean verify() throws SignatureException {
    boolean res = true;
    if (!validateSignatureValue()) {
      statusCode = Constants.INVALID_SIGNATURE_VALUE;
      SignatureException.traceByte("SignatureIncorrect", "");
      res = false;
      if (!SignatureException.dumpFlag) {
        return res;
      }
    }

    if (!verifyReferences()) {
      statusCode = Constants.CONTAINS_INVALID_REFERENCE;
      SignatureException.traceByte("ReferenceIncorrect", "");
      res = false;
    }
    if (!res) {
      return false;
    }
    statusCode = Constants.VERIFY_OK;
    return true;
  }

  public boolean verifyReferences() throws SignatureException {
    boolean result = true;
    GenericElement signedInfo = signature.getDescendant(Constants.SIGNATURE_SPEC_NS, "SignedInfo");
    Vector vectRef = signedInfo.getDirectChildren(Constants.SIGNATURE_SPEC_NS, "Reference");
    references = new Reference[vectRef.size()];
    int i =0;
    for (; i < vectRef.size(); i++) {
      Reference ref = (Reference) vectRef.get(i);
      references[i] = ref;
      ref.setTransformationFactory(trFact);
      ref.setIDAttribute(idLocalName, idNamespaceURI);
      try {
        if (!ref.validate()) {
          this.statusCode = Constants.CONTAINS_INVALID_REFERENCE;
          if (!SignatureException.dumpFlag){
            // if debug is not active - do not check all references after an error
            result = false;
            break;
          }

        }
      } catch (SignatureException e) {
        new SignatureException(e);
        this.statusCode = Constants.CONTAINS_INVALID_REFERENCE;
        if (!SignatureException.dumpFlag){
          // if debug is not active - do not check all references after an error
          result = false;
          break;
        }
      }
    }
//    for (; i < vectRef.size(); i++) {
//      Reference ref = (Reference) vectRef.get(i);
//      references[i] = ref;
//      ref.setTransformationFactory(trFact);
//      ref.setIDAttribute(idLocalName, idNamespaceURI);
//      // after an error occurs no more references are validated
//    }

    return result;
  }

  public boolean validateSignatureValue() throws SignatureException {
    Reusable reusable = null;
    try {
      GenericElement sInfo = signature.getDirectChild(Constants.SIGNATURE_SPEC_NS, "SignedInfo");
      GenericElement canonAlg = sInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, "CanonicalizationMethod");
      String canonAlgorithm = canonAlg.getAttribute("Algorithm", null, null);
      String sigAlgorithm = sInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, "SignatureMethod").getAttribute("Algorithm", null, null);
      try {
        if (publicKey == null){
            //TODO change interface
        	publicKey = getPublicKey();
        }
      } catch (SignatureException e) {
        statusCode = Constants.NO_KEY_INFO;
        throw e;
      } catch (java.security.GeneralSecurityException e) {
        statusCode = Constants.NO_KEY_INFO;
        throw new SignatureException("No valid key info", new java.lang.Object[]{signature}, e);
      }

      boolean[] commExcl;
      try {
        commExcl = SignatureGenerator.detCommExcl(canonAlgorithm);
      } catch (SignatureException e) {
        statusCode = Constants.UNKNOWN_CANONICALIZATION_ALGORITHM;
        throw e;
      }
      boolean retainComments = commExcl[0];
      boolean localExclusive = commExcl[1];
      
//      Document owner = sInfo.getOwner();
//      Node dummyNode = owner.createElementNS(Constants.SIGNATURE_SPEC_NS, "ds:dummy");
//      signature.getDomRepresentation().appendChild(dummyNode);
//
//      String locator = "ancestor-or-self::ds:SignedInfo and count(ancestor::*/child::node()[name()='ds:dummy']) > 0";
//      byte[] canonInfo = Canonicalization.canonicalize(owner, true); // reduce!
//      InputStream s = new ByteArrayInputStream(canonInfo);
//
//      if (!localExclusive) {
//        canonInfo = XPathTransformation.filterSet(s, locator, SignatureGenerator.nsMappings, retainComments);
//      } else {
//        GenericElement el = canonAlg.getDirectChild(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "InclusiveNamespaces");
//        String[] iN = null;
//        if (el != null) {
//          String prefixList = el.getAttribute("PrefixList", null, null);
//          StringTokenizer tokenizer = new StringTokenizer(prefixList);
//          iN = new String[tokenizer.countTokens()];
//          for (int i = 0; i < iN.length; i++) {
//            iN[i] = tokenizer.nextToken();
//      }
//        }
//        canonInfo = XPathTransformation.filterSetSpecial(s, locator, SignatureGenerator.nsMappings, true, iN, retainComments);
//      }
//      signature.getDomRepresentation().removeChild(dummyNode);
      byte[] canonInfo;
      if (localExclusive) {
        //much more faster with exclusive - no xpath
        GenericElement el = canonAlg.getDirectChild(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "InclusiveNamespaces");
        String[] iN = null;
        if (el != null) {
          String prefixList = el.getAttribute("PrefixList", null, null);
          StringTokenizer tokenizer = new StringTokenizer(prefixList);
          iN = new String[tokenizer.countTokens()];
          for (int i = 0; i < iN.length; i++) {
            iN[i] = tokenizer.nextToken();
          }
        }
        canonInfo = ExclusiveCanonicalization.canonicalize(sInfo.getDomRepresentation(), retainComments, iN);
      } else {
        // problem with normal canonicalization - needs namespaces from parents...
        Document owner = sInfo.getOwner();
        Node dummyNode = owner.createElementNS(Constants.SIGNATURE_SPEC_NS, "ds:dummy");
        signature.getDomRepresentation().appendChild(dummyNode);
        String locator = "ancestor-or-self::ds:SignedInfo and count(ancestor::*/child::node()[name()='ds:dummy']) > 0";
        canonInfo = Canonicalization.canonicalize(owner, true); // reduce!
        InputStream s = new ByteArrayInputStream(canonInfo);
        canonInfo = XPathTransformation.filterSet(s, locator, SignatureGenerator.nsMappings, retainComments);
        signature.getDomRepresentation().removeChild(dummyNode);
      }
      
//      {
//        byte[] canonInfo1 = Canonicalization.canonicalize(sInfo.getDomRepresentation(), retainComments);
//        //TODO: see why it fails!!!
//        if (localExclusive) {
//          GenericElement el = canonAlg.getDirectChild(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "InclusiveNamespaces");
//          String[] iN = null;
//          if (el != null) {
//            String prefixList = el.getAttribute("PrefixList", null, null);
//            StringTokenizer tokenizer = new StringTokenizer(prefixList);
//            iN = new String[tokenizer.countTokens()];
//            for (int i = 0; i < iN.length; i++) {
//              iN[i] = tokenizer.nextToken();
//            }
//          }
//          canonInfo1 = ExclusiveCanonicalization.canonicalize(canonInfo1, retainComments, iN);
//        }
//        if (!Arrays.equals(canonInfo, canonInfo1)){
//          LogWriter.getSystemLogWriter().println("Canonicalized signature for verification(alternative):\n"+ new String(canonInfo1));
//          LogWriter.getSystemLogWriter().println("---------------------------------");
//          LogWriter.getSystemLogWriter().println("Canonicalized signature for verification\n" +  new String(canonInfo));
//        }
//      }
      String sValue = signature.getSignatureValue();
      SignatureException.traceByteAsString("Canonicalized signature for verification", canonInfo);
      reusable = Reusable.getInstance(sigAlgorithm);
      CustomSignature sig = (CustomSignature) reusable;
      byte[] toCheck = BASE64Decoder.decode(sValue.getBytes()); //$JL-I18N$
      if (sigAlgorithm.equals(Constants.SIGN_DSA)) {
        toCheck = xmlSignatureToAsn1(toCheck);
      }
      sig.initVerify(publicKey);
      sig.update(canonInfo);

      return sig.verify(toCheck);
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Unable to validate signature", new java.lang.Object[]{signature}, e);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }
  }

  public String getKeyName() throws SignatureException {
    GenericElement keyInfo = signature.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyInfo");
    GenericElement keyName = keyInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyName");
    return (keyName == null) ? null : keyName.getNodeValue();
  }

  public Certificate getCertificate() throws SignatureException, java.security.GeneralSecurityException {
    if (cert != null) {
      return cert;
    }

    String encCert = null;
    String certType = null;
    GenericElement keyInfo = signature.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyInfo");

    for (int i = 0; i < certTypes.length; i++) {
      GenericElement certData = keyInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, certTypes[i] + "Data");

      if (certData != null) {
        certType = certTypes[i];
        GenericElement certCertificate = certData.getDirectChild(Constants.SIGNATURE_SPEC_NS, certTypes[i] + "Certificate");
        encCert = certCertificate.getNodeValue();
        break;
      }
    }

    if (encCert == null) {
      return null;
    }

    byte[] decCert = BASE64Decoder.decode(encCert.getBytes()); //$JL-I18N$
    CertificateFactory cFact = CertificateFactory.getInstance(certType);
    return cFact.generateCertificate(new ByteArrayInputStream(decCert));
  }

  public Certificate[] getCertificates() throws SignatureException, java.security.GeneralSecurityException {
    Vector certs = new Vector(10);
    Vector types = new Vector(10);
    GenericElement keyInfo = signature.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyInfo");

    for (int i = 0; i < certTypes.length; i++) {
      GenericElement certData = keyInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, certTypes[i] + "Data");

      if (certData != null) {
        Vector children = certData.getDirectChildren(Constants.SIGNATURE_SPEC_NS, certTypes[i] + "Certificate");
        for (int j = 0; j < children.size(); j++) {
          certs.add(((GenericElement) children.get(j)).getNodeValue());
          types.add(certTypes[i]);
        }
      }
    }

    Certificate[] res = new Certificate[certs.size()];

    for (int i = 0; i < res.length; i++) {
      byte[] decCert = BASE64Decoder.decode(((String) certs.get(i)).getBytes()); //$JL-I18N$
      CertificateFactory cFact = CertificateFactory.getInstance((String) types.get(i));
      res[i] = cFact.generateCertificate(new ByteArrayInputStream(decCert));
    }

    return res;

  }

  public PublicKey getPublicKey() throws SignatureException, java.security.GeneralSecurityException {
    if (publicKey != null) {
      return (PublicKey) publicKey;
    }

    GenericElement keyInfo = signature.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyInfo");
    GenericElement keyValue = null;

    if (keyInfo != null) {
      keyValue = keyInfo.getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyValue");
    }

    if (keyValue == null) {
      Certificate localCert = getCertificate();

      if (localCert != null) {
        return localCert.getPublicKey();
      } else {
        throw new SignatureException("Can't constuct a public key without keyValue element and no certificate info.", new java.lang.Object[]{signature});
      }
    }

    GenericElement specValue = keyValue.getFirstChild();
    String specLocal = specValue.getLocalName();
    String specURI = specValue.getNamespaceURI();

    if (!specURI.equals(Constants.SIGNATURE_SPEC_NS)) {
      throw new SignatureException("Can't create key - direct child of Key Value in wrong namespace: " + specURI + " and should be in "
          + Constants.SIGNATURE_SPEC_NS, new java.lang.Object[]{signature});
    }

    if (specLocal.equals("DSAKeyValue")) {
      String pString = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "P").getNodeValue();
      String qString = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "Q").getNodeValue();
      String gString = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "G").getNodeValue();
      String yString = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "Y").getNodeValue();
      BigInteger p = new BigInteger(1, BASE64Decoder.decode(pString.getBytes())); //$JL-I18N$
      BigInteger q = new BigInteger(1, BASE64Decoder.decode(qString.getBytes())); //$JL-I18N$
      BigInteger g = new BigInteger(1, BASE64Decoder.decode(gString.getBytes())); //$JL-I18N$
      BigInteger y = new BigInteger(1, BASE64Decoder.decode(yString.getBytes())); //$JL-I18N$
      DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
      KeyFactory fact = KeyFactory.getInstance("DSA", Configurator.getProviderName());
      return fact.generatePublic(spec);
    } else if (specLocal.equals("RSAKeyValue")) {
      String _modulus = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "Modulus").getNodeValue();
      String _exponent = specValue.getDirectChildIgnoreCase(Constants.SIGNATURE_SPEC_NS, "Exponent").getNodeValue();
      BigInteger modulus = new BigInteger(1, BASE64Decoder.decode(_modulus.getBytes())); //$JL-I18N$
      BigInteger exponent = new BigInteger(1, BASE64Decoder.decode(_exponent.getBytes())); //$JL-I18N$
      RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
      KeyFactory fact = KeyFactory.getInstance("RSA", Configurator.getProviderName());
      return fact.generatePublic(spec);
    } else {
      throw new SignatureException("Unknown signauture algorithm:" + specLocal, new java.lang.Object[]{signature});
    }
  }

  private static byte[] xmlSignatureToAsn1(byte signatureOctets[]) throws SignatureException {
    if (signatureOctets.length != 40) {
      throw new SignatureException("Invalid format for DSA. ", new java.lang.Object[]{signatureOctets});
    }

    int i;
    for (i = 20; (i > 0) && (signatureOctets[20 - i] == 0); i--) {
      ;
    }
    int j = i;

    if (signatureOctets[20 - i] < 0) {
      j += 1;
    }

    int k;
    for (k = 20; (k > 0) && (signatureOctets[40 - k] == 0); k--) {
      ;
    }
    int l = k;

    if (signatureOctets[40 - k] < 0) {
      l += 1;
    }

    byte asn1Octets[] = new byte[6 + j + l];
    asn1Octets[0] = 48;
    asn1Octets[1] = (byte) (4 + j + l);
    asn1Octets[2] = 2;
    asn1Octets[3] = (byte) j;
    System.arraycopy(signatureOctets, 20 - i, asn1Octets, (4 + j) - i, i);
    asn1Octets[4 + j] = 2;
    asn1Octets[5 + j] = (byte) l;
    System.arraycopy(signatureOctets, 40 - k, asn1Octets, (6 + j + l) - k, k);
    return asn1Octets;
  }

  public Key getSharedKey() {
    return publicKey;
  }
  
  public void setSharedKey(Key key) {
    publicKey = key;
  }

}
