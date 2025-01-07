package com.sap.engine.lib.schema.components;

import java.util.*;

/**
 * Represents a simple type definition component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Simple Type Definition
 *     {name}                  Optional. An NCName as defined by [XML-Namespaces].
 *     {target namespace}      Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {base type definition}  A simple type definition, which may be the \u00b7simple ur-type definition\u00b7.
 *     {facets}                A set of constraining facets.
 *     {fundamental facets}    A set of fundamental facets.
 *     {final}                 A subset of {extension, list, restriction, union}.
 *     {variety}
 *     One of {atomic, list, union}. Depending on the value of {variety}, further properties are defined as follows:
 *       atomic
 *       {primitive type definition}
 *       A built-in primitive simple type definition (or the \u00b7simple ur-type definition\u00b7).
 *       list
 *       {item type definition}
 *       A simple type definition.
 *       union
 *       {member type definitions}
 *       A non-empty sequence of simple type definitions.
 *     {annotation}            Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface SimpleTypeDefinition
  extends TypeDefinitionBase {

  /**
   * Returns the {base type definition}, or null if not specified
   */
  SimpleTypeDefinition getBaseTypeDefinitionSimple();

  /**
   * Returns true if and only if 'list' is in {final}
   */
  boolean isFinalList();

  /**
   * Returns true if and only if 'union' is in {final}
   */
  boolean isFinalUnion();


  /**
   * Returns true if and only if {variety} is 'atomic'
   */
  boolean isVarietyAtomic();


  /**
   * Returns true if and only if {variety} is 'list'
   */
  boolean isVarietyList();


  /**
   * Returns true if and only if {variety} is 'union'
   */
  boolean isVarietyUnion();


  /**
   * If {variety} is 'atomic' returns the {primitive type definition} if present,
   * otherwise null
   */
  SimpleTypeDefinition getPrimitiveTypeDefinition();


  /**
   * If {variety} is 'list' returns the {item type definition} if present,
   * otherwise null
   */
  SimpleTypeDefinition getItemTypeDefinition();


  /**
   * If {variety} is 'union' appends to the (non-null) Vector
   * the {member type definitions}
   */
  void getMemberTypeDefinitions(java.util.Vector v);
  
  Vector getMemberTypeDefinitions();

  Vector getFacets();

  void getFacets(Vector v);


  Facet[] getFacetsArray();


  /**
   * For primitives shouldn't return null
   */
  FundamentalFacets getFundamentalFacets();


  boolean isPrimitive();

  String getWhiteSpaceNormalizationValue();

}

