package com.sap.engine.lib.schema.components;

import java.util.Vector;

/**
 * Represents an element declaration component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Element Declaration
 *     {name}             An NCName as defined by [XML-Namespaces].
 *     {target namespace} Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {type definition}  Either a simple type definition or a complex type definition.
 *     {scope}            Optional. Either global or a complex type definition.
 *     {value constraint} Optional. A pair consisting of a value and one of default, fixed.
 *     {nillable}         A boolean.
 *     {identity-constraint definitions} A set of constraint definitions.
 *     {substitution group affiliation}  Optional. A top-level element definition.
 *     {substitution group exclusions}   A subset of {extension, restriction}.
 *     {disallowed substitutions}        A subset of {substitution, extension, restriction}.
 *     {abstract}         A boolean.
 *     {annotation}       Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface ElementDeclaration
  extends InfoItemDeclarationBase {

  /**
   * Corresponds to the {nillable} property
   */
  boolean isNillable();

  
  Vector getIdentityConstraintDefinitions();

  /**
   * Appends to the (non-null) Vector
   * all {identity constraint definitions} of this component.
   */
  void getIdentityConstraintDefinitions(java.util.Vector v);


  /**
   *
   */
  IdentityConstraintDefinition[] getIdentityConstraintDefinitionsArray();


  /**
   * If there is a {substitution group affilation}, returns it,
   * otherwise returns null
   */
  ElementDeclaration getSubstitutionGroupAffiliation();

  ElementDeclaration[] getSubstitutableElementDeclarationsArray();

  void getSubstitutableElementDeclarations(java.util.Vector v);

  ElementDeclaration getSubstitutableElementDeclaration(String targetNamespace, String name);

  /**
   * Returns true if and only if 'extension' is in {substitution group exclusions}
   */
  boolean isSubstitutionGroupExclusionExtension();


  /**
   * Returns true if and only if 'restriction' is in {substitution group exclusions}
   */
  boolean isSubstitutionGroupExclusionRestriction();


  /**
   * Returns true if and only if 'substitution' is in {disallowed substitutions}
   */
  boolean isDisallowedSubstitutionSubstitution();


  /**
   * Returns true if and only if 'extension' is in {disallowed substitutions}
   */
  boolean isDisallowedSubstitutionExtension();


  /**
   * Returns true if and only if 'restriction' is in {disallowed substitutions}
   */
  boolean isDisallowedSubstitutionRestriction();


  /**
   * Corresponds to the {abstract} property
   */
  boolean isAbstract();

}

