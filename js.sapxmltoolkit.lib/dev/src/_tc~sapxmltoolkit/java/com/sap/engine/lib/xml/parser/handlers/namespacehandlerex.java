package com.sap.engine.lib.xml.parser.handlers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;

/**
 * Class description - Handles namespace tags.
 *
 * @author Vladimir Videlov
 * @version 1.00
 */
public final class NamespaceHandlerEx implements INamespaceHandler {
	protected XMLParser parent = null;

	public final static CharArray defaultPrefixName = new CharArray("<<<>>>").setStatic();
	protected CharArray defaultPrefixMapping = CharArray.EMPTY;
	protected Stack defaultUri = null;

	protected int level = 0;
	protected Hashtable hashNS = null;
	
	protected Vector<List<CharArray>> nsLevels = null; // ~
	
	private boolean hashNSDirty = true; // hashNS has been modified
	
  protected ArrayList<String[]> endedPrefMappings = new ArrayList();
  
	/**
	* Constructs the NamespaceHandler
	*
	* @param   parent  The XMLParser instanse which has created this object
	*/
	public NamespaceHandlerEx(XMLParser parent) {
		defaultUri = new Stack();
		hashNS = new Hashtable();
		nsLevels = new Vector();
    defaultPrefixName.bufferHash();

		reuse(parent);
	}
	
	public boolean isPrefMappingChangedAndClear(){
		boolean dirty = hashNSDirty;
		hashNSDirty = false;
		return dirty;
	}

	public INamespaceHandler reuse(XMLParser parent) {
		this.parent = parent;

		level = 0;

		nsLevels.clear();
		defaultUri.clear();
		
		hashNS.clear();
		hashNSDirty = true;
		

		addNamespace(XMLParser.crXML, XMLParser.caXMLNamespace);
		addNamespace(XMLParser.caXMLNS, XMLParser.crXMLNSNamespace);
		addNamespace(defaultPrefixName, CharArray.EMPTY);

		//Set prefixes = new HashSet();
		List<CharArray> prefixes = new ArrayList<CharArray>(); //~
		prefixes.add(XMLParser.crXML);
		prefixes.add(XMLParser.caXMLNS);
		prefixes.add(defaultPrefixName);

		nsLevels.add(level, prefixes);
		defaultPrefixMapping = CharArray.EMPTY;

		levelUp();

		return this;
	}

	/**
	* Debug printing of contents of default prefix
	*
	*/
	public void print() {
		//.print();
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
    if (prefix == null || prefix.length() == 0) {
      //prefix = defaultPrefixName;
      return defaultPrefixMapping;
    }
    
    Stack stackURIs = (Stack) hashNS.get(prefix);

	  if (stackURIs != null) {
      return (CharArray) stackURIs.peek();
	  } else  {
      return CharArray.EMPTY;
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
	  CharArray result = CharArray.EMPTY;

	  Stack stackURIs = (Stack) hashNS.get(prefix);

    if (stackURIs != null) {
      result = (CharArray) stackURIs.peek();
    }

	  return result;
  }


  private Stack stIsMappedRes = null;
  public CharArray isMapped(CharArray prefix) throws Exception {
    if (prefix == null || prefix.length() == 0)  {
     // prefix = defaultPrefixName;
      return defaultPrefixMapping;
      //return CharArray.EMPTY;
    }
      try {
        return (CharArray)((Stack)hashNS.get(prefix)).peek();
      } catch (Exception e) {
        throw new Exception("XMLParser: Prefix \'" + prefix + "\' is not mapped to a namespace");
      }
    //return getDefault();
  }

  public CharArray isMappedAttr(CharArray prefix) throws Exception {
    if (prefix != null && prefix.length() > 0) { // && !prefix.equals("xmlns")) {
      if ((stIsMappedRes = (Stack) hashNS.get(prefix)) != null) {
        return (CharArray)stIsMappedRes.peek();
      } else {
        throw new Exception("XMLParser: Prefix \'" + prefix + "\' is not mapped to a namespace");
      }
    }
    return CharArray.EMPTY;
  }

//	public boolean isMapped(CharArray prefix) {
//	  boolean result = true;
//
//    if (prefix != null && prefix.length() > 0 && !prefix.equals("xmlns")) {
//      if (hashNS.get(prefix) != null) {
//	      result = true;
//      } else {
//        result = false;
//      }
//    }
//
//    return result;
//  }

  /**
   * Adds a namespace, with given prefix, and uri
   *
   * @param   prefix
   * @param   uri
   */
  public void add(CharArray prefix, CharArray uri) {
    //LogWriter.getSystemLogWriter().println("NamespaceHandlerEx.add() prefix=" + prefix + ", uri=" + uri);
	if (!prefix.getStatic()) {
      prefix = prefix.copy();
    }

    if (!uri.getStatic()) {
      uri = uri.copy();
    }

	  addNamespace(prefix, uri);

	  if (nsLevels.size() > level) {
		  List<CharArray> prefixes = (List<CharArray>) nsLevels.get(level); //~

		  if (prefixes != null) {
			  prefixes.add(prefix);
		  } else {
			  //prefixes = new HashSet();
			  prefixes = new ArrayList<CharArray>(); //~
			  prefixes.add(prefix);
			  nsLevels.set(level, prefixes);
		  }
	  } else {
			//Set prefixes = new HashSet();
		  	ArrayList<CharArray> prefixes = new ArrayList<CharArray>(); //~
			prefixes.add(prefix);
			nsLevels.setSize(level + 1);
			nsLevels.set(level, prefixes);
	  }
  }

  /**
   * Called when the xml scanner goes a level deeper into the xml, and the
   * level counters have to be updated
   *
   */
  public void levelUp() {
	  level++;
    this.endedPrefMappings.clear();
  }

  /**
   * Called when the xml scanner goes a level higher in the xml, and checks
   * whether some of the Namespace enties have gone out of scope
   *
   * @exception   Exception
   */
  public void levelDown() throws Exception {
	  level--;
	  this.endedPrefMappings.clear();
    
	  if (nsLevels.size() > level) {
			//Set prefixes = (Set) nsLevels.get(level); //~
		  ArrayList<CharArray>  prefixes = (ArrayList<CharArray>)nsLevels.get(level);
			
		  if (prefixes != null) {
				/*Iterator itor = prefixes.iterator();
				while (itor.hasNext()) {
					CharArray prefix = (CharArray) itor.next();
					CharArray ns = removeLast(prefix);
					this.endedPrefMappings.add(new String[]{prefix.getString(), ns.getString()});
				}*/
			  CharArray ns;
			  for(CharArray prefix: prefixes){ //~
				  ns = removeLast(prefix);
				  this.endedPrefMappings.add(new String[]{prefix.getString(), ns.getString()});
			  }
			  prefixes = null;
		  }
		  nsLevels.remove(level);
	  }
  }

  public void findNamespaceNodes(Element el, NamespaceManager nsmanager) {
    //    LogWriter.getSystemLogWriter().println("processing namespace nodes for node: " + el.getNodeName());
    NamedNodeMap nm = el.getAttributes();

    for (int i = 0; i < nm.getLength(); i++) {
      Attr attr = (Attr) nm.item(i);
      String prefix = attr.getPrefix();

	    if (prefix == null) {
      	prefix = "";
      }

	    if (prefix.equals("xmlns")) {
	      //LogWriter.getSystemLogWriter().println("Adding namespace:" + attr.getLocalName() + " = " + attr.getValue());
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

  public Hashtable getNamespaceMappings() {
  	Hashtable mappings = new Hashtable();
		Enumeration enum1 = hashNS.keys();

	  while (enum1.hasMoreElements()) {
		  Object prefix = enum1.nextElement();
		  if (prefix == XMLParser.crXML || prefix == XMLParser.caXMLNS) {
		  	continue;
		  }
			Stack uri = (Stack) (hashNS.get(prefix));

		  if (uri != null && !uri.isEmpty()) {
		     mappings.put(prefix.equals(defaultPrefixName) ? CharArray.EMPTY : prefix, ((CharArray) uri.peek()).getString());
		  }
		}

		return mappings;
  }

	protected void addNamespace(CharArray prefix, CharArray uri) {
		Stack stackURIs = (Stack) hashNS.get(prefix);

		if (stackURIs != null) {
			stackURIs.push(uri);
			if (prefix == defaultPrefixName)  {
				defaultPrefixMapping = uri;
			}
		} else {
			stackURIs = new Stack();
			stackURIs.push(uri);
			hashNS.put(prefix, stackURIs);
		}
		hashNSDirty = true;
	}

	protected CharArray removeLast(CharArray prefix) {
		CharArray result = null;
		Stack stackURIs = ((Stack) hashNS.get(prefix));

		if (stackURIs != null) {
			result = (CharArray) stackURIs.pop();
			if (prefix == defaultPrefixName)  {
				defaultPrefixMapping = (CharArray)stackURIs.peek();
			}
			if (stackURIs.empty()) {
				hashNS.remove(prefix);
				stackURIs = null;
			}
			hashNSDirty = true;
		}

		return result;
	}

	protected void removePrefix(CharArray prefix) {
//		Stack stackURIs = (Stack) hashNS.remove(prefix);
//		stackURIs = null;
	}
  
    public int getLevel() {
      return level;
    }

  //private final Set<CharArray> EMPTY_SET = new HashSet();
    private final ArrayList<CharArray> EMPTY_LIST = new ArrayList<CharArray>(); 
  
  public List<CharArray> getPrefixesOnLastStartElement() { //~
    if (level > nsLevels.size()) {
      return EMPTY_LIST;
    } else {
      return (List<CharArray>) nsLevels.get(level - 1);
    }
  }
  /**
   * List of prefix definitions. Where one prefix definition
   * is an array is size 2, where [0] is the prefix and [1] is the namespace.
   * @return
   */
  public List<String[]> getEndedPrefixMappings() {
    return this.endedPrefMappings;
  }
  
	public static void main(String[] args) throws Exception {
    XMLParser pp = new XMLParser();
		EmptyDocHandler eh = new EmptyDocHandler();
		//TestDocHandler eh = new TestDocHandler();
		pp.setSoapProcessing(true);

		int iter = 50;

		long a = System.currentTimeMillis();

		for (int i=0; i < iter; i++) {
			pp.parse("D:/develop/xmls/nstestBasic.xml", eh);
		}

		long b = System.currentTimeMillis() - a;

		LogWriter.getSystemLogWriter().println("Elapsed time: " + b + " ms");
	}
}