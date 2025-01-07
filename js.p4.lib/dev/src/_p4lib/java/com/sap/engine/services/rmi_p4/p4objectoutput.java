package com.sap.engine.services.rmi_p4;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public interface P4ObjectOutput extends ObjectOutput {

  public void writeRemoteObject(RemoteRef ref, Class _class) throws IOException;

}

