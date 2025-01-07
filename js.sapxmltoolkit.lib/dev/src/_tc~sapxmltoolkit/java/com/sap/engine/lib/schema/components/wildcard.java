package com.sap.engine.lib.schema.components;


/**
 * Represents a wildcard component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Wildcard
 *     {namespace constraint} One of any; a pair of not and a namespace name or \u00b7absent\u00b7;
 *                                            or a set whose members are either namespace
 *                                            names or \u00b7absent\u00b7.
 *     {process contents}     One of skip, lax or strict.
 *     {annotation}           Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface Wildcard
  extends Base {

  /**
   * Returns true if and only if {process contents} is 'skip'
   */
  boolean isProcessContentsSkip();

  /**
   * Returns true if and only if {process contents} is 'lax'
   */
  boolean isProcessContentsLax();

  /**
   * Returns true if and only if {process contents} is 'strict'
   */
  boolean isProcessContentsStrict();

  /**
   * Returns true if and only if {namespace constraint} is 'any'
   */
  boolean isNamespaceConstraintAny();

  /**
   * If {namespace constraint} is a pair of 'not' and s, returns s,
   * otherwise null
   */
  String getNamespaceConstraintNegated();

  /**
   *
   */
  void getNamespaceConstraintMembers(java.util.Vector v);


  /**
   *
   */
  String[] getNamespaceConstraintMembersAsArray();
  
  boolean isAttribWildcard();

}

