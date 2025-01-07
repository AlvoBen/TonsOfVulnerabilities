package com.sap.engine.lib.schema.components;

/**
 * Represents a model group definition component.
 *
 * Excerpt:
 * <pre>
 *   Schema Component: Model Group Definition
 *     {name}                An NCName as defined by [XML-Namespaces].
 *     {target namespace}    Either \u00b7absent\u00b7 or a namespace name, as defined in [XML-Namespaces].
 *     {model group}         A model group.
 *     {annotation}          Optional. An annotation.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface ModelGroupDefinition extends RedefineableQualifiedBase {

  /**
   * Returns the {model group}
   */
  ModelGroup getModelGroup();

}

