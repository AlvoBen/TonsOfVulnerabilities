package com.sap.engine.lib.xml.parser.handlers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public class NamespaceHandler implements INamespaceHandler {

  protected Vector ns = null;
  protected Hashtable hashns = null;
  public final static CharArray defaultPrefixName = new CharArray("<<<>>>").setStatic();
  protected XMLParser parent = null;
  protected int level = 0;

  /**
   * Constructs the NamespaceHandler
   *
   * @param   parent  The XMLParser instanse which has created this object
   */

   private NamespaceEntry neXML = new NamespaceEntry(XMLParser.crXML, XMLParser.caXMLNamespace);
   private NamespaceEntry neXMLNS = new NamespaceEntry(XMLParser.caXMLNS, XMLParser.crXMLNSNamespace);

     /**
      * Constructs the NamespaceHandler
      *
      * @param   parent  The XMLParser instanse which has created this object
      */
     public NamespaceHandler(XMLParser parent) {
       ns = new Vector();
       hashns = new Hashtable();
       reuse(parent);
     //this.parent = parent;
     //add(XMLParser.crXML, XMLParser.caXMLNamespace);
     //add(XMLParser.caXMLNS, XMLParser.crXMLNSNamespace);
     //levelUp();
     //levelUp();
     }

     public INamespaceHandler reuse(XMLParser parent) {
       level = 0;
       ns.clear();
       hashns.clear();
       this.parent = parent;
	   hashns.put(XMLParser.crXML, neXML);
       ns.add(neXML);
       hashns.put(XMLParser.caXMLNS, neXMLNS);
       ns.add(neXMLNS);
     //add(XMLParser.crXML, XMLParser.caXMLNamespace);
     //add(XMLParser.caXMLNS, XMLParser.crXMLNSNamespace);
       levelUp();
       levelUp();
       return this;
     }  /**
   * Debug printing of contents of default prefix
   *
   */
  public void print() {
    ((NamespaceEntry) hashns.get(defaultPrefixName)).print();
  }

  /**
   * Inserts the default namespace
   *
   * @param   uri
   */
  public void addDefault(CharArray uri) {
    add(defaultPrefixName, uri.copy());
  }

  /**
   * Returns the default namspace
   *
   * @return
   */
  public CharArray getDefault() {
    return get(defaultPrefixName);
  }

  /**
   * Returns the uri of the namespace pointed by prefix
   *
   * @param   prefix
   * @return
   */
  public CharArray get(CharArray prefix) {
    //    LogWriter.getSystemLogWriter().println("getting prefix:" + prefix);
    if (prefix == null || prefix.length() == 0) {
      return getDefault();
    } else {
      NamespaceEntry ne = (NamespaceEntry) hashns.get(prefix);
      //      LogWriter.getSystemLogWriter().println("got: " + ne);
      return (ne != null) ? ne.getUri() : CharArray.EMPTY;
    }
  }

  /**
   * Same as get(String prefix), but for attribute namspapces,
   * there is no default namespacing
   *
   * @param   prefix
   * @return
   */
  public CharArray getAttr(CharArray prefix) {
    NamespaceEntry ne = (NamespaceEntry) hashns.get(prefix);
    return (ne != null) ? ne.getUri() : CharArray.EMPTY;
  }

  /**
   * Adds a namespace, with given prefix, and uri
   *
   * @param   prefix
   * @param   uri
   */
  public void add(CharArray prefix, CharArray uri) {
    if (!prefix.getStatic()) {
      prefix = prefix.copy();
    }

    if (!uri.getStatic()) {
      uri = uri.copy();
    }

    //CharArray p = prefix.copy();
    NamespaceEntry ne = new NamespaceEntry(prefix, uri);
    //    LogWriter.getSystemLogWriter().println("\nputting: " + ne);

    NamespaceEntry neout = (NamespaceEntry) hashns.put(prefix, ne);

    //LogWriter.getSystemLogWriter().println("\nputting: " + ne.getPrefix() + " : " + ne.getUri());
    if (neout != null) {
      //LogWriter.getSystemLogWriter().println("\noutting; " + neout.getPrefix() + " : " + ne.getUri());
    }

    if (neout != null) {
      ne.setPrevNamespace(neout);
      ns.removeElement(neout);
      //print();
    }

    ns.add(ne);
  }

  /**
   * Called when the xml scanner goes a level deeper into the xml, and the
   * level counters of the NamespaceEntries have to be updated
   *
   */
  public void levelUp() {
    //    LogWriter.getSystemLogWriter().println("Going UUP");
    level ++;
    for (int i = 0; i < ns.size(); i++) {
      ((NamespaceEntry) ns.get(i)).levelUp();
    }
  }

  /**
   * Called when the xml scanner goes a level higher in the xml, and checks
   * whether some of the NamespaceEnties have gone out of scope
   *
   * @exception   Exception
   */
  public void levelDown() throws Exception {
    //    LogWriter.getSystemLogWriter().println("Going DOOOWN");
    level --;
    for (int i = 0; i < ns.size(); i++) {
      NamespaceEntry nsent = (NamespaceEntry) ns.get(i);
      //LogWriter.getSystemLogWriter().println(nsent);
      boolean changedMapping = nsent.levelDown();

      //      LogWriter.getSystemLogWriter().println("changedMapping?:" + changedMapping);
      if (parent != null && changedMapping) {
        parent.changingMapping(nsent.getPrefix(), nsent.getUri());
      }

      if (!((NamespaceEntry) ns.get(i)).isValid()) {
        CharArray pref = ((NamespaceEntry) ns.get(i)).getPrefix();
        CharArray pref2 = pref;

        if (pref == defaultPrefixName) {
          pref2 = CharArray.EMPTY;
        }

        if (parent != null) {
          parent.endPrefixMapping(pref2);
        }

        //        LogWriter.getSystemLogWriter().println("------------ End of prefix: " + pref );
        hashns.remove(pref);
        ns.removeElementAt(i);
        i--;
      }
    }
  }

  public void findNamespaceNodes(Element el, NamespaceManager nsmanager) {
    //    LogWriter.getSystemLogWriter().println("processing namespace nodes for node: " + el.getNodeName());
    NamedNodeMap nm = el.getAttributes();

    for (int i = 0; i < nm.getLength(); i++) {
      Attr attr = (Attr) nm.item(i);

      String prefix = attr.getPrefix();
      if(prefix == null) {
      	prefix = "";
      }

	    if (prefix.equals("xmlns")) {
	      //        LogWriter.getSystemLogWriter().println("Adding namespace:" + attr.getLocalName() + " = " + attr.getValue());
	      add(new CharArray(attr.getLocalName()), new CharArray(attr.getValue()));
	      if (nsmanager != null) {
	        nsmanager.put(attr.getValue());
	      }
	    } else if (prefix.length() == 0 && attr.getLocalName().equals("xmlns")) {
	      addDefault(new CharArray(attr.getValue()));
	      if (nsmanager != null) {
	        nsmanager.put(attr.getValue());
	      }
	    }
    }
  }
  private NamespaceEntry neIsMappedRes = null;
  public CharArray isMapped(CharArray prefix) throws Exception {
    if (prefix != null && prefix.length() > 0 && !prefix.equals("xmlns")) {
      if ((neIsMappedRes = (NamespaceEntry) hashns.get(prefix)) != null) {
        return neIsMappedRes.getUri();
      } else {
        throw new Exception("XMLParser: Prefix \'" + prefix + "\' is not mapped to a namespace");
      }
    }
    return getDefault();
  }

  public CharArray isMappedAttr(CharArray prefix) throws Exception {
    if (prefix != null && prefix.length() > 0) {// && !prefix.equals("xmlns")) {
      if ((neIsMappedRes = (NamespaceEntry) hashns.get(prefix)) != null) {
        return neIsMappedRes.getUri();
      } else {
        throw new Exception("XMLParser: Prefix \'" + prefix + "\' is not mapped to a namespace");
      }
    }
    return CharArray.EMPTY;
  }

  public Hashtable getNamespaceMappings() {
  	Hashtable mappings = new Hashtable();
		Enumeration prefixesEnum = hashns.keys();
		while(prefixesEnum.hasMoreElements()) {
			Object prefix = prefixesEnum.nextElement();
			NamespaceEntry nsEntry = (NamespaceEntry)(hashns.get(prefix));
			mappings.put(prefix.equals(defaultPrefixName.getString()) ? "" : prefix, nsEntry.uri.getString());
		}
		return(mappings);
  }
  
  public int getLevel() {
    return level;
  }
}

