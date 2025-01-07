package com.sap.engine.lib.schema.components;

/**
 * Represents an attribute use component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Attribute Use
 *     {required}                 A boolean.
 *     {attribute declaration}    An attribute declaration.
 *     {value constraint}         Optional. A pair consisting of a value and one of default, fixed.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface AttributeUse
  extends Base {

  /**
   * Corresponds to the {required} property
   */
  boolean isRequired();

  boolean isProhibited();


  /**
   * Returns the {attribute declaration} attached to this attribute use
   */
  AttributeDeclaration getAttributeDeclaration();


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

