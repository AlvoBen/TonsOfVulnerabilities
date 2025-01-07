package com.sap.engine.lib.schema.components;

import java.util.Vector;

/**
 * Represents an identity-constraint definition component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Identity-constraint Definition
 *     {name}               An NCName as defined by [XML-Namespaces].
 *     {target namespace}   Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {identity-constraint category}  One of key, keyref or unique.
 *     {selector}           A restricted XPath ([XPath]) expression.
 *     {fields}             A non-empty list of restricted XPath ([XPath]) expressions.
 *     {referenced key}     Required if {identity-constraint category} is keyref, forbidden otherwise.
 *                              An identity-constraint definition with {identity-constraint
 *                                category} equal to key or unique.
 *     {annotation}         Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface IdentityConstraintDefinition
  extends QualifiedBase {

  /**
   * Returns true if and only if {identity-constraint category} is 'key'
   */
  boolean isIdentityConstraintCategoryKey();


  /**
   * Returns true if and only if {identity-constraint category} is 'keyref'
   */
  boolean isIdentityConstraintCategoryKeyref();


  /**
   * Returns true if and only if {identity-constraint category} is 'unique'
   */
  boolean isIdentityConstraintCategoryUnique();


  /**
   * Returns the {selector} as a String
   */
  String getSelector();


  Vector getFields();
  
  /**
   * Appends to the (non-null) Vector
   * all {fields} of this component. (java.lang.String-s)
   */
  void getFields(java.util.Vector v);


  /**
   * If this the {identity-constraint category} is 'keyref',
   * returns the {referenced key}, otherwise null
   */
  IdentityConstraintDefinition getReferencedKey();

  ElementDeclaration getOwner();
}

