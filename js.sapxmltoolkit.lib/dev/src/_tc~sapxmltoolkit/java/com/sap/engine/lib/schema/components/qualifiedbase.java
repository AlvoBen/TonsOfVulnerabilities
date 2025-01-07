package com.sap.engine.lib.schema.components;


/**
 * A base interface for all components with a {name} and {target namespace} properties.
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public abstract interface QualifiedBase
  extends Base {

  /**
   * Should never return null.
   */
  String getName();


  /**
   * Should never return null.
   * If the target namespace is a 'null target namespace' (i.e. absent),
   * returns the empty String "".
   */
  String getTargetNamespace();

  boolean isAnonymous();

  boolean isTopLevel();

  String getQualifiedKey();
}

