package com.sap.engine.lib.schema.components;

import java.util.Vector;

/**
 * Represents a complex type definition component.
 *
 * Excerpt:
 * <pre>
 * Schema Component: Complex Type Definition
 *   {name}                 Optional. An NCName as defined by [XML-Namespaces].
 *   {target namespace}     Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *   {base type definition} Either a simple type definition or a complex type definition.
 *   {derivation method}    Either extension or restriction.
 *   {final}                A subset of {extension, restriction}.
 *   {abstract}             A boolean
 *   {attribute uses}       A set of attribute uses.
 *   {attribute wildcard}   Optional. A wildcard.
 *   {content type}         One of empty, a simple type definition or a pair consisting
 *                                      of a \u00b7content model\u00b7 (I.e. a Particle (\u00a72.2.3.2))
 *                                      and one of mixed, element-only.
 *   {prohibited substitutions}  A subset of {extension, restriction}.
 *   {annotations}          A set of annotations.
 * </pre>
 *
 * @author       Nick Nickolov
 * @version      November 2001
 */
public interface ComplexTypeDefinition
  extends TypeDefinitionBase {

  /**
   * Should return true if the {derivation method} is extension,
   * or if there is no {base type definition} set.
   */
  boolean isDerivationMethodExtension();

  /**
   * Returns true if and only if the {derivation method} is restriction
   */
  boolean isDerivationMethodRestriction();

  /**
   * Corresponds to the {abstract} property
   */
  boolean isAbstract();


  /**
   * Returns the {attribute wildcard} if present, otherwise null
   */
  Wildcard getAttributeWildcard();


  Vector getAttributeUses();
  
  /**
   * Appends to the (non-null) Vector
   * all {attribute uses} of this component.
   */
  void getAttributeUses(java.util.Vector v);


  /**
   *
   */
  AttributeUse[] getAttributeUsesArray();

  /**
   * Returns true if and only if 'extension' is in {prohibited substitutions}
   */
  boolean isProhibitedSubstitutionExtension();


  /**
   * Returns true if and only if 'restriction' is in {prohibited substitutions}
   */
  boolean isProhibitedSubstitutionRestriction();


  /**
   * If the {content type} is a simple type definition, returns it,
   * otherwise returns null
   */
  SimpleTypeDefinition getContentTypeSimpleTypeDefinition();


  /**
   * If the {content type} is a pair of a content model and one of { mixed, element-only },
   * returns the content model (which is a Particle), otherwise returns null
   */
  Particle getContentTypeContentModel();


  /**
   * Returns true if and only if both getContentTypeSimpleTypeDefinition and
   * getContentTypeContentModel would return null
   */
  boolean isContentTypeEmpty();


  /**
   * If the {content type} is a pair of a content model and 'mixed'
   * returns true, otherwise returns false
   */
  boolean isMixed();
}

