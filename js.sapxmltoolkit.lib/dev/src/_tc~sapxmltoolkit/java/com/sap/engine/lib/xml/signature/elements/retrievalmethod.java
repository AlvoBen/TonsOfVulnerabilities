package com.sap.engine.lib.xml.signature.elements;

import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.transform.Transformation;

public class RetrievalMethod extends Reference {

//  private String type = null;
  
  public RetrievalMethod(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

  public RetrievalMethod(String uri) throws SignatureException {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "RetrievalMethod");
    this.uri = uri;
  }

  public RetrievalMethod(Element domRepr, GenericElement parent) throws SignatureException {
    super(domRepr, parent);
  }

  public void construct(GenericElement $parent) throws SignatureException {
    construct($parent, Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Transforms");
  }

  public byte[] getReferenced() throws SignatureException {
    return undergoTransformations();
  }

  public void init() throws com.sap.engine.lib.xml.signature.SignatureException {
    initializeDescendants();
    uri = getAttribute("URI", null, null);
//    type = getAttribute("Type", null, null);
    GenericElement transformsElement = getDirectChild(Constants.SIGNATURE_SPEC_NS, "Transforms");

    if (transformsElement != null) {
      Vector v = transformsElement.getDirectChildren(Constants.SIGNATURE_SPEC_NS, "Transform");
      transforms = new Transformation[v.size()];

      for (int i = 0; i < v.size(); i++) {
        transforms[i] = trFact.getInstance((GenericElement) v.get(i));
      } 
    }
  }

}

