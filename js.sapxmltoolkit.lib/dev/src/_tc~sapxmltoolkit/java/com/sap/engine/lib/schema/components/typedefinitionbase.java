package com.sap.engine.lib.schema.components;

/**
 * A base interface for SimpleTypeDefinition and ComplexTypeDefinition.
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public abstract interface TypeDefinitionBase extends RedefineableQualifiedBase {

  /**
   * The {base type definition}.
   * If this is a SimpleTypeDefinition, then should return a SimpleTypeDefinition.
   * If this is a ComplexTypeDefinition, then should return a ComplexTypeDefinition,
   * ( If a xs:complexType extends a xs:simpleType, you can get the base
   * through ComplexTypeDefinition.getContentTypeSimpleTypeDefinition() )
   */
  TypeDefinitionBase getBaseTypeDefinition();

  boolean isDerivedFrom(TypeDefinitionBase typeDefinition, boolean disallowedRestriction, boolean disallowedExtension);

  /**
   * Returns true if and only if 'extension' is in {final}
   */
  boolean isFinalExtension();

  /**
   * Returns true if and only if 'restriction' is in {final}
   */
  boolean isFinalRestriction();
}

