/*
 * Created on 2004-4-30
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xsl.xpath;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.dom.AttrImpl;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class DTMDOMBuilder {

  private DTM dtm;
  /**
   * @return Returns the dtm.
   */
  public DTM getDTM() {
    return dtm;
  }
  /**
   * @param dtm The dtm to set.
   */
  public void setDTM(DTM dtm) {
    this.dtm = dtm;
  }
  /**
   * 
   */
  public DTMDOMBuilder(DTM dtm) {
    super();
    this.dtm = dtm;
    // TODO Auto-generated constructor stub
  }
  
  private boolean[] initialized = null;
  private static final String[] empty = new String[] {};// an empty String array
  // when part of the DOM tree is initialized there could be some nodes that have all thier ancestors
  // excluded form the tree - these are detected, marked in this array and later appended to the DocumentFragment node
  private boolean[] dangling = null;
  private boolean domInitialized = false;
  private DocumentImpl doc = null;
  private Node frag = null;
  
  private int danglingLength = 0;
  
  
  public Node[] domtree = null;
  public Document getDocument() {
    initializeDOM();
    return doc;
  }
  public Node getFragment() {
    return frag;
  }
  public void reinitializeDOM(int[] $excluding) {
    reinitializeDOM($excluding, false, empty, false);
  }
  public void reinitializeDOM(int[] $excluding, String[] specialNamespaces) {
    reinitializeDOM($excluding, false, specialNamespaces, false);
  }
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces) {
    reinitializeDOM($excluding, retainParentNamespaces, null, false);
  }
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces, String[] specialNamespaces) {
    reinitializeDOM($excluding, retainParentNamespaces, specialNamespaces, false);
  }
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces, String[] specialNamespaces, boolean $visibleOnly) {
    	retain = retainParentNamespaces;
      doc = new DocumentImpl();
      domtree = new Node[dtm.size];
      dangling = new boolean[dtm.size];
      initialized = new boolean[dtm.size];
      excluding = $excluding;
      inclusive = !$visibleOnly; 
      specialPrefixes = specialNamespaces;
      int tempIndex = (excluding.length==0)||(excluding[0]>0)?0:1;
      danglingLength = 0;
      for (int i = 1; i < domtree.length; i++) {
  //      domtree[i] = subtree(i, null/*retainParentNamespaces, specialNamespaces, visibleOnly*/,null);
        if ((tempIndex>=excluding.length)||(i<excluding[tempIndex])){
  // node must be build - it is not in the excluding set
          domtree[i] = subtree(i, null, null, null);
  //      node must be added to document fragment        
          dangling[i] = isDangling(i, excluding);
        }
        else {
  // node may not be build - it is in the excluding set, only namespace must be dragged if inclusive        
          subtreeEx(i, null, null, null, null);
          tempIndex++;
        }
      } 
  
      int i = 1;
      tempIndex = (excluding.length==0)||(excluding[0]>0)?0:1;
  // flag indicating if i is in the excluding list    
      boolean found = false;
      for (i = 1; i < dtm.size; i++) {
        if ((tempIndex>=excluding.length)||(i<excluding[tempIndex])){
          found = false;
        }  else {
          found = true; // i increases every step by 1 - so it will eventually reach excluding[tempIndex]
          tempIndex++;
        }      
        if ((!found)&&(domtree[i]!=null)&&(domtree[i].getNodeType() == Node.TEXT_NODE)) {
  // this text node is to be added - it was not found in the excluding list        
          dtm.getLeadingText().append(dtm.stringValue[i].getData(),dtm.stringValue[i].getOffset(),dtm.stringValue[i].getSize());
          dangling[i]=false;
          danglingLength--;
        }
  
        if (found){
  // index found in the excluding list - try next        
          continue;
        }
  
        break;
      } 
  // no root element found! - only leading text
      if (i >= domtree.length) {
        return;
      }
  
  //    doc.appendChild(domtree[i]);
      
      frag = doc.createDocumentFragment();
  //    frag.appendChild(do)
      if (danglingLength >= 1) {
        for (int j = i; j < dtm.size; j++) {
          if (dangling[j]){
            frag.appendChild(domtree[j]);
          }
        } 
      } else {
        frag = doc;
      }
  
      domtree[0] = doc;
      domInitialized = true;
    }
  public void initializeDOM() {
    //    LogWriter.getSystemLogWriter().println("DTM.initializeDOM: "+ domInitialized + ", size = " + size);
    if (!domInitialized) {
      doc = new DocumentImpl();
      domtree = new Node[dtm.size];
      initialized = new boolean[dtm.size];
      excluding = new int[] {};
      specialPrefixes = empty;
      inclusive = true;
      for (int i = 1; i < domtree.length; i++) {
        domtree[i] = subtree(i, null/*false, empty, false*/, null, null);
      } 
  
      int i = 1;
      for (; i < domtree.length && domtree[i].getNodeType() == Node.TEXT_NODE; i++) {
        ; 
      }
      doc.appendChild(domtree[i]);
      domtree[0] = doc;
      domInitialized = true;
    }
  }
  private boolean isDangling(int node, int[] excludeList) {
    int father = dtm.parent[node];
  
    if (dtm.nodeType[father] != Node.DOCUMENT_NODE) {
      if (Arrays.binarySearch(excludeList, father) >= 0) {
        return isDangling(father, excludeList);
      } else {
        return false;
      }
    } else {
      danglingLength++;
      return true;
    }
  }
  private int[] excluding = null;
  private String[] specialPrefixes = null;
  private boolean inclusive = true;
  private Hashtable hiddenXML = new Hashtable();
  private Hashtable ownXMLAttribs = new Hashtable();
  boolean retain = true;
  public Node subtree(int index, Hashtable namespaces, Hashtable usedNamespaces, Hashtable xmlAttributes) {
      if (initialized[index]) {
        return domtree[index];
      }
  
      Node n;
      switch (dtm.nodeType[index]) {
        case Node.TEXT_NODE: {
          n = doc.createTextNode(dtm.getStringValue(index).toString());
          break;
        }
        case Node.ATTRIBUTE_NODE: {
          n = doc.createAttributeNS(dtm.name[index].uri.toString(), dtm.name[index].rawname.toString());
          ((AttrImpl) n).setValue(dtm.getStringValue(index).toString());
          break;
        }
        case Node.PROCESSING_INSTRUCTION_NODE: {
          n = doc.createProcessingInstruction(dtm.name[index].rawname.toString(), dtm.getStringValue(index).toString());
          break;  
        }
        case DTM.NAMESPACE_NODE: {
          n = null;
          break;
        }
        case Node.COMMENT_NODE: {
          n = doc.createComment(dtm.getStringValue(index).toString());
          break;
        }
        case Node.CDATA_SECTION_NODE: {
          n = doc.createCDATASection(dtm.getStringValue(index).toString());
          break;
        }
  
        case Node.ELEMENT_NODE: {
          boolean notCloned1 = true;
          hiddenXML.clear();
          ownXMLAttribs.clear();
  // namespaces which are used in this node
  // only namespaces which are used in this node and not defined in the output ancesstors or are overriden are
  // set in the namespace axis
          usedNamespaces = usedNamespaces==null?new Hashtable():usedNamespaces;
          n = doc.createElementNS(dtm.name[index].uri.toString(), dtm.name[index].rawname.toString());
          CharArray prefix = dtm.name[index].prefix;
          usedNamespaces.put(((prefix==null)||(prefix.length()==0))?SignatureContext.DEFAULT:prefix, dtm.name[index].uri==null?CharArray.EMPTY:dtm.name[index].uri);
          int x = index + 1;
          int i2 = Arrays.binarySearch(excluding, x); 
          if (i2<0){i2=~i2;}
  // i2 - first not excluded node index in the excluding list
          while ((x < dtm.size)) {
          
            if ((i2<excluding.length)&&(excluding[i2]<x)){
              i2++;
            } else if ((i2<excluding.length)&&(excluding[i2]==x)){
              // do not build excluded elements 
              //TODO:fix for xml namespace elements
              QName q1 = dtm.name[x];
              if ((dtm.nodeType[x] == Node.ATTRIBUTE_NODE)&&SignatureContext.XML.equals(q1.prefix)&&inclusive) {
                Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
                ai.setValue(dtm.getStringValue(x).toString());
                if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!dtm.stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                  if (notCloned1){
                    if (xmlAttributes==null){
                      xmlAttributes = new Hashtable();
                    } else {
                      xmlAttributes = (Hashtable) xmlAttributes.clone();
                    }
                    notCloned1 = false;
                  }
                  xmlAttributes.put(q1.localname, ai);
                }     
                hiddenXML.put(q1.localname, ai);
              }
              domtree[x]=null;
              initialized[x]=true;
              i2++;
              x++;
            } else {
                if (dtm.nodeType[x] != Node.ATTRIBUTE_NODE) {
                  if (dtm.nodeType[x] == DTM.NAMESPACE_NODE) {
  // if this is inclusive canonicalization or this namespace is in inclusive namespaces
                    CharArray tempPrefix = dtm.name[x].localname;
                    if ((tempPrefix==null)||(tempPrefix.length()==0)){
                      tempPrefix = SignatureContext.DEFAULT;
                    }
                    if (retain||inclusive|| ((specialPrefixes!=null)&& (Arrays.binarySearch(specialPrefixes, tempPrefix.toString()) >= 0))) {                  
                      usedNamespaces.put(tempPrefix, dtm.stringValue[x]);
                    }
                  } else if (dtm.nodeType[x]== Node.COMMENT_NODE){
  // creates comments in the element, such as <foo <!-- comment --> />                  
                    Comment comment = doc.createComment(dtm.getStringValue(index).toString());
                    ((Element) n).appendChild(comment);
                    domtree[x] = comment;
                  } else {
  // only attribute nodes belonging to this parent are searched              
                    break;
                  }
              } else {
                QName q1 = dtm.name[x];
  //              if (q1.localname.equals("xmlns")){
  ////TODO: Never used
  //// if this is inclusive canonicalization or this namespace is in inclusive namespaces                                  
  //                if (inclusive|| ((specialPrefixes!=null)&& (Arrays.binarySearch(specialPrefixes, "#default") >= 0))) {                  
  //                  usedNamespaces.put(DEFAULT, stringValue[x]);
  //                }                
  //              } else
                if (q1.uri != null) {
  
                  Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
                  ai.setValue(dtm.getStringValue(x).toString());
  //TODO: uri comparison!                
                  if (SignatureContext.XML.equals(q1.prefix)&&inclusive){
                    if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!dtm.stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                      if (notCloned1){
                        if (xmlAttributes==null){
                          xmlAttributes = new Hashtable();
                        } else {
                          xmlAttributes = (Hashtable) xmlAttributes.clone();
                        }
                        notCloned1 = false;
                      }
                      xmlAttributes.put(q1.localname, ai);
                      ownXMLAttribs.put(q1.localname, ai);
                    }               
                  } else {
                    ((Element) n).setAttributeNodeNS(ai);
                  }
                  domtree[x]=ai;
                  if (((q1.prefix!=null) && (q1.prefix.length()!=0))){
                    usedNamespaces.put(q1.prefix, q1.uri);
                  }
                } else {                
                  Attr ai = doc.createAttribute(q1.rawname.toString());
                  ai.setValue(dtm.getStringValue(x).toString());
                  ((Element) n).setAttributeNode(ai);
                  domtree[x] = ai;
  //                usedNamespaces.put(DEFAULT, CharArray.EMPTY);
                }
              }
                    initialized[x] = true;
                    x++;              
            }
          }
          
          Enumeration newNamespaces = usedNamespaces.keys();
  //namespace addition
          while (newNamespaces.hasMoreElements()) {
            CharArray nextPrefix = (CharArray) newNamespaces.nextElement();
            CharArray nextValue = namespaces==null?null:(CharArray) namespaces.get(nextPrefix);
            CharArray currentValue = (CharArray) usedNamespaces.get(nextPrefix);
            if(!currentValue.equals(nextValue)){
              Attr at = SignatureContext.DEFAULT.equals(nextPrefix)?doc.createAttribute("xmlns"):doc.createAttribute("xmlns:".concat(nextPrefix.toString()));
              at.setValue(currentValue.toString());
              ((Element) n).setAttributeNode(at);
            }
          }
  
  //      adding attributes from xml namespace        
          if (xmlAttributes!=null){
              if (domtree[dtm.parent[index]]==null){ // check excluded list
                Enumeration enum1 = xmlAttributes.keys();
                while(enum1.hasMoreElements()){
                  CharArray key = (CharArray) enum1.nextElement();
                  if (!hiddenXML.containsKey(key)){
                      Attr atr = (Attr) ((Attr) xmlAttributes.get(key)).cloneNode(true);
                      ((Element) n).setAttributeNodeNS(atr);
                  }
                }
              } else {
                Enumeration enum1 = ownXMLAttribs.keys();
                while(enum1.hasMoreElements()){
                  CharArray key = (CharArray) enum1.nextElement();
                  Attr atr = (Attr) ((Attr) ownXMLAttribs.get(key)).cloneNode(true);
                  ((Element) n).setAttributeNodeNS(atr);
                 }                
              }
          }
          
  // overriding namespaces declaration for children - may optimize if they are the same!        
          if (namespaces != null){
            Enumeration enum1 = namespaces.keys();
            while (enum1.hasMoreElements()){
              Object key = enum1.nextElement();
              if (usedNamespaces.get(key)==null){
                usedNamespaces.put(key, namespaces.get(key));
              }
            }
          }
          
          namespaces = usedNamespaces;
          int i1 = dtm.firstChild[index];
          domtree[index] = n;
          while (i1 != DTM.NONE) {
            if (Arrays.binarySearch(excluding, i1) < 0) {
              n.appendChild(subtree(i1, namespaces, null, xmlAttributes));
            } else {
              subtreeEx(i1, namespaces, n,null, xmlAttributes);
            }
            i1 = dtm.nextSibling[i1];
          }         
          break;
        }
          
        default: {
  // unknown node type          
          return (Node) (doc.createTextNode(""));
          
        }
      }
      
      domtree[index] = n;
      initialized[index] = true;
      return n;
    }
  public void subtreeEx(int index, Hashtable namespaces, Node outputAncesstor, Hashtable usedNamespaces, Hashtable xmlAttributes){
      if (dtm.nodeType[index] == Node.ELEMENT_NODE){
  // drag parent namespaces in inclusive canonicalization      
        if (inclusive) {
          boolean notCloned = true;
          boolean notCloned1 = true;
          int x = index + 1;
          while ((x < dtm.size)) {
            
              if (dtm.nodeType[x] == DTM.NAMESPACE_NODE) {
                CharArray tempPrefix = dtm.name[x].localname;
                if ((tempPrefix==null)||(tempPrefix.length()==0)){
                  tempPrefix = SignatureContext.DEFAULT;
                }
  // this namespace declaration is not in output ancestors' namespace axis              
                if (((namespaces==null)||(!dtm.stringValue[x].equals(namespaces.get(tempPrefix))))&&
                    ((usedNamespaces==null)||(!dtm.stringValue[x].equals(usedNamespaces.get(tempPrefix))))){
                  if (notCloned){
                    if (usedNamespaces==null){
                      usedNamespaces = new Hashtable();
                    } else {
                      usedNamespaces = (Hashtable) usedNamespaces.clone();
                    }
                    notCloned = false;
                  }
                  usedNamespaces.put(tempPrefix, dtm.stringValue[x]);  
                }
                domtree[x] = null;
              } else if (dtm.nodeType[x] == Node.ATTRIBUTE_NODE){
              QName q1 = dtm.name[x];
              if (q1.localname.equals("xmlns")) {
  // TODO: never used!              
                if (((namespaces==null)||(!dtm.stringValue[x].equals(namespaces.get(SignatureContext.DEFAULT))))&&
                    ((usedNamespaces==null)||(!dtm.stringValue[x].equals(usedNamespaces.get(SignatureContext.DEFAULT))))){
                  if (notCloned){
                    if (usedNamespaces==null){
                      usedNamespaces = new Hashtable();
                    } else {
                      usedNamespaces = (Hashtable) usedNamespaces.clone();
                    }
                    notCloned = false;
                  }
                  usedNamespaces.put(SignatureContext.DEFAULT, dtm.stringValue[x]);  
                }
                domtree[x] = null;
              }
              
              if (SignatureContext.XML.equals(q1.prefix)){
                if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!dtm.stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                  if (notCloned1){
                    if (xmlAttributes==null){
                      xmlAttributes = new Hashtable();
                    } else {
                      xmlAttributes = (Hashtable) xmlAttributes.clone();
                    }
                    notCloned1 = false;
                  }
                  Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
                  ai.setValue(dtm.getStringValue(x).toString());                
                  xmlAttributes.put(q1.localname, ai);
                  domtree[x] = ai;
                }               
              }
  //          TODO: add attributes from xml namespace            
            } else if (dtm.nodeType[x] != Node.COMMENT_NODE){
              break;  
            }
            
            initialized[x++]=true;
          }
        } else if (retain) {
          boolean notCloned = true;
          int x = index + 1;
          while ((x < dtm.size)) {
            
              if (dtm.nodeType[x] == DTM.NAMESPACE_NODE) {
                CharArray tempPrefix = dtm.name[x].localname;
                if ((tempPrefix==null)||(tempPrefix.length()==0)){
                  tempPrefix = SignatureContext.DEFAULT;
                }
  // this namespace declaration is not in output ancestors' namespace axis              
                if (((namespaces==null)||(!dtm.stringValue[x].equals(namespaces.get(tempPrefix))))&&
                    ((usedNamespaces==null)||(!dtm.stringValue[x].equals(usedNamespaces.get(tempPrefix))))){
                  if (notCloned){
                    if (usedNamespaces==null){
                      usedNamespaces = new Hashtable();
                    } else {
                      usedNamespaces = (Hashtable) usedNamespaces.clone();
                    }
                    notCloned = false;
                  }
                  usedNamespaces.put(tempPrefix, dtm.stringValue[x]);  
                }
                domtree[x] = null;
              } else if (dtm.nodeType[x] == Node.ATTRIBUTE_NODE){
              QName q1 = dtm.name[x];
              if (q1.localname.equals("xmlns")) {
  // TODO: never used!              
                if (((namespaces==null)||(!dtm.stringValue[x].equals(namespaces.get(SignatureContext.DEFAULT))))&&
                    ((usedNamespaces==null)||(!dtm.stringValue[x].equals(usedNamespaces.get(SignatureContext.DEFAULT))))){
                  if (notCloned){
                    if (usedNamespaces==null){
                      usedNamespaces = new Hashtable();
                    } else {
                      usedNamespaces = (Hashtable) usedNamespaces.clone();
                    }
                    notCloned = false;
                  }
                  usedNamespaces.put(SignatureContext.DEFAULT, dtm.stringValue[x]);  
                }
                domtree[x] = null;
              }
            } else if (dtm.nodeType[x] != Node.COMMENT_NODE){
              break;  
            }
            
            initialized[x++]=true;
          }
        }
        
        
        int i1 = dtm.firstChild[index];
    //TODO: see fix!
    // xml: attribs handling      
        while (i1 != DTM.NONE) {
          if (Arrays.binarySearch(excluding, i1) < 0) {
            if (outputAncesstor!=null){
              outputAncesstor.appendChild(subtree(i1, namespaces, usedNamespaces, xmlAttributes));
            } else {
              subtree(i1, namespaces, usedNamespaces, xmlAttributes);
            }
          } else {
            subtreeEx(i1, namespaces, outputAncesstor, usedNamespaces, xmlAttributes);
          }
          i1 = dtm.nextSibling[i1];
        }         
      }
      domtree[index] = null;
      initialized[index] = true;
    }
}
