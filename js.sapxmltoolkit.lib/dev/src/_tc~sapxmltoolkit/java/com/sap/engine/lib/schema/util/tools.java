package com.sap.engine.lib.schema.util;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.UTF8Encoding;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.structures.BaseImpl;
import com.sap.engine.lib.schema.components.impl.structures.SchemaImpl;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.exception.SchemaException;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public final class Tools implements Constants {
	
	private static final String PREFIX_DELIMITER = ":";
	
  private Tools() {

  }

  public static String formatQNameWithUri(String uri, String local) {
    return "{" + uri + "}" + PREFIX_DELIMITER + local;
  }
  
  public static String formatQNameWithPrefix(String prefix, String local) {
  	return((prefix == null || prefix.equals("")) ? local : prefix + PREFIX_DELIMITER + local);	
  }

  public static String formatQNameWithUri(String uri, CharArray local) {
    return formatQNameWithUri(uri, local.toString());
  }
  
  public static boolean compareBases(Base base1, Base base2) {
  	if(base1 == base2 || base1.equals(base2)) {
  		return(true);
  	}
  	if(base1 == null ^ base2 == null) {
  		return(false);
  	}
  	if(base1 != null) {
  		return(base1.match(base2));
  	}
  	return(true);
  }
  
  public static boolean compareObjects(Object obj1, Object obj2) {
  	if(obj1 == null ^ obj2 == null) {
  		return(false);
  	}
  	if(obj1 != null) {
  		return(obj1.equals(obj2));
  	}
  	return(true);
  }
  
  public static boolean compareUnorderedBases(Vector basesCollector1, Vector basesCollector2) {
  	if(basesCollector1 == null ^ basesCollector2 == null) {
  		return(false);
  	}
  	if(basesCollector1 != null) {
	  	if(basesCollector1.size() != basesCollector2.size()) {
	  		return(false);
	  	}
	  	boolean[] checked = new boolean[basesCollector1.size()];
	  	for(int i = 0; i < basesCollector1.size(); i++) {
	  		Base base1 = (Base)(basesCollector1.get(i));
	  		boolean found = false;
	  		for(int j = 0; j < basesCollector2.size(); j++) {
	  			if(!checked[j]) {
	  				Base base2 = (Base)(basesCollector2.get(j));
	  		  	if(base1 == base2 || base1.equals(base2) || base1.match(base2)) {
	  					checked[j] = true;
	  					found = true;
	  					break;
	  				}
	  			}
	  		}
	  		if(!found) {
	  			return(false);
	  		}
	  	}
  	}
		return(true);  	    
	}

  public static boolean compareUnorderdBases(Hashtable basesMapping1, Hashtable basesMapping2) {
  	if(basesMapping1 == null ^ basesMapping2 == null) {
  		return(false);
  	}
  	if(basesMapping1 != null) {
	  	if(basesMapping1.size() != basesMapping2.size()) {
	  		return(false);
	  	}
	  	boolean[] checked = new boolean[basesMapping1.size()];
      Enumeration srcEnum = basesMapping1.elements();
      Enumeration dstEnum = basesMapping2.elements();
	  	for(int i = 0; i < basesMapping1.size(); i++) {
	  		Base base1 = (Base)(srcEnum.nextElement());
	  		boolean found = false;
	  		for(int j = 0; j < basesMapping2.size(); j++) {
	  			if(!checked[j]) {
	  				Base base2 = (Base)(dstEnum.nextElement());
	  		  	if(base1 == base2 || base1.equals(base2) || base1.match(base2)) {
	  					checked[j] = true;
	  					found = true;
	  					break;
	  				}
	  			}
	  		}
	  		if(!found) {
	  			return(false);
	  		}
	  	}
  	}
		return(true);
	}

  public static boolean compareUnorderdBases(Base[] bases1, Base[] bases2) {
  	if(bases1 == null ^ bases2 == null) {
  		return(false);
  	}
  	if(bases1 != null) {
	  	if(bases1.length != bases2.length) {
	  		return(false);
	  	}
	  	boolean[] checked = new boolean[bases1.length];
	  	for(int i = 0; i < bases1.length; i++) {
	  		Base base1 = bases1[i];
	  		boolean found = false;
	  		for(int j = 0; j < bases2.length; j++) {
	  			if(!checked[j]) {
	  				Base base2 = bases2[j];
	  				if(base1.match(base2)) {
	  					checked[j] = true;
	  					found = true;
	  					break;
	  				}
	  			}
	  		}
	  		if(!found) {
	  			return(false);
	  		}
	  	}
  	}
		return(true);  	    
	}	
	
	public static boolean compareOrderedBases(Vector basesCollector1, Vector basesCollector2) {
  	if(basesCollector1 == null ^ basesCollector2 == null) {
  		return(false);
  	}
  	if(basesCollector1 != null) {
	  	if(basesCollector1.size() != basesCollector2.size()) {
	  		return(false);
	  	}
	  	for(int i = 0; i < basesCollector1.size(); i++) {
	  		Base base1 = (Base)(basesCollector1.get(i));
	  		Base base2 = (Base)(basesCollector2.get(i));
	  		if(!base1.match(base2)) {
	  			return(false);
	  		}
	  	}
  	}
  	return(true);
 	}
	
	public static boolean compareUnorderedStrings(String[] strings1, String[] strings2) {
  	if(strings1 == null ^ strings2 == null) {
  		return(false);
  	}
  	if(strings1 != null) {
	  	if(strings1.length != strings2.length) {
	  		return(false);
	  	}
	  	boolean[] checked = new boolean[strings1.length];
	  	for(int i = 0; i < strings1.length; i++) {
	  		String string1 = strings1[i];
	  		boolean found = false;
	  		for(int j = 0; j < strings2.length; j++) {
	  			if(!checked[j]) {
	  				String string2 = strings2[j];
	  				if(string1.equals(string2)) {
	  					checked[j] = true;
	  					found = true;
	  					break;
	  				}
	  			}
	  		}
	  		if(!found) {
	  			return(false);
	  		}
	  	}
  	}
		return(true);  	    
	}
	
	public static boolean compareUnorderedObjects(Vector collector1, Vector collector2) {
  	if(collector1 == null ^ collector2 == null) {
  		return(false);
  	}
  	if(collector1 != null) {
	  	if(collector1.size() != collector2.size()) {
	  		return(false);
	  	}
	  	boolean[] checked = new boolean[collector1.size()];
	  	for(int i = 0; i < collector1.size(); i++) {
	  		Object object1 = collector1.get(i);
	  		boolean found = false;
	  		for(int j = 0; j < collector2.size(); j++) {
	  			if(!checked[j]) {
	  				Object object2 = collector2.get(i);
	  				if(object1.equals(object2)) {
	  					checked[j] = true;
	  					found = true;
	  					break;
	  				}
	  			}
	  		}
	  		if(!found) {
	  			return(false);
	  		}
	  	}
  	}
		return(true);  	    
	}

  public static String generateKey(String uri, String name) {
    if(uri == null) {
      uri = "";
    }
    return("{" + uri + "}:" + name);
  }

  public static void removeFromHashtableToVector(Hashtable hash, Vector collector) {
    if(hash != null) {
      Enumeration enum1 = hash.elements();
      while(enum1.hasMoreElements()) {
        collector.add(enum1.nextElement());
      }
    }
  }

  public static void removeFromVectorToVector(Vector srcCollector, Vector dstCollector) {
    if(srcCollector != null) {
      dstCollector.addAll(srcCollector);
    }
  }

  public static void removeFromHashtableToArray(Hashtable hash, Object[] array) {
    Enumeration enum1 = hash.elements();
    int index = 0;
    while(enum1.hasMoreElements()) {
      array[index++] = enum1.nextElement();
    }
  }

  public static void cloneVectorWithBases(Vector srcCollector, Vector dstCollector, Hashtable typesCollector) {
    for(int i = 0; i < srcCollector.size(); i++) {
      BaseImpl base = (BaseImpl)(srcCollector.get(i));
      dstCollector.add(base.clone(typesCollector));
    }
  }

  public static void cloneHashtableWithBases(Hashtable srcCollector, Hashtable dstCollector, Hashtable typesCollector) {
    Enumeration keys = srcCollector.keys();
    while(keys.hasMoreElements()) {
      String key = (String)(keys.nextElement());
      BaseImpl base = (BaseImpl)(srcCollector.get(key));
      dstCollector.put(key, base.clone(typesCollector));
    }
  }

//  public static String canonicalizeLocation(String base, String location) throws IOException {
//    String result = null;
//    if(location != null) {
//      if(location.startsWith("http:")) {
//        int index = "http:".length();
//        while(location.charAt(index++) == '/' && index < location.length());
//        result = "http://" + location.substring(index - 1);
//      } else if(location.startsWith("file:")) {
//        int index = "file:".length();
//        while(location.charAt(index++) == '/' && index < location.length());
//        result = "file://" + location.substring(index - 1);
//      } else if(location.startsWith(".")) {
//        result = "file://" + (base == null ? (new File(location)).getCanonicalPath()
//                                           : (new File(base, location)).getCanonicalPath());
//      } else {
//        result = "file://" + location;
//      }
//      result = result.replace('\\', '/');
//    }
//    return(result);
//  }

  public static String[] parseQName(String qName) {
    int index = qName.indexOf(":");
    if(index < 0) {
      return(new String[]{null, qName});
    }
    return(new String[]{qName.substring(0, index), qName.substring(index + 1)});
  }

  public static Vector createVectorFromEnumeration(Enumeration enum1) {
    Vector result = new Vector();
    while(enum1.hasMoreElements()) {
      result.add(enum1.nextElement());
    }
    return(result);
  }

  public static String getUri(Hashtable mappings, String prefix) {
    return((String)(mappings.get(prefix == null ? "" : prefix)));
  }

  public static String getDefaultUri(Hashtable mappings) {
    String uri = getUri(mappings, "");
    return(uri == null ? "" : uri);
  }

  public static String[] parseQName(String qName, Hashtable prefixesMapping) {
    String[] prefixAndName = parseQName(qName);
    String uri = getUri(prefixesMapping, prefixAndName[0]);
    return(new String[]{uri, prefixAndName[1]});
  }
  
	public static String normalizeValue(String initialValue, String whiteSpaceNormValue) {
		if(whiteSpaceNormValue == null) {
			return(initialValue);
		}
		StringBuffer buffer = null;
		if(whiteSpaceNormValue.equals(WHITE_SPACE_REPLACE_NORM_VALUE)) {
			buffer = new StringBuffer();
			for(int i = 0; i < initialValue.length(); i++) {
				char ch = initialValue.charAt(i);
				char chToAppend = ch;
				if(ch == '\t' || ch == '\r' || ch == '\n') {
					chToAppend = ' ';
				}
				buffer.append(chToAppend);
			}
		} else if(whiteSpaceNormValue.equals(WHITE_SPACE_PRESERVE_NORM_VALUE)) {
			buffer = new StringBuffer(initialValue);
		} else {
			buffer = new StringBuffer();
			initialValue = initialValue.trim();
			for(int i = 0; i < initialValue.length(); i++) {
				char ch = initialValue.charAt(i);
				char chToAppend = ch;
				boolean append = false;
				if(ch == '\t' || ch == '\r' || ch == '\n') {
					if(buffer.charAt(buffer.length() - 1) != ' ') {
						chToAppend = ' ';
						append = true;
					}
				} else if(ch == ' ') {
					append = buffer.charAt(buffer.length() - 1) != ' '; 
				} else {
					append = true;
				}
				if(append) {
					buffer.append(chToAppend);
				}
			}
		}
		return(buffer.toString());
	}

//  public static String normalizeValue(String initialValue, String whiteSpaceNormValue, boolean isAnyURIValue) {
//    if(whiteSpaceNormValue == null) {
//      return(initialValue);
//    }
//		byte[] utf8Bytes = new byte[6];
//		UTF8Encoding utf8Encoding = new UTF8Encoding(); 
//    StringBuffer buffer = null;
//    if(whiteSpaceNormValue.equals(WHITE_SPACE_REPLACE_NORM_VALUE)) {
//    	buffer = new StringBuffer();
//      for(int i = 0; i < initialValue.length(); i++) {
//        char ch = initialValue.charAt(i);
//        char chToAppend = ch;
//        if(ch == '\t' || ch == '\r' || ch == '\n') {
//					chToAppend = ' ';
//        }
//        if(isAnyURIValue) {
//					appendEscapedUnexceptedURIChars(chToAppend, buffer, utf8Bytes, utf8Encoding);
//        } else {
//        	buffer.append(chToAppend);
//        }
//      }
//    } else if(whiteSpaceNormValue.equals(WHITE_SPACE_PRESERVE_NORM_VALUE)) {
//    	if(isAnyURIValue) {
//				buffer = new StringBuffer();
//				for(int i = 0; i < initialValue.length(); i++) {
//					char ch = initialValue.charAt(i);
//					appendEscapedUnexceptedURIChars(ch, buffer, utf8Bytes, utf8Encoding);
//				}
//    	} else {
//      	buffer = new StringBuffer(initialValue);
//    	}
//    } else {
//    	buffer = new StringBuffer();
//      initialValue = initialValue.trim();
//      for(int i = 0; i < initialValue.length(); i++) {
//        char ch = initialValue.charAt(i);
//        char chToAppend = ch;
//        boolean append = false;
//        if(ch == '\t' || ch == '\r' || ch == '\n') {
//          if(buffer.charAt(buffer.length() - 1) != ' ') {
//          	chToAppend = ' ';
//            append = true;
//          }
//        } else if(ch == ' ') {
//        	append = buffer.charAt(buffer.length() - 1) != ' '; 
//        } else {
//        	append = true;
//        }
//        if(append) {
//        	if(isAnyURIValue) {
//						appendEscapedUnexceptedURIChars(chToAppend, buffer, utf8Bytes, utf8Encoding);
//        	} else {
//        		buffer.append(chToAppend);
//        	}
//        }
//      }
//    }
//    return(buffer.toString());
//  }
	
	private static void appendEscapedUnexceptedURIChars(char ch, StringBuffer buffer, byte[] utf8Bytes, UTF8Encoding utf8Encoding) {
		if((ch >= 0x00 && ch <= 0x001f) || ch >= 0x007f || ch == '<' || ch == '>' || ch == '"' || ch == '{' || ch == '}' || ch == '|' || ch == '\\' || ch == '^' || ch == '\'' || (ch == 0x0020 && (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '%'))) {
			int length = utf8Encoding.reverseEncode(utf8Bytes, ch);
			String escapedCharacterSequence = "";
			for(int j = 0; j < length; j++) {
				String hexNotation = Integer.toHexString((int)utf8Bytes[j] & 0xff);
				if(hexNotation.length() == 1) {
					hexNotation = "0" + hexNotation; 
				} 
				buffer.append("%" + hexNotation);  
			}
		} else {
			buffer.append(ch);
		}
	}
	
  public static InputSource retrieveInputSource(EntityResolver resolver, String parentLocation, String location) throws SchemaComponentException {
    try {
      if(resolver != null) {
        InputSource inputSource = resolver.resolveEntity(null, location);
        if(inputSource != null) {
					if(inputSource.getByteStream() == null && inputSource.getCharacterStream() == null && inputSource.getSystemId() == null) {
					  throw new SchemaException("[location : " + parentLocation + "] ERROR : Entity resolved to an InputSource, with no InputStream, CharacterStream and SystemId.");
          }
          return(inputSource);
        }
      }
      return(retriveInputSource(parentLocation, location));
    } catch(Throwable tr) {
      //$JL-EXC$

      if(tr instanceof SchemaComponentException) {
        throw (SchemaComponentException)tr;
      } else if(tr instanceof OutOfMemoryError) {
        throw (OutOfMemoryError)tr;
      } else {
        throw new SchemaComponentException("[location : " + parentLocation + "] ERROR : " + tr.getMessage(), tr);
      }
    }
  }

  public static InputSource retriveInputSource(String parentLocation, String location) throws IOException {
    InputSource result = new InputSource();
    URL baseURL = parentLocation == null ? null : new URL(parentLocation);
    URL sourceURL = URLLoader.fileOrURLToURL(baseURL, location);
    result.setByteStream(sourceURL.openStream());
    result.setSystemId(sourceURL.toExternalForm());
    return(result);
  }

  public static Element retrieveNode(StandardDOMParser parser, InputSource source) throws SchemaComponentException {
    if(source == null) {
      return(null);
    }
    try {
      Document doc = parser.parse(source);
      return(doc.getDocumentElement());
    } catch(Exception exc) {
      throw new SchemaComponentException(exc);
    }
  }

  public static Element retrieveNode(DocumentBuilder docBuilder, InputSource source) throws SchemaComponentException {
    if(source == null) {
      return(null);
    }
    try {
      Document doc = docBuilder.parse(source);
      return(doc.getDocumentElement());
    } catch(Exception exc) {
      throw new SchemaComponentException(exc);
    }
  }

  public static Element retrieveDocElement(Node node) {
    Element result = null;
    if(node instanceof Element) {
      result = (Element)node;
    } else if(node instanceof Document) {
      Document doc = (Document)node;
      result = doc.getDocumentElement();
    }
    return(result);
  }

  public static InputSource createInputSource(StreamSource streamSource) {
    InputSource result = new InputSource();
    result.setByteStream(streamSource.getInputStream());
    result.setPublicId(streamSource.getPublicId());
    result.setCharacterStream(streamSource.getReader());
    result.setSystemId(streamSource.getSystemId());
    return(result);
  }
  
	public static BaseImpl getTopLevelComponent(SchemaImpl schema, Node node, String qName, String componentId) throws SchemaComponentException {
    String[] namespaceAndName = getNamespaceAndName(qName, schema, node);
    String uri = namespaceAndName[0];
    String localName = namespaceAndName[1];
		BaseImpl base = (BaseImpl)(schema.getTopLevelComponent(uri, localName, componentId));
		if(base == null) {
			if(uri.equals(schema.getTargetNamespace())) {
				schema.includeNamespace();
			} else if(schema.getLoader().getUriResolver() != null) {
				schema.importNamespace(uri, true);
			}
			base = (BaseImpl)(schema.getTopLevelComponent(uri, localName, componentId));
		}
		if(base == null) {
			throw new SchemaComponentException("[location : " + schema.getLocation() + "] ERROR : Definition of " + DOM.toXPath(node) + " is not correct. Missing top level component (uri: '" + uri + "'; name: '" + localName + "').");
		}
    return(base);
  }
  
  public static String[] getNamespaceAndName(String qName, SchemaImpl schema, Node node) throws SchemaComponentException {
		String[] prefixAndLocalName = Tools.parseQName(qName);
    String prefix = prefixAndLocalName[0];
    String localName = prefixAndLocalName[1];
    String uri = null;
    if(prefix != null) {
			uri = prefix.equals(XML_ATTRIBUTES_PREFIX) ? XML_NAMESPACE : Tools.getUri(DOM.getNamespaceMappingsInScope(node), prefix);
      if(uri == null) {
				throw new SchemaComponentException("[location : " + schema.getLocation() + "] ERROR : Definition of qualified base " + DOM.toXPath(node) + " is not correct. Missing prefix mapping to prefix '" + prefix + "' in the scope node " + DOM.toXPath(node) + ".");
      }
    } else {
			uri = Tools.getDefaultUri(DOM.getNamespaceMappingsInScope(node));
    }
    return(new String[]{uri, localName});
  }
  
//  public static String canonicalizeLocation(String location) {
//    String result = null;
//    if(location != null) {
//      if(location.startsWith("http:")) {
//        return(canonicalizeHttpLocation(location));
//      } else if(location.startsWith("file:")) {
//        return(canonicalizeFileLocation(location, true));
//      } else if(location.startsWith("jar:")) {
//        return(canonicalizeJarLocation(location));
//      }
//      return(canonicalizeFileLocation(location, false));
//    }
//    return(null);
//  }
//  
//  private static String canonicalizeFileLocation(String location, boolean hasScheme) {
//    if(hasScheme) {
//      return(canonicalizeLocation("file", location, "/"));
//    }
//    return("file:/" + location);
//  }
//  
//  private static String canonicalizeHttpLocation(String location) {
//    return(canonicalizeLocation("http", location, "//"));
//  }
//  
//  private static String canonicalizeJarLocation(String location) {
//    return(canonicalizeLocation("jar", location, ""));
//  }
//  
//  private static String canonicalizeLocation(String scheme, String location, String schemeFromPathDelimiter) {
//    int index = scheme.length() + 1;
//    while(location.charAt(index++) == '/' && index < location.length());
//    String canonicalizaedLocation = scheme + ":" + schemeFromPathDelimiter + location.substring(index - 1);
//    return(canonicalizaedLocation.replace('\\', '/'));
//  }
}

