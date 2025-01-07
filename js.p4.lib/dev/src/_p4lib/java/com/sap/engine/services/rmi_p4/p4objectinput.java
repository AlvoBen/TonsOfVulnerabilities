package com.sap.engine.services.rmi_p4;

import java.io.IOException;
import java.io.ObjectInput;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public interface P4ObjectInput extends ObjectInput {

  public Object readRemoteObject() throws IOException, ClassNotFoundException;

}

