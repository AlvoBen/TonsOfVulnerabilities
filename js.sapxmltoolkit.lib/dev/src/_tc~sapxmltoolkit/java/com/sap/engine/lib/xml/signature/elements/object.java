package com.sap.engine.lib.xml.signature.elements;

//import java.security.*;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;

public class Object extends GenericElement {

  public Object() {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Object");
  }

  public Object(GenericElement parent) throws SignatureException {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "Object", parent);
  }

  //  public void construct() throws SignatureException {
  //  }

}

