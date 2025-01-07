package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.Constants;

public abstract class Automat implements Constants {
  
  protected static final String TO_STRING_OFFSET = "    ";
  
  protected boolean isUntouched;
  
  public Automat() {
    isUntouched = true;
  }

  protected abstract Switch switchState(String uri, String name);

  protected void reset() {
    isUntouched = true;
  }

  protected boolean isUntouched() {
    return(isUntouched);
  }

  protected abstract boolean isSatisfied();
  
  protected abstract boolean isDrained();

  protected AutomatState getState() {
    AutomatState state = createState();
    initState(state);
    return(state);
  }
  
  protected AutomatState createState() {
    return(new AutomatState());
  }
  
  protected void initState(AutomatState state) {
    state.isUntouched = isUntouched;
  }
  
  protected void setState(AutomatState state) {
    isUntouched = state.isUntouched;
  }
  
  public String toString() {
  	StringBuffer toStringBuffer = new StringBuffer();
    initToStringBuffer(toStringBuffer, "");
    return(toStringBuffer.toString());
  }
  
  protected abstract void initToStringBuffer(StringBuffer toStringBuffer, String offset);
  
  protected abstract void initExpectedBuffer(StringBuffer expectedBuffer);
}