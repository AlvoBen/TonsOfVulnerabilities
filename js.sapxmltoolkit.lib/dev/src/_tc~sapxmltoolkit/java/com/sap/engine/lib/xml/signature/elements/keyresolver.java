/*
 * Created on 2004-4-7
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.elements;

import java.security.Key;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.signature.SignatureException;

/**
 * This interface is used in KeyInfo element to resolve keys from xml element.
 * All registred KeyResolvers must be specified in the "config.xml", e.g.: <br>
 * &lt;KeyResolver class="com.acme.keys.KeyResolverImpl"
 * uri="http://foo.bar/namespace" rank="0"/&gt;, <br>
 * where class stays for the class of the key resolver implementation, uri -
 * the name space uri of the elements to be handled by this class and rank is
 * the order in which key resolvers are called, if there are some with the same
 * namespace uri. <br>
 * Every KeyResolver implementation must have empty constructor.
 * 
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public interface KeyResolver {

  /**
   * This method must return key constructed from the xmlEncodedKey element for
   * the encryption algorithm specified by encryptionAlgorithmURI. The
   * encryptionAlgorithmURI is deduced from the encrypted data, which was
   * encrypted with this key. If no key can be extracted null must be returned,
   * so another registred KeyResolver for this namespace uri to be used.
   * 
   * @param xmlEncodedKey
   *          XML element containing key information (e.g. KeyName, encoded key etc)
   * @param encryptionAlgorithmURI
   *          encryption algorithm to be used with this key (may be null if it
   *          cannot be deduced).
   * @return extracted key (may be null if no key can be extracted).
   * @throws SignatureException
   *           if an error occurs.
   */
  public Key extractKey(Element xmlEncodedKey, String encryptionAlgorithmURI) throws SignatureException;

}
