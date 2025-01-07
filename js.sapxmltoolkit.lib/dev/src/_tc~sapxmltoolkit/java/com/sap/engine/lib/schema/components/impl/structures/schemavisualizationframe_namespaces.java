package com.sap.engine.lib.schema.components.impl.structures;

import com.sap.engine.lib.schema.components.impl.structures.SchemaImpl;
import com.sap.engine.lib.schema.components.impl.structures.BaseImpl;
import com.sap.engine.lib.schema.components.impl.structures.QualifiedBaseImpl;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2003-9-19
 * Time: 16:23:30
 * To change this template use Options | File Templates.
 */

public class SchemaVisualizationFrame_Namespaces {
//
//public class SchemaVisualizationFrame extends JFrame {
//
//  private SchemaImpl schema;
//  private Vector processedSchemasCollector;
//	private Vector processedIdentConstrDefsCollector;
//	private Vector processedTypeDefsCollector;
//	private Vector processedModelGroupCollector;
//	private Vector processedElemDeclrsCollector;
//	private Vector processedAttribDeclrsCollector;
//	private Vector processedAttribGropDefsCollector;
//	private Vector processedNotationDEclrsCollector;
//
//  public SchemaVisualizationFrame(SchemaImpl schema) {
//    super();
//    this.schema = schema;
//    setDefaultCloseOperation(EXIT_ON_CLOSE);
//		processedSchemasCollector = new Vector();
//		processedIdentConstrDefsCollector = new Vector();
//		processedTypeDefsCollector = new Vector();
//		processedModelGroupCollector = new Vector();
//		processedElemDeclrsCollector = new Vector();
//		processedAttribDeclrsCollector = new Vector();
//		processedAttribGropDefsCollector = new Vector();
//		processedNotationDEclrsCollector = new Vector();
//  }
//
//  public void vizualize() throws Exception {
//    setSize(1000, 700);
//    JTree visualizationTree = new JTree(createSchemaNode(schema));
//    JScrollPane treeVisualizationScrollPane = new JScrollPane(visualizationTree);
//    visualizationTree.setVisible(true);
//    treeVisualizationScrollPane.getViewport().repaint();
//    getContentPane().add(treeVisualizationScrollPane);
//    setVisible(true);
//  }
//
//  private DefaultMutableTreeNode createSchemaNode(SchemaImpl schema) {
//		DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode("{" + schema.getTargetNamespace() + "} : " + schema.getLocation());
//		if(!processedSchemasCollector.contains(schema)) {
//			processedSchemasCollector.add(schema);
//			augmentBaseSpecifics(schemaNode, schema);
//			schemaNode.add(new DefaultMutableTreeNode("target namespace : " + schema.getTargetNamespace()));
//			schemaNode.add(new DefaultMutableTreeNode("elements form default : " + schema.isElemsFormDefaultQualified()));
//			schemaNode.add(new DefaultMutableTreeNode("attributes form default : " + schema.isAttribsFormDefaultQualified()));
//			schemaNode.add(new DefaultMutableTreeNode("final extension : " + schema.isFinalExtension()));
//			schemaNode.add(new DefaultMutableTreeNode("final restriction : " + schema.isFinalRestriction()));
//			schemaNode.add(new DefaultMutableTreeNode("prohibited substitution : " + schema.isProhibitedSubstitution()));
//			schemaNode.add(new DefaultMutableTreeNode("prohibited extension : " + schema.isProhibitedExtension()));
//			schemaNode.add(new DefaultMutableTreeNode("prohibited restriction : " + schema.isProhibitedRestriction()));
//			schemaNode.add(new DefaultMutableTreeNode("prohibited list : " + schema.isProhibitedList()));
//			schemaNode.add(new DefaultMutableTreeNode("prohibited union : " + schema.isProhibitedUnion()));
//			schemaNode.add(new DefaultMutableTreeNode("location : " + schema.getLocation()));
//			schemaNode.add(createImportedSchemasNode(schema));
//			schemaNode.add(createIncludedSchemasNode(schema));
//			schemaNode.add(createIdentityConstraintsNode(schema));
//			schemaNode.add(createTypeDefinitionsNode(schema));
//			schemaNode.add(createAttributeDeclarationsNode(schema));
//			schemaNode.add(createElementDeclarationsNode(schema));
//			schemaNode.add(createAttributeGroupDefinitionsNode(schema));
//			schemaNode.add(createModelGroupDefinitionsNode(schema));
//			schemaNode.add(createNotationDeclarationsNode(schema));
//			schemaNode.add(createAllComponentsNode(schema));
//        }
//		return(schemaNode);
//  }
//  
//  private DefaultMutableTreeNode createIdentityConstraintsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode identityConstraintsNode = new DefaultMutableTreeNode("identity constr defs");
//		Vector collector = new Vector();
//		schema.getIdentityConstraintDefinitions(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			IdentityConstraintDefinitionImpl identityConstrDef = (IdentityConstraintDefinitionImpl)(collector.get(i));
//			identityConstraintsNode.add(createIdentityConstraintNode(identityConstrDef, true));
//		}
//		return(identityConstraintsNode);
//  }
//  
//	private DefaultMutableTreeNode createIdentityConstraintNode(IdentityConstraintDefinitionImpl identConstrDef, boolean display) {
//		DefaultMutableTreeNode identityConstrDefNode = new DefaultMutableTreeNode("ident constr def " + identConstrDef.toString());
//		if(displayQBase(identConstrDef, processedIdentConstrDefsCollector, display)) {
//			registerQBase(identConstrDef, processedIdentConstrDefsCollector);
//			augmentBaseSpecifics(identityConstrDefNode, identConstrDef);
//			augmentQualifiedBaseSpecifics(identityConstrDefNode, identConstrDef);
//			identityConstrDefNode.add(new DefaultMutableTreeNode("isKey : " + identConstrDef.isIdentityConstraintCategoryKey()));
//			identityConstrDefNode.add(new DefaultMutableTreeNode("isKeyref : " + identConstrDef.isIdentityConstraintCategoryKeyref()));
//			identityConstrDefNode.add(new DefaultMutableTreeNode("isUnique : " + identConstrDef.isIdentityConstraintCategoryUnique()));
//			identityConstrDefNode.add(new DefaultMutableTreeNode("selector : " + identConstrDef.getSelector()));
//			identityConstrDefNode.add(createIdentityconstrDefFildsNode(identConstrDef));
//		}
//		return(identityConstrDefNode);
//	}
//	
//	private void registerQBase(QualifiedBaseImpl qBase, Vector collector) {
//		if(qBase.isTopLevel()) {
//			collector.add(qBase);
//		}
//	}
//	
//	private boolean displayQBase(QualifiedBaseImpl qBase, Vector collector, boolean displayIfTopLevel) {
//		return(!qBase.isTopLevel() || (displayIfTopLevel && !collector.contains(qBase)));
//	}
//	
//	private DefaultMutableTreeNode createIdentityconstrDefFildsNode(IdentityConstraintDefinitionImpl identConstrDef) {
//		DefaultMutableTreeNode identityConstrDefNode = new DefaultMutableTreeNode("fields");
//		Vector collector = new Vector();
//		identConstrDef.getFields(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			identityConstrDefNode.add(new DefaultMutableTreeNode((String)(collector.get(i))));
//		}
//		return(identityConstrDefNode);
//	}
//	
//	private void augmentBaseSpecifics(DefaultMutableTreeNode baseNode, BaseImpl base) {
//		baseNode.add(new DefaultMutableTreeNode("isBuitIn : " + base.isBuiltIn()));
//		baseNode.add(new DefaultMutableTreeNode(createAnnotationNode((AnnotationImpl)(base.getAnnotation()))));
//		baseNode.add(new DefaultMutableTreeNode("isLoading : " + base.getAnnotation()));
//		baseNode.add(new DefaultMutableTreeNode("isLoaded : " + base.isLoaded()));
//		baseNode.add(new DefaultMutableTreeNode("isLoaded : " + base.isLoaded()));
//      }
//	
//	private DefaultMutableTreeNode createAnnotationNode(AnnotationImpl annotation) {
//		return(new DefaultMutableTreeNode("annotation"));
//	}
//	
//	private void augmentQualifiedBaseSpecifics(DefaultMutableTreeNode baseNode, QualifiedBaseImpl base) {
//		baseNode.add(new DefaultMutableTreeNode("name : " + base.getName()));
//		baseNode.add(new DefaultMutableTreeNode("namespace : " + base.getTargetNamespace()));
//		baseNode.add(new DefaultMutableTreeNode("isAnonimous : " + base.isAnonymous()));
//		baseNode.add(new DefaultMutableTreeNode("isTopLevel : " + base.isTopLevel()));
//		baseNode.add(new DefaultMutableTreeNode("qualifiedKey : " + base.getQualifiedKey()));
//	}
//  
//	private DefaultMutableTreeNode createTypeDefinitionsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode typeDefsNode = new DefaultMutableTreeNode("type defs");
//		Vector collector = new Vector();
//		schema.getTopLevelTypeDefinitions(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			TypeDefinitionBaseImpl typeDef = (TypeDefinitionBaseImpl)(collector.get(i));
//			typeDefsNode.add(createTypeDefinitionNode(typeDef, true));
//    }
//		return(typeDefsNode);
//  }
//
//	private DefaultMutableTreeNode createTypeDefinitionNode(TypeDefinitionBaseImpl typeDef, boolean displayIfTopLevel) {
//		return(typeDef instanceof SimpleTypeDefinitionImpl ? createSimpleTypeDefinitionNode((SimpleTypeDefinitionImpl)typeDef, displayIfTopLevel) : createComplexTypeDefinitionNode((ComplexTypeDefinitionImpl)typeDef, displayIfTopLevel));
//    }
//
//	private DefaultMutableTreeNode createComplexTypeDefinitionNode(ComplexTypeDefinitionImpl complexTypeDef, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode typeDefNode = new DefaultMutableTreeNode("complex type def " + complexTypeDef.toString());
//		if(!complexTypeDef.isBuiltIn() && displayQBase(complexTypeDef, processedTypeDefsCollector, displayIfTopLevel)) {
//			registerQBase(complexTypeDef, processedTypeDefsCollector);
//			augmentBaseSpecifics(typeDefNode, complexTypeDef);
//			augmentQualifiedBaseSpecifics(typeDefNode, complexTypeDef);
//			augmentTypeDefBaseSpecifics(typeDefNode, complexTypeDef);
//			typeDefNode.add(new DefaultMutableTreeNode("isDerivationMethodExtension : " + complexTypeDef.isDerivationMethodExtension()));
//			typeDefNode.add(new DefaultMutableTreeNode("isDerivationMethodRestriction : " + complexTypeDef.isDerivationMethodRestriction()));
//			typeDefNode.add(new DefaultMutableTreeNode("isProhibitedSubstitutionExtension : " + complexTypeDef.isProhibitedSubstitutionExtension()));
//			typeDefNode.add(new DefaultMutableTreeNode("isProhibitedSubstitutionRestriction : " + complexTypeDef.isProhibitedSubstitutionRestriction()));
//			typeDefNode.add(new DefaultMutableTreeNode("isAbstract : " + complexTypeDef.isAbstract()));
//			typeDefNode.add(createAttributeWildcardNode(complexTypeDef));
//			typeDefNode.add(createAttributeUsesNode(complexTypeDef));
//			typeDefNode.add(createSimpleContentTypeDefNode(complexTypeDef));
//			typeDefNode.add(createContentTypeContentModelNode(complexTypeDef));
//			typeDefNode.add(new DefaultMutableTreeNode("isContentTypeEmpty : " + complexTypeDef.isContentTypeEmpty()));
//			typeDefNode.add(new DefaultMutableTreeNode("isMixed : " + complexTypeDef.isMixed()));
//		}
//		return(typeDefNode);
//	}
//
//	private DefaultMutableTreeNode createContentTypeContentModelNode(ComplexTypeDefinitionImpl complTypeDef) {
//		DefaultMutableTreeNode contentModelNode = new DefaultMutableTreeNode("content model");
//		ParticleImpl particle = (ParticleImpl)(complTypeDef.getContentTypeContentModel());
//		if(particle != null) {
//			contentModelNode.add(createParticleNode(particle));
//		}
//		return(contentModelNode);
//	}
//
//	private DefaultMutableTreeNode createParticleNode(ParticleImpl particle) {
//		DefaultMutableTreeNode particleNode = new DefaultMutableTreeNode("particle");
//		augmentBaseSpecifics(particleNode, particle);
//		particleNode.add(new DefaultMutableTreeNode("min occurs : " + particle.getMinOccurs()));
//		particleNode.add(new DefaultMutableTreeNode("isMaxOccursUnbounded : " + particle.isMaxOccursUnbounded()));
//		particleNode.add(new DefaultMutableTreeNode("max occurs : " + particle.getMaxOccurs()));
//		particleNode.add(createTermNode((BaseImpl)(particle.getTerm())));
//		return(particleNode);
//	}
//	
//	private DefaultMutableTreeNode createTermNode(BaseImpl term) {
//		DefaultMutableTreeNode termNode = new DefaultMutableTreeNode("term");
//		if(term != null) {
//			if(term instanceof ElementDeclarationImpl) {
//				termNode.add(createElementDeclarationNode((ElementDeclarationImpl)term, false));
//			} else if(term instanceof ModelGroupDefinitionImpl) {
//				termNode.add(createModelGroupDefinitionNode((ModelGroupDefinitionImpl)term, false));
//			} else if(term instanceof ModelGroupImpl) {
//				termNode.add(createModelGroupNode((ModelGroupImpl)term));
//            } else {
//				termNode.add(createWildcardNode((WildcardImpl)term));
//            }
//          }
//		return(termNode);
//        }
//	
//	private DefaultMutableTreeNode createModelGroupDefinitionNode(ModelGroupDefinitionImpl modelGroupDef, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode modelGroupDefNode = new DefaultMutableTreeNode("model group def " + modelGroupDef.toString());
//		if(displayQBase(modelGroupDef, processedModelGroupCollector, displayIfTopLevel)) {
//			registerQBase(modelGroupDef, processedModelGroupCollector);
//			augmentBaseSpecifics(modelGroupDefNode, modelGroupDef);
//			augmentQualifiedBaseSpecifics(modelGroupDefNode, modelGroupDef);
//			ModelGroupImpl modelGroup = (ModelGroupImpl)(modelGroupDef.getModelGroup());
//			modelGroupDefNode.add(createModelGroupNode(modelGroup));
//      }
//		return(modelGroupDefNode);
//    }
//	
//	private DefaultMutableTreeNode createModelGroupNode(ModelGroupImpl modelGroup) {
//		DefaultMutableTreeNode modelGroupNode = new DefaultMutableTreeNode("model group");
//		augmentBaseSpecifics(modelGroupNode, modelGroup);
//		modelGroupNode.add(new DefaultMutableTreeNode("isCompositorAll : " + modelGroup.isCompositorAll()));
//		modelGroupNode.add(new DefaultMutableTreeNode("isCompositorChoice : " + modelGroup.isCompositorChoice()));
//		modelGroupNode.add(new DefaultMutableTreeNode("isCompositorSequence : " + modelGroup.isCompositorSequence()));
//		modelGroupNode.add(createParticlesNode(modelGroup));
//		return(modelGroupNode);
//  }
//
//	private DefaultMutableTreeNode createParticlesNode(ModelGroupImpl modelGroup) {
//		DefaultMutableTreeNode particlesNode = new DefaultMutableTreeNode("particles");
//		Vector collector = new Vector();
//		modelGroup.getParticles(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			particlesNode.add(createParticleNode((ParticleImpl)(collector.get(i))));
//		}
//		return(particlesNode);
//	}
//	
//	private DefaultMutableTreeNode createElementDeclarationNode(ElementDeclarationImpl elemDeclr, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode elemDeclrNode = new DefaultMutableTreeNode("elem declr " + elemDeclr.toString());
//		if(displayQBase(elemDeclr, processedElemDeclrsCollector, displayIfTopLevel)) {
//			registerQBase(elemDeclr, processedElemDeclrsCollector);
//			augmentBaseSpecifics(elemDeclrNode, elemDeclr);
//			augmentQualifiedBaseSpecifics(elemDeclrNode, elemDeclr);
//			augmentItemDeclrBaseSpecifics(elemDeclrNode, elemDeclr);
//			elemDeclrNode.add(new DefaultMutableTreeNode("isNillable : " + elemDeclr.isNillable()));
//			elemDeclrNode.add(createIdentityConstriantDefinitionsNode(elemDeclr));
//			elemDeclrNode.add(createSubstElementDeclarationsNode(elemDeclr));
//			elemDeclrNode.add(createSubstElementDeclarationNode(elemDeclr));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isSubstitutionGroupExclusionExtension : " + elemDeclr.isSubstitutionGroupExclusionExtension()));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isSubstitutionGroupExclusionRestriction : " + elemDeclr.isSubstitutionGroupExclusionRestriction()));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isDisallowedSubstitutionSubstitution : " + elemDeclr.isDisallowedSubstitutionSubstitution()));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isDisallowedSubstitutionExtension : " + elemDeclr.isDisallowedSubstitutionExtension()));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isDisallowedSubstitutionRestriction : " + elemDeclr.isDisallowedSubstitutionRestriction()));
//			elemDeclrNode.add(new DefaultMutableTreeNode("isAbstract : " + elemDeclr.isAbstract()));
//    }
//		return(elemDeclrNode);
//  }
//
//	private DefaultMutableTreeNode createIdentityConstriantDefinitionsNode(ElementDeclarationImpl elemDeclr) {
//		DefaultMutableTreeNode identCostrDefsNode = new DefaultMutableTreeNode("ident constr defs");
//		Vector collector = new Vector();
//		elemDeclr.getIdentityConstraintDefinitions(collector);
//    for(int i = 0; i < collector.size(); i++) {
//			identCostrDefsNode.add(new DefaultMutableTreeNode("ident constr def " + collector.get(i).toString()));
//		}
//		return(identCostrDefsNode);
//	}
//	
//	private DefaultMutableTreeNode createSubstElementDeclarationsNode(ElementDeclarationImpl elemDeclr) {
//		DefaultMutableTreeNode substElemDeclrsNode = new DefaultMutableTreeNode("subst elem declrs");
//		Vector collector = new Vector();
//		elemDeclr.getSubstitutableElementDeclarations(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			ElementDeclarationImpl substElemdEclr = (ElementDeclarationImpl)(collector.get(i));
//			substElemDeclrsNode.add(createElementDeclarationNode(substElemdEclr, false));
//		}
//		return(substElemDeclrsNode);
//	}
//	
//	private DefaultMutableTreeNode createSubstElementDeclarationNode(ElementDeclarationImpl elemDeclr) {
//		DefaultMutableTreeNode substElemAffiliationNode = new DefaultMutableTreeNode("subst elem affiliation");
//		ElementDeclarationImpl substElemDeclrAffiliation = (ElementDeclarationImpl)(elemDeclr.getSubstitutionGroupAffiliation());
//		if(substElemDeclrAffiliation != null) {
//			substElemAffiliationNode.add(createElementDeclarationNode(substElemDeclrAffiliation, false));
//		}
//		return(substElemAffiliationNode);
//	}
//	
//	private DefaultMutableTreeNode createSimpleContentTypeDefNode(ComplexTypeDefinitionImpl complTypeDef) {
//		DefaultMutableTreeNode simpleContentTypeNode = new DefaultMutableTreeNode("simple content");
//		SimpleTypeDefinitionImpl simpleTypeDef = (SimpleTypeDefinitionImpl)(complTypeDef.getContentTypeSimpleTypeDefinition());
//		if(simpleTypeDef != null) {
//			simpleContentTypeNode.add(createSimpleTypeDefinitionNode(simpleTypeDef, false));
//		}
//		return(simpleContentTypeNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeUsesNode(ComplexTypeDefinitionImpl complexTypeDef) {
//		DefaultMutableTreeNode attribUsesNode = new DefaultMutableTreeNode("attrib uses");
//		Vector collector = new Vector();
//		complexTypeDef.getAttributeUses(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			AttributeUseImpl attribUse = (AttributeUseImpl)(collector.get(i));
//			attribUsesNode.add(createAttributeUseNode(attribUse));
//		}
//		return(attribUsesNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeUseNode(AttributeUseImpl attribUse) {
//		DefaultMutableTreeNode attribUseNode = new DefaultMutableTreeNode("attrib use");
//		augmentBaseSpecifics(attribUseNode, attribUse);
//		attribUseNode.add(new DefaultMutableTreeNode("isRequired : " + attribUse.isRequired()));
//		attribUseNode.add(new DefaultMutableTreeNode("isProhibited : " + attribUse.isProhibited()));
//		attribUseNode.add(createAttributeDeclarationNode((AttributeDeclarationImpl)(attribUse.getAttributeDeclaration()), false));
//		attribUseNode.add(new DefaultMutableTreeNode("value constr default : " + attribUse.getValueConstraintDefault()));
//		attribUseNode.add(new DefaultMutableTreeNode("value constr fixed : " + attribUse.getValueConstraintFixed()));
//		return(attribUseNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeDeclarationNode(AttributeDeclarationImpl attribDeclr, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode attribDeclrNode = new DefaultMutableTreeNode("attrib declr " + attribDeclr.toString());
//		if(displayQBase(attribDeclr, processedAttribDeclrsCollector, displayIfTopLevel)) {
//			registerQBase(attribDeclr, processedAttribDeclrsCollector);
//			augmentBaseSpecifics(attribDeclrNode, attribDeclr);
//			augmentQualifiedBaseSpecifics(attribDeclrNode, attribDeclr);
//			augmentItemDeclrBaseSpecifics(attribDeclrNode, attribDeclr);
//		}
//		return(attribDeclrNode);
//	}
//	
//	private void augmentItemDeclrBaseSpecifics(DefaultMutableTreeNode node, InfoItemDeclarationBaseImpl declrBase) {
//		node.add(createInfoItemDeclrBaseTypeNode(declrBase));
//		node.add(createInfoItemDeclrBaseScopeNode(declrBase));
//		node.add(new DefaultMutableTreeNode("value constr default : " + declrBase.getValueConstraintDefault()));
//		node.add(new DefaultMutableTreeNode("value constr fixed : " + declrBase.getValueConstraintFixed()));
//	}
//	
//	private DefaultMutableTreeNode createInfoItemDeclrBaseTypeNode(InfoItemDeclarationBaseImpl declrBase) {
//		DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode("type");
//		typeNode.add(createTypeDefinitionNode((TypeDefinitionBaseImpl)(declrBase.getTypeDefinition()), false));
//		return(typeNode);
//      }
//	
//	private DefaultMutableTreeNode createInfoItemDeclrBaseScopeNode(InfoItemDeclarationBaseImpl declrBase) {
//		DefaultMutableTreeNode scopeNode = new DefaultMutableTreeNode("scope");
//		ComplexTypeDefinitionImpl scopeType = (ComplexTypeDefinitionImpl)(declrBase.getScope());
//		if(scopeType != null) {
//			scopeNode.add(createComplexTypeDefinitionNode(scopeType, false));
//		}
//		return(scopeNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeWildcardNode(ComplexTypeDefinitionImpl complexTypeDef) {
//		DefaultMutableTreeNode attribWildcardNode = new DefaultMutableTreeNode("attrib wildcard");
//		WildcardImpl attribWildcard = (WildcardImpl)(complexTypeDef.getAttributeWildcard());
//		if(attribWildcard != null) {
//			attribWildcardNode.add(createWildcardNode(attribWildcard));
//		}
//		return(attribWildcardNode);
//	}
//	
//	private DefaultMutableTreeNode createWildcardNode(WildcardImpl wildcard) {
//		DefaultMutableTreeNode wildcardNode = new DefaultMutableTreeNode("wildcard");
//		augmentBaseSpecifics(wildcardNode, wildcard);
//		wildcardNode.add(new DefaultMutableTreeNode("isProcessContentsSkip : " + wildcard.isProcessContentsSkip()));
//		wildcardNode.add(new DefaultMutableTreeNode("isProcessContentsLax : " + wildcard.isProcessContentsLax()));
//		wildcardNode.add(new DefaultMutableTreeNode("isProcessContentsStrict : " + wildcard.isProcessContentsStrict()));
//		wildcardNode.add(new DefaultMutableTreeNode("isNamespaceConstraintAny : " + wildcard.isNamespaceConstraintAny()));
//		wildcardNode.add(new DefaultMutableTreeNode("negated : " + wildcard.getNamespaceConstraintNegated()));
//		wildcardNode.add(new DefaultMutableTreeNode("isAttribWildcard : " + wildcard.isAttribWildcard()));
//		wildcardNode.add(new DefaultMutableTreeNode(createNsConstrMemebersNode(wildcard)));
//		return(wildcardNode);
//	}
//	
//	private DefaultMutableTreeNode createNsConstrMemebersNode(WildcardImpl wildcard) {
//		DefaultMutableTreeNode nsConstrMemebersNode = new DefaultMutableTreeNode("ns constr members");
//		Vector collector = new Vector();
//		wildcard.getNamespaceConstraintMembers(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			nsConstrMemebersNode.add(new DefaultMutableTreeNode("ns : " + (String)(collector.get(i))));
//		}
//		return(nsConstrMemebersNode);
//	}
//	
//	private DefaultMutableTreeNode createSimpleTypeDefinitionNode(SimpleTypeDefinitionImpl simpleTypeDef, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode typeDefNode = new DefaultMutableTreeNode("simple type def " + simpleTypeDef.toString());
//		if(!simpleTypeDef.isBuiltIn() && displayQBase(simpleTypeDef, processedTypeDefsCollector, displayIfTopLevel)) {
//			registerQBase(simpleTypeDef, processedTypeDefsCollector);
//			augmentBaseSpecifics(typeDefNode, simpleTypeDef);
//			augmentQualifiedBaseSpecifics(typeDefNode, simpleTypeDef);
//			augmentTypeDefBaseSpecifics(typeDefNode, simpleTypeDef);
//			typeDefNode.add(new DefaultMutableTreeNode("whiteSpaceNormal : " + simpleTypeDef.getWhiteSpaceNormalizationValue()));
//			typeDefNode.add(new DefaultMutableTreeNode("isFinalList : " + simpleTypeDef.isFinalList()));
//			typeDefNode.add(new DefaultMutableTreeNode("isFinalUnion : " + simpleTypeDef.isFinalUnion()));
//			typeDefNode.add(new DefaultMutableTreeNode("isVarietyAtomic : " + simpleTypeDef.isVarietyAtomic()));
//			typeDefNode.add(new DefaultMutableTreeNode("isVarietyList : " + simpleTypeDef.isVarietyList()));
//			typeDefNode.add(new DefaultMutableTreeNode("isVarietyUnion : " + simpleTypeDef.isVarietyUnion()));
//			typeDefNode.add(new DefaultMutableTreeNode("isPrimitive : " + simpleTypeDef.isPrimitive()));
//			typeDefNode.add(new DefaultMutableTreeNode(createPrimitiveTypeDefNode(simpleTypeDef)));
//			typeDefNode.add(new DefaultMutableTreeNode(createItemTypeDefNode(simpleTypeDef)));
//			typeDefNode.add(new DefaultMutableTreeNode(createMemberTypeDefsNode(simpleTypeDef)));
//			typeDefNode.add(new DefaultMutableTreeNode(createFacetsNode(simpleTypeDef)));
//		}
//		return(typeDefNode);
//	}
//	
//	private DefaultMutableTreeNode createFacetsNode(SimpleTypeDefinitionImpl simpleTypeDef) {
//		DefaultMutableTreeNode facetsNode = new DefaultMutableTreeNode("facets");
//		Vector collector = new Vector();
//		simpleTypeDef.getFacets(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			FacetImpl facet = (FacetImpl)(collector.get(i));
//			facetsNode.add(createFacetNode(facet));
//		}
//		return(facetsNode);
//	}
//
//	private DefaultMutableTreeNode createFacetNode(FacetImpl facet) {
//		DefaultMutableTreeNode facetNode = new DefaultMutableTreeNode("facet");
//		facetNode.add(new DefaultMutableTreeNode("name : " + facet.getName()));
//		facetNode.add(new DefaultMutableTreeNode("value : " + facet.getValue()));
//		facetNode.add(new DefaultMutableTreeNode("isFixed : " + facet.isFixed()));
//		return(facetNode);
//	}
//	
//	private DefaultMutableTreeNode createMemberTypeDefsNode(SimpleTypeDefinitionImpl simpleTypeDef) {
//		DefaultMutableTreeNode memberTypeDefsNode = new DefaultMutableTreeNode("member types");
//		Vector collector = new Vector();
//		simpleTypeDef.getMemberTypeDefinitions(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			SimpleTypeDefinitionImpl memberTypeDef = (SimpleTypeDefinitionImpl)(collector.get(i));
//			memberTypeDefsNode.add(createSimpleTypeDefinitionNode(memberTypeDef, false));
//		}
//		return(memberTypeDefsNode);
//	}
//	
//	private void augmentTypeDefBaseSpecifics(DefaultMutableTreeNode node, TypeDefinitionBaseImpl typeDef) {
//		node.add(createBaseTypeNode(typeDef));
//		node.add(new DefaultMutableTreeNode("isFinalExtension : " + typeDef.isFinalExtension()));
//		node.add(new DefaultMutableTreeNode("isFinalRestriction : " + typeDef.isFinalRestriction()));
//	}
//	
//	private DefaultMutableTreeNode createBaseTypeNode(TypeDefinitionBaseImpl typeDef) {
//		TypeDefinitionBaseImpl baseTypeDef = (TypeDefinitionBaseImpl)(typeDef.getBaseTypeDefinition());
//		DefaultMutableTreeNode baseTypeDefNode = new DefaultMutableTreeNode("base");
//		if(baseTypeDef != null) {
//			baseTypeDefNode.add(createTypeDefinitionNode(baseTypeDef, false));
//		}
//		return(baseTypeDefNode);
//	}
//	
//	private DefaultMutableTreeNode createPrimitiveTypeDefNode(SimpleTypeDefinitionImpl typeDef) {
//		TypeDefinitionBaseImpl primitiveTypeDef = (TypeDefinitionBaseImpl)(typeDef.getPrimitiveTypeDefinition());
//		DefaultMutableTreeNode primitiveTypeDefNode = new DefaultMutableTreeNode("primitive type def");
//		if(primitiveTypeDef != null) {
//			primitiveTypeDefNode.add(createTypeDefinitionNode(primitiveTypeDef, false));
//		}
//		return(primitiveTypeDefNode);
//	}
//	
//	private DefaultMutableTreeNode createItemTypeDefNode(SimpleTypeDefinitionImpl typeDef) {
//		TypeDefinitionBaseImpl itemTypeDef = (TypeDefinitionBaseImpl)(typeDef.getItemTypeDefinition());
//		DefaultMutableTreeNode itemTypeDefNode = new DefaultMutableTreeNode("item type def");
//		if(itemTypeDef != null) {
//			itemTypeDefNode.add(createTypeDefinitionNode(itemTypeDef, false));
//		}
//		return(itemTypeDefNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeDeclarationsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode attributeDeclrsNode = new DefaultMutableTreeNode("attrib declrs");
//		Vector collector = new Vector();
//		schema.getTopLevelAttributeDeclarations(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			AttributeDeclarationImpl attribDeclr = (AttributeDeclarationImpl)(collector.get(i));
//			attributeDeclrsNode.add(createAttributeDeclarationNode(attribDeclr, true));
//		}
//		return(attributeDeclrsNode);
//	}
//	
//	private DefaultMutableTreeNode createElementDeclarationsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode elemsDeclrsNode = new DefaultMutableTreeNode("elem declrs");
//		Vector collector = new Vector();
//		schema.getTopLevelElementDeclarations(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			ElementDeclarationImpl elemDeclr = (ElementDeclarationImpl)(collector.get(i));
//			elemsDeclrsNode.add(createElementDeclarationNode(elemDeclr, true));
//    }
//		return(elemsDeclrsNode);
//  }
//
//	private DefaultMutableTreeNode createAttributeGroupDefinitionsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode attribGroupDefsNode = new DefaultMutableTreeNode("attrib group defs");
//		Vector collector = new Vector();
//		schema.getTopLevelAttributeGroupDefinitions(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			AttributeGroupDefinitionImpl attribGroupDef = (AttributeGroupDefinitionImpl)(collector.get(i));
//			attribGroupDefsNode.add(createAttributeGroupDefinitionNode(attribGroupDef, true));
//		}
//		return(attribGroupDefsNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeGroupDefinitionNode(AttributeGroupDefinitionImpl attribGroupDef, boolean displayIfTopLevel) {
//		DefaultMutableTreeNode attribGroupDefsNode = new DefaultMutableTreeNode("attrib group def " + attribGroupDef.toString());
//		if(displayQBase(attribGroupDef, processedAttribGropDefsCollector, displayIfTopLevel)) {
//			registerQBase(attribGroupDef, processedAttribGropDefsCollector);
//			augmentBaseSpecifics(attribGroupDefsNode, attribGroupDef);
//			augmentQualifiedBaseSpecifics(attribGroupDefsNode, attribGroupDef);
//			attribGroupDefsNode.add(createAttributeWildcardNode(attribGroupDef));
//			attribGroupDefsNode.add(createAttributeUsesNode(attribGroupDef));
//		}
//		return(attribGroupDefsNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeWildcardNode(AttributeGroupDefinitionImpl attribGroupDef) {
//		DefaultMutableTreeNode attribWildcardNode = new DefaultMutableTreeNode("attrib wildcard");
//		WildcardImpl attribWildcard = (WildcardImpl)(attribGroupDef.getAttributeWildcard());
//		if(attribWildcard != null) {
//			attribWildcardNode.add(createWildcardNode(attribWildcard));
//		}
//		return(attribWildcardNode);
//	}
//	
//	private DefaultMutableTreeNode createAttributeUsesNode(AttributeGroupDefinitionImpl attribGroupDef) {
//		DefaultMutableTreeNode attribUsesNode = new DefaultMutableTreeNode("attrib uses");
//		Vector collector = new Vector();
//		attribGroupDef.getAttributeUses(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			AttributeUseImpl attribUse = (AttributeUseImpl)(collector.get(i));
//			attribUsesNode.add(createAttributeUseNode(attribUse));
//		}
//		return(attribUsesNode);
//    }
//	
//	private DefaultMutableTreeNode createModelGroupDefinitionsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode modelGroupDefsNode = new DefaultMutableTreeNode("model group defs");
//		Vector collector = new Vector();
//		schema.getTopLevelModelGroupDefinitions(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			ModelGroupDefinitionImpl modelGroupDef = (ModelGroupDefinitionImpl)(collector.get(i));
//			modelGroupDefsNode.add(createModelGroupDefinitionNode(modelGroupDef, true));
//		}
//		return(modelGroupDefsNode);
//	}
//	
//	private DefaultMutableTreeNode createNotationDeclarationsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode notationDeclrsNode = new DefaultMutableTreeNode("notation declrs");
//		Vector collector = new Vector();
//		schema.getTopLevelNotationDeclarations(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			NotationDeclarationImpl notationDeclr = (NotationDeclarationImpl)(collector.get(i));
//			notationDeclrsNode.add(createNotationDeclrationNode(notationDeclr));
//		}
//		return(notationDeclrsNode);
//	}
//	
//	private DefaultMutableTreeNode createNotationDeclrationNode(NotationDeclarationImpl notationDeclr) {
//		DefaultMutableTreeNode notationDeclrNode = new DefaultMutableTreeNode("notation declr " + notationDeclr.toString());
//		if(displayQBase(notationDeclr, processedNotationDEclrsCollector, true)) {
//			registerQBase(notationDeclr, processedNotationDEclrsCollector);
//			augmentBaseSpecifics(notationDeclrNode, notationDeclr);
//			augmentQualifiedBaseSpecifics(notationDeclrNode, notationDeclr);
//			notationDeclrNode.add(new DefaultMutableTreeNode("system identifier : " + notationDeclr.getSystemIdentifier()));
//			notationDeclrNode.add(new DefaultMutableTreeNode("public identifier : " + notationDeclr.getPublicIdentifier()));
//		}
//		return(notationDeclrNode);
//	}
//	
//	private DefaultMutableTreeNode createAllComponentsNode(SchemaImpl schema) {
//		DefaultMutableTreeNode allComponentsNode = new DefaultMutableTreeNode("all components");
//		Vector collector = new Vector();
//		schema.getAllComponents(collector);
//		for(int i = 0; i < collector.size(); i++) {
//			BaseImpl base = (BaseImpl)(collector.get(i));
//			allComponentsNode.add(createBaseNode(base));
//		}
//		return(allComponentsNode);
//  }
//
//	private DefaultMutableTreeNode createBaseNode(BaseImpl base) {
//		if(base instanceof NotationDeclarationImpl) {
//			return(createNotationDeclrationNode((NotationDeclarationImpl)base));
//    }
//		if(base instanceof SimpleTypeDefinitionImpl) {
//			return(createSimpleTypeDefinitionNode((SimpleTypeDefinitionImpl)base, false));
//  }
//		if(base instanceof ComplexTypeDefinitionImpl) {
//			return(createComplexTypeDefinitionNode((ComplexTypeDefinitionImpl)base, false));
//		}
//		if(base instanceof ModelGroupDefinitionImpl) {
//			return(createModelGroupDefinitionNode((ModelGroupDefinitionImpl)base, false));
//		}
//		if(base instanceof AttributeGroupDefinitionImpl) {
//			return(createAttributeGroupDefinitionNode((AttributeGroupDefinitionImpl)base, false));
//		}
//		if(base instanceof ElementDeclarationImpl) {
//			return(createElementDeclarationNode((ElementDeclarationImpl)base, false));
//		}
//		return(createAttributeDeclarationNode((AttributeDeclarationImpl)base, false));
//	}
//  
//  private DefaultMutableTreeNode createImportedSchemasNode(SchemaImpl schema) {
//		DefaultMutableTreeNode importedSchemasNode = new DefaultMutableTreeNode("imported schemas");
//  	Hashtable nsToImportedSchemasMapping = schema.getImportedSchemas();
//  	Enumeration keysEnum = nsToImportedSchemasMapping.keys();
//  	while(keysEnum.hasMoreElements()) {
//  		String ns = (String)(keysEnum.nextElement());
//  		Vector schemas = (Vector)(nsToImportedSchemasMapping.get(ns));
//			importedSchemasNode.add(createNsImportedSchemas(ns, schemas));
//  	}
//  	return(importedSchemasNode);
//  } 
//  
//  private DefaultMutableTreeNode createNsImportedSchemas(String ns, Vector schemas) {
//		DefaultMutableTreeNode nsImportedSchemasNode = new DefaultMutableTreeNode("ns : " + ns);
//		for(int i = 0; i < schemas.size(); i++) {
//			SchemaImpl schema = (SchemaImpl)(schemas.get(i));
//			nsImportedSchemasNode.add(createSchemaNode(schema)); 
//		}
//		return(nsImportedSchemasNode);
//  }
//  
//	private DefaultMutableTreeNode createIncludedSchemasNode(SchemaImpl schema) {
//		DefaultMutableTreeNode includedSchemasNode = new DefaultMutableTreeNode("included schemas");
//		Vector includedSchemasCollector = schema.getIncludedSchemas();
//		for(int i = 0; i < includedSchemasCollector.size(); i++) {
//			SchemaImpl includedSchema = (SchemaImpl)(includedSchemasCollector.get(i));
//			includedSchemasNode.add(createSchemaNode(includedSchema));
//		}
//		return(includedSchemasNode);
//	} 
//	
//	
}
