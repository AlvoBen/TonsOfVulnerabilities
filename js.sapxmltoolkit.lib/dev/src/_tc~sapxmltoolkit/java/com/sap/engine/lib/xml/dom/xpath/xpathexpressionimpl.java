package com.sap.engine.lib.xml.dom.xpath;

import org.w3c.dom.*;
//import org.w3c.dom.xpath.*;
import com.sap.engine.lib.xsl.xpath.XPathProcessor;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.ETBuilder;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.DTMFactory;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;
import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.util.DOMToDocHandler;


public class XPathExpressionImpl { //implements XPathExpression {
  private String expression = null;
  protected XPathProcessor xpp = null;
  protected XPathContext xpc = null;
  protected ETBuilder builder = new ETBuilder();
  protected DTM dtm = new DTM();
  protected DTMFactory dtmfact = new DTMFactory();
  protected DOMToDocHandler dtdh = new DOMToDocHandler();
  protected NamespaceHandler nshandler = null;
  protected NamespaceManager nsmanager = null;

  XPathExpressionImpl(String expression, XPathNSResolverImpl resolver) {
    this.expression = expression;
  }
  
  public XPathResultImpl evaluate(Node contextNode, short type, XPathResultImpl result) 
                              throws XPathException, DOMException{
    try { 

      Document owner;
      if (contextNode instanceof Document) {
        owner = (Document)contextNode;
      } else {
        owner = contextNode.getOwnerDocument();
      }

      //LogWriter.getSystemLogWriter().println("XpathResultImpl: Owner document is: " + owner);
      //LogWriter.getSystemLogWriter().println("XpathResultImpl: Expression is: " + expression);

      
      int[] ancestry = getAncestry(contextNode);
      //LogWriter.getSystemLogWriter().println("Ancestry is: ");
      //for (int i = 0; i < ancestry.length; i ++) {
      //  LogWriter.getSystemLogWriter().println(ancestry[i]);
      //}
      
      nshandler = new NamespaceHandler(null);
      nsmanager = new NamespaceManager();
      builder.setNamespaceStuff(nsmanager, nshandler);
      dtmfact.initialize(dtm, new NamespaceManager());
      //dtdh.process(contextNode, dtmfact); 
      dtdh.process(owner, dtmfact); 
      
      xpp = new XPathProcessor(dtm);
      xpc = dtm.getInitialContext();
      xpc.node = getNodeFromAncestry(dtm, ancestry);
//      LogWriter.getSystemLogWriter().println("XPathExpressionImpl: Context node is: " + xpc.node);
     
      
      
      //result = new XPathResultImpl(xpp.process(builder.process(expression), xpc));
      ((XPathResultImpl)result).reuse(xpp.process(builder.process(expression), xpc));
      //LogWriter.getSystemLogWriter().println(xpp.process(builder.process(expression), xpc));
      result.setOwnerDocument(owner);
      return result;
   
    } catch (com.sap.engine.lib.xsl.xpath.XPathException e) {
      e.printStackTrace();
      throw new XPathException((short)1, e.toString());
    } catch (Exception e) {
      e.printStackTrace();
      throw new XPathException((short)1, e.toString());
    }
    
  
  }
  
  static int[] getAncestry(DTM dtm, int i) {
    int count = 0;
    if (dtm.parent[i] < 0) {
      return new int[]{0};
    } 
    if (dtm.nodeType[i] == DTM.ATTRIBUTE_NODE) {
      int parentEl = dtm.parent[i];
      
      int[] attributes = new int[dtm.getAttributesAndNSEndIndex(parentEl) - dtm.getAttributesStartIndex(parentEl) + 1];
//      LogWriter.getSystemLogWriter().println("XPathExpressionImpl: attr.length = " + attributes.length);
      dtm.getAttributeAndNSNodes(parentEl, attributes, 0);
      for (int j = 0; j < attributes.length; j++) {
        count++;
        if (attributes[j] == i) {
          count = -count;
          break;
        }
//        LogWriter.getSystemLogWriter().println("XPathExpressionImpl: attr = " + dtm.name[attributes[j]]);
      }
      
    } else { 
      int temp = i;
      while (dtm.previousSibling[temp] >= 0) {
        temp = dtm.previousSibling[temp];
        count = count + 1;
      }
    }
    
    int[] upperBranch = getAncestry(dtm, dtm.parent[i]);
    int[] result = new int[upperBranch.length + 1];
    System.arraycopy(upperBranch, 0, result, 0, upperBranch.length);
    result[result.length - 1] = count;
    return result;   
  }
  
  
  static int[] getAncestry(Node n) {
    Node parent = n.getParentNode();
    if (parent == null) {
      return new int[]{0};
    }
    int count = 0;
    Node temp = n; 
    while (temp.getPreviousSibling() != null ) {
      temp = temp.getPreviousSibling();
      count = count + 1;
    }
    
    int[] upperBranch = getAncestry(parent);
    int[] result = new int[upperBranch.length + 1];
    System.arraycopy(upperBranch, 0, result, 0, upperBranch.length);
    result[result.length - 1] = count;
    return result;   
  }
  
  static int getNodeFromAncestry(DTM dtm, int[] ancestry) {
    int rootNode = 0;
    int depth = ancestry.length;
    int next = rootNode;
    //LogWriter.getSystemLogWriter().println("XPathExpressionImpl: depth is " + depth);
    
    
    for (int i = 0; i < depth - 1; i ++ ) {
      //LogWriter.getSystemLogWriter().println("XPathExpressionImp: At depth " + i + " the offset is " + ancestry[i]);
      for (int j = 0; j < ancestry[i]; j ++) {
        next = dtm.nextSibling[next];
      }
      next = dtm.firstChild[next];
    }
    //LogWriter.getSystemLogWriter().println("XPathExpressionImp: At depth " + (ancestry.length - 1) + " the offset is " + ancestry[ancestry.length - 1]);
    if (ancestry.length > 0) {
      for (int i = 0; i < ancestry[ancestry.length - 1]; i ++) {
        next = dtm.nextSibling[next];
      }
    }
    
    return next;
  }
  
  static Node getNodeFromAncestry(Document doc, int[] ancestry ) {
//    LogWriter.getSystemLogWriter().println("XPathExpressionImpl: Document is: " + doc);
//    LogWriter.getSystemLogWriter().println("XPathExpressionImpl: Ancestry is: ");
//      for (int i = 0; i < ancestry.length; i ++) {
//        LogWriter.getSystemLogWriter().println(ancestry[i]);
//      }

    Node rootNode = doc;
    int depth = ancestry.length;
    Node next = rootNode;
    
    for (int i = 0; i < depth - 1; i ++) {
      for (int j = 0; j < ancestry[i]; j++) {
        next = next.getNextSibling();
      }
      if (i < depth - 2) {
        next = getFirstChild(next);
      }
    }
    
     if (ancestry[ancestry.length - 1] >= 0) {
        next = getFirstChild(next);
        for (int j = 0; j < ancestry[ancestry.length - 1]; j++) {
          next = next.getNextSibling();
        }
        return next;
      } else {
        NamedNodeMap map = next.getAttributes();
        return map.item(-ancestry[ancestry.length - 1] - 1);
      }
  }
  
  private static Node getFirstChild(Node node) {
    return(node.getNodeType() == Node.DOCUMENT_NODE ? ((Document)node).getDocumentElement() : node.getFirstChild());
  }
}
