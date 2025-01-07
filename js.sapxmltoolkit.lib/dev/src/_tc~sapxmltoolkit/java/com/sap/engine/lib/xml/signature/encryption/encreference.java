package com.sap.engine.lib.xml.signature.encryption;

import java.util.Vector;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.Reference;
import com.sap.engine.lib.xml.signature.transform.Transformation;

public class EncReference extends Reference {

  private boolean dataReference = true;

  public EncReference(String namespaceUri, String qualifiedName, GenericElement parent, boolean $dataReference) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
    this.dataReference = $dataReference;
  }

  public EncReference(String uri, boolean dataReference) throws SignatureException {
    super(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + (dataReference ? "DataReference" : "KeyReference"));
    this.uri = uri;
    this.dataReference = dataReference;
  }

  public EncReference(Element domRepr, GenericElement parent, boolean dataReference) throws SignatureException {
    super(domRepr, parent);
    this.dataReference = dataReference;
  }

  public void construct(GenericElement $parent) throws SignatureException {
    construct($parent, Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "Transforms");
  }

  public byte[] getReferenced() throws SignatureException {
    return undergoTransformations();
  }

  public void init() throws com.sap.engine.lib.xml.signature.SignatureException {
    initializeDescendants();
    uri = getAttribute("URI", null, null);
    GenericElement transformsElement = getDirectChild(Constants.ENCRYPTION_SPEC_NS, "Transforms");

    if (transformsElement != null) {
      Vector v = transformsElement.getDirectChildren(Constants.SIGNATURE_SPEC_NS, "Transform");
      transforms = new Transformation[v.size()];

      for (int i = 0; i < v.size(); i++) {
        transforms[i] = trFact.getInstance((GenericElement) v.get(i));
      } 
    }
  }

}

