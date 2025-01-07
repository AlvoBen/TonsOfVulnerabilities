package com.sap.engine.lib.schema.validator.automat;

import com.sap.engine.lib.schema.components.Wildcard;

public final class WildcardAutomat extends BaseAutomat {

  private Wildcard wildcard;

  protected WildcardAutomat(Wildcard wildcard) {
    super();
    this.wildcard = wildcard;
  }

  protected Switch switchState(String uri, String name) {
    isUntouched = false;
    switchResult.base = wildcard;
    return(switchResult);
  }

  protected void initToStringBuffer(StringBuffer toStringBuffer, String offset) {
    toStringBuffer.append(offset);
    initIdBuffer(toStringBuffer);
  }
  
  protected void initExpectedBuffer(StringBuffer expectedBuffer) {
    initIdBuffer(expectedBuffer);
    expectedBuffer.append(" ");
  }
  protected void initIdBuffer(StringBuffer idBuffer) {
    idBuffer.append("Any");
  }
}