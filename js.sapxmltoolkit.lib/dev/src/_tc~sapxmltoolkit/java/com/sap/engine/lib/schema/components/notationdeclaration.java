package com.sap.engine.lib.schema.components;

/**
 * Represents a notation declaration component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Notation Declaration
 *    {name}               An NCName as defined by [XML-Namespaces].
 *    {target namespace}   Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *    {system identifier}  Optional if {public identifier} is present. A URI reference.
 *    {public identifier}  Optional if {system identifier} is present. A public identifier, as defined in [XML 1.0 (Second Edition)].
 *    {annotation}         Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
/*
 */
public interface NotationDeclaration
  extends QualifiedBase {

  String getSystemIdentifier();


  String getPublicIdentifier();

}

