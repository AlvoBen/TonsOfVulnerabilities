/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent;

import java.io.*;
import java.util.HashMap;
//import com.inqmy.services.cross.communication.ClusterRemoteReference;
import com.sap.engine.services.jndi.implclient.ClientContext;

/**
 * Input stream for CPO
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class ClientCPOInputStream extends CPOInputStream {

  /**
   * table mapping primitive type names to corresponding class objects
   */
  private static final HashMap primClasses = new HashMap(8, 1.0F);

  static {
    primClasses.put("boolean", boolean.class);
    primClasses.put("byte", byte.class);
    primClasses.put("char", char.class);
    primClasses.put("short", short.class);
    primClasses.put("int", int.class);
    primClasses.put("long", long.class);
    primClasses.put("float", float.class);
    primClasses.put("double", double.class);
    primClasses.put("void", void.class);
  }

  /**
   * Constructor
   *
   * @param bais Input stream to use
   * @param cc   Client context to use
   * @throws IOException Thrown if a problem occures.
   */
  public ClientCPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException {
    super(bais, cc);
  }

  /**
   * Resolves clss
   *
   * @param osc Object stream class to use
   * @return Resolved class
   * @throws IOException            Thrown if a problem occures.
   * @throws ClassNotFoundException Thrown if the class could not be found.
   */
  protected Class resolveClass(ObjectStreamClass osc) throws java.io.IOException, ClassNotFoundException {
    String loaderName = (String) readObject();
    String name = osc.getName();

    //System.out.println("*-*-*-* LoaderName:" + loaderName + "  " + osc.getName());
    try {
      if (loaderName.equals("NoName")) {
        return Class.forName(name);
      } else {
        return Class.forName(name);
      }
    } catch (ClassNotFoundException e) {
      Class cl = (Class) primClasses.get(name);
      if (cl != null) {
        return cl;
      } else {
        throw e;
      }
    }
  }

}

