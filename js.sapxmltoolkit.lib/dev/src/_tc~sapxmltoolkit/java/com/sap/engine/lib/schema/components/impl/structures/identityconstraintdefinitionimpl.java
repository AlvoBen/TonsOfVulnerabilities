package com.sap.engine.lib.schema.components.impl.structures;

import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.util.LexicalParser;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

import java.util.*;

/**
 * @author Nick Nickolov
 * @version November 2001
 */
public final class IdentityConstraintDefinitionImpl extends QualifiedBaseImpl implements IdentityConstraintDefinition {

  public static final int KEY = 1;
  public static final int KEYREF = 2;
  public static final int UNIQUE = 3;

  protected int category;
  protected String selector;
  protected Vector fields;
  protected IdentityConstraintDefinitionImpl referencedKey;
  protected ElementDeclaration owner;

  public IdentityConstraintDefinitionImpl() {
    this(null, null);
  }

  public IdentityConstraintDefinitionImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema, true);
		fields = new Vector();
  }

  public boolean isIdentityConstraintCategoryKey() {
    return(category == KEY);
  }

  public boolean isIdentityConstraintCategoryKeyref() {
    return(category == KEYREF);
  }

  public boolean isIdentityConstraintCategoryUnique() {
    return(category == UNIQUE);
  }

  public String getSelector() {
    return(selector);
  }

  public Vector getFields() {
    return(fields);
  }
  
  public void getFields(Vector collector) {
    Tools.removeFromVectorToVector(fields, collector);
  }

  public IdentityConstraintDefinition getReferencedKey() {
    return(referencedKey);
  }

  public int getTypeOfComponent() {
    return(C_IDENTITY_CONSTRAINT_DEFINITION);
  }

  public ElementDeclaration getOwner() {
    return(owner);
  }

  public boolean match(Base identityConstrDef) {
  	if(!super.match(identityConstrDef)) {
  		return(false);
  	}
  	IdentityConstraintDefinitionImpl targetIdentConstrDef = (IdentityConstraintDefinitionImpl)identityConstrDef;
  	if(category != targetIdentConstrDef.category) {
  		return(false);
  	}
    Hashtable dstPrefixesMapping = DOM.getNamespaceMappingsInScope(associatedNode);
    Hashtable srcPrefixesMapping = DOM.getNamespaceMappingsInScope(targetIdentConstrDef.associatedNode);
  	if(!equalXPaths(selector, dstPrefixesMapping, targetIdentConstrDef.selector, srcPrefixesMapping)) {
  		return(false);
  	}
  	
  	if(fields.size() != targetIdentConstrDef.fields.size()) {
  		return(false);
  	}
  	for(int i = 0; i < fields.size(); i++) {
  		if(!equalXPaths((String)(fields.get(i)), dstPrefixesMapping, (String)(targetIdentConstrDef.fields.get(i)), srcPrefixesMapping)) {
  			return(false);
  		}
  	}
  	
  	return(Tools.compareBases(referencedKey, targetIdentConstrDef.referencedKey));
  }
  
  private boolean equalXPaths(String dstXPath, Hashtable dstPrefixesMapping, String srcXPath, Hashtable srcPrefixesMapping) {
  	StringTokenizer dstTokenizer = new StringTokenizer(dstXPath, "/");
  	StringTokenizer srcTokenizer = new StringTokenizer(srcXPath, "/");
  	if(dstTokenizer.countTokens() != srcTokenizer.countTokens()) {
  		return(false);
  	}
  	while(dstTokenizer.hasMoreTokens()) {
  		String dstStep = dstTokenizer.nextToken();
  		boolean dstIsAttribute = dstStep.startsWith("@") || dstStep.startsWith("attribute:");
      String[] dstUriAndName = Tools.parseQName(dstStep, dstPrefixesMapping);

  		String srcStep = srcTokenizer.nextToken();
  		boolean srcIsAttribute = srcStep.startsWith("@") || srcStep.startsWith("attribute:");
      String[] srcUriAndName = Tools.parseQName(srcStep, srcPrefixesMapping);

  		if(dstIsAttribute ^ srcIsAttribute || !Tools.compareObjects(dstUriAndName[0], srcUriAndName[0]) || !dstUriAndName[1].equals(srcUriAndName[1])) {
  			return(false);
  		}
  	}
  	return(true);
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String localName = associatedNode.getLocalName();
      if(localName.equals(NODE_UNIQUE_NAME)) {
        category = UNIQUE;
      } else if(localName.equals(NODE_KEY_NAME)) {
        category = KEY;
      } else if(localName.equals(NODE_KEYREF_NAME)) {
        category = KEYREF;
        String qName = loadAttribsCollector.getProperty(NODE_REFER_NAME);
        referencedKey = (IdentityConstraintDefinitionImpl)(Tools.getTopLevelComponent(schema, associatedNode, qName, IDENTITY_CONSTRAINT_DEFINITION_ID));
				SchemaStructuresLoader.loadBase(referencedKey);
        if(((IdentityConstraintDefinitionImpl)referencedKey).isIdentityConstraintCategoryKeyref()) {
          throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of qualified base " + DOM.toXPath(associatedNode) + " is not correct. A keyref identity constraint definition should refer to a key or unique identity constraint definition.");
        }
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_SELECTOR_NAME)) {
              selector = ((Element)node).getAttribute(NODE_XPATH_NAME);
              if(!LexicalParser.parseXPathSelector(selector)) {
                throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : XPath expression " + selector + " is not supported.");
              }
            } else if(nodeLocalName.equals(NODE_FIELD_NAME)) {
              String field = ((Element)node).getAttribute(NODE_XPATH_NAME);
              if(!LexicalParser.parseXPathField(field)) {
                throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : XPath expression '" + field + "' is not supported.");
              }
              fields.add(field);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
    }
    if(category == KEYREF && fields.size() != referencedKey.fields.size()) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of qualified base " + DOM.toXPath(associatedNode) + " is not correct. The count of the specified fields should be equal to the count of the refered identity constraint definition fileds.");
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    IdentityConstraintDefinitionImpl result = (IdentityConstraintDefinitionImpl)(super.initializeBase(base, clonedCollector));
    if(result.category == IdentityConstraintDefinitionImpl.KEY) {
      clonedCollector.put(this, result);
    }
    result.category = category;
    result.selector = selector;
    result.fields = fields;
    if(referencedKey != null) {
      result.referencedKey = (IdentityConstraintDefinitionImpl)(referencedKey.clone(clonedCollector));
    }
    return(result);
  }
}

