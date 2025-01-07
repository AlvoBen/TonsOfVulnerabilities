package com.sap.engine.lib.xml.signature.encryption;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

public class EncryptionProperty extends GenericElement {

  public static final String attrNamespace = "http://www.w3.org/XML/1998/namespace";
  public String target = null;
  public Integer id = null;

  public EncryptionProperty(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(parent.getOwner(), namespaceUri, qualifiedName, parent);
  }

  public EncryptionProperty(Element domRepr, GenericElement parent) throws SignatureException{
    super(domRepr, parent);
  }
}

