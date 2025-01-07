package com.sap.engine.lib.schema.validator;

import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import com.sap.engine.lib.xml.dom.NodeImpl;
import com.sap.engine.lib.xml.dom.DOMDocHandler1;
import com.sap.engine.lib.xml.dom.AttrImpl;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.schema.components.impl.structures.SchemaImpl;
import com.sap.engine.lib.schema.components.impl.LoaderImpl;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.components.impl.ffacets.ValueComparator;
import com.sap.engine.lib.schema.components.*;
import com.sap.engine.lib.schema.validator.xpath.AttributeXPathStep;
import com.sap.engine.lib.schema.validator.xpath.ElementXPathStep;
import com.sap.engine.lib.schema.validator.xpath.XPath;
import com.sap.engine.lib.schema.validator.xpath.XPathStep;
import com.sap.engine.lib.schema.validator.automat.ContentAutomat;
import com.sap.engine.lib.schema.validator.automat.Switch;
import com.sap.engine.lib.schema.validator.identity.IdentityConstraintRegResult;
import com.sap.engine.lib.schema.validator.identity.IdentityConstraintsValidator;
import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.exception.SchemaException;
import com.sap.engine.lib.schema.exception.SchemaValidationException;

import java.net.URL;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.xml.transform.URIResolver;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-9-7c
 * Time: 10:00:50
 * To change this template use Options | File Templates.
 */
public final class SchemaDocHandler implements DocHandler, Constants {

  private ValidationConfigContext validationConfigContext;
  private XPath xPath;
  private Loader loader;
  private Vector errors;
  private NamespaceHandler namespaceHandler;
  private IdentityConstraintsValidator identConstrValidator;
  private Vector idValues;
  private Hashtable attribRepresentToIdrefValueMapping;
  private Hashtable attribRepresentToIdrefValuesMapping;
  private boolean canonicalProcessing;
  private boolean validateXSDDoc;
  private EntityResolver entityResolver;
  private ReusableObjectsPool reusableObjectsPool;
  private Vector attribUses;

  public SchemaDocHandler(ValidationConfigContext validationConfigContext) {
    this.validationConfigContext = validationConfigContext;
    reusableObjectsPool = new ReusableObjectsPool();
    xPath = new XPath(reusableObjectsPool);
    errors = new Vector();
    namespaceHandler = new NamespaceHandler();
    idValues = new Vector();
    attribRepresentToIdrefValueMapping = new Hashtable();
    attribRepresentToIdrefValuesMapping = new Hashtable();
    identConstrValidator = new IdentityConstraintsValidator(this);
    attribUses = new Vector();
    
  }
  
  public ReusableObjectsPool getReusableObjectsPool() {
    return(reusableObjectsPool);
  }

  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception {
    validationConfigContext.getDocHandler().onXMLDecl(version, encoding, ssdecl);
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    ElementXPathStep elemXPathStep = createElementXPathStep(uri, localName);
    xPath.addXPathStep(elemXPathStep);
    namespaceHandler.levelUp();
    validationConfigContext.getDocHandler().startElementStart(uri, localName, qname);
  }
  
  private ElementXPathStep createElementXPathStep(CharArray uri, CharArray localName) {
    ElementXPathStep elemXPathStep = reusableObjectsPool.getElementXPathStep();
    elemXPathStep.setUriStr(reusableObjectsPool.getString(uri));
    elemXPathStep.setLocalNameStr(reusableObjectsPool.getString(localName));
    elemXPathStep.setLine(validationConfigContext.getLocator().getLineNumber());
    elemXPathStep.setColumn(validationConfigContext.getLocator().getColumnNumber());
    return(elemXPathStep);
  }
  
  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    String attribUri = reusableObjectsPool.getString(uri);
    String attribLocalName = reusableObjectsPool.getString(localName);
    String attribValue = reusableObjectsPool.getString(value).trim();
    ElementXPathStep elemXPathStep = (ElementXPathStep)(xPath.getLastXPathStep());
    AttributeXPathStep attribXPathStep = null;
    if(attribUri.equals(SCHEMA_INSTANCE_COMPONENTS_NS)) {
      if(attribLocalName.equals(NODE_SCHEMA_LOCATION_NAME)) {
        attribXPathStep = initSchema(uri, prefix, localName, qname, value, type, attribUri, attribLocalName, attribValue, elemXPathStep.getUriStr(), true);
      } else if(attribLocalName.equals(Constants.NODE_NO_NAMESPACE_SCHEMA_LOCATION_NAME)) {
        attribXPathStep = initSchema(uri, prefix, localName, qname, value, type, attribUri, attribLocalName, attribValue, elemXPathStep.getUriStr(), false);
      } else if(attribLocalName.equals(XSI_TYPE_ATTRIB_NAME)) {
        elemXPathStep.setXsiTypeAttributeValue(attribValue);
      } else if(attribLocalName.equals(XSI_NIL_ATTRIB_NAME)) {
        elemXPathStep.setXsiNillAttributeValue(attribValue);
      } else {
        attribXPathStep = createAttributeXPathStep(uri, prefix, localName, qname, type, value, attribUri, attribLocalName, attribValue);
      }
    } else if(reusableObjectsPool.getString(qname).equals(XMLNS_ATTRIB_NAME)) {
      namespaceHandler.addUri("", attribValue);
    } else if(attribUri.equals(NAMESPACE_ATTRIBS_NAMESPACE)) {
      namespaceHandler.addUri(attribLocalName, attribValue);
    } else {
      attribXPathStep = createAttributeXPathStep(uri, prefix, localName, qname, type, value, attribUri, attribLocalName, attribValue);
    }
    if(attribXPathStep != null) {
      elemXPathStep.addAttributeXPathStep(attribXPathStep);
    } else {
      validationConfigContext.getDocHandler().addAttribute(uri, prefix, localName, qname, type, value);
    }
  }
  
  private AttributeXPathStep initSchema(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, CharArray value, String type, String attribUri, String attribLocalName, String attribValue, String parentUri, boolean hasNamespace) throws Exception {
    AttributeXPathStep attribXPathStep = null;
    if(xPath.getXPathStepsCount() == 1) {
      if(validationConfigContext.getSchema() == null || !validationConfigContext.getSchema().getTargetNamespace().equals(parentUri)) {
        validationConfigContext.setSchema(hasNamespace ? loadSchema_Namespace(attribValue) : loadSchema_NoNamespace(attribValue));
      }
    } else {
      attribXPathStep = createAttributeXPathStep(uri, prefix, localName, qname, type, value, attribUri, attribLocalName, attribValue);
    }
    return(attribXPathStep);
  }
  
  private Object determineXSDDocSourceObject(String xsdDocSystemId) throws Exception {
    String xmlDocSystemId = validationConfigContext.getLocator().getSystemId();
    Loader loader = determineLoader();
    URIResolver uriResolver = loader.getUriResolver();
    EntityResolver entityResolver = loader.getEntityResolver();
    if((uriResolver != null && uriResolver.resolve(xsdDocSystemId, xmlDocSystemId) != null) ||
       (entityResolver != null && entityResolver.resolveEntity(null, xsdDocSystemId) != null)) {
      return(xsdDocSystemId);
    }
    URL xmlDocURL = null;
    if(xmlDocSystemId != null) {
      xmlDocURL = URLLoader.fileOrURLToURL(null, xmlDocSystemId);
    }
    URL xsdDocURL = URLLoader.fileOrURLToURL(xmlDocURL, xsdDocSystemId);
    InputSource inputSource = new InputSource();
    inputSource.setSystemId(xsdDocURL.toExternalForm());
    inputSource.setByteStream(xsdDocURL.openStream());
    return(inputSource);
  }

  private SchemaImpl loadSchema_Namespace(String location) throws Exception {
    StringTokenizer strTokenizer = new StringTokenizer(location);
    int countTokens = strTokenizer.countTokens() / 2;
    if(strTokenizer.countTokens() % 2 != 0) {
      throw new SchemaException("Incorrect initializing of external schema locations. The count of the relevant target namespaces must be equal to the count of the schema locations.");
    }
    String[] namespaces = new String[countTokens];
    Object[] xsdDocSourceObjects = new Object[countTokens];
    int index = 0;
    while(strTokenizer.hasMoreTokens()) {
      namespaces[index] = strTokenizer.nextToken();
      xsdDocSourceObjects[index] = determineXSDDocSourceObject(strTokenizer.nextToken());
      index++;
    }
    return((SchemaImpl)(determineLoader().load(namespaces, xsdDocSourceObjects)));
  }

  private SchemaImpl loadSchema_NoNamespace(String location) throws Exception {
    return((SchemaImpl)determineLoader().load(determineXSDDocSourceObject(location)));
  }

  private AttributeXPathStep createAttributeXPathStep(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value, String uriStr, String localNameStr, String valueStr) {
    AttributeXPathStep attribXPathStep = reusableObjectsPool.getAttributeXPathStep();
    attribXPathStep.setUri(uri);
    attribXPathStep.setPrefix(prefix);
    attribXPathStep.setLocalName(localName);
    attribXPathStep.setQName(qname);
    attribXPathStep.setType(type);
    attribXPathStep.setValue(value);
    attribXPathStep.setUriStr(uriStr);
    attribXPathStep.setLocalNameStr(localNameStr);
    attribXPathStep.setValueStr(valueStr);
    attribXPathStep.setLine(validationConfigContext.getLocator().getLineNumber());
    attribXPathStep.setColumn(validationConfigContext.getLocator().getColumnNumber());
    return(attribXPathStep);
  }
  
  public void startElementEnd(boolean isEmpty) throws Exception {
    ElementXPathStep elemXPathStep = (ElementXPathStep)(xPath.getLastXPathStep());
    ElementXPathStep parentElemXPathStep = xPath.getParentElementXPathStep();
		boolean processAttributes = false;
		
		try {
			String uri = elemXPathStep.getUriStr();
			String localName = elemXPathStep.getLocalNameStr();
			ModelGroup modelGroup_All = null;
			  
			if(parentElemXPathStep == null || parentElemXPathStep.isLax() || !parentElemXPathStep.isSkip()) {
        
        if(parentElemXPathStep != null && !parentElemXPathStep.isSkip()) {
          if(parentElemXPathStep.isNil()) {
            initParentElementXPathStep_Error(parentElemXPathStep);
            initElementXPathStep_Error("Element is declared to be nil. No children elements are allowed.", parentElemXPathStep);
            return;
          }
          if(determineSimpleTypeDefinition(parentElemXPathStep.getTypeDefinition()) != null) {
            initParentElementXPathStep_Error(parentElemXPathStep);
            initElementXPathStep_Error("Element is declared to be with simple type. No children elements are allowed.", parentElemXPathStep);
            return;
          }
          ElementDeclaration parentElemDeclaration = (ElementDeclaration)(parentElemXPathStep.getElementDeclaration());
          if(parentElemDeclaration != null && parentElemDeclaration.getValueConstraintFixed() != null) {
            initParentElementXPathStep_Error(parentElemXPathStep);
            initElementXPathStep_Error("Element is declared to be with simple type and fixed value. No children elements are allowed.", parentElemXPathStep);
            return;
          }
        }
        
				boolean isSubstitution = false;
				ElementDeclaration elemDeclaration = null;
        ContentAutomat contentAutomat = null;
				if(parentElemXPathStep == null) {
					if(validationConfigContext.getSchema() == null) {
            if(validationConfigContext.getDynamicValidationFeature()) {
              validationConfigContext.getParser().setDocHandler(validationConfigContext.getDocHandler());
              return;
            }
						throw new SchemaValidationException("External schema is not set.");
					}
					elemDeclaration = validationConfigContext.getSchema().getTopLevelElementDeclaration(uri, localName);
				} else if(parentElemXPathStep.isLax()) {
					elemDeclaration = validationConfigContext.getSchema().getTopLevelElementDeclaration(uri, localName);
					initElementXPathStep_LaxContent(elemXPathStep, elemDeclaration == null);
				} else {
					contentAutomat = parentElemXPathStep.getContentAutomat();
          Switch _switch = contentAutomat.switchState(uri, localName);
					if(_switch != null) {
            Base base = _switch.base;
            modelGroup_All = _switch.scopeModelGroupAll;
            isSubstitution = _switch.isSubstitution;
                        
						if(base instanceof Wildcard) {
							Wildcard wildcard = (Wildcard)base;
							validateWildcardAllowanceNamespace(wildcard, elemXPathStep);
							if(wildcard.isProcessContentsLax()) {
								elemDeclaration = validationConfigContext.getSchema().getTopLevelElementDeclaration(uri, localName);
								initElementXPathStep_LaxContent(elemXPathStep, elemDeclaration == null);
							} else if(wildcard.isProcessContentsSkip()) {
								elemXPathStep.setSkip(true);
							} else {
								elemDeclaration = validationConfigContext.getSchema().getTopLevelElementDeclaration(uri, localName);
								if(elemDeclaration == null) {
									initElementXPathStep_Error("Element is matched to a wildcard, whose {process contents} property is 'strict'. Top level element declaration with such an uri and name is not defined.", elemXPathStep);
									return;
								}
							}
						} else {
							elemDeclaration = (ElementDeclaration)base;
						}
					} else {
//            initElementXPathStep_Error("Element is not allowed." + (contentAutomat == null ? "" : " Expected elements : " + contentAutomat.getExpected() + "."), elemXPathStep);
            initElementXPathStep_Error("Element is not allowed.", elemXPathStep);
            return;
          }
				}

				if(!elemXPathStep.isSkip()) {
					elemXPathStep.setElementDeclaration(elemDeclaration);
					String xsiTypeAttribValue = elemXPathStep.getXsiTypeAttributeValue();
					TypeDefinitionBase typeDefinition = null;
					Vector identConstrDefinitions = null;
					if(xsiTypeAttribValue != null) {
						String xsiTypeUri = null;
						String xsiTypeName = null;
						String[] prefixAndTypeName = Tools.parseQName(xsiTypeAttribValue);
						String prefix = prefixAndTypeName[0];
						if(prefix == null) {
							xsiTypeUri = namespaceHandler.getUri("");
							if(xsiTypeUri == null) {
								xsiTypeUri = "";
							}
						} else {
							xsiTypeUri = namespaceHandler.getUri(prefix);
							if(xsiTypeUri == null) {
								initElementXPathStep_Error("Element is declared to be with '" + xsiTypeAttribValue + "' type definition. The prefix is not mapped to any uri.", elemXPathStep);
								return;
							}
						}
						xsiTypeName = prefixAndTypeName[1];
						if(xsiTypeName == null) {
							initElementXPathStep_Error("Element is declared to be with '" + xsiTypeAttribValue + "' type definition. Local name is not specified.", elemXPathStep);
							return;
						}
						typeDefinition = validationConfigContext.getSchema().getTopLevelTypeDefinition(xsiTypeUri, xsiTypeName);
						if(typeDefinition == null) {
							initElementXPathStep_Error("Element is declared to be with '" + xsiTypeAttribValue + "' type definition. Top level type definition (uri : " + xsiTypeUri + "; name : " + xsiTypeName + ") is not defined.", elemXPathStep);
							return;
						}
						if(elemDeclaration != null) {
							boolean disallowedRestriction = elemDeclaration.isDisallowedSubstitutionRestriction();
							boolean disallowedExtension = elemDeclaration.isDisallowedSubstitutionExtension();
							TypeDefinitionBase elemDeclrTypeDefinition = elemDeclaration.getTypeDefinition();
							if(elemDeclrTypeDefinition instanceof ComplexTypeDefinition) {
								disallowedRestriction = disallowedRestriction || ((ComplexTypeDefinition)elemDeclrTypeDefinition).isProhibitedSubstitutionRestriction();
								disallowedExtension = disallowedExtension || ((ComplexTypeDefinition)elemDeclrTypeDefinition).isProhibitedSubstitutionExtension();
							}
							if(!typeDefinition.isDerivedFrom(elemDeclrTypeDefinition, disallowedRestriction, disallowedExtension)) {
								initElementXPathStep_Error("Element is declared to be with '" + xsiTypeAttribValue + "' type definition. This type definition is not validily derived from the corresponding element declaration's type definition.", elemXPathStep);
								return;
							}
						}
					} else if(elemDeclaration != null) {
						typeDefinition = elemDeclaration.getTypeDefinition();
					}

					if(typeDefinition == null) {
            initElementXPathStep_Error("Element is not allowed." + (contentAutomat == null ? "" : " Expected children elements : " + contentAutomat.getExpected() + "."), elemXPathStep);
						return;
					} 
					elemXPathStep.setTypeDefinition(typeDefinition);
					if(elemDeclaration != null) {
						if(isSubstitution) {
							ElementDeclaration headElemDeclaration = elemDeclaration.getSubstitutionGroupAffiliation();
							boolean disallowedRestriction = headElemDeclaration.isDisallowedSubstitutionRestriction();
							boolean disallowedExtension = headElemDeclaration.isDisallowedSubstitutionExtension();
							TypeDefinitionBase headElemDeclrTypeDef = headElemDeclaration.getTypeDefinition();
							if(headElemDeclrTypeDef instanceof ComplexTypeDefinition) {
								disallowedRestriction = disallowedRestriction || ((ComplexTypeDefinition)headElemDeclrTypeDef).isProhibitedSubstitutionRestriction();
								disallowedExtension = disallowedExtension || ((ComplexTypeDefinition)headElemDeclrTypeDef).isProhibitedSubstitutionExtension();
							}
							if(!typeDefinition.isDerivedFrom(headElemDeclrTypeDef, disallowedRestriction, disallowedExtension)) {
								initElementXPathStep_Error("Element is a substitution. The type definition of this element is not validily derived from the type definition of the head element declaration.", elemXPathStep);
								return;
							}
							if(headElemDeclaration.isDisallowedSubstitutionSubstitution()) {
								initElementXPathStep_Error("Element is a substitution. The head element declaration has a substitution blocking constraint.", elemXPathStep);
								return;
							}
						}

						if(elemDeclaration.isAbstract()) {
							initElementXPathStep_Error("Element is matched to an element declaration whose {abstract} property is 'true'.", elemXPathStep);
							return;
						}

						identConstrDefinitions = elemDeclaration.getIdentityConstraintDefinitions();
						boolean isNillableElemDeclaration = elemDeclaration.isNillable();
						if(!isNillableElemDeclaration && elemXPathStep.getXsiNillAttributeValue() != null) {
							initElementXPathStep_Error("Element is matched to an element declaration whose {nillable} property is 'false'. Attribute {http://www.w3.org/2001/XMLSchema-instance}:nil is not allowed.", elemXPathStep);
							return;
						}
						String xsiNilAttribValue = elemXPathStep.getXsiNillAttributeValue();
						elemXPathStep.setNil(isNillableElemDeclaration && xsiNilAttribValue != null && xsiNilAttribValue.equals("true"));
						if(!elemXPathStep.isNil() && identConstrDefinitions != null && identConstrDefinitions.size() != 0) {
							identConstrValidator.registerValidators(identConstrDefinitions, xPath.getXPathSteps().size());
						}
					}

					ComplexTypeDefinition complTypeDefinition = determineComplexTypeDefinition(elemXPathStep.getTypeDefinition());
					Vector attribXPathSteps = elemXPathStep.getAttributeXPathSteps();
					if(complTypeDefinition != null) {
						if(complTypeDefinition.isAbstract()) {
							initElementXPathStep_Error("Element is declared to be with complex type whose {abstract} property is 'true'.", elemXPathStep);
							return;
						}
						elemXPathStep.setContentAutomat(reusableObjectsPool.getContentAutomat(complTypeDefinition));
						Wildcard wildcard = complTypeDefinition.getAttributeWildcard();
						complTypeDefinition.getAttributeUses(attribUses);
						processAttributes = true;
            while(attribXPathSteps.size() > 0) {
              AttributeXPathStep attribXPathStep = (AttributeXPathStep)(attribXPathSteps.lastElement());
							String attribUri = attribXPathStep.getUriStr();
							String attribName = attribXPathStep.getLocalNameStr();
							AttributeDeclaration validationAttribDeclaration = null;
							String attribUseFixedValue = null;
							boolean isProhibited = false;
							for(int j = 0; j < attribUses.size(); j++) {
								AttributeUse attribUse = (AttributeUse)(attribUses.get(j));
								AttributeDeclaration attribDeclaration = attribUse.getAttributeDeclaration();
								if(attribUri.equals(attribDeclaration.getTargetNamespace()) && attribName.equals(attribDeclaration.getName())) {
									if(attribUse.isProhibited()) {
										collectError(attribXPathStep.getRepresentation(), "Attribute is prohibited.");
										isProhibited = true;
									} else { 
										validationAttribDeclaration = attribDeclaration;
										attribUseFixedValue = attribUse.getValueConstraintFixed();
									}
									attribUses.remove(j);
									break;
								}
							}
							boolean invokeDocHandler_AddAttribute = true;
              xPath.addXPathStep(attribXPathStep);
							if(!isProhibited) {
								if(validationAttribDeclaration != null) {
									validateAttributeDeclaration(attribXPathStep, validationAttribDeclaration, attribUseFixedValue);
									invokeDocHandler_AddAttribute = false;
								} else if(wildcard != null) {
									if(validateWildcardAllowanceNamespace(wildcard, attribXPathStep)) {
										if(wildcard.isProcessContentsStrict()) {
											AttributeDeclaration attribDeclaration = validationConfigContext.getSchema().getTopLevelAttributeDeclaration(attribUri, attribName);
											if(attribDeclaration == null) {
												collectError(attribXPathStep.getRepresentation(), "Attribute is matched to a wildcard whose {process contents} property is 'strict'. Top level attribute declaration with such an uri and name is not declared.");
											} else {
												validateAttributeDeclaration(attribXPathStep, attribDeclaration, attribUseFixedValue);
												invokeDocHandler_AddAttribute = false;
											}
										} else if(wildcard.isProcessContentsLax()) {
											AttributeDeclaration attribDeclaration = validationConfigContext.getSchema().getTopLevelAttributeDeclaration(attribUri, attribName);
											if(attribDeclaration != null) {
												validateAttributeDeclaration(attribXPathStep, attribDeclaration, attribUseFixedValue);
												invokeDocHandler_AddAttribute = false;
											} 
										} 
									}
								} else {
									collectError(attribXPathStep.getRepresentation(), "Attribute is not allowed.");
								}
							}
							if(invokeDocHandler_AddAttribute) {
                validationConfigContext.getDocHandler().addAttribute(attribXPathStep.getUri(), attribXPathStep.getPrefix(), attribXPathStep.getLocalName(), attribXPathStep.getQName(), attribXPathStep.getType(), attribXPathStep.getValue());
							}
              xPath.removeXPathStep();
						}
						for(int i = 0; i < attribUses.size(); i++) {
							AttributeUse attribUse = (AttributeUse)(attribUses.get(i));
							if(attribUse.isRequired()) {
								collectError(elemXPathStep.getRepresentation(), "Element is not valid. Attribute {" + attribUse.getAttributeDeclaration().getTargetNamespace() + "}:" + attribUse.getAttributeDeclaration().getName() + " is required.");
							} else {
								AttributeDeclaration attribDeclaration = attribUse.getAttributeDeclaration();
								String constraintValue = attribDeclaration.getValueConstraintDefault();
								if(constraintValue == null) {
									constraintValue = attribDeclaration.getValueConstraintFixed();
								} 
								if(constraintValue != null) {
									CharArray attribUri= reusableObjectsPool.getCharArray(attribDeclaration.getTargetNamespace());
									CharArray attribPrefix = reusableObjectsPool.getCharArray("");
									CharArray ayttribLocalName = reusableObjectsPool.getCharArray(attribDeclaration.getName());
									CharArray attribQNname = reusableObjectsPool.getCharArray(attribDeclaration.getName());
									String attribType = "";
									CharArray attribValue = reusableObjectsPool.getCharArray(constraintValue);
									validationConfigContext.getDocHandler().addAttribute(attribUri, attribPrefix, ayttribLocalName, attribQNname, attribType, attribValue);
								}
							}
						}
            attribUses.clear();
					} else if(attribXPathSteps.size() != 0) {
						collectError(elemXPathStep.getRepresentation(), "Element is declared to be with simple type. No attributes are allowed.");
					}
				}
			} else {
				elemXPathStep.setSkip(true);
			}
      augmentNodeForCanonicalizationProcessing_ElementDeclaration(elemXPathStep, modelGroup_All);
		} finally {
			if(!processAttributes) {
				Vector attribXPathSteps = elemXPathStep.getAttributeXPathSteps();
				for(int i = 0; i < attribXPathSteps.size(); i++) {
					AttributeXPathStep attribXPathStep = (AttributeXPathStep)(attribXPathSteps.get(i));
					validationConfigContext.getDocHandler().addAttribute(attribXPathStep.getUri(), attribXPathStep.getPrefix(), attribXPathStep.getLocalName(), attribXPathStep.getQName(), attribXPathStep.getType(), attribXPathStep.getValue());
				}
			}
			validationConfigContext.getDocHandler().startElementEnd(isEmpty);
		}
  }
  
  private void augmentNodeForCanonicalizationProcessing_ElementDeclaration(ElementXPathStep elemXPathStep, ModelGroup modelGroup_All) {
    if(canonicalProcessing && !elemXPathStep.isSkip() && validationConfigContext.getDocHandler() instanceof DOMDocHandler1) {
      NodeImpl node = ((DOMDocHandler1)(validationConfigContext.getDocHandler())).getElement();
      node.setAugmentation(AUG_TYPE_DEFINITION, elemXPathStep.getTypeDefinition());
      ElementDeclaration elemDeclaration = elemXPathStep.getElementDeclaration();
      node.setAugmentation(AUG_ELEMENT_DECLARATION, elemDeclaration);
      node.setAugmentation(AUG_ANNOTATION, elemDeclaration != null ? elemDeclaration.getAnnotation() : null);
      node.setAugmentation(AUG_VALIDATING_MODEL_GROUP_ALL, modelGroup_All);
      NamedNodeMap namedNodeMap = node.getAttributes();
      for(int i = 0; i < namedNodeMap.getLength(); i++) {
        NodeImpl attr = (NodeImpl)(namedNodeMap.item(i));
        if(attr.getAugmentation(AUG_VALIDATION_ATTEMPTED) == null) {
          node.setAugmentation(AUG_VALIDATION_ATTEMPTED, AUG_VALIDATION_ATTEMPTED_VALUE_PARTIAL);
          break;
        }
      }
      if(node.getAugmentation(AUG_VALIDATION_ATTEMPTED) == null) {
        NodeList nodeList = node.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
          com.sap.engine.lib.xml.dom.Base childNode = (com.sap.engine.lib.xml.dom.Base)(nodeList.item(i));
          if(childNode instanceof Element) {
            if(childNode.getAugmentation(AUG_VALIDATION_ATTEMPTED) == null) {
              node.setAugmentation(AUG_VALIDATION_ATTEMPTED, AUG_VALIDATION_ATTEMPTED_VALUE_PARTIAL);
              break;
            }
          }
        }
      }
      if(node.getAugmentation(AUG_VALIDATION_ATTEMPTED) == null) {
        node.setAugmentation(AUG_VALIDATION_ATTEMPTED, AUG_VALIDATION_ATTEMPTED_VALUE_FULL);
      }
    }
  }
  
  private void initElementXPathStep_Error(String errorMessage, ElementXPathStep elemXPathStep) throws Exception {
  	collectError(elemXPathStep.getRepresentation(), errorMessage);
    elemXPathStep.setLax(false);
    elemXPathStep.setSkip(true);
    elemXPathStep.setContentAutomat(null);
    elemXPathStep.setElementDeclaration(null);
    elemXPathStep.setTypeDefinition(null);
  }
  
	private void initParentElementXPathStep_Error(ElementXPathStep parentElemXPathStep) throws Exception {
    parentElemXPathStep.setLax(false);
    parentElemXPathStep.setSkip(true);
	}
  
  private void initElementXPathStep_LaxContent(ElementXPathStep elemXPathStep, boolean isLax) {
    elemXPathStep.setLax(isLax);
    elemXPathStep.setSkip(isLax);
  }
	
  private void validateAttributeDeclaration(AttributeXPathStep attribXPathStep, AttributeDeclaration attribDeclaration, String attribUseFixedValue) throws Exception {
    String initialValue = attribXPathStep.getValueStr();
		SimpleTypeDefinition typeDefinition = (SimpleTypeDefinition)(attribDeclaration.getTypeDefinition());
		String normalizedValue = reusableObjectsPool.getNormalizedString(initialValue, typeDefinition.getWhiteSpaceNormalizationValue());
		validationConfigContext.getDocHandler().addAttribute(attribXPathStep.getUri(), attribXPathStep.getPrefix(), attribXPathStep.getLocalName(), attribXPathStep.getQName(), attribXPathStep.getType(), reusableObjectsPool.getCharArray(normalizedValue));
    if(!(attribXPathStep.getUriStr().equals(attribDeclaration.getTargetNamespace()) && attribXPathStep.getLocalNameStr().equals(attribDeclaration.getName()))) {
      collectError(attribXPathStep.getRepresentation(), "The namespace and name do not match.");
      return;
    } 
    AttrImpl attr = (validationConfigContext.getDocHandler() instanceof DOMDocHandler1) ? ((DOMDocHandler1)(validationConfigContext.getDocHandler())).getAttr() : null;
    String fixedValue = attribUseFixedValue == null ? attribDeclaration.getValueConstraintFixed() : attribUseFixedValue;
    if(!validateData(initialValue, normalizedValue, typeDefinition, fixedValue, attribXPathStep.getRepresentation(), false, attr)) {
    	return;
    }
    augmentNodeForCanonicalizationProcessing_AttributeDeclaration(attr, attribDeclaration, typeDefinition);
  }
  
  private void augmentNodeForCanonicalizationProcessing_AttributeDeclaration(AttrImpl attr, AttributeDeclaration attribDeclaration, TypeDefinitionBase typeDefinition) {
    if(canonicalProcessing && attr != null) {
      attr.setAugmentation(AUG_ATTRIBUTE_DECLARATION, attribDeclaration);
      attr.setAugmentation(AUG_TYPE_DEFINITION, typeDefinition);
      attr.setAugmentation(AUG_ANNOTATION, attribDeclaration.getAnnotation());
      attr.setAugmentation(AUG_VALIDATION_ATTEMPTED, AUG_VALIDATION_ATTEMPTED_VALUE_FULL);
    }
  }

  private boolean validateWildcardAllowanceNamespace(Wildcard wildcard, XPathStep xPathStep) throws SAXException {
    String uri = xPathStep.getUriStr();
  	if(wildcard.isNamespaceConstraintAny()) {
  		return(true);
  	}
		String constraintNegated = wildcard.getNamespaceConstraintNegated();
		if(constraintNegated != null && !uri.equals(constraintNegated)) {
			return(true);
		}
		String[] namespaceConstrMembers = wildcard.getNamespaceConstraintMembersAsArray();
		if(namespaceConstrMembers != null) {
			for(int i = 0; i < namespaceConstrMembers.length; i++) {
				if(uri.equals(namespaceConstrMembers[i])) {
					return(true);
				}
			}
		}
		collectError(xPathStep.getRepresentation(), "Information item is matched to a wildcard. The uri '" + uri + "' is not allowed according to the wildcard's {namespace constraint} property.");
		return(false);
  }
  
  private boolean validateData(String initialValue, String normalizedValue, SimpleTypeDefinition simpleTypeDefinition, String fixedValue, String nodeRepresent, boolean isNillableElemDeclaration, NodeImpl node) throws SAXException {
    Value fFacetsNormalizedValue = null;
    Value fFacetsFixedValue = null;
    boolean fFacetsNormalizedValueIsRegistered = false;
    try {
      FundamentalFacets fFacets = simpleTypeDefinition.getFundamentalFacets();
      fFacetsNormalizedValue = fFacets.parse(normalizedValue, reusableObjectsPool);
      if(fixedValue != null) {
        fFacetsFixedValue = fFacets.parse(fixedValue, reusableObjectsPool);
      }
      if(!SimpleTypeValidator.validateSimpleTypeDefinition(simpleTypeDefinition, this, fFacetsNormalizedValue, normalizedValue, nodeRepresent, node)) {
        return(false);
      } 
      if(fFacetsFixedValue != null && ValueComparator.compare(fFacetsFixedValue, fFacetsNormalizedValue) != COMPARE_RESULT_EQUAL) {
        collectError(nodeRepresent, "Node is associated with declaration, whose {fixed value constraint} property is '" + fFacetsFixedValue.getValue() + "'. Value '" + initialValue + "' should be identical to this constraint fixed value.");
        return(false);
      }
      
      IdentityConstraintRegResult regResult = identConstrValidator.registerValue(fFacetsNormalizedValue, normalizedValue, nodeRepresent);
      fFacetsNormalizedValueIsRegistered = regResult.valueIsRegistered();
      boolean identConstrRegFail = regResult.registraitionFail();
      reusableObjectsPool.reuseIdentityConstraintRegResult(regResult);
      if(!registerValueForIdentityValidation_ID_IDREF_IDREFS(simpleTypeDefinition, normalizedValue, nodeRepresent) || identConstrRegFail) {
        collectError(nodeRepresent, "The value '" + normalizedValue + "' can not be registered for identity constraint validation.");
        return(false);
      }
      augmentNodeForCanonicalizationProcessing_Value(node, initialValue, normalizedValue);
      return(true);
    } finally {
      if(!fFacetsNormalizedValueIsRegistered) {
        reusableObjectsPool.reuseFFacetValue(fFacetsNormalizedValue);
      }
      reusableObjectsPool.reuseFFacetValue(fFacetsFixedValue);
    }
  }
  
  private void augmentNodeForCanonicalizationProcessing_Value(NodeImpl node, String initialValue, String normalizedValue) {
    if(canonicalProcessing && node != null) {
      node.setAugmentation(AUG_NORMALIZED_VALUE, initialValue);
      node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, normalizedValue);
    }
  }

  private boolean registerValueForIdentityValidation_ID_IDREF_IDREFS(TypeDefinitionBase typeDefinition, String value, String nodeRepresent) throws SAXException {
		if(typeDefinition != null) {
			if(typeDefinition.isDerivedFrom(validationConfigContext.getSchema().getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_ID_NAME), false, false)) {
				if(idValues.contains(value)) {
					collectError(nodeRepresent, "Attribute is declared to be with type derived form {http://www.w3.org/2001/XMLSchema}:ID. It's value '" + value + "' is not unique.");
					return(false);
				} 
				idValues.add(value);
			} else if(typeDefinition.isDerivedFrom(validationConfigContext.getSchema().getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_IDREF_NAME), false, false)) {
        attribRepresentToIdrefValueMapping.put(nodeRepresent, value);
			} else if(typeDefinition.isDerivedFrom(validationConfigContext.getSchema().getTopLevelTypeDefinition(SCHEMA_COMPONENTS_NS, TYPE_IDREFS_NAME), false, false)) {
        attribRepresentToIdrefValuesMapping.put(nodeRepresent, value);
			}
		}
    return(true);
  }

  private SimpleTypeDefinition determineSimpleTypeDefinition(TypeDefinitionBase typeDefBase) {
    if(typeDefBase instanceof SimpleTypeDefinition) {
      return((SimpleTypeDefinition)typeDefBase);
    }
    return(((ComplexTypeDefinition)typeDefBase).getContentTypeSimpleTypeDefinition());
  }

  private ComplexTypeDefinition determineComplexTypeDefinition(TypeDefinitionBase typeDefBase) {
    if(typeDefBase instanceof ComplexTypeDefinition) {
      return((ComplexTypeDefinition)typeDefBase);
    }
    return(null);
  }
  
  private void validateIdentityConstraintDefinitions(ElementDeclaration elemDeclaration, ElementXPathStep elemXPathStep) throws SAXException {
    Vector identityConstrDefinitions = elemDeclaration.getIdentityConstraintDefinitions();
    if(!elemXPathStep.isNil() && identityConstrDefinitions != null && identityConstrDefinitions.size() != 0) {
      if(!identConstrValidator.validate(identityConstrDefinitions)) {
        collectError(elemXPathStep.getRepresentation(), "Element is not valid according to the defined identity constraints.");
      }
      identConstrValidator.unregisterValidators(identityConstrDefinitions);
    }
  }
  
  private void validateComplexContent(ElementXPathStep elemXPathStep) throws SAXException {
    ContentAutomat contentAutomat = elemXPathStep.getContentAutomat();
    if(contentAutomat != null) {
      if(!contentAutomat.isSatisfied() && !elemXPathStep.isNil()) {
        collectError(elemXPathStep.getRepresentation(), "Element is not valid according to it's complex type. Expected children elements : " + contentAutomat.getExpected() + ".");
      }
      contentAutomat.reset();
    }
  }
  
  private void validateSimpleContent(ElementXPathStep elemXPathStep, SimpleTypeDefinition simpleTypeDefinition, String fixedValue, String defaultValue) throws Exception {
    CharArray data = elemXPathStep.getValue();
    CharArray docHandlerInvokationData = data;
    NodeImpl node = (validationConfigContext.getDocHandler() instanceof DOMDocHandler1) ? ((DOMDocHandler1)(validationConfigContext.getDocHandler())).getCurrentNode() : null;
    
    String constrValue = null;
    if(fixedValue != null) {
      constrValue = fixedValue;
    } else {
      constrValue = defaultValue;
    }
    
    if(elemXPathStep.isNil() && data != null) {
      collectError(elemXPathStep.getRepresentation(), "Element is declared to be nil. No character children are allowed.");
    }
    
    if(constrValue != null && data == null) {
      FundamentalFacets fFacets = simpleTypeDefinition.getFundamentalFacets();
      Value fFacetsConstrValue = fFacets.parse(constrValue, reusableObjectsPool);
      if(!SimpleTypeValidator.validateSimpleTypeDefinition(simpleTypeDefinition, this, fFacetsConstrValue, constrValue, elemXPathStep.getRepresentation(), null)) {
        collectError(elemXPathStep.getRepresentation(), "Element is matched to an element declaration whose {value constraint} property is '" + constrValue + "'. This value is not valid according to the element's simple type definition.");
      }
      reusableObjectsPool.reuseFFacetValue(fFacetsConstrValue);
      docHandlerInvokationData = reusableObjectsPool.getCharArray(constrValue);
      augmentNodeForCanonicalizatinProcessing_ValueConstraint(node, constrValue);
    } else if(!elemXPathStep.isNil()) {
      String initialValue = data == null ? "" : reusableObjectsPool.getString(data);
      String normalizedValue = reusableObjectsPool.getNormalizedString(initialValue, simpleTypeDefinition.getWhiteSpaceNormalizationValue());
      validateData(initialValue, normalizedValue, simpleTypeDefinition, fixedValue, elemXPathStep.getRepresentation(), elemXPathStep.isNil(), node);
      docHandlerInvokationData = reusableObjectsPool.getCharArray(normalizedValue);
    }
    if(docHandlerInvokationData != null) {
      invokeDocHandler_CharData(docHandlerInvokationData, elemXPathStep.getBDisableOutputEscaping(), elemXPathStep.hasTextNodeChild());
    }
  }
  
  private void augmentNodeForCanonicalizatinProcessing_ValueConstraint(NodeImpl node, String valueConstraint) {
    if(canonicalProcessing && node != null) {
      node.setAugmentation(AUG_NORMALIZED_VALUE, valueConstraint);
      node.setAugmentation(AUG_SCHEMA_NORMALIZED_VALUE, valueConstraint);
    }
  }
  
  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    ElementXPathStep elemXPathStep = (ElementXPathStep)(xPath.getLastXPathStep());
    if(!elemXPathStep.isSkip()) {
      ElementDeclaration elemDeclaration = elemXPathStep.getElementDeclaration();
      SimpleTypeDefinition simpleTypeDefinition = determineSimpleTypeDefinition(elemXPathStep.getTypeDefinition());
      String fixedValue = null;
      String defaultValue = null;
      Value fFacetsFixedValue = null;
      Value fFacetsDefaultValue = null;
      if(elemDeclaration != null) {
        fixedValue = elemDeclaration.getValueConstraintFixed();
        defaultValue = elemDeclaration.getValueConstraintDefault();
        validateIdentityConstraintDefinitions(elemDeclaration, elemXPathStep);
      }
      validateComplexContent(elemXPathStep);
      if(simpleTypeDefinition != null) {
        validateSimpleContent(elemXPathStep, simpleTypeDefinition, fixedValue, defaultValue);
      }
    }
		xPath.removeXPathStep();
    namespaceHandler.levelDown();
    validationConfigContext.getDocHandler().endElement(uri, localName, qname, isEmpty);
  }
  
  private void invokeDocHandler_CharData(CharArray data, boolean bDisableOutputEscaping, boolean isTextNode) throws Exception {
  	if(isTextNode) {
      validationConfigContext.getDocHandler().charData(data, bDisableOutputEscaping);
  	} else {
      validationConfigContext.getDocHandler().onCDSect(data);
  	}
  }

  public void startDocument() throws Exception {
    validationConfigContext.getDocHandler().startDocument();
  }

  public void endDocument() throws Exception {
    validateRegisteredValuesForIdentityValidation_ID_IDREF_IDREFS();
    validationConfigContext.getDocHandler().endDocument();
  }

  public void charData(CharArray data, boolean bDisableOutputEscaping) throws Exception {
  	processCharData(data, bDisableOutputEscaping, true);
  }
  
  private void processCharData(CharArray data, boolean bDisableOutputEscaping, boolean isTextNode) throws Exception {
  	boolean invokeDocHandler_CharData = true;
  	try {
      ElementXPathStep elemXPathStep = (ElementXPathStep)(xPath.getLastXPathStep());
			if(!elemXPathStep.isSkip()) {
				ElementDeclaration elemDeclaration = elemXPathStep.getElementDeclaration();
				ComplexTypeDefinition complTypeDefinition = determineComplexTypeDefinition(elemXPathStep.getTypeDefinition());
				SimpleTypeDefinition simpleTypeDefinition = determineSimpleTypeDefinition(elemXPathStep.getTypeDefinition());
				if(simpleTypeDefinition != null) {
          elemXPathStep.appendValue(data);
          elemXPathStep.setBDisableOutputEscaping(bDisableOutputEscaping);
          elemXPathStep.setHasTextNodeChild(isTextNode);
					invokeDocHandler_CharData = false;
				} else if(!isWhitespace(data)) {
					if(complTypeDefinition.isMixed()) {
						String initialValue = reusableObjectsPool.getString(data);
						if(elemXPathStep.isNil()) {
							collectError(elemXPathStep.getRepresentation(), "Element is declared to be nil. No character children are allowed.");
						}
						String valueConstraintFixed = elemDeclaration != null ? elemDeclaration.getValueConstraintFixed() : null;
						if(valueConstraintFixed != null) {
							if(!valueConstraintFixed.equals(initialValue)) {
								collectError(elemXPathStep.getRepresentation(), "Element is matched to an element declaration, whose {fixed value constraint} is '" + valueConstraintFixed + "'. Value '" + initialValue + "' is not identical to this constraint fixed value.");
							}
						}
            IdentityConstraintRegResult regResult = identConstrValidator.registerValue(null, reusableObjectsPool.getNormalizedString(initialValue, WHITE_SPACE_REPLACE_NORM_VALUE), elemXPathStep.getRepresentation());
            reusableObjectsPool.reuseIdentityConstraintRegResult(regResult);
					} else {
						collectError(elemXPathStep.getRepresentation(), "Element is declared to be with complex type. No character children are allowed.");
					}
				}
			}
  	} finally {
  		if(invokeDocHandler_CharData) {
  			invokeDocHandler_CharData(data, bDisableOutputEscaping, isTextNode);
  		}
  	}
  }

  private boolean isWhitespace(CharArray data) {
    char[] chars = data.getData();
    int offset = data.getOffset();
    for(int i = offset; i < data.getSize() + offset; i++) {
      char ch = chars[i];
      if(!Symbols.isWhitespace(ch)) {
        return(false);
      }
    }
    return(true);
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    validationConfigContext.getDocHandler().onPI(target, data);
  }

  public void onComment(CharArray text) throws Exception {
    validationConfigContext.getDocHandler().onComment(text);
  }

  public void onCDSect(CharArray text) throws Exception {
		processCharData(text, false, false);
  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {
    validationConfigContext.getDocHandler().onDTDElement(name, model);
  }

  public void onDTDAttListStart(CharArray name) throws Exception {
    validationConfigContext.getDocHandler().onDTDAttListStart(name);
  }

  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws Exception {
    validationConfigContext.getDocHandler().onDTDAttListItem(name, attname, type, defDecl, vAttValue, note);
  }

  public void onDTDAttListEnd() throws Exception {
    validationConfigContext.getDocHandler().onDTDAttListEnd();
  }

  public void onDTDEntity(Entity entity) throws Exception {
    validationConfigContext.getDocHandler().onDTDEntity(entity);
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {
    validationConfigContext.getDocHandler().onDTDNotation(name, pub, sys);
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
    validationConfigContext.getDocHandler().startDTD(name, pub, sys);
  }

  public void endDTD() throws Exception {
    validationConfigContext.getDocHandler().endDTD();
  }

  public void onContentReference(Reference ref) throws Exception {
    validationConfigContext.getDocHandler().onContentReference(ref);
  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {
    validationConfigContext.getDocHandler().startPrefixMapping(prefix, uri);
  }

  public void endPrefixMapping(CharArray prefix) throws Exception {
    validationConfigContext.getDocHandler().endPrefixMapping(prefix);
  }


  public void onWarning(String warning) throws Exception {
    validationConfigContext.getDocHandler().onWarning(warning);
  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {
    validationConfigContext.getDocHandler().onCustomEvent(eventId, obj);
  }

  public void onStartContentEntity(CharArray name, boolean isExpandingReferences) throws Exception {
    validationConfigContext.getDocHandler().onStartContentEntity(name, isExpandingReferences);
  }

  public void onEndContentEntity(CharArray name) throws Exception {
    validationConfigContext.getDocHandler().onEndContentEntity(name);
  }

  public void collectError(String nodeWrapperRepresent, String errorMessage) throws SAXException  {
    collectError(nodeWrapperRepresent + " : " + errorMessage);
  }
  
  public void collectError(String errorMessage) throws SAXException  {
    errors.add(errorMessage);
    if(validationConfigContext.getErrorHandler() != null) {
      validationConfigContext.getErrorHandler().error(new SAXParseException("Validation error : " + errorMessage, validationConfigContext.getLocator()));
    }
  }

  public String getErrorsRepresentation() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("\nERRORS : \n");
    if(errors.size() == 0) {
      buffer.append("none");
    } else {
      for(int i = 0; i < errors.size(); i++) {
        buffer.append(errors.get(i));
        buffer.append("\n");
      }
    }
    return(buffer.toString());
  }
  
  public boolean hasErrors() {
  	return(errors.size() != 0);
  }

  private Loader determineLoader() {
    if(loader == null) {
      loader = new LoaderImpl();
    }
    loader.setEntityResolver(entityResolver);
    loader.setValidateXSDDoc(validateXSDDoc);
    return(loader);
  }

  private void validateRegisteredValuesForIdentityValidation_ID_IDREF_IDREFS() throws SAXException {
    validateRegisteredValuesForIdentityValidation_ID_IDREF();
    validateRegisteredValuesForIdentityValidation_ID_IDREFS();
  }
  
  private void validateRegisteredValuesForIdentityValidation_ID_IDREF() throws SAXException {
    Enumeration enum1 = attribRepresentToIdrefValueMapping.keys();
    while(enum1.hasMoreElements()) {
      String attribRepresent = (String)(enum1.nextElement());
      String value = (String)(attribRepresentToIdrefValueMapping.get(attribRepresent));
      if(!idValues.contains(value)) {
        collectError(attribRepresent, "Attribute has a type, derived from the {http://www.w3.org/2001/XMLSchema}:IDREF. It's value '" + value + "' is not identical to any of the attribute values which type is derived from the {http://www.w3.org/2001/XMLSchema}:ID.");
      }
    }
  }

  private void validateRegisteredValuesForIdentityValidation_ID_IDREFS() throws SAXException {
    Enumeration enum1 = attribRepresentToIdrefValuesMapping.keys();
    while(enum1.hasMoreElements()) {
      String attribRepresent = (String)(enum1.nextElement());
      String values = (String)(attribRepresentToIdrefValuesMapping.get(attribRepresent));
      StringTokenizer valuesTokenizer = new StringTokenizer(values);
      while(valuesTokenizer.hasMoreTokens()) {
        String value = valuesTokenizer.nextToken();
        if(!idValues.contains(value)) {
          collectError(attribRepresent, "Attribute has a type, derived from {http://www.w3.org/2001/XMLSchema}:IDREFS. The value '" + value + "' is not identical to any of the attribute values which type is derived from the {http://www.w3.org/2001/XMLSchema}:ID.");
        }
      }
    }
  }

  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  public EntityResolver getEntityResolver(EntityResolver entityResolver) {
    return(entityResolver);
  }

  public XPath getXPath() {
    return(xPath);
  }

  public void setCanonicalizationProcessing(boolean canonicalProcessing) {
    this.canonicalProcessing = canonicalProcessing;
  }

  public boolean getCanonicalizationProcessing() {
    return(canonicalProcessing);
  }

  public void setValidateXSDDoc(boolean validateXSDDoc) {
    this.validateXSDDoc = validateXSDDoc;
  }

  public boolean getValidateXSDDoc() {
    return(validateXSDDoc);
  }
}
