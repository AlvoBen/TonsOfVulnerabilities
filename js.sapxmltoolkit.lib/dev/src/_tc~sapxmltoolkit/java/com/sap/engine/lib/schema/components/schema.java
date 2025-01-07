package com.sap.engine.lib.schema.components;

import java.util.*;

/**
 * Represents a schema component.
 *
 * Exceprt:
 * <pre>
 *   Schema Component: Schema
 *     {type definitions}             A set of named simple and complex type definitions.
 *     {attribute declarations}       A set of named (top-level) attribute declarations.
 *     {element declarations}         A set of named (top-level) element declarations.
 *     {attribute group definitions}  A set of named attribute group definitions.
 *     {model group definitions}      A set of named model group definitions.
 *     {notation declarations}        A set of notation declarations.
 *     {annotations}                  A set of annotations.
 * </pre>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 */
public interface Schema
  extends Base {

  IdentityConstraintDefinition getIdentityConstraintDefinition(String uri, String local);

  TypeDefinitionBase getTopLevelTypeDefinition(String uri, String local);

  AttributeDeclaration getTopLevelAttributeDeclaration(String uri, String local);

  ElementDeclaration getTopLevelElementDeclaration(String uri, String local);

  AttributeGroupDefinition getTopLevelAttributeGroupDefinition(String uri, String local);

  ModelGroupDefinition getTopLevelModelGroupDefinition(String uri, String local);

  NotationDeclaration getTopLevelNotationDeclaration(String uri, String local);

  QualifiedBase getTopLevelComponent(String uri, String local, int typeOfComponent);

	QualifiedBase getTopLevelComponent(String uri, String local, String componentId);

  void getIdentityConstraintDefinitions(Vector v);

  void getTopLevelTypeDefinitions(Vector v);

  void getTopLevelAttributeDeclarations(Vector v);

  void getTopLevelElementDeclarations(Vector v);

  void getTopLevelAttributeGroupDefinitions(Vector v);

  void getTopLevelModelGroupDefinitions(Vector v);

  void getTopLevelNotationDeclarations(Vector v);

  void getTopLevelComponents(Vector v);

  IdentityConstraintDefinition[] getIdentityConstraintDefinitionsArray();

  TypeDefinitionBase[] getTopLevelTypeDefinitionsArray();

  AttributeDeclaration[] getTopLevelAttributeDeclarationsArray();

  ElementDeclaration[] getTopLevelElementDeclarationsArray();

  AttributeGroupDefinition[] getTopLevelAttributeGroupDefinitionsArray();

  ModelGroupDefinition[] getTopLevelModelGroupDefinitionsArray();

  NotationDeclaration[] getTopLevelNotationDeclarationsArray();

	QualifiedBase[] getTopLevelComponentsArray();

  Base[] getAllComponentsAsArray();

  void getAllComponents(Vector collector);

  void getBuiltInTypeDefinitions(Vector collector);

  TypeDefinitionBase[] getBuiltInTypeDefinitions();
  
  String getLocation();
  
  String getTargetNamespace();
}

