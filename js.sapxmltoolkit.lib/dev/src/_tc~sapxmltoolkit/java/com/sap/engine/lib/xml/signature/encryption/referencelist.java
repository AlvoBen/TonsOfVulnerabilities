/*
 * Created on 2004-3-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.encryption;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ReferenceList extends GenericElement {

  /**
   * @param namespaceUri
   * @param qualifiedName
   */
  public ReferenceList(String namespaceUri, String qualifiedName) {
    super(namespaceUri, qualifiedName);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param namespaceUri
   * @param qualifiedName
   * @param parent
   * @throws SignatureException
   */
  public ReferenceList(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param n
   * @param namespaceUri
   * @param qualifiedName
   * @param parent
   * @param generateDom
   * @throws SignatureException
   */
  public ReferenceList(Node n, String namespaceUri, String qualifiedName, GenericElement parent, boolean generateDom) throws SignatureException {
    super(n, namespaceUri, qualifiedName, parent, generateDom);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param owner
   * @param namespaceUri
   * @param qualifiedName
   * @param parent
   * @throws SignatureException
   */
  public ReferenceList(Document owner, String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(owner, namespaceUri, qualifiedName, parent);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param domRepresentation
   * @param parent
   * @throws SignatureException
   */
  public ReferenceList(Element domRepresentation, GenericElement parent) throws SignatureException {
    super(domRepresentation, parent);
    // TODO Auto-generated constructor stub
  }

}
