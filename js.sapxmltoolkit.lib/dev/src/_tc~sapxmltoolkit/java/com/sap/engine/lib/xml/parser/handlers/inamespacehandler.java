/*
 * Created on 2003-9-29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.xml.parser.handlers;

import java.util.Hashtable;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;

/**
 * @author Vladimir-S
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface INamespaceHandler {

  public static final CharArray defaultPrefixName = new CharArray("<<<>>>");

  public INamespaceHandler reuse(XMLParser parent);

  public void print();

  /**
   * Inserts the default namespace
   *
   * @param   uri
   */
  public void addDefault(CharArray uri);

  /**
   * Returns the default namspace
   *
   * @return
   */
  public CharArray getDefault();

  /**
   * Returns the uri of the namespace pointed by prefix
   *
   * @param   prefix
   * @return
   */
  public CharArray get(CharArray prefix);

  /**
   * Same as get(String prefix), but for attribute namspapces,
   * there is no default namespacing
   *
   * @param   prefix
   * @return
   */
  public CharArray getAttr(CharArray prefix);

  /**
   * Adds a namespace, with given prefix, and uri
   *
   * @param   prefix
   * @param   uri
   */
  public void add(CharArray prefix, CharArray uri);

  /**
   * Called when the xml scanner goes a level deeper into the xml, and the
   * level counters of the NamespaceEntries have to be updated
   *
   */
  public void levelUp();

  /**
   * Called when the xml scanner goes a level higher in the xml, and checks
   * whether some of the NamespaceEnties have gone out of scope
   *
   * @exception   Exception
   */
  public void levelDown() throws Exception ;

  public void findNamespaceNodes(Element el, NamespaceManager nsmanager);

  public CharArray isMapped(CharArray prefix) throws Exception;
  public CharArray isMappedAttr(CharArray prefix) throws Exception;

  public Hashtable getNamespaceMappings();

  public int getLevel();

}
