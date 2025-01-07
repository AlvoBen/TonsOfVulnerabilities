package com.sap.engine.lib.schema.validator.automat;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.sap.engine.lib.schema.components.ElementDeclaration;

public final class ElementAutomat extends BaseAutomat {
	
  private Hashtable uriToElemDeclrWrappersMap;
  
  protected ElementAutomat(ElementDeclaration elementDeclaration) {
    super();
    uriToElemDeclrWrappersMap = new Hashtable();
    mapElementDeclaration(elementDeclaration, false);
  }
  
  private void mapElementDeclaration(ElementDeclaration elementDeclaration, boolean isSubstitution) {
  	String uri = elementDeclaration.getTargetNamespace();
  	String name = elementDeclaration.getName();
  	Hashtable nameToElemDclrWrappersMap = (Hashtable)(uriToElemDeclrWrappersMap.get(uri));
  	if(nameToElemDclrWrappersMap == null) {
  	  nameToElemDclrWrappersMap = new Hashtable();
      uriToElemDeclrWrappersMap.put(uri, nameToElemDclrWrappersMap);
  	}
    ElementDeclarationWrapper elemDeclrWrapper = new ElementDeclarationWrapper();
    elemDeclrWrapper.elementDeclaration = elementDeclaration;
    elemDeclrWrapper.isSubstitution = isSubstitution;
    nameToElemDclrWrappersMap.put(name, elemDeclrWrapper);
	Vector substitutions = new Vector();
	elementDeclaration.getSubstitutableElementDeclarations(substitutions);
	for(int i = 0; i < substitutions.size(); i++) {
      ElementDeclaration substitution = (ElementDeclaration)(substitutions.get(i));
      mapElementDeclaration(substitution, true);
	}
  }

  protected Switch switchState(String uri, String name) {
  	Hashtable nameToElemDclrWrappersMap = (Hashtable)(uriToElemDeclrWrappersMap.get(uri));
  	if(nameToElemDclrWrappersMap != null) {
  	  ElementDeclarationWrapper elemDeclrWrapper = (ElementDeclarationWrapper)(nameToElemDclrWrappersMap.get(name));
      if(elemDeclrWrapper != null) {
        isUntouched = false;
        switchResult.base = elemDeclrWrapper.elementDeclaration;
        switchResult.isSubstitution = elemDeclrWrapper.isSubstitution;
        switchResult.scopeModelGroupAll = null;
        return(switchResult);
      }
  	}
    return(null);
  }
  
  protected void initToStringBuffer(StringBuffer toStringBuffer, String offset) {
    toStringBuffer.append(offset);
    toStringBuffer.append("Element");
    initToStringBuffer_UriToElemDeclrWrappersMap(toStringBuffer, offset);
  }
  
  private void initToStringBuffer_UriToElemDeclrWrappersMap(StringBuffer toStringBuffer, String offset) {
    toStringBuffer.append("[");
    Enumeration nameToElemDclrWrappersMapsEnum = uriToElemDeclrWrappersMap.elements();
    while(nameToElemDclrWrappersMapsEnum.hasMoreElements()) {
      Hashtable nameToElemDclrWrappersMap = (Hashtable)(nameToElemDclrWrappersMapsEnum.nextElement());
      initToStringBuffer_NameToElemDclrWrappersMap(toStringBuffer, offset, nameToElemDclrWrappersMap);
    }
    toStringBuffer.append("]");
  }
  
  private void initToStringBuffer_NameToElemDclrWrappersMap(StringBuffer toStringBuffer, String offset, Hashtable nameToElemDclrWrappersMap) {
    Enumeration elemDeclrWrappersEnum = nameToElemDclrWrappersMap.elements();
    while(elemDeclrWrappersEnum.hasMoreElements()) {
      ElementDeclarationWrapper elemDeclrWrapper = (ElementDeclarationWrapper)(elemDeclrWrappersEnum.nextElement());
      elemDeclrWrapper.initToStringBuffer(toStringBuffer);
      toStringBuffer.append(" ");
    }
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    Enumeration nameToElemDeclrWrappersMapsEnum = uriToElemDeclrWrappersMap.elements();
    while(nameToElemDeclrWrappersMapsEnum.hasMoreElements()) {
      Hashtable nameToElemDeclrWrappersMap = (Hashtable)(nameToElemDeclrWrappersMapsEnum.nextElement());
      Enumeration elemWrappersEnum = nameToElemDeclrWrappersMap.elements();
      while(elemWrappersEnum.hasMoreElements()) {
        ElementDeclarationWrapper elemDeclrWrapper = (ElementDeclarationWrapper)(elemWrappersEnum.nextElement());
        elemDeclrWrapper.initQNameBuffer(expectedBuffer);
        expectedBuffer.append(" ");
      }
    }
  }
}