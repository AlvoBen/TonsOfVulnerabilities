package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.Wildcard;
import com.sap.engine.lib.schema.components.Base;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.schema.exception.SchemaException;
import com.sap.engine.lib.schema.exception.SchemaRuntimeException;
import com.sap.engine.lib.xml.dom.DOM;

public final class WildcardImpl extends BaseImpl implements Wildcard {
	
	protected static final int SKIP = 0;
	protected static final int LAX = 1;
	protected static final int STRICT = 2;
  protected int processContents;
  protected boolean anyNamespace;
  protected boolean negateTargetNamespace;
  protected String negatedNamespace;
  protected Vector constraintNamespaces;
  protected boolean isAttribWildcard;
	protected boolean negateOther;

  private int targetNamespacePosition;

  public WildcardImpl() {
    this(null);
  }

  public WildcardImpl(SchemaImpl schema) {
    this(null, schema);
  }

  public WildcardImpl(Node associatedNode, SchemaImpl schema) {
		super(associatedNode, schema);
		anyNamespace = false;
		negateTargetNamespace = false;
		constraintNamespaces = new Vector();
		isAttribWildcard = false;
		targetNamespacePosition = -1;
		negateOther = false;
  }

  public int getTypeOfComponent() {
    return(C_WILDCARD);
  }

  public boolean isProcessContentsSkip() {
    return(processContents == SKIP);
  }

  public boolean isProcessContentsLax() {
    return(processContents == LAX);
  }

  public boolean isProcessContentsStrict() {
    return(processContents == STRICT);
  }

  public boolean isNamespaceConstraintAny() {
    return(anyNamespace);
  }

  public String getNamespaceConstraintNegated() {
  	if(negateTargetNamespace) {
  		return(schema.getTargetNamespace());
  	}
    return(negatedNamespace);
  }
  
  public boolean isAttribWildcard() {
  	return(isAttribWildcard);
  }

  public String[] getNamespaceConstraintMembersAsArray() {
  	String[] namespaceMemebers = new String[constraintNamespaces.size()];
		for(int i = 0; i < constraintNamespaces.size(); i++) {
			namespaceMemebers[i] = ((Namespace)(constraintNamespaces.get(i))).getNamespace();
		}
		return(namespaceMemebers);
  }

  public void getNamespaceConstraintMembers(Vector collector) {
  	for(int i = 0; i < constraintNamespaces.size(); i++) {
			collector.add(((Namespace)(constraintNamespaces.get(i))).getNamespace());
  	}
  }

  public boolean match(Base wildcard) {
  	if(!super.match(wildcard)) {
  		return(false);
  	}
  	WildcardImpl targetWildcard = (WildcardImpl)wildcard;
  	boolean result = processContents == targetWildcard.processContents &&
  										equalNamespaceConstraint(targetWildcard);
  	return(result);									
  }
  
  private boolean equalNamespaceConstraint(WildcardImpl wildcard) {
  	if(anyNamespace != wildcard.anyNamespace) {
  		return(false);
  	}
  	if(!Tools.compareObjects(getNamespaceConstraintNegated(), wildcard.getNamespaceConstraintNegated())) {
  		return(false);
  	}
		return(Tools.compareUnorderedObjects(constraintNamespaces, wildcard.constraintNamespaces));		
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
			isAttribWildcard = associatedNode.getLocalName().equals(NODE_ANY_ATTRIBUTE_NAME);
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String namespaceAttribValue = loadAttribsCollector.getProperty(NODE_NAMESPACE_NAME);
      if(namespaceAttribValue != null) {
        if(namespaceAttribValue.equals(VALUE_ANY_NAME)) {
					anyNamespace = true;
        } else if(namespaceAttribValue.equals(VALUE_OTHER_NAME)) {
					negateTargetNamespace = true;
					negateOther = true;
        } else {
          StringTokenizer tokenizer = new StringTokenizer(namespaceAttribValue);
          while(tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            Namespace constraintNamespace = null;
            if(token.equals(VALUE_TARGET_NAMESPACE_NAME)) {
							constraintNamespace = new ConstraintTargetNamespace();
              targetNamespacePosition = constraintNamespaces.size();
            } else if(token.equals(VALUE_LOCAL_NAME)) {
							constraintNamespace = new ConstraintNamespace("");
            } else if(token.equals(VALUE_NOT_NAME)) {
              negatedNamespace = "";
              while(tokenizer.hasMoreElements()) {
								negatedNamespace += tokenizer.nextToken();
              }
            } else {
							constraintNamespace = new ConstraintNamespace(token);
            }
            if(constraintNamespace != null) {
							constraintNamespaces.add(constraintNamespace);
            }
          }
        }
      } else {
				anyNamespace = true;
      }
      String processContentsAttribValue = loadAttribsCollector.getProperty(NODE_PROCESS_CONTENTS_NAME);
      if(processContentsAttribValue != null) {
        if(processContentsAttribValue.equals(VALUE_LAX_NAME)) {
          processContents = LAX;
        } else if(processContentsAttribValue.equals(VALUE_SKIP_NAME)) {
          processContents = SKIP;
        } else if(processContentsAttribValue.equals(VALUE_STRICT_NAME)) {
          processContents = STRICT;
        }
      } else {
        processContents = STRICT;
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            }
          }
        }
      }
    }
  }

  protected void intersect(WildcardImpl intersectionWildcard) throws SchemaComponentException {
  	String negatedNamespace = getNamespaceConstraintNegated();
  	String intersectionNegatedNamespace = intersectionWildcard.getNamespaceConstraintNegated();
    if(negatedNamespace != null && intersectionNegatedNamespace != null && !negatedNamespace.equals(intersectionNegatedNamespace)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : The intersection of wildcard " + DOM.toXPath(associatedNode) + " with widcard " + DOM.toXPath(intersectionWildcard.associatedNode) + " is not expressible.");
    }
    if(intersectionWildcard.constraintNamespaces.size() != 0 && anyNamespace) {
			anyNamespace = false;
			constraintNamespaces.addAll(intersectionWildcard.constraintNamespaces);
      return;
    }
    if(intersectionWildcard.constraintNamespaces.size() != 0 && negatedNamespace != null) {
			constraintNamespaces.addAll(intersectionWildcard.constraintNamespaces);
			constraintNamespaces.remove(new ConstraintNamespace(negatedNamespace));
			negateTargetNamespace = false;
			negatedNamespace = null;
      return;
    }
    if(intersectionNegatedNamespace != null && constraintNamespaces.size() != 0) {
			constraintNamespaces.remove(new ConstraintNamespace(intersectionNegatedNamespace));
      return;
    }
    if(intersectionWildcard.constraintNamespaces.size() != 0 && constraintNamespaces.size() != 0) {
      int index = 0;
      while(index != constraintNamespaces.size()) {
      	Object constraintNamespace = constraintNamespaces.get(index);
      	if(!intersectionWildcard.constraintNamespaces.contains(constraintNamespace)) {
					constraintNamespaces.remove(index);
        } else {
          index++;
        }
      }
    }
  }

  protected void unite(WildcardImpl unitiveWildcard) throws SchemaComponentException {
		String negatedNamespace = getNamespaceConstraintNegated();
		String unitiveNegatedNamespace = unitiveWildcard.getNamespaceConstraintNegated();
    if(negatedNamespace != null && unitiveNegatedNamespace != null && !negatedNamespace.equals(unitiveNegatedNamespace)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : The unification of wildcard " + DOM.toXPath(associatedNode) + " with wildcard " + DOM.toXPath(unitiveWildcard.associatedNode) + " is not expressible.");
    }
    if(constraintNamespaces.size() != 0 && unitiveWildcard.constraintNamespaces.size() != 0) {
      for(int i = 0; i < unitiveWildcard.constraintNamespaces.size(); i++) {
        Object constraintNamespace = unitiveWildcard.constraintNamespaces.get(i);
        if(!constraintNamespaces.contains(constraintNamespace)) {
					constraintNamespaces.add(constraintNamespace);
        }
      }
      return;
    }
    if(negatedNamespace != null && unitiveWildcard.constraintNamespaces.size() != 0) {
      if(unitiveWildcard.constraintNamespaces.contains(new ConstraintNamespace(negatedNamespace))) {
				constraintNamespaces.clear();
				negateTargetNamespace = false;
				negatedNamespace = null;
        anyNamespace = true;
      }
      return;
    }
    if(constraintNamespaces.size() != 0 && unitiveNegatedNamespace != null) {
			constraintNamespaces.clear();
      if(constraintNamespaces.contains(new ConstraintNamespace(unitiveNegatedNamespace))) {
				negateTargetNamespace = false;
				negatedNamespace = null;
        anyNamespace = true;
      } else {
				negateTargetNamespace = unitiveWildcard.negateTargetNamespace;
				negatedNamespace = unitiveWildcard.negatedNamespace;
      }
      return;
    }
  }

  protected BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    WildcardImpl result = (WildcardImpl)(super.initializeBase(base, clonedCollector));
    result.processContents = processContents;
    result.anyNamespace = anyNamespace;
    result.negateTargetNamespace = negateTargetNamespace;
    result.negatedNamespace = negatedNamespace;
    result.constraintNamespaces.addAll(constraintNamespaces);
    result.isAttribWildcard = isAttribWildcard;
    result.negateOther = negateOther;
    result.targetNamespacePosition = targetNamespacePosition;
    return(result);
  }
  
  private abstract class Namespace {
  	
  	abstract String getNamespace();
  	
		public boolean equals(Object object) {
			if(object == null) {
				return(false);
			}
			if(object instanceof Namespace) {
				return(((Namespace)object).getNamespace().equals(getNamespace()));
			}
			return(false);
		}
  }
  
  private class ConstraintNamespace extends Namespace {
  	
  	public String namespace;
  	
  	private ConstraintNamespace(String namespace) {
  		this.namespace = namespace;
  	}
  	
  	public String getNamespace() {
  		return(namespace);
  	}
  	
  }
  
  private class ConstraintTargetNamespace extends Namespace {
		
		public String getNamespace() {
			return(schema.getTargetNamespace());
		}  	
  }
}

