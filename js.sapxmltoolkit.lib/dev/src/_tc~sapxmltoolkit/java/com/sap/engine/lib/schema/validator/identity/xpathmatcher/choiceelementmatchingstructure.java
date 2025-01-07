/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ChoiceElementMatchingStructure extends ElementMatchingStructure {
  
  private NodeMatchingStructure[] structures;
  
  protected ChoiceElementMatchingStructure(NodeMatchingStructure[] structures) {
    this.structures = structures;
  }
  
  protected void addSubStructure(NodeMatchingStructure substructure) {
    this.substructure = substructure;
    for(int i = 0; i < structures.length; i++) {
      NodeMatchingStructure nodeMatchingStruc = structures[i];
      if(nodeMatchingStruc instanceof ElementMatchingStructure) {
        ((ElementMatchingStructure)nodeMatchingStruc).addSubStructure(substructure);
      }
    }
  }
  
  public boolean process(XPathTokenizer tokenizer) {
    int tokenizerIndex = tokenizer.getIndex();
    for(int i = 0; i < structures.length; i++) {
      NodeMatchingStructure nodeMatchingStruc = structures[i];
      if(nodeMatchingStruc.process(tokenizer) && tokenizer.peek() == null) {
        if(nodeMatchingStruc instanceof ElementMatchingStructure) {
          xPathStepIndex = ((ElementMatchingStructure)nodeMatchingStruc).getXPathStepIndex(substructure);
        }
        return(true);
      }
      tokenizer.setIndex(tokenizerIndex);
    }
    return(false);
  }

}
