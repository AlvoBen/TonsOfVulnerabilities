package com.sap.engine.lib.xml.signature.elements;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.*;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.signature.transform.TransformationFactory;
import com.sap.engine.lib.xml.signature.transform.algorithms.Canonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.ExclusiveCanonicalization;
import com.sap.engine.lib.xml.signature.transform.algorithms.XPathTransformation;
import com.sap.engine.lib.xml.util.BASE64Decoder;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class Reference extends GenericElement {

  public int state = Constants.REFERENCE_INITIAL_STATE;
  private String digestAlgorithmURI = null;
  private String digestValue = null;
  private String canonicalizationURI = null;
  protected TransformationFactory trFact = TransformationFactory.newInstance();
  protected Transformation[] transforms = new Transformation[0];
  private Node envelopingElement = null;
  protected String proxyHost = null;
  protected String proxyPort = null;
  protected String uri = null;
  // customization of the ID used to dereference same document URI's 
  private String idNamespaceURI = null;
  private String idLocalName = "Id";
  
  protected boolean skipPrefixList = true;
  public byte[] transformedData = null;

  public String toString(){
    StringBuffer b = new StringBuffer();
    for (int i=0;i<transforms.length;i++){
      b.append(transforms[i]).append(',');
    }
    return b.toString();
  }
  
  public void setProxy(String proxyHost, String proxyPort) {
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
  }

  public void setIDAttribute(String localName, String uri) {
    idNamespaceURI = uri;
    idLocalName = localName;
  }
  
  public void setTransformationFactory(TransformationFactory trFact) {
    this.trFact = trFact;
  }

  public Reference() {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Reference");
  }

  public Reference(String namespaceURI, String qName) {
    super(namespaceURI, qName);
  }

  public Reference(String namespaceUri, String qualifiedName, GenericElement parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

  public Reference(Element domRepr, GenericElement parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(domRepr, parent);
    envelopingElement = parent.getDomRepresentation();
  }

  private void init() throws com.sap.engine.lib.xml.signature.SignatureException {
    initializeDescendants();
    uri = getAttribute("URI", null, null);
    digestAlgorithmURI = getDirectChild(Constants.SIGNATURE_SPEC_NS, "DigestMethod").getAttribute("Algorithm", null, null);
    digestValue = getDirectChild(Constants.SIGNATURE_SPEC_NS, "DigestValue").getNodeValue();
    GenericElement transformsElement = getDirectChild(Constants.SIGNATURE_SPEC_NS, "Transforms");
    int xpathIndex = -1;
    try {
      if (transformsElement != null) {
        Vector v = transformsElement.getDirectChildren(Constants.SIGNATURE_SPEC_NS, "Transform");
        transforms = new Transformation[v.size()];

        for (int i = 0; i < v.size(); i++) {
          transforms[i] = trFact.getInstance((GenericElement) v.get(i));
          if (transforms[i] instanceof ExclusiveCanonicalization){
            if (xpathIndex!=-1){
              ((XPathTransformation) transforms[xpathIndex]).setExclusive(true);
            }
          } else if (transforms[i] instanceof XPathTransformation){
            xpathIndex = i;
          }
        } 
      }

    } catch (com.sap.engine.lib.xml.signature.SignatureException e) {
      this.state = Constants.UNKNOWN_TRANSFORMATION_ALGORITHM;
      throw e;
    }
  }

  public void setEnvelopingElement(Node envelopingElement) {
    this.envelopingElement = envelopingElement;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }

  public void setDigestURI(String digestAlgorithmURI) {
    this.digestAlgorithmURI = digestAlgorithmURI;
  }

  public String getDigestAlgorithmURI() {
    return digestAlgorithmURI;
  }

  public String getCanonicalizationURI() {
    return canonicalizationURI;
  }

  public String getURI() {
    return uri;
  }

  public Transformation[] getTransformations() {
    return transforms;
  }

  public void setTransforms(Transformation[] transforms) {
    this.transforms = transforms;
  }

  private void updateNamespaces() {
    Hashtable table = null;
    for (int i = 0; i < transforms.length; i++) {
      if (transforms[i] instanceof XPathTransformation) {
        //TODO: if canonicalizing external reference what to do with mappings?
        if (table == null){
            table = DOM.getNamespaceMappingsInScopeSpecial(this.domRepresentation);
        }
        ((XPathTransformation) transforms[i]).setPrefixMappings(table);
      }
    } 
  }

  protected Element getElementByID(String idValue) {
    Document doc = envelopingElement.getOwnerDocument();
    Element el = DOM.getElementByAttribute(doc, idLocalName, idNamespaceURI, idValue);
    String defaultNS = DOM.prefixToURI("xmlns", el);
    Hashtable parentNamespaces = DOM.getNamespaceMappingsInScopeSpecial(el);
    Enumeration keys = parentNamespaces.keys();

    if (defaultNS == null) {
      defaultNS = "http://www.w3.org/2000/xmlns/";
    }

    while (keys.hasMoreElements()) {
      String nextPrefix = (String) keys.nextElement();
      String nextURI = (String) parentNamespaces.get(nextPrefix);
      el.setAttributeNS(defaultNS, ((nextPrefix==null)||(nextPrefix.length()==0))?"xmlns":("xmlns:" + nextPrefix), nextURI);
    }

    return el;
  }


  protected byte[] dereference(String $uri) throws com.sap.engine.lib.xml.signature.SignatureException {
    if ($uri == null || $uri.equals("") || $uri.equals("#xpointer(/)")) {
      return Canonicalization.canonicalize(envelopingElement.getOwnerDocument(), true);
    } else if ($uri.startsWith("#")) {
      String idReference = null;
      // if this is xpointer
      
      if ($uri.startsWith("#xpointer(")){
        idReference = $uri.substring(14,$uri.length()-3);
      } else {
        idReference = $uri.substring(1);
      }
      Element el = getElementByID(idReference);
      if (el == null) {
        return new byte[0] ;
      }
  
     return Canonicalization.canonicalize(el, true);

    } else {
      try {
        
        if (proxyHost != null && proxyPort != null) {
          SystemProperties.setProperty("http.proxyHost", proxyHost);
          SystemProperties.setProperty("http.proxyPort", proxyPort);
        }
        InputStream in = SignatureContext.getEntityResolver().resolveEntity(null, $uri).getByteStream();
        byte[] result = new byte[0];
        byte[] barr = new byte[5000];
        int read;
        while ((read = in.read(barr)) != -1) {
          byte[] temp1 = new byte[result.length + read];
          System.arraycopy(result, 0, temp1, 0, result.length);
          System.arraycopy(barr, 0, temp1, result.length, read);
          result = temp1;
        }
        return result;
      } catch (Exception e) {
        this.state = Constants.REFERENCE_UNREACHABLE;
        throw new com.sap.engine.lib.xml.signature.SignatureException("Reference unreachable: " + $uri, new java.lang.Object[]{$uri}, e);
      } 
    }
  }

  public boolean validate() throws com.sap.engine.lib.xml.signature.SignatureException {
    SignatureException.traceMessage("Validating reference");
    init();
    updateNamespaces();
    byte[] shouldBe = getDigestValue();
    byte[] is = BASE64Decoder.decode(digestValue.getBytes()); //$JL-I18N$
    boolean result = Arrays.equals(shouldBe, is) ? true : false;
    this.state = result ? Constants.REFERENCE_VERIFY_OK : Constants.INVALID_DIGEST_VALUE;
    return result;
  }
  
  protected byte[] undergoTransformations() throws com.sap.engine.lib.xml.signature.SignatureException {
    Data data = new Data(dereference(uri));

    if (transforms != null) {
      for (int i = 0; i < transforms.length; i++) {
       transforms[i].transform(data);
       if (SignatureContext.debug){
         try  {
           FileOutputStream fos = new FileOutputStream("c14n-"+SignatureContext.count+++".txt");
           fos.write(data.getOctets());
           fos.close();
         } catch (Exception ex){
//         $JL-EXC$
         }
       }
      } 
    }
    return data.getOctets();
  }
  
  protected byte[] getDigestValue() throws com.sap.engine.lib.xml.signature.SignatureException {
    byte[] data = undergoTransformations();
    //TODO: remove!
    transformedData = data;
 
    SignatureException.traceNode("Reference", domRepresentation);
    SignatureException.traceByteAsString("Transformed reference", data);
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(digestAlgorithmURI);
      MessageDigest md = (MessageDigest) reusable.getInternal();//SignatureContext.getCryptographicPool().getMessageDigestFromPool(digestAlgorithmURI);
      if (md == null) {
        this.state = Constants.UNKNOWN_DIGEST_ALGORITHM;
        throw new com.sap.engine.lib.xml.signature.SignatureException("Unrecognized digest algorithm namespace: " + digestAlgorithmURI,  new java.lang.Object[]{digestAlgorithmURI});
      }
      byte[] digest = md.digest(data);
      return digest;
    } catch (com.sap.engine.lib.xml.signature.SignatureException se){
      throw se;
    } catch (Exception e) {
      throw new com.sap.engine.lib.xml.signature.SignatureException("Error getting digest value",new java.lang.Object[]{data}, e);
    } finally {
      if (reusable != null){
        reusable.release();
      }
    }
  }

  protected void construct(GenericElement $parent, String transformsURI, String transformsQName) throws com.sap.engine.lib.xml.signature.SignatureException {
    if ($parent == null) {
      $parent = this.parent;
    }

    initDOM($parent.getOwner());
    setAttribute("URI", uri);
    $parent.appendChild(this);
    updateNamespaces();

    if (transforms.length > 0) {
      GenericElement elTransforms = new GenericElement(transformsURI, transformsQName, this);

      for (int i = 0; i < transforms.length; i++) {
        GenericElement transform = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Transform", elTransforms);
        String algURI = transforms[i].uri;
        transform.setAttribute("Algorithm", algURI);

        /*XPath transformation elements requere some additional care*/
        if (algURI.equals(Constants.TR_XPATH)) {
          GenericElement xpathTrans = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "XPath", transform);
          String query = (String) transforms[i].additionalArgs[0];
          Hashtable prefixMappings = (Hashtable) transforms[i].additionalArgs[1];

          if (prefixMappings != null) {
            Enumeration e = prefixMappings.keys();

            while (e.hasMoreElements()) {
              String next = (String) e.nextElement();
              xpathTrans.setAttribute("xmlns:" + next, (String) prefixMappings.get(next));
            }
          }

          xpathTrans.appendTextChild(query);
        } else if (algURI.startsWith(Constants.TR_C14N_EXCL_OMIT_COMMENTS)){
          if ((transforms[i].additionalArgs!=null)&&(transforms[i].additionalArgs.length>0)&&(transforms[i].additionalArgs[0]!=null)&&(transforms[i].additionalArgs[0] instanceof String[])){
            String[] prefixes = (String[]) transforms[i].additionalArgs[0];
            if (prefixes.length>0){
              StringBuffer prefixList = new StringBuffer(prefixes.length*4);
              for(int j=0;j<prefixes.length-1;j++){
                prefixList.append(prefixes[j]).append(' ');
              }
              prefixList.append(prefixes[prefixes.length-1]);
              GenericElement inclusiveNamespaces = new GenericElement(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "c14:InclusiveNamespaces", transform);
              inclusiveNamespaces.setAttribute("PrefixList",prefixList.toString());
            } else if (!skipPrefixList){
              GenericElement inclusiveNamespaces = new GenericElement(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "c14:InclusiveNamespaces", transform);
              inclusiveNamespaces.setAttribute("PrefixList","c14");    
            }
          } else if (!skipPrefixList){
            GenericElement inclusiveNamespaces = new GenericElement(Constants.TR_C14N_EXCL_OMIT_COMMENTS, "c14:InclusiveNamespaces", transform);
            inclusiveNamespaces.setAttribute("PrefixList","c14");            
          }
        }
      } 
    }
  }

  public void digest(GenericElement $parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    SignatureException.traceMessage("Digesting reference");
    construct($parent, Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Transforms");
    GenericElement dMethod = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "DigestMethod", this);
    GenericElement dValue = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "DigestValue", this);
    byte[] digest = getDigestValue();
    byte[] encodedDigest = BASE64Encoder.encode(digest);
    dMethod.setAttribute("Algorithm", digestAlgorithmURI);
    dValue.appendTextChild(new String(encodedDigest)); //$JL-I18N$
  }
  /**
   * @return Returns the skipPrefixList.
   */
  public boolean isSkipPrefixList() {
    return skipPrefixList;
  }
  /**
   * @param skipPrefixList The skipPrefixList to set.
   */
  public void setSkipPrefixList(boolean skipPrefixList) {
    this.skipPrefixList = skipPrefixList;
  }
}

