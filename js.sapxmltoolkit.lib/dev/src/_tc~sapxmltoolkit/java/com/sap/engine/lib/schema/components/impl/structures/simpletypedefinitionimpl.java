package com.sap.engine.lib.schema.components.impl.structures;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.components.impl.ffacets.FundamentalFacetsList;
import com.sap.engine.lib.schema.components.impl.ffacets.FundamentalFacetsUnion;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaComponentException;
import com.sap.engine.lib.xml.dom.DOM;

public final class SimpleTypeDefinitionImpl extends TypeDefinitionBaseImpl implements SimpleTypeDefinition {

  protected static final int ATOMIC = 1;
  protected static final int LIST = 2;
  protected static final int UNION = 3;
  protected static final int UNKNOWN = 0;
  protected int variety;
  protected SimpleTypeDefinitionImpl primitiveTypeDefinition;
  protected SimpleTypeDefinitionImpl itemTypeDefinition;
  protected Vector memberTypeDefsCollector;
  protected Vector facetsCollector;
  protected FundamentalFacets fundamentalFacets;
  protected boolean isPrimitive;
  protected boolean isFinalList;
  protected boolean isFinalUnion;
  protected String whiteSpaceNormalizationValue;

  public SimpleTypeDefinitionImpl() {
    this(null, null, false, false);
  }

  public SimpleTypeDefinitionImpl(Node associatedNode, SchemaImpl schema, boolean isTopLevel, boolean isRedefined) {
		super(associatedNode, schema, isTopLevel, isRedefined);
    memberTypeDefsCollector = new Vector();
    facetsCollector = new Vector();
  }

  public String getWhiteSpaceNormalizationValue() {
    return(whiteSpaceNormalizationValue);
  }

  public int getTypeOfComponent() {
    return(C_SIMPLE_TYPE_DEFINITION);
  }

  public SimpleTypeDefinition getBaseTypeDefinitionSimple() {
  	if(baseTypeDefinition instanceof SimpleTypeDefinition) {
    return((SimpleTypeDefinition)baseTypeDefinition);
  }
    return(null);
  }

  public boolean isFinalList() {
    return(isFinalList);
  }

  public boolean isFinalUnion() {
    return(isFinalUnion);
  }

  public boolean isVarietyAtomic() {
    return(variety == ATOMIC);
  }

  public boolean isVarietyList() {
    return(variety == LIST);
  }

  public boolean isVarietyUnion() {
    return(variety == UNION);
  }

  public SimpleTypeDefinition getPrimitiveTypeDefinition() {
    return(primitiveTypeDefinition);
  }

  public SimpleTypeDefinition getItemTypeDefinition() {
    return(itemTypeDefinition);
  }

  public void getMemberTypeDefinitions(Vector collector) {
    Tools.removeFromVectorToVector(memberTypeDefsCollector, collector);
  }
  
  public Vector getMemberTypeDefinitions() {
    return(memberTypeDefsCollector);
  }

  public Vector getFacets() {
    return(facetsCollector);
  }
  
  public void getFacets(Vector collector) {
    Tools.removeFromVectorToVector(facetsCollector, collector);
  }

  public Facet[] getFacetsArray() {
    Facet[] result = new Facet[facetsCollector.size()];
    facetsCollector.copyInto(result);
    return(result);
  }

  public FundamentalFacets getFundamentalFacets() {
    return(fundamentalFacets);
  }

  public boolean isPrimitive() {
    return isPrimitive;
  }

  public boolean match(Base simpleTypeDef) {

  	if(!super.match(simpleTypeDef)) {
  		return(false);
  	}
  	
  	SimpleTypeDefinitionImpl targetSimpleTypeDef = (SimpleTypeDefinitionImpl)simpleTypeDef;
  	if(isBuiltIn) {
  		return(targetSimpleTypeDef.isBuiltIn);
  	}
  	
   	return(Tools.compareUnorderedBases(facetsCollector, targetSimpleTypeDef.facetsCollector) &&
  					isFinalExtension == targetSimpleTypeDef.isFinalExtension && 
		   		  isFinalRestriction == targetSimpleTypeDef.isFinalRestriction &&
  		 			isFinalList == targetSimpleTypeDef.isFinalList &&
  		 			isFinalUnion == targetSimpleTypeDef.isFinalUnion &&
  		 			variety == targetSimpleTypeDef.variety &&
  		 			Tools.compareBases(primitiveTypeDefinition, targetSimpleTypeDef.primitiveTypeDefinition) &&
  		 			Tools.compareBases(itemTypeDefinition, targetSimpleTypeDef.itemTypeDefinition) &&
  		 			Tools.compareUnorderedBases(memberTypeDefsCollector, targetSimpleTypeDef.memberTypeDefsCollector));
  }

  public void load() throws SchemaComponentException {
    if(associatedNode != null) {
      id = loadAttribsCollector.getProperty(NODE_ID_NAME);
      String finalAttribValue = loadAttribsCollector.getProperty(NODE_FINAL_NAME);
      if(finalAttribValue != null) {
        if(finalAttribValue.equals(VALUE_ALL_NAME)) {
          isFinalExtension = true;
          isFinalRestriction = true;
          isFinalList = true;
          isFinalUnion = true;
        } else if(finalAttribValue.equals(VALUE_LIST_NAME)) {
          isFinalList = true;
        } else if(finalAttribValue.equals(VALUE_UNION_NAME)) {
          isFinalUnion = true;
        } else if(finalAttribValue.equals(VALUE_RESTRICTION_NAME)) {
          isFinalRestriction = true;
        }
      } else {
        isFinalExtension = schema.isFinalExtension();
        isFinalRestriction = schema.isFinalRestriction();
      }
      NodeList nodeList = associatedNode.getChildNodes();
      for(int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if(node instanceof Element) {
          String nodeUri = node.getNamespaceURI();
          if(nodeUri != null && nodeUri.equals(SCHEMA_COMPONENTS_NS)) {
            String nodeLocalName = node.getLocalName();
            if(nodeLocalName.equals(VALUE_RESTRICTION_NAME)) {
              loadRestriction(node);
            } else if(nodeLocalName.equals(NODE_ANNOTATION_NAME)) {
              annotation = SchemaStructuresLoader.createAnnotation(node, schema);
							SchemaStructuresLoader.loadBase(annotation);
            } else if(nodeLocalName.equals(VALUE_LIST_NAME)) {
              loadList(node);
            } else if(nodeLocalName.equals(VALUE_UNION_NAME)) {
              loadUnion(node);
            }
          }
        }
      }
			schema.addComponent(this);
    }
  }

  private void loadRestriction(Node node) throws SchemaComponentException {
		NamedNodeMap attribs = node.getAttributes();
		for(int i = 0; i < attribs.getLength(); i++) {
			Node attrib = attribs.item(i);
			String attribName = attrib.getLocalName();
			String uri = attrib.getNamespaceURI();
			String value = attrib.getNodeValue();  	
			if(uri == null || uri.equals("")) {
				if(attribName.equals(NODE_BASE_NAME)) {
					initBase((TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID)));
				}
			}
		}
		
		NodeList childNodes = node.getChildNodes();
		boolean removeEnumerationFacets = true;
		boolean removePatternFacets = true;
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				String uri = childNode.getNamespaceURI();
				String elemName = childNode.getLocalName();
				if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {
					if(elemName.equals(NODE_ANNOTATION_NAME)) {
					} else if(elemName.equals(NODE_SIMPLE_TYPE_NAME)) {
            if(baseTypeDefinition != null) {
              throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of simple type " + DOM.toXPath(associatedNode) + " is not correct. If the <restriction> alternative is chosen, it must have a base [attribute] or a <simpleType> among its [children], but not both.");
            }
						initBase(SchemaStructuresLoader.createSimpleTypeDefinition(childNode, schema, false, false));
					} else if(elemName.equals(FACET_MIN_EXCLUSIVE_NAME) || elemName.equals(FACET_MIN_INCLUSIVE_NAME) ||
										elemName.equals(FACET_MAX_EXCLUSIVE_NAME) || elemName.equals(FACET_MAX_INCLUSIVE_NAME) ||
										elemName.equals(FACET_TOTAL_DIGITS_NAME) || elemName.equals(FACET_FRACTION_DIGITS_NAME) ||
										elemName.equals(FACET_LENGTH_NAME) || elemName.equals(FACET_MIN_LENGTH_NAME) ||
										elemName.equals(FACET_MAX_LENGTH_NAME) || elemName.equals(FACET_ENUMERATION_NAME) ||
										elemName.equals(FACET_WHITE_SPACE_NAME) || elemName.equals(FACET_PATTERN_NAME)) {
            FacetImpl restrictedFacet = SchemaStructuresLoader.createFacet(childNode, schema);
						SchemaStructuresLoader.loadBase(restrictedFacet);
						addRestrictedFacet(restrictedFacet, removeEnumerationFacets, removePatternFacets);
						removeEnumerationFacets = !elemName.equals(FACET_ENUMERATION_NAME);
						removePatternFacets = !elemName.equals(FACET_PATTERN_NAME);
					}
        }
			}
		}
	}

	private void initBase(TypeDefinitionBaseImpl typeDefBase) throws SchemaComponentException {
    if(!(typeDefBase instanceof SimpleTypeDefinitionImpl)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of simple type " + DOM.toXPath(associatedNode) + " is not corect. The base type definition must be a simple type.");
    }
    baseTypeDefinition = typeDefBase;
		SchemaStructuresLoader.loadBase(baseTypeDefinition);
		SimpleTypeDefinitionImpl baseTypeDef = (SimpleTypeDefinitionImpl)baseTypeDefinition;
		facetsCollector.addAll(baseTypeDef.facetsCollector);
		fundamentalFacets = baseTypeDef.fundamentalFacets;
		variety = baseTypeDef.variety;
    whiteSpaceNormalizationValue = baseTypeDef.whiteSpaceNormalizationValue;
		if(baseTypeDef.isFinalRestriction) {
			throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. The {final} of the base type definition must not contain 'restriction'.");
		}
		if(variety == ATOMIC) {
			primitiveTypeDefinition = baseTypeDef.primitiveTypeDefinition;
		} else if(variety == LIST) {
			if(baseTypeDef.isFinalList) {
				throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is 'list', then the {final} of the base type definition must not contain 'list'.");
			}
      itemTypeDefinition = baseTypeDef.itemTypeDefinition;
		} else {
			if(baseTypeDef.isFinalUnion) {
				throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is 'union', then the {final} of the base type definition must not contain 'union'.");
			}
			memberTypeDefsCollector.addAll(baseTypeDef.memberTypeDefsCollector);
		}
	}
	
	private void loadList(Node node) throws SchemaComponentException {
		NamedNodeMap attribs = node.getAttributes();
		for(int i = 0; i < attribs.getLength(); i++) {
			Node attrib = attribs.item(i);
			String attrName = attrib.getLocalName();
			String uri = attrib.getNamespaceURI();
			String value = attrib.getNodeValue();
			if(uri == null || uri.equals("")) {
				if(attrName.equals(NODE_ITEM_TYPE_NAME)) {
					initItemTypeDefinition((TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, value, TYPE_DEFINITION_ID)));
				} 	  
			}
		}

		NodeList childNodes = node.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				String uri = childNode.getNamespaceURI();
				String elemName = childNode.getLocalName();
				if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {	
					if(elemName.equals(NODE_ANNOTATION_NAME)) {
					} else if(elemName.equals(NODE_SIMPLE_TYPE_NAME)) {
            if(itemTypeDefinition != null) {
              throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of simple type " + DOM.toXPath(associatedNode) + " is not correct. If the <list> alternative is chosen, it must have an <itemType> [attribute] or a <simpleType> among its [children], but not both.");
            }
						initItemTypeDefinition(SchemaStructuresLoader.createSimpleTypeDefinition(childNode, schema, false, false));
					}
        }
			}
		}
    whiteSpaceNormalizationValue = WHITE_SPACE_COLLAPSE_NORM_VALUE;
    baseTypeDefinition = (SimpleTypeDefinitionImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_SIMPLE_TYPE_NAME));
		variety = LIST;
    fundamentalFacets = FundamentalFacetsList.newInstance();
	}
	
	private void loadUnion(Node node) throws SchemaComponentException {
		NamedNodeMap attribs = node.getAttributes();
		for(int i = 0; i < attribs.getLength(); i++) {
			Node attrib = attribs.item(i);
			String attribName = attrib.getLocalName();
			String uri = attrib.getNamespaceURI();
			String value = attrib.getNodeValue();  	
			if(uri == null || uri.equals("")) {  	
				if(attribName.equals(NODE_MEMBER_TYPES_NAME)) {
					StringTokenizer tokenizer = new StringTokenizer(value);
					while(tokenizer.hasMoreTokens()) {
						String token = tokenizer.nextToken();
						TypeDefinitionBaseImpl typeDefBase = (TypeDefinitionBaseImpl)(Tools.getTopLevelComponent(schema, node, token, TYPE_DEFINITION_ID));
						initMemeberTypeDefinition(typeDefBase);
					}
				}
			}
		}
		
		NodeList childNodes = node.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				String uri = childNode.getNamespaceURI();
				String elemName = childNode.getLocalName();
				if(uri.equals(SchemaImpl.SCHEMA_COMPONENTS_NS)) {	
					if(elemName.equals(NODE_ANNOTATION_NAME)) {
					} else if(elemName.equals(NODE_SIMPLE_TYPE_NAME)) {
						initMemeberTypeDefinition(SchemaStructuresLoader.createSimpleTypeDefinition(childNode, schema, false, false));
					}
        }
			}
		}
    baseTypeDefinition = (SimpleTypeDefinitionImpl)(schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ANY_SIMPLE_TYPE_NAME));
		variety = UNION;
    fundamentalFacets = FundamentalFacetsUnion.newInstance();
	}
	
	private void initItemTypeDefinition(TypeDefinitionBaseImpl itemTypeDef) throws SchemaComponentException {
    if(!(itemTypeDef instanceof SimpleTypeDefinitionImpl)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of simple type " + DOM.toXPath(associatedNode) + " is not correct. The item type definition must be a simple type definition.");
    }
		itemTypeDefinition = (SimpleTypeDefinitionImpl)itemTypeDef;
		SchemaStructuresLoader.loadBase(itemTypeDefinition);
    if(itemTypeDefinition.isFinalList) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. The item type definition must not have {final} property set to 'list'.");
    }
		if(itemTypeDefinition.variety == LIST) {
			throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. The item type definition must have a {variety} of atomic or 'union'.");
		}
		if(itemTypeDefinition.variety == UNION) {
			for(int i = 0; i < itemTypeDefinition.memberTypeDefsCollector.size(); i++) {
				SimpleTypeDefinitionImpl memberTypeDef = (SimpleTypeDefinitionImpl)(itemTypeDefinition.memberTypeDefsCollector.get(i));
				if(memberTypeDef.variety != ATOMIC) {
					throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. The item type definition must have a {variety} of atomic or union(in which case all the {member type definitions} must be atomic).");
				}
			}
		} 	
	}
	
	private void initMemeberTypeDefinition(TypeDefinitionBaseImpl memberTypeDef) throws SchemaComponentException {
    if(!(memberTypeDef instanceof SimpleTypeDefinitionImpl)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Definition of simple type " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is union, then each member type definition must be a simple type definition.");
    }
    SimpleTypeDefinitionImpl memberSimpleTypeDef = (SimpleTypeDefinitionImpl)memberTypeDef;
		SchemaStructuresLoader.loadBase(memberSimpleTypeDef);
		if(memberSimpleTypeDef.variety == UNION) {
			throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is union, then the member type definitions must all have {variety} of 'atomic' or 'list'.");
		}
    if(memberSimpleTypeDef.isFinalUnion) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is union, then any of the member type definitions must not have {final} set to 'union'.");
    }
		memberTypeDefsCollector.add(memberTypeDef);
	}
	
  public boolean isDerivedFrom(TypeDefinitionBase typeDefinition, boolean disallowedRestriction, boolean disallowedExtension) {
    TypeDefinitionBaseImpl typeDefBase = (TypeDefinitionBaseImpl)typeDefinition;
  	if(typeDefBase == null) {
  		return(false);
  	}
    if(typeDefBase.isUrType) {
      return(true);
    }
  	if(this == typeDefBase || match(typeDefBase)) {
  		return(true);
  	}
    if(disallowedRestriction) {
      return(false);
    }

    if(!baseTypeDefinition.isUrType) {
      return(baseTypeDefinition.isDerivedFrom(typeDefBase, disallowedRestriction, disallowedExtension));
    }
    if(typeDefBase instanceof SimpleTypeDefinitionImpl) {
      SimpleTypeDefinitionImpl simpleTypeDef = (SimpleTypeDefinitionImpl)typeDefBase;
      if(simpleTypeDef.variety == UNION) {
        for(int i = 0; i < simpleTypeDef.memberTypeDefsCollector.size(); i++) {
          SimpleTypeDefinitionImpl memberTypeDef = (SimpleTypeDefinitionImpl)(simpleTypeDef.memberTypeDefsCollector.get(i));
          if(isDerivedFrom(memberTypeDef, disallowedRestriction, disallowedExtension)) {
            return(true);
          }
        }
      }
    }
  	return(false);
  }
  
  protected void addRestrictedFacet(FacetImpl facet, boolean removeEnumerationFactes, boolean removePatternFacets) throws SchemaComponentException {
  	if(variety == LIST && !facet.facetName.equals(FACET_LENGTH_NAME) &&
  		 !facet.facetName.equals(FACET_MIN_LENGTH_NAME) && !facet.facetName.equals(FACET_MAX_LENGTH_NAME) &&
  		 !facet.facetName.equals(FACET_PATTERN_NAME) && !facet.facetName.equals(FACET_ENUMERATION_NAME)) {
			throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is list, then only length, minLength, maxLength, pattern and enumeration facet components are allowed among the {facets}.");
  	}
    if(variety == UNION && !facet.facetName.equals(FACET_PATTERN_NAME) && !facet.facetName.equals(FACET_ENUMERATION_NAME)) {
      throw new SchemaComponentException("[location : '" + schema.getLocation() + "'] ERROR : Simple type definition " + DOM.toXPath(associatedNode) + " is not correct. If the {variety} is union, then only pattern and enumeration facet components are allowed among the {facets}.");
    }
    if(facet.facetName.equals(FACET_PATTERN_NAME)) {
    	if(removePatternFacets) {
				for(int i = 0; i < facetsCollector.size(); i++) {
					FacetImpl includedFacet = (FacetImpl)(facetsCollector.get(i));
					if(includedFacet.facetName.equals(FACET_PATTERN_NAME)) {
						facetsCollector.remove(i);
					}
				}
    	}
    } else if(facet.facetName.equals(FACET_ENUMERATION_NAME)) {
			if(removeEnumerationFactes) {
				for(int i = 0; i < facetsCollector.size(); i++) {
					FacetImpl includedFacet = (FacetImpl)(facetsCollector.get(i));
					if(includedFacet.facetName.equals(FACET_ENUMERATION_NAME)) {
						facetsCollector.remove(i);
					}
				}
			}
  	} else {
      if(facet.facetName.equals(FACET_WHITE_SPACE_NAME)) {
        whiteSpaceNormalizationValue = facet.valueString;
      }
  		for(int i = 0; i < facetsCollector.size(); i++) {
  			FacetImpl includedFacet = (FacetImpl)(facetsCollector.get(i));
  			if(includedFacet.facetName.equals(facet.facetName)) {
  				facetsCollector.set(i, facet);
  				return;
  			}
  		}
  	}
    
    if(!isBuiltIn && facet.facetName.equals(FACET_ENUMERATION_NAME) && ((SimpleTypeDefinitionImpl)baseTypeDefinition).primitiveTypeDefinition == schema.getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_NOTATION_NAME)) { 
      Tools.getTopLevelComponent(schema, facet.getAssociatedDOMNode(), facet.getValue(), NOTATION_DECLARATION_ID);
    }
		facetsCollector.add(facet);
  }

  public BaseImpl initializeBase(BaseImpl base, Hashtable clonedCollector) {
    SimpleTypeDefinitionImpl result = (SimpleTypeDefinitionImpl)(super.initializeBase(base, clonedCollector));
    result.variety = variety;
    result.primitiveTypeDefinition = primitiveTypeDefinition;
    if(itemTypeDefinition != null) {
      result.itemTypeDefinition = itemTypeDefinition.isBuiltIn ? itemTypeDefinition : (SimpleTypeDefinitionImpl)(itemTypeDefinition.clone(clonedCollector));
    }
    Tools.cloneVectorWithBases(memberTypeDefsCollector, result.memberTypeDefsCollector, clonedCollector);
    Tools.cloneVectorWithBases(facetsCollector, result.facetsCollector, clonedCollector);
    result.fundamentalFacets = fundamentalFacets;
    result.isPrimitive = isPrimitive;
    result.isFinalList = isFinalList;
    result.isFinalUnion = isFinalUnion;
    result.whiteSpaceNormalizationValue = whiteSpaceNormalizationValue;
    return(result);
  }
}

