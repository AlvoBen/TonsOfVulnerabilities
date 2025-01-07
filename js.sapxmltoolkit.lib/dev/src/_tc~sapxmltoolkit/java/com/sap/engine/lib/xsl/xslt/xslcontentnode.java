package com.sap.engine.lib.xsl.xslt;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.dom.TextImpl;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;

/**
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 */
public abstract class XSLContentNode extends XSLNode {

  public XSLContentNode(XSLStylesheet owner, XSLNode parent) throws XSLException {
    super(owner, parent);
  }

  public XSLContentNode(XSLStylesheet owner, XSLNode parent, Node content) throws XSLException {
    super(owner, parent, content);
    if (content != null) {
      scanContent(content);
    }
  }

  public void init(Element el) throws XSLException {

  }

  public void scanContent(Node cont) throws XSLException {
    NodeList nl = cont.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      scanNode(nl.item(i));
    } 
  }

  public void scanNode(Node node) throws XSLException {
    switch (node.getNodeType()) {
      case Node.TEXT_NODE: {
        if (!TextImpl.isWhiteSpace(((Text) node).getData())) {
          append(new XSLText(owner, this, ((Text) node).getData()));
        }

        break;
      }
      case Node.ELEMENT_NODE: {
        Element el = (Element) node;
        owner.getNamespaceHandler().findNamespaceNodes(el, owner.getNamespaceManager());
        owner.getNamespaceHandler().levelUp();

        //if (el.getNodeName().startsWith("xsl:")) {
        if (el.getNamespaceURI() != null && el.getNamespaceURI().equals(XSLStylesheet.sXSLNamespace)) {
          String name = el.getLocalName();
          if (name == null) {
            name = el.getNodeName();
          }

          if (name.equals("apply-templates")) {
            append(new XSLApplyTemplates(owner, this, el));
          } else if (name.equals("value-of")) {
            append(new XSLValueOf(owner, this, el));
          } else if (name.equals("element")) {
            append(new XSLElement(owner, this, el));
          } else if (name.equals("call-template")) {
            append(new XSLCallTemplate(owner, this, el));
          } else if (name.equals("choose")) {
            append(new XSLChoose(owner, this, el));
          } else if (name.equals("comment")) {
            append(new XSLComment(owner, this, el));
          } else if (name.equals("copy")) {
            append(new XSLCopy(owner, this, el));
          } else if (name.equals("copy-of")) {
            append(new XSLCopyOf(owner, this, el));
          } else if (name.equals("fallback")) {
            append(new XSLFallback(owner, this, el));
          } else if (name.equals("for-each")) {
            append(new XSLForEach(owner, this, el));
          } else if (name.equals("if")) {
            append(new XSLIf(owner, this, el));
          } else if (name.equals("message")) {
            append(new XSLMessage(owner, this, el));
          } else if (name.equals("otherwise")) {
            append(new XSLOtherwise(owner, this, el));
          } else if (name.equals("param")) {
            append(new XSLParam(owner, this, el));
          } else if (name.equals("processing-instruction")) {
            append(new XSLProcessingInstruction(owner, this, el));
          } else if (name.equals("sort")) {
            append(new XSLSort(owner, this, el));
          } else if (name.equals("text")) {
            append(new XSLText(owner, this, el));
          } else if (name.equals("variable")) {
            append(new XSLVariable(owner, this, el));
          } else if (name.equals("when")) {
            append(new XSLWhen(owner, this, el));
          } else if (name.equals("with-param")) {
            append(new XSLWithParam(owner, this, el));
          } else if (name.equals("attribute")) {
            append(new XSLAttribute(owner, this, el));
          } else if (name.equals("number")) {
            append(new XSLNumber(owner, this, el));
          } else if (name.equals("document")) {
            append(new XSLDocument(owner, this, el));
          } else if (name.equals("result")) {
            append(new XSLResult(owner, this, el));
          }
        } else {
          append(new XSLElement(owner, this, el));
        }

        try {
          owner.getNamespaceHandler().levelDown();
        } catch (Exception e) {
          throw new XSLException("Error whileprocessing namespaces for node '" + el.getNodeName() + "'", e);
        }
        break;
      }
      case Node.COMMENT_NODE: {
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        break;
      }
      case Node.CDATA_SECTION_NODE: {
        append(new XSLText(owner, this, ((Text) node).getData(), false));
        break;
      }
      default: {
        throw new XSLException("Not Supported: " + node.getNodeName());
      }
    }
  }

  public void process(XPathContext xcont, int node) throws XSLException, XPathException {
    processFromFirst(xcont, node);
//    if (getFirst() != null) {
//      getFirst().process(xcont, node);
//    }
//
//    if (getNext() != null) {
//      getNext().process(xcont, node);
//    }
  }

  public void print(String ind) {
    LogWriter.getSystemLogWriter().println(ind + "#XSLContentNode: child:");

    if (getFirst() != null) {
      getFirst().print(ind + "  ");
    }

    if (getNext() != null) {
      getNext().print(ind);
    }
  }

}

