/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic.iiop;

import java.io.Serializable;

/**
 * Public class ClassDescriptor. Iinformation class representing a Descriptor
 * XML file.
 * refer Chapter 10.6 of SAP Java EE Application Server Connector Architecture 1.0
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov
 * @version 4.0
 */
public class ClassDescriptor implements Serializable {

  static final long serialVersionUID = 4649078063676609576L;

  private String name;
  private String superClass;
  private String superInterface[];
  private String _package;
  private String interfacePackage;
  private String accessFlag;
  private String exception[];
  private String attribute[];
  private String[] RMIRepositoryID;
  private String IDLRepositoryID;
  private String forStubName;
  private String resourceId;
  private ClassDescriptorField field[];
  private ClassDescriptorMethod method[];

  /*
   * Name field accessors
   * all are returned as strings
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * SuperClass field accessors
   * all are returned as strings
   */
  public String getSuperClass() {
    return superClass;
  }

  public void setSuperClass(String superClass) {
    this.superClass = superClass;
  }

  /*
   * SuperInterface field accessors
   * all are returned as strings
   */
  public String[] getSuperInterface() {
    return superInterface;
  }

  public void setSuperInterface(String[] superInterface) {
    this.superInterface = superInterface;
  }

  public String getSuperInterface(int i) {
    if (superInterface != null && superInterface.length > i && i >= 0) {
      return superInterface[i];
    } else {
      return null;
    }
  }

  public void setSuperInterface(int i, String entry) {
    if (superInterface == null || i >= superInterface.length) {
      String[] newObjects = new String[i + 1];

      if (superInterface != null) {
        for (int j = 0; j < superInterface.length; j++) {
          newObjects[j] = superInterface[j];
        } 
      }

      superInterface = newObjects;
    }

    superInterface[i] = entry;
  }

  public void setSuperInterface(String entry) {
    int length = 0;
    String[] newObjects = null;

    if (superInterface == null) {
      newObjects = new String[1];
    } else {
      length = superInterface.length;
      newObjects = new String[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(superInterface[i])) {
          superInterface[i] = entry;
          return;
        } else {
          newObjects[i] = superInterface[i];
        }
      } 
    }

    newObjects[length] = entry;
    superInterface = newObjects;
  }

  public void removeSuperInterface(String entry) {
    if (entry == null) {
      return;
    }

    if (superInterface != null && superInterface.length > 0) {
      String[] newObjects = new String[superInterface.length - 1];
      int index = 0;

      for (int j = 0; j < superInterface.length; j++) {
        if (superInterface[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = superInterface[j];
        }
      } 

      superInterface = newObjects;
    }
  }

  /*
   * Package field accessors
   * all are returned as strings
   */
  public String getPackage() {
    return _package;
  }

  public void setPackage(String _package) {
    this._package = _package;
  }

  /*
   * Interface package field accessors
   * all are returned as strings and used for
   * making _Stub packages
   */
  public String getInterfacePackage() {
    return interfacePackage;
  }

  public void setInterfacePackage(String interfacePackage) {
    this.interfacePackage = interfacePackage;
  }

  /*
   * AccessFlag field accessors
   * all are returned as strings
   */
  public String getAccessFlag() {
    return accessFlag;
  }

  public void setAccessFlag(String accessFlag) {
    this.accessFlag = accessFlag;
  }

  /*
   * Attribute field accessors
   * all are returned as strings
   */
  public String[] getAttribute() {
    return attribute;
  }

  public void setAttribute(String[] attribute) {
    this.attribute = attribute;
  }

  public String getAttribute(int i) {
    if (attribute != null && attribute.length > i && i >= 0) {
      return attribute[i];
    } else {
      return null;
    }
  }

  public void setAttribute(int i, String entry) {
    if (attribute == null || i >= attribute.length) {
      String[] newObjects = new String[i + 1];

      if (attribute != null) {
        for (int j = 0; j < attribute.length; j++) {
          newObjects[j] = attribute[j];
        } 
      }

      attribute = newObjects;
    }

    attribute[i] = entry;
  }

  public void setAttribute(String entry) {
    int length = 0;
    String[] newObjects = null;

    if (attribute == null) {
      newObjects = new String[1];
    } else {
      length = attribute.length;
      newObjects = new String[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(attribute[i])) {
          attribute[i] = entry;
          return;
        } else {
          newObjects[i] = attribute[i];
        }
      } 
    }

    newObjects[length] = entry;
    attribute = newObjects;
  }

  public void removeAttribute(String entry) {
    if (entry == null) {
      return;
    }

    if (attribute != null && attribute.length > 0) {
      String[] newObjects = new String[attribute.length - 1];
      int index = 0;

      for (int j = 0; j < attribute.length; j++) {
        if (attribute[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = attribute[j];
        }
      } 

      attribute = newObjects;
    }
  }

  /*
   * Field field accessors
   */
  public ClassDescriptorField[] getField() {
    return field;
  }

  public void setField(ClassDescriptorField[] field) {
    this.field = field;
  }

  public ClassDescriptorField getField(int i) {
    if (field != null && field.length > i && i >= 0) {
      return field[i];
    } else {
      return null;
    }
  }

  public void setField(int i, ClassDescriptorField entry) {
    if (field == null || i >= field.length) {
      ClassDescriptorField[] newObjects = new ClassDescriptorField[i + 1];

      if (field != null) {
        for (int j = 0; j < field.length; j++) {
          newObjects[j] = field[j];
        } 
      }

      field = newObjects;
    }

    field[i] = entry;
  }

  public void setField(ClassDescriptorField entry) {
    int length = 0;
    ClassDescriptorField[] newObjects = null;

    if (field == null) {
      newObjects = new ClassDescriptorField[1];
    } else {
      length = field.length;
      newObjects = new ClassDescriptorField[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(field[i])) {
          field[i] = entry;
          return;
        } else {
          newObjects[i] = field[i];
        }
      } 
    }

    newObjects[length] = entry;
    field = newObjects;
  }

  public void removeField(ClassDescriptorField entry) {
    if (entry == null) {
      return;
    }

    if (field != null && field.length > 0) {
      ClassDescriptorField[] newObjects = new ClassDescriptorField[field.length - 1];
      int index = 0;

      for (int j = 0; j < field.length; j++) {
        if (field[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = field[j];
        }
      } 

      field = newObjects;
    }
  }

  /*
   * Method field accessors
   */
  public ClassDescriptorMethod[] getMethod() {
    return method;
  }

  public void setMethod(ClassDescriptorMethod[] method) {
    this.method = method;
  }

  public ClassDescriptorMethod getMethod(int i) {
    if (method != null && method.length > i && i >= 0) {
      return method[i];
    } else {
      return null;
    }
  }

  public void setMethod(int i, ClassDescriptorMethod entry) {
    if (method == null || i >= method.length) {
      ClassDescriptorMethod[] newObjects = new ClassDescriptorMethod[i + 1];

      if (method != null) {
        for (int j = 0; j < method.length; j++) {
          newObjects[j] = method[j];
        } 
      }

      method = newObjects;
    }

    method[i] = entry;
  }

  public void setMethod(ClassDescriptorMethod entry) {
    int length = 0;
    ClassDescriptorMethod[] newObjects = null;

    if (method == null) {
      newObjects = new ClassDescriptorMethod[1];
    } else {
      length = method.length;
      newObjects = new ClassDescriptorMethod[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(method[i])) {
          method[i] = entry;
          return;
        } else {
          newObjects[i] = method[i];
        }
      } 
    }

    newObjects[length] = entry;
    method = newObjects;
  }

  public void removeMethod(ClassDescriptorMethod entry) {
    if (entry == null) {
      return;
    }

    if (method != null && method.length > 0) {
      ClassDescriptorMethod[] newObjects = new ClassDescriptorMethod[method.length - 1];
      int index = 0;

      for (int j = 0; j < method.length; j++) {
        if (method[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = method[j];
        }
      } 

      method = newObjects;
    }
  }

  /*
   * Repository ID's
   */
  public void setRMIRepositoryID(String[] RMIRepositoryID) {
    this.RMIRepositoryID = RMIRepositoryID;
  }

  public String[] getRMIRepositoryID() {
    return RMIRepositoryID;
  }

  public String getRMIRepositoryID(int i) {
    if (RMIRepositoryID != null && RMIRepositoryID.length > i && i >= 0) {
      return RMIRepositoryID[i];
    } else {
      return null;
    }
  }

  public void setRMIRepositoryID(String entry) {
    int length = 0;
    String[] newObjects = null;

    if (RMIRepositoryID == null) {
      newObjects = new String[1];
    } else {
      length = RMIRepositoryID.length;
      newObjects = new String[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(RMIRepositoryID[i])) {
          RMIRepositoryID[i] = entry;
          return;
        } else {
          newObjects[i] = RMIRepositoryID[i];
        }
      } 
    }

    newObjects[length] = entry;
    RMIRepositoryID = newObjects;
  }

  public void setIDLRepositoryID(String IDLRepositoryID) {
    this.IDLRepositoryID = IDLRepositoryID;
  }

  public String getIDLRepositoryID() {
    return IDLRepositoryID;
  }

  public void setForStubName(String forStubName) {
    this.forStubName = forStubName;
  }

  public String getForStubName() {
    return forStubName;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

}

