/*
 * Created on 2005-6-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator;

import java.util.Hashtable;
import java.util.Vector;

import com.sap.engine.lib.schema.components.ComplexTypeDefinition;
import com.sap.engine.lib.schema.components.IdentityConstraintDefinition;
import com.sap.engine.lib.schema.components.impl.ffacets.Value;
import com.sap.engine.lib.schema.util.Tools;
import com.sap.engine.lib.schema.validator.automat.ContentAutomat;
import com.sap.engine.lib.schema.validator.identity.IdentityConstraintRegResult;
import com.sap.engine.lib.schema.validator.identity.KeySequence;
import com.sap.engine.lib.schema.validator.identity.KeyWrapper;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.IdentityConstraintXPathMatcher;
import com.sap.engine.lib.schema.validator.identity.xpathmatcher.XPathTokenizer;
import com.sap.engine.lib.schema.validator.xpath.AttributeXPathStep;
import com.sap.engine.lib.schema.validator.xpath.ElementXPathStep;
import com.sap.engine.lib.schema.validator.xpath.XPathStep;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ReusableObjectsPool {
  
  private Hashtable complTypeToAutomatMapping;
  private Hashtable charArrayToStringMapping;
  private Hashtable stringAndNormWhitespaceValueToNormStringMapping;
  private Hashtable stringToCharArrayMapping;
  private Vector fFacetValues;
  private Vector elemXPathSteps;
  private Vector attribXPathSteps;
  private Vector stringBuffers;
  private Vector allStructureStates;
  private Vector baseStructureStates;
  private Vector groupStructureStates;
  private Vector particleStructureStates;
  private Vector keySequences;
  private Vector keyWrappers;
  private Vector identConstrRegResults;
  private Hashtable qKeyToMatchersMapping;
  
  protected ReusableObjectsPool() {
    complTypeToAutomatMapping = new Hashtable();
    charArrayToStringMapping = new Hashtable();
    stringAndNormWhitespaceValueToNormStringMapping = new Hashtable();
    stringToCharArrayMapping = new Hashtable();
    fFacetValues = new Vector();
    elemXPathSteps = new Vector();
    attribXPathSteps = new Vector();
    stringBuffers = new Vector();
    allStructureStates = new Vector();
    baseStructureStates = new Vector();
    groupStructureStates = new Vector();
    particleStructureStates = new Vector();
    keySequences = new Vector();
    keyWrappers = new Vector();
    identConstrRegResults = new Vector();
    qKeyToMatchersMapping = new Hashtable();
  }
  
  public IdentityConstraintXPathMatcher getIdentityConstraintXPathMatcher(IdentityConstraintDefinition identConstrDefinition, XPathTokenizer xPathTokenizer) {
    IdentityConstraintXPathMatcher identConstrXPathMatcher = (IdentityConstraintXPathMatcher)(qKeyToMatchersMapping.get(identConstrDefinition.getQualifiedKey()));
    if(identConstrXPathMatcher == null) {
      identConstrXPathMatcher = new IdentityConstraintXPathMatcher(xPathTokenizer, identConstrDefinition);
      qKeyToMatchersMapping.put(identConstrDefinition.getQualifiedKey(), identConstrXPathMatcher);
    }
    return(identConstrXPathMatcher);
  }

//TODO  
//  protected ContentAutomat getContentAutomat(ComplexTypeDefinition complTypeDefinition) {
//    ContentAutomat contentAutomat = (ContentAutomat)(complTypeToAutomatMapping.get(complTypeDefinition));
//    if(contentAutomat == null) {
//      contentAutomat = new ContentAutomat(this, complTypeDefinition);
//      complTypeToAutomatMapping.put(complTypeDefinition, contentAutomat);
//    } else if(!contentAutomat.isInStartingState()) {
//      contentAutomat = new ContentAutomat(this, complTypeDefinition);
//    }
//    return(contentAutomat);
//  }
  
  protected ContentAutomat getContentAutomat(ComplexTypeDefinition complTypeDefinition) {
    ContentAutomat contentAutomat = (ContentAutomat)(complTypeToAutomatMapping.get(complTypeDefinition));
    if(contentAutomat == null) {
      contentAutomat = new ContentAutomat(complTypeDefinition);
      complTypeToAutomatMapping.put(complTypeDefinition, contentAutomat);
    } else if(!contentAutomat.isUntouched()) {
      contentAutomat = new ContentAutomat(complTypeDefinition);
    }
    return(contentAutomat);
  }
  
  protected String getString(CharArray charArray) {
    String result = (String)(charArrayToStringMapping.get(charArray));
    if(result == null) {
      result = charArray.getStringFast();
      charArrayToStringMapping.put(charArray, result);
    }
    return(result);
  }
  
  protected String getNormalizedString(String initialString, String normWhitespaceValue) {
    if(normWhitespaceValue != null) {
      Hashtable stringToNormStringMapping = stringToNormStringMapping = (Hashtable)(stringAndNormWhitespaceValueToNormStringMapping.get(normWhitespaceValue));
      if(stringToNormStringMapping != null) {
        String normString = (String)(stringToNormStringMapping.get(initialString));
        if(normString == null) {
          normString = storeNormalizedString(initialString, normWhitespaceValue, stringToNormStringMapping);
        }
        return(normString);
      }
      stringToNormStringMapping = new Hashtable();
      stringAndNormWhitespaceValueToNormStringMapping.put(normWhitespaceValue, stringToNormStringMapping);
      return(storeNormalizedString(initialString, normWhitespaceValue, stringToNormStringMapping));
    }
    return(Tools.normalizeValue(initialString, normWhitespaceValue));
  }
  
  private String storeNormalizedString(String initialString, String normWhitespaceValue, Hashtable stringToNormStringMapping) {
    String normString = Tools.normalizeValue(initialString, normWhitespaceValue);
    stringToNormStringMapping.put(initialString, normString);
    return(normString);
  }
  
  protected void reuseFFacetValue(Value fFacetValue) {
    if(fFacetValue != null) {
      fFacetValue.reuse();
      if(fFacetValue.isReusable()) {
        fFacetValues.add(fFacetValue);
      }
    }
  }
  
  protected CharArray getCharArray(String string) {
    CharArray charArray = (CharArray)(stringToCharArrayMapping.get(string));
    if(charArray == null) {
      charArray = new CharArray(string);
      stringToCharArrayMapping.put(string, charArray);
    }
    return(charArray);
  }
  
  public Value getFFacetValue() {
    if(fFacetValues.size() > 0) {
      return((Value)(fFacetValues.remove(fFacetValues.size() - 1)));
    }
    return(new Value());
  }
  
  public ElementXPathStep getElementXPathStep() {
    if(elemXPathSteps.size() > 0) {
      return((ElementXPathStep)(elemXPathSteps.remove(elemXPathSteps.size() - 1)));
    }
    return(new ElementXPathStep(this));
  }
  
  public void reuseElementXPathStep(XPathStep elemXPathStep) {
    elemXPathStep.reuse();
    elemXPathSteps.add(elemXPathStep);
  }
  
  public AttributeXPathStep getAttributeXPathStep() {
    if(attribXPathSteps.size() > 0) {
      return((AttributeXPathStep)(attribXPathSteps.remove(attribXPathSteps.size() - 1)));
    }
    return(new AttributeXPathStep(this));
  }
  
  public void reuseAttributeXPathStep(XPathStep attribXPathStep) {
    attribXPathStep.reuse();
    attribXPathSteps.add(attribXPathStep);
  }
  
  public StringBuffer getStringBuffer() {
    if(stringBuffers.size() > 0) {
      return((StringBuffer)(stringBuffers.remove(stringBuffers.size() - 1)));
    }
    return(new StringBuffer());
  }
  
  public void reuseStringBuffer(StringBuffer stringBuffer) {
    stringBuffer.delete(0, stringBuffer.length());
    stringBuffers.add(stringBuffer);
  }
  
//  public AllStructureState getAllStructureState() {
//    if(allStructureStates.size() > 0) {
//      return((AllStructureState)(allStructureStates.remove(allStructureStates.size() - 1)));
//    }
//    return(new AllStructureState());
//  }
//  
//  public void reuseAllStructureState(AllStructureState allStructureState) {
//    if(allStructureState != null) {
//      allStructureStates.add(allStructureState);
//    }
//  }
//  
//  public BaseStructureState getBaseStructureState() {
//    if(baseStructureStates.size() > 0) {
//      return((BaseStructureState)(baseStructureStates.remove(baseStructureStates.size() - 1)));
//    }
//    return(new BaseStructureState());
//  }
//  
//  public void reuseBaseStructureState(BaseStructureState baseStructureState) {
//    if(baseStructureState != null) {
//      baseStructureStates.add(baseStructureState);
//    }
//  }
//  
//  public GroupStructureState getGroupStructureState() {
//    if(groupStructureStates.size() > 0) {
//      return((GroupStructureState)(groupStructureStates.remove(groupStructureStates.size() - 1)));
//    }
//    return(new GroupStructureState());
//  }
//  
//  public void reuseGroupStructureState(GroupStructureState groupStructureState) {
//    if(groupStructureState != null) {
//      reuseStructureState(groupStructureState.getProcessingStructureState());
//      groupStructureStates.add(groupStructureState);
//    }
//  }
//  
//  public ParticleStructureState getParticleStructureState() {
//    if(particleStructureStates.size() > 0) {
//      return((ParticleStructureState)(particleStructureStates.remove(particleStructureStates.size() - 1)));
//    }
//    return(new ParticleStructureState());
//  }
//  
//  public void reuseParticleStructureState(ParticleStructureState particleStructureState) {
//    if(particleStructureState != null) {
//      reuseStructureState(particleStructureState.getStructureState());
//      particleStructureStates.add(particleStructureState);
//    }
//  }
//  
//  public void reuseStructureState(StructureState structureState) {
//    if(structureState instanceof AllStructureState) {
//      reuseAllStructureState((AllStructureState)structureState);
//    } else if(structureState instanceof BaseStructureState) {
//      reuseBaseStructureState((BaseStructureState)structureState);
//    } else if(structureState instanceof GroupStructureState) {
//      reuseGroupStructureState((GroupStructureState)structureState);
//    } else {
//      reuseParticleStructureState((ParticleStructureState)structureState);
//    }
//  }
  
  public KeySequence getKeySequence() {
    if(keySequences.size() > 0) {
      return((KeySequence)(keySequences.remove(keySequences.size() - 1)));
    }
    return(new KeySequence());
  }
  
  public void reuseKeySequence(KeySequence keySequence) {
    keySequence.reuse();
    KeyWrapper[] keyWrappers = keySequence.getKeyWrappers();
    for(int i = 0; i < keyWrappers.length; i++) {
      reuseKeyWrapper(keyWrappers[i]);
    }
    keySequences.add(keySequence);
  }
  
  public KeyWrapper getKeyWrapper() {
    if(keyWrappers.size() > 0) {
      return((KeyWrapper)(keyWrappers.remove(keyWrappers.size() - 1)));
    }
    return(new KeyWrapper());
  }
  
  public void reuseKeyWrapper(KeyWrapper keyWrapper) {
    Value fFacetsValue = keyWrapper.getFFacetsValue();
    if(fFacetsValue != null) {
      reuseFFacetValue(fFacetsValue);
    }
    keyWrappers.add(keyWrapper);
  }
  
  public IdentityConstraintRegResult getIdentityConstraintRegResult() {
    if(identConstrRegResults.size() > 0) {
      return((IdentityConstraintRegResult)(identConstrRegResults.remove(identConstrRegResults.size() - 1)));
    }
    return(new IdentityConstraintRegResult());
  }
  
  public void reuseIdentityConstraintRegResult(IdentityConstraintRegResult identConstrRegResult) {
    identConstrRegResult.reuse();
    identConstrRegResults.add(identConstrRegResult);
  }
}
