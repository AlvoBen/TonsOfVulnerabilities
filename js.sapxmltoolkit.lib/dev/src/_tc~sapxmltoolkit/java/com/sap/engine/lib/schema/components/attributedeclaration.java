package com.sap.engine.lib.schema.components;

/**
 * Represents an attribute declaration component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Attribute Declaration
 *   {name}             An NCName as defined by [XML-Namespaces].
 *   {target namespace} Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *   {type definition}  A simple type definition.
 *   {scope}            Optional. Either global or a complex type definition.
 *   {value constraint} Optional. A pair consisting of a value and one of default, fixed.
 *   {annotation}       Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface AttributeDeclaration
  extends InfoItemDeclarationBase {

//  public boolean isProhibited();
//
//  public boolean isRequired();

}

