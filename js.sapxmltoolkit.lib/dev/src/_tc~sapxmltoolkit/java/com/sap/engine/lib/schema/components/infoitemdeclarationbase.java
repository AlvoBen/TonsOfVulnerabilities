package com.sap.engine.lib.schema.components;


/**
 * A base class for ElementDeclaration and AttributeDeclaration.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Attribute Declaration
 *     {name}             An NCName as defined by [XML-Namespaces].
 *     {target namespace} Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {type definition}  A simple type definition.
 *     {scope}            Optional. Either global or a complex type definition.
 *     {value constraint} Optional. A pair consisting of a value and one of default, fixed.
 *     {annotation}       Optional. An annotation.
 *
 *     Schema Component: Element Declaration
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
public abstract interface InfoItemDeclarationBase
  extends QualifiedBase {

  /**
   * Returns the {type definition} (or null, if not specified)
   */
  TypeDefinitionBase getTypeDefinition();


  /**
   * Returns the {scope} if it is not 'global', otherwise null
   */
  ComplexTypeDefinition getScope();


  /**
   * If the {value constraint} is a pair of a String, s, and 'default',
   * then returns s, otherwise returns null
   */
  String getValueConstraintDefault();
  
  /**
   * If the {value constraint} is a pair of a String, s, and 'fixed',
   * then returns s, otherwise returns null
   */
  String getValueConstraintFixed();
}

