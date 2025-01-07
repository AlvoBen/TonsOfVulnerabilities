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

import com.sap.engine.lib.xml.StandardDOMWriterNoSpace;
import com.sap.engine.lib.xml.WrongStructureException;
import com.sap.engine.rmic.log.RMICLogger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * ClassDescriptorDocument is used to load ClassDescriptor from xml Document or
 * create an xml Document from ClassDescriptor. It defines the special structure of
 * the expected xml in the dtd constant and uses it to read and write descriptors in xmls
 * according to it.
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov, Mladen Droshev
 * @version 6.30
 */
public class ClassDescriptorDocument implements DTDContastants {

  // The structure of the xml document that is generated or read.
  private static final String dtd = "<!DOCTYPE Class [" +
          "\r\n<!ELEMENT Class (Package?,StubPackage,AccessFlag?,Attribute*,Name,SuperClass?,SuperInterface*,ForStubName?,RMIRepositoryID+,ResourceId,Field*,Method*) >" +
          "\r\n  <!ELEMENT Package (#PCDATA) >" +
          "\r\n  <!ELEMENT StubPackage (#PCDATA) >" +
          "\r\n  <!ELEMENT AccessFlag (#PCDATA) >" +
          "\r\n  <!ELEMENT Name (#PCDATA) >" +
          "\r\n  <!ELEMENT SuperClass (#PCDATA) >" +
          "\r\n  <!ELEMENT SuperInterface (#PCDATA) >" +
          "\r\n  <!ELEMENT ForStubName (#PCDATA) >" +
          "\r\n  <!ELEMENT RMIRepositoryID (#PCDATA) >" +
          "\r\n  <!ELEMENT ResourceId (#PCDATA) >" +
          "\r\n  <!ELEMENT Field (Name,AccessFlag,Attribute*,Type) >" +
          "\r\n  <!ELEMENT Type (#PCDATA) >" +
          "\r\n  <!ELEMENT Attribute (#PCDATA) >" +
          "\r\n  <!ELEMENT Method (Name,IDLName,AccessFlag,Attribute*,IsRemoteReturnType,ReturnType,Parameter*,Exception*)>" +
          "\r\n  <!ELEMENT ReturnType (IsReturnType,IsInterface,ClassType,ToWriteAsObject,ForStubName,Method*)>" +
          "\r\n  <!ELEMENT IsReturnType (#PCDATA)>" +
          "\r\n  <!ELEMENT ClassType (#PCDATA)>" +
          "\r\n  <!ELEMENT IDLName (#PCDATA) >" +
          "\r\n  <!ELEMENT IsRemoteReturnType (#PCDATA) >" +
          "\r\n  <!ELEMENT ToWriteAsObject (#PCDATA)>" +
          "\r\n  <!ELEMENT Parameter (Type,IsInterface,IsRemote,ToWriteAsObject,ForStubName,Method) >" +
          "\r\n  <!ELEMENT IsInterface (#PCDATA) >" +
          "\r\n  <!ELEMENT IsRemote (#PCDATA) >" +
          "\r\n  <!ELEMENT Exception (Name,IDLRepostoryID*,IsRemoteException) >" +
          "\r\n  <!ELEMENT IDLRepostoryID (#PCDATA) >" +
          "\r\n  <!ELEMENT IsRemoteException (#PCDATA) >" +
          "]>";

  protected Document mainDocument;
  protected ClassLoader loader;

  /**
   * Constructs object that will be used to load ClassDescriptor from xml Document or
   * create a xml Document from ClassDescriptor.
   */
  public ClassDescriptorDocument() {

  }

  /**
   * Constructs object that will be used to create a xml Document from ClassDescriptor.
   */
  public ClassDescriptorDocument(ClassDescriptor descriptor) {
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl");
    loadDocumentFromDescriptor(descriptor);
  }

  /**
   * Returns the instance of the generated Document for the ClassDescriptor.
   *
   * @return the generated Document.
   */
  public Document getMainDocument() {
    return mainDocument;
  }

  /**
   * Sets the origial xml Document from where the ClassDescriptor will be read.
   *
   * @param doc the xml Document.
   */
  public void setMainDocument(Document doc) {
    mainDocument = doc;
  }

  /**
   * Sets class loader to load service classes when the original classes are not in standard classpath.
   *
   * @param _loader the loader used to load methods of the service's classes.
   */
  public void setLoader(ClassLoader _loader) {
    this.loader = _loader;
  }

  /**
   * Creates Document with the structure defined by resourceDescriptorDocument's dtd
   * for the specified ClassDescriptor.
   *
   * @param descriptor an  instance of the ClassDescriptor for which xml document is beenig generated.
   * @return the generated xml document.
   */
  public Document loadDocumentFromDescriptor(ClassDescriptor descriptor) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      mainDocument = builder.newDocument();
      mainDocument.appendChild(loadElementFromDescriptor(descriptor, mainDocument));
    } catch (ParserConfigurationException ex) {      //$JL-EXC$
      RMICLogger.throwing(ex);
      return mainDocument;
    }
    return mainDocument;
  }

  /**
   * Creates the main element of the Document with the structure defined by resourceDescriptorDocument's dtd
   * for the specified ClassDescriptor.
   *
   * @param descriptor an instance of the ClassDescriptor for which xml document is beenig generated.
   * @param document   an instance of the xml Document where the genertated element will be added.
   * @return the generated xml main element of the document.
   */
  public Element loadElementFromDescriptor(ClassDescriptor descriptor, Document document) {
    Element docElement = null;
    Element tempElement = null;
    Element tempsElement = null;
    Element innerEl1 = null;
    Element innerEl2 = null;
    Element innerEl3 = null;
    //String tempName = null;
    mainDocument = document;
    // creating the tags one by one
    try {
      docElement = mainDocument.createElement(CLASS); // Class

      if (descriptor.getPackage() != null) {
        load(docElement, descriptor.getPackage(), Package); // Package
      }

      if (descriptor.getInterfacePackage() != null) {
        load(docElement, descriptor.getInterfacePackage(), StubPackage); //StubPackage
      }

      load(docElement, descriptor.getAccessFlag(), AccessFlag); // AccessFlag

      if (descriptor.getAttribute() != null) {
        for (int i = 0; i < descriptor.getAttribute().length; i++) {
          load(docElement, descriptor.getAttribute(i), Attribute); // Attribute
        }
      }

      load(docElement, descriptor.getName(), Name); // Name

      if (descriptor.getSuperClass() != null) {
        load(docElement, descriptor.getSuperClass(), SuperClass); // SuperClass
      }

      if (descriptor.getSuperInterface() != null) {
        for (int i = 0; i < descriptor.getSuperInterface().length; i++) {
          load(docElement, descriptor.getSuperInterface(i), SuperInterface); // SuperInterface
        }
      }

      if (descriptor.getForStubName() != null) {
        load(docElement, descriptor.getForStubName(), ForStubName); // ForStubName
      }

      if (descriptor.getRMIRepositoryID() != null) {
        for (int i = 0; i < descriptor.getRMIRepositoryID().length; i++) {
          load(docElement, descriptor.getRMIRepositoryID(i), RMIRepositoryID); // RMIRepositoryID
        }
      }

      if (descriptor.getResourceId() != null) {
        load(docElement, descriptor.getResourceId(), ResourceId); // ResourceId
      }

      if (descriptor.getField() != null) {
        for (int i = 0; i < descriptor.getField().length; i++) {
          tempElement = mainDocument.createElement(Field); // Field
          docElement.appendChild(tempElement);
          load(tempElement, (descriptor.getField(i)).getName(), Name); // Name
          load(tempElement, (descriptor.getField(i)).getAccessFlag(), AccessFlag); // AccessFlag

          if ((descriptor.getField(i)).getAttribute() != null) {
            for (int j = 0; j < (descriptor.getField(i)).getAttribute().length; j++) {
              load(tempElement, (descriptor.getField(i)).getAttribute(j), Attribute); // Attribute
            }
          }

          load(tempElement, (descriptor.getField(i)).getType(), Type); // Type
        }
      }

      if (descriptor.getMethod() != null) {
        for (int i = 0; i < descriptor.getMethod().length; i++) {
          tempElement = mainDocument.createElement(Method); // Method
          docElement.appendChild(tempElement);
          load(tempElement, (descriptor.getMethod(i)).getName(), Name); // Name
          load(tempElement, (descriptor.getMethod(i)).getIDLname(), IDLName); // IDLName
          load(tempElement, (descriptor.getMethod(i)).getAccessFlag(), AccessFlag); // AccessFlag

          if ((descriptor.getMethod(i)).getAttribute() != null) {
            for (int j = 0; j < (descriptor.getMethod(i)).getAttribute().length; j++) {
              load(tempElement, (descriptor.getMethod(i)).getAttribute(j), Attribute); // Attribute
            }
          }

          load(tempElement, (descriptor.getMethod(i)).getRemoteType(), IsRemoteReturnType); // IsRemoteReturnType

          //          load(tempElement, (descriptor.getMethod(i)).getRemoteType(), strings[20]); // ReturnType
          if ((descriptor.getMethod(i).getReturnType()) != null) {
            innerEl1 = mainDocument.createElement(ReturnType); // ReturnType
            tempElement.appendChild(innerEl1);
            load(innerEl1, (descriptor.getMethod(i).getReturnType().getIsReturnType()), IsReturnType); // IsReturnType
            load(innerEl1, (descriptor.getMethod(i).getReturnType().getIsInterface()), IsInterface); // IsInterface
            load(innerEl1, (descriptor.getMethod(i).getReturnType().getClassType()), ClassType); // ClassType
            load(innerEl1, (descriptor.getMethod(i).getReturnType().getToWriteAsObject()), ToWriteAsObject); // ToWriteAsObject
            load(innerEl1, (descriptor.getMethod(i).getReturnType().getForStubName()), ForStubName);

            if ((descriptor.getMethod(i).getReturnType().getMethod().length) > 0) {
              for (int m = 0; m < descriptor.getMethod(i).getReturnType().getMethod().length; m++) {
                innerEl2 = mainDocument.createElement(Method); // Method
                innerEl1.appendChild(innerEl2);
                load(innerEl2, (descriptor.getMethod(i).getReturnType().getMethod(m).getName()), Name); // Name

                if ((descriptor.getMethod(i).getReturnType().getMethod(m)).getException().length > 0) {
                  for (int l = 0; l < (descriptor.getMethod(i).getReturnType().getMethod(m)).getException().length; l++) {
                    innerEl3 = mainDocument.createElement(Exception); // Exception
                    innerEl2.appendChild(innerEl3);
                    load(innerEl3, (descriptor.getMethod(i).getReturnType().getMethod(m)).getException(l).getName(), Name); // Name

                    if ((descriptor.getMethod(i).getReturnType().getMethod(m)).getException(l).getName() != "java.rmi.RemoteException") {
                      load(innerEl3, (descriptor.getMethod(i).getReturnType().getMethod(m)).getException(l).getIDLRepID(), IDLRepostoryID); // IDLRepostoryID
                    }

                    load(innerEl3, (descriptor.getMethod(i).getReturnType().getMethod(m)).getException(l).isRemoteException(), IsRemoteException); // IsRemoteException
                  }
                }
              }
            }
          }

          //          load(tempElement, (descriptor.getMethod(i)).getInterfaceType(), "IsInterfaceReturnType");
          //          load(tempElement, (descriptor.getMethod(i)).getType(), strings[20]); // ReturnType
          if ((descriptor.getMethod(i)).getParameter() != null) {
            for (int j = 0; j < (descriptor.getMethod(i)).getParameter().length; j++) {
              innerEl1 = mainDocument.createElement(Parameter); // Parameter
              tempElement.appendChild(innerEl1);
              load(innerEl1, (descriptor.getMethod(i)).getParameter(j).getType(), Type); // Type
              load(innerEl1, (descriptor.getMethod(i)).getParameter(j).isInterface(), IsInterface); // IsInterface
              load(innerEl1, (descriptor.getMethod(i)).getParameter(j).isRemote(), IsRemote); // IsRemote
              load(innerEl1, (descriptor.getMethod(i)).getParameter(j).getForStubName(), ForStubName);
              load(innerEl1, (descriptor.getMethod(i)).getParameter(j).getToWriteAsObject(), ToWriteAsObject);

              if (((descriptor.getMethod(i)).getParameter(j)).getMethod().length > 0) {
                for (int k = 0; k < ((descriptor.getMethod(i)).getParameter(j)).getMethod().length; k++) {
                  innerEl2 = mainDocument.createElement(Method); // Method
                  innerEl1.appendChild(innerEl2);
                  load(innerEl2, ((descriptor.getMethod(i)).getParameter(j)).getMethod(k).getName(), Name); // Name

                  if ((((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException().length > 0) {
                    for (int l = 0; l < (((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException().length; l++) {
                      innerEl3 = mainDocument.createElement(Exception); // Exception
                      innerEl2.appendChild(innerEl3);
                      load(innerEl3, (((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException(l).getName(), Name); // Name

                      if ((((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException(l).getName() != "java.rmi.RemoteException") {
                        load(innerEl3, (((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException(l).getIDLRepID(), IDLRepostoryID); // IDLRepostoryID
                      }

                      load(innerEl3, (((descriptor.getMethod(i)).getParameter(j)).getMethod(k)).getException(l).isRemoteException(), IsRemoteException); // IsRemoteException
                    }
                  }
                }
              }

              //              load(tempsElement, (descriptor.getMethod(i)).getParameter(j).isInterface(), strings[9]); // IsInterface
              //           load(tempElement, (descriptor.getMethod(i)).getParameter(j), strings[17]); // Parameter
            }
          }

          if ((descriptor.getMethod(i)).getException() != null) {
            for (int j = 0; j < (descriptor.getMethod(i)).getException().length; j++) {
              tempsElement = mainDocument.createElement(Exception); // Exception
              tempElement.appendChild(tempsElement);
              load(tempsElement, (descriptor.getMethod(i)).getException(j).getName(), Name); // Name

              if (descriptor.getMethod(i).getException(j).getName() != "java.rmi.RemoteException") {
                load(tempsElement, (descriptor.getMethod(i)).getException(j).getIDLRepID(), IDLRepostoryID); // IDLRepostoryID
              }

              load(tempsElement, (descriptor.getMethod(i)).getException(j).isRemoteException(), IsRemoteException); // IsRemoteException
            }
          }
        }
      }
    } catch (Exception e) {//$JL-EXC$
      RMICLogger.throwing(e);
    }
    return docElement;
  }

  /**
   * Returns the ClassDescriptor that was generated by reading the xml Document.
   *
   * @param document the input xml Document that will be read to load the service descriptor.
   * @return the loaded ClassDescriptor form the xml Document.
   */
  public ClassDescriptor loadDescriptorFromDocument(Document document) {
    ClassDescriptor descriptor = new ClassDescriptor();
    try {
      descriptor = loadDescriptorFromDocument(document.getDocumentElement());
    } catch (Exception ex) {//$JL-EXC$
      RMICLogger.throwing(ex);
      return descriptor;
    }
    return descriptor;
  }

  /**
   * Returns the ClassDescriptor that was generated by reading the element of xml Document,
   * which represent description of one ClassDescriptor.
   * Its used when reading more than one descriptors for each on of them.
   *
   * @param elmnt the input Element of xml Document that will be read to load the service descriptor.
   * @return the loaded ClassDescriptor form the input Element.
   */
  public ClassDescriptor loadDescriptorFromDocument(Element elmnt) throws WrongStructureException {
    ClassDescriptor resourceDescriptor = new ClassDescriptor();
    Element element = null;
    String tag = null;
    //String value = null;
    // boolean flag = false;
    ClassDescriptorField classDescriptorField = null;
    ClassDescriptorMethod classDescriptorMethod = null;
    ClassDescriptorMethod innerClassDescriptorMethod = null;
    //ClassDescriptorMethodParameter innerClassDescriptorMethodParameter = null;
    //ClassDescriptorMethodException innerClassDescriptorMethodException = null;
    ClassDescriptorReturnType classDescriptorReturnType = null;
    ClassDescriptorMethodParameter classDescriptorMethodParameter = null;
    ClassDescriptorMethodException classDescriptorMethodException = null;
    NodeList list = elmnt.getChildNodes();

    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        element = (Element) list.item(i);
        tag = element.getTagName();

        if (tag.equals(Method)) { // Method
          classDescriptorMethod = new ClassDescriptorMethod();
          NodeList paramsList = element.getChildNodes();

          if (paramsList != null) {
            Element nextEl = null;
            String nextElTag = null;

            for (int k = 0; k < paramsList.getLength(); k++) {
              if (paramsList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                nextEl = (Element) paramsList.item(k);
                nextElTag = nextEl.getNodeName();

                if (nextElTag.equals(Name)) { // Name
                  classDescriptorMethod.setName(getTextValue(nextEl));
                } else if (nextElTag.equals(IDLName)) { // IDLName
                  classDescriptorMethod.setIDLname(getTextValue(nextEl));
                } else if (nextElTag.equals(Parameter)) { // Parameter
                  NodeList paramsParamsList = nextEl.getChildNodes();

                  if (paramsParamsList != null) {
                    Element nextParamsEl = null;
                    String nextParamsElTag = null;
                    classDescriptorMethodParameter = new ClassDescriptorMethodParameter();

                    for (int j = 0; j < paramsParamsList.getLength(); j++) {
                      if (paramsParamsList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        nextParamsEl = (Element) paramsParamsList.item(j);
                        nextParamsElTag = nextParamsEl.getNodeName();

                        if (nextParamsElTag.equals(Type)) { // Type
                          classDescriptorMethodParameter.setType(getTextValue(nextParamsEl).replace('$', '.'));     //replace for inner class
                        } else if (nextParamsElTag.equals(IsInterface)) { // IsInterface
                          classDescriptorMethodParameter.setInterface(getTextValue(nextParamsEl));
                        } else if (nextParamsElTag.equals(IsRemote)) { // IsRemote
                          classDescriptorMethodParameter.setRemote(getTextValue(nextParamsEl));
                        } else if (nextParamsElTag.equals(ForStubName)) {
                          classDescriptorMethodParameter.setForStubName(getTextValue(nextParamsEl));
                        } else if (nextParamsElTag.equals(ToWriteAsObject)) {
                          classDescriptorMethodParameter.setToWriteAsObject(getTextValue(nextParamsEl));
                        } else if (nextParamsElTag.equals(Method)) {
                          innerClassDescriptorMethod = new ClassDescriptorMethod();
                          NodeList elemTypeList = nextParamsEl.getChildNodes();

                          if (elemTypeList != null) {
                            Element nextMethEl = null;
                            String nextMethElTag = null;

                            //														innerClassDescriptorMethodParameter = new ClassDescriptorMethodParameter();
                            for (int m = 0; m < elemTypeList.getLength(); m++) {
                              if (elemTypeList.item(m).getNodeType() == Node.ELEMENT_NODE) {
                                nextMethEl = (Element) elemTypeList.item(m);
                                nextMethElTag = nextMethEl.getNodeName();

                                if (nextMethElTag.equals(Exception)) { // Exception
                                  NodeList exceptionParsList = nextMethEl.getChildNodes();

                                  if (exceptionParsList != null) {
                                    Element nextParsEl = null;
                                    String nextParsElTag = null;
                                    classDescriptorMethodException = new ClassDescriptorMethodException();

                                    for (int n = 0; n < exceptionParsList.getLength(); n++) {
                                      if (exceptionParsList.item(n).getNodeType() == Node.ELEMENT_NODE) {
                                        nextParsEl = (Element) exceptionParsList.item(n);
                                        nextParsElTag = nextParsEl.getNodeName();

                                        if (nextParsElTag.equals(Name)) { // Name
                                          classDescriptorMethodException.setName(getTextValue(nextParsEl).replace('$', '.'));     //replace for inner class
                                        } else if (nextParsElTag.equals(IsRemoteException)) { // IsRemoteException
                                          classDescriptorMethodException.setIsRemoteException(getTextValue(nextParsEl));
                                        } else if (nextParsElTag.equals(IDLRepostoryID)) { // IDLRepostoryID
                                          classDescriptorMethodException.setIDLRepID(replaceStr(getTextValue(nextParsEl), "$", "__"));
                                        } else {
                                          throw new WrongStructureException("ID019155: Unknown Tag " + tag);
                                        }
                                      }
                                    }

                                    innerClassDescriptorMethod.setException(classDescriptorMethodException);
                                  }
                                }
                              }
                            }
                          }
                        } else {
                          throw new WrongStructureException("ID019156: Unknown Tag " + tag);
                        }
                      }
                    }

                    classDescriptorMethod.setParameter(classDescriptorMethodParameter);
                  }
                } else if (nextElTag.equals(AccessFlag)) { // AccessFlag
                  classDescriptorMethod.setAccessFlag(getTextValue(nextEl));
                } else if (nextElTag.equals(Attribute)) { // Attribute
                  classDescriptorMethod.setAttribute(getTextValue(nextEl));
                } else if (nextElTag.equals(IsRemoteReturnType)) { // IsRemoteReturnType
                  classDescriptorMethod.setRemoteType(getTextValue(nextEl));
                } else if (nextElTag.equals(ReturnType)) { // ReturnType
                  NodeList paramsReturnList = nextEl.getChildNodes();

                  if (paramsReturnList != null) {
                    Element nextParamRetEl = null;
                    String nextParamRetElTag = null;
                    classDescriptorReturnType = new ClassDescriptorReturnType();

                    for (int n = 0; n < paramsReturnList.getLength(); n++) {
                      if (paramsReturnList.item(n).getNodeType() == Node.ELEMENT_NODE) {
                        nextParamRetEl = (Element) paramsReturnList.item(n);
                        nextParamRetElTag = nextParamRetEl.getNodeName();

                        if (nextParamRetElTag.equals(IsReturnType)) { // IsReturnType
                          classDescriptorReturnType.setIsReturnType(getTextValue(nextParamRetEl));
                        } else if (nextParamRetElTag.equals(IsInterface)) { // IsInterface
                          classDescriptorReturnType.setIsInterface(getTextValue(nextParamRetEl));
                        } else if (nextParamRetElTag.equals(ClassType)) { // ClassType
                          classDescriptorReturnType.setClassType((getTextValue(nextParamRetEl).replace('$', '.')));     //replace for inner class
                        } else if (nextParamRetElTag.equals(ForStubName)) {
                          classDescriptorReturnType.setForStubName(getTextValue(nextParamRetEl));
                        } else if (nextParamRetElTag.equals(ToWriteAsObject)) { // ToWriteAsObject
                          classDescriptorReturnType.setIsReturnType(getTextValue(nextParamRetEl));
                        } else if (nextParamRetElTag.equals(Method)) { // Method
                          innerClassDescriptorMethod = new ClassDescriptorMethod();
                          NodeList paramRetList = nextParamRetEl.getChildNodes();

                          if (paramRetList != null) {
                            Element nextMethEl = null;
                            String nextMethElTag = null;

                            //														innerClassDescriptorMethodParameter = new ClassDescriptorMethodParameter();
                            for (int p = 0; p < paramRetList.getLength(); p++) {
                              if (paramRetList.item(p).getNodeType() == Node.ELEMENT_NODE) {
                                nextMethEl = (Element) paramRetList.item(p);
                                nextMethElTag = nextMethEl.getNodeName();

                                if (nextMethElTag.equals(Exception)) { // Exception
                                  NodeList exceptionParsList = nextMethEl.getChildNodes();

                                  if (exceptionParsList != null) {
                                    Element nextParsEl = null;
                                    String nextParsElTag = null;
                                    classDescriptorMethodException = new ClassDescriptorMethodException();

                                    for (int r = 0; r < exceptionParsList.getLength(); r++) {
                                      if (exceptionParsList.item(r).getNodeType() == Node.ELEMENT_NODE) {
                                        nextParsEl = (Element) exceptionParsList.item(r);
                                        nextParsElTag = nextParsEl.getNodeName();

                                        if (nextParsElTag.equals(Name)) { // Name
                                          classDescriptorMethodException.setName(getTextValue(nextParsEl).replace('$', '.'));   // replace for inner class
                                        } else if (nextParsElTag.equals(IsRemoteException)) { // IsRemoteException
                                          classDescriptorMethodException.setIsRemoteException(getTextValue(nextParsEl));
                                        } else if (nextParsElTag.equals(IDLRepostoryID)) { // IDLRepostoryID
                                          classDescriptorMethodException.setIDLRepID(replaceStr(getTextValue(nextParsEl), "$", "__"));
                                        } else {
                                          throw new WrongStructureException("ID019157: Unknown Tag " + tag);
                                        }
                                      }
                                    }

                                    innerClassDescriptorMethod.setException(classDescriptorMethodException);
                                  }
                                }
                              }
                            }
                          }

                          classDescriptorReturnType.setMethod(innerClassDescriptorMethod);
                        }
                      }
                    }

                    classDescriptorMethod.setReturnType(classDescriptorReturnType);
                  }
                } else if (nextElTag.equals(Exception)) { // Exception
                  NodeList exceptionParamsList = nextEl.getChildNodes();

                  if (exceptionParamsList != null) {
                    Element nextParamsEl = null;
                    String nextParamsElTag = null;
                    classDescriptorMethodException = new ClassDescriptorMethodException();

                    for (int j = 0; j < exceptionParamsList.getLength(); j++) {
                      if (exceptionParamsList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        nextParamsEl = (Element) exceptionParamsList.item(j);
                        nextParamsElTag = nextParamsEl.getNodeName();

                        if (nextParamsElTag.equals(Name)) { // Name
                          classDescriptorMethodException.setName(getTextValue(nextParamsEl).replace('$', '.'));     //replace for inner class
                        } else if (nextParamsElTag.equals(IsRemoteException)) { // IsRemoteException
                          classDescriptorMethodException.setIsRemoteException(getTextValue(nextParamsEl));
                        } else if (nextParamsElTag.equals(IDLRepostoryID)) { // IDLRepostoryID
                          classDescriptorMethodException.setIDLRepID(replaceStr(getTextValue(nextParamsEl), "$", "__"));
                        } else {
                          throw new WrongStructureException("ID019158: Unknown Tag " + tag);
                        }
                      }
                    }

                    classDescriptorMethod.setException(classDescriptorMethodException);
                  }
                } else {
                  throw new WrongStructureException("ID019159: Unknown Tag " + tag);
                }
              }
            }
          }

          resourceDescriptor.setMethod(classDescriptorMethod);
        } else if (tag.equals(Field)) { // Field
          classDescriptorField = new ClassDescriptorField();
          NodeList paramsList = element.getChildNodes();

          if (paramsList != null) {
            Element nextEl = null;
            String nextElTag = null;

            for (int k = 0; k < paramsList.getLength(); k++) {
              if (paramsList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                nextEl = (Element) paramsList.item(k);
                nextElTag = nextEl.getNodeName();

                if (nextElTag.equals(Name)) { // Name
                  classDescriptorField.setName(getTextValue(nextEl));
                } else if (nextElTag.equals(AccessFlag)) { // AccessFlag
                  classDescriptorField.setAccessFlag(getTextValue(nextEl));
                } else if (nextElTag.equals(Attribute)) { // Attribute
                  classDescriptorField.setAttribute(getTextValue(nextEl));
                } else if (nextElTag.equals(Type)) { // Type
                  classDescriptorField.setType(getTextValue(nextEl));
                } else {
                  throw new WrongStructureException("ID019160: Unknown Tag " + tag);
                }
              }
            }
          }

          resourceDescriptor.setField(classDescriptorField);
        } else if (tag.equals(Attribute)) { // Attribute
          resourceDescriptor.setAttribute(getTextValue(element));
        } else if (tag.equals(Package)) { // Package
          resourceDescriptor.setPackage(getTextValue(element));
        } else if (tag.equals(StubPackage)) { // StubPackage
          resourceDescriptor.setInterfacePackage(getTextValue(element));
        } else if (tag.equals(RMIRepositoryID)) { // RMIRepositoryID
          resourceDescriptor.setRMIRepositoryID(getTextValue(element));
        } else if (tag.equals(AccessFlag)) { // AccessFlag
          resourceDescriptor.setAccessFlag(getTextValue(element));
        } else if (tag.equals(Name)) { // Name
          resourceDescriptor.setName(getTextValue(element));
        } else if (tag.equals(SuperClass)) { // SuperClass
          resourceDescriptor.setSuperClass(getTextValue(element));
        } else if (tag.equals(ForStubName)) { // ForStubName
          resourceDescriptor.setForStubName(getTextValue(element));
        } else if (tag.equals(ResourceId)) { // ResourceId
          resourceDescriptor.setResourceId(getTextValue(element));
        } else if (tag.equals(SuperInterface)) { // SuperInterface
          resourceDescriptor.setSuperInterface(getTextValue(element));
        } else {
          throw new WrongStructureException("ID019161: Unknown Tag " + tag);
        }
      }
    }

    return resourceDescriptor;
  }

  private String replaceStr(String base, String old, String rep) {
    int i = base.indexOf(old);
    if (i != -1) {
      return base.substring(0, (i - 1)) + rep + base.substring(i + 1);
    } else {
      return base;
    }

  }

  /*
   * The following 5 method are helpful methods for reading
   * and writing operation with the xml's Element.
   */
  protected void load(Element el, String value, String tagName) {
    if (value == null) {
      value = "";
    }

    Element nextElement = mainDocument.createElement(tagName);
    Text textNode = mainDocument.createTextNode(value);
    nextElement.appendChild(textNode);
    el.appendChild(nextElement);
  }

  protected void load(String name, String tagName) {
    load(mainDocument.getDocumentElement(), name, tagName);
  }

  protected String getTextValue(Element element) {
    if (element != null) {
      Node node = element.getFirstChild();

      if ((node != null) && (node.getNodeType() == Node.TEXT_NODE)) { // "node" is a TextNode
        String value = node.getNodeValue();

        if ((value.trim()).equals("") || (value.trim()).equals(NULL)) { // null
          return null;
        } else {
          return value.trim();
        }
      }
    }

    return null;
  }

  /**
   * Write data to destination XML file
   */
  public void writeDTDtoFile(String filename) {
    try {
      StandardDOMWriterNoSpace wr = new StandardDOMWriterNoSpace();
      wr.write(mainDocument, filename, dtd);
    } catch (IOException ioex) { //$JL-EXC$
      RMICLogger.throwing(ioex);
    }
  }

}

