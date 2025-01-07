package com.sap.engine.lib.schema.components;

import org.w3c.dom.Node;

import com.sap.engine.lib.schema.Constants;

/**
 * A base interface for all schema components.
 *
 * <pre>
 * Hierarchy:
 *
 *    Base
 *     +------QualifiedBase
 *     |        +------InfoItemDeclarationBase
 *     |        |        +------AttributeDeclaration
 *     |        |        +------ElementDeclaration
 *     |        +------TypeDefinitionBase
 *     |        |        +-------ComplexTypeDefinition
 *     |        |        +-------SimpleTypeDefinition
 *     |        +------AttributeGroupDefinition
 *     |        +------IdentityConstraintDefinition
 *     |        +------ModelGroupDefinition
 *     |        +------NotationDeclaration
 *     +------ModelGroup
 *     +------Particle
 *     +------Schema
 *     +------Wildcard
 *     +------AttributeUse
 *     +------Facet
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public abstract interface Base extends Constants {



  /**
   * Every component has a unique int identifier, see the constants above.
   * Returns the unique int identifier for the current component
   */
  int getTypeOfComponent();


  /**
   * Returns the name of the component, as defined in the spec
   * @see #NAMES_OF_COMPONENTS
   */
  String getNameOfComponent();


  /**
   * Indicates whether this component was produced implicitly by the processor.
   */
  boolean isBuiltIn();


  Annotation getAnnotation();


  Schema getOwnerSchema();


  /**
   *
   */
  Node getAssociatedDOMNode();


  /**
   * If there was an 'id' attribute in the schema, returns it;
   * otherwise returns null.
   */
  String getId();
  
  boolean match(Base base);
}

