﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}entity
 */
public  class Entity implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}name
  private java.lang.String _a_Name;
  /**
   * Set method for attribute {}name
   */
  public void setName(java.lang.String _Name) {
    this._a_Name = _Name;
  }
  /**
   * Get method for attribute {}name
   */
  public java.lang.String getName() {
    return _a_Name;
  }

  // Attribute field for attribute {}class
  private java.lang.String _a_ClassElement;
  /**
   * Set method for attribute {}class
   */
  public void setClassElement(java.lang.String _ClassElement) {
    this._a_ClassElement = _ClassElement;
  }
  /**
   * Get method for attribute {}class
   */
  public java.lang.String getClassElement() {
    return _a_ClassElement;
  }

  // Attribute field for attribute {}access
  private java.lang.String _a_Access;
  /**
   * Set method for attribute {}access
   */
  public void setAccess(java.lang.String _Access) {
    this._a_Access = _Access;
  }
  /**
   * Get method for attribute {}access
   */
  public java.lang.String getAccess() {
    return _a_Access;
  }

  // Attribute field for attribute {}metadata-complete
  private java.lang.Boolean _a_MetadataComplete;
  /**
   * Set method for attribute {}metadata-complete
   */
  public void setMetadataComplete(java.lang.Boolean _MetadataComplete) {
    this._a_MetadataComplete = _MetadataComplete;
  }
  /**
   * Get method for attribute {}metadata-complete
   */
  public java.lang.Boolean getMetadataComplete() {
    return _a_MetadataComplete;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}description
  private java.lang.String _f_Description;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}description
   */
  public void setDescription(java.lang.String _Description) {
    this._f_Description = _Description;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}description
   */
  public java.lang.String getDescription() {
    return this._f_Description;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}table
  private com.sap.engine.lib.descriptors5.ormapping.Table _f_Table;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}table
   */
  public void setTable(com.sap.engine.lib.descriptors5.ormapping.Table _Table) {
    this._f_Table = _Table;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}table
   */
  public com.sap.engine.lib.descriptors5.ormapping.Table getTable() {
    return this._f_Table;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}secondary-table
  private java.util.ArrayList _f_SecondaryTable = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}secondary-table
   */
  public void setSecondaryTable(com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] _SecondaryTable) {
    this._f_SecondaryTable.clear();
    if (_SecondaryTable != null) {
      for (int i=0; i<_SecondaryTable.length; i++) {
        if (_SecondaryTable[i] != null)
          this._f_SecondaryTable.add(_SecondaryTable[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}secondary-table
   */
  public com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] getSecondaryTable() {
    com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] result = new com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[_f_SecondaryTable.size()];
    _f_SecondaryTable.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}primary-key-join-column
  private java.util.ArrayList _f_PrimaryKeyJoinColumn = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}primary-key-join-column
   */
  public void setPrimaryKeyJoinColumn(com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] _PrimaryKeyJoinColumn) {
    this._f_PrimaryKeyJoinColumn.clear();
    if (_PrimaryKeyJoinColumn != null) {
      for (int i=0; i<_PrimaryKeyJoinColumn.length; i++) {
        if (_PrimaryKeyJoinColumn[i] != null)
          this._f_PrimaryKeyJoinColumn.add(_PrimaryKeyJoinColumn[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}primary-key-join-column
   */
  public com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] getPrimaryKeyJoinColumn() {
    com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] result = new com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[_f_PrimaryKeyJoinColumn.size()];
    _f_PrimaryKeyJoinColumn.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}id-class
  private com.sap.engine.lib.descriptors5.ormapping.IdClass _f_IdClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}id-class
   */
  public void setIdClass(com.sap.engine.lib.descriptors5.ormapping.IdClass _IdClass) {
    this._f_IdClass = _IdClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}id-class
   */
  public com.sap.engine.lib.descriptors5.ormapping.IdClass getIdClass() {
    return this._f_IdClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}inheritance
  private com.sap.engine.lib.descriptors5.ormapping.Inheritance _f_Inheritance;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}inheritance
   */
  public void setInheritance(com.sap.engine.lib.descriptors5.ormapping.Inheritance _Inheritance) {
    this._f_Inheritance = _Inheritance;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}inheritance
   */
  public com.sap.engine.lib.descriptors5.ormapping.Inheritance getInheritance() {
    return this._f_Inheritance;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-value
  private java.lang.String _f_DiscriminatorValue;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-value
   */
  public void setDiscriminatorValue(java.lang.String _DiscriminatorValue) {
    this._f_DiscriminatorValue = _DiscriminatorValue;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-value
   */
  public java.lang.String getDiscriminatorValue() {
    return this._f_DiscriminatorValue;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-column
  private com.sap.engine.lib.descriptors5.ormapping.DiscriminatorColumn _f_DiscriminatorColumn;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-column
   */
  public void setDiscriminatorColumn(com.sap.engine.lib.descriptors5.ormapping.DiscriminatorColumn _DiscriminatorColumn) {
    this._f_DiscriminatorColumn = _DiscriminatorColumn;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}discriminator-column
   */
  public com.sap.engine.lib.descriptors5.ormapping.DiscriminatorColumn getDiscriminatorColumn() {
    return this._f_DiscriminatorColumn;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}sequence-generator
  private com.sap.engine.lib.descriptors5.ormapping.SequenceGenerator _f_SequenceGenerator;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}sequence-generator
   */
  public void setSequenceGenerator(com.sap.engine.lib.descriptors5.ormapping.SequenceGenerator _SequenceGenerator) {
    this._f_SequenceGenerator = _SequenceGenerator;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}sequence-generator
   */
  public com.sap.engine.lib.descriptors5.ormapping.SequenceGenerator getSequenceGenerator() {
    return this._f_SequenceGenerator;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}table-generator
  private com.sap.engine.lib.descriptors5.ormapping.TableGenerator _f_TableGenerator;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}table-generator
   */
  public void setTableGenerator(com.sap.engine.lib.descriptors5.ormapping.TableGenerator _TableGenerator) {
    this._f_TableGenerator = _TableGenerator;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}table-generator
   */
  public com.sap.engine.lib.descriptors5.ormapping.TableGenerator getTableGenerator() {
    return this._f_TableGenerator;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}named-query
  private java.util.ArrayList _f_NamedQuery = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}named-query
   */
  public void setNamedQuery(com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] _NamedQuery) {
    this._f_NamedQuery.clear();
    if (_NamedQuery != null) {
      for (int i=0; i<_NamedQuery.length; i++) {
        if (_NamedQuery[i] != null)
          this._f_NamedQuery.add(_NamedQuery[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}named-query
   */
  public com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] getNamedQuery() {
    com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] result = new com.sap.engine.lib.descriptors5.ormapping.NamedQuery[_f_NamedQuery.size()];
    _f_NamedQuery.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}named-native-query
  private java.util.ArrayList _f_NamedNativeQuery = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}named-native-query
   */
  public void setNamedNativeQuery(com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] _NamedNativeQuery) {
    this._f_NamedNativeQuery.clear();
    if (_NamedNativeQuery != null) {
      for (int i=0; i<_NamedNativeQuery.length; i++) {
        if (_NamedNativeQuery[i] != null)
          this._f_NamedNativeQuery.add(_NamedNativeQuery[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}named-native-query
   */
  public com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] getNamedNativeQuery() {
    com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] result = new com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[_f_NamedNativeQuery.size()];
    _f_NamedNativeQuery.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}sql-result-set-mapping
  private java.util.ArrayList _f_SqlResultSetMapping = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}sql-result-set-mapping
   */
  public void setSqlResultSetMapping(com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] _SqlResultSetMapping) {
    this._f_SqlResultSetMapping.clear();
    if (_SqlResultSetMapping != null) {
      for (int i=0; i<_SqlResultSetMapping.length; i++) {
        if (_SqlResultSetMapping[i] != null)
          this._f_SqlResultSetMapping.add(_SqlResultSetMapping[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}sql-result-set-mapping
   */
  public com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] getSqlResultSetMapping() {
    com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] result = new com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[_f_SqlResultSetMapping.size()];
    _f_SqlResultSetMapping.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}exclude-default-listeners
  private com.sap.engine.lib.descriptors5.ormapping.EmptyType _f_ExcludeDefaultListeners;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}exclude-default-listeners
   */
  public void setExcludeDefaultListeners(com.sap.engine.lib.descriptors5.ormapping.EmptyType _ExcludeDefaultListeners) {
    this._f_ExcludeDefaultListeners = _ExcludeDefaultListeners;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}exclude-default-listeners
   */
  public com.sap.engine.lib.descriptors5.ormapping.EmptyType getExcludeDefaultListeners() {
    return this._f_ExcludeDefaultListeners;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}exclude-superclass-listeners
  private com.sap.engine.lib.descriptors5.ormapping.EmptyType _f_ExcludeSuperclassListeners;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}exclude-superclass-listeners
   */
  public void setExcludeSuperclassListeners(com.sap.engine.lib.descriptors5.ormapping.EmptyType _ExcludeSuperclassListeners) {
    this._f_ExcludeSuperclassListeners = _ExcludeSuperclassListeners;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}exclude-superclass-listeners
   */
  public com.sap.engine.lib.descriptors5.ormapping.EmptyType getExcludeSuperclassListeners() {
    return this._f_ExcludeSuperclassListeners;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}entity-listeners
  private com.sap.engine.lib.descriptors5.ormapping.EntityListeners _f_EntityListeners;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}entity-listeners
   */
  public void setEntityListeners(com.sap.engine.lib.descriptors5.ormapping.EntityListeners _EntityListeners) {
    this._f_EntityListeners = _EntityListeners;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}entity-listeners
   */
  public com.sap.engine.lib.descriptors5.ormapping.EntityListeners getEntityListeners() {
    return this._f_EntityListeners;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}pre-persist
  private com.sap.engine.lib.descriptors5.ormapping.PrePersist _f_PrePersist;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}pre-persist
   */
  public void setPrePersist(com.sap.engine.lib.descriptors5.ormapping.PrePersist _PrePersist) {
    this._f_PrePersist = _PrePersist;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}pre-persist
   */
  public com.sap.engine.lib.descriptors5.ormapping.PrePersist getPrePersist() {
    return this._f_PrePersist;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}post-persist
  private com.sap.engine.lib.descriptors5.ormapping.PostPersist _f_PostPersist;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}post-persist
   */
  public void setPostPersist(com.sap.engine.lib.descriptors5.ormapping.PostPersist _PostPersist) {
    this._f_PostPersist = _PostPersist;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}post-persist
   */
  public com.sap.engine.lib.descriptors5.ormapping.PostPersist getPostPersist() {
    return this._f_PostPersist;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}pre-remove
  private com.sap.engine.lib.descriptors5.ormapping.PreRemove _f_PreRemove;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}pre-remove
   */
  public void setPreRemove(com.sap.engine.lib.descriptors5.ormapping.PreRemove _PreRemove) {
    this._f_PreRemove = _PreRemove;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}pre-remove
   */
  public com.sap.engine.lib.descriptors5.ormapping.PreRemove getPreRemove() {
    return this._f_PreRemove;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}post-remove
  private com.sap.engine.lib.descriptors5.ormapping.PostRemove _f_PostRemove;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}post-remove
   */
  public void setPostRemove(com.sap.engine.lib.descriptors5.ormapping.PostRemove _PostRemove) {
    this._f_PostRemove = _PostRemove;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}post-remove
   */
  public com.sap.engine.lib.descriptors5.ormapping.PostRemove getPostRemove() {
    return this._f_PostRemove;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}pre-update
  private com.sap.engine.lib.descriptors5.ormapping.PreUpdate _f_PreUpdate;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}pre-update
   */
  public void setPreUpdate(com.sap.engine.lib.descriptors5.ormapping.PreUpdate _PreUpdate) {
    this._f_PreUpdate = _PreUpdate;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}pre-update
   */
  public com.sap.engine.lib.descriptors5.ormapping.PreUpdate getPreUpdate() {
    return this._f_PreUpdate;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}post-update
  private com.sap.engine.lib.descriptors5.ormapping.PostUpdate _f_PostUpdate;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}post-update
   */
  public void setPostUpdate(com.sap.engine.lib.descriptors5.ormapping.PostUpdate _PostUpdate) {
    this._f_PostUpdate = _PostUpdate;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}post-update
   */
  public com.sap.engine.lib.descriptors5.ormapping.PostUpdate getPostUpdate() {
    return this._f_PostUpdate;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}post-load
  private com.sap.engine.lib.descriptors5.ormapping.PostLoad _f_PostLoad;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}post-load
   */
  public void setPostLoad(com.sap.engine.lib.descriptors5.ormapping.PostLoad _PostLoad) {
    this._f_PostLoad = _PostLoad;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}post-load
   */
  public com.sap.engine.lib.descriptors5.ormapping.PostLoad getPostLoad() {
    return this._f_PostLoad;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}attribute-override
  private java.util.ArrayList _f_AttributeOverride = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}attribute-override
   */
  public void setAttributeOverride(com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] _AttributeOverride) {
    this._f_AttributeOverride.clear();
    if (_AttributeOverride != null) {
      for (int i=0; i<_AttributeOverride.length; i++) {
        if (_AttributeOverride[i] != null)
          this._f_AttributeOverride.add(_AttributeOverride[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}attribute-override
   */
  public com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] getAttributeOverride() {
    com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] result = new com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[_f_AttributeOverride.size()];
    _f_AttributeOverride.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}association-override
  private java.util.ArrayList _f_AssociationOverride = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}association-override
   */
  public void setAssociationOverride(com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] _AssociationOverride) {
    this._f_AssociationOverride.clear();
    if (_AssociationOverride != null) {
      for (int i=0; i<_AssociationOverride.length; i++) {
        if (_AssociationOverride[i] != null)
          this._f_AssociationOverride.add(_AssociationOverride[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}association-override
   */
  public com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] getAssociationOverride() {
    com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] result = new com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[_f_AssociationOverride.size()];
    _f_AssociationOverride.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}attributes
  private com.sap.engine.lib.descriptors5.ormapping.Attributes _f_Attributes;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}attributes
   */
  public void setAttributes(com.sap.engine.lib.descriptors5.ormapping.Attributes _Attributes) {
    this._f_Attributes = _Attributes;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}attributes
   */
  public com.sap.engine.lib.descriptors5.ormapping.Attributes getAttributes() {
    return this._f_Attributes;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof Entity)) return false;
    Entity typed = (Entity) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    if (this._a_ClassElement != null) {
      if (typed._a_ClassElement == null) return false;
      if (!this._a_ClassElement.equals(typed._a_ClassElement)) return false;
    } else {
      if (typed._a_ClassElement != null) return false;
    }
    if (this._a_Access != null) {
      if (typed._a_Access == null) return false;
      if (!this._a_Access.equals(typed._a_Access)) return false;
    } else {
      if (typed._a_Access != null) return false;
    }
    if (this._a_MetadataComplete != null) {
      if (typed._a_MetadataComplete == null) return false;
      if (!this._a_MetadataComplete.equals(typed._a_MetadataComplete)) return false;
    } else {
      if (typed._a_MetadataComplete != null) return false;
    }
    if (this._f_Description != null) {
      if (typed._f_Description == null) return false;
      if (!this._f_Description.equals(typed._f_Description)) return false;
    } else {
      if (typed._f_Description != null) return false;
    }
    if (this._f_Table != null) {
      if (typed._f_Table == null) return false;
      if (!this._f_Table.equals(typed._f_Table)) return false;
    } else {
      if (typed._f_Table != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] _f_SecondaryTable1 = this.getSecondaryTable();
    com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] _f_SecondaryTable2 = typed.getSecondaryTable();
    if (_f_SecondaryTable1 != null) {
      if (_f_SecondaryTable2 == null) return false;
      if (_f_SecondaryTable1.length != _f_SecondaryTable2.length) return false;
      for (int i1 = 0; i1 < _f_SecondaryTable1.length ; i1++) {
        if (_f_SecondaryTable1[i1] != null) {
          if (_f_SecondaryTable2[i1] == null) return false;
          if (!_f_SecondaryTable1[i1].equals(_f_SecondaryTable2[i1])) return false;
        } else {
          if (_f_SecondaryTable2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SecondaryTable2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] _f_PrimaryKeyJoinColumn1 = this.getPrimaryKeyJoinColumn();
    com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] _f_PrimaryKeyJoinColumn2 = typed.getPrimaryKeyJoinColumn();
    if (_f_PrimaryKeyJoinColumn1 != null) {
      if (_f_PrimaryKeyJoinColumn2 == null) return false;
      if (_f_PrimaryKeyJoinColumn1.length != _f_PrimaryKeyJoinColumn2.length) return false;
      for (int i1 = 0; i1 < _f_PrimaryKeyJoinColumn1.length ; i1++) {
        if (_f_PrimaryKeyJoinColumn1[i1] != null) {
          if (_f_PrimaryKeyJoinColumn2[i1] == null) return false;
          if (!_f_PrimaryKeyJoinColumn1[i1].equals(_f_PrimaryKeyJoinColumn2[i1])) return false;
        } else {
          if (_f_PrimaryKeyJoinColumn2[i1] != null) return false;
        }
      }
    } else {
      if (_f_PrimaryKeyJoinColumn2 != null) return false;
    }
    if (this._f_IdClass != null) {
      if (typed._f_IdClass == null) return false;
      if (!this._f_IdClass.equals(typed._f_IdClass)) return false;
    } else {
      if (typed._f_IdClass != null) return false;
    }
    if (this._f_Inheritance != null) {
      if (typed._f_Inheritance == null) return false;
      if (!this._f_Inheritance.equals(typed._f_Inheritance)) return false;
    } else {
      if (typed._f_Inheritance != null) return false;
    }
    if (this._f_DiscriminatorValue != null) {
      if (typed._f_DiscriminatorValue == null) return false;
      if (!this._f_DiscriminatorValue.equals(typed._f_DiscriminatorValue)) return false;
    } else {
      if (typed._f_DiscriminatorValue != null) return false;
    }
    if (this._f_DiscriminatorColumn != null) {
      if (typed._f_DiscriminatorColumn == null) return false;
      if (!this._f_DiscriminatorColumn.equals(typed._f_DiscriminatorColumn)) return false;
    } else {
      if (typed._f_DiscriminatorColumn != null) return false;
    }
    if (this._f_SequenceGenerator != null) {
      if (typed._f_SequenceGenerator == null) return false;
      if (!this._f_SequenceGenerator.equals(typed._f_SequenceGenerator)) return false;
    } else {
      if (typed._f_SequenceGenerator != null) return false;
    }
    if (this._f_TableGenerator != null) {
      if (typed._f_TableGenerator == null) return false;
      if (!this._f_TableGenerator.equals(typed._f_TableGenerator)) return false;
    } else {
      if (typed._f_TableGenerator != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] _f_NamedQuery1 = this.getNamedQuery();
    com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] _f_NamedQuery2 = typed.getNamedQuery();
    if (_f_NamedQuery1 != null) {
      if (_f_NamedQuery2 == null) return false;
      if (_f_NamedQuery1.length != _f_NamedQuery2.length) return false;
      for (int i1 = 0; i1 < _f_NamedQuery1.length ; i1++) {
        if (_f_NamedQuery1[i1] != null) {
          if (_f_NamedQuery2[i1] == null) return false;
          if (!_f_NamedQuery1[i1].equals(_f_NamedQuery2[i1])) return false;
        } else {
          if (_f_NamedQuery2[i1] != null) return false;
        }
      }
    } else {
      if (_f_NamedQuery2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] _f_NamedNativeQuery1 = this.getNamedNativeQuery();
    com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] _f_NamedNativeQuery2 = typed.getNamedNativeQuery();
    if (_f_NamedNativeQuery1 != null) {
      if (_f_NamedNativeQuery2 == null) return false;
      if (_f_NamedNativeQuery1.length != _f_NamedNativeQuery2.length) return false;
      for (int i1 = 0; i1 < _f_NamedNativeQuery1.length ; i1++) {
        if (_f_NamedNativeQuery1[i1] != null) {
          if (_f_NamedNativeQuery2[i1] == null) return false;
          if (!_f_NamedNativeQuery1[i1].equals(_f_NamedNativeQuery2[i1])) return false;
        } else {
          if (_f_NamedNativeQuery2[i1] != null) return false;
        }
      }
    } else {
      if (_f_NamedNativeQuery2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] _f_SqlResultSetMapping1 = this.getSqlResultSetMapping();
    com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] _f_SqlResultSetMapping2 = typed.getSqlResultSetMapping();
    if (_f_SqlResultSetMapping1 != null) {
      if (_f_SqlResultSetMapping2 == null) return false;
      if (_f_SqlResultSetMapping1.length != _f_SqlResultSetMapping2.length) return false;
      for (int i1 = 0; i1 < _f_SqlResultSetMapping1.length ; i1++) {
        if (_f_SqlResultSetMapping1[i1] != null) {
          if (_f_SqlResultSetMapping2[i1] == null) return false;
          if (!_f_SqlResultSetMapping1[i1].equals(_f_SqlResultSetMapping2[i1])) return false;
        } else {
          if (_f_SqlResultSetMapping2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SqlResultSetMapping2 != null) return false;
    }
    if (this._f_ExcludeDefaultListeners != null) {
      if (typed._f_ExcludeDefaultListeners == null) return false;
      if (!this._f_ExcludeDefaultListeners.equals(typed._f_ExcludeDefaultListeners)) return false;
    } else {
      if (typed._f_ExcludeDefaultListeners != null) return false;
    }
    if (this._f_ExcludeSuperclassListeners != null) {
      if (typed._f_ExcludeSuperclassListeners == null) return false;
      if (!this._f_ExcludeSuperclassListeners.equals(typed._f_ExcludeSuperclassListeners)) return false;
    } else {
      if (typed._f_ExcludeSuperclassListeners != null) return false;
    }
    if (this._f_EntityListeners != null) {
      if (typed._f_EntityListeners == null) return false;
      if (!this._f_EntityListeners.equals(typed._f_EntityListeners)) return false;
    } else {
      if (typed._f_EntityListeners != null) return false;
    }
    if (this._f_PrePersist != null) {
      if (typed._f_PrePersist == null) return false;
      if (!this._f_PrePersist.equals(typed._f_PrePersist)) return false;
    } else {
      if (typed._f_PrePersist != null) return false;
    }
    if (this._f_PostPersist != null) {
      if (typed._f_PostPersist == null) return false;
      if (!this._f_PostPersist.equals(typed._f_PostPersist)) return false;
    } else {
      if (typed._f_PostPersist != null) return false;
    }
    if (this._f_PreRemove != null) {
      if (typed._f_PreRemove == null) return false;
      if (!this._f_PreRemove.equals(typed._f_PreRemove)) return false;
    } else {
      if (typed._f_PreRemove != null) return false;
    }
    if (this._f_PostRemove != null) {
      if (typed._f_PostRemove == null) return false;
      if (!this._f_PostRemove.equals(typed._f_PostRemove)) return false;
    } else {
      if (typed._f_PostRemove != null) return false;
    }
    if (this._f_PreUpdate != null) {
      if (typed._f_PreUpdate == null) return false;
      if (!this._f_PreUpdate.equals(typed._f_PreUpdate)) return false;
    } else {
      if (typed._f_PreUpdate != null) return false;
    }
    if (this._f_PostUpdate != null) {
      if (typed._f_PostUpdate == null) return false;
      if (!this._f_PostUpdate.equals(typed._f_PostUpdate)) return false;
    } else {
      if (typed._f_PostUpdate != null) return false;
    }
    if (this._f_PostLoad != null) {
      if (typed._f_PostLoad == null) return false;
      if (!this._f_PostLoad.equals(typed._f_PostLoad)) return false;
    } else {
      if (typed._f_PostLoad != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] _f_AttributeOverride1 = this.getAttributeOverride();
    com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] _f_AttributeOverride2 = typed.getAttributeOverride();
    if (_f_AttributeOverride1 != null) {
      if (_f_AttributeOverride2 == null) return false;
      if (_f_AttributeOverride1.length != _f_AttributeOverride2.length) return false;
      for (int i1 = 0; i1 < _f_AttributeOverride1.length ; i1++) {
        if (_f_AttributeOverride1[i1] != null) {
          if (_f_AttributeOverride2[i1] == null) return false;
          if (!_f_AttributeOverride1[i1].equals(_f_AttributeOverride2[i1])) return false;
        } else {
          if (_f_AttributeOverride2[i1] != null) return false;
        }
      }
    } else {
      if (_f_AttributeOverride2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] _f_AssociationOverride1 = this.getAssociationOverride();
    com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] _f_AssociationOverride2 = typed.getAssociationOverride();
    if (_f_AssociationOverride1 != null) {
      if (_f_AssociationOverride2 == null) return false;
      if (_f_AssociationOverride1.length != _f_AssociationOverride2.length) return false;
      for (int i1 = 0; i1 < _f_AssociationOverride1.length ; i1++) {
        if (_f_AssociationOverride1[i1] != null) {
          if (_f_AssociationOverride2[i1] == null) return false;
          if (!_f_AssociationOverride1[i1].equals(_f_AssociationOverride2[i1])) return false;
        } else {
          if (_f_AssociationOverride2[i1] != null) return false;
        }
      }
    } else {
      if (_f_AssociationOverride2 != null) return false;
    }
    if (this._f_Attributes != null) {
      if (typed._f_Attributes == null) return false;
      if (!this._f_Attributes.equals(typed._f_Attributes)) return false;
    } else {
      if (typed._f_Attributes != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    if (this._a_ClassElement != null) {
      result+= this._a_ClassElement.hashCode();
    }
    if (this._a_Access != null) {
      result+= this._a_Access.hashCode();
    }
    if (this._a_MetadataComplete != null) {
      result+= this._a_MetadataComplete.hashCode();
    }
    if (this._f_Description != null) {
      result+= this._f_Description.hashCode();
    }
    if (this._f_Table != null) {
      result+= this._f_Table.hashCode();
    }
    com.sap.engine.lib.descriptors5.ormapping.SecondaryTable[] _f_SecondaryTable1 = this.getSecondaryTable();
    if (_f_SecondaryTable1 != null) {
      for (int i1 = 0; i1 < _f_SecondaryTable1.length ; i1++) {
        if (_f_SecondaryTable1[i1] != null) {
          result+= _f_SecondaryTable1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.ormapping.PrimaryKeyJoinColumn[] _f_PrimaryKeyJoinColumn1 = this.getPrimaryKeyJoinColumn();
    if (_f_PrimaryKeyJoinColumn1 != null) {
      for (int i1 = 0; i1 < _f_PrimaryKeyJoinColumn1.length ; i1++) {
        if (_f_PrimaryKeyJoinColumn1[i1] != null) {
          result+= _f_PrimaryKeyJoinColumn1[i1].hashCode();
        }
      }
    }
    if (this._f_IdClass != null) {
      result+= this._f_IdClass.hashCode();
    }
    if (this._f_Inheritance != null) {
      result+= this._f_Inheritance.hashCode();
    }
    if (this._f_DiscriminatorValue != null) {
      result+= this._f_DiscriminatorValue.hashCode();
    }
    if (this._f_DiscriminatorColumn != null) {
      result+= this._f_DiscriminatorColumn.hashCode();
    }
    if (this._f_SequenceGenerator != null) {
      result+= this._f_SequenceGenerator.hashCode();
    }
    if (this._f_TableGenerator != null) {
      result+= this._f_TableGenerator.hashCode();
    }
    com.sap.engine.lib.descriptors5.ormapping.NamedQuery[] _f_NamedQuery1 = this.getNamedQuery();
    if (_f_NamedQuery1 != null) {
      for (int i1 = 0; i1 < _f_NamedQuery1.length ; i1++) {
        if (_f_NamedQuery1[i1] != null) {
          result+= _f_NamedQuery1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.ormapping.NamedNativeQuery[] _f_NamedNativeQuery1 = this.getNamedNativeQuery();
    if (_f_NamedNativeQuery1 != null) {
      for (int i1 = 0; i1 < _f_NamedNativeQuery1.length ; i1++) {
        if (_f_NamedNativeQuery1[i1] != null) {
          result+= _f_NamedNativeQuery1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.ormapping.SqlResultSetMapping[] _f_SqlResultSetMapping1 = this.getSqlResultSetMapping();
    if (_f_SqlResultSetMapping1 != null) {
      for (int i1 = 0; i1 < _f_SqlResultSetMapping1.length ; i1++) {
        if (_f_SqlResultSetMapping1[i1] != null) {
          result+= _f_SqlResultSetMapping1[i1].hashCode();
        }
      }
    }
    if (this._f_ExcludeDefaultListeners != null) {
      result+= this._f_ExcludeDefaultListeners.hashCode();
    }
    if (this._f_ExcludeSuperclassListeners != null) {
      result+= this._f_ExcludeSuperclassListeners.hashCode();
    }
    if (this._f_EntityListeners != null) {
      result+= this._f_EntityListeners.hashCode();
    }
    if (this._f_PrePersist != null) {
      result+= this._f_PrePersist.hashCode();
    }
    if (this._f_PostPersist != null) {
      result+= this._f_PostPersist.hashCode();
    }
    if (this._f_PreRemove != null) {
      result+= this._f_PreRemove.hashCode();
    }
    if (this._f_PostRemove != null) {
      result+= this._f_PostRemove.hashCode();
    }
    if (this._f_PreUpdate != null) {
      result+= this._f_PreUpdate.hashCode();
    }
    if (this._f_PostUpdate != null) {
      result+= this._f_PostUpdate.hashCode();
    }
    if (this._f_PostLoad != null) {
      result+= this._f_PostLoad.hashCode();
    }
    com.sap.engine.lib.descriptors5.ormapping.AttributeOverride[] _f_AttributeOverride1 = this.getAttributeOverride();
    if (_f_AttributeOverride1 != null) {
      for (int i1 = 0; i1 < _f_AttributeOverride1.length ; i1++) {
        if (_f_AttributeOverride1[i1] != null) {
          result+= _f_AttributeOverride1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.ormapping.AssociationOverride[] _f_AssociationOverride1 = this.getAssociationOverride();
    if (_f_AssociationOverride1 != null) {
      for (int i1 = 0; i1 < _f_AssociationOverride1.length ; i1++) {
        if (_f_AssociationOverride1[i1] != null) {
          result+= _f_AssociationOverride1[i1].hashCode();
        }
      }
    }
    if (this._f_Attributes != null) {
      result+= this._f_Attributes.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
