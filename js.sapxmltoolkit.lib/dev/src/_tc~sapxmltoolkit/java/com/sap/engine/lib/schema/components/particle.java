package com.sap.engine.lib.schema.components;

/**
 * Represents a particle component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Particle
 *     {min occurs}          A non-negative integer.
 *     {max occurs}          Either a non-negative integer or unbounded.
 *     {term}                One of a model group, a wildcard, or an element declaration.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface Particle
  extends Base {

  /**
   * The {min occurs} property
   */
  int getMinOccurs();


  /**
   * Returns true if and only if {max occurs} is 'unbounded'
   */
  boolean isMaxOccursUnbounded();


  /**
   * The {max occurs} property.
   * If maxOccurs is 'unbounded', returns Integer.MAX_VALUE
   */
  int getMaxOccurs();

  /**
   * The {term} - a model group, a wildcard, or an element declaration
   */
  Base getTerm();
  
  boolean isEmptiable();
}

