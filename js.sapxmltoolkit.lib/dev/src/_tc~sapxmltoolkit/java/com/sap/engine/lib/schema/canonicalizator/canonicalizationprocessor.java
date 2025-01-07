package com.sap.engine.lib.schema.canonicalizator;

import org.w3c.dom.*;

import java.io.*;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Enumeration;

import com.sap.engine.lib.schema.exception.CanonicalizationException;
import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.structures.SchemaImpl;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.canonicalizator.helpers.SSCAnnotation;
import com.sap.engine.lib.schema.canonicalizator.helpers.Triple;
import com.sap.engine.lib.schema.canonicalizator.helpers.NSAttribStructure;
import com.sap.engine.lib.xml.dom.NodeImpl;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.dom.ElementImpl;
import com.sap.engine.lib.xml.dom.TextImpl;
import com.sap.engine.lib.xml.util.NS;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-3-5
 * Time: 16:29:23
 * To change this template use Options | File Templates.
 */
public class CanonicalizationProcessor implements Constants {

  private int normalizedNamespaceAttribIndex;
  private OutputStream xmlOutput;
  private Writer xmlWriter;
  private Element docElement;

  private static int COMPARE_LESS = 0;
  private static int COMPARE_EQUAL = 1;
  private static int COMPARE_GREATER = 2;

  protected CanonicalizationProcessor(Element docElement, OutputStream xmlOutput) {
    this(docElement);
    this.xmlOutput = xmlOutput;
  }

  protected CanonicalizationProcessor(Element docElement, Writer xmlWriter) {
    this(docElement);
    this.xmlWriter = xmlWriter;
  }

  private CanonicalizationProcessor(Element docElement) {
    this.docElement = docElement;
    normalizedNamespaceAttribIndex = 0;
  }

  protected void process() throws CanonicalizationException {
    process((ElementImpl)docElement, new Hashtable(), new Hashtable());
  }

  private void process(ElementImpl element, Hashtable parentInScopeNSesCollector, Hashtable parentInScopeNormNSAttribsCollector) throws CanonicalizationException {
    InfoItemDeclarationBase elementDeclaration = (InfoItemDeclarationBase)(element.getAugmentation(AUG_ELEMENT_DECLARATION));
    TypeDefinitionBase elementTypeDefBase = null;
    TypeDefinitionBase elementMemberTypeDefBase = null;
    SSCAnnotation elementSscAnnotation = null;
    String embededLanguage = null;
    Hashtable inScopeNSesCollector = new Hashtable();
    inScopeNSesCollector.putAll(parentInScopeNSesCollector);
    Vector attribsCollector = new Vector();
    Vector nsAttribsCollector = new Vector();
    Vector attribsSSCAnnotationsCollector = new Vector();
    Hashtable normNSAttribsCollector = null;
    Hashtable inScopeNormNSAttribsCollector = null;

    if(elementDeclaration != null) {
      elementSscAnnotation = new SSCAnnotation();
      elementTypeDefBase = (TypeDefinitionBase)(element.getAugmentation(AUG_TYPE_DEFINITION));
      elementMemberTypeDefBase = (TypeDefinitionBase)(element.getAugmentation(AUG_MEMBER_TYPE_DEFINITION));
      initSSCAnnotation_InfoItemDeclarationBase(elementSscAnnotation, elementDeclaration, elementTypeDefBase, elementMemberTypeDefBase);
    }

    NamedNodeMap namedNodeMap = element.getAttributes();
    for(int i = 0; i < namedNodeMap.getLength(); i++) {
      NodeImpl attrib = (NodeImpl)(namedNodeMap.item(i));
      String namespace = determineUri(attrib);
      String value = attrib.getNodeValue();
      String name = attrib.getLocalName();
      if(!namespace.equals(SCHEMA_INSTANCE_COMPONENTS_NS)) {
        if(isNamespaceAttribute(attrib)) {
          add_Sorted(nsAttribsCollector, attrib);
          String localName = attrib.getLocalName();
          inScopeNSesCollector.put(localName.equals("xmlns") ? "" : localName, attrib);
        } else {
          InfoItemDeclarationBase attributeDeclaration = (InfoItemDeclarationBase)(attrib.getAugmentation(AUG_ATTRIBUTE_DECLARATION));
          if(attributeDeclaration != null) {
            SSCAnnotation attributeSscAnnotation = new SSCAnnotation();
            TypeDefinitionBase attributeTypeDefBase = (TypeDefinitionBase)(attrib.getAugmentation(AUG_TYPE_DEFINITION));
            TypeDefinitionBase attributeMemberTypeDefBase = (TypeDefinitionBase)(attrib.getAugmentation(AUG_MEMBER_TYPE_DEFINITION));
            initSSCAnnotation_InfoItemDeclarationBase(attributeSscAnnotation, attributeDeclaration, attributeTypeDefBase, attributeMemberTypeDefBase);

            String attribEmbeddedLang = attributeSscAnnotation.getEmbededLang();
            if(attribEmbeddedLang == null && isQNameEmbeddedLang(attributeTypeDefBase)) {
              attribEmbeddedLang = SCC_QNAME_EMBEDDED_LANG_NAMESPACE;
            }
            namespacePrefixDesensitization(attrib, attribEmbeddedLang);
            attribsSSCAnnotationsCollector.add(attributeSscAnnotation);
          }

          add_Sorted(attribsCollector, attrib);
          if(namespace.equals(SCC_NAMESPACE) && name.equals(SCC_EMBEDDED_LANG_ATTRIB_NAME)) {
            embededLanguage = value;
          }
        }
      }
    }

    if(elementDeclaration != null) {
      if(embededLanguage == null) {
        if(elementSscAnnotation.getEmbededLangAttributeName() != null) {
          String attribUri = elementSscAnnotation.getEmbededLangAttributeUri();
          String attribName = elementSscAnnotation.getEmbededLangAttributeName();
          if(LexicalParser.parseNCName(attribName) == null) {
            throw new CanonicalizationException("Attribute name '" + attribName + "' must be a valid ncname.");
          }
          Attr embeddedLangAttr = element.getAttributeNode(attribName);
          if(embeddedLangAttr == null || !determineUri((NodeImpl)embeddedLangAttr).equals(attribUri)) {
            throw new CanonicalizationException("Missing embeded language attribute {" + attribUri + "}:" + attribName + ".");
          }
          embededLanguage = embeddedLangAttr.getValue();

        } else if(elementSscAnnotation.getEmbededLang() != null) {
          embededLanguage = elementSscAnnotation.getEmbededLang();
        } else if(isQNameEmbeddedLang(elementTypeDefBase)) {
          embededLanguage = SCC_QNAME_EMBEDDED_LANG_NAMESPACE;
        }
      }

      namespacePrefixDesensitization(element, embededLanguage);

      normNSAttribsCollector = new Hashtable();
      namespaceAttributeNormalization(element, attribsCollector, inScopeNSesCollector, parentInScopeNormNSAttribsCollector, normNSAttribsCollector);
      inScopeNormNSAttribsCollector = new Hashtable();
      inScopeNormNSAttribsCollector.putAll(parentInScopeNormNSAttribsCollector);
      inScopeNormNSAttribsCollector.putAll(normNSAttribsCollector);

      dataTypeCanonicalization(element, elementSscAnnotation, elementTypeDefBase);
    }

    serializeElement_Start(element, false);

    boolean wildcardOutputRoot = wildcardOutputRoot(element, attribsCollector);
    boolean wildcarded = wildcarded(element);
    if(elementDeclaration != null) {
      Vector normNSAttribsVectorCollector = createSortedVector(normNSAttribsCollector);
      for(int i = 0; i < normNSAttribsVectorCollector.size(); i++) {
        serializeNormNSAttribStructure((NSAttribStructure)(normNSAttribsVectorCollector.get(i)));
      }
    }
    if(wildcardOutputRoot) {
      Vector inScopeNsAttribsCollector = createSortedVector(inScopeNSesCollector);
      for(int i = 0; i < inScopeNsAttribsCollector.size(); i++) {
        serializeNSAttrib((NodeImpl)(inScopeNsAttribsCollector.get(i)));
      }
    } else if(wildcarded) {
      for(int i = 0; i < nsAttribsCollector.size(); i++) {
        serializeNSAttrib((NodeImpl)(nsAttribsCollector.get(i)));
      }
    }
    for(int i = 0; i < attribsCollector.size(); i++) {
      NodeImpl attrib = (NodeImpl)(attribsCollector.get(i));
      InfoItemDeclarationBase attributeDeclaration = (InfoItemDeclarationBase)(attrib.getAugmentation(AUG_ATTRIBUTE_DECLARATION));
      TypeDefinitionBase attributeTypeDefBase = (TypeDefinitionBase)(attrib.getAugmentation(AUG_TYPE_DEFINITION));
      if(attributeDeclaration != null) {
        namespaceAttributeNormalization(attrib, null, inScopeNSesCollector, inScopeNormNSAttribsCollector, new Hashtable());
        dataTypeCanonicalization(attrib, (SSCAnnotation)(attribsSSCAnnotationsCollector.get(i)), attributeTypeDefBase);
      }
      serializeAttribute(attrib);
    }

    serializeElement_Start(element, true);

    //serializeValue(element);

    NodeList nodeList = element.getChildNodes();
    ModelGroup processingModelGroup = null;
    Vector processingModelGropuElemsCollector = null;
    for(int i = 0; i < nodeList.getLength(); i++) {
      Node childNode = nodeList.item(i);
      if(childNode instanceof Element) {
        ElementImpl childElement = (ElementImpl)childNode;
        ModelGroup modelGroup = (ModelGroup)(childElement.getAugmentation(AUG_VALIDATING_MODEL_GROUP_ALL));
        if(modelGroup == null) {
          if(processingModelGropuElemsCollector != null) {
            processProcessingModelGroupElements(processingModelGropuElemsCollector, inScopeNSesCollector, inScopeNormNSAttribsCollector);
            processingModelGropuElemsCollector = null;
          }
          process(childElement, inScopeNSesCollector, inScopeNormNSAttribsCollector);
        } else {
          if(processingModelGroup == null) {
            processingModelGroup = modelGroup;
            processingModelGropuElemsCollector = new Vector();
          } else if(processingModelGroup != modelGroup) {
            if(processingModelGropuElemsCollector != null) {
              processProcessingModelGroupElements(processingModelGropuElemsCollector, inScopeNSesCollector, inScopeNormNSAttribsCollector);
            }
            processingModelGropuElemsCollector = new Vector();
          }
          add_Sorted(processingModelGropuElemsCollector, childElement);
        }
      } else if(childNode instanceof TextImpl) {
        String value = childNode.getNodeValue();
        if(elementTypeDefBase instanceof ComplexTypeDefinition) {
          ComplexTypeDefinition elementComplTypeDef = (ComplexTypeDefinition)elementTypeDefBase;
          SimpleTypeDefinition contentSimpleTypeDef = elementComplTypeDef.getContentTypeSimpleTypeDefinition();
          if(elementComplTypeDef.isMixed() && contentSimpleTypeDef == null) {
            String mixContentValue = createMixContentValue(value);
            if(mixContentValue != null) {
              write(mixContentValue);
            }
          } else if(contentSimpleTypeDef != null) {
            if(!isWhiteSpace(value)) {
              serializeValue(element);
            }
          }
        } else {
          if(!isWhiteSpace(value)) {
            serializeValue(element);
          }
        }
      }
    }
    if(processingModelGropuElemsCollector != null) {
      processProcessingModelGroupElements(processingModelGropuElemsCollector, inScopeNSesCollector, inScopeNormNSAttribsCollector);
      processingModelGropuElemsCollector = null;
    }

    serializeElement_End(element);
  }

  private boolean isWhiteSpace(String value) {
    for(int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if(ch != '\n' && ch != '\t' && ch != '\r') {
        return(false);
      }
    }
    return(true);
  }

  private String createMixContentValue(String value) {
    StringBuffer buffer = new StringBuffer();
    int index1 = 0;
    while(index1 < value.length()) {
      char ch = value.charAt(index1);
      if(ch != '\n' && ch != '\t' && ch != '\r') {
        break;
      } else {
        index1++;
      }
    }
    int index2 = value.length() - 1;
    while(index2 >= index1) {
      char ch = value.charAt(index2--);
      if(ch != '\n' && ch != '\t' && ch != '\r') {
        buffer.insert(0, ch);
      }
    }
    return(buffer.length() == 0 ? null : buffer.toString());
  }

  private void processProcessingModelGroupElements(Vector processingModelGropuElemsCollector, Hashtable inScopeNSesCollector, Hashtable inScopeNormNSAttribsCollector) throws CanonicalizationException {
    for(int i = 0; i < processingModelGropuElemsCollector.size(); i++) {
      process((ElementImpl)(processingModelGropuElemsCollector.get(i)), inScopeNSesCollector, inScopeNormNSAttribsCollector);
    }
  }

  private void serializeNSAttrib(NodeImpl attrib) throws CanonicalizationException {
    String localName = attrib.getLocalName();
    write(localName.equals("xmlns") ? " xmlns=\"" + attrib.getNodeValue() + "\"" : " xmlns:" + localName + "=\"" + attrib.getNodeValue() + "\"");
  }

  private void serializeNormNSAttribStructure(NSAttribStructure nsAttribStructure) throws CanonicalizationException {
    write(" xmlns:" + nsAttribStructure.getPrefix() + "=\"" + nsAttribStructure.getValue() + "\"");
  }

  private boolean isNamespaceAttribute(NodeImpl node) {
    return(determineUri(node).equals(NAMESPACE_ATTRIBS_NAMESPACE) || determinePrefix(node).equals("xmlns"));
  }

  private Vector createSortedVector(Hashtable collector) {
    Vector result = new Vector();
    Enumeration enum1 = collector.elements();
    while(enum1.hasMoreElements()) {
      add_Sorted(result, enum1.nextElement());
    }
    return(result);
  }

  private void add_Sorted(Vector collector, Object obj) {
    int index = 0;
    while(index < collector.size()) {
      Object baseObj = collector.get(index);
      int compareResult = compare(baseObj, obj);
      if(compareResult == COMPARE_GREATER) {
        index++;
      } else {
        collector.add(index, obj);
        break;
      }
    }
    if(index >= collector.size()) {
      collector.add(obj);
    }
  }

  private int compare(Object baseObj, Object obj) {
    int compareResult = -1;
    if(baseObj instanceof Element) {
      NodeImpl baseNode = (NodeImpl)baseObj;
      NodeImpl node = (NodeImpl)obj;
      compareResult = compare(determineUri(baseNode), determineUri(node));
      if(compareResult == COMPARE_EQUAL) {
        compareResult = compare(baseNode.getLocalName(), node.getLocalName());
      }
    } else if(baseObj instanceof Attr) {
      NodeImpl baseNode = (NodeImpl)baseObj;
      NodeImpl node = (NodeImpl)obj;
      if(isNamespaceAttribute(baseNode)) {
        compareResult = compare(baseNode.getNodeValue(), node.getNodeValue());
        if(compareResult == COMPARE_EQUAL) {
          compareResult = compare(determinePrefix(baseNode), determinePrefix(node));
        }
      } else {
        compareResult = compare(determineUri(baseNode), determineUri(node));
        if(compareResult == COMPARE_EQUAL) {
          compareResult = compare(baseNode.getLocalName(), node.getLocalName());
        }
      }
    } else if(baseObj instanceof NSAttribStructure) {
      NSAttribStructure baseNsAttribStruc = (NSAttribStructure)baseObj;
      NSAttribStructure nsAttribStruc = (NSAttribStructure)obj;
      compareResult = compare(baseNsAttribStruc.getValue(), nsAttribStruc.getValue());
      if(compareResult == COMPARE_EQUAL) {
        compareResult = compare(baseNsAttribStruc.getPrefix(), nsAttribStruc.getPrefix());
      }
    }
    return(compareResult);
  }

  private String determineUri(NodeImpl node) {
    return(determineUri(node.getNamespaceURI()));
  }

  private String determineUri(String uri) {
    return(uri == null ? "" : uri);
  }

  private int compare(String baseStr, String str) {
    for(int i = 0; i < baseStr.length(); i++) {
      if(i >= str.length()) {
        return(COMPARE_GREATER);
      }
      char baseCh = baseStr.charAt(i);
      char ch = str.charAt(i);
      if(baseCh < ch) {
        return(COMPARE_GREATER);
      } else if(baseCh > ch) {
        return(COMPARE_LESS);
      }
    }
    return(COMPARE_EQUAL);
  }

  private void serializeElement_End(ElementImpl element) throws CanonicalizationException {
    String prefix = wildcarded(element) ? determinePrefix(element) : determineNormalizedPrefix(element);
    write("</" + prefix + (prefix.equals("") ? "" : ":") + element.getLocalName() + ">");
  }

  private void serializeAttribute(NodeImpl attr) throws CanonicalizationException {
    write(" ");
    String prefix = null;
    String attrUri = attr.getNamespaceURI();
    if(attrUri != null && attrUri.equals(XML_NAMESPACE)) {
      prefix = "xml";
    } else {
      prefix = !wildcarded(attr) ? determineNormalizedPrefix(attr) : determinePrefix(attr);
    }
    write(prefix + (prefix.equals("") ? "" : ":"));
    write(attr.getLocalName() + "=\"");
    serializeValue(attr);
    write("\"");
  }

  private void serializeElement_Start(ElementImpl element, boolean attribsAreSerialized) throws CanonicalizationException {
    if(!attribsAreSerialized) {
      boolean isWildcarded = wildcarded(element);
      String prefix = isWildcarded ? determinePrefix(element) : determineNormalizedPrefix(element);
      String localName = element.getLocalName();
      write("<");
      write(prefix + (prefix.equals("") ? "" : ":") + localName);
    } else {
      write(">");
    }
  }

  private void serializeValue(NodeImpl node) throws CanonicalizationException {
    String prefixAndSchemaNormalizedValue = (String)(node.getAugmentation(AUG_PREFIX_AND_SCHEMA_NORMALIZED_VALUE));
    if(prefixAndSchemaNormalizedValue != null) {
      write(escape(prefixAndSchemaNormalizedValue));
    } else {
      String schemaNormalizedValue = (String)(node.getAugmentation(AUG_SCHEMA_NORMALIZED_VALUE));
      if(schemaNormalizedValue != null) {
        write(escape(schemaNormalizedValue));
      } else {
        String normalizedValue = (String)(node.getAugmentation(AUG_NORMALIZED_VALUE));
        if(normalizedValue != null) {
          write(escape(normalizedValue));
        } else {
          String nodeValue = DOM.getNodeValue(node);
          if(nodeValue != null) {
            write(nodeValue);
          }
        }
      }
    }
  }

  private String determineNormalizedPrefix(NodeImpl node) {
    String normalizedPrefix = (String)(node.getAugmentation(AUG_NORMALIZED_PREFIX));
    return(normalizedPrefix == null ? "" : normalizedPrefix);
  }

  private String determinePrefix(NodeImpl node) {
    return(determinePrefix(node.getPrefix()));
  }

  private String determinePrefix(String prefix) {
    return(prefix == null ? "" : prefix);
  }

  private boolean wildcarded(NodeImpl node) {
    return(node.getAugmentation(AUG_VALIDATION_ATTEMPTED) == null);
  }

  private boolean wildcardOutputRoot(NodeImpl node, Vector attribsCollector) {
    if(!wildcarded(node)) {
      for(int i = 0; i < attribsCollector.size(); i++) {
        NodeImpl attr = (NodeImpl)(attribsCollector.get(i));
        if(wildcarded(attr)) {
          node.setAugmentation(AUG_WILDCARDED, new Boolean(true));
          return(true);
        }
      }
    } else {
      NodeImpl parent = (NodeImpl)(node.getParentNode());
      node.setAugmentation(AUG_WILDCARDED, new Boolean(true));
      if(parent instanceof Document || parent.getAugmentation(AUG_WILDCARDED) == null) {
        return(true);
      }
    }
    return(false);
  }

  private void namespaceAttributeNormalization(NodeImpl node, Vector attribsCollector, Hashtable inScopeNSAttribsCollector, Hashtable parentNormNSAttribsCollector, Hashtable normNSAttribsCollector) throws CanonicalizationException {
    Enumeration enum1 = inScopeNSAttribsCollector.elements();
    String namespace = determineUri(node);

    while(enum1.hasMoreElements()) {
      Attr nsAttr = (Attr)(enum1.nextElement());
      String inScopeNS = nsAttr.getNodeValue();
      if(isVisiblyUtilized(inScopeNS, node, attribsCollector) && !parentNormNSAttribsCollector.containsKey(inScopeNS) && !normNSAttribsCollector.containsKey(inScopeNS)) {
        String normalizedPrefix = "n" + normalizedNamespaceAttribIndex++;
        normNSAttribsCollector.put(inScopeNS, new NSAttribStructure(normalizedPrefix, inScopeNS));
      }
    }

    NSAttribStructure nsAttribStructure = getNSAttribStructure(parentNormNSAttribsCollector, normNSAttribsCollector, namespace);
    if(nsAttribStructure != null) {
      node.setAugmentation(AUG_NORMALIZED_PREFIX, nsAttribStructure.getPrefix());
    }

    String schemaNormValue = (String)(node.getAugmentation(AUG_SCHEMA_NORMALIZED_VALUE));
    if(schemaNormValue != null) {
      Vector triplesCollector = (Vector)(node.getAugmentation(AUG_PREFIX_USAGE_LOCATIONS));
      if(triplesCollector != null) {
        StringBuffer buffer = new StringBuffer(schemaNormValue);
        int additionalOffset = 0;
        for(int i = 0; i < triplesCollector.size(); i++) {
          Triple triple = (Triple)(triplesCollector.get(i));
          String prefix = triple.getPrefix();
          String uri = triple.getUri();
          int offset = triple.getOffset() + additionalOffset;
          NSAttribStructure nsAttribStruc = getNSAttribStructure(parentNormNSAttribsCollector, normNSAttribsCollector, uri);
          String normalizedPrefix = nsAttribStruc.getPrefix();
          int prefixLength = prefix.length();
          String replacement = null;
          if(prefixLength == 0) {
            replacement = normalizedPrefix + ":";
            additionalOffset++;
          } else {
            replacement = normalizedPrefix;
          }
          buffer.replace(offset, offset + prefixLength, replacement);
          additionalOffset += normalizedPrefix.length() - prefixLength;
        }
        node.setAugmentation(AUG_PREFIX_AND_SCHEMA_NORMALIZED_VALUE, buffer.toString());
      }
    }
  }

  private NSAttribStructure getNSAttribStructure(Hashtable parentNormNSAttribsCollector, Hashtable normNSAttribsCollector, String uri) {
    NSAttribStructure nsAttribStructure = (NSAttribStructure)(normNSAttribsCollector.get(uri));
    if(nsAttribStructure == null) {
      nsAttribStructure = (NSAttribStructure)(parentNormNSAttribsCollector.get(uri));
    }
    return(nsAttribStructure);
  }

  private boolean isVisiblyUtilized(String ns, NodeImpl node, Vector attribsCollector) {
    if(ns.equals(node.getNamespaceURI())) {
      return(true);
    }
    Vector triplesCollector = (Vector)(node.getAugmentation(AUG_PREFIX_USAGE_LOCATIONS));
    if(triplesCollector != null) {
      if(isVisiblyUtilized(ns, triplesCollector)) {
        return(true);
      }
    }
    if(attribsCollector != null) {
      for(int i = 0; i < attribsCollector.size(); i++) {
        if(isVisiblyUtilized(ns, (NodeImpl)(attribsCollector.get(i)), null)) {
          return(true);
        }
      }
    }
    return(false);
  }

  private boolean isVisiblyUtilized(String ns, Vector prefixUsageLocationsCollector) {
    for(int i = 0; i < prefixUsageLocationsCollector.size(); i++) {
      Triple triple = (Triple)(prefixUsageLocationsCollector.get(i));
      if(ns.equals(triple.getUri())) {
        return(true);
      }
    }
    return(false);
  }

  private void dataTypeCanonicalization(NodeImpl node, SSCAnnotation sscAnnotation, TypeDefinitionBase typeDef) {
    String normalizedValue = (String)(node.getAugmentation(AUG_SCHEMA_NORMALIZED_VALUE));
    if(normalizedValue != null) {
      StringBuffer buffer = new StringBuffer(normalizedValue);
      if(typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_DATE_TIME_NAME), false, false)) {
        int start = normalizedValue.indexOf("T");
        if(start >= 0) {
          start++;
          int end = normalizedValue.indexOf(":", start);
          String hours = normalizedValue.substring(start, end);
          if(hours.equals("24")) {
            buffer.replace(start, end, "00");
          }
        }
        node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, buffer.toString());
      } else if(typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_FLOAT_NAME), false, false) || typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_DOUBLE_NAME), false, false)) {
        int index = 0;
        int delIndex = -1;
        int decimalPointIndex = -1;
        while(index < buffer.length()) {
          char ch = buffer.charAt(index);
          if(ch == '+') {
            buffer.deleteCharAt(index);
          } else if(ch == 'e') {
            buffer.setCharAt(index, 'E');
            delIndex = index;
            index++;
          } else if(ch == 'E') {
            delIndex = index;
            index++;
          } else if(ch == '.') {
            decimalPointIndex = index;
            index++;
          } else if(ch == '0' && (index == 0 || buffer.charAt(index - 1) == '-' || buffer.charAt(index - 1) == 'E')) {
            buffer.deleteCharAt(index);
          } else {
            index++;
          }
        }
        if(delIndex == -1) {
          buffer.append("E0");
          delIndex = buffer.length() - 2;
        }
        if(decimalPointIndex == -1) {
          buffer.insert(delIndex - 1, ".0");
        } else {
          int hlpIndex = delIndex - 1;
          while(true) {
            char ch = buffer.charAt(hlpIndex);
            if(ch == '0' && buffer.charAt(hlpIndex - 1) != '.') {
              buffer.deleteCharAt(hlpIndex);
              hlpIndex--;
            } else {
              break;
            }
          }
        }
        node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, buffer.toString());
      } else if(typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_LANGUAGE_NAME), false, false)) {
        node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, normalizedValue.toUpperCase(Locale.ENGLISH));
      } else if(typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_STRING_NAME), false, false) || typeDef.isDerivedFrom(SchemaImpl.getBuiltInTypeDefnition(TYPE_ANY_URI_NAME), false, false)) {
        String caseMapKind = sscAnnotation.getCaseMapKind();
        if(caseMapKind == null) {
          if(sscAnnotation.getCaseMap() != null || sscAnnotation.getCaseMapAttributeName() != null) {
            node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, normalizedValue.toUpperCase(Locale.ENGLISH));
          }
        } else {
          if(caseMapKind.equals(SCC_UPPER_CASE_MAP) || caseMapKind.equals(SCC_FOLD_CASE_MAP)) {
            node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, normalizedValue.toUpperCase(Locale.ENGLISH));
          } else if(caseMapKind.equals(SCC_LOWER_CASE_MAP)) {
            node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, normalizedValue.toLowerCase(Locale.ENGLISH));
          }
        }
      }
    }
  }

  private boolean isQNameEmbeddedLang(TypeDefinitionBase typeDefBase) {
    SimpleTypeDefinition qNameTypeDef = (SimpleTypeDefinition)(SchemaImpl.getBuiltInTypeDefnition(TYPE_QNAME_NAME));
    if(typeDefBase.isDerivedFrom(qNameTypeDef, false, false)) {
      return(true);
    }
    if(typeDefBase instanceof SimpleTypeDefinition) {
      SimpleTypeDefinition simpleTypeDef = (SimpleTypeDefinition)typeDefBase;
      if(simpleTypeDef.isVarietyList()) {
        if(simpleTypeDef.getItemTypeDefinition().isDerivedFrom(qNameTypeDef, false, false)) {
          return(true);
        }
      }
    }
    return(false);
  }

  private void namespacePrefixDesensitization(NodeImpl node, String embededLanguage) throws CanonicalizationException {
    if(embededLanguage != null) {
      if(!embededLanguage.equals(SCC_XPATH_EMBEDDED_LANG_NAMESPACE) && !embededLanguage.equals(SCC_QNAME_EMBEDDED_LANG_NAMESPACE)) {
        throw new CanonicalizationException("Embedded language '" + embededLanguage + "' is not supported.");
      }
      String normalizedValue = (String)(node.getAugmentation(AUG_SCHEMA_NORMALIZED_VALUE));
      if(normalizedValue != null) {
        Vector triplesCollector = new Vector();
        node.setAugmentation(AUG_PREFIX_USAGE_LOCATIONS, triplesCollector);
        StringTokenizer tokenizer = new StringTokenizer(normalizedValue, embededLanguage.equals(SCC_XPATH_EMBEDDED_LANG_NAMESPACE) ? "/" : " ");
        int offset = 0;
        while(tokenizer.hasMoreElements()) {
          String qName = tokenizer.nextToken();
          String[] prefixAndName = Tools.parseQName(qName);
          String prefix = determinePrefix(prefixAndName[0]);
          String uri = Tools.getUri(determineNamespaceMappings(node), prefix);
          if(uri == null) {
            throw new CanonicalizationException("Prefix '" + prefix + "' is not mapped to any uri.");
          }
          offset = normalizedValue.indexOf(qName, offset);
          triplesCollector.add(new Triple(offset, prefix, uri));
          offset += qName.length();
        }
      }
    }
  }

  private Hashtable determineNamespaceMappings(Node node) {
    if(node instanceof Element) {
      return(DOM.getNamespaceMappingsInScope(node));
    }
    return(DOM.getNamespaceMappingsInScope(((Attr)node).getOwnerElement()));
  }

  private void initSSCAnnotation_InfoItemDeclarationBase(SSCAnnotation sscAnnotation, InfoItemDeclarationBase infoItemDeclrBase, TypeDefinitionBase typeDefBase, TypeDefinitionBase memberTypeDefBase) throws CanonicalizationException {
    if(infoItemDeclrBase != null) {
      initSSCAnnotation(infoItemDeclrBase, sscAnnotation);
    }
    if(sscAnnotation.getEmbededLang() == null || sscAnnotation.getEmbededLangAttributeName() == null || sscAnnotation.getCaseMap() == null || sscAnnotation.getCaseMapAttributeName() == null || sscAnnotation.getCaseMapKind() == null) {
      scan_TypeDefinitionBase(sscAnnotation, memberTypeDefBase == null ? typeDefBase : memberTypeDefBase);
    }
  }

  private void scan_TypeDefinitionBase(SSCAnnotation sscAnnotation, TypeDefinitionBase typeDefBase) throws CanonicalizationException {
    initSSCAnnotation(typeDefBase, sscAnnotation);
    if(sscAnnotation.getEmbededLang() == null || sscAnnotation.getEmbededLangAttributeName() == null || sscAnnotation.getCaseMap() == null || sscAnnotation.getCaseMapAttributeName() == null || sscAnnotation.getCaseMapKind() == null) {
      if(typeDefBase instanceof ComplexTypeDefinition) {
        TypeDefinitionBase baseTypeDef = typeDefBase.getBaseTypeDefinition();
        if(!baseTypeDef.isBuiltIn()) {
          scan_TypeDefinitionBase(sscAnnotation, baseTypeDef);
        }
      } else {
        SimpleTypeDefinition simpleTypeDef = (SimpleTypeDefinition)typeDefBase;
        if(simpleTypeDef.isVarietyAtomic()) {
          TypeDefinitionBase baseTypeDef = typeDefBase.getBaseTypeDefinition();
          if(!baseTypeDef.isBuiltIn()) {
            scan_TypeDefinitionBase(sscAnnotation, baseTypeDef);
          }
        } else if(simpleTypeDef.isVarietyList()) {
          TypeDefinitionBase itemTypeDef = simpleTypeDef.getItemTypeDefinition();
          if(!itemTypeDef.isBuiltIn()) {
            scan_TypeDefinitionBase(sscAnnotation, itemTypeDef);
          }
        }
      }
    }
  }

  private void initSSCAnnotation(Base base, SSCAnnotation sscAnnotation) throws CanonicalizationException {
  	Element associatedElement = (Element)base.getAssociatedDOMNode();
  	if(associatedElement != null) {
  		NamedNodeMap namedNodeMap = associatedElement.getAttributes();
  		if(namedNodeMap != null) {
  			for(int i = 0; i < namedNodeMap.getLength(); i++) {
					NodeImpl node = (NodeImpl)(namedNodeMap.item(i));
        String name = node.getLocalName();
        String value = node.getNodeValue();
        if(determineUri(node).equals(SCC_NAMESPACE)) {
          if(name.equals(SCC_EMBEDDED_LANG_ATTRIB_NAME) && sscAnnotation.getEmbededLang() == null) {
            sscAnnotation.setEmbededLang(value);
          } else if(name.equals(SCC_EMBEDDED_LANG_ATTRIB_ATTRIB_NAME) && sscAnnotation.getEmbededLangAttributeName() == null) {
            String[] prefixAndName = Tools.parseQName(value);
            String prefix = determinePrefix(prefixAndName[0]);
            String attribName = prefixAndName[1];
            sscAnnotation.setEmbededLangAttributeUri(determineUri(Tools.getUri(determinePrefixesMapping(base), prefix)));
            sscAnnotation.setEmbededLangAttributeName(attribName);
          } else if(name.equals(SCC_CASE_MAP_ATTRIB_NAME) && sscAnnotation.getCaseMap() == null) {
            sscAnnotation.setCaseMap(value);
          } else if(name.equals(SCC_CASE_MAP_ATTRIB_ATTRIB_NAME) && sscAnnotation.getCaseMapAttributeName() == null) {
            String[] prefixAndName = Tools.parseQName(value);
            String prefix = determinePrefix(prefixAndName[0]);
            String attribName = prefixAndName[1];
            sscAnnotation.setCaseMapAttributeUri(determineUri(Tools.getUri(determinePrefixesMapping(base), prefix)));
            sscAnnotation.setCaseMapAttributeName(attribName);
          } else if(name.equals(SCC_CASE_MAP_KIND_ATTRIB_NAME) && sscAnnotation.getCaseMapKind() == null) {
            sscAnnotation.setCaseMapKind(value);
          }
        }
      }
    }
  }
  }

  private Hashtable determinePrefixesMapping(Base base) {
    Node associatedNode = base.getAssociatedDOMNode();
    if(associatedNode != null) {
      return(DOM.getNamespaceMappingsInScope(associatedNode));
    }
    return(new Hashtable());
  }

  private String escape(String value) {
    StringBuffer buffer = new StringBuffer(value);
    int index = 0;
    while(index < buffer.length()) {
      char ch = buffer.charAt(index);
      String replacement = null;
      switch(ch) {
        case '<' : {
          replacement = "&lt";
          break;
        }
        case '>' : {
          replacement = "&gt";
          break;
        }
        case '&' : {
          replacement = "&amp";
          break;
        }
        case '\'' : {
          replacement = "&apos";
          break;
        }
        case '"' : {
          replacement = "&quot";
          break;
        }
      }
      if(replacement != null) {
        buffer.replace(index, index + 1, replacement);
        index += replacement.length();
      } else {
        index++;
      }
    }
    return(buffer.toString());
  }

  private void write(String str) throws CanonicalizationException {
    try {
      if(xmlOutput != null) {
        xmlOutput.write(str.getBytes()); //$JL-I18N$
        xmlOutput.flush();
      } else {
        xmlWriter.write(str);
        xmlWriter.flush();
      }
    } catch(IOException ioExc) {
      throw new CanonicalizationException(ioExc.getMessage());
    }
  }
}
