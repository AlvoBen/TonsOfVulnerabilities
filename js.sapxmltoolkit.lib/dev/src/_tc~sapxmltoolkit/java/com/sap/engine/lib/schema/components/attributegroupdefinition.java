package com.sap.engine.lib.schema.components;

import java.util.Vector;

/**
 * Represents an attribute group definition component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Attribute Group Definition
 *     {name}                An NCName as defined by [XML-Namespaces].
 *     {target namespace}    Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {attribute uses}      A set of attribute uses.
 *     {attribute wildcard}  Optional. A wildcard.
 *     {annotation}          Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface AttributeGroupDefinition extends RedefineableQualifiedBase {

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

}

